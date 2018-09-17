package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.TaskJob;
import com.cheche365.cheche.manage.common.model.TaskJobDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by wangshaobin on 2017/7/6.
 */
public interface TaskJobDetailRepository extends PagingAndSortingRepository<TaskJobDetail, Long>, JpaSpecificationExecutor<TaskJobDetail> {
}
