package com.fuint.base.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 用户 密码 验证码 token
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public class CaptchaUsernamePasswordToken extends UsernamePasswordToken {

    private String captcha;


    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public CaptchaUsernamePasswordToken(String username, String password,
                                        boolean rememberMe, String captcha) {
        super(username, password, rememberMe);
        this.captcha = captcha;
    }

}
