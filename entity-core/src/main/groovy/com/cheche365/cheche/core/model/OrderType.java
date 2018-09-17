package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.repository.OrderTypeRepository;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class OrderType implements Serializable {

    private static final long serialVersionUID = -2787618606047820022L;
    private Long id;
    // INSURANCE("车险"), CLAIM("理赔"), MAINTAIN("保养"), CARWASH("洗车"), DRIVING("代驾"), REPAIR("维修");
    private String name;//订单类型名称：车险，	理赔，保养，洗车，代价，维修，…
    private String description;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum{
        //车险
        public static OrderType INSURANCE;
        //理赔
        public static OrderType CLAIM;
        //保养
        public static OrderType MAINTAIN;
        //洗车
        public static OrderType CARWASH;
        //代驾
        public static OrderType DRIVING;
        //维修
        public static OrderType REPAIR;
        //支付宝活动
        public static OrderType ACTIVITY;
        //保险
        public static OrderType HEALTH;

        static {
            OrderTypeRepository orderTypeRepository = ApplicationContextHolder.getApplicationContext().getBean(OrderTypeRepository.class);

            INSURANCE = orderTypeRepository.findFirstByName("车险");
            CLAIM = orderTypeRepository.findFirstByName("理赔");
            MAINTAIN = orderTypeRepository.findFirstByName("保养");
            CARWASH = orderTypeRepository.findFirstByName("洗车");
            DRIVING = orderTypeRepository.findFirstByName("代驾");
            REPAIR = orderTypeRepository.findFirstByName("维修");
            ACTIVITY = orderTypeRepository.findOne(7l);
            HEALTH = orderTypeRepository.findFirstByName("保险");
        }

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof OrderType && EqualsBuilder.reflectionEquals(this, obj);
    }
}



