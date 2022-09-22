package com.fuint.application.service.store;

import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dao.repositories.MtStoreRepository;
import com.fuint.application.dto.MtStoreDto;
import com.fuint.application.enums.StatusEnum;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.collections.MapUtils;
import com.fuint.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 店铺管理业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private MtStoreRepository storeRepository;

    @PersistenceContext(unitName = "defaultPersistenceUnit")
    private EntityManager entityManager;

    /**
     * 分页查询店铺列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtStore> queryStoreListByPagination(PaginationRequest paginationRequest) {
        PaginationResponse<MtStore> paginationResponse = storeRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 保存店铺信息
     *
     * @param storeDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "保存店铺信息")
    public MtStore saveStore(MtStoreDto storeDto) throws BusinessCheckException {
        MtStore mtStore = new MtStore();

        // 编辑店铺
        if (storeDto.getId() != null) {
            mtStore = this.queryStoreById(storeDto.getId());
        }

        mtStore.setName(storeDto.getName());
        mtStore.setContact(storeDto.getContact());
        mtStore.setOperator(storeDto.getOperator());

        mtStore.setUpdateTime(new Date());
        if (storeDto.getId() == null) {
            mtStore.setCreateTime(new Date());
        }

        mtStore.setDescription(storeDto.getDescription());
        mtStore.setPhone(storeDto.getPhone());

        if (storeDto.getIsDefault() != null) {
            if (storeDto.getIsDefault().equals("Y")) {
                storeRepository.resetDefaultStore();
            }
        }

        mtStore.setIsDefault(storeDto.getIsDefault());
        mtStore.setAddress(storeDto.getAddress());
        mtStore.setHours(storeDto.getHours());
        mtStore.setLatitude(storeDto.getLatitude());
        mtStore.setLongitude(storeDto.getLongitude());

        if (mtStore.getStatus() == null) {
            mtStore.setStatus(StatusEnum.ENABLED.getKey());
        }

        return storeRepository.save(mtStore);
    }

    /**
     * 根据店铺ID获取店铺信息
     *
     * @param id 店铺ID
     * @throws BusinessCheckException
     */
    @Override
    public MtStore queryStoreById(Integer id) throws BusinessCheckException {
        if (id == null || id < 1) {
            return null;
        }
        return storeRepository.findOne(id);
    }

    /**
     * 根据店铺id列表获取店铺信息
     *
     * @param ids 店铺ID列表
     * @throws BusinessCheckException
     */
    @Override
    public List<MtStore> queryStoresByIds(List<Integer> ids) {
        return storeRepository.findStoresByIds(ids);
    }

    /**
     * 根据店铺名称获取店铺信息
     *
     * @param storeName 店铺名称
     * @throws BusinessCheckException
     */
    @Override
    public MtStoreDto queryStoreByName(String storeName) throws InvocationTargetException,IllegalAccessException {
        MtStore mtStore = storeRepository.queryStoreByName(storeName);
        MtStoreDto storeDto = null;

        if (mtStore != null) {
            storeDto = new MtStoreDto();
            ConvertUtils.register(new DateConverter(null), java.util.Date.class);
            BeanUtils.copyProperties(storeDto, mtStore);
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
    public MtStoreDto queryStoreDtoById(Integer id) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        MtStore mtStore = this.queryStoreById(id);
        if (null == mtStore || StatusEnum.DISABLE.getKey().equals(mtStore.getStatus())) {
            throw new BusinessCheckException("该店铺状态异常");
        }

        MtStoreDto mtStoreDto = new MtStoreDto();
        ConvertUtils.register(new DateConverter(null), java.util.Date.class);
        BeanUtils.copyProperties(mtStoreDto, mtStore);

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
    @OperationServiceLog(description = "更新店铺状态")
    public void updateStatus(Integer id, String operator, String status) throws BusinessCheckException {
        MtStore mtStore = this.queryStoreById(id);
        if (null == mtStore) {
            throw new BusinessCheckException("该店铺不存在.");
        }

        mtStore.setStatus(status);
        mtStore.setUpdateTime(new Date());
        mtStore.setOperator(operator);

        storeRepository.save(mtStore);
    }

    @Override
    public List<MtStore> queryStoresByParams(Map<String, Object> params) {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        Specification<MtStore> specification = storeRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtStore> result = storeRepository.findAll(specification, sort);

        return result;
    }

    @Override
    public List<MtStore> queryByDistance(String latitude, String longitude) {
        List<MtStore> dataList = new ArrayList<>();

        if (StringUtil.isEmpty(latitude) || StringUtil.isEmpty(longitude)) {
            return dataList;
        }

        StringBuffer queryStr = new StringBuffer();
        queryStr.append("SELECT t.id,(6371 * ACOS(COS( RADIANS(" + latitude + "))*COS(RADIANS(t.latitude))*COS(RADIANS(t.longitude ) - RADIANS(" + longitude +")) + SIN(RADIANS(" + latitude + "))*SIN(RADIANS(t.latitude)))) AS distance FROM mt_store t WHERE t.status = 'A' ORDER BY distance LIMIT 0,1000");
        Query query = entityManager.createNativeQuery(queryStr.toString());

        List<Object[]> contentObjList = query.getResultList();

        Map<String, Object> params = new HashMap<>();
        params.put("EQ_status", StatusEnum.ENABLED.getKey());
        Specification<MtStore> specification = storeRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtStore> result = storeRepository.findAll(specification, sort);

        for (Object[] objArray : contentObjList) {
             for(MtStore store : result) {
                 if (objArray[0].equals(store.getId())) {
                     store.setDistance(objArray[1].toString());
                     dataList.add(store);
                 }
             }
        }

        return dataList;
    }
}
