package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Quote
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.repository.QuoteRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@Slf4j
class QuoteService {

    @Autowired
    private QuoteRecordRepository quoteRecordRepository
    @Autowired
    private QuoteRepository quoteRepository
    @Autowired
    private AutoService autoService
    @Autowired
    private InsurancePackageService insurancePackageService

    QuoteRecord saveRecord(QuoteRecord quoteRecord, User user, Channel channel) {

        if (null == quoteRecord.auto.id) {
            quoteRecord.auto = this.autoService.saveOrMerge(quoteRecord.auto, user)
            log.warn("Save the auto when saving the Quote Record, should not happen.")
        }

        quoteRecord.insurancePackage = insurancePackageService.mergeInsurancePackage(quoteRecord.insurancePackage)

        Quote quote = new Quote()
        quote.setAuto(quoteRecord.getAuto())
        quote.setApplicant(user)
        quoteRecord.setQuote(quote)
        quoteRecord.setApplicant(user)
        quoteRecord.setChannel(channel)
        quoteRecord.setCreateTime(Calendar.getInstance().getTime())

        this.quoteRepository.save(quoteRecord.getQuote())
        this.quoteRecordRepository.save(quoteRecord)

    }

    QuoteRecord save(QuoteRecord quoteRecord) {
        this.quoteRecordRepository.save(quoteRecord)
    }

    QuoteRecord getById(Long id) {
        this.quoteRecordRepository.findOne(id)
    }
}
