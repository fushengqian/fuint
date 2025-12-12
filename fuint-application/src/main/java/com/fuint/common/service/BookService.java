package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.BookDto;
import com.fuint.common.param.BookPage;
import com.fuint.common.param.BookableParam;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtBook;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 预约业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface BookService extends IService<MtBook> {

    /**
     * 分页查询预约列表
     *
     * @param bookPage
     * @return
     */
    PaginationResponse<BookDto> queryBookListByPagination(BookPage bookPage) throws BusinessCheckException;

    /**
     * 添加预约
     *
     * @param  mtBook
     * @throws BusinessCheckException
     * @return
     */
    MtBook addBook(MtBook mtBook) throws BusinessCheckException;

    /**
     * 根据ID获取预约项目信息
     *
     * @param  id 预约项目ID
     * @param fillDate 填充日期
     * @throws BusinessCheckException
     * @return
     */
    BookDto getBookById(Integer id, boolean fillDate) throws BusinessCheckException, ParseException;

    /**
     * 更新预约项目
     *
     * @param  mtBook
     * @throws BusinessCheckException
     * @return
     * */
    MtBook updateBook(MtBook mtBook) throws BusinessCheckException;

    /**
     * 是否可预约
     *
     * @param  param
     * @throws BusinessCheckException
     * @return
     * */
    List<String> isBookable(BookableParam param) throws BusinessCheckException, ParseException;

    /**
     * 根据条件搜索预约项目
     *
     * @param  params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    List<MtBook> queryBookListByParams(Map<String, Object> params) throws BusinessCheckException;

}
