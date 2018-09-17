package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1



/**
 * 修改全险套餐
 */
@Component
@Slf4j
class AdjustInsurancePackage implements IStep {

    @Override
    run(context) {

        if (context.accurateInsurancePackage) {
            context.accurateInsurancePackage.with {
                theft = true;
                theftIop = true;
                engine = true;
                engineIop = true;
                glass = true;
                glassType = DOMESTIC_1;
                driverAmount = 50000;
                driverIop = true;
                passengerAmount = 50000;
                passengerIop = true;
                spontaneousLoss = true;
                scratchAmount = 2000;
                scratchIop = true;
                spontaneousLossIop = true;
                unableFindThirdParty = true;
                it
            }
            log.info '因保险公司商业险险种限制，修改全险险种继续投保'
            getLoopContinueFSRV null, '修改全险险种继续投保'
        }
    }

}
