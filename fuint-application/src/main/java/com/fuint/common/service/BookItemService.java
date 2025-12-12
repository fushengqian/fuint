package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.BookItemDto;
import com.fuint.common.param.BookItemPage;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtBookItem;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 预约订单业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface BookItemService extends IService<MtBookItem> {

    /**
     * 分页查询列表
     *
     * @param bookItemPage
     * @return
     */
    PaginationResponse<BookItemDto> queryBookItemListByPagination(BookItemPage bookItemPage) throws BusinessCheckException;

    /**
     * 添加预约订单
     *
     * @param  mtBookItem
     * @throws BusinessCheckException
     * @return
     */
    MtBookItem addBookItem(MtBookItem mtBookItem) throws BusinessCheckException, ParseException;

    /**
     * 根据ID获取预约订单信息
     *
     * @param  id 预约订单ID
     * @throws BusinessCheckException
     * @return
     */
    MtBookItem getBookItemById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID获取预约订单详情
     *
     * @param  id 预约订单ID
     * @throws BusinessCheckException
     * @return
     */
    BookItemDto getBookDetail(Integer id) throws BusinessCheckException;

    /**
     * 更新预约订单
     *
     * @param  mtBookItem
     * @throws BusinessCheckException
     * @return
     * */
    MtBookItem updateBookItem(MtBookItem mtBookItem) throws BusinessCheckException;

    /**
     * 根据条件搜索预约订单
     *
     * @param  params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    List<MtBookItem> queryBookItemListByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 取消预约
     *
     * @param id 预约订单ID
     * @param remark 备注信息
     * @throws BusinessCheckException
     * @return
     * */
    Boolean cancelBook(Integer id, String remark) throws BusinessCheckException;
}
