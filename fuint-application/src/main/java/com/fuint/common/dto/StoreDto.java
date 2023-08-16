package com.fuint.common.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺实体
 * */
public class StoreDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 所属商户ID
     * */
    private Integer merchantId;

    /**
     * 所属商户名称
     * */
    private String merchantName;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺LOGO
     * */
    private String logo;

    /**
     * 是否默认店铺
     */
    private String isDefault;

    /**
     * 联系人姓名
     */
    private String contact;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 店铺地址
     */
    private String address;

    /**
     * 营业时间
     */
    private String hours;

    /**
     * 经度
     */
    private String latitude;

    /**
     * 纬度
     */
    private String longitude;

    /**
     * 备注信息
     */
    private String description;

    /**
     * 微信商户号
     */
    private String wxMchId;

    /**
     * 微信支付秘钥
     */
    private String wxApiV2;

    /**
     * 支付宝appId
     * */
    private String alipayAppId;

    /**
     * 支付宝应用私钥
     * */
    private String alipayPrivateKey;

    /**
     * 支付宝支付公钥
     * */
    private String alipayPublicKey;

    /**
     * 营业执照
     * */
    private String license;

    /**
     * 统一社会信用代码
     * */
    private String creditCode;

    /**
     * 银行名称
     * */
    private String bankName;

    /**
     * 银行卡账户名
     * */
    private String bankCardName;

    /**
     * 银行卡卡号
     * */
    private String bankCardNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 状态，1：正常；2：删除
     */
    private String status;

    /**
     * 最后操作人
     */
    private String operator;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id=id;
    }
    public Integer getMerchantId(){
        return merchantId;
    }
    public void setMerchantId(Integer merchantId){
        this.merchantId=merchantId;
    }
    public String getMerchantName(){
        return merchantName;
    }
    public void setMerchantName(String merchantName){
        this.merchantName=merchantName;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getLogo(){
        return logo;
    }
    public void setLogo(String logo){
        this.logo=logo;
    }
    public String getIsDefault(){
        return isDefault;
    }
    public void setIsDefault(String isDefault){
        this.isDefault=isDefault;
    }
    public String getContact(){
        return contact;
    }
    public void setContact(String contact){
        this.contact=contact;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address=address;
    }
    public String getHours(){
        return hours;
    }
    public void setHours(String hours){
        this.hours=hours;
    }
    public String getLatitude(){
        return latitude;
    }
    public void setLatitude(String latitude){
        this.latitude=latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public void setLongitude(String longitude){
        this.longitude=longitude;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public String getWxMchId(){
        return wxMchId;
    }
    public void setWxMchId(String wxMchId){
        this.wxMchId=wxMchId;
    }
    public String getWxApiV2(){
        return wxApiV2;
    }
    public void setWxApiV2(String wxApiV2){
        this.wxApiV2=wxApiV2;
    }
    public String getAlipayAppId(){
        return alipayAppId;
    }
    public void setAlipayAppId(String alipayAppId){
        this.alipayAppId=alipayAppId;
    }
    public String getAlipayPrivateKey(){
        return alipayPrivateKey;
    }
    public void setAlipayPrivateKey(String alipayPrivateKey){
        this.alipayPrivateKey=alipayPrivateKey;
    }
    public String getAlipayPublicKey(){
        return alipayPublicKey;
    }
    public void setAlipayPublicKey(String alipayPublicKey){
        this.alipayPublicKey=alipayPublicKey;
    }
    public String getLicense(){
        return license;
    }
    public void setLicense(String license){
        this.license=license;
    }
    public String getCreditCode(){
        return creditCode;
    }
    public void setCreditCode(String creditCode){
        this.creditCode=creditCode;
    }
    public String getBankName(){
        return bankName;
    }
    public void setBankName(String bankName){
        this.bankName=bankName;
    }
    public String getBankCardName(){
        return bankCardName;
    }
    public void setBankCardName(String bankCardName){
        this.bankCardName=bankCardName;
    }
    public String getBankCardNo(){
        return bankCardNo;
    }
    public void setBankCardNo(String bankCardNo){
        this.bankCardNo=bankCardNo;
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
    public String getOperator(){
        return operator;
    }
    public void setOperator(String operator){
        this.operator=operator;
    }
}