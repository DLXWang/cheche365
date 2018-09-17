package com.cheche365.cheche.core.model.developer

import com.cheche365.cheche.core.model.AutoLoadEnum
import com.cheche365.cheche.core.repository.developer.DeveloperTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

/**
 * Created by zhengwei on 07/03/2018.
 */

@Entity
class DeveloperType extends AutoLoadEnum {

    static class Enum {
        static DeveloperType COMPANY_1,  INDIVIDUAL_2

        static  {
            RuntimeUtil.loadEnum(DeveloperTypeRepository, DeveloperType, Enum)
        }
    }

}
