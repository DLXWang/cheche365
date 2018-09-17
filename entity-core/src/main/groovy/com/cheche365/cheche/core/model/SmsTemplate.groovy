package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import org.springframework.context.ApplicationContext

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Created by guoweifu on 2015/10/8.
 */
@Entity
public class SmsTemplate {

    private Long id;
    private String name;
    private String zucpCode;
    private String yxtCode;
    private boolean disable = true;
    private String content;
    private String comment;
    @Column(columnDefinition = "DATETIME")
    private Date createTime;
    @Column (columnDefinition = "DATETIME")
    private Date updateTime;
    private InternalUser operator;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(30)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(30)")
    public String getZucpCode() {
        return zucpCode;
    }

    public void setZucpCode(String zucpCode) {
        this.zucpCode = zucpCode;
    }

    @Column(columnDefinition = "VARCHAR(30)")
    public String getYxtCode() {
        return yxtCode;
    }

    public void setYxtCode(String yxtCode) {
        this.yxtCode = yxtCode;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Column(columnDefinition = "VARCHAR(1000)")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_SMS_TEMPLATE_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user (id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public static class Enum {
        // 人工报价短信模板
        public static final SmsTemplate CUSTOMER_QUOTE;
        public static final SmsTemplate CUSTOMER_QUOTE_ORDER;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                def smsTemplateRepository = applicationContext.getBean('smsTemplateRepository');
                CUSTOMER_QUOTE = smsTemplateRepository.findFirstByZucpCode("PENDING_021");
                CUSTOMER_QUOTE_ORDER = smsTemplateRepository.findFirstByZucpCode("PENDING_022");
            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Sms Template 初始化失败");
            }
        }
    }
}
