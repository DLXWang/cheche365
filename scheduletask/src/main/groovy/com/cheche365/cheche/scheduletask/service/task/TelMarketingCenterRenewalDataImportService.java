package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.manage.common.model.*;
import com.cheche365.cheche.core.repository.TelMarketingCenterRepeatRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by chenxiangyin on 2018/05/08.
 */
@Service
public class TelMarketingCenterRenewalDataImportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterRenewalDataImportService.class);
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private InternalUserRepository internalUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AutoRepository autoRepository;
    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;
    @Autowired
    private TelMarketingCenterRepeatRepository telMarketingCenterRepeatRepository;

    private TelMarketingCenterRepeat saveData(Object[] sqlData) {
        try {
            Date latestMonth = DateUtils.getAroundMonthsDay(DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN), -1);
            List<TelMarketingCenterRepeat> telMarketingCenterRepeats = telMarketingCenterRepeatRepository.findRenewalCountByMobileAndDate(sqlData[1].toString(), latestMonth, new Date());
            //该手机一个月之内没分配过 则跟进人置空 变更为无跟进人进入续保数据
            if (telMarketingCenterRepeats.size() == 0) {
                User user = userRepository.findById(Long.parseLong(sqlData[0].toString()));
                String mobile = sqlData[1].toString();
                Date expireDate = DateUtils.getDate(sqlData[3].toString(), DateUtils.DATE_SHORTDATE_PATTERN);
                Date createDate = DateUtils.getDate(sqlData[4].toString(), DateUtils.DATE_LONGTIME24_PATTERN);
                Long sourceId = Long.parseLong(sqlData[5].toString());
                String sourceTable = sqlData[6].toString();
                Channel channel = Channel.toChannel(Long.parseLong(sqlData[7].toString()));
                Date effectiveDate = DateUtils.getDate(sqlData[9].toString(), DateUtils.DATE_LONGTIME24_PATTERN);
                return telMarketingCenterService.save(null, user, mobile,
                    TelMarketingCenterSource.Enum.RENEWAL_ONCE, expireDate, createDate, sourceId, sourceTable, channel, new Date(), TelMarketingCenterStatus.Enum.UNTREATED,effectiveDate);
            }
        } catch (Exception e) {
            logger.error("续保导入电销出现异常", e);
        }
        return null;
    }

    public void renewalIntoOrdercenter() {
        logger.debug("续保导入电销开始");

        List<Object[]> result = autoRepository.renewalCommercialInsurance();
        result.addAll(autoRepository.renewalCompulsoryInsurance());

        logger.debug("共计数据" + result.size());
        result.forEach(info -> saveData(info));
    }

//    private List<InternalUser> getUserList(){
//        return internalUserRepository.getUserByRolesAndEmails(Arrays.asList(29L),Arrays.asList("guanwy@cheche365.com","yangrui@cheche365.com","liuyuwan@cheche365.com","liuyw@cheche365.com","liuyuwan1@cheche365.com"));
//    }
//
//    private InternalUser getRandomUser(List<InternalUser> userList){
//        return userList.get(new Random().nextInt(userList.size()));
//    }

    public void renewalIntoOrdercenterTwice() {
        logger.debug("续保二次提醒开始");
        List<Object[]> result = telMarketingCenterRepository.findRemindByCommercialRenewal();
        List<TelMarketingCenterRepeat> repeatList = new ArrayList<>();
        result.addAll(telMarketingCenterRepository.findRemindByCompulsoryRenewal());
        result.forEach(obj -> {
            TelMarketingCenter info = telMarketingCenterRepository.findOne(Long.parseLong(obj[0].toString()));
            Long sourceId = Long.parseLong(obj[1].toString());
            String sourceTable = obj[2].toString();
            Channel channel = Channel.toChannel(Long.parseLong(obj[3].toString()));
            Date createTime = DateUtils.getDate(obj[4].toString(), DateUtils.DATE_LONGTIME24_PATTERN);
            Date effectiveDate = DateUtils.getDate(obj[5].toString(), DateUtils.DATE_LONGTIME24_PATTERN);
            repeatList.add(createRepeat(info, sourceId, sourceTable, channel, createTime,effectiveDate));
        });
        telMarketingCenterRepeatRepository.save(repeatList);
        logger.debug("续保二次提醒结束，新增repeat：" + repeatList.size());
    }

    public TelMarketingCenterRepeat createRepeat(TelMarketingCenter telMarketingCenter, Long sourceId, String sourceTable, Channel channel, Date createTime,Date effectiveDate) {
        TelMarketingCenterRepeat telMarketingCenterRepeat = new TelMarketingCenterRepeat();
        telMarketingCenterRepeat.setUser(telMarketingCenter.getUser());
        telMarketingCenterRepeat.setMobile(telMarketingCenter.getMobile());
        telMarketingCenterRepeat.setUserName(telMarketingCenter.getUserName());
        telMarketingCenterRepeat.setSource(TelMarketingCenterSource.Enum.RENEWAL_INSURANCE);
        telMarketingCenterRepeat.setCreateTime(Calendar.getInstance().getTime());
        telMarketingCenterRepeat.setSourceCreateTime(createTime);
        telMarketingCenterRepeat.setSourceId(sourceId);
        telMarketingCenterRepeat.setSourceTable(sourceTable);
        telMarketingCenterRepeat.setChannel(channel);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(effectiveDate);
        Integer renewalYear = calendar.get(Calendar.YEAR);
        telMarketingCenterRepeat.setRenewalDate(renewalYear.toString());
        return telMarketingCenterRepeat;

    }
}
