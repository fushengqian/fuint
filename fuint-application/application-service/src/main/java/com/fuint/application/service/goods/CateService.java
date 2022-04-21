package com.fuint.application.service.goods;

import com.fuint.application.dao.entities.MtGoodsCate;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * 商品分类业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface CateService {
    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtGoodsCate> queryCateListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加分类
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtGoodsCate addCate(MtGoodsCate reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取分类信息
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    MtGoodsCate queryCateById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID 删除
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteCate(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新分类
     * @param reqDto
     * @throws BusinessCheckException
     * */
    MtGoodsCate updateCate(MtGoodsCate reqDto) throws BusinessCheckException;

    /**
     * 根据条件搜索分类
     * */
    List<MtGoodsCate> queryCateListByParams(Map<String, Object> params) throws BusinessCheckException;
}
