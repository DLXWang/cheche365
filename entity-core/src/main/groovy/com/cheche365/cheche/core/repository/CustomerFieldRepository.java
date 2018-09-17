package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.CustomerField;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/8/25.
 */
@Repository
public interface CustomerFieldRepository extends PagingAndSortingRepository<CustomerField, Long> {
    List<CustomerField> findByBusinessActivityOrderById(BusinessActivity businessActivity);
}
