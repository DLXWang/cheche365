package com.cheche365.cheche.core.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Created by wangshaobin on 2016/7/22.
 */
@Entity
public class AreaInsuranceTimeLimit {
    private Long id;
    private Area area;//城市
    private Integer days;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_AREA_INSURANCE_TIME_LIMIT_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (`area`) REFERENCES `area` (`id`)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Column
    public Integer getDays() {
        return days
    }

    public void setDays(Integer days) {
        this.days = days
    }
}
