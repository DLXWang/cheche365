package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.PingPlusAppSupport;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PingPlusAppSupportRepository extends PagingAndSortingRepository<PingPlusAppSupport, Long> {

    List<PingPlusAppSupport> findByAreaAndInsuranceCompany(Area area, InsuranceCompany insuranceCompany);

    PingPlusAppSupport findFirstByArea(Area area);
}
