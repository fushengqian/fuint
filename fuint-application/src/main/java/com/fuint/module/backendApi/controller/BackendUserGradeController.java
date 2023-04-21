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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
@RequestMapping(value = "/backendApi/userGrade")
public class BackendUserGradeController extends BaseController {

    /**
     * 会员等级服务接口
     */
    @Autowired
    private UserGradeService userGradeService;

    /**
     * 会员等级列表查询
     *
     * @param   request  HttpServletRequest对象
     * @return 会员等级列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String catchTypeKey = request.getParameter("catchType");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

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

        List<ParamDto> catchTypes = new ArrayList<>();
        for (UserGradeCatchTypeEnum catchTypeEnum : catchTypeList) {
             ParamDto catchType = new ParamDto();
             catchType.setKey(catchTypeEnum.getKey());
             catchType.setName(catchTypeEnum.getValue());
             catchType.setValue(catchTypeEnum.getKey());
             catchTypes.add(catchType);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("catchTypeList", catchTypes);

        return getSuccessResult(result);
    }

    /**
     * 更新等级状态
     *
     * @return
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer userGradeId = param.get("userGradeId") == null ? 0 : Integer.parseInt(param.get("userGradeId").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        MtUserGrade gradeInfo = userGradeService.queryUserGradeById(userGradeId);
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
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        userGradeService.deleteUserGrade(id, operator);

        return getSuccessResult(true);
    }

    /**
     * 提交保存
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

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

        MtUserGrade info;
        if (StringUtil.isEmpty(id)) {
            info = new MtUserGrade();
        } else {
            info = userGradeService.queryUserGradeById(Integer.parseInt(id));
        }

        info.setGrade(Integer.parseInt(grade));
        info.setName(name);

        if (StringUtil.isNotEmpty(catchType)) {
            info.setCatchType(catchType);
        }

        if (StringUtil.isNotEmpty(condition)) {
            info.setCatchCondition(condition);
        }

        if (StringUtil.isNotEmpty(privilege)) {
            info.setUserPrivilege(privilege);
        }

        if (StringUtil.isNotEmpty(catchValue)) {
            info.setCatchValue(Integer.parseInt(catchValue));
        }

        if (StringUtil.isNotEmpty(validDay)) {
            info.setValidDay(Integer.parseInt(validDay));
        }

        if (StringUtil.isNotEmpty(discount)) {
            info.setDiscount(Float.parseFloat(discount));
        }

        if (StringUtil.isNotEmpty(speedPoint)) {
            info.setSpeedPoint(Float.parseFloat(speedPoint));
        }

        info.setStatus(status);

        if (StringUtil.isEmpty(id)) {
            userGradeService.addUserGrade(info);
        } else {
            info.setId(Integer.parseInt(id));
            userGradeService.updateUserGrade(info);
        }

        return getSuccessResult(true);
    }

    /**
     * 会员等级信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtUserGrade userGradeInfo = userGradeService.queryUserGradeById(id);

        Map<String, Object> result = new HashMap<>();
        result.put("userGradeInfo", userGradeInfo);

        return getSuccessResult(result);
    }
}
