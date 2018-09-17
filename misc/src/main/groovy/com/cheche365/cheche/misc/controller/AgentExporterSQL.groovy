package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.Agent
import com.cheche365.cheche.core.model.AgentCompany
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.UserRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner

import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.util.BusinessUtils.checkAndAmendFile

@Slf4j
//@Controller
class AgentExporterSQL implements CommandLineRunner {

    private static final _CLI = new CliBuilder().with {
        // @formatter:off
        agtsql longOpt: 'fake-user-export',  '执行压力测试数据导出命令 <默认：false>', required: true
        sf     longOpt: 'agent-source-file', '用户列表文件（CSV格式）',  args: 1, argName: 'agent-source-file', required: true
        df     longOpt: 'agent-data-file',   '导出数据文件（JSON格式）', args: 1, argName: 'agent-data-file',   required: true
        rf     longOpt: 'agent-report-file', '导出数据报表（csv格式）',  args: 1, argName: 'report-file'
        h      longOpt: 'help',              '打印此使用信息'
        v      longOpt: 'version',           '打印版本'
        // @formatter:on

        usage = 'agtsql [options]'
        header = """$_APP_VERSION
Options:"""
        footer = """
Report bugs to: zhanghb@cheche365.com"""

        formatter.leftPadding = 4
        formatter.syntaxPrefix = 'Usage: '
        width = formatter.width = 200

        it
    }

    @Autowired
    private UserRepository userRepo

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

        def dataFile = options.df as File
        checkAndAmendFile dataFile

        def reportFile = options.rf
        if (reportFile) {
            reportFile = reportFile as File
            checkAndAmendFile dataFile
        }

        def sourceFile = options.sf as File
        sourceFile.readLines('GBK').collect { line ->
            line.tokenize(',')[0..3]
        }[1..-1].unique {
            [it[0], it[1], it[2]]
        }.collect { companyName, name, account, openingBank ->
            account = account.trim().replace(' ', '')
            def randomPhone = randomMobile
            def existedUser = userRepo.findByMobile(randomPhone)
            while (existedUser) {
                log.warn '手机号已存在{}', randomPhone
                randomPhone = randomMobile
                existedUser = userRepo.findByMobile(randomPhone)
            }

            def user = new User(name: name, mobile: randomPhone)
            def agentCompany = new AgentCompany(name: companyName)
            def agent = new Agent(name: name, mobile: randomPhone, agentCompany: agentCompany, cardNumber: account, openingBank: openingBank)

            [user, agent]
        }.with { userAgentPairs ->
            log.info '共{}个代理人', userAgentPairs.size()

//            dataFile.withPrintWriter { writer ->
//                writer.println toJson(userAgentPairs)
//            }

            // 改成SQL

            // println
            reportFile.withPrintWriter('GBK') { writer ->
                userAgentPairs.each { user, agent ->
                    log.info '创建代理人：{}，代理公司：{}', agent.name, agent.agentCompany?.name
                    writer.println "${agent.name},${agent.agentCompany?.name}"
                }
            }
        }

        log.debug '总耗时{}', (System.currentTimeMillis() - startTime) / 1000
    }

}


