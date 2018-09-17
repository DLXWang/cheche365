package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterChannelFilterService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterTaskType;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterExpireTimeDataImportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterExpireTimeDataImportService.class);
    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TelMarketingCenterChannelFilterService taskExcludeChannelSettingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuoteRecordRepository quoteRecordRepository;
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    /**
     * 通过起止日期导入数据到电销表.
     * @param type
     */
    @Transactional
    public void importInsuranceDataByDate(int type) {
        Date currentDate = Calendar.getInstance().getTime();
        Date startDate = null;
        Date endDate = DateUtils.getDate(currentDate,DateUtils.DATE_SHORTDATE_PATTERN);
        String redisKey = type == TaskConstants.COMMERCIAL_INSURANCE_EXPIRE_DATE ? TaskConstants.INSURANCE_CACHE : TaskConstants.COMPULSORY_INSURANCE_CACHE;
        String previousTimeStr = stringRedisTemplate.opsForValue().get(redisKey);
        logger.debug("从redis缓存中获取的类型为{}的定时任务上次执行时间是{}",
            type == TaskConstants.COMMERCIAL_INSURANCE_EXPIRE_DATE ? "商业险即将到期" : "交强险即将到期",previousTimeStr);
        if(!StringUtils.isEmpty(previousTimeStr)) {
            startDate = DateUtils.getDate(previousTimeStr,DateUtils.DATE_SHORTDATE_PATTERN);
        }
        List<Object[]> insuranceList = null;
        int startIndex = 0; int pageSize = TaskConstants.PAGE_SIZE;
        TelMarketingCenterTaskType taskType = type == TaskConstants.COMMERCIAL_INSURANCE_EXPIRE_DATE ? TelMarketingCenterTaskType.Enum.INSURANCE : TelMarketingCenterTaskType.Enum.COMPULSORY_INSURANCE;
        List<Channel> excludeChannels= taskExcludeChannelSettingService.findExcludeChannelsByTaskType(taskType);
        if(type == TaskConstants.COMMERCIAL_INSURANCE_EXPIRE_DATE) {
            insuranceList = insuranceRepository.findPageDataByExpireDate(startDate, endDate, startIndex, pageSize, excludeChannels);
        } else {
            insuranceList = compulsoryInsuranceRepository.findPageDataByExpireDate(startDate, endDate, startIndex, pageSize, excludeChannels);
        }
        logger.debug("从{}开始来源为{}的从第{}条的数量为{}",
            DateUtils.getDateString(startDate,DateUtils.DATE_SHORTDATE_PATTERN),
            type == TaskConstants.COMMERCIAL_INSURANCE_EXPIRE_DATE ? "商业险即将到期" : "交强险即将到期",
            startIndex, insuranceList.size());
        TelMarketingCenterSource source = type == TaskConstants.COMMERCIAL_INSURANCE_EXPIRE_DATE ?
            TelMarketingCenterSource.Enum.INSURANCE_EXPIRE_DATE : TelMarketingCenterSource.Enum.COMPULSORY_INSURANCE_EXPIRE_DATE;
        while(CollectionUtils.isNotEmpty(insuranceList)) {
            saveTelMarketingCenterForInsurance(insuranceList, source);
            if(insuranceList.size() < TaskConstants.PAGE_SIZE) {
                break;
            }
            startIndex += insuranceList.size();
            if(type == TaskConstants.COMMERCIAL_INSURANCE_EXPIRE_DATE) {
                insuranceList = insuranceRepository.findPageDataByExpireDate(startDate, endDate, startIndex, pageSize, excludeChannels);
            } else {
                insuranceList = compulsoryInsuranceRepository.findPageDataByExpireDate(startDate, endDate, startIndex, pageSize, excludeChannels);
            }
            logger.debug("从{}开始来源为{}的从第{}条的数量为{}",
                DateUtils.getDateString(startDate,DateUtils.DATE_SHORTDATE_PATTERN),
                type == TaskConstants.COMMERCIAL_INSURANCE_EXPIRE_DATE ? "商业险即将到期" : "交强险即将到期",
                startIndex, insuranceList.size());
        }
        stringRedisTemplate.opsForValue().set(redisKey,DateUtils.getDateString(currentDate, DateUtils.DATE_LONGTIME24_PATTERN));
    }

    private void saveTelMarketingCenterForInsurance(List<Object[]> insuranceList, TelMarketingCenterSource source) {
        for (Object[] object : insuranceList) {
            User user = userRepository.findById(Long.valueOf(object[0].toString()));
            if (null == user.getMobile()) {
                continue;
            }
            QuoteRecord quoteRecord = quoteRecordRepository.findOne(Long.valueOf(object[5].toString()));
            Channel channel = Channel.Enum.WAP_8;
            if(quoteRecord != null)
                channel = quoteRecord.getChannel();
            Date expireDate = DateUtils.getDate(object[1].toString(), DateUtils.DATE_SHORTDATE_PATTERN);
            telMarketingCenterService.save(user, user.getMobile(), source, expireDate, expireDate,
                Long.valueOf(object[3].toString()), object[4].toString(), channel);
        }
    }
}
