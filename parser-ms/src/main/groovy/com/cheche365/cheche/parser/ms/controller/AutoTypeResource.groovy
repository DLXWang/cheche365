package com.cheche365.cheche.parser.ms.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.service.IThirdPartyAutoTypeService
import com.cheche365.cheche.parser.dto.AutoTypeRequestObject
import com.cheche365.cheche.parser.dto.AutoTypeResponseObject
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController



/**
 * 通用车型服务
 */
@RestController
@Slf4j
class AutoTypeResource {

    @Autowired(required = false)
    private IThirdPartyAutoTypeService service


    @PostMapping('/autoTypes')
    @ResponseBody
    def getAutoTypes(@RequestBody AutoTypeRequestObject body) {

        log.info '根据：{} 查询车型信息', body
        def vehicleLicense = body.vehicleLicense
        def additionalParams = body.additionalParameters.with { properties ->
            properties.area = new Area(properties.area)
            properties.insuranceCompany = new InsuranceCompany(properties.insuranceCompany)
            properties
        }

        try {
            service.getAutoTypes(vehicleLicense, additionalParams).with { autoTypes ->
                log.info '{}查询车型成功', autoTypes
                new AutoTypeResponseObject(
                    code: 0,
                    autoTypes: autoTypes
                )
            }
        } catch (e) {
            log.error '查询车型失败', e
            if (e instanceof BusinessException) {
                new AutoTypeResponseObject(
                    code: e.code.codeValue,
                    errorData: e.errorObject,
                    message: e.message
                )
            } else {
                new AutoTypeResponseObject(
                    code: -1,
                    message: e.message
                )
            }
        }
    }

}
