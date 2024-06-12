package com.fuint.common.vo.printer;

/**
 * 打印机状态
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum PrinterStatusType {

    /**
     * 离线
     */
    Offline(0),
    /**
     * 在线正常
     */
    OnlinNormal(1),
    /**
     * 在线缺纸
     */
    OnlineMissingPaper(2);

    private final int val;

    public int getVal() {
        return val;
    }

    private PrinterStatusType(int num) {
        this.val = num;
    }

    public static PrinterStatusType getPrinterStatusType(int val) {
        for (PrinterStatusType type : PrinterStatusType.values()) {
            if (type.getVal() == val) {
                return type;
            }
        }
        return Offline;
    }

}
