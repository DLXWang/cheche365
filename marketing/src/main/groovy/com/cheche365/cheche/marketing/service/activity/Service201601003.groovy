package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.model.GiftType
import com.cheche365.cheche.core.model.MarketingSuccess
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.GiftRepository
import com.cheche365.cheche.marketing.model.AttendResult
import com.cheche365.cheche.marketing.service.MarketingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by mahong on 2016/1/14.
 */
@Service
public class Service201601003 extends MarketingService {

    @Autowired
    private GiftRepository giftRepository;

    @Override
    protected String activityName() {
        return "扫描二维码送300元活动";
    }

    @Override
    protected AttendResult doAfterAttend(MarketingSuccess ms, User user, Map<String, Object> payload) {

        Gift gift = Gift.genGiftTemplate(user, GiftType.Enum.COUPON_3).with {
            it.reason = '营销活动赠送代金券'
            it.giftAmount = ms.amount
            it.giftDisplay = DoubleUtils.formatStripTrailingZeros(ms.amount)
            it.source = ms.id
            it.quantity = 1
            it.unit = "张"
            it.description = "扫描二维码送300元活动"
            it.usageRuleDescription = '商业和交强同时投保使用'
            it
        }

        this.giftRepository.save(gift);

        AttendResult attendResult = new AttendResult();
        attendResult.setMessage("成功参与活动");

        return attendResult;
    }
}
