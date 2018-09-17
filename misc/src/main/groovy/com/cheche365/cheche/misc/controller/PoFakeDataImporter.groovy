package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.Address
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.OrderAgent
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.UserAuto
import com.cheche365.cheche.core.model.UserType
import com.cheche365.cheche.core.repository.AddressRepository
import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.AutoRepository
import com.cheche365.cheche.core.repository.BusinessActivityRepository
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.GlassTypeRepository
import com.cheche365.cheche.core.repository.IdentityTypeRepository
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.core.repository.InsurancePackageRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.OrderAgentRepository
import com.cheche365.cheche.core.repository.OrderSourceTypeRepository
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
import com.cheche365.cheche.misc.util.BusinessUtils

import java.time.format.DateTimeFormatterBuilder
import java.util.concurrent.ConcurrentHashMap

import static com.cheche365.cheche.common.Constants._DATE_FORMAT5
import static com.cheche365.cheche.core.model.IdentityType.Enum.IDENTITYCARD
import static com.cheche365.cheche.misc.Constants._CURRENT_AUDIT
import static groovyx.gpars.GParsPool.withPool
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional

import java.text.SimpleDateFormat

import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.util.BusinessUtils.getInsuranceEffectiveDate
import static com.cheche365.cheche.misc.util.BusinessUtils.getStartAndEndDateTime
import static groovy.json.JsonParserType.LAX

/**
 * -Pargs=" -poimp -df D:/201705/pos -rf D:/201705/pos/imp"
 */
@Controller
@Slf4j
class PoFakeDataImporter implements CommandLineRunner {


    private static final _CLI = new CliBuilder().with {
        // @formatter:off
        poimp longOpt: 'fake-data-import', 'PO导入数据导入命令 <默认:false>', required: true
        df    longOpt: 'data-file',        '数据文件（JSON格式）', args: 1, argName: 'data-file', required: true
        rf    longOpt: 'report-file',      '报告文件（CSV格式）',  args: 1, argName: 'report-file', required: true
        h     longOpt: 'help',             '打印此使用信息'
        v     longOpt: 'version',          '打印版本'
        // @formatter:on

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

//    private static final _DATE_FORMAT_T = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+0000")

    private static final _AUTO_BEAN_PROP_NAMES = [
        'licensePlateNo',
        'vinNo',
        'engineNo',
        [
            'enrollDate',
            {
                (it instanceof Date) ? it : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+0000").parse(it)
            }
        ],
        'owner',
        'identity',
        'mobile',
    ]

    private static final _INSURANCE_BEAN_PROP_NAMES = [
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
//        'discount',
        'originalPolicyNo',
        'compulsoryOriginalPolicyNo'
    ]

    private static final _PURCHASE_ORDER_BEAN_PROP_NAMES = [
        'insuredName',
        'payableAmount',
        'paidAmount',
        [
            'createTime',
            {
                (it instanceof Date) ? it : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+0000").parse(it)
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
    private OrderSourceTypeRepository orderSourceTypeRepo

    @Autowired
    private InsuranceRepository insuranceRepo

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepo

    @Autowired
    private AgentRepository agentRepo

    @Autowired
    private OrderAgentRepository orderAgentRepo

    @Autowired
    private BusinessActivityRepository businessActivityRepo

    //</editor-fold>


    private static final _AUDIT = _CURRENT_AUDIT

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

        def dataFileDir = options.df
        def reportFileDir = options.rf as File
        if (!reportFileDir.exists()) {
            if (!reportFileDir.exists()) {
                reportFileDir.mkdirs()
            }
        }

        def startTime = System.currentTimeMillis()

        def glassType = glassTypeRepo.findFirstByName '国产'
        def qfType = qfTypeRepo.findFirstByName '通用流程'
        def orderType = orderTypeRepo.findFirstByName '车险'
        def orderStatus = orderStatusRepo.findFirstByStatus '订单完成'
        def paymentStatus = paymentStatusRepo.findFirstByStatus '支付成功'


        withPool(6) {
            def fileOrderNoMappings = [:] as ConcurrentHashMap
            new File(dataFileDir).listFiles().findAll {
                it.name.contains('data.json_')
            }.eachWithIndexParallel { dataFile, index ->

                def reportFile = new File(reportFileDir, "rf_${dataFile.name[dataFile.name.lastIndexOf('_') + 1..-1]}.csv")
                reportFile.createNewFile()

                def json = new JsonSlurper().with {
                    it.type = LAX
                    it
                }.parse(dataFile as File)

                try {
                    def payments = importFile(json, reportFile, glassType, qfType, orderType, orderStatus, paymentStatus, (index * 550), index, fileOrderNoMappings)
                    log.debug '插入总计 {}个payment记录，总耗时{}', payments.size(), (System.currentTimeMillis() - startTime) / 1000
                } catch (ex) {
                    log.error "File IDX: $index", ex
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception)
    def importFile(json, reportFile, glassType, qfType, orderType, orderStatus, paymentStatus, orderNoOffset, idx, fileOrderNoMappings) {
        def orderNoMappings = [:]
        json.withIndex().collect { jsonPair, index ->
            def (qrJson, poJson) = jsonPair

//            def createTime = poJson.createTime instanceof Date ? poJson.createTime : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+0000").parse(poJson.createTime)
////            createTime.setHours(createTime.getHours() + 8) // TODO 转过来从现象看需加8小时

            def createTime = _DATE_FORMAT5.parse(poJson.description)

            def ic = icRepo.findOne qrJson.insuranceCompany.id as Long

            def area = areaRepo.findById qrJson.auto.area.id

            def userType = qrJson.applicant.userType?.name == '代理' ? UserType.Enum.Agent : UserType.Enum.Customer
            def poChannel = poJson.sourceChannel?.name ? channelRepo.findFirstByName(poJson.sourceChannel.name) : channelRepo.findFirstByName('UNKNOWN')
            def paymentChannel = poJson.channel?.channel ? pcRepo.findFirstByChannel(poJson.channel.channel) : pcRepo.findFirstByChannel('线下付款刷卡')
            def orderSourceType = poJson.OrderSourceType?.name ? orderSourceTypeRepo.findFirstByName(poJson.OrderSourceType?.name) : null
            def orderSourceId = poJson.orderSourceId ? businessActivityRepo.findFirstByCode(poJson.orderSourceId) : null
            if (orderSourceId) {
                orderSourceId = orderSourceId.id
            }
            def ownerIdentity = BusinessUtils.getRandomId()

            def user = qrJson.applicant.id ? userRepo.findOne(qrJson.applicant.id as Long)
                : new User().with {
                mobile = qrJson.applicant.mobile
                it.userType = userType
                registerChannel = poChannel
                audit = _AUDIT
                bound = true

                userRepo.save it
            }

            def autoProps = _AUTO_BEAN_PROP_NAMES.collectEntries(_EXTRACT_BEAN_PROP_FROM_JSON.curry(qrJson.auto))
            def auto = new Auto(autoProps).with {
                it.area = area
                it.createTime = createTime
                it.identity = ownerIdentity
                it.identityType = IDENTITYCARD

                autoRepo.save it
            }
            log.debug 'File IDX: {}, Obj IDX:{}，auto id:{}', idx, index, auto.id

            def userAuto = new UserAuto(user: user, auto: auto).with {
                userAutoRepo.save it
            }
            log.debug 'File IDX: {}, IDX:{}，userAuto id:{}', idx, index, userAuto.id


            def (startCreateTime, _) = getStartAndEndDateTime(createTime)
            def orderNo = getOrderNo orderNoMappings, startCreateTime, createTime, orderNoOffset, fileOrderNoMappings
            log.debug 'orderNo:{}', orderNo

            // insurance package
            def insurancePackage = ipRepo.findFirstByUniqueString qrJson.insurancePackage.uniqueString
            if (!insurancePackage) {
                insurancePackage = new InsurancePackage(qrJson.insurancePackage).with {
                    it.glassType = glass ? glassType : null

                    ipRepo.save it
                }
            }
            log.debug 'File IDX: {}, IDX:{}，insurancePackage id:{}', idx, index, insurancePackage.id

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
            log.debug 'File IDX: {}, IDX:{}，quoteRecord id:{}', idx, index, quoteRecord.id


            def address
            if(poJson.deliveryAddress) {
                address = new Address(
                    applicant: user,
                    mobile: qrJson.applicant.mobile,
                    street: poJson.deliveryAddress.street,
                    district: poJson.deliveryAddress.district,
                    createTime: createTime,
                    city: poJson.deliveryAddress.city
                ).with {
                    addrRepo.save it
                }
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


            def premiumSum = quoteRecord.with {
                premium + compulsoryPremium + autoTax
            }

            // purchase order
            def poProps = _PURCHASE_ORDER_BEAN_PROP_NAMES.collectEntries(_EXTRACT_BEAN_PROP_FROM_JSON.curry(poJson))
            def purchaseOrder = new PurchaseOrder(poProps).with {
                audit = _AUDIT
                it.orderNo = orderNo
                objId = quoteRecord.id
                it.area = area
                applicant = user
                it.auto = auto
                type = orderType
                status = orderStatus

                deliveryAddress = address
                it.createTime = createTime
                it.updateTime = updateTime(it.createTime)

                timePeriod = paidAmount > 5000 ? '10:00~12:00' : '14:00~16:00'
                def dateFormat3 = new SimpleDateFormat('yyyy-MM-dd')
                def dateTimeFormat3 = new DateTimeFormatterBuilder().appendPattern('yyyy-MM-dd').toFormatter()
                sendDate = dateFormat3.parse dateTimeFormat3.format(getLocalDate(createTime).plusDays(paidAmount > 5000 ? 1 : 2))

                sourceChannel = poChannel
                channel = paymentChannel
                it.orderSourceType = orderSourceType
                it.orderSourceId = orderSourceId

                it.payableAmount = it.payableAmount ?: premiumSum
                it.paidAmount = it.paidAmount ?: premiumSum

                poRepo.save it
            }
            log.debug 'File IDX: {}, IDX:{}，purchaseOrder id:{}', idx, index, purchaseOrder.id

            def agent = agentRepo.findFirstByName poJson.agent?.name
            if (agent) {
                new OrderAgent(purchaseOrder: purchaseOrder, agent: agent).with {
                    orderAgentRepo.save it
                }
            }

            def (startDate, endDate) = getInsuranceEffectiveDate(createTime)
            def insuranceProp = [
                createTime      : createTime,
                applicant       : user,
                applicantName   : user.name ?: auto.owner,
                applicantIdNo   : user.identity ?: ownerIdentity,
                applicantMobile : user.mobile,
                insuredIdNo     : user.identity ?: ownerIdentity,
                insuredMobile   : user.mobile,
                insuredName     : user.name ?: auto.owner,
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
                policyNo = quoteRecord.originalPolicyNo
                insuranceRepo.save it
            }
            log.debug 'File IDX: {}, IDX:{}，insurance id:{}', idx, index, insurance.id

            // compulsory insurance
            def compulsoryInsuranceProps = _COMPULSORY_INSURANCE_BEAN_PROP_NAMES.collectEntries(_EXTRACT_BEAN_PROP_FROM_JSON.curry(qrJson)) +
                insuranceProp
            def compulsoryInsurance = new CompulsoryInsurance(compulsoryInsuranceProps).with {
                policyNo = quoteRecord.compulsoryOriginalPolicyNo
                compulsoryInsuranceRepo.save it
            }
            log.debug 'File IDX: {}, IDX:{}，compulsoryInsurance id:{}', idx, index, compulsoryInsurance.id

            // payment
            def payment = new Payment(
                amount: purchaseOrder.paidAmount,
                purchaseOrder: purchaseOrder,
                status: paymentStatus,
                user: user,
                channel: paymentChannel,
                comments: paymentChannel.description,
                createTime: createTime,
                paymentType : PaymentType.Enum.INITIALPAYMENT_1
            ).with {
                paymentRepo.save it
            }
            log.debug 'File IDX: {}, IDX:{}，payment id:{}', idx, index, payment.id

            // order operation info
            orderOperationInfoService.saveOrderCenterInfo purchaseOrder

            orderNo
        }.with { payments ->
            reportFile.withPrintWriter { writer ->
                payments.each { payment ->
                    writer.println payment
                }
            }
            payments
        }
    }


    private getOrderNo(poNoMappings, startCreateTime, createTime, orderNoOffset, fileOrderNoMappings) {
        def orderNo = poNoMappings[new SimpleDateFormat('yyyy-MM-dd').format(startCreateTime)]
        if (orderNo) {
            orderNo = orderNo[0..<9] + (((orderNo[9..-1] as long) + 1) as String).padLeft(6, '0')
        } else {
            def orderDateText = "${'production' == env.getProperty('spring.profiles.active') ? 'I' : 'I'}${new SimpleDateFormat('yyyyMMdd').format(createTime)}%" as String
            orderNo = (
                fileOrderNoMappings[orderDateText] ?:

                    poRepo.findLastOrderNo(orderDateText).with { lastPoNo ->
                        def dateText = new SimpleDateFormat('yyyyMMdd').format(startCreateTime)
                        ((ConcurrentHashMap) fileOrderNoMappings).putIfAbsent(orderDateText, lastPoNo ?: "${'production' == env.getProperty('spring.profiles.active') ? 'I' : 'I'}$dateText${'1'.padLeft(6, '0')}")
                        fileOrderNoMappings[orderDateText]
                    }
            ).with { lastPoNo ->
                lastPoNo[0..<9] + (((lastPoNo[9..-1] as long) + (orderNoOffset ?: 10)) as String).padLeft(6, '0')
            }
        }
        poNoMappings[new SimpleDateFormat('yyyy-MM-dd').format(startCreateTime)] = orderNo

        orderNo
    }

    def updateTime(date) {
        def d = new Date(date.getTime())
        d.setMinutes(d.getMinutes() + RandomUtils.nextInt(100, 24 * 60 * 2))
        d
    }

}
