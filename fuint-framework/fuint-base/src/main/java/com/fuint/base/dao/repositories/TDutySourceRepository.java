package com.fuint.base.dao.repositories;

import com.fuint.base.dao.BaseRepository;

import com.fuint.base.dao.entities.TDutySource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 角色菜单关系
 *
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository("tDutySourceRepository")
public interface TDutySourceRepository extends BaseRepository<TDutySource, Long> {

    @Query("select t.tSource.id from TDutySource t where t.tDuty.id = :dutyId")
    public List<Long> findSourceIdsByDutyId(@Param("dutyId") Long dutyId);

    @Transactional
    @Modifying
    @Query("delete from TDutySource t where t.tDuty.id = :dutyId")
    public void deleteSourcesByDutyId(@Param("dutyId") Long dutyId);
}
