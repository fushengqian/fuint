package com.fuint.coupon.service.member;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.MtUser;
import com.fuint.coupon.dao.entities.UvCouponInfo;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 会员用户业务接口
 * Created by zach 20190820
 */
public interface MemberService {

    /**
     * 分页查询会员用户列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtUser> queryMemberListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加会员用户
     *
     * @param reqStoreDto
     * @throws BusinessCheckException
     */
    MtUser addMember(MtUser reqStoreDto) throws BusinessCheckException;

    /**
     * 修改会员用户
     *
     * @param reqStoreDto
     * @throws BusinessCheckException
     */
    MtUser updateMember(MtUser reqStoreDto) throws BusinessCheckException;

    /**
     * 根据会员用户ID获取会员用户信息
     *
     * @param id 会员用户ID
     * @throws BusinessCheckException
     */
    MtUser queryMemberById(Integer id) throws BusinessCheckException;


    /**
     * 根据会员用户手机获取会员用户信息
     *
     * @param mobile 会员用户手机
     * @throws BusinessCheckException
     */
    MtUser queryMemberByMobile(String mobile) throws BusinessCheckException;

    /**
     * 根据参数查询会员用户信息
     * @param params
     * @return
     * @throws BusinessCheckException
     */
    public List<MtUser> queryEffectiveMemberRange(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 更改状态(禁用)
     *
     * @param ids
     * @throws com.fuint.exception.BusinessCheckException
     */
    Integer updateStatus(List<Integer> ids, String statusEnum) throws BusinessCheckException;

    /**
     * 根据条件搜索会员用户
     * */
    public List<MtUser> queryMembersByParams(Map<String, Object> params) throws BusinessCheckException;


    /**
     * 根据会员ID 删除店铺信息
     *
     * @param id      会员ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    Integer deleteMember(Integer id, String operator) throws BusinessCheckException;
}
