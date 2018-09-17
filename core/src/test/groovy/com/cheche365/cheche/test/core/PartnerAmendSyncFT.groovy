package com.cheche365.cheche.test.core

import com.cheche365.cheche.core.service.AmendSyncObject
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.test.core.common.EntityCenter
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import net.sf.json.JSONArray
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by zhengwei on 2/10/17.
 */

class PartnerAmendSyncFT extends Specification {

    @Unroll
    def "增补同步报文格式校验"() {

        given:

        def purchaseOrderService = Stub(PurchaseOrderService) {
            getInsuranceBillsByOrder(_) >> EntityCenter.insurance
            getCIBillByOrder(_) >> EntityCenter.compulsoryInsurance
        }
        JsonParser parser = new JsonParser()
        AmendSyncObject syncObject = new AmendSyncObject(purchaseOrderService)

        expect:
        parser.parse(CacheUtil.doJacksonSerialize(syncObject.convert(partnerOrder, payments))) == parser.parse(expectedJson.toString())

        where:
        partnerOrder                           |   payments                      |   expectedJson
        EntityCenter.partnerOrderList.get(0)   |   EntityCenter.paymentList[0]   |   EntityCenter.expectedResult.getJSONObject(0)
        EntityCenter.partnerOrderList.get(1)   |   EntityCenter.paymentList[1]   |   EntityCenter.expectedResult.getJSONObject(1)
    }

}
