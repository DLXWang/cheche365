package com.cheche365.cheche.core.model;

import javax.persistence.*;

/**
 * Created by sunhuazhong on 2015/5/6.
 */
@Entity
public class InternalUserRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customerUser", foreignKey=@ForeignKey(name="FK_RELATION_CUSTOMER_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (customer_user) REFERENCES internal_user(id)"))
    private InternalUser customerUser;//客服

    @ManyToOne
    @JoinColumn(name = "internalUser", foreignKey=@ForeignKey(name="FK_RELATION_INTERNAL_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (internal_user) REFERENCES internal_user(id)"))
    private InternalUser internalUser;//内勤

    @ManyToOne
    @JoinColumn(name = "externalUser", foreignKey=@ForeignKey(name="FK_RELATION_EXTERNAL_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (external_user) REFERENCES internal_user(id)"))
    private InternalUser externalUser;//外勤

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InternalUser getCustomerUser() {
        return customerUser;
    }

    public void setCustomerUser(InternalUser customerUser) {
        this.customerUser = customerUser;
    }

    public InternalUser getInternalUser() {
        return internalUser;
    }

    public void setInternalUser(InternalUser internalUser) {
        this.internalUser = internalUser;
    }

    public InternalUser getExternalUser() {
        return externalUser;
    }

    public void setExternalUser(InternalUser externalUser) {
        this.externalUser = externalUser;
    }
}
