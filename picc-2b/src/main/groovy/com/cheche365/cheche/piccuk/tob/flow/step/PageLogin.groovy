package com.cheche365.cheche.piccuk.tob.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.piccuk.tob.pages.LoginPage
import geb.Browser
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV


/**
 * 登录
 * Created by suyaqiang on 2017/12/12.
 */
@Component
@Slf4j
class PageLogin implements IStep {

    @Override
    run(context) {

        Browser browser = context.browser

        def actionScript = {
            to LoginPage

            userName.value('C110100102')
            passWord.value('pass@word1')
            loginButton.click()
        }
        actionScript.delegate = browser
        Browser.drive(browser, actionScript)

        if (browser.page.title in ['PICC第三代核心业务系统', 'PICC-核心业务系统-登录中心']) {
            log.info '用户{}登录成功，报价车辆：{}', '', context.auto.licensePlateNo
            getContinueFSRV '登录成功'
        } else {
            log.info '用户{}登录失败，报价车辆：{}', '', context.auto.licensePlateNo
            getFatalErrorFSRV '登录失败'
        }
    }

}
