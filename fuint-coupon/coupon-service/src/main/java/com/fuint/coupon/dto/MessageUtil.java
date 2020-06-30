package com.fuint.coupon.dto;

/**
 * Created by wang.yq on 2016/7/25.
 */
public class MessageUtil {

    /**
     * 创建空消息体
     * @return Message
     */
    public static Message createEmptyMessage(){
        Head head = new Head();
        Body body = new Body();
        Message message = new Message();
        message.setHead(head);
        message.setBody(body);

        return message;
    }

    /**
     * 创建空消息体
     * @return Message
     */
    public static Message createEmptyBodyMessage(){
        Body body = new Body();
        Message message = new Message();
        message.setBody(body);

        return message;
    }

    /**
     * 更新消息返回状态
     * @param message
     * @param returnCode
     * @param returnDesc
     */
    public static void UpdateMessageStatus(Message message, String returnCode, String returnDesc){
        Head head = message.getHead();
        head.setReturnCode(returnCode);
        head.setReturnDesc(returnDesc);
    }

    /**
     * 更新消息返回状态
     * @param message
     * @param messageStatus
     */
    public static void UpdateMessageStatus(Message message, MessageStatusEnum messageStatus){
        Head head = message.getHead();
        head.setReturnCode(messageStatus.getReturnCode());
        head.setReturnDesc(messageStatus.getReturnDesc());
    }
}
