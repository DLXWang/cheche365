package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC

/**
 * 进入车商渠道专用出单
 * Created by wangxiaofei on 2016.9.1
 */
@Slf4j
class QuickProposal implements IStep {

    private static final _URL_QUICK_PROPOSAL = '/prpall/business/quickProposal.do'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_QUICK_PROPOSAL,
            query             : [
                bizType          : 'PROPOSAL',
                editType         : 'NEW',
                is4S             : 'Y',
                isEnterPrjectFlag: 'N'
            ]
        ]

        def (quoteBaseInfo, agentInfo) = client.get( args, { resp, reader ->
            def inputs = htmlParser.parse(reader).depthFirst().INPUT

            [_BASE_INFO_NAMES, _AGENT_INFO_NAMES].collect { names ->
                inputs.findResults { input ->
                    if (input.@name in names) {
                        [(input.@name): input.@value]
                    }
                }.collectEntries Closure.IDENTITY
            }
        })

        if (quoteBaseInfo && agentInfo) {
            log.info '登录车商渠道专用出单成功，基本信息：{}', quoteBaseInfo
            context.baseInfo = quoteBaseInfo
            context.angentInfo = agentInfo

            getContinueFSRV quoteBaseInfo
        } else {
            getFatalErrorFSRV '登录车商渠道专用出单失败'
        }
    }

    static _BASE_INFO_NAMES = [
        'is4SFlag',
        'comCode',
        'agentCode',
        'bIInsureFlag',
        'cIInsureFlag',
        'bizType',
        'useYear',
        'riskCode',
        'operatorCode',
        'ciStartDate',
        'biStartDate',
        'queryCarModelInfo',
        'prpCmainagentName',
        'prpBatchVehicle.carId',
        'prpCmainCI.endDate',
        'prpCmainCI.endHour',
        'prpCmainCI.startDate',
        'prpCmainCI.startHour',
        'prpCcarShipTax.id.itemNo',
        'prpCcarShipTax.taxComCode',
        'prpCitemCar.id.itemNo',
        'prpCitemCar.runMiles',
        'prpCitemCar.carProofNo',
        'prpCmain.agentCode',
        'prpCmain.businessNature',
        'prpCmain.comCode',
        'prpCmain.startDate',
        'prpCmain.startHour',
        'prpCmain.endDate',
        'prpCmain.endHour',
        'prpCmain.makeCom',
        'prpCmain.handler1Code',
        'prpCmain.handlerCode',
        'prpCmain.operateDate',
        'prpCmain.projectCode',
        'prpCmainCommon.clauseIssue',
        'prpCinsureds[0].id.serialNo',
        'prpCinsureds[0].insuredFlag',
        'prpCitemKindCI.calculateFlag',
        'prpCitemKindCI.clauseCode',
        'prpCitemKindCI.disCount',
        'prpCitemKindCI.dutyFlag',
        'prpCitemKindCI.familyNo',
        'prpCitemKindCI.flag',
        'prpCitemKindCI.id.itemKindNo',
        'prpCitemKindCI.kindCode',
        'ciInsureDemandCheckCIVo.flag',
        'ciInsureDemandCheckVo.flag',
        'Today',
        'prpCmain.proposalNo',
        'idCardCheckInfo[0].flag',
        'prpCitemCar.carKindCode',
    ]

    static final _AGENT_INFO_NAMES = [
        'prpCmain.operatorCode',
        'qualificationNo',
        'queryArea',
        'quotationRisk',
        'userCode',
        'userType',
        'queryCarModelInfo',
        'qualificationName',
        'prpCremarks_[0].operatorCode',
        'operatorName',
        'makeComDes',
        'handlerCodeDes',
        'handler1CodeDes',
        'comCodeDes',
        'businessNatureTranslation',
        'prpCmain.coinsFlag',
    ]

}
