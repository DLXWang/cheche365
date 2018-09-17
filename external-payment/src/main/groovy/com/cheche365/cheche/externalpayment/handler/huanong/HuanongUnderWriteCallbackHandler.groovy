package com.cheche365.cheche.externalpayment.handler.huanong

import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import com.cheche365.cheche.externalpayment.handler.SyncPurchaseOrderHandler
import com.cheche365.cheche.externalpayment.model.HuanongCallbackBody
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.externalpayment.model.HuanongCallbackBody.HUANONG_UNDERWRITE_STATUS

/**
 * Created by wen on 2018/8/7.
 */
@Service
@Slf4j
class HuanongUnderWriteCallbackHandler extends HuanongCallbackHandler{

    @Autowired
    private PurchaseOrderImageService poiService

    @Autowired
    SyncPurchaseOrderHandler syncOrderCenterHandler

    @Override
    boolean support(HuanongCallbackBody body) {
        body.isUnderWrite()
    }

    @Override
    def handle(HuanongCallbackBody body){

        OrderRelatedService.OrderRelated or = super.handle(body)
        Map currentStatus = HUANONG_UNDERWRITE_STATUS.get(body.status())

        log.debug("订单号 {} , 华农人工核保回调的核保状态 {} ",or.po.orderNo,currentStatus.desc)
        or.po.statusDisplay = currentStatus.statusDisplay ?: null

        if(currentStatus.toStatus){
            or.po.status = currentStatus.toStatus
        }

        if(body.isNeedVehicleExaminatios()){
            poiService.vehicleExaminatiosOnImage(or.po)
            log.debug("需上传验车照片，审核意见 {}, 核保时间 {}",body.auditOpinion(),body.auditTime())
        } else if(body.isNeedUploadImages()){
            poiService.persistCustomImage(or.po, body.auditOpinion())
            log.debug("自定义上传照片，审核意见 {}, 核保时间 {}",body.auditOpinion(),body.auditTime())
            return
        }


        log.debug("订单号 {} , 订单状态为 {} ，statusDisplay为 {}",or.po.orderNo,or.po.status.description,or.po.statusDisplay)

        or.toBePersist << or.po
        or.persist()
        syncOrderCenterHandler.syncOrderCenter(or.po)

    }


}
