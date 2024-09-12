package com.fuint.common.util;

import com.alibaba.fastjson.JSON;
import com.fuint.framework.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 快递100查询工具
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class KD100Util {

    private static final Logger logger = LoggerFactory.getLogger(KD100Util.class);

    /**
     * 查询订单信息
     *
     * @param com 快递公司
     * @param num 快递单号
     * @param key
     * @param customer
     * @return
     */
    public static Map<String, Object> queryExpress(String com, String num, String key, String customer) throws BusinessCheckException {
        String url = "https://poll.kuaidi100.com/poll/query.do";

        Map param = new HashMap();
        param.put("com", com);
        param.put("num", num);
        param.put("resultv2", 1);
        String jsonPar = JSON.toJSONString(param);
        logger.info(jsonPar);
        String sign = DigestUtils.md5DigestAsHex((jsonPar + key + customer).getBytes()).toUpperCase();
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("param", jsonPar);
        reqParams.put("sign", sign);
        reqParams.put("customer", customer);
        logger.info(reqParams.toString());
        String result = HttpClientUtil.doPost(url, reqParams);

        logger.info(result);

        Map<String, Object> resMap = JSON.parseObject(result, Map.class);
        if (resMap.get("result") != null && (boolean) resMap.get("result") == false) {
            throw new BusinessCheckException("查询失败！"+resMap.get("message"));
        }

        if ("ok".equals(resMap.get("message"))) {
            Map<String, Object> resultMap = new HashMap<>();
            List<Map<String, Object>> jList = (List<Map<String, Object>>) resMap.get("data");
            resultMap.put("data", jList);
            resultMap.put("ischeck", resMap.get("ischeck")); //是否已签收
            resultMap.put("ship_no", resMap.get("nu")); //订单号
            resultMap.put("state", resMap.get("state")); //订单状态
            return resultMap;
        }

        throw new BusinessCheckException("查询失败！");
    }
}