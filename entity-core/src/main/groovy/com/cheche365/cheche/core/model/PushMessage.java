package com.cheche365.cheche.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by liqiang on 7/27/15.
 */
@Entity
public class PushMessage {
    private long id;
    private String messageNo;
    private String title;
    private String body;
    private String data;
    private String sound;
    private String operation;
    private Date createTime;
    private int type;
    private boolean  disable;
    private boolean autoIncrementBadge;

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Column(columnDefinition = "TINYINT(1)")
    public boolean isAutoIncrementBadge() {
        return autoIncrementBadge;
    }

    public void setAutoIncrementBadge(boolean autoIncrementBadge) {
        this.autoIncrementBadge = autoIncrementBadge;
    }

    @Column(columnDefinition = "VARCHAR(10)")
    public String getMessageNo() {
        return messageNo;
    }

    public void setMessageNo(String messageNo) {
        this.messageNo = messageNo;
    }

    @Column(columnDefinition = "INT(11)")
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

