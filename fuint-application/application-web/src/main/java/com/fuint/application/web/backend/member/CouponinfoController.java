package com.fuint.application.web.backend.member;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.application.dao.entities.UvCouponInfo;
import com.fuint.application.dto.CouponTotalDto;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.member.UvCouponInfoService;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.web.backend.util.ExcelUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.fuint.application.util.XlsUtil.objectConvertToString;

/**
 * 会员卡券控制器controller
 * Created by zach on 2019-09-12
 */
@Controller
@RequestMapping(value = "/backend/member")
public class CouponinfoController {

    private static final Logger logger = LoggerFactory.getLogger(CouponinfoController.class);

    /**
     * 卡券信息服务接口
     */
    @Autowired
    private UvCouponInfoService uvCouponInfoService;
    /**
     * 店铺信息管理服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 后台员工账户服务接口
     */
    @Autowired
    private TAccountService tAccountService;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 会员卡券消费记录
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/member/usedCouponinfoQueryList")
    @RequestMapping(value = "/usedCouponinfoQueryList")
    public String couponinfoQueryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        DateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String GTE_usedTime = request.getParameter("GTE_usedTime");
        String LTE_usedTime = request.getParameter("LTE_usedTime");

        try {
            if (StringUtils.isNotEmpty(GTE_usedTime)) {
                params.put("GTE_usedTime", GTE_usedTime+" 00:00:00");
            }
            if (StringUtils.isNotEmpty(LTE_usedTime)) {
                params.put("LTE_usedTime", LTE_usedTime+" 23:59:59");
            }
        }
        catch (Exception e)
        {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }
        if (params == null || params.size()==0) {
            params = new HashMap<String, Object>();
            String mobile = request.getParameter("LIKE_mobile");
            String hnaName = request.getParameter("LIKE_hnaName");
            String groupId = request.getParameter("EQ_groupId");
            String couponGroupName = request.getParameter("LIKE_couponGroupName");
            String couponId = request.getParameter("EQ_couponId");
            String couponName = request.getParameter("LIKE_couponName");
            String storeId = request.getParameter("EQ_storeId");
            String couponInfoStatus = request.getParameter("EQ_couponInfoStatus");
            if (StringUtils.isNotEmpty(mobile)) {
                params.put("LIKE_mobile", mobile);
            }
            if (StringUtils.isNotEmpty(hnaName)) {
                params.put("LIKE_hnaName", hnaName);
            }
            if (StringUtils.isNotEmpty(groupId)) {
                params.put("EQ_groupId", Integer.parseInt(groupId));
            }
            if (StringUtils.isNotEmpty(couponGroupName)) {
                params.put("LIKE_couponGroupName", couponGroupName);
            }
            if (StringUtils.isNotEmpty(couponId)) {
                params.put("EQ_couponId", Integer.parseInt(couponId));
            }
            if (StringUtils.isNotEmpty(couponName)) {
                params.put("LIKE_couponName", couponName);
            }
            if (StringUtils.isNotEmpty(storeId)) {
                params.put("EQ_storeId", Integer.parseInt(storeId));
            }
            if (StringUtils.isNotEmpty(couponInfoStatus)) {
                params.put("EQ_couponInfoStatus", couponInfoStatus);
            } else {
                params.put("EQ_couponInfoStatus",UserCouponStatusEnum.USED.getKey());
            }
        }

        paginationRequest.setSortColumn(new String[]{"updateTime desc", "id desc"});
        paginationRequest.setSearchParams(params);

        List<MtStore> storeList;

        // 登录员工所属店铺处理
        Long accID = ShiroUserHelper.getCurrentShiroUser().getId();
        TAccount tAccount=tAccountService.findAccountById(accID);
        HashMap<String, Object> params_store = new HashMap<String, Object>();
        if (tAccount.getStoreId() == null || tAccount.getStoreId().equals(-1)) {
            //处理历史异常数据：没有选择店铺的情况
            if(tAccount.getStoreId()==null){
                params_store.put("EQ_storeId",0);
                params.put("EQ_storeId",0);
            }
            storeList = storeService.queryStoresByParams(new HashMap<>());
        } else {
            params_store.put("EQ_id",tAccount.getStoreId().intValue());
            params.put("EQ_storeId",tAccount.getStoreId().intValue());  //查询消费列表
            List<Integer> ids = new ArrayList<Integer>();
            ids.add(tAccount.getStoreId().intValue());
            storeList = storeService.queryStoresByIds(ids);
        }

        PaginationResponse<UvCouponInfo> paginationResponse = uvCouponInfoService.queryCouponInfoListByPagination(paginationRequest);
        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("storeList", storeList);
        model.addAttribute("params", params);

        return "member/member_user_coupon_list";
    }

    /**
     * 导出报表
     *
     * @return
     */
    @RequiresPermissions("backend/member/exportUsedCouponinfoQueryList")
    @RequestMapping(value = "/exportUsedCouponinfoQueryList")
    @ResponseBody
    public void export(HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();
        String GTE_usedTime = request.getParameter("GTE_usedTime");
        String LTE_usedTime = request.getParameter("LTE_usedTime");

        try {
            if (StringUtils.isNotEmpty(GTE_usedTime)) {
                params.put("GTE_usedTime", GTE_usedTime+" 00:00:00");
                //params.put("GTE_usedTime", dtformat.parse(GTE_usedTime+" 00:00:00"));
            }
            if (StringUtils.isNotEmpty(LTE_usedTime)) {
                params.put("LTE_usedTime", LTE_usedTime+" 23:59:59");
                // params.put("LTE_usedTime", dtformat.parse(LTE_usedTime+" 23:59:59"));
            }
        } catch (Exception e) {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }
        if (params == null || params.size() == 0) {
            params = new HashMap<String, Object>();
            String mobile = request.getParameter("LIKE_mobile");
            String hnaName = request.getParameter("LIKE_hnaName");
            String groupId = request.getParameter("EQ_groupId");
            String couponGroupName = request.getParameter("LIKE_couponGroupName");
            String couponId = request.getParameter("EQ_couponId");
            String couponName = request.getParameter("LIKE_couponName");
            String storeId = request.getParameter("EQ_storeId");
            String couponInfoStatus = request.getParameter("EQ_couponInfoStatus");
            if (StringUtils.isNotEmpty(mobile)) {
                params.put("LIKE_mobile", mobile);
            }
            if (StringUtils.isNotEmpty(hnaName)) {
                params.put("LIKE_hnaName", hnaName);
            }
            if (StringUtils.isNotEmpty(groupId)) {
                params.put("EQ_groupId", Integer.parseInt(groupId));
            }
            if (StringUtils.isNotEmpty(couponGroupName)) {
                params.put("LIKE_couponGroupName", couponGroupName);
            }
            if (StringUtils.isNotEmpty(couponId)) {
                params.put("EQ_couponId", Integer.parseInt(couponId));
            }
            if (StringUtils.isNotEmpty(couponName)) {
                params.put("LIKE_couponName", couponName);
            }
            if (StringUtils.isNotEmpty(storeId)) {
                params.put("EQ_storeId", Integer.parseInt(storeId));
            }
            if (StringUtils.isNotEmpty(couponInfoStatus)) {
                params.put("EQ_couponInfoStatus", couponInfoStatus);
            } else {
                params.put("EQ_couponInfoStatus",UserCouponStatusEnum.USED.getKey());
            }
        }

        //排序字段，SQL数据库字段名称
        params.put("sort_type_custom","UPDATE_TIME DESC");

        //查询限制行数50000,默认5万
        params.put("limit_rownum_custom"," 50000");

        //获取数据
        List<UvCouponInfo> list = uvCouponInfoService.queryCouponInfoByParams(params);
        //excel标题
        String[] title = {"记录ID", "用户手机号", "FuInt账号", "消费分组ID"
                , "消费分组名称", "消费券ID","消费券名称","金额","使用店铺","消费时间"};

        //excel文件名
        String fileName = "会员消费记录" + System.currentTimeMillis() + ".xls";

        //sheet名
        String sheetName = "会员消费记录";

        String[][] content=null;
        if (list.size()>0) {
            content= new String[list.size()][title.length];
        }

        for (int i = 0; i < list.size(); i++) {
            UvCouponInfo obj = list.get(i);
            content[i][0] = objectConvertToString(obj.getId());
            content[i][1] = objectConvertToString(obj.getMobile());
            content[i][3] = objectConvertToString(obj.getGroupId());
            content[i][4] = objectConvertToString(obj.getCouponGroupName());
            content[i][5] = objectConvertToString(obj.getCouponId());
            content[i][6] = objectConvertToString(obj.getCouponName());
            content[i][7] = objectConvertToString(obj.getMoney());
            content[i][8] = objectConvertToString(obj.getStoreName());
            content[i][9] = objectConvertToString(obj.getUsedTime());
        }

        //创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);

        ExcelUtil.setResponseHeader(response, fileName, wb);
    }

    /**
     * 会员卡券列表
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/member/CouponinfoList")
    @RequestMapping(value = "/CouponinfoList")
    public String CouponinfoList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();
        if (params == null || params.size() == 0) {
            params = new HashMap<>();
            String mobile = request.getParameter("LIKE_mobile");
            String groupId = request.getParameter("EQ_groupId");
            String couponGroupName = request.getParameter("LIKE_couponGroupName");
            String couponId = request.getParameter("EQ_couponId");
            String couponName = request.getParameter("LIKE_couponName");
            String storeId = request.getParameter("EQ_storeId");
            String suitStoreIds = request.getParameter("LIKE_suitStoreIds");
            String couponInfoStatus = request.getParameter("EQ_couponInfoStatus");
            String uuid = request.getParameter("EQ_uuid");

            if (StringUtils.isNotEmpty(mobile)) {
                params.put("LIKE_mobile", CommonUtil.filter(mobile));
            }
            if (StringUtils.isNotEmpty(groupId)) {
                params.put("EQ_groupId", Integer.parseInt(groupId));
            }
            if (StringUtils.isNotEmpty(couponGroupName)) {
                params.put("LIKE_couponGroupName", CommonUtil.filter(couponGroupName));
            }
            if (StringUtils.isNotEmpty(couponId)) {
                params.put("EQ_couponId", Integer.parseInt(couponId));
            }
            if (StringUtils.isNotEmpty(couponName)) {
                params.put("LIKE_couponName", CommonUtil.filter(couponName));
            }
            if (StringUtils.isNotEmpty(suitStoreIds)) {
                params.put("LIKE_suitStoreIds",CommonUtil.filter(suitStoreIds));
            }
            if (StringUtils.isNotEmpty(storeId)) {
                params.put("EQ_storeId",Integer.parseInt(storeId));
            }
            if (StringUtils.isNotEmpty(couponInfoStatus)) {
                params.put("EQ_couponInfoStatus", CommonUtil.filter(couponInfoStatus));
            }
            if (StringUtils.isNotEmpty(uuid)) {
                params.put("EQ_uuid", uuid);
            }
        }

        paginationRequest.setSearchParams(params);
        PaginationResponse<UvCouponInfo> paginationResponse = uvCouponInfoService.queryCouponInfoListByPagination(paginationRequest);
        CouponTotalDto couponTotalDto = uvCouponInfoService.queryCouponInfoTotalByParams(params);

        // 状态更新
        String usercouponstatus;
        for (UvCouponInfo u :paginationResponse.getContent()) {
            usercouponstatus = u.getCouponInfoStatus();
            u.setCouponInfoStatusDesc(UserCouponStatusEnum.getValue(usercouponstatus));
        }

        List<MtStore> storeList = storeService.queryStoresByParams(new HashMap<>());

        model.addAttribute("storeList", storeList);
        model.addAttribute("couponTotalDto", couponTotalDto);
        model.addAttribute("params", params);
        model.addAttribute("paginationResponse", paginationResponse);

        return "coupon/userCoupon";
    }

    /**
     * 导出报表
     *
     * @return
     */
    @RequiresPermissions("backend/member/exportCouponinfoList")
    @RequestMapping(value = "/exportCouponinfoList")
    @ResponseBody
    public void exportCouponinfoList(HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();
        if (params == null || params.size()==0) {
            params = new HashMap<String, Object>();
            String mobile = request.getParameter("LIKE_mobile");
            String hnaName = request.getParameter("LIKE_hnaName");
            String groupId = request.getParameter("EQ_groupId");
            String couponGroupName = request.getParameter("LIKE_couponGroupName");
            String couponId = request.getParameter("EQ_couponId");
            String couponName = request.getParameter("LIKE_couponName");
            String storeId = request.getParameter("EQ_storeId");
            String suitStoreIds = request.getParameter("LIKE_suitStoreIds");
            String couponInfoStatus = request.getParameter("EQ_couponInfoStatus");
            String isHna = request.getParameter("EQ_isHna");
            String uuid = request.getParameter("EQ_uuid");
            if (StringUtils.isNotEmpty(mobile)) {
                params.put("LIKE_mobile", CommonUtil.filter(mobile));
            }
            if (StringUtils.isNotEmpty(hnaName)) {
                params.put("LIKE_hnaName", CommonUtil.filter(hnaName));
            }
            if (StringUtils.isNotEmpty(groupId)) {
                params.put("EQ_groupId", Integer.parseInt(groupId));
            }
            if (StringUtils.isNotEmpty(couponGroupName)) {
                params.put("LIKE_couponGroupName", CommonUtil.filter(couponGroupName));
            }
            if (StringUtils.isNotEmpty(couponId)) {
                params.put("EQ_couponId", Integer.parseInt(couponId));
            }
            if (StringUtils.isNotEmpty(couponName)) {
                params.put("LIKE_couponName", CommonUtil.filter(couponName));
            }
            if (StringUtils.isNotEmpty(suitStoreIds)) {
                params.put("LIKE_suitStoreIds",suitStoreIds);
            }
            if (StringUtils.isNotEmpty(storeId)) {
                params.put("EQ_storeId",Integer.parseInt(storeId));
            }
            if (StringUtils.isNotEmpty(couponInfoStatus)) {
                params.put("EQ_couponInfoStatus", CommonUtil.filter(couponInfoStatus));
            }

            if (StringUtils.isNotEmpty(isHna)) {
                params.put("EQ_isHna", CommonUtil.filter(isHna));
            }

            if (StringUtils.isNotEmpty(uuid)) {
                params.put("EQ_uuid", CommonUtil.filter(uuid));
            }
        }
        //排序字段，SQL数据库字段名称
        params.put("sort_type_custom","UPDATE_TIME DESC");

        //查询限制行数50000,默认5万
        params.put("limit_rownum_custom"," 50000");
        //获取数据
        List<UvCouponInfo> list = uvCouponInfoService.queryCouponInfoByParams(params);
        //excel标题
        String[] title = {"最后操作时间", "消费分组ID", "消费分组名称", "消费券ID"
                , "消费券名称", "用户手机号","FuInt用户","FuInt账号","状态","面额","使用店铺","使用时间"};
        //excel文件名
        String fileName = "会员卡券列表.xls";
        //sheet名
        String sheetName = "数据列表";

        String[][] content=null;
        if (list.size() > 0) {
            content= new String[list.size()][title.length];
        }

        for (int i = 0; i < list.size(); i++) {
            UvCouponInfo obj = list.get(i);
            content[i][0] = objectConvertToString(obj.getUpdateTime());
            content[i][1] = objectConvertToString(obj.getGroupId());
            content[i][2] = objectConvertToString(obj.getCouponGroupName());
            content[i][3] = objectConvertToString(obj.getCouponId());
            content[i][4] = objectConvertToString(obj.getCouponName());
            content[i][5] = objectConvertToString(obj.getMobile());
            content[i][6] = UserCouponStatusEnum.getValue(obj.getCouponInfoStatus());
            content[i][7] = objectConvertToString(obj.getMoney().toString());
            content[i][8] = objectConvertToString(obj.getStoreName());
            content[i][9] = objectConvertToString(obj.getUsedTime());
        }

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);

        ExcelUtil.setResponseHeader(response, fileName, wb);

        return;
    }

    /**
     * 核销页面
     * */
    @RequiresPermissions("backend/member/confirmerUserCouponPage/{id}")
    @RequestMapping(value = "/confirmerUserCouponPage/{id}")
    public String confirmerUserCouponPage(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {

        UvCouponInfo uvCounponInfo=uvCouponInfoService.queryUvCouponInfoById(id.intValue());
        if (uvCounponInfo==null) {
            throw new BusinessCheckException("用户卡券不存在.");
        }

        String StringIds=uvCounponInfo.getSuitStoreIds();
        if (StringUtils.isEmpty(StringIds)) {
            StringIds = "";
        }
        String[] arrayIds=StringIds.split(",");
        List<Integer> storeIds=new ArrayList<Integer>();
        List<MtStore> storeList=null;
        try {
            for (String s : arrayIds) {
                if(s.length()>0) {
                    storeIds.add(Integer.valueOf(s));
                }
            }
        } catch (Exception e) {

            storeIds = null;
        }

        if (storeIds==null || storeIds.size() == 0) {
            storeList = storeService.queryStoresByParams(new HashMap<>());
        } else {
            storeList = storeService.queryStoresByIds(storeIds);
        }

        model.addAttribute("uvCounponInfo", uvCounponInfo);
        model.addAttribute("storeList", storeList);

        return "components/confirmerCouponQuickPage";
    }

    /**
     * 核销用户卡券,刷新页面
     * */
    @RequiresPermissions("backend/member/stringconfirmerUserCoupon")
    @RequestMapping(value = "/stringconfirmerUserCoupon")
    public String stringconfirmerUserCoupon(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String id = request.getParameter("id");
        String storeId = request.getParameter("storeId");
        MtUserCoupon mtUserCoupon = couponService.queryUserCouponById(Integer.parseInt(id));

        if (mtUserCoupon == null) {
            throw new BusinessCheckException("用户卡券不存在.");
        }

        MtStore store = storeService.queryStoreById(Integer.parseInt(storeId));

        if ( store == null) {
            throw new BusinessCheckException("店铺不存在.");
        }

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (null == shiroUser) {
            return "redirect:/login";
        }

        couponService.useCoupon(Long.parseLong(id),shiroUser.getId().intValue(),Integer.parseInt(storeId));

        return "redirect:/backend/member/CouponinfoList";
    }
}
