package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 报价后保存报价信息
 */
@Component
@Slf4j
class SaveInsureInfo implements IStep {

    private static final _API_PATH_SAVE_INSUREINFO = '/ecar/insure/saveInsureInfo'

    @Override
    Object run(Object context) {
        RESTClient client = context.client
        def calculateResult = context.calculateResult
        def ecarvo = context.ecarvo
        if (context.carVo) {
            ecarvo = ecarvo + context.carVo
        }
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_SAVE_INSUREINFO,
            body              : [
                redata: [
                    ecarvo: ecarvo + calculateResult.ecarvo.subMap(
                        ['carModelRiskLevel', 'inType', 'lastyearPurchaseprice',
                         'lastyearModeltype', 'stCertificateValidity',
                         'lastyearModelcode', 'transferCompanyName'])
                ] + calculateResult.subMap([
                    'quotationNo', 'commercial', 'compulsory', 'commercialInsuransVo',
                    'compulsoryInsuransVo', 'quoteInsuranceVos', 'accident', 'commercialAgreementVos',
                    'isHolidaysDouble', 'ifAllroundClause', 'schemeId', 'platformVo', 'inType'

                ])
            ]
        ]
        def saveResult = client.post args, { resp, json ->
            json
        }
        if (saveResult.result) {
            log.debug '保存报价信息成功'
            getContinueFSRV saveResult.result
        } else {
            getFatalErrorFSRV '保存报价信息失败'
        }

    }
}
