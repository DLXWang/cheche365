package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.SourceTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

@Entity
class SourceType extends AutoLoadEnum {

    static class Enum {
        public static SourceType PURCHASE_ORDER_1, WECHATRED_2, GIFT_CODE_4
        public static List<SourceType> ALL;

        static {
            ALL = RuntimeUtil.loadEnum(SourceTypeRepository, SourceType, Enum)
        }

        static SourceType getById(Long id){
            ALL.find {it.id == id}
        }
    }
}
