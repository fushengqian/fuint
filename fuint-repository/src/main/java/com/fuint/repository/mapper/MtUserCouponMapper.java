package com.fuint.repository.mapper;

import com.fuint.repository.bean.CouponNumBean;
import com.fuint.repository.model.MtUserCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员卡券表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtUserCouponMapper extends BaseMapper<MtUserCoupon> {

    Boolean updateExpireTime(@Param("couponId") Integer couponId, @Param("expireTime") String expireTime);

    Long getSendNum(@Param("couponId") Integer couponId);

    CouponNumBean getPeopleNumByCouponId(@Param("couponId") Integer couponId);

    List<MtUserCoupon> getUserCouponList(@Param("userId") Integer userId, @Param("statusList") List<String> statusList);

    List<MtUserCoupon> getUserCouponListByCouponId(@Param("userId") Integer userId, @Param("couponId") Integer couponId ,@Param("statusList") List<String> statusList);

    MtUserCoupon findByCode(@Param("code") String code);

    int removeUserCoupon(@Param("uuid") String uuid, @Param("couponIds") List<Integer> couponIds, @Param("operator") String operator);

    List<MtUserCoupon> queryExpireNumByGroupId(@Param("groupId") Integer groupId);

    List<Integer> getCouponIdsByUuid(@Param("uuid") String uuid);

    List<MtUserCoupon> findUserCouponDetail(@Param("couponId") Integer couponId, @Param("userId") Integer userId);

    List<MtUserCoupon> getUserCouponListByExpireTime(@Param("userId") Integer userId, @Param("status") String status, @Param("startTime") String startTime, @Param("endTime") String endTime);

}
