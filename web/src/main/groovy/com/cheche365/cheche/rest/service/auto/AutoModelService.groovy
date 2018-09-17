package com.cheche365.cheche.rest.service.auto

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.QuoteFlowConfig
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.service.IThirdPartyAutoTypeService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.service.http.SessionScopeLogger
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.Area.Enum.getValueByCode
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.service.SupplementInfoService.formatAutoModelSupplementInfo
import static com.cheche365.cheche.core.util.AutoUtils.getAreaOfAuto
import static com.cheche365.cheche.rest.QuoterFactory.QUOTE_ATTRIBUTE_REFER_TO_OTHER_AUTO_MODEL
import static com.cheche365.cheche.rest.QuoterFactory.QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH

@Service
@Slf4j
class AutoModelService extends ContextResource {

    @Autowired
    @Qualifier("piccAutoTypeService")
    private IThirdPartyAutoTypeService piccAutoTypeService;

    @Autowired
    private QuoteFlowConfigRepository configRepository

    @Autowired
    private SessionScopeLogger logger

    List getAutoModels(VehicleLicense vl, Long insuranceAreaId) {

        vl.licensePlateNo = vl.licensePlateNo ?: Auto.NEW_CAR_PLATE_NO
        Boolean newCarFlag = (Auto.NEW_CAR_PLATE_NO == vl.licensePlateNo)
        Area area = newCarFlag ? getValueByCode(insuranceAreaId) : getAreaOfAuto(vl.licensePlateNo)
//        if(allBotpyQuote(area)){
//            logger.debugVL("${area.name}全部使用金斗云报价，忽略查车型请求")
//            return null
//        }
        if (!area) {
            log.info("根据车牌号：{}，无法查到地区",vl.licensePlateNo)
            return null
        }

        def additionalParameters = [
            area                                       : area,
            newCarFlag                                 : newCarFlag,
            (QUOTE_ATTRIBUTE_REFER_TO_OTHER_AUTO_MODEL): true,
            (QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH): false
        ]

        List autoModels = piccAutoTypeService.getAutoTypes(vl, additionalParameters)
        logger.debugVL("车型列表返回数据转换前", CacheUtil.toJSONPretty(autoModels))
        formatAutoModelSupplementInfo(PICC_10000, autoModels, false)
        logger.debugVL("车型列表返回数据转换后", CacheUtil.toJSONPretty(autoModels))
        autoModels
    }

    boolean allBotpyQuote(Area area) {
        List<QuoteFlowConfig> configs = configRepository.findByAreaAndChannel(area, getChannel())
        configs && configs.every {it.configValue == QuoteSource.Enum.PLATFORM_BOTPY_11.id}
    }

}
