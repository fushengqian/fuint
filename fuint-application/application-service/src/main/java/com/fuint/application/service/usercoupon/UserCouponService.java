package com.fuint.application.service.usercoupon;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtUserCoupon;

import java.util.List;
import java.util.Map;

/**
 * 用户卡券业务接口
 * Created by zach on 2019/9/20
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
     * 领券
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
}
