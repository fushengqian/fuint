package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.BookItemDto;
import com.fuint.common.enums.BookStatusEnum;
import com.fuint.common.param.BookItemPage;
import com.fuint.common.param.BookableParam;
import com.fuint.common.service.BookItemService;
import com.fuint.common.service.BookService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.SeqUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.*;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
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
@AllArgsConstructor(onConstructor_= {@Lazy})
public class BookItemServiceImpl extends ServiceImpl<MtBookItemMapper, MtBookItem> implements BookItemService {

    private static final Logger logger = LoggerFactory.getLogger(BookItemServiceImpl.class);

    private MtBookItemMapper mtBookItemMapper;

    private MtBookMapper mtBookMapper;

    private MtStoreMapper mtStoreMapper;

    private MtOrderGoodsMapper mtOrderGoodsMapper;

    private MtGoodsMapper mtGoodsMapper;

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
     * @param bookItemPage
     * @return
     */
    @Override
    public PaginationResponse<BookItemDto> queryBookItemListByPagination(BookItemPage bookItemPage) {
        Page<MtBookItem> pageHelper = PageHelper.startPage(bookItemPage.getPage(), bookItemPage.getPageSize());
        LambdaQueryWrapper<MtBookItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBookItem::getStatus, StatusEnum.DISABLE.getKey());

        String mobile = bookItemPage.getMobile();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.like(MtBookItem::getMobile, mobile);
        }
        String contact = bookItemPage.getContact();
        if (StringUtils.isNotBlank(contact)) {
            lambdaQueryWrapper.like(MtBookItem::getContact, contact);
        }
        String status = bookItemPage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtBookItem::getStatus, status);
        }
        if (bookItemPage.getMerchantId() != null) {
            lambdaQueryWrapper.eq(MtBookItem::getMerchantId, bookItemPage.getMerchantId());
        }
        if (bookItemPage.getStoreId() != null) {
            lambdaQueryWrapper.eq(MtBookItem::getStoreId, bookItemPage.getStoreId());
        }
        if (bookItemPage.getUserId() != null) {
            lambdaQueryWrapper.eq(MtBookItem::getUserId, bookItemPage.getUserId());
        }
        if (bookItemPage.getCateId() != null) {
            lambdaQueryWrapper.eq(MtBookItem::getCateId, bookItemPage.getCateId());
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
                if (mtBookItem.getGoodsId() != null && mtBookItem.getGoodsId() > 0) {
                    MtOrderGoods mtOrderGoods = mtOrderGoodsMapper.selectById(mtBookItem.getGoodsId());
                    if (mtOrderGoods != null) {
                        MtGoods mtGoods = mtGoodsMapper.selectById(mtOrderGoods.getGoodsId());
                        if (mtGoods != null) {
                            bookItemDto.setGoodsName(mtGoods.getName());
                        }
                    }
                }
                 bookItemDto.setStatusName(BookStatusEnum.getValue(bookItemDto.getStatus()));
                 dataList.add(bookItemDto);
            }
        }

        PageRequest pageRequest = PageRequest.of(bookItemPage.getPage(), bookItemPage.getPageSize());
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
        if (mtBookItem.getGoodsId() != null && mtBookItem.getGoodsId() > 0) {
            params.put("goodsId", mtBookItem.getGoodsId());
        }
        params.put("status", BookStatusEnum.CREATED.getKey());
        List<MtBookItem> data = queryBookItemListByParams(params);
        if (data != null && data.size() > 0) {
            throw new BusinessCheckException("您已提交预约，请等待确认！");
        }

        mtBookItem.setStatus(BookStatusEnum.CREATED.getKey());
        mtBookItem.setUpdateTime(new Date());
        mtBookItem.setCreateTime(new Date());
        mtBookItem.setVerifyCode(SeqUtil.getRandomNumber(4));
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
     * 获取用户预约订单信息
     *
     * @param bookId 预约项目ID
     * @param userId 用户ID
     * @param orderGoodsId 订单商品ID
     * @return
     */
    public MtBookItem getUserBookItem(Integer bookId, Integer userId, Integer orderGoodsId) {
        Map<String, Object> params = new HashMap<>();
        params.put("bookId", bookId);
        params.put("userId", userId);
        params.put("goodsId", orderGoodsId);
        List<MtBookItem> bookItemList = queryBookItemListByParams(params);
        if (bookItemList != null && bookItemList.size() > 0) {
            return bookItemList.get(0);
        }
        return null;
    }

    /**
     * 根据ID获取预约订单详情
     *
     * @param  id 预约订单ID
     * @return
     */
    @Override
    public BookItemDto getBookDetail(Integer id) {
        MtBookItem mtBookItem = mtBookItemMapper.selectById(id);
        if (mtBookItem == null) {
            return null;
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
        bookItemDto.setStatusName(BookStatusEnum.getValue(bookItemDto.getStatus()));
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
     * @param params 查询参数
     * @return
     * */
    @Override
    public List<MtBookItem> queryBookItemListByParams(Map<String, Object> params) {
        String status = params.get("status") == null ? "" : params.get("status").toString();
        String storeId = params.get("storeId") == null ? "" : params.get("storeId").toString();
        String merchantId = params.get("merchantId") == null ? "" : params.get("merchantId").toString();
        String mobile = params.get("mobile") == null ? "" : params.get("mobile").toString();
        String contact = params.get("contact") == null ? "" : params.get("contact").toString();
        String bookId = params.get("bookId") == null ? "" : params.get("bookId").toString();
        String userId = params.get("userId") == null ? "" : params.get("userId").toString();
        String goodsId = params.get("goodsId") == null ? "" : params.get("goodsId").toString();

        LambdaQueryWrapper<MtBookItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBookItem::getStatus, BookStatusEnum.DELETE.getKey());
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtBookItem::getMobile, mobile);
        }
        if (StringUtils.isNotBlank(contact)) {
            lambdaQueryWrapper.like(MtBookItem::getContact, contact);
        }
        if (StringUtils.isNotBlank(bookId)) {
            lambdaQueryWrapper.like(MtBookItem::getBookId, bookId);
        }
        if (StringUtils.isNotBlank(userId)) {
            lambdaQueryWrapper.like(MtBookItem::getUserId, userId);
        }
        if (StringUtils.isNotBlank(goodsId)) {
            lambdaQueryWrapper.like(MtBookItem::getGoodsId, goodsId);
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
        return mtBookItemMapper.selectList(lambdaQueryWrapper);
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
