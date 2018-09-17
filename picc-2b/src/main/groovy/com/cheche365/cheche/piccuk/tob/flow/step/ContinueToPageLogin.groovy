package com.cheche365.cheche.piccuk.tob.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.piccuk.tob.pages.ContinuePage
import geb.Browser
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_1


/**
 * 继续到登录
 * Created by suyaqiang on 2017/12/12.
 */
@Component
@Slf4j
class ContinueToPageLogin implements IStep {

    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_1

        Browser browser = context.browser

        def actionScript = {
            to ContinuePage
            driver.get("javascript:document.getElementById('overridelink').click();")
        }
        actionScript.delegate = browser

        Browser.drive(browser, actionScript)

        getContinueFSRV '进入登录页'
    }

}
