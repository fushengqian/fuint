package com.fuint.application.dto;

/**
 * 小程序订阅消息dto
 * Created by FSQ
 * Contact wx fsq_better
 */
public class SubMessageDto {
    private String key;
    private String templateId;
    private String title;
    private String content;
    private String status;

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
}
