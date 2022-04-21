package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtStaff;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * mt_staff Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtStaffRepository extends BaseRepository<MtStaff, Integer> {

      /**
       * 更新状态
       *
       * @return
       */
      @Transactional
      @Modifying
      @Query(value = "update MtStaff p set p.auditedStatus =:statusenum,p.updateTime=:currentDT where p.id in (:ids)")
      int updateStatus(@Param("ids") List<Integer> ids, @Param("statusenum") String statusenum,@Param("currentDT") Date currentDT );

      /**
       * 根据手机号查找员工
       *
       * @return
       */
      @Query("select t from MtStaff t where t.mobile = :mobile")
      MtStaff queryStaffByMobile(@Param("mobile") String mobile);

      /**
       * 根据会员ID查找员工
       *
       * @return
       */
      @Query("select t from MtStaff t where t.userId = :userId")
      MtStaff queryStaffByUserId(@Param("userId") Integer userId);
}

