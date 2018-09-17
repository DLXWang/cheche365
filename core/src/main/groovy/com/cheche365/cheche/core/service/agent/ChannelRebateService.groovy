package com.cheche365.cheche.core.service.agent

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.InsurancePurchaseOrderRebate
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.ChannelRebateRepository
import com.cheche365.cheche.core.repository.InsurancePurchaseOrderRebateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChannelRebateService {

    @Autowired
    private ChannelRebateRepository rebateRepository

    @Autowired
    private InsurancePurchaseOrderRebateRepository orderRebateRepository

    ChannelRebate getChannelRebate(QuoteRecord quoteRecord, PurchaseOrder order) {
        InsurancePurchaseOrderRebate orderRebate
        if (order) {
            orderRebate = orderRebateRepository.findFirstByPurchaseOrder(order)
        }

        orderRebate ? new ChannelRebate(commercialRebate: orderRebate.upCommercialRebate, compulsoryRebate: orderRebate.upCompulsoryRebate) :
            rebateRepository.findFirstByChannelAndAreaAndInsuranceCompanyAndStatus(Channel.findAgentChannel(quoteRecord.channel), quoteRecord.area, quoteRecord.insuranceCompany, ChannelRebate.Enum.EFFECTIVED_1)
    }

    /**
     * 代理人代缴税费计算
     * 增值税:vat
     * 城建税:urbanTax
     * 教育费附加:educationTax
     * 个税:personalTax
     */
    def calculateTaxFee(Double rebate) {
        Double vat = (rebate > 30000) ? (rebate / 1.03) * 0.03 : 0
        Double urbanTax = vat * 0.07
        Double educationTax = (rebate > 100000) ? vat * 0.05 : 0
        Double personalTax
        if (rebate > 107939.30) {
            personalTax = (rebate / 1.03 * 0.6 - urbanTax - educationTax) * 0.8 * 0.4 - 7000
        } else if (rebate > 43067.40) {
            personalTax = (rebate / 1.03 * 0.6 - urbanTax - educationTax) * 0.8 * 0.3 - 2000
        } else if (rebate > 6866.67) {
            personalTax = (rebate / 1.03 * 0.6 - urbanTax - educationTax) * 0.8 * 0.2
        } else if (rebate > 1373.33) {
            personalTax = (rebate / 1.03 * 0.6 - urbanTax - educationTax - 800) * 0.2
        } else {
            personalTax = 0
        }
        return DoubleUtils.displayDoubleValue(vat + urbanTax + educationTax + personalTax)
    }
}
