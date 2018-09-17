package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.OrderStatusRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
public class OrderStatus implements Serializable{
    private static final long serialVersionUID = 1L

    private Long id;
    private String status;//1.创建,2.处理中,3. 已付款,4. 已配送,5. 订单完成,6. 订单取消
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public OrderStatus setId(Long id) {
        this.id = id;
        return this;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getStatus() {
        return status;
    }

    public OrderStatus setStatus(String status) {
        this.status = status;
        return this;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public OrderStatus setDescription(String description) {
        this.description = description;
        return this;
    }

    public static class Enum{
        public static OrderStatus PENDING_PAYMENT_1;
        @Deprecated
        public static OrderStatus HANDLING_2;
        public static OrderStatus PAID_3;
        public static OrderStatus DELIVERED_4;
        public static OrderStatus FINISHED_5;
        public static OrderStatus CANCELED_6;
        public static OrderStatus INSURE_FAILURE_7;
        @Deprecated
        public static OrderStatus EXPIRED_8;
        public static OrderStatus REFUNDED_9;
        public static OrderStatus REFUNDING_10;
        public static Iterable<OrderStatus> ALL;

        public static FLOW = [:]  //定义状态流转关系，key表示当前状态，value是上游状态的数组，比如FLOW[CANCELED_6] = [PENDING_PAYMENT_1, INSURE_FAILURE_7]表示CREATED和INSURE_FAILURE可以流转到CANCELED

        static {

            ALL = RuntimeUtil.loadEnum(OrderStatusRepository, OrderStatus, Enum)

            FLOW[CANCELED_6] = [PENDING_PAYMENT_1, INSURE_FAILURE_7]
            FLOW[REFUNDED_9] = [PAID_3, DELIVERED_4, FINISHED_5]
        }

        public static boolean isInsureFailure(OrderStatus orderStatus){
            INSURE_FAILURE_7 == orderStatus
        }


        public static List<OrderStatus> allAvailable(){
            [PENDING_PAYMENT_1, PAID_3, HANDLING_2, DELIVERED_4, FINISHED_5, CANCELED_6, INSURE_FAILURE_7 ]
        }

        public static OrderStatus findById(Long id){
            ALL.find{it.id == id}
        }

        public static List<OrderStatus> payableStatus(){
            [PENDING_PAYMENT_1];
        }

        public static List<OrderStatus> immutableStatus() {
            [PAID_3, DELIVERED_4, FINISHED_5, CANCELED_6];
        }


        public static isStatusFlowAllowed(OrderStatus source, OrderStatus target){
            FLOW.containsKey(target) && FLOW.get(target).contains(source)
        }

        public static List<OrderStatus> format(List<String> ids){
           return  ALL.findAll {ids.contains(it.id.toString())}
        }


        public static List<OrderStatus> paidStatus() {
            [PAID_3, DELIVERED_4, FINISHED_5 ]
        }

        public static List<OrderStatus> paidEdStatus(){
            [PAID_3, DELIVERED_4, FINISHED_5, REFUNDED_9, REFUNDING_10 ]
        }

        public static List<OrderStatus> memberCodeStatus(){
            [OrderStatus.Enum.PENDING_PAYMENT_1, OrderStatus.Enum.INSURE_FAILURE_7, OrderStatus.Enum.FINISHED_5]
        }

        public static List<OrderStatus> orderCenterAllAvailable(){
            [PENDING_PAYMENT_1, PAID_3, FINISHED_5, CANCELED_6, INSURE_FAILURE_7, REFUNDED_9, REFUNDING_10 ]
        }

        public static List<Long> allowModiftStatus(){
            [PENDING_PAYMENT_1.getId(),HANDLING_2.getId(),PAID_3.getId()]
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof OrderStatus && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
