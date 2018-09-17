package com.cheche365.cheche.zhongan

import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.zhongan.AZhonganFT
import com.cheche365.cheche.zhongan.app.config.ZhonganConfig
import org.springframework.test.context.ContextConfiguration

/**
 * 南京
 */
@ContextConfiguration(classes = [ZhonganConfig, CoreConfig])
class Zhongan320100nanjingFT extends AZhonganFT {

    @Override
    protected getAreaProperties() {
        [id: 320100L, name: '南京市']
    }

}
