package com.fuint.application.dto;

public class TokenDto {

    private String token;

    //token创建时间
    private Long tokenCreatedTime;

    //失效时间
    private Long tokenExpiryTime;
    //是否登录 true or false
    private String isLogin;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTokenCreatedTime() {
        return tokenCreatedTime;
    }

    public void setTokenCreatedTime(Long tokenCreatedTime) {
        this.tokenCreatedTime = tokenCreatedTime;
    }

    public Long getTokenExpiryTime() {
        return tokenExpiryTime;
    }

    public void setTokenExpiryTime(Long tokenExpiryTime) {
        this.tokenExpiryTime = tokenExpiryTime;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }
}
