package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.abao.InsurancePolicyExport;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by xu.yelong on 2016/12/28.
 */
public interface InsurancePolicyExportRepository extends PagingAndSortingRepository<InsurancePolicyExport, Long>, JpaSpecificationExecutor<InsurancePolicyExport> {

}
