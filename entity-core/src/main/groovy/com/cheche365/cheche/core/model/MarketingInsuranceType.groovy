package com.cheche365.cheche.core.model


import com.cheche365.cheche.core.repository.MarketingInsuranceTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

/**
 * Created by wenling on 2017/5/17.
 */
@Entity
class MarketingInsuranceType extends AutoLoadEnum{

    public static class Enum {
        public static MarketingInsuranceType COMPULSORY_1,  COMMERCIAL_2, AUTO_TAX_3;

        static List<MarketingInsuranceType> ALL
        static {
            ALL = RuntimeUtil.loadEnum(MarketingInsuranceTypeRepository, MarketingInsuranceType, Enum)
        }

        public static MarketingInsuranceType getMarketingInsuranceTypeById(Long id) {
            ALL.find {it.id == id}
        }
    }
}
