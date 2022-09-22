package com.fuint.application.service.message;

import com.fuint.application.dao.entities.MtMessage;
import com.fuint.exception.BusinessCheckException;
import java.util.List;

/**
 * 消息业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface MessageService {

    /**
     * 添加消息
     *
     * @param reqMsgDto
     * @throws BusinessCheckException
     */
    void addMessage(MtMessage reqMsgDto) throws BusinessCheckException;

    /**
     * 置为已读
     *
     * @param msgId
     * @throws BusinessCheckException
     */
    void readMessage(Integer msgId) throws BusinessCheckException;

    /**
     * 置为发送
     *
     * @param  msgId
     * @throws BusinessCheckException
     */
    void sendMessage(Integer msgId, boolean isRead) throws BusinessCheckException;

    /**
     * 获取最新一条未读消息
     *
     * @throws BusinessCheckException
     */
    MtMessage getOne(Integer userId) throws BusinessCheckException;

    /**
     * 获取需要发送的消息
     * @throws BusinessCheckException
     * */
    List<MtMessage> getNeedSendList() throws BusinessCheckException;
}
