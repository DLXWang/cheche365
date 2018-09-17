package com.cheche365.cheche.parser.insuranceinfo

import com.cheche365.cheche.bihu.app.config.BihuConfig
import com.cheche365.cheche.chinalife.app.config.ChinalifeConfig
import com.cheche365.cheche.cpic.app.config.CpicConfig
import com.cheche365.cheche.pingan.app.config.PinganConfig
import com.cheche365.cheche.pinganuk.app.config.PinganUKNonProductionConfig
import com.cheche365.cheche.sinosig.app.config.SinosigConfig
import com.cheche365.cheche.test.parser.AThirdPartyInsuranceInfoServiceFT
import org.springframework.test.context.ContextConfiguration



/**
 * 获取保险信息的测试用例基类
 * Created by Huabin on 2017/5/30.
 */
@ContextConfiguration(classes = [
    CpicConfig,
    ChinalifeConfig,
    PinganConfig,
    PinganUKNonProductionConfig,
    SinosigConfig,
    BihuConfig
])
class AConcurrentInsuranceInfoServiceFT extends AThirdPartyInsuranceInfoServiceFT {

    @Override
    protected getDefaultServiceIndex() {
        1
    }

    @Override
    protected long getZzzTime() {
        30000L
    }

}
