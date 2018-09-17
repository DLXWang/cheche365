package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.AddressRepository
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.AutoRepository
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.GlassTypeRepository
import com.cheche365.cheche.core.repository.IdentityTypeRepository
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.core.repository.InsurancePackageRepository
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
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static org.apache.commons.lang3.RandomUtils.nextInt as randomInt

@Controller
@Transactional(rollbackFor = Exception)
@Slf4j
class FakeDataUpdater implements CommandLineRunner {


    private static final _CLI = new CliBuilder().with {
        fdupd   longOpt: 'fake-data-import',    '执行压力测试数据导入命令 <默认：false>', required: true
        df      longOpt: 'data-file',           '数据文件（JSON格式）', args: 1, argName: 'data-file', required: true
        rf      longOpt: 'report-file',         '报告文件（CSV格式）',  args: 1, argName: 'report-file', required: true
        h       longOpt: 'help',                '打印此使用信息'
        v       longOpt: 'version',             '打印版本'

        usage = 'fdupd [options]'
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

        def dataFile = options.df as File
        def reportFile = options.rf as File

        def startTime = System.currentTimeMillis()

        def updateData = dataFile.readLines().collect { paymentId ->

            def payment = paymentRepo.findOne paymentId as Long
            def po = payment.purchaseOrder
            if (!20 == po.audit) {
                return
            }
            def qr = qrRepo.findOne po.objId
            def amount = 500

            qr.damagePremium += amount
            qr.premium += amount
            po.payableAmount += amount
            po.paidAmount += amount
            payment.amount += amount

            paymentRepo.save payment
            poRepo.save po
            qrRepo.save qr

            log.debug 'paymentId {}，amount+= {}', paymentId, amount

            [qr, po]
        }.with { qrPoPairs ->
            reportFile.withPrintWriter { writer ->
                qrPoPairs.each { QuoteRecord qr, PurchaseOrder po ->
                    log.debug '日期：{}，公司：{}，车主：{}，被保险人：{}，车牌号：{}，商业险：{}，交强险：{}，车船税：{}，总保费：{}，联系方式：{}', _DATE_FORMAT3.format(po.createTime), qr.insuranceCompany.name, po.auto.owner, po.auto.owner, po.auto.licensePlateNo, qr.premium, qr.compulsoryPremium, qr.autoTax, qr.premium + qr.compulsoryPremium + qr.autoTax, po.applicant.mobile
                    writer.println "${_DATE_FORMAT3.format(po.createTime)},${qr.insuranceCompany.name},${po.auto.owner},${po.auto.owner},${po.auto.licensePlateNo},${qr.premium},${qr.compulsoryPremium},${qr.autoTax},${qr.premium + qr.compulsoryPremium + qr.autoTax},${po.applicant.mobile}"
                }
            }
        }

        log.debug '更新{}条，总耗时{}', updateData.size(), (System.currentTimeMillis() - startTime) / 1000
//        throw new Exception()

    }

}
