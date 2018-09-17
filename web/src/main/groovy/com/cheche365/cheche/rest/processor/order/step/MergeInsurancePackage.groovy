package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.InsurancePackageRepository
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by zhengwei on 12/21/16.
 */

@Component
@Slf4j
class MergeInsurancePackage implements TPlaceOrderStep {

    @Override
    def run(Object context) {

        QuoteRecord quoteRecord = context.quoteRecord
        InsurancePackageRepository insurancePackageRepository = context.insurancePackageRepository

        InsurancePackage newInsurancePackage = quoteRecord.getInsurancePackage();
        if (newInsurancePackage.getUniqueString() == null) {
            newInsurancePackage.calculateUniqueString();
        }

        quoteRecord.setInsurancePackage(null);//workaround,
        InsurancePackage existInsurancePackage = insurancePackageRepository.findFirstByUniqueString(newInsurancePackage.getUniqueString());
        if (existInsurancePackage != null) {
            quoteRecord.setInsurancePackage(existInsurancePackage);
            log.debug("Found the existed insurance package with unique string " + existInsurancePackage.getUniqueString());
        } else {
            quoteRecord.setInsurancePackage(insurancePackageRepository.save(newInsurancePackage));
            log.debug("The insurance package with unique string " + quoteRecord.getInsurancePackage().getUniqueString() + " doesn't not exit, will save this new one.");
        }

        getContinueFSRV true
    }
}
