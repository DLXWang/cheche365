package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.Agent
import com.cheche365.cheche.core.model.AgentCompany
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.UserType
import com.cheche365.cheche.core.repository.AgentCompanyRepository
import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.core.repository.UserRepository
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.Constants._CURRENT_AUDIT
import static groovy.json.JsonParserType.LAX

/**
 * 导入代理人
 */
@Controller
@Transactional(rollbackFor = Exception)
@Slf4j
class AgentImpoter implements CommandLineRunner {

    private static final _CLI = new CliBuilder().with {
        // @formatter:off
        agtimp longOpt: 'fake-user-import', '代理人数据入命令 <默认：false>', required: true
        df     longOpt: 'agent-data-file',  '导出数据文件（JSON格式）', args: 1, argName: 'agent-data-file', required: true
        h      longOpt: 'help',             '打印此使用信息'
        v      longOpt: 'version',          '打印版本'
        // @formatter:on

        usage = 'agtimp [options]'
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

    @Autowired
    private AgentRepository agentRepo

    @Autowired
    private AgentCompanyRepository agentCompanyRepo

    private static _AUDIT = _CURRENT_AUDIT

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

        def agentUserType = UserType.Enum.Agent

        def dataFile = options.df as File
        def json = new JsonSlurper().with {
            it.type = LAX
            it
        }.parse(dataFile as File)

        def agents = json.withIndex().collect { userAgentPair, index ->
            def (jsonUser, jsonAgent) = userAgentPair
            log.debug 'user Idx {}', index
            def agentUser = new User([
                userType: agentUserType,
                name    : jsonUser.name,
                mobile  : jsonUser.mobile,
                bound   : true,
                audit   : _AUDIT,
            ]).with {
                userRepo.save it
            }

            def acJson = jsonAgent.agentCompany
            def ac = agentCompanyRepo.findFirstByName acJson.name
            if (!ac) {
                log.info '保存代理公司：{}', acJson.name
                ac = new AgentCompany(name: acJson.name).with { company ->
                    agentCompanyRepo.save company
                }
            }

            log.debug 'agent Idx {}', index
            new Agent([
                enable      : false,
                name        : jsonAgent.name,
                mobile      : jsonAgent.mobile,
                agentCompany: ac,
                cardNumber  : jsonAgent.cardNumber,
                bankAccount : jsonAgent.account,
                openingBank : jsonAgent.openingBank,
                user        : agentUser,
            ]).with {
                agentRepo.save it
            }
        }

        log.info '代理公司保存完成'
        log.debug '插入总计{}agent记录，总耗时{}', agents.size(), (System.currentTimeMillis() - startTime) / 1000
//        throw new Exception()
    }

}


