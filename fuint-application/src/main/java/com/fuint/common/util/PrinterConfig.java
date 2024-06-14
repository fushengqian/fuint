package com.fuint.common.util;

import com.fuint.common.vo.printer.RestRequest;

/**
 * 云打印公共配置类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class PrinterConfig {

    /**
     * *必填*：开发者ID ：芯烨云后台注册账号（即邮箱地址或开发者ID），开发者用户注册成功之后，登录芯烨云后台，在【个人中心=》账号信息】下可查看开发者ID
     *
     * 当前【xxxxxxxxxxxxxxx】只是样例，需修改再使用
     */
    public static final String USER_NAME = "xxxxxxxxxxxxxxx";

    /**
     * *必填*：开发者密钥 ：芯烨云后台注册账号后自动生成的开发者密钥，开发者用户注册成功之后，登录芯烨云后台，在【个人中心=》账号信息】下可查看开发者密钥
     *
     * 当前【xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx】只是样例，需修改再使用
     */
    public static final String USER_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    /**
     * *必填*：打印机设备编号，必须要在芯烨云管理后台的【打印管理->打印机管理】下添加打印机或调用API接口添加打印机，测试小票机和标签机的时候注意替换打印机编号
     * 打印机设备编号获取方式：在打印机底部会有带PID或SN字样的二维码且PID或SN后面的一串字符即为打印机编号
     *
     * 当前【xxxxxxxxxxxxxxx】只是样例，需修改再使用，此处只是作为测试样例，所以打印机编号采用常量化，开发者可根据自己的实际需要进行变量化
     */
    public static final String OK_PRINTER_SN = "xxxxxxxxxxxxxxx";

    /**
     * 生成通用的请求头
     *
     * @param request 所有请求都必须传递的参数。
     */
    public static void createRequestHeader(RestRequest request) {
        //*必填*：芯烨云平台注册用户名（开发者 ID）
        request.setUser(USER_NAME);
        //*必填*：当前UNIX时间戳
        request.setTimestamp(System.currentTimeMillis() + "");
        //*必填*：对参数 user + UserKEY + timestamp 拼接后（+号表示连接符）进行SHA1加密得到签名，值为40位小写字符串，其中 UserKEY 为用户开发者密钥
        request.setSign(HashSignUtil.sign(request.getUser() + USER_KEY + request.getTimestamp()));

        //debug=1返回非json格式的数据，仅测试时候使用
        request.setDebug("0");
    }
}
