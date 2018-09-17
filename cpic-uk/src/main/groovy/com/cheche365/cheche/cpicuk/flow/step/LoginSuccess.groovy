package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

 import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

@Component
@Slf4j
class LoginSuccess implements IStep{

    @Override
    run (context) {
        log.debug "小鳄鱼成功登录城市编码： {}", context.city
        context.newCity = context.city
        getContinueFSRV  context.city
    }

}
