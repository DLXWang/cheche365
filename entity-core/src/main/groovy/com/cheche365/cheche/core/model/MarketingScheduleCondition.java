package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.MarketingScheduleConditionRepository;
import com.cheche365.cheche.core.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by mahong on 2016/6/2.
 */
@Entity
public class MarketingScheduleCondition {
    private Long id;
    private ScheduleCondition scheduleCondition;
    private Marketing marketing;
    private String channel;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "scheduleCondition", foreignKey = @ForeignKey(name = "FK_MSC_REF_SCHEDULE_CONDITION", foreignKeyDefinition = "FOREIGN KEY (schedule_condition) REFERENCES schedule_condition(id)"))
    public ScheduleCondition getScheduleCondition() {
        return scheduleCondition;
    }

    public void setScheduleCondition(ScheduleCondition scheduleCondition) {
        this.scheduleCondition = scheduleCondition;
    }

    @ManyToOne
    @JoinColumn(name = "marketing", foreignKey = @ForeignKey(name = "FK_MSC_REF_MARKETING", foreignKeyDefinition = "FOREIGN KEY (marketing) REFERENCES marketing(id)"))
    public Marketing getMarketing() {
        return marketing;
    }

    public void setMarketing(Marketing marketing) {
        this.marketing = marketing;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public static class Enum {

        public static final Iterable<MarketingScheduleCondition> ALL_MARKETING_SCHEDULE_CONDITIONS;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                MarketingScheduleConditionRepository repository = applicationContext.getBean(MarketingScheduleConditionRepository.class);
                ALL_MARKETING_SCHEDULE_CONDITIONS = repository.findAll();
            } else {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "marketing_schedule_condition 初始化失败");
            }
        }

        public static ScheduleCondition getScheduleConditionByMarketing(Marketing marketing, Channel channel) {
            if (marketing == null || channel == null || ALL_MARKETING_SCHEDULE_CONDITIONS == null) {
                return null;
            }

            MarketingScheduleCondition defaultMSC = null;
            Iterator<MarketingScheduleCondition> iterator = ALL_MARKETING_SCHEDULE_CONDITIONS.iterator();
            while (iterator.hasNext()) {
                MarketingScheduleCondition msc = iterator.next();
                if (BeanUtil.equalsID(msc.getMarketing(), marketing) && !StringUtils.isBlank(msc.getChannel()) && Arrays.asList(msc.getChannel().split(";")).contains(String.valueOf(channel.getId()))) {
                    return msc.getScheduleCondition();
                }

                if (BeanUtil.equalsID(msc.getMarketing(), marketing) && StringUtils.isBlank(msc.getChannel())) {
                    defaultMSC = msc;
                }
            }
            return (defaultMSC == null) ? null : defaultMSC.getScheduleCondition();
        }
    }

}
