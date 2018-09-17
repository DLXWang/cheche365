package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.common.util.MobileUtil;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class TelMarketingCenter {
    private Long id;//主键
    private String mobile;//号码
    private Integer priority;//优先级
    private Long processedNumber;//处理次数
    private Date expireTime;//到期日
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private Date triggerTime;//触发时间
    private User user;//用户
    private String userName;//用户名称
    private TelMarketingCenterStatus status;//状态
    private TelMarketingCenterSource source;//来源
    private InternalUser operator;//操作人
    private boolean display;//是否显示  1：显示 0：不显示
    private Date sourceCreateTime;//来源创建时间
    private List<TelMarketingCenterHistory> historyList = new ArrayList<TelMarketingCenterHistory>();

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "telMarketingCenter", fetch = FetchType.EAGER)
    public List<TelMarketingCenterHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<TelMarketingCenterHistory> historyList) {
        this.historyList = historyList;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getSourceCreateTime() {
        return sourceCreateTime;
    }

    public void setSourceCreateTime(Date sourceCreateTime) {
        this.sourceCreateTime = sourceCreateTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(columnDefinition = "TINYINT(2)")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getProcessedNumber() {
        return processedNumber;
    }

    public void setProcessedNumber(Long processedNumber) {
        this.processedNumber = processedNumber;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
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

    @Column(columnDefinition = "DATETIME")
    public Date getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Date triggerTime) {
        this.triggerTime = triggerTime;
    }

    @ManyToOne
    @JoinColumn(name = "user", foreignKey=@ForeignKey(name="FK_TEL_MARKETING_CENTER_REF_USER", foreignKeyDefinition="FOREIGN KEY (user) REFERENCES user(id)"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey=@ForeignKey(name="FK_TEL_MARKETING_CENTER_REF_STATUS", foreignKeyDefinition="FOREIGN KEY (status) REFERENCES tel_marketing_center_status(id)"))
    public TelMarketingCenterStatus getStatus() {
        return status;
    }

    public void setStatus(TelMarketingCenterStatus status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name = "source", foreignKey=@ForeignKey(name="FK_TEL_MARKETING_CENTER_REF_SOURCE", foreignKeyDefinition="FOREIGN KEY (source) REFERENCES tel_marketing_center_source(id)"))
    public TelMarketingCenterSource getSource() {
        return source;
    }

    public void setSource(TelMarketingCenterSource source) {
        this.source = source;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_TEL_MARKETING_CENTER_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "TINYINT(1)")
    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    @Transient
    public String getEncyptMobile(){
        return MobileUtil.getEncyptMobile(mobile);
    }
}
