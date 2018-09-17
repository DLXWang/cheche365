package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * Created by sunhuazhong on 2015/3/24.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = false)
public class UserAuto extends BaseEntity {

    private static final long serialVersionUID = -982126454379460208L;
    private Auto auto;
    private User user;

    @ManyToOne
    @JoinColumn(name = "auto", foreignKey=@ForeignKey(name="FK_AUTO_USER_REF_AUTO", foreignKeyDefinition="FOREIGN KEY (auto) REFERENCES auto(id)"))
    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    @ManyToOne
    @JoinColumn(name = "user", foreignKey=@ForeignKey(name="FK_AUTO_USER_REF_USER", foreignKeyDefinition="FOREIGN KEY (user) REFERENCES user(id)"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
