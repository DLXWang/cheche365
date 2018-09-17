package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.AutoRepository
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
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.Constants._INSURANCE_KIND_AMOUNTS_PERSON
import static com.cheche365.cheche.misc.Constants._INSURANCE_KIND_AMOUNTS_SCRATCH
import static com.cheche365.cheche.misc.Constants._INSURANCE_KIND_AMOUNTS_THIRD_PARTY
import static com.cheche365.cheche.misc.util.BusinessUtils.getDateInSpecificRange
import static com.cheche365.cheche.misc.util.BusinessUtils.getQuoteRecord
import static com.cheche365.cheche.misc.util.BusinessUtils.getRandomEnrollDate
import static groovy.json.JsonOutput.toJson
import static java.lang.Math.round
import static java.time.ZoneId.systemDefault
import static java.time.ZonedDateTime.now
import static java.time.ZonedDateTime.ofInstant as date
import static java.time.temporal.ChronoUnit.YEARS
import static org.apache.commons.lang3.RandomUtils.nextDouble as randomDouble
import static org.apache.commons.lang3.RandomUtils.nextInt as randomInt


/**
 * 上年需要创建的续保数据（根据提供的上年的车和人）
 * -Pargs="   -rexp -uaf  D:/20170504/renewrenew/4sameic.csv  -rf D:/20170504/renewrenew/2017-04-rf.csv -df D:/20170504/renewrenew/2017-04-data.json -ctsd 2016-04-02 -cted 2016-04-30 "
 */
@Controller
@Slf4j
class RenewExporter implements CommandLineRunner {

    private static final _CLI = new CliBuilder().with {
        // @formatter:off
        rexp longOpt: 'fake-data-export',    '续保数据导出命令 <默认：false>', required: true
        uaf  longOpt: 'user-auto-list-file', '用户车列表文件（CSV格式）', args: 1, argName: 'user-auto-list-file'
        rf   longOpt: 'report-file',         '报告文件（CSV格式）', args: 1, argName: 'report-file', required: true
        df   longOpt: 'data-file',           '数据文件（JSON格式）', args: 1, argName: 'data-file', required: true
        ctsd longOpt: 'create-time-start-date', '创建时间起始日期（格式：yyyy-MM-dd）', args: 1, argName: 'create-time-start-date', required: true
        cted longOpt: 'create-time-end-date',   '创建时间终止日期（格式：yyyy-MM-dd）', args: 1, argName: 'create-time-end-date', required: true
        h    longOpt: 'help',                   '打印此使用信息'
        v    longOpt: 'version',                '打印版本'
        // @formatter:on

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
        compulsory      : true,
        autoTax         : true,
        damage          : true,
        damageIop       : true,
        thirdPartyAmount: { it() },
        thirdPartyIop   : true
    ]

    private static
    final _GENERATE_INSURANCE_PACKAGE_COMMON = { Auto auto, genRandom3rdPartyAmount, genRandomPersonAmount, genRandomScratchAmount, glassType, basePackage = _INSURANCE_PACKAGE_PROPS_BASE ->
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
                theft          : theft,
                theftIop       : theftIop,
                engine         : engine,
                engineIop      : engineIop,
                driverAmount   : driverAmount,
                driverIop      : driverIop,
                passengerAmount: passengerAmount,
                passengerIop   : passengerIop,
                scratchAmount  : scratchAmount,
                scratchIop     : scratchIop,
                spontaneousLoss: spontaneousLoss,
                glassType      : glazzType
            ]
        }
    }

    private static
    final _GENERATE_INSURANCE_PACKAGE_VALUABLE = { Auto auto, genRandom3rdPartyAmount, genRandomPersonAmount, genRandomScratchAmount, glassType, basePackage = _INSURANCE_PACKAGE_PROPS_BASE ->
        (basePackage + [
            thirdPartyAmount: genRandom3rdPartyAmount(),
            theft           : true,
            theftIop        : true,
            engine          : true,
            engineIop       : true,
            driverAmount    : genRandomPersonAmount(),
            driverIop       : true,
            passengerAmount : genRandomPersonAmount(),
            passengerIop    : true,
            glassType       : round(randomDouble(0, 1)) ? glassType : null
        ]).with { ip ->
            def spontaneousLoss = round(randomDouble(0, 1)) as boolean
            def autoAge = date(auto.enrollDate.toInstant(), systemDefault()).until now(), YEARS
            def scratchAmount = 3 <= autoAge ? round(randomDouble(0, 1)) ? genRandomScratchAmount() : 0.0 : 0.0
            def scratchIop = scratchAmount as boolean
            ip + [
                scratchAmount  : scratchAmount,
                scratchIop     : scratchIop,
                spontaneousLoss: spontaneousLoss,
            ]
        }
    }

    private static
    final _GENERATE_INSURANCE_PACKAGE_LUXURY = { Auto auto, genRandom3rdPartyAmount, genRandomPersonAmount, genRandomScratchAmount, glassType, basePackage = _INSURANCE_PACKAGE_PROPS_BASE ->
        (basePackage + [
            thirdPartyAmount: genRandom3rdPartyAmount(),
            theft           : true,
            theftIop        : true,
            engine          : true,
            engineIop       : true,
            driverAmount    : genRandomPersonAmount(),
            driverIop       : true,
            passengerAmount : genRandomPersonAmount(),
            passengerIop    : true,
            glassType       : glassType,
            spontaneousLoss : true
        ]).with { ip ->
            def autoAge = date(auto.enrollDate.toInstant(), systemDefault()).until now(), YEARS
            def scratchAmount = 3 <= autoAge ? round(randomDouble(0, 1)) ? genRandomScratchAmount() : 0.0 : 0.0
            def scratchIop = scratchAmount as boolean
            ip + [
                scratchAmount: scratchAmount,
                scratchIop   : scratchIop,
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
{ int dice -> 0 <= dice && dice <= 100 }: [
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

    @Autowired
    private AutoRepository autoRepo

    //</editor-fold>


    @Override
    void run(String... args) throws Exception {

        ''.tokenize()

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


        def glassType = glassTypeRepo.findFirstByName '国产'
        def qfType = qfTypeRepo.findFirstByName '通用流程'

        def reportFile = options.rf as File
        def dataFile = options.df as File
        def createTimeStartDate = getLocalDate(_DATE_FORMAT3.parse(options.ctsd))
        def createTimeEndDate = getLocalDate(_DATE_FORMAT3.parse(options.cted))
//        def poSourceChannel = channelRepo.findFirstByName options.posc
//        poSourceChannel.parent = null // !否则死循环

        def channelList = [
            Channel.Enum.WE_CHAT_3,
            Channel.Enum.IOS_4,
            Channel.Enum.ANDROID_6,
            Channel.Enum.WAP_8,
            Channel.Enum.WEB_5
        ]

        def userAutoList = options.uaf ? options.uaf as File : null
        def usersAutos = userAutoList?.readLines()?.collect { line ->
            line.tokenize ','
        }


        def header = usersAutos.remove(0)
        def count = header[1] as int
        def totalPremium = (header[2] - ',') as int
        def avgPremium = (totalPremium / count)

        def dataList = usersAutos.withIndex().collect {  userAuto, idx ->
            def userId = userAuto[2]
            def autoId = userAuto[1]

            def auto = autoRepo.findOne(autoId as Long)

            def area = auto.area
            if (!area) {
                areaRepo.findOne(110000L)
            }
            if (!auto.enrollDate) {
                auto.enrollDate = getRandomEnrollDate(auto.vinNo)
            }

            def poSourceChannel = RandomUtils.nextInt(1, channelList.size()).with {
                channelList[it]
            }
            poSourceChannel.parent = null


            def user = new User(id: userId as Long)
//            def auto = new Auto(id: autoId as Long, enrollDate: )

            def createDate = getDateInSpecificRange createTimeStartDate, createTimeEndDate, count, idx

            def qr  // quote record
            while (true) {
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
                // 商业险平局保费 = avgPremium - 1350
                if (qr.premium > (avgPremium -1350 - 500) && qr.premium < (avgPremium - 1350 + 500)) {
                    break
                }
            }
            def po = new PurchaseOrder(auto: auto, applicant: user, createTime: createDate, sourceChannel: poSourceChannel)
            log.debug 'index   IDX {}', idx
            [qr, po]
        }.with { qrPoPairs ->
            log.debug '至此已耗时{}，qr、po列表已构造完成，准备导出完整json和summary报表', (System.currentTimeMillis() - startTime) / 1000

            // 多退少补
            def sumPremium = qrPoPairs.sum {
                (it[0].premium as long) + (it[0].autoTax as long) + (it[0].compulsoryPremium as long)
            }
            def diff = totalPremium - sumPremium
            def avgDiff = diff / count

            qrPoPairs.each { qr, po ->  // 加在车损上
                qr.damagePremium = qr.damagePremium + avgDiff
                qr.premium = qr.premium + avgDiff
            }
            log.info '最终总价：{}', qrPoPairs.sum { (it[0].premium as long) + (it[0].autoTax as long) + (it[0].compulsoryPremium as long) }

            dataFile.withPrintWriter { writer ->
                writer.println toJson(qrPoPairs)
            }

            reportFile.withPrintWriter { writer ->
                qrPoPairs.each { QuoteRecord qr, PurchaseOrder po ->
                    log.debug '日期：{}，公司：{}，车主：{}，被保险人：{}，车牌号：{}，商业险：{}，交强险：{}，车船税：{}，总保费：{}，联系方式：{}', _DATE_FORMAT3.format(po.createTime), qr.insuranceCompany.name, po.auto.owner, po.insuredName, po.auto.licensePlateNo, qr.premium, qr.compulsoryPremium, qr.autoTax, qr.premium + qr.compulsoryPremium + qr.autoTax, po.auto.mobile
                    writer.println "${_DATE_FORMAT3.format(po.createTime)},${qr.insuranceCompany.name},${po.auto.owner},${po.insuredName},${po.auto.licensePlateNo},${qr.premium},${qr.compulsoryPremium},${qr.autoTax},${qr.premium + qr.compulsoryPremium + qr.autoTax},${po.auto.mobile}"
                }
            }
            def sum = qrPoPairs.collect { qr, _ ->
                qr.premium + qr.compulsoryPremium + qr.autoTax
            }.sum()
            log.debug '导出数据总额>>>>>>>>>>>>>>>>>>>>>>>>>>> {}', sum
            log.debug '导出数据平均单价 >>>>>>>>>>>>>>>>>>>>>>>> {}', sum / count
        }

        log.debug '总耗时{}', (System.currentTimeMillis() - startTime) / 1000
    }

}
