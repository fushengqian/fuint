package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtMessage;
import java.util.List;

/**
 * mt_message Repository
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Repository 
public interface MtMessageRepository extends BaseRepository<MtMessage, Integer> {
   /**
    * 获取最新未读消息
    *
    * @return
    * */
   @Query(value = "select t from MtMessage t where t.userId=:userId and t.type='pop' and t.isRead = 'N' order by t.id desc")
   List<MtMessage> findNewMessage(@Param("userId") Integer userId);
}

