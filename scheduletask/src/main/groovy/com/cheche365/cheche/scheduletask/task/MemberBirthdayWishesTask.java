package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员生日发送短信
 * 通过车车购买过车险的用户（排除代理人出单的用户）&符合车主与保单收货人姓名相同的用户
 * Created by sunhuazhong on 2016/1/29.
 */
@Service
public class MemberBirthdayWishesTask extends BaseTask {
    private Logger logger = LoggerFactory.getLogger(MemberBirthdayWishesTask.class);

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;

    @Override
    protected void doProcess() throws Exception {
        String currentDate = DateUtils.getCurrentDateString("MMdd");
        List<String> memberBirthdayMobileList = purchaseOrderService.findMemberBirthdayWishedUserMobile(currentDate);
        logger.debug("今天会员生日用户总数: {}", CollectionUtils.isEmpty(memberBirthdayMobileList) ? 0 : memberBirthdayMobileList.size());

        if (!CollectionUtils.isEmpty(memberBirthdayMobileList)) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.MEMBER_BIRTHDAY_WISHES.getId().toString());
            memberBirthdayMobileList.forEach(mobile -> {
                logger.debug("会员生日祝福短信，用户手机号:{}", mobile);
                paramMap.put("mobile", mobile);
                conditionTriggerHandler.process(paramMap);
            });
        } else {
            logger.debug("今天没有会员生日");
        }
    }
}
