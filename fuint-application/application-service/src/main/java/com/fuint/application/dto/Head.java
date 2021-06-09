package com.fuint.application.dto;

import java.io.Serializable;

/**
 * 消息体头信息
 * Created by zach on 2016/7/19.
 */
public class Head implements Serializable{

    private static final long serialVersionUID = -4183589142746100110L;
    //服务编号，用于确定唯一的服务
    private String serviceId;
    //授权Token信息
    private String token;
    //服务执行返回码(000000:正常)
    private String returnCode;
    //服务执行返回信息
    private String returnDesc;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnDesc() {
        return returnDesc;
    }

    public void setReturnDesc(String returnDesc) {
        this.returnDesc = returnDesc;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Head{");
        sb.append("serviceId='").append(serviceId).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append(", returnCode='").append(returnCode).append('\'');
        sb.append(", returnDesc='").append(returnDesc).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
