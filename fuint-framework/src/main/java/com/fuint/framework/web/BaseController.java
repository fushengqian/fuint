package com.fuint.framework.web;

import com.fuint.framework.FrameworkConstants;
import com.fuint.utils.PropertiesUtil;

/**
 * 控制器基类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class BaseController {

    /**
     * 获取成功返回结果
     *
     * @param data
     * @return
     */
    public ResponseObject getSuccessResult(Object data) {
        return new ResponseObject(FrameworkConstants.HTTP_RESPONSE_CODE_SUCCESS, "操作成功", data);
    }

    /**
     * 获取成功返回结果
     *
     * @param message
     * @param data
     * @return
     */
    public ResponseObject getSuccessResult(String message, Object data) {
        return new ResponseObject(FrameworkConstants.HTTP_RESPONSE_CODE_SUCCESS, message, data);
    }

    /**
     * 获取成功返回结果
     *
     * @param code
     * @param message
     * @param data
     * @return
     */
    public ResponseObject getSuccessResult(int code, String message, Object data) {
        return new ResponseObject(code, message, data);
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

    /**
     * 获取错返回结果(带参数替换)
     *
     * @param errorCode
     * @param message
     * @return
     */
    public ResponseObject getFailureResult(int errorCode, String message, Object data) {
        return new ResponseObject(errorCode, message, data);
    }
}