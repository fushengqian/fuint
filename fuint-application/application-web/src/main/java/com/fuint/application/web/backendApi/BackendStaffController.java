package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.service.token.TokenService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dao.entities.MtStaff;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.staff.StaffService;
import com.fuint.application.util.CommonUtil;
import com.fuint.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 员工管理
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/staff")
public class BackendStaffController extends BaseController {

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
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

    /**
     * 员工列表查询
     *
     * @param request  HttpServletRequest对象
     * @return 员工列表页面
     */
    @RequestMapping(value = "/list")
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String mobile = request.getParameter("mobile");
        String realName = request.getParameter("realName");
        String auditedStatus = request.getParameter("status");
        String storeId = request.getParameter("storeId");

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(realName)) {
            params.put("LIKE_realName", realName);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            params.put("EQ_mobile", mobile);
        }
        if (StringUtil.isNotEmpty(auditedStatus)) {
            params.put("EQ_auditedStatus", auditedStatus);
        }
        if (StringUtil.isNotEmpty(storeId)) {
            params.put("EQ_storeId", storeId);
        }

        params.put("NQ_auditedStatus", StatusEnum.DISABLE.getKey());
        paginationRequest.setSearchParams(params);
        paginationRequest.setSortColumn(new String[]{"auditedStatus asc", "id desc"});
        PaginationResponse<MtStaff> paginationResponse = staffService.queryStaffListByPagination(paginationRequest);
        for (MtStaff m : paginationResponse.getContent()) {
             MtStore mtStore = storeService.queryStoreById(m.getStoreId());
             if (mtStore != null) {
                 m.setStoreName(mtStore.getName());
             }
        }

        return getSuccessResult(paginationResponse);
    }

    /**
     * 更新员工状态
     * @return
     */
    @RequestMapping(value = "/updateStatus")
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        List<Integer> ids = new ArrayList<>();
        ids.add(id.intValue());

        staffService.updateAuditedStatus(ids, status);

        return getSuccessResult(true);
    }

    /**
     * 保存员工信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/save")
    @CrossOrigin
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String id = params.get("id") == null ? "0" : params.get("id").toString();
        String storeId = params.get("storeId") == null ? "0" : params.get("storeId").toString();
        String mobile = params.get("mobile") == null ? "" : CommonUtil.replaceXSS(params.get("mobile").toString());
        String realName = params.get("realName") == null ? "" : CommonUtil.replaceXSS(params.get("realName").toString());
        String description = CommonUtil.replaceXSS(params.get("description").toString());
        String status = params.get("auditedStatus") == null ? StatusEnum.FORBIDDEN.getKey() : CommonUtil.replaceXSS(params.get("auditedStatus").toString());

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        MtStaff mtStaff = new MtStaff();
        if (StringUtil.isNotEmpty(id)) {
            mtStaff = staffService.queryStaffById(Integer.parseInt(id));
        }

        if (mtStaff == null && StringUtil.isNotEmpty(id)) {
            return getFailureResult(201, "员工信息不存在");
        }

        mtStaff.setStoreId(Integer.parseInt(storeId));
        mtStaff.setRealName(realName);
        mtStaff.setMobile(mobile);
        mtStaff.setAuditedStatus(status);
        mtStaff.setDescription(description);

        if (StringUtil.isEmpty(mtStaff.getMobile())) {
            return getFailureResult(201, "手机号码不能为空");
        } else {
            MtStaff tempUser = staffService.queryStaffByMobile(mtStaff.getMobile());
            if (tempUser  != null && tempUser.getId() != mtStaff.getId()) {
                return getFailureResult(201, "该手机号码已经存在");
            }
        }

        staffService.saveStaff(mtStaff);

        return getSuccessResult(true);
    }

    /**
     * 查询员工信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/info/{id}")
    @CrossOrigin
    public ResponseObject getStaffInfo(@PathVariable("id") Integer id) throws BusinessCheckException {
        MtStaff staffInfo = staffService.queryStaffById(id);

        Map<String, Object> result = new HashMap<>();
        result.put("staffInfo", staffInfo);

        return getSuccessResult(result);
    }
}
