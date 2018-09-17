package com.cheche365.cheche.piccuk.tob.service

import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.parser.service.AThirdPartyHandlerService
import com.cheche365.cheche.parser.service.THttpClientGenerator
import geb.Browser
import geb.ConfigurationLoader
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import groovyx.net.http.EncoderRegistry
import org.apache.http.HttpHost
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.ie.InternetExplorerOptions
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICCUK_10500
import static com.cheche365.cheche.parser.Constants._INSURANCE_DATE_EXTRACTOR
import static com.cheche365.cheche.piccuk.tob.flow.FlowMappings._PAGE_FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.piccuk.tob.flow.FlowMappings._PAGE_FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.piccuk.tob.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.piccuk.tob.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY

/**
 * PICCUK-2b服务实现
 */
@Service
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Slf4j
class PiccUK2bService extends AThirdPartyHandlerService implements THttpClientGenerator {

    @Override
    protected createContext(QuoteRecord quoteRecord, additionalParameters) {

        System.setProperty('webdriver.ie.driver', 'E:/programx/iedriver/IEDriverServer.exe')

        Browser browser = new Browser(new ConfigurationLoader(null, System.properties, new GroovyClassLoader(getClass().classLoader)).getConf(null))
        InternetExplorerOptions options = new InternetExplorerOptions()

        browser.driver = new InternetExplorerDriver(options)
        browser.baseUrl = 'https://10.134.136.48:8888/casserver/login?service=http%3A%2F%2F10.134.136.48%3A80%2Fportal%2Findex.jsp'
        browser.driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)


        def area = quoteRecord.area
        def minContext = [env: env, area: quoteRecord.area]

        [
            client                  : getHttpClient(
                (getEnvProperty(minContext, 'piccuk.casserver_host')), [suptPro: ['TLSv1'] as String[]]
            ).with {
                it.encoderRegistry = new EncoderRegistry(charset: 'GBK')
                it.client.params.setParameter(
                    DEFAULT_PROXY,
                    new HttpHost(
                        getEnvProperty(minContext, 'piccuk.http_proxy_host'),
                        getEnvProperty(minContext, 'piccuk.http_proxy_port') as int)
                )
                it
            },
            insuranceDateExtractor  : _INSURANCE_DATE_EXTRACTOR,
            insuranceCompany        : quoteRecord.insuranceCompany,
            cityRpgMappings         : _CITY_RPG_MAPPINGS,
            cityRhMappings          : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings : _PAGE_FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings: _PAGE_FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            iopAlone                : true,
            browser                 : browser
        ]
    }

    @Override
    boolean isInsuringFlowEnabled() {
        false
    }

    @Override
    boolean isSuitable(Map conditions) {
        PICCUK_10500 == conditions.insuranceCompany
    }

}
