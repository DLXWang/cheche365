package com.cheche365.cheche.chinalife.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.AThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import groovyx.net.http.EncoderRegistry
import org.springframework.core.env.Environment

import static com.cheche365.cheche.chinalife.flow.Constants._AUTOTYPE_EXTRACTOR
import static com.cheche365.cheche.chinalife.flow.Constants._AUTO_INFO_EXTRACTOR
import static com.cheche365.cheche.chinalife.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.chinalife.util.BusinessUtils._CHINA_LIFE_GET_VEHICLE_OPTION
import static com.cheche365.cheche.chinalife.util.CityCodeMappings._CITY_CODE_MAPPINGS
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CHINALIFE_40000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.WEBPARSER_2
import static com.cheche365.cheche.parser.Constants._INSURANCE_DATE_EXTRACTOR



/**
 * 国寿财服务实现
 */
@TupleConstructor(
    includeSuperFields = true,
    includeFields = true
)
@Slf4j
class ChinalifeService extends AThirdPartyHandlerService {

    private IThirdPartyDecaptchaService decaptchaService

    ChinalifeService(Environment env) {
        this(env, null, null)
    }

    ChinalifeService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService) {
        super(env, insuranceCompanyChecker)
        this.decaptchaService = decaptchaService
    }

    @Override
    protected createContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {

        def area = quoteRecord.area
        def cityCodeMapping = getObjectByCityCode area, _CITY_CODE_MAPPINGS, true

        [
            client                              : new RESTClient(env.getProperty('chinalife.base_url')).with {
                encoderRegistry = new EncoderRegistry(charset: 'GBK')
                it
            },
            deptId                              : cityCodeMapping.deptId,
            parentId                            : cityCodeMapping.parentId,
            comCode                             : cityCodeMapping.comCode,
            autoInfoExtractor                   : _AUTO_INFO_EXTRACTOR,
            autoTypeExtractor                   : _AUTOTYPE_EXTRACTOR,
            insuranceDateExtractor              : _INSURANCE_DATE_EXTRACTOR,
            insuranceCompany                    : quoteRecord.insuranceCompany,
            cityRpgMappings                     : _CITY_RPG_MAPPINGS,
            cityRhMappings                      : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings             : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings            : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            supplementInfoMapping               : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                    : _CHINA_LIFE_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping   : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            decaptchaService                    : decaptchaService,
            decaptchaInputTopic                 : 'decaptcha-in-type02',

            //<editor-fold defaultstate="collapsed" desc="重要参数obtainCarModelV2XFlag">
            /**
             * 标识是否是费改，详见FindCarInfo
             *
             * 这个参数来源于国寿财的JS，北京是mod01_cl_01，南阳、大连等是mod01_bl_03
             * 其中北京的关键逻辑是如下代码：
             *
             * //========= start cx_1177  全国费改地区各渠道切换费改接口调整 wuyanshen =============================
             * if(temporary.carVerify.newCarFeeReformFlag=='1'){//如果是费改地区,第一次调用车型及纯风险保费信息查询接口
             *   temporary.quoteMain.geQuoteCars[0].obtainCarModelV2XFlag = '0';
             *   getClauseTypeVersion();//调用条款版本获取接口
             * }
             * //========= end cx_1177  全国费改地区各渠道切换费改接口调整 wuyanshen =============================
             *
             * 南阳、大连等是如下关键代码：
             * //============cx_1004  费改  柴新  2015-4-3======== start 费率改革  =============================
             * if(temporary.carVerify.newCarFeeReformFlag=='1'){//如果是费改地区,第一次调用车型及纯风险保费信息查询接口
             *   temporary.quoteMain.geQuoteCars[0].obtainCarModelV2XFlag = '1';
             *   getClauseTypeVersion();//调用条款版本获取接口
             *   temporary.quoteMain.geQuoteCars[0].RBCode = '';
             * }
             * //==========cx_1004  费改  柴新  2015-4-3========== end 费率改革  =============================
             *
             * //========= start cx_1177  全国费改地区各渠道切换费改接口调整 wuyanshen =============================
             * if(temporary.carVerify.newCarFeeReformFlag=='1'){//如果是费改地区
             *   if(temporary.carVerify.newCarSecondObtainCarModelInfoFlag=='1'){//(上海费改)第一次调用车型及纯风险保费
             *     temporary.quoteMain.geQuoteCars[0].obtainCarModelV2XFlag = '1';
             *     findCarModelInfo();
             *   }else{
             *     findCarModelInfo(); // 调用车型信息查询接口
             *   }
             * }else{
             *   findCarModelInfo(); // 调用车型信息查询接口
             * }
             * //========= end cx_1177  全国费改地区各渠道切换费改接口调整 wuyanshen =============================
             *
             * TODO 注意：尽管目前测试的城市几乎newCarFeeReformFlag的值都是1，所以我们无需判断，但是不一定所有城市都是1，所以后续如果某城市不为1的话，我们还要从响应中获取这个值来做判断
             */
            //</editor-fold>
            obtainCarModelV2XFlag               : cityCodeMapping.obtainCarModelV2XFlag
        ]

    }


    @Override
    boolean isSuitable(Map conditions) {
        CHINALIFE_40000 == conditions.insuranceCompany && (WEBPARSER_2 == conditions.quoteSource)
    }
}
