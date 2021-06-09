package com.fuint.application.service.store;

import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dao.entities.TAccount;
import com.fuint.application.dao.repositories.MtStoreRepository;
import com.fuint.application.dao.repositories.TAccountRepository;
import com.fuint.application.dto.MtStoreDto;
import com.fuint.application.enums.StatusEnum;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 店铺管理业务实现类
 * Created by zach 2021-04-13
 */
@Service
public class StoreServiceImpl implements StoreService {

    private static final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);

    @Autowired
    private MtStoreRepository storeRepository;

    @Autowired
    private TAccountRepository tAccountRepository;

    /**
     * 分页查询店铺列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtStore> queryStoreListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        PaginationResponse<MtStore> paginationResponse = storeRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加店铺信息
     *
     * @param mtStoreDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "添加店铺信息")
    public MtStore addStore(MtStoreDto mtStoreDto) throws BusinessCheckException {
        MtStore mtStore = new MtStore();

        // 编辑才需要
        if(null != mtStoreDto.getId()) {
            mtStore.setId(mtStoreDto.getId());
        }
        mtStore.setName(mtStoreDto.getName());
        mtStore.setContact(mtStoreDto.getContact());
        mtStore.setStatus(StatusEnum.ENABLED.getKey());
        try {
            // 创建时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt=sdf.format(new Date());
            Date addtime = sdf.parse(dt);
            mtStore.setUpdateTime(addtime);
            mtStore.setCreateTime(addtime);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }
        mtStore.setDescription(mtStoreDto.getDescription());
        mtStore.setPhone(mtStoreDto.getPhone());

        mtStore=storeRepository.save(mtStore);
        return mtStore;
    }

    /**
     * 根据店铺信息ID获取店铺信息信息
     *
     * @param id 店铺信息ID
     * @throws BusinessCheckException
     */
    @Override
    public MtStore queryStoreById(Integer id) throws BusinessCheckException {
        return storeRepository.findOne(id);
    }

    /**
     * 根据店铺id列表获取店铺信息
     *
     * @param ids 店铺ID列表
     * @throws BusinessCheckException
     */
    @Override
    public List<MtStore> queryStoresByIds(List<Integer> ids) throws BusinessCheckException {
        return storeRepository.findStoresByIds(ids);
    }

    /**
     * 根据店铺名称获取店铺信息信息
     *
     * @param storeName 店铺名称
     * @throws BusinessCheckException
     */
    @Override
    public MtStoreDto queryStoreByName(String storeName) throws BusinessCheckException,InvocationTargetException,IllegalAccessException  {
        MtStore mtStore = storeRepository.queryStoreByName(storeName);
        MtStoreDto mtStoreDto = null;

        if (mtStore != null) {
            mtStoreDto = new MtStoreDto();
            ConvertUtils.register(new DateConverter(null), java.util.Date.class);
            BeanUtils.copyProperties(mtStoreDto, mtStore);
        }

        return mtStoreDto;
    }

    /**
     * 根据店铺ID获取店铺响应DTO
     *
     * @param id 店铺信息ID
     * @return
     * @throws BusinessCheckException
     */
    @Override
    public MtStoreDto queryStoreDtoById(Integer id) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        MtStore mtStore = this.queryStoreById(id);
        if (null == mtStore || StatusEnum.DISABLE.getKey().equals(mtStore.getStatus())) {
            log.error("该店铺状态异常");
            throw new BusinessCheckException("该店铺状态异常");
        }
        MtStoreDto mtStoreDto = new MtStoreDto();
        ConvertUtils.register(new DateConverter(null), java.util.Date.class);
        BeanUtils.copyProperties(mtStoreDto, mtStore);
        return mtStoreDto;
    }

    /**
     * 根据店铺ID 删除店铺信息
     *
     * @param id       店铺信息ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除店铺")
    public void deleteStore(Integer id, String operator) throws BusinessCheckException {
        MtStore MtStore = this.queryStoreById(id);
        if (null == MtStore) {
            return;
        }
        List<TAccount>  listTAccount=tAccountRepository.queryTAccountByStoreId(id);
        if (listTAccount != null && listTAccount.size()>0) {
            throw new BusinessCheckException("该店铺存在关联账号");
        }

        MtStore.setStatus(StatusEnum.DISABLE.getKey());
        MtStore.setUpdateTime(new Date());
        storeRepository.save(MtStore);
    }

    /**
     * 修改店铺
     *
     * @param MtStoreDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改店铺")
    public MtStore updateStore(MtStoreDto MtStoreDto) throws BusinessCheckException {

        MtStore MtStore = this.queryStoreById(MtStoreDto.getId());
        if (null == MtStore || StatusEnum.DISABLE.getKey().equals(MtStore.getStatus())) {
            log.error("该店铺状态异常");
            throw new BusinessCheckException("该店铺状态异常");
        }
        MtStore.setId(MtStoreDto.getId());
        MtStore.setName(MtStoreDto.getName());
        MtStore.setDescription(MtStoreDto.getDescription());
        //修改时间
        MtStore.setUpdateTime(new Date());

        MtStore.setPhone(MtStoreDto.getPhone());
        MtStore.setContact(MtStoreDto.getContact());

        storeRepository.save(MtStore);
        return MtStore;
    }

    @Override
    public List<MtStore> queryEffectiveStoreRange(Map<String, Object> params) throws BusinessCheckException {
        log.info("############ 根据创建时间参数查询店铺信息 #################.");
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }
        Date beginTime = (Date) params.get("beginTime");
        Date endTime = (Date) params.get("endTime");

        List<MtStore> result = storeRepository.queryEffectiveStoreRange(beginTime,endTime);
        return result;
    }

    /**
     * 更改状态(禁用)
     *
     * @param ids
     * @throws com.fuint.exception.BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "店铺更改状态")
    public Integer updateStatus(List<Integer> ids, String StatusEnum) throws BusinessCheckException {
        List<MtStore> mtStores = storeRepository.findStoresByIds(ids);
        if (mtStores == null) {
            log.error("店铺不存在.");
            throw new BusinessCheckException("店铺不存在.");
        }
        return storeRepository.updateStatus(ids,StatusEnum);
    }

    @Override
    public List<MtStore> queryStoresByParams(Map<String, Object> params) throws BusinessCheckException {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        Specification<MtStore> specification = storeRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtStore> result = storeRepository.findAll(specification, sort);

        return result;
    }
}
