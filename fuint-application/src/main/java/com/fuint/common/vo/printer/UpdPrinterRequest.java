package com.fuint.common.vo.printer;

/**
 * 修改打印机请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class UpdPrinterRequest extends RestRequest {

    /**
     * 打印机编号
     */
    private String sn;
    /**
     * 打印机名称
     */
    private String name;
    /**
     * 打印机识别码
     */
    private String idcode;
    /**
     * 流量卡号码
     */
    private String cardno;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcode() {
        return idcode;
    }

    public void setIdcode(String idcode) {
        this.idcode = idcode;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }
}
