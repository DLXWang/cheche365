package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.FanhuaNonAuto
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.InsuranceField
import com.cheche365.cheche.core.model.InsurancePolicy
import com.cheche365.cheche.core.model.InsuranceProduct
import com.cheche365.cheche.core.model.InsuranceQuote
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.FanhuaNanAutoRepository
import com.cheche365.cheche.core.repository.GlassTypeRepository
import com.cheche365.cheche.core.repository.IdentityTypeRepository
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.core.repository.InsuranceFieldRepository
import com.cheche365.cheche.core.repository.InsuranceProductRepository
import com.cheche365.cheche.core.repository.InsuranceQuoteFieldRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuoteFlowTypeRepository
import com.cheche365.cheche.core.repository.QuoteSourceRepository
import com.cheche365.cheche.core.repository.UserAutoRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.repository.VehicleLicenseRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.misc.util.BusinessUtils
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller

import java.text.SimpleDateFormat

import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.core.model.InsuranceProductType.Enum.ALL
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.util.BusinessUtils.checkFileDirs
import static com.cheche365.cheche.misc.util.BusinessUtils.getInsuranceCompanyByFuzzyName
import static groovy.json.JsonOutput.toJson
import static java.lang.Math.ceil
import static org.apache.commons.lang3.RandomUtils.nextInt as randomInt

/**
 * -Pargs=" -natexp -df D:/20170504/nonauto/rryp/data.json -rf D:/20170504/nonauto/rryp/rf/rf.csv  -n 500    -posc PARTNER_RRYP"
 */
@Controller
@Slf4j
class ZNonAutoExporter implements CommandLineRunner {

    private static final _CLI = new CliBuilder().with {
        // @formatter:off
        natexp longOpt: 'fake-data-export', '执行压力测试数据导出命令 <默认：false>', required: true
        df     longOpt: 'data-file', '数据文件（JSON格式）',            args: 1, argName: 'data-file', required: true
        rf     longOpt: 'agent-report-file', '导出数据报表（csv格式）', args: 1, argName: 'report-file'
        n      longOpt: 'count', '单个json文件的最大记录条数',          args: 1, argName: 'n'
//      ecul longOpt: 'exporting-count-upper-limit', '导出数量上限', args: 1, argName: 'exporting-count-upper-limit', required: true
        posc   longOpt: 'po-source-channel', '订单渠道',             args: 1, argName: 'po-source-channel'
        h      longOpt: 'help', '打印此使用信息'
        v      longOpt: 'version', '打印版本'
        // @formatter:on

        usage = 'natexp [options]'
        header = """$_APP_VERSION
Options:"""
        footer = """
Report bugs to: zhanghb@cheche365.com"""

        formatter.leftPadding = 4
        formatter.syntaxPrefix = 'Usage: '
        width = formatter.width = 200

        it
    }

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
    private FanhuaNanAutoRepository fanhuaNanAutoRepo

    @Autowired
    private InsuranceProductRepository insuranceProductRepo

    @Autowired
    private InsuranceFieldRepository insuranceFieldRepo

    @Autowired
    private InsuranceQuoteFieldRepository insuranceQuoteFieldRepo

    @Autowired
    private AgentRepository agentRepo

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

        def dataFile0 = options.df as File
        checkFileDirs dataFile0
        def reportFile0 = options.rf
        if (reportFile0) {
            reportFile0 = reportFile0 as File
            checkFileDirs reportFile0
        }

        def n = options.n as int
//        def maxCount = options.ecul as int
        def poSourceChannel = options.posc ? channelRepo.findFirstByName(options.posc) : null
        if (poSourceChannel) {
            poSourceChannel.parent = null
        }
//
//        def orderList = fanhuaNanAutoRepo.findAllValidDataNonXuejia()
//        orderList = orderList.collect {
//            it.insuranceNo = null
//            it.orderDate = new Date(2017, 2, 1).plus(RandomUtils.nextInt(1, 31)) // 三月份
//            it
//        }
//        orderList = orderList
//
//
        def totalPremium = 452642

        def totalCount = 2795

//
//        orderList = orderList[0..totalCount - 1]

        def orderList = fanhuaNanAutoRepo.findAllValidDataNonXuejia()
        orderList = orderList.collect {
            it.insuranceNo = null
            if (it.insuranceKind == '学驾险') {
                it.insuranceKind = '意外险'
            }
//            it.premium = 180
            // TODO 日期
            it.orderDate = new SimpleDateFormat('yyyyMMdd').parse('20170401').plus(RandomUtils.nextInt(0, 29)) // 三月份 TODO
            it
        }
        orderList = orderList * 20
        Collections.shuffle(orderList)
        orderList = orderList[0..totalCount - 1]



        ceil(orderList.size() / n).times {
            def m = (it + 1) * n
            if (m > orderList.size()) {
                m = orderList.size()
            }
            def s = it * n
            def e = (m - 1)

            def theCount = (e - s + 1)
            def theTotalPremium = ((totalPremium / totalCount) * theCount)

            def dataFile = (dataFile0.absolutePath + '_' + it) as File
            dataFile.createNewFile()
            def reportFile = (reportFile0.absolutePath + '_' + it) as File
            reportFile.createNewFile()

            def allAgents = null //  agentRepo.findDisabledAgents()

            (orderList[s..e].withIndex().collect { FanhuaNonAuto nonAuto, idx ->

                def createDate = nonAuto.orderDate
                createDate.setHours randomInt(8, 21)
                createDate.setMinutes randomInt(0, 59)
                createDate.setSeconds randomInt(8, 59)

                def randomPhone = randomMobile
                def existedUser = userRepo.findByMobile(randomPhone)
                while (existedUser) {
                    log.warn '手机号已存在{}', randomPhone
                    randomPhone = randomMobile
                    existedUser = userRepo.findByMobile(randomPhone)
                }
                def user = new User(mobile: randomPhone)


                def insuranceKind = nonAuto.insuranceKind
                def insuranceNo = nonAuto.insuranceNo
                def premium = nonAuto.premium
                def icShortName = nonAuto.icShortName
                // 根据名字查询/创建保险产品
                def ic = getInsuranceCompanyByFuzzyName(icShortName) ?: InsuranceCompany.Enum.PICC
                def insuranceCompany = new InsuranceCompany(id: ic.id)
                def productType = insuranceKind.contains('寿险') ? ALL.find { it.name = '寿险' } :
                    insuranceKind.contains('意外') ? ALL.find { it.name = '意外险' } :
                        insuranceKind.contains('寿险') ? ALL.find { it.name = '寿险' } :
                            ALL.find { it.name = '财险' }

                def insuranceFields = new InsuranceField(code: insuranceKind, name: insuranceKind)
                def product = new InsuranceProduct(name: insuranceKind, productType: productType, insuranceFields: [insuranceFields])
//                def productField = new InsuranceProductField(insuranceField: insuranceField, insuranceProduct: product)

                // 代理人
                def agent = allAgents.find {
                    nonAuto.agent == it.name
                }
                if (!agent && allAgents) {
                    agent = allAgents[RandomUtils.nextInt(0, allAgents.size() - 1)]
                }

                def insuranceQuote = new InsuranceQuote(
                    insuranceProduct: product,
                    insuranceCompany: insuranceCompany,
                    user: user,
                    premium: premium
                )


                def insurancePolicy = new InsurancePolicy(policyNo: insuranceNo)
                // insurance quote field
                def po = new PurchaseOrder(
                    insuredName: nonAuto.customer,
                    insuredIdNo: BusinessUtils.randomId,
                    createTime: createDate,
                    applicant: user,
                    payableAmount: premium,
                    paidAmount: premium,
                    agent: agent,
                    insurancePolicy: insurancePolicy,
                    sourceChannel: poSourceChannel
                )



                log.debug 'index   IDX {}', idx
                [insuranceQuote, po]
            } - null).with { qrPoPairs ->
                log.debug '至此已耗时{}，qr、po列表已构造完成，准备导出完整json和summary报表', (System.currentTimeMillis() - startTime) / 1000

                // 多退少补
                def sumPremium = qrPoPairs.sum {
                    it[0].premium as long
                }
                def diff = theTotalPremium - sumPremium
                def avgDiff = diff / theCount

                qrPoPairs.each { iq, po ->
                    iq.premium = iq.premium + avgDiff
                    po.payableAmount = po.payableAmount + avgDiff
                    po.paidAmount = po.paidAmount + avgDiff
                }
                log.info 'avgDiff -------------------------- {}', avgDiff
                log.info '最终总价：{}', qrPoPairs.sum { (it[0].premium as long) }

                dataFile.withPrintWriter { writer ->
                    writer.println toJson(qrPoPairs)
                }

                reportFile.withPrintWriter('GBK') { writer ->
                    qrPoPairs.each { insuranceQuote, po ->
                        log.debug('date：{}，保险公司：{}，产品：{}，保费：{}',
                            po.createTime, insuranceQuote.insuranceCompany.id, insuranceQuote.insuranceProduct.name, po.paidAmount)
                        writer.println "$po.createTime,$insuranceQuote.insuranceCompany.id,$insuranceQuote.insuranceProduct.name,$po.paidAmount"
                    }
                }
            }
        }

        log.debug '总耗时{}', (System.currentTimeMillis() - startTime) / 1000
    }

}
