package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.MarketingRuleStatusRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id;

/**
 * Created by mahong on 2016/8/15.
 */
@Entity
public class MarketingRuleStatus {

    private Long id;
    private String status;//1.未生效, 2.生效中, 3.已失效
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public MarketingRuleStatus setId(Long id) {
        this.id = id;
        return this;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getStatus() {
        return status;
    }

    public MarketingRuleStatus setStatus(String status) {
        this.status = status;
        return this;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public MarketingRuleStatus setDescription(String description) {
        this.description = description;
        return this;
    }

    public static class Enum {
        public static MarketingRuleStatus PRE_EFFECTIVE_1, EFFECTIVE_2, EXPIRED_3;
        public static Iterable<MarketingRuleStatus> ALL;

        static {
            ALL = RuntimeUtil.loadEnum(MarketingRuleStatusRepository, MarketingRuleStatus, Enum)
        }
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof MarketingRuleStatus && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
