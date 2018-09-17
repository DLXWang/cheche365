package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity;

/**
 * Created by mahong on 2016/6/16.
 */
@Entity
public class ActivityType extends AutoLoadEnum{
    private static final long serialVersionUID = 1L

    public static class Enum {
        public static ActivityType ORDINARY_ACTIVITY_1,  REDUCE_ACTIVITY_2, TUNNEL_ACTIVITY_3, FULL_REDUCE_4,
            FULL_SEND_5, INSURANCE_PACKAGE_DEDUCT_6,DISCOUNT_SEND_7;

        static List<ActivityType> ALL
        static {
            ALL = RuntimeUtil.loadEnum('activityTypeRepository', ActivityType, Enum)
        }

        public static ActivityType getActivityTypeById(Long id) {
            ALL.find {it.id == id}
        }
    }
}
