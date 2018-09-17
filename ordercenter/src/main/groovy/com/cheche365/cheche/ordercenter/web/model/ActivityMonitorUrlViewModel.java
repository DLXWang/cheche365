package com.cheche365.cheche.ordercenter.web.model;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.model.ActivityMonitorUrl;
import com.cheche365.cheche.manage.common.model.SchedulingJob;

/**
 * Created by wangshaobin on 2017/7/12.
 */
public class ActivityMonitorUrlViewModel {
    private Long id;
    private Long businessActivity;
    private String scope;//岗位
    private String source;//渠道
    private String plan;//计划
    private String unit;//单元
    private String keyword;//关键词
    private String url; //生成链接
    private String tinyUrl;//短链接

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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTinyUrl() {
        return tinyUrl;
    }

    public void setTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

    public static ActivityMonitorUrlViewModel createViewModel(ActivityMonitorUrl url){
        ActivityMonitorUrlViewModel viewModel = new ActivityMonitorUrlViewModel();
        String[] properties = new String[]{"id", "scope", "source", "plan", "unit", "keyword", "url", "tinyUrl"};
        BeanUtil.copyPropertiesContain(url, viewModel, properties);
        viewModel.setBusinessActivity(url.getBusinessActivity().getId());
        return viewModel;
    }
}
