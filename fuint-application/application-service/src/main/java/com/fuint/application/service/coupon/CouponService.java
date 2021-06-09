package com.fuint.application.service.coupon;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dto.ReqCouponDto;
import com.fuint.application.ResponseObject;
import java.util.List;
import java.util.Map;

/**
 * 卡券业务接口
 * Created by zach on 2021/3/17.
 * Updated by zach on 2021/4/23.
 */
public interface CouponService {

    /**
     * 分页查询卡券列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtCoupon> queryCouponListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加卡券
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    MtCoupon addCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException;

    /**
     * 修改卡券
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    MtCoupon updateCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException;

    /**
     * 根据组ID获取卡券信息
     *
     * @param id 券ID
     * @throws BusinessCheckException
     */
    MtCoupon queryCouponById(Long id) throws BusinessCheckException;

    /**
     * 根据分组ID 删除卡券信息
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteCoupon(Long id, String operator) throws BusinessCheckException;

    /**
     * 获取用户的卡券
     * @param paramMap 查询参数
     * @throws BusinessCheckException
     * */
    ResponseObject findMyCouponList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 获取卡券列表
     * @param paramMap 查询参数
     * @throws BusinessCheckException
     * */
    ResponseObject findCouponList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 发放卡券
     *
     * @param groupId 券ID
     * @param mobile  操作人
     * @param num     发放套数
     * @throws BusinessCheckException
     */
    void sendCoupon(Long groupId, String mobile, Integer num, String uuid) throws BusinessCheckException;

    /**
     * 根据分组获取卡券列表
     * @param groupId 查询参数
     * @throws BusinessCheckException
     * */
    List<MtCoupon> queryCouponListByGroupId(Long groupId) throws BusinessCheckException;

    /**
     * 核销卡券
     * @param userCouponId 用户券ID
     * @param userId       核销用户ID
     * @param storeId      店铺ID
     * @throws BusinessCheckException
     * */
    String useCoupon(Long userCouponId, Integer userId, Integer storeId) throws BusinessCheckException;

    /**
     * 根据券ID 删除个人卡券 zach 20190912 add
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteUserCoupon(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据券ID 撤销个人卡券消费流水 zach 20191012 add
     *
     * @param id       消费流水ID
     * @param userCouponId       用户卡券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void rollbackUserCoupon(Integer id, Integer userCouponId,String operator) throws BusinessCheckException;

    /**
     * 根据ID获取用户卡券信息
     * @param userCouponId 查询参数
     * @throws BusinessCheckException
     * */
    MtUserCoupon queryUserCouponById(Integer userCouponId) throws BusinessCheckException;

    /**
     * 根据批次撤销卡券
     * @param id       ID
     * @param uuid       批次ID
     * @param operator   操作人
     * @throws BusinessCheckException
     */
    void removeUserCoupon(Long id, String uuid, String operator) throws BusinessCheckException;

    /**
     * 判断卡券码是否过期
     * @param code 券码
     * */
    boolean codeExpired(String code);

    /**
     * 判断卡券是否有效
     * @param coupon
     * @return
     * */
    boolean isCouponEffective(MtCoupon coupon);
}
