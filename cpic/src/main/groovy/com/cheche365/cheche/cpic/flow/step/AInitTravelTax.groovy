package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.FlowUtils.getLoopForceContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static groovyx.net.http.ContentType.JSON



/**
 * 初始化交强险报价
 * Created by xushao on 2015/7/22.
 */
@Component
@Slf4j
abstract class AInitTravelTax implements IStep {

    abstract protected getApiPath()

    @Override
    run(context) {
        RESTClient client = context.client
        def bodyContent = [
            random : context.baseInfoResult.random,
            orderNo: context.orderNo,
            otherSource: '02',
        ]

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : apiPath,
            body              : bodyContent
        ]

        def result
        try {
            result = client.post args, { resp, json ->
                def taxRule = json.preRules.SpecialContextForTaxDTO
                context.travelTaxInfo = [
                    endDate               : json.endDate,
                    startDate             : json.startDate,
                    taxEndDate            : json.taxEndDate,
                    taxStartDate          : json.taxStartDate,
                    productType           : json.preRules.productType,
                    payCheckbox           : taxRule.payCheckbox,
                    payTaxStatus          : json.preRules.payTaxStatus,
                    taxType               : json.preRules.taxType,
                    deductionDueType      : json.preRules.deductionDueType,
                    deductionDueCode      : json.preRules.deductionDueCode,
                    registryNumber        : json.RegistryNumber,
                    deductionDueProportion: json.preRules.deductionDueProportion,
                    derateNo              : json.preRules.derateNo,
                    sex                   : getGenderByIdentity(context.auto.identity)
                ]
                context.travelTaxInfo.VehicleTax = json.VehicleTax
                //能源类型
                context.travelTaxInfo.fuelType = taxRule.fuelType.initValue
                //纳税车辆类型
                if (taxRule.taxVehicleType.showFlag == '1') {
                    // TODO：城市相关代码不应该出现在这里，必须予以解决
                    //宁波地区一口价页面默认轿车K33
                    if (context.area.id == 330200) {
                        context.travelTaxInfo.taxVehicleType = getTaxVehicleType_330200(json.preRules.taxVehicleTypes) ? 'K33' : json.preRules.taxVehicleTypes[0].kind
                    } else if ('3010100' == context.branchCode && context.vehicleInfo && !context.vehicleInfo.engineCapacity) {
                        context.vehicleInfo.engineCapacity = json.engineCapacity
                    } else {
                        context.travelTaxInfo.taxVehicleType = json.preRules.taxVehicleTypes[0].kind
                    }
                }
                // 税务登记单号
                context.registryNumber = json.RegistryNumber
                json
            }
        } catch (ex) {
            log.warn '初始化交强险异常：{}，尝试重试', ex.message
            return getLoopForceContinueFSRV (null, '初始化交强险异常')
        }
        log.info '初始化交强险结果 AInitTravelTax，{}', result

        getResponseResult result, context, this
    }

    def getTaxVehicleType_330200 (vehicleTypes) {
        vehicleTypes.name.any {
            it ==~ /.*轿车.*/ && it != '微型轿车'
        }
    }
}
