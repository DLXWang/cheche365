package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.abao.InsuranceProduct
import com.cheche365.cheche.core.model.abao.InsuranceProductStatus
import com.cheche365.cheche.core.model.abao.InsuranceProductType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Created by wangjiahuan on 2016/11/22 0022.
 */
@Repository
public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, Long> {

    List<InsuranceProduct> findAllByProductType(InsuranceProductType insuranceProductType);

    List<InsuranceProduct> findAllByProductTypeAndStatus(InsuranceProductType insuranceProductType, InsuranceProductStatus status);

    @Query(value = "select * from insurance_product where hot_sale=1 and status=2", nativeQuery = true)
    List<InsuranceProduct> findAllByHotSale();

    @Query(value = "select * from insurance_product a RIGHT JOIN insurance_product_tag b on a.id=b.insurance_product where b.tag_type=?1 and a.status=2", nativeQuery = true)
    List<InsuranceProduct> findAllByTagType(Long id);

    InsuranceProduct findFirstByName(String name);

}
