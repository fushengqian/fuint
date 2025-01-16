package com.fuint.repository.mapper;

import com.fuint.repository.bean.UploadShippingLogBean;
import com.fuint.repository.model.MtUploadShippingLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 微信小程序上传发货信息 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtUploadShippingLogMapper extends BaseMapper<MtUploadShippingLog> {

   List<UploadShippingLogBean> getUploadShippingLogList(@Param("merchantId") Integer merchantId);

}
