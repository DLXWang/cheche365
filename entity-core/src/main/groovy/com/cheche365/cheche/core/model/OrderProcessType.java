package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.OrderProcessTypeRepository;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;

/**
 * 订单类型表
 * Created by sunhuazhong on 2015/11/13.
 */
@Entity
public class OrderProcessType {
    private Long id;
    private String name;//类型名称
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum {
        // 独立下单方式
        public static final OrderProcessType INDEPENDENCE;
        // 合作下单方式
        public static final OrderProcessType COOPERATION;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                OrderProcessTypeRepository orderProcessTypeRepository = applicationContext.getBean(OrderProcessTypeRepository.class);
                INDEPENDENCE = orderProcessTypeRepository.findFirstByName("独立下单");
                COOPERATION = orderProcessTypeRepository.findFirstByName("合作下单");
            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Order Process Type 初始化失败");
            }
        }
    }
}
