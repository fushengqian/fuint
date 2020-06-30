package com.fuint.coupon.service.give;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.MtCoupon;
import com.fuint.coupon.dao.entities.MtGive;
import com.fuint.coupon.dao.entities.MtGiveItem;
import com.fuint.coupon.dao.entities.MtUserCoupon;
import com.fuint.coupon.dto.GiveDto;
import com.fuint.coupon.dto.ReqCouponDto;
import com.fuint.coupon.ResponseObject;
import java.util.List;
import java.util.Map;

/**
 * 转赠业务接口
 * Created by zach on 2019/10/09.
 */
public interface GiveService {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<GiveDto> queryGiveListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 转赠优惠券
     *
     * @param paramMap
     * @throws BusinessCheckException
     */
    ResponseObject addGive(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 根据组ID获取信息
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    MtGive queryGiveById(Long id) throws BusinessCheckException;

    /**
     * 根据条件搜索详情
     *
     * @param params
     * @throws BusinessCheckException
     * */
    List<MtGiveItem> queryItemByParams(Map<String, Object> params) throws BusinessCheckException;
}
