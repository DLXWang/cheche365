package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.MarketingRepository;
import com.cheche365.cheche.core.repository.MarketingSuccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by mahong on 2015/10/29.
 */
@Service
@Transactional
public class MarketingSuccessService {

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository;

    public List<Marketing> getActiveMarketingActivities(String mobile, Long channelId) {
        List<Marketing> marketings = marketingRepository.findActiveMarketingActivities(channelId);
        for (Marketing marketing : marketings) {
            MarketingSuccess marketingSuccess = marketingSuccessRepository.findFirstByMobileAndMarketingId(mobile, marketing.getId());
            if (marketingSuccess != null) {
                marketing.setInvolved(true);
            } else {
                marketing.setInvolved(false);
            }
        }
        return marketings;
    }


    public MarketingSuccess getMarketingSuccessByGift(Gift gift) {
        if (SourceType.Enum.WECHATRED_2.getId().equals(gift.getSourceType().getId())) {
            return marketingSuccessRepository.findOne(gift.getSource());
        }
        return null;
    }

    public MarketingSuccess save(MarketingSuccess marketingSuccess) {
        return marketingSuccessRepository.save(marketingSuccess);
    }

    /**
     * 释放直减优惠
     *
     * @param order
     */
    public void releaseDirectReduce(PurchaseOrder order) {
        List<MarketingSuccess> marketingSuccesses = marketingSuccessRepository.findByDetailTableNameAndDetail("PURCHASE_ORDER", order.getId());
        if (marketingSuccesses == null || marketingSuccesses.isEmpty()) {
            return;
        }
        marketingSuccesses.forEach(it -> {
            order.appendDescription(",取消订单与marketing_success的关联关系:" + it.getDetail());
            it.setDetailTableName(null);
            it.setDetail(null);
        });
        marketingSuccessRepository.save(marketingSuccesses);
    }
}
