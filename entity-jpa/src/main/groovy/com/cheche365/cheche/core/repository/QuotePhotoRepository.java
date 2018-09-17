package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Marketing;
import com.cheche365.cheche.core.model.QuotePhoto;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wangfei on 2015/10/20.
 */
@Repository
public interface QuotePhotoRepository extends PagingAndSortingRepository<QuotePhoto, Long>, JpaSpecificationExecutor<QuotePhoto> {

    Long countByUserAndActivityAndDisable(User user, Marketing activity, boolean disable);

    QuotePhoto findQuotePhotoByUserAndActivity(User user, Marketing activity);

    @Query(value = "SELECT qp.* FROM quote_photo AS qp WHERE qp.user=?1 and qp.license_plate_no is not null ORDER BY qp.id DESC",nativeQuery = true)
    List<QuotePhoto> listLicensePlateNoByUser(Long userId);

    @Query(value = "SELECT qp.* FROM quote_photo AS qp WHERE qp.license_plate_no=?1 ORDER BY qp.id DESC",nativeQuery = true)
    List<QuotePhoto> listByLicensePlateNo(String LicensePlateNo);

    @Query(value = "select count(id) from quote_photo where create_time like ?1% and activity is not null", nativeQuery = true)
    Integer getCountByCreateTimeAndActivityNotNull(String createTime);

    @Query(value = "SELECT qp.* FROM quote_photo AS qp WHERE qp.disable= ?1 and qp.activity = 15 and (qp.update_time between ?2 and ?3 )ORDER BY qp.update_time ASC",nativeQuery = true)
    List<QuotePhoto> listByDisableAndUpdateTime (boolean disable,String updateTimeStart,String updateTimeEnd);

    @Query(value = "SELECT * FROM quote_photo qp WHERE qp.disable = 0 AND  qp.create_time >= CURRENT_DATE()", nativeQuery = true)
    List<QuotePhoto> getCurDateQuotePhotos();

    @Query(value = "SELECT * from quote_photo qp where qp.user = ?1 and qp.license_plate_no is NOT null", nativeQuery = true)
    List<QuotePhoto> findByUser(User user);

    @Query(value = "SELECT * FROM quote_photo qp WHERE qp.disable = 0 AND  qp.create_time between ?1 and ?2", nativeQuery = true)
    List<QuotePhoto> listQuotePhotoByDate(Date startDate, Date endDate);

    @Query("SELECT qp FROM QuotePhoto qp, User ur ,UserImg ui " +
        "WHERE qp.user = ur.id and qp.userImg=ui.id " +
        "and (ur.userType is null or ur.userType.id = 1) " +
        "and qp.createTime between ?1 and ?2 " +
        "and ui.sourceChannel not in(?3) "+
        "order by qp.id")
    org.springframework.data.domain.Page<QuotePhoto> findPageDataByDate(Date startDate, Date endDate,List<Channel> channels, Pageable pageable);

    @Query(value = "select id from quote_photo where create_time <= ?1 order by id desc limit 1", nativeQuery = true)
    Long findMaxIdByTime(Date createTime);
}
