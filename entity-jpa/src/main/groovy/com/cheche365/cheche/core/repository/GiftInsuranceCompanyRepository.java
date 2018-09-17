package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.GiftInsuranceCompany;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.SourceType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mahong on 2016/2/16.
 */
@Repository
public interface GiftInsuranceCompanyRepository  extends PagingAndSortingRepository<GiftInsuranceCompany, Long> {

    @Query(value = "SELECT gic.insuranceCompany FROM GiftInsuranceCompany gic WHERE gic.sourceType = ?1 AND gic.source = ?2 ")
    List<InsuranceCompany> findInsuranceCompanyBySourceTypeAndSource(SourceType sourceType, Long id);

}
