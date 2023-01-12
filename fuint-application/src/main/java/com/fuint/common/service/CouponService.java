package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.ReqCouponDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtUserCoupon;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 卡券业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CouponService extends IService<MtCoupon> {

    /**
     * 分页查询卡券列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtCoupon> queryCouponListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 保存卡券
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    MtCoupon saveCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException, ParseException;

    /**
     * 根据ID获取卡券信息
     *
     * @param id 卡券ID
     * @throws BusinessCheckException
     */
    MtCoupon queryCouponById(Integer id) throws BusinessCheckException;

    /**
     * 删除卡券信息
     *
     * @param id       卡券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteCoupon(Long id, String operator) throws BusinessCheckException;

    /**
     * 获取卡券列表
     * @param paramMap 查询参数
     * @throws BusinessCheckException
     * */
    ResponseObject findCouponList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 发放卡券
     *
     * @param couponId 券ID
     * @param mobile  操作人
     * @param num     发放套数
     * @throws BusinessCheckException
     */
    void sendCoupon(Integer couponId, String mobile, Integer num, String uuid, String operator) throws BusinessCheckException;

    /**
     * 根据分组获取卡券列表
     * @param groupId 查询参数
     * @throws BusinessCheckException
     * */
    List<MtCoupon> queryCouponListByGroupId(Integer groupId) throws BusinessCheckException;

    /**
     * 核销卡券
     * @param userCouponId 用户券ID
     * @param userId       核销会员ID
     * @param storeId      店铺ID
     * @param orderId      订单ID
     * @param amount 核销金额
     * @param remark 核销备注
     * @throws BusinessCheckException
     * */
    String useCoupon(Integer userCouponId, Integer userId, Integer storeId, Integer orderId, BigDecimal amount, String remark) throws BusinessCheckException;

    /**
     * 根据券ID删除个人卡券
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteUserCoupon(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据券ID 撤销个人卡券消费流水
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
     * @param id         ID
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
