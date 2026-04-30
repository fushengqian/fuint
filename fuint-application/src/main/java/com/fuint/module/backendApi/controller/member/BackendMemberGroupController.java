package com.fuint.module.backendApi.controller.member;

import com.fuint.common.dto.member.MemberGroupDto;
import com.fuint.common.dto.member.UserGroupDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.MemberGroupPage;
import com.fuint.common.param.StatusParam;
import com.fuint.common.service.MemberGroupService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtUserMapper;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
     */
    @ApiOperation(value = "查询会员分组列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:group:index')")
    public ResponseObject list(@ModelAttribute MemberGroupPage memberGroupPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            memberGroupPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            memberGroupPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<UserGroupDto> paginationResponse = memberGroupService.queryMemberGroupListByPagination(memberGroupPage);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 保存分组信息
     */
    @ApiOperation(value = "保存分组信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:group:index')")
    public ResponseObject save(@RequestBody MemberGroupDto memberGroupDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(5002);
        }

        memberGroupDto.setMerchantId(accountInfo.getMerchantId());
        memberGroupDto.setStoreId(accountInfo.getStoreId());
        memberGroupDto.setOperator(accountInfo.getAccountName());
        if (memberGroupDto.getId() != null && memberGroupDto.getId() > 0) {
            memberGroupService.updateMemberGroup(memberGroupDto, accountInfo);
        } else {
            memberGroupService.addMemberGroup(memberGroupDto);
        }
        return getSuccessResult(true);
    }

    /**
     * 删除会员分组
     */
    @ApiOperation(value = "删除会员分组")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:group:index')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        // 该分组已有会员，不允许删除
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("GROUP_ID", id.toString());
        searchParams.put("STATUS", StatusEnum.ENABLED.getKey());
        List<MtUser> dataList = mtUserMapper.selectByMap(searchParams);
        if (dataList.size() > 0) {
            return getFailureResult(201, "该分组下有会员，不能删除");
        }

        memberGroupService.deleteMemberGroup(id, accountInfo);

        return getSuccessResult(true);
    }

    /**
     * 更新分组状态
     */
    @ApiOperation(value = "更新分组状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:group:index')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MemberGroupDto groupDto = new MemberGroupDto();
        groupDto.setOperator(accountInfo.getAccountName());
        groupDto.setId(params.getId());
        groupDto.setStatus(params.getStatus());
        memberGroupService.updateMemberGroup(groupDto, accountInfo);

        return getSuccessResult(true);
    }

    /**
     * 获取分组详情
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
