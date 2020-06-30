package com.fuint.coupon.web.backend.util;

import java.io.Serializable;

/**
 * 上传图片返回结果
 * Created by liuguofang on 2016/9/13.
 */
public class UploadResult implements Serializable{
    /**
     * 结果状态(success,fail)
     */
    private String status;

    /**
     * 图片的相对路径
     */
    private String filename;

    /**
     * 图片的绝对路径
     */
    private String rfilename;

    private String message;

    /**
     * 批次编码
     */
    private String batchCode;

    public String getRfilename() {
        return rfilename;
    }

    public void setRfilename(String rfilename) {
        this.rfilename = rfilename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }
}
