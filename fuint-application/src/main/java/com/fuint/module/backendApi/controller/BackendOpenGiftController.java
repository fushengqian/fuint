package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.OpenGiftDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OpenGiftService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOpenGift;
import com.fuint.repository.model.MtUserGrade;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开卡赠礼管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-开卡赠礼相关接口")
@RestController
@RequestMapping(value = "/backendApi/openGift")
public class BackendOpenGiftController extends BaseController {

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 开卡赠礼服务接口
     */
    @Autowired
    private OpenGiftService openGiftService;

    /**
     * 开卡礼列表查询
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String couponId = request.getParameter("couponId");
        String gradeId = request.getParameter("gradeId");
        String status = request.getParameter("status");

        Map<String, Object> param = new HashMap<>();
        if (StringUtil.isNotEmpty(couponId)) {
            param.put("couponId", couponId);
        }
        if (StringUtil.isNotEmpty(gradeId)) {
            param.put("gradeId", gradeId);
        }
        if (StringUtil.isNotEmpty(status)) {
            param.put("status", status);
        }
        param.put("pageNumber", page);
        param.put("pageSize", pageSize);

        ResponseObject response = openGiftService.getOpenGiftList(param);

        Map<String, Object> params = new HashMap<>();
        List<MtUserGrade> userGradeList = memberService.queryMemberGradeByParams(params);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", response.getData());
        result.put("userGradeList", userGradeList);

        return getSuccessResult(result);
    }

    /**
     * 详情信息
     * @param request  HttpServletRequest对象
     * @return
     * */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Map<String, Object> param = new HashMap<>();
        List<MtUserGrade> userGradeMap = memberService.queryMemberGradeByParams(param);

        OpenGiftDto openGiftInfo = openGiftService.getOpenGiftDetail(id);

        Map<String, Object> result = new HashMap<>();
        result.put("openGiftInfo", openGiftInfo);
        result.put("userGradeMap", userGradeMap);

        return getSuccessResult(result);
    }

    /**
     * 提交处理
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject handleSave(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String id = param.get("id").toString();
        String gradeId = param.get("gradeId").toString();
        String couponId = param.get("couponId").toString();
        String couponNum = param.get("couponNum").toString();
        String point = param.get("point").toString();
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        if (StringUtil.isEmpty(couponId) && StringUtil.isEmpty(couponNum) && StringUtil.isEmpty(point)) {
            return getFailureResult(201, "积分和卡券必须填写一项");
        }

        if (StringUtil.isEmpty(gradeId)) {
            return getFailureResult(201, "会员等级不能为空");
        }

        MtOpenGift reqDto = new MtOpenGift();
        if (StringUtil.isNotEmpty(couponId)) {
            reqDto.setCouponId(Integer.parseInt(couponId));
        } else {
            reqDto.setCouponId(0);
        }
        if (StringUtil.isNotEmpty(couponNum)) {
            reqDto.setCouponNum(Integer.parseInt(couponNum));
        } else {
            reqDto.setCouponNum(0);
        }
        if (StringUtil.isNotEmpty(gradeId)) {
            reqDto.setGradeId(Integer.parseInt(gradeId));
        } else {
            reqDto.setGradeId(0);
        }
        if (StringUtil.isNotEmpty(point)) {
            reqDto.setPoint(Integer.parseInt(point));
        } else {
            reqDto.setPoint(0);
        }
        reqDto.setStoreId(0);
        reqDto.setStatus(status);
        String operator = accountInfo.getAccountName();
        reqDto.setOperator(operator);

        if (StringUtil.isNotEmpty(id)) {
            reqDto.setId(Integer.parseInt(id));
            openGiftService.updateOpenGift(reqDto);
        } else {
            openGiftService.addOpenGift(reqDto);
        }

        return getSuccessResult(true);
    }

    /**
     * 更新状态
     *
     * @return
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer id = param.get("id") == null ? 0 : Integer.parseInt(param.get("id").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        OpenGiftDto info = openGiftService.getOpenGiftDetail(id);
        if (info == null) {
            return getFailureResult(201, "会员等级不存在");
        }

        MtOpenGift reqDto = new MtOpenGift();
        reqDto.setId(id);
        reqDto.setStatus(status);

        try {
            openGiftService.updateOpenGift(reqDto);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        return getSuccessResult(true);
    }

    /**
     * 删除数据项
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
        openGiftService.deleteOpenGift(id, operator);

        return getSuccessResult(true);
    }
}
