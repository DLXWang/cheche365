package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.UserImg;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImgRepository extends PagingAndSortingRepository<UserImg, Long>, JpaSpecificationExecutor<UserImg> {


}
