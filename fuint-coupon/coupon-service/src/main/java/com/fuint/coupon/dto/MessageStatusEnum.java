package com.fuint.coupon.dto;

/**
 * 消息状态枚举
 * Created by wang.yq on 2016/7/25.
 */
public enum MessageStatusEnum {
    SUCCESS("000000","成功"),
    FAILD("999999","服务执行异常"),
    NO_AUTHORIZE("unauthorized","没有权限访问"),

    IN_PARAMETER_NULL("1001","入参不可为空"),
    IN_PARAMETER_NULL_OR_LESS("1002","入参不可为空或者缺少参数");


    private MessageStatusEnum(String returnCode, String returnDesc){
        this.returnCode = returnCode;
        this.returnDesc = returnDesc;
    }

    private String returnCode;
    private String returnDesc;

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
}
