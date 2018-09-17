package com.cheche365.cheche.partner.service.order

import com.cheche365.cheche.core.model.InsurancePurchaseOrderRebate
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.PartnerOrderSync
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.partner.api.ApiLoader
import com.cheche365.cheche.web.model.Message
import com.cheche365.cheche.web.service.InsurancePurchaseOrderRebateService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_AGENT_URL
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey

/**
 * Created by mahong on 2016/2/20.
 */
@Slf4j
@Service
class PartnerOrderService {

    @Autowired
    private PartnerOrderRepository partnerOrderRepository
    @Autowired
    private PartnerUserRepository partnerUserRepository
    @Autowired
    private InsurancePurchaseOrderRebateService insurancePurchaseOrderRebateService
    @Autowired
    private ApiLoader apiLoader
    @Autowired
    private InsurancePurchaseOrderRebateRepository orderRebateRepository
    @Autowired
    private PartnerOrderSyncRepository partnerOrderSyncRepository
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository

    /**
     * 同步订单信息到第三方
     */
    void syncPurchaseOrder(Map orderPaymentMap, boolean isNew = false) {
        PurchaseOrder purchaseOrder = CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(orderPaymentMap.get("order")), PurchaseOrder.class)
        if (!purchaseOrder.getSourceChannel().isThirdPartnerChannel() || !purchaseOrder.sourceChannel.apiPartner.needSyncOrder()) {
            return
        }
        log.debug("同步数据到第三方，订单号:{}，订单状态:{}，订单来源:{}，用户ID:{}，用户手机号:{}",
            purchaseOrder.getOrderNo(), purchaseOrder.getStatus().getStatus(),
            purchaseOrder.getSourceChannel() == null ? "无" : purchaseOrder.getSourceChannel().getName(),
            purchaseOrder.getApplicant().getId(), purchaseOrder.getApplicant().getMobile())

        PartnerOrder partnerOrder = partnerOrderRepository.findFirstByPurchaseOrderId(purchaseOrder.getId())
        // 出单中心反录时没有partnerOrder，同步需new一个，不做持久化
        if (partnerOrder == null) {
            log.debug("同步订单未找到{}订单关联信息,订单号为 {}", purchaseOrder.sourceChannel.apiPartner.description, purchaseOrder.orderNo)
            partnerOrder = new PartnerOrder(apiPartner: purchaseOrder.sourceChannel.apiPartner, purchaseOrder: purchaseOrder)
        }

        partnerOrder.setPurchaseOrder(purchaseOrder)  //出单中心调用时候事务没结束，状态没有更新
        calculateRebate(purchaseOrder)

        def api = apiLoader.findApi(partnerOrder.apiPartner, partnerOrder)
        def syncOrderMap = ["partnerOrder": partnerOrder]

        if (orderPaymentMap.containsKey("payments")) {
            syncOrderMap.put("payments", orderPaymentMap.get("payments"))
            syncOrderMap.put("insurance", orderPaymentMap.get("insurance"))
            syncOrderMap.put("compulsoryInsurance", orderPaymentMap.get("compulsoryInsurance"))
        }

        if (api) {
            if (isNew) {
                api.newCall(syncOrderMap)
            } else {
                api.call(syncOrderMap)
            }
        } else {
            log.debug("无对应订单'{}'状态的同步api, 忽略同步操作", partnerOrder.purchaseOrder?.status.status)
        }
    }

    void syncChannelAgent(Message<ChannelAgent> message) {
        def channel = message.payload.channel
        def apiPartner = channel.apiPartner
        if (apiPartner?.needSyncOrder() && channel.levelAgent && findByPartnerAndKey(apiPartner, SYNC_AGENT_URL)) {
            apiLoader.findAgentApi(apiPartner)?.call([
                headers     : message.headers,
                channelAgent: message.payload,
                partnerUser : partnerUserRepository.findFirstByPartnerAndUser(apiPartner, message.payload.user)
            ])
        }
    }

    void calculateRebate(PurchaseOrder purchaseOrder) {
        InsurancePurchaseOrderRebate insurancePurchaseOrderRebate = orderRebateRepository.findFirstByPurchaseOrder(purchaseOrder)
        if (insurancePurchaseOrderRebate) {
            insurancePurchaseOrderRebateService.updateChannelRebate(insurancePurchaseOrderRebateService.upType, insurancePurchaseOrderRebate?.getUpCompulsoryRebate(),
                insurancePurchaseOrderRebate?.getUpCommercialRebate(), purchaseOrder)
        }
    }

    void saveSyncOrderResultMessage(Object apiInput, boolean success, String requestURL, String requestBody, String responseBody) {
        PartnerOrder partnerOrder = (PartnerOrder) apiInput
        PartnerOrderSync partnerOrderSync = new PartnerOrderSync()
        partnerOrderSync.setPartnerOrder(partnerOrder)
        partnerOrderSync.setStatus(success ? PartnerOrderSync.Enum.SUCCESSE : PartnerOrderSync.Enum.FAIL)
        partnerOrderSync.setSendSyncMessage(requestURL + " " + requestBody)
        partnerOrderSync.setSyncBody(requestBody)
        partnerOrderSync.setReceiveSyncMessage(responseBody)
        partnerOrderSync.setPurchaseOrderStatus(partnerOrder.getPurchaseOrder().getStatus())
        PartnerOrderSync partnerOrderSyncParent = partnerOrderSyncRepository.findPartnerOrderAndOrderStatus(partnerOrder, partnerOrder.getPurchaseOrder().getStatus(), PartnerOrderSync.Enum.FAIL)
        partnerOrderSync.setSendSyncParent(partnerOrderSyncParent != null ? partnerOrderSyncParent : null)
        partnerOrderSyncRepository.save(partnerOrderSync)
    }

}
