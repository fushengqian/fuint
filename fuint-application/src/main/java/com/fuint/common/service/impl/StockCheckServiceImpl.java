package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.goods.StockCheckGoodsDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.param.StockCheckPage;
import com.fuint.common.service.StockCheckService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.*;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存盘点业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_ = {@Lazy})
public class StockCheckServiceImpl extends ServiceImpl<MtStockCheckMapper, MtStockCheck> implements StockCheckService {

    private MtStockCheckMapper mtStockCheckMapper;

    private MtStockCheckItemMapper mtStockCheckItemMapper;

    private MtStockMapper mtStockMapper;

    private MtStockItemMapper mtStockItemMapper;

    private MtGoodsMapper mtGoodsMapper;

    private MtGoodsSkuMapper mtGoodsSkuMapper;

    /**
     * 分页查询盘点记录列表
     *
     * @param stockCheckPage 分页参数
     * @return
     */
    @Override
    public PaginationResponse<MtStockCheck> queryCheckListByPagination(StockCheckPage stockCheckPage) {
        Page<MtStockCheck> pageHelper = PageHelper.startPage(stockCheckPage.getPage(), stockCheckPage.getPageSize());
        LambdaQueryWrapper<MtStockCheck> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtStockCheck::getStatus, StatusEnum.DISABLE.getKey());

        String status = stockCheckPage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtStockCheck::getStatus, status);
        }
        String checkNo = stockCheckPage.getCheckNo();
        if (StringUtils.isNotBlank(checkNo)) {
            lambdaQueryWrapper.like(MtStockCheck::getCheckNo, checkNo);
        }
        Integer merchantId = stockCheckPage.getMerchantId();
        if (merchantId != null) {
            lambdaQueryWrapper.eq(MtStockCheck::getMerchantId, merchantId);
        }
        Integer storeId = stockCheckPage.getStoreId();
        if (storeId != null) {
            lambdaQueryWrapper.eq(MtStockCheck::getStoreId, storeId);
        }
        String description = stockCheckPage.getDescription();
        if (StringUtils.isNotBlank(description)) {
            lambdaQueryWrapper.like(MtStockCheck::getDescription, description);
        }

        lambdaQueryWrapper.orderByDesc(MtStockCheck::getId);
        List<MtStockCheck> dataList = mtStockCheckMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(stockCheckPage.getPage(), stockCheckPage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtStockCheck> paginationResponse = new PaginationResponse(pageImpl, MtStockCheck.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 创建盘点任务
     *
     * @param mtStockCheck 盘点主信息
     * @param goodsList 盘点商品列表
     * @param accountInfo 操作人
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "创建库存盘点任务")
    public MtStockCheck createCheck(MtStockCheck mtStockCheck, List<StockCheckGoodsDto> goodsList, AccountInfo accountInfo) throws BusinessCheckException {
        if (goodsList == null || goodsList.isEmpty()) {
            throw new BusinessCheckException("盘点商品列表不能为空");
        }

        // 生成盘点单号：PD + 时间戳 + 随机数
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String checkNo = "PD" + sdf.format(new Date()) + String.format("%04d", (int) (Math.random() * 10000));

        MtStockCheck stockCheck = new MtStockCheck();
        stockCheck.setMerchantId(accountInfo.getMerchantId());
        stockCheck.setStoreId(mtStockCheck.getStoreId());
        stockCheck.setCheckNo(checkNo);
        stockCheck.setCheckTime(mtStockCheck.getCheckTime() != null ? mtStockCheck.getCheckTime() : new Date());
        stockCheck.setDescription(mtStockCheck.getDescription());
        stockCheck.setStatus("A");
        stockCheck.setOperator(accountInfo.getAccountName());
        Date createTime = new Date();
        stockCheck.setCreateTime(createTime);
        stockCheck.setUpdateTime(createTime);
        this.save(stockCheck);

        // 创建盘点明细，自动获取系统库存
        Integer checkId = stockCheck.getId();
        for (StockCheckGoodsDto goods : goodsList) {
            MtStockCheckItem item = new MtStockCheckItem();
            item.setCheckId(checkId);
            Integer goodsId = goods.getId() != null ? goods.getId() : goods.getGoodsId();
            Integer skuId = goods.getSkuId();
            item.setGoodsId(goodsId);
            if (skuId != null && skuId > 0) {
                item.setSkuId(skuId);
            }

            // 自动获取系统库存
            Double systemStock = 0.0;
            MtGoods goodsInfo = mtGoodsMapper.selectById(goodsId);
            if (goodsInfo != null) {
                if (goodsInfo.getIsSingleSpec().equals(YesOrNoEnum.YES.getKey())) {
                    systemStock = goodsInfo.getStock() != null ? goodsInfo.getStock() : 0.0;
                } else if (skuId != null && skuId > 0) {
                    MtGoodsSku goodsSku = mtGoodsSkuMapper.selectById(skuId);
                    if (goodsSku != null) {
                        systemStock = goodsSku.getStock() != null ? goodsSku.getStock() : 0.0;
                    }
                }
            }

            item.setSystemStock(systemStock);
            item.setActualStock(systemStock); // 初始默认实际库存等于系统库存
            item.setDiffStock(0.0);
            item.setDescription(goods.getDescription());
            item.setStatus(StatusEnum.ENABLED.getKey());
            item.setCreateTime(createTime);
            item.setUpdateTime(createTime);
            mtStockCheckItemMapper.insert(item);
        }

        return stockCheck;
    }

    /**
     * 提交盘点结果
     *
     * @param checkId 盘点ID
     * @param goodsList 盘点商品列表（含实际库存）
     * @param accountInfo 操作人
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "提交库存盘点结果")
    public MtStockCheck submitCheck(Integer checkId, List<StockCheckGoodsDto> goodsList, AccountInfo accountInfo) throws BusinessCheckException {
        MtStockCheck stockCheck = mtStockCheckMapper.selectById(checkId);
        if (stockCheck == null) {
            throw new BusinessCheckException("盘点记录不存在");
        }
        if (!"A".equals(stockCheck.getStatus())) {
            throw new BusinessCheckException("当前盘点状态不允许提交，仅盘点中状态可提交");
        }

        if (goodsList == null || goodsList.isEmpty()) {
            throw new BusinessCheckException("盘点商品列表不能为空");
        }

        // 查询原有盘点明细
        Map<String, Object> itemParams = new HashMap<>();
        itemParams.put("CHECK_ID", checkId);
        itemParams.put("STATUS", StatusEnum.ENABLED.getKey());
        List<MtStockCheckItem> existingItems = mtStockCheckItemMapper.selectByMap(itemParams);

        Date updateTime = new Date();

        for (StockCheckGoodsDto goods : goodsList) {
            Integer goodsId = goods.getId() != null ? goods.getId() : goods.getGoodsId();
            Integer skuId = goods.getSkuId();
            Double actualStock = goods.getActualStock() != null ? goods.getActualStock() : 0.0;

            // 查找对应的明细记录
            MtStockCheckItem targetItem = null;
            for (MtStockCheckItem existingItem : existingItems) {
                boolean match = existingItem.getGoodsId().equals(goodsId);
                if (skuId != null && skuId > 0) {
                    match = match && (existingItem.getSkuId() != null && existingItem.getSkuId().equals(skuId));
                } else {
                    match = match && (existingItem.getSkuId() == null || existingItem.getSkuId() == 0);
                }
                if (match) {
                    targetItem = existingItem;
                    break;
                }
            }

            if (targetItem == null) {
                continue;
            }

            Double systemStock = targetItem.getSystemStock() != null ? targetItem.getSystemStock() : 0.0;
            Double diffStock = actualStock - systemStock;

            // 更新盘点明细
            targetItem.setActualStock(actualStock);
            targetItem.setDiffStock(diffStock);
            targetItem.setDescription(goods.getDescription());
            targetItem.setUpdateTime(updateTime);
            mtStockCheckItemMapper.updateById(targetItem);

            // 库存有差异时，调整库存并生成出入库记录
            if (Math.abs(diffStock) > 0.001) {
                String adjustType = diffStock > 0 ? "increase" : "reduce";
                String adjustDesc = "库存盘点调整，盘点单号：" + stockCheck.getCheckNo();

                // 更新商品/SKU库存
                MtGoods goodsInfo = mtGoodsMapper.selectById(goodsId);
                if (goodsInfo != null) {
                    if (goodsInfo.getIsSingleSpec().equals(YesOrNoEnum.YES.getKey())) {
                        goodsInfo.setStock(actualStock);
                        mtGoodsMapper.updateById(goodsInfo);
                    } else if (skuId != null && skuId > 0) {
                        MtGoodsSku goodsSku = mtGoodsSkuMapper.selectById(skuId);
                        if (goodsSku != null) {
                            goodsSku.setStock(actualStock);
                            mtGoodsSkuMapper.updateById(goodsSku);
                        }
                    }
                }

                // 生成出入库调整记录
                MtStock adjustStock = new MtStock();
                adjustStock.setMerchantId(stockCheck.getMerchantId());
                adjustStock.setStoreId(stockCheck.getStoreId());
                adjustStock.setStatus(StatusEnum.ENABLED.getKey());
                adjustStock.setType(adjustType);
                adjustStock.setDescription(adjustDesc);
                adjustStock.setOperator(accountInfo.getAccountName());
                adjustStock.setCreateTime(updateTime);
                adjustStock.setUpdateTime(updateTime);
                mtStockMapper.insert(adjustStock);

                MtStockItem adjustItem = new MtStockItem();
                adjustItem.setStockId(adjustStock.getId());
                adjustItem.setGoodsId(goodsId);
                if (skuId != null && skuId > 0) {
                    adjustItem.setSkuId(skuId);
                }
                adjustItem.setNum(Math.abs(diffStock));
                adjustItem.setDescription(adjustDesc);
                adjustItem.setStatus(StatusEnum.ENABLED.getKey());
                adjustItem.setCreateTime(updateTime);
                adjustItem.setUpdateTime(updateTime);
                mtStockItemMapper.insert(adjustItem);
            }
        }

        // 更新盘点状态为已完成
        stockCheck.setStatus("B");
        stockCheck.setUpdateTime(updateTime);
        stockCheck.setOperator(accountInfo.getAccountName());
        this.updateById(stockCheck);

        return stockCheck;
    }

    /**
     * 根据ID获取盘点记录
     *
     * @param id ID
     * @return
     */
    @Override
    public MtStockCheck queryCheckById(Integer id) {
        return mtStockCheckMapper.selectById(id);
    }

    /**
     * 根据条件查询盘点明细
     *
     * @param params 查询条件
     * @return
     */
    @Override
    public List<MtStockCheckItem> queryCheckItemsByParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        return mtStockCheckItemMapper.selectByMap(params);
    }

    /**
     * 删除盘点记录
     *
     * @param id ID
     * @param accountInfo 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除库存盘点记录")
    public void deleteCheck(Integer id, AccountInfo accountInfo) throws BusinessCheckException {
        MtStockCheck stockCheck = mtStockCheckMapper.selectById(id);
        if (stockCheck == null) {
            throw new BusinessCheckException("盘点记录不存在");
        }
        if (accountInfo.getMerchantId() > 0 && !stockCheck.getMerchantId().equals(accountInfo.getMerchantId())) {
            throw new BusinessCheckException("不同商户，无权限操作");
        }

        stockCheck.setStatus(StatusEnum.DISABLE.getKey());
        stockCheck.setUpdateTime(new Date());
        stockCheck.setOperator(accountInfo.getAccountName());
        this.updateById(stockCheck);

        // 同时删除盘点明细
        Map<String, Object> itemParams = new HashMap<>();
        itemParams.put("CHECK_ID", id);
        List<MtStockCheckItem> items = mtStockCheckItemMapper.selectByMap(itemParams);
        if (items != null) {
            Date now = new Date();
            for (MtStockCheckItem item : items) {
                item.setStatus(StatusEnum.DISABLE.getKey());
                item.setUpdateTime(now);
                mtStockCheckItemMapper.updateById(item);
            }
        }
    }
}
