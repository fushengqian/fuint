package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtAddress;
import org.springframework.transaction.annotation.Transactional;

/**
 * mt_address Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtAddressRepository extends BaseRepository<MtAddress, Integer> {
   /**
    * 更新默认地址
    *
    * @return
    */
   @Transactional
   @Modifying
   @Query(value = "update MtAddress p set p.isDefault = 'N' where p.userId = :userId and p.id <> :addressId")
   int setDefault(@Param("userId") Integer userId, @Param("addressId") Integer addressId);
}

