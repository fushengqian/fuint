package com.fuint.base.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import com.fuint.base.dao.entities.TAccountDuty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 账户角色关联关系
 * <p/>
 * Created by hanxiaoqiang on 16/7/18.
 */
@Repository("tAccountDutyRepository")
public interface TAccountDutyRepository extends BaseRepository<TAccountDuty, Long> {

    @Query("select t.tDuty.id from TAccountDuty t where t.tAccount.id = :accountId")
    public List<Long> findDutyIdsByAccountId(@Param("accountId") Long accountId);

    @Transactional
    @Modifying
    @Query("delete from TAccountDuty t where t.tAccount.id = :accountId")
    public void deleteDutiesByAccountId(@Param("accountId") Long accountId);
}
