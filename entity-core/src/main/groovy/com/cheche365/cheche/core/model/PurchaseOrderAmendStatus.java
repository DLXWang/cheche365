package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.repository.PurchaseOrderAmendStatusRepository;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.context.ApplicationContext;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
@Entity
public class PurchaseOrderAmendStatus implements Serializable{

    private static final long serialVersionUID = -1641685960777889399L;
    private Long id;
    private String name;
    private String description;

    @Id
    public Long getId() {
        return id;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getName() {
        return name;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum{
        public static PurchaseOrderAmendStatus CREATE;
        public static PurchaseOrderAmendStatus CANCEL;
        public static PurchaseOrderAmendStatus FINISHED;
        public static Iterable<PurchaseOrderAmendStatus> ALL;

        static{
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            PurchaseOrderAmendStatusRepository PurchaseOrderAmendStatusRepository = applicationContext.
                getBean(PurchaseOrderAmendStatusRepository.class);
            ALL = PurchaseOrderAmendStatusRepository.findAll();
            ALL.forEach((PurchaseOrderAmendStatus)->{
                if(PurchaseOrderAmendStatus.getId()==1l){
                    CREATE = PurchaseOrderAmendStatus;
                }else if(PurchaseOrderAmendStatus.getId()==2l){
                    CANCEL = PurchaseOrderAmendStatus;
                }else if(PurchaseOrderAmendStatus.getId()==3l){
                    FINISHED = PurchaseOrderAmendStatus;
                }
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PurchaseOrderAmendStatus && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
