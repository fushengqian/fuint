package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserGradeCatchTypeEnum;
import com.fuint.common.service.UserGradeService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUserGrade;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
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
@Api(tags="管理端-会员等级相关接口")
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
     *
     * @param request HttpServletRequest对象
     * @return 会员等级列表
     */
    @ApiOperation(value = "会员等级列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userGrade:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String catchTypeKey = request.getParameter("catchType");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(catchTypeKey)) {
            params.put("catchType", catchTypeKey);
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        paginationRequest.setSearchParams(params);

        PaginationResponse<MtUserGrade> paginationResponse = userGradeService.queryUserGradeListByPagination(paginationRequest);
        List<MtUserGrade> dataList = paginationResponse.getContent();
        List<MtUserGrade> content = new ArrayList<>();
        UserGradeCatchTypeEnum[] catchTypeList = UserGradeCatchTypeEnum.values();
        for (MtUserGrade grade : dataList) {
            for (UserGradeCatchTypeEnum catchType : catchTypeList) {
                if (grade.getCatchType().equals(catchType.getKey())) {
                    grade.setCatchType(catchType.getValue());
                    continue;
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
     *
     * @return
     */
    @ApiOperation(value = "更新会员等级状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userGrade:index')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        Integer userGradeId = param.get("userGradeId") == null ? 0 : Integer.parseInt(param.get("userGradeId").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        MtUserGrade gradeInfo = userGradeService.queryUserGradeById(accountInfo.getMerchantId(), userGradeId, 0);
        if (gradeInfo == null) {
            return getFailureResult(201, "会员等级不存在");
        }

        gradeInfo.setStatus(status);
        userGradeService.updateUserGrade(gradeInfo);

        return getSuccessResult(true);
    }

    /**
     * 删除会员等级
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "删除会员等级")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userGrade:index')")
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        String operator = accountInfo.getAccountName();

        MtUserGrade mtUserGrade = userGradeService.queryUserGradeById(0, id, 0);
        if (mtUserGrade == null || !mtUserGrade.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(201, "您没有删除权限");
        }

        userGradeService.deleteUserGrade(id, operator);
        return getSuccessResult(true);
    }

    /**
     * 保存会员等级
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存会员等级")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userGrade:add')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        String grade = param.get("grade") == null ? "0" : param.get("grade").toString();
        String name = CommonUtil.replaceXSS(param.get("name").toString());
        String catchType = CommonUtil.replaceXSS(param.get("catchType").toString());
        String catchValue = CommonUtil.replaceXSS(param.get("catchValue").toString());
        String validDay = CommonUtil.replaceXSS(param.get("validDay").toString());
        String discount = CommonUtil.replaceXSS(param.get("discount").toString());
        String speedPoint = CommonUtil.replaceXSS(param.get("speedPoint").toString());
        String condition = param.get("catchCondition") == null ? "" : CommonUtil.replaceXSS(param.get("catchCondition").toString());
        String privilege = param.get("userPrivilege") == null ? "" : CommonUtil.replaceXSS(param.get("userPrivilege").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : CommonUtil.replaceXSS(param.get("status").toString());
        String id = param.get("id") == null ? "" : param.get("id").toString();

        if (StringUtil.isEmpty(grade) || StringUtil.isEmpty(name)) {
            return getFailureResult(201, "参数有误");
        }
        if (!CommonUtil.isNumeric(grade) || Integer.parseInt(grade) < 1) {
            return getFailureResult(201, "会员等级必须为正整数");
        }
        if (!CommonUtil.isNumeric(validDay) || Integer.parseInt(validDay) < 0) {
            return getFailureResult(201, "有效天数必须为正整数");
        }
        if (!CommonUtil.isNumeric(speedPoint) || Integer.parseInt(speedPoint) < 0) {
            return getFailureResult(201, "积分加速必须为正整数");
        }
        MtUserGrade mtUserGrade;
        if (StringUtil.isEmpty(id)) {
            mtUserGrade = new MtUserGrade();
        } else {
            mtUserGrade = userGradeService.queryUserGradeById(accountInfo.getMerchantId(), Integer.parseInt(id), 0);
        }

        mtUserGrade.setGrade(Integer.parseInt(grade));
        mtUserGrade.setName(name);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            mtUserGrade.setMerchantId(accountInfo.getMerchantId());
        }
        if (StringUtil.isNotEmpty(catchType)) {
            mtUserGrade.setCatchType(catchType);
        }
        if (StringUtil.isNotEmpty(condition)) {
            mtUserGrade.setCatchCondition(condition);
        }
        if (StringUtil.isNotEmpty(privilege)) {
            mtUserGrade.setUserPrivilege(privilege);
        }
        if (StringUtil.isNotEmpty(catchValue)) {
            mtUserGrade.setCatchValue(new BigDecimal(catchValue));
        }
        if (StringUtil.isNotEmpty(validDay)) {
            mtUserGrade.setValidDay(Integer.parseInt(validDay));
        }
        if (StringUtil.isNotEmpty(discount)) {
            mtUserGrade.setDiscount(Float.parseFloat(discount));
        }
        if (StringUtil.isNotEmpty(speedPoint)) {
            mtUserGrade.setSpeedPoint(Float.parseFloat(speedPoint));
        }
        mtUserGrade.setStatus(status);
        if (StringUtil.isEmpty(id)) {
            userGradeService.addUserGrade(mtUserGrade);
        } else {
            mtUserGrade.setId(Integer.parseInt(id));
            userGradeService.updateUserGrade(mtUserGrade);
        }
        return getSuccessResult(true);
    }

    /**
     * 获取会员等级信息
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取会员等级信息")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userGrade:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtUserGrade userGradeInfo = userGradeService.queryUserGradeById(accountInfo.getMerchantId(), id, 0);

        Map<String, Object> result = new HashMap<>();
        result.put("userGradeInfo", userGradeInfo);

        return getSuccessResult(result);
    }
}
