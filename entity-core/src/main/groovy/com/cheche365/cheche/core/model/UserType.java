package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.UserTypeRepository;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.context.ApplicationContext;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by liqiang on 3/31/15.
 */
@Entity
public class UserType implements Serializable {

    private static final long serialVersionUID = -2575132889888493622L;
    private long id;
    private String name; //消费者，代理
    private String description;

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        public static final UserType Customer;

        public static final UserType Agent;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                UserTypeRepository userTypeRepository = applicationContext.getBean(UserTypeRepository.class);
                Customer = userTypeRepository.findOne(1l);
                Agent = userTypeRepository.findOne(2l);
            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "User Type 初始化失败");
            }
        }

        public static boolean isCustomer(UserType userType){
            return (userType == null) || (Customer.getId() == userType.getId());
        }

        public static boolean isAgent(UserType userType){
            return (userType != null) && (Agent.getId() == userType.getId());
        }

    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UserType && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


}
