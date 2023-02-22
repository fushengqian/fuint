package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtVerifyCode;

/**
 * 验证码接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface VerifyCodeService extends IService<MtVerifyCode> {
    /**
     * 添加验证码
     *
     * @param mobile
     * @param verifyCode
     * @param expireSecond 间隔秒数
     * @throws BusinessCheckException
     */
    MtVerifyCode addVerifyCode(String mobile, String verifyCode, Integer expireSecond) throws BusinessCheckException;

    /**
     * 根据手机号,验证码，查询时间
     *
     * @param mobile 电话号码
     * @param verifyCode 验证码
     * @throws BusinessCheckException
     */
    MtVerifyCode checkVerifyCode(String mobile, String verifyCode) throws BusinessCheckException;

    /**
     * 更改验证码状态
     *
     * @param id
     * @throws BusinessCheckException
     */
    MtVerifyCode updateValidFlag(Long id, String validFlag) throws BusinessCheckException;
   }