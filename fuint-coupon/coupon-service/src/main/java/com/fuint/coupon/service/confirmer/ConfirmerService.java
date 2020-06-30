package com.fuint.coupon.service.confirmer;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.MtConfirmer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 核销人员管理接口
 * Created by zach 20190909
 */
public interface ConfirmerService {

    /**
     * 核销人员查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtConfirmer> queryConfirmerListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;


    /**
     * 添加核销人员记录
     *
     * @param reqConfirmerDto
     * @throws BusinessCheckException
     */
    MtConfirmer addConfirmer(MtConfirmer reqConfirmerDto) throws BusinessCheckException;


    /**
     * 修改核销人员信息
     *
     * @param reqConfirmerDto
     * @throws BusinessCheckException
     */
    MtConfirmer updateStore(MtConfirmer reqConfirmerDto) throws BusinessCheckException;

    /**
     * 根据ID获取店铺信息
     *
     * @param id 核销人员id
     * @throws BusinessCheckException
     */
    MtConfirmer queryConfirmerById(Integer id) throws BusinessCheckException;



    /**
     * 审核更改状态(禁用，审核通过)
     *
     * @param ids
     * @throws BusinessCheckException
     */
    Integer updateAuditedStatus(List<Integer> ids, String statusEnum) throws BusinessCheckException;

    /**
     * 根据条件搜索审核人员
     * */
    public List<MtConfirmer> queryConfirmerByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 根据会员用户手机获取会员用户信息
     *
     * @param mobile 会员用户手机
     * @throws BusinessCheckException
     */
    MtConfirmer queryConfirmerByMobile(String mobile) throws BusinessCheckException;

}
