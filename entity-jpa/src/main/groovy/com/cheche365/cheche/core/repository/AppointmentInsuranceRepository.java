package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/7/24.
 */
@Repository
public interface AppointmentInsuranceRepository extends PagingAndSortingRepository<AppointmentInsurance, Long>, JpaSpecificationExecutor<AppointmentInsurance> {

    @Query(value = "select * from appointment_insurance where id=?1 and user=?2", nativeQuery = true)
    AppointmentInsurance searchById(Long id, Long userId);

    @Query(value=" FROM AppointmentInsurance ai WHERE ai.user = ?1 ORDER BY ai.createTime ")
    Page<AppointmentInsurance> searchAppointmentsPageable(User user,  Pageable pageable);

    List<AppointmentInsurance> findByUser(User user);

    // 客户姓名、手机、车险到期日、车牌号、提交时间
    @Query(value = "select distinct us.mobile, ai.contact, ai.expire_before, ai.license_plate_no, ai.create_time, c.name " +
        "from appointment_insurance ai, user us , channel c " +
        "where ai.user = us.id " +
        "and c.id = ai.source_channel "+
        "and hex(ai.contact) regexp('^.+(e[4-9][0-9a-f]{4}|3[0-9]|4[0-9A-F]|5[0-9A]|6[0-9A-F]|7[0-9A])$') " +
        "and ai.create_time >= ?1 and ai.create_time < ?2 " +
        "order by ai.create_time asc",
        nativeQuery = true)
    List<Object[]> selectDistinctCustomers(Date beginDate, Date endDate);

    @Query(value = "SELECT * FROM appointment_insurance ai " +
        "where ai.id in (select max(id) from appointment_insurance where expire_before BETWEEN ?1 and ?2 GROUP BY user) " +
        "and not exists (select 1 from tel_marketing_center_repeat rpt where rpt.user = ai.user and rpt.source in (10,90,100,110,113,115))", nativeQuery =  true)
    List<AppointmentInsurance> getExpireBeforeByDate(Date firstDate, Date secondDate);

    @Query("SELECT ai FROM AppointmentInsurance ai, User ur " +
        "where ai.user = ur.id " +
        "and (ur.userType is null or ur.userType.id = 1) " +
        "and ai.createTime between ?1 and ?2 " +
        "and ai.sourceChannel not in(?3) "+
        "order by ai.id")
    Page<AppointmentInsurance> getPageDataByDate(Date firstDate, Date secondDate,List<Channel> channels, Pageable pageable);

    AppointmentInsurance findById(Long id);

    @Query(value = "select * from appointment_insurance a where a.source = 1 order by create_time Desc",nativeQuery = true)
    List<AppointmentInsurance> findexcel();

    @Query(value = "select id from appointment_insurance where create_time <= ?1 order by id desc limit 1", nativeQuery = true)
    Long findMaxIdByTime(Date createTime);
}
