package com.fuint.application.service.message;

import com.fuint.application.dao.entities.MtMessage;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;

/**
 * 消息业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface MessageService {

    /**
     * 分页查询消息列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtMessage> queryMessageListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

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
     * @param msg_id
     * @throws BusinessCheckException
     */
    void readMessage(Integer msg_id) throws BusinessCheckException;

    /**
     * 获取最新一条未读消息
     *
     * @throws BusinessCheckException
     */
    MtMessage getOne(Integer userId) throws BusinessCheckException;
}
