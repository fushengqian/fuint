package com.fuint.repository.mapper;

import com.fuint.repository.model.MtStaff;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 店铺员工表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtStaffMapper extends BaseMapper<MtStaff> {

    int updateStatus(@Param("id") Integer id, @Param("status") String status, @Param("updateTime") Date updateTime);

    MtStaff queryStaffByMobile(@Param("mobile") String mobile);

    MtStaff queryStaffByUserId(@Param("userId") Integer userId);
}
