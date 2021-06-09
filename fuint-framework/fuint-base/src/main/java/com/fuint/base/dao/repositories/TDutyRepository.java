package com.fuint.base.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import com.fuint.base.dao.entities.TDuty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色Repository服务接口
 *
 * @author fsq
 * @version $Id: TDutyRepository.java, v 0.1 2015年11月19日 下午5:36:07 fsq Exp $
 */
@Repository("tDutyRepository")
public interface TDutyRepository extends BaseRepository<TDuty, Long> {


    public TDuty findByNameAndStatus(String name, String status);

    public List<TDuty> findByStatus(String status);

    public List<TDuty> findByIdIn(List<String> ids);

    @Query("select t.tDuty from TAccountDuty t where t.tAccount.id = :accountId and t.tDuty.status = 'A'")
    public List<TDuty> findDutysByIdAccountId(@Param("accountId") Long accountId);
}
