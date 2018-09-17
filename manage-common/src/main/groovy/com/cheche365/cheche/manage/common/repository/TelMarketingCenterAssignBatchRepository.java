package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.TelMarketingCenterAssignBatch;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TelMarketingCenterAssignBatchRepository extends PagingAndSortingRepository<TelMarketingCenterAssignBatch, Long>, JpaSpecificationExecutor<TelMarketingCenterAssignBatch> {
    @Query(value = "select count(*) from tel_marketing_center where create_time between ?1 and ?2",nativeQuery = true)
    int findInputNumByTime(Date startTime, Date endTime);

    @Query(value = "select SUM(assign_num) from tel_marketing_center_assign_batch where operator=?1 and create_time between ?2 and ?3",nativeQuery = true)
    Integer findAssignNumByUserAndTime(Long operator,Date startTime,Date endTime);

    @Query(value = "select count(*) from tel_marketing_center_assign_batch tmcab join tel_marketing_center_assign_batch_data tmcabd on tmcab.id=tmcabd.batch join tel_marketing_center tmc on tmcabd.tel_marketing_center=tmc.id where tmcab.operator=?1 and tmc.create_time between ?2 and ?3",nativeQuery = true)
    int findNewDataNumByUserAndTime(Long operator,Date startTime,Date endTime);

    @Query(value = "select count(t.id),iu.name " +
        "from tel_marketing_center t " +
        "join tel_marketing_center_assign_batch_data tbd on t.id = tbd.tel_marketing_center " +
        "join tel_marketing_center_assign_batch tb on tb.id = tbd.batch " +
        "join internal_user iu on t.operator =iu.id  " +
        "where tb.id=?1 and tb.target_assigner <> t.operator group by t.operator limit ?2, ?3", nativeQuery = true)
    List<Object[]> findPageByBatchId(Long batchId, int startIndex, int pageSize);

    @Query(value = "select count(distinct t.operator) " +
        "from tel_marketing_center t " +
        "join tel_marketing_center_assign_batch_data tbd on t.id = tbd.tel_marketing_center " +
        "join tel_marketing_center_assign_batch tb on tb.id = tbd.batch " +
        "join internal_user iu on t.operator =iu.id  " +
        "where tb.id = ?1 and tb.target_assigner <> t.operator ", nativeQuery = true)
    Long countByBatchId(Long batch);

}
