package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.StoreDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.StoreService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.bean.StoreDistanceBean;
import com.fuint.repository.mapper.MtStoreMapper;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 店铺管理业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class StoreServiceImpl extends ServiceImpl<MtStoreMapper, MtStore> implements StoreService {

    @Resource
    private MtStoreMapper mtStoreMapper;

    /**
     * 分页查询店铺列表
     *
     * @param  paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtStore> queryStoreListByPagination(PaginationRequest paginationRequest) {
        Page<MtStore> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtStore> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtStore::getStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtStore::getName, name);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtStore::getStatus, status);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtStore::getId, storeId);
        }

        lambdaQueryWrapper.orderByAsc(MtStore::getStatus).orderByDesc(MtStore::getIsDefault);
        List<MtStore> dataList = mtStoreMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtStore> paginationResponse = new PaginationResponse(pageImpl, MtStore.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 保存店铺信息
     *
     * @param storeDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "保存店铺信息")
    public MtStore saveStore(StoreDto storeDto) {
        MtStore mtStore = new MtStore();

        // 编辑店铺
        if (storeDto.getId() != null) {
            mtStore = this.queryStoreById(storeDto.getId());
        }

        mtStore.setName(storeDto.getName());
        mtStore.setContact(storeDto.getContact());
        mtStore.setOperator(storeDto.getOperator());
        mtStore.setWxMchId(storeDto.getWxMchId());
        if (storeDto.getWxApiV2() != null && !storeDto.getWxApiV2().equals(mtStore.getId()+"")) {
            mtStore.setWxApiV2(storeDto.getWxApiV2());
        }

        mtStore.setUpdateTime(new Date());
        if (storeDto.getId() == null) {
            mtStore.setCreateTime(new Date());
        }

        mtStore.setDescription(storeDto.getDescription());
        mtStore.setPhone(storeDto.getPhone());

        if (storeDto.getIsDefault() != null) {
            if (storeDto.getIsDefault().equals(YesOrNoEnum.YES.getKey())) {
                mtStoreMapper.resetDefaultStore();
            }
        }

        mtStore.setIsDefault(storeDto.getIsDefault());
        mtStore.setAddress(storeDto.getAddress());
        mtStore.setHours(storeDto.getHours());
        mtStore.setLatitude(storeDto.getLatitude());
        mtStore.setLongitude(storeDto.getLongitude());
        mtStore.setStatus(storeDto.getStatus());

        if (mtStore.getStatus() == null) {
            mtStore.setStatus(StatusEnum.ENABLED.getKey());
        }
        if (mtStore.getId() == null || mtStore.getId() < 1) {
            this.save(mtStore);
        } else {
            mtStoreMapper.updateById(mtStore);
        }
        return mtStore;
    }

    /**
     * 根据店铺ID获取店铺信息
     *
     * @param id 店铺ID
     * @throws BusinessCheckException
     */
    @Override
    public MtStore queryStoreById(Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        return mtStoreMapper.selectById(id);
    }

    /**
     * 获取系统默认店铺
     *
     * @throws BusinessCheckException
     */
    @Override
    public MtStore getDefaultStore() {
        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        params.put("is_default", YesOrNoEnum.YES.getKey());
        List<MtStore> storeList = this.queryStoresByParams(params);
        if (storeList.size() > 0) {
            return storeList.get(0);
        } else {
            Map<String, Object> param = new HashMap<>();
            param.put("status", StatusEnum.ENABLED.getKey());
            List<MtStore> dataList = this.queryStoresByParams(param);
            if (dataList.size() > 0) {
                return dataList.get(0);
            } else {
                return null;
            }
        }
    }

    /**
     * 根据店铺id列表获取店铺信息
     *
     * @param ids 店铺ID列表
     * @throws BusinessCheckException
     */
    @Override
    public List<MtStore> queryStoresByIds(List<Integer> ids) {
        return mtStoreMapper.findStoresByIds(ids);
    }

    /**
     * 根据店铺名称获取店铺信息
     *
     * @param storeName 店铺名称
     * @throws BusinessCheckException
     */
    @Override
    public StoreDto queryStoreByName(String storeName) {
        MtStore mtStore = mtStoreMapper.queryStoreByName(storeName);
        StoreDto storeDto = null;

        if (mtStore != null) {
            storeDto = new StoreDto();
            BeanUtils.copyProperties(mtStore, storeDto);
        }

        return storeDto;
    }

    /**
     * 根据店铺ID获取店铺信息
     *
     * @param id 店铺ID
     * @return
     * @throws BusinessCheckException
     */
    @Override
    public StoreDto queryStoreDtoById(Integer id) throws BusinessCheckException {
        MtStore mtStore = this.queryStoreById(id);
        if (null == mtStore || StatusEnum.DISABLE.getKey().equals(mtStore.getStatus())) {
            throw new BusinessCheckException("该店铺状态异常");
        }

        StoreDto mtStoreDto = new StoreDto();
        BeanUtils.copyProperties(mtStore, mtStoreDto);

        return mtStoreDto;
    }

    /**
     * 更新店铺状态
     *
     * @param id       店铺ID
     * @param operator 操作人
     * @param status   状态
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改店铺状态")
    public void updateStatus(Integer id, String operator, String status) throws BusinessCheckException {
        MtStore mtStore = this.queryStoreById(id);
        if (null == mtStore) {
            throw new BusinessCheckException("该店铺不存在.");
        }

        mtStore.setStatus(status);
        mtStore.setUpdateTime(new Date());
        mtStore.setOperator(operator);

        mtStoreMapper.updateById(mtStore);
    }

    @Override
    public List<MtStore> queryStoresByParams(Map<String, Object> params) {
        LambdaQueryWrapper<MtStore> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtStore::getStatus, StatusEnum.DISABLE.getKey());

        String storeId = params.get("storeId") == null ? "" : params.get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtStore::getId, storeId);
        }

        String name = params.get("name") == null ? "" : params.get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtStore::getName, name);
        }
        String status = params.get("status") == null ? "" : params.get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtStore::getStatus, status);
        }

        lambdaQueryWrapper.orderByAsc(MtStore::getStatus).orderByDesc(MtStore::getIsDefault);
        List<MtStore> dataList = mtStoreMapper.selectList(lambdaQueryWrapper);

        return dataList;
    }

    @Override
    public List<MtStore> queryByDistance(String keyword, String latitude, String longitude) {
        List<MtStore> dataList = new ArrayList<>();
        List<StoreDistanceBean> distanceList = mtStoreMapper.queryByDistance(keyword, latitude, longitude);
        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = mtStoreMapper.selectByMap(param);

        if (distanceList != null) {
            for (StoreDistanceBean bean : distanceList) {
                 for (MtStore mtStore : storeList) {
                      if (mtStore.getId().equals(bean.getId())) {
                          if (StringUtil.isNotEmpty(latitude) && StringUtil.isNotEmpty(longitude)) {
                              mtStore.setDistance(new BigDecimal(bean.getDistance()));
                          } else {
                              mtStore.setDistance(new BigDecimal("0.0"));
                          }
                          dataList.add(mtStore);
                      }
                 }
            }
        }


        return dataList;
    }
}
