package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSupplementInfo
import com.cheche365.cheche.core.model.SourceType
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.repository.QuoteSupplementInfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author sunhuazhong
 */
@Service
@Transactional
class QuoteRecordService  {

    @Autowired
    private QuoteRecordRepository quoteRecordRepository

    @Autowired
    private QuoteSupplementInfoRepository supplementInfoRepository

    public QuoteRecord getById(Long id) {
        this.quoteRecordRepository.findOne(id)
    }

    QuoteRecord getByIdAndUser(Long id, User user) {
        this.quoteRecordRepository.findFirstByApplicantAndId(user, id)
    }

    public QuoteRecord reGenerateQuoteRecord(QuoteRecord old) {
        QuoteRecord quoteRecordNew = new QuoteRecord();
        QuoteRecord.copyProperties(old, quoteRecordNew);
        quoteRecordNew.setId(null);
        return quoteRecordRepository.save(quoteRecordNew);
    }

    def fillUpAutoModel(QuoteRecord quoteRecord, Map additionalParameters) {
        additionalParameters = additionalParameters ?: [:]
        quoteRecord.auto.autoType = quoteRecord.auto.autoType ?: new AutoType()
        if (quoteRecord.id) {
            List<QuoteSupplementInfo> quoteSupplementInfoList = supplementInfoRepository.findByQuoteRecord(quoteRecord)
            LackOfSupplementInfoHandler.getByQuoteSupplementInfo(quoteSupplementInfoList, additionalParameters)
        }
        quoteRecord.auto.autoType.setModel(additionalParameters.supplementInfo?.selectedAutoModel?.text)
    }

    QuoteRecord getByPurchaseOrderByObjId(long id){
        quoteRecordRepository.findOne(id)
    }
}
