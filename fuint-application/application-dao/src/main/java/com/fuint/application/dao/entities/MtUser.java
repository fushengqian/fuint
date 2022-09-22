package com.fuint.application.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * mt_user 实体类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Entity 
@Table(name = "mt_user")
public class MtUser implements Serializable{
   /**
    * 会员ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

    /**
     * 会员号
     */
    @Column(name = "USER_NO", length = 30)
    private String userNo;

    /**
     * 头像
     */
    @Column(name = "AVATAR", length = 100)
    private String avatar;

   /**
    * 称呼
    */ 
    @Column(name = "NAME", length = 30)
    private String name;

    /**
     * open_id
     */
    @Column(name = "OPEN_ID", length = 50)
    private String openId;

   /**
    * 手机号码 
    */ 
    @Column(name = "MOBILE", length = 20)
    private String mobile;

   /**
    * 证件号码 
    */ 
    @Column(name = "IDCARD", length = 20)
    private String idcard;

   /**
    * 等级
    */ 
    @Column(name = "GRADE_ID", length = 10)
    private String gradeId;

    /**
     * 默认店铺
     * */
    @Column(name = "STORE_ID", length = 10)
    private Integer storeId;

    /**
     * 等级开始时间
     */
    @Column(name = "START_TIME")
    private Date startTime;

    /**
     * 等级结束时间
     */
    @Column(name = "END_TIME")
    private Date endTime;

   /**
    * 性别 0男；1女 
    */ 
    @Column(name = "SEX", length = 1)
    private Integer sex;

   /**
    * 出生日期 
    */ 
    @Column(name = "BIRTHDAY", length = 20)
    private String birthday;

   /**
    * 车牌号 
    */ 
    @Column(name = "CAR_NO", length = 10)
    private String carNo;

    /**
     * 来源渠道
     */
    @Column(name = "SOURCE", length = 30)
    private String source;

   /**
    * 密码 
    */ 
    @Column(name = "PASSWORD", length = 32)
    private String password;

   /**
    * salt 
    */ 
    @Column(name = "SALT", length = 4)
    private String salt;

   /**
    * 地址 
    */ 
    @Column(name = "ADDRESS", length = 100)
    private String address;

    /**
     * 余额
     * */
    @Column(name = "BALANCE", length = 10)
    private BigDecimal balance;

   /**
    * 积分 
    */ 
    @Column(name = "POINT", length = 10)
    private Integer point;

   /**
    * 创建时间 
    */ 
    @Column(name = "CREATE_TIME")
    private Date createTime;

   /**
    * 更新时间 
    */ 
    @Column(name = "UPDATE_TIME", nullable = false)
    private Date updateTime;

   /**
    * 状态，A：激活；N：禁用；D：删除 
    */ 
    @Column(name = "STATUS", length = 1)
    private String status;

   /**
    * 备注信息 
    */ 
    @Column(name = "DESCRIPTION", length = 255)
    private String description;

   /**
    * 最后操作人 
    */ 
    @Column(name = "OPERATOR", length = 30)
    private String operator;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public String getUserNo(){
        return userNo;
    }
    public void setUserNo(String userNo){
        this.userNo=userNo;
    }
    public String getAvatar(){
        return avatar;
    }
    public void setAvatar(String avatar){
        this.avatar=avatar;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
    this.name=name;
    }
    public String getOpenId(){
        return openId;
    }
    public void setOpenId(String openId){
        this.openId=openId;
    }
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
    this.mobile=mobile;
    }
    public String getIdcard(){
        return idcard;
    }
    public void setIdcard(String idcard){
    this.idcard=idcard;
    }
    public String getGradeId(){
        return gradeId;
    }
    public void setGradeId(String gradeId){
    this.gradeId=gradeId;
    }
    public Date getStartTime(){
        return startTime;
    }
    public void setStartTime(Date startTime){
        this.startTime=startTime;
    }
    public Date getEndTime(){
        return endTime;
    }
    public void setEndTime(Date endTime){
        this.endTime=endTime;
    }
    public Integer getSex(){
        return sex;
    }
    public void setSex(Integer sex){
    this.sex=sex;
    }
    public String getBirthday(){
        return birthday;
    }
    public void setBirthday(String birthday){
    this.birthday=birthday;
    }
    public String getCarNo(){
        return carNo;
    }
    public void setCarNo(String carNo){
    this.carNo=carNo;
    }
    public String getSource(){
        return source;
    }
    public void setSource(String source){
        this.source=source;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
    this.password=password;
    }
    public String getSalt(){
        return salt;
    }
    public void setSalt(String salt){
    this.salt=salt;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
    this.address=address;
    }
    public BigDecimal getBalance(){
        return balance;
    }
    public void setBalance(BigDecimal balance){
        this.balance=balance;
    }
    public Integer getPoint(){
        return point;
    }
    public void setPoint(Integer point){
    this.point=point;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
        this.storeId=storeId;
    }
    public Date getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Date createTime){
    this.createTime=createTime;
    }
    public Date getUpdateTime(){
        return updateTime;
    }
    public void setUpdateTime(Date updateTime){
    this.updateTime=updateTime;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
    this.status=status;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
    this.description=description;
    }
    public String getOperator(){
        return operator;
    }
    public void setOperator(String operator){
    this.operator=operator;
    }
}

