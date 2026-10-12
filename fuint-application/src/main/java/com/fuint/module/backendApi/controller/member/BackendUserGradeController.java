package com.fuint.module.backendApi.controller.member;

import com.fuint.common.dto.common.ParamDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserGradeCatchTypeEnum;
import com.fuint.common.param.UserGradePage;
import com.fuint.common.param.UserGradeParam;
import com.fuint.common.service.UserGradeService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUserGrade;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员等级管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="管理端-会员等级相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/userGrade")
public class BackendUserGradeController extends BaseController {

    /**
     * 会员等级服务接口
     */
    private UserGradeService userGradeService;

    /**
     * 会员等级列表查询
     */
    @Operation(summary="会员等级列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userGrade:index')")
    public ResponseObject list(@ModelAttribute UserGradePage userGradePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            userGradePage.setMerchantId(accountInfo.getMerchantId());
        }
        PaginationResponse<MtUserGrade> paginationResponse = userGradeService.queryUserGradeListByPagination(userGradePage);
        List<MtUserGrade> dataList = paginationResponse.getContent();
        List<MtUserGrade> content = new ArrayList<>();
        UserGradeCatchTypeEnum[] catchTypeList = UserGradeCatchTypeEnum.values();
        for (MtUserGrade grade : dataList) {
            for (UserGradeCatchTypeEnum catchType : catchTypeList) {
                 if (grade.getCatchType().equals(catchType.getKey())) {
                     grade.setCatchType(catchType.getValue());
                 }
            }
            content.add(grade);
        }
        paginationResponse.setContent(content);

        List<ParamDto> catchTypes = UserGradeCatchTypeEnum.getUserGradeCatchTypeList();

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("catchTypeList", catchTypes);

        return getSuccessResult(result);
    }

    /**
     * 更新会员等级状态
     */
    @Operation(summary="更新会员等级状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userGrade:index')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        Integer userGradeId = param.get("userGradeId") == null ? 0 : Integer.parseInt(param.get("userGradeId").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        MtUserGrade mtUserGrade = userGradeService.queryUserGradeById(accountInfo.getMerchantId(), userGradeId, 0);
        if (mtUserGrade == null) {
            return getFailureResult(201, "会员等级不存在");
        }

        mtUserGrade.setStatus(status);
        userGradeService.updateUserGrade(mtUserGrade);

        return getSuccessResult(true);
    }

    /**
     * 删除会员等级
     */
    @Operation(summary="删除会员等级")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userGrade:index')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtUserGrade mtUserGrade = userGradeService.queryUserGradeById(0, id, 0);
        if (mtUserGrade == null || !mtUserGrade.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(201, "您没有删除权限");
        }

        userGradeService.deleteUserGrade(id, accountInfo);
        return getSuccessResult(true);
    }

    /**
     * 保存会员等级
     */
    @Operation(summary="保存会员等级")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userGrade:add')")
    public ResponseObject saveHandler(@RequestBody UserGradeParam userGrade) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() < 1) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }
        MtUserGrade mtUserGrade = new MtUserGrade();
        BeanUtils.copyProperties(userGrade, mtUserGrade);
        if (userGrade.getGrade() == null) {
            return getFailureResult(201, "参数有误");
        }
        if (userGrade.getGrade() < 1) {
            return getFailureResult(201, "会员等级必须为正整数");
        }
        if (userGrade.getValidDay() < 0) {
            return getFailureResult(201, "有效天数必须为正整数");
        }
        if (userGrade.getSpeedPoint() < 0) {
            return getFailureResult(201, "积分加速必须是数字");
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            mtUserGrade.setMerchantId(accountInfo.getMerchantId());
        }
        if (mtUserGrade.getId() == null) {
            userGradeService.addUserGrade(mtUserGrade);
        } else {
            userGradeService.updateUserGrade(mtUserGrade);
        }
        return getSuccessResult(true);
    }

    /**
     * 获取会员等级信息
     */
    @Operation(summary="获取会员等级信息")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userGrade:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtUserGrade userGradeInfo = userGradeService.queryUserGradeById(accountInfo.getMerchantId(), id, 0);

        Map<String, Object> result = new HashMap<>();
        result.put("userGradeInfo", userGradeInfo);

        return getSuccessResult(result);
    }
}
