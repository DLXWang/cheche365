package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.PaymentTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PaymentType implements Serializable{
    private static final long serialVersionUID = 1L

    private Long id
    private String name
    private String description

    @Id
    Long getId() {
        return id
    }

    @Column(columnDefinition = "VARCHAR(50)")
    String getName() {
        return name
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    String getDescription() {
        return description
    }

    void setId(Long id) {
        this.id = id
    }

    void setName(String name) {
        this.name = name
    }

    void setDescription(String description) {
        this.description = description
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (o == null || !getClass().is(o.getClass())) return false
        PaymentType paymentType = (PaymentType) o
        return id == paymentType.id
    }

    @Override
    int hashCode() {
        return id.hashCode()
    }

    static class Segment {

        public static final String ZB = "Z" //增补
        public static final String TK = "T" //退款
        public static final String DR = "D" //按天买车险
    }

    static class Enum {

        public static PaymentType INITIALPAYMENT_1
        public static PaymentType ADDITIONALPAYMENT_2
        public static PaymentType PARTIALREFUND_3
        public static PaymentType FULLREFUND_4
        public static PaymentType DISCOUNT_5
        public static PaymentType CHECHEPAY_6
        public static PaymentType DAILY_RESTART_PAY_7
        public static PaymentType BAOXIANPAY_8

        public static Iterable<PaymentType> ALL
        public static List<Long> PAY_TYPES_ID
        public static List<Long> REFUND_TYPES_ID
        public static List<PaymentType> PAY_TYPES
        public static List<PaymentType> REFUND_TYPES
        public static Map<PaymentType, OrderStatus> PAYMENT_TYPE_TO_ORDER_TYPE
        public static Map<PaymentType, String> PAYMENT_TYPE_SEGMENT_MAP

        static {
            ALL = RuntimeUtil.loadEnum(PaymentTypeRepository, PaymentType, Enum)
            PAY_TYPES = [INITIALPAYMENT_1, ADDITIONALPAYMENT_2, DAILY_RESTART_PAY_7]
            PAY_TYPES_ID = PAY_TYPES.collect { it.id }
            REFUND_TYPES = [PARTIALREFUND_3, FULLREFUND_4]
            REFUND_TYPES_ID = REFUND_TYPES.collect { it.id }

            PAYMENT_TYPE_TO_ORDER_TYPE = [
                (ADDITIONALPAYMENT_2): OrderStatus.Enum.PENDING_PAYMENT_1,
                (FULLREFUND_4)       : OrderStatus.Enum.REFUNDING_10,
                (CHECHEPAY_6)        : OrderStatus.Enum.PAID_3,
                (BAOXIANPAY_8)       : OrderStatus.Enum.PAID_3
            ]

            PAYMENT_TYPE_SEGMENT_MAP = [
                (INITIALPAYMENT_1)   : (Segment.ZB),
                (ADDITIONALPAYMENT_2): (Segment.ZB),
                (PARTIALREFUND_3)    : (Segment.TK),
                (FULLREFUND_4)       : (Segment.TK),
                (DAILY_RESTART_PAY_7): (Segment.DR)
            ]
        }
    }
}
