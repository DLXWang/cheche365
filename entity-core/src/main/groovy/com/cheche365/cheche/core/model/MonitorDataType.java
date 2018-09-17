package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.MonitorDataTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * 监控数据类型
 * Created by sunhuazhong on 2015/8/25.
 */
@Entity
public class MonitorDataType {
    private Long id;
    private String name;//监控数据类型表，包括PV，UV，注册，试算，提交订单数，提交订单总额，支付订单数，支付订单总额，不含车船税金额，特殊监控
    private String description;
    private boolean showFlag;//是否显示，0-不显示，1-显示

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

    @Column(columnDefinition = "tinyint(1)")
    public boolean isShowFlag() {
        return showFlag;
    }

    public void setShowFlag(boolean showFlag) {
        this.showFlag = showFlag;
    }

    @Component
    public static class Enum{
        //PV
        public static MonitorDataType PV;
        //UV
        public static MonitorDataType UV;
        //注册
        public static MonitorDataType REGISTER;
        //试算
        public static MonitorDataType QUOTE;
        //提交订单数
        public static MonitorDataType SUBMIT_COUNT;
        //提交订单总额
        public static MonitorDataType SUBMIT_AMOUNT;
        //支付订单数
        public static MonitorDataType PAYMENT_COUNT;
        //不含车船税总额
        public static MonitorDataType NO_AUTO_TAX_AMOUNT;
        //支付订单总额
        public static MonitorDataType PAYMENT_AMOUNT;
        //特殊监控
        public static MonitorDataType SPECIAL_MONITOR;
        //预算
        public static MonitorDataType BUDGET;
        //佣金
        public static MonitorDataType REBATE;

        @Autowired
        public Enum(MonitorDataTypeRepository monitorDataTypeRepository){
            PV = monitorDataTypeRepository.findFirstByName("PV");
            UV = monitorDataTypeRepository.findFirstByName("UV");
            REGISTER = monitorDataTypeRepository.findFirstByName("注册");
            QUOTE = monitorDataTypeRepository.findFirstByName("试算");
            SUBMIT_COUNT = monitorDataTypeRepository.findFirstByName("提交订单数");
            SUBMIT_AMOUNT = monitorDataTypeRepository.findFirstByName("提交订单总额");
            PAYMENT_COUNT = monitorDataTypeRepository.findFirstByName("支付订单数");
            PAYMENT_AMOUNT = monitorDataTypeRepository.findFirstByName("支付订单总额");
            NO_AUTO_TAX_AMOUNT = monitorDataTypeRepository.findFirstByName("不含车船税总额");
            SPECIAL_MONITOR = monitorDataTypeRepository.findFirstByName("特殊监控");
            BUDGET = monitorDataTypeRepository.findFirstByName("预算");
            REBATE = monitorDataTypeRepository.findFirstByName("佣金");
        }
    }
}
