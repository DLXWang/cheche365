package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.Address
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.UserAuto
import com.cheche365.cheche.core.repository.AddressRepository
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.AutoRepository
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.GlassTypeRepository
import com.cheche365.cheche.core.repository.IdentityTypeRepository
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.core.repository.InsurancePackageRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.OrderStatusRepository
import com.cheche365.cheche.core.repository.OrderTypeRepository
import com.cheche365.cheche.core.repository.PaymentChannelRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PaymentStatusRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuoteFlowTypeRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.repository.QuoteSourceRepository
import com.cheche365.cheche.core.repository.UserAutoRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.repository.VehicleLicenseRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.OrderOperationInfoService
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.env.Environment
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional

import java.text.SimpleDateFormat

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT2
import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT5
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.util.BusinessUtils.getInsuranceEffectiveDate
import static com.cheche365.cheche.misc.util.BusinessUtils.getStartAndEndDateTime
import static groovy.json.JsonParserType.LAX

@Deprecated
//@Controller
@Transactional(rollbackFor = Exception)
@Slf4j
class FakeDataImporter implements CommandLineRunner {


    private static final _CLI = new CliBuilder().with {
        fdimp longOpt: 'fake-data-import', '执行压力测试数据导入命令 <默认：false>', required: true
        df    longOpt: 'data-file',        '数据文件（JSON格式）',  args: 1, argName: 'data-file', required: true
        rf    longOpt: 'report-file',      '报告文件（CSV格式）',   args: 1, argName: 'report-file', required: true
        h     longOpt: 'help',             '打印此使用信息'
        v     longOpt: 'version',          '打印版本'

        usage = 'fdimp [options]'
        header = """$_APP_VERSION
Options:"""
        footer = """
Report bugs to: zhanghb@cheche365.com"""

        formatter.leftPadding = 4
        formatter.syntaxPrefix = 'Usage: '
        width = formatter.width = 200

        it
    }

    private static final _EXTRACT_BEAN_PROP_FROM_JSON = { json, propNameConvert ->
        if (propNameConvert instanceof String) {
            [(propNameConvert): json[propNameConvert]]
        } else {
            def (propName, convert) = propNameConvert
            [(propName): convert(json[propName])]
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Bean属性列表">

    private static final _DATE_FORMAT_T = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+0000")

    private static final _AUTO_BEAN_PROP_NAMES = [
        'licensePlateNo',
        'vinNo',
        'engineNo',
        [
            'enrollDate',
            {
                (it instanceof Date) ? it : _DATE_FORMAT_T.parse(it)
            }
        ],
        'owner',
        'identity',
        'mobile',
    ]

    private static final _INSURANCE_BEAN_PROP_NAMES = [
        'discount',

        'premium',

        'damageAmount',
        'damagePremium',
        'damageIop',

        'thirdPartyAmount',
        'thirdPartyPremium',
        'thirdPartyIop',

        'theftAmount',
        'theftPremium',
        'theftIop',

        'enginePremium',
        'engineIop',

        'driverAmount',
        'driverPremium',
        'driverIop',

        'passengerAmount',
        'passengerPremium',
        'passengerIop',
        'passengerCount',

        'spontaneousLossAmount',
        'spontaneousLossPremium',

        'scratchAmount',
        'scratchPremium',
        'scratchIop',

        'glassPremium'
    ]

    private static final _COMPULSORY_INSURANCE_BEAN_PROP_NAMES = [
        'compulsoryPremium',
        'autoTax',
    ]

    private static final _QUOTE_RECORD_BEAN_PROP_NAMES = _INSURANCE_BEAN_PROP_NAMES + _COMPULSORY_INSURANCE_BEAN_PROP_NAMES + [
    ]

    private static final _PURCHASE_ORDER_BEAN_PROP_NAMES = [
        'insuredName',
        [
            'createTime',
            {
                (it instanceof Date) ? it : _DATE_FORMAT_T.parse(it)
            }
        ]
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="注入的组件">

    @Autowired
    private Environment env

    @Autowired
    private UserRepository userRepo

    @Autowired
    private UserAutoRepository userAutoRepo

    @Autowired
    private AutoRepository autoRepo

    @Autowired
    private AppointmentInsuranceRepository aiRepo

    @Autowired
    private QuoteRecordRepository qrRepo

    @Autowired
    private PurchaseOrderRepository poRepo

    @Autowired
    private InsurancePackageRepository ipRepo

    @Autowired
    private VehicleLicenseRepository vlRepo

    @Autowired
    private AreaRepository areaRepo

    @Autowired
    private InsuranceCompanyRepository icRepo

    @Autowired
    private GlassTypeRepository glassTypeRepo

    @Autowired
    private QuoteSourceRepository quoteSourceRepo

    @Autowired
    private IdentityTypeRepository idTypeRepo

    @Autowired
    private OrderTypeRepository orderTypeRepo

    @Autowired
    private OrderStatusRepository orderStatusRepo

    @Autowired
    private QuoteFlowTypeRepository qfTypeRepo

    @Autowired
    private PaymentChannelRepository pcRepo

    @Autowired
    private ChannelRepository cRepo

    @Autowired
    private PaymentStatusRepository paymentStatusRepo

    @Autowired
    private PaymentRepository paymentRepo

    @Autowired
    private AddressRepository addrRepo

    @Autowired
    private PlatformTransactionManager tm

    @Autowired
    private AutoService autoService

    @Autowired
    private OrderOperationInfoService orderOperationInfoService

    @Autowired
    private ChannelRepository channelRepo

    @Autowired
    private InsuranceRepository insuranceRepo

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepo

    //</editor-fold>


    @Override
    void run(String... args) throws Exception {

        def options = _CLI.parse args
        if (!options) {
            return
        }
        if (options.h) {
            _CLI.usage()
            return
        }
        if (options.v) {
            println _APP_VERSION
            return
        }

        def dataFile = options.df
        def reportFile = options.rf as File
        def reportFileDir = (reportFile.absolutePath - reportFile.name) as File
        if (!reportFileDir.exists()) {
            reportFileDir.mkdirs()
        }

        def startTime = System.currentTimeMillis()

        //def area = areaRepo.findOne 110000L
        def idType = idTypeRepo.findFirstByName '身份证'
        def glassType = glassTypeRepo.findFirstByName '国产'
        def qfType = qfTypeRepo.findFirstByName '通用流程'
        def orderType = orderTypeRepo.findFirstByName '车险'
        def orderStatus = orderStatusRepo.findOne 5L // 已完成
        def pChannel = pcRepo.findFirstByChannel '线下付款刷卡'
        def paymentStatus = paymentStatusRepo.findFirstByStatus '支付成功'

        def json = new JsonSlurper().with {
            it.type = LAX
            it
        }.parse(dataFile as File)

        def poNoMappings = [:]
        def payments = json.withIndex().collect { jsonPair, index ->
            def (qrJson, poJson) = jsonPair
            def poChannel = channelRepo.findFirstByName qrJson.applicant.registerChannel.name

            def createTime = poJson.createTime instanceof Date ? poJson.createTime : _DATE_FORMAT_T.parse(poJson.createTime)

            def ic = icRepo.findOne qrJson.insuranceCompany.id as Long
            // auto
            def autoProps = _AUTO_BEAN_PROP_NAMES.collectEntries(_EXTRACT_BEAN_PROP_FROM_JSON.curry(qrJson.auto))
            def area = areaRepo.findById qrJson.auto.area.id
            def auto = new Auto(autoProps).with {
                identityType = idType
                it.area = area
                it.createTime = createTime

                autoRepo.save it
            }

            log.debug '索引：{}，auto id：{}', index, auto.id

            def user = qrJson.applicant.id ? userRepo.findOne(qrJson.applicant.id as Long)
                : new User(name: qrJson.applicant.name, mobile: qrJson.applicant.mobile).with {
                registerChannel = poChannel
                audit = 20   // TODO 关键参数
                bound = true

                userRepo.save it
            }

            def (startCreateTime, endCreateTime) = getStartAndEndDateTime(createTime)

            def poNo = poNoMappings[_DATE_FORMAT3.format(startCreateTime)]
            if (poNo) {
                poNo = poNo[0..<9] + (((poNo[9..-1] as long) + 1) as String).padLeft(6, '0')
            } else {
                poNo = poRepo.findLastOrderNo(_DATE_FORMAT5.format(startCreateTime), _DATE_FORMAT5.format(endCreateTime)).with { lastPoNo ->

                    def dateText = _DATE_FORMAT2.format createTime
                    if (lastPoNo) {
                        lastPoNo[0..<9] + (((lastPoNo[9..-1] as long) + 100) as String).padLeft(6, '0')
                    } else {
                        "${'production' == env.getProperty('spring.profiles.active') ? 'I' : 'T'}$dateText${'1'.padLeft(6, '0')}"
                    }
                }
            }
            poNoMappings[_DATE_FORMAT3.format(startCreateTime)] = poNo
            log.debug 'poNo：{}', poNo

            // user auto，无需与其他实体关联
            def userAuto = new UserAuto(user: user, auto: auto).with {
                userAutoRepo.save it
            }
            log.debug '索引：{}，userAuto id：{}', index, userAuto.id

            // insurance package
            def insurancePackage = ipRepo.findFirstByUniqueString qrJson.insurancePackage.uniqueString
            if (!insurancePackage) {
                insurancePackage = new InsurancePackage(qrJson.insurancePackage).with {
                    it.glassType = glass ? glassType : null

                    ipRepo.save it
                }
            }
            log.debug '索引：{}，insurancePackage id：{}', index, insurancePackage.id

            // quote record
            def qrProps = _QUOTE_RECORD_BEAN_PROP_NAMES.collectEntries(_EXTRACT_BEAN_PROP_FROM_JSON.curry(qrJson))
            def quoteRecord = new QuoteRecord(qrProps).with {
                it.insurancePackage = insurancePackage
                insuranceCompany = ic
                it.area = area
                applicant = user
                it.auto = auto
                quoteFlowType = qfType
                it.createTime = createTime

                qrRepo.save it
            }
            log.debug '索引：{}，quoteRecord id：{}', index, quoteRecord.id

            def premiumSum = quoteRecord.with {
                premium + compulsoryPremium + autoTax
            }

            def address
            if(poJson.deliveryAddress) {
                address = new Address(
                    applicant: user,
                    mobile: qrJson.applicant.mobile,
                    street: poJson.deliveryAddress.street,
                    district: poJson.deliveryAddress.district,
                    createTime: createTime,
                    city: poJson.deliveryAddress.city
                )
            } else {
                // address
                address = new Address(
                    applicant: user,
                    mobile: qrJson.applicant.mobile,
                    street: '北京市朝阳区北苑路甲13号北辰新纪元2座704室',
                    district: '110105',
                    createTime: createTime,
                    city: '110000'
                ).with {
                    addrRepo.save it
                }
            }

            log.debug '索引：{}，address id：{}', index, address.id

            // purchase order
            def poProps = _PURCHASE_ORDER_BEAN_PROP_NAMES.collectEntries(_EXTRACT_BEAN_PROP_FROM_JSON.curry(poJson))
            def purchaseOrder = new PurchaseOrder(poProps).with {
                audit = 20      // TODO 关键参数
                orderNo = poNo
                objId = quoteRecord.id
                it.area = area
                applicant = user
                it.auto = auto
                type = orderType
                status = orderStatus
                payableAmount = premiumSum
                paidAmount = premiumSum
                channel = pChannel
                deliveryAddress = address
                timePeriod = paidAmount > 5000 ? '10:00~12:00' : '14:00~16:00'
                def dateFormat3 = _DATE_FORMAT3
                def dateTimeFormat3 = _DATETIME_FORMAT3
                sendDate = dateFormat3.parse dateTimeFormat3.format(
                    getLocalDate(createTime).plusDays(paidAmount > 5000 ? 2 : 3))
                it.createTime = createTime
                sourceChannel = poChannel

                poRepo.save it
            }
            log.debug '索引：{}，purchaseOrder id：{}', index, purchaseOrder.id


            def (startDate, endDate) = getInsuranceEffectiveDate(createTime)
            def insuranceProp = [
                createTime      : createTime,
                dicount         : 1,
                applicant       : user,
                applicantName   : user.name,
                applicantIdNo   : user.identity,
                applicantMobile : user.mobile,
                insuredIdNo     : user.identity,
                insuredMobile   : user.mobile,
                insuredName     : user.name,
                auto            : auto,
                insuranceCompany: ic,
                insurancePackage: insurancePackage,
                quoteRecord     : quoteRecord,

                effectiveDate   : startDate,
                expireDate      : endDate,
            ]

            // insurance
            def insuranceProps = _INSURANCE_BEAN_PROP_NAMES.collectEntries(_EXTRACT_BEAN_PROP_FROM_JSON.curry(qrJson)) +
                insuranceProp
            def insurance = new Insurance(insuranceProps).with {
                policyNo = null
                proposalNo = null

                insuranceRepo.save it
            }
            log.debug '索引：{}，insurance id：{}', index, insurance.id

            // compulsory insurance
            def compulsoryInsuranceProps = _COMPULSORY_INSURANCE_BEAN_PROP_NAMES.collectEntries(_EXTRACT_BEAN_PROP_FROM_JSON.curry(qrJson)) +
                insuranceProp
            def compulsoryInsurance = new CompulsoryInsurance(compulsoryInsuranceProps).with {
                policyNo = null
                proposalNo = null

                compulsoryInsuranceRepo.save it
            }
            log.debug '索引：{}，compulsoryInsurance id：{}', index, compulsoryInsurance.id

            // payment
            def payment = new Payment(
                amount: premiumSum,
                purchaseOrder: purchaseOrder,
                status: paymentStatus,
                user: user,
                channel: pChannel,
                comments: pChannel.description,
                createTime: createTime
            ).with {
                paymentRepo.save it
            }
            log.debug '索引：{}，payment id：{}', index, payment.id

            // order operation info
            orderOperationInfoService.saveOrderCenterInfo purchaseOrder

            payment
        }.with { payments ->
            reportFile.withPrintWriter { writer ->
                payments.each { payment ->
                    writer.println payment.id
                }
            }
        }

        log.debug '插入总计{}个payment记录，总耗时{}', payments.size(), (System.currentTimeMillis() - startTime) / 1000
        throw new Exception()
    }

}
