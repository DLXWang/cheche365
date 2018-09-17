package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.abao.InsuranceProductDetailName
import com.cheche365.cheche.core.model.abao.InsuranceProductType
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query;

/**
 * Created by mahong on 2016/11/25.
 */
@Repository
public interface InsuranceProductDetailNameRepository extends PagingAndSortingRepository<InsuranceProductDetailName, Long> {

    @Query(value = "select distinct dn.* from insurance_product_detail_name dn,insurance_product_detail d,insurance_product p where dn.id=d.detail_name and d.insurance_product = p.id and p.id = ?1", nativeQuery = true)
    List<InsuranceProductDetailName> findAllByProductType(InsuranceProductType insuranceProductType);

    InsuranceProductDetailName findFirstByDetailName(String name)
}
