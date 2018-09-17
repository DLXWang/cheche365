package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.Constants.get_DATE_FORMAT5
import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.huanong.flow.Constants._STATUS_CODE_HUANONG_CONFIRM_INSURE_FAILURE
import static com.cheche365.cheche.huanong.flow.Constants._SUCCESS
import static com.cheche365.cheche.huanong.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.huanong.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.populateNewQuoteRecordAndInsurances



/**
 * 提交核保
 * create by liuguo
 *
 */
@Slf4j
class Submit implements IStep {

    private static final _TRAN_CODE = 'Submit'

    @Override
    run(Object context) {
        def result = sendParamsAndReceive context, getRequestParams(context), _TRAN_CODE, log

        if (result.head.responseCode == _SUCCESS) {
            insureSuccess context, result, result.ruleMsg ?: result.head.responseMsg
        } else {
            def errorMsg = result.head?.responseMsg
            if (errorMsg?.contains('该订单已经提交人工审核')) {
                //人工核保异常
                log.error '该订单已经提交人工审核'
                [_ROUTE_FLAG_DONE, _STATUS_CODE_HUANONG_CONFIRM_INSURE_FAILURE, null, '转人工核保原因：' + errorMsg]
            } else {
                getFatalErrorFSRV errorMsg
            }
        }
    }

    private static getRequestParams(context) {
        def order = context.order
        def params = [
            orderNo        : context.orderNo,
            vin            : context.auto.vinNo,
            deliveryInfo   : [
                name    : order.deliveryAddress.name,
                mobile  : order.deliveryAddress.mobile,
                province: order.deliveryAddress.provinceName ?: order.deliveryAddress.cityName,
                city    : order.deliveryAddress.cityName,
                district: order.deliveryAddress.districtName,
                detail  : order.deliveryAddress.street,
//                name    : '123',
//                mobile  : '13071115144',
//                province: '四川',
//                city    : '成都',
//                district: '都江堰',
//                detail  :'大取灯胡同2号楼',
            ],
            pageUrl        : context.additionalParameters.frontCallBackUrl,
            callbackUrl    : context.additionalParameters.callbackUrl,//保单通知接口
            undWrtNoticeUrl: context.additionalParameters.callbackUrl,//核保状态通知接口
        ]

        createRequestParams context, _TRAN_CODE, params
    }

    private static insureSuccess(context, result, errorMsg) {
        //更新PurchaseOrder信息
        log.info '华农业务流水号：{}，{}', result.forceProposalNo, result.bizProposalNo
        context.newQuoteRecordAndInsurances = populateNewQuoteRecordAndInsurances context, result.bizProposalNo, null, result.forceProposalNo, null
        dealSuccessResultInfo context, result
        context.formId = result.orderNo ?: context.orderNo
        context.imageUploadId = result.bizProposalNo ?: result.forceProposalNo
        //status为1是核保通过，2是提交上级，3是审核不通过，4是转人工审核
        if (result.status == '1') {
            getContinueFSRV result
        } else if (result.status == '3') {
            //核保不通过，判断是否需要影像上传
            if (needUpdateImage(errorMsg)) {
                getUploadFSRV context
            } else {
                getFatalErrorFSRV errorMsg
            }
        } else if (result.status in ['2', '4']) {
            if (needUpdateImage(errorMsg)) {
                getUploadFSRV context
            } else {
                [_ROUTE_FLAG_DONE, _STATUS_CODE_HUANONG_CONFIRM_INSURE_FAILURE, null, '转人工核保原因:' + errorMsg]
            }

        }
    }

    private static dealSuccessResultInfo(context, result) {
        //更新订单起保和终保日期,
        if (context.insurance && context.commercialExpireDate) {
            context.insurance.proposalNo = result.bizProposalNo
            context.insurance.effectiveDate = _DATE_FORMAT5.parse context.commercialBeginDate
            context.insurance.expireDate = _DATE_FORMAT5.parse context.commercialExpireDate

        }
        if (context.compulsoryInsurance && context.compulsoryExpireDate) {
            context.compulsoryInsurance.proposalNo = result.forceProposalNo
            context.compulsoryInsurance.effectiveDate = _DATE_FORMAT5.parse context.compulsoryBeginDate
            context.compulsoryInsurance.expireDate = _DATE_FORMAT5.parse context.compulsoryExpireDate
        }
    }

    private static needUpdateImage(errorMsg) {
        errorMsg.contains('验车')
    }

    private static getUploadFSRV(context) {
        context.isUpdateImages = true
        getSupplementInfoFSRV(
            [mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])])
    }

}
