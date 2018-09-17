package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.MobileTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * @Author shanxf
 * @Date 2018/1/15  14:38
 */
@Entity
public class MobileSourceType {

    private Long id;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Component
    public static class Enum {
        public static MobileSourceType PARTNER_URL;

        @Autowired
        public Enum(MobileTypeRepository mobileTypeRepository) {
            PARTNER_URL = mobileTypeRepository.findOne(1L);
        }

    }
}
