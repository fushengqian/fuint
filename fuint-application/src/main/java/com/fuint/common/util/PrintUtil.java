package com.fuint.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fuint.common.vo.printer.*;
import java.util.List;

/**
 * 云打印相关接口封装类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class PrintUtil {

    private static String BASE_URL = "https://open.xpyun.net/api/openapi";
    
    /**
     * 1.批量添加打印机
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<PrinterResult> addPrinters(AddPrinterRequest restRequest) {
        String url = BASE_URL + "/xprinter/addPrinters";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<PrinterResult> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<PrinterResult>>(){});
        return result;
    }

    /**
     * 2.设置打印机语音类型
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<Boolean> setPrinterVoiceType(SetVoiceTypeRequest restRequest) {
        String url = BASE_URL + "/xprinter/setVoiceType";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<Boolean> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<Boolean>>(){});
        return result;
    }

    /**
     * 3.打印小票订单
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<String> print(PrintRequest restRequest) {
        String url = BASE_URL + "/xprinter/print";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<String> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<String>>(){});
        return result;
    }

    /**
     * 4.打印标签订单
     *
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<String> printLabel(PrintRequest restRequest) {
        String url = BASE_URL + "/xprinter/printLabel";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<String> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<String>>(){});
        return result;
    }

    /**
     * 5.批量删除打印机
     *
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<PrinterResult> delPrinters(DelPrinterRequest restRequest) {
        String url = BASE_URL + "/xprinter/delPrinters";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<PrinterResult> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<PrinterResult>>(){});
        return result;
    }

    /**
     * 6.修改打印机信息
     *
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<Boolean> updPrinter(UpdPrinterRequest restRequest) {
        String url = BASE_URL + "/xprinter/updPrinter";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<Boolean> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<Boolean>>(){});
        return result;
    }

    /**
     * 7.清空待打印队列
     *
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<Boolean> delPrinterQueue(PrinterRequest restRequest) {
        String url = BASE_URL + "/xprinter/delPrinterQueue";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<Boolean> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<Boolean>>(){});
        return result;
    }

    /**
     * 8.查询订单是否打印成功
     *
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<Boolean> queryOrderState(QueryOrderStateRequest restRequest) {
        String url = BASE_URL + "/xprinter/queryOrderState";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<Boolean> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<Boolean>>(){});
        return result;
    }

    /**
     * 9.查询打印机某天的订单统计数
     *
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<OrderStatisResult> queryOrderStatis(QueryOrderStatisRequest restRequest) {
        String url = BASE_URL + "/xprinter/queryOrderStatis";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<OrderStatisResult> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<OrderStatisResult>>(){});
        return result;
    }

    /**
     * 10.查询打印机状态
     *
     * 0、离线 1、在线正常 2、在线不正常
     * 备注：异常一般是无纸，离线的判断是打印机与服务器失去联系超过30秒
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<Integer> queryPrinterStatus(PrinterRequest restRequest) {
        String url = BASE_URL + "/xprinter/queryPrinterStatus";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<Integer> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<Integer>>(){});
        return result;
    }

    /**
     * 11.批量查询打印机状态
     *
     * 0、离线 1、在线正常 2、在线不正常
     * 备注：异常一般是无纸，离线的判断是打印机与服务器失去联系超过30秒
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<List<Integer>> queryPrintersStatus(PrintersRequest restRequest) {
        String url = BASE_URL + "/xprinter/queryPrintersStatus";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<List<Integer>> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<List<Integer>>>(){});
        return result;
    }

    /**
     * 12.云喇叭播放语音
     *
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<String> playVoice(VoiceRequest restRequest) {
        String url = BASE_URL + "/xprinter/playVoice";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<String> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<String>>(){});
        return result;
    }

    /**
     * 13.POS指令
     *
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<String> pos(PrintRequest restRequest) {
        String url = BASE_URL + "/xprinter/pos";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<String> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<String>>(){});
        return result;
    }

    /**
     * 14.钱箱控制
     *
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<String> controlBox(PrintRequest restRequest) {
        String url = BASE_URL + "/xprinter/controlBox";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<String> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<String>>(){});
        return result;
    }

}
