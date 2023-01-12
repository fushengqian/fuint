package com.fuint.common.service;

import com.fuint.common.dto.OpenGiftDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOpenGift;

import java.util.Map;

/**
 * 开卡赠礼接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface OpenGiftService {

    /**
     * 获取用户的开卡赠礼
     * @param paramMap 查询参数
     * @throws BusinessCheckException
     * */
    ResponseObject getOpenGiftList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 新增开卡赠礼
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtOpenGift addOpenGift(MtOpenGift reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取开卡赠礼
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    OpenGiftDto getOpenGiftDetail(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除开卡赠礼
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteOpenGift(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新订单
     * @param reqDto
     * @throws BusinessCheckException
     * */
    MtOpenGift updateOpenGift(MtOpenGift reqDto) throws BusinessCheckException;

    /**
     * 开卡赠礼
     * @param userId
     * @param gradeId
     * @throws BusinessCheckException
     * */
    void openGift(Integer userId, Integer gradeId) throws BusinessCheckException;
}
