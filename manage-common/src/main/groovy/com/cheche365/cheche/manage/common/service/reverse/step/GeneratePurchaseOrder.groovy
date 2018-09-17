package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.AddressRepository
import com.cheche365.cheche.core.repository.DeliveryInfoRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.GiftService
import com.cheche365.cheche.core.service.InternalUserService
import com.cheche365.cheche.core.service.OrderAgentService
import com.cheche365.cheche.core.service.PurchaseOrderIdService
import com.cheche365.cheche.core.util.BeanUtil
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse
import groovy.util.logging.Slf4j
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by yellow on 2017/11/6.
 */
@Service
@Slf4j
class GeneratePurchaseOrder implements TPlaceInsuranceStep {

    @Transactional
    @Override
    Object run(Object context) {
        log.debug("------生成订单------")
        OrderReverse model = context.model
        QuoteRecord quoteRecord = context.quoteRecord
        PurchaseOrderRepository purchaseOrderRepository = context.purchaseOrderRepository
        PurchaseOrderIdService purchaseOrderIdService = context.purchaseOrderIdService
        OrderAgentService orderAgentService = context.orderAgentService
        InternalUserService internalUserService = context.internalUserService
        AddressRepository addressRepository = context.addressRepository
        DeliveryInfoRepository deliveryInfoRepository = context.deliveryInfoRepository
        InternalUserManageService internalUserManageService = context.internalUserManageService
        GiftService giftService = context.giftService
        PurchaseOrder prevOrder
        PurchaseOrder purchaseOrder = new PurchaseOrder()
        if (StringUtils.isNotEmpty(model.getOrderNo())) {
            prevOrder = purchaseOrderRepository.findFirstByOrderNo(model.getOrderNo())
            BeanUtil.copyPropertiesContain(prevOrder, purchaseOrder)
        } else {
            purchaseOrder.setType(OrderType.Enum.INSURANCE)
            purchaseOrder.setChannel(PaymentChannel.Enum.ALIPAY_1)
            purchaseOrder.setCreateTime(quoteRecord.getCreateTime())
            purchaseOrder.setOrderNo(purchaseOrderIdService.getNextByTime(OrderType.Enum.INSURANCE, quoteRecord.getCreateTime(), 1L))
            purchaseOrder.setDeliveryAddress(this.createAddress(quoteRecord.getApplicant(), quoteRecord.getArea(), addressRepository))
            purchaseOrder.setOrderSourceType(model.getOrderSourceType())
            purchaseOrder.appendDescription("该订单是由出单中心录入保单功能反向生成的订单，状态置为已完成")
        }
        purchaseOrder.setStatus(OrderStatus.Enum.FINISHED_5)
        purchaseOrder.setSourceChannel(quoteRecord.getChannel())
        purchaseOrder.setUpdateTime(new Date())
        purchaseOrder.setApplicant(quoteRecord.getApplicant())
        purchaseOrder.setAuto(quoteRecord.getAuto())
        purchaseOrder.setArea(quoteRecord.getArea())
        purchaseOrder.setObjId(quoteRecord.getId())
        purchaseOrder.setPayableAmount(model.getOriginalPremium())
        purchaseOrder.setPaidAmount(model.getRebateExceptPremium())
        purchaseOrder.setOperator(internalUserService.getRandomCustomer())
        purchaseOrder.setTrackingNo(model.getTrackingNo())
        purchaseOrder.setDeliveryInfo(createDeliveryInfo(purchaseOrder, model, deliveryInfoRepository, internalUserManageService))
        context.purchaseOrder = purchaseOrderRepository.save(purchaseOrder)
        orderAgentService.checkAgent(purchaseOrder)
        getContinueFSRV true
    }

    def createAddress(User user, Area area, AddressRepository addressRepository) {
        List<Address> addressList = addressRepository.findByApplicant(user)
        if (CollectionUtils.isNotEmpty(addressList)) {
            return addressList.get(0)
        } else {
            Address address = new Address()
            address.setApplicant(user)
            address.setStreet("北苑路北")
            address.setArea(area)
            address.setCity(area.getId().toString())
            address.setCreateTime(new Date())
            addressRepository.save(address)
        }
    }

    def createDeliveryInfo(PurchaseOrder purchaseOrder, OrderReverse model, DeliveryInfoRepository deliveryInfoRepository, InternalUserManageService internalUserManageService) {
        DeliveryInfo deliveryInfo = purchaseOrder.getDeliveryInfo()
        if (deliveryInfo == null) {
            deliveryInfo = new DeliveryInfo()
            deliveryInfo.setCreateTime(Calendar.getInstance().getTime())
        }
        deliveryInfo.setExpressCompany(model.getExpressCompany())
        deliveryInfo.setTrackingNo(model.getTrackingNo())
        deliveryInfo.setUpdateTime(Calendar.getInstance().getTime())

        deliveryInfoRepository.save(deliveryInfo)
    }
}
