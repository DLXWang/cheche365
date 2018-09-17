package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.manage.common.model.*;
import com.cheche365.cheche.core.repository.TelMarketingCenterRepeatRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterChannelFilterService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
 * Created by wangshaobin on 2016/12/21.
 */
@Service
public class TelMarketingCenterRegisterNoOperationDataImportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterRegisterNoOperationDataImportService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppointmentInsuranceRepository appointmentInsuranceRepository;
    @Autowired
    private QuotePhotoRepository quotePhotoRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private GiftRepository giftRepository;
    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository;
    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;
    @Autowired
    private TelMarketingCenterRepeatRepository telMarketingCenterRepeatRepository;
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TelMarketingCenterChannelFilterService taskExcludeChannelSettingService;

    /**
     * 注册但无行为用户数据.
     */
    public void importRegisterNoOperationData() {
        logger.debug("import register user no operation data");
        try {
            // 新用户(只有手机号，无预约、无拍照、无下过订单、无领过优惠券)
            Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
            String previousMaxUserId = stringRedisTemplate.opsForValue().get(TaskConstants.REGISTER_NO_OPERATION_USER_ID_CACHE);
            if (StringUtils.isEmpty(previousMaxUserId)) {
                User user = userRepository.findFirstByOrderById();
                previousMaxUserId = user.getId().toString();
            }
            String previousMaxAppointmentInsuranceId = stringRedisTemplate.opsForValue().get(TaskConstants.REGISTER_NO_OPERATION_APPOINTMENT_INSURANCE_ID_CACHE);
            String previousMaxQuotePhotoId = stringRedisTemplate.opsForValue().get(TaskConstants.REGISTER_NO_OPERATION_QUOTE_PHOTO_ID_CACHE);
            String previousMaxPurchaseOrderId = stringRedisTemplate.opsForValue().get(TaskConstants.REGISTER_NO_OPERATION_PURCHASE_ORDER_ID_CACHE);
            String previousMaxGiftId = stringRedisTemplate.opsForValue().get(TaskConstants.REGISTER_NO_OPERATION_GIFT_ID_CACHE);
            String previousMaxMarketingSuccessId = stringRedisTemplate.opsForValue().get(TaskConstants.REGISTER_NO_OPERATION_MARKETING_SUCCESS_ID_CACHE);
            String previousMaxTelMarketingCenterId = stringRedisTemplate.opsForValue().get(TaskConstants.REGISTER_NO_OPERATION_TEL_MARKETING_CENTER_ID_CACHE);
            if (StringUtils.isBlank(previousMaxAppointmentInsuranceId) || "\"null\"".equals(previousMaxAppointmentInsuranceId)
                || StringUtils.isBlank(previousMaxQuotePhotoId) || "\"null\"".equals(previousMaxQuotePhotoId)
                || StringUtils.isBlank(previousMaxPurchaseOrderId) || "\"null\"".equals(previousMaxPurchaseOrderId)
                || StringUtils.isBlank(previousMaxGiftId) || "\"null\"".equals(previousMaxGiftId)
                || StringUtils.isBlank(previousMaxMarketingSuccessId) || "\"null\"".equals(previousMaxMarketingSuccessId)
                || StringUtils.isBlank(previousMaxTelMarketingCenterId) || "\"null\"".equals(previousMaxTelMarketingCenterId)
            ) {
                Date previousExecuteTime = NumberUtils.createInteger(previousMaxUserId)!=0? userRepository.findOne(Long.valueOf(previousMaxUserId)).getCreateTime():DateUtils.getDayStartTime(new Date());
                previousMaxAppointmentInsuranceId = String.valueOf(appointmentInsuranceRepository.findMaxIdByTime(previousExecuteTime));
                previousMaxQuotePhotoId = String.valueOf(quotePhotoRepository.findMaxIdByTime(previousExecuteTime));
                previousMaxPurchaseOrderId = String.valueOf(purchaseOrderRepository.findMaxIdByTime(previousExecuteTime));
                previousMaxGiftId = String.valueOf(giftRepository.findMaxIdByTime(previousExecuteTime));
                previousMaxMarketingSuccessId = String.valueOf(marketingSuccessRepository.findMaxIdByTime(previousExecuteTime));
                previousMaxTelMarketingCenterId = String.valueOf(telMarketingCenterRepository.findMaxIdByTime(previousExecuteTime));
                logger.debug("更新注册但无行为用户的最大时间：{},最大用户id值:{}",previousExecuteTime,previousMaxUserId);
            }
            logger.debug("上次查询的注册但无行为用户的最大用户id值:{},最大主动预约id值:{},最大拍照预约id值:{},最大订单id值:{},最大礼品id值:{},最大参与活动id值:{},最大电销中心id值:{}",
                previousMaxUserId, previousMaxAppointmentInsuranceId, previousMaxQuotePhotoId,
                previousMaxPurchaseOrderId, previousMaxGiftId, previousMaxMarketingSuccessId, previousMaxTelMarketingCenterId);
            Integer checkUserCount = 0;// 已检查注册但无行为用户总数
            List<Channel> excludeChannelList = taskExcludeChannelSettingService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.REGISTER_NO_OPERATION);
            List<User> userList = userRepository.getRegisterNoOperationUsers(
                Long.valueOf(previousMaxUserId),
                Long.valueOf(previousMaxAppointmentInsuranceId),
                Long.valueOf(previousMaxQuotePhotoId),
                Long.valueOf(previousMaxPurchaseOrderId),
                Long.valueOf(previousMaxGiftId),
                Long.valueOf(previousMaxMarketingSuccessId),
                excludeChannelList);
            while (CollectionUtils.isNotEmpty(userList)) {
                checkUserCount = checkUserCount + userList.size();
                logger.debug("注册但无行为用户正在处理从{}条到{}条的数据", checkUserCount - userList.size(), checkUserCount);
                saveTelMarketingCenter(userList);
                Long maxUserId = userList.get(userList.size() - 1).getId();
                stringRedisTemplate.opsForValue().set(TaskConstants.REGISTER_NO_OPERATION_USER_ID_CACHE, maxUserId.toString());
                if (userList.size() < TaskConstants.PAGE_SIZE) {
                    break;
                }
                userList = userRepository.getRegisterNoOperationUsers(
                    maxUserId,
                    Long.valueOf(previousMaxAppointmentInsuranceId),
                    Long.valueOf(previousMaxQuotePhotoId),
                    Long.valueOf(previousMaxPurchaseOrderId),
                    Long.valueOf(previousMaxGiftId),
                    Long.valueOf(previousMaxMarketingSuccessId),
                    excludeChannelList);
            }
            setPreviousMaxId(currentTime);
        } catch (Exception ex) {
            logger.error("import register user no operation has an error.", ex);
        }
    }

    @Transactional
    public void saveTelMarketingCenter(List<User> userList) {
        List<TelMarketingCenter> telMarketingCenters = new ArrayList<>();
        List<TelMarketingCenterRepeat> telMarketingCenterRepeats = new ArrayList<>();

        for (User user : userList) {
            Date currentDate = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
            TelMarketingCenter telMarketingCenter = new TelMarketingCenter();
            telMarketingCenter.setUser(user);
            telMarketingCenter.setMobile(user.getMobile());
            telMarketingCenter.setUserName(user.getName());
            telMarketingCenter.setProcessedNumber(0L);
            telMarketingCenter.setStatus(TelMarketingCenterStatus.Enum.UNTREATED);
            telMarketingCenter.setPriority(1);
            telMarketingCenter.setSource(TelMarketingCenterSource.Enum.NO_BUY_RECOURD);
            telMarketingCenter.setUpdateTime(currentDate);
            telMarketingCenter.setCreateTime(currentDate);
            telMarketingCenter.setDisplay(true);
            telMarketingCenter.setSourceCreateTime(user.getCreateTime());
            telMarketingCenter.setExpireTime(null);
            telMarketingCenterService.isToA(telMarketingCenter, user.getRegisterChannel());//如果是toA的来源,就分给 杜亚茹 , edit on 2017-07-18,根据用户的行为来源渠道来判断

            telMarketingCenters.add(telMarketingCenter);

            TelMarketingCenterRepeat telMarketingCenterRepeat = new TelMarketingCenterRepeat();
            telMarketingCenterRepeat.setUser(telMarketingCenter.getUser());
            telMarketingCenterRepeat.setMobile(telMarketingCenter.getMobile());
            telMarketingCenterRepeat.setUserName(telMarketingCenter.getUserName());
            telMarketingCenterRepeat.setSource(telMarketingCenter.getSource());
            telMarketingCenterRepeat.setCreateTime(Calendar.getInstance().getTime());
            telMarketingCenterRepeat.setSourceCreateTime(user.getCreateTime());
            telMarketingCenterRepeat.setSourceId(user.getId());
            telMarketingCenterRepeat.setSourceTable("user");
            telMarketingCenterRepeat.setChannel(
                user.getRegisterChannel() == null ? Channel.Enum.WAP_8 : user.getRegisterChannel());
            telMarketingCenterRepeats.add(telMarketingCenterRepeat);
        }

        telMarketingCenterService.save(telMarketingCenters);
        telMarketingCenterRepeatRepository.save(telMarketingCenterRepeats);
    }

    private void setPreviousMaxId(Date currentTime) {
        stringRedisTemplate.opsForValue().set(TaskConstants.REGISTER_NO_OPERATION_APPOINTMENT_INSURANCE_ID_CACHE,
            String.valueOf(appointmentInsuranceRepository.findMaxIdByTime(currentTime)));
        stringRedisTemplate.opsForValue().set(TaskConstants.REGISTER_NO_OPERATION_QUOTE_PHOTO_ID_CACHE,
            String.valueOf(quotePhotoRepository.findMaxIdByTime(currentTime)));
        stringRedisTemplate.opsForValue().set(TaskConstants.REGISTER_NO_OPERATION_PURCHASE_ORDER_ID_CACHE,
            String.valueOf(purchaseOrderRepository.findMaxIdByTime(currentTime)));
        stringRedisTemplate.opsForValue().set(TaskConstants.REGISTER_NO_OPERATION_GIFT_ID_CACHE,
            String.valueOf(giftRepository.findMaxIdByTime(currentTime)));
        stringRedisTemplate.opsForValue().set(TaskConstants.REGISTER_NO_OPERATION_MARKETING_SUCCESS_ID_CACHE,
            String.valueOf(marketingSuccessRepository.findMaxIdByTime(currentTime)));
        stringRedisTemplate.opsForValue().set(TaskConstants.REGISTER_NO_OPERATION_TEL_MARKETING_CENTER_ID_CACHE,
            String.valueOf(telMarketingCenterRepository.findMaxIdByTime(currentTime)));
    }
}
