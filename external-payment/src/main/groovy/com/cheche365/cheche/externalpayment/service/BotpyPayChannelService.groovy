package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.PaymentChannelRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderAttributeService
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.core.service.ThirdPartyPaymentTemplate
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.externalapi.api.botpy.BotpyGetAccountAPI
import com.cheche365.cheche.externalapi.api.botpy.BotpyPaymentChannelAPI
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.externalpayment.model.BotpyCallBackBody.PAYMENT_CHANNEL_CO_BANK

/**
 * Created by Administrator on 2018/3/6.
 */
@Service
class BotpyPayChannelService implements ThirdPartyPaymentTemplate{

    private Logger logger = LoggerFactory.getLogger(BotpyPayChannelService.class);
    @Autowired
    Environment env
    @Autowired
    private QuoteConfigService quoteConfigService
    @Autowired
    PurchaseOrderRepository purchaseOrderRepository
    @Autowired
    PaymentChannelRepository paymentChannelRepository
    @Autowired
    BotpyPaymentChannelAPI botpyPaymentChannelAPI
    @Autowired
    StringRedisTemplate stringRedisTemplate
    @Autowired
    BotpyGetAccountAPI botpyGetAccountAPI
    @Autowired
    OrderAttributeService orderAttributeService

    @Override
    boolean acceptable(QuoteRecord quoteRecord) {
        return QuoteSource.Enum.PLATFORM_BOTPY_11== quoteRecord.getType()
    }

    @Override
    Object prePay(PurchaseOrder purchaseOrder, Channel channel, QuoteRecord quoteRecord) {

        accountInfoHandle(purchaseOrder,quoteRecord)

        def resultChannels = botpyPaymentChannelAPI.call(purchaseOrder.orderSourceId)
        logger.debug("金斗云返回支付渠道 ${resultChannels}")
        if(!resultChannels){
            logger.error('金斗云返回支付渠道失败')
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'获取支付渠道失败')
        }

        def currentPaymentChannels=PaymentChannel.Enum.ALL.findAll {it.parentId ==  PaymentChannel.Enum.BOTPY_52.id}
        def paymentChannel = currentPaymentChannels.findAll {
            it.name in resultChannels.channels?.code
        }


        if(!paymentChannel){
            throw new BusinessException(BusinessException.Code.PAY_NOT_ALLOWED,'该渠道不支持在线支付')
        }

        stringRedisTemplate.opsForHash().put(PAYMENT_CHANNEL_CO_BANK,purchaseOrder.orderNo,CacheUtil.doJacksonSerialize(resultChannels))
        logger.debug("金斗云支付渠道信息缓存，原始报文:${resultChannels}")

        paymentChannel
    }

    def accountInfoHandle(PurchaseOrder purchaseOrder,QuoteRecord quoteRecord){
        def resultAccount = botpyGetAccountAPI.call(accountId(quoteRecord))
        if(!resultAccount?.info){
            logger.info("订单号 ${purchaseOrder.orderNo} 获取金斗云账户信息异常")
            return
        }

        orderAttributeService.savePurchaseOrderAttribute(purchaseOrder,AttributeType.Enum.BOTPY_ACCOUNT_1,resultAccount.info?.user)

    }

    String accountId(QuoteRecord quoteRecord){
        def prefixes = [quoteRecord.channel.parent.id, quoteRecord.insuranceCompany.id, quoteRecord.area.id].toArray()
        getEnvPropertyNew([env: env, configService: botpyPaymentChannelAPI.configService, namespace: 'botpy'], 'account_id', null, prefixes)
    }

}
