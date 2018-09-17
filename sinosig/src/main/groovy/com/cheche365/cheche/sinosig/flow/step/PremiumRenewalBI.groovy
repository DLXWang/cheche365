package com.cheche365.cheche.sinosig.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._INSURANCE_COMMERCIAL_MAPPINGS
import static com.cheche365.cheche.parser.util.InsuranceUtils.updateInsurancePackageByQuoteRecord
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils._KIND_CODE_CONVERTERS
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils.getCommercialRequestParameters
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.sinosig.flow.util.ComboUtils._KIND_ITEM_CONFIG
import static com.cheche365.cheche.sinosig.flow.util.ComboUtils.getKindItemFromList

/**
 * 计算商业险
 */
@Slf4j
class PremiumRenewalBI extends APremiumBI {

    private static final _JSON_DETAILS_PROP_NAME_DEBUG_LIST = [
        'kindCode',
        'kindName',
        'amount',
        'unitAmount',
        'premium',
        'excludingPremium',
        'modeCode'
    ]

    @Override
    protected getResponseResult(context) {
        def kindItemList, paraMap
        (kindItemList, paraMap) = _KIND_ITEM_CONFIG.inject([context.kindItemList, context.paraMap], { kindListAndParaMapPair, propName, config ->
            /*由于在官网中如果选择了发动机涉水险，且发动机涉水险不可投的情况下，会改变其他已报价险种的状态，导致其他险种也报价失败，必须选择发动机涉水险为不投保，其他险种才能正常报价。
              对于这种情况，我们之前的处理是将发动机涉水险报价返回的结果设置为null，即“(kindList, paraMap) = [null, null]”，但是程序继续执行时会抛空指针异常，即使我们处理了空指针
              异常且测试能通过，但是后续其他险种报价会不正常（其实，在官网中这个时候由于选择了发动机涉水险且发动机涉水险不能报价，导致其他已选择的正常报价的险种投保状态发生改变且有些
              险种会报价为0），所以这里的处理是：判断kindList是否为空，如果为空后续险种不在进行遍历，将这种情况视为报价失败，且直接退出程序。
            */
            if (!kindListAndParaMapPair[0]) {
                return [null, null]
            }

            def (kindList) = kindListAndParaMapPair
            def kindItem = getKindItemFromList kindList, propName
            def kindFlag = context.iopEnabled ? config.kindFlag.withIOP : config.kindFlag.withoutIOP
            context.flowParticipant?.sendMessage "${_INSURANCE_COMMERCIAL_MAPPINGS[propName].description}报价中"
            def kindItemParams = [
                'paraMap.kindCode': config.kindCode,
                'paraMap.kindFlag': kindFlag
            ] + _KIND_CODE_CONVERTERS[propName](kindItem, context.accurateInsurancePackage[propName], context)

            (kindList, paraMap) = getKindItemQuote(context, getCommercialRequestParameters(context, 2, -1) + kindItemParams)

            kindList?.find {
                config.kindCode == it.kindCode
            }?.with {
                if (thisObject.log.debugEnabled) {
                    thisObject.log.debug "{} 的报价：{}，iop：{}", config.kindName, it.premium, ('0.00' == it.premium ? 0.00 : it.excludingPremium)
                }
            }

            [kindList, paraMap]
        })

        if (!kindItemList) {
            return getFatalErrorFSRV('商业险报价失败')
        }
        def quoteRecord = populateQuoteRecord context, kindItemList, paraMap
        updateInsurancePackageByQuoteRecord quoteRecord

        if (log.debugEnabled) {
            log.debug '精准报价总览JSON：{}', paraMap
            log.debug '精准报价明细JSON：{}', kindItemList.collect({ kindItem ->
                kindItem.findAll { k, _ ->
                    k in _JSON_DETAILS_PROP_NAME_DEBUG_LIST
                }
            })
            log.debug '组装后的新QuoteRecord：{}', quoteRecord
        }

        getContinueFSRV quoteRecord
    }
}
