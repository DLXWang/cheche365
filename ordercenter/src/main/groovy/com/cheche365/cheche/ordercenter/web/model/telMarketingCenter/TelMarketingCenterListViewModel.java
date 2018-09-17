package com.cheche365.cheche.ordercenter.web.model.telMarketingCenter;

import java.util.List;

public class TelMarketingCenterListViewModel {
    private List<TelMarketingCenterViewModel> priorityList;//优先处理列表
    private List<TelMarketingCenterViewModel> normalList;//正常处理列表

    public List<TelMarketingCenterViewModel> getPriorityList() {
        return priorityList;
    }

    public void setPriorityList(List<TelMarketingCenterViewModel> priorityList) {
        this.priorityList = priorityList;
    }

    public List<TelMarketingCenterViewModel> getNormalList() {
        return normalList;
    }

    public void setNormalList(List<TelMarketingCenterViewModel> normalList) {
        this.normalList = normalList;
    }
}
