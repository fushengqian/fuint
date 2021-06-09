package com.fuint.application.web.backend.userGrade;

import com.fuint.application.dao.entities.MtUserGrade;
import com.fuint.application.enums.UserGradeCatchTypeEnum;
import com.fuint.application.service.usergrade.UserGradeService;
import com.fuint.application.util.CommonUtil;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dto.ReqResult;
import jodd.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 会员等级管理controller
 * Created by zach on 2021/05/12
 */
@Controller
@RequestMapping(value = "/backend/userGrade")
public class UserGradeController {

    private static final Logger logger = LoggerFactory.getLogger(UserGradeController.class);

    /**
     * 会员等级服务接口
     */
    @Autowired
    private UserGradeService userGradeService;

    /**
     * 会员等级列表查询
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return 会员列表展现页面
     */
    @RequiresPermissions("backend/userGrade/queryList")
    @RequestMapping(value = "/queryList")
    public String queryList(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);

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

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("catchTypeList", catchTypeList);

        return "userGrade/list";
    }

    /**
     * 删除会员等级
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/userGrade/delete/{id}")
    @RequestMapping(value = "/delete/{id}")
    public String delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        List<Long> ids = new ArrayList<Long>();
        ids.add(id);

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();

        userGradeService.deleteUserGrade(id.intValue(), operator);
        ReqResult reqResult = new ReqResult();

        reqResult.setResult(true);

        return "redirect:/backend/userGrade/queryList";
    }

    /**
     * 添加初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/userGrade/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        UserGradeCatchTypeEnum[] catchTypeList = UserGradeCatchTypeEnum.values();

        model.addAttribute("catchTypeList", catchTypeList);
        return "userGrade/add";
    }

    /**
     * 提交新增
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/userGrade/save")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String addGradeHandler(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String grade = request.getParameter("grade");
        String name = CommonUtil.replaceXSS(request.getParameter("name"));
        String catchType = CommonUtil.replaceXSS(request.getParameter("catchType"));
        String catchValue = CommonUtil.replaceXSS(request.getParameter("catchValue"));
        String validDay = CommonUtil.replaceXSS(request.getParameter("validDay"));
        String discount = CommonUtil.replaceXSS(request.getParameter("discount"));
        String speedPoint = CommonUtil.replaceXSS(request.getParameter("speedPoint"));
        String condition = CommonUtil.replaceXSS(request.getParameter("condition"));
        String privilege = CommonUtil.replaceXSS(request.getParameter("privilege"));
        String status = CommonUtil.replaceXSS(request.getParameter("status"));
        String id = request.getParameter("id");

        if (StringUtil.isEmpty(grade) || StringUtil.isEmpty(name)) {
            throw new BusinessCheckException("参数有误");
        }

        MtUserGrade info = new MtUserGrade();
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

        if (null == id) {
            userGradeService.addUserGrade(info);
        } else {
            info.setId(Integer.parseInt(id));
            userGradeService.updateUserGrade(info);
        }

        return "redirect:/backend/userGrade/queryList";
    }

    /**
     * 编辑初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/userGrade/editInit/{id}")
    @RequestMapping(value = "/editInit/{id}")
    public String editInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        MtUserGrade info = userGradeService.queryUserGradeById(id);
        UserGradeCatchTypeEnum[] catchTypeList = UserGradeCatchTypeEnum.values();

        model.addAttribute("catchTypeList", catchTypeList);
        model.addAttribute("info", info);

        return "userGrade/edit";
    }
}
