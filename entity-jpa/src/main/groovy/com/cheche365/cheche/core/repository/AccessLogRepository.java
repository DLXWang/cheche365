package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.AccessLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessLogRepository extends PagingAndSortingRepository<AccessLog, Long> {
    @Query(value = "SELECT * FROM access_log where id > ?1 order by id limit ?2", nativeQuery = true)
    List<AccessLog> findLimit(Long preId, Integer pageSize);
}
