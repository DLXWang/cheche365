package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.User
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
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static groovy.json.JsonParserType.LAX

@Controller
@Transactional(rollbackFor = Exception)
@Slf4j
class FakeUserImporter implements CommandLineRunner {


    private static final _CLI = new CliBuilder().with {
        fuimp longOpt: 'fake-data-import', '执行压力测试数据导入命令 <默认：false>', required: true
        df    longOpt: 'data-file',        '数据文件（JSON格式）', args: 1, argName: 'data-file',   required: true
        rf    longOpt: 'report-file',      '报告文件（CSV格式）',  args: 1, argName: 'report-file', required: true
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

        def startTime = System.currentTimeMillis()


        def json = new JsonSlurper().with {
            it.type = LAX
            it
        }.parse(dataFile as File)

        def users = json.withIndex().collect { jsonUser, index ->
            log.debug 'userIdx {}', index

            new User().with {
                mobile = jsonUser.mobile
                createTime = (jsonUser.createTime instanceof Date) ? jsonUser.createTime : _DATE_FORMAT3.parse(jsonUser.createTime.tokenize('T')[0])
                registerChannel = channelRepo.findFirstByName jsonUser.registerChannel.name
                bound = true
                audit = 20

                userRepo.save it
            }

        }.with { users ->
            reportFile.withPrintWriter { writer ->
                users.each { user ->
                    writer.println user.id
                }
            }
        }

        log.debug '插入总计{}user记录，总耗时{}', users.size(), (System.currentTimeMillis() - startTime) / 1000
        throw new Exception()
    }

}
