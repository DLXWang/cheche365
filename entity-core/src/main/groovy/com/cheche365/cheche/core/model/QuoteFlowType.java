package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.QuoteFlowTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by mahong on 2015/10/26.
 */
@Entity
public class QuoteFlowType implements Serializable {

    private static final long serialVersionUID = -1772329955933613745L;
    private Long id;
    private String name;//报价通道类型：1：通用流程; 2:续保通道流程
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

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Component
    public static class Enum {
        //通用流程
        public static QuoteFlowType GENERAL;
        //续保流程
        public static QuoteFlowType RENEWAL_CHANNEL;

        @Autowired
        public Enum(QuoteFlowTypeRepository quoteFlowTypeRepository) {
            GENERAL = quoteFlowTypeRepository.findFirstByName("通用流程");
            RENEWAL_CHANNEL = quoteFlowTypeRepository.findFirstByName("续保流程");
        }
    }

    public static QuoteFlowType toQuoteFlowType(Long id) {
        if (Enum.GENERAL.getId().equals(id)) {
            return Enum.GENERAL;
        } else if (Enum.RENEWAL_CHANNEL.getId().equals(id)) {
            return Enum.RENEWAL_CHANNEL;
        } else {
            return null;
        }
    }

}
