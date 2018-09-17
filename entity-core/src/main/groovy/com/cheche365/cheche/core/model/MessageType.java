package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.MessageTypeRepository;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaozhong on 2015/9/1.
 */

@Entity
@JsonIgnoreProperties(ignoreUnknown = false)
public class MessageType implements Serializable{

    private static final long serialVersionUID = 5921541608803792740L;
    private Long id;
    private String type;
    private String description;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(10)")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(columnDefinition = "VARCHAR(400)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Component
    public static class Enum {

        public static MessageType COMPANY;
        public static MessageType COUNT;
        public static MessageType TK_DISCOUNT;
        public static MessageType A_HOME_TOP_IMAGE;

        @Autowired
        public Enum(MessageTypeRepository messageTypeRepository) {
            COMPANY = messageTypeRepository.findFirstByType("COMPANY");
            COUNT = messageTypeRepository.findFirstByType("COUNT");
            TK_DISCOUNT = messageTypeRepository.findFirstByType("TK_DISCOUNT");
            A_HOME_TOP_IMAGE = messageTypeRepository.findFirstByType("A_HOME_TOP_IMAGE");
        }

    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj ) {
            return true;
        }

        if (obj == null|| !getClass().equals(obj.getClass())) {
            return false;
        }
        MessageType messageType = (MessageType) obj;
        return this.id == messageType.id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
