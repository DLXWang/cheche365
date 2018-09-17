package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.baoxian.flow.step.AUpdateQuoteInfo
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import java.time.LocalDate

import static com.cheche365.cheche.common.Constants.get_DATETIME_FORMAT3
import static com.cheche365.cheche.common.util.ContactUtils.getRandomEmail
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
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
class UpdateQuoteInfoForDeliveryV2 extends AUpdateQuoteInfo {

    @Override
    protected getParams(context) {
        def auto = context.auto
        PurchaseOrder order = context.order
        Insurance insurance = context.insurance
        CompulsoryInsurance compulsoryInsurance = context.compulsoryInsurance
        def email = getEnvProperty(context,'baoxian.email')
        def address = order.deliveryAddress
        def mobile = address.mobile
        def idValidDate = _DATETIME_FORMAT3.format LocalDate.now().plusYears(10)
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
            insureSupplys: [
                //行驶证地址
                [
                        itemCode : 'drivingLicenseAddress',
                        itemValue: address.street
                ],
                //权益索赔人邮箱
                [
                    itemCode : 'claimantEmail',
                    itemValue: email
                ],
                [
                    itemCode : 'ownerEmail',
                    itemValue: email
                ],
                [
                    itemCode : 'ownerAddress',
                    itemValue: address.street
                ],
                [
                    itemCode : 'ownerMobile',
                    itemValue: mobile
                ],
                [
                    itemCode : 'ownerIDCardValidDate',
                    itemValue: idValidDate
                ],

                [
                    itemCode : 'insuredEmail',
                    itemValue: email
                ],
                [
                    itemCode : 'insuredAddress',
                    itemValue: address.street
                ],
                [
                    itemCode : 'insuredMobile',
                    itemValue: mobile
                ],
                [
                    itemCode : 'insuredIDCardValidDate',
                    itemValue: idValidDate
                ],

                [
                    itemCode : 'applicantEmail',
                    itemValue: email
                ],
                [
                    itemCode : 'applicantAddress',
                    itemValue: address.street
                ],
                [
                    itemCode : 'applicantMobile',
                    itemValue: mobile
                ],
                [
                    itemCode : 'applicantIDCardValidDate',
                    itemValue: idValidDate
                ]
            ]
        ]
    }

    @Override
    protected getResultFSRV(result,context) {
        if ('00' == result.respCode) {
            log.info '提交更改关系人和地址信息成功'
            getContinueFSRV(context.additionalParameters.supplementInfo?.images as boolean)
        } else {
            log.error '提交更改关系人和地址信息失败：{}', result.erroeMsg
            getKnownReasonErrorFSRV result.erroeMsg
        }
    }
}
