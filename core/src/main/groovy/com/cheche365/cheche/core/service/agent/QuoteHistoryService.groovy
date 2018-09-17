package com.cheche365.cheche.core.service.agent

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.QuoteHistory
import com.cheche365.cheche.core.repository.agent.QuoteHistoryRepository
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class QuoteHistoryService {

    @Autowired
    QuoteHistoryRepository quoteHistoryRepository

    Page<QuoteHistory> findQuoteHistory(User user, Channel channel, String keyWords, Pageable pageable) {
        quoteHistoryRepository.findByUserAndChannel(user, channel, keyWords, DateUtils.addYears(new Date(), -1), pageable)
    }

    QuoteHistory saveQuoteHistory(quoteContext, Auto autoSaved) {
        quoteHistoryRepository.save(new QuoteHistory(
            user: quoteContext.user,
            auto: autoSaved,
            area: quoteContext.insuranceArea,
            channel: quoteContext.channel,
            insurancePackage: quoteContext.insurancePackage,
            compulsoryStartDate: quoteContext.supplementInfo?.compulsoryStartDate,
            commercialStartDate: quoteContext.supplementInfo?.commercialStartDate,
            createTime: new Date(),
            updateTime: new Date()
        ))
    }
}
