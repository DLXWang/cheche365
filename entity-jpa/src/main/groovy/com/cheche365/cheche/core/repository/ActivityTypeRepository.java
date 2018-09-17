package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ActivityType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mahong on 2016/6/22.
 */
@Repository
public interface ActivityTypeRepository extends PagingAndSortingRepository<ActivityType, Long> {

    @Query(value = "select * from activity_type ", nativeQuery = true)
    List<ActivityType> findAll();
}
