package com.cheche365.cheche.aibao.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.aibao.util.BusinessUtils._I2O_PREMIUM_CONVERTER
import static com.cheche365.cheche.aibao.util.BusinessUtils._KIND_CODE_CONVERTERS_CONFIG
import static com.cheche365.cheche.aibao.util.BusinessUtils.getAllKindItems
import static com.cheche365.cheche.aibao.util.BusinessUtils.str2double
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.parser.Constants.get_GLASS_TYPE
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.getQuoteKindItemParams
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ



/**
 * 报价
 * Created by xuecl on 2018/08/30.
 */
@Slf4j
class UserPriceQuote extends APriceQuote {

    def interfaceID = '100072'

    @Override
    protected dealSuccessResult(context, result) {
        log.info '报价成功：{}', result.head.errorMsg
        // 外转内
        def sumTax = str2double result.body.mainInfo.carShipTaxFee
        def seats = str2double(context.auto.seats ?: context.selectedCarModel?.carModel?.seatCount) as int
        populateQR context, _KIND_CODE_CONVERTERS_CONFIG, result.body, seats - 1, sumTax
        getLoopBreakFSRV '报价完成'
    }

    @Override
    protected def getSpecificParams(context) {
        context.kindCodeConvertersConfig = _KIND_CODE_CONVERTERS_CONFIG
        def quoteParams = getQuoteKindItemParams context, getAllKindItems(_KIND_CODE_CONVERTERS_CONFIG),
            selectNeededConfig(context, _KIND_CODE_CONVERTERS_CONFIG), _I2O_PREMIUM_CONVERTER
        [itemKindInfo: quoteParams]
    }

    private static selectNeededConfig(context, convertConfig) {
        def insurancePackage = context.accurateInsurancePackage
        convertConfig.findAll { config ->
            insurancePackage[config[1] as String] ? 1 : 0
        }
    }

    private static populateQR(context, kindCodeConvertersConfig, body, seatCount, sumTax) {
        def compulsoryPremium = body.mainInfo.bzPremium
        def commercial = body.mainInfo.busiSumPremium
        if (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage)) {
            //context  交强险  车船税carShipTaxFee
            populateQuoteRecordBZ context, str2double(compulsoryPremium), str2double(sumTax)
        } else {
            disableCompulsoryAndAutoTax context
        }
        if (isCommercialQuoted(context.accurateInsurancePackage)) {
            // 获取险别信息 外围编码 ：itemKindInfo
            def allKindItems = getQuotedItems(context, body.itemKindInfo, seatCount)
            populateQuoteRecord context, allKindItems, kindCodeConvertersConfig, str2double(commercial), null
        } else {
            disableCommercial context
        }
    }

    static getQuotedItems(context, items, seatCount) {
        items.collectEntries { item ->
            [
                (item.kindCode): [
                    amount    : str2double(item.amount),
                    premium   : str2double(item.premium),
                    iopPremium: null,
                    quantity  : (seatCount as int) - 1,                                  // 乘客的数量
                    glassType : context.accurateInsurancePackage[_GLASS_TYPE as String]  // 玻璃类型  (默认就是1 国产)
                ]
            ]
        }
    }

}
