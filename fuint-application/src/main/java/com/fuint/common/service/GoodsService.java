package com.fuint.common.service;

import com.fuint.common.dto.GoodsDto;
import com.fuint.common.dto.GoodsSpecValueDto;
import com.fuint.common.dto.GoodsTopDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtGoods;
import com.fuint.repository.model.MtGoodsSku;
import com.fuint.repository.model.MtGoodsSpec;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
     * @param  paginationRequest
     * @return
     */
    PaginationResponse<GoodsDto> queryGoodsListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 保存商品
     *
     * @param  reqDto 商品信息
     * @param  storeIds 分配店铺
     * @throws BusinessCheckException
     * @return
     */
    MtGoods saveGoods(MtGoods reqDto, String storeIds) throws BusinessCheckException;

    /**
     * 根据ID获取商品信息
     *
     * @param  id 商品ID
     * @throws BusinessCheckException
     * @return
     */
    MtGoods queryGoodsById(Integer id) throws BusinessCheckException;

    /**
     * 根据编码获取商品信息
     *
     * @param  merchantId 商户ID
     * @param  goodsNo 商品编码
     * @throws BusinessCheckException
     * @return
     */
    MtGoods queryGoodsByGoodsNo(Integer merchantId, String goodsNo) throws BusinessCheckException;

    /**
     * 根据条码获取sku信息
     *
     * @param  skuNo skuNo
     * @throws BusinessCheckException
     * @return
     * */
    MtGoodsSku getSkuInfoBySkuNo(String skuNo) throws BusinessCheckException;

    /**
     * 根据ID获取商品详情
     *
     * @param  id 商品ID
     * @throws BusinessCheckException
     * @return
     */
    GoodsDto getGoodsDetail(Integer id, boolean getDeleteSpec) throws InvocationTargetException, IllegalAccessException;

    /**
     * 根据ID删除
     *
     * @param  id 商品ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteGoods(Integer id, String operator) throws BusinessCheckException;

    /**
     * 获取店铺的商品列表
     *
     * @param storeId 店铺ID
     * @param keyword 关键字
     * @param cateId 分类ID
     * @param page 当前页码
     * @param pageSize 每页数量
     * @return
     * */
    Map<String, Object> getStoreGoodsList(Integer storeId, String keyword, Integer cateId, Integer page, Integer pageSize) throws BusinessCheckException;

    /**
     * 根据skuId获取规格列表
     *
     * @param skuId
     * @return
     * */
    List<GoodsSpecValueDto> getSpecListBySkuId(Integer skuId) throws BusinessCheckException;

    /**
     * 获取规格详情
     *
     * @param specId 规格ID
     * @return
     * */
    MtGoodsSpec getSpecDetail(Integer specId);

    /**
     * 更新已售数量
     *
     * @param goodsId 商品ID
     * @param saleNum 销售数量
     * @return
     * */
    Boolean updateInitSale(Integer goodsId, Integer saleNum);

    /**
     * 获取选择商品列表
     *
     * @param params 查询参数
     * @return
     */
    PaginationResponse<GoodsDto> selectGoodsList(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 获取商品销售排行榜
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * */
    List<GoodsTopDto> getGoodsSaleTopList(Integer merchantId, Integer storeId, Date startTime, Date endTime);

    /**
     * 获取商品分配的店铺
     *
     * @param goodsId 商品ID
     * @return
     * */
    String getStoreIds(Integer goodsId);

}
