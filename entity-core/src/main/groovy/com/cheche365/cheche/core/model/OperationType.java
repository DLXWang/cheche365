package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.OperationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by mahong on 2016/9/27.
 */
@Entity
public class OperationType implements Serializable {
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

    @Column(columnDefinition = "VARCHAR(20)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Component
    public static class Enum {
        public static OperationType ORDER_CREATE;
        public static OperationType ORDER_UPDATE;
        public static OperationType ORDER_CANCEL;
        public static OperationType ORDER_AMEND;
        public static OperationType ORDER_REFUND;
        public static OperationType FULL_REFUND_CANCEL;

        @Autowired
        public Enum(OperationTypeRepository operationTypeRepository) {
            ORDER_CREATE = operationTypeRepository.findOne(1L);
            ORDER_UPDATE = operationTypeRepository.findOne(2L);
            ORDER_CANCEL = operationTypeRepository.findOne(3L);
            ORDER_AMEND = operationTypeRepository.findOne(4L);
            ORDER_REFUND = operationTypeRepository.findOne(5L);
            FULL_REFUND_CANCEL = operationTypeRepository.findOne(6L);
        }
    }
}
