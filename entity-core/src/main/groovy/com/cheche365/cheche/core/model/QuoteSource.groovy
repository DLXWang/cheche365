package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.QuoteSourceRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.Entity

import static com.cheche365.cheche.core.model.InsuranceCompany.apiQuoteCompanies

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class QuoteSource extends AutoLoadEnum {

    static class Enum {

        public static QuoteSource RULEENGINE_1, WEBPARSER_2, TELEMARKETING_3, API_4, WAP_5,
                                  PLANTFORM_BX_6, REFERENCED_7, RULEENGINE2_8, AGENTPARSER_9,
                                  PLATFORM_BIHU_10, PLATFORM_BOTPY_11

        public static List<QuoteSource> ALL
        public static List<QuoteSource> API_QUOTE_SOURCES
        public static List<QuoteSource> THIRD_PAY_SOURCES
        public static List<QuoteSource> PLATFORM_SOURCES

        static {
            ALL = RuntimeUtil.loadEnum(QuoteSourceRepository.class, QuoteSource.class, Enum.class)
            API_QUOTE_SOURCES = [API_4, PLANTFORM_BX_6, PLATFORM_BIHU_10, PLATFORM_BOTPY_11,AGENTPARSER_9]
            THIRD_PAY_SOURCES = [API_4, PLANTFORM_BX_6, PLATFORM_BIHU_10,AGENTPARSER_9]
            PLATFORM_SOURCES = [AGENTPARSER_9,PLATFORM_BOTPY_11]
        }

        static QuoteSource findById(Long id) {
            return ALL.find { it.id == id }
        }

    }

    static QuoteSource getQuoteSource(company, quoteSourceMap) {
        (quoteSourceMap.get(company) && (Enum.REFERENCED_7 != quoteSourceMap.get(company))) ?
            quoteSourceMap.get(company) :
            (apiQuoteCompanies().contains(company) ? Enum.API_4 : Enum.WEBPARSER_2)
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (o == null || !getClass().is(o.getClass())) return false
        QuoteSource quoteSource = (QuoteSource) o
        return id == quoteSource.id
    }

    @Override
    int hashCode() {
        return id.hashCode()
    }

}
