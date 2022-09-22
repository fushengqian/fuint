package com.fuint.application.web.backendApi;

import javax.servlet.http.HttpServletRequest;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.enums.AdminRoleEnum;
import com.fuint.base.dao.entities.TDuty;
import com.fuint.base.dao.entities.TSource;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.duty.TDutyService;
import com.fuint.base.service.source.TSourceService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import com.fuint.application.dto.RoleDto;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台角色管理控制类
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/duty")
public class BackendDutyController extends BaseController {

    @Autowired
    private TDutyService tDutyService;

    @Autowired
    private TSourceService tSourceService;

    /**
     * 角色列表
     *
     * @param request  HttpServletRequest对象
     * @return 角色信息列表
     */
    @RequestMapping(value = "/list")
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name") == null ? "" : request.getParameter("name");
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        if (StringUtil.isNotEmpty(name)) {
            searchParams.put("EQ_name", name);
        }
        if (StringUtil.isNotEmpty(status)) {
            searchParams.put("EQ_status", status);
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"status asc"});

        PaginationResponse<TDuty> paginationResponse = tDutyService.findDutiesByPagination(paginationRequest);
        List<RoleDto> content = new ArrayList<>();
        if (paginationResponse.getContent().size() > 0) {
            for (TDuty tDuty : paginationResponse.getContent()) {
                 RoleDto dto = new RoleDto();
                 dto.setId(tDuty.getId());
                 dto.setName(tDuty.getName());
                 String type = AdminRoleEnum.getName(tDuty.getDutyType());
                 dto.setType(type);
                 dto.setStatus(tDuty.getStatus());
                 content.add(dto);
            }
        }

        PageRequest pageRequest = new PageRequest((paginationRequest.getCurrentPage() + 1), paginationRequest.getPageSize());
        Page pageImpl = new PageImpl(content, pageRequest, paginationResponse.getTotalElements());
        PaginationResponse<RoleDto> result = new PaginationResponse(pageImpl, AccountDto.class);
        result.setTotalPages(paginationResponse.getTotalPages());
        result.setTotalElements(paginationResponse.getTotalElements());
        result.setContent(content);

        return getSuccessResult(result);
    }

    /**
     * 新增角色
     *
     * @param request  HttpServletRequest对象
     * @return 角色列表页面
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject addHandler(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        List<Integer> menuIds = (List) param.get("menuIds");
        String name = param.get("roleName").toString();
        String type = param.get("roleType").toString();
        String status = param.get("status").toString();
        String description = param.get("description").toString();

        // 获取角色所分配的菜单
        List<TSource> sources = null;

        if (menuIds.size() > 0) {
            String[] sourceIds = new String[menuIds.size()];
            for (int i = 0; i < sourceIds.length; i++) {
                sourceIds[i] = menuIds.get(i).toString();
            }
            sources = tSourceService.findDatasByIds(sourceIds);
        }

        TDuty tDuty = new TDuty();
        tDuty.setName(name);
        tDuty.setDutyType(type);
        tDuty.setStatus(status);
        tDuty.setDescribe(description);

        // 添加角色信息
        tDutyService.saveDuty(tDuty, sources);

        return getSuccessResult(true);
    }

    /**
     * 角色详情
     *
     * @param roleId
     * @return 账户信息
     */
    @RequestMapping(value = "/info/{roleId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(@PathVariable("roleId") Long roleId) {
        TDuty htDuty = tDutyService.getRoleById(roleId);

        Map<String, Object> result = new HashMap<>();

        RoleDto roleInfo = new RoleDto();
        roleInfo.setId(htDuty.getId());
        roleInfo.setName(htDuty.getName());
        roleInfo.setType(htDuty.getDutyType());
        roleInfo.setStatus(htDuty.getStatus());
        roleInfo.setDescription(htDuty.getDescribe());

        result.put("roleInfo", roleInfo);
        List<Long> checkedKeys = tDutyService.getSourceIdsByDutyId(roleId);
        if (checkedKeys != null && checkedKeys.size() > 0) {
            result.put("checkedKeys", checkedKeys);
        }

        return getSuccessResult(result);
    }

    /**
     * 修改角色处理
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateHandler(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        List<Integer> menuIds = (List) param.get("menuIds");
        String id = param.get("id").toString();
        String name = param.get("roleName").toString();
        String type = param.get("roleType").toString();
        String status = param.get("status").toString();
        String description = param.get("description").toString();

        if (StringUtil.isEmpty(id)) {
            return getFailureResult(201, "信息提交有误");
        }

        TDuty duty = tDutyService.getRoleById(Long.parseLong(id));
        duty.setDescribe(description);
        duty.setName(name);
        duty.setStatus(status);
        duty.setDutyType(type);

        // 获取角色所分配的菜单
        List<TSource> sources = null;
        if (menuIds.size() > 0) {
            String[] sourceIds = new String[menuIds.size()];
            for (int i = 0; i < sourceIds.length; i++) {
                sourceIds[i] = menuIds.get(i).toString();
            }
            sources = tSourceService.findDatasByIds(sourceIds);
        }

        tDutyService.updateDuty(duty, sources);

        return getSuccessResult(true);
    }

    /**
     * 删除角色信息
     *
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/delete/{roleId}", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject deleteAccount(@PathVariable("roleId") Long roleId) throws BusinessCheckException {
        tDutyService.deleteDuty(roleId);
        return getSuccessResult(true);
    }
}
