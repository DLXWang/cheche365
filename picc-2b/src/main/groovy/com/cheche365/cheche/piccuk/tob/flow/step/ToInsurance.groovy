package com.cheche365.cheche.piccuk.tob.flow.step

import com.cheche365.cheche.common.flow.IStep
import geb.Browser
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * 进入报价前页面
 * Created by suyaqiang on 2017/12/12.
 */
@Component
@Slf4j
class ToInsurance implements IStep {

    @Override
    run(context) {

        Browser browser = context.browser

        def actionScript = {
            driver.switchTo().frame('main')
            def originalWindows = driver.windowHandles
            toInsuranceButton.click()

            def withinNewWindows = driver.windowHandles
            def theNewWindow = (withinNewWindows - originalWindows) as List
            driver.switchTo().window(theNewWindow.first())
            driver.get("javascript:document.getElementById('overridelink').click();")
        }

        actionScript.delegate = browser
        Browser.drive(browser, actionScript)

        getContinueFSRV '进入报价前页面'
    }

}
