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
public interface InstitutionRebateHistoryRepository extends PagingAndSortingRepository<InstitutionRebateHistory,Long>, JpaSpecificationExecutor<InstitutionRebateHistory> {
    InstitutionRebateHistory findFirstByInstitutionAndAreaAndInsuranceCompanyOrderByIdDesc(Institution institution, Area area, InsuranceCompany insuranceCompany);

    InstitutionRebateHistory findFirstByInstitutionAndAreaAndInsuranceCompanyOrderByStartTimeDesc(Institution institution, Area area, InsuranceCompany insuranceCompany);

    @Query("select irh from InstitutionRebateHistory irh,Institution i " +
        "where irh.area = ?1 and irh.insuranceCompany = ?2 " +
        "and irh.operation !=3 and irh.startTime<?3 and ( irh.endTime>=?3 or irh.endTime is null) and irh.institution=i.id and i.enable=1" )
    List<InstitutionRebateHistory> findByAreaAndInsuranceCompanyAndDateTime(Area area, InsuranceCompany insuranceCompany,Date orderCreateTime);

    InstitutionRebateHistory findFirstByInstitutionAndAreaAndInsuranceCompanyOrderByStartTime(Institution institution, Area area, InsuranceCompany insuranceCompany);

    @Query(value = "select * from institution_rebate_history where institution = ?1 and area = ?2 and insurance_company = ?3 and ?4 between start_time and end_time order by id asc limit 1",nativeQuery = true)
    InstitutionRebateHistory findByInstitutionAndAreaAndInsuranceCompanyAndStartTime(Long institution, Long area, Long insuranceCompany, Date createTime);

    @Query(value = "select * from institution_rebate_history where institution = ?1 and area = ?2 and insurance_company = ?3 and start_time > ?4 order by start_time asc limit 1",nativeQuery = true)
    InstitutionRebateHistory findNextByInstitutionAndAreaAndInsuranceCompany(Long institution, Long area, Long insuranceCompany, Date createTime);

    @Query(value = "select * from institution_rebate_history where institution = ?1 and area = ?2 and insurance_company = ?3 and start_time < ?4 order by start_time desc limit 1",nativeQuery = true)
    InstitutionRebateHistory findLastByInstitutionAndAreaAndInsuranceCompany(Long institution, Long area, Long insuranceCompany, Date createTime);

    List<InstitutionRebateHistory> findByInstitutionOrderByStartTime(Institution institution);

    @Query(value = "select * from institution_rebate_history where institution=?1 and start_time <?2 and (end_time >=?2 or end_time is null) and area=?3 and insurance_company =?4",nativeQuery = true)
    InstitutionRebateHistory findByInstitutionAndDateTimeAndAreAndCompany(Long institutionId,Date orderCreateTime,Long areaId,Long insuranceCompanyId);
}
