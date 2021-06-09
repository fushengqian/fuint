package com.fuint.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * UserOrderDto 实体类
 * Created by zach
 * Tue Apr 13 16:31:40 GMT+08:00 2021
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
	 * 卡券ID
	 */
	private Integer couponId;

	/**
	 * 用户ID
	 */
	private Integer userId;

	/**
	 * 总金额
	 * */
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
	 * 最后操作人
	 */
	private String operator;

	/**
	 * 订单商品列表
	 * */
	private List<OrderGoodsDto> goods;

	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id=id;
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
	public String getCreateTime(){
		return createTime;
	}
	public void setCreateTime(String createTime){
		this.createTime=createTime;
	}
	public String getUpdateTime(){
		return updateTime;
	}
	public void setUpdateTime(String updateTime){
		this.updateTime=updateTime;
	}
	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
		this.status=status;
	}
	public String getStatusText(){
		return statusText;
	}
	public void setStatusText(String statusText){
		this.statusText=statusText;
	}
	public String getOperator(){
		return operator;
	}
	public void setOperator(String operator){
		this.operator=operator;
	}
	public List<OrderGoodsDto> getGoods(){
		return goods;
	}
	public void setGoods(List<OrderGoodsDto> goods){
		this.goods=goods;
	}
}

