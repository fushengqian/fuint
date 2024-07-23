package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtBookCate;
import java.util.List;
import java.util.Map;

/**
 * 预约类别业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface BookCateService extends IService<MtBookCate> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtBookCate> queryBookCateListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加预约类别
     *
     * @param  mtBookCate
     * @throws BusinessCheckException
     * @return
     */
    MtBookCate addBookCate(MtBookCate mtBookCate) throws BusinessCheckException;

    /**
     * 根据ID获取预约类别
     *
     * @param  id 预约分类ID
     * @throws BusinessCheckException
     * @return
     */
    MtBookCate getBookCateById(Integer id) throws BusinessCheckException;

    /**
     * 更新预约类别
     *
     * @param  mtBookCate
     * @throws BusinessCheckException
     * @return
     * */
    MtBookCate updateBookCate(MtBookCate mtBookCate) throws BusinessCheckException;

    /**
     * 根据条件搜索预约类别
     *
     * @param  params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    List<MtBookCate> queryBookCateListByParams(Map<String, Object> params) throws BusinessCheckException;

}
