package com.fuint.base.shiro.interceptor;

import com.fuint.base.shiro.ShiroUser;
import com.fuint.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 一般信息拦截器
 *
 * @author hanxiaoqiang
 * @version $Id: ShiroInterceptor.java, v 0.1 2015年11月25日 下午1:51:41 hanxiaoqiang Exp $
 */
public class ShiroInterceptor extends HandlerInterceptorAdapter {
    /**
     * 响应请求前进入拦截器
     *
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        this.collectClientInfo(request);
        return true;
    }

    /**
     * 方法前手机当前客户端信息
     *
     * @param request
     * @return
     */
    public ShiroUser collectClientInfo(HttpServletRequest request) {
        Subject sub = SecurityUtils.getSubject();
        ShiroUser user = (ShiroUser) sub.getPrincipal();
        if (user != null && user.getSources() != null) {
            user.setClientIp(request.getRemoteHost());
            user.setClientPort(request.getRemotePort());
            //平台选择页面不记录访问的url
            if (StringUtil.isNotBlank(request.getRequestURI()) && request.getRequestURI().indexOf("owner") <= -1) {
                Enumeration<String> parameterNames = request.getParameterNames();
                Map<String, Object> params = new HashMap<String, Object>();
                StringBuffer param = new StringBuffer();

                while (parameterNames.hasMoreElements()) {
                    String name = StringUtil.trim(parameterNames.nextElement().toString());
                    String value = StringUtil.trim(request.getParameter(name));
                    if (StringUtil.isNotBlank(value)) {
                        param.append(name);
                        param.append("=");
                        param.append(value);
                        param.append("&");
                    }
                }
                String requestUrl = "";
                if (StringUtil.isNotBlank(param.toString())) {
                    requestUrl = request.getRequestURI() + "?" + param.toString();
                } else {
                    requestUrl = request.getRequestURI();
                }
                user.setRequestURL(requestUrl);
            }
            user.setMethod(request.getMethod());
            user.setUserAgent(request.getHeader("user-agent"));
        }
        return user;
    }

}
