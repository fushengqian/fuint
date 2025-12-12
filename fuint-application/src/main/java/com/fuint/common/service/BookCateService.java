package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.param.BookCatePage;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtBookCate;
import java.util.List;

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
     * @param bookCatePage
     * @return
     */
    PaginationResponse<MtBookCate> queryBookCateListByPagination(BookCatePage bookCatePage) throws BusinessCheckException;

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
     * 获取可用的预约类别
     *
     * @param  merchantId 商户ID
     * @param  storeId 店铺ID
     * @throws BusinessCheckException
     * @return
     * */
    List<MtBookCate> getAvailableBookCate(Integer merchantId, Integer storeId) throws BusinessCheckException;

}
