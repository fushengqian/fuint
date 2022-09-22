package com.fuint.application.dto;

/**
 * 参数dto
 * Created by FSQ
 * Contact wx fsq_better
 */
public class ParamDto {
    private String key;
    private String name;
    private String value;

    public String getKey(){
        return key;
    }
    public void setKey(String key){
        this.key = key;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }
}
