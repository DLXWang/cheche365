package com.cheche365.cheche.core.model.abao

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.repository.RelationshipRepository
import org.springframework.context.ApplicationContext

import javax.persistence.*

/**
 * Created by mahong on 2016/12/30.
 */
@Entity
class Relationship {
    private Long id;
    private String name;//关系：本人，父母，配偶，子女，兄弟，姊妹，朋友
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
        //本人
        public static Relationship SELF;
        //父母
        public static Relationship PARENT;
        //配偶
        public static Relationship SPOUSE;
        //子女
        public static Relationship CHILD;
        //兄弟
        public static Relationship BROTHER;
        //姊妹
        public static Relationship SISTER;
        //朋友
        public static Relationship FRIEND;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                RelationshipRepository RelationshipRepository = applicationContext.getBean(RelationshipRepository.class);
                SELF = RelationshipRepository.findFirstByName("本人");
                PARENT = RelationshipRepository.findFirstByName("父母");
                SPOUSE = RelationshipRepository.findFirstByName("配偶");
                CHILD = RelationshipRepository.findFirstByName("子女");
                BROTHER = RelationshipRepository.findFirstByName("兄弟");
                SISTER = RelationshipRepository.findFirstByName("姊妹");
                FRIEND = RelationshipRepository.findFirstByName("朋友");
            } else {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Relationship 初始化失败");
            }
        }
    }
}
