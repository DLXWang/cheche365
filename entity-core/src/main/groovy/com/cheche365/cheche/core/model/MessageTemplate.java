package com.cheche365.cheche.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MessageTemplate {

    private Long id;
    private String messageTemplate;
    private String messageConveter;//a java class name to convert message template to a real message

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Column(columnDefinition = "VARCHAR(120)")
    public String getMessageConveter() {
        return messageConveter;
    }

    public void setMessageConveter(String messageConveter) {
        this.messageConveter = messageConveter;
    }

}
