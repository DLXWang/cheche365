package com.cheche365.cheche.core.model;

import javax.persistence.*;

/**
 * 商务活动支持的区域
 * Created by sunhuazhong on 2015/8/25.
 */
@Entity
public class ActivityArea {
    private Long id;
    private BusinessActivity businessActivity;//商务活动
    private Area area;//城市

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "businessActivity", foreignKey=@ForeignKey(name="FK_ACTIVITY_AREA_REF_BUSINESS_ACTIVITY", foreignKeyDefinition="FOREIGN KEY (business_activity) REFERENCES business_activity(id)"))
    public BusinessActivity getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(BusinessActivity businessActivity) {
        this.businessActivity = businessActivity;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey=@ForeignKey(name="FK_ACTIVITY_AREA_REF_AREA", foreignKeyDefinition="FOREIGN KEY (area) REFERENCES area(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
