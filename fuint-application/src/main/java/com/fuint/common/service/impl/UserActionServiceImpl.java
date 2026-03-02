package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.UserActionPage;
import com.fuint.common.service.UserActionService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtUserActionMapper;
import com.fuint.repository.model.MtUserAction;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员行为业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class UserActionServiceImpl extends ServiceImpl<MtUserActionMapper, MtUserAction> implements UserActionService {

    private MtUserActionMapper mtUserActionMapper;

    /**
     * 分页查询会员行为记录列表
     *
     * @param userActionPage
     * @return
     */
    @Override
    public PaginationResponse<MtUserAction> queryUserActionListByPagination(UserActionPage userActionPage) {
        Page<MtUserAction> pageHelper = PageHelper.startPage(userActionPage.getPage(), userActionPage.getPageSize());
        LambdaQueryWrapper<MtUserAction> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtUserAction::getStatus, StatusEnum.DISABLE.getKey());

        String description = userActionPage.getDescription();
        if (StringUtils.isNotBlank(description)) {
            lambdaQueryWrapper.like(MtUserAction::getDescription, description);
        }
        Integer merchantId = userActionPage.getMerchantId();
        if (merchantId != null) {
            lambdaQueryWrapper.eq(MtUserAction::getMerchantId, merchantId);
        }
        Integer storeId = userActionPage.getStoreId();
        if (storeId != null) {
            lambdaQueryWrapper.eq(MtUserAction::getStoreId, storeId);
        }
        String status = userActionPage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtUserAction::getStatus, status);
        }

        lambdaQueryWrapper.orderByDesc(MtUserAction::getId);
        List<MtUserAction> dataList = mtUserActionMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(userActionPage.getPage(), userActionPage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtUserAction> paginationResponse = new PaginationResponse(pageImpl, MtUserAction.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 新增会员行为
     *
     * @param  reqUserAction 会员行为
     * @return
     */
    @Override
    public boolean addUserAction(MtUserAction reqUserAction) throws BusinessCheckException {
        if (reqUserAction.getAction() == null || reqUserAction.getUserId() == null) {
            throw new BusinessCheckException("会员行为信息不完整");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("USER_ID", reqUserAction.getUserId());
        params.put("action", reqUserAction.getAction());
        if (reqUserAction.getParam() != null) {
            params.put("param", reqUserAction.getParam());
        }

        List<MtUserAction> dataList = mtUserActionMapper.selectByMap(params);

        // 防止重复
        if (dataList.size() == 0) {
            MtUserAction mtUserAction = new MtUserAction();
            mtUserAction.setAction(reqUserAction.getAction());
            mtUserAction.setUserId(reqUserAction.getUserId());
            mtUserAction.setMerchantId(reqUserAction.getMerchantId());
            mtUserAction.setStoreId(reqUserAction.getStoreId());
            mtUserAction.setParam(reqUserAction.getParam());
            mtUserAction.setOperator(reqUserAction.getOperator());
            mtUserAction.setDescription(reqUserAction.getDescription());
            mtUserAction.setStatus(StatusEnum.ENABLED.getKey());
            mtUserAction.setCreateTime(new Date());
            mtUserAction.setUpdateTime(new Date());
            mtUserActionMapper.insert(mtUserAction);
        }

        return true;
    }

    /**
     * 根据ID获取信息
     *
     * @param id
     * @return
     */
    @Override
    public MtUserAction getUserActionDetail(Integer id) {
        return mtUserActionMapper.selectById(id);
    }

    /**
     * 根据ID删除
     *
     * @param id
     * @param operator 操作人
     * @return
     */
    @Override
    public void deleteUserAction(Integer id, String operator) {
        MtUserAction mtUserAction = this.getUserActionDetail(id);
        if (mtUserAction == null) {
            return;
        }
        mtUserAction.setStatus(StatusEnum.DISABLE.getKey());
        mtUserAction.setUpdateTime(new Date());
        mtUserActionMapper.updateById(mtUserAction);
    }
}
