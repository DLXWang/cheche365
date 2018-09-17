package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.PermissionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * Created by wangfei on 2015/9/11.
 */
@Entity
public class PermissionType {
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
        public static PermissionType PERMISSION_PC;

        @Autowired
        public Enum(PermissionTypeRepository permissionTypeRepository) {
            PERMISSION_PC = permissionTypeRepository.findFirstByName("WEB");
        }
    }
}
