package com.fuint.common.web;

import com.fuint.common.Constants;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.service.MerchantService;
import com.fuint.common.util.AuthUserUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.utils.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 后台登录拦截
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class AdminUserInterceptor implements AsyncHandlerInterceptor {

    @Resource
    private MerchantService merchantService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader("Access-Token");

        // 验证Token
        if (StringUtils.isEmpty(accessToken)) {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.getOutputStream().print("{\"code\":1001,\"message\":\"" + PropertiesUtil
                    .getResponseErrorMessageByCode(Constants.HTTP_RESPONSE_CODE_NOLOGIN) + "\",\"data\":null}");
            return false;
        }

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(accessToken);
        // 验证session中的Token
        if (accountInfo != null && accountInfo.getToken().equals(accessToken)) {
            // 校验商户是否已过期，过期则清除token并拒绝请求
            if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
                try {
                    merchantService.checkMerchantValid(accountInfo.getMerchantId());
                } catch (BusinessCheckException e) {
                    TokenUtil.removeToken(accessToken);
                    response.setHeader("Content-Type", "application/json;charset=UTF-8");
                    response.getOutputStream().print("{\"code\":1003,\"message\":\"商户使用权已过期\",\"data\":null}");
                    return false;
                }
            }
            AuthUserUtil.set(accountInfo);
            return true;
        }

        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.getOutputStream().print("{\"code\":1001,\"message\":\"" + PropertiesUtil
                .getResponseErrorMessageByCode(Constants.HTTP_RESPONSE_CODE_NOLOGIN) + "\",\"data\":null}");
        return false;
    }
}
