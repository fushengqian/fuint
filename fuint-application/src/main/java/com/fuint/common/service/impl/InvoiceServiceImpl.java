package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.param.InvoiceParam;
import com.fuint.common.service.OrderService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtInvoice;
import com.fuint.common.service.InvoiceService;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.mapper.MtInvoiceMapper;
import com.fuint.repository.model.MtOrder;
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

import java.math.BigDecimal;
import java.util.*;

/**
 * 发票服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class InvoiceServiceImpl extends ServiceImpl<MtInvoiceMapper, MtInvoice> implements InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private MtInvoiceMapper mtInvoiceMapper;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 分页查询数据列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtInvoice> queryInvoiceListByPagination(PaginationRequest paginationRequest) {
        Page<MtInvoice> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtInvoice> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtInvoice::getStatus, StatusEnum.DISABLE.getKey());

        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.like(MtInvoice::getMobile, mobile);
        }
        String orderSn = paginationRequest.getSearchParams().get("orderSn") == null ? "" : paginationRequest.getSearchParams().get("orderSn").toString();
        if (StringUtils.isNotBlank(orderSn)) {
            lambdaQueryWrapper.like(MtInvoice::getOrderSn, orderSn);
        }
        String title = paginationRequest.getSearchParams().get("title") == null ? "" : paginationRequest.getSearchParams().get("title").toString();
        if (StringUtils.isNotBlank(title)) {
            lambdaQueryWrapper.like(MtInvoice::getTitle, title);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtInvoice::getStatus, status);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtInvoice::getMerchantId, merchantId);
        }

        lambdaQueryWrapper.orderByAsc(MtInvoice::getId);
        List<MtInvoice> dataList = mtInvoiceMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtInvoice> paginationResponse = new PaginationResponse(pageImpl, MtInvoice.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加发票
     *
     * @param invoice 发票信息
     * @return
     */
    @Override
    @OperationServiceLog(description = "新增发票")
    public MtInvoice addInvoice(InvoiceParam invoice) throws BusinessCheckException {
        MtInvoice mtInvoice = new MtInvoice();
        if ((invoice.getOrderId() == null || invoice.getOrderId() <= 0) && StringUtil.isBlank(invoice.getOrderSn())) {
            throw new BusinessCheckException("新增发票数据失败，订单参数不能为空");
        }
        MtOrder order;
        if (invoice.getOrderId() != null) {
            order = orderService.getOrderInfo(invoice.getOrderId());
        } else {
            order = orderService.getOrderInfoByOrderSn(invoice.getOrderSn());
        }
        if (order == null) {
            throw new BusinessCheckException("新增发票数据失败，订单信息不存在");
        }

        BeanUtils.copyProperties(invoice, mtInvoice);
        Date nowTime = new Date();
        mtInvoice.setStatus(StatusEnum.ENABLED.getKey());
        mtInvoice.setUpdateTime(nowTime);
        mtInvoice.setCreateTime(nowTime);
        mtInvoice.setMerchantId(order.getMerchantId());
        mtInvoice.setStoreId(order.getStoreId());
        Integer id = mtInvoiceMapper.insert(mtInvoice);
        if (id > 0) {
            return mtInvoice;
        } else {
            logger.error("新增发票数据失败.");
            throw new BusinessCheckException("新增发票数据失败");
        }
    }

    /**
     * 根据ID获取发票取息
     *
     * @param id 发票ID
     * @return
     */
    @Override
    public MtInvoice queryInvoiceById(Integer id) {
        return mtInvoiceMapper.selectById(id);
    }

    /**
     * 根据ID删除发票
     *
     * @param id 发票ID
     * @param operator 操作人
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "删除发票")
    public void deleteInvoice(Integer id, String operator) {
        MtInvoice mtInvoice = queryInvoiceById(id);
        if (null == mtInvoice) {
            return;
        }
        mtInvoice.setStatus(StatusEnum.DISABLE.getKey());
        mtInvoice.setUpdateTime(new Date());
        mtInvoice.setOperator(operator);
        mtInvoiceMapper.updateById(mtInvoice);
        logger.info("删除发票信息");
    }

    /**
     * 修改发票数据
     *
     * @param invoice
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新发票")
    public MtInvoice updateInvoice(InvoiceParam invoice) throws BusinessCheckException {
        MtInvoice mtInvoice = queryInvoiceById(invoice.getId());
        if (mtInvoice == null) {
            throw new BusinessCheckException("该发票状态异常");
        }
        BeanUtils.copyProperties(invoice, mtInvoice);
        mtInvoice.setUpdateTime(new Date());
        mtInvoiceMapper.updateById(mtInvoice);
        return mtInvoice;
    }

   /**
    * 根据条件搜索发票
    *
    * @param  params 查询参数
    * @throws BusinessCheckException
    * @return
    * */
    @Override
    public List<MtInvoice> queryInvoiceListByParams(Map<String, Object> params) {
        String orderSn =  params.get("orderSn") == null ? "" : params.get("orderSn").toString();
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey() : params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String merchantId =  params.get("merchantId") == null ? "" : params.get("merchantId").toString();

        LambdaQueryWrapper<MtInvoice> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtInvoice::getStatus, status);
        }
        if (StringUtils.isNotBlank(orderSn)) {
            lambdaQueryWrapper.eq(MtInvoice::getOrderSn, orderSn);
        }
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtInvoice::getMerchantId, merchantId);
        }
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtInvoice::getStoreId, 0)
                    .or()
                    .eq(MtInvoice::getStoreId, storeId));
        }

        lambdaQueryWrapper.orderByAsc(MtInvoice::getId);
        List<MtInvoice> dataList = mtInvoiceMapper.selectList(lambdaQueryWrapper);
        return dataList;
    }

    /**
     * 获取开票金额
     *
     * @param  merchantId 商户ID
     * @param  storeId 店铺ID
     * @param  beginTime 开始时间
     * @param  endTime 结束时间
     * @return
     */
    @Override
    public BigDecimal getInvoiceTotalAmount(Integer merchantId, Integer storeId,  Date beginTime, Date endTime) {
        return mtInvoiceMapper.getInvoiceTotalAmount(merchantId, storeId, beginTime, endTime);
    }
}
