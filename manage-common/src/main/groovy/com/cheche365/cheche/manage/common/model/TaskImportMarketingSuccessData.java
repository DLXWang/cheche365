package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Marketing;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;

import javax.persistence.*;

/**
 * Created by xu.yelong on 2016-03-24.
 */
@Entity
public class TaskImportMarketingSuccessData {
    private Long id;
    private String cacheKey;
    private Marketing marketing;
    private TelMarketingCenterSource source;
    private Boolean enable = true;
    private Integer priority;
    private Channel channel;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "cacheKey", columnDefinition = "VARCHAR(100)")
    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    @ManyToOne
    @JoinColumn(name = "marketing", foreignKey = @ForeignKey(name = "FK_TASK_TEL_MARKETING_CENTER_REF_MARKETING", foreignKeyDefinition = "FOREIGN KEY (marketing) REFERENCES marketing(id)"))
    public Marketing getMarketing() {
        return marketing;
    }

    public void setMarketing(Marketing marketing) {
        this.marketing = marketing;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @ManyToOne
    @JoinColumn(name = "source", foreignKey = @ForeignKey(name = "FK_TASK_TEL_MARKETING_CENTER_REF_TEL_MARKETING_CENTER_SOURCE", foreignKeyDefinition = "FOREIGN KEY (source) REFERENCES tel_marketing_center_source(id)"))
    public TelMarketingCenterSource getSource() {
        return source;
    }

    public void setSource(TelMarketingCenterSource source) {
        this.source = source;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_TASK_TEL_MARKETING_CENTER_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES channel(id)"))
    public Channel getChannel() { return channel; }

    public void setChannel(Channel channel) { this.channel = channel; }
}
