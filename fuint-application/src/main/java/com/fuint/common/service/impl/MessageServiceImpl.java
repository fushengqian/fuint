package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.MessageEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.MessageService;
import com.fuint.repository.mapper.MtMessageMapper;
import com.fuint.repository.model.MtMessage;
import com.fuint.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 消息业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MtMessageMapper, MtMessage> implements MessageService {

    private MtMessageMapper messageRepository;

    /**
     * 添加消息
     *
     * @param mtMsg 消息参数
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMessage(MtMessage mtMsg) {
        if (mtMsg.getUserId() < 0 || StringUtil.isEmpty(mtMsg.getContent())) {
            return;
        }

        mtMsg.setStatus(StatusEnum.ENABLED.getKey());
        mtMsg.setIsRead(YesOrNoEnum.NO.getKey());
        mtMsg.setCreateTime(new Date());
        mtMsg.setUpdateTime(new Date());

        this.save(mtMsg);
    }

    /**
     * 将消息置为已读
     *
     * @param  msgId 消息ID
     * @param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readMessage(Integer msgId) {
        if (msgId < 0) {
            return;
        }

        MtMessage mtMsg = messageRepository.selectById(msgId);
        if (mtMsg == null) {
            return;
        }

        mtMsg.setIsRead(YesOrNoEnum.YES.getKey());
        mtMsg.setUpdateTime(new Date());

        messageRepository.updateById(mtMsg);
    }

    /**
     * 消息置为发送
     *
     * @param msgId 消息ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(Integer msgId, boolean isRead) {
        if (msgId < 0 ) {
            return;
        }

        MtMessage mtMsg = messageRepository.selectById(msgId);
        if (mtMsg == null) {
            return;
        }

        mtMsg.setIsSend(YesOrNoEnum.YES.getKey());

        // 订阅消息发送成功就算是已读了
        if (isRead) {
            mtMsg.setIsRead(YesOrNoEnum.YES.getKey());
        } else {
            mtMsg.setIsRead(YesOrNoEnum.NO.getKey());
        }

        mtMsg.setUpdateTime(new Date());

        messageRepository.updateById(mtMsg);
    }

    /**
     * 获取最新一条未读弹框消息
     *
     * @param userId 会员ID
     * @return
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
     * @return
     */
    @Override
    public List<MtMessage> getNeedSendList() {
        List<MtMessage> messageList = messageRepository.findNeedSendMessage(MessageEnum.SUB_MSG.getKey());
        return messageList;
    }
}
