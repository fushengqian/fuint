package com.fuint.coupon.service.verifycode;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.MtVerifyCode;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 店铺业务接口
 * Created by zach 20190820
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
    public MtVerifyCode addVerifyCode(String mobile, String verifycode,Integer expireSecond) throws BusinessCheckException;



    /**
     * 根据手机号,验证码，查询时间
     *
     * @param mobile 电话号码
     * @param verifycode 验证码
     * @throws BusinessCheckException
     */
    public MtVerifyCode checkVerifyCode(String mobile, String verifycode) throws BusinessCheckException;


    /**
     * 更改验证码状态
     *
     * @param id
     * @throws BusinessCheckException
     */
    public MtVerifyCode updateValidFlag(Long id, String validFlag) throws BusinessCheckException;

   }
