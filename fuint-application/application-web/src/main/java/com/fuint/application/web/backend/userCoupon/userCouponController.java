package com.fuint.application.web.backend.userCoupon;

import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.util.DateUtil;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.web.backend.util.ExcelUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;
import static com.fuint.application.util.XlsUtil.objectConvertToString;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡券统计管理类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/userCoupon")
public class userCouponController {

    /**
     * 卡券分组服务接口
     */
    @Autowired
    private UserCouponService userCouponService;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 店铺接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 后台用户接口
     * */
    @Autowired
    private TAccountService accountService;

    /**
     * 查询会员卡券列表
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list")
    @RequiresPermissions("/backend/userCoupon/list")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();
        Map<String, Object> param = new HashMap<>();

        param.put("status", params.get("EQ_status"));
        param.put("pageNumber", paginationRequest.getCurrentPage());
        param.put("pageSize", paginationRequest.getPageSize());
        param.put("userId", params.get("EQ_userId"));
        param.put("mobile", params.get("EQ_mobile"));
        param.put("storeId", params.get("EQ_storeId"));
        param.put("couponId", params.get("EQ_couponId"));
        param.put("type", params.get("EQ_type"));
        param.put("code", params.get("EQ_code"));

        ResponseObject result = userCouponService.getUserCouponList(param);

        Map<String, Object> storeParams = new HashMap<>();
        storeParams.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(storeParams);
        model.addAttribute("storeList", storeList);
        model.addAttribute("paginationResponse", result.getData());
        model.addAttribute("params", params);

        CouponTypeEnum[] typeList = CouponTypeEnum.values();
        model.addAttribute("typeList", typeList);

        return "userCoupon/list";
    }

    /**
     * 核销用户卡券
     * */
    @RequiresPermissions("backend/userCoupon/doConfirm")
    @RequestMapping(value = "/doConfirm")
    public String doConfirm(HttpServletRequest request) throws BusinessCheckException {
        String userCouponId = request.getParameter("userCouponId");
        MtUserCoupon mtUserCoupon = couponService.queryUserCouponById(Integer.parseInt(userCouponId));

        if (mtUserCoupon == null || StringUtils.isEmpty(userCouponId)) {
            throw new BusinessCheckException("错误，用户卡券不存在！");
        }

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        TAccount account = accountService.findAccountById(shiroUser.getId());
        Integer storeId = account.getStoreId();

        BigDecimal confirmAmount = mtUserCoupon.getAmount();
        if (mtUserCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            confirmAmount = mtUserCoupon.getBalance();
        }

        couponService.useCoupon(Integer.parseInt(userCouponId), shiroUser.getId().intValue(), storeId, 0, confirmAmount, "后台核销");

        return "redirect:/backend/userCoupon/list";
    }

    /**
     * 导出二维码
     *
     * @return
     */
    @RequiresPermissions("backend/member/exportList")
    @RequestMapping(value = "/exportList")
    @ResponseBody
    public void exportList(HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);

        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(50000);

        PaginationResponse<MtUserCoupon> result = userCouponService.queryUserCouponListByPagination(paginationRequest);

        // excel标题
        String[] title = { "核销二维码", "卡券ID", "卡券名称", "会员手机号", "状态", "面额", "余额" };

        // excel文件名
        String fileName = "会员卡券二维码"+ DateUtil.formatDate(new Date(), "yyyy.MM.dd_HHmm") +".xls";

        // sheet名
        String sheetName = "数据列表";

        String[][] content = null;

        List<MtUserCoupon> list = result.getContent();

        if (list.size() > 0) {
            content= new String[list.size()][title.length];
        }

        for (int i = 0; i < list.size(); i++) {
            MtUserCoupon obj = list.get(i);
            MtCoupon mtCoupon = couponService.queryCouponById(obj.getCouponId());

            if (mtCoupon != null) {
                content[i][0] = objectConvertToString(obj.getCode());
                content[i][1] = objectConvertToString(obj.getCouponId());
                content[i][2] = objectConvertToString(mtCoupon.getName());
                content[i][3] = objectConvertToString(obj.getMobile());
                content[i][4] = UserCouponStatusEnum.getValue(obj.getStatus());
                content[i][5] = objectConvertToString(obj.getAmount() != null ? obj.getAmount().toString() : "0.00");
                content[i][6] = objectConvertToString(obj.getBalance() != null ? obj.getBalance().toString() : "0.00");
            }
        }

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);

        ExcelUtil.setResponseHeader(response, fileName, wb);

        return;
    }
}
