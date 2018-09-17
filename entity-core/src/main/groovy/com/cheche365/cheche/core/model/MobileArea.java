package com.cheche365.cheche.core.model;


import javax.persistence.*;

/**
 * Created by xu.yelong on 2016/7/15.
 */
@Entity
public class MobileArea {
    private Long id;
    private String mobile;
    private Area area;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_MOBILE_AREA_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (`area`) REFERENCES `area` (`id`)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
