package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.entities.MtSetting;
import com.fuint.application.dao.entities.MtUserGrade;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.enums.SettingTypeEnum;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.UserSettingEnum;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.usergrade.UserGradeService;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.service.member.MemberService;
import com.fuint.util.StringUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 会员管理类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
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
     * 后台账户服务接口
     */
    @Autowired
    private TAccountService accountService;

    /**
     * 会员等级服务接口
     * */
    @Autowired
    private UserGradeService userGradeService;

    /**
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

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

        Map<String, Object> params = new HashedMap();
        if (StringUtil.isNotEmpty(userId)) {
            params.put("EQ_id", userId);
        }
        if (StringUtil.isNotEmpty(name)) {
            params.put("LIKE_name", name);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            params.put("EQ_mobile", mobile);
        }
        if (StringUtil.isNotEmpty(birthday)) {
            params.put("LIKE_birthday", birthday);
        }
        if (StringUtil.isNotEmpty(userNo)) {
            params.put("EQ_userNo", userNo);
        }
        if (StringUtil.isNotEmpty(gradeId)) {
            params.put("EQ_gradeId", gradeId);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("EQ_status", status);
        }

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        TAccount account = accountService.findAccountById(accountInfo.getId());
        Integer storeId = account.getStoreId();
        if (storeId != null && storeId > 0) {
            params.put("EQ_storeId", storeId.toString());
        }

        // 注册时间比对
        if (StringUtil.isNotEmpty(regTime)) {
            String[] dateTime = regTime.split("~");
            if (dateTime.length == 2) {
                params.put("GTE_createTime", dateTime[0].trim() + ":00");
                params.put("LTE_createTime", dateTime[1].trim() + ":00");
            }
        }

        // 活跃时间比对
        if (StringUtil.isNotEmpty(activeTime)) {
            String[] dateTime = activeTime.split("~");
            if (dateTime.length == 2) {
                params.put("GTE_updateTime", dateTime[0].trim() + ":00");
                params.put("LTE_updateTime", dateTime[1].trim() + ":00");
            }
        }

        // 会员有效期比对
        if (StringUtil.isNotEmpty(memberTime)) {
            String[] dateTime = memberTime.split("~");
            if (dateTime.length == 2) {
                params.put("GTE_startTime", dateTime[0].trim() + ":00");
                params.put("LTE_endTime", dateTime[1].trim() + ":00");
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
                    params.put("NQ_gradeId", defaultGrade.getId().toString());
                }
            }
        }
        params.put("NQ_status", StatusEnum.DISABLE.getKey());
        paginationRequest.setSearchParams(params);
        PaginationResponse<MtUser> paginationResponse = memberService.queryMemberListByPagination(paginationRequest);

        // 会员等级列表
        Map<String, Object> param = new HashMap<>();
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
    public ResponseObject updateStatus(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

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
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
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
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        String id = param.get("id").toString();
        String name = param.get("name") == null ? "" : param.get("name").toString();
        String gradeId = param.get("gradeId") == null ? "0" :param.get("gradeId").toString();
        String userNo = param.get("userNo") == null ? "" : param.get("userNo").toString();
        String mobile = param.get("mobile") == null ? "" : param.get("mobile").toString();
        String sex = param.get("sex") == null ? "0" : param.get("sex").toString();
        String idcard = param.get("idcard") == null ? "" : param.get("idcard").toString();
        String birthday = param.get("birthday") == null ? "" : param.get("birthday").toString();
        String address = param.get("address") == null ? "" : param.get("address").toString();
        String description = param.get("description") == null ? "" : param.get("description").toString();
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        if (StringUtil.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(mobile);
            if (userInfo != null && !userInfo.getId().toString().equals(id)) {
                return getFailureResult(201, "该手机号已注册");
            }
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
        memberInfo.setIdcard(idcard);
        memberInfo.setBirthday(birthday);
        memberInfo.setAddress(address);
        memberInfo.setDescription(description);

        TAccount account = accountService.findAccountById(accountInfo.getId());
        Integer storeId = account.getStoreId();
        memberInfo.setStoreId(storeId);

        if (StringUtil.isEmpty(id)) {
            memberService.addMember(memberInfo);
        } else {
            memberService.updateMember(memberInfo);
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
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
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
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
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

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
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
