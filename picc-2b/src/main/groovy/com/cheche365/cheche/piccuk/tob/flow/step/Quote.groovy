package com.cheche365.cheche.piccuk.tob.flow.step

import com.cheche365.cheche.common.flow.IStep
import geb.Browser
import geb.module.Checkbox
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV


/**
 * 报价
 * Created by suyaqiang on 2017/12/12.
 */
@Component
@Slf4j
class Quote implements IStep {

    @Override
    run(context) {

        def auto = context.auto

        Browser browser = context.browser
        def actionScript = {

            $('li', id: 'kindTab').click()

            // 三者
            def c = $('input', name: 'prpCitemKindsTemp[2].chooseFlag').module(Checkbox)
            if (c.unchecked) {
                c.check()
            }
            $('select', name: 'selectOption[2]').value('20')

            // 乘客
            def passenger = $('input', name: 'prpCitemKindsTemp[4].chooseFlag').module(Checkbox)
            if (passenger.unchecked) {
                passenger.check()
            }
            $('input', name: 'prpCitemKindsTemp[4].unitAmount').value('10000')

            $('#buttonPremiumForFG-button').click() // 保费计算

            waitFor(180) {
                $('input', name: 'prpCmain.sumNetPremium').value() != ''
            }

            log.info '总保费:{}', $('input', name: 'prpCmain.sumNetPremium').value()

        }

        actionScript.delegate = browser
        Browser.drive(browser, actionScript)

        getContinueFSRV '进入报价页面'
    }

}
