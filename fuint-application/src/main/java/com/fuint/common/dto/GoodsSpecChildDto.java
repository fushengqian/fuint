package com.fuint.common.dto;

import java.io.Serializable;

public class GoodsSpecChildDto implements Serializable {

   /**
    * 自增ID
    * */
   private Integer id;

   /**
    * 规格名称 
    */
   private String name;

   /**
    * 是否选择
    */
    private boolean checked;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
    this.name=name;
    }
    public boolean getChecked(){
        return checked;
    }
    public void setChecked(boolean checked){
    this.checked=checked;
    }
}

