package com.cheche365.cheche.botpy.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.service.FileBasedConfigService
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyCheckAccountService
import com.cheche365.flow.core.service.TSimpleService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_BOTPY_11
import static groovyx.net.http.Method.GET
import static java.nio.file.Paths.get as getPath


/**
 * 金斗云账号监控
 */
@Service
@Slf4j
class BotpyCheckAccountService implements IThirdPartyCheckAccountService, ISuitability<Map>, TSimpleService {

    private ConcurrentMap propertySources
    @Autowired
    private IConfigService configService
    @Autowired
    private Environment env

    BotpyCheckAccountService(
        Environment env,
        IConfigService configService) {
        this.configService = configService
        this.env = env
    }

    void postInit() {
        def confDirs = this.env.getProperty('conf.paths', 'conf')
        log.info '查找并加载配置路径下所有的指定类型的配置文件：{}', confDirs
        def confDirPaths = confDirs.tokenize(',').collect { confDir ->
            getPath confDir
        }
        def confDirFiles = confDirPaths*.toFile()
        def propertySources = confDirFiles*.listFiles({ file ->
            file.name.endsWith('.properties') && file.name.contains('botpy')
        } as FileFilter).flatten().findAll(Closure.IDENTITY).collectEntries { file ->
            [(file.canonicalFile.absolutePath): createProperties(file)]
        } as ConcurrentHashMap
        this.propertySources = propertySources
    }

    def detect() {
        postInit()
        def prop = (this.propertySources.values())[0]
        def keys = prop.keySet() as String[]
        def context = createContext(prop)
        def error = []
        keys.each {
            try {
                if(it.endsWith('account_id')) {
                    def path = '/accounts/' + prop[it.toString()] + '/ic-engages'
                    def result = sendParamsAndReceive context, path, [:], GET, log
                    if(result.error) {
                        def key = it.split('\\.')
                        def keySize = key.size()
                        if(keySize == 3 || keySize == 4) {
                            error << buildError(key, result)
                        } else {
                            error <<
                                [
                                    city            : '',
                                    channel         : '',
                                    insuranceCompany: '',
                                    message : '系统检测到您的金斗云账号异常，配置项为：' + it + '，详细信息：' + result.error
                                ]
                        }
                    }
                }
            } catch (Exception e) {
                log.error("账号{}检测时出现异常{}", keys, e.getMessage())
            }
        }
        log.info "botpy account check response {}",error
        error
    }

    @Override
    boolean isSuitable(Map conditions) {
        PLATFORM_BOTPY_11 == conditions.quoteSource
    }

    private static createContext(prop) {
        def base_url = prop.base_url
        [
            client    : new RESTClient(base_url),
            appKey    : prop.app_key,
            appVersion: prop.app_version,
            appId     : prop.app_id,
        ]
    }

    private static createProperties(file) {
        new Properties().with { props ->
            file.withReader { reader ->
                props.load reader
            }
            props
        }
    }

    private static buildError(key, result) {
        def channel = key.size() == 3 ? '' : key[0]
        [
            city            : key[key.size() - 2],  //所属地区
            channel         : channel,              //所属渠道
            insuranceCompany: key[key.size() - 3],  //所属保险公司
            message         : result.error          //详细信息
        ]
    }

    @Override
    List<Map> getFailedAccounts() {
        detect()
    }
}
