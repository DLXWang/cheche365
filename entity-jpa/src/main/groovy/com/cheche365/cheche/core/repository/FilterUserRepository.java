package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.FilterUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoweifu on 2015/10/8.
 */
@Repository
public interface FilterUserRepository extends PagingAndSortingRepository<FilterUser, Long> , JpaSpecificationExecutor<FilterUser> {

    FilterUser findFirstByName(String name);

    Long countByIdNotAndName(Long id, String name);

    List<FilterUser> findByDisable(boolean disable);

}
