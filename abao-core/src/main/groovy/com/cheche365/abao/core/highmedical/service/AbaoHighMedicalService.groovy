package com.cheche365.abao.core.highmedical.service

import com.cheche365.abao.core.highmedical.model.InsureObject
import com.cheche365.abao.core.highmedical.model.InsureResult
import com.cheche365.abao.core.highmedical.model.OrderObject
import com.cheche365.abao.core.highmedical.model.OrderResult
import com.cheche365.abao.core.highmedical.model.QuoteObject
import com.cheche365.abao.core.highmedical.model.QuoteResult
import com.cheche365.cheche.core.model.Area
import groovy.util.logging.Slf4j
import org.kie.api.runtime.KieContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.abao.core.highmedical.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS



/**
 * 阿宝高端医疗报价/核保/承保服务
 * Created by suyq on 2016/12/21.
 */
@Service
@Slf4j
class AbaoHighMedicalService
    extends AFunctionalGeneralService<
                QuoteObject,
                QuoteResult,
                InsureObject,
                InsureResult,
                OrderObject,
                OrderResult> {

    @Autowired
    private KieContainer kieContainer


    @Override
    protected createContext(quoteObject) {
        [
            kieContainer           : kieContainer,
            cityQuotingFlowMappings: _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            // flow是城市相关的，所以必须有一个area在context中，目前高端医疗没有城市相关的处理需求，所以area给一个不存在的值即可让机制选取default flow
            area                   : new Area(id: -1L),
        ]
    }

}
