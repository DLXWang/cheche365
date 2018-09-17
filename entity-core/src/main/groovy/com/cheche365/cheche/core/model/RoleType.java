package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.RoleTypeRepository;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;

@Entity
public class RoleType {
    private Long id;
    private String name;//角色类别：User(1), InternalUser(2), Partner(3), Vendor(4)
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

    public static class Enum{
        //普通用户
        public static final RoleType USER;
        //内部用户
        public static final RoleType INTERNAL_USER;
        //外部用户
        public static final RoleType EXTERNAL_USER;
        //合伙人
        public static final RoleType PARTNER;
        //供应商
        public static final RoleType VENDOR;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                RoleTypeRepository roleTypeRepository = applicationContext.getBean(RoleTypeRepository.class);
                USER = roleTypeRepository.findFirstByName("普通用户");
                INTERNAL_USER = roleTypeRepository.findFirstByName("内部用户");
                EXTERNAL_USER = roleTypeRepository.findFirstByName("外部用户");
                PARTNER = roleTypeRepository.findFirstByName("合伙人");
                VENDOR = roleTypeRepository.findFirstByName("供应商");
            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Role Type 初始化失败");
            }
        }
    }
}
