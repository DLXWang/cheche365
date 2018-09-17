package com.cheche365.cheche.manage.common.service;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import com.cheche365.cheche.core.repository.TelMarketingCenterRepeatRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by sunhuazhong on 2016/2/17.
 */
@Service
@Transactional
public class TelMarketingCenterService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;

    @Autowired
    private TelMarketingCenterRepeatRepository telMarketingCenterRepeatRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InternalUserRepository internalUserRepository;

    private static final String MOBILE_AREA_SYNC_QUEUE = "schedules.task.mobile.area.sync";

    public TelMarketingCenterRepeat save(User user, String mobile, TelMarketingCenterSource source, Date expireDate,
                                         Date sourceCreateTime, Long sourceId, String sourceTable, Channel channel) {
        return save(null, user, mobile, source, expireDate,
            sourceCreateTime, sourceId, sourceTable, channel, null, null,null);
    }

    public TelMarketingCenterRepeat save(TelMarketingCenter telMarketingCenter, User user, String mobile, TelMarketingCenterSource source, Date expireDate,
                                         Date sourceCreateTime, Long sourceId, String sourceTable, Channel channel, Date triggerTime, TelMarketingCenterStatus newStatus,Date effectiveDate) {
        if (telMarketingCenter == null) {
            telMarketingCenter = findFirstByMobile(mobile);
        }
        if (telMarketingCenter == null && user != null) {
            telMarketingCenter = findByUser(user);
        }
        if (telMarketingCenter == null) {
            return saveByUserAndSourceAndTimeNoCheck(
                user, mobile, source, expireDate, sourceCreateTime,
                sourceId, sourceTable, channel, triggerTime,effectiveDate);
        } else {
            return checkSource(
                telMarketingCenter, source, sourceCreateTime,
                sourceId, sourceTable, channel, triggerTime, newStatus,effectiveDate);
        }
    }

    private TelMarketingCenterRepeat saveByUserAndSourceAndTimeNoCheck(User user, String mobile, TelMarketingCenterSource source,
                                                                       Date expireDate, Date sourceCreateTime, Long sourceId,
                                                                       String sourceTable, Channel channel, Date triggerTime,Date effectiveDate) {
        Date currentDate = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        TelMarketingCenter telMarketingCenter = new TelMarketingCenter();
        telMarketingCenter.setUser(user);
        telMarketingCenter.setMobile(user == null || StringUtils.isBlank(user.getMobile()) ? mobile : user.getMobile());
        telMarketingCenter.setUserName(user == null ? "" : user.getName());
        telMarketingCenter.setProcessedNumber(0L);
        telMarketingCenter.setStatus(TelMarketingCenterStatus.Enum.UNTREATED);
        telMarketingCenter.setPriority(1);
        telMarketingCenter.setSource(source);
        telMarketingCenter.setUpdateTime(currentDate);
        telMarketingCenter.setCreateTime(currentDate);
        telMarketingCenter.setDisplay(true);
        telMarketingCenter.setSourceCreateTime(sourceCreateTime);
        telMarketingCenter.setExpireTime(expireDate);
        telMarketingCenter.setTriggerTime(triggerTime);

        isToA(telMarketingCenter, channel);//edit on 2017-07-10 如果是toA的来源,就分给 杜亚茹 , edit on 2017-07-18,根据用户的行为来源渠道来判断
        this.save(telMarketingCenter);
        return saveTelMarketingCenterRepeat(telMarketingCenter, source, sourceCreateTime, sourceId, sourceTable, channel,effectiveDate);
    }


    /**
     * 检查电销表中的数据源是否相同，不相同则生成重复数据
     *
     * @param telMarketingCenter
     * @param source
     * @param sourceCreateTime
     */
    private TelMarketingCenterRepeat checkSource(TelMarketingCenter telMarketingCenter, TelMarketingCenterSource source,
                                                 Date sourceCreateTime, Long sourceId, String sourceTable, Channel channel, Date triggerTime, TelMarketingCenterStatus newStatus,Date effectiveDate) {
        //如果数据源不同，插入电销repeat表
        TelMarketingCenterRepeat telMarketingCenterRepeat = telMarketingCenterRepeatRepository.countByUserAndMobileAndSourceData(
            telMarketingCenter.getUser() == null ? null : telMarketingCenter.getUser().getId(),
            telMarketingCenter.getMobile(), source.getId(), sourceId, sourceTable);
        if (telMarketingCenterRepeat == null) {
            //生成重复数据修改center
            telMarketingCenter.setSource(source);
            if (triggerTime != null) {
                telMarketingCenter.setTriggerTime(triggerTime);
            }
//            if (telMarketingCenter.getOperator() != null) { //update by yinjianbin  重新进入电销,就算之前的operator为空,也修改为 display= true
            telMarketingCenter.setDisplay(true);
//            }
            if (source.getId().equals(TelMarketingCenterSource.Enum.INSURANCE_EXPIRE_DATE.getId()) || source.getId().equals(TelMarketingCenterSource.Enum.COMPULSORY_INSURANCE_EXPIRE_DATE.getId()))
                telMarketingCenter.setExpireTime(sourceCreateTime);

            //edit on 2018-07-19 续保首次进电销，默认为未分配状态
            if (source.getId().equals(TelMarketingCenterSource.Enum.RENEWAL_ONCE.getId())) {
                telMarketingCenter.setOperator(null);
                logger.debug("首次续保进电销ID:{}，设置跟进人为空", telMarketingCenter.getId());
            }

            if (newStatus != null)
                telMarketingCenter.setStatus(newStatus);
            telMarketingCenter.setSourceCreateTime(sourceCreateTime);
            isToA(telMarketingCenter, channel);//edit on 2017-07-10 如果是toA的来源,就分给 杜亚茹 , edit on 2017-07-18,根据用户的行为来源渠道来判断

            telMarketingCenterRepository.save(telMarketingCenter);

            if (source.getId().equals(TelMarketingCenterSource.Enum.QUOTE_RECORD.getId())) {
                sourceId = null;
                telMarketingCenterRepeat = telMarketingCenterRepeatRepository.findByUserAndMobileAndChannel(telMarketingCenter.getUser() == null ? null : telMarketingCenter.getUser().getId(), telMarketingCenter.getMobile(), source.getId(), channel);
                if (telMarketingCenterRepeat != null) {
                    return telMarketingCenterRepeat;
                }
            }

            return saveTelMarketingCenterRepeat(telMarketingCenter, source, sourceCreateTime, sourceId, sourceTable, channel,effectiveDate);
        }
        return telMarketingCenterRepeat;
    }

    /**
     * 根据电销中心数据生成重复数据
     *
     * @param telMarketingCenter
     * @param source
     * @param sourceCreateTime
     */
    public TelMarketingCenterRepeat saveTelMarketingCenterRepeat(TelMarketingCenter telMarketingCenter, TelMarketingCenterSource source,
                                                                 Date sourceCreateTime, Long sourceId, String sourceTable, Channel channel,Date effectiveDate) {
        TelMarketingCenterRepeat telMarketingCenterRepeat = new TelMarketingCenterRepeat();
        telMarketingCenterRepeat.setUser(telMarketingCenter.getUser());
        telMarketingCenterRepeat.setMobile(telMarketingCenter.getMobile());
        telMarketingCenterRepeat.setUserName(telMarketingCenter.getUserName());
        if (null == source) {
            telMarketingCenterRepeat.setSource(telMarketingCenter.getSource());
        } else {
            telMarketingCenterRepeat.setSource(source);
        }
        telMarketingCenterRepeat.setCreateTime(Calendar.getInstance().getTime());
        telMarketingCenterRepeat.setSourceCreateTime(sourceCreateTime);
        telMarketingCenterRepeat.setSourceId(sourceId);
        telMarketingCenterRepeat.setSourceTable(sourceTable);
        telMarketingCenterRepeat.setChannel(channel);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(effectiveDate);
        Integer renewalYear = calendar.get(Calendar.YEAR);
        telMarketingCenterRepeat.setRenewalDate(renewalYear.toString());
        return telMarketingCenterRepeatRepository.save(telMarketingCenterRepeat);
    }

    public List<TelMarketingCenter> findByMobile(String mobile) {
        List<TelMarketingCenter> telMarketingCenterList = new ArrayList<>();
        User user = userService.getBindingUser(mobile);
        if (user != null) {
            telMarketingCenterList = telMarketingCenterRepository.findByUser(user);
        }
        if (CollectionUtils.isEmpty(telMarketingCenterList)) {
            telMarketingCenterList = telMarketingCenterRepository.findByMobile(mobile);
        }
        return telMarketingCenterList;
    }

    public TelMarketingCenter findFirstByMobile(String mobile) {
        TelMarketingCenter telMarketingCenter = telMarketingCenterRepository.findFirstByMobileOrderByUpdateTimeDesc(mobile);
        return telMarketingCenter;
    }

//    public TelMarketingCenter findFirstByMobile(String mobile) {
//        User user = userService.getBindingUser(mobile);
//        if(user != null) {
//            TelMarketingCenter telMarketingCenter = telMarketingCenterRepository.findFirstByUser(user);
//            if(telMarketingCenter != null) {
//                return telMarketingCenter;
//            }
//        }
//        return telMarketingCenterRepository.findFirstByMobileOrderByUpdateTimeDesc(mobile);
//    }

    public TelMarketingCenter findByUser(User user) {
        if (user == null) return null;
        TelMarketingCenter telMarketingCenter = telMarketingCenterRepository.findFirstByUser(user);
        if (telMarketingCenter == null && StringUtils.isNotEmpty(user.getMobile())) {
            telMarketingCenter = findFirstByMobile(user.getMobile());
            if (telMarketingCenter != null) {
                telMarketingCenter.setUser(user);
                telMarketingCenter = this.save(telMarketingCenter);
            }
        }

        return telMarketingCenter;
    }

    public TelMarketingCenter save(TelMarketingCenter telMarketingCenter) {
        telMarketingCenter = telMarketingCenterRepository.save(telMarketingCenter);
        stringRedisTemplate.opsForList().leftPush(MOBILE_AREA_SYNC_QUEUE, telMarketingCenter.getMobile());
        return telMarketingCenter;
    }

    /**
     * 如果是toA的来源,就分给 toA 电销指定跟进人
     *
     * @param telMarketingCenter
     * @return
     */
    public TelMarketingCenter isToA(TelMarketingCenter telMarketingCenter, Channel channel) {
        if (channel.isAgentChannel()) {
            InternalUser toAOperator = internalUserRepository.findFirstByRoleId(Role.Enum.INTERNAL_USER_ROLE_TOA_QUOTE_OPERATOR.getId());
            if (toAOperator != null) {
                telMarketingCenter.setOperator(toAOperator);
            }
            logger.info("当前电销数据,telMarketingCenterId:[{}]为第三方渠道数据,分配给制定跟进人-->[{}].", telMarketingCenter.getId(), toAOperator == null ? "无指定跟进人" : toAOperator.getName());
        }
        return telMarketingCenter;
    }

    public void save(List<TelMarketingCenter> telMarketingCenters) {
        for (TelMarketingCenter telMarketingCenter : telMarketingCenters) {
            this.save(telMarketingCenter);
        }
    }

    /**
     * @param purchaseOrder
     * @param newStatus
     * @param operator      如果不设置则传null
     */
    public void refreshStatus(PurchaseOrder purchaseOrder, TelMarketingCenterStatus newStatus, InternalUser operator) {
        //更新电销表状态,并清空过期时间
        TelMarketingCenter telMarketingCenter = this.findByUser(purchaseOrder.getApplicant());
        if (telMarketingCenter != null) {
            telMarketingCenter.setStatus(newStatus);
            telMarketingCenter.setProcessedNumber(telMarketingCenter.getProcessedNumber() + 1L);
            telMarketingCenter.setExpireTime(null);
            telMarketingCenter.setTriggerTime(null);
            telMarketingCenter.setUpdateTime(new Date());
            telMarketingCenter.setDisplay(false);
            telMarketingCenter.setOperator(operator);

            telMarketingCenterRepository.save(telMarketingCenter);
        }

    }


}
