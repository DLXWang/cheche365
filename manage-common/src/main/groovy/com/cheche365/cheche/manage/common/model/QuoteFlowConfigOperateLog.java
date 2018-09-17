package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.QuoteFlowConfig;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yellow on 2017/7/6.
 * 渠道报价配置操作日志
 */
@Entity
public class QuoteFlowConfigOperateLog {
    private Long id;
    private QuoteFlowConfig quoteFlowConfig;
    private Integer operationType;// 0 上线下线 1 换接入方式 3 覆盖 4
    private Integer operationValue;
    private InternalUser operator;
    private String comment;
    private Date createTime;
    private Date executionTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "quoteFlowConfig", foreignKey = @ForeignKey(name = "FK_QUOTE_FLOW_CONFIG_OPERATE_LOG_REF_QUOTE_FLOW_CONFIG", foreignKeyDefinition = "FOREIGN KEY (quoteFlowConfig) REFERENCES quote_flow_config" +
        "(id)"))
    public QuoteFlowConfig getQuoteFlowConfig() {
        return quoteFlowConfig;
    }

    public void setQuoteFlowConfig(QuoteFlowConfig quoteFlowConfig) {
        this.quoteFlowConfig = quoteFlowConfig;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_QUOTE_FLOW_CONFIG_OPERATE_LOG_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Integer getOperationValue() {
        return operationValue;
    }

    public void setOperationValue(Integer operationValue) {
        this.operationValue = operationValue;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Date executionTime) {
        this.executionTime = executionTime;
    }

    public enum OperationValue {
        OFF_LINE(0, "下线"), ON_LINE(1, "上线");

        private Integer index;
        private String name;

        OperationValue(Integer index, String name) {
            this.index = index;
            this.name = name;
        }

        public Integer getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public static String getName(int index) {
            for (QuoteFlowConfigOperateLog.OperationValue operationValue : QuoteFlowConfigOperateLog.OperationValue.values()) {
                if (operationValue.getIndex() == index) {
                    return operationValue.name;
                }
            }
            return null;
        }
    }

    public enum OperationType{
        ON_OOF_LINE(0,"上下线"),
        ACCESS_MODE(1,"接入方式"),
        EDIT_PAY(2,"修改支付"),
        OVERRIDE(3,"覆盖报价");
        private Integer index;
        private String name;
        OperationType(Integer index, String name) {
            this.index = index;
            this.name = name;
        }
        public Integer getIndex() {
            return index;
        }
        public String getName() {
            return name;
        }
    }
}






