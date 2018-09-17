package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.TelMarketingCenterRepeat;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TelMarketingCenterRepeatInfo {
    private Long id;//主键
    private TelMarketingCenterRepeat telMarketingCenterRepeat;
    private String sourceTable;//来源表名
    private String sourceId;//来源id
    private Date createTime;//创建时间
    private Date updateTime;//更新时间

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "repeat_id", foreignKey = @ForeignKey(name = "FK_TEL_MARKETING_CENTER_REPEAT_INFO_REF_TEL_REPEAT", foreignKeyDefinition = "FOREIGN KEY (repeat_id) REFERENCES tel_marketing_center_repeat(id)"))
    public TelMarketingCenterRepeat getTelMarketingCenterRepeat() {
        return telMarketingCenterRepeat;
    }

    public void setTelMarketingCenterRepeat(TelMarketingCenterRepeat telMarketingCenterRepeat) {
        this.telMarketingCenterRepeat = telMarketingCenterRepeat;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Column(columnDefinition = "DATETIME")
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

    @Component
    public static class Enum {
        public static String APPLICATION_LOG = "application_log";
    }
}
