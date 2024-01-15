package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtVerifyCode;

/**
 * 图形验证码接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface VerifyCodeService extends IService<MtVerifyCode> {

    /**
     * 新增验证码
     *
     * @param mobile 手机号
     * @param verifyCode 验证码
     * @param expireSecond 间隔秒数
     * @throws BusinessCheckException
     * @return
     */
    MtVerifyCode addVerifyCode(String mobile, String verifyCode, Integer expireSecond) throws BusinessCheckException;

    /**
     * 根据手机号,验证码，查询时间
     *
     * @param mobile 电话号码
     * @param verifyCode 验证码
     * @throws BusinessCheckException
     * @return
     */
    MtVerifyCode checkVerifyCode(String mobile, String verifyCode) throws BusinessCheckException;

    /**
     * 更改验证码状态
     *
     * @param id 验证码ID
     * @param validFlag 是否验证
     * @throws BusinessCheckException
     * @return
     */
    MtVerifyCode updateValidFlag(Long id, String validFlag) throws BusinessCheckException;
}