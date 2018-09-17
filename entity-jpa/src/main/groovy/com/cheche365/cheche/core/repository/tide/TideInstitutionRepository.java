package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.tide.TideInstitution;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by yinJianBin on 2018/4/19.
 */
public interface TideInstitutionRepository extends PagingAndSortingRepository<TideInstitution, Long>, JpaSpecificationExecutor<TideInstitution> {

    List<TideInstitution> findAllByTideBranchId(Long tideBranch_id);

    Iterable<TideInstitution> findAllByInstitutionNameAndTideBranchId(String institutionName, Long tideBranch_id);
}