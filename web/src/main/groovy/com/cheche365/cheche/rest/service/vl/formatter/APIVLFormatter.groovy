package com.cheche365.cheche.rest.service.vl.formatter

import com.cheche365.cheche.core.exception.BusinessException
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

import static com.cheche365.cheche.core.exception.BusinessException.Code.UNAUTHORIZED_ACCESS
import static com.cheche365.cheche.core.serializer.SerializerUtil.toMapExceptClass

/**
 * Created by liheng on 2018/8/24 0024.
 */
@Service
@Order(1)
@Slf4j
class APIVLFormatter extends VLFormatter {

    @Override
    def support(context) {
        context.channel.partnerAPIChannel
    }

    @Override
    def needFillAutoModels(context) {
        false
    }

    def needFilterPublicAuto(context) {
        false
    }

    def needEncryptVL(context) {
        false
    }

    @Override
    def insuranceInfoToMap(context) {
        def vehicleLicense = context.iInfo?.vehicleLicense
        if (vehicleLicense) {
            toMapExceptClass(vehicleLicense).subMap(['licensePlateNo', 'owner', 'vinNo', 'engineNo', 'enrollDate', 'brandCode', 'useCharacter', 'fuelType'] + ('baoxian' == context.channel.apiPartner?.code ? ['newPrice', 'seats'] : [])).with {
                it.enrollDate = it.enrollDate ? new SimpleDateFormat('yyyy-MM-dd').format(it.enrollDate) : it.enrollDate
                it
            }
        } else {
            throw new BusinessException(UNAUTHORIZED_ACCESS, '未查到车辆信息！')
        }
    }
}
