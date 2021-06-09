package com.fuint.base.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import com.fuint.base.dao.entities.TSource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单Repository服务接口
 *
 * @author fsq
 * @version $Id: TSourceRepository.java, v 0.1 2015年11月19日 下午5:36:52 fsq Exp $
 */
@Repository("tSourceRepository")
public interface TSourceRepository extends BaseRepository<TSource, Long> {

    @Query("select t from TSource t where t.status='A' order by t.style ASC")
    public List<TSource> findAllTSource();


    public List<TSource> findByStatus(String status);

    public List<TSource> findByIdIn(List<String> ids);

    @Query("select t.tSource from TDutySource t where t.tSource.status='A' and EXISTS (select 1 from TAccountDuty c where t.tDuty.id = c.tDuty.id and c.tAccount.id = :accountId) order by t.tSource.style asc")
    public List<TSource> findSourcesByAccountId(@Param("accountId") long accountId);

    public List<TSource> findBySourceCode(String sourceCode);

}
