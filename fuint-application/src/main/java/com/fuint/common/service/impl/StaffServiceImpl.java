package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.SendSmsService;
import com.fuint.common.service.StaffService;
import com.fuint.common.service.StoreService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtStaffMapper;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 员工管理接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class StaffServiceImpl extends ServiceImpl<MtStaffMapper, MtStaff> implements StaffService {

    @Resource
    private MtStaffMapper mtStaffMapper;

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 短信发送接口
     */
    @Autowired
    private SendSmsService sendSmsService;

    /**
     * 店铺接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 员工查询列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtStaff> queryStaffListByPagination(PaginationRequest paginationRequest) {
        Page<MtStaff> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtStaff> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtStaff::getAuditedStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtStaff::getRealName, name);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtStaff::getAuditedStatus, status);
        }
        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtStaff::getMobile, mobile);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtStaff::getStoreId, storeId);
        }
        String category = paginationRequest.getSearchParams().get("category") == null ? "" : paginationRequest.getSearchParams().get("category").toString();
        if (StringUtils.isNotBlank(category)) {
            lambdaQueryWrapper.eq(MtStaff::getCategory, category);
        }

        lambdaQueryWrapper.orderByDesc(MtStaff::getId);
        List<MtStaff> dataList = mtStaffMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtStaff> paginationResponse = new PaginationResponse(pageImpl, MtStaff.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 保存员工信息
     *
     * @param  mtStaff
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "保存店铺员工")
    public MtStaff saveStaff(MtStaff mtStaff) throws BusinessCheckException {
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
            MtStaff mtStaffTemp = mtStaffMapper.selectById(id);
            if (mtStaffTemp.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
                mtStaff.setAuditedTime(new Date());
            }
        }

        // 关联会员信息
        if (mtStaff.getUserId() == null) {
            MtUser userInfo = new MtUser();
            userInfo.setName(mtStaff.getRealName());
            userInfo.setDescription("系统自动注册店铺员工账号");
            userInfo.setStoreId(mtStaff.getStoreId());
            MtUser mtUser = memberService.addMember(userInfo);
            if (mtUser != null) {
                mtStaff.setUserId(mtUser.getId());
            }
        }

        // 更新员工
        this.updateById(mtStaff);
        return mtStaff;
    }

    /**
     * 根据ID获取员工信息
     *
     * @param  id 员工id
     * @throws BusinessCheckException
     */
    @Override
    public MtStaff queryStaffById(Integer id) {
        return mtStaffMapper.selectById(id);
    }

    /**
     * 更改员工状态
     *
     * @param  staffId
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "修改店铺员工状态")
    public Integer updateAuditedStatus(Integer staffId, String status) throws BusinessCheckException {
        MtStaff mtStaff = mtStaffMapper.selectById(staffId);
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
                sendSmsService.sendSms("confirmer-authed", mobileList, params);
            } catch (Exception e) {
                // empty
            }
        } else {
            return 0;
        }

        return staffId;
    }

    /**
     * 根据条件搜索员工
     * */
    @Override
    public List<MtStaff> queryStaffByParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        List<MtStaff> result = mtStaffMapper.selectByMap(params);
        return result;
    }

    /**
     * 根据手机号获取员工信息
     *
     * @param mobile 手机号
     * @throws BusinessCheckException
     */
    @Override
    public MtStaff queryStaffByMobile(String mobile) {
        MtStaff mtStaff = mtStaffMapper.queryStaffByMobile(mobile);
        return mtStaff;
    }

    /**
     * 根据会员ID获取员工信息
     *
     * @param userId 会员ID
     * @throws BusinessCheckException
     */
    @Override
    public MtStaff queryStaffByUserId(Integer userId) {
        MtStaff mtStaff = mtStaffMapper.queryStaffByUserId(userId);
        return mtStaff;
    }
}
