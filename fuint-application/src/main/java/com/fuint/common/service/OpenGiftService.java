package com.fuint.common.service;

import com.fuint.common.dto.member.OpenGiftDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.OpenGiftPage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOpenGift;

/**
 * 开卡赠礼接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface OpenGiftService {

    /**
     * 获取用户的开卡赠礼
     *
     * @param openGiftPage 查询参数
     * @return
     * */
    ResponseObject getOpenGiftList(OpenGiftPage openGiftPage);

    /**
     * 新增开卡赠礼
     *
     * @param mtOpenGift
     * @throws BusinessCheckException
     * @return
     */
    MtOpenGift addOpenGift(MtOpenGift mtOpenGift) throws BusinessCheckException;

    /**
     * 根据ID获取开卡赠礼
     *
     * @param id ID
     * @throws BusinessCheckException
     * @return
     */
    OpenGiftDto getOpenGiftDetail(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除开卡赠礼
     *
     * @param  id       ID
     * @param  accountInfo 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteOpenGift(Integer id, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 更新开卡赠礼
     *
     * @param mtOpenGift
     * @param accountInfo
     * @throws BusinessCheckException
     * @return
     * */
    MtOpenGift updateOpenGift(MtOpenGift mtOpenGift, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 开卡赠礼
     *
     * @param userId 会员ID
     * @param gradeId 会员等级
     * @param isNewMember 是否新会员
     * @throws BusinessCheckException
     * @return
     * */
    Boolean openGift(Integer userId, Integer gradeId, boolean isNewMember) throws BusinessCheckException;
}
