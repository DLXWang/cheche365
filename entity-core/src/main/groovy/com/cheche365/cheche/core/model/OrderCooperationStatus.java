package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.OrderCooperationStatusRepository;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

/**
 * 合作出单状态表
 * Created by sunhuazhong on 2015/11/13.
 */
@Entity
public class OrderCooperationStatus {
    private Long id;
    private String status;//状态
    private String description;//描述

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

    @Column(columnDefinition = "VARCHAR(200)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum {
        // 订单新建
        public static final OrderCooperationStatus CREATED;
        // 已报价待审核
        public static final OrderCooperationStatus QUOTE_NO_AUDIT;
        // 通过审核待结款
        public static final OrderCooperationStatus AUDIT_NO_PAYMENT;
        // 结款完成待出单
        public static final OrderCooperationStatus PAYMENT_NO_INSURANCE;
        // 已出单
        public static final OrderCooperationStatus INSURANCE;
        // 订单完成
        public static final OrderCooperationStatus FINISHED;
        // 订单异常
        public static final OrderCooperationStatus ABNORMITY;
        // 订单退款
        public static final OrderCooperationStatus REFUND;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                OrderCooperationStatusRepository orderCooperationStatusRepository = applicationContext.getBean(OrderCooperationStatusRepository.class);
                CREATED = orderCooperationStatusRepository.findFirstByStatus("订单新建");
                QUOTE_NO_AUDIT = orderCooperationStatusRepository.findFirstByStatus("已报价待审核");
                AUDIT_NO_PAYMENT = orderCooperationStatusRepository.findFirstByStatus("通过审核待结款");
                PAYMENT_NO_INSURANCE = orderCooperationStatusRepository.findFirstByStatus("结款完成待出单");
                INSURANCE = orderCooperationStatusRepository.findFirstByStatus("已出单");
                FINISHED = orderCooperationStatusRepository.findFirstByStatus("订单完成");
                ABNORMITY = orderCooperationStatusRepository.findFirstByStatus("订单异常");
                REFUND = orderCooperationStatusRepository.findFirstByStatus("订单退款");
            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Order Cooperation Status 初始化失败");
            }
        }

        public static OrderCooperationStatus format(Long statusId) {
            for (OrderCooperationStatus status : OrderCooperationStatus.Enum.allAvailable()) {
                if (statusId.equals(status.getId())) {
                    return status;
                }
            }
            return null;
        }

        public static List<OrderCooperationStatus> allAvailable() {
            return Arrays.asList(
                CREATED, QUOTE_NO_AUDIT,
                AUDIT_NO_PAYMENT, PAYMENT_NO_INSURANCE,
                INSURANCE, FINISHED,
                ABNORMITY, REFUND
            );
        }

        public static List<Long> allAvailableIds() {
            return Arrays.asList(
                CREATED.getId(), QUOTE_NO_AUDIT.getId(),
                AUDIT_NO_PAYMENT.getId(), PAYMENT_NO_INSURANCE.getId(),
                INSURANCE.getId(), FINISHED.getId(),
                ABNORMITY.getId(), REFUND.getId()
            );
        }

        public static List<OrderCooperationStatus> getSwitchStatus(OrderCooperationStatus currentStatus) {
            if (null == currentStatus) return null;
            if (CREATED.getId().equals(currentStatus.getId())) {
                return Arrays.asList(ABNORMITY);
            }
            else if (QUOTE_NO_AUDIT.getId().equals(currentStatus.getId())) {
                return Arrays.asList(CREATED, ABNORMITY);
            }
            else if (AUDIT_NO_PAYMENT.getId().equals(currentStatus.getId())) {
                return Arrays.asList(CREATED, QUOTE_NO_AUDIT, ABNORMITY);
            }
            else if (PAYMENT_NO_INSURANCE.getId().equals(currentStatus.getId())) {
                return Arrays.asList(CREATED, QUOTE_NO_AUDIT, AUDIT_NO_PAYMENT, ABNORMITY);
            }
            else if (INSURANCE.getId().equals(currentStatus.getId())) {
                return Arrays.asList(CREATED, QUOTE_NO_AUDIT, AUDIT_NO_PAYMENT, PAYMENT_NO_INSURANCE, ABNORMITY);
            }
            else if (FINISHED.getId().equals(currentStatus.getId())) {
                return Arrays.asList(CREATED, QUOTE_NO_AUDIT, AUDIT_NO_PAYMENT, PAYMENT_NO_INSURANCE, INSURANCE, ABNORMITY);
            }
            else if (REFUND.getId().equals(currentStatus.getId())) {
                return Arrays.asList(ABNORMITY);
            }
            return null;
        }
    }
}
