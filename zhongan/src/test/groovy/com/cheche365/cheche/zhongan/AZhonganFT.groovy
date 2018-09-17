package com.cheche365.cheche.zhongan

//import com.cheche365.cheche.test.parser.AThirdPartyAPIHandlerServiceFT
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import com.cheche365.cheche.zhongan.app.config.ZhonganConfig
import com.cheche365.cheche.zhongan.app.config.ZhonganNonProductionConfig
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [ZhonganConfig,ZhonganNonProductionConfig])
abstract class AZhonganFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 50000, code: 'ZHONGAN', name: '众安保险']
    }

}
