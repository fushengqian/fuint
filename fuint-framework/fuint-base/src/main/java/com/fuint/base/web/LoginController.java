package com.fuint.base.web;

import com.fuint.base.dao.entities.TStore;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.repositories.TStoreRepository;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.exception.AccountInvalidException;
import com.fuint.base.shiro.exception.IncorrectCaptchaException;
import com.fuint.base.shiro.filter.AuthFilter;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.ForbiddenException;
import com.fuint.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * LoginController负责打开登录页面(GET请求)和登录出错页面(POST请求)，
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
public class LoginController {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * 账户服务接口
     */
    @Autowired
    private TAccountService accountService;

    @Autowired
    private TStoreRepository storeRepository;

    /**
     * 根路径跳转登陆页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/to", method = RequestMethod.GET)
    public String context(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "redirect:/login";
    }

    /**
     * 处理登陆get请求，加载登陆页面
     *
     * @return 登陆页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();

        // 已登陆则 跳到首页
        if (subject.isAuthenticated()) {
            ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
            accountService.doLogin(shiroUser);
            return "redirect:/home";
        }

        String requestType = request.getHeader("X-Requested-With");
        if (StringUtil.isNotBlank(requestType)) {
            // 清除保存的前一次访问URL
            WebUtils.getAndClearSavedRequest(request);
            throw new ForbiddenException();
        }

        return "login";
    }

    /**
     * 首页Get 请求
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String indexGet(HttpServletRequest request, HttpServletResponse response, Model model) {
        ShiroUser shirouser = ShiroUserHelper.getCurrentShiroUser();
        if (shirouser == null) {
            return "redirect:/login";
        }
        TAccount account = accountService.findAccountById(shirouser.getId());
        Integer storeId = account.getStoreId();

        TStore storeInfo = null;
        if (storeId > 0) {
            storeInfo = storeRepository.findOne(storeId.longValue());
        }

        model.addAttribute("storeInfo", storeInfo);

        return "index";
    }

    /**
     * 首页Post请求
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/home", method = RequestMethod.POST)
    public String indexPost(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "index";
    }

    /**
     * 处理身份认证异常后的 post 请求
     *
     * @param userName 登陆用户账户
     * @param model
     * @return 登陆异常后定向路径
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, @RequestParam(value = AuthFilter.DEFAULT_USERNAME_PARAM, required = true) String userName, Model model) {
        Subject subject = SecurityUtils.getSubject();

        // 已登陆则 跳到首页
        if (subject.isAuthenticated()) {
            return "redirect:/home";
        }

        String exception = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);

        logger.info("====user {} login information===={}", userName, exception);
        if (StringUtil.equals(UnknownAccountException.class.getName(), exception)) {
            model.addAttribute("loginError", "账户不存在！");
        } else if (StringUtil.equals(IncorrectCredentialsException.class.getName(), exception)) {
            model.addAttribute("loginError", "密码错误！");
        } else if (StringUtil.equals(LockedAccountException.class.getName(), exception)) {
            model.addAttribute("loginError", "账户已经锁定，请联系管理员！");
        } else if (StringUtil.equals(ExcessiveAttemptsException.class.getName(), exception)) {
            model.addAttribute("loginError", "密码错误3次，锁定登陆3分钟！");
        } else if (StringUtil.equals(IncorrectCaptchaException.class.getName(), exception)) {
            model.addAttribute("loginError", "验证码错误！");
        } else if (StringUtil.equals(AccountInvalidException.class.getName(), exception)) {
            model.addAttribute("loginError", "账户状态错误,请联系管理员！");
        } else {
            model.addAttribute("loginError", "登录异常，请重试！");
        }

        model.addAttribute("userName", userName);

        return "login";
    }

    /**
     * 处理退出get请求，加载登陆页面
     *
     * @return 退出登陆
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        accountService.logout(shiroUser);

        logger.info("====退出登录===={}", shiroUser);

        ShiroUserHelper.cleanUser();
        return "redirect:/login";
    }
}
