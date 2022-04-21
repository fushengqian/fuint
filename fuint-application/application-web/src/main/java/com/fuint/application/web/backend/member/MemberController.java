package com.fuint.application.web.backend.member;

import com.fuint.application.dao.entities.MtSetting;
import com.fuint.application.dao.entities.MtUserGrade;
import com.fuint.application.enums.SettingTypeEnum;
import com.fuint.application.enums.UserSettingEnum;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.util.CommonUtil;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.service.member.MemberService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 会员管理类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/member")
public class MemberController {

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
     * 会员列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 会员列表展现页面
     */
    @RequiresPermissions("backend/member/queryList")
    @RequestMapping(value = "/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        String mobile = request.getParameter("LIKE_mobile");
        String name = request.getParameter("LIKE_name");
        String birthday = request.getParameter("LIKE_birthday");
        String userNo = request.getParameter("EQ_userNo");

        Map<String, Object> params = paginationRequest.getSearchParams();
        if (params == null) {
            params = new HashMap<>();
            if (StringUtils.isNotEmpty(mobile)) {
                params.put("LIKE_mobile", mobile);
            }
            if (StringUtils.isNotEmpty(name)) {
                params.put("LIKE_name", name);
            }
        }

        ShiroUser shirouser = ShiroUserHelper.getCurrentShiroUser();
        TAccount account = accountService.findAccountById(shirouser.getId());
        Integer storeId = account.getStoreId();
        if (storeId > 0) {
            params.put("EQ_storeId", storeId.toString());
        }

        paginationRequest.setSearchParams(params);
        PaginationResponse<MtUser> paginationResponse = memberService.queryMemberListByPagination(paginationRequest);

        Map<String, Object> param = new HashMap<>();
        List<MtUserGrade> userGradeMap = memberService.queryMemberGradeByParams(param);

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("userGradeMap", userGradeMap);
        model.addAttribute("LIKE_mobile", mobile);
        model.addAttribute("LIKE_name", name);
        model.addAttribute("EQ_userNo", userNo);
        model.addAttribute("LIKE_birthday", birthday);

        return "member/member_list";
    }

    /**
     * 删除会员
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/member/delete/{id}")
    @RequestMapping(value = "/delete/{id}")
    public String delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();

        memberService.deleteMember(id, operator);
        ReqResult reqResult = new ReqResult();

        reqResult.setResult(true);
        return "redirect:/backend/member/queryList";
    }

    /**
     * 添加初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/member/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        Map<String, Object> param = new HashMap<>();
        List<MtUserGrade> userGradeMap = memberService.queryMemberGradeByParams(param);

        model.addAttribute("userGradeMap", userGradeMap);
        return "member/member_add";
    }

    /**
     * 提交新增
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/member/create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        MtUser memberInfo = (MtUser) RequestHandler.createBean(request, new MtUser());

        ShiroUser shirouser = ShiroUserHelper.getCurrentShiroUser();
        TAccount account = accountService.findAccountById(shirouser.getId());
        Integer storeId = account.getStoreId();
        memberInfo.setStoreId(storeId);

        if (StringUtils.isEmpty(memberInfo.getMobile())) {
            throw new BusinessRuntimeException("手机号码不能为空");
        } else {
            MtUser tempUser = null;
            if (memberInfo.getId() == null) {
                tempUser = memberService.queryMemberByMobile(memberInfo.getMobile());
            } else {
                memberInfo.setUpdateTime(new Date());
            }
            if (null != tempUser) {
                throw new BusinessCheckException("该会员手机号码已经存在!");
            }
        }

        memberService.addMember(memberInfo);

        return "redirect:/backend/member/queryList";
    }

    /**
     * 编辑初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/member/memberEditInit/{id}")
    @RequestMapping(value = "/memberEditInit/{id}")
    public String userEditInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        MtUser mtUserInfo = memberService.queryMemberById(id);

        Map<String, Object> param = new HashMap<>();
        List<MtUserGrade> userGradeMap = memberService.queryMemberGradeByParams(param);

        model.addAttribute("userGradeMap", userGradeMap);
        model.addAttribute("member", mtUserInfo);

        return "member/member_edit";
    }

    /**
     * 提交编辑
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/member/update")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        MtUser param = (MtUser) RequestHandler.createBean(request, new MtUser());

        MtUser memberInfo = memberService.queryMemberById(param.getId());
        if (memberInfo == null) {
            throw new BusinessCheckException("该会员不存在");
        }
        if (StringUtils.isNotEmpty(param.getUserNo())) {
            memberInfo.setUserNo(param.getUserNo());
        }
        if (StringUtils.isEmpty(memberInfo.getUserNo())) {
            memberInfo.setUserNo(CommonUtil.createUserNo());
        }
        if (StringUtils.isNotEmpty(param.getName())) {
            memberInfo.setName(param.getName());
        }
        if (StringUtils.isNotEmpty(param.getGradeId())) {
            memberInfo.setGradeId(param.getGradeId());
        }
        if (StringUtils.isNotEmpty(param.getMobile())) {
            memberInfo.setMobile(param.getMobile());
        }
        if (StringUtils.isNotEmpty(param.getIdcard())) {
            memberInfo.setIdcard(param.getIdcard());
        }
        if (StringUtils.isNotEmpty(param.getBirthday())) {
            memberInfo.setBirthday(param.getBirthday());
        }
        if (param.getPoint() != null) {
            memberInfo.setPoint(param.getPoint());
        }
        if (StringUtils.isNotEmpty(param.getAddress())) {
            memberInfo.setAddress(param.getAddress());
        }
        if (StringUtils.isNotEmpty(param.getStatus())) {
            memberInfo.setStatus(param.getStatus());
        }
        if (StringUtils.isNotEmpty(param.getDescription())) {
            memberInfo.setDescription(param.getDescription());
        }
        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        memberInfo.setOperator(operator);

        memberService.updateMember(memberInfo);

        return "redirect:/backend/member/queryList";
    }

    /**
     * 编辑初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/member/setting")
    @RequestMapping(value = "/setting")
    public String editInit(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.USER.getKey());

        for (MtSetting setting : settingList) {
            if (setting.getName().equals("getCouponNeedPhone")) {
                model.addAttribute("getCouponNeedPhone", setting.getValue());
            } else if (setting.getName().equals("submitOrderNeedPhone")) {
                model.addAttribute("submitOrderNeedPhone", setting.getValue());
            } else if (setting.getName().equals("loginNeedPhone")) {
                model.addAttribute("loginNeedPhone", setting.getValue());
            }
        }

        return "member/setting";
    }

    /**
     * 提交保存
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/member/saveSetting")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult saveSetting(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String getCouponNeedPhone = request.getParameter("getCouponNeedPhone") != null ? request.getParameter("getCouponNeedPhone") : "false";
        String submitOrderNeedPhone = request.getParameter("submitOrderNeedPhone") != null ? request.getParameter("submitOrderNeedPhone") : "false";
        String loginNeedPhone = request.getParameter("loginNeedPhone") != null ? request.getParameter("loginNeedPhone") : "false";

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();

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

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        return reqResult;
    }
}
