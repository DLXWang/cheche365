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
public class TelMarketingCenterQuotePhotoDataImportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterQuotePhotoDataImportService.class);

    @Autowired
    private QuotePhotoRepository quotePhotoRepository;
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TelMarketingCenterChannelFilterService taskExcludeChannelSettingService;
    /**
     * 导入拍照报价数据.
     */
    public void importQuotePhotoData() {
        logger.debug("import quote photo data");
        Date endTime = Calendar.getInstance().getTime();
        Date startTime = null;
        String previousTimeStr = stringRedisTemplate.opsForValue().get(TaskConstants.QUOTE_PHOTO_DATA_CACHE);
        if (StringUtils.isNotEmpty(previousTimeStr)) {
            startTime = DateUtils.getDate(previousTimeStr, DateUtils.DATE_LONGTIME24_PATTERN);
        } else {
            startTime = getFirstQueryTime(endTime);
        }
        List excludeChannelList= taskExcludeChannelSettingService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.QUOTE_PHOTO);
        Pageable pageable = new PageRequest(TaskConstants.PAGE_NUMBER, TaskConstants.PAGE_SIZE);
        org.springframework.data.domain.Page<QuotePhoto> quotePhotoPage =
            quotePhotoRepository.findPageDataByDate(startTime, endTime, excludeChannelList, pageable);
        List<QuotePhoto> quotePhotoList = quotePhotoPage.getContent();
        logger.debug("在{}到{}范围内的拍照报价第{}页的数量为{}",
            DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN),
            DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN),
            pageable.getPageNumber() + 1, quotePhotoList.size());
        if (CollectionUtils.isEmpty(quotePhotoList)) {
            return;
        }
        while(quotePhotoList.size() > 0) {
            saveTelMarketingCenterForQuotePhoto(quotePhotoList);
            if(quotePhotoList.size() < TaskConstants.PAGE_SIZE) {
                break;
            }
            pageable = pageable.next();
            quotePhotoPage = quotePhotoRepository.findPageDataByDate(startTime, endTime, excludeChannelList, pageable);
            quotePhotoList = quotePhotoPage.getContent();
            logger.debug("在{}到{}范围内的拍照报价第{}页的数量为{}",
                DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN),
                DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN),
                pageable.getPageNumber() + 1, quotePhotoList.size());
        }
        stringRedisTemplate.opsForValue().set(
            TaskConstants.QUOTE_PHOTO_DATA_CACHE,
            DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN));
    }

    private void saveTelMarketingCenterForQuotePhoto(List<QuotePhoto> quotePhotoList) {
        for (QuotePhoto quotePhoto : quotePhotoList) {
            try {
                User user = quotePhoto.getUser();
                Channel sourceChannel = quotePhoto.getUserImg().getSourceChannel() == null?
                    Channel.Enum.WAP_8 : quotePhoto.getUserImg().getSourceChannel();
                TelMarketingCenterSource source=TelMarketingCenterSource.Enum.SOURCE_CHANNEL_MAP.get(sourceChannel);
                source = source ==null ? TelMarketingCenterSource.Enum.PHOTO_APPOINTMENT :source;
                telMarketingCenterService.save(user, user.getMobile(), source, quotePhoto.getExpireDate(),
                    quotePhoto.getCreateTime(), quotePhoto.getId(), "quote_photo", sourceChannel);
            } catch (Exception ex) {
                logger.error("import quote Photo data has an error, id:{}", quotePhoto.getId(), ex);
            }
        }
    }

    private Date getFirstQueryTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
}
