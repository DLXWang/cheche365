package com.cheche365.cheche.manage.common.web.model.sms;


import com.cheche365.cheche.manage.common.web.model.MarketingViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoweifu on 2015/10/13.
 */
public class SqlParameterViewModel {

    private long id;
    private String code;
    private String name;
    private String type;
    private String placeholder;
    private Integer length;
    private List<MarketingViewModel> marketingViewModelList = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public List<MarketingViewModel> getMarketingViewModelList() {
        return marketingViewModelList;
    }

    public void setMarketingViewModelList(List<MarketingViewModel> marketingViewModelList) {
        this.marketingViewModelList = marketingViewModelList;
    }
}
