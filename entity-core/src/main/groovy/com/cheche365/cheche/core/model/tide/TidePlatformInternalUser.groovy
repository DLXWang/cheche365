package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.*

/**
 * 潮汐系统: 合约点位
 * Created by yinJianBin on 2018/4/19.
 */
@Entity
class TidePlatformInternalUser implements Serializable {
    private static final long serialVersionUID = 1L

    Long id
    String email
    InternalUser internalUser
    TidePlatform tidePlatform
    Integer status


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    String getEmail() {
        return email
    }

    @ManyToOne
    @JoinColumn(name = "internalUser", foreignKey = @ForeignKey(name = "TIDE_PLATFORM_INTERNAL_USER_REF_INTERNAL_USER_FK", foreignKeyDefinition = "FOREIGN KEY (internalUser) REFERENCES internal_user(id)"))
    InternalUser getInternalUser() {
        return internalUser
    }

    @ManyToOne
    @JoinColumn(name = "tidePlatform", foreignKey = @ForeignKey(name = "TIDE_PLATFORM_INTERNAL_USER_REF_TIDE_PLATFORM_FK", foreignKeyDefinition = "FOREIGN KEY (tidePlatform) REFERENCES tide_platform(id)"))
    TidePlatform getTidePlatform() {
        return tidePlatform
    }

    Integer getStatus() {
        return status
    }
}
