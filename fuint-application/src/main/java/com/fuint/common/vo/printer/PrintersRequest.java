package com.fuint.common.vo.printer;

import java.util.List;

/**
 * 批量打印机请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class PrintersRequest extends RestRequest {

    /**
     * 打印机编号列表
     */
    private List<String> snlist;

    public List<String> getSnlist() {
        return snlist;
    }

    public void setSnlist(List<String> snlist) {
        this.snlist = snlist;
    }
}
