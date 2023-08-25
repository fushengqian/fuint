package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtStock;
import com.fuint.repository.model.MtStockItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface StockService extends IService<MtStock> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtStock> queryStockListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 新增库存管理记录
     *
     * @param mtStock
     * @param goodsList
     * @throws BusinessCheckException
     */
    ResponseObject addStock(MtStock mtStock, List<LinkedHashMap> goodsList) throws BusinessCheckException;

    /**
     * 删除库存管理记录
     *
     * @param id
     * @param operator
     * @return
     * */
    void delete(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据ID获取信息
     *
     * @param  id ID
     * @throws BusinessCheckException
     */
    MtStock queryStockById(Long id) throws BusinessCheckException;

    /**
     * 根据条件搜索详情
     *
     * @param  params
     * @throws BusinessCheckException
     * */
    List<MtStockItem> queryItemByParams(Map<String, Object> params) throws BusinessCheckException;
}
