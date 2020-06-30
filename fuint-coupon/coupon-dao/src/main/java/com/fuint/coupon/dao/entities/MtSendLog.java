package com.fuint.coupon.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;

   /**
    * mt_send_Log 实体类
    * Created by zach
    * Mon Sep 16 17:22:38 GMT+08:00 2019
    */ 
@Entity 
@Table(name = "mt_send_Log")
public class MtSendLog implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 1：单用户发券；2：批量发券 
    */ 
    @Column(name = "TYPE", nullable = false, length = 1)
    private Byte type;

   /**
    * 用户ID 
    */ 
    @Column(name = "USER_ID", length = 10)
    private Integer userId;

   /**
    * 导入excel文件名 
    */ 
    @Column(name = "FILE_NAME", length = 100)
    private String fileName;

       /**
        * 导入excel文件路径
        */
       @Column(name = "FILE_PATH", length = 200)
       private String filePath;

   /**
    * 用户手机 
    */ 
    @Column(name = "MOBILE", nullable = false, length = 20)
    private String mobile;

   /**
    * 分组ID
    */ 
    @Column(name = "GROUP_ID", nullable = false, length = 10)
    private Integer groupId;

   /**
    * 分组名称
    */
   @Column(name = "GROUP_NAME", length = 100)
   private String groupName;

   /**
    * 发放套数 
    */ 
    @Column(name = "SEND_NUM", length = 10)
    private Integer sendNum;

   /**
    * 操作时间 
    */ 
    @Column(name = "CREATE_TIME")
    private Date createTime;

   /**
    * 操作人 
    */ 
    @Column(name = "OPERATOR", length = 30)
    private String operator;

   /**
    * 状态，A正常；B：部分作废；D全部作废 
    */ 
    @Column(name = "STATUS", length = 1)
    private String status;

   /**
    * 导入uuid
    */
   @Column(name = "UUID", length = 50)
   private String uuid;

   /**
    * 作废成功张数
    */
   @Column(name = "REMOVE_SUCCESS_NUM", length = 10)
   private Integer removeSuccessNum;

   /**
    * 作废失败张数
    */
   @Column(name = "REMOVE_FAIL_NUM", length = 10)
   private Integer removeFailNum;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public Byte getType(){
        return type;
    }
    public void setType(Byte type){
    this.type=type;
    }
    public Integer getUserId(){
        return userId;
    }
    public void setUserId(Integer userId){
    this.userId=userId;
    }
    public String getFileName(){
        return fileName;
    }
    public void setFileName(String fileName){
    this.fileName=fileName;
    }
    public String getFilePath(){
       return filePath;
   }
    public void setFilePath(String filePath){
       this.filePath=filePath;
    }
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
    this.mobile=mobile;
    }
    public Integer getGroupId(){
        return groupId;
    }
    public void setGroupId(Integer groupId){
    this.groupId=groupId;
    }
    public String getGroupName(){
           return groupName;
       }
    public void setGroupName(String groupName){
           this.groupName=groupName;
       }
    public Integer getSendNum(){
        return sendNum;
    }
    public void setSendNum(Integer sendNum){
    this.sendNum=sendNum;
    }
    public Date getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Date createTime){
    this.createTime=createTime;
    }
    public String getOperator(){
        return operator;
    }
    public void setOperator(String operator){
    this.operator=operator;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
    this.status=status;
    }
    public String getUuid() {
           return uuid;
       }
    public void setUuid(String uuid) { this.uuid = uuid;}
    public Integer getRemoveSuccessNum(){
           return removeSuccessNum;
       }
    public void setRemoveSuccessNum(Integer removeSuccessNum){
           this.removeSuccessNum=removeSuccessNum;
       }
    public Integer getRemoveFailNum(){
           return removeFailNum;
       }
    public void setRemoveFailNum(Integer removeFailNum){
           this.removeFailNum=removeFailNum;
       }
}

