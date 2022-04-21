package com.fuint.application.dto;

import java.util.List;

public class RegionDto {

    private Integer id;

    private String name;

    private Integer pid;

    private String code;

    private String level;

    private List<RegionDto> city;

    private List<RegionDto> region;

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
    public Integer getPid(){
        return pid;
    }
    public void setPid(Integer pid){
        this.pid=pid;
    }
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
    public String getLevel(){
        return level;
    }
    public void setLevel(String level){
        this.level=level;
    }
    public List<RegionDto> getCity() {
       return city;
    }
    public void setCity(List<RegionDto> city) {
        this.city = city;
    }
    public List<RegionDto> getRegion() {
        return region;
    }
    public void setRegion(List<RegionDto> region) {
        this.region = region;
    }
}
