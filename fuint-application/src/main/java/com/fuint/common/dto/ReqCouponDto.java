package com.fuint.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 卡券请求DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class ReqCouponDto implements Serializable {

    /**
     * 券ID
     */
    private Integer id;

    /**
     * 分组ID
     * */
    private Integer groupId;

    /**
     * 类型
     */
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 是否允许转赠
     */
    private Integer isGive;

    /**
     * 获得卡券所消耗积分
     */
    private Integer point;

    /**
     * 获得计次卡卡所消耗积分
     */
    private Integer timerPoint;

    /**
     * 领取码
     */
    private String receiveCode;

    /**
     * 使用专项
     */
    private String useFor;

    /**
     * 计次卡领取码
     */
    private String timerReceiveCode;

    /**
     * 有效期开始时间
     * */
    private String beginTime;

    /**
     * 有效期结束时间
     * */
    private String endTime;

    /**
     * 价值金额
     * */
    private BigDecimal amount;

    /**
     * 发放方式
     * */
    private String sendWay;

    /**
     * 适用商品
     * */
    private String applyGoods;

    /**
     * 每次发放数量
     * */
     private Integer sendNum;

    /**
     * 发行总数量
     * */
    private Integer total;

    /**
     * 每人最多拥有数量
     * */
    private Integer limitNum;

     /**
      * 例外时间
      * */
     private String exceptTime;

    /**
     * 店铺Id
     * */
    private String storeIds;

    /**
     * 会员等级Id
     * */
    private String gradeIds;

    /**
     * 适用商品
     * */
    private String goodsIds;

    /**
     * 后台备注
     * */
    private String remarks;

    /**
     * 图片
     * */
    private String image;

    /**
     * 备注
     */
    private String description;

    /**
     * 预存规则
     * */
    private String inRule;

    /**
     * 核销规则
     * */
    private String outRule;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 状态
     * */
    private String status;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsGive(){
        return isGive;
    }
    public void setIsGive(Integer isGive){
        this.isGive=isGive;
    }

    public Integer getPoint(){
        return point;
    }
    public void setPoint(Integer point){
        this.point=point;
    }

    public Integer getTimerPoint(){
        return timerPoint;
    }
    public void setTimerPoint(Integer timerPoint){
        this.timerPoint=timerPoint;
    }

    public String getReceiveCode(){
        return receiveCode;
    }
    public void setReceiveCode(String receiveCode){
        this.receiveCode=receiveCode;
    }

    public String getUseFor(){
        return useFor;
    }
    public void setUseFor(String useFor){
        this.useFor=useFor;
    }

    public String getTimerReceiveCode(){
        return timerReceiveCode;
    }
    public void setTimerReceiveCode(String timerReceiveCode){
        this.timerReceiveCode=timerReceiveCode;
    }

    public String getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSendWay() {
        return sendWay;
    }
    public void setSendWay(String sendWay) {
        this.sendWay = sendWay;
    }

    public String getApplyGoods() {
        return applyGoods;
    }
    public void setApplyGoods(String applyGoods) {
        this.applyGoods = applyGoods;
    }

    public Integer getSendNum() {
        return sendNum;
    }
    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public Integer getTotal() {
        return total;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getLimitNum() {
        return limitNum;
    }
    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public String getExceptTime() {
        return exceptTime;
    }
    public void setExceptTime(String exceptTime) {
        this.exceptTime = exceptTime;
    }

    public String getStoreIds() {
        return storeIds;
    }
    public void setStoreIds(String storeIds) {
        this.storeIds = storeIds;
    }

    public String getGradeIds() {
        return gradeIds;
    }
    public void setGradeIds(String gradeIds) {
        this.gradeIds = gradeIds;
    }

    public String getGoodsIds() {
        return goodsIds;
    }
    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getInRule() {
        return inRule;
    }
    public void setInRule(String inRule) {
        this.inRule = inRule;
    }

    public String getOutRule() {
        return outRule;
    }
    public void setOutRule(String outRule) {
        this.outRule = outRule;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) { this.status = status;}
}
