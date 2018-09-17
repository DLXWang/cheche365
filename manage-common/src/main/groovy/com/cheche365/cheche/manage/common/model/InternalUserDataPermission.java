package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.core.model.InternalUser;

import javax.persistence.*;

/**
 * Created by yellow on 2017/6/14.
 * 用户数据权限
 */
@Entity
public class InternalUserDataPermission {
    private Long id;
    private InternalUser internalUser;
    private String entity;
    private String field;
    private String values;
    private String code;
    private String comment;
    private Boolean enable = true;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "internal_user", foreignKey = @ForeignKey(name = "FK_INTERNAL_USER_DATA_PERMISSION_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (internal_user) REFERENCES internal_user(id)"))
    public InternalUser getInternalUser() {
        return internalUser;
    }

    public void setInternalUser(InternalUser internalUser) {
        this.internalUser = internalUser;
    }

    @Column(columnDefinition = "VARCHAR(255)")
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    @Column(columnDefinition = "VARCHAR(255)")
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Column(columnDefinition = "VARCHAR(255)", name = "value")
    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    @Column(columnDefinition = "VARCHAR(255)")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(columnDefinition = "VARCHAR(255)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
