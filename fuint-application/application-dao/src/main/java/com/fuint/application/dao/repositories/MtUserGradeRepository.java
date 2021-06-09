package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtUserGrade;

   /**
    * mt_user_grade Repository
    * Created by zach
    * Tue May 18 22:35:56 GMT+08:00 2021
    */ 
@Repository 
public interface MtUserGradeRepository extends BaseRepository<MtUserGrade, Integer> {
}

