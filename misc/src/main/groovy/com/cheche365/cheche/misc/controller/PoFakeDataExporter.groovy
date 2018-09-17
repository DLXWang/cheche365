package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.common.Constants
import com.cheche365.cheche.core.model.Address
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.OrderSourceType
import com.cheche365.cheche.core.model.PurchaseOrder
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

import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.util.BusinessUtils.checkFileDirs
import static com.cheche365.cheche.misc.util.BusinessUtils.fakeQuoteInfoByPremium
import static com.cheche365.cheche.misc.util.BusinessUtils.getChannelByFuzzyName
import static com.cheche365.cheche.misc.util.BusinessUtils.getInsuranceCompanyByFuzzyName
import static com.cheche365.cheche.misc.util.BusinessUtils.getPayChannelByFuzzyName
import static com.cheche365.cheche.misc.util.BusinessUtils.getRandomEnrollDate
import static com.cheche365.cheche.misc.util.BusinessUtils.isCPS
import static com.cheche365.cheche.misc.util.BusinessUtils.randomEngineNo
import static com.cheche365.cheche.misc.util.BusinessUtils.randomVinNo
import static groovy.json.JsonOutput.toJson
import static java.lang.Math.ceil
import static org.apache.commons.lang3.RandomUtils.nextInt
import static org.apache.commons.lang3.RandomUtils.nextInt as randomInt

/**
 * 有订单数据的导出
 * -Pargs="-poexp -id pos  -rf D:/201705/pos/rf/rf.csv -df D:/201705/pos/data.json  -n 500 -addrlf D:/201705/addr.csv"
 */
@Controller
@Slf4j
class PoFakeDataExporter implements CommandLineRunner {

    private static final _CLI = new CliBuilder().with {
        // @formatter:off
        poexp longOpt: 'fake-data-export',  '有订单数据导出命令 <默认：false>', required: true
        addrlf longOpt: 'addr-list-file',   '地址列表文件（CSV格式）', args: 1, argName: 'addr-list-file'
        id    longOpt: 'fake-data-export',  '查出数据的id',             args: 1, required: true
        df    longOpt: 'data-file',         '数据文件（JSON格式）',      args: 1, argName: 'data-file',      required: true
        rf    longOpt: 'agent-report-file', '导出数据报表（csv格式）',    args: 1, argName: 'report-file'
        n     longOpt: 'count',             '单个json文件的最大记录条数', args: 1, argName: 'n'
        h     longOpt: 'help',              '打印此使用信息'
        v     longOpt: 'version',           '打印版本'
        // @formatter:on

        usage = 'posexp [options]'
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
    def UserRepository userRepo

    @Autowired
    def UserAutoRepository userAutoRepo

    @Autowired
    def AppointmentInsuranceRepository aiRepo

    @Autowired
    def PurchaseOrderRepository poRepo

    @Autowired
    def VehicleLicenseRepository vlRepo

    @Autowired
    def AreaRepository areaRepo

    @Autowired
    def InsuranceCompanyRepository icRepo

    @Autowired
    def GlassTypeRepository glassTypeRepo

    @Autowired
    def QuoteSourceRepository quoteSourceRepo

    @Autowired
    def IdentityTypeRepository idTypeRepo

    @Autowired
    def QuoteFlowTypeRepository qfTypeRepo

    @Autowired
    def ChannelRepository channelRepo

    @Autowired
    def AutoService autoService

    @Autowired
    def AgentRepository agentRepo

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

        if (!checkId(options.id)) {
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

        def addrlf = options.addrlf as File
        def addrs = (addrlf.readLines().collect { line ->
            line.tokenize ','
        }.groupBy { street, cityId , district->
            try {
                cityId.replace('"', '') as Long
            } catch (e) {
                println street
            }
        }-null)

        def n = options.n as int

        def orderList = orderList
        def allAgents = agentList

        ceil(orderList.size() / n).times {
            def m = (it + 1) * n
            if (m > orderList.size()) {
                m = orderList.size()
            }
            def s =  it * n
            def e =  (m - 1)

            def dataFile = (dataFile0.absolutePath + '_' + it) as File
            dataFile.createNewFile()
            def reportFile = (reportFile0.absolutePath + '_' + it) as File
            reportFile.createNewFile()

            (orderList[s..e].withIndex().collect { info, idx ->
                def (
                    createDate,
                    licensePlateNo,
                    ownerName,
                    premium,
                    compulsoryPremium,
                    autoTax,
                    insuranceCompanyName,
                    sourceChannelName,
                    payChannelName,
                    couponAmount,
                    policyNo,
                    compulsoryPolicyNo,
                    agentName
                ) = orderInfo.call(info)

                def userType = sourceChannelName?.contains('代理') ? UserType.Enum.Agent : UserType.Enum.Customer
                def user = randomUser(userType)

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
                    address =  new Address().with {
                        it.street = randomAddr[0].replace('"', '')
                        it.city = randomAddr[1].replace('"', '')
                        it.district = randomAddr[2].replace('"', '')
                        it
                    }
                }


                def auto = createAuto licensePlateNo, area, ownerName, user

                def ic = getInsuranceCompanyByFuzzyName(insuranceCompanyName) ?: InsuranceCompany.Enum.PICC

                def (ip, qr) =  fakeQuoteInfoByPremium(premium ?: 0, compulsoryPremium, autoTax)
                qr.with {
                    it.insuranceCompany = new InsuranceCompany(id : ic.id)
                    it.auto = auto
                    it.insurancePackage = ip
                    it.applicant = user
                    it.originalPolicyNo = policyNo
                    it.compulsoryOriginalPolicyNo = compulsoryPolicyNo
                    it
                }

                def poSourceChannel = getChannelByFuzzyName sourceChannelName
                poSourceChannel.setParent null
                def payChannel = getPayChannelByFuzzyName payChannelName
                def payableAmount = (premium ?: 0) + (autoTax ?: 0) + (compulsoryPremium ?: 0)
                def paidAmount = payableAmount - (couponAmount ?: 0)

                // 代理人
                def agent = allAgents?.find {
                    agentName == it.name
                }
                if (!agent && allAgents) {
                    agent = allAgents[nextInt(0, allAgents.size() - 1)]
                }

                //  CPS // TODO
                def orderSourceType = isCPS(sourceChannelName) ? OrderSourceType.Enum.CPS_CHANNEL : null
                def orderSourceId = ('微车' == sourceChannelName ? businessActivityRepo.findFirstByCode('weiche-3') : null)

                def po = new PurchaseOrder(
                    auto: auto,
                    insuredName: ownerName,
                    applicant: user,
                    createTime: createDate,
                    payableAmount: payableAmount, paidAmount: paidAmount,
                    sourceChannel: poSourceChannel, channel: payChannel,
                    agent: agent,
                    orderSourceType : orderSourceType,
                    orderSourceId: orderSourceId,
                    description: Constants.get_DATE_FORMAT5().format(createDate),
                    deliveryAddress : address,
                )
                log.debug '---- IDX {}', idx

                [qr, po]
            }- null) .with  { qrPoPairs ->
                log.debug '至此已耗时{}，qr、po列表已构造完成，准备导出完整json和summary报表', (System.currentTimeMillis() - startTime) / 1000

                dataFile.withPrintWriter { writer ->
                    writer.println toJson(qrPoPairs)
                }

                reportFile.withPrintWriter('GBK') { writer ->
                    qrPoPairs.each { qr, po ->
                        log.debug('date：{}，代理人名字：{}，保险公司：{}，车牌号：{}，被保人：{}，商业保费：{}，交强保费：{}，车船税：{}',
                            po.createTime, po.agent?.name, qr.insuranceCompany.name, qr.auto.licensePlateNo, qr.applicant.name, qr.premium, qr.compulsoryPremium, qr.autoTax)
                        writer.println "$po.description,${ po.agent?.name },$qr.insuranceCompany.id,$qr.auto.licensePlateNo,$po.insuredName,$qr.premium,$qr.compulsoryPremium,$qr.autoTax"
                    }
                }
            }
        }

        log.debug '总耗时{}', (System.currentTimeMillis() - startTime) / 1000
    }

    def getOrderList() {}

    def getAgentList() {}

    def getOrderInfo() {}

    def checkId(id) {}


    def randomTime(date) {
        date.setHours randomInt(8, 21)
        date.setMinutes randomInt(0, 59)
        date.setSeconds randomInt(8, 59)
        date
    }

    def randomUser(userType) {
        def randomPhone = randomMobile
        def existedUser = userRepo.findByMobile(randomPhone)
        while (existedUser) {
            log.warn '手机号已存在{}',  randomPhone
            randomPhone = randomMobile
            existedUser = userRepo.findByMobile(randomPhone)
        }
        new User(mobile: randomPhone, userType: userType)
    }

    def createAuto(licensePlateNo, area, ownerName, user) {
        def vinNo = randomVinNo
        new Auto(
            licensePlateNo  : licensePlateNo,
            vinNo           : vinNo,
            engineNo        : randomEngineNo,
            enrollDate      : getRandomEnrollDate(vinNo),
            owner           : ownerName,
            area            : area,
            mobile          : user.mobile,
            autoType        : new AutoType(seats: 5)
        )
    }

}
