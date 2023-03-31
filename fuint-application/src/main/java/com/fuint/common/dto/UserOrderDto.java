package com.fuint.common.dto;

import com.fuint.repository.model.MtRefund;
import com.fuint.repository.model.MtStore;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 会员订单实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class UserOrderDto implements Serializable {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 类型
     */
    private String type;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 支付类型
     * */
    private String payType;

    /**
     * 订单模式
     */
    private String orderMode;

    /**
     * 是否核销
     * */
    private boolean isVerify;

    /**
     * 卡券ID
     */
    private Integer couponId;

    /**
     * 会员ID
     */
    private Integer userId;

    /**
     * 是否游客
     */
    private String isVisitor;

    /**
     * 核销码
     * */
    private String verifyCode;

    /**
     * 员工ID
     */
    private Integer staffId;

    /**
     * 总金额
     */
    private BigDecimal amount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discount;

    /**
     * 使用积分
     * */
    private Integer usePoint;

    /**
     * 积分金额
     */
    private BigDecimal pointAmount;

    /**
     * 订单参数
     */
    private String param;

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
     * 支付时间
     */
    private String payTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 支付状态
     */
    private String payStatus;

    /**
     * 状态
     */
    private String statusText;

    /**
     * 最后操作人
     */
    private String operator;

    /**
     * 订单商品列表
     */
    private List<OrderGoodsDto> goods;

    /**
     * 下单用户信息
     */
    private OrderUserDto userInfo;

    /**
     * 配送地址
     */
    private AddressDto address;

    /**
     * 物流信息
     * */
    private ExpressDto expressInfo;

    /**
     * 所属店铺
     */
    private MtStore storeInfo;

    /**
     * 售后订单
     * */
    private MtRefund refundInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType=payType;
    }

    public String getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(String orderMode) {
        this.orderMode = orderMode;
    }

    public boolean getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(boolean isVerify) {
        this.isVerify = isVerify;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getIsVisitor() {
        return isVisitor;
    }

    public void setIsVisitor(String isVisitor) {
        this.isVisitor = isVisitor;
    }

    public Integer getStaffId(){
        return staffId;
    }

    public void setStaffId(Integer staffId){
        this.staffId=staffId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Integer getUsePoint() {
        return usePoint;
    }

    public void setUsePoint(Integer usePoint) {
        this.usePoint = usePoint;
    }

    public BigDecimal getPointAmount() {
        return pointAmount;
    }

    public void setPointAmount(BigDecimal pointAmount) {
        this.pointAmount = pointAmount;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
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

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<OrderGoodsDto> getGoods() {
        return goods;
    }

    public void setGoods(List<OrderGoodsDto> goods) {
        this.goods = goods;
    }

    public OrderUserDto getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(OrderUserDto userInfo) {
        this.userInfo = userInfo;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public ExpressDto getExpressInfo() {
        return expressInfo;
    }

    public void setExpressInfo(ExpressDto expressInfo) {
        this.expressInfo = expressInfo;
    }

    public MtStore getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(MtStore storeInfo) {
        this.storeInfo = storeInfo;
    }

    public MtRefund getRefundInfo() {
        return refundInfo;
    }

    public void setRefundInfo(MtRefund refundInfo) {
        this.refundInfo = refundInfo;
    }
}

