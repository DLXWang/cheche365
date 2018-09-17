package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.InsuranceField
import com.cheche365.cheche.core.model.InsurancePerson
import com.cheche365.cheche.core.model.InsurancePolicy
import com.cheche365.cheche.core.model.InsurancePolicyPerson
import com.cheche365.cheche.core.model.InsuranceProduct
import com.cheche365.cheche.core.model.InsuranceProductField
import com.cheche365.cheche.core.model.InsuranceProductType
import com.cheche365.cheche.core.model.InsuranceQuote
import com.cheche365.cheche.core.model.InsuranceQuotePerson
import com.cheche365.cheche.core.model.OrderAgent
import com.cheche365.cheche.core.model.OrderOperationInfo
import com.cheche365.cheche.core.model.OrderTransmissionStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.AddressRepository
import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.AutoRepository
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.GlassTypeRepository
import com.cheche365.cheche.core.repository.IdentityTypeRepository
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.core.repository.InsuranceFieldRepository
import com.cheche365.cheche.core.repository.InsuranceFieldTypeRepository
import com.cheche365.cheche.core.repository.InsurancePackageRepository
import com.cheche365.cheche.core.repository.InsurancePersonRepository
import com.cheche365.cheche.core.repository.InsurancePolicyPersonRepository
import com.cheche365.cheche.core.repository.InsurancePolicyRepository
import com.cheche365.cheche.core.repository.InsuranceProductFieldRepository
import com.cheche365.cheche.core.repository.InsuranceProductRepository
import com.cheche365.cheche.core.repository.InsuranceProductStatusRepository
import com.cheche365.cheche.core.repository.InsuranceQuoteFieldRepository
import com.cheche365.cheche.core.repository.InsuranceQuotePersonRepository
import com.cheche365.cheche.core.repository.InsuranceQuoteRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.OrderAgentRepository
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
import java.util.concurrent.ConcurrentHashMap

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.core.model.IdentityType.Enum.IDENTITYCARD
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.OFFLINEBYCARD_6
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.Constants._CURRENT_AUDIT
import static com.cheche365.cheche.misc.util.BusinessUtils.getInsuranceEffectiveDate
import static com.cheche365.cheche.misc.util.BusinessUtils.getStartAndEndDateTime
import static groovy.json.JsonParserType.LAX
import static groovyx.gpars.GParsPool.withPool

/**
 * -Pargs=" -natimp -df D:/20170504/nonauto/toubaoli/  -rf D:/20170504/nonauto/toubaoli/imp"
 */
@Controller
@Slf4j
class ZNonAutoImporter implements CommandLineRunner {


    private static final _CLI = new CliBuilder().with {
        // @formatter:off
        natimp longOpt: 'fake-data-import', '执行压力测试数据导入命令 <默认:false>', required: true
        df     longOpt: 'data-file',        '数据文件（JSON格式）',  args: 1, argName: 'data-file', required: true
        rf     longOpt: 'report-file',      '报告文件（CSV格式）',   args: 1, argName: 'report-file', required: true
        h      longOpt: 'help',             '打印此使用信息'
        v      longOpt: 'version',          '打印版本'
        // @formatter:on

        usage = 'natimp [options]'
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

    private static final get_DATE_FORMAT_T() { new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+0000") }

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
        'discount',
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

    @Autowired
    private AgentRepository agentRepo

    @Autowired
    private OrderAgentRepository orderAgentRepo

    @Autowired
    private InsuranceProductRepository insuranceProductRepo

    @Autowired
    private InsuranceFieldRepository insuranceFieldRepo

    @Autowired
    private InsuranceFieldTypeRepository insuranceFieldTypeRepo

    @Autowired
    private InsuranceProductFieldRepository insuranceProductFieldRepo

    @Autowired
    private InsuranceQuoteFieldRepository insuranceQuoteFieldRepo

    @Autowired
    private InsuranceQuoteRepository insuranceQuoteRepo

    @Autowired
    private InsurancePolicyRepository insurancePolicyRepo

    @Autowired
    private InsurancePersonRepository insurancePersonRepo

    @Autowired
    private InsuranceQuotePersonRepository insuranceQuotePersonRepo

    @Autowired
    private InsurancePolicyPersonRepository insurancePolicyPersonRepo

    @Autowired
    private InsuranceProductStatusRepository insuranceProductStatusRepo

    //</editor-fold>


    static final _AUDIT = _CURRENT_AUDIT

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

        //def area = areaRepo.findOne 110000L
        def orderType = orderTypeRepo.findFirstByName '保险'
        def orderStatus = orderStatusRepo.findOne 5L // 已完成
        def pChannel = OFFLINEBYCARD_6
        def paymentStatus = paymentStatusRepo.findFirstByStatus '支付成功'
        def poChannel =  channelRepo.findFirstByName 'UNKNOWN'
        def identityType = IDENTITYCARD
        def payType = PaymentType.Enum.INITIALPAYMENT_1 // 首次支付


        def insuranceFieldType = insuranceFieldTypeRepo.findFirstByType 'Boolean'
        def productStatus = insuranceProductStatusRepo.findFirstByName '未上架'

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
                    def payments = importFile(json, productStatus, insuranceFieldType, poChannel, orderType, orderStatus, pChannel, identityType, paymentStatus, payType,
                        reportFile, (index * 550), index, fileOrderNoMappings)
                    log.debug '插入总计 {}个payment记录，总耗时{}', payments.size(), (System.currentTimeMillis() - startTime) / 1000
                } catch (ex) {
                    log.error "File IDX: $index", ex
                }
            }
        }

//        def json = new JsonSlurper().with {
//            it.type = LAX
//            it
//        }.parse(dataFile as File)
//
//        importFile(json, productStatus, insuranceFieldType, poChannel, orderType, orderStatus, pChannel, identityType, paymentStatus, payType, reportFile)
//
//        log.debug '插入总计{}个payment记录，总耗时{}', payments.size(), (System.currentTimeMillis() - startTime) / 1000
//        throw new Exception()
    }

    @Transactional(rollbackFor = Exception)
    private importFile(json, productStatus, insuranceFieldType, poChannel, orderType, orderStatus, pChannel, identityType, paymentStatus, payType, reportFile, orderNoOffset, idx,
                       fileOrderNoMappings) {
        def orderNoMappings = [:]
        json.withIndex().collect { jsonPair, index ->
            def (qtJson, poJson) = jsonPair

          poChannel = poJson.sourceChannel?.name ? channelRepo.findFirstByName(poJson.sourceChannel.name) :  channelRepo.findFirstByName('UNKNOWN')

            def createTime = poJson.createTime instanceof Date ? poJson.createTime : _DATE_FORMAT_T.parse(poJson.createTime)
            // TODO 转过来从现象看需加8小时
            createTime.setHours(createTime.getHours() + 8)

            def ic = icRepo.findOne qtJson.insuranceCompany.id as Long
            def ownerIdentity = BusinessUtils.getRandomId()

            // 查询/保存保险产品
            def product = insuranceProductRepo.findFirstByName(qtJson.insuranceProduct.name)
            def insuranceField
            if (!product) {
                product = new InsuranceProduct(qtJson.insuranceProduct).with {
                    status = productStatus
                    insuranceCompany = ic
                    productType = InsuranceProductType.Enum.ALL.find { it.name == qtJson.insuranceProduct.productType.name }
                    insuranceProductRepo.save it
                }
                insuranceField = new InsuranceField().with {
                    code = product.name
                    name = product.name
                    fieldType = insuranceFieldType
                    shortName = product.name

                    insuranceFieldRepo.save it
                }

                new InsuranceProductField().with {
                    insuranceProduct = product
                    it.insuranceField = insuranceField

                    insuranceProductFieldRepo.save it
                }

                log.debug 'File IDX:{} IDX:{}，insuranceProduct id:{}', idx, index, product.id
            }

            def user = qtJson.user.id ? userRepo.findOne(qtJson.user.id as Long)
                : new User(mobile: qtJson.user.mobile).with {
                registerChannel = poChannel
                identity = ownerIdentity
                identityType = IDENTITYCARD
                audit = _AUDIT
                bound = true

                userRepo.save it
            }

            def premium = qtJson.premium


            def (startCreateTime, endCreateTime) = getStartAndEndDateTime(createTime)

            def poNo = getOrderNo orderNoMappings, startCreateTime, createTime, orderNoOffset, fileOrderNoMappings
            log.debug 'poNo:{}', poNo

            def purchaseOrder = new PurchaseOrder().with {
                applicant = user
                payableAmount = premium
                paidAmount = premium
                insuredIdNo = poJson.insuredIdNo

                audit = _AUDIT
                orderNo = poNo
                applicant = user
                type = orderType
                status = orderStatus
                channel = pChannel
                it.createTime = createTime
//                it.updateTime = updateTime(it.createTime)
                timePeriod = paidAmount > 5000 ? '10:00~12:00' : '14:00~16:00'
                def dateFormat3 = _DATE_FORMAT3
                def dateTimeFormat3 = _DATETIME_FORMAT3
                sendDate = dateFormat3.parse dateTimeFormat3.format(
                    getLocalDate(createTime).plusDays(paidAmount > 5000 ? 2 : 3))
                sourceChannel = poChannel

                poRepo.save it
            }

            def agent = agentRepo.findFirstByName poJson.agent?.name
            if (agent) {
                new OrderAgent(purchaseOrder: purchaseOrder, agent: agent).with {
                    orderAgentRepo.save it
                }
            }

            def insuranceQuote = new InsuranceQuote().with {
                insuranceProduct = product
                insuranceCompany = ic
                it.premium = premium
                it.user = user
                channel = poChannel
                it.createTime = createTime
                type = QuoteSource.Enum.API_4


                insuranceQuoteRepo.save it
            }

            def insurancePerson = new InsurancePerson().with {
                name = purchaseOrder.insuredName?.size() > 44 ? purchaseOrder.insuredName[0..43] : purchaseOrder.insuredName
                identity = purchaseOrder.insuredIdNo ?: ownerIdentity
                it.user = user
                it.createTime = createTime
                it.identityType = identityType
                identity = purchaseOrder.insuredIdNo ?: ownerIdentity
                it.name = poJson.insuredName

                insurancePersonRepo.save it
            }

            def insuranceQuotePerson = new InsuranceQuotePerson().with {
                insuranceProduct = product
                it.insuranceQuote = insuranceQuote
                it.insurancePerson = insurancePerson
                it.createTime = createTime

                insuranceQuotePersonRepo.save it
            }

            def (startDate, endDate) = getInsuranceEffectiveDate(createTime)
            def insurancePolicy = new InsurancePolicy(poJson.insurancePolicy).with {
                it.insuranceQuote = insuranceQuote
                it.purchaseOrder = purchaseOrder
                it.user = user
                it.insuranceCompany = ic
                it.premium = premium
                it.createTime = createTime
                effectiveDate = startDate
                expireDate = endDate

                insuredPerson = insurancePerson
                applicantPerson = insurancePerson

                insurancePolicyRepo.save it
            }

            def insurancePolicyPerson = new InsurancePolicyPerson().with {
                it.insurancePolicy = insurancePolicy
                it.insurancePerson = insurancePerson

                insurancePolicyPersonRepo.save it
            }

            // payment
            def payment = new Payment(
                amount: premium,
                purchaseOrder: purchaseOrder,
                status: paymentStatus,
                user: user,
                channel: pChannel,
                comments: pChannel.description,
                createTime: createTime,
                paymentType: payType
            ).with {
                paymentRepo.save it
            }
            log.debug 'File IDX:{} IDX:{}，payment id:{}', idx, index, payment.id

            // order operation info
            def ooi = new OrderOperationInfo(
                purchaseOrder: purchaseOrder
            ).with {
                Date date = purchaseOrder.getCreateTime().plus(1)
                it.setCreateTime(date)
                it.setUpdateTime(date)
                it.setCurrentStatus(OrderTransmissionStatus.Enum.PAID_AND_FINISH_ORDER)

                orderOperationInfoService.save it
            }
            log.debug 'File IDX:{} IDX:{}，orderOperationInfo id:{}', idx, index, ooi.id

            payment
        }.with { payments ->
            reportFile.withPrintWriter { writer ->
                payments.each { payment ->
                    writer.println payment.id
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
        def d= new Date(date.getTime())
        d.setMinutes(d.getMinutes() + RandomUtils.nextInt(100 ,24*60*3))
        d
    }
}
