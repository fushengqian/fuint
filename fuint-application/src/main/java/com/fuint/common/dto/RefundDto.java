package com.fuint.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 售后实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class RefundDto implements Serializable {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 店铺ID
     */
    private Integer storeId;

    /**
     * 退款金额
     */
    private BigDecimal amount;

    /**
     * 售后类型
     */
    private String type;

    /**
     * 会员ID
     */
    private Integer userId;

    /**
     * 用户备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态
     */
    private String statusText;

    /**
     * 申请凭证图片
     */
    private List<String> imageList;

    /**
     * 申请凭证图片
     */
    private String images;

    /**
     * 最后操作人
     */
    private String operator;

    /**
     * 订单详情
     * */
    private UserOrderDto orderInfo;

    /**
     * 退货地址
     */
    private AddressDto address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStoreId(){
        return storeId;
    }

    public void setStoreId(Integer storeId){
        this.storeId=storeId;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public void setAmount(BigDecimal amount){
        this.amount=amount;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type=type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public List<String> getImageList(){
        return imageList;
    }

    public void setImageList(List<String> imageList){
        this.imageList=imageList;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public UserOrderDto getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(UserOrderDto orderInfo) {
        this.orderInfo = orderInfo;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }
}

