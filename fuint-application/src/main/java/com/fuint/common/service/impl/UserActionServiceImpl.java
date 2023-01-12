package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.UserActionService;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtUserActionMapper;
import com.fuint.repository.model.MtUserAction;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class UserActionServiceImpl extends ServiceImpl<MtUserActionMapper, MtUserAction> implements UserActionService {

    @Resource
    private MtUserActionMapper mtUserActionMapper;

    /**
     * 分页查询会员行为记录列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtUserAction> queryUserActionListByPagination(PaginationRequest paginationRequest) {
        Page<MtUserAction> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtUserAction> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtUserAction::getStatus, StatusEnum.DISABLE.getKey());

        String description = paginationRequest.getSearchParams().get("description") == null ? "" : paginationRequest.getSearchParams().get("description").toString();
        if (StringUtils.isNotBlank(description)) {
            lambdaQueryWrapper.like(MtUserAction::getDescription, description);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtUserAction::getStatus, status);
        }

        lambdaQueryWrapper.orderByDesc(MtUserAction::getId);
        List<MtUserAction> dataList = mtUserActionMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
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
     * @param  reqUserAction
     */
    @Override
    public boolean addUserAction(MtUserAction reqUserAction) {
        if (reqUserAction.getAction() == null || reqUserAction.getUserId() == null) {
            return false;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", reqUserAction.getUserId());
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
