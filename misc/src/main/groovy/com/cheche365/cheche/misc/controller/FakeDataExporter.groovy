package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.GlassTypeRepository
import com.cheche365.cheche.core.repository.IdentityTypeRepository
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuoteFlowTypeRepository
import com.cheche365.cheche.core.repository.QuoteSourceRepository
import com.cheche365.cheche.core.repository.UserAutoRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.repository.VehicleLicenseRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.Constants._INSURANCE_KIND_AMOUNTS_PERSON
import static com.cheche365.cheche.misc.Constants._INSURANCE_KIND_AMOUNTS_SCRATCH
import static com.cheche365.cheche.misc.Constants._INSURANCE_KIND_AMOUNTS_THIRD_PARTY
import static com.cheche365.cheche.misc.util.BusinessUtils.getDateInSpecificRange
import static com.cheche365.cheche.misc.util.BusinessUtils.getQuoteRecord
import static com.cheche365.cheche.misc.util.BusinessUtils.getRandomEnrollDate
import static com.cheche365.cheche.misc.util.BusinessUtils.randomPid
import static groovy.json.JsonOutput.toJson
import static java.lang.Math.min
import static java.lang.Math.round
import static java.time.ZoneId.systemDefault
import static java.time.ZonedDateTime.now
import static java.time.ZonedDateTime.ofInstant as date
import static java.time.temporal.ChronoUnit.YEARS
import static org.apache.commons.lang3.RandomUtils.nextDouble as randomDouble
import static org.apache.commons.lang3.RandomUtils.nextInt as randomInt

/**
 * 根据城市渠道两个维度导出po数据
 */
//@Controller
@Deprecated
@Slf4j
class FakeDataExporter implements CommandLineRunner {

    private static final _CLI = new CliBuilder().with {
        fdexp   longOpt: 'fake-data-export','执行压力测试数据导出命令 <默认：false>', required: true
        ulf     longOpt: 'user-list-file',  '用户列表文件（CSV格式）',  args: 1, argName: 'user-list-file'
        alf     longOpt: 'auto-list-file',  '车辆列表文件（CSV格式）',  args: 1, argName: 'auto-list-file', required: true
        alof    longOpt: 'auto-list-offset','车辆列表起始偏移位置',     args: 1, argName: 'auto-list-pointer', required: true
        rf      longOpt: 'report-file',     '报告文件（CSV格式）',     args: 1, argName: 'report-file',    required: true
        df      longOpt: 'data-file',       '数据文件（JSON格式）',    args: 1, argName: 'data-file',      required: true
        ctsd    longOpt: 'create-time-start-date',  '创建时间起始日期（格式：yyyy-MM-dd）',  args: 1, argName: 'create-time-start-date', required: true
        cted    longOpt: 'create-time-end-date',    '创建时间终止日期（格式：yyyy-MM-dd）',  args: 1, argName: 'create-time-end-date', required: true
        ecul    longOpt: 'exporting-count-upper-limit', '导出数量上限',  args: 1, argName: 'exporting-count-upper-limit', required: true
        posc    longOpt: 'po-source-channel', '订单渠道',  args: 1, argName: 'po-source-channel', required: true
        city    longOpt: 'city',            '城市',  args: 1, argName: 'city', required: true
        h       longOpt: 'help',            '打印此使用信息'
        v       longOpt: 'version',         '打印版本'

        usage = 'fdexp [options]'
        header = """$_APP_VERSION
Options:"""
        footer = """
Report bugs to: zhanghb@cheche365.com"""

        formatter.leftPadding = 4
        formatter.syntaxPrefix = 'Usage: '
        width = formatter.width = 200

        it
    }


    //<editor-fold defaultstate="collapsed" desc="随机保额生成器">

    private static final _AMOUNT_RANDOM_GENERATOR_BASE = { List amounts, begin, end ->
        def slices = amounts[begin..end]
        slices[randomInt(0, slices.size() - 1)]
    }

    private static final _AMOUNT_RANDOM_GENERATOR_THIRD_PARTY = _AMOUNT_RANDOM_GENERATOR_BASE.curry _INSURANCE_KIND_AMOUNTS_THIRD_PARTY

    private static final _AMOUNT_RANDOM_GENERATOR_PERSON = _AMOUNT_RANDOM_GENERATOR_BASE.curry _INSURANCE_KIND_AMOUNTS_PERSON

    private static final _AMOUNT_RANDOM_GENERATOR_SCRATCH = _AMOUNT_RANDOM_GENERATOR_BASE.curry _INSURANCE_KIND_AMOUNTS_SCRATCH

    private static final _AMOUNT_RANDOM_GENERATOR_DAMAGE_BASE = { begin, end ->
        randomDouble begin, end
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="分类的套餐生成器">

    private static final _INSURANCE_PACKAGE_PROPS_BASE = [
        compulsory: true,
        autoTax: true,
        damage: true,
        damageIop: true,
        thirdPartyAmount: { it() },
        thirdPartyIop: true
    ]

    private static final _GENERATE_INSURANCE_PACKAGE_COMMON = { Auto auto, genRandom3rdPartyAmount, genRandomPersonAmount, genRandomScratchAmount, glassType, basePackage = _INSURANCE_PACKAGE_PROPS_BASE ->
        (basePackage + [
            thirdPartyAmount: genRandom3rdPartyAmount(),
        ]).with { ip ->
            def theft = round(randomDouble(0, 1)) as boolean
            def theftIop = theft
            def spontaneousLoss = round(randomDouble(0, 1)) as boolean
            def engine = round(randomDouble(0, 1)) as boolean
            def engineIop = engine
            def driverAmount = round(randomDouble(0, 1)) ? genRandomPersonAmount() : 0.0
            def driverIop = driverAmount as boolean
            def passengerAmount = round(randomDouble(0, 1)) ? genRandomPersonAmount() : 0.0
            def passengerIop = passengerAmount as boolean
            def autoAge = date(auto.enrollDate.toInstant(), systemDefault()).until now(), YEARS
            def scratchAmount = 3 <= autoAge ? round(randomDouble(0, 1)) ? genRandomScratchAmount() : 0.0 : 0.0
            def scratchIop = scratchAmount as boolean
            def glazzType = round(randomDouble(0, 1)) ? glassType : null
            ip + [
                theft : theft,
                theftIop : theftIop,
                engine : engine,
                engineIop : engineIop,
                driverAmount : driverAmount,
                driverIop : driverIop,
                passengerAmount : passengerAmount,
                passengerIop : passengerIop,
                scratchAmount : scratchAmount,
                scratchIop : scratchIop,
                spontaneousLoss : spontaneousLoss,
                glassType : glazzType
            ]
        }
    }

    private static final _GENERATE_INSURANCE_PACKAGE_VALUABLE = { Auto auto, genRandom3rdPartyAmount, genRandomPersonAmount, genRandomScratchAmount, glassType, basePackage = _INSURANCE_PACKAGE_PROPS_BASE ->
        (basePackage + [
            thirdPartyAmount : genRandom3rdPartyAmount(),
            theft : true,
            theftIop : true,
            engine : true,
            engineIop : true,
            driverAmount : genRandomPersonAmount(),
            driverIop : true,
            passengerAmount : genRandomPersonAmount(),
            passengerIop : true,
            glassType : round(randomDouble(0, 1)) ? glassType : null
        ]).with { ip ->
            def spontaneousLoss = round(randomDouble(0, 1)) as boolean
            def autoAge = date(auto.enrollDate.toInstant(), systemDefault()).until now(), YEARS
            def scratchAmount = 3 <= autoAge ? round(randomDouble(0, 1)) ? genRandomScratchAmount() : 0.0 : 0.0
            def scratchIop = scratchAmount as boolean
            ip + [
                scratchAmount : scratchAmount,
                scratchIop : scratchIop,
                spontaneousLoss : spontaneousLoss,
            ]
        }
    }

    private static final _GENERATE_INSURANCE_PACKAGE_LUXURY = { Auto auto, genRandom3rdPartyAmount, genRandomPersonAmount, genRandomScratchAmount, glassType, basePackage = _INSURANCE_PACKAGE_PROPS_BASE ->
        (basePackage + [
            thirdPartyAmount : genRandom3rdPartyAmount(),
            theft : true,
            theftIop : true,
            engine : true,
            engineIop : true,
            driverAmount : genRandomPersonAmount(),
            driverIop : true,
            passengerAmount : genRandomPersonAmount(),
            passengerIop : true,
            glassType : glassType,
            spontaneousLoss : true
        ]).with { ip ->
            def autoAge = date(auto.enrollDate.toInstant(), systemDefault()).until now(), YEARS
            def scratchAmount = 3 <= autoAge ? round(randomDouble(0, 1)) ? genRandomScratchAmount() : 0.0 : 0.0
            def scratchIop = scratchAmount as boolean
            ip + [
                scratchAmount : scratchAmount,
                scratchIop : scratchIop,
            ]
        }
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="决策树">

    /**
     * 决策树，key用于判断骰子的分布范围，value是具体策略
     */
    private static final _FAKE_DATA_DECISION_TREE = [
//        { int dice -> 0 <= dice && dice < 40 }      : [
//            _GENERATE_INSURANCE_PACKAGE_COMMON,
//            _AMOUNT_RANDOM_GENERATOR_DAMAGE_BASE.curry(50_000, 150_000),
//            _AMOUNT_RANDOM_GENERATOR_THIRD_PARTY.curry(0, 2),
//            _AMOUNT_RANDOM_GENERATOR_PERSON.curry(0, 2),
//            _AMOUNT_RANDOM_GENERATOR_SCRATCH.curry(0, 1)
//        ],
        { int dice -> 0 <= dice && dice <= 100 }     : [
            _GENERATE_INSURANCE_PACKAGE_COMMON,
            _AMOUNT_RANDOM_GENERATOR_DAMAGE_BASE.curry(50_000, 250_000),
            _AMOUNT_RANDOM_GENERATOR_THIRD_PARTY.curry(1, 3),
            _AMOUNT_RANDOM_GENERATOR_PERSON.curry(1, 2),
            _AMOUNT_RANDOM_GENERATOR_SCRATCH.curry(0, 1)
        ],
//        { int dice -> 70 <= dice && dice < 90 }     : [
//            _GENERATE_INSURANCE_PACKAGE_VALUABLE,
//            _AMOUNT_RANDOM_GENERATOR_DAMAGE_BASE.curry(310_000, 600_000),
//            _AMOUNT_RANDOM_GENERATOR_THIRD_PARTY.curry(4, 6),
//            _AMOUNT_RANDOM_GENERATOR_PERSON.curry(2, 3),
//            _AMOUNT_RANDOM_GENERATOR_SCRATCH.curry(1, 2)
//        ],
//        { int dice -> 90 <= dice && dice <= 100 }   : [
//            _GENERATE_INSURANCE_PACKAGE_LUXURY,
//            _AMOUNT_RANDOM_GENERATOR_DAMAGE_BASE.curry(610_000, 1_000_000),
//            _AMOUNT_RANDOM_GENERATOR_THIRD_PARTY.curry(6, 7),
//            _AMOUNT_RANDOM_GENERATOR_PERSON.curry(2, 4),
//            _AMOUNT_RANDOM_GENERATOR_SCRATCH.curry(2, 3)
//        ]
    ]

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="注入的组件">

    @Autowired
    private UserRepository userRepo

    @Autowired
    private UserAutoRepository userAutoRepo

    @Autowired
    private AppointmentInsuranceRepository aiRepo

    @Autowired
    private PurchaseOrderRepository poRepo

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
    private QuoteFlowTypeRepository qfTypeRepo

    @Autowired
    private ChannelRepository channelRepo

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

        def startTime = System.currentTimeMillis()

        def area = areaRepo.findOne (options.city as Long)
        def idType = idTypeRepo.findFirstByName '身份证'
        def glassType = glassTypeRepo.findFirstByName '国产'
        def qfType = qfTypeRepo.findFirstByName '通用流程'

        def userListFile = options.ulf ? options.ulf as File : null
        def autoListFile = options.alf as File
        def autoListPointer = options.alof as int
        def reportFile = options.rf as File
        def dataFile = options.df as File
        def createTimeStartDate = getLocalDate(_DATE_FORMAT3.parse(options.ctsd))
        def createTimeEndDate = getLocalDate(_DATE_FORMAT3.parse(options.cted))
        def exportingCountUpperLimit = options.ecul as long
        def poSourceChannel = channelRepo.findFirstByName options.posc
        poSourceChannel.parent = null // !否则死循环

        def usersInfo = userListFile?.readLines()?.collect { line ->
            line.tokenize ','
        }?.collect { userId, _ ->
            def user = userRepo.findOne(userId as Long)
            [user]
        }

        def vlsInfo = autoListFile.readLines().collect { line ->
            line.tokenize ','
        }.groupBy { licensePlateNo, _1, _2, _3, _4 ->
            licensePlateNo
        }.collect { _, groupedVlInfo ->
            groupedVlInfo.first()
        }

        def exportingCount = min exportingCountUpperLimit, vlsInfo.size() - autoListPointer
        vlsInfo[autoListPointer..<autoListPointer + exportingCount].withIndex().collect { vlInfo, idx ->
            // 车
            def (licensePlateNo, owner, vinNo, frameNo, engineNo) = vlInfo.collect {
                it.trim()
            }
            vinNo = 17 == vinNo.length() ? vinNo : frameNo
            def createDate = getDateInSpecificRange createTimeStartDate, createTimeEndDate, exportingCount, idx

            // 人
            def user = new User(name: owner)
            if (usersInfo) {
                (user) = usersInfo[randomInt(0, usersInfo.size() - 1)]
            }
            def randomPhone = randomMobile
            def existedUser = userRepo.findByMobile(randomPhone)
            while (existedUser) {
                log.warn '手机号已存在{}',  randomPhone
                randomPhone = randomMobile
                existedUser = userRepo.findByMobile(randomPhone)
            }
            user.mobile = randomPhone
            user.registerChannel = poSourceChannel

            def auto = new Auto(
                licensePlateNo  : licensePlateNo,
                vinNo           : vinNo,
                engineNo        : engineNo,
                enrollDate      : getRandomEnrollDate(vinNo),
                owner           : owner,
                identity        : randomPid,
                identityType    : idType,
                area            : area,
                mobile          : user.mobile,
                autoType        : new AutoType(seats: 5)
            )

            def qr  // quote record
            while(true) {
                def dice = randomInt 0, 100
                def (generateInsurancePackage, generateDamageAmount, generateThirdPartyAmount, generatePersonAmount, generateScratchAmount) = _FAKE_DATA_DECISION_TREE.findResult { hitCheck, decision ->
                    hitCheck(dice) ? decision : null
                }

                def ipProps = generateInsurancePackage auto, generateThirdPartyAmount, generatePersonAmount, generateScratchAmount, glassType
                def ip = new InsurancePackage(ipProps).with {
                    calculateUniqueString()
                    it
                }
                qr = getQuoteRecord ip, generateDamageAmount, auto, user, area, icRepo, qfType
                if (qr.premium > 2560 && qr.premium < 3550) {  // 3060  调整保费
                    break
                }
            }
            def po = new PurchaseOrder(auto: auto, insuredName: owner, applicant: user, createTime: createDate, sourceChannel: poSourceChannel)
            log.debug 'index   IDX {}', idx
            [qr, po]
        }.with  { qrPoPairs ->
            log.debug '至此已耗时{}，qr、po列表已构造完成，准备导出完整json和summary报表', (System.currentTimeMillis() - startTime) / 1000

            dataFile.withPrintWriter { writer ->
                writer.println toJson(qrPoPairs)
            }

            reportFile.withPrintWriter { writer ->
                qrPoPairs.each { QuoteRecord qr, PurchaseOrder po ->
                    log.debug '日期：{}，公司：{}，车主：{}，被保险人：{}，车牌号：{}，商业险：{}，交强险：{}，车船税：{}，总保费：{}，联系方式：{}', _DATE_FORMAT3.format(po.createTime), qr.insuranceCompany.name, po.auto.owner, po.insuredName, po.auto.licensePlateNo, qr.premium, qr.compulsoryPremium, qr.autoTax,qr.premium + qr.compulsoryPremium + qr.autoTax, po.auto.mobile
                    writer.println "${_DATE_FORMAT3.format(po.createTime)},${qr.insuranceCompany.name},${po.auto.owner},${po.insuredName},${po.auto.licensePlateNo},${qr.premium},${qr.compulsoryPremium},${qr.autoTax},${qr.premium + qr.compulsoryPremium + qr.autoTax},${po.auto.mobile}"
                }
            }
            def sum1 = qrPoPairs.collect { qr, _ ->
                qr.premium + qr.compulsoryPremium
            }.sum()
            log.debug '导出数据总额（不含商业险）>>>>>>>>>>>>>>>>>>>>>>>>>>> {}', sum1
            log.debug '导出数据平均单价（不含商业险）>>>>>>>>>>>>>>>>>>>>>>>> {}', sum1/exportingCount
        }

        log.debug '总耗时{}', (System.currentTimeMillis() - startTime) / 1000
    }

}
