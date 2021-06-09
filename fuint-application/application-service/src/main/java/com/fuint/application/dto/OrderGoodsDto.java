package com.fuint.application.dto;

import java.io.Serializable;

public class OrderGoodsDto implements Serializable {
	private Integer id;

	private String name;

	private String price;

	private Integer num;

	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id=id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}

	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
}

