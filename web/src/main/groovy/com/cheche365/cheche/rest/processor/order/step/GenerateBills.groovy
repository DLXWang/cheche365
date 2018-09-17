package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.IdentityType
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by zhengwei on 12/20/16.
 */

@Component
@Slf4j
class GenerateBills implements TPlaceOrderStep {

    @Override
    def run(Object context) {

        QuoteRecord quoteRecord = context.quoteRecord
        PurchaseOrder order = context.order

        if (quoteRecord.premium) {
            context.insurance = generateBill(quoteRecord, Insurance.class, order)
            context.toBePersistObjects << context.insurance
        }
        if (quoteRecord.compulsoryPremium) {
            CompulsoryInsurance compulsoryInsurance = generateBill(quoteRecord, CompulsoryInsurance.class, order)
            compulsoryInsurance.effectiveDate = quoteRecord.compulsoryEffectiveDate
            compulsoryInsurance.expireDate = quoteRecord.compulsoryExpireDate
            context.compulsoryInsurance = compulsoryInsurance
            context.toBePersistObjects << context.compulsoryInsurance
        }

        getContinueFSRV true
    }

    def generateBill(QuoteRecord quoteRecord, Class targetClass, PurchaseOrder order) {

        def ignoreProperties = ['metaClass', 'class', 'id']
        def target = targetClass.newInstance();
        target.metaClass.properties.each {
            if (quoteRecord.metaClass.hasProperty(quoteRecord, it.name) && !ignoreProperties.contains(it.name))
                it.setProperty(target, quoteRecord.metaClass.getProperty(quoteRecord, it.name))
        }

        target.with {
            it.quoteRecord = quoteRecord
            insuredName = order.insuredName ? order.insuredName : quoteRecord.auto?.owner
            insuredIdNo = order.insuredIdNo && !order.insuredIdNo.contains('*') ? order.insuredIdNo : quoteRecord.auto.identity
            insuredIdentityType = order.insuredIdentityType ?: IdentityType.Enum.IDENTITYCARD
            applicantName = order.applicantName ? order.applicantName : target.insuredName
            applicantIdNo = order.applicantIdNo && !order.applicantIdNo.contains('*') ? order.applicantIdNo : target.insuredIdNo
            applicantIdentityType = order.applicantIdentityType ?: IdentityType.Enum.IDENTITYCARD
            it
        }

    }

}
