package com.cheche365.cheche.piccuk.tob.flow.step

import com.cheche365.cheche.common.flow.IStep
import geb.Browser
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV

/**
 * 填写关系人信息
 * Created by suyaqiang on 2017/12/12.
 */
@Component
@Slf4j
class RelationPersons implements IStep {

    @Override
    run(context) {

        def auto = context.auto

        Browser browser = context.browser
        def actionScript = {

            $('li', id: 'insuredTab').click()
            waitFor(10, 0.5) {
                $('input', name: '_identifyNumber')
            }

            $('input', name: '_identifyNumber').value(auto.identity)
            $('input', name: 'save2').click()
            $('input', name: 'insured_btn_Save').click()
        }

        actionScript.delegate = browser
        try {
            Browser.drive(browser, actionScript)
            getLoopBreakFSRV('填写关系人信息完成')
        } catch (e) {
            getLoopContinueFSRV(null, '进入关系信息失败，继续')
        }
    }

}
