package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtUserTag;

import java.util.List;

/**
 * 会员标签服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface UserTagService extends IService<MtUserTag> {

    /**
     * 获取商户标签列表
     *
     * @param merchantId 商户ID
     * @param status 状态
     * @return
     */
    List<MtUserTag> getMerchantTagList(Integer merchantId, String status);

    /**
     * 添加标签
     *
     * @param mtUserTag 标签信息
     * @param merchantId 当前商户ID
     * @return
     * @throws BusinessCheckException
     */
    MtUserTag addTag(MtUserTag mtUserTag, Integer merchantId) throws BusinessCheckException;

    /**
     * 编辑标签
     *
     * @param mtUserTag 标签信息
     * @param merchantId 当前商户ID
     * @return
     * @throws BusinessCheckException
     */
    MtUserTag updateTag(MtUserTag mtUserTag, Integer merchantId) throws BusinessCheckException;

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @param accountInfo 当前登录账号信息
     * @throws BusinessCheckException
     */
    void deleteTag(Integer id, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return
     */
    MtUserTag getTagById(Integer id);
}
