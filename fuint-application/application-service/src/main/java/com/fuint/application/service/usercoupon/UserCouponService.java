package com.fuint.application.service.usercoupon;

import com.fuint.application.ResponseObject;
import com.fuint.application.dto.CouponDto;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtUserCoupon;
import java.util.List;
import java.util.Map;

/**
 * 用户卡券业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface UserCouponService {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtUserCoupon> queryUserCouponListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 领取卡券
     * @param paramMap
     * @return
     * */
    boolean receiveCoupon(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 预存
     * @param paramMap
     * @return
     * */
    boolean preStore(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 获取会员卡券列表
     * @param userId
     * @param status
     * @return
     * */
    List<MtUserCoupon> getUserCouponList(Integer userId, List<String> status) throws BusinessCheckException;

    /**
     * 获取用户的卡券
     * @param paramMap 查询参数
     * @throws BusinessCheckException
     * */
    ResponseObject getUserCouponList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 获取会员可支付用的卡券
     * @param userId
     * @return
     * */
    List<CouponDto> getPayAbleCouponList(Integer userId) throws BusinessCheckException;

    /**
     * 获取会员卡券详情
     * @param userId
     * @param couponId
     * */
    List<MtUserCoupon> getUserCouponDetail(Integer userId, Integer couponId) throws BusinessCheckException;

    /**
     * 获取会员卡券详情
     * @param userCouponId
     * */
    MtUserCoupon getUserCouponDetail(Integer userCouponId) throws BusinessCheckException;

    /**
     * 根据条件查询会员卡券
     * @param params
     * */
    List<MtUserCoupon> getUserCouponListByParams(Map<String, Object> params) throws BusinessCheckException;
}
