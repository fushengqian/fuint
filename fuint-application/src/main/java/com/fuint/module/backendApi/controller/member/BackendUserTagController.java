package com.fuint.module.backendApi.controller.member;

import com.fuint.common.dto.member.UserTagDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.UserTagRelationService;
import com.fuint.common.service.UserTagService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUserTag;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    private MerchantService merchantService;

    @ApiOperation(value = "标签列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tag:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer merchantId = merchantService.getMerchantId(token);

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
    public ResponseObject save(@RequestBody MtUserTag mtUserTag, HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer merchantId = merchantService.getMerchantId(token);
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        String operator = accountInfo != null ? accountInfo.getAccountName() : "";

        mtUserTag.setMerchantId(merchantId);
        mtUserTag.setOperator(operator);

        if (mtUserTag.getId() != null && mtUserTag.getId() > 0) {
            userTagService.updateTag(mtUserTag);
        } else {
            userTagService.addTag(mtUserTag);
        }

        return getSuccessResult(true);
    }

    @ApiOperation(value = "删除标签")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tag:delete')")
    public ResponseObject delete(@PathVariable("id") Integer id, HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        String operator = accountInfo != null ? accountInfo.getAccountName() : "";

        userTagService.deleteTag(id, operator);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "获取会员标签")
    @RequestMapping(value = "/userTags/{userId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getUserTags(@PathVariable("userId") Integer userId) {
        List<Integer> tagIds = userTagRelationService.getTagIdsByUserId(userId);
        return getSuccessResult(tagIds);
    }

    @ApiOperation(value = "设置会员标签")
    @RequestMapping(value = "/setUserTags", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tag:edit')")
    public ResponseObject setUserTags(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        String operator = accountInfo != null ? accountInfo.getAccountName() : "";

        Integer userId = params.get("userId") == null ? 0 : Integer.parseInt(params.get("userId").toString());
        @SuppressWarnings("unchecked")
        List<Integer> tagIds = (List<Integer>) params.get("tagIds");

        userTagRelationService.setUserTags(userId, tagIds, operator);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "批量设置会员标签")
    @RequestMapping(value = "/batchSetTags", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:tag:edit')")
    public ResponseObject batchSetTags(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        String operator = accountInfo != null ? accountInfo.getAccountName() : "";

        @SuppressWarnings("unchecked")
        List<Integer> userIds = (List<Integer>) params.get("userIds");
        @SuppressWarnings("unchecked")
        List<Integer> tagIds = (List<Integer>) params.get("tagIds");

        userTagRelationService.batchSetUserTags(userIds, tagIds, operator);

        return getSuccessResult(true);
    }
}
