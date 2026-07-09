package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.goods.StockCheckGoodsDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.StockCheckPage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtStockCheck;
import com.fuint.repository.model.MtStockCheckItem;

import java.util.List;
import java.util.Map;

/**
 * 库存盘点业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface StockCheckService extends IService<MtStockCheck> {

    /**
     * 分页查询盘点记录列表
     *
     * @param stockCheckPage 分页参数
     * @return
     */
    PaginationResponse<MtStockCheck> queryCheckListByPagination(StockCheckPage stockCheckPage);

    /**
     * 创建盘点任务
     *
     * @param mtStockCheck 盘点主信息
     * @param goodsList 盘点商品列表
     * @param accountInfo 操作人
     * @throws BusinessCheckException
     * @return
     */
    MtStockCheck createCheck(MtStockCheck mtStockCheck, List<StockCheckGoodsDto> goodsList, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 提交盘点结果
     *
     * @param checkId 盘点ID
     * @param goodsList 盘点商品列表（含实际库存）
     * @param accountInfo 操作人
     * @throws BusinessCheckException
     * @return
     */
    MtStockCheck submitCheck(Integer checkId, List<StockCheckGoodsDto> goodsList, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 根据ID获取盘点记录
     *
     * @param id ID
     * @return
     */
    MtStockCheck queryCheckById(Integer id);

    /**
     * 根据条件查询盘点明细
     *
     * @param params 查询条件
     * @return
     */
    List<MtStockCheckItem> queryCheckItemsByParams(Map<String, Object> params);

    /**
     * 删除盘点记录
     *
     * @param id ID
     * @param accountInfo 操作人
     * @throws BusinessCheckException
     */
    void deleteCheck(Integer id, AccountInfo accountInfo) throws BusinessCheckException;
}
