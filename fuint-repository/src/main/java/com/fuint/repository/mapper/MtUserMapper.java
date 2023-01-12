package com.fuint.repository.mapper;

import com.fuint.repository.model.MtUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 会员个人信息 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtUserMapper extends BaseMapper<MtUser> {

    List<MtUser> queryMemberByMobile(@Param("mobile") String mobile);

    List<MtUser> queryMemberByName(@Param("name") String name);

    MtUser queryMemberByOpenId(@Param("openId") String openId);

    List<MtUser> findMembersByUserNo(@Param("userNo") String userNo);

    void updateActiveTime(@Param("userId") Integer userId, @Param("updateTime") Date updateTime);

    void resetMobile(@Param("mobile") String mobile, @Param("userId") Integer userId);

    Long getUserCount();

    Long getStoreUserCount(@Param("storeId") Integer storeId);

    Long getUserCountByTime(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    Long getStoreUserCountByTime(@Param("storeId") Integer storeId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);
}
