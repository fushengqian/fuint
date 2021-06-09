package com.fuint.application.web.backend.confirmLog;

import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dto.ConfirmLogDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.confirmlog.ConfirmLogService;
import com.fuint.application.service.store.StoreService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static com.fuint.application.util.XlsUtil.objectConvertToString;
import java.util.*;

/**
 * 会员卡券核销统计controller
 * Created by zach on 2020-04-17
 */
@Controller
@RequestMapping(value = "/backend/confirmLog")
public class ConfirmLogController {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmLogController.class);

    /**
     * 卡券核销流水接口
     */
    @Autowired
    private ConfirmLogService ConfirmLogService;

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
     * 会员卡券核销记录列表
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/confirmLog/confirmLogList")
    @RequestMapping(value = "/confirmLogList")
    public String confirmLogList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        String GTE_usedTime = request.getParameter("GTE_usedTime");
        String LTE_usedTime = request.getParameter("LTE_usedTime");

        params = this._setDateFormat(request, params, GTE_usedTime, LTE_usedTime);

        paginationRequest.setSortColumn(new String[]{"updateTime desc", "id desc"});
        paginationRequest.setSearchParams(params);

        List<MtStore> storeList;

        // 登录员工所属店铺处理
        Long accID = ShiroUserHelper.getCurrentShiroUser().getId();
        TAccount tAccount = tAccountService.findAccountById(accID);
        HashMap<String, Object> params_store = new HashMap<String, Object>();
        if (tAccount.getStoreId() == null || tAccount.getStoreId().equals(-1)) {
            // 处理历史异常数据：没有选择店铺的情况
            if (tAccount.getStoreId() == null) {
                params_store.put("EQ_id", "0");
                params.put("EQ_storeId", "0");
            }
            storeList = storeService.queryStoresByParams(params_store);
        } else {
            params_store.put("EQ_id",tAccount.getStoreId().toString());
            params.put("EQ_storeId",tAccount.getStoreId().toString());
            List<Integer> ids = new ArrayList<Integer>();
            ids.add(tAccount.getStoreId().intValue());
            storeList = storeService.queryStoresByIds(ids);
        }

        PaginationResponse<ConfirmLogDto> paginationResponse = ConfirmLogService.queryConfirmLogListByPagination(paginationRequest);
        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("storeList", storeList);
        model.addAttribute("params", params);

        return "confirmLog/confirmLogList";
    }

    /**
     * 导出报表
     *
     * @return
     */
    @RequiresPermissions("backend/confirmLog/exportConfirmLogList")
    @RequestMapping(value = "/exportConfirmLogList")
    @ResponseBody
    public void export(HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();
        String GTE_usedTime = request.getParameter("GTE_usedTime");
        String LTE_usedTime = request.getParameter("LTE_usedTime");

        params = this._setDateFormat(request, params, GTE_usedTime, LTE_usedTime);

        //排序字段，SQL数据库字段名称
        params.put("sort_type_custom","ConfirmTime DESC");

        //查询限制行数50000,默认5万
        params.put("limit_rownum_custom"," 50000");

        //获取数据
        List<ConfirmLogDto> list = ConfirmLogService.queryConfirmLogListByParams(params);

        // excel标题
        String[] title = {"记录流水号", "用户手机号", "消费分组ID"
                        , "消费分组名称", "消费券ID","消费券名称","金额","使用店铺","消费时间","状态"};

        //excel文件名
        String fileName = "会员消费记录" + System.currentTimeMillis() + ".xls";

        //sheet名
        String sheetName = "会员消费记录";

        String[][] content=null;
        if (list.size()>0) {
            content= new String[list.size()][title.length];
        }

        for (int i = 0; i < list.size(); i++) {
            ConfirmLogDto obj = list.get(i);
            content[i][0] = objectConvertToString(obj.getCode());
            content[i][1] = objectConvertToString(obj.getMobile());
            content[i][2] = objectConvertToString(obj.getGroupId());
            content[i][3] = objectConvertToString(obj.getCouponGroupName());
            content[i][4] = objectConvertToString(obj.getCouponId());
            content[i][5] = objectConvertToString(obj.getCouponName());
            content[i][6] = objectConvertToString(obj.getMoney());
            content[i][7] = objectConvertToString(obj.getStoreName());
            content[i][8] = objectConvertToString(obj.getConfirmTime());
            content[i][9] = obj.getConfirmStatus().equals(StatusEnum.ENABLED.getKey())? "已使用":"已撤销";
        }

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);

        ExcelUtil.setResponseHeader(response, fileName, wb);

        return;
    }


    private Map<String, Object> _setDateFormat(HttpServletRequest request, Map<String, Object> params, String GTE_usedTime, String LTE_usedTime) {
        try {
            if (StringUtils.isNotEmpty(GTE_usedTime)) {
                params.put("GTE_usedTime", CommonUtil.filter(GTE_usedTime)+" 00:00:00");
            }
            if (StringUtils.isNotEmpty(LTE_usedTime)) {
                params.put("LTE_usedTime", CommonUtil.filter(LTE_usedTime)+" 23:59:59");
            }
        } catch (Exception e) {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }

        if (params == null || params.size() == 0) {
            params = new HashMap<>();
            String mobile = request.getParameter("LIKE_mobile");
            String hnaName = request.getParameter("LIKE_hnaName");
            String groupId = request.getParameter("EQ_groupId");
            String couponGroupName = request.getParameter("LIKE_couponGroupName");
            String couponId = request.getParameter("EQ_couponId");
            String couponName = request.getParameter("LIKE_couponName");
            String storeId = request.getParameter("EQ_storeId");
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
            if (StringUtils.isNotEmpty(storeId)) {
                params.put("EQ_storeId", Integer.parseInt(storeId));
            }
        }

        return params;
    }
}
