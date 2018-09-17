package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.SysVersion;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mahong on 2015/7/13.
 */
@Repository
public interface SysVersionRepository extends PagingAndSortingRepository<SysVersion, Long>, JpaSpecificationExecutor<SysVersion> {
    @Query(value = " SELECT * FROM sys_version where channel = ?1 order by id desc limit 1 ",nativeQuery = true)
    SysVersion findLatestSysVersionByChannel(Long channelId);

    @Query(value = " SELECT * FROM sys_version where channel = ?1 and update_advice = 'required' order by id desc limit 1 ",nativeQuery = true)
    SysVersion findLatestRequiredSysVersion(Long channelId);
}
