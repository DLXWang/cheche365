package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.QuotePhone;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangfei on 2015/10/15.
 */
@Repository
public interface QuotePhoneRepository extends PagingAndSortingRepository<QuotePhone, Long>, JpaSpecificationExecutor<QuotePhone> {

    List<QuotePhone> findByLicensePlateNo(String licensePlateNo);

    List<QuotePhone> findByUser(User user);
}
