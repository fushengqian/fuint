package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.service.VerifyCodeService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.exception.BusinessRuntimeException;
import com.fuint.repository.mapper.MtVerifyCodeMapper;
import com.fuint.repository.model.MtVerifyCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 验证码业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class VerifyCodeServiceImpl extends ServiceImpl<MtVerifyCodeMapper, MtVerifyCode> implements VerifyCodeService {

    @Resource
    private MtVerifyCodeMapper mtVerifyCodeMapper;

    /**
     * 添加验证码
     *
     * @param mobile
     * @param verifyCode
     * @throws BusinessCheckException
     */
    public MtVerifyCode addVerifyCode(String mobile, String verifyCode, Integer expireSecond) {
        if (null == expireSecond || expireSecond<0) {
            expireSecond=0;
        }

        MtVerifyCode reqVerifyCodeDto = new MtVerifyCode();
        reqVerifyCodeDto.setMobile(mobile);
        reqVerifyCodeDto.setVerifyCode(verifyCode);
        reqVerifyCodeDto.setValidFlag("0");

        Date addTime = new Date();
        reqVerifyCodeDto.setAddTime(addTime);

        // 验证码过期时间5分钟
        addTime.setTime(addTime.getTime()+5*60*1000);
        Date expireTime = addTime;
        reqVerifyCodeDto.setExpireTime(expireTime);

        // 发送验证码2分钟后才能继续发送,取最后一条
        List<MtVerifyCode> verifyCodeList = mtVerifyCodeMapper.queryVerifyCodeLastRecord(mobile);
        if (null == verifyCodeList || verifyCodeList.size() == 0) {
            // 没发过短信
            this.save(reqVerifyCodeDto);
            return reqVerifyCodeDto;
        }
        MtVerifyCode verifyCodeLastRecord = verifyCodeList.get(0);
        Long curInt = reqVerifyCodeDto.getAddTime().getTime(); //时间毫秒,长整型
        Long lastInt = verifyCodeLastRecord.getAddTime().getTime();
        Integer diffSecond = (int)((curInt-lastInt) / 1000); //间隔秒数
        if (diffSecond<expireSecond) {
            reqVerifyCodeDto.setValidFlag("1");
            return reqVerifyCodeDto;
        }

        // 2,同一个手机号码新的验证码插入
        this.save(reqVerifyCodeDto);

        return reqVerifyCodeDto;
    }

    /**
     * 更改验证码状态
     *
     * @param id
     * @param validFlag
     * @throws BusinessCheckException
     */
    @Override
    public MtVerifyCode updateValidFlag(Long id, String validFlag)  throws BusinessCheckException {

        MtVerifyCode mtVerifyCode = mtVerifyCodeMapper.selectById(id);
        if (mtVerifyCode == null) {
            throw new BusinessCheckException("验证码不存在");
        }

        mtVerifyCode.setValidFlag(validFlag);
        mtVerifyCode.setUsedTime(new Date());
        mtVerifyCodeMapper.updateById(mtVerifyCode);

        return mtVerifyCode;
    }


    /**
     * 根据手机号,验证码，查询时间
     *
     * @param mobile 电话号码
     * @param verifycode 验证码
     * @throws BusinessCheckException
     */
    public MtVerifyCode checkVerifyCode(String mobile, String verifycode) {
        MtVerifyCode  reVerifyCode;
        Date queryTime;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = sdf.format(new Date());
            queryTime = sdf.parse(dt);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }
        reVerifyCode = mtVerifyCodeMapper.queryByMobileVerifyCode(mobile, verifycode, queryTime);
        return reVerifyCode;
    }
}
