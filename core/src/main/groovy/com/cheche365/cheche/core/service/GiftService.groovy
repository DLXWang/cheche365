package com.cheche365.cheche.core.service
import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.service.gift.rules.DefaultGiftRule
import com.cheche365.cheche.core.service.giftcode.GiftCodeExchange
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.text.DecimalFormat

import static com.cheche365.cheche.core.model.GiftStatus.Enum.*
import static com.cheche365.cheche.core.model.GiftType.Enum.*
/**
 * Created by zhengwei on 2015/4/20.
 */

@Service
@Slf4j
class GiftService {

    private Logger logger = LoggerFactory.getLogger(GiftService.class);

    @Autowired
    GiftRepository giftRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderGiftRepository pogRepo;

    @Autowired
    private GiftCodeRepository giftCodeRepository;

    @Autowired
    @Qualifier('defaultGiftRule')
    private DefaultGiftRule giftRule;

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private MarketingSuccessService marketingSuccessService;

    @Transactional
    Gift useGift(QuoteRecord quoteRecord, PurchaseOrder order, Long giftId) {
        Gift gift = isMyGift(giftId, order.getApplicant());
        if (gift == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "优惠券不存在");
        }
        if (!giftRule.check(gift, quoteRecord)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "优惠券校验失败，下单失败");
        }
        giftRule.beforePlaceOrder(gift, quoteRecord);
        gift.setStatus(USED_3);
        if (SourceType.Enum.PURCHASE_ORDER_1 == gift.getSourceType()) {
            gift.setSource(order.getId())
        }
        return save(gift);
    }

    Page<Gift> searchGift(User user, List<GiftStatus> giftStatus, Pageable pageable, Channel channel) {

        giftRepository
            .searchGifts(user.id, giftStatus.id, BILLABLE_GIFTS)
            .findAll {giftRule.checkGiftChannel(it, channel)}
            .with { sort (it) }
            .with { orderedGifts ->
                new Page<>(pageable.getPageNumber(), pageable.getPageSize(), orderedGifts? orderedGifts.size():0, orderedGifts)
            }
    }

    List<Gift> filterByRuleClass(List<Gift> gifts, QuoteRecord quoteRecord) {

        gifts
            .findAll {giftRule.check(it, quoteRecord)}
            .each {giftRule.beforePlaceOrder(it, quoteRecord)}
            .with { sort(it) }
    }

    static sort(List<Gift> gifts){
        gifts.groupBy {it.status}
            .values()
            .each { sameStatus -> sameStatus.sort {a, b -> b.comparableAmount() - a.comparableAmount() } }
            .sum() as List
    }

    List<Gift> getValidGiftsByQuoteRecordAndMarketing(User user, QuoteRecord quoteRecord, Marketing marketing) {

        giftRepository.searchValidGiftsByMarketing(user.id, ALL_VALID_IDS, BILLABLE_GIFTS_TYPE.id, marketing).with {
            filterByRuleClass(it, quoteRecord);
        }
    }

    Page<Gift> findByQR (User user, QuoteRecord quoteRecord, Pageable pageable) {

        this.giftRepository.searchGifts(user.id, ALL_VALID_IDS, BILLABLE_GIFTS_TYPE.id).with{
            filterByRuleClass(it, quoteRecord).with {gifts ->
                new Page<>(pageable.getPageNumber(), pageable.getPageSize(), (gifts?.size() ?: 0) as Long, gifts);
            }
        }
    }


    private List<Gift> getUsedGifts(QuoteRecord quoteRecord, Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderId);
        pogRepo.findByPurchaseOrder(purchaseOrder)
            .collect {it.gift}
            .findAll{GiftTypeUseType.Enum.REDUCE_1 == it.giftType.useType}
            .findAll {WebConstants.COMMON_MARKETING_CODE != marketingSuccessService.getMarketingSuccessByGift(it)?.marketing?.code}
            .with { filterByRuleClass(it, quoteRecord) }
    }

    @Transactional
    List<Gift> exchangeGiftCard(GiftCard giftCard, User user) {

        GiftCode giftCode = giftCodeRepository.findFirstByCode(giftCard.getId());

        validateGiftCode(giftCode);
        validateHasExchangedOtherGiftCode(giftCode, user);

        GiftCodeExchange giftCodeExchange = giftCode.getExchangeWay().createGiftCodeExchanggInstance();
        List<Gift> gifts = giftCodeExchange.exchangeGiftCode(giftCode, user);

        List<Gift> giftsAfterSave = this.giftRepository.save(gifts);

        updateGiftCode(giftCode, user)

        return giftsAfterSave;
    }

    @Transactional
    private void updateGiftCode(GiftCode giftCode, User user) {

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        giftCode.setExchangeTime(now);

        giftCode.setApplicant(user);
        giftCode.setExchanged(true);

        giftCodeRepository.save(giftCode);
    }

    private void validateHasExchangedOtherGiftCode(GiftCode giftCode, User user) {
        List<GiftCode> giftCodes = giftCodeRepository.findByApplicantAndExchangeWayAndExchangedTrue(user, giftCode.exchangeWay);
        if (giftCodes) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "当前用户已经兑换过其他优惠码")
        }
    }

    private static void validateGiftCode(GiftCode giftCode) {

        if (!giftCode || giftCode.exchanged) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "优惠码不存在");
        }

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        if (giftCode.effectiveDate && giftCode.expireDate) {
            Date effectiveDate = giftCode.effectiveDate;
            Date expireDate = DateUtils.addDays(giftCode.expireDate, 1);
            if (!(now.after(effectiveDate) && now.before(expireDate))) {
                throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "不在优惠码兑换有效期范围内");
            }
        }
        if (!giftCode.effectiveDate && giftCode.expireDate) {
            Date expireDate = DateUtils.addDays(giftCode.expireDate, 1);
            if (!now.before(expireDate)) {
                throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "不在优惠码兑换有效期范围内");
            }
        }
        if (giftCode.effectiveDate && !giftCode.expireDate) {
            Date effectiveDate = giftCode.effectiveDate;
            if (!now.after(effectiveDate)) {
                throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "不在优惠码兑换有效期范围内");
            }
        }

    }

    @Transactional
    void resetOrderGift(PurchaseOrder purchaseOrder) {
        List<PurchaseOrderGift> orderGiftList = pogRepo.findByPurchaseOrder(purchaseOrder);
        if (orderGiftList != null && !orderGiftList.isEmpty()) {
            logger.info("reset gift status by purchaseOrder -> orderId : {}", purchaseOrder.getId());
            List<Gift> giftList = new ArrayList<>();
            orderGiftList.collect { orderGift ->
                Gift gift = orderGift.getGift();
                if (orderGift.isGivenAfterOrder()) {
                    gift.setStatus(CANCLED_5);
                    gift.appendDescription(",订单取消，重置礼品状态为已取消");
                } else {
                    if (gift.isExpired()) {
                        gift.setStatus(EXCEEDED_4);
                        gift.appendDescription(",订单取消，重置礼品状态为已过期");
                    } else {
                        gift.setStatus(CREATED_1);
                        gift.appendDescription(",订单取消，重置礼品状态为已创建");
                    }
                }

                giftRule.processAfterReleaseGift(gift);
                gift.setUpdateTime(Calendar.getInstance().getTime());
                giftList.add(gift);
            }
            giftRepository.save(giftList);
            pogRepo.delete((Collection<PurchaseOrderGift>) orderGiftList);
        }
    }


    static Double calculateGiftReduceAmount(Gift gift, QuoteRecord quoteRecord) {
        if (COUPON_3 == gift.giftType) {
            Double payableAmount = DoubleUtils.displayDoubleValue(quoteRecord.getTotalPremium() - DoubleUtils.doubleValue(quoteRecord.getAutoTax()));
            return payableAmount < gift.getGiftAmount() ? payableAmount : gift.getGiftAmount();
        }
        return gift.getGiftAmount();
    }

    List<Gift> getGiftByOrder(PurchaseOrder purchaseOrder) {
        List<Gift> gifts = new ArrayList<>();
        List<PurchaseOrderGift> orderGifts = pogRepo.findByPurchaseOrderAndGivenAfterOrder(purchaseOrder, false);
        if (orderGifts != null && !orderGifts.isEmpty()) {
            for (PurchaseOrderGift orderGift : orderGifts) {
                gifts.add(orderGift.getGift());
            }
            return gifts;
        }
        return null;
    }

    List<Gift> findGiftsByOrder(sourceData) {
        PurchaseOrder po = sourceData.purchaseOrder
        Double ccPaidAmount = PaymentAmountCalculator.chechePaid(sourceData.payments).toDouble()
        Gift reduceGift = ccPaidAmount ? Gift.genGiftTemplate(po.applicant, CASH_34).with {
            status = USED_3
            giftAmount = ccPaidAmount
            giftDisplay = new DecimalFormat("0.00").format(ccPaidAmount)
            it
        } : null
        pogRepo.findByPurchaseOrder(po)
            .findAll { GiftTypeUseType.Enum.REDUCE_1 != it.gift.giftType.useType }.gift
            .with { realGifts ->
            reduceGift ? (realGifts << reduceGift) : null
            realGifts
        }
    }

    List<Gift> getExpireDatePutGray(String nowDateTime, Pageable pageable) {
        return this.giftRepository.getExpireDatePutGray(nowDateTime, pageable);
    }

    long getCountByExpireDate(String nowDateTime) {
        Object[] obj = giftRepository.getCountByExpireDate(nowDateTime);
        return Long.parseLong(Objects.toString(obj[0], "0"));
    }

    @Transactional
    Gift save(Gift gift) {
        return this.giftRepository.save(gift);
    }

    @Transactional
    List<Gift> save(List<Gift> gifts) {
        return this.giftRepository.save(gifts);
    }

    Gift isMyGift(Long id, User user) {
        return this.giftRepository.getGift(id, user.id);
    }

    public void mergeUsedGifts(QuoteRecord quoteRecord, Long purchaseOrderId, List<Gift> gifts) {
        def usedGifts = getUsedGifts(quoteRecord, purchaseOrderId)
        usedGifts && gifts.addAll(usedGifts)
    }

    List<Gift> getGiftByUser(User user, String status) {
        if (StringUtils.isNoneBlank(status))
            return giftRepository.selectByApplicantAndStatus(user.getId(), status);
        else
            return giftRepository.findByApplicant(user);
    }
}
