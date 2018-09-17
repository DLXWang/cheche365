package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.InsurancePackage;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.User;

public interface IQuoteCalculator {

    public QuoteRecord calculate(User applicant, Auto auto, InsurancePackage insurancePackage);

}
