package com.cheche365.cheche.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class DisplayMessage implements Serializable{

    private static final long serialVersionUID = -3706714826651453507L;
    private Long id;
    private MessageType messageType;
    private String title;
    private String name;
    private String url;
    private String iconUrl;
    private String description;
    private Integer weight;
    private String version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(400)")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(columnDefinition = "VARCHAR(400)")
    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Column(columnDefinition = "VARCHAR(400)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String descripton) {
        this.description = descripton;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "message_type", foreignKey = @ForeignKey(name = "fk_display_message_ref_message_type", foreignKeyDefinition = "FOREIGN KEY (message_type) REFERENCES message_type(id)"))
    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @JsonIgnore
    @Column(length = 10)
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Transient
    public String getType() {
        return messageType == null ? null : messageType.getType();
    }

    @Column
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

