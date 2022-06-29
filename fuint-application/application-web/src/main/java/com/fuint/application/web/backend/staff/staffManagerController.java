package com.fuint.application.web.backend.staff;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dao.entities.MtStaff;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.staff.StaffService;
import com.fuint.application.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 员工管理
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/staff")
public class staffManagerController {

    /**
     * 员工接口
     */
    @Autowired
    private StaffService staffService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 员工列表查询
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return 员工列表页面
     */
    @RequiresPermissions("backend/staff/queryList")
    @RequestMapping(value = "/queryList")
    public String queryList(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        String mobile = request.getParameter("LIKE_mobile");
        String auditedStatus = request.getParameter("EQ_auditedStatus");
        String storeId = request.getParameter("EQ_storeId");

        Map<String, Object> params = paginationRequest.getSearchParams();
        Map<String, Object> paramsStore = new HashMap<>();
        if (StringUtils.isNotEmpty(mobile)) {
            paramsStore.put("EQ_mobile", mobile);
        }
        if (StringUtils.isNotEmpty(auditedStatus)) {
            params.put("EQ_auditedStatus", auditedStatus);
        }
        if (StringUtils.isNotEmpty(storeId)) {
            paramsStore.put("EQ_storeId", storeId);
        }

        params.put("NQ_auditedStatus", StatusEnum.DISABLE.getKey());
        paginationRequest.setSearchParams(params);
        paginationRequest.setSortColumn(new String[]{"auditedStatus asc", "id asc"});
        PaginationResponse<MtStaff> paginationResponse = staffService.queryStaffListByPagination(paginationRequest);
        for (MtStaff m : paginationResponse.getContent()) {
            MtStore mtStore = storeService.queryStoreById(m.getStoreId());
            if (mtStore != null) {
                m.setStoreName(mtStore.getName());
            }
        }

        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);
        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("params", params);
        model.addAttribute("storeList", storeList);

        return "staff/list";
    }

    /**
     * 审核
     * @return
     */
    @RequiresPermissions("backend/staff/updateStatus")
    @RequestMapping(value = "/updateStatus")
    public String updateStatus(HttpServletRequest request) throws BusinessCheckException {
        String status = request.getParameter("status") != null ? request.getParameter("status"): StatusEnum.ENABLED.getKey();
        Integer id = request.getParameter("id") == null ? 0 : Integer.parseInt(request.getParameter("id"));

        List<Integer> ids = new ArrayList<>();
        ids.add(id.intValue());

        staffService.updateAuditedStatus(ids, status);

        return "redirect:/backend/staff/queryList";
    }

    /**
     * 编辑初始化页面
     * @param model
     * @return
     */
    @RequiresPermissions("backend/staff/add")
    @RequestMapping(value = "/add")
    public String addInit(Model model) throws BusinessCheckException {
        Map<String, Object> params = new HashMap<>();
        params.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(params);

        model.addAttribute("storeList", storeList);

        return "staff/edit";
    }

    /**
     * 编辑初始化页面
     * @param model
     * @return
     */
    @RequiresPermissions("backend/staff/staffEditInit/{id}")
    @RequestMapping(value = "/staffEditInit/{id}")
    public String staffEditInit(Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        MtStaff mtStaff = staffService.queryStaffById(id.intValue());

        Map<String, Object> params_store = new HashMap<>();
        params_store.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(params_store);

        model.addAttribute("staff", mtStaff);
        model.addAttribute("storeList", storeList);

        return "staff/edit";
    }

    /**
     * 审核通过
     *
     * @param request
     * @return
     */
    @RequiresPermissions("backend/staff/doEdit")
    @RequestMapping(value = "/doEdit")
    public String doEdit(HttpServletRequest request) throws BusinessCheckException {
        String id_str = request.getParameter("id");
        Integer id = 0;
        if (StringUtils.isNotEmpty(id_str)) {
            id = Integer.parseInt(id_str);
        }

        String storeId_str = request.getParameter("storeId");
        Integer storeId = 0;
        if (StringUtils.isNotEmpty(storeId_str)) {
            storeId = Integer.parseInt(storeId_str);
        }

        String mobile = CommonUtil.replaceXSS(request.getParameter("mobile"));
        String realName = CommonUtil.replaceXSS(request.getParameter("realName"));
        String description = CommonUtil.replaceXSS(request.getParameter("description"));
        String status = request.getParameter("status") == null ? StatusEnum.FORBIDDEN.getKey(): CommonUtil.replaceXSS(request.getParameter("status"));

        MtStaff mtStaff = staffService.queryStaffById(id);
        if (mtStaff == null && id > 0) {
            throw new BusinessRuntimeException("记录不存在!");
        }

        if (null == mtStaff) {
            mtStaff = new MtStaff();
        }

        mtStaff.setStoreId(storeId);
        mtStaff.setRealName(realName);
        mtStaff.setMobile(mobile);
        mtStaff.setAuditedStatus(status);
        mtStaff.setDescription(description);

        if (StringUtils.isEmpty(mtStaff.getMobile())) {
            throw new BusinessRuntimeException("手机号码不能为空");
        } else {
            MtStaff tempUser = staffService.queryStaffByMobile(mtStaff.getMobile());
            if (null != tempUser && tempUser.getId() != mtStaff.getId()) {
                throw new BusinessCheckException("该会员手机号码已经存在!");
            }
        }

        staffService.addStaff(mtStaff);

        return "redirect:/backend/staff/queryList";
    }
}
