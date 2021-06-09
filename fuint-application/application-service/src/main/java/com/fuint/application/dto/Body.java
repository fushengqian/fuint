package com.fuint.application.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * 消息体Body信息
 * Created by zach on 2016/7/19.
 */
public class Body implements Serializable{

    private static final long serialVersionUID = 5748371865401175733L;

    private Map<String,Object> inParams;//入参信息
    private Map<String,Object> outParams;//出参信息

    public Map<String, Object> getInParams() {
        return inParams;
    }

    public void setInParams(Map<String, Object> inParams) {
        this.inParams = inParams;
    }

    public Map<String, Object> getOutParams() {
        return outParams;
    }

    public void setOutParams(Map<String, Object> outParams) {
        this.outParams = outParams;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Body{");
        sb.append("inParams=").append(inParams);
        sb.append(", outParams=").append(outParams);
        sb.append('}');
        return sb.toString();
    }
}
