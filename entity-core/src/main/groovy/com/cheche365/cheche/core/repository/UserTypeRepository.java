package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.UserType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by liqiang on 3/31/15.
 */
@Repository
public interface UserTypeRepository extends PagingAndSortingRepository<UserType, Long>{

    UserType findFirstByName(String name);
}
