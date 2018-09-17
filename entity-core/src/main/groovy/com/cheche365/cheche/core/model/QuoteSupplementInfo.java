package com.cheche365.cheche.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by mahong on 2015/12/21.
 */
@Entity
public class QuoteSupplementInfo {

    private Long id;
    private Auto auto;
    private QuoteRecord quoteRecord;
    private String fieldPath;
    private String value;
    private Date createTime;
    private Date updateTime;
    private String valueName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "auto", foreignKey = @ForeignKey(name = "FK_SUPPLEMENT_INFO_REF_AUTO", foreignKeyDefinition = "FOREIGN KEY (auto) REFERENCES auto(id)"))
    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    @ManyToOne
    @JoinColumn(name = "quoteRecord", foreignKey = @ForeignKey(name = "FK_SUPPLEMENT_INFO_REF_QUOTE_RECORD", foreignKeyDefinition = "FOREIGN KEY (quote_record) REFERENCES quote_record(id)"))
    public QuoteRecord getQuoteRecord() {
        return quoteRecord;
    }

    public void setQuoteRecord(QuoteRecord quoteRecord) {
        this.quoteRecord = quoteRecord;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }
}
