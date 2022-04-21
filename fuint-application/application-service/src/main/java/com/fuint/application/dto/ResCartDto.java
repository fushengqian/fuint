package com.fuint.application.dto;

import com.fuint.application.dao.entities.MtGoods;
import java.io.Serializable;
import java.util.List;

/**
 * 购物车返回DTO
 * Created by FSQ
 * Contact wx fsq_better
 */
public class ResCartDto implements Serializable {

    /**
     * ID
     * */
    private Integer id;

    /**
     * 分类名称
     */
    private Integer userId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * skuID
     */
    private Integer skuId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 商品规格
     * */
    private List<GoodsSpecValueDto> specList;

    /**
     * 商品数据
     * */
    private MtGoods goodsInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer name) {
        this.userId = name;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getSkuId(){
        return skuId;
    }
    public void setSkuId(Integer skuId){
        this.skuId=skuId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public MtGoods getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(MtGoods goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public List<GoodsSpecValueDto> getSpecList() {
        return specList;
    }

    public void setSpecList(List<GoodsSpecValueDto> specList) {
        this.specList = specList;
    }
}
