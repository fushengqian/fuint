package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtInvoice;
import com.fuint.common.service.InvoiceService;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.mapper.MtInvoiceMapper;
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
     * @param mtInvoice 发票信息
     * @return
     */
    @Override
    @OperationServiceLog(description = "新增发票")
    public MtInvoice addInvoice(MtInvoice mtInvoice) throws BusinessCheckException {
        mtInvoice.setStatus(StatusEnum.ENABLED.getKey());
        mtInvoice.setUpdateTime(new Date());
        mtInvoice.setCreateTime(new Date());
        Integer id = mtInvoiceMapper.insert(mtInvoice);
        if (id > 0) {
            return mtInvoice;
        } else {
            throw new BusinessCheckException("新增发票数据失败");
        }
    }

    /**
     * 根据ID获发票取息
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
        mtInvoiceMapper.updateById(mtInvoice);
    }

    /**
     * 修改发票数据
     *
     * @param mtInvoice
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新发票")
    public MtInvoice updateInvoice(MtInvoice mtInvoice) throws BusinessCheckException {
        mtInvoice = queryInvoiceById(mtInvoice.getId());
        if (mtInvoice == null) {
            throw new BusinessCheckException("该发票状态异常");
        }
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
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String merchantId =  params.get("merchantId") == null ? "" : params.get("merchantId").toString();

        LambdaQueryWrapper<MtInvoice> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtInvoice::getStatus, status);
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
}
