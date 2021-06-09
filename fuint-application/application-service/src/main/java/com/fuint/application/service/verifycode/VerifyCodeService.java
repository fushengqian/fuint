package com.fuint.application.service.verifycode;

import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtVerifyCode;

/**
 * 验证码接口
 * Created by zach 2019/08/20
 */
public interface VerifyCodeService {
    /**
     * 添加验证码
     *
     * @param mobile
     * @param verifycode
     * @param expireSecond 间隔秒数
     * @throws BusinessCheckException
     */
    MtVerifyCode addVerifyCode(String mobile, String verifycode,Integer expireSecond) throws BusinessCheckException;

    /**
     * 根据手机号,验证码，查询时间
     *
     * @param mobile 电话号码
     * @param verifycode 验证码
     * @throws BusinessCheckException
     */
    MtVerifyCode checkVerifyCode(String mobile, String verifycode) throws BusinessCheckException;

    /**
     * 更改验证码状态
     *
     * @param id
     * @throws BusinessCheckException
     */
    MtVerifyCode updateValidFlag(Long id, String validFlag) throws BusinessCheckException;
   }