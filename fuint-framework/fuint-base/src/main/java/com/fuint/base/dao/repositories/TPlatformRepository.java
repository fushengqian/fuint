package com.fuint.base.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import com.fuint.base.dao.entities.TPlatform;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 平台 Repository类
 * <p/>
 * Created by hanxiaoqiang on 16/8/1.
 */
@Repository("tPlatformRepository")
public interface TPlatformRepository extends BaseRepository<TPlatform, Long> {

    @Query("select t from TPlatform t where t.status = :status")
    List<TPlatform> findByStatus(@Param("status") int status);

    @Query("select t from TPlatform t where t.name = :name")
    TPlatform findByName(@Param("name") String name);
}
