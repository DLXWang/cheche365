package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.QuoteStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class QuoteStatus implements Serializable {

    private static final long serialVersionUID = -8523715436141348578L;
    private Long id;
    private String name;//报价状态：Queued, Processing, Completed, InvalidData
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
    public static class Enum{
        //正在排队
        public QuoteStatus QUEUED;
        //报价进行中
        public QuoteStatus PROCESSING;
        //报价完成
        public QuoteStatus COMPLETED;
        //不可用数据
        public QuoteStatus INVALIDDATA;

        @Autowired
        public Enum(QuoteStatusRepository quoteStatusRepository){
            QUEUED = quoteStatusRepository.findFirstByName("正在排队");
            PROCESSING = quoteStatusRepository.findFirstByName("报价进行中");
            COMPLETED = quoteStatusRepository.findFirstByName("报价完成");
            INVALIDDATA = quoteStatusRepository.findFirstByName("不可用数据");
        }
    }
}
