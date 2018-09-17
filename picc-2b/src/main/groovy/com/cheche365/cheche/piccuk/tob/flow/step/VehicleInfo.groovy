package com.cheche365.cheche.piccuk.tob.flow.step

import com.cheche365.cheche.common.flow.IStep
import geb.Browser
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * 填写车辆信息
 * Created by suyaqiang on 2017/12/12.
 */
@Component
@Slf4j
class VehicleInfo implements IStep {

    @Override
    run(context) {

        def auto = context.auto

        Browser browser = context.browser
        def actionScript = {
            $('input', name: 'prpCitemCar.licenseNo').value(auto.licensePlateNo)

            $('input', name: 'prpCitemCar.vinNo').click()

            $('input', name: 'prpCitemCar.vinNo').value(auto.vinNo)

            try {
                withNoAlert {
                    $('input', name: 'carinfoPlat').click()
                }
            } catch (e) {
                $('input', name: 'carinfoPlat').click()
            }

            $('input', name: 'prpCitemCar.runMiles').value('5000')
        }

        actionScript.delegate = browser
        Browser.drive(browser, actionScript)

        getContinueFSRV '填写车辆信息完成'
    }
}
