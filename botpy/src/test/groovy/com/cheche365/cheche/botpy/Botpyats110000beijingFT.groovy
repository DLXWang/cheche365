package com.cheche365.cheche.botpy

import com.cheche365.cheche.botpy.app.config.BotpyTestConfig
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.service.IThirdPartyAutoTypeService
import com.cheche365.cheche.test.parser.AParserServiceFT
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.test.parser.util.BusinessUtils.getAreaOfAuto
import static com.cheche365.cheche.test.parser.util.BusinessUtils.mergeTestDataParams
import static com.cheche365.cheche.test.util.ValidationUtils.verify



/**
 * 车型列表服务测试类
 */
@ContextConfiguration(
    classes = [BotpyTestConfig]
)
@Slf4j
class Botpyats110000beijingFT extends ABotpyFT {
    @Override
    protected final getAreaProperties() {
        [id: 110000L, name: '北京']
    }
}
