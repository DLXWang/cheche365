package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.TelMarketingCenterAssignBatchData;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelMarketingCenterAssignBatchDataRepository extends PagingAndSortingRepository<TelMarketingCenterAssignBatchData, Long>, JpaSpecificationExecutor<TelMarketingCenterAssignBatchData> {

}
