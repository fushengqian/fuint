package com.fuint.common.vo.printer;

/**
 * 添加打印机请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class AddPrinterRequest extends RestRequest {

    public AddPrinterRequestItem[] getItems() {
        return items;
    }

    public void setItems(AddPrinterRequestItem[] items) {
        this.items = items;
    }

    /**
     * 请求项集合
     */
    private AddPrinterRequestItem[] items;

}
