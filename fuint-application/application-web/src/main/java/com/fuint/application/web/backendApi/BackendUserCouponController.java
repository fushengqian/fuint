package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtSendLog;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.repositories.MtSendLogRepository;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.dto.ParamDto;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.sendlog.SendLogService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.util.DateUtil;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.web.backend.util.ExcelUtil;
import com.fuint.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static com.fuint.application.util.XlsUtil.objectConvertToString;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * 卡券统计管理类controller
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/userCoupon")
public class BackendUserCouponController extends BaseController {

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

    @Autowired
    private SendLogService sendLogService;

    /**
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private MtSendLogRepository sendLogRepository;

    /**
     * 查询会员卡券列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("pageNumber", page);
        param.put("pageSize", pageSize);
        param.put("status", request.getParameter("status"));
        param.put("userId", request.getParameter("userId"));
        param.put("mobile", request.getParameter("mobile"));
        param.put("storeId", request.getParameter("storeId"));
        param.put("couponId", request.getParameter("couponId"));
        param.put("type", request.getParameter("type"));
        param.put("code", request.getParameter("code"));

        ResponseObject result = userCouponService.getUserCouponList(param);

        Map<String, Object> storeParams = new HashMap<>();
        storeParams.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(storeParams);

        // 卡券类型列表
        CouponTypeEnum[] typeListEnum = CouponTypeEnum.values();
        List<ParamDto> typeList = new ArrayList<>();
        for (CouponTypeEnum enumItem : typeListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            typeList.add(paramDto);
        }

        // 卡券状态列表
        UserCouponStatusEnum[] statusListEnum = UserCouponStatusEnum.values();
        List<ParamDto> statusList = new ArrayList<>();
        for (UserCouponStatusEnum enumItem : statusListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            statusList.add(paramDto);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("paginationResponse", result.getData());
        data.put("storeList", storeList);
        data.put("typeList", typeList);
        data.put("statusList", statusList);

        return getSuccessResult(data);
    }

    /**
     * 核销用户卡券
     * */
    @RequestMapping(value = "/doConfirm", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject doConfirm(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String userCouponId = request.getParameter("userCouponId");
        MtUserCoupon mtUserCoupon = couponService.queryUserCouponById(Integer.parseInt(userCouponId));

        if (mtUserCoupon == null || StringUtil.isEmpty(userCouponId)) {
            throw new BusinessCheckException("错误，用户卡券不存在！");
        }

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        TAccount account = accountService.findAccountById(accountInfo.getId());
        Integer storeId = account.getStoreId();

        BigDecimal confirmAmount = mtUserCoupon.getAmount();
        if (mtUserCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            confirmAmount = mtUserCoupon.getBalance();
        }

        try {
            couponService.useCoupon(Integer.parseInt(userCouponId), accountInfo.getId().intValue(), storeId, 0, confirmAmount, "后台核销");
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        return getSuccessResult(true);
    }

    /**
     * 作废会员卡券
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        try {
            couponService.deleteUserCoupon(id, accountInfo.getAccountName());
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        // 发券记录，部分作废
        MtUserCoupon userCoupon = userCouponRepository.findOne(id);
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(Constants.PAGE_NUMBER);
        paginationRequest.setPageSize(Constants.MAX_ROWS);
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("EQ_uuid", userCoupon.getUuid());
        paginationRequest.setSearchParams(requestParams);
        PaginationResponse<MtSendLog> list = sendLogService.querySendLogListByPagination(paginationRequest);
        if (list.getContent().size() > 0) {
            MtSendLog sendLog = list.getContent().get(0);
            if (sendLog.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
                Integer total = sendLog.getRemoveSuccessNum();
                if (null == total) {
                    total = 0;
                }
                sendLog.setRemoveSuccessNum((total + 1));
                sendLog.setStatus(UserCouponStatusEnum.USED.getKey());
                sendLogRepository.save(sendLog);
            }
        }

        return getSuccessResult(true);
    }

    /**
     * 导出二维码
     *
     * @return
     */
    @RequestMapping(value = "/exportList", method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin
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
    }
}
