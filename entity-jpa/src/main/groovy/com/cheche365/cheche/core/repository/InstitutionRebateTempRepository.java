package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface InstitutionRebateTempRepository extends PagingAndSortingRepository<InstitutionRebateTemp, Long> {
    List<InstitutionRebateTemp> findByInstitutionTemp(InstitutionTemp institutionTemp);

    @Query("select distinct obj.area from InstitutionRebateTemp obj where obj.institutionTemp = ?1")
    List<Area> findAreaByInstitutionTemp(InstitutionTemp institutionTemp);

    @Query("select distinct obj.insuranceCompany from InstitutionRebateTemp obj where obj.institutionTemp = ?1")
    List<InsuranceCompany> findInsuranceCompanyByInstitutionTemp(InstitutionTemp institutionTemp);

    @Query("from InstitutionRebateTemp ir where ir.area = ?1 and ir.insuranceCompany = ?2 and ir.commercialRebate > 0 and ir.compulsoryRebate > 0")
    List<InstitutionRebateTemp> findByAreaAndInsuranceCompany(Area area, InsuranceCompany insuranceCompany);

    InstitutionRebateTemp findFirstByAreaAndInsuranceCompanyAndInstitutionTemp(Area area, InsuranceCompany insuranceCompany, InstitutionTemp institutionTemp);

    @Query("select distinct a from Area as a ,InstitutionRebateTemp as ir ,InstitutionTemp as i where a.id=ir.area and ir.institutionTemp=i.id and i.enable=?1 order by a.type")
    List<Area> findAreaByInstitutionTempAndEnable(Boolean enable);

    @Query(value = "select count(distinct ins.id) from institution_rebate_temp rebate, institutionTemp ins " +
        "where rebate.institution_temp = ins.id and ins.enable = 1 " +
        "and rebate.area = ?1 and rebate.insurance_company = ?2", nativeQuery = true)
    Long countByAreaAndInsuranceCompany(Long areaId, Long insuranceCompanyId);
}
