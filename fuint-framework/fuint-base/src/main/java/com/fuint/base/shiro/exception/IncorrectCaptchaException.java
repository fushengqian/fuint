package com.fuint.base.shiro.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 验证码异常
 * <p/>
 * Created by hanxiaoqiang on 16/7/6.
 */
public class IncorrectCaptchaException extends AuthenticationException {

    public IncorrectCaptchaException() {
        super();
    }

    public IncorrectCaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectCaptchaException(String message) {
        super(message);
    }

    public IncorrectCaptchaException(Throwable cause) {
        super(cause);
    }
}
