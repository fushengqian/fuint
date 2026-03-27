package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.merchant.StaffDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.param.StaffPage;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtStaffMapper;
import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
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
 * 员工管理接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class StaffServiceImpl extends ServiceImpl<MtStaffMapper, MtStaff> implements StaffService {

    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);

    private MtStaffMapper mtStaffMapper;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 短信发送接口
     */
    private SendSmsService sendSmsService;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    /**
     * 商户接口
     */
    private MerchantService merchantService;

    /**
     * 员工查询列表
     *
     * @param staffPage
     * @return
     */
    @Override
    public PaginationResponse<StaffDto> queryStaffListByPagination(StaffPage staffPage) {
        Page<MtStaff> pageHelper = PageHelper.startPage(staffPage.getPage(), staffPage.getPageSize());
        LambdaQueryWrapper<MtStaff> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtStaff::getAuditedStatus, StatusEnum.DISABLE.getKey());

        String name = staffPage.getRealName();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtStaff::getRealName, name);
        }
        String status = staffPage.getAuditedStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtStaff::getAuditedStatus, status);
        }
        String mobile = staffPage.getMobile();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtStaff::getMobile, mobile);
        }
        Integer merchantId = staffPage.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtStaff::getMerchantId, merchantId);
        }
        Integer storeId = staffPage.getStoreId();
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.eq(MtStaff::getStoreId, storeId);
        }
        Integer category = staffPage.getCategory();
        if (category != null && category > 0) {
            lambdaQueryWrapper.eq(MtStaff::getCategory, category);
        }
        String keyword = staffPage.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtStaff::getMobile, keyword)
                    .or()
                    .eq(MtStaff::getRealName, keyword));
        }
        lambdaQueryWrapper.orderByDesc(MtStaff::getId);
        List<MtStaff> staffList = mtStaffMapper.selectList(lambdaQueryWrapper);
        List<StaffDto> dataList = new ArrayList<>();

        if (staffList != null && staffList.size() > 0) {
            for (MtStaff mtStaff : staffList) {
                 StaffDto staffDto = new StaffDto();
                 mtStaff.setMobile(CommonUtil.hidePhone(mtStaff.getMobile()));
                 BeanUtils.copyProperties(mtStaff, staffDto);
                 if (mtStaff.getStoreId() != null && mtStaff.getStoreId() > 0) {
                     MtStore mtStore = storeService.queryStoreById(mtStaff.getStoreId());
                     staffDto.setStoreInfo(mtStore);
                 }
                 dataList.add(staffDto);
            }
        }
        PageRequest pageRequest = PageRequest.of(staffPage.getPage(), staffPage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<StaffDto> paginationResponse = new PaginationResponse(pageImpl, StaffDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 保存员工信息
     *
     * @param  mtStaff 员工参数
     * @param  accountInfo 操作人
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "保存店铺员工")
    public MtStaff saveStaff(MtStaff mtStaff, AccountInfo accountInfo) throws BusinessCheckException {
        mtStaff.setUpdateTime(new Date());
        if (mtStaff.getId() == null || mtStaff.getId() <= 0) {
            mtStaff.setCreateTime(new Date());
            if (mtStaff.getAuditedStatus() == null) {
                mtStaff.setAuditedStatus(StatusEnum.UnAudited.getKey());
            } else {
                mtStaff.setAuditedStatus(mtStaff.getAuditedStatus());
            }
            this.save(mtStaff);
        } else {
            Integer id = mtStaff.getId();
            MtStaff mtStaffOld = mtStaffMapper.selectById(id);
            if (mtStaffOld.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
                mtStaff.setAuditedTime(new Date());
            }
            mtStaff.setMerchantId(mtStaffOld.getMerchantId());
        }

        if (!mtStaff.getMerchantId().equals(accountInfo.getMerchantId())) {
            throw new BusinessCheckException("没有操作权限");
        }

        MtUser mtUser = null;
        if (mtStaff.getUserId() != null) {
            mtUser = memberService.queryMemberById(mtStaff.getUserId());
        }

        // 关联会员信息
        if (mtStaff.getUserId() == null || mtUser == null) {
            MtUser userInfo = new MtUser();
            userInfo.setName(mtStaff.getRealName());
            userInfo.setDescription("系统自动注册店铺员工账号");
            userInfo.setStoreId(mtStaff.getStoreId());
            userInfo.setMerchantId(mtStaff.getMerchantId());
            userInfo.setIsStaff(YesOrNoEnum.YES.getKey());
            userInfo.setOperator(accountInfo.getAccountName());
            mtUser = memberService.addMember(userInfo, "0");
            if (mtUser != null) {
                mtStaff.setUserId(mtUser.getId());
            } else {
                throw new BusinessCheckException("新增员工失败");
            }
        } else {
            mtUser.setIsStaff(YesOrNoEnum.YES.getKey());
            mtUser.setOperator(accountInfo.getAccountName());
            memberService.updateMember(mtUser, false);
        }

        // 更新员工
        this.updateById(mtStaff);
        logger.info("operator：{} 保存员工信息mtStaff：{}", accountInfo.getAccountName(), mtStaff);
        return mtStaff;
    }

    /**
     * 根据ID获取员工信息
     *
     * @param  id 员工ID
     * @return
     */
    @Override
    public MtStaff queryStaffById(Integer id) {
        if (id == null || id <= 0) {
            return null;
        }
        return mtStaffMapper.selectById(id);
    }

    /**
     * 修改店铺员工状态
     *
     * @param  staffId 员工ID
     * @param status 状态
     * @param accountInfo 操作人
     * @return
     */
    @Override
    @OperationServiceLog(description = "修改店铺员工状态")
    public Integer updateAuditedStatus(Integer staffId, String status, AccountInfo accountInfo) throws BusinessCheckException {
        MtStaff mtStaff = mtStaffMapper.selectById(staffId);
        if (!mtStaff.getMerchantId().equals(accountInfo.getMerchantId())) {
            throw new BusinessCheckException("没有操作权限");
        }
        if (mtStaff != null) {
            mtStaff.setAuditedStatus(status);
            mtStaff.setUpdateTime(new Date());
            mtStaff.setAuditedTime(new Date());
            mtStaffMapper.updateById(mtStaff);

            // 发送短信通知
            MtStore mtStore = storeService.queryStoreById(mtStaff.getStoreId());
            if (mtStore == null) {
                mtStore = new MtStore();
                mtStore.setName("全部店铺");
            }
            List<String> mobileList = new ArrayList<>();
            mobileList.add(mtStaff.getMobile());

            try {
                Map<String, String> params = new HashMap<>();
                params.put("name", mtStaff.getRealName());
                params.put("storeId", mtStaff.getStoreId().toString());
                sendSmsService.sendSms(mtStaff.getMerchantId(), "confirmer-authed", mobileList, params);
            } catch (Exception e) {
                logger.error("修改店铺员工状态发送短信出错：", e.getMessage());
            }
        } else {
            return 0;
        }

        return staffId;
    }

    /**
     * 根据条件搜索员工
     *
     * @param params 查询参数
     * @return
     * */
    @Override
    public List<MtStaff> queryStaffByParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        return mtStaffMapper.selectByMap(params);
    }

    /**
     * 根据手机号获取员工信息
     *
     * @param mobile 手机号
     * @return
     */
    @Override
    public MtStaff queryStaffByMobile(String mobile) {
        if (StringUtil.isBlank(mobile)) {
            return null;
        }
        return mtStaffMapper.queryStaffByMobile(mobile);
    }

    /**
     * 根据会员ID获取员工信息
     *
     * @param  userId 会员ID
     * @return
     */
    @Override
    public MtStaff queryStaffByUserId(Integer userId) {
        return mtStaffMapper.queryStaffByUserId(userId);
    }

    /**
     * 根据手机号获取员工信息
     *
     * @param mobile 手机号
     * @return
     */
    @Override
    public StaffDto getStaffInfoByMobile(String mobile) {
        MtStaff mtStaff =  mtStaffMapper.queryStaffByMobile(mobile);
        StaffDto staffDto = new StaffDto();
        if (mtStaff != null) {
            BeanUtils.copyProperties(mtStaff, staffDto);
            if (staffDto.getStoreId() != null && staffDto.getStoreId() > 0) {
                MtStore mtStore = storeService.queryStoreById(staffDto.getStoreId());
                if (mtStore != null) {
                    staffDto.setStoreInfo(mtStore);
                }
            }
            if (staffDto.getMerchantId() != null && staffDto.getMerchantId() > 0) {
                MtMerchant mtMerchant = merchantService.getById(staffDto.getMerchantId());
                if (mtMerchant != null) {
                    staffDto.setMerchantInfo(mtMerchant);
                }
            }
        } else {
            return null;
        }
        return staffDto;
    }
}
