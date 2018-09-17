package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.OrderCancelReasonTypeRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * Created by zhaozhong on 2015/9/2.
 */
@Entity
public class OrderCancelReasonType {

    private Long id;
    private String reason;
    @JsonIgnore
    private String description;
    @JsonIgnore
    private Integer order;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Column(columnDefinition = "VARCHAR(400)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * 取消原因：
     * ① 选择险种或保额；
     * ② 有点贵
     * ③ 地址填写错误
     * ④ 无法支付
     * ⑤ 不能使用优惠券
     * ⑥ 不想买了
     * ⑦ 其他原因
     */
    @Component
    public static class Enum {
        //选择险种或保额；
        public OrderCancelReasonType INSURANCEKIND_OR_SUMINSURED;
        //有点贵
        public OrderCancelReasonType TOO_EXPENSIVE;
        //地址填写错误
        public OrderCancelReasonType ERROR_ADDR;
        //无法支付
        public OrderCancelReasonType CAN_NOT_PAY;
        //不能使用优惠券
        public OrderCancelReasonType CAN_NOT_GIFT;
        //不想买了
        public OrderCancelReasonType DONT_WANT_BUY;
        //其他原因
        public OrderCancelReasonType OTHER_REASON;

        @Autowired
        public Enum(OrderCancelReasonTypeRepository orderCancelReasonTypeRepository) {
            INSURANCEKIND_OR_SUMINSURED = orderCancelReasonTypeRepository.findFirstByReason("选择险种或保额");
            TOO_EXPENSIVE = orderCancelReasonTypeRepository.findFirstByReason("有点贵");
            ERROR_ADDR = orderCancelReasonTypeRepository.findFirstByReason("地址填写错误");
            CAN_NOT_PAY = orderCancelReasonTypeRepository.findFirstByReason("无法支付");
            CAN_NOT_GIFT = orderCancelReasonTypeRepository.findFirstByReason("不能使用优惠券");
            DONT_WANT_BUY = orderCancelReasonTypeRepository.findFirstByReason("不想买了");
            OTHER_REASON = orderCancelReasonTypeRepository.findFirstByReason("其他原因");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderCancelReasonType that = (OrderCancelReasonType) o;

        if (!id.equals(that.id)) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        return !(description != null ? !description.equals(that.description) : that.description != null);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "OrderCancelReasonType{" +
            "id=" + id +
            ", reason='" + reason + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
