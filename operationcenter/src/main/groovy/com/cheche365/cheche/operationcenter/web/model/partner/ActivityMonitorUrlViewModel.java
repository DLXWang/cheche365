package com.cheche365.cheche.operationcenter.web.model.partner;


import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.manage.common.model.ActivityMonitorUrl;
import com.cheche365.cheche.web.util.UrlUtil;

/**
 * Created by yellow on 2017/6/22.
 */
public class ActivityMonitorUrlViewModel {
    private Long id;
    private String scope;//岗位
    private String source;//渠道
    private String plan;//计划
    private String unit;//单元
    private String keyword;//关键词
    private String url; //生成链接
    private String tinyUrl;//短链接
    private String createTime;//创建时间
    private Boolean enable;//启用状态
    private String referralLink;//推广链接
    private Boolean quote;//是否报价

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getReferralLink() {
        return referralLink;
    }

    public void setReferralLink(String referralLink) {
        this.referralLink = referralLink;
    }

    public Boolean getQuote() {
        return quote;
    }

    public void setQuote(Boolean quote) {
        this.quote = quote;
    }

    public static ActivityMonitorUrlViewModel createViewModel(ActivityMonitorUrl url){
        ActivityMonitorUrlViewModel viewModel = new ActivityMonitorUrlViewModel();
        viewModel.setId(url.getId());
        viewModel.setUrl(UrlUtil.toFullUrl(url.getBusinessActivity().getLandingPage()));
        viewModel.setReferralLink((url.getUrl()== null)? "":UrlUtil.toFullUrl(url.getUrl()));
        viewModel.setScope(url.getScope());
        viewModel.setSource(url.getSource());
        viewModel.setPlan(url.getPlan());
        viewModel.setUnit(url.getUnit());
        viewModel.setKeyword(url.getKeyword());
        viewModel.setCreateTime(DateUtils.getDateString(url.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setQuote(url.getQuote());
        return viewModel;

    }
}
