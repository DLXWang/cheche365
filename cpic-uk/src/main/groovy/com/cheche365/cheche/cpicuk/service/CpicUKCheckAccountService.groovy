package com.cheche365.cheche.cpicuk.service

import com.cheche365.cheche.common.flow.IFlow
import com.cheche365.cheche.core.service.FileBasedConfigService
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyCheckAccountService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.flow.core.service.TSimpleService
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.common.util.FlowUtils.getFlow
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.cpicuk.flow.FlowMappings._CHECK_LOGIN_FLOW_MAPPINGS
import static java.nio.file.Paths.get as getPath



/**
 * 太平洋UK服务实现
 */
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Service('cpicukCheckAccountService')
@Slf4j
class CpicUKCheckAccountService implements IThirdPartyCheckAccountService, ISuitability<Map>, TSimpleService {

    @Autowired
    private IThirdPartyDecaptchaService decaptchaService

    private ConcurrentMap propertySources
    @Autowired
    private IConfigService configService
    @Autowired
    private Environment env
    @Autowired
    private IInsuranceCompanyChecker insuranceCompanyChecker
//TODO  重构   抽出公共接口
    CpicUKCheckAccountService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService,
        ConcurrentMap propertySources,
        FileBasedConfigService configService) {
        this.decaptchaService = decaptchaService
        this.configService = configService
        this.env = env
        this.insuranceCompanyChecker = insuranceCompanyChecker
        this.propertySources = propertySources
    }


    Object createContexts(base_url, newEnv, prefixes, city) {
        [
            client             : new RESTClient(base_url),
            decaptchaInputTopic: 'decaptcha-in-type07',
            decaptchaService   : decaptchaService,
            CheckLoginStatus   : _CHECK_LOGIN_FLOW_MAPPINGS,
            username           : getEnvPropertyNew(newEnv, 'username', null, prefixes),
            password           : getEnvPropertyNew(newEnv, 'password', null, prefixes),
            realPassword       : getEnvPropertyNew(newEnv, 'realPassword', null, prefixes),
            city               : city
        ]
    }

    void postInit() {
        def confDirs = this.env.getProperty('conf.paths', 'conf')
        log.info '查找并加载配置路径下所有的指定类型的配置文件：{}', confDirs
        def confDirPaths = confDirs.tokenize(',').collect { confDir ->
            getPath confDir
        }
        def confDirFiles = confDirPaths*.toFile()
        def propertySources = confDirFiles*.listFiles({ file ->
            file.name.endsWith('.properties') && file.name.contains("cpic")
        } as FileFilter).flatten().findAll(Closure.IDENTITY).collectEntries { file ->
            [(file.canonicalFile.absolutePath): createProperties(file)]
        } as ConcurrentHashMap
        this.propertySources = propertySources
        log.debug '---------------------propertySources:{}'+ propertySources
    }


    List <Object> checkLoginStatus() {
        postInit()
        def keys = (this.propertySources.values())[0]
        //所有的城市编码
        def newEnv = [env: env, configService: configService, namespace: 'cpicuk']
        def base_url = keys.base_url
        def prifix = []
        def AllKeySet = keys.keySet() as String[]
        def insuranceCompany
        AllKeySet.each{
            it ->
                def arry = it.split('\\.').reverse()
                if (arry[0] == 'username'){
                    if (arry.size()== 3){
                        prifix << [null, arry[2], arry[1] as Long ]
                    } else if (arry.size()== 4) {
                        prifix << [arry[3], arry[2], arry[1] as Long ]
                    }
                }
        }
        def msgList = []
        prifix.each {
            it ->
                def context = createContexts(base_url, newEnv, it.toArray(), it[2])
                def flow = getFlow context, 'CheckLoginStatus'
                def mass = service null, flow, context
                if (!mass?.newCity) {
                    msgList << buildMsg(context, it[1],it[0])
                    log.debug '太平洋登陆验证失败:{}', msgList
                }
        }
        msgList
    }


    private static buildMsg(Object context, Object insuranceCompany, channel) {
        [
            username:context.username,
            password: context.realPassword,
            city: context.city,   //所属地区
            channel:channel,       //所属渠道
            insuranceCompany:insuranceCompany //所属保险公司
        ]
    }

    @Override
    List<Map> getFailedAccounts() {
        def msgList = checkLoginStatus()
    }

    @Override
    def service(businessObjects, IFlow flow, context) {
        flow.run context
        context
    }

    @Override
    boolean isSuitable(Map conditions) {
        CPIC_25000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)
    }

    private static createProperties(file) {
        new Properties().with { props ->
            file.withReader { reader ->
                props.load reader
            }
            props
        }
    }


}



