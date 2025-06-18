package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.BookItemDto;
import com.fuint.common.enums.BookStatusEnum;
import com.fuint.common.param.BookableParam;
import com.fuint.common.service.BookItemService;
import com.fuint.common.service.BookService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.SeqUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtBookItemMapper;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.mapper.MtBookMapper;
import com.fuint.repository.mapper.MtStoreMapper;
import com.fuint.repository.model.MtBook;
import com.fuint.repository.model.MtBookItem;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

/**
 * 预约订单服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class BookItemServiceImpl extends ServiceImpl<MtBookItemMapper, MtBookItem> implements BookItemService {

    private static final Logger logger = LoggerFactory.getLogger(BookItemServiceImpl.class);

    private MtBookItemMapper mtBookItemMapper;

    private MtBookMapper mtBookMapper;

    private MtStoreMapper mtStoreMapper;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    /**
     * 预约项目服务接口
     * */
    private BookService bookService;

    /**
     * 分页查询预约订单列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<BookItemDto> queryBookItemListByPagination(PaginationRequest paginationRequest) {
        Page<MtBookItem> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtBookItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBookItem::getStatus, StatusEnum.DISABLE.getKey());

        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.like(MtBookItem::getMobile, mobile);
        }
        String contact = paginationRequest.getSearchParams().get("contact") == null ? "" : paginationRequest.getSearchParams().get("contact").toString();
        if (StringUtils.isNotBlank(contact)) {
            lambdaQueryWrapper.like(MtBookItem::getContact, contact);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtBookItem::getStatus, status);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtBookItem::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtBookItem::getStoreId, storeId);
        }
        String userId = paginationRequest.getSearchParams().get("userId") == null ? "" : paginationRequest.getSearchParams().get("userId").toString();
        if (StringUtils.isNotBlank(userId)) {
            lambdaQueryWrapper.eq(MtBookItem::getUserId, userId);
        }
        String cateId = paginationRequest.getSearchParams().get("cateId") == null ? "" : paginationRequest.getSearchParams().get("cateId").toString();
        if (StringUtils.isNotBlank(cateId)) {
            lambdaQueryWrapper.eq(MtBookItem::getCateId, cateId);
        }

        lambdaQueryWrapper.orderByDesc(MtBookItem::getId);
        List<MtBookItem> bookItemList = mtBookItemMapper.selectList(lambdaQueryWrapper);
        List<BookItemDto> dataList = new ArrayList<>();
        if (bookItemList != null && bookItemList.size() > 0) {
            for (MtBookItem mtBookItem : bookItemList) {
                 BookItemDto bookItemDto = new BookItemDto();
                 BeanUtils.copyProperties(mtBookItem, bookItemDto);
                 MtBook mtBook = mtBookMapper.selectById(mtBookItem.getBookId());
                 if (mtBook != null) {
                     bookItemDto.setBookName(mtBook.getName());
                 }
                 dataList.add(bookItemDto);
            }
        }

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<BookItemDto> paginationResponse = new PaginationResponse(pageImpl, BookItemDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 新增预约订单
     *
     * @param mtBookItem 预约信息
     * @return
     */
    @Override
    @OperationServiceLog(description = "新增预约订单")
    public MtBookItem addBookItem(MtBookItem mtBookItem) throws BusinessCheckException, ParseException {
        Integer storeId = mtBookItem.getStoreId() == null ? 0 : mtBookItem.getStoreId();
        if (mtBookItem.getMerchantId() == null || mtBookItem.getMerchantId() <= 0) {
            throw new BusinessCheckException("新增预约订单失败：所属商户不能为空！");
        }
        if (mtBookItem.getMerchantId() == null || mtBookItem.getMerchantId() <= 0) {
            MtStore mtStore = storeService.queryStoreById(storeId);
            if (mtStore != null) {
                mtBookItem.setMerchantId(mtStore.getMerchantId());
            }
        }

        BookableParam param = new BookableParam();
        param.setBookId(mtBookItem.getBookId());
        param.setDate(mtBookItem.getServiceDate());
        param.setTime(mtBookItem.getServiceTime());
        List<String> bookable = bookService.isBookable(param);
        if (bookable.size() <= 0) {
            throw new BusinessCheckException("当前时间段不可预约，请重新选择！");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("merchantId", mtBookItem.getMerchantId());
        params.put("storeId", mtBookItem.getMerchantId());
        params.put("bookId", mtBookItem.getBookId());
        params.put("mobile", mtBookItem.getMobile());
        params.put("status", BookStatusEnum.CREATED.getKey());
        List<MtBookItem> data = queryBookItemListByParams(params);
        if (data != null && data.size() > 0) {
            throw new BusinessCheckException("您已提交预约，请等待确认！");
        }

        mtBookItem.setStatus(BookStatusEnum.CREATED.getKey());
        mtBookItem.setUpdateTime(new Date());
        mtBookItem.setCreateTime(new Date());
        mtBookItem.setVerifyCode(SeqUtil.getRandomNumber(6));
        Integer id = mtBookItemMapper.insert(mtBookItem);
        if (id > 0) {
            return mtBookItem;
        } else {
            logger.error("新增预约记录失败.");
            throw new BusinessCheckException("抱歉，新增预约记录失败！");
        }
    }

    /**
     * 根据ID获取预约订单信息
     *
     * @param id 预约订单ID
     * @return
     */
    @Override
    public MtBookItem getBookItemById(Integer id) {
        return mtBookItemMapper.selectById(id);
    }

    /**
     * 根据ID获取预约订单详情
     *
     * @param  id 预约订单ID
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public BookItemDto getBookDetail(Integer id) throws BusinessCheckException {
        MtBookItem mtBookItem = mtBookItemMapper.selectById(id);
        if (mtBookItem == null) {
            throw new BusinessCheckException("预约不存在.");
        }
        BookItemDto bookItemDto = new BookItemDto();
        BeanUtils.copyProperties(mtBookItem, bookItemDto);

        MtBook mtBook = mtBookMapper.selectById(mtBookItem.getBookId());
        if (mtBook != null) {
            bookItemDto.setBookName(mtBook.getName());
        }

        if (mtBookItem.getStoreId() != null) {
            MtStore mtStore = mtStoreMapper.selectById(mtBookItem.getStoreId());
            if (mtStore != null) {
                bookItemDto.setStoreInfo(mtStore);
            }
        }


        return bookItemDto;
    }

    /**
     * 修改预约订单
     *
     * @param  mtBookItem
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改预约订单")
    public MtBookItem updateBookItem(MtBookItem mtBookItem) throws BusinessCheckException {
        MtBookItem bookItem = getBookItemById(mtBookItem.getId());
        if (bookItem == null) {
            throw new BusinessCheckException("该预约订单信息异常");
        }

        bookItem.setId(mtBookItem.getId());
        if (mtBookItem.getBookId() != null) {
            bookItem.setBookId(mtBookItem.getBookId());
        }
        if (mtBookItem.getStoreId() != null) {
            bookItem.setStoreId(mtBookItem.getStoreId());
        }
        if (mtBookItem.getRemark() != null) {
            bookItem.setRemark(mtBookItem.getRemark());
        }
        if (mtBookItem.getOperator() != null) {
            bookItem.setOperator(mtBookItem.getOperator());
        }
        if (mtBookItem.getStatus() != null) {
            bookItem.setStatus(mtBookItem.getStatus());
        }
        if (mtBookItem.getMobile() != null) {
            bookItem.setMobile(mtBookItem.getMobile());
        }

        bookItem.setUpdateTime(new Date());
        mtBookItemMapper.updateById(bookItem);
        return bookItem;
    }

    /**
     * 根据条件搜索预约订单
     *
     * @param  params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    public List<MtBookItem> queryBookItemListByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String merchantId =  params.get("merchantId") == null ? "" : params.get("merchantId").toString();
        String mobile = params.get("mobile") == null ? "" : params.get("mobile").toString();
        String contact = params.get("contact") == null ? "" : params.get("contact").toString();
        String bookId = params.get("bookId") == null ? "" : params.get("bookId").toString();

        LambdaQueryWrapper<MtBookItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBookItem::getStatus, StatusEnum.DISABLE.getKey());
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtBookItem::getMobile, mobile);
        }
        if (StringUtils.isNotBlank(contact)) {
            lambdaQueryWrapper.like(MtBookItem::getContact, contact);
        }
        if (StringUtils.isNotBlank(bookId)) {
            lambdaQueryWrapper.like(MtBookItem::getBookId, bookId);
        }
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtBookItem::getStatus, status);
        }
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtBookItem::getMerchantId, merchantId);
        }
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtBookItem::getStoreId, storeId);
        }

        lambdaQueryWrapper.orderByDesc(MtBookItem::getId);
        List<MtBookItem> dataList = mtBookItemMapper.selectList(lambdaQueryWrapper);

        return dataList;
    }

    /**
     * 取消预约
     *
     * @param id 预约ID
     * @param remark 备注信息
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    @Transactional
    public Boolean cancelBook(Integer id, String remark) throws BusinessCheckException {
        MtBookItem mtBookItem = getBookItemById(id);
        if (mtBookItem == null) {
            throw new BusinessCheckException("该预约订单信息异常");
        }
        if (StringUtil.isNotEmpty(remark)) {
            mtBookItem.setRemark(mtBookItem.getRemark() == null ? remark : mtBookItem.getRemark() + remark);
        }
        mtBookItem.setStatus(BookStatusEnum.CANCEL.getKey());
        mtBookItemMapper.updateById(mtBookItem);
        return true;
    }
}
