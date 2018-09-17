package com.cheche365.cheche.operationcenter.web.model.area;

import com.cheche365.cheche.core.model.Area;

import javax.validation.constraints.NotNull;

/**
 * Created by sunhuazhong on 2015/7/21.
 */
public class AreaViewData {
    private Long id;//区域id
    @NotNull
    private String name;//区域名称

    public Long getId() {
        return id;
    }
    public Long type;//区域类型

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public static AreaViewData createViewModel(Area area) {
        if (null == area)
            return null;

        AreaViewData viewData = new AreaViewData();
        viewData.setId(area.getId());
        viewData.setName(area.getName());
        viewData.setType(area.getType().getId());
        return viewData;
    }
}
