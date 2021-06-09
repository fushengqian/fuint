/**
 * rainbow.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.fuint.base.web;


import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.entities.TDuty;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.service.duty.TDutyService;
import com.fuint.base.service.platform.TPlatformService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 用户管理 - Controller
 *
 * @author fsq
 * @version $Id: AccountController.java, v 0.1 2015年10月26日 下午3:36:29 fsq Exp $
 */
@Controller
@RequestMapping(value = "/user")
public class AccountController {

    /**
     * 账户服务接口
     */
    @Autowired
    private TAccountService htAccountServiceImpl;

    /**
     * 平台接口服务
     */
    @Autowired
    private TPlatformService tPlatformService;

    /**
     * 角色
     */
    @Autowired
    private TDutyService tDutyService;

    /**
     * 账户信息列表展现
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 账户信息列表展现页面
     */
    @RequiresPermissions("user/query")
    @RequestMapping(value = "/query")
    public String accountList(HttpServletRequest request, HttpServletResponse response, Model model) {
        PaginationResponse<TAccount> paginationResponse = htAccountServiceImpl
                .findAccountsByPagination(RequestHandler.buildPaginationRequest(request, model));
        model.addAttribute("paginationResponse", paginationResponse);
        return "account/account_list";
    }

    /**
     * 新增账户页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 账户信息新增页面
     */
    @RequiresPermissions("user/add")
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addAccount(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("platforms", tPlatformService.getPlatforms());
        return "account/account_add";
    }

    /**
     * 新增账户页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 账户列表页面
     * @throws BusinessCheckException
     */
    @RequiresPermissions("user/add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addAccountHandler(HttpServletRequest request, HttpServletResponse response,
                                    Model model) throws BusinessCheckException {
        String params = request.getParameter("params");//获取角色所分配的菜单
        List<TDuty> duties = null;
        if (StringUtil.isNotBlank(params)) {
            String[] dutyIds = params.split(",");
            duties = tDutyService.findDatasByIds(dutyIds);
            if (duties.size() < dutyIds.length) {
                throw new BusinessCheckException("分配的角色不存在.");
            }
        }
        htAccountServiceImpl.addAccount((TAccount) RequestHandler.createBean(request,
                new TAccount()), duties, request.getParameter("platform"));
        return "redirect:/user/query";
    }

    /**
     * 修改账户页面
     *
     * @param request    HttpServletRequest对象
     * @param response   HttpServletResponse对象
     * @param model      SpringFramework Model对象
     * @param accountkey 账户编码
     * @return 账户信息修改页面
     */
    @RequiresPermissions("user/edit")
    @RequestMapping(value = "/edit/{accountkey}", method = RequestMethod.GET)
    public String editAccount(HttpServletRequest request, HttpServletResponse response,
                              Model model, @PathVariable("accountkey") String accountkey) {
        TAccount tAccount = htAccountServiceImpl.findAccountByKey(accountkey);
        if (tAccount != null) {
            List<Long> dutyIds = htAccountServiceImpl.getDutyIdsByAccountId(tAccount.getId());
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < dutyIds.size(); i++) {
                stringBuffer.append(dutyIds.get(i));
                if (i + 1 < dutyIds.size()) {
                    stringBuffer.append(",");
                }
            }
            model.addAttribute("selectedDuties", stringBuffer.toString());
        }
        model.addAttribute("platforms", tPlatformService.getPlatforms());
        model.addAttribute("account", tAccount);
        return "account/account_edit";
    }

    /**
     * 修改账户信息处理
     *
     * @param request
     * @param response
     * @param model
     * @param acctId
     * @return
     * @throws BusinessCheckException
     */
    @RequiresPermissions("user/edit")
    @RequestMapping(value = "/edit/{acctId}", method = RequestMethod.POST)
    public String editAccountHandler(HttpServletRequest request, HttpServletResponse response,
                                     Model model, @PathVariable("acctId") Long acctId) throws BusinessCheckException {
        TAccount tAccount = (TAccount) RequestHandler.createBean(request,
                new TAccount());
        tAccount.setId(acctId);

        String params = request.getParameter("params");//获取角色所分配的菜单
        List<TDuty> duties = null;
        if (StringUtil.isNotBlank(params)) {
            String[] sourceIds = params.split(",");
            duties = tDutyService.findDatasByIds(sourceIds);
            if (duties.size() < sourceIds.length) {
                throw new BusinessCheckException("分配的角色不存在.");
            }
        }

        htAccountServiceImpl.editAccount(tAccount, duties, request.getParameter("platform"));
        return "redirect:/user/query";
    }

    /**
     * 删除账户信息
     *
     * @param request    HttpServletRequest对象
     * @param response   HttpServletResponse对象
     * @param model      SpringFramework Model对象
     * @param accountkey 账户编码
     * @return 跳转回账户列表页面
     * @throws BusinessCheckException
     */
    @RequiresPermissions("user/delete")
    @RequestMapping(value = "/delete/{accountkey}", method = RequestMethod.GET)
    public String deleteAccount(HttpServletRequest request, HttpServletResponse response,
                                Model model, @PathVariable("accountkey") String accountkey)
            throws BusinessCheckException {
        TAccount tAccount = htAccountServiceImpl.findAccountByKey(accountkey);
        if(tAccount == null){
            throw new BusinessCheckException("账户不存在.");
        }
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if(StringUtil.equals(shiroUser.getAcctName(),tAccount.getAccountName())){
            throw new BusinessCheckException("您不能删除自己.");
        }
        htAccountServiceImpl.deleteAccount(accountkey);//删除账户
        return "redirect:/user/query";
    }

    /**
     * 账户详情
     *
     * @param request    HttpServletRequest对象
     * @param response   HttpServletResponse对象
     * @param model      SpringFramework Model对象
     * @param accountkey 账户编码
     * @return 账户详情页面
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/profile/{accountkey}", method = RequestMethod.GET)
    public String profileAccount(HttpServletRequest request, HttpServletResponse response,
                                 Model model, @PathVariable("accountkey") String accountkey)
            throws BusinessCheckException {
        return "account/account_profile";
    }

    /**
     * 修改账户密码页面
     *
     * @return 账户信息修改页面
     */
    @RequiresPermissions("user/editpwdinit")
    @RequestMapping(value = "/editpwdinit", method = RequestMethod.GET)
    public String editpwdinit(HttpServletRequest request, HttpServletResponse response,
                              Model model) {
        return "account/account_update_pwd";
    }

    /**
     * 修改账户密码页面
     *
     * @return 账户信息修改页面
     */
    @RequiresPermissions("user/editpwd")
    @RequestMapping(value = "/editpwd", method = RequestMethod.POST)
    public String editpwd(HttpServletRequest request, HttpServletResponse response,
                          Model model) {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        TAccount tAccount = htAccountServiceImpl.findAccountById(shiroUser.getId());
        tAccount = (TAccount) RequestHandler.createBean(request, tAccount);
        if (tAccount != null) {
            htAccountServiceImpl.entryptPassword(tAccount);
            htAccountServiceImpl.updateAccount(tAccount);
        }
        ShiroUserHelper.cleanUser();
        return "redirect:/login";
    }

}
