package com.cheche365.cheche.botpy.service

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BadQuoteParameterException
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IContext
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.parserapi.service.AThirdPartyAPIHandlerService
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.core.env.Environment

import static com.cheche365.cheche.botpy.flow.Constants._BOTPY_GET_VEHICLE_OPTION
import static com.cheche365.cheche.botpy.flow.Constants._BOTPY_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.botpy.flow.Constants._BOTPY_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.botpy.flow.Constants._CITY_RULES_MAPPINGS
import static com.cheche365.cheche.botpy.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.botpy.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.botpy.flow.Constants._BOTPY_AUTO_MODEL_SELECTION_OPTIONS
import static com.cheche365.cheche.botpy.flow.FlowMappings._FLOW_CATEGORY_INSURE_FLOW_MAPPINGS
import static com.cheche365.cheche.botpy.flow.FlowMappings._FLOW_CATEGORY_QUOTE_FLOW_MAPPINGS
import static com.cheche365.cheche.botpy.util.BusinessUtils.getCityCode
import static com.cheche365.cheche.botpy.flow.AutoModelMappings._AUTO_MODEL_MAPPINGS
import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_BOTPY_11
import static com.cheche365.flow.core.util.ServiceUtils.persistState
import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT



/**
 * 壁虎服务实现
 */
@Slf4j
class BotpyService extends AThirdPartyAPIHandlerService {

    private IConfigService configService
    private IContext globalContext
    private messageHandler
    private MoApplicationLogRepository logRepo


    BotpyService(
        Environment env,
        IConfigService configService,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IContext globalContext,
        messageHandler,
        MoApplicationLogRepository logRepo) {
        super(env, insuranceCompanyChecker)
        this.configService = configService
        this.globalContext = globalContext
        this.messageHandler = messageHandler
        this.logRepo = logRepo
    }

    @Override
    protected Object doCreateContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {

        def prefixes = [quoteRecord.channel.apiPartner?.code, quoteRecord.insuranceCompany.id, quoteRecord.area.id].toArray()
        log.info('金斗云报价的渠道={},保险公司={},地区={}', prefixes[0], prefixes[1], prefixes[2])
        def newEnv = [env: env, configService: configService, namespace: 'botpy']
        def base_url = MockUrlUtil.findBaseUrl(additionalParameters) ?: getEnvPropertyNew(newEnv, 'base_url', null, prefixes)
        def insuranceCompany = quoteRecord.insuranceCompany

        [
            client                           : new RESTClient(base_url).with {
                client.params.setParameter(CONNECTION_TIMEOUT, getEnvPropertyNew(newEnv, 'conn_timeout', null, prefixes) as Integer)
                client.params.setParameter(SO_TIMEOUT, getEnvPropertyNew(newEnv, 'so_timeout', null, prefixes) as Integer)
                it
            },
            appId                            : getEnvPropertyNew(newEnv, 'app_id', null, prefixes),
            appKey                           : getEnvPropertyNew(newEnv, 'app_key', null, prefixes),
            appVersion                       : getEnvPropertyNew(newEnv, 'app_version', null, prefixes),
            outerCode                        : getEnvPropertyNew(newEnv, 'code', null, prefixes),
            accountId                        : getEnvPropertyNew(newEnv, 'account_id', null, prefixes),
            samCode                          : getEnvPropertyNew(newEnv, 'sam_code', null, prefixes),
            icEngages                        : getEnvPropertyNew(newEnv, 'ic_engages', null, prefixes),
            callBackUrl                      : "${WebConstants.domainURL}${getEnvPropertyNew(newEnv, 'callback_api_path', null, prefixes)}".toString(),
            configService                    : configService,
            namespace                        : 'botpy',
            cityCodeMappings                 : getCityCode(quoteRecord.area),
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTE_FLOW_MAPPINGS,
            cityInsuringFlowMappings         : _FLOW_CATEGORY_INSURE_FLOW_MAPPINGS,
            cityRulesMappings                : _CITY_RULES_MAPPINGS,
            getVehicleOption                 : _BOTPY_GET_VEHICLE_OPTION,
            insuranceCompany                 : insuranceCompany,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            supplementInfoMapping            : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            globalContext                    : globalContext,
            messageHandler                   : messageHandler,
            logRepo                          : logRepo,
            loadPersistentState              : _BOTPY_LOAD_PERSISTENT_STATE,
            savePersistentState              : _BOTPY_SAVE_PERSISTENT_STATE,
            compulsoryAndAutoTaxAllowAlone   : true,
            channelCode                      : quoteRecord.channel.apiPartner?.code,
            qr                               : quoteRecord,
            quoteLimitChecker                : additionalParameters.quote_limit_checker,
            getAutoModelSelectionOptions     : _BOTPY_AUTO_MODEL_SELECTION_OPTIONS,
            autoModelMappings                : _AUTO_MODEL_MAPPINGS
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        PLATFORM_BOTPY_11 == conditions.quoteSource
    }

    @Override
    def handleException(context, businessObjects, ex) {
        if (ex instanceof BadQuoteParameterException && ex.code == BusinessException.Code.BAD_QUOTE_PARAMETER) {
            ex.errorObject.each { it ->
                def fieldList = (it.fieldPath as String).split("\\.") as List
                it.originalValue = fieldList.inject(context) { value, key -> value == null ? null : value[key] }
                it
            }
        }

        persistState context
        super.handleException context, businessObjects, ex
    }

}
