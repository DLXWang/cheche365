package com.cheche365.cheche.developer.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.MoSyncOrderLog
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.mongodb.repository.MoHttpClientLogRepository
import com.cheche365.cheche.core.mongodb.repository.MoSyncOrderLogRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.developer.vo.SyncOrder
import com.cheche365.cheche.web.model.Message
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.developer.util.LogMessageUtils.formatMessage
import static com.cheche365.cheche.developer.util.StatusConstants.SUPPORT_STATUS
import static com.cheche365.cheche.web.integration.Constants._SYNC_ORDER_CHANNEL



/**
 * @Author shanxf
 * @Date 2018/4/21  15:48
 */
@Service
@Slf4j
class SyncOrderService {


    @Autowired
    private MoSyncOrderLogRepository moSyncOrderLogRepository
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository
    @Autowired
    private List<SyncOrderProcess> processes
    @Autowired
    private MoHttpClientLogRepository moHttpClientLogRepository

    void saveLog(SyncOrder syncOrder, String signHeader) {

        // 同步订单premium 反序列化之后是String 之后在进行FormattedDoubleSerializer.serialize 需要一个Double类型
        // 所以需要将premium 转成Double 在进行序列化
        syncOrder.fields.each {
            it.premium = Double.valueOf(it.premium)
        }

        log.info("sync order body message:{},signHeader:{}", CacheUtil.doJacksonSerialize(syncOrder), signHeader)

        moSyncOrderLogRepository.save(
            new MoSyncOrderLog().with {
                it.orderStatus = syncOrder.status
                it.orderNo = syncOrder.orderNo
                it.signHeader = signHeader
                it.requestBody = syncOrder
                it.createTime = new Date()
                it
            }
        )
    }

    List<Map> findSyncHistory(String orderNo) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(orderNo)
        if (!purchaseOrder) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "订单不存在!")
        }
        moHttpClientLogRepository.findByObjIdOrderByCreateTime(purchaseOrder.id.toString())?.collect {
            ["message": formatMessage(it.logMessage), "create_time": it.createTime]
        }
    }

    String syncOrder(String orderNo, Long status) {

        if (!SUPPORT_STATUS.contains(OrderStatus.Enum.findById(status))) {
            throw new BusinessException(BusinessException.Code.BAD_QUOTE_PARAMETER, "不支持该状态")
        }

        statusFlow(OrderStatus.Enum.findById(status), orderNo)?.each { orderStatus ->
            SyncOrderProcess syncService = processes.find { it.status().id == orderStatus.id }
            syncService.handle(orderNo)
        }

    }

    List<OrderStatus> statusFlow(OrderStatus orderStatus, String orderNo) {
        PurchaseOrder poInnerDb = purchaseOrderRepository.findFirstByOrderNo(orderNo)
        if (!poInnerDb) {
            throw new BusinessException(BusinessException.Code.BAD_QUOTE_PARAMETER, "无此订单")
        }

        if (poInnerDb.status == orderStatus) {
            _SYNC_ORDER_CHANNEL.send(new Message(poInnerDb))
            return null
        }

        if (!SUPPORT_STATUS.contains(poInnerDb.status) && orderStatus == OrderStatus.Enum.FINISHED_5) {
            return SUPPORT_STATUS
        } else {
            [(orderStatus)]
        }

    }
}
