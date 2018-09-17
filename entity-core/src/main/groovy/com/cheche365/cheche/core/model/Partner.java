package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 合作商
 * Created by sunhuazhong on 2015/8/25.
 */
@Entity
public class Partner implements Serializable{

    private static final long serialVersionUID = 5468131490037945732L;
    private Long id;
    private String name;//合作商名称
    private Date cooperationTime;//预计首次合作时间，类型为yyyy-MM-dd
    private PartnerType partnerType;//合作商类型
    private boolean enable = false;//是否启用，0-禁用，1-启用
    private String comment;//备注
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人，关联internal_user表
    private List<CooperationMode> cooperationModes;//合作方式集合

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

    @Column(columnDefinition = "DATE")
    public Date getCooperationTime() {
        return cooperationTime;
    }

    public void setCooperationTime(Date cooperationTime) {
        this.cooperationTime = cooperationTime;
    }

    @ManyToOne
    @JoinColumn(name = "partnerType", foreignKey=@ForeignKey(name="FK_PARTNER_REF_PARTNER_TYPE", foreignKeyDefinition="FOREIGN KEY (partner_type) REFERENCES partner_type(id)"))
    public PartnerType getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(PartnerType partnerType) {
        this.partnerType = partnerType;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
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

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_PARTNER_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @ManyToMany
    @JoinTable(name="partner_cooperation_mode",
        inverseJoinColumns =  @JoinColumn (name =  "cooperation_mode"),
        joinColumns =  @JoinColumn (name =  "partner" ))
    public List<CooperationMode> getCooperationModes() {
        return cooperationModes;
    }

    public void setCooperationModes(List<CooperationMode> cooperationModes) {
        this.cooperationModes = cooperationModes;
    }
}
