package com.cheche365.cheche.test.core.common

import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.app.config.CoreDevConfig
import com.cheche365.cheche.test.core.config.CoreTestConfig
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Created by zhengwei on 1/8/17.
 */

@EnableAutoConfiguration
@ContextConfiguration( classes = [CoreDevConfig] )
class CoreDBMockFT extends Specification {

}
