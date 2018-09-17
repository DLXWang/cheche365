package com.cheche365.cheche.taikang.flow

import static com.cheche365.cheche.taikang.util.BusinessUtils.createPrivy

import java.time.LocalDate

import static com.cheche365.cheche.parser.Constants.get_DATETIME_FORMAT3



/**
 * 泰康 RPG&RH
 * Created by LIUGUO on 2018/09/07
 */
class Handlers {

    private static final _QUOTE_PROPOSAL_RPG_BASE = { postProcessor, context ->
        def auto = context.auto
        def order = context.order
        def applicant = order.applicant //投保人
        def checkList = context.quoteCheckList
        def autoId = auto.identity                      // 车主身份证
        def applicantId = order.applicantIdNo ?: autoId // 投保人身份证
        def insureId = order.insuredIdNo ?: autoId      // 被保险人身份证
        def deliveryAddress = order.deliveryAddress

        def insurances = context.insurance ?: context.compulsoryInsurance
        def userMobile = insurances.applicantMobile ?: applicant?.mobile ?: randomMobile
        def userName = insurances.applicantName ?: order.applicantName ?: auto.owner

        def privyList = [
            createPrivy('1000000', userName, applicantId, userMobile, order.applicantIdentityType?.id ?: 1, context.defaultEmail), //投保人
            createPrivy('0100000', order.insuredName ?: auto.owner, insureId, userMobile, order.insuredIdentityType?.id ?: 1, context.defaultEmail),      //被保人
            createPrivy('0010000', auto.owner, autoId, auto.mobile ?: userMobile, auto.identityType.id, context.defaultEmail)                              //车主
        ]

        def params = [
            courierInfo  : [
                contacts   : deliveryAddress?.name ?: auto.owner,               // 联系人
                phoneNumber: deliveryAddress?.mobile ?: userMobile,  // 联系电话
                province   : deliveryAddress?.provinceName ?: deliveryAddress?.cityName,// 配送地址(省)
                city       : deliveryAddress?.cityName,                         // 配送地址(市)
                adress     : deliveryAddress?.street,                         // 配送详细地址
            ],
            privyList    : privyList,
            quotationNoCI: context.quotationNoCI,                              //商业险询价单号
            quotationNoBI: context.quotationNoBI,                              //交强险询价单号
        ]
        //转保业务
        if (checkList) {
            def checinfos = []
            checinfos << checkList.last()
            params << [checkList: checinfos]
        }

        postProcessor context, params

    }

    private static final _QUOTE_PROPOSAL_POST_PROCESSOR_DEFAULT = {
        context, params -> params
    }

    private static final _QUOTE_PROPOSAL_POST_PROCESSOR_110000 = {
        context, params ->
            def now = LocalDate.now()
            params.privyList = params.privyList.collect {
                privyInfo ->
                    [
                        privy: privyInfo.privy << [
                            nation        : '汉',
                            issuer        : '签发机构',
                            certiStartDate: _DATETIME_FORMAT3.format(now),
                            certiEndDate  : _DATETIME_FORMAT3.format(now.plusYears(10)),
                        ]
                    ]
            }
            params
    }

    static final _QUOTE_PROPOSAL_110000 = _QUOTE_PROPOSAL_RPG_BASE.curry(_QUOTE_PROPOSAL_POST_PROCESSOR_110000)

    static final _QUOTE_PROPOSAL_RPG_DEFAULT = _QUOTE_PROPOSAL_RPG_BASE.curry(_QUOTE_PROPOSAL_POST_PROCESSOR_DEFAULT)

}
