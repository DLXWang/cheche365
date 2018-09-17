package com.cheche365.cheche.huanong.flow

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT2
import static com.cheche365.cheche.common.Constants._DATE_FORMAT5
import static com.cheche365.cheche.huanong.flow.Constants._CERTI_TYPE_MAPPING
import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getRandomMobile
import static com.cheche365.cheche.huanong.flow.Constants._USE_CHARACTER
import static com.cheche365.cheche.huanong.util.BusinessUtils.createCoverage
import static com.cheche365.cheche.huanong.util.BusinessUtils.getAllKindItems
import static com.cheche365.cheche.huanong.util.BusinessUtils.getBirthdayFromId
import static com.cheche365.cheche.huanong.util.BusinessUtils.getEndTime
import static com.cheche365.cheche.huanong.util.BusinessUtils._I2O_PREMIUM_CONVERTER_INSURANCE
import static com.cheche365.cheche.huanong.util.BusinessUtils._KEY_VALUES
import static com.cheche365.cheche.huanong.util.BusinessUtils._KIND_CODE_CONVERTERS_CONFIG
import static com.cheche365.cheche.huanong.util.BusinessUtils.selectNeededConfig
import static com.cheche365.cheche.parser.util.BusinessUtils.getQuoteKindItemParams
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static java.time.LocalDateTime.now



/**
 * 华农报价参数
 */
class Handlers {
    //精准报价
    private static final _QUOTED_PRICE_RPG_BASE = { paramPostProcessor, context ->
        //根据车车险种,针对华农的险种进行险种封装
        context.kindCodeConvertersConfig = _KIND_CODE_CONVERTERS_CONFIG
        //获取套餐
        def insurancePackage = context.accurateInsurancePackage

        def compulsoryStartDate = context.extendedAttributes?.compulsoryStartDate ?: '' //交强险起始日期
        //商业险 整理参数格式和相关的信息
        def commercialStartDate = context.extendedAttributes?.commercialStartDate ?: ''//商业险起保日期
        def selectedCarModel = context.selectedCarModel
        def auto = context.auto
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate//判断是否是过户车
        def enrollDateText = _DATE_FORMAT5.format(auto.enrollDate)

        def transferDateText = context.additionalParameters.supplementInfo?.transferDate?.with { transferDate ->
            transferDate ? _DATE_FORMAT5.format(transferDate) : null
        }

        def parameter = []

        //交强险结构
        if (isCompulsoryOrAutoTaxQuoted(insurancePackage)) {
            //交强险 这里是为了拼接所需要的格式 以及整理参数格式和相关的信息
            def compulsoryParams = [
                createCoverage(context, 'BZ', _KEY_VALUES['BZ'], 0, 0)
            ]
            //如果投交强险
            parameter << [
                contractMain        : [
                    ValidDate       : _DATE_FORMAT5.format(compulsoryStartDate),//生效日期（起保日期）
                    ExpiryDate      : getEndTime(compulsoryStartDate),//失效日期（终保日期）
                    ArgueSolution   : '',//不传时默认1-诉讼
                    ArgueSolutionCom: '',//诉讼/仲裁机构
                    agQueryCode     : '',//中介校验查询码
                    proposalType    : '',//投保单单证种类
                    riskCode        : '0507',//交强险
                ],
                coverage            : compulsoryParams,
                tax                 : [
                    TaxConditionCode: '1N',//缴费类型为纳税
                    TaxPayerName    : context.applicant.name,//为了测试，写死
                ],
                plantFormMessageBean: [
                    QuerySequenceNo: context.querySequenceNo_JQ ?: '',//平台查询码
                    QueryDate      : '',//平台查询日期
                    ExpireDate     : '',//查询有效止期
                    CheckCode      : context.additionalParameters.supplementInfo?.compulsoryCaptchaImage ?: '',//校验码图片转换后的串
                    CheckCodeRe    : '',//校验码 半角数字和字母
                    Question       : '',//平台校验问题
                    Answer         : '',//校验问题答案
                ],
            ]
        }

        //商业险种结构
        if (isCommercialQuoted(insurancePackage)) {
            def commercialParams = getQuoteKindItemParams context, getAllKindItems(_KIND_CODE_CONVERTERS_CONFIG), selectNeededConfig(context, _KIND_CODE_CONVERTERS_CONFIG), _I2O_PREMIUM_CONVERTER_INSURANCE
            //商业险
            parameter << [
                contractMain        : [
                    ValidDate       : _DATE_FORMAT5.format(commercialStartDate),//生效日期（起保日期）
                    ExpiryDate      : getEndTime(commercialStartDate),//失效日期（终保日期）
                    ArgueSolution   : '',//不传时默认1-诉讼
                    ArgueSolutionCom: '',//诉讼/仲裁机构
                    agQueryCode     : '',//中介校验查询码
                    proposalType    : '',//投保单单证种类
                    riskCode        : '0520',
                ],
                coverage            : commercialParams,
                plantFormMessageBean: [
                    QuerySequenceNo: context.querySequenceNo_SY ?: '',//平台查询码
                    QueryDate      : '',//平台查询日期
                    ExpireDate     : '',//查询有效止期
                    CheckCode      : context.additionalParameters.supplementInfo?.commercialCaptchaImage ?: '',//校验码图片转换后的串
                    CheckCodeRe    : '',//校验码 半角数字和字母
                    Question       : '',//平台校验问题
                    Answer         : '',//校验问题答案
                ],
            ]
        }

        //报文整体结构
        def params = [
            BusinessMain : [
                //业务基础信息
                SaleSys: '2',//销售渠道     怎么获取 0是直接渠道 2-专业代理
            ],
            businessType : 'T',//业务类型
            Customer     : [
                //客户信息
                [
                    //投保人
                    id                  : '',//客户标识码
                    Type                : '02',//客户类型
                    Role                : '1',//客户角色
                    Name                : auto.owner,//名称
                    IdentifyType        : _CERTI_TYPE_MAPPING[auto.identityType.id],//user.identityType,//证件类型
                    IdentifyNumber      : auto.identity,//user.identity,//证件号码 为了测试，先写死
                    Mobile              : context.extendedAttributes?.verificationMobile ?: context.applicant?.mobile ?: randomMobile,//user.mobile,//移动电话 为了测试，先写死
                    Addr                : context.applicant?.area?.name ?: auto.area?.name,//地址 为了测试，先写死
                    IdentifyValidDate   : '',//证件有效起期--用于北京
                    IdentifyValidEndDate: '',//证件有效止期--用于北京
                    IdentifyIssuedCom   : '',//身份证签发机构--用于北京
                    Sex                 : '',//性别--用于北京
                    Birthday            : '',//出生日期--用于北京
                    Nation              : '',//民族--用于北京
                    NativePlace         : '',//户籍地址--用于北京
                    Email               : context.email,//Email
                ],
                [
                    //被保险人
                    id                  : '',//客户标识码
                    Type                : '02',//客户类型
                    Role                : '2',//客户角色
                    Name                : auto.owner,//名称
                    IdentifyType        : _CERTI_TYPE_MAPPING[auto.identityType.id],//user.identityType,//证件类型  为了测试，先写死
                    IdentifyNumber      : auto.identity,//user.identity,//证件号码 为了测试，先写死
                    Mobile              : context.extendedAttributes?.verificationMobile ?: context.applicant?.mobile ?: randomMobile,//user.mobile,//移动电话 为了测试，先写死
                    Addr                : context.applicant?.area?.name ?: auto.area?.name,//地址 为了测试，先写死
                    IdentifyValidDate   : '',//证件有效起期--用于北京
                    IdentifyValidEndDate: '',//证件有效止期--用于北京
                    IdentifyIssuedCom   : '',//身份证签发机构--用于北京
                    Sex                 : '',//性别--用于北京
                    Birthday            : '',//出生日期--用于北京
                    Nation              : '',//民族--用于北京
                    NativePlace         : '',//户籍地址--用于北京
                    Email               : context.email,//Email
                ],
                [
                    //车主
                    id                  : '',//客户标识码
                    Type                : '02',//客户类型
                    Role                : '3',//客户角色
                    Name                : auto.owner,//名称
                    IdentifyType        : _CERTI_TYPE_MAPPING[auto.identityType.id],//user.identityType,//证件类型  为了测试，先写死
                    IdentifyNumber      : auto.identity,//user.identity,//证件号码 为了测试，先写死
                    Mobile              : context.extendedAttributes?.verificationMobile ?: context.applicant?.mobile ?: randomMobile,//user.mobile,//移动电话 为了测试，先写死
                    Addr                : context.applicant?.area?.name ?: auto.area?.name,//地址 为了测试，先写死
                    IdentifyValidDate   : '',//证件有效起期--用于北京
                    IdentifyValidEndDate: '',//证件有效止期--用于北京
                    IdentifyIssuedCom   : '',//身份证签发机构--用于北京
                    Sex                 : '',//性别--用于北京
                    Birthday            : '',//出生日期--用于北京
                    Nation              : '',//民族--用于北京
                    NativePlace         : '',//户籍地址--用于北京
                    Email               : context.email,//Email  为了测试，县写死
                ],
            ],
            InsuredObject: [
                //保险采集车信息
                PolicyCar        : [
                    VIN               : auto.vinNo,//VIN/车架号
                    Engine            : auto.engineNo,//发动机号
                    PlateNo           : auto.licensePlateNo,//车牌号
                    Model             : auto.autoType.code,//品牌型号
                    chgOwnerFlag      : transferFlag ? 1 : 0,//过户标志
                    RegisterDate      : enrollDateText,//行驶证注册登记日期（初登日期）
                    TransferDate      : transferFlag ? transferDateText : '',//转移登记日期（过户日期）
                    NewCarSign        : auto.licensePlateNo ? true : false,//新车标志
                    EcdemicVehicleFlag: '',//外地车标志
                    MotorUsageTypeCode: _USE_CHARACTER."${auto.useCharacter?.id ?: 21}",//使用性质
                    MotorTypeCode     : 'A0',//车辆种类
                    CarCheckStatus    : '',//验车状态 --选传
                    CarChecker        : '',//验车人 --选传
                    CarCheckTime      : '',//验车时间 --选传
                    CarCheckReason    : '',//验车原因 --选传
                    LoanVehicleFlag   : false,//是否车贷投保多年标志  为了测试，先写死
                    LoanStatus        : false,//贷款车标志  为了测试，先写死
                    IssueDate         : enrollDateText,//行驶证发证日期
                    transferMark      : transferFlag ? 1 : 0,//过户车标志
                    CertificateType   : '',//车辆来历凭证种类--北京
                    CertificateNo     : '',//车辆来历凭证编号--北京
                    CertificateDate   : '',//车辆来历凭证所载日期--北京
                    NoDamageYears     : '',//跨省首年投保未出险证明的年数
                    platformOwnerName : '',//北京平台返回车主名称 --选传
                    firstBeneMan      : '',//第一受益人--贷款车标志勾选时填写 为了测试，先写死
                ],
                //简化版精友信息
                simpleStadarCar  : [
                    LocalModelCode            : selectedCarModel.localModelCode,//本地车型代码
                    ModelCode                 : selectedCarModel.modelCode,//行业车型代码
                    CarAlias                  : selectedCarModel.carAlias,//车型别名(多个别名用,分开)
                    Brand                     : selectedCarModel.brand,//品牌名称
                    BrandCode                 : selectedCarModel.brandCode,//品牌编码
                    Series                    : selectedCarModel.series,//车系名称
                    SeriesCode                : selectedCarModel.seriesCode,//车系编码
                    ApprovedPassengersCapacity: selectedCarModel.approvedPassengersCapacity,//核定载客
                    ApprovedLoad              : selectedCarModel.approvedLoad,//核定载质量
                    Displacement              : selectedCarModel.displacement,//排量(ml)
                    Power                     : selectedCarModel.power,//功率(kw)
                    UnladenMass               : selectedCarModel.unladenMass,//整备质量
                    GrossMass                 : selectedCarModel.grossMass,//总质量
                    TractionMass              : selectedCarModel.tractionMass,//准牵引总质量
                    PurchasePrice             : selectedCarModel.purchasePrice,//新车购置价
                    PurchasePriceTax          : selectedCarModel.purchasePriceTax,//新车购置价格(含税)
                    EnergyTypes               : selectedCarModel.energyTypes,//能源种类
                    TransmissionType          : selectedCarModel.transmissionType,//变速箱
                    LfDate                    : selectedCarModel.lfDate ?: null,//出厂日期
                    checkImg                  : selectedCarModel.checkImg ?: null,//交管车辆查询校验码
                    checkNo                   : selectedCarModel.checkNo ?: null,//交管车辆查询码
                    PriceFloatRateMax         : '',//价格浮动区间起
                    PriceFloatRateMin         : '',//价格浮动区间止
                ],
                //指定设备
                SpecializedDevice: [
                    DeviceName : '',//设备名称
                    BuyDate    : '',//设备购买时间
                    Price      : '',//单价
                    Amount     : '',//购买个数
                    DeviceValue: '',//设备实际价值

                ]
            ],
            Contract     : parameter,
            orderNo      : ''
        ]

        paramPostProcessor context, params

    }

    private static final _QUOTED_PRICE_RPG_POST_PROCESSOR_DEFAULT = {
        context, params -> params
    }

    private static final _QUOTED_PRICE_RPG_POST_PROCESSOR_110000 = {
        context, params ->
            def nowDate = now()
            def identifyValidDate = _DATETIME_FORMAT2.format nowDate
            def identifyValidEndDate = _DATETIME_FORMAT2.format nowDate.plusYears(10)
            def idInfo = params.Customer.collect { customer ->
                customer + [
                    IdentifyValidDate   : identifyValidDate,//证件有效起期--用于北京
                    IdentifyValidEndDate: identifyValidEndDate,//证件有效止期--用于北京
                    IdentifyIssuedCom   : '签发机构',//身份证签发机构--用于北京
                    Sex                 : getGenderByIdentity(customer.IdentifyNumber),//性别--用于北京
                    Birthday            : getBirthdayFromId(customer.IdentifyNumber) + ' 00:00:00',//出生日期--用于北京
                    Nation              : '1',//民族--用于北京
                    NativePlace         : context.area.name,//户籍地址--用于北京
                ]
            }
            params + [Customer: idInfo]

    }

    static final _QUOTED_PRICE_RPG_DEFAULT = _QUOTED_PRICE_RPG_BASE.curry(_QUOTED_PRICE_RPG_POST_PROCESSOR_DEFAULT)
    static final _QUOTED_PRICE_RPG_110000 = _QUOTED_PRICE_RPG_BASE.curry(_QUOTED_PRICE_RPG_POST_PROCESSOR_110000)

}
