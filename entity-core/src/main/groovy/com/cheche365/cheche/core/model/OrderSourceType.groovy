package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.OrderSourceTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

@Entity
public class OrderSourceType extends AutoLoadEnum {
    private static final long serialVersionUID = 1L

    public static class Enum {
        public static OrderSourceType CPS_CHANNEL_1, PLANTFORM_BX_5, OFFLINE_6,INTF_SYNC_7,PLANTFORM_BOTPY_8,PLANTFORM_AGENT_PARSER_9;
        public static List<OrderSourceType> ALL;

        static {
            ALL = RuntimeUtil.loadEnum(OrderSourceTypeRepository, OrderSourceType, Enum)
        }
    }
}
