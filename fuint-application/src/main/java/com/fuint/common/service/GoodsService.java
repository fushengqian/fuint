package com.fuint.common.service;

import com.fuint.common.dto.GoodsDto;
import com.fuint.common.dto.GoodsSpecValueDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtGoods;
import com.fuint.repository.model.MtGoodsSku;
import com.fuint.repository.model.MtGoodsSpec;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 商品业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface GoodsService {

    /**
     * 分页查询商品列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<GoodsDto> queryGoodsListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 保存商品
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtGoods saveGoods(MtGoods reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取商品信息
     *
     * @param  id 商品ID
     * @throws BusinessCheckException
     */
    MtGoods queryGoodsById(Integer id) throws BusinessCheckException;

    /**
     * 根据编码获取商品信息
     *
     * @param goodsNo
     * @throws BusinessCheckException
     */
    MtGoods queryGoodsByGoodsNo(String goodsNo) throws BusinessCheckException;

    /**
     * 根据条码获取sku信息
     *
     * @param skuNo skuNo
     * @throws BusinessCheckException
     * */
    MtGoodsSku getSkuInfoBySkuNo(String skuNo) throws BusinessCheckException;

    /**
     * 根据ID获取商品详情
     *
     * @param id
     * @throws BusinessCheckException
     */
    GoodsDto getGoodsDetail(Integer id, boolean getDeleteSpec) throws InvocationTargetException, IllegalAccessException;

    /**
     * 根据ID删除
     *
     * @param  id       ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     */
    void deleteGoods(Integer id, String operator) throws BusinessCheckException;

    /**
     * 获取店铺的商品列表
     * @param storeId
     * @param keyword
     * @return
     * */
    List<MtGoods> getStoreGoodsList(Integer storeId, String keyword) throws BusinessCheckException;

    /**
     * 根据skuId获取规格列表
     * */
    List<GoodsSpecValueDto> getSpecListBySkuId(Integer skuId) throws BusinessCheckException;

    /**
     * 获取规格详情
     * */
    MtGoodsSpec getSpecDetail(Integer specId);

    /**
     * 更新已售数量
     * */
    Boolean updateInitSale(Integer goodsId);
}
