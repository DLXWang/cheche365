package com.cheche365.cheche.core.model.abao

import com.cheche365.cheche.core.repository.InsuranceProductStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.*

/**
 * Created by wangjiahuan on 2016/11/16 0016.
 */
@Entity
class InsuranceProductStatus {

    private Long id;
    private String name;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Column(columnDefinition = "VARCHAR(50)")
    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @Column(columnDefinition = "VARCHAR(500)")
    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

    @Component
    public static class Enum {

        public static InsuranceProductStatus CREATE;
        public static InsuranceProductStatus EFFECTIVE;
        public static InsuranceProductStatus INVALID;

        @Autowired
        public Enum(InsuranceProductStatusRepository insuranceProductStatusRepository) {
            CREATE = insuranceProductStatusRepository.findOne(1l);
            EFFECTIVE = insuranceProductStatusRepository.findOne(2l);
            INVALID = insuranceProductStatusRepository.findOne(3l);
        }
    }

}
