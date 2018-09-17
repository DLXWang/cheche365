package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.ResourceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * Created by wangfei on 2015/9/11.
 */
@Entity
public class ResourceType {
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
    public static class Enum{
        //菜单
        public static ResourceType RESOURCE_MENU;
        //操作
        public static ResourceType RESOURCE_OPERATION;
        //方法
        public static ResourceType RESOURCE_METHOD;
        //数据
        public static ResourceType RESOURCE_DATA;

        @Autowired
        public Enum(ResourceTypeRepository resourceTypeRepository){
            RESOURCE_MENU = resourceTypeRepository.findFirstByName("菜单");
            RESOURCE_OPERATION = resourceTypeRepository.findFirstByName("操作");
            RESOURCE_METHOD = resourceTypeRepository.findFirstByName("方法");
            RESOURCE_DATA = resourceTypeRepository.findFirstByName("数据");
        }
    }
}
