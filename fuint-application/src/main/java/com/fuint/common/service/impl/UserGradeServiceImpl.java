package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserGradeCatchTypeEnum;
import com.fuint.common.service.UserGradeService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtStaffMapper;
import com.fuint.repository.mapper.MtUserGradeMapper;
import com.fuint.repository.model.MtBanner;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserGrade;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员等级业务接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class UserGradeServiceImpl extends ServiceImpl<MtUserGradeMapper, MtUserGrade> implements UserGradeService {

    private MtUserGradeMapper mtUserGradeMapper;

    private MtStaffMapper mtStaffMapper;

    /**
     * 分页查询会员等级列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtUserGrade> queryUserGradeListByPagination(PaginationRequest paginationRequest) {
        Page<MtUserGrade> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtUserGrade> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtUserGrade::getStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtUserGrade::getName, name);
        }
        String catchType = paginationRequest.getSearchParams().get("catchType") == null ? "" : paginationRequest.getSearchParams().get("catchType").toString();
        if (StringUtils.isNotBlank(catchType)) {
            lambdaQueryWrapper.like(MtUserGrade::getCatchType, catchType);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtUserGrade::getStatus, status);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtUserGrade::getMerchantId, merchantId);
        }

        lambdaQueryWrapper.orderByDesc(MtUserGrade::getGrade);
        List<MtUserGrade> dataList = mtUserGradeMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtUserGrade> paginationResponse = new PaginationResponse(pageImpl, MtBanner.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加会员等级信息
     *
     * @param mtUserGrade 会员等级
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "新增会员等级")
    public MtUserGrade addUserGrade(MtUserGrade mtUserGrade) throws BusinessCheckException {
        if (mtUserGrade.getMerchantId() == null || mtUserGrade.getMerchantId() < 1) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }
        if (mtUserGrade.getGrade() != null && (mtUserGrade.getGrade() <= 0)) {
            throw new BusinessCheckException("会员等级需大于0");
        }
        if (mtUserGrade.getDiscount() != null && (mtUserGrade.getDiscount() > 10 || mtUserGrade.getDiscount() < 0)) {
            throw new BusinessCheckException("会员折扣需在0和10之间");
        }
        mtUserGradeMapper.insert(mtUserGrade);
        return mtUserGrade;
    }

    /**
     * 根据ID获取会员等级信息
     *
     * @param merchantId 商户ID
     * @param gradeId 会员等级ID
     * @param userId 会员ID
     * @return
     */
    @Override
    public MtUserGrade queryUserGradeById(Integer merchantId, Integer gradeId, Integer userId) {
        if (userId != null && userId > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("AUDITED_STATUS", StatusEnum.ENABLED.getKey());
            params.put("USER_ID", userId);
            List<MtStaff> staffList = mtStaffMapper.selectByMap(params);
            // 如果是员工关联的会员，就返回默认的会员等级
            if (staffList != null && staffList.size() > 0) {
                return getInitUserGrade(merchantId);
            }
        }
        return mtUserGradeMapper.selectById(gradeId);
    }

    /**
     * 修改会员等级
     *
     * @param mtUserGrade 会员等级
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改会员等级")
    public MtUserGrade updateUserGrade(MtUserGrade mtUserGrade) throws BusinessCheckException {
        if (mtUserGrade.getDiscount() != null && (mtUserGrade.getDiscount() > 10 || mtUserGrade.getDiscount() < 0)) {
            throw new BusinessCheckException("会员折扣需在0和10之间");
        }
        if (mtUserGrade.getGrade() != null && (mtUserGrade.getGrade() <= 0)) {
            throw new BusinessCheckException("会员等级需大于0");
        }
        MtUserGrade userGrade = mtUserGradeMapper.selectById(mtUserGrade.getId());
        if (null != userGrade) {
            mtUserGrade.setMerchantId(userGrade.getMerchantId());
            mtUserGradeMapper.updateById(mtUserGrade);
        }
        return mtUserGrade;
    }

    /**
     * 根据ID删除会员等级
     *
     * @param id ID
     * @param operator 操作人
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "删除会员等级")
    public Integer deleteUserGrade(Integer id, String operator) {
        MtUserGrade mtUserGrade = queryUserGradeById(0, id, 0);
        if (null == mtUserGrade) {
            return 0;
        }
        mtUserGrade.setStatus(StatusEnum.DISABLE.getKey());
        mtUserGradeMapper.updateById(mtUserGrade);
        return mtUserGrade.getId();
    }

    /**
     * 获取默认的会员等级
     *
     * @param merchantId 商户ID
     * @return
     */
    @Override
    public MtUserGrade getInitUserGrade(Integer merchantId) {
        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        param.put("CATCH_TYPE", UserGradeCatchTypeEnum.INIT.getKey());
        param.put("MERCHANT_ID", merchantId);

        List<MtUserGrade> dataList = mtUserGradeMapper.selectByMap(param);
        MtUserGrade initGrade;
        if (dataList != null && dataList.size() > 0) {
            initGrade = dataList.get(0);
        } else {
            initGrade = new MtUserGrade();
            initGrade.setId(0);
            initGrade.setStatus(StatusEnum.ENABLED.getKey());
            initGrade.setGrade(0);
            initGrade.setMerchantId(0);
            initGrade.setSpeedPoint(1f);
            initGrade.setDiscount(0f);
        }
        return initGrade;
    }

    /**
     * 获取付费会员等级列表
     *
     * @param merchantId 商户ID
     * @param userInfo 会员信息
     * @return
     * */
    @Override
    public List<MtUserGrade> getPayUserGradeList(Integer merchantId, MtUser userInfo) {
        LambdaQueryWrapper<MtUserGrade> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MtUserGrade::getStatus, StatusEnum.ENABLED.getKey());
        lambdaQueryWrapper.eq(MtUserGrade::getCatchType, UserGradeCatchTypeEnum.PAY.getKey());
        lambdaQueryWrapper.eq(MtUserGrade::getMerchantId, merchantId);
        lambdaQueryWrapper.orderByAsc(MtUserGrade::getGrade);

        List<MtUserGrade> userGrades = mtUserGradeMapper.selectList(lambdaQueryWrapper);
        List<MtUserGrade> dataList = new ArrayList<>();

        Integer userGradeId = 0;
        if (userInfo != null) {
            if (userInfo.getGradeId() != null && userInfo.getGradeId() > 0) {
                userGradeId = userInfo.getGradeId();
            }
        }

        if (userGrades.size() > 0) {
            MtUserGrade myGradeInfo = mtUserGradeMapper.selectById(userGradeId);
            if (myGradeInfo != null) {
                Integer myGrade = myGradeInfo.getGrade();
                for (MtUserGrade grade : userGrades) {
                    if (!myGrade.equals(grade.getGrade().toString()) && (grade.getGrade() > myGrade)) {
                        dataList.add(grade);
                    }
                }
            } else {
                for (MtUserGrade grade : userGrades) {
                     dataList.add(grade);
                }
            }
        }

        return dataList;
    }

    /**
     * 获取商户会员等级列表
     *
     * @param  merchantId 商户ID
     * @param  status 状态
     * @return
     * */
    @Override
    public List<MtUserGrade> getMerchantGradeList(Integer merchantId, String status) {
        return mtUserGradeMapper.getMerchantGradeList(merchantId, status);
    }
}
