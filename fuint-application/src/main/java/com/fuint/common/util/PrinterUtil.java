package com.fuint.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fuint.common.vo.printer.*;
import com.fuint.framework.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * 芯烨云打印相关接口封装类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class PrinterUtil {

    private static String BASE_URL = "https://open.xpyun.net/api/openapi";

    private static final Logger logger = LoggerFactory.getLogger(PrinterUtil.class);

    /**
     * 1.批量添加打印机
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<PrinterResult> addPrinters(AddPrinterRequest restRequest) throws BusinessCheckException {
        String url = BASE_URL + "/xprinter/addPrinters";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<PrinterResult> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<PrinterResult>>(){});
        logger.info("添加打印机接口参数：{},返回：{}", JSON.toJSONString(restRequest), JSON.toJSONString(result));
        if (result == null || result.getCode() != 0 || result.getData().getSuccess() == null || result.getData().getSuccess().size() <= 0) {
            // 判断打印机是否已经存在
            String errorMsg = "添加打印机失败，发生未知错误！";
            if (result != null && result.getData() != null && result.getData().getFailMsg() != null && result.getData().getFailMsg().size() > 0) {
                String failMsg = result.getData().getFailMsg().get(0);
                if (failMsg.contains("1011")) {
                    return result;
                } else if (failMsg.contains("1001")) {
                    errorMsg = "添加打印机失败，打印机编号和用户不匹配！";
                } else if (failMsg.contains("1002")) {
                    errorMsg = "添加打印机失败，打印机未注册！";
                } else if (failMsg.contains("1003")) {
                    errorMsg = "添加打印机失败，打印机不在线！";
                } else if (failMsg.contains("1010")) {
                    errorMsg = "添加打印机失败，打印机设备编号无效！";
                } else if (failMsg.contains("1012")) {
                    errorMsg = "添加打印机失败，请稍后再试！";
                }
            }
            logger.error("云打印机新增失败，原因：", result.getMsg());
            throw new BusinessCheckException(errorMsg);
        }
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
        return JSON.parseObject(resp, new TypeReference<ObjectRestResponse<Boolean>>(){});
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

        if (result == null || result.getCode() != 0) {
            logger.error("云打印机打印失败，原因：", result.getMsg());
        }

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
        return JSON.parseObject(resp, new TypeReference<ObjectRestResponse<String>>(){});
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

        if (result == null || result.getCode() != 0) {
            logger.error("云打印机删除失败，原因：", result.getMsg());
        }

        return result;
    }

    /**
     * 6.修改打印机信息
     *
     * @param restRequest
     * @return
     */
    public static ObjectRestResponse<Boolean> updPrinter(UpdPrinterRequest restRequest) throws BusinessCheckException {
        String url = BASE_URL + "/xprinter/updPrinter";
        String jsonRequest = JSON.toJSONString(restRequest);
        String resp = HttpClientUtil.doPostJSON(url, jsonRequest);
        ObjectRestResponse<Boolean> result = JSON.parseObject(resp, new TypeReference<ObjectRestResponse<Boolean>>(){});

        if (result == null || result.getCode() != 0 ) {
            logger.error("云打印机更新失败，原因：", result.getMsg());
            throw new BusinessCheckException("修改云打印机失败，请检查设备编号是否正确！");
        }

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
        return JSON.parseObject(resp, new TypeReference<ObjectRestResponse<Boolean>>(){});
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
        return JSON.parseObject(resp, new TypeReference<ObjectRestResponse<Boolean>>(){});
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
        return JSON.parseObject(resp, new TypeReference<ObjectRestResponse<OrderStatisResult>>(){});
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
        return JSON.parseObject(resp, new TypeReference<ObjectRestResponse<Integer>>(){});
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
        return JSON.parseObject(resp, new TypeReference<ObjectRestResponse<List<Integer>>>(){});
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
        return JSON.parseObject(resp, new TypeReference<ObjectRestResponse<String>>(){});
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
        return JSON.parseObject(resp, new TypeReference<ObjectRestResponse<String>>(){});
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
        return JSON.parseObject(resp, new TypeReference<ObjectRestResponse<String>>(){});
    }

}
