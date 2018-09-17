package com.cheche365.cheche.ccint.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.ccint.util.BusinessUtils.saveApplicationLog
import static com.cheche365.cheche.ccint.util.BusinessUtils.sendAndReceive
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovy.json.StringEscapeUtils.unescapeJava

/**
 * 识别行驶证信息
 * 有API调用次数的限制
 */
@Component
@Slf4j
class RecognizeVehicleLicense implements IStep {

    private static final _API_PATH_RECOGNIZE_VEHICLE_LICENSE = '/icr/recognize_vehicle_license_s'

    @Override
    run(context) {

        def imageFile = context.imageFile

        saveApplicationLog context, imageFile.toURI(), this.class.simpleName

        def result = sendAndReceive context, _API_PATH_RECOGNIZE_VEHICLE_LICENSE, imageFile

        saveApplicationLog context, result ? unescapeJava(new JsonBuilder(result).toString()) : '调用合合行驶证识别服务失败', this.class.simpleName

        log.debug '返回的查询结果：{}', result

        /**
         * 正确响应JSON：
         *{
         *  "address": "浙江省宁波市镇海区繁川街道临食北路2094",
         *  "issue_date": "2015-09-28",
         *  "type": "中国行驶证正本",
         *  "vehicle_license_main_engine_no": "FB114025",
         *  "vehicle_license_main_model": "北京现代牌BH7160QAY",
         *  "vehicle_license_main_owner": "侯兴华",
         *  "vehicle_license_main_plate_num": "浙B957FZ",
         *  "vehicle_license_main_register_date": "2015-09-28",
         *  "vehicle_license_main_user_character": "非营运",
         *  "vehicle_license_main_vehicle_type": "小型轿车",
         *  "vehicle_license_main_vin": "LBEGCBFC3FX095329"
         * }
         * 错误响应：null
         */
        if (result) {
            context.vehicleLicense = result
            getContinueFSRV result
        } else {
            log.error '行驶证识别失败：{}',imageFile.toURI()
            getFatalErrorFSRV null
        }
    }

}
