package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import java.time.LocalDate

import static com.cheche365.cheche.common.Constants.get_DATETIME_FORMAT3
import static com.cheche365.cheche.common.util.ContactUtils.getRandomEmail
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV



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
class UpdateQuoteInfoForDelivery extends AUpdateQuoteInfo {

    @Override
    protected getParams(context) {
        def auto = context.auto
        PurchaseOrder order = context.order
        Insurance insurance = context.insurance
        CompulsoryInsurance compulsoryInsurance = context.compulsoryInsurance
        def address = order.deliveryAddress
        def email = randomEmail
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
                        itemValue: '天畅园2号楼'
                ],
                //权益索赔人邮箱
                [
                    itemCode : 'claimantEmail',
                    itemValue: email
                ],
                //权益索赔人证件类型
//                    [
//                            itemCode : 'claimantDocumentType ',
//                            itemValue: '0'
//                    ],
//                    [
//                            itemCode : 'claimantDocumentNumber ',
//                            itemValue: insurance?.insuredIdNo ?: compulsoryInsurance?.insuredIdNo,
//                    ],
//                    [
//                            itemCode : 'claimantMobile ',
//                            itemValue:  insurance?.insuredMobile ?: compulsoryInsurance?.insuredMobile
//                    ],

                //车主
//                    [
//                            itemCode : 'owerMobile',
//                            itemValue: randomMobile
//
//                    ],
                //车主身份证地址
                [
                    itemCode : 'ownerAddress',
                    itemValue: address.address

                ],
              //车主邮箱
                [
                    itemCode : 'ownerEmail',
                    itemValue: email
                ],
               //车主身份证有效止期
                [
                    itemCode : 'ownerIDCardValidDate',
                    itemValue: idValidDate
                ],
                //被保人
//                    [
//                            itemCode : 'insuredMobile',
//                            itemValue: insurance?.insuredMobile ?: compulsoryInsurance?.insuredMobile
//                    ],
                 //被保人身份证有效止期
                [
                    itemCode : 'insuredAddress',
                    itemValue: address.address
                ],
               //被保人邮件
                [
                    itemCode : 'insuredEmail',
                    itemValue: email
                ],
                //被保人身份证有效止期
                [
                    itemCode : 'insuredIDCardValidDate',
                    itemValue: idValidDate
                ],
                //投保人
//                    [
//                            itemCode : 'applicantMobile',
//                            itemValue:insurance?.applicantMobile ?: compulsoryInsurance?.applicantMobile
//                    ],
                // 投保人身份证地址
                [
                    itemCode : 'applicantAddress',
                    itemValue: address.address
                ],
             // 投保人邮箱
                [
                    itemCode : 'applicantEmail',
                    itemValue: email
                ],
               // 投保人身份证有效止期
                [
                    itemCode : 'applicantIDCardValidDate',
                    itemValue: idValidDate
                ],

            ],

            delivery     : [
                name        : address?.name,
                phone       : address?.mobile,
                province    : address?.province,
                city        : address?.city,
                area        : address?.district,
                //和泛华确认过，address只用到街道的名称，在泛华的订单里会自动的拼写省市的名称
                address     : address?.street,
                deliveryType: '1'
            ],
        ]
    }

    @Override
    protected getResultFSRV(result,context) {
        if ('0' == result.code || '00' == result.respCode) {
            log.info '提交更改关系人和地址信息成功'
            getContinueFSRV context.additionalParameters.isBaoXianPay
        } else {
            log.error '提交更改关系人和地址信息失败：{}', result.msg
            getFatalErrorFSRV result.msg
        }
    }
}
