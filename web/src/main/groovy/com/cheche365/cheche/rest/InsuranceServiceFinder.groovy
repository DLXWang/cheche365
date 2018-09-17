package com.cheche365.cheche.rest

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.web.util.ClientTypeUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Service
@Slf4j
class InsuranceServiceFinder {

    @Autowired
    List<IThirdPartyHandlerService> services
    @Autowired
    QuoteConfigService quoteConfigService
    @Autowired(required = false)
    public HttpSession session

    IThirdPartyHandlerService find(InsuranceCompany company, Area area, QuoteSource quoteSource = null) {

        if (!quoteSource) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
            Channel channel = ClientTypeUtil.getChannel(request)
            quoteSource = quoteConfigService.findQuoteSource(channel, area, company)
        }

        services.find {
            it.isSuitable([
                "quoteSource"     : quoteSource,
                "insuranceCompany": company.getParent()
            ])
        }
    }

}
