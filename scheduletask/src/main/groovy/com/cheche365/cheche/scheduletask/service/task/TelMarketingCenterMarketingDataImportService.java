package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Marketing;
import com.cheche365.cheche.core.model.MarketingSuccess;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.MarketingRepository;
import com.cheche365.cheche.core.repository.MarketingSuccessRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.core.service.AgentService;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.manage.common.model.TaskImportMarketingSuccessData;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterTaskType;
import com.cheche365.cheche.manage.common.repository.TaskImportMarketingSuccessDataRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterChannelFilterService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterMarketingDataImportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterMarketingDataImportService.class);

    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private MarketingRepository marketingRepository;
    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TaskImportMarketingSuccessDataRepository taskImportMarketingSuccessDataRepository;
    @Autowired
    private TelMarketingCenterChannelFilterService taskExcludeChannelSettingService;

    public void importMarketingData() {
        List<TaskImportMarketingSuccessData> successDataList = taskImportMarketingSuccessDataRepository.findByEnable(true);
        successDataList.forEach(data -> {
            importMarketingSuccessData(
                    data.getCacheKey(),
                    data.getMarketing().getCode(),
                    data.getSource(),
                    data.getChannel()
            );
        });
    }

    private void importMarketingSuccessData(String dataCacheGroup, String marketingCode, TelMarketingCenterSource source, Channel defaultChannel) {
        logger.debug("import marketing data begin, marketingCode:{}", marketingCode);
        Date startTime = null;
        String previousTimeStr = stringRedisTemplate.opsForValue().get(dataCacheGroup);
        if (StringUtils.isNotEmpty(previousTimeStr)) {
            startTime = DateUtils.getDate(previousTimeStr, DateUtils.DATE_LONGTIME24_PATTERN);
        }
        Date endTime = Calendar.getInstance().getTime();
        Marketing marketing = marketingRepository.findFirstByCode(marketingCode);
        org.springframework.data.domain.Page<MarketingSuccess> marketingSuccessPage = null;
        List<MarketingSuccess> marketingSuccessList = null;
        Pageable pageable = new PageRequest(TaskConstants.PAGE_NUMBER, TaskConstants.PAGE_SIZE);
        List<Channel> excludeChannels = taskExcludeChannelSettingService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.MARKETING_SUCCESS);
        if (startTime == null) {
            marketingSuccessPage = marketingSuccessRepository.findPageDataByMarketingAndTimeUnUseGift(
                    marketing, endTime, excludeChannels, pageable);
        } else {
            marketingSuccessPage = marketingSuccessRepository.findPageDataByMarketingAndTimeUnUseGift(
                    marketing, startTime, endTime, excludeChannels, pageable);
        }
        marketingSuccessList = marketingSuccessPage.getContent();
        logger.debug("在{}到{}范围内的参与活动[{}]第{}页的数量为{}",
                DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN),
                DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN),
                marketing.getName(), pageable.getPageNumber() + 1, marketingSuccessList.size());
        if (CollectionUtils.isEmpty(marketingSuccessList)) {
            return;
        }
        while (marketingSuccessList.size() > 0) {
            saveTelMarketingCenterForMarketingSuccess(source, marketingSuccessList, defaultChannel);
            if (marketingSuccessList.size() < TaskConstants.PAGE_SIZE) {
                break;
            }
            pageable = pageable.next();
            if (startTime == null) {
                marketingSuccessPage = marketingSuccessRepository.findPageDataByMarketingAndTimeUnUseGift(
                        marketing, endTime, excludeChannels, pageable);
            } else {
                marketingSuccessPage = marketingSuccessRepository.findPageDataByMarketingAndTimeUnUseGift(
                        marketing, startTime, endTime, excludeChannels, pageable);
            }
            marketingSuccessList = marketingSuccessPage.getContent();
            logger.debug("在{}到{}范围内的参与活动[{}]第{}页的数量为{}",
                    DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN),
                    DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN),
                    marketing.getName(), pageable.getPageNumber() + 1, marketingSuccessList.size());
        }
        logger.debug("import marketing data success, marketingCode:{}", marketingCode);
        stringRedisTemplate.opsForValue().set(dataCacheGroup, DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN));
    }

    private void saveTelMarketingCenterForMarketingSuccess(TelMarketingCenterSource source, List<MarketingSuccess> marketingSuccessList, Channel defaultChannel) {
        for (MarketingSuccess marketingSuccess : marketingSuccessList) {
            if (marketingSuccess.getUserId() == null && StringUtils.isEmpty(marketingSuccess.getMobile())) {
                continue;
            }
            TelMarketingCenter telMarketingCenter = null;
            User user = null;
            if (marketingSuccess.getUserId() != null) {
                user = userRepository.findOne(marketingSuccess.getUserId());
                // 过滤代理人
                if (agentService.checkAgent(user))
                    continue;
                telMarketingCenter = telMarketingCenterService.findByUser(user);
            }
            if (telMarketingCenter == null && StringUtils.isNotEmpty(marketingSuccess.getMobile())) {
                user = userService.getBindingUser(marketingSuccess.getMobile());
                if (user != null) {
                    // 过滤代理人
                    if (agentService.checkAgent(user))
                        continue;
                    telMarketingCenter = telMarketingCenterRepository.findFirstByUser(user);
                }
                if (telMarketingCenter == null) {
                    telMarketingCenter = telMarketingCenterService.findFirstByMobile(marketingSuccess.getMobile());
                }
                if (telMarketingCenter != null && user != null && telMarketingCenter.getUser() == null) {
                    telMarketingCenter.setUser(user);
                    telMarketingCenter = telMarketingCenterService.save(telMarketingCenter);
                }
            }
            try {
                Channel channel = marketingSuccess.getChannel() == null ? defaultChannel : marketingSuccess.getChannel();
                telMarketingCenterService.save(telMarketingCenter, user, marketingSuccess.getMobile(),
                        source, null, marketingSuccess.getEffectDate(),
                        marketingSuccess.getId(), "marketing_success", channel, null, null,null);
            } catch (Exception e) {
                logger.debug("import marketing data error, marketingSuccess:{}", marketingSuccess.getId(), e);
            }
        }
    }
}
