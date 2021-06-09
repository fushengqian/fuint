package com.fuint.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderDto implements Serializable {
	/**
	 * 自增ID
	 */
	private Integer id;

	/**
	 * 订单类型
	 * */
	private String type;

	/**
	 * 订单号
	 */
	private String orderSn;

	/**
	 * 卡券ID
	 */
	private Integer couponId;

	/**
	 * 用户ID
	 */
	private Integer userId;

	/**
	 * 订单金额
	 */
	private BigDecimal amount;

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
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 状态
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
	public String getType(){
		return type;
	}
	public void setType(String type){
		this.type=type;
	}
	public String getOrderSn(){
		return orderSn;
	}
	public void setOrderSn(String orderSn){
		this.orderSn=orderSn;
	}
	public Integer getCouponId(){
		return couponId;
	}
	public void setCouponId(Integer couponId){
		this.couponId=couponId;
	}
	public Integer getUserId(){
		return userId;
	}
	public void setUserId(Integer userId){
		this.userId=userId;
	}
	public BigDecimal getAmount(){
		return amount;
	}
	public void setAmount(BigDecimal amount){
		this.amount=amount;
	}
	public String getParam(){
		return param;
	}
	public void setParam(String param){
		this.param=param;
	}
	public String getRemark(){
		return remark;
	}
	public void setRemark(String remark){
		this.remark=remark;
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

