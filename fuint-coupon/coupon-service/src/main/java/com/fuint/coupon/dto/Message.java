package com.fuint.coupon.dto;

import java.io.Serializable;

/**
 * 消息结构定义
 * Created by wang.yq on 2016/7/19.
 */
public class Message implements Serializable{
    private static final long serialVersionUID = -7956811510222855939L;

    private Head head;//消息头信息
    private Page page;//消息分页信息
    private Body body;//消息体信息

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("head=").append(head);
        sb.append(", page=").append(page);
        sb.append(", body=").append(body);
        sb.append('}');
        return sb.toString();
    }
}
