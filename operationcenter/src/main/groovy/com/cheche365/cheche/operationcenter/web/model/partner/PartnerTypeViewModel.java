package com.cheche365.cheche.operationcenter.web.model.partner;

import javax.validation.constraints.NotNull;

/**
 * 合作商类型
 * Created by sunhuazhong on 2015/8/26.
 */
public class PartnerTypeViewModel {
    @NotNull
    private Long id;
    private String name;//合作商类型，包括媒体类，电商类，金融类，保险类，汽车前市场，汽车后市场，通信类，旅游类，餐饮酒店类，生活娱乐类
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
