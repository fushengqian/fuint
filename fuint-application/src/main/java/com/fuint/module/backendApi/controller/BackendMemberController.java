package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.UserDto;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserSettingEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.PhoneFormatCheckUtils;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-会员相关接口")
@RestController
@RequestMapping(value = "/backendApi/member")
public class BackendMemberController extends BaseController {

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 配置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 员工接口
     */
    @Autowired
    private StaffService staffService;

    /**
     * 后台账户服务接口
     */
    @Autowired
    private AccountService accountService;

    /**
     * 会员等级服务接口
     * */
    @Autowired
    private UserGradeService userGradeService;

    /**
     * 会员列表查询
     *
     * @param request  HttpServletRequest对象
     * @return 会员列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String mobile = request.getParameter("mobile");
        String userId = request.getParameter("id");
        String name = request.getParameter("name");
        String birthday = request.getParameter("birthday");
        String userNo = request.getParameter("userNo");
        String gradeId = request.getParameter("gradeId");
        String orderBy = request.getParameter("orderBy") == null ? "" : request.getParameter("orderBy");
        String regTime = request.getParameter("regTime") == null ? "" : request.getParameter("regTime");
        String activeTime = request.getParameter("activeTime") == null ? "" : request.getParameter("activeTime");
        String memberTime = request.getParameter("memberTime") == null ? "" : request.getParameter("memberTime");
        String status = request.getParameter("status");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(userId)) {
            params.put("id", userId);
        }
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        if (StringUtil.isNotEmpty(birthday)) {
            params.put("birthday", birthday);
        }
        if (StringUtil.isNotEmpty(userNo)) {
            params.put("userNo", userNo);
        }
        if (StringUtil.isNotEmpty(gradeId)) {
            params.put("gradeId", gradeId);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId();
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId.toString());
        }

        // 注册时间比对
        if (StringUtil.isNotEmpty(regTime)) {
            String[] dateTime = regTime.split("~");
            if (dateTime.length == 2) {
                params.put("createTime", dateTime[0].trim() + ":00");
                params.put("createTime", dateTime[1].trim() + ":00");
            }
        }

        // 活跃时间比对
        if (StringUtil.isNotEmpty(activeTime)) {
            String[] dateTime = activeTime.split("~");
            if (dateTime.length == 2) {
                params.put("updateTime", dateTime[0].trim() + ":00");
                params.put("updateTime", dateTime[1].trim() + ":00");
            }
        }

        // 会员有效期比对
        if (StringUtil.isNotEmpty(memberTime)) {
            String[] dateTime = memberTime.split("~");
            if (dateTime.length == 2) {
                params.put("startTime", dateTime[0].trim() + ":00");
                params.put("endTime", dateTime[1].trim() + ":00");
            }
        }

        // 会员排序方式
        if (StringUtil.isNotEmpty(orderBy)) {
            if (orderBy.equals("balance")) {
                paginationRequest.setSortColumn(new String[]{"balance desc"});
            } else if (orderBy.equals("point")) {
                paginationRequest.setSortColumn(new String[]{"point desc"});
            } else if (orderBy.equals("memberGrade")) {
                paginationRequest.setSortColumn(new String[]{"gradeId desc"});
            } else if (orderBy.equals("payAmount")) {
                paginationRequest.setSortColumn(new String[]{"balance desc"});
            } else if (orderBy.equals("memberTime")) {
                paginationRequest.setSortColumn(new String[]{"endTime desc", "gradeId desc"});
                MtUserGrade defaultGrade = userGradeService.getInitUserGrade();
                if (defaultGrade != null) {
                    params.put("gradeId", defaultGrade.getId().toString());
                }
            }
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<UserDto> paginationResponse = memberService.queryMemberListByPagination(paginationRequest);

        // 会员等级列表
        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        List<MtUserGrade> userGradeList = memberService.queryMemberGradeByParams(param);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("userGradeList", userGradeList);

        return getSuccessResult(result);
    }

    /**
     * 更新会员状态
     *
     * @return
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtUser userInfo = memberService.queryMemberById(userId);
        if (userInfo == null) {
            return getFailureResult(201, "会员不存在");
        }

        userInfo.setStatus(status);
        memberService.updateMember(userInfo);

        return getSuccessResult(true);
    }

    /**
     * 删除会员
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
        memberService.deleteMember(id, operator);

        return getSuccessResult(true);
    }

    /**
     * 保存会员信息
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String id = param.get("id").toString();
        String name = param.get("name") == null ? "" : param.get("name").toString();
        String gradeId = param.get("gradeId") == null ? "0" :param.get("gradeId").toString();
        String userNo = param.get("userNo") == null ? "" : param.get("userNo").toString();
        String mobile = param.get("mobile") == null ? "" : param.get("mobile").toString();
        String sex = param.get("sex") == null ? "0" : param.get("sex").toString();
        String idCard = param.get("idcard") == null ? "" : param.get("idcard").toString();
        String birthday = param.get("birthday") == null ? "" : param.get("birthday").toString();
        String address = param.get("address") == null ? "" : param.get("address").toString();
        String description = param.get("description") == null ? "" : param.get("description").toString();
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        if (!PhoneFormatCheckUtils.isChinaPhoneLegal(mobile) && StringUtil.isNotEmpty(mobile)) {
            return getFailureResult(201, "手机号格式有误！");
        }

        if (StringUtil.isNotEmpty(mobile)) {
            // 重置该手机号
            memberService.resetMobile(mobile, StringUtil.isEmpty(id) ? 0 : Integer.parseInt(id));
        }

        MtUser memberInfo;
        if (StringUtil.isEmpty(id)) {
            memberInfo = new MtUser();
        } else {
            memberInfo = memberService.queryMemberById(Integer.parseInt(id));
        }

        memberInfo.setName(name);
        memberInfo.setStatus(status);
        memberInfo.setGradeId(gradeId);
        memberInfo.setUserNo(userNo);
        memberInfo.setMobile(mobile);
        memberInfo.setSex(Integer.parseInt(sex));
        memberInfo.setIdcard(idCard);
        memberInfo.setBirthday(birthday);
        memberInfo.setAddress(address);
        memberInfo.setDescription(description);

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId();
        memberInfo.setStoreId(storeId);

        try {
            if (StringUtil.isEmpty(id)) {
                memberService.addMember(memberInfo);
            } else {
                memberService.updateMember(memberInfo);
            }
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        return getSuccessResult(true);
    }

    /**
     * 会员详情
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

        MtUser mtUserInfo = memberService.queryMemberById(id);

        Map<String, Object> param = new HashMap<>();
        List<MtUserGrade> userGradeList = memberService.queryMemberGradeByParams(param);

        Map<String, Object> result = new HashMap<>();
        result.put("userGradeList", userGradeList);
        result.put("memberInfo", mtUserInfo);

        return getSuccessResult(result);
    }

    /**
     * 会员设置信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject setting(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.USER.getKey());

        String getCouponNeedPhone = "false";
        String submitOrderNeedPhone = "false";
        String loginNeedPhone = "false";

        for (MtSetting setting : settingList) {
             if (setting.getName().equals("getCouponNeedPhone")) {
                 getCouponNeedPhone = setting.getValue();
             } else if (setting.getName().equals("submitOrderNeedPhone")) {
                 submitOrderNeedPhone = setting.getValue();
             } else if (setting.getName().equals("loginNeedPhone")) {
                 loginNeedPhone = setting.getValue();
             }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("getCouponNeedPhone", getCouponNeedPhone);
        result.put("submitOrderNeedPhone", submitOrderNeedPhone);
        result.put("loginNeedPhone", loginNeedPhone);

        return getSuccessResult(result);
    }

    /**
     * 提交设置保存
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveSetting(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String getCouponNeedPhone = param.get("getCouponNeedPhone") != null ? param.get("getCouponNeedPhone").toString() : "false";
        String submitOrderNeedPhone = param.get("submitOrderNeedPhone") != null ? param.get("submitOrderNeedPhone").toString() : "false";
        String loginNeedPhone = param.get("loginNeedPhone") != null ? param.get("loginNeedPhone").toString() : "false";

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String operator = accountInfo.getAccountName();

        UserSettingEnum[] settingList = UserSettingEnum.values();
        for (UserSettingEnum setting : settingList) {
            MtSetting info = new MtSetting();
            info.setType(SettingTypeEnum.USER.getKey());
            info.setName(setting.getKey());

            if (setting.getKey().equals("getCouponNeedPhone")) {
                info.setValue(getCouponNeedPhone);
            } else if (setting.getKey().equals("submitOrderNeedPhone")) {
                info.setValue(submitOrderNeedPhone);
            } else if (setting.getKey().equals("loginNeedPhone")) {
                info.setValue(loginNeedPhone);
            }

            info.setDescription(setting.getValue());
            info.setOperator(operator);
            info.setUpdateTime(new Date());

            settingService.saveSetting(info);
        }

        return getSuccessResult(true);
    }
}
