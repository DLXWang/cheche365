package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.InternalUser;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TelMarketingCenterAssignBatch{

    private Long id;//主键
    private TelMarketingCenterAssignBatch parent;//父id
    private InternalUser sourceAssigner;//分配来源
    private InternalUser targetAssigner;//分配目标
    private String channel;//渠道
    private String sourceType;//类型
    private String area;//地区
    private Long assignNum;//分配数量
    private InternalUser operator;//操作者
    private Date createTime;//分配时间
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "parent_id", foreignKey=@ForeignKey(name="FK_TMCAB_REF_TEL_MARKETING_CENTER_ASSIGN_BATCH", foreignKeyDefinition="FOREIGN KEY (parent_id) REFERENCES tel_marketing_center_assign_batch(id)"))
    public TelMarketingCenterAssignBatch getParent() {
        return parent;
    }

    public void setParent(TelMarketingCenterAssignBatch parent) {
        this.parent = parent;
    }

    @ManyToOne
    @JoinColumn(name = "source_assigner", foreignKey=@ForeignKey(name="FK_TMCAB_SOURCE_ASSIGNER_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (source_assigner) REFERENCES internal_user(id)"))
    public InternalUser getSourceAssigner() {
        return sourceAssigner;
    }
     public void setSourceAssigner(InternalUser sourceAssigner) {
        this.sourceAssigner = sourceAssigner;
    }
    @ManyToOne
    @JoinColumn(name = "target_assigner", foreignKey=@ForeignKey(name="FK_TMCAB_TARGET_ASSIGNER_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (target_assigner) REFERENCES internal_user(id)"))
    public InternalUser getTargetAssigner() {
        return targetAssigner;
    }
    public void setTargetAssigner(InternalUser targetAssigner) {
        this.targetAssigner = targetAssigner;
    }

     public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Long getAssignNum() {
        return assignNum;
    }

    public void setAssignNum(Long assignNum) {
        this.assignNum = assignNum;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_TMCAB_OPERATOR_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
