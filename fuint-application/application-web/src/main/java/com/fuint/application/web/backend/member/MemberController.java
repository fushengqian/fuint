package com.fuint.application.web.backend.member;

import com.fuint.application.dao.entities.MtUserGrade;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.util.StringUtil;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.member.MemberService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会员信息管理类controller
 * Created by zach on 2019/07/19
 */
@Controller
@RequestMapping(value = "/backend/member")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

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
        String realName = request.getParameter("LIKE_realName");
        String birthday = request.getParameter("LIKE_birthday");

        Map<String, Object> params = paginationRequest.getSearchParams();
        if (params == null) {
            params = new HashMap<>();
            if (StringUtils.isNotEmpty(mobile)) {
                params.put("LIKE_mobile", mobile);
            }
            if (StringUtils.isNotEmpty(realName)) {
                params.put("LIKE_realName", realName);
            }
        }

        paginationRequest.setSearchParams(params);
        PaginationResponse<MtUser> paginationResponse = memberService.queryMemberListByPagination(paginationRequest);

        Map<String, Object> param = new HashMap<>();
        List<MtUserGrade> userGradeMap = memberService.queryMemberGradeByParams(param);

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("userGradeMap", userGradeMap);
        model.addAttribute("LIKE_mobile", mobile);
        model.addAttribute("LIKE_realName", realName);
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
    public String delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        Integer i = memberService.deleteMember(id.intValue(), "删除会员");
        ReqResult reqResult = new ReqResult();

        reqResult.setResult(true);
        return "redirect:/backend/member/queryList";
    }

    /**
     * 激活会员
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/member/active/{id}")
    @RequestMapping(value = "/active/{id}")
    public String userActive(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        List<Integer> ids = new ArrayList<>();
        ids.add(id.intValue());
        return "redirect:/backend/member/queryList";
    }

    /**
     * 批量删除会员
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/member/batchDelete")
    @RequestMapping(value = "/batchDelete")
    @ResponseBody
    public ReqResult batchDelete(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String paramIds = request.getParameter("ids");
        if (StringUtil.isNotBlank(paramIds)) {
            String[] ids = paramIds.split(",");
            List<Integer> idList = new ArrayList<Integer>();
            if (ids.length > 0) {
                for (String id : ids) {
                    idList.add(Integer.parseInt(id));
                }
            }
            memberService.updateStatus(idList, StatusEnum.DISABLE.getKey());
        }
        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);
        return reqResult;
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
        List<MtUserGrade> userGroupMap = memberService.queryMemberGradeByParams(param);

        model.addAttribute("userGroupMap", userGroupMap);
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
    public String addUserHandler(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        MtUser memberInfo = (MtUser) RequestHandler.createBean(request, new MtUser());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt=sdf.format(new Date());
        Date currentDT;
        try {
            currentDT = sdf.parse(dt);
        } catch (ParseException e) {
            throw new BusinessCheckException("日期转换错误!");
        }

        if (StringUtils.isEmpty(memberInfo.getMobile())) {
            throw new BusinessRuntimeException("手机号码不能为空");
        } else {
            MtUser tempUser = null;
            if (memberInfo.getId() == null) {
                tempUser = memberService.queryMemberByMobile(memberInfo.getMobile());

            } else {
                memberInfo.setUpdateTime(currentDT);
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
        List<MtUserGrade> userGroupMap = memberService.queryMemberGradeByParams(param);

        model.addAttribute("userGroupMap", userGroupMap);
        model.addAttribute("member", mtUserInfo);

        return "member/member_edit";
    }
}
