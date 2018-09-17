package com.cheche365.cheche.rest.service.vl

import com.cheche365.cheche.bihu.service.BihuInsuranceInfoService
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.exception.Constants
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.IdentityType
import com.cheche365.cheche.core.model.InsuranceBasicInfo
import com.cheche365.cheche.core.model.InsuranceInfo
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.repository.AutoRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.spi.IVehicleLicenseFinder
import com.cheche365.cheche.rest.processor.quote.QuoteProcessor
import com.cheche365.cheche.rest.service.vl.formatter.VLFormatterFactory
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.response.RestResponseEnvelope
import com.cheche365.cheche.web.service.http.SessionScopeLogger
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.exception.Constants.getEXCEPT_FIELDS
import static com.cheche365.cheche.core.exception.Constants.getFIELD_PATH_MAPPING
import static com.cheche365.cheche.core.model.VehicleLicense.createVLByAuto
import static com.cheche365.cheche.core.util.AutoUtils.decrypt
import static com.cheche365.cheche.core.util.AutoUtils.getAreaOfAuto
import static com.cheche365.cheche.core.util.AutoUtils.isAutoContainStarChars
import static com.cheche365.cheche.core.util.CacheUtil.toJSONPretty
import static java.lang.reflect.Modifier.isStatic

/**
 * Created by zhengwei on 30/11/2017.
 */

@Service
@Slf4j
class VehicleLicenseService extends ContextResource {

    @Autowired(required = false)
    public HttpServletRequest request
    @Autowired
    private AutoService autoService
    @Autowired
    private AutoRepository autoRepo
    @Autowired
    private ConcurrentVLFinder concurrentVLFinder
    @Autowired
    private BihuInsuranceInfoService biHuInsuranceInfoService
    @Autowired
    private VLFormatterFactory vlFormatterFactory

    List<IVehicleLicenseFinder> finders
    SessionScopeLogger logger

    VehicleLicenseService(List<IVehicleLicenseFinder> finders, SessionScopeLogger logger){
        this.finders = finders
        this.logger = logger
    }

    HttpEntity<RestResponseEnvelope> find(String licensePlateNo, String owner, String extraFields) {

        def (normalizedLicensePlateNo, normalizedOwner) = preCheck(licensePlateNo, owner)

        Map<String, InsuranceInfo> infos = concurrentVLFinder.find(normalizedLicensePlateNo, normalizedOwner)

        InsuranceInfo iInfo = mergeInsuranceInfo(infos)

        def additionalParameters = [
            owner      : owner,
            extraFields: extraFields
        ]

        return getResponseEntity(formatInsuranceInfo(iInfo, additionalParameters))
    }

    private InsuranceInfo mergeInsuranceInfo(Map<String, InsuranceInfo> insuranceInfoMap) {
        insuranceInfoMap.keySet().each { logger.debugVL("使用${it}命中行驶证") }

        List<InsuranceInfo> infos = insuranceInfoMap.values().findAll{it}
        VehicleLicense vehicleLicense = new VehicleLicense()

        VehicleLicense.PROPERTIES.findAll { !EXCEPT_FIELDS.contains(it.name) }.each {
            vehicleLicense."$it.name" = infos?.vehicleLicense?."$it.name"?.find{ value ->
            (value instanceof String) && !value.contains("*") || !(value instanceof String) && value}// 加*数据置空
        }

        InsuranceBasicInfo insuranceBasicInfo = new InsuranceBasicInfo()
        InsuranceBasicInfo.PROPERTIES.findAll { !EXCEPT_FIELDS.contains(it.name) }.each {
            if (it.readMethod.returnType == Date.class) {
                insuranceBasicInfo."$it.name" = infos?.insuranceBasicInfo?."$it.name"?.max()
            } else {
                insuranceBasicInfo."$it.name" = infos?.insuranceBasicInfo?."$it.name"?.find()
            }
        }

        insuranceBasicInfo = insuranceBasicInfo.class.declaredFields.findAll {
            !isStatic(it.modifiers) && !EXCEPT_FIELDS.contains(it.name)
        }.name.any { insuranceBasicInfo."$it" } ? insuranceBasicInfo : null

        new InsuranceInfo(vehicleLicense: vehicleLicense, insuranceBasicInfo: insuranceBasicInfo)
    }

    def findVLInternal(String licensePlateNo) {
        biHuInsuranceInfoService.getInsuranceInfo(getAreaOfAuto(licensePlateNo), new Auto(licensePlateNo: licensePlateNo), ["use1": true])
    }

    def formatNullInsuranceInfo(String owner, String extraFields) {
        def additionalParameters = [
            owner      : owner,
            extraFields: extraFields
        ]
        formatInsuranceInfo(new InsuranceInfo(), additionalParameters)
    }

    def formatInsuranceInfo(InsuranceInfo iInfo, Map additionalParameters) {
        def context = [
            channel   : getChannel(),
            iInfo     : iInfo,
            user      : safeGetCurrentUser(),
            apiVersion: apiVersion(),
            request   : request
        ]

        context << additionalParameters ?: [:]

        vlFormatterFactory.getVLFormatter(context).format(context)
    }

    def preCheck(String licensePlateNo, String owner){
        if(!licensePlateNo || licensePlateNo.length()<2){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, '车牌号校验失败')
        }
        if(!getAreaOfAuto(licensePlateNo)){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, '车牌号校验失败')
        }
        Boolean orderCenter = getChannel().isOrderCenterChannel()
        if (!safeGetCurrentUser() && !owner && !orderCenter) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, '请输入车主姓名')
        }

        def auto = new Auto(owner: owner, licensePlateNo: licensePlateNo)
        logger.clear()
        logger.debugVL('脱密前查询参数', toJSONPretty(auto))
        autoService.decryptAuto(
            auto,
            null,
            session.id
        )
        logger.debugVL('脱密后查询参数', toJSONPretty(auto))
        [auto.licensePlateNo.trim().toUpperCase(), auto.owner?.trim()]

    }

    List formatQuoteParam(original) {

        QuoteProcessor.loadSupplementInfo(original)

        InsuranceInfo iInfo = quoteQueryToIInfo(original)

        def additionalParameters = [
            owner          : iInfo.vehicleLicense.owner,
            insuranceAreaId: original.pref?.insuranceAreaId
        ]

        def formattedParams = formatInsuranceInfo(iInfo, additionalParameters).vehicleLicenseInfo

        Long companyId = (original.pref?.companyIds?.size() == 1) ? original.pref.companyIds[0] as Long : null
        if (!companyId) {
            log.debug('格式化补充信息参数，无保险公司， 过滤掉auto model')
            formattedParams = formattedParams.findAll { !it.key != 'autoModel' }
        }

        Boolean allProps = original.additionalParameters?.quote?.format?.fields
        allProps ? formattedParams : formattedParams.findAll { it.originalValue }
    }

    InsuranceInfo quoteQueryToIInfo(original) {
        Auto auto = original.auto
        if (isAutoContainStarChars(auto) && auto.id) {
            decrypt(autoRepo.findOne(auto.id), auto)
        }
        if (auto.identityType?.id) {
            auto.identityType = IdentityType.toIdentityType(auto.identityType.id)
        }
        InsuranceInfo iInfo = new InsuranceInfo(
            vehicleLicense: createVLByAuto(auto),
            insuranceBasicInfo: new InsuranceBasicInfo()
        )

        original.supplementInfo?.findAll {
            it.key && it.value
        }?.each {
            def formatter = FIELD_PATH_MAPPING[it.key]?.writeFormatter
            if (VehicleLicense.PROPERTIES.name.contains(it.key) && !iInfo.vehicleLicense[it.key]) {
                iInfo.vehicleLicense[it.key] = formatter ? formatter(it.value) : it.value
            }
            if (InsuranceBasicInfo.PROPERTIES.name.contains(it.key) && !iInfo.insuranceBasicInfo[it.key]) {
                iInfo.insuranceBasicInfo[it.key] = formatter ? formatter(it.value) : it.value
            }
        }
        iInfo
    }


}
