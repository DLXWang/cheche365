package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.AttributeTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

/**
 * Created by wen on 2018/6/14.
 */
@Entity
class AttributeType extends AutoLoadEnum {

    static class Enum {
        public static AttributeType BOTPY_ACCOUNT_1
        public static AttributeType AGENT_PARSER_ACCOUNT_2
        public static AttributeType AGENT_PARSER_PICC_SERIAL_NO_3
        public static AttributeType AGENT_PARSER_PICC_PAY_TYPE_4

        static {
            RuntimeUtil.loadEnum(AttributeTypeRepository, AttributeType, Enum)
        }
    }
}
