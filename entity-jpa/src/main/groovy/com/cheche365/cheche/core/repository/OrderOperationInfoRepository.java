package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wangfei on 2015/4/30.
 */
@Repository
public interface OrderOperationInfoRepository extends PagingAndSortingRepository<OrderOperationInfo, Long>,JpaSpecificationExecutor<OrderOperationInfo> {

    List<OrderOperationInfo> findByCurrentStatus(OrderTransmissionStatus currentStatus);
    OrderOperationInfo findFirstByPurchaseOrder(PurchaseOrder order);

    @Query(value = "select MAX(purchase_order) from order_operation_info",nativeQuery = true)
    Long findMaxPurchaseOrder();

    Long countByAssigner(InternalUser internalUser);

    List<OrderOperationInfo> findByCurrentStatusAndReConfirmDateAndAssigner(OrderTransmissionStatus currentStatus, Date reconfirmDate, InternalUser assigner);

    @Query(value = "SELECT ooi.* FROM order_operation_info ooi, purchase_order po " +
            "where ooi.purchase_order=po.id and po.create_time like ?1 " +
            "AND (po.channel in (5,6) OR (po.channel IN (1,2,3,4) AND po.status in (3,4,5)))",nativeQuery = true)
    List<OrderOperationInfo> findOrderOperationInfoByDate(String dateStr);

    @Query(value="select ooi.* from order_operation_info as ooi left join purchase_order as po on ooi.purchase_order=po.id where po.status in(?1) and po.source_channel in(?2)",nativeQuery = true)
    List<OrderOperationInfo> findCooperationOrderByOperateStatus(List<Long> statusList, List<Long> sourceList);


    @Query(value="select ooi.* from order_operation_info as ooi ,purchase_order as po,auto where ooi.purchase_order=po.id and po.auto=auto.id and auto.license_plate_no=?1 and po.create_time>=?2 and po.audit=1",nativeQuery = true)
    List<OrderOperationInfo> findByLicensePlateNo(String licensePlateNo,Date beginDate);

    @Query(value=" SELECT                       " +
        " 	*                                   " +
        " FROM                                  " +
        " 	order_operation_info i              " +
        " WHERE                                 " +
        " 	i.current_status IN ?3    " +
        " AND i.confirm_order_date BETWEEN ?1   " +
        " AND ?2                                " ,nativeQuery = true)
    List<OrderOperationInfo> findConfirmOrderByTimePeriod(Date startTime,Date endTime,List orderStatus);

    @Query(value = "select distinct ooi.* from order_operation_info as ooi join purchase_order  as p " +
        "on ooi.purchase_order = p.id " +
        "where (" +
        "(p.status =1 and ooi.current_status not in(20,22)) " +
        "or (p.status =3 and ooi.current_status not in(1,13,14)) " +
        "or (p.status=5 and ooi.current_status != 15) " +
        "or (p.status=6 and ooi.current_status != 19) " +
        "or (p.status=9 and ooi.current_status != 17) " +
        "or (p.status=10 and ooi.current_status not in(16,21)) " +
        "or ((select status from payment where purchase_order=p.id order by id desc limit 1)=3 and ooi.current_status != 21))  " +
        "and p.update_time between ?1 and ?2",nativeQuery = true)
    List<OrderOperationInfo> findQestionableOrderByUpdateTime(Date startTime,Date endTime);

    @Query(value="select * from order_operation_info where assigner=?1 order by update_time desc limit ?2, ?3",nativeQuery = true)
    List<OrderOperationInfo> findByAssigner(Long operatorId,int startIndex,int pageSize);

    @Query(value = "select * from order_operation_info ooi where ooi.id in ?1 order by ooi.id desc ",nativeQuery = true)
    List<OrderOperationInfo> findByIds(List<String> ids);

    @Query(value="select ooi.* from order_operation_info as ooi join purchase_order as po on ooi.purchase_order=po.id where po.order_no = ?1",nativeQuery = true)
    OrderOperationInfo findByOrderNo(String orderNo);
}
