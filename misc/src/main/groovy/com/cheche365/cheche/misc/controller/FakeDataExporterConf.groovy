package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.Address
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.GlassType
import com.cheche365.cheche.core.model.IdentityType
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteFlowType
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.UserType
import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.BusinessActivityRepository
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
import com.cheche365.cheche.core.service.AutoService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT5
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.core.model.Channel.Enum.ALIPAY_10
import static com.cheche365.cheche.core.model.Channel.Enum.UNKNOWN_9
import static com.cheche365.cheche.core.model.Channel.Enum.WE_CHAT_3
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.Constants._INSURANCE_KIND_AMOUNTS_PERSON
import static com.cheche365.cheche.misc.Constants._INSURANCE_KIND_AMOUNTS_SCRATCH
import static com.cheche365.cheche.misc.Constants._INSURANCE_KIND_AMOUNTS_THIRD_PARTY
import static com.cheche365.cheche.misc.util.BusinessUtils.getDateInSpecificRange
import static com.cheche365.cheche.misc.util.BusinessUtils.getQuoteRecord
import static com.cheche365.cheche.misc.util.BusinessUtils.getRandomEnrollDate
import static com.cheche365.cheche.misc.util.BusinessUtils.randomPid
import static groovy.json.JsonOutput.toJson
import static java.lang.Math.ceil
import static java.lang.Math.round
import static java.time.ZoneId.systemDefault
import static java.time.ZonedDateTime.now
import static java.time.ZonedDateTime.ofInstant as date
import static java.time.temporal.ChronoUnit.YEARS
import static org.apache.commons.lang3.RandomUtils.nextDouble as randomDouble
import static org.apache.commons.lang3.RandomUtils.nextInt
import static org.apache.commons.lang3.RandomUtils.nextInt as randomInt

/**
 * 根据已提供车辆创建 qr po
 * -Pargs="-fdexp -alf D:/201706/新车  -df D:/201706/ -ctsd 2017-05-11 -cted 2017-05-12  -n 500  -addrlf D:/201705/addr.csv -script  D:\201706\script.conf"
 *      [渠道，总数，总保费，车辆偏移，目录名称]
 *      [
 *       ['PARTNER_NCI',      151,   573909.0015,	 0,    'nic'],
 *       ['PARTNER_BXS',      56,    212066.9443,     151,  'bxs'],
 *       ['PARTNER_RRYP',     211,   800185.9563,     207,  'rryp'],
 *       ['UNKNOWN',          158,   599102.5589,     418,  'agent'],
 *       ['ALIPAY',           2,     8141.65016 ,     576,  'alipay'],
 *       ['PARTNER_BAIDU',    4,     15592.02814,     578,  'baidu' ],
 *       ['PARTNER_AUTOHOME', 7,     27881.3114,      582,  'autohome' ],
 *       ['PARTNER_TUHU',     14,    54572.09848,     589,  'tuhu' ],
 *       ['PARTNER_WEICHE',   41,    155305.8172,     603,  'weiche'  ],
 *       ['IOS',              34,    129306.3023,     644,  'ios'   ],
 *       ['ANDROID',          12,    45508.75207,     678,  'android'   ],
 *       ['WE_CHAT',          14,    53035.93807,     690,  'wechat'   ],
 *       ['WEB',              39,    149084,          704,  'web'  ],
 *       ['WAP',              7,     26613.97906,     743,  'wap'  ],
 *       ]
 */
@Controller
@Slf4j
class FakeDataExporterConf implements CommandLineRunner {

    private static final _CLI = new CliBuilder().with {
        // @formatter:off
        fdexp longOpt: 'fake-data-export',       'PO数据导出命令 <默认：false>', required: true
        ulf   longOpt: 'user-list-file',         '用户列表文件（CSV格式）', args: 1, argName: 'user-list-file'
        addrlf   longOpt: 'addr-list-file',       '地址列表文件（CSV格式）', args: 1, argName: 'addr-list-file'
        alf   longOpt: 'auto-list-file',         '车辆列表文件（CSV格式）', args: 1, argName: 'auto-list-file', required: true
//      rf    longOpt: 'report-file',            '报告文件（CSV格式）',     args: 1, argName: 'report-file', required: true
        df    longOpt: 'data-file',              '数据文件（JSON格式）/报表文件目录',    args: 1, argName: 'data-file', required: true
        ctsd  longOpt: 'create-time-start-date', '创建时间起始日期（格式：yyyy-MM-dd）', args: 1, argName: 'create-time-start-date', required: true
        cted  longOpt: 'create-time-end-date',   '创建时间终止日期（格式：yyyy-MM-dd）', args: 1, argName: 'create-time-end-date', required: true
        n     longOpt: 'count',                  '单个json文件的最大记录条数', args: 1, argName: 'n',  required: true

//      posc  longOpt: 'po-source-channel',      '订单渠道',                 args: 1, argName: 'po-source-channel'
//      ecul  longOpt: 'exporting-count-upper-limit', '导出数量上限',         args: 1, argName: 'exporting-count-upper-limit'
//      sp    longOpt: 'sum-premium',            '总保费',                   args: 1, argName: 'sum-premium'
//      alof  longOpt: 'auto-list-offset',       '车辆列表文件起始偏移位置', args: 1, argName: 'auto-list-pointer'

        conf  longOpt: 'conf',               '渠道配置文件', args: 1, argName: 'conf'

        h     longOpt: 'help',                   '打印此使用信息'
        v     longOpt: 'version',                '打印版本'
        // @formatter:off

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
    private AutoService autoService

    @Autowired
    private AgentRepository agentRepo


    @Autowired
    def BusinessActivityRepository businessActivityRepo

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

        // 代理人数据
        def agents0 = agentRepo.findDisabledAgents()

        def scriptConfig
        def scriptFile = options.conf
        if(scriptFile) {
            def script = new FileInputStream(scriptFile).text

            def gs = new GroovyShell()
            scriptConfig = gs.evaluate script
        }

        def dataFile0 = options.df as File
        if (!dataFile0) {
            dataFile0.mkdir()
        }

        def userListFile = options.ulf ? options.ulf as File : null
        def createTimeStartDate = getLocalDate(_DATE_FORMAT3.parse(options.ctsd))
        def createTimeEndDate = getLocalDate(_DATE_FORMAT3.parse(options.cted))

        def addrs = getAddrs(options.addrlf)
        def vlsInfo = getVlsInfo(options.alf)

        def n = options.n as int // 每个文件最多的条数

        if(scriptConfig) {
            scriptConfig.each { poSourceChannelName_, exportingCountUpperLimit_, totalPremium_, autoListPointer_, dirName ->
                def autoListPointer = autoListPointer_ as int           // 从0开始
                def exportingCountUpperLimit = exportingCountUpperLimit_ as long
                def poSourceChannelName = poSourceChannelName_
                def poSourceChannel = channelRepo.findFirstByName(poSourceChannelName) ?: UNKNOWN_9
                poSourceChannel.parent = null // !否则死循环 IdentityId

                def payChannel = WE_CHAT_3 == poSourceChannel ? PaymentChannel.Enum.WECHAT_4 :
                    ALIPAY_10 == poSourceChannel ? PaymentChannel.Enum.ALIPAY_1 :
                        PaymentChannel.Enum.OFFLINEBYCARD_6

                def totalPremium = totalPremium_ as double
                def currVlsInfo = vlsInfo[autoListPointer..-1]

                def dataFileDir = (dataFile0.path + File.separator + dirName) as File
                if (!dataFileDir.exists()) {
                    dataFileDir.mkdir()
                }
                def currDataFile = dataFileDir.path  + File.separator  + 'data.json'

                def reportFileDir =  (dataFileDir.path + File.separator + 'rf') as File
                if (!reportFileDir.exists()) {
                    reportFileDir.mkdir()
                }
                def currReportFile = reportFileDir.path  + File.separator + 'rf.csv'

                doExport(exportingCountUpperLimit, n, currDataFile, currReportFile, totalPremium, currVlsInfo, addrs, poSourceChannel, createTimeStartDate, createTimeEndDate, poSourceChannelName, agents0, payChannel)
            }
        }
    }


    private doExport(exportingCountUpperLimit, n, dataFile0, reportFile0, totalPremium, currVlsInfo, addrs, poSourceChannel, createTimeStartDate, createTimeEndDate, poSourceChannelName, agents0, payChannel) {
        def startTime = System.currentTimeMillis()
        ceil(exportingCountUpperLimit / n).times {
            def m = (it + 1) * n
            if (m > exportingCountUpperLimit) {
                m = exportingCountUpperLimit
            }
            def s = it * n
            def e = (m - 1)

            def dataFile = (dataFile0 + '_' + it) as File
            dataFile.createNewFile()
            def reportFile = (reportFile0 + '_' + it) as File
            reportFile.createNewFile()

            def theCount = (e - s + 1)
            def avgPremium = totalPremium / exportingCountUpperLimit // 包含交强险平均保费
            def theTotalPremium = avgPremium * theCount

            (currVlsInfo[s..e].withIndex().collect { vlInfo, idx ->
                // 车
                def (licensePlateNo, owner, vinNo, frameNo, engineNo) = vlInfo.collect {
                    it.trim()
                }
                vinNo = 17 == vinNo.length() ? vinNo : frameNo
                //地区
                def area = autoService.getAreaByLicensePlateNo licensePlateNo
                if (!area) {
                    log.warn '车牌号：{}未找到对应地区，被忽略！', licensePlateNo
                    return
                }

                // 地址
                def address
                def cityAddrs = addrs[area.id]
                if (cityAddrs) {
                    def randomAddr = cityAddrs[nextInt(0, cityAddrs.size())]
                    address = new Address().with {
                        it.street = randomAddr[0].replace('"', '')
                        it.city = randomAddr[1].replace('"', '')
                        it.district = randomAddr[2].replace('"', '')
                        it
                    }
                }

                // 人
                def user = new User(name: owner, userType: UserType.Enum.Customer)
//                if (usersInfo) {
//                    (user) = usersInfo[randomInt(0, usersInfo.size() - 1)]
//                }
                def randomPhone = randomMobile
                def existedUser = userRepo.findByMobile(randomPhone)
                while (existedUser) {
                    log.warn '手机号已存在{}', randomPhone
                    randomPhone = randomMobile
                    existedUser = userRepo.findByMobile(randomPhone)
                }
                user.mobile = randomPhone
                user.registerChannel = poSourceChannel

                def createDate = getDateInSpecificRange createTimeStartDate, createTimeEndDate, theCount, idx
                // 由于日期在反序列化时具有不稳定性!所以使用字符表示日期，存在po的description属性中
                def createDateStr = _DATE_FORMAT5.format(createDate)

                def auto = new Auto(
                    licensePlateNo: licensePlateNo,
                    vinNo: vinNo,
                    engineNo: engineNo,
                    enrollDate: getRandomEnrollDate(vinNo),
                    owner: owner,
                    identity: randomPid,
                    identityType: IdentityType.Enum.IDENTITYCARD,
                    area: area,
                    mobile: user.mobile,
                    autoType: new AutoType(seats: 5)
                )

                def qr  // quote record
                while (true) {
                    def dice = randomInt 0, 100
                    def (generateInsurancePackage, generateDamageAmount, generateThirdPartyAmount, generatePersonAmount, generateScratchAmount) = _FAKE_DATA_DECISION_TREE.findResult { hitCheck, decision ->
                        hitCheck(dice) ? decision : null
                    }

                    def ipProps = generateInsurancePackage auto, generateThirdPartyAmount, generatePersonAmount, generateScratchAmount, GlassType.Enum.DOMESTIC
                    def ip = new InsurancePackage(ipProps).with {
                        calculateUniqueString()
                        it
                    }
                    qr = getQuoteRecord ip, generateDamageAmount, auto, user, area, icRepo, QuoteFlowType.Enum.GENERAL
                    // 商业险平局保费 = avgPremium - 1350
                    if (qr.premium > (avgPremium - 1350 - 500) && qr.premium < (avgPremium - 1350 + 500)) {
                        break
                    }
                }

                // 代理人
                def allAgents  // 兼业代理渠道设为未知
                if ('UNKNOWN' == poSourceChannelName) {
                    allAgents = agents0
                }
                def agent
                if (!agent && allAgents) {
                    agent = allAgents[nextInt(0, allAgents.size() - 1)]
                }

                def po = new PurchaseOrder(auto: auto,
                    insuredName: owner,
                    applicant: user,
                    createTime: createDate,
                    sourceChannel: poSourceChannel,
                    agent: agent,
                    channel: payChannel,
                    deliveryAddress: address,
                    description: createDateStr
                ).with {
//                    if ('weiche' == poSourceChannelName) {
//                        it.orderSourceType = OrderSourceType.Enum.CPS_CHANNEL
//                        it.orderSourceId = businessActivityRepo.findFirstByCode('weiche-3')
//                    }

                    it
                }
                log.debug 'index   IDX {}', idx
                [qr, po]
            }.with { qrPoPairs ->
                log.debug '至此已耗时{}，qr、po列表已构造完成，准备导出完整json和summary报表', (System.currentTimeMillis() - startTime) / 1000
                // 多退少补
                def sumPremium = qrPoPairs.sum {
                    (it[0].premium as long) + (it[0].autoTax as long) + (it[0].compulsoryPremium as long)
                }
                def diff = theTotalPremium - sumPremium
                def avgDiff = diff / theCount

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
                        writer.println "${po.description},${qr.insuranceCompany.name},${po.auto.owner},${po.insuredName},${po.auto.licensePlateNo},${qr.premium},${qr.compulsoryPremium},${qr.autoTax},${qr.premium + qr.compulsoryPremium + qr.autoTax},${po.auto.mobile}"
                    }
                }
                def sum = qrPoPairs.collect { qr, _ ->
                    qr.premium + qr.compulsoryPremium + qr.autoTax
                }.sum()
                log.debug '导出数据总额>>>>>>>>>>>>>>>>>>>>>>>>>>> {}', sum
                log.debug '导出数据平均单价>>>>>>>>>>>>>>>>>>>>>>>> {}', sum / theCount
            })
            log.debug '总耗时{}', (System.currentTimeMillis() - startTime) / 1000

        }

    }

    // 根据提供的车辆所在的文件夹，将车辆合并并打散，并输出到文件中
    private static getVlsInfo(alf) {
        def autoListFile = alf as File
        if(autoListFile.isDirectory()) {
            def lines = autoListFile.listFiles().collect { f ->
                f.readLines()
            }.flatten()

            // 去重
            def autos = lines.collect {
                it.tokenize ','
            }.groupBy { no, _1, _2, _3, _4 ->
                no
            }.collect { no, ats ->
                ats[0]
            }

            // 打散
            Collections.shuffle(autos)

            // 输出到文件
            def w = new FileWriter(autoListFile.path + File.separator + '合并打散后的车型' as File)
            autos.each {
                w.println it.join(',')
                w.flush()
            }
            w.close()
            log.info '车辆总数>>>>>>>>>>>>>>>>>>>：{}', autos.size()

            autos
        } else {
            autoListFile.readLines().collect { line ->
                line.tokenize ','
            }.groupBy { licensePlateNo, _1, _2, _3, _4 ->
                licensePlateNo
            }.collect { _, groupedVlInfo ->
                groupedVlInfo.first()
            }
        }
    }

    // 从文件中获取文件列表
    private static getAddrs(addrlf) {
        def addrlFile = addrlf as File
        (addrlFile.readLines().collect { line ->
            line.tokenize ','
        }.groupBy { street, cityId , district->
            try {
                cityId.replace('"', '') as Long
            } catch (e) {
                log.warn '地址格式错误：{}', street
            }
        }-null)
    }
}
