package com.fuint.coupon.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import com.fuint.coupon.dao.entities.MtVerifyCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.coupon.dao.entities.MtVerifyCode;

import java.util.Date;
import java.util.List;

/**
    * mt_verify_code Repository
    * Created by zach
    * Tue Aug 20 10:26:51 CST 2019
    */ 
@Repository 
public interface MtVerifyCodeRepository extends BaseRepository<MtVerifyCode, Long> {

      /**
       * 手机号，验证码，过期时间检验验证码是否可用
       *
       * @return
       */
      @Query("select t from MtVerifyCode t where  t.mobile = :mobile and t.verifycode = :verifycode and t.validflag = 0 and t.expiretime >= :queryTime")
      MtVerifyCode queryByMobileVerifyCode( @Param("mobile") String mobile, @Param("verifycode") String verifycode,@Param("queryTime") Date queryTime);

    /**
     * 手机号，过期时间列表
     *
     * @return
     */
    @Query("select t from MtVerifyCode t where  t.mobile = :mobile and t.validflag = 0 and t.expiretime >= :queryTime")
    List<MtVerifyCode> queryVerifyCodeListByMobile( @Param("mobile") String mobile,@Param("queryTime") Date queryTime);

    /**
     * 手机号，获取最新一条短信
     *
     * @return
     */
    @Query("select t from MtVerifyCode t where  t.mobile = :mobile")
    Page<MtVerifyCode> queryVerifyCodeLastRecord(Pageable pageable, @Param("mobile") String mobile);

}

