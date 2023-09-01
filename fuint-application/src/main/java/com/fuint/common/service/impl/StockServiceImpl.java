package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.*;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.*;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;

/**
 * 库存业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class StockServiceImpl extends ServiceImpl<MtStockMapper, MtStock> implements StockService {

    @Resource
    private MtStockMapper mtStockMapper;

    @Resource
    private MtStockItemMapper mtStockItemMapper;

    @Resource
    private MtGoodsMapper mtGoodsMapper;

    @Resource
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
     * 新增库存管理记录
     *
     * @param stockParam
     * @param goodsList
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject addStock(MtStock stockParam, List<LinkedHashMap> goodsList) throws BusinessCheckException {
        MtStock mtStock = new MtStock();

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
        for (LinkedHashMap goods : goodsList) {
             MtStockItem mtStockItem = new MtStockItem();
             mtStockItem.setStockId(stockId);
             Integer goodsId = Integer.parseInt(goods.get("id").toString());
             Integer skuId = null;
             Integer num = Integer.parseInt(goods.get("num").toString());
             mtStockItem.setGoodsId(goodsId);
             if (goods.get("skuId") != null && StringUtil.isNotEmpty(goods.get("skuId").toString())) {
                 skuId = Integer.parseInt(goods.get("skuId").toString());
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
                Integer stock;
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
                    Integer stock;
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
        ResponseObject result = new ResponseObject(200, "", mtStock);
        return result;
    }

    /**
     * 删除库存管理记录
     *
     * @param  id       ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除商品分类")
    public void delete(Integer id, String operator) throws BusinessCheckException {
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
     * @throws BusinessCheckException
     */
    @Override
    public MtStock queryStockById(Long id) {
        return mtStockMapper.selectById(id.intValue());
    }

    @Override
    public List<MtStockItem> queryItemByParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        List<MtStockItem> result = mtStockItemMapper.selectByMap(params);
        return result;
    }
}
