package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MerchantService;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtMerchantMapper;
import com.fuint.repository.mapper.MtStoreMapper;
import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;

/**
 * 商户业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MtMerchantMapper, MtMerchant> implements MerchantService {

    @Resource
    private MtMerchantMapper mtMerchantMapper;

    @Resource
    private MtStoreMapper mtStoreMapper;

    /**
     * 分页查询商户列表
     *
     * @param  paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtMerchant> queryMerchantListByPagination(PaginationRequest paginationRequest) {
        Page<MtMerchant> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtMerchant> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtMerchant::getStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtMerchant::getName, name);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtMerchant::getStatus, status);
        }
        String id = paginationRequest.getSearchParams().get("id") == null ? "" : paginationRequest.getSearchParams().get("id").toString();
        if (StringUtils.isNotBlank(id)) {
            lambdaQueryWrapper.eq(MtMerchant::getId, id);
        }

        lambdaQueryWrapper.orderByAsc(MtMerchant::getStatus).orderByDesc(MtMerchant::getId);
        List<MtMerchant> dataList = mtMerchantMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtMerchant> paginationResponse = new PaginationResponse(pageImpl, MtMerchant.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 保存商户信息
     *
     * @param  merchant
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "保存商户信息")
    public MtMerchant saveMerchant(MtMerchant merchant) {
        MtMerchant mtMerchant = new MtMerchant();

        // 编辑商户
        if (merchant.getId() != null) {
            mtMerchant = queryMerchantById(merchant.getId());
        }

        if (merchant.getNo() == null || StringUtil.isEmpty(merchant.getNo())) {
            mtMerchant.setNo(CommonUtil.createMerchantNo());
        } else {
            mtMerchant.setNo(merchant.getNo());
        }
        if (merchant.getType() != null) {
            mtMerchant.setType(merchant.getType());
        }
        mtMerchant.setName(merchant.getName());
        mtMerchant.setLogo(merchant.getLogo());
        mtMerchant.setContact(merchant.getContact());
        mtMerchant.setOperator(merchant.getOperator());
        mtMerchant.setUpdateTime(new Date());
        if (merchant.getId() == null) {
            mtMerchant.setCreateTime(new Date());
        }
        mtMerchant.setWxAppId(merchant.getWxAppId());
        mtMerchant.setWxAppSecret(merchant.getWxAppSecret());
        mtMerchant.setWxOfficialAppId(merchant.getWxOfficialAppId());
        mtMerchant.setWxOfficialAppSecret(merchant.getWxOfficialAppSecret());
        mtMerchant.setDescription(merchant.getDescription());
        mtMerchant.setPhone(merchant.getPhone());
        mtMerchant.setAddress(merchant.getAddress());

        if (mtMerchant.getStatus() == null) {
            mtMerchant.setStatus(StatusEnum.ENABLED.getKey());
        }
        if (mtMerchant.getId() == null || mtMerchant.getId() < 1) {
            this.save(mtMerchant);
        } else {
            mtMerchantMapper.updateById(mtMerchant);
        }
        return mtMerchant;
    }

    /**
     * 根据ID获取商户信息
     *
     * @param  id 商户ID
     * @throws BusinessCheckException
     */
    @Override
    public MtMerchant queryMerchantById(Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        return mtMerchantMapper.selectById(id);
    }

    /**
     * 根据名称获取商户信息
     *
     * @param  name 商户名称
     * @throws BusinessCheckException
     */
    @Override
    public MtMerchant queryMerchantByName(String name) {
        MtMerchant mtMerchant = mtMerchantMapper.queryMerchantByName(name);
        return mtMerchant;
    }

    /**
     * 根据商户号获取商户信息
     *
     * @param  merchantNo 商户号
     * @return
     */
    @Override
    public MtMerchant queryMerchantByNo(String merchantNo) {
        return mtMerchantMapper.queryMerchantByNo(merchantNo);
    }

    /**
     * 根据商户号获取商户ID
     *
     * @param merchantNo 商户号
     * @return
     */
    @Override
    public Integer getMerchantId(String merchantNo) {
       if (merchantNo == null || StringUtil.isEmpty(merchantNo)) {
           return 0;
       }
       MtMerchant mtMerchant = queryMerchantByNo(merchantNo);
       if (mtMerchant != null) {
           return  mtMerchant.getId();
       } else {
           return 0;
       }
    }

    /**
     * 更新商户状态
     *
     * @param  id       商户ID
     * @param  operator 操作人
     * @param  status   状态
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改商户状态")
    public void updateStatus(Integer id, String operator, String status) throws BusinessCheckException {
        MtMerchant mtMerchant = queryMerchantById(id);
        if (null == mtMerchant) {
            throw new BusinessCheckException("该商户不存在.");
        }

        mtMerchant.setStatus(status);
        mtMerchant.setUpdateTime(new Date());
        mtMerchant.setOperator(operator);

        mtMerchantMapper.updateById(mtMerchant);
    }

    @Override
    public List<MtMerchant> queryMerchantByParams(Map<String, Object> params) {
        LambdaQueryWrapper<MtMerchant> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtMerchant::getStatus, StatusEnum.DISABLE.getKey());

        String merchantId = params.get("merchantId") == null ? "" : params.get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtMerchant::getId, merchantId);
        }
        String storeId = params.get("storeId") == null ? "" : params.get("storeId").toString();
        if (StringUtils.isNotBlank(storeId) && StringUtil.isEmpty(merchantId)) {
            MtStore mtStore = mtStoreMapper.selectById(storeId);
            if (mtStore != null && mtStore.getMerchantId() > 0) {
                lambdaQueryWrapper.eq(MtMerchant::getId, mtStore.getMerchantId());
            }
        }
        String name = params.get("name") == null ? "" : params.get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtMerchant::getName, name);
        }
        String status = params.get("status") == null ? "" : params.get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtMerchant::getStatus, status);
        }

        lambdaQueryWrapper.orderByAsc(MtMerchant::getStatus).orderByDesc(MtMerchant::getId);
        List<MtMerchant> dataList = mtMerchantMapper.selectList(lambdaQueryWrapper);

        return dataList;
    }
}
