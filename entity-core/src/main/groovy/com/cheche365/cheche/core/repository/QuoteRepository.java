package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.Quote;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface QuoteRepository extends PagingAndSortingRepository<Quote, Long> {
    Quote findFirstByApplicantAndAutoAndQuoteTimeBeforeOrderByQuoteTimeDesc(User user, Auto auto, Date quoteTime);
    Quote findFirstByApplicantAndAuto(User user, Auto auto);
}
