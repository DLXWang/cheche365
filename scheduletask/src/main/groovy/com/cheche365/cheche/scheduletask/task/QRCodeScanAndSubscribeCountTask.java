package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.QRCodeChannel;
import com.cheche365.cheche.core.model.QRCodeStatistics;
import com.cheche365.cheche.core.repository.QRCodeChannelRepository;
import com.cheche365.cheche.core.repository.QRCodeStatisticsRepository;
import com.cheche365.cheche.core.util.QRCodeChannelUtil;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.core.model.WechatQRCode;
import com.cheche365.cheche.core.repository.WechatQRCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 保存当前的二维码扫描关注数
 * 执行时间：每天0点
 * Created by sunhuazhong on 2015/8/4.
 */
@Service(value = "qrCodeScanAndSubscribeCountTask")
//@Service
public class QRCodeScanAndSubscribeCountTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(QRCodeScanAndSubscribeCountTask.class);

    // 微信二维码渠道：从redis load 关注数扫描数开关
    private String WECHAT_QRCODE_COUNT_LOADING_FLAG = "refresh.scan.subscribe.count.flag";
    // 微信二维码渠道：从redis load 关注数扫描数开关：打开
    private String WECHAT_QRCODE_COUNT_LOADING_FLAG_OPEN = "1";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private QRCodeChannelRepository qrCodeChannelRepository;

    @Autowired
    private QRCodeStatisticsRepository qrCodeStatisticsRepository;

    @Autowired
    private WechatQRCodeRepository wechatQRCodeRepository;


    /**
     * 执行任务详细内容
     *
     * @return
     */
    @Override
    public void doProcess() throws Exception {
        logger.debug("qrcode channel scan and subscribe count task is starting.");

        if (checkGetDataFlagFromRedis()) {
            try {
                Date currentTimeWithHour = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_HOUR_PATTERN);
                Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_SHORTDATE_PATTERN);
                String timeStr = stringRedisTemplate.opsForValue().get(QRCodeChannelUtil.getQRCodeChannelDateKey());
                logger.debug("上一次获取扫描关注数日期:{}", timeStr);
                if (timeStr == null) {
                    // 获取当前日期redis中的扫描关注数并保存
                    saveScanAndSubscribeCount(QRCodeChannelUtil.getCurrentKey(), currentTimeWithHour);
                } else {
                    Date statisticsTime = DateUtils.getDate(timeStr, DateUtils.DATE_SHORTDATE_PATTERN);
                    while (DateUtils.dateDiff(statisticsTime, currentTime, DateUtils.INTERNAL_DATE_DAY) >= 0) {
                        // 获取指定日期redis中的扫描关注数并保存
                        String key = QRCodeChannelUtil.getKey(
                            DateUtils.getDateString(statisticsTime, DateUtils.DATE_SHORTDATE_PATTERN));
                        saveScanAndSubscribeCount(key, currentTimeWithHour);
                        statisticsTime = getNextStatisticsTime(statisticsTime);
                    }
                }
                stringRedisTemplate.opsForValue().set(QRCodeChannelUtil.getQRCodeChannelDateKey(),
                    DateUtils.getDateString(currentTime, DateUtils.DATE_SHORTDATE_PATTERN));
            } catch (Exception ex) {
                logger.error("qrcode channel scan and subscribe count task error.", ex);
            } finally {
                resetGetDataFlagFromRedis();
            }
        }

        logger.debug("qrcode channel scan and subscribe count task is finished.");

    }


    private void resetGetDataFlagFromRedis() {
        stringRedisTemplate.delete(WECHAT_QRCODE_COUNT_LOADING_FLAG);
    }

    private boolean checkGetDataFlagFromRedis() {
        //获取loading 扫描数和关注数 开关，如果正在loading则不load redis中数据到数据库
        boolean isUnused = stringRedisTemplate.opsForValue().setIfAbsent(
            WECHAT_QRCODE_COUNT_LOADING_FLAG, WECHAT_QRCODE_COUNT_LOADING_FLAG_OPEN);
        // isUnUsed false:redis中数据跟保存数据相同，true：redis中数据跟保存数据不相同
        if (isUnused) {
            stringRedisTemplate.expire(WECHAT_QRCODE_COUNT_LOADING_FLAG, 1, TimeUnit.HOURS);
            return true;
        }
        return  false;
    }

    private Date getNextStatisticsTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private void saveScanAndSubscribeCount(String key, Date currentTimeWithHour) {
        List<String> deleteHashKeyList = new ArrayList<>();
        List<QRCodeStatistics> qrCodeStatisticsList = getScanAndSubscribeCount(key, deleteHashKeyList, currentTimeWithHour);
        // 保存二维码渠道的扫描数和关注数
        if (!CollectionUtils.isEmpty(qrCodeStatisticsList)) {
            for (QRCodeStatistics qrCodeStatistics : qrCodeStatisticsList) {
                logger.debug("二维码渠道获取到的redis数据： channel:{}, time:{}, scan:{}, subscribe:{}",
                    qrCodeStatistics.getQrCodeChannel().getId(),
                    DateUtils.getDateString(qrCodeStatistics.getStatisticsTime(), DateUtils.DATE_LONGTIME24_PATTERN),
                    qrCodeStatistics.getScanCount(),
                    qrCodeStatistics.getSubscribeCount());
                QRCodeStatistics existedQRCodeStatistics = qrCodeStatisticsRepository
                    .findFirstByQrCodeChannelAndStatisticsTime(
                        qrCodeStatistics.getQrCodeChannel(), qrCodeStatistics.getStatisticsTime());
                // 数据库中不存在该数据，并且扫描数或关注数不为0，则保存到数据库
                if (existedQRCodeStatistics == null) {
                    if(qrCodeStatistics.getScanCount() != 0 || qrCodeStatistics.getSubscribeCount() != 0) {
                        qrCodeStatisticsRepository.save(qrCodeStatistics);
                    }
                } else {
                    existedQRCodeStatistics.setScanCount(qrCodeStatistics.getScanCount());
                    existedQRCodeStatistics.setSubscribeCount(qrCodeStatistics.getSubscribeCount());
                    qrCodeStatisticsRepository.save(existedQRCodeStatistics);
                }
            }
        }

        // 删除距离当前日期相差1小时的redis数据
        if (!CollectionUtils.isEmpty(deleteHashKeyList)) {
            stringRedisTemplate.opsForHash().delete(key, deleteHashKeyList.toArray());
        }
    }

    private List<QRCodeStatistics> getScanAndSubscribeCount(String key, List<String> deleteHashKeyList, Date currentTimeWithHour) {
        Map<String, QRCodeChannel> qrCodeChannelMap = new ConcurrentHashMap<>();
        Map<String, QRCodeStatistics> qrCodeStatisticsMap = new HashMap<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().count(1000).build();
        Cursor<Map.Entry<Object, Object>> result = stringRedisTemplate.opsForHash().scan(key, scanOptions);
        while(result.hasNext()){
            Map.Entry<Object, Object> mapEntry = result.next();
            String hashKey = mapEntry.getKey().toString();
            String hashValue = mapEntry.getValue().toString();
            int startIndex = hashKey.indexOf(":");
            int endIndex  = hashKey.lastIndexOf(":");
            String qrCodeChannelId = hashKey.substring(0, startIndex);
            String statisticsTime  = hashKey.substring(startIndex + 1, endIndex);

            // 判断距离当前日期是否大于1小时
            if (checkOneHour(statisticsTime, currentTimeWithHour)) {
                deleteHashKeyList.add(hashKey);
            }
            QRCodeChannel qrCodeChannel = getQRCodeChannel(qrCodeChannelMap, qrCodeChannelId);
            if(qrCodeChannel == null) {
                continue;
            }
            Integer count = hashValue == null ? 0 : Integer.valueOf(hashValue);
            setQRCodeStatistics(qrCodeStatisticsMap, qrCodeChannel, hashKey, statisticsTime, count);
        }
        try {
            result.close();
        } catch (IOException e) {
            logger.error("result close error",e);
        }

        List<QRCodeStatistics> qrCodeStatisticsList = new ArrayList<>(qrCodeStatisticsMap.values());
        return qrCodeStatisticsList;
    }

    private boolean checkOneHour(String statisticsTime, Date currentTimeWithHour) {
        long hours = DateUtils.dateDiff(
            DateUtils.getDate(statisticsTime, DateUtils.DATE_LONGTIME24_PATTERN),
            currentTimeWithHour, DateUtils.INTERNAL_DATE_HOUR);
        if(hours >= 1) {
            return true;
        }
        return false;
    }

    private void setQRCodeStatistics(Map<String, QRCodeStatistics> qrCodeStatisticsMap, QRCodeChannel qrCodeChannel,
                                     String hashKey, String statisticsTime, Integer count) {
        String mapKey = qrCodeChannel.getId() + ":" + statisticsTime;
        QRCodeStatistics qrCodeStatistics = qrCodeStatisticsMap.get(mapKey);
        if (qrCodeStatistics == null) {
            qrCodeStatistics = new QRCodeStatistics();
            qrCodeStatistics.setQrCodeChannel(qrCodeChannel);
            qrCodeStatistics.setStatisticsTime(DateUtils.getDate(statisticsTime, DateUtils.DATE_LONGTIME24_PATTERN));
            qrCodeStatistics.setScanCount(0);
            qrCodeStatistics.setSubscribeCount(0);
            qrCodeStatisticsMap.put(mapKey, qrCodeStatistics);
        }
        if (hashKey.contains(TaskConstants.WECHAT_QRCODE_SCAN)) {
            qrCodeStatistics.setScanCount(count);//扫描数
        } else if (hashKey.contains(TaskConstants.WECHAT_QRCODE_SUBSCRIBE)) {
            qrCodeStatistics.setSubscribeCount(count);//关注数
        }
    }

    /**
     * 获取二维码渠道
     * 由于以前代码问题，导致参数赋值错误，部分是二维码渠道id，部分是二维码sceneId
     * @param qrCodeChannelMap
     * @param qrCodeChannelId
     * @return
     */
    private QRCodeChannel getQRCodeChannel(Map<String, QRCodeChannel> qrCodeChannelMap, String qrCodeChannelId) {
        QRCodeChannel qrCodeChannel = qrCodeChannelMap.get(qrCodeChannelId);
        if (qrCodeChannel == null) {
            // 二维码渠道
            qrCodeChannel = qrCodeChannelRepository.findOne(Long.valueOf(qrCodeChannelId));
            // 通过二维码sceneId获取渠道
            if (qrCodeChannel == null) {
                WechatQRCode wechatQRCode = wechatQRCodeRepository.findFirstBySceneId(Long.valueOf(qrCodeChannelId));
                qrCodeChannel = qrCodeChannelRepository.findFirstByWechatQRCodeAndDisable(wechatQRCode.getId(), false);
            }
            if (qrCodeChannel == null) {
                logger.warn("qrcode channel id:{} is not existed", qrCodeChannelId);
                return null;
            }
            qrCodeChannelMap.put(qrCodeChannelId, qrCodeChannel);
        }
        return qrCodeChannel;
    }
}
