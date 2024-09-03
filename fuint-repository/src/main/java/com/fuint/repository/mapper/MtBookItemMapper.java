package com.fuint.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuint.repository.model.MtBookItem;
import org.apache.ibatis.annotations.Param;

/**
 *  预约订单 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtBookItemMapper extends BaseMapper<MtBookItem> {

    Integer getBookNum(@Param("bookId") Integer bookId, @Param("date") String date, @Param("time") String time);

}
