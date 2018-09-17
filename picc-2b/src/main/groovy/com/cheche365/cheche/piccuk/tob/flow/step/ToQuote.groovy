package com.cheche365.cheche.piccuk.tob.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.piccuk.tob.pages.PreQuotePage
import geb.Browser
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV


/**
 * 点击投保单录入，进入报价页面
 * Created by suyaqiang on 2017/12/12.
 */
@Component
@Slf4j
class ToQuote implements IStep {

    @Override
    run(context) {

        Browser browser = context.browser

        def actionScript = {
            to PreQuotePage

            try {
                driver.switchTo().alert().accept()
            } catch (e) {
                log.warn 'alert error'
            }
            Thread.sleep(1000)
            driver.switchTo().frame('main')
            Thread.sleep(3000)
            try {
                driver.switchTo().frame('page')
            } catch (e) {
                log.debug driver.getPageSource()
                driver.switchTo().frame('page')
            }

            waitFor(5, 1) {
                $('input', name: 'quick_proposal')
            }
            Thread.sleep(5000)

            $('input', name: 'quick_proposal').click()
            try {
                $('input', name: 'quick_proposal').click()
            } catch (e) {
                log.error '进入报价页失败', e
            }
        }

        actionScript.delegate = browser
        Browser.drive(browser, actionScript)

        getContinueFSRV '进入报价页面'
    }

}
