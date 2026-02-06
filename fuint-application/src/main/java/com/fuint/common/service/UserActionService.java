package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtUserAction;

/**
 * 会员行为业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface UserActionService extends IService<MtUserAction> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtUserAction> queryUserActionListByPagination(PaginationRequest paginationRequest);

    /**
     * 新增会员行为
     *
     * @param  mtUserAction
     * @throws BusinessCheckException
     * @return
     */
    boolean addUserAction(MtUserAction mtUserAction) throws BusinessCheckException;

    /**
     * 根据ID获取会员行为详情
     *
     * @param  id ID
     * @return
     */
    MtUserAction getUserActionDetail(Integer id);

    /**
     * 根据ID删除会员行为n
     *
     * @param id ID
     * @param operator 操作人
     * @return
     */
    void deleteUserAction(Integer id, String operator);
}
