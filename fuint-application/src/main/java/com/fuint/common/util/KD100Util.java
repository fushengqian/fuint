package com.fuint.common.util;

import com.alibaba.fastjson.JSON;
import com.fuint.framework.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import java.util.ArrayList;
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
     * 查询物流信息
     *
     * @param com 快递公司编码
     * @param num 快递单号
     * @param key 授权密钥
     * @param customer 授权编号
     * @return
     */
    public static Map<String, Object> queryExpress(String com, String num, String key, String customer) throws BusinessCheckException {
        return queryExpress(com, num, "", key, customer);
    }

    /**
     * 查询物流信息
     *
     * @param com 快递公司编码
     * @param num 快递单号
     * @param phone 收/寄件人手机号后四位(顺丰、京东等快递必填)
     * @param key 授权密钥
     * @param customer 授权编号
     * @return
     */
    public static Map<String, Object> queryExpress(String com, String num, String phone, String key, String customer) throws BusinessCheckException {
        String url = "https://poll.kuaidi100.com/poll/query.do";

        if (com == null || com.isEmpty() || num == null || num.isEmpty()) {
            throw new BusinessCheckException("查询失败！快递公司或快递单号不能为空");
        }
        if (key == null || key.isEmpty() || customer == null || customer.isEmpty()) {
            throw new BusinessCheckException("查询失败！快递100配置不完整");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("com", com);
        param.put("num", num);
        param.put("resultv2", 1);
        // 顺丰、京东等快递要求校验手机号后四位
        if (phone != null && !phone.isEmpty()) {
            param.put("phone", phone);
        }
        String jsonPar = JSON.toJSONString(param);
        String sign = DigestUtils.md5DigestAsHex((jsonPar + key + customer).getBytes()).toUpperCase();
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("param", jsonPar);
        reqParams.put("sign", sign);
        reqParams.put("customer", customer);

        String result = HttpClientUtil.doPost(url, reqParams);
        logger.info("快递100查询结果：{}", result);

        if (result == null || result.isEmpty()) {
            throw new BusinessCheckException("查询失败！物流接口未返回结果");
        }

        Map<String, Object> resMap = null;
        try {
            resMap = JSON.parseObject(result, Map.class);
        } catch (Exception e) {
            logger.error("解析快递100返回结果失败：{}", e.getMessage());
        }
        if (resMap == null) {
            throw new BusinessCheckException("查询失败！物流接口返回结果异常");
        }

        // result=false 表示查询失败
        Object resultFlag = resMap.get("result");
        if (resultFlag != null && Boolean.parseBoolean(String.valueOf(resultFlag)) == false) {
            Object message = resMap.get("message");
            throw new BusinessCheckException("查询失败！" + (message == null ? "" : message.toString()));
        }

        // 部分异常场景不返回 result 字段，以 returnCode 区分
        Object returnCode = resMap.get("returnCode");
        if (returnCode != null && !"200".equals(String.valueOf(returnCode))) {
            Object message = resMap.get("message");
            throw new BusinessCheckException("查询失败！" + (message == null ? "" : message.toString()));
        }

        if ("ok".equals(String.valueOf(resMap.get("message")))) {
            Map<String, Object> resultMap = new HashMap<>();
            List<Map<String, Object>> jList = (List<Map<String, Object>>) resMap.get("data");
            if (jList == null) {
                jList = new ArrayList<>();
            }
            resultMap.put("data", jList);
            resultMap.put("ischeck", resMap.get("ischeck")); //是否已签收
            resultMap.put("ship_no", resMap.get("nu")); //快递单号
            resultMap.put("state", resMap.get("state")); //物流状态
            return resultMap;
        }

        throw new BusinessCheckException("查询失败！");
    }
}