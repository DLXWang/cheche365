package com.cheche365.cheche.core.repository;
import com.cheche365.cheche.core.model.abao.InsuranceQuote;
import com.cheche365.cheche.core.model.abao.InsuranceQuoteField;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by CXY on 16-12-28.
 */
@Repository
public interface InsuranceQuoteFieldRepository extends PagingAndSortingRepository<InsuranceQuoteField, Long>, JpaSpecificationExecutor<InsuranceQuoteField> {
    InsuranceQuoteField findFirstByInsuranceQuote(InsuranceQuote quote);
}
