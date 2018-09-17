package com.cheche365.cheche.core.model;

import javax.persistence.*;

@Entity
public class InternalUserRole {

    private Long id;
    private InternalUser internalUser;
    private Role role;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "internalUser", foreignKey=@ForeignKey(name="FK_INTERNAL_USER_ROLE_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (internal_user) REFERENCES internal_user(id)"))
    public InternalUser getInternalUser() {
        return internalUser;
    }

    public void setInternalUser(InternalUser internalUser) {
        this.internalUser = internalUser;
    }

    @ManyToOne
    @JoinColumn(name = "role", foreignKey=@ForeignKey(name="FK_INSURANCE_USER_ROLE_REF_ROLE", foreignKeyDefinition="FOREIGN KEY (role) REFERENCES role(id)"))
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
