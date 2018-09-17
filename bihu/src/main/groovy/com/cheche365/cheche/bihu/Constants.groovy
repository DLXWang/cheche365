package com.cheche365.cheche.bihu

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PINGAN_20000

/**
 * 壁虎常量
 * Created by suyaqiang on 2017/11/22.
 */
class Constants {

    static final _INSURANCE_COMPANY_MAPPING = [
        (CPIC_25000.code)  : 1,
        (PINGAN_20000.code): 2,
        (PICC_10000.code)  : 4
    ]

}


