package com.cheche365.cheche.operationcenter.web.model.partner;

import javax.validation.constraints.NotNull;

/**
 * Created by sunhuazhong on 2015/8/26.
 */
public class ActivityAreaViewModel {
    private Long id;
    @NotNull
    private Long businessActivity;//商务活动id
    private String businessActivityName;//商务活动名称
    @NotNull
    private Long area;//城市
    private String areaName;//城市名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(Long businessActivity) {
        this.businessActivity = businessActivity;
    }

    public String getBusinessActivityName() {
        return businessActivityName;
    }

    public void setBusinessActivityName(String businessActivityName) {
        this.businessActivityName = businessActivityName;
    }

    public Long getArea() {
        return area;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
