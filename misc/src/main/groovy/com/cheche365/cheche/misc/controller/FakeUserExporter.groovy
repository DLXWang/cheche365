package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.*
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.util.BusinessUtils.getDateInSpecificRange
import static groovy.json.JsonOutput.toJson
import static java.time.ZoneId.systemDefault
import static java.time.ZonedDateTime.ofInstant as date

@Controller
@Slf4j
class FakeUserExporter implements CommandLineRunner {

    private static final _CLI = new CliBuilder().with {
        fuexp   longOpt: 'fake-user-export','执行压力测试数据导出命令 <默认：false>', required: true
        ulf     longOpt: 'user-list-file',  '用户列表文件（CSV格式）',  args: 1, argName: 'user-list-file'
        alf     longOpt: 'auto-list-file',  '车辆列表文件（CSV格式）',  args: 1, argName: 'auto-list-file'
        rf      longOpt: 'report-file',     '报告文件（CSV格式）',     args: 1, argName: 'report-file',    required: true
        df      longOpt: 'data-file',       '数据文件（JSON格式）',    args: 1, argName: 'data-file',      required: true
        ctsd    longOpt: 'create-time-start-date',  '创建时间起始日期（格式：yyyy-MM-dd）',  args: 1, argName: 'create-time-start-date', required: true
        cted    longOpt: 'create-time-end-date',    '创建时间终止日期（格式：yyyy-MM-dd）',  args: 1, argName: 'create-time-end-date', required: true
        uc      longOpt: 'user-count',      '各渠道创建用户数量配置Map',  args: 1, argName: 'user-count', required: true
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

        def sourceCountText = options.uc
        def dataFile = options.df as File
        def reportFile = options.rf as File

        def createTimeStartDate = date(_DATE_FORMAT3.parse(options.ctsd).toInstant(), systemDefault()).toLocalDate()
        def createTimeEndDate = date(_DATE_FORMAT3.parse(options.cted).toInstant(), systemDefault()).toLocalDate()


        GroovyShell shell = new GroovyShell()
        def sourceCount = shell.evaluate(sourceCountText)
        sourceCount.collect { sourceCode, count ->
            def sourceUsers = []
            count.times { idx ->
                def createDate = getDateInSpecificRange createTimeStartDate, createTimeEndDate, count, idx

                def registerChannel = channelRepo.findFirstByName(sourceCode)
                if (!registerChannel) {
                    throw new Exception()
                }

                def randomPhone = randomMobile
                def existedUser = userRepo.findByMobile(randomPhone)
                while (existedUser) {
                    log.warn '手机号已存在{}', randomPhone
                    randomPhone = randomMobile
                    existedUser = userRepo.findByMobile(randomPhone)
                }
                sourceUsers << new User(mobile: randomPhone, registerChannel: registerChannel, createTime: createDate)
            }
            log.debug '渠道{}，user数量{}', sourceCode, sourceUsers.size()

            sourceUsers
        }.inject([]) { prev, item ->
            prev + item
        }.with { users ->

            log.debug '至此已耗时{}，user列表已构造完成，总数{}', (System.currentTimeMillis() - startTime) / 1000, users.size()

            dataFile.withPrintWriter { writer ->
                writer.println toJson(users)
            }

            reportFile.withPrintWriter { writer ->
                users.each{ user ->
                    writer.println "${user.mobile},${user.createTime},${user.registerChannel.name}"
                }
            }
        }

        log.debug '总耗时{}', (System.currentTimeMillis() - startTime) / 1000
    }

}
