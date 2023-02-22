package com.fuint.common.dto;

import com.fuint.repository.model.MtGoodsCate;
import com.fuint.repository.model.MtGoodsSku;
import com.fuint.repository.model.MtGoodsSpec;
import com.fuint.repository.model.MtStore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class GoodsDto implements Serializable {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 所属店铺ID
     * */
    private Integer storeId;

    /**
     * 所属店铺信息
     * */
    private MtStore storeInfo;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品类型
     */
    private String type;

    /**
     * 分类ID
     */
    private Integer cateId;

    /**
     * 分类信息
     * */
    private MtGoodsCate cateInfo;

    /**
     * 商品条码
     */
    private String goodsNo;

    /**
     * 可否单规格
     */
    private String isSingleSpec;

    /**
     * 主图地址
     */
    private String logo;

    /**
     * 图片地址
     */
    private String images;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 划线价格
     */
    private BigDecimal linePrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 服务时长
     */
    private Integer serviceTime;

    /**
     * 服务时长
     */
    private String couponIds;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 初始销量
     */
    private Integer initSale;

    /**
     * 商品卖点
     */
    private String salePoint;

    /**
     * 可否使用积分抵扣
     */
    private String canUsePoint;

    /**
     * 会员是否有折扣
     */
    private String isMemberDiscount;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 最后操作人
     */
    private String operator;

    /**
     * A：正常；D：删除
     */
    private String status;

    /**
     * sku列表
     * */
    private List<MtGoodsSku> skuList;

    /**
     * 规格列表
     * */
    private List<MtGoodsSpec> specList;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id=id;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
        this.storeId=storeId;
    }
    public MtStore getStoreInfo(){
        return storeInfo;
    }
    public void setStoreInfo(MtStore storeInfo){
        this.storeInfo=storeInfo;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type=type;
    }
    public Integer getCateId(){
        return cateId;
    }
    public void setCateId(Integer cateId){
        this.cateId=cateId;
    }
    public MtGoodsCate getCateInfo(){
        return cateInfo;
    }
    public void setCateInfo(MtGoodsCate cateInfo){
        this.cateInfo=cateInfo;
    }
    public String getGoodsNo(){
        return goodsNo;
    }
    public void setGoodsNo(String goodsNo){
        this.goodsNo=goodsNo;
    }
    public String getIsSingleSpec(){
        return isSingleSpec;
    }
    public void setIsSingleSpec(String isSingleSpec){
        this.isSingleSpec=isSingleSpec;
    }
    public String getLogo(){
        return logo;
    }
    public void setLogo(String logo){
        this.logo=logo;
    }
    public String getImages(){
        return images;
    }
    public void setImages(String images){
        this.images=images;
    }
    public BigDecimal getPrice(){
        return price;
    }
    public void setPrice(BigDecimal price){
        this.price=price;
    }
    public BigDecimal getLinePrice(){
        return linePrice;
    }
    public void setLinePrice(BigDecimal linePrice){
        this.linePrice=linePrice;
    }
    public Integer getServiceTime(){
        return serviceTime;
    }
    public void setServiceTime(Integer serviceTime){
        this.serviceTime=serviceTime;
    }
    public String getCouponIds(){
        return couponIds;
    }
    public void setCouponIds(String couponIds){
        this.couponIds=couponIds;
    }
    public Integer getStock(){
        return stock;
    }
    public void setStock(Integer stock){
        this.stock=stock;
    }
    public BigDecimal getWeight(){
        return weight;
    }
    public void setWeight(BigDecimal weight){
        this.weight=weight;
    }
    public Integer getInitSale(){
        return initSale;
    }
    public void setInitSale(Integer initSale){
        this.initSale=initSale;
    }
    public String getSalePoint(){
        return salePoint;
    }
    public void setSalePoint(String salePoint){
        this.salePoint=salePoint;
    }
    public String getCanUsePoint(){
        return canUsePoint;
    }
    public void setCanUsePoint(String canUsePoint){
        this.canUsePoint=canUsePoint;
    }
    public String getIsMemberDiscount(){
        return isMemberDiscount;
    }
    public void setIsMemberDiscount(String isMemberDiscount){
        this.isMemberDiscount=isMemberDiscount;
    }
    public Integer getSort(){
        return sort;
    }
    public void setSort(Integer sort){
        this.sort=sort;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
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
    public List<MtGoodsSku> getSkuList(){
        return skuList;
    }
    public void setSkuList(List<MtGoodsSku> skuList){
        this.skuList=skuList;
    }
    public List<MtGoodsSpec> getSpecList(){
        return specList;
    }
    public void setSpecList(List<MtGoodsSpec> specList){
        this.specList=specList;
    }
}

