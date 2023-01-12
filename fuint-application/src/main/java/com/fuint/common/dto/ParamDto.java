package com.fuint.common.dto;

/**
 * 请求参数实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
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
