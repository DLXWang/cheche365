package com.cheche365.cheche.partner.model;

import com.cheche365.cheche.core.model.OrderStatus;

/**
 * Created by mahong on 2016/2/19.
 */
public enum BaiduBillStatus {
    UNPAID(1, "未支付"),
    PAID(2, "已支付"),
    CLOSED(3, "已关闭"),
    ERROR(4, "异常"),
    FINISHED(5, "已完成");

    private final Integer id;
    private final String description;

    private BaiduBillStatus(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static Integer convertToBaiduBillStatus(OrderStatus status) {
        if (status == null) {
            return null;
        }

        if (OrderStatus.Enum.PENDING_PAYMENT_1.getId().equals(status.getId())) {
            return BaiduBillStatus.UNPAID.getId();
        }
        if (OrderStatus.Enum.HANDLING_2.getId().equals(status.getId())) {
            return BaiduBillStatus.UNPAID.getId();
        }
        if (OrderStatus.Enum.PAID_3.getId().equals(status.getId())) {
            return BaiduBillStatus.PAID.getId();
        }
        if (OrderStatus.Enum.DELIVERED_4.getId().equals(status.getId())) {
            return BaiduBillStatus.PAID.getId();
        }
        if (OrderStatus.Enum.FINISHED_5.getId().equals(status.getId())) {
            return BaiduBillStatus.FINISHED.getId();
        }
        if (OrderStatus.Enum.CANCELED_6.getId().equals(status.getId())) {
            return BaiduBillStatus.CLOSED.getId();
        }
        if (OrderStatus.Enum.INSURE_FAILURE_7.getId().equals(status.getId())) {
            return BaiduBillStatus.ERROR.getId();
        }
        if (OrderStatus.Enum.EXPIRED_8.getId().equals(status.getId())) {
            return BaiduBillStatus.CLOSED.getId();
        }
        if (OrderStatus.Enum.REFUNDING_10.getId().equals(status.getId())) {
            return BaiduBillStatus.CLOSED.getId();
        }
        if (OrderStatus.Enum.REFUNDED_9.getId().equals(status.getId())) {
            return BaiduBillStatus.CLOSED.getId();
        }

        return null;
    }
}
