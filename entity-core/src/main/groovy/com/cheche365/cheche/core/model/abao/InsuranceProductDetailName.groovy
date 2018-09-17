package com.cheche365.cheche.core.model.abao

import com.cheche365.cheche.core.context.ApplicationContextHolder
import org.springframework.context.ApplicationContext

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Created by wangjiahuan on 2016/11/16 0016.
 */
@Entity
class InsuranceProductDetailName {

    private Long id;
    private String detailName;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getDetailName() {
        return detailName
    }

    void setDetailName(String detailName) {
        this.detailName = detailName
    }

    @Column(columnDefinition = "VARCHAR(500)")
    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

    public static class Enum {
        public static InsuranceProductDetailName VALID_DAYS;
        static{
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            def insuranceProductDetailNameRepository = applicationContext.getBean('insuranceProductDetailNameRepository');
            VALID_DAYS = insuranceProductDetailNameRepository.findFirstByDetailName("effectiveDate");
        }
    }
}
