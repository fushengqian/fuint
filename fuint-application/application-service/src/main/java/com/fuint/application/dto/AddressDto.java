package com.fuint.application.dto;

public class AddressDto {

    private Integer id;

    private Integer userId;

    private String name;

    private String mobile;

    private Integer provinceId;

    private String provinceName;

    private Integer cityId;

    private String cityName;

    private Integer regionId;

    private String regionName;

    private String detail;

    private String isDefault;

    private String status;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id=id;
    }
    public Integer getUserId(){
        return userId;
    }
    public void setUserId(Integer userId){
        this.userId=userId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
        this.mobile=mobile;
    }
    public Integer getProvinceId(){
        return provinceId;
    }
    public void setProvinceId(Integer provinceId){
        this.provinceId=provinceId;
    }
    public String getProvinceName(){
        return provinceName;
    }
    public void setProvinceName(String provinceName){
        this.provinceName=provinceName;
    }
    public Integer getCityId(){
        return cityId;
    }
    public void setCityId(Integer cityId){
        this.cityId=cityId;
    }
    public String getCityName(){
        return cityName;
    }
    public void setCityName(String cityName){
        this.cityName=cityName;
    }
    public Integer getRegionId(){
        return regionId;
    }
    public void setRegionId(Integer regionId){
        this.regionId=regionId;
    }
    public String getRegionName(){
        return regionName;
    }
    public void setRegionName(String regionName){
        this.regionName=regionName;
    }
    public String getDetail(){
        return detail;
    }
    public void setDetail(String detail){
        this.detail=detail;
    }
    public String getIsDefault(){
        return isDefault;
    }
    public void setIsDefault(String isDefault){
        this.isDefault=isDefault;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
}
