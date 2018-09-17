package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

@Entity class InsuranceType extends AutoLoadEnum {

    static class Enum{
        public static InsuranceType COMMERCIAL_1, COMPULSORY_2

        static {
            RuntimeUtil.loadEnum('insuranceTypeRepository', InsuranceType, Enum)
        }

    }
}
