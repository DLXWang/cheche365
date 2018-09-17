package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User

/**
 * @author sunhuazhong
 */
interface IQuoteRecordService {

    QuoteRecord getFirstQuoteRecordByApplicantAndAuto(User user, Auto auto)

    QuoteRecord getFirstByApplicantAndAutoAndInsurancePackageAndCreateTimeAfter(
        User user, Auto auto, InsurancePackage insurancePackage, Date createTime)

    public void saveRecord(QuoteRecord record)

    QuoteRecord getById(Long id)

}
