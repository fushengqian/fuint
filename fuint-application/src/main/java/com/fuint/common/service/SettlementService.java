package com.fuint.common.service;

import com.fuint.framework.exception.BusinessCheckException;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 订单结算相关业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface SettlementService {

    /**
     * 订单提交结算
     * */
    Map<String, Object> doSubmit(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException;

}