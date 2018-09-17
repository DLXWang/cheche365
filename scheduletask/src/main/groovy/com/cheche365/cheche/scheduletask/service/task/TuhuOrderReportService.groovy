package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.common.math.NumberUtils
import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.PurchaseOrderGiftService
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.scheduletask.model.TuhuEmailInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.lang.reflect.InvocationTargetException

/**
 * Created by zhangtc on 2018/1/23.
 */
@Service
class TuhuOrderReportService {

    def logger = LoggerFactory.getLogger(DatebaoOrderReportService.class)

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    PurchaseOrderGiftService purchaseOrderGiftService

    @Autowired
    PurchaseOrderService purchaseOrderService

    def getEmailDataList() {
        def yesterdayStart = DateUtils.getCustomDate(new Date(), -1, 00, 00, 00)
        def yesterdayEnd = DateUtils.getCustomDate(new Date(), -1, 23, 59, 59)
        def tuhuChannelList = Arrays.asList(
            Channel.Enum.ORDER_CENTER_TUHU_18.getId(),
            Channel.Enum.PARTNER_TUHUAPI_46.getId(),
            Channel.Enum.ORDER_CENTER_TUHUAPI_60.getId(),
            Channel.Enum.PARTNER_TUHU_203.getId())
        def fieldNameList = Arrays.asList("orderNo", "mobile", "licensePlateNo", "applicantName", "compulsoryPremium", "commecialPremium", "orderCreateTime", "cityName", "insuranceCompanyName", "giftDetail")

        def objectsList = purchaseOrderRepository.findTuhuOrders(yesterdayStart, yesterdayEnd, tuhuChannelList)
        objectsList.collect() { obj ->
            int i = fieldNameList.size() - 1
            Long orderId = Long.valueOf(obj[i].toString())
            obj[i] = purchaseOrderGiftService.getGiftInfo(orderId, purchaseOrderService.findById(orderId))
        }
        def emailInfoList = objectMapper(objectsList, fieldNameList)
        logger.info("途虎订单统计数据共[{}]条", emailInfoList.size())
        emailInfoList
    }

    def objectMapper(List<Object[]> objectsList, List<String> fieldNameList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        def emailInfoList = new ArrayList<>(objectsList.size())
        MetaClass metaClass = TuhuEmailInfo.metaClass
        for (int i = 0; i < objectsList.size(); i++) {
            def objects = objectsList.get(i)
            def model = new TuhuEmailInfo()
            for (int j = 0; j < fieldNameList.size(); j++) {
                String name = fieldNameList.get(j)
                def value = objects[j]
                if (value != null) {
                    if (value in BigDecimal) {
                        value = NumberUtils.toFinancialDouble((String) objects[j])
                        value = value.equals(0d) ? "0" : (value as String) + " "
                    } else {
                        value = (String) objects[j]
                    }

                    metaClass.setAttribute(model, name, value)
                }
            }
            model.getOrderCreateTime() && model.setOrderCreateTime(model.getOrderCreateTime().substring(0, model.getOrderCreateTime().indexOf(".")))

            emailInfoList << model
        }

        emailInfoList
    }
}
