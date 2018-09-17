package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunhuazhong on 2015/7/27.
 */
@Entity
@Table(name="qrcode_channel")
public class QRCodeChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "varchar(10)")
    private String code;
    @Column(columnDefinition = "varchar(20)")
    private String name;
    @Column(columnDefinition = "varchar(20)")
    private String department;
    @Column(columnDefinition = "DATETIME")
    private Date expireTime;
    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    private Double rebate;
    @Column(columnDefinition = "varchar(2000)")
    private String comment;
    @Column(columnDefinition = "DATETIME")
    private Date createTime;
    @Column(columnDefinition = "DATETIME")
    private Date updateTime;
    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_QRCODE_CHANNEL_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    private InternalUser operator;
    @Column(name = "wechat_qrcode", columnDefinition = "bigint(20)")
    private Long wechatQRCode;
    @Column(columnDefinition = "tinyint(1)")
    private boolean disable;//是否失效

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Double getRebate() {
        return rebate;
    }

    public void setRebate(Double rebate) {
        this.rebate = rebate;
    }

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

    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public Long getWechatQRCode() {
        return wechatQRCode;
    }

    public void setWechatQRCode(Long wechatQRCode) {
        this.wechatQRCode = wechatQRCode;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }
}
