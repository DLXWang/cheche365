package com.cheche365.cheche.core.mongodb.repository;

import com.cheche365.cheche.core.model.MoApplicationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wenling on 2017/8/7.
 */
@Repository
public interface MoApplicationLogRepository extends MongoRepository<MoApplicationLog, String> {

    /**
     * 已检测：通过
     **/
    @Query(value = "{\"objTable\":?2,\"objId\":?0,\"logType._id\":?1}")
    List<MoApplicationLog> findByObjIdAndLogTypeAndObjTableOrderByCreateTime(String objId, Long logType, String objTable, Sort sort);

    @Query(value = "{'logType._id':?0, 'createTime': {'$gt':?1 ,'$lt':?2}}")
    Page<MoApplicationLog> findByLogTypeAndCreateTimeBetween(Long quote_cache_record, Date startDate, Date yesterdayEnd, Pageable page);

    @Query(value = "{'logType._id':?0, 'objTable':?1}")
    Page<MoApplicationLog> findByLogTypeAndObjTableOrderByCreateTimeDesc(Long logType, String tableName, Pageable page);

    @Query(value = "{'logType._id':?0, 'objTable':?1, 'objId':?2}")
    Page<MoApplicationLog> findByLogTypeAndObjTableAndObjIdOrderByCreateTimeDesc(Long logType, String tableName, String objId, Pageable page);

    /**
     * 已检测：通过
     **/
    List<MoApplicationLog> findByIdInOrderByIdDesc(String[] idList);

    List<MoApplicationLog> findByIdInOrderByIdDesc(Long[] idList);

    MoApplicationLog findById(String id);

    MoApplicationLog findById(Long id);

    /**
     * 已检测：通过
     **/
    @Query(value = "{'logType._id':?0, 'createTime': {'$lt':?2, '$gt':?1}, 'user':{'$ne':null}}")
    List<MoApplicationLog> findByLogTypeAndCreateTimeBetweenAndUserNotNull(Long logType, Date startDate, Date endDate);

    /**
     * 已检测：通过
     **/
    @Query(value = "{'logType._id':?0, 'createTime': {'$gt':?1 ,'$lt':?2}, 'logMessage.channel.id':{'$in':?3}}")
    Page<MoApplicationLog> findByLogTypeAndCreateTimeAndLogMessageAndId(Long logType, Date startDate, Date endDate, Long[] channelIds, Pageable page);

    @Query(value = "{'user._id':{'$in':?0}, 'createTime': {'$lt':?2, '$gt':?1}, 'logType._id':?3}")
    List<MoApplicationLog> findByUserIdInAndCreateTimeLessThan(Long[] idList, Date startTime, Date endTime, Long logType);

    @Query(value = "{\"objId\":?0,\"logType._id\":?1}")
    List<MoApplicationLog> findByObjIdAndLogType(String objId, Long logType);

    @Query(value = "{'createTime':{ $gte:?0,$lt:?1}, 'logType._id':?3, 'user.mobile':{$in:?2}}")
    List<MoApplicationLog> findAllByCreateTimeAndObjTableAndMobileList(Date startTime, Date endTime, List<String> mobileList, Long logType);

    @Query(value = "{'logType._id':53, 'logMessage.UserInfo.LicenseNo':?0}")
    List<MoApplicationLog> findBihuInfoByLicenseNo(String licenseNo);
}
