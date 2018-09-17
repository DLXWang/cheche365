package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by xu.yelong on 2016-05-20.
 */
@Repository
public interface InstitutionRebateHistoryTempRepository extends PagingAndSortingRepository<InstitutionRebateHistoryTemp,Long>, JpaSpecificationExecutor<InstitutionRebateHistoryTemp> {
    InstitutionRebateHistoryTemp findFirstByInstitutionTempAndAreaAndInsuranceCompanyOrderByIdDesc(InstitutionTemp institutionTemp, Area area, InsuranceCompany insuranceCompany);

    InstitutionRebateHistoryTemp findFirstByInstitutionTempAndAreaAndInsuranceCompanyOrderByStartTimeDesc(InstitutionTemp institutionTemp, Area area, InsuranceCompany insuranceCompany);

    @Query("select irh from InstitutionRebateHistoryTemp irh,InstitutionTemp i " +
        "where irh.area = ?1 and irh.insuranceCompany = ?2 " +
        "and irh.operation !=3 and irh.startTime<?3 and ( irh.endTime>=?3 or irh.endTime is null) and irh.institutionTemp=i.id and i.enable=1" )
    List<InstitutionRebateHistoryTemp> findByAreaAndInsuranceCompanyAndDateTime(Area area, InsuranceCompany insuranceCompany, Date orderCreateTime);

    InstitutionRebateHistoryTemp findFirstByInstitutionTempAndAreaAndInsuranceCompanyOrderByStartTime(InstitutionTemp institutionTemp, Area area, InsuranceCompany insuranceCompany);

    List<InstitutionRebateHistoryTemp> findByInstitutionTempOrderByStartTime(InstitutionTemp institutionTemp);
}
