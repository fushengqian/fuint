package com.fuint.common.vo.printer;

/**
 * 设置打印机语音类型请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class SetVoiceTypeRequest extends RestRequest {

    /**
     * 打印机编号
     */
    private String sn;

    /**
     * 声音类型： 0真人语音（大） 1真人语音（中） 2真人语音（小） 3 嘀嘀声  4 静音
     */
    private Integer voiceType;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(Integer voiceType) {
        this.voiceType = voiceType;
    }
}
