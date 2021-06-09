package com.fuint.application.dto;

import java.io.Serializable;

/**
 * 请求结果bean
 *
 * Created by zach on 2021/3/18.
 */
public class ReqResult implements Serializable {

    private String resultCode;
    private String msg;
    private boolean result;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
