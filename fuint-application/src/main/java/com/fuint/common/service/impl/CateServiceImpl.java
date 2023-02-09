package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.GoodsCateDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.CateService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.StoreService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtGoodsCateMapper;
import com.fuint.repository.mapper.MtStoreMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 商品分类业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class CateServiceImpl extends ServiceImpl<MtGoodsCateMapper, MtGoodsCate> implements CateService {

    private static final Logger log = LoggerFactory.getLogger(CateServiceImpl.class);

    @Resource
    private MtGoodsCateMapper mtGoodsCateMapper;

    @Resource
    private MtStoreMapper mtStoreMapper;

    /**
     * 商户服务接口
     */
    @Autowired
    private MerchantService merchantService;

    /**
     * 店铺接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 分页查询分类列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<GoodsCateDto> queryCateListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        Page<MtBanner> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtGoodsCate> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtGoodsCate::getStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtGoodsCate::getName, name);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtGoodsCate::getStatus, status);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtGoodsCate::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq.eq(MtGoodsCate::getStoreId, 0).or().eq(MtGoodsCate::getStoreId, storeId));
        }

        lambdaQueryWrapper.orderByDesc(MtGoodsCate::getId);
        List<MtGoodsCate> cateList = mtGoodsCateMapper.selectList(lambdaQueryWrapper);
        List<GoodsCateDto> dataList = new ArrayList<>();
        for (MtGoodsCate mtGoodsCate : cateList) {
             GoodsCateDto goodsCateDto = new GoodsCateDto();
             BeanUtils.copyProperties(mtGoodsCate, goodsCateDto);
             if (goodsCateDto.getMerchantId() != null && goodsCateDto.getMerchantId() > 0) {
                 MtMerchant mtMerchant = merchantService.queryMerchantById(goodsCateDto.getMerchantId());
                 if (mtMerchant != null) {
                     goodsCateDto.setMerchantName(mtMerchant.getName());
                 }
             }
             if (goodsCateDto.getStoreId() != null && goodsCateDto.getStoreId() > 0) {
                 MtStore mtStore = storeService.queryStoreById(goodsCateDto.getStoreId());
                 if (mtStore != null) {
                     goodsCateDto.setStoreName(mtStore.getName());
                 }
             }
             dataList.add(goodsCateDto);
        }

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<GoodsCateDto> paginationResponse = new PaginationResponse(pageImpl, GoodsCateDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加商品分类
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "新增商品分类")
    public MtGoodsCate addCate(MtGoodsCate reqDto) {
        MtGoodsCate mtCate = new MtGoodsCate();
        if (null != reqDto.getId()) {
            mtCate.setId(reqDto.getId());
        }
        mtCate.setMerchantId(reqDto.getMerchantId());
        mtCate.setStoreId(reqDto.getStoreId());
        mtCate.setName(reqDto.getName());
        mtCate.setStatus(StatusEnum.ENABLED.getKey());
        mtCate.setLogo(reqDto.getLogo());
        mtCate.setDescription(reqDto.getDescription());
        mtCate.setOperator(reqDto.getOperator());

        mtCate.setUpdateTime(new Date());
        mtCate.setCreateTime(new Date());

        this.save(mtCate);

        return mtCate;
    }

    /**
     * 根据ID获取分类信息
     *
     * @param id 分类ID
     * @throws BusinessCheckException
     */
    @Override
    public MtGoodsCate queryCateById(Integer id) {
        return mtGoodsCateMapper.selectById(id);
    }

    /**
     * 根据ID删除分类信息
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除商品分类")
    public void deleteCate(Integer id, String operator) {
        MtGoodsCate cateInfo = this.queryCateById(id);
        if (null == cateInfo) {
            return;
        }

        cateInfo.setStatus(StatusEnum.DISABLE.getKey());
        cateInfo.setUpdateTime(new Date());

        this.updateById(cateInfo);
    }

    /**
     * 修改分类
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "更新商品分类")
    public MtGoodsCate updateCate(MtGoodsCate reqDto) throws BusinessCheckException {
        MtGoodsCate mtCate = this.queryCateById(reqDto.getId());
        if (null == mtCate) {
            log.error("该分类状态异常");
            throw new BusinessCheckException("该分类状态异常");
        }

        mtCate.setId(reqDto.getId());
        if (reqDto.getMerchantId() != null) {
            mtCate.setMerchantId(reqDto.getMerchantId());
        }
        if (reqDto.getStoreId() != null) {
            mtCate.setStoreId(reqDto.getStoreId());
        }
        if (reqDto.getLogo() != null) {
            mtCate.setLogo(reqDto.getLogo());
        }
        if (reqDto.getName() != null) {
            mtCate.setName(reqDto.getName());
        }
        if (reqDto.getDescription() != null) {
            mtCate.setDescription(reqDto.getDescription());
        }
        mtCate.setUpdateTime(new Date());
        if (StringUtil.isNotEmpty(reqDto.getOperator())) {
            mtCate.setOperator(reqDto.getOperator());
        }
        if (reqDto.getStatus() != null) {
            mtCate.setStatus(reqDto.getStatus());
        }
        if (reqDto.getSort() != null) {
            mtCate.setSort(reqDto.getSort());
        }

        this.updateById(mtCate);
        return mtCate;
    }

    @Override
    public List<MtGoodsCate> getStoreCateList(Integer storeId) {
        Integer merchantId = 0;
        MtStore mtStore = mtStoreMapper.selectById(storeId);
        if (mtStore != null) {
            merchantId = mtStore.getMerchantId();
        }
        List<MtGoodsCate> dataList = mtGoodsCateMapper.getStoreGoodsCateList(merchantId);
        return dataList;
    }

    @Override
    public List<MtGoodsCate> queryCateListByParams(Map<String, Object> params) {
        Map<String, Object> param = new HashMap<>();

        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        param.put("status", status);

        String merchantId = params.get("merchantId") == null ? "" : params.get("merchantId").toString();
        if (StringUtil.isNotEmpty(merchantId)) {
            param.put("merchant_id", merchantId);
        }
        String storeId = params.get("storeId") == null ? "" : params.get("storeId").toString();
        if (StringUtil.isNotEmpty(storeId)) {
            param.put("store_id", storeId);
        }

        List<MtGoodsCate> result = mtGoodsCateMapper.selectByMap(param);
        return result;
    }
}
