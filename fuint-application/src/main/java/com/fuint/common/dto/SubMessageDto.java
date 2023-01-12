package com.fuint.common.dto;

import java.util.List;

/**
 * 小程序订阅消息dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class SubMessageDto {
    private String key;
    private String templateId;
    private String tid;
    private String title;
    private String content;
    private String status;
    private List<ParamDto> params;

    public String getKey(){
        return key;
    }
    public void setKey(String key){
        this.key = key;
    }

    public String getTemplateId(){
        return templateId;
    }
    public void setTemplateId(String templateId){
        this.templateId = templateId;
    }

    public String getTid(){
        return tid;
    }
    public void setTid(String tid){
        this.tid = tid;
    }

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }

    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }

    public List<ParamDto> getParams(){
        return params;
    }
    public void setParams(List<ParamDto> params){
        this.params = params;
    }
}
