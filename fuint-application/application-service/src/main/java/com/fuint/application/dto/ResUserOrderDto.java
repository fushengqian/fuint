package com.fuint.application.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 用户订单返回DTO
 * Created by zach on 2021/5/05.
 */
public class ResUserOrderDto implements Serializable {

    /**
     *  当前页
     * */
    private Integer pageNumber;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总数量
     */
    private Long totalRow;

    /**
     * 总页数
     * */
    private Integer totalPage;

    /**
     * 数据列表
     * */
    private List<UserOrderDto> content;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalRow() {
        return totalRow;
    }

    public void getTotalRow(Long totalRow) {
        this.totalRow = totalRow;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<UserOrderDto> getContent() {
        return content;
    }

    public void setContent(List<UserOrderDto> content) {
        this.content = content;
    }
}
