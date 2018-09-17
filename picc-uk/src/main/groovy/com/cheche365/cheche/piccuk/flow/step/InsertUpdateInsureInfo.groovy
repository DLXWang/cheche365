package com.cheche365.cheche.piccuk.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getAddress
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getProposalNoForBICI



/**
 * 保存InsureInfo
 */
@Component
@Slf4j
class InsertUpdateInsureInfo extends AInsertUpdate {

    @Override
    protected getUpdateParameters(context) {
        def (ciNo, biNo) = getProposalNoForBICI(context.proposalNos)
        context.insertArgs.body + [
            editType                        : 'UPDATE',
            'prpCinsureds[0].email'         : context?.insurance.applicantEmail ?: context.compulsoryInsurance?.applicantEmail,
            'prpCinsureds[0].insuredAddress': getAddress(context.order?.deliveryAddress),
            'prpCinsuredsview[0].mobile'    : context.extendedAttributes?.verificationMobile ?: context.insurance?.applicantMobile ?: context.compulsoryInsurance?.applicantMobile,
            'prpCinsureds[0].mobile'        : context.extendedAttributes?.verificationMobile ?: context.insurance?.applicantMobile ?: context.compulsoryInsurance?.applicantMobile,
            'prpCmain.proposalNo'           : biNo ?: ciNo,
            'prpCmainCI.proposalNo'         : ciNo,
            'prpCmain.checkFlag'            : 0,
            'prpCmain.renewalFlag'          : '01',
            isQueryCarModelFlag             : 1,
            operatorCode                    : '00',
            premiumChangeFlag               : 0,
            switchFlag                      : 1,
            updateQuotation                 : 1,
            oldPolicyNo                     : biNo ?: ciNo,
            bizNo                           : biNo,
        ]
    }

    @Override
    protected getFsrv(result) {
        if (result) {
            getContinueFSRV '更新报价单成功'
        } else {
            log.error '更新报价单信息失败'
            getFatalErrorFSRV '更新报价单失败'
        }
    }

}
