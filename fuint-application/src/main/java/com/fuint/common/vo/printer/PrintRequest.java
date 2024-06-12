package com.fuint.common.vo.printer;

/**
 * 打印机打印请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class PrintRequest extends RestRequest {

    /**
     * 打印机编号
     */
    private String sn;

    /**
     * 打印内容,不能超过5000字节
     */
    private String content;

    /**
     * 打印份数，默认为1
     */
    private int copies = 1;

    /**
     * 打印模式，默认为0
     */
    private int mode = 0;

    /**
     * 支付方式41~55：支付宝 微信 ...
     */
    private Integer payType;
    /**
     * 支付与否59~61：退款 到账 消费
     */
    private Integer payMode;
    /**
     * 支付金额
     */
    private Double money;
    /**
     * 声音播放模式，0 为取消订单模式，1 为静音模式，2 为来单播放模式，默认为 2 来单播放模式
     */
    private Integer voice;

    /**
     * 打印接口回调地址对应标识(取值范围 [ 1 - 5 ] 的整数)，对于web管理后台 “功能设置” 菜单设置界面的打印接口回调标识。
     */
    private Integer backurlFlag;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPayMode() {
        return payMode;
    }

    public void setPayMode(Integer payMode) {
        this.payMode = payMode;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getVoice() {
        return voice;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }

    public Integer getBackurlFlag() {
        return backurlFlag;
    }

    public void setBackurlFlag(Integer backurlFlag) {
        this.backurlFlag = backurlFlag;
    }
}
