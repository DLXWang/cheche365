package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 自定义字段
 * Created by sunhuazhong on 2015/8/25.
 */
@Entity
public class CustomerField {
    private Long id;
    private String name;//自定义字段名称
    private BusinessActivity businessActivity;//商务活动
    private MonitorDataType firstField;//基础字段A
    private MonitorDataType secondField;//基础字段B
    private ArithmeticOperator operator;//运算符

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "businessActivity", foreignKey=@ForeignKey(name="FK_CUSTOMER_FIELD_REF_BUSINESS_ACTIVITY", foreignKeyDefinition="FOREIGN KEY (business_activity) REFERENCES business_activity(id)"))
    public BusinessActivity getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(BusinessActivity businessActivity) {
        this.businessActivity = businessActivity;
    }

    @ManyToOne
    @JoinColumn(name = "firstField", foreignKey=@ForeignKey(name="FK_CUSTOMER_FIELD_REF_FIRST_MONITOR_DATA_TYPE", foreignKeyDefinition="FOREIGN KEY (first_field) REFERENCES monitor_data_type(id)"))
    public MonitorDataType getFirstField() {
        return firstField;
    }

    public void setFirstField(MonitorDataType firstField) {
        this.firstField = firstField;
    }

    @ManyToOne
    @JoinColumn(name = "secondField", foreignKey=@ForeignKey(name="FK_CUSTOMER_FIELD_REF_SECOND_MONITOR_DATA_TYPE", foreignKeyDefinition="FOREIGN KEY (second_field) REFERENCES monitor_data_type(id)"))
    public MonitorDataType getSecondField() {
        return secondField;
    }

    public void setSecondField(MonitorDataType secondField) {
        this.secondField = secondField;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_CUSTOMER_FIELD_REF_ARITHMETIC_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES arithmetic_operator(id)"))
    public ArithmeticOperator getOperator() {
        return operator;
    }

    public void setOperator(ArithmeticOperator operator) {
        this.operator = operator;
    }
}
