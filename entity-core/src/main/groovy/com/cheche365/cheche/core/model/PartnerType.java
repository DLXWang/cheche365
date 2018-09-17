package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.PartnerTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 合作商类型
 * Created by sunhuazhong on 2015/8/25.
 */
@Entity
public class PartnerType implements Serializable{

    private static final long serialVersionUID = 6842042478614645215L;
    private Long id;
    private String name;//合作商类型，包括媒体类，电商类，金融类，保险类，汽车前市场，汽车后市场，通信类，旅游类，餐饮酒店类，生活娱乐类
    private String description;

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

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Component
    public static class Enum{
        //媒体类
        public PartnerType MEDIA;
        //电商类
        public PartnerType ELECTRONIC;
        //金融类
        public PartnerType FINANCE;
        //保险类
        public PartnerType INSURANCE;
        //汽车前市场
        public PartnerType CAR_FRONT_MARKET;
        //汽车后市场
        public PartnerType CAR_BACK_MARKET;
        //通信类
        public PartnerType COMMUNICATION;
        //旅游类
        public PartnerType TOURISM;
        //餐饮酒店类
        public PartnerType DINING_HOTEL;
        //生活娱乐类
        public PartnerType LIFE_ENTERTAINMENT;

        @Autowired
        public Enum(PartnerTypeRepository partnerTypeRepository){
            MEDIA = partnerTypeRepository.findFirstByName("媒体类");
            ELECTRONIC = partnerTypeRepository.findFirstByName("电商类");
            FINANCE = partnerTypeRepository.findFirstByName("金融类");
            INSURANCE = partnerTypeRepository.findFirstByName("保险类");
            CAR_FRONT_MARKET = partnerTypeRepository.findFirstByName("汽车前市场");
            CAR_BACK_MARKET = partnerTypeRepository.findFirstByName("汽车后市场");
            COMMUNICATION = partnerTypeRepository.findFirstByName("通信类");
            TOURISM = partnerTypeRepository.findFirstByName("旅游类");
            DINING_HOTEL = partnerTypeRepository.findFirstByName("餐饮酒店类");
            LIFE_ENTERTAINMENT = partnerTypeRepository.findFirstByName("生活娱乐类");
        }
    }
}
