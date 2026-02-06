package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.MerchantDto;
import com.fuint.common.dto.MerchantSettingDto;
import com.fuint.common.dto.StoreDto;
import com.fuint.common.enums.OrderSettingEnum;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.RegexUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.merchantApi.request.MerchantSettingParam;
import com.fuint.repository.mapper.MtGoodsMapper;
import com.fuint.repository.mapper.MtMerchantMapper;
import com.fuint.repository.mapper.MtStoreMapper;
import com.fuint.repository.model.MtGoods;
import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtSetting;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 商户业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class MerchantServiceImpl extends ServiceImpl<MtMerchantMapper, MtMerchant> implements MerchantService {

    private static final Logger logger = LoggerFactory.getLogger(MerchantServiceImpl.class);

    private MtMerchantMapper mtMerchantMapper;

    private MtStoreMapper mtStoreMapper;

    private MtGoodsMapper mtGoodsMapper;

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 分页查询商户列表
     *
     * @param  paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MerchantDto> queryMerchantListByPagination(PaginationRequest paginationRequest) {
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
        List<MtMerchant> merchantList = mtMerchantMapper.selectList(lambdaQueryWrapper);
        List<MerchantDto> dataList = new ArrayList<>();
        if (merchantList != null && merchantList.size() > 0) {
            for (MtMerchant mtMerchant : merchantList) {
                 MerchantDto merchantDto = new MerchantDto();
                 BeanUtils.copyProperties(mtMerchant, merchantDto);
                 merchantDto.setPhone(CommonUtil.hidePhone(mtMerchant.getPhone()));
                 dataList.add(merchantDto);
            }
        }

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MerchantDto> paginationResponse = new PaginationResponse(pageImpl, MtMerchant.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 保存商户信息
     *
     * @param  merchant 商户信息
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "保存商户信息")
    public MtMerchant saveMerchant(MtMerchant merchant) throws BusinessCheckException {
        MtMerchant mtMerchant = queryMerchantByName(merchant.getName());
        if (mtMerchant != null) {
            if ((merchant.getId() != null && !merchant.getId().equals(mtMerchant.getId())) || (merchant.getId() == null || merchant.getId() <= 0)) {
                throw new BusinessCheckException("该商户名称已经存在");
            }
        }
        mtMerchant = queryMerchantByNo(merchant.getNo());
        if (mtMerchant != null) {
            if ((merchant.getId() != null && !merchant.getId().equals(mtMerchant.getId())) || (merchant.getId() == null || merchant.getId() <= 0)) {
                throw new BusinessCheckException("该商户名称已经存在");
            }
        }

        mtMerchant = new MtMerchant();
        // 商户号不能含有中文
        if (RegexUtil.containsChinese(merchant.getNo())) {
            throw new BusinessCheckException("商户号不能含有中文字符");
        }

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
        if (merchant.getSettleRate() != null) {
            mtMerchant.setSettleRate(merchant.getSettleRate());
        }
        mtMerchant.setDescription(merchant.getDescription());
        mtMerchant.setPhone(merchant.getPhone());
        mtMerchant.setAddress(merchant.getAddress());
        mtMerchant.setStatus(merchant.getStatus());

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
     * @return
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
     * @return
     */
    @Override
    public MtMerchant queryMerchantByName(String name) {
        return mtMerchantMapper.queryMerchantByName(name);
    }

    /**
     * 根据商户号获取商户信息
     *
     * @param merchantNo 商户号
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
     * @return
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改商户状态")
    public void updateStatus(Integer id, String operator, String status) throws BusinessCheckException {
        MtMerchant mtMerchant = queryMerchantById(id);
        if (null == mtMerchant) {
            throw new BusinessCheckException("该商户不存在.");
        }

        // 如果是删除，检查是否有商品等数据
        if (status.equals(StatusEnum.DISABLE.getKey())) {
            // 删除店铺
            storeService.deleteStoreByMerchant(id);

            // 删除商品
            Map<String, Object> params = new HashMap<>();
            params.put("status", StatusEnum.ENABLED.getKey());
            params.put("merchant_id", id);
            List<MtGoods> goodsList = mtGoodsMapper.selectByMap(params);
            if (goodsList != null && goodsList.size() > 0) {
                logger.info("删除商户，连同商品一起删除", mtMerchant.getId());
                mtGoodsMapper.removeMerchantGoods(mtMerchant.getId());
            }
        }

        mtMerchant.setStatus(status);
        mtMerchant.setUpdateTime(new Date());
        mtMerchant.setOperator(operator);

        mtMerchantMapper.updateById(mtMerchant);
    }

    /**
     * 根据条件查询商户列表
     *
     * @param params 查询参数
     * @return
     * */
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
        return mtMerchantMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 查询我的商户列表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param status 状态
     * @return
     * */
    @Override
    public List<MtMerchant> getMyMerchantList(Integer merchantId, Integer storeId, String status) {
        Map<String, Object> param = new HashMap<>();
        if (merchantId != null && merchantId > 0) {
            param.put("merchantId", merchantId);
        }
        if (storeId != null && storeId > 0) {
            param.put("storeId", storeId);
        }
        if (StringUtils.isNotBlank(status)) {
            param.put("status", status);
        }
        return queryMerchantByParams(param);
    }

    /**
     * 获取商户设置信息
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @return
     * */
    @Override
    public MerchantSettingDto getMerchantSettingInfo(Integer merchantId, Integer storeId) {
       String name = "";
       Integer id = merchantId;
       String contact = "";
       String logo = "";
       String phone = "";
       if (storeId != null && storeId > 0) {
           MtStore storeInfo = storeService.queryStoreById(storeId);
           if (storeInfo != null) {
               id = storeInfo.getId();
               name = storeInfo.getName();
               contact = storeInfo.getContact();
               logo = storeInfo.getLogo();
               phone = storeInfo.getPhone();
           }
       } else {
           MtMerchant merchantInfo = getById(merchantId);
           if (merchantInfo != null) {
               name = merchantInfo.getName();
               contact = merchantInfo.getContact();
               logo = merchantInfo.getLogo();
               phone = merchantInfo.getPhone();
           }
       }
       MtSetting mtSetting = settingService.querySettingByName(merchantId, storeId, SettingTypeEnum.ORDER.getKey(), OrderSettingEnum.IS_CLOSE.getKey());
       MerchantSettingDto merchantSettingDto = new MerchantSettingDto();
       merchantSettingDto.setName(name);
       merchantSettingDto.setId(id);
       merchantSettingDto.setContact(contact);
       merchantSettingDto.setLogo(logo);
       merchantSettingDto.setPhone(phone);
       if (mtSetting != null) {
           merchantSettingDto.setStatus(mtSetting.getValue());
       } else {
           merchantSettingDto.setStatus(YesOrNoEnum.YES.getKey());
       }
       return merchantSettingDto;
    }

    /**
     * 保存商户设置信息
     *
     * @param params 商户设置项
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "保存商户设置信息")
    public MerchantSettingDto saveMerchantSetting(MerchantSettingParam params) throws BusinessCheckException {
        if (params.getStoreId() != null && params.getStoreId() > 0) {
            MtStore storeInfo = storeService.queryStoreById(params.getStoreId());
            if (storeInfo != null) {
                StoreDto storeDto = new StoreDto();
                storeDto.setId(storeInfo.getId());
                storeDto.setName(params.getName());
                storeDto.setContact(params.getContact());
                storeDto.setPhone(params.getPhone());
                storeDto.setLogo(params.getLogo());
                storeService.saveStore(storeDto);
            }
        } else {
            MtMerchant merchantInfo = getById(params.getMerchantId());
            if (merchantInfo != null) {
                merchantInfo.setName(params.getName());
                merchantInfo.setContact(params.getContact());
                merchantInfo.setPhone(params.getPhone());
                merchantInfo.setLogo(params.getLogo());
                updateById(merchantInfo);
            }
        }
        MtSetting mtSetting = settingService.querySettingByName(params.getMerchantId(), params.getStoreId(), SettingTypeEnum.ORDER.getKey(), OrderSettingEnum.IS_CLOSE.getKey());
        if (mtSetting != null && StringUtil.isNotEmpty(params.getStatus())) {
            mtSetting.setValue(params.getStatus());
            settingService.saveSetting(mtSetting);
        }
        return getMerchantSettingInfo(params.getMerchantId(), params.getStoreId());
    }
}
