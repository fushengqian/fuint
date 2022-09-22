package com.fuint.application.service.message;

import com.fuint.application.dao.entities.MtMessage;
import com.fuint.application.dao.repositories.MtMessageRepository;
import com.fuint.application.enums.MessageEnum;
import com.fuint.application.enums.StatusEnum;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 消息业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MtMessageRepository messageRepository;

    /**
     * 添加消息
     *
     * @param  mtMsg
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    public void addMessage(MtMessage mtMsg) {
        if (mtMsg.getUserId() < 0 || StringUtil.isEmpty(mtMsg.getContent())) {
            return;
        }

        mtMsg.setStatus(StatusEnum.ENABLED.getKey());
        mtMsg.setIsRead("N");
        mtMsg.setCreateTime(new Date());
        mtMsg.setUpdateTime(new Date());

        messageRepository.save(mtMsg);
    }

    /**
     * 置为已读
     *
     * @param  msgId
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    public void readMessage(Integer msgId) {
        if (msgId < 0) {
            return;
        }

        MtMessage mtMsg = messageRepository.findOne(msgId);
        if (mtMsg == null) {
            return;
        }

        mtMsg.setIsRead("Y");
        mtMsg.setUpdateTime(new Date());

        messageRepository.save(mtMsg);
    }

    /**
     * 置为发送
     *
     * @param  msgId
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    public void sendMessage(Integer msgId, boolean isRead) {
        if (msgId < 0 ) {
            return;
        }

        MtMessage mtMsg = messageRepository.findOne(msgId);
        if (mtMsg == null) {
            return;
        }

        mtMsg.setIsSend("Y");

        // 订阅消息发送成功就算是已读了
        if (isRead) {
            mtMsg.setIsRead("Y");
        } else {
            mtMsg.setIsRead("N");
        }

        mtMsg.setUpdateTime(new Date());

        messageRepository.save(mtMsg);
    }

    /**
     * 获取最新一条未读弹框消息
     *
     * @throws BusinessCheckException
     */
    @Override
    public MtMessage getOne(Integer userId) {
        List<MtMessage> messageList = messageRepository.findNewMessage(userId, MessageEnum.POP_MSG.getKey());

        if (messageList.size() > 0) {
            MtMessage messageInfo = messageList.get(0);
            return messageInfo;
        }

        return null;
    }

    /**
     * 获取需要发送的消息
     *
     * @throws BusinessCheckException
     */
    @Override
    public List<MtMessage> getNeedSendList() {
        List<MtMessage> messageList = messageRepository.findNeedSendMessage(MessageEnum.SUB_MSG.getKey());
        return messageList;
    }
}
