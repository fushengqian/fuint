package com.fuint.module.backendApi.controller.member;

import com.fuint.common.dto.member.UserTagDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.BatchSetUserTagParam;
import com.fuint.common.param.SetUserTagParam;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.UserTagRelationService;
import com.fuint.common.service.UserTagService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserTag;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台会员标签管理控制器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags = "后台-会员标签管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/userTag")
public class BackendUserTagController extends BaseController {

    private UserTagService userTagService;

    private UserTagRelationService userTagRelationService;

    private MemberService memberService;

    @ApiOperation(value = "标签列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tag:index')")
    public ResponseObject list() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId();

        List<MtUserTag> tagList = userTagService.getMerchantTagList(merchantId, StatusEnum.ENABLED.getKey());

        List<UserTagDto> result = tagList.stream().map(tag -> {
            UserTagDto dto = new UserTagDto();
            dto.setId(tag.getId());
            dto.setName(tag.getName());
            dto.setColor(tag.getColor());
            dto.setSort(tag.getSort());
            dto.setDescription(tag.getDescription());
            dto.setCreateTime(tag.getCreateTime());
            // 统计会员数量
            List<Integer> userIds = userTagRelationService.getUserIdsByTagId(tag.getId());
            dto.setUserCount(userIds.size());
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("list", result);

        return getSuccessResult(data);
    }

    @ApiOperation(value = "保存标签")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tag:edit')")
    public ResponseObject save(@RequestBody MtUserTag mtUserTag) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId();
        String operator = accountInfo != null ? accountInfo.getAccountName() : "";

        mtUserTag.setMerchantId(merchantId);
        mtUserTag.setOperator(operator);

        if (mtUserTag.getId() != null && mtUserTag.getId() > 0) {
            userTagService.updateTag(mtUserTag, merchantId);
        } else {
            userTagService.addTag(mtUserTag, merchantId);
        }

        return getSuccessResult(true);
    }

    @ApiOperation(value = "删除标签")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tag:delete')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        userTagService.deleteTag(id, accountInfo);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "获取会员标签")
    @RequestMapping(value = "/userTags/{userId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getUserTags(@PathVariable("userId") Integer userId) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId();

        // 校验商户权限
        if (merchantId != null && merchantId > 0) {
            MtUser userInfo = memberService.queryMemberById(userId);
            if (userInfo == null) {
                return getFailureResult(201, "会员不存在");
            }
            if (!merchantId.equals(userInfo.getMerchantId())) {
                return getFailureResult(1004, "抱歉，您没有查看权限");
            }
        }

        List<Integer> tagIds = userTagRelationService.getTagIdsByUserId(userId);
        return getSuccessResult(tagIds);
    }

    @ApiOperation(value = "设置会员标签")
    @RequestMapping(value = "/setUserTags", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tag:edit')")
    public ResponseObject setUserTags(@RequestBody SetUserTagParam param) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        String operator = accountInfo != null ? accountInfo.getAccountName() : "";
        Integer merchantId = accountInfo.getMerchantId();

        Integer userId = param.getUserId();
        List<Integer> tagIds = param.getTagIds();

        // 校验商户权限
        if (merchantId != null && merchantId > 0) {
            MtUser userInfo = memberService.queryMemberById(userId);
            if (userInfo == null) {
                return getFailureResult(201, "会员不存在");
            }
            if (!merchantId.equals(userInfo.getMerchantId())) {
                return getFailureResult(1004, "抱歉，您没有操作权限");
            }
            // 校验标签是否属于当前商户
            if (tagIds != null && !tagIds.isEmpty()) {
                for (Integer tagId : tagIds) {
                    MtUserTag tagInfo = userTagService.getTagById(tagId);
                    if (tagInfo != null && !merchantId.equals(tagInfo.getMerchantId())) {
                        return getFailureResult(1004, "抱歉，您没有操作权限");
                    }
                }
            }
        }

        userTagRelationService.setUserTags(userId, tagIds, operator);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "批量设置会员标签")
    @RequestMapping(value = "/batchSetTags", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tag:edit')")
    public ResponseObject batchSetTags(@RequestBody BatchSetUserTagParam param) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        String operator = accountInfo != null ? accountInfo.getAccountName() : "";
        Integer merchantId = accountInfo.getMerchantId();

        List<Integer> userIds = param.getUserIds();
        List<Integer> tagIds = param.getTagIds();

        // 校验商户权限
        if (merchantId != null && merchantId > 0) {
            // 校验会员是否属于当前商户
            if (userIds != null && !userIds.isEmpty()) {
                for (Integer userId : userIds) {
                    MtUser userInfo = memberService.queryMemberById(userId);
                    if (userInfo == null) {
                        return getFailureResult(201, "会员不存在");
                    }
                    if (!merchantId.equals(userInfo.getMerchantId())) {
                        return getFailureResult(1004, "抱歉，您没有操作权限");
                    }
                }
            }
            // 校验标签是否属于当前商户
            if (tagIds != null && !tagIds.isEmpty()) {
                for (Integer tagId : tagIds) {
                    MtUserTag tagInfo = userTagService.getTagById(tagId);
                    if (tagInfo != null && !merchantId.equals(tagInfo.getMerchantId())) {
                        return getFailureResult(1004, "抱歉，您没有操作权限");
                    }
                }
            }
        }

        userTagRelationService.batchSetUserTags(userIds, tagIds, operator);

        return getSuccessResult(true);
    }
}
