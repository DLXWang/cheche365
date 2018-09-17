package com.cheche365.cheche.core.model.abao

import com.cheche365.cheche.core.repository.InsuranceProductTypeRepository
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.*

/**
 * Created by wangjiahuan on 2016/11/16 0016.
 */
@Entity
@JsonIgnoreProperties(["description"])
class InsuranceProductType {

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

    @Column(columnDefinition = "VARCHAR(100)")
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

    public static class Enum {
        public static List<InsuranceProductType> ALL;

        @Autowired
        public Enum(InsuranceProductTypeRepository productTypeRepository) {
            ALL = productTypeRepository.findAll();
        }
    }

    public static InsuranceProductType findById(Long productTypeId) {
        InsuranceProductType.Enum.ALL.find { productType -> productType.id == productTypeId }
    }

}
