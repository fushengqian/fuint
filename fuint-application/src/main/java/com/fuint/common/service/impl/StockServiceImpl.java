package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.StockGoodsDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.StockService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtGoodsMapper;
import com.fuint.repository.mapper.MtGoodsSkuMapper;
import com.fuint.repository.mapper.MtStockItemMapper;
import com.fuint.repository.mapper.MtStockMapper;
import com.fuint.repository.model.MtGoods;
import com.fuint.repository.model.MtGoodsSku;
import com.fuint.repository.model.MtStock;
import com.fuint.repository.model.MtStockItem;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class StockServiceImpl extends ServiceImpl<MtStockMapper, MtStock> implements StockService {

    private MtStockMapper mtStockMapper;

    private MtStockItemMapper mtStockItemMapper;

    private MtGoodsMapper mtGoodsMapper;

    private MtGoodsSkuMapper mtGoodsSkuMapper;

    /**
     * 分页查询库存管理记录列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtStock> queryStockListByPagination(PaginationRequest paginationRequest) {
        Page<MtStock> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtStock> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtStock::getStatus, StatusEnum.DISABLE.getKey());

        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtStock::getStatus, status);
        }
        String type = paginationRequest.getSearchParams().get("type") == null ? "" : paginationRequest.getSearchParams().get("type").toString();
        if (StringUtils.isNotBlank(type)) {
            lambdaQueryWrapper.eq(MtStock::getType, type);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtStock::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtStock::getStoreId, storeId);
        }
        String description = paginationRequest.getSearchParams().get("description") == null ? "" : paginationRequest.getSearchParams().get("description").toString();
        if (StringUtils.isNotBlank(description)) {
            lambdaQueryWrapper.like(MtStock::getDescription, description);
        }

        lambdaQueryWrapper.orderByDesc(MtStock::getId);
        List<MtStock> dataList = mtStockMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtStock> paginationResponse = new PaginationResponse(pageImpl, MtStock.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 新增库存管理记录 (操作库存)
     *
     * @param  stockParam 库存参数
     * @param  goodsList 商品列表
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "新增库存管理记录")
    public ResponseObject addStock(MtStock stockParam, List<StockGoodsDto> goodsList) throws BusinessCheckException {
        MtStock mtStock = new MtStock();
        mtStock.setMerchantId(stockParam.getMerchantId());
        mtStock.setStoreId(stockParam.getStoreId());
        mtStock.setStatus(StatusEnum.ENABLED.getKey());
        mtStock.setType(stockParam.getType());
        Date createTime = new Date();
        mtStock.setCreateTime(createTime);
        mtStock.setUpdateTime(createTime);
        mtStock.setDescription(stockParam.getDescription());
        mtStock.setOperator(stockParam.getOperator());
        this.save(mtStock);

        Integer stockId = mtStock.getId();
        for (StockGoodsDto goods : goodsList) {
             MtStockItem mtStockItem = new MtStockItem();
             mtStockItem.setStockId(stockId);
             Integer goodsId = goods.getId();
             Integer skuId = goods.getSkuId();
             Double num = goods.getNum();
             mtStockItem.setGoodsId(goodsId);
             if (goods.getSkuId() != null) {
                 mtStockItem.setSkuId(skuId);
             }
             mtStockItem.setStatus(StatusEnum.ENABLED.getKey());
             mtStockItem.setNum(num);
             mtStockItem.setCreateTime(createTime);
             mtStockItem.setUpdateTime(createTime);
             mtStockItemMapper.insert(mtStockItem);

            // 库存操作
            MtGoods goodsInfo = mtGoodsMapper.selectById(goodsId);
            if (goodsInfo.getIsSingleSpec().equals(YesOrNoEnum.YES.getKey())) {
                // 单规格库存
                Double stock;
                if (mtStock.getType().equals("increase")) {
                    stock = goodsInfo.getStock() + num;
                } else {
                    stock = goodsInfo.getStock() - num;
                }
                if (stock < 0) {
                    throw new BusinessCheckException("商品“" + goodsInfo.getName() + "”库存不足，提交失败");
                }
                goodsInfo.setStock(stock);
                mtGoodsMapper.updateById(goodsInfo);
            } else {
                // 多规格库存
                MtGoodsSku mtGoodsSku = mtGoodsSkuMapper.selectById(skuId);
                if (mtGoodsSku != null) {
                    Double stock;
                    if (mtStock.getType().equals("increase")) {
                        stock = mtGoodsSku.getStock() + num;
                    } else {
                        stock = mtGoodsSku.getStock() - num;
                    }
                    if (stock < 0) {
                        throw new BusinessCheckException("商品sku编码“" + mtGoodsSku.getSkuNo() +"”库存不足，提交失败");
                    }
                    mtGoodsSku.setStock(stock);
                    mtGoodsSkuMapper.updateById(mtGoodsSku);
                }
            }
        }
        return new ResponseObject(200, "", mtStock);
    }

    /**
     * 删除库存管理记录
     *
     * @param  id ID
     * @param  operator 操作人
     * @return
     */
    @Override
    @OperationServiceLog(description = "删除库存管理记录")
    public void delete(Integer id, String operator) {
        MtStock mtStock = mtStockMapper.selectById(id);
        if (mtStock == null) {
            return;
        }
        mtStock.setStatus(StatusEnum.DISABLE.getKey());
        mtStock.setUpdateTime(new Date());
        mtStock.setOperator(operator);
        this.updateById(mtStock);
    }

    /**
     * 根据ID获取库存管理记录
     *
     * @param  id ID
     * @return
     */
    @Override
    public MtStock queryStockById(Long id) {
        return mtStockMapper.selectById(id.intValue());
    }

    /**
     * 根据条件查询库存项
     *
     * @param params 查询条件
     * @return
     * */
    @Override
    public List<MtStockItem> queryItemByParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        return mtStockItemMapper.selectByMap(params);
    }

    /**
     * 生成出入库记录
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param goodsId 商品ID
     * @param skuId 商品SKU ID
     * @param type 类型，increase:入库，reduce:出库
     * @param num 数量
     * @param description 说明
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addStockRecord(Integer merchantId, Integer storeId, Integer goodsId, Integer skuId, String type, Double num, String description) {
        MtStock mtStock = new MtStock();
        mtStock.setMerchantId(merchantId);
        mtStock.setStoreId(storeId);
        mtStock.setStatus(StatusEnum.ENABLED.getKey());
        mtStock.setType(type);
        Date createTime = new Date();
        mtStock.setCreateTime(createTime);
        mtStock.setUpdateTime(createTime);
        mtStock.setDescription(description);
        mtStockMapper.insert(mtStock);
        // 生成库存明细
        MtStockItem mtStockItem = new MtStockItem();
        mtStockItem.setStockId(mtStock.getId());
        mtStockItem.setGoodsId(goodsId);
        if (skuId != null) {
            mtStockItem.setSkuId(skuId);
        }
        mtStockItem.setStatus(StatusEnum.ENABLED.getKey());
        mtStockItem.setNum(num);
        mtStockItem.setDescription(description);
        mtStockItem.setCreateTime(createTime);
        mtStockItem.setUpdateTime(createTime);
        mtStockItemMapper.insert(mtStockItem);
        return true;
    }
}
