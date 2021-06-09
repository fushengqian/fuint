package com.fuint.application.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 发放卡券记录请求DTO
 * Created by zach on 2019/9/17.
 */
public class ReqSendLogDto implements Serializable {

    /**
     * ID
     * */
    private Integer id;

    /**
     * 1：单用户发券；2：批量发券
     * */
    private Integer type;

    /**
     * 用户ID
     * */
    private Integer user_id;

    /**
     * 导入文件名
     */
    private String file_name;

    /**
     * 导入文件路径
     */
    private String file_path;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 分组ID
     */
    private Integer group_id;

    /**
     * 分组名称
     */
    private String group_name;

    /**
     * 发放数量
     */
    private Integer send_num;

    /**
     * 发放时间
     * */
    private Date create_time;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 作态
     */
    private String status;

    /**
     * uuid
     */
    private String uuid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public String getFileName() {
        return file_name;
    }

    public void setFileName(String file_name) {
        this.file_name = file_name;
    }

    public String getFilePath() {
        return file_path;
    }

    public void setFilePath(String file_path) {
        this.file_path = file_path;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getGroupId() {
        return group_id;
    }

    public void setGroupId(Integer group_id) {
        this.group_id = group_id;
    }

    public String getGroupName() {
        return group_name;
    }

    public void setGroupName(String group_name) {
        this.group_name = group_name;
    }

    public Integer getSendNum() {
        return send_num;
    }

    public void setSendNum(Integer send_num) {
        this.send_num = send_num;
    }

    public Date getCreateTime() {
        return create_time;
    }

    public void setCreateTime(Date create_time) {
        this.create_time = create_time;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) { this.status = status;}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) { this.uuid = uuid;}
}
