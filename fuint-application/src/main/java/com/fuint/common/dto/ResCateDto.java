package com.fuint.common.dto;

import com.fuint.repository.model.MtGoods;
import java.io.Serializable;
import java.util.List;

/**
 * 商品分类返回DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class ResCateDto implements Serializable {

    /**
     *  分类ID
     * */
    private Integer cateId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * logo
     */
    private String logo;

    /**
     * 数据列表
     * */
    private List<MtGoods> goodsList;

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<MtGoods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<MtGoods> goodsList) {
        this.goodsList = goodsList;
    }
}
