package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.AreaTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

@Entity
class AreaType extends AutoLoadEnum implements Serializable{
    private static final long serialVersionUID = 1L

    static class Enum {
        public static AreaType PROVINCE_1, MUNICIPALITY_2, CITY_3, DISTRICT_4, SAR_5;

        static {
            RuntimeUtil.loadEnum(AreaTypeRepository, AreaType, Enum)
        }
    }

}
