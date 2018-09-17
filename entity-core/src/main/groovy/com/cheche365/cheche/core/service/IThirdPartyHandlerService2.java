package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.QuoteRecord;

import java.util.Map;

/**
 * 第三方报价业务
 */
public interface IThirdPartyHandlerService2 extends IThirdPartyHandlerService {

    Map<InsuranceCompany, Map> quotes(QuoteRecord quoteRecord, Map<String, Object> additionalParameters);

}
