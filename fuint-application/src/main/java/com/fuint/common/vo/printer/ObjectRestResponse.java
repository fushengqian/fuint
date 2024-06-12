package com.fuint.common.vo.printer;

/**
 * 返回公共参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class ObjectRestResponse<T> {

    public static final String REST_RESPONSE_OK = "ok";

    /**
     * 返回码，正确返回0，【注意：结果正确与否的判断请用此返回参数】，错误返回非零
     */
    private int code;
    /**
     * 结果提示信息，正确返回”ok”，如果有错误，返回错误信息
     */
    private String msg;
    /**
     * 数据类型和内容详看私有返回参数data，如果有错误，返回null
     */
    private T data;
    /**
     * 服务器程序执行时间，单位：毫秒
     */
    private long serverExecutedTime;

    public ObjectRestResponse() {
        this.setCode(0);
        this.setMsg(REST_RESPONSE_OK);
    }

    public ObjectRestResponse code(int code) {
        this.setCode(code);
        return this;
    }

    public ObjectRestResponse data(T data) {
        this.setData(data);
        return this;
    }

    public ObjectRestResponse msg(String msg) {
        this.setMsg(msg);
        return this;
    }

    public ObjectRestResponse setResult(int code, T data) {
        this.setCode(code);
        this.setData(data);
        return this;
    }

    public ObjectRestResponse setResult(int code, T data, String msg) {
        this.setCode(code);
        this.setData(data);
        this.setMsg(msg);
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getServerExecutedTime() {
        return serverExecutedTime;
    }

    public void setServerExecutedTime(long serverExecutedTime) {
        this.serverExecutedTime = serverExecutedTime;
    }
}
