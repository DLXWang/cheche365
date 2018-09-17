package com.cheche365.cheche.core.model;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class RolePermission {

    private Long id;
    private Role role;
    private Permission permission;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "role", foreignKey=@ForeignKey(name="FK_ROLE_PERMISSION_REF_ROLE", foreignKeyDefinition="FOREIGN KEY (role) REFERENCES role(id)"))
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @ManyToOne
    @JoinColumn(name = "permission", foreignKey=@ForeignKey(name="FK_ROLE_PERMISSION_REF_PERMISSION", foreignKeyDefinition="FOREIGN KEY (permission) REFERENCES permission(id)"))
    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

}
