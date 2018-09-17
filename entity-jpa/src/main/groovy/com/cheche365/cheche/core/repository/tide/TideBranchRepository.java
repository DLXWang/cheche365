package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.tide.TideBranch;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by yinJianBin on 2018/4/19.
 */
public interface TideBranchRepository extends PagingAndSortingRepository<TideBranch, Long>, JpaSpecificationExecutor<TideBranch> {

    TideBranch findFirstByTidePlatformId(Long tidePlatformId);

    List<TideBranch> findAllByTidePlatformId(Long tidePlatform_id);

}