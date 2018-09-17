package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.util.RuntimeUtil
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Created by mahong on 2016/7/28.
 */
@Entity
public class RuleParam {
    private Long id;
    private String paramName;
    private RuleParamType paramType;
    private ActivityType activityType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    @ManyToOne
    @JoinColumn(name = "paramType", foreignKey = @ForeignKey(name = "FK_RULE_PARAM_REF_PARAM_TYPE", foreignKeyDefinition = "FOREIGN KEY (`param_type`) REFERENCES `rule_param_type` (`id`)"))
    public RuleParamType getParamType() {
        return paramType;
    }

    public void setParamType(RuleParamType paramType) {
        this.paramType = paramType;
    }

    @ManyToOne
    @JoinColumn(name = "activityType", foreignKey = @ForeignKey(name = "FK_RULE_PARAM_REF_ACTIVITY_TYPE", foreignKeyDefinition = "FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`)"))
    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public static class Enum {
        public static RuleParam REDUCE_FULL_RULE_1;//满减规则
        public static RuleParam REDUCE_IS_REGULAR_RULE_2;//是否累计
        public static RuleParam REDUCE_TOP_RULE_3;//最高减免
        public static RuleParam REDUCE_ORDER_PKG_LIMIT_RULE_4;//购买险种限制
        public static RuleParam REDUCE_CALCULATE_PKG_LIMIT_RULE_5;//满额险种限制
        public static RuleParam REDUCE_OTHER_SEND_RULE_6;//其他附加优惠

        public static RuleParam SEND_FULL_RULE_7;//满送规则
        public static RuleParam SEND_IS_REGULAR_RULE_8;//是否累计
        public static RuleParam SEND_TOP_RULE_9;//最高赠送
        public static RuleParam SEND_ORDER_PKG_LIMIT_RULE_10;//购买险种限制
        public static RuleParam SEND_CALCULATE_PKG_LIMIT_RULE_11;//满额险种限制
        public static RuleParam SEND_OTHER_SEND_RULE_12;//其他附加优惠

        public static RuleParam DEDUCT_INSURANCE_TYPE_RULE_13;//抵扣险种类型
        public static RuleParam DEDUCT_TOP_PKG_RULE_14;//最高抵扣计算需要包含的险种类型
        public static RuleParam DEDUCT_PERCENT_RULE_15;//抵扣比例
        public static RuleParam DEDUCT_ORDER_PKG_LIMIT_RULE_16;//购买险种限制
        public static RuleParam DEDUCT_CALCULATE_PKG_LIMIT_RULE_17;//满额险种限制
        public static RuleParam DEDUCT_OTHER_SEND_RULE_18;//其他附加优惠


        ///////////////////////////////折扣赠送
        public static  RuleParam DISCOUNT_SEND_CALCULATE_PKG_LIMIT_TYPE_RULE_19;//满额险种限制  【门槛  -> 非必填】
        public static  RuleParam DISCOUNT_SEND_CALCULATE_PKG_LIMIT_RULE_20;//折扣满额限制  【门槛  -> 非必填】
        public static  RuleParam DISCOUNT_SEND_INSURANCE_TYPE_PERCENT_RULE_22;//赠送险种百分比 【必填】
        public static  RuleParam DISCOUNT_SEND_TOP_PKG_RULE_23;//最高赠送限制   【封顶  ->非必填】
        public static  RuleParam DISCOUNT_SEND_OTHER_SEND_RULE_24;//其他附加优惠  【额外赠送  ->非必填】

        public static String TRUE;
        public static String FALSE;

        public static Long[] MUST_BY_INSURANCE_TYPES = new Long[4];
        public static Long[] FULL_BY_INSURANCE_TYPES = new Long[4];

        public static final Iterable<RuleParam> ALL;

        static {
            ALL = RuntimeUtil.loadEnum('ruleParamRepository', RuleParam, Enum)

            REDUCE_FULL_RULE_1 = ALL.find { ruleParam -> 1L == ruleParam.getId() };
            REDUCE_IS_REGULAR_RULE_2 = ALL.find { ruleParam -> 2L == ruleParam.getId() };
            REDUCE_TOP_RULE_3 = ALL.find { ruleParam -> 3L == ruleParam.getId() };
            REDUCE_ORDER_PKG_LIMIT_RULE_4 = ALL.find { ruleParam -> 4L == ruleParam.getId() };
            REDUCE_CALCULATE_PKG_LIMIT_RULE_5 = ALL.find { ruleParam -> 5L == ruleParam.getId() };
            REDUCE_OTHER_SEND_RULE_6 = ALL.find { ruleParam -> 6L == ruleParam.getId() };

            SEND_FULL_RULE_7 = ALL.find { ruleParam -> 7L == ruleParam.getId() };
            SEND_IS_REGULAR_RULE_8 = ALL.find { ruleParam -> 8L == ruleParam.getId() };
            SEND_TOP_RULE_9 = ALL.find { ruleParam -> 9L == ruleParam.getId() };
            SEND_ORDER_PKG_LIMIT_RULE_10 = ALL.find { ruleParam -> 10L == ruleParam.getId() };
            SEND_CALCULATE_PKG_LIMIT_RULE_11 = ALL.find { ruleParam -> 11L == ruleParam.getId() };
            SEND_OTHER_SEND_RULE_12 = ALL.find { ruleParam -> 12L == ruleParam.getId() };

            DEDUCT_INSURANCE_TYPE_RULE_13 = ALL.find { ruleParam -> 13L == ruleParam.getId() };
            DEDUCT_TOP_PKG_RULE_14 = ALL.find { ruleParam -> 14L == ruleParam.getId() };
            DEDUCT_PERCENT_RULE_15 = ALL.find { ruleParam -> 15L == ruleParam.getId() };
            DEDUCT_ORDER_PKG_LIMIT_RULE_16 = ALL.find { ruleParam -> 16L == ruleParam.getId() };
            DEDUCT_CALCULATE_PKG_LIMIT_RULE_17 = ALL.find { ruleParam -> 17L == ruleParam.getId() };
            DEDUCT_OTHER_SEND_RULE_18 = ALL.find { ruleParam -> 18L == ruleParam.getId() };


            DISCOUNT_SEND_CALCULATE_PKG_LIMIT_TYPE_RULE_19= ALL.find { ruleParam -> 19L == ruleParam.getId() };
            DISCOUNT_SEND_CALCULATE_PKG_LIMIT_RULE_20= ALL.find { ruleParam -> 20L == ruleParam.getId() };
            DISCOUNT_SEND_INSURANCE_TYPE_PERCENT_RULE_22= ALL.find { ruleParam -> 22L == ruleParam.getId() };
            DISCOUNT_SEND_TOP_PKG_RULE_23= ALL.find { ruleParam -> 23L == ruleParam.getId() };
            DISCOUNT_SEND_OTHER_SEND_RULE_24= ALL.find { ruleParam -> 24L == ruleParam.getId() };

            MUST_BY_INSURANCE_TYPES[0] = 4L;
            MUST_BY_INSURANCE_TYPES[1] = 10L;
            MUST_BY_INSURANCE_TYPES[2] = 16L;
            MUST_BY_INSURANCE_TYPES[3] = 21L;
            FULL_BY_INSURANCE_TYPES[0] = 5L;
            FULL_BY_INSURANCE_TYPES[1] = 11L;
            FULL_BY_INSURANCE_TYPES[2] = 17L;
            FULL_BY_INSURANCE_TYPES[3] = 19L;
//                MUST_BY_INSURANCE_TYPES[0] = 4L;
//                MUST_BY_INSURANCE_TYPES[1] = 10L;
//                MUST_BY_INSURANCE_TYPES[2] = 16L;
//                FULL_BY_INSURANCE_TYPES[0] = 5L;
//                FULL_BY_INSURANCE_TYPES[1] = 11L;
//                FULL_BY_INSURANCE_TYPES[2] = 17L;

            TRUE = "true";
            FALSE = "false";

        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RuleParam && EqualsBuilder.reflectionEquals(this, o, 'paramName', 'paramType', 'activityType');
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, 'paramName', 'paramType', 'activityType');
    }
}
