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
public class TelMarketingCenterAppointmentInsuranceDataImportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterAppointmentInsuranceDataImportService.class);

    @Autowired
    private AppointmentInsuranceRepository appointmentInsuranceRepository;
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TelMarketingCenterChannelFilterService taskExcludeChannelSettingService;
    /**
     * 导入主动预约数据.
     */
    public void importAppointmentInsuranceData() {
        logger.debug("import appointment insurance data");
        Date endTime = Calendar.getInstance().getTime();
        Date startTime = null;
        String previousTimeStr = stringRedisTemplate.opsForValue().get(TaskConstants.APPOINTMENT_INSURANCE_DATA_CACHE);
        if (StringUtils.isNotEmpty(previousTimeStr)) {
            startTime = DateUtils.getDate(previousTimeStr, DateUtils.DATE_LONGTIME24_PATTERN);
        } else {
            startTime = getFirstQueryTime(endTime);
        }
        List excludeChannelList= taskExcludeChannelSettingService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.APPOINTMENT_INSURANCE);
        Pageable pageable = new PageRequest(TaskConstants.PAGE_NUMBER, TaskConstants.PAGE_SIZE);
        org.springframework.data.domain.Page<AppointmentInsurance> appointmentInsurancePage =
            appointmentInsuranceRepository.getPageDataByDate(startTime, endTime,excludeChannelList, pageable);
        List<AppointmentInsurance> appointmentInsuranceList = appointmentInsurancePage.getContent();
        logger.debug("在{}到{}范围内的主动预约第{}页的数量为{}",
            DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN),
            DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN),
            pageable.getPageNumber() + 1, appointmentInsuranceList.size());
        if (CollectionUtils.isEmpty(appointmentInsuranceList)) {
            return;
        }
        while(appointmentInsuranceList.size() > 0) {
            saveTelMarketingCenterForAppointmentInsurance(appointmentInsuranceList);
            if(appointmentInsuranceList.size() < TaskConstants.PAGE_SIZE) {
                break;
            }
            pageable = pageable.next();
            appointmentInsurancePage = appointmentInsuranceRepository.getPageDataByDate(startTime, endTime,excludeChannelList, pageable);
            appointmentInsuranceList = appointmentInsurancePage.getContent();
            logger.debug("在{}到{}范围内的主动预约第{}页的数量为{}",
                DateUtils.getDateString(startTime, DateUtils.DATE_LONGTIME24_PATTERN),
                DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN),
                pageable.getPageNumber() + 1, appointmentInsuranceList.size());
        }
        stringRedisTemplate.opsForValue().set(
            TaskConstants.APPOINTMENT_INSURANCE_DATA_CACHE,
            DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN));
    }

    private void saveTelMarketingCenterForAppointmentInsurance(List<AppointmentInsurance> appointmentInsuranceList) {
        for (AppointmentInsurance appointmentInsurance : appointmentInsuranceList) {
            try {
                User user = appointmentInsurance.getUser();
                Channel sourceChannel = appointmentInsurance.getSourceChannel() == null?
                    Channel.Enum.WAP_8 : appointmentInsurance.getSourceChannel();
                TelMarketingCenterSource source=getSource(appointmentInsurance,sourceChannel);
                telMarketingCenterService.save(user, user.getMobile(),
                    source, appointmentInsurance.getExpireBefore(), appointmentInsurance.getCreateTime(),
                    appointmentInsurance.getId(), "appointment_insurance", sourceChannel);
            } catch (Exception ex) {
                logger.error("import appointment insurance data has an error, id:{}", appointmentInsurance.getId(), ex);
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

    private TelMarketingCenterSource getSource(AppointmentInsurance appointmentInsurance,Channel sourceChannel){
        TelMarketingCenterSource source=(TelMarketingCenterSource)TelMarketingCenterSource.Enum.SOURCE_CHANNEL_MAP.get(sourceChannel);
        //按天买车险，暂时取消
//        if(appointmentInsurance.getSource().equals(AppointmentInsurance.Enum.SOURCE_INSURANCE_BY_DAY_APPOINTMENT)){
//            source=TelMarketingCenterSource.Enum.INSURANCE_DAY_APPOINTMENT;
//        }
        return source==null?TelMarketingCenterSource.Enum.APPOINTMENT:source;
    }
}
