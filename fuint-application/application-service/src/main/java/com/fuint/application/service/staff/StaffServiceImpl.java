package com.fuint.application.service.staff;

import com.fuint.application.dao.entities.*;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.repositories.MtStaffRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.sms.SendSmsInterface;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 员工管理接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private MtStaffRepository staffRepository;
    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 短信发送接口
     */
    @Autowired
    private SendSmsInterface sendSmsService;

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
        PaginationResponse<MtStaff> paginationResponse = staffRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 保存员工信息
     *
     * @param reqStaffDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "保存员工信息")
    @Transactional
    public MtStaff saveStaff(MtStaff reqStaffDto) throws BusinessCheckException {
        reqStaffDto.setUpdateTime(new Date());

        MtUser mtUser = memberService.queryMemberByMobile(reqStaffDto.getMobile());
        if (mtUser == null) {
            mtUser = new MtUser();
            mtUser.setMobile(reqStaffDto.getMobile());
            mtUser.setName(reqStaffDto.getRealName());
            mtUser.setStoreId(reqStaffDto.getStoreId());
            mtUser.setDescription("员工注册自动添加");
            mtUser = memberService.addMember(mtUser);
        }
        reqStaffDto.setUserId(mtUser.getId());
        if (null == reqStaffDto.getId()) {
            reqStaffDto.setCreateTime(new Date());
            if (reqStaffDto.getAuditedStatus() == null) {
                reqStaffDto.setAuditedStatus(StatusEnum.UnAudited.getKey());
            } else {
                reqStaffDto.setAuditedStatus(reqStaffDto.getAuditedStatus());
            }
        } else {
            Integer id = reqStaffDto.getId();
            MtStaff mtStaffTemp = staffRepository.findOne(id);
            if (mtStaffTemp.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
                reqStaffDto.setAuditedTime(new Date());
            }
        }

        return staffRepository.save(reqStaffDto);
    }

    /**
     * 根据ID获取员工信息
     *
     * @param id 员工id
     * @throws BusinessCheckException
     */
    @Override
    public MtStaff queryStaffById(Integer id) {
        return staffRepository.findOne(id);
    }

    /**
     * 审核更改状态
     *
     * @param ids
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "更改员工状态")
    public Integer updateAuditedStatus(List<Integer> ids, String statusEnum) throws BusinessCheckException {
        Integer i = 0;
        Boolean flag = false;
        StatusEnum[] sees = StatusEnum.values();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt=sdf.format(new Date());
        Date currentDT;
        try {
            currentDT = sdf.parse(dt);
        } catch (ParseException e) {
            throw new BusinessCheckException("日期转换错误!");
        }
        // 遍历枚举key ,如果枚举合法,返回成功
        for (StatusEnum se : sees) {
            if (se.getKey().equals(statusEnum)) {
                flag = true;
                i = staffRepository.updateStatus(ids,statusEnum,currentDT);
                if (StatusEnum.ENABLED.getKey().equals(statusEnum)) {
                    // 审核通过，转移到普通会员表
                    for(Integer id : ids) {
                        MtStaff mtStaff = staffRepository.findOne(id);
                        if(mtStaff != null) {
                            MtUser tmemberInfo = new MtUser();
                            tmemberInfo.setMobile(mtStaff.getMobile());
                            tmemberInfo.setName(mtStaff.getRealName());
                            MtUser mtUser1 = memberService.queryMemberByMobile(mtStaff.getMobile());
                            if (mtUser1 == null) {
                                mtUser1 = memberService.addMember(tmemberInfo);
                            }

                            // 关联员工账户id
                            mtStaff.setUserId(mtUser1.getId());
                            mtStaff.setUpdateTime(currentDT);
                            mtStaff.setAuditedTime(currentDT);
                            staffRepository.save(mtStaff);

                            // 发送短信通知
                            MtStore mtStore = storeService.queryStoreById(mtStaff.getStoreId());
                            if (mtStore == null) {
                                mtStore = new MtStore();
                                mtStore.setName("全部店铺");
                            }
                            mtStaff.setStoreName(mtStore.getName());
                            List<String> mobileList = new ArrayList<String>();
                            mobileList.add(mtStaff.getMobile());

                            try {
                                Map<String, String> params = new HashMap<>();
                                params.put("name", mtStaff.getRealName());
                                params.put("storeName", mtStaff.getStoreName());
                                sendSmsService.sendSms("confirmer-authed", mobileList, params);
                            } catch (Exception e) {
                                throw new BusinessCheckException("短信发送失败");
                            }

                        }
                    }
                }
            }
        }

        if (Boolean.FALSE.equals(flag)) {
            throw new BusinessCheckException("枚举值不存在.");
        }

        return i;
    }

    /**
     * 根据条件搜索员工
     * */
    @Override
    public List<MtStaff> queryStaffByParams(Map<String, Object> params) {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        Specification<MtStaff> specification = staffRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtStaff> result = staffRepository.findAll(specification, sort);
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
        MtStaff mtStaff = staffRepository.queryStaffByMobile(mobile);
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
        MtStaff mtStaff = staffRepository.queryStaffByUserId(userId);
        return mtStaff;
    }
}
