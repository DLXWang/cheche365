package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.abao.InsurancePolicy;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by chenxiaozhe on 15-12-11.
 */
@Repository
public interface InsurancePolicyRepository extends PagingAndSortingRepository<InsurancePolicy, Long>, JpaSpecificationExecutor<InsurancePolicy> {
    InsurancePolicy findFirstByPurchaseOrder(PurchaseOrder order);

    Page<InsurancePolicy> findByUser(User user, Pageable pageable);

    @Query(value = "select a from InsurancePolicy a ,PurchaseOrder b  where a.purchaseOrder=b.id  and b.status=1 and a.user=?1")
    Page<InsurancePolicy> findByStatus(User user, Pageable pageable);

    @Query(value = "select a from InsurancePolicy a  where a.effectiveDate <= CURDATE() and a.expireDate >= CURDATE() and a.user=?1")
    Page<InsurancePolicy> findByEffectiveDate(User user, Pageable pageable);

    @Query(value = "select a from InsurancePolicy a  where a.expireDate <= CURDATE() and a.user=?1")
    Page<InsurancePolicy> findByExpireDate(User user, Pageable pageable);

    @Query(value = "select * from insurance_policy ip where not exists(select 1 from insurance_policy_export ipe where ipe.insurance_policy = ip.id)", nativeQuery = true)
    List<InsurancePolicy> findByExportNotExists();

    InsurancePolicy findFirstByPolicyNo(String policyNo);

    @Query(value = "select i.* from insurance_policy i join purchase_order p on i.purchase_order = p.id where p.order_no =?1 order by i.id desc ", nativeQuery = true)
    InsurancePolicy findFirstByOrderNo(String orderNo);
}
