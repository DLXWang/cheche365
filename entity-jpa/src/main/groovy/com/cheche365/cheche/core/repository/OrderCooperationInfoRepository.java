package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.OrderCooperationInfo;
import com.cheche365.cheche.core.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface OrderCooperationInfoRepository extends PagingAndSortingRepository<OrderCooperationInfo, Long>, JpaSpecificationExecutor<OrderCooperationInfo> {
    OrderCooperationInfo findFirstByPurchaseOrder(PurchaseOrder purchaseOrder);

    @Modifying @Query(value="update order_cooperation_info as oci, payment as p set oci.status=1 " +
        "where oci.status is null and oci.purchase_order=p.purchase_order and p.status=2 and p.channel in(1,2,3,4,5,6)", nativeQuery = true)
    Integer updateByPaymentStatus();

    @Modifying @Query(value="update order_cooperation_info as oci, area_contact_info as aci " +
        "set oci.area_contact_info=aci.id where oci.area = aci.area and oci.area_contact_info is null", nativeQuery = true)
    Integer updateAreaContactInfo();

    @Query(value="select oci.* from order_cooperation_info as oci left join purchase_order as po on oci.purchase_order=po.id where po.status in(?1) and po.source_channel in(?2)",nativeQuery = true)
    List<OrderCooperationInfo> findCooperationOrderByOperateStatus(List<Long> statusList,List<Long> sourceList);
}
