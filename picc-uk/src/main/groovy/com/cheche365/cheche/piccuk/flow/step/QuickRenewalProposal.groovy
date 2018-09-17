package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static com.cheche365.cheche.parser.util.InsuranceUtils.afterGeneratedRenewalPackage
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.piccuk.util.BusinessUtils.generateRenewalPackage
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取续保套餐
 */
@Slf4j
class QuickRenewalProposal implements IStep {

    private static final _URL_QUICK_RENEWAL_PROPOSAL = '/prpall/business/quickProposalEditRenewalCopy.do'

    @Override
    run(context) {

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_QUICK_RENEWAL_PROPOSAL,
            query             : [
                bizNo: context.bizNo,
            ]
        ]

        def renewalBaseInfo = client.get args, { resp, reader ->
            htmlParser.parse(reader).depthFirst().INPUT
        }

        //获取商业险和交强险的起保日期
        def newStartDate = renewalBaseInfo.findAll { input ->
            input.@id in ['prpCmain.startDate', 'prpCmainCI.startDate']
        }.collectEntries { input ->
            [(input.@id): input.@value]
        }

        //获取商业险险种
        def allItemMappings = renewalBaseInfo.findAll { input ->
            input.@id =~ /prpCitemKindsTemp\[\d+\]/
        }.collectEntries { input ->
            if (input.@checked == 'checked') {
                input.@value = true
            }
            [(input.@id): input.@value]
        }

        def preItemKinds = allItemMappings.groupBy { key, value ->
            def matches = key =~ /prpCitemKindsTemp\[\d+\]/
            matches[0]
        }
        def allItemKinds = preItemKinds.collectEntries { group, kindItem ->
            def newKindItem = kindItem.collectEntries { key, value ->
                [(key - "$group."): value]
            }
            [(newKindItem.kindCode): newKindItem]
        }


        if (newStartDate && allItemKinds) {
            //续保套餐转换为内部套餐
            if (_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType && !context.insurancePackage) {
                context.insurancePackage = generateRenewalPackage context, allItemKinds
                afterGeneratedRenewalPackage context
                log.debug '续保套餐：{}', context.accurateInsurancePackage
            }

            //修改今年投保的的起保日期
            setCommercialInsurancePeriodTexts(context, newStartDate.'prpCmain.startDate')
            setCompulsoryInsurancePeriodTexts(context, newStartDate.'prpCmainCI.startDate')

            log.info '商业险起保日期：{}，交强险起保日期： {}', newStartDate.'prpCmain.startDate', newStartDate.'prpCmainCI.startDate'

            getContinueFSRV allItemKinds
        } else {
            getFatalErrorFSRV '续保套餐获取异常'
        }
    }

}
