package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.QuoteRecordService;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangfei on 2016/3/31.
 */
@Service
public class FollowUpOrdersTask extends BaseTask {
    Logger logger = LoggerFactory.getLogger(FollowUpOrdersTask.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private SimpleDateFormat dateFormat_zh = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");

    private String emailConfigPath = "/emailconfig/follow_up_orders_email.yml";
    private String emailConfigPath_no = "/emailconfig/follow_up_no_orders_email.yml";

    private static final String START_TIME_CACHE_KEY = "schedules.task.order.followup.time";

    @Override
    protected void doProcess() throws Exception {
        Date today = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);

        Date yesterday;
        String lastStartTime = stringRedisTemplate.opsForValue().get(START_TIME_CACHE_KEY);
        if (StringUtils.isNotBlank(lastStartTime)) {
            logger.debug("从redis取得上次定时任务执行完毕时间{}记为本次查询数据的初始时间", lastStartTime);
            yesterday = DateUtils.getDate(lastStartTime, DateUtils.DATE_LONGTIME24_PATTERN);
        } else {
            logger.debug("redis未缓存上次任务执行完毕时间，本次查询数据初始时间记为当前时间前一天");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            yesterday = calendar.getTime();
        }

        String strYesterday = dateFormat_zh.format(yesterday);
        String strToday = dateFormat_zh.format(today);

        logger.debug("查询下单时间从{}至{}的需跟进订单", strYesterday, strToday);

        List<Long> excludeChannelIds= Arrays.asList(Channel.Enum.PARTNER_NCI_25.getId(),Channel.Enum.ORDER_CENTER_NCI_26.getId());
        List<PurchaseOrderInfo> purchaseOrderInfoList = getOrderInfoList(purchaseOrderRepository.getYesterdayFollowUpOrders(yesterday, today,excludeChannelIds));
        if (!CollectionUtils.isEmpty(purchaseOrderInfoList)) {
            logger.debug("查询下单时间从{}至{}的需跟进订单，订单总数量：{}", strYesterday, strToday, purchaseOrderInfoList.size());
        } else {
            logger.debug("执行完成查询需跟进订单定时任务，没有查询到需要跟进的订单");
        }
        messageInfoList.add(getMessageInfo(strYesterday, strToday, purchaseOrderInfoList));

        //将本次执行的时间记为下次执行取数据的开始时间
        stringRedisTemplate.opsForValue().set(START_TIME_CACHE_KEY, DateUtils.getDateString(today, DateUtils.DATE_LONGTIME24_PATTERN));
    }

    private MessageInfo getMessageInfo(String startTime, String endTime, List<PurchaseOrderInfo> purchaseOrderInfoList) throws IOException {
        // 邮件内容参数
        Map<String, Object> contentMap = new HashMap<>();

        contentMap.put("startTime", startTime);//统计开始时间
        contentMap.put("endTime", endTime);//统计结束时间

        //装配邮件信息
        EmailInfo emailInfo;
        if (!CollectionUtils.isEmpty(purchaseOrderInfoList)) {
            loadEmailConfig(emailConfigPath);
            emailInfo = assembleEmailInfo(emailConfigPath, contentMap);
            addSimpleAttachment(emailInfo, emailConfigPath, contentMap, purchaseOrderInfoList);
        } else {
            loadEmailConfig(emailConfigPath_no);
            emailInfo = assembleEmailInfo(emailConfigPath_no, contentMap);
        }

        return MessageInfo.createMessageInfo(emailInfo);
    }

    private List<PurchaseOrderInfo> getOrderInfoList(List<PurchaseOrder> purchaseOrderList) {
        if (CollectionUtils.isEmpty(purchaseOrderList)) {
            return null;
        }

        List<PurchaseOrderInfo> purchaseOrderInfoList = new ArrayList<>();

        purchaseOrderList.forEach(purchaseOrder -> {
            try {
                PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();

                purchaseOrderInfo.setOrderNo(purchaseOrder.getOrderNo());
                purchaseOrderInfo.setOrderTime(DateUtils.getDateString(purchaseOrder.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
                purchaseOrderInfo.setOrderStatus(null != purchaseOrder.getStatus() ? purchaseOrder.getStatus().getStatus() : "");
                purchaseOrderInfo.setSource(purchaseOrder.getSourceChannel().getDescription());

                purchaseOrderInfo.setLicenseNo(purchaseOrder.getAuto().getLicensePlateNo());
                purchaseOrderInfo.setOwner(purchaseOrder.getAuto().getOwner());
                purchaseOrderInfo.setCityName(purchaseOrder.getArea().getName());

                QuoteRecord quoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
                purchaseOrderInfo.setInsuranceCompany(quoteRecord.getInsuranceCompany().getName());

                purchaseOrderInfo.setPaidAmount(purchaseOrder.getPaidAmount().toString());
                purchaseOrderInfo.setLinkPhone(purchaseOrder.getApplicant().getMobile());

                purchaseOrderInfoList.add(purchaseOrderInfo);
            } catch (Exception ex) {
                logger.error("获取订单信息异常，跟进订单排除订单-> " + purchaseOrder.getOrderNo(), ex);
            }
        });

        return purchaseOrderInfoList;
    }
}
