package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.OrderSubStatusRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

/**
 * Created by Administrator on 2017/12/20.
 */
@Entity
class OrderSubStatus extends AutoLoadEnum{
    private static final long serialVersionUID = 1L

    static class Enum{
        public static OrderSubStatus FAILED_1;

        static {
             RuntimeUtil.loadEnum(OrderSubStatusRepository, OrderSubStatus, Enum)
        }

    }

}
