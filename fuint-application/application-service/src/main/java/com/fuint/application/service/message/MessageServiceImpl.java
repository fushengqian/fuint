package com.fuint.application.service.message;

import com.fuint.application.dao.entities.MtMessage;
import com.fuint.application.dao.repositories.MtMessageRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private MtMessageRepository messageRepository;

    /**
     * 分页查询积分列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtMessage> queryMessageListByPagination(PaginationRequest paginationRequest) {
        PaginationResponse<MtMessage> paginationResponse = messageRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加消息
     *
     * @param mtMsg
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    public void addMessage(MtMessage mtMsg) {
        if (mtMsg.getUserId() < 0 || StringUtils.isEmpty(mtMsg.getContent())) {
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
     * @param msg_id
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    public void readMessage(Integer msg_id) {
        if (msg_id < 0 ) {
            return;
        }

        MtMessage mtMsg = messageRepository.findOne(msg_id);
        if (mtMsg == null) {
            return;
        }

        mtMsg.setIsRead("Y");
        mtMsg.setUpdateTime(new Date());

        messageRepository.save(mtMsg);
    }

    /**
     * 获取最新一条未读消息
     *
     * @throws BusinessCheckException
     */
    @Override
    public MtMessage getOne(Integer userId) {
        List<MtMessage> messageList = messageRepository.findNewMessage(userId);
        if (messageList.size() > 0) {
            MtMessage messageInfo = messageList.get(0);
            return messageInfo;
        }
        return null;
    }
}
