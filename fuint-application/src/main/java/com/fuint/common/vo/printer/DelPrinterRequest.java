package com.fuint.common.vo.printer;

/**
 * 删除打印机请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class DelPrinterRequest extends RestRequest {

    /**
     * 打印机编号集合
     */
    private String[] snlist;

    public String[] getSnlist() {
        return snlist;
    }

    public void setSnlist(String[] snlist) {
        this.snlist = snlist;
    }
}
