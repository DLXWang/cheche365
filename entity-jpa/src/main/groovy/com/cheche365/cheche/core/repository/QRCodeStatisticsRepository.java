package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.QRCodeChannel;
import com.cheche365.cheche.core.model.QRCodeStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/8/4.
 */
@Repository
public interface QRCodeStatisticsRepository extends PagingAndSortingRepository<QRCodeStatistics, Long>, JpaSpecificationExecutor<QRCodeStatistics> {
    QRCodeStatistics findFirstByQrCodeChannelAndStatisticsTime(QRCodeChannel qrCodeChannel, Date statisticsTime);

    @Query(value = "select min(statistics_time) from qrcode_statistics where qrcode_channel = ?1", nativeQuery = true)
    Date getMinStatisticsTime(Long qrCodeChannelId);

    @Query(value = "select IFNULL(sum(q1.scan_count), 0), IFNULL(sum(q1.subscribe_count), 0) from qrcode_statistics q1, qrcode_channel q2 " +
        "where q1.qrcode_channel = q2.id and q2.id = ?1 " +
        "and (q2.expire_time is null or q1.statistics_time <= DATE_ADD(q2.expire_time,INTERVAL 3 DAY))", nativeQuery = true)
    List<Object[]> getScanAndSubscribeCount(Long qrCodeChannelId);

    @Query(value = "select q2.id, IFNULL(sum(q1.scan_count), 0), IFNULL(sum(q1.subscribe_count), 0) from qrcode_statistics q1, qrcode_channel q2 " +
        "where q1.qrcode_channel = q2.id and q2.id in (?1)" +
        "and (q2.expire_time is null or q1.statistics_time <= DATE_ADD(q2.expire_time,INTERVAL 3 DAY)) " +
        "group by q2.id", nativeQuery = true)
    List<Object[]> getScanAndSubscribeCount(List<Long> qrCodeChannelIdList);

    @Query("select qs from QRCodeStatistics as qs, QRCodeChannel as qc " +
        "where qs.qrCodeChannel = qc.id and qs.qrCodeChannel = ?1 " +
        "and (qc.expireTime is null or (qc.expireTime is not null and qs.statisticsTime <= ?2)) " +
        "group by qs.statisticsTime order by qs.statisticsTime")
    org.springframework.data.domain.Page<QRCodeStatistics> getQRCodeStatisticsPage(QRCodeChannel qrcodeChannel, Date expireTime, Pageable pageable);
}
