package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.abao.InsuranceProduct
import com.cheche365.cheche.core.model.abao.InsuranceProductTag
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
public interface InsuranceProductTagRepository extends JpaRepository<InsuranceProductTag, Long>, JpaSpecificationExecutor<InsuranceProductTag> {
    @Modifying
    @Transactional
    @Query(value = "delete from InsuranceProductTag where insuranceProduct in ?1")
    void deleteByInsuranceProduct(ArrayList<InsuranceProduct> insuranceProducts)
}
