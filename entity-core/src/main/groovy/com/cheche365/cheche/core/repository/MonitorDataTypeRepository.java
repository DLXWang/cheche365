package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.MonitorDataType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/8/25.
 */
@Repository
public interface MonitorDataTypeRepository extends PagingAndSortingRepository<MonitorDataType, Long> {
    MonitorDataType findFirstByName(String name);
    List<MonitorDataType> findByShowFlag(boolean showFlag);
}
