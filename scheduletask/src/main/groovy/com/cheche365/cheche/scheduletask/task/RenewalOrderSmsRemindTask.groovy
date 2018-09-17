package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.ScheduleCondition
import com.cheche365.cheche.core.repository.AutoRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler
import com.cheche365.cheche.core.service.sms.SmsCodeConstant
import groovy.util.logging.Slf4j
import org.apache.commons.collections4.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.DateUtils.getCustomDate
import static com.cheche365.cheche.common.util.DateUtils.getDateString

/**
 * 一件续保用户短信提醒task
 * Created by yinJianBin on 2018/02/01.
 */
@Component
@Slf4j
class RenewalOrderSmsRemindTask extends BaseTask {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    private AutoRepository autoRepository

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;

    @Override
    void doProcess() throws Exception {
        def currentDate = new Date()
        def before1Days = getDateString(getCustomDate(currentDate, 1, 0, 0, 0), DateUtils.DATE_SHORTDATE_PATTERN)
        def before8Days = getDateString(getCustomDate(currentDate, 8, 0, 0, 0), DateUtils.DATE_SHORTDATE_PATTERN)
        def before15Days = getDateString(getCustomDate(currentDate, 15, 0, 0, 0), DateUtils.DATE_SHORTDATE_PATTERN)
        def before22Days = getDateString(getCustomDate(currentDate, 22, 0, 0, 0), DateUtils.DATE_SHORTDATE_PATTERN)
        def before30Days = getDateString(getCustomDate(currentDate, 30, 0, 0, 0), DateUtils.DATE_SHORTDATE_PATTERN)
        def before45Days = getDateString(getCustomDate(currentDate, 45, 0, 0, 0), DateUtils.DATE_SHORTDATE_PATTERN)
        def before60Days = getDateString(getCustomDate(currentDate, 60, 0, 0, 0), DateUtils.DATE_SHORTDATE_PATTERN)
//        def before75Days = getDateString(getCustomDate(currentDate, 75, 0, 0, 0), DateUtils.DATE_SHORTDATE_PATTERN)
        def before90Days = getDateString(getCustomDate(currentDate, 90, 0, 0, 0), DateUtils.DATE_SHORTDATE_PATTERN)

        def autoList = autoRepository.findRenewalOrderAutoByExpireDate([before1Days, before8Days, before15Days, before22Days, before30Days, before45Days, before60Days, before90Days])
        log.info("一键续保短信提醒定时任务开始运行,获取到要发送续保短信的订单{}条", autoList.size())
        sendRenewRemindMessage(autoList)
    }

    private void sendRenewRemindMessage(List<Object[]> dataList) {
        if (CollectionUtils.isNotEmpty(dataList)) {
            Map<String, String> paramMap = [:]
            paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.RENEWAL_ORDER_REMIND.getId().toString());
            PurchaseOrder purchaseOrder = null
            for (Object[] obj : dataList) {
                purchaseOrder = purchaseOrderRepository.findSuccessByObjId(obj[3] as String)
                if (purchaseOrder != null) {
                    paramMap.put(SmsCodeConstant.RENEWAL_PAY_LINK, purchaseOrder.orderNo);//订单号
                    paramMap.put(SmsCodeConstant.AUTO_LICENSEPLATE_NO, obj[1] as String);//车牌号
                    paramMap.put(SmsCodeConstant.MOBILE, purchaseOrder.applicant.mobile);//手机号
                    paramMap.put(SmsCodeConstant.USER_NAME, obj[2] as String)//投保人姓名
                    paramMap.put(SmsCodeConstant.ORDER_EXPIRE_DATE, obj[4] as String);//失效日期
                    conditionTriggerHandler.process(paramMap);
                }
            }
        } else {
            log.debug("续保订单短信通知今日无符合条件的数据");
        }
    }
}
