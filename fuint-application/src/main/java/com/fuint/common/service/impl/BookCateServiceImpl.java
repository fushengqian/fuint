package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.service.BookCateService;
import com.fuint.common.service.StoreService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtBookCateMapper;
import com.fuint.common.service.SettingService;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.model.MtBookCate;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
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
 * 预约分类服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class BookCateServiceImpl extends ServiceImpl<MtBookCateMapper, MtBookCate> implements BookCateService {

    private static final Logger logger = LoggerFactory.getLogger(BookCateServiceImpl.class);

    private MtBookCateMapper mtBookCateMapper;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    /**
     * 分页查询预约分类列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtBookCate> queryBookCateListByPagination(PaginationRequest paginationRequest) {
        Page<MtBookCate> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtBookCate> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBookCate::getStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtBookCate::getName, name);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtBookCate::getStatus, status);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtBookCate::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtBookCate::getStoreId, 0)
                    .or()
                    .eq(MtBookCate::getStoreId, storeId));
        }

        lambdaQueryWrapper.orderByAsc(MtBookCate::getSort);
        List<MtBookCate> dataList = mtBookCateMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtBookCate> paginationResponse = new PaginationResponse(pageImpl, MtBookCate.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加预约分类
     *
     * @param mtBookCate 分类信息
     * @return
     */
    @Override
    @OperationServiceLog(description = "添加预约分类")
    public MtBookCate addBookCate(MtBookCate mtBookCate) throws BusinessCheckException {
        MtBookCate bookCate = new MtBookCate();
        Integer storeId = mtBookCate.getStoreId() == null ? 0 : mtBookCate.getStoreId();
        if (mtBookCate.getMerchantId() == null || mtBookCate.getMerchantId() <= 0) {
            MtStore mtStore = storeService.queryStoreById(storeId);
            if (mtStore != null && mtStore.getMerchantId() != null) {
                bookCate.setMerchantId(mtStore.getMerchantId());
            }
        }
        if (mtBookCate.getMerchantId() == null || mtBookCate.getMerchantId() <= 0) {
            throw new BusinessCheckException("新增预约分类失败：所属商户不能为空！");
        }
        if (StringUtil.isEmpty(mtBookCate.getName())) {
            throw new BusinessCheckException("新增预约分类失败：分类名称不能为空！");
        }
        if (StringUtil.isEmpty(mtBookCate.getLogo())) {
            throw new BusinessCheckException("新增预约分类失败：封面图片不能为空！");
        }
        bookCate.setStoreId(storeId);
        bookCate.setName(mtBookCate.getName());
        bookCate.setLogo(mtBookCate.getLogo());
        bookCate.setDescription(mtBookCate.getDescription());
        bookCate.setStatus(StatusEnum.ENABLED.getKey());
        bookCate.setUpdateTime(new Date());
        bookCate.setCreateTime(new Date());
        bookCate.setSort(mtBookCate.getSort());
        bookCate.setOperator(mtBookCate.getOperator());
        bookCate.setMerchantId(mtBookCate.getMerchantId());
        Integer id = mtBookCateMapper.insert(bookCate);
        if (id > 0) {
            return bookCate;
        } else {
            logger.error("新增预约分类失败.");
            throw new BusinessCheckException("抱歉，新增预约分类失败！");
        }
    }

    /**
     * 根据ID获取预约分类信息
     *
     * @param id 预约分类ID
     * @return
     */
    @Override
    public MtBookCate getBookCateById(Integer id) {
        return mtBookCateMapper.selectById(id);
    }

    /**
     * 修改预约分类
     *
     * @param  mtBookCate
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改预约分类")
    public MtBookCate updateBookCate(MtBookCate mtBookCate) throws BusinessCheckException {
        MtBookCate bookCate = getBookCateById(mtBookCate.getId());
        if (bookCate == null) {
            throw new BusinessCheckException("该预约分类状态异常");
        }

        bookCate.setId(mtBookCate.getId());
        if (mtBookCate.getLogo() != null) {
            bookCate.setLogo(mtBookCate.getLogo());
        }
        if (mtBookCate.getName() != null) {
            bookCate.setName(mtBookCate.getName());
        }
        if (mtBookCate.getStoreId() != null) {
            bookCate.setStoreId(mtBookCate.getStoreId());
        }
        if (mtBookCate.getDescription() != null) {
            bookCate.setDescription(mtBookCate.getDescription());
        }
        if (mtBookCate.getOperator() != null) {
            bookCate.setOperator(mtBookCate.getOperator());
        }
        if (mtBookCate.getStatus() != null) {
            bookCate.setStatus(mtBookCate.getStatus());
        }
        if (mtBookCate.getSort() != null) {
            bookCate.setSort(mtBookCate.getSort());
        }
        bookCate.setUpdateTime(new Date());
        mtBookCateMapper.updateById(bookCate);

        return bookCate;
    }

    /**
     * 根据条件搜索焦点图
     *
     * @param params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    public List<MtBookCate> queryBookCateListByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String merchantId =  params.get("merchantId") == null ? "" : params.get("merchantId").toString();
        String name = params.get("name") == null ? "" : params.get("name").toString();

        LambdaQueryWrapper<MtBookCate> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBookCate::getStatus, StatusEnum.DISABLE.getKey());
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtBookCate::getName, name);
        }
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtBookCate::getStatus, status);
        }
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtBookCate::getMerchantId, merchantId);
        }
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtBookCate::getStoreId, 0)
                    .or()
                    .eq(MtBookCate::getStoreId, storeId));
        }

        lambdaQueryWrapper.orderByAsc(MtBookCate::getSort);
        List<MtBookCate> dataList = mtBookCateMapper.selectList(lambdaQueryWrapper);
        String baseImage = settingService.getUploadBasePath();

        if (dataList.size() > 0) {
            for (MtBookCate mtBookCate : dataList) {
                 mtBookCate.setLogo(baseImage + mtBookCate.getLogo());
            }
        }

        return dataList;
    }
}
