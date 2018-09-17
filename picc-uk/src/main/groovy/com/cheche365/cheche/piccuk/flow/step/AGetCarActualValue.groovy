package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getEnrollDateText
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取车辆真实价格基类
 */
@Slf4j
abstract class AGetCarActualValue implements IStep {


    private static final _URL_GET_CAR_ACTUAL_VALUE = '/prpall/business/calActualValue.do'

    @Override
    run(context) {

        RESTClient client = context.client
        Auto auto = context.auto
        def selectedCarModel = context.selectedCarModel

        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_GET_CAR_ACTUAL_VALUE,
            body              : [
                'prpCitemCar.licenseNo'    : auto.licensePlateNo,
                'prpCitemCar.engineNo'     : auto.engineNo,
                'prpCitemCar.frameNo'      : auto.vinNo,
                'prpCitemCar.enrollDate'   : getEnrollDateText(context),
                'prpCitemCar.purchasePrice': selectedCarModel.priceT,
                'prpCitemCar.modelCode'    : selectedCarModel.vehicleId,
                'prpCmain.startDate'       : getCommercialInsurancePeriodTexts(context).first,
                'prpCitemCar.carKindCode'  : 'A01',
                'prpCmainCar.resureFundFee': '0',    // 交强险救助基金金额
                'prpCitemCar.clauseType'   : 'F42',  // 机动车综合条款（家庭自用汽车产品）
                'prpCitemCar.id.itemNo'    : '1',
            ]
        ]
        def actualValue = client.post args, { resp, reader ->
            reader.text
        }
        log.info '获取车辆真实价格结果：{}', actualValue
        if (actualValue) {
            context.carActualValue = actualValue
            returnFSRV context
        } else {
            getFatalErrorFSRV '获取车辆真实价格失败'
        }
    }

    protected abstract returnFSRV(context)

}
