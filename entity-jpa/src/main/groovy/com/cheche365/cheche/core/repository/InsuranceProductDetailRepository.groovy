package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.abao.InsuranceProduct
import com.cheche365.cheche.core.model.abao.InsuranceProductDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * Created by mahong on 2016/11/28.
 */
@Transactional(readOnly = true)
@Repository
public interface InsuranceProductDetailRepository extends JpaRepository<InsuranceProductDetail, Long>, JpaSpecificationExecutor<InsuranceProductDetail> {
    @Modifying
    @Transactional
    @Query(value = "delete from InsuranceProductDetail where insuranceProduct in ?1")
    void deleteByInsuranceProduct(ArrayList<InsuranceProduct> insuranceProducts)

    @Query(value = "SELECT * FROM insurance_product_detail WHERE insurance_product = ?1 AND detail_name=?2 LIMIT 1", nativeQuery = true)
    InsuranceProductDetail findDetailByProductAndDetailName(Long productId, Long nameId);
}
