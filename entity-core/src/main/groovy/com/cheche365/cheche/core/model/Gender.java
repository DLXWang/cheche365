package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.GenderRepository;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Gender implements Serializable {

    private static final long serialVersionUID = 165927977821518819L;
    private Long id;
    private String name;//性别：男，女
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

    public static class Enum {
        //男
        public static Gender MALE;
        //女
        public static Gender FEMALE;


        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                GenderRepository genderRepository = applicationContext.getBean(GenderRepository.class);
                MALE = genderRepository.findFirstByName("男");
                FEMALE = genderRepository.findFirstByName("女");
            } else {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Gender 初始化失败");
            }
        }

    }
}
