package com.fuint.coupon;

import org.springframework.util.CollectionUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zach on 19/8/19.
 */
public class BaseController {

    /**
     * 获取成功返回结果
     *
     * @param data
     * @return
     */
    public ResponseObject getSuccessResult(Object data) {
        return new ResponseObject(FrameworkConstants.HTTP_RESPONSE_CODE_SUCCESS, "请求成功", data);
    }

    public ResponseObject getSuccessResultList(List data) {
        if (CollectionUtils.isEmpty(data)) {
            return new ResponseObject(FrameworkConstants.HTTP_RESPONSE_CODE_SUCCESS, "请求成功", Collections.emptyList());
        }
        return new ResponseObject(FrameworkConstants.HTTP_RESPONSE_CODE_SUCCESS, "请求成功", data);
    }

    /**
     * 获取错返回结果(无参数替换)
     *
     * @param errorCode
     * @return
     */
    public ResponseObject getFailureResult(int errorCode) {
        return new ResponseObject(errorCode, PropertiesUtil.getResponseErrorMessageByCode(errorCode), null);
    }

    /**
     * 获取错返回结果(带参数替换)
     *
     * @param errorCode
     * @param message
     * @return
     */
    public ResponseObject getFailureResult(int errorCode, String message) {
        return new ResponseObject(errorCode, message, null);
    }

    public Object getFromSession(String key, HttpServletRequest request) {
        return request.getSession().getAttribute(key);
    }

    public void setSession(String key, Object object, HttpServletRequest request) {
        request.getSession().setAttribute(key, object);
    }

    public void removeSession(String key, HttpServletRequest request) {
        request.getSession().removeAttribute(key);
    }

    protected static Map<String, Object> getRequestMap(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Map<String, String[]> map2 = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : map2.entrySet()) {
            map.put(entry.getKey(), entry.getValue()[0]);
        }
        return map;
    }

    public ResponseObject getCustomrResult(int code, String msg, Object data) {
        return new ResponseObject(code, msg, data);
    }
}