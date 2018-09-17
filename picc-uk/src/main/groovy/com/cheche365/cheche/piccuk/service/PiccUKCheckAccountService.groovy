package com.cheche365.cheche.piccuk.service

import com.cheche365.cheche.common.flow.IFlow
import com.cheche365.cheche.core.service.FileBasedConfigService
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyCheckAccountService
import com.cheche365.cheche.parser.service.THttpClientGenerator
import com.cheche365.flow.core.service.TSimpleService
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import groovyx.net.http.EncoderRegistry
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.common.util.FlowUtils.getFlow
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.piccuk.flow.FlowMappings._CHECK_LOGIN_FLOW_MAPPINGS
import static java.nio.file.Paths.get as getPath
import static org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY



/**
 * 人保UK服务实现
 */
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Service('piccUKCheckAccountService')
@Slf4j
class PiccUKCheckAccountService implements IThirdPartyCheckAccountService, ISuitability<Map>, TSimpleService, THttpClientGenerator {


    private ConcurrentMap propertySources
    @Autowired
    private IConfigService configService
    @Autowired
    private Environment env

    PiccUKCheckAccountService(
        Environment env,
        ConcurrentMap propertySources,
        FileBasedConfigService configService) {
        this.configService = configService
        this.env = env
        this.propertySources = propertySources
    }


    Object createContexts(newEnv, prefixes, city) {

        [
            client          : getHttpClient((getEnvPropertyNew(newEnv, 'casserver_host', null, prefixes)), [suptPro: ['TLSv1'] as String[]]
            ).with {
                it.encoderRegistry = new EncoderRegistry(charset: 'GBK')
                if (getEnvPropertyNew(newEnv, 'http_proxy_username', null, prefixes)) {
                    it.client.getCredentialsProvider().setCredentials(
                        new AuthScope(
                            getEnvPropertyNew(newEnv, 'http_proxy_host', null, prefixes),
                            getEnvPropertyNew(newEnv, 'http_proxy_port', null, prefixes) as int),
                        new UsernamePasswordCredentials(
                            getEnvPropertyNew(newEnv, 'http_proxy_username', null, prefixes),
                            getEnvPropertyNew(newEnv, 'http_proxy_password', null, prefixes))
                    )
                }
                it.client.params.setParameter(
                    DEFAULT_PROXY,
                    new HttpHost(
                        getEnvPropertyNew(newEnv, 'http_proxy_host', null, prefixes),
                        getEnvPropertyNew(newEnv, 'http_proxy_port', null, prefixes) as int)
                )
                it
            },
            CheckLoginStatus: _CHECK_LOGIN_FLOW_MAPPINGS,
            username        : getEnvPropertyNew(newEnv, 'username', null, prefixes),
            password        : getEnvPropertyNew(newEnv, 'password', null, prefixes),
            city            : city,
            portal_host     : getEnvPropertyNew(newEnv, 'portal_host', null, prefixes)
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
            file.name.endsWith('.properties') && file.name.contains("picc")
        } as FileFilter).flatten().findAll(Closure.IDENTITY).collectEntries { file ->
            [(file.canonicalFile.absolutePath): createProperties(file)]
        } as ConcurrentHashMap
        this.propertySources = propertySources
        log.debug '---------------------propertySources:{}' + propertySources
    }


    List<Object> checkLoginStatus() {
        postInit()
        def keys = (this.propertySources.values())[0]
        //所有的城市编码
        def newEnv = [env: env, configService: configService, namespace: 'piccuk']
        def citys = new HashSet<>()
        def ma = keys.keySet() as String[]
        def insuranceCompany
        ma.each {
            it ->
                if (it.split('\\.').size() == 3) {
                    citys << it.split('\\.')[1]
                    insuranceCompany = it.split('\\.')[0]
                }
        }
        def msgList = []
        citys.each {
            it ->
                def prefixes = [8, insuranceCompany, it as Long].toArray() //[8,25000,110000]
                def context = createContexts(newEnv, prefixes, it as Long)
                def flow = getFlow context, 'CheckLoginStatus'
                def mass = service null, flow, context
                def channel
                if (!mass?.newCity) {
                    msgList << buildMsg(context, insuranceCompany, channel)
                    log.debug '人保登陆验证失败:{}', msgList
                }
        }
        msgList
    }


    private static buildMsg(Object context, Object insuranceCompany, Object channel) {
        [
            username        : context.username,
            password        : context.password,
            city            : context.city,   //所属地区
            channel         : channel,       //所属渠道
            insuranceCompany: insuranceCompany //所属保险公司
        ]
    }


    @Override
    def service(businessObjects, IFlow flow, context) {
        flow.run context
        context
    }

    @Override
    boolean isSuitable(Map conditions) {
        PICC_10000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)
    }

    private static createProperties(file) {
        new Properties().with { props ->
            file.withReader { reader ->
                props.load reader
            }
            props
        }
    }

    @Override
    List<Map> getFailedAccounts() {
        checkLoginStatus()
    }
}



