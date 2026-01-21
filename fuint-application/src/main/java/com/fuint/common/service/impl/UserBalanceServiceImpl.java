package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.UserBalancePage;
import com.fuint.common.service.UserBalanceService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtUserBalanceMapper;
import com.fuint.repository.model.MtUserBalance;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 会员余额服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class UserBalanceServiceImpl extends ServiceImpl<MtUserBalanceMapper, MtUserBalance> implements UserBalanceService {

    private static final Logger logger = LoggerFactory.getLogger(UserBalanceServiceImpl.class);

    private MtUserBalanceMapper mtUserBalanceMapper;

    /**
     * 分页查询数据列表
     *
     * @param userBalancePage
     * @return
     */
    @Override
    public PaginationResponse<MtUserBalance> queryUserBalanceListByPagination(UserBalancePage userBalancePage) {
        Page<MtUserBalance> pageHelper = PageHelper.startPage(userBalancePage.getPage(), userBalancePage.getPageSize());
        LambdaQueryWrapper<MtUserBalance> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtUserBalance::getStatus, StatusEnum.DISABLE.getKey());

        lambdaQueryWrapper.orderByAsc(MtUserBalance::getId);
        List<MtUserBalance> dataList = mtUserBalanceMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(userBalancePage.getPage(), userBalancePage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtUserBalance> paginationResponse = new PaginationResponse(pageImpl, MtUserBalance.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加会员余额
     *
     * @param mtUserBalance 会员余额信息
     * @return
     */
    @Override
    @OperationServiceLog(description = "新增会员余额")
    public MtUserBalance addUserBalance(MtUserBalance mtUserBalance) throws BusinessCheckException {
        mtUserBalance.setStatus(StatusEnum.ENABLED.getKey());
        Date dateTime = new Date();
        mtUserBalance.setUpdateTime(dateTime);
        mtUserBalance.setCreateTime(dateTime);
        Integer id = mtUserBalanceMapper.insert(mtUserBalance);
        if (id > 0) {
            logger.info("新增会员余额数据成功，会员ID:{}", mtUserBalance.getUserId());
            return mtUserBalance;
        } else {
            throw new BusinessCheckException("新增会员余额数据失败");
        }
    }

    /**
     * 根据ID获会员余额取息
     *
     * @param id 会员余额ID
     * @return
     */
    @Override
    public MtUserBalance queryUserBalanceById(Integer id) {
        return mtUserBalanceMapper.selectById(id);
    }

    /**
     * 修改会员余额数据
     *
     * @param mtUserBalance
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新会员余额")
    public MtUserBalance updateUserBalance(MtUserBalance mtUserBalance) throws BusinessCheckException {
        mtUserBalance = queryUserBalanceById(mtUserBalance.getId());
        if (mtUserBalance == null) {
            throw new BusinessCheckException("该会员余额状态异常");
        }
        mtUserBalance.setUpdateTime(new Date());
        mtUserBalanceMapper.updateById(mtUserBalance);
        logger.info("更新会员余额数据成功，会员ID:{}", mtUserBalance.getUserId());
        return mtUserBalance;
    }
}
