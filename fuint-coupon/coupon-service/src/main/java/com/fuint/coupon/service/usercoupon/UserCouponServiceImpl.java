package com.fuint.coupon.service.usercoupon;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.*;
import com.fuint.coupon.dao.repositories.MtUserCouponRepository;
import com.fuint.coupon.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 用户优惠券业务实现类
 * Created by zach on 2019/09/06.
 */
@Service
public class UserCouponServiceImpl extends BaseService implements UserCouponService {

    private static final Logger log = LoggerFactory.getLogger(UserCouponServiceImpl.class);

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    /**
     * 分页查询券列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtUserCoupon> queryUserCouponListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        paginationRequest.setSortColumn(new String[]{"status asc", "id desc"});
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }
}
