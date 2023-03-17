package com.fuint.common.web;

import com.fuint.common.Constants;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.service.MemberService;
import com.fuint.common.util.TokenUtil;
import com.fuint.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 会员端登录拦截器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class ClientUserInterceptor implements AsyncHandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ClientUserInterceptor.class);

    @Resource
    private MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跨域预检请求处理
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            logger.info("预检请求地址{}...", request.getRequestURI());
            return true;
        }

        String accessToken = request.getHeader("Access-Token");
        String requestURI = request.getRequestURI();
        if (StringUtils.isEmpty(accessToken)) {
            if (!requestURI.equals("/clientApi/system/config")) {
                response.setHeader("Content-Type", "application/json;charset=UTF-8");
                response.getOutputStream().print("{\"code\":1001,\"message\":\"" + PropertiesUtil
                        .getResponseErrorMessageByCode(Constants.HTTP_RESPONSE_CODE_NOLOGIN) + "\",\"data\":null}");
                return false;
            } else {
                return true;
            }
        }

        // 验证session中的Token
        UserInfo loginInfo = TokenUtil.getUserInfoByToken(accessToken);
        if (loginInfo != null) {
            if (!StringUtils.isEmpty(loginInfo.getToken()) && loginInfo.getToken().equals(accessToken)) {
                // 更新活跃时间
                boolean isActive = memberService.updateActiveTime(loginInfo.getId());
                if (!isActive) {
                    return false;
                }
                return true;
            }
        } else {
            if (requestURI.equals("/clientApi/system/config")) {
                return true;
            }
        }

        logger.info("根据token未查到用户信息,token={}, url={}", accessToken, request.getRequestURI());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.getOutputStream().print("{\"code\":1001,\"message\":\"" + PropertiesUtil
                .getResponseErrorMessageByCode(Constants.HTTP_RESPONSE_CODE_NOLOGIN) + "\",\"data\":null}");
        return false;
    }
}
