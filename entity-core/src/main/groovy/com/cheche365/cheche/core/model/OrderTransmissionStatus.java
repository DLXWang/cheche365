package com.cheche365.cheche.core.model;


import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.repository.OrderTransmissionStatusRepository;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
@Entity
public class OrderTransmissionStatus implements Serializable {

    private static final long serialVersionUID = 7820986745235222793L;
    private Long id;
    private String status;
    private String description;
    private OrderTransmissionStatus reference;
    public static Map<Long,List<OrderTransmissionStatus>> NEXT_STATUS;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "reference", foreignKey=@ForeignKey(name="FK_ORDER_TRANSMISSION_STATUS_REF_ORDER_TRANSMISSION_STATUS", foreignKeyDefinition="FOREIGN KEY (reference) REFERENCES order_transmission_status(id)"))
    public OrderTransmissionStatus getReference() {
        return reference;
    }

    public void setReference(OrderTransmissionStatus reference) {
        this.reference = reference;
    }

    public List<OrderTransmissionStatus> next(){
        return  NEXT_STATUS.get(this.id);
    }

    public static class Enum{
        //等待付款 20
        public static OrderTransmissionStatus UNPAID;
        //未确认：1
        public static OrderTransmissionStatus UNCONFIRMED;
        //确认出单 13
        public static OrderTransmissionStatus CONFIRM_TO_ORDER;
        //出单完成 14
        public static OrderTransmissionStatus PAID_AND_FINISH_ORDER;
        //录单完成 15
        public static OrderTransmissionStatus ORDER_INPUTED;
        //退款中 16
        public static OrderTransmissionStatus APPLY_FOR_REFUND;
        //已退款 17
        public static OrderTransmissionStatus REFUNDED;
        //已取消 19
        public static OrderTransmissionStatus CANCELED;
        //退款失败 21
        public static OrderTransmissionStatus REFUND_FAILED;
        //追加付款 22
        public static OrderTransmissionStatus ADDITION_PAID;
        //核保失败 23
        public static OrderTransmissionStatus UNDERWRITING_FAILED;

        public static List<OrderTransmissionStatus> ALLSTATUS;

        public static Map ALL_STATUS_MAP;


        static {
            OrderTransmissionStatusRepository orderTransmissionStatusRepository =ApplicationContextHolder.getApplicationContext().getBean(OrderTransmissionStatusRepository.class);
            UNPAID = orderTransmissionStatusRepository.findFirstByStatus("等待付款");
            UNCONFIRMED = orderTransmissionStatusRepository.findFirstByStatus("未确认");
            CONFIRM_TO_ORDER = orderTransmissionStatusRepository.findFirstByStatus("确认出单");
            PAID_AND_FINISH_ORDER = orderTransmissionStatusRepository.findFirstByStatus("出单完成");
            ORDER_INPUTED = orderTransmissionStatusRepository.findFirstByStatus("录单完成");
            APPLY_FOR_REFUND = orderTransmissionStatusRepository.findFirstByStatus("退款中");
            REFUNDED = orderTransmissionStatusRepository.findFirstByStatus("退款成功");
            CANCELED = orderTransmissionStatusRepository.findFirstByStatus("订单取消");
            REFUND_FAILED = orderTransmissionStatusRepository.findFirstByStatus("退款失败");
            ADDITION_PAID = orderTransmissionStatusRepository.findFirstByStatus("追加付款中");
            UNDERWRITING_FAILED = orderTransmissionStatusRepository.findFirstByStatus("核保失败");

            ALLSTATUS = Arrays.asList(new OrderTransmissionStatus[]{UNPAID,UNCONFIRMED,CONFIRM_TO_ORDER, PAID_AND_FINISH_ORDER, ORDER_INPUTED,
                APPLY_FOR_REFUND, REFUNDED, CANCELED,REFUND_FAILED,ADDITION_PAID,UNDERWRITING_FAILED});
            initAllStatusKeys();
        }

        public static void initAllStatusKeys(){
            ALL_STATUS_MAP=new HashMap(){{
                put("UNPAID",UNPAID.getId());
                put("UNCONFIRMED",UNCONFIRMED.getId());
                put("CONFIRM_TO_ORDER",CONFIRM_TO_ORDER.getId());
                put("PAID_AND_FINISH_ORDER",PAID_AND_FINISH_ORDER.getId());
                put("ORDER_INPUTED",ORDER_INPUTED.getId());
                put("APPLY_FOR_REFUND",APPLY_FOR_REFUND.getId());
                put("REFUNDED",REFUNDED.getId());
                put("CANCELED",CANCELED.getId());
                put("REFUND_FAILED",REFUND_FAILED.getId());
                put("ADDITION_PAID",ADDITION_PAID.getId());
                put("UNDERWRITING_FAILED",UNDERWRITING_FAILED.getId());
            }};

            NEXT_STATUS = new HashMap(){{
                put(UNPAID.getId(),Arrays.asList(CANCELED));
                put(UNCONFIRMED.getId(),Arrays.asList(CONFIRM_TO_ORDER));
                put(CONFIRM_TO_ORDER.getId(),Arrays.asList(PAID_AND_FINISH_ORDER,UNCONFIRMED));
                put(PAID_AND_FINISH_ORDER.getId(),Arrays.asList(ORDER_INPUTED));
                put(REFUND_FAILED.getId(),Arrays.asList(APPLY_FOR_REFUND));
                put(UNDERWRITING_FAILED.getId(),Arrays.asList(UNPAID,CANCELED));
            }};
        }


        public static List<OrderTransmissionStatus> getNewStatusList() {
            return Arrays.asList(UNPAID,UNCONFIRMED,CONFIRM_TO_ORDER, PAID_AND_FINISH_ORDER, ORDER_INPUTED,
                APPLY_FOR_REFUND, REFUNDED, CANCELED,REFUND_FAILED,ADDITION_PAID,UNDERWRITING_FAILED);
        }

        public static OrderTransmissionStatus format(Long statusId) {
            for (OrderTransmissionStatus status : getNewStatusList()) {
                if (statusId.equals(status.getId())) return status;
            }
            return null;
        }

        //可增补操作状态
        public static List<Long> supportAmendStatus(){
             return Arrays.asList(UNPAID.id, UNCONFIRMED.id, UNDERWRITING_FAILED.id, ADDITION_PAID.id );
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof OrderTransmissionStatus && EqualsBuilder.reflectionEquals(this, obj);
        }
    }

}
