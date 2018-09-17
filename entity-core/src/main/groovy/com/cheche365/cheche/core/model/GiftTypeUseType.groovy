package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.GiftTypeUseTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

@Entity
class GiftTypeUseType extends AutoLoadEnum {

    static class Enum {
        public static GiftTypeUseType REDUCE_1, GIVENAFTERORDER_3;
        public static List<GiftTypeUseType> ALL;

        static {
            ALL = RuntimeUtil.loadEnum(GiftTypeUseTypeRepository, GiftTypeUseType, Enum)
        }

        static GiftTypeUseType findById(Long id){
            ALL.find {it.id == id}
        }
    }
}
