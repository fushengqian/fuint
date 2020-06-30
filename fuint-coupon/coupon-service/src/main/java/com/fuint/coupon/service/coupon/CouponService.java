package com.fuint.coupon.service.coupon;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.MtCoupon;
import com.fuint.coupon.dao.entities.MtUserCoupon;
import com.fuint.coupon.dto.ReqCouponDto;
import com.fuint.coupon.ResponseObject;
import java.util.List;
import java.util.Map;

/**
 * 优惠券业务接口
 * Created by zach on 2019/8/06.
 */
public interface CouponService {

    /**
     * 分页查询分组列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtCoupon> queryCouponListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加优惠分组
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    MtCoupon addCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException;

    /**
     * 修改优惠分组
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    MtCoupon updateCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException;

    /**
     * 根据组ID获取分组信息
     *
     * @param id 券ID
     * @throws BusinessCheckException
     */
    MtCoupon queryCouponById(Long id) throws BusinessCheckException;

    /**
     * 根据分组ID 删除券信息
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteCoupon(Long id, String operator) throws BusinessCheckException;

    /**
     * 获取用户的优惠券
     * @param paramMap 查询参数
     * @throws BusinessCheckException
     * */
    ResponseObject findMyCouponList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 发放优惠券
     *
     * @param groupId 券ID
     * @param mobile  操作人
     * @param num     发放套数
     * @throws BusinessCheckException
     */
    void sendCoupon(Long groupId, String mobile, Integer num, String uuid) throws BusinessCheckException;

    /**
     * 根据分组获取优惠券列表
     * @param groupId 查询参数
     * @throws BusinessCheckException
     * */
    List<MtCoupon> queryCouponListByGroupId(Long groupId) throws BusinessCheckException;

    /**
     * 核销优惠券
     * @param userCouponId 用户券ID
     * @param userId       核销用户ID
     * @param storeId      店铺ID
     * @throws BusinessCheckException
     * */
    String useCoupon(Long userCouponId, Integer userId, Integer storeId) throws BusinessCheckException;

    /**
     * 把ID转换成名称
     * @param contentIds， 如1,2,3
     * @throws BusinessCheckException
     * */
    String getConetntByIds(String contentIds) throws BusinessCheckException;

    /**
     * 根据券ID 删除个人优惠券 zach 20190912 add
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteUserCoupon(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据券ID 撤销个人已使用的优惠券
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void rollbackUserCoupon(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据券ID 撤销个人优惠券消费流水 zach 20191012 add
     *
     * @param id       消费流水ID
     * @param userCouponId       用户优惠券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void rollbackUserCoupon(Integer id, Integer userCouponId,String operator) throws BusinessCheckException;

    /**
     * 根据ID获取用户优惠券信息
     * @param userCouponId 查询参数
     * @throws BusinessCheckException
     * */
    MtUserCoupon queryUserCouponById(Integer userCouponId) throws BusinessCheckException;

    /**
     * 根据批次撤销优惠券
     * @param id       ID
     * @param uuid       批次ID
     * @param operator   操作人
     * @throws BusinessCheckException
     */
    void removeUserCoupon(Long id, String uuid, String operator) throws BusinessCheckException;

    /**
     * 判断优惠券码是否过期
     * @param code 券码
     * */
    boolean codeExpired(String code);
}
