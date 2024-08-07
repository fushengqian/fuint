package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.service.BookItemService;
import com.fuint.common.service.StoreService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtBookItemMapper;
import com.fuint.common.service.SettingService;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.model.MtBookItem;
import com.fuint.repository.model.MtStore;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    /**
     * 分页查询预约订单列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtBookItem> queryBookItemListByPagination(PaginationRequest paginationRequest) {
        Page<MtBookItem> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtBookItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBookItem::getStatus, StatusEnum.DISABLE.getKey());

        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.like(MtBookItem::getMobile, mobile);
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

        lambdaQueryWrapper.orderByDesc(MtBookItem::getId);
        List<MtBookItem> dataList = mtBookItemMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtBookItem> paginationResponse = new PaginationResponse(pageImpl, MtBookItem.class);
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
    public MtBookItem addBookItem(MtBookItem mtBookItem) throws BusinessCheckException {
        MtBookItem bookItem = new MtBookItem();
        Integer storeId = mtBookItem.getStoreId() == null ? 0 : mtBookItem.getStoreId();
        if (mtBookItem.getMerchantId() == null || mtBookItem.getMerchantId() <= 0) {
            throw new BusinessCheckException("新增预约订单失败：所属商户不能为空！");
        }
        if (mtBookItem.getMerchantId() == null || mtBookItem.getMerchantId() <= 0) {
            MtStore mtStore = storeService.queryStoreById(storeId);
            if (mtStore != null) {
                bookItem.setMerchantId(mtStore.getMerchantId());
            }
        }
        bookItem.setStoreId(storeId);
        bookItem.setStatus(StatusEnum.ENABLED.getKey());
        bookItem.setUpdateTime(new Date());
        bookItem.setCreateTime(new Date());
        Integer id = mtBookItemMapper.insert(bookItem);
        if (id > 0) {
            return bookItem;
        } else {
            logger.error("新增预约订单失败.");
            throw new BusinessCheckException("抱歉，新增预约订单失败！");
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

        LambdaQueryWrapper<MtBookItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtBookItem::getMobile, mobile);
        }
        if (StringUtils.isNotBlank(contact)) {
            lambdaQueryWrapper.like(MtBookItem::getContact, contact);
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
}
