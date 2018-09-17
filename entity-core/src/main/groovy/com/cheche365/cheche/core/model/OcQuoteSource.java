package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.OcQuoteSourceRepository;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;

/**
 * Created by wangfei on 2016/5/4.
 */
@Entity
public class OcQuoteSource {
    private Long id;
    private String name;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum {
        //电话报价
        public static OcQuoteSource QUOTE_SOURCE_PHONE;
        //拍照报价
        public static OcQuoteSource QUOTE_SOURCE_PHOTO;
        //好车主
        public static OcQuoteSource QUOTE_SOURCE_PERFECT_DRIVER;
        //订单增补报价
        public static OcQuoteSource QUOTE_SOURCE_ORDER;
        //根据上次报价记录报价
        public static OcQuoteSource QUOTE_SOURCE_RECORD;
        //一键续保
        public static OcQuoteSource QUOTE_SOURCE_RENEW_INSURANCE;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                OcQuoteSourceRepository quoteSourceRepository = applicationContext.getBean(OcQuoteSourceRepository.class);
                QUOTE_SOURCE_PHONE = quoteSourceRepository.findOne(1L);
                QUOTE_SOURCE_PHOTO = quoteSourceRepository.findOne(2L);
                QUOTE_SOURCE_PERFECT_DRIVER = quoteSourceRepository.findOne(3L);
                QUOTE_SOURCE_ORDER=quoteSourceRepository.findOne(4L);
                QUOTE_SOURCE_RECORD=quoteSourceRepository.findOne(5L);
                QUOTE_SOURCE_RENEW_INSURANCE=quoteSourceRepository.findOne(6L);
            } else {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "OcQuoteSource 初始化失败");
            }
        }
    }
}
