package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.GoodsCateDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.CateService;
import com.fuint.common.service.StoreService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtGoodsCateMapper;
import com.fuint.repository.mapper.MtGoodsMapper;
import com.fuint.repository.model.MtGoods;
import com.fuint.repository.model.MtGoodsCate;
import com.fuint.repository.model.MtStore;
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
    private MtGoodsMapper mtGoodsMapper;

    @Resource
    private MtGoodsCateMapper cateMapper;

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
        Page<MtGoodsCate> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
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
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtGoodsCate::getStoreId, 0)
                    .or()
                    .eq(MtGoodsCate::getStoreId, storeId));
        }
        lambdaQueryWrapper.orderByAsc(MtGoodsCate::getSort);
        List<GoodsCateDto> dataList = new ArrayList<>();
        List<MtGoodsCate> cateList = cateMapper.selectList(lambdaQueryWrapper);
        for (MtGoodsCate mtCate : cateList) {
             GoodsCateDto cateDto = new GoodsCateDto();
             BeanUtils.copyProperties(mtCate, cateDto);
             if (mtCate.getStoreId() != null && mtCate.getStoreId() > 0) {
                 MtStore storeInfo = storeService.queryStoreById(mtCate.getStoreId());
                 if (storeInfo != null) {
                     cateDto.setStoreName(storeInfo.getName());
                 }
             }
             dataList.add(cateDto);
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
        mtCate.setName(reqDto.getName());
        mtCate.setStatus(StatusEnum.ENABLED.getKey());
        mtCate.setLogo(reqDto.getLogo());
        mtCate.setDescription(reqDto.getDescription());
        mtCate.setOperator(reqDto.getOperator());
        mtCate.setMerchantId(reqDto.getMerchantId());
        mtCate.setStoreId(reqDto.getStoreId() == null ? 0 : reqDto.getStoreId());
        mtCate.setUpdateTime(new Date());
        mtCate.setCreateTime(new Date());
        this.save(mtCate);
        return mtCate;
    }

    /**
     * 根据ID获取分类信息
     *
     * @param  id 分类ID
     * @throws BusinessCheckException
     */
    @Override
    public MtGoodsCate queryCateById(Integer id) {
        return cateMapper.selectById(id);
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
    public void deleteCate(Integer id, String operator) throws BusinessCheckException {
        MtGoodsCate cateInfo = queryCateById(id);

        Map<String, Object> params = new HashMap<>();
        params.put("cate_id", id);
        params.put("status", StatusEnum.ENABLED.getKey());
        List<MtGoods> goodsList = mtGoodsMapper.selectByMap(params);
        if (goodsList != null && goodsList.size() > 0) {
            throw new BusinessCheckException("删除失败，该分类有商品存在");
        }
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
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新商品分类")
    public MtGoodsCate updateCate(MtGoodsCate reqDto) throws BusinessCheckException {
        MtGoodsCate mtCate = queryCateById(reqDto.getId());
        if (null == mtCate) {
            log.error("该分类状态异常");
            throw new BusinessCheckException("该分类状态异常");
        }
        mtCate.setId(reqDto.getId());
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
            if (reqDto.getStatus().equals(StatusEnum.DISABLE.getKey())) {
                deleteCate(mtCate.getId(), reqDto.getOperator());
            }
            mtCate.setStatus(reqDto.getStatus());
        }
        if (reqDto.getSort() != null) {
            mtCate.setSort(reqDto.getSort());
        }
        if (reqDto.getMerchantId() != null) {
            mtCate.setMerchantId(reqDto.getMerchantId());
        }
        if (reqDto.getStoreId() != null) {
            mtCate.setStoreId(reqDto.getStoreId());
        }
        this.updateById(mtCate);
        return mtCate;
    }

    @Override
    public List<MtGoodsCate> queryCateListByParams(Map<String, Object> params) {
        LambdaQueryWrapper<MtGoodsCate> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtGoodsCate::getStatus, StatusEnum.DISABLE.getKey());
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String merchantId =  params.get("merchantId") == null ? "" : params.get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtGoodsCate::getMerchantId, merchantId);
        }
        String name =  params.get("name") == null ? "" : params.get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtGoodsCate::getName, name);
        }
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtGoodsCate::getStatus, status);
        }
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtGoodsCate::getStoreId, 0)
                    .or()
                    .eq(MtGoodsCate::getStoreId, storeId));
        }
        lambdaQueryWrapper.orderByAsc(MtGoodsCate::getSort);
        List<MtGoodsCate> dataList = cateMapper.selectList(lambdaQueryWrapper);
        return dataList;
    }
}
