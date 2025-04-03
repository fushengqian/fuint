package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.MemberGroupDto;
import com.fuint.common.dto.UserGroupDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MemberGroupService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtUserMapper;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserGroup;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员分组管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-会员分组相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/memberGroup")
public class BackendMemberGroupController extends BaseController {

    private MtUserMapper mtUserMapper;

    /**
     * 会员分组服务接口
     */
    private MemberGroupService memberGroupService;

    /**
     * 查询会员分组列表
     *
     * @param  request
     * @return
     * @throws BusinessCheckException
     */
    @ApiOperation(value = "查询会员分组列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:group:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? 1 : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name") == null ? "" : request.getParameter("name");
        String id = request.getParameter("id") == null ? "" : request.getParameter("id");
        String status = request.getParameter("status") == null ? StatusEnum.ENABLED.getKey() : request.getParameter("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtil.isNotEmpty(name)) {
            searchParams.put("name", name);
        }
        if (StringUtil.isNotEmpty(id)) {
            searchParams.put("id", id);
        }
        if (StringUtil.isNotEmpty(status)) {
            searchParams.put("status", status);
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            searchParams.put("merchantId", accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            searchParams.put("storeId", accountInfo.getStoreId());
        }

        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<UserGroupDto> paginationResponse = memberGroupService.queryMemberGroupListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 保存分组信息
     *
     * @param request
     * @param memberGroupDto
     * @return
     */
    @ApiOperation(value = "保存分组信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:group:index')")
    public ResponseObject save(HttpServletRequest request, @RequestBody MemberGroupDto memberGroupDto) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(201, "平台方帐号无法执行该操作，请使用商户帐号操作");
        }

        memberGroupDto.setMerchantId(accountInfo.getMerchantId());
        memberGroupDto.setStoreId(accountInfo.getStoreId());
        memberGroupDto.setOperator(accountInfo.getAccountName());
        if (memberGroupDto.getId() != null && memberGroupDto.getId() > 0) {
            memberGroupService.updateMemberGroup(memberGroupDto);
        } else {
            memberGroupService.addMemberGroup(memberGroupDto);
        }
        return getSuccessResult(true);
    }

    /**
     * 删除会员分组
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "删除会员分组")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:group:index')")
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        // 该分组已有会员，不允许删除
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("GROUP_ID", id.toString());
        searchParams.put("STATUS", StatusEnum.ENABLED.getKey());
        List<MtUser> dataList = mtUserMapper.selectByMap(searchParams);
        if (dataList.size() > 0) {
            return getFailureResult(201, "该分组下有会员，不能删除");
        }

        memberGroupService.deleteMemberGroup(id, accountInfo.getAccountName());

        return getSuccessResult(true);
    }

    /**
     * 更新分组状态
     *
     * @return
     */
    @ApiOperation(value = "更新分组状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:group:index')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        String operator = accountInfo.getAccountName();
        MemberGroupDto groupDto = new MemberGroupDto();
        groupDto.setOperator(operator);
        groupDto.setId(id);
        groupDto.setStatus(status);
        memberGroupService.updateMemberGroup(groupDto);

        return getSuccessResult(true);
    }

    /**
     * 获取分组详情
     *
     * @param groupId
     * @return
     */
    @ApiOperation(value = "获取分组详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:group:index')")
    public ResponseObject info(@PathVariable("id") Integer groupId) throws BusinessCheckException {
        MtUserGroup mtUserGroup = memberGroupService.queryMemberGroupById(groupId);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("groupInfo", mtUserGroup);

        return getSuccessResult(resultMap);
    }
}
