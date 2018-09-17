package com.cheche365.cheche.rest.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.model.Marketing
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.MarketingRepository
import com.cheche365.cheche.core.service.AgentService
import com.cheche365.cheche.core.service.GiftService
import com.cheche365.cheche.marketing.service.MarketingService
import com.cheche365.cheche.marketing.service.MarketingServiceFactory
import com.cheche365.cheche.marketing.service.activity.Service201608002
import com.cheche365.cheche.rest.model.QuoteDiscountResult
import com.cheche365.cheche.web.ContextResource
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 1/6/17.
 */

@Service
@Slf4j
class QuoteMarketingService  extends ContextResource {

    @Autowired
    private AgentService agentService
    @Autowired
    private MarketingServiceFactory marketingServiceFactory
    @Autowired
    private GiftService giftService;
    @Autowired
    private MarketingRepository marketingRepository
    @Autowired
    private MarketingService marketingService

    void supportMarketing(QuoteRecord qr) {
        qr.marketingList = marketingService.supportMarketing(qr)
    }

    QuoteDiscountResult getQuoteDiscountResult(QuoteRecord cachedQuoteRecord, String marketingCode, Channel channel) {
        MarketingService marketingService = marketingServiceFactory.getService(marketingCode);
        if (null == marketingService) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "营销活动服务加载异常，活动代码" + marketingCode);
        }
        Marketing marketing = marketingService.getMarketingByCode(marketingCode);
        List<Gift> gifts;
        if (COMMON_MARKETING_CODE == marketingCode) {
            Service201608002 service201608002 = (Service201608002) marketingService;
            gifts = service201608002.attend(marketing, this.currentUser(), channel, cachedQuoteRecord);
            if (gifts != null && !gifts.isEmpty()) {
                gifts.get(0).setId(Marketing.generateCommonVirtualGiftId(this.currentUser()))
            }
        } else {
            try {
                marketingService.preCheck(marketing, this.currentUser().getMobile(), channel);
                marketingService.attend(marketing, this.currentUser(), channel, new HashMap<>());
            } catch (Exception e) {
                log.error("参加活动异常,{}", e);
            }
            gifts = giftService.getValidGiftsByQuoteRecordAndMarketing(this.currentUser(), cachedQuoteRecord, marketing);
        }
        QuoteDiscountResult quoteDiscountResult = new QuoteDiscountResult();
        quoteDiscountResult.setGift((gifts != null && gifts.size() > 0) ? gifts.get(0) : null);
        boolean needDiscount = (quoteDiscountResult.getGift() != null && quoteDiscountResult.getGift().getGiftAmount() != null);
        quoteDiscountResult.setPaidAmount(cachedQuoteRecord.calculatePaidAmount(needDiscount ? quoteDiscountResult.getGift().getGiftAmount() : 0d));
        return quoteDiscountResult;
    }

}
