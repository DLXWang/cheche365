package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.OfflineInsuranceCompanyImportData;
import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * #
 * Created by yinJianBin on 2017/11/5.
 */
@Repository
public interface OfflineInsuranceCompanyImportDataRepository extends PagingAndSortingRepository<OfflineInsuranceCompanyImportData, Long>, JpaSpecificationExecutor<OfflineInsuranceCompanyImportData> {

    List<OfflineInsuranceCompanyImportData> findByStatus(Integer status);

    List<OfflineInsuranceCompanyImportData> findByPolicyNo(String policyNo);

    OfflineInsuranceCompanyImportData findByPolicyNoAndRebateAddTimes(String policyNo, Integer rebateAddTimes);

    List<OfflineInsuranceCompanyImportData> findByHistory(OfflineOrderImportHistory history);

    @Query(value = "" +
            " select ocd.id, ocd.policy_no, ocd.insured_name, ocd.paid_amount, ocd.rebate, ocd.rebate_amount, ocd.balance_time, ocd.license_plate_no, " +
            " ocd.brand_model, ocd.engine_no, ocd.vin_no, ocd.issue_time, ocd.purchase_order, ocd.status, ocd.match_num, ocd.create_time, ocd.update_time," +
            "  ocd.comment, temp.quote as description, ocd.history, ocd.rebate_add_times " +
            " from offline_insurance_company_import_data ocd " +
            " join (" +
            "         select a.id, a.policy_no,ifnull(i.quote_record,ci.quote_record) as quote from offline_insurance_company_import_data a " +
            "         left join insurance i on a.policy_no = i.policy_no " +
            "         left join compulsory_insurance ci on a.policy_no=ci.policy_no " +
            "         where a.purchase_order is  null " +
            "         and ( i.policy_no is not null or ci.policy_no is not null)" +
            "         group by a.policy_no " +
            "         order by id limit ?1 " +
            " ) temp " +
            " on ocd.policy_no = temp.policy_no " +
            " where ocd.status in (0,15)  ", nativeQuery = true)
    List<OfflineInsuranceCompanyImportData> findNotMatchedData(Integer pageSize);

//    @Query(value = "" +
//            "select * from offline_insurance_company_import_data ocd " +
//            "join (select policy_no from offline_insurance_company_import_data where purchase_order is  null group by policy_no order by id  limit ?,?) temp " +
//            "on ocd.policy_no = temp.policy_no where ocd.purchase_order is null order by id ", nativeQuery = true)
//    List<OfflineInsuranceCompanyImportData> findNotMatchedData(Integer offset, Integer pageSize);

}
