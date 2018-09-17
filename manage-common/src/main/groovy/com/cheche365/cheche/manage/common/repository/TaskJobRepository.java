package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.TaskJob;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu.yelong on 2016-04-12.
 */
@Repository
public interface TaskJobRepository extends PagingAndSortingRepository<TaskJob, Long>, JpaSpecificationExecutor<TaskJob> {

    List<TaskJob> findByStatus(Boolean status);

    TaskJob findByJobName(String jobName);
}
