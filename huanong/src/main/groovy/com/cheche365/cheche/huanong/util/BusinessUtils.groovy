package com.cheche365.cheche.huanong.util

import groovyx.net.http.Method

import static com.cheche365.cheche.common.Constants._DATE_FORMAT5
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.core.model.LogType.Enum.HuaNong_60
import static com.cheche365.cheche.huanong.flow.Constants._HUANONG_SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.huanong.flow.Constants._HUANONG_THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.huanong.flow.Constants._USE_CHARACTER
import static com.cheche365.cheche.huanong.flow.Constants.get_SUCCESS
import static com.cheche365.cheche.parser.Constants._AUTO_TAX
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_COMMERCIAL_MAPPINGS
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants._UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.util.BusinessUtils.addQFSMessage
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.BusinessUtils.saveAppLog
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV
import static com.sinosoft.RSAUtils.encryptByPublicKey
import static java.math.BigDecimal.ROUND_HALF_UP
import static net.sf.json.JSONObject.fromObject as toJson

class BusinessUtils {

    private static String encrypt(params, context) {
        encryptByPublicKey toJson(params).toString(), context.publicKey
    }

    /**
     * 华农发送报文公共方法
     * @param context 上下文
     * @param params 请求报文
     * @param transCode 报文标识
     * @param log 传入日志
     * @return
     */
    static sendParamsAndReceive(context, params, transCode, log) {
        saveAppLog(context.logRepo, HuaNong_60, context.taskId, context.insuranceCompany?.name, toJson(params).toString(), transCode, "$context.auto.licensePlateNo:request")

        def requestParams = encrypt(params, context)

        log.debug '{}-华农车牌：{}：{}请求参数为：{}', transCode, context.auto.licensePlateNo, _DATE_FORMAT5.format(new Date()), requestParams
        def result = context.client.request(Method.POST, 'application/json;charset=UTF-8') {
            body = requestParams
            response.success = { resp, json ->
                json
            }
            response.failure = { resp, json ->
                json
            }
        }
        log.debug '{}-华农车牌：{}：{}返回结果为：{}', transCode, context.auto.licensePlateNo, _DATE_FORMAT5.format(new Date()), toJson(result).toString()

        result
    }

    /**
     * 起始时间加1年减一天拼接23:59:59
     * @param date
     * @return
     */
    static getEndTime(date) {
        (getLocalDate(_DATE_FORMAT5.parse(_DATE_FORMAT5.format(date))).plusYears(1).minusDays(1) as String) + ' 23:59:59'
    }

    private static getBizConfig(kindConvertConfig, insurance) {
        kindConvertConfig.findAll { item -> item[0] in insurance.collect { it.kindCode } }
    }

    /**
     *
     * 报文头
     *
     * */
    static createRequestParams(context, transCode, body) {

        [
            head: [
                interfaceCode: context.interfaceCode,
                transCode    : transCode,
                transType    : context.transType,
                token        : context.token ?: null,
                sign         : '',
                logid        : '',
            ]
        ] + body
    }

    //商业险主线
    private static final _CONVERTER_FROM_BOOLEAN = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        if (propName in [_DAMAGE, _THEFT, _SPONTANEOUS_LOSS] && insurancePackage[propName]) {
            getDamageAmount(context)
        } else if (!(propName in [_ENGINE, _UNABLE_FIND_THIRDPARTY])) {
            insurancePackage[propName]
        } else {
            0
        }
    }
    //商业险不计免赔
    private static final _CONVERTER_FROM_BOOLEAN_IOP = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        1
    }

    private static final _AMOUNT_CONVERTER_FROM_AMOUNT_LIST = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        def expectedAmount = insurancePackage[propName]
        def listAmount = kindItem?.amountList?.reverse()

        if (_GLASS == propName && expectedAmount) {
            def glassType = insurancePackage.glassType
            expectedAmount = (DOMESTIC_1 == glassType ? 1 : IMPORT_2 == glassType ? 2 : 0)
        }

        def actualAmount = expectedAmount ?
            (adjustInsureAmount(expectedAmount as int, listAmount, { item -> item as double },
                { item -> item as double }, 1) ?: 0) : 0

        actualAmount as double
    }

    private static final _O2I_PREMIUM_CONVERTER = { context, innerKindCode, kindItem, amountName, premiumName, isIop,
                                                    iopPremiumName, extConfig ->
        def other = null
        if (_GLASS == innerKindCode) {
            other = ('1' == kindItem?.glassType) ? DOMESTIC_1 : ('2' == kindItem?.glassType) ? IMPORT_2 : null
        }
        if (_PASSENGER_AMOUNT == innerKindCode) {
            other = kindItem?.quantity ?: 0  // 座位数在上一步已-1
        }

        [
            isIop ? null : kindItem?.amount,
            isIop ? null : kindItem?.premium,
            isIop ? kindItem?.iopPremium : null,
            other
        ]
    }

    //文字描述
    static final _KEY_VALUES = [
        A  : '机动车损失险',
        A1 : '机动车损失保险无法找到第三方特约险',
        B  : '机动车第三者责任险',
        D2 : '车上货物责任险',
        D3 : '机动车车上人员责任险(驾驶员)',
        D4 : '机动车车上人员责任险(乘客)',
        F  : '玻璃单独破损险',
        G  : '机动车全车盗抢险',
        J1 : '指定修理厂险',
        L  : '车身划痕损失险',
        MA : '不计免赔险(机动车损失)',
        MB : '不计免赔险(机动车第三者责任险)',
        MD2: '不计免赔险(车上货物责任险)',
        MD3: '不计免赔险(机动车车上人员责任险(驾驶员))',
        MD4: '不计免赔险(机动车车上人员责任险(乘客))',
        MG : '不计免赔险(全车盗抢险)',
        ML : '不计免赔险(车身划痕损失险)',
        MR : '不计免赔险（精神损害抚慰金损失险（第三者责任险））',
        MX : '不计免赔率险（新增加设备损失险）',
        MX1: '不计免赔险(发动机涉水损失险)',
        MZ : '不计免赔险(自燃损失险)',
        R  : '精神损害抚慰金责任险(第三者责任险)',
        T  : '修理期间费用补偿',
        X  : '新增设备损失险',
        X1 : '发动机涉水损失险',
        Z  : '自燃损失险',
        BZ : '机动车交通事故责任强制保险',
    ]

    static final _RESPONSE_CODE_DESC_MAPPINGS = [
        '#000101' : '报文格式不正确',
        '#000201' : '空指针异常',
        '#000202' : '数组越界异常',
        '#000206' : '类型强制转换异常',
        '#000214' : '方法未找到异常',
        '106'     : '起保日期必须在当前日期的规定时间范围内!',
        '107'     : '车牌号为空时，发动机号和车架号不能为空！',
        '108'     : '车牌号录入不规范！',
        '115'     : '保单归属地省/市/县均不能为空',
        '117'     : '初登日期为2000年以后的车，车架号必须为17位',
        '120'     : '被保险人不能为空',
        '127'     : '车龄大于等于15年不允许投保!',
        '136'     : '指定专修厂险：国产车费率在10-30之间',
        '137'     : '指定专修厂险：进口车费率在15-60之间',
        '138'     : '请选择要投保的险别！',
        '139'     : '此车辆用途不允许投保车身划痕险',
        '140'     : '只支持9座及以下的家庭自用车辆的投保',
        '141'     : '投保人、被保险人、车主、驾驶员,存在身份证件相同，但姓名不同，不允许投保！',
        '#000421' : '数据库异常',
        '003'     : '｛字段｝不能为空!',
        '079'     : '新车购置价不能为0',
        '#0003003': '请补充信息',
    ]

    //华农险种
    static final _KIND_CODE_CONVERTERS_CONFIG = [
        ['A', _DAMAGE, _CONVERTER_FROM_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null],//机动车损失险 0
        ['B', _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _THIRD_PARTY_AMOUNT_LIST, outAmountList: _HUANONG_THIRD_PARTY_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null],//机动车第三者责任险
        ['D3', _DRIVER_AMOUNT, _CONVERTER_FROM_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null],//机动车车上人员责任险(驾驶员)
        ['D4', _PASSENGER_AMOUNT, _CONVERTER_FROM_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null],//机动车车上人员责任险(乘客)
        ['F', _GLASS, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, null, _O2I_PREMIUM_CONVERTER, null],//玻璃单独破损险
        ['G', _THEFT, _CONVERTER_FROM_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null],//机动车全车盗抢险 0
        ['X1', _ENGINE, _CONVERTER_FROM_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null],//发动机涉水损失险 0
        ['Z', _SPONTANEOUS_LOSS, _CONVERTER_FROM_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null],//自燃损失险  0
        ['L', _SCRATCH_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _SCRATCH_AMOUNT_LIST, outAmountList: _HUANONG_SCRATCH_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null],//车身划痕损失险
        ['A1', _UNABLE_FIND_THIRDPARTY, _CONVERTER_FROM_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null],//机动车损失保险无法找到第三方特约险 0

        ['MA', _DAMAGE_IOP, _CONVERTER_FROM_BOOLEAN_IOP, null, _O2I_PREMIUM_CONVERTER, null],//不计免赔险(机动车损失) 0
        ['MB', _THIRD_PARTY_IOP, _CONVERTER_FROM_BOOLEAN_IOP, null, _O2I_PREMIUM_CONVERTER, null],//不计免赔险(机动车第三者责任险) 0
        ['MD3', _DRIVER_IOP, _CONVERTER_FROM_BOOLEAN_IOP, null, _O2I_PREMIUM_CONVERTER, null],//不计免赔险(机动车车上人员责任险(驾驶员)) 0
        ['MD4', _PASSENGER_IOP, _CONVERTER_FROM_BOOLEAN_IOP, null, _O2I_PREMIUM_CONVERTER, null],//不计免赔险(机动车车上人员责任险(乘客)) 0
        ['MG', _THEFT_IOP, _CONVERTER_FROM_BOOLEAN_IOP, null, _O2I_PREMIUM_CONVERTER, null],//不计免赔险(全车盗抢险) 0
        ['MX1', _ENGINE_IOP, _CONVERTER_FROM_BOOLEAN_IOP, null, _O2I_PREMIUM_CONVERTER, null],//不计免赔险(发动机涉水损失险) 0
        ['MZ', _SPONTANEOUS_LOSS_IOP, _CONVERTER_FROM_BOOLEAN_IOP, null, _O2I_PREMIUM_CONVERTER, null],//不计免赔险(自燃损失险) 0
        ['ML', _SCRATCH_IOP, _CONVERTER_FROM_BOOLEAN_IOP, null, _O2I_PREMIUM_CONVERTER, null],//不计免赔险(车身划痕损失险) 0
    ]

    /**
     * 商业险内部的保额转换成外部的请求(内转外)
     */
    static final _I2O_PREMIUM_CONVERTER_INSURANCE = { context, outerKindCode, kindItem, amount ->
        //是否是不计免赔
        def insurancePackage = context.accurateInsurancePackage//获取套餐
        //不计免赔标识
        def relatedInd
        //获取内部编码
        def innerKindCodeConverters = _KIND_CODE_CONVERTERS_CONFIG.find { kindCode, innerKindCode, _2, _3, _4, _5 ->
            outerKindCode == kindCode
        }[1]
        //华农不计免赔险包含M,所以用M来判断是否是不计免赔
        if (outerKindCode.contains('M')) {
            relatedInd = 1
        } else {
            def parameter = _QUOTE_RECORD_COMMERCIAL_MAPPINGS[innerKindCodeConverters]?.iopPremiumName
            relatedInd = parameter ? insurancePackage[parameter] ? 1 : 0 : 0
        }
        createCoverage context, outerKindCode, _KEY_VALUES[outerKindCode], amount, relatedInd
    }

    /**
     * 内转外险种格式
     * D4是乘客险
     * */
    static createCoverage(context, kindCode, kindName, amount, iopAmount) {
        def seats = 0
        def passengerAmount = 0
        if (kindCode == 'D4') {//处理乘客险
            seats = (context.selectedCarModel.approvedPassengersCapacity as int) - 1 //座位数
            passengerAmount = context.accurateInsurancePackage.passengerAmount as int//座位数乘以单个保额
            amount = seats * passengerAmount
        }
        // 套餐项格式：
        def parameter = [
            KindCode  : kindCode, //外部标识
            KindName  : kindName, //外部险种名称
            Amount    : amount, //保额
            RelatedInd: iopAmount //IOP
        ]
        if (kindCode == 'F') {//处理玻璃险
            parameter << [
                ModeCode: context.accurateInsurancePackage['glassType'].id
            ]
        }
        if (kindCode == 'D4') {//处理乘客险,为了保证报文格式
            parameter << [
                Quantity  : seats,
                UnitAmount: passengerAmount as double
            ]
        }
        parameter
    }


    static selectNeededConfig(context, convertConfig) {
        def insurancePackage = context.accurateInsurancePackage
        convertConfig.findAll {
            config -> insurancePackage[config[1]] ? 1 : 0
        }
    }

    static getAllKindItems(kindCodeConvertersConfig) {
        //collectEntries  ：list 转 map
        kindCodeConvertersConfig.collectEntries { outerKindCode, innerKindCoder, _2, itemFeatures, _4, _5 ->
            [
                (outerKindCode): [
                    amountList: itemFeatures?.inAmountList?.intersect(itemFeatures?.outAmountList)
                ]
            ]
        }
    }

    static populateQR(context, kindCodeConvertersConfig, compulsoryInsurance, insurance, seatCount, sumTax, insuranceTotalPremium) {
        if (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage)) {
            //context  交强险  车船税sumTax
            populateQuoteRecordBZ(context, str2double(compulsoryInsurance ? compulsoryInsurance.coverage[0].premium : 0), str2double(sumTax))
        } else {
            disableCompulsoryAndAutoTax context
        }

        if (isCommercialQuoted(context.accurateInsurancePackage)) {
            //获取险别信息 外围编码
            def allKindItems = getQuotedItems context, insurance.coverage, seatCount//商业险
            //iop和主险对应外部编码不一致
            context.iopAlone = true
            populateQuoteRecord(context, allKindItems, getBizConfig(kindCodeConvertersConfig, insurance.coverage), insuranceTotalPremium, null)
        } else {
            disableCommercial context
        }
    }

    private static getQuotedItems(context, items, seatCount) {
        def seats = context.selectedCarModel.approvedPassengersCapacity as int //座位数
        items.collectEntries {
            [
                (it.kindCode): [
                    amount       : it.kindCode == 'D4' ? str2double(it.amount / (seats - 1)) : str2double(it.amount),//'D4'是乘客险,需要算出单个座位
                    premium      : it.kindName.contains('不计免赔险') ? null : it.premium,
                    iopPremium   : it.kindName.contains('不计免赔险') ? it.premium : null,
                    quantity     : (seatCount as int) - 1,//乘客的数量
                    glassType    : it.modeCode == null ?: it.modeCode, //玻璃类型  (默认就是1 国产 2进口)
                    nonDeductible: null
                ]
            ]
        }
    }

    private static str2double(value) {
        ((value ?: 0) as BigDecimal).setScale(2, ROUND_HALF_UP).doubleValue()
    }

    /**
     * 比对两次报价结果，比对出差异，Map<String:List<String:Object>> 比对出商业险和交强险差异
     * @param context 上下文，保存有第一次报价结果
     * @param newQuotePriceResult 干系人信息更新之后新的报价结果
     */
    static compareQuoteResult(context, newQuotePriceResult) {
        //返回结果集为 险种列表：List<险别代码:险别信息>
        def differenceInfo = []
        def originQuotePriceResult = context.firstQuotePriceRespJOSN
        //比较交强险总额和商业险总额
        if (!(originQuotePriceResult.bizPremium == newQuotePriceResult.bizPremium && originQuotePriceResult.forcePremium == newQuotePriceResult.forcePremium)) {
            //总额不一致，比较险别明细
            newQuotePriceResult.contract.coverage.collect { kindInfo ->
                if (!searchSameKind(kindInfo, originQuotePriceResult)) {
                    differenceInfo << kindInfo
                }
                differenceInfo - null
            }
        }
        differenceInfo
    }

    private static searchSameKind(def kindInfo, def quotePriceResult) {
        quotePriceResult.contract.coverage.find { item ->
            item.kindCode == kindInfo.kindCode && item.amount == kindInfo.amount && item.premium == kindInfo.premium
        }
    }

    /**
     * 格式化两次报价结果差异，格式待定
     * @param difference
     * @return
     */
    static format(difference) {
        difference
    }
    //是否禁掉车船税
    static getCompulsoryPremiumAndAutoTax(context, compulsoryInsurance) {
        def compulsoryPremium = compulsoryInsurance?.coverage?.premium
        def autoTax = compulsoryInsurance?.tax?.sumTax//车船税
        if (!compulsoryPremium) {
            disableCompulsoryAndAutoTax context
        }
        if (compulsoryPremium && !autoTax) {
            context.accurateInsurancePackage.autoTax = false
            addQFSMessage context, _AUTO_TAX, '不支持投保车船税'
        }
    }

    private static getDamageAmount(context) {
        def damageAmount = { ctx ->
            def rateNum = _USE_CHARACTER."${ctx.auto.useCharacter?.id ?: 21}" == '8A' ? ctx.selectedCarModel.approvedPassengersCapacity >= 10 ? 0.009 : 0.006 : 0.009
            def nowDate = _DATE_FORMAT5.format(new Date()).split('-')[0..1].collect { it as int }
            def lfDate = (_DATE_FORMAT5.format(context?.additionalParameters?.supplementInfo?.enrollDate) ?: ctx.selectedCarModel.lfDate).split('-')[0..1].collect { date ->
                date as int
            }
            str2double((1 - ((nowDate[0] - lfDate[0]) * 12 + (nowDate[1] - lfDate[1])) * rateNum) * ctx.selectedCarModel.purchasePrice)
        }
        damageAmount context
    }

    static getBirthdayFromId(id) {
        "${id[6..9]}-${id[10..11]}-${id[12..13]}" as String
    }

    /**
     * 根据返回message截取所需的精友车型编码和品牌型号
     * @param start 字符串起始值
     * @param end 字符串结束值
     * e.g: 投保算费异常！ 异常信息：报价选择车型与平台记录车型信息不符，请重新选择车型进行报价:第1条记录：精友车型代码为：JHAFQD0001,平台车型代码为：BJHAHAUB0008,车型名称为：江淮HFC6460M轻型客车。
     */
    private static getAutoInfo(str, start, end) {
        str.substring(str.indexOf(start) + start.size(), str.indexOf(end)).trim()
    }

    /**
     * 报价后处理(对正常报价和核保报价返回结果做处理)
     * @param context 上下文
     * @param result 返回结果
     */
    static postProcessQuoting(context, result, log) {

        if (_SUCCESS == result.head.responseCode) {
            //根据华农返回的险种类型,区分出是交强险还是商业险
            //交强险
            def compulsoryInsurance = result.contract.find { contract -> //交强险
                contract.contractMain.riskCode == '0507'
            }
            //交强险返回转保车验证码
            //返回报文有验证码、返回的交强险金额是0、没有获取到前端输入的交强险验证码才会推送验证码到前端
            if (compulsoryInsurance?.plantFormMessageBean?.checkCode && !result.forcePremium && context.additionalParameters.supplementInfo?.compulsoryCaptchaImage) {
                log.info '推送验证码给前端'
                context.needSupplementInfos << mergeMaps(_SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING, [meta: [imageData: compulsoryInsurance?.plantFormMessageBean?.checkCode]])
                return getNeedSupplementInfoFSRV { context.needSupplementInfos }
            }
            //获取交强险平台查询码
            //返回的报文存在交强险平台查询码并且输入了交强险验证码
            if (compulsoryInsurance?.plantFormMessageBean?.querySequenceNo && context.additionalParameters.supplementInfo?.compulsoryCaptchaImage) {
                log.info '获取平台查询码'
                context.querySequenceNo_JQ = compulsoryInsurance?.plantFormMessageBean?.querySequenceNo
            }
            //持久化起保，终保时间给前台使用
            context.compulsoryExpireDate = compulsoryInsurance?.contractMain?.expiryDate
            context.compulsoryBeginDate = compulsoryInsurance?.contractMain?.validDate
            //商业险
            def insurance = result.contract.find { contract -> //商业险
                contract.contractMain.riskCode == '0520'
            }
            //商业险返回转保车验证码
            //返回报文有验证码、返回的商业险金额是0、没有获取到前端输入的商业险验证码才会推送验证码到前端
            if (insurance?.plantFormMessageBean?.checkCode && !result.bizPremium == 0 && context.additionalParameters.supplementInfo?.commercialCaptchaImage) {
                log.info '推送验证码给前端'
                context.needSupplementInfos << mergeMaps(_SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING, [meta: [imageData: insurance?.plantFormMessageBean?.checkCode]])
                return getNeedSupplementInfoFSRV { context.needSupplementInfos }
            }
            //获取商业险平台查询码
            //返回的报文存在商业险平台查询码并且输入了商业险验证码
            if (insurance?.plantFormMessageBean?.querySequenceNo && !context.additionalParameters.supplementInfo?.commercialCaptchaImage) {
                log.info '获取平台查询码'
                context.querySequenceNo_SY = insurance?.plantFormMessageBean?.querySequenceNo
            }

            context.commercialExpireDate = insurance?.contractMain?.expiryDate
            context.commercialBeginDate = insurance?.contractMain?.validDate
            context.phoneNo = context.applicant?.mobile //持久化，给前台调用交易申请接口时候用
            context.firstQuotePriceRespJOSN = result  //持久化给核保比对用

            //为之后的更新QR做准备
            context.kindCodeConvertersConfig = _KIND_CODE_CONVERTERS_CONFIG
            context.compulsoryInsurance = compulsoryInsurance ?: 0
            context.insurance = insurance
            context.seatCount = context.selectedCarModel.approvedPassengersCapacity
            context.sumTax = compulsoryInsurance?.tax?.sumTax ?: 0
            context.insuranceTotalPremium = result?.bizPremium ?: 0//商业险总保费
            //判断是否要循环第二次大循环
            context.breakflag = '跳出第二层大循环'
            getCompulsoryPremiumAndAutoTax context, compulsoryInsurance
            //commercialCaptchaImage商业险验证码 compulsoryCaptchaImage交强险验证码
            if (context.additionalParameters.supplementInfo?.commercialCaptchaImage || context.additionalParameters.supplementInfo?.compulsoryCaptchaImage) {
                log.info '用户已经补充验证码'
                getContinueFSRV true
            } else {
                getLoopBreakFSRV true
            }
        } else {
            def rspMsg = result.head.responseMsg
            if (rspMsg && rspMsg.contains('报价选择车型与平台记录车型信息不符')) {
                //第二次报价，如果发现返回的车辆信息还是有问题，将错误推到前端
                if (context.hasAjustedAutoModel) {
                    log.error '自动修改车型报价失败，{}', rspMsg
                    return getFatalErrorFSRV(rspMsg)
                }
                //修改车型重新报价
                def modelStartKey = '精友车型代码为：'
                def modelEndKey = ',平台车型代码为：'
                def codeStartKey = '车型名称为：'
                def codeEndKey = '。'
                context.auto.autoType.code = getAutoInfo rspMsg, codeStartKey, codeEndKey //先reviewer。提交后，在修正，原因是code 和 autoModel的设定值，应该是其他字段吧？
                context.additionalParameters.supplementInfo.autoModel = getAutoInfo rspMsg, modelStartKey, modelEndKey
                context.hasAjustedAutoModel = true
                getLoopBreakFSRV '重新报价'
            } else {
                getFatalErrorFSRV _RESPONSE_CODE_DESC_MAPPINGS[result.head.responseCode] ?: result.head.responseMsg
            }
        }
    }
}
