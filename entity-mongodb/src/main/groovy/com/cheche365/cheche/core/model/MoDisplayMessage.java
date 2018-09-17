package com.cheche365.cheche.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Shanxf on 2017/11/16.
 */
@Document(collection = "moDisplayMessage")
public class MoDisplayMessage implements Serializable{

    private static final long serialVersionUID = -8941941336594181149L;
    @Id
    private String id;
    private List<Map<String,Object>> message;
    private MessageType messageType;
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Map<String, Object>> getMessage() {
        return message;
    }

    public void setMessage(List<Map<String, Object>> message) {
        this.message = message;
    }
}
