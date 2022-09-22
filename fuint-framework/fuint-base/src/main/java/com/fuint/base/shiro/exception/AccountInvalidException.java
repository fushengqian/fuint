package com.fuint.base.shiro.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 账户无效异常
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public class AccountInvalidException extends AuthenticationException {

    public AccountInvalidException() {
        super();
    }

    public AccountInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountInvalidException(String message) {
        super(message);
    }

    public AccountInvalidException(Throwable cause) {
        super(cause);
    }
}
