package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.baoxian.flow.step.AUpdateQuoteInfo
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import java.time.LocalDate

import static com.cheche365.cheche.common.Constants.get_DATETIME_FORMAT3
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV

/**
 * 修改数据接口：传关系人信息和配送信息
 * @author zhaoym
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class UpdateQuoteInfoForInsure extends AUpdateQuoteInfo {


    private static final _INSURE_SUPPLYS_MAPPINGS =
        [
            claimantEmail            : { context -> getEnvProperty(context,'baoxian.email') },
            ownerEmail               : { context -> getEnvProperty(context,'baoxian.email') },
            insuredEmail             : { context -> getEnvProperty(context,'baoxian.email') },
            applicantEmail           : { context -> getEnvProperty(context,'baoxian.email') },
            drivingLicenseAddress    : { context -> context.order.deliveryAddress.address },
            insuredAddress           : { context -> context.order.deliveryAddress.address },
            ownerAddress             : { context -> context.order.deliveryAddress.address },
            applicantAddress         : { context -> context.order.deliveryAddress.address },
            applicantMobile          : { context -> context.order.applicant.mobile },
            insuredMobile            : { context -> context.order.applicant.mobile },
            ownerMobile              : { context -> context.order.applicant.mobile },
            ownerIDCardValidDate     : { context -> _DATETIME_FORMAT3.format LocalDate.now().plusYears(10) },
            insuredIDCardValidDate   : { context -> _DATETIME_FORMAT3.format LocalDate.now().plusYears(10) },
            applicantIDCardValidDate : { context -> _DATETIME_FORMAT3.format LocalDate.now().plusYears(10) },
        ]

    @Override
    protected getParams(context) {
        def auto = context.auto
        Insurance insurance = context.insurance
        CompulsoryInsurance compulsoryInsurance = context.compulsoryInsurance
        def insureSupplysList =  context.insureSupplys?.itemCode?.collect { itemCode ->
            [
                itemCode : itemCode,
                itemValue: _INSURE_SUPPLYS_MAPPINGS[(itemCode)](context)
            ]
        }
        def insureSupplys = insureSupplysList ? [insureSupplys : insureSupplysList] : [:]
        [
            taskId     : context.taskId,
            prvId      : context.provider.prvId,
            //先默认是同一个人
            carOwner   : [//车主
                  name      : auto.owner,
                  idcardType: '0',
                  idcardNo  : auto.identity,
                  phone     : auto.mobile
            ],
            applicant  : [//投保人
                  name      : insurance?.applicantName ?: compulsoryInsurance?.applicantName,
                  idcardType: '0',
                  idcardNo  : insurance?.applicantIdNo ?: compulsoryInsurance?.applicantIdNo,
                  phone     : insurance?.applicantMobile ?: compulsoryInsurance?.applicantMobile
            ],
            insured    : [//被保人
                  name      : insurance?.insuredName ?: compulsoryInsurance?.insuredName,
                  idcardType: '0',
                  idcardNo  : insurance?.insuredIdNo ?: compulsoryInsurance?.insuredIdNo,
                  phone     : insurance?.insuredMobile ?: compulsoryInsurance?.insuredMobile
            ],
            beneficiary: [//索赔权益人
                  name      : insurance?.insuredName ?: compulsoryInsurance?.insuredName,
                  idcardType: '0',
                  idcardNo  : insurance?.insuredIdNo ?: compulsoryInsurance?.insuredIdNo,
                  phone     : insurance?.insuredMobile ?: compulsoryInsurance?.insuredMobile
            ],
        ] + insureSupplys
    }

    @Override
    protected getResultFSRV(result,context) {
        if ('00' == result.respCode) {
            log.info '提交更改关系人和地址信息成功'
            getContinueFSRV(context.additionalParameters?.supplementInfo?.images as boolean)
        } else {
            log.error '提交更改关系人和地址信息失败：{}', result.erroeMsg
            getKnownReasonErrorFSRV result.erroeMsg
        }
    }
}
