package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.Institution;
import com.cheche365.cheche.core.model.InstitutionRebate;
import com.cheche365.cheche.core.model.InsuranceCompany;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface InstitutionRebateRepository extends PagingAndSortingRepository<InstitutionRebate, Long> {
    List<InstitutionRebate> findByInstitution(Institution institution);

    @Query("select distinct obj.area from InstitutionRebate obj where obj.institution = ?1")
    List<Area> findAreaByInstitution(Institution institution);

    @Query("select distinct obj.insuranceCompany from InstitutionRebate obj where obj.institution = ?1")
    List<InsuranceCompany> findInsuranceCompanyByInstitution(Institution institution);

    @Query("from InstitutionRebate ir where ir.area = ?1 and ir.insuranceCompany = ?2 and ir.commercialRebate > 0 and ir.compulsoryRebate > 0")
    List<InstitutionRebate> findByAreaAndInsuranceCompany(Area area, InsuranceCompany insuranceCompany);

    InstitutionRebate findFirstByAreaAndInsuranceCompanyAndInstitution(Area area, InsuranceCompany insuranceCompany, Institution institution);

	@Query("select distinct a from Area as a ,InstitutionRebate as ir ,Institution as i where a.id=ir.area and ir.institution=i.id and i.enable=?1 order by a.type")
    List<Area> findAreaByInstitutionAndEnable(Boolean enable);

	@Query(value = "select count(distinct ins.id) from institution_rebate rebate, institution ins " +
        "where rebate.institution = ins.id and ins.enable = 1 " +
        "and rebate.area = ?1 and rebate.insurance_company = ?2", nativeQuery = true)
    Long countByAreaAndInsuranceCompany(Long areaId, Long insuranceCompanyId);
}
