package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.RebateChannelRepository;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;

/**
 * 订单佣金渠道，包括代理人，出单机构，保险公司，BD渠道等，
 * 公司的上游（公司给渠道佣金，亏钱）包括代理人、BD渠道，
 * 公司的下游（渠道给公司返点，赚钱）包括出单机构、保险公司
 * Created by sunhuazhong on 2016/5/25.
 */
@Entity
public class RebateChannel {
    private Long id;
    private String name;//佣金渠道名称
    private Integer type;//类型，0-上游，1-下游，默认值为0
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "TINYINT(1)")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum {
        //代理人
        public static RebateChannel REBATE_CHANNEL_AGENT;
        //BD渠道
        public static RebateChannel REBATE_CHANNEL_BD_CHANNEL;
        //出单机构
        public static RebateChannel REBATE_CHANNEL_INSTITUTION;
        //保险公司
        public static RebateChannel REBATE_CHANNEL_INSURANCE_COMPANY;
        //TOA渠道
        public static RebateChannel REBATE_CHANNEL_TOA;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                RebateChannelRepository rebateChannelRepository = applicationContext.getBean(RebateChannelRepository.class);
                REBATE_CHANNEL_AGENT = rebateChannelRepository.findOne(1L);
                REBATE_CHANNEL_BD_CHANNEL = rebateChannelRepository.findOne(2L);
                REBATE_CHANNEL_INSTITUTION = rebateChannelRepository.findOne(3L);
                REBATE_CHANNEL_INSURANCE_COMPANY = rebateChannelRepository.findOne(4L);
                REBATE_CHANNEL_TOA = rebateChannelRepository.findOne(5L);
            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Rebate Channel 初始化失败");
            }
        }
    }

    public static class TYPE {
        public static Integer CHANNEL_UP = 0;
        public static Integer CHANNEL_DOWN = 1;
    }
}
