package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.InstitutionRebateHistory;
import com.cheche365.cheche.core.model.InstitutionRebateHistoryTemp;

import javax.validation.constraints.NotNull;

/**
 * Created by xu.yelong on 2016-06-17.
 */
public class InstitutionHistoryRebateHistoryViewModel {
    private Long id;
    @NotNull
    private Long area;
    private String areaName;
    @NotNull
    private Long insuranceCompany;
    private String companyName;
    @NotNull
    private Double commercialRebate;
    @NotNull
    private Double compulsoryRebate;
    @NotNull
    private Long institution;
    @NotNull
    private String startTime;
    private String endTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCommercialRebate() {
        return commercialRebate;
    }

    public void setCommercialRebate(Double commercialRebate) {
        this.commercialRebate = commercialRebate;
    }

    public Double getCompulsoryRebate() {
        return compulsoryRebate;
    }

    public void setCompulsoryRebate(Double compulsoryRebate) {
        this.compulsoryRebate = compulsoryRebate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getArea() {
        return area;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    public Long getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(Long insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public Long getInstitution() {
        return institution;
    }

    public void setInstitution(Long institution) {
        this.institution = institution;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * organize institutionRebateHistoryData for show
     *
     * @param institutionRebateHistory
     * @return
     * @throws Exception
     */
    public static InstitutionHistoryRebateHistoryViewModel createViewData(InstitutionRebateHistory institutionRebateHistory) {
        InstitutionHistoryRebateHistoryViewModel historyViewModel = new InstitutionHistoryRebateHistoryViewModel();
        historyViewModel.setId(institutionRebateHistory.getId());
        historyViewModel.setCompanyName(institutionRebateHistory.getInsuranceCompany().getName());
        historyViewModel.setAreaName(institutionRebateHistory.getArea().getName());
        historyViewModel.setStartTime(DateUtils.getDateString(institutionRebateHistory.getStartTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        historyViewModel.setEndTime(DateUtils.getDateString(institutionRebateHistory.getEndTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        historyViewModel.setCommercialRebate(institutionRebateHistory.getCommercialRebate());
        historyViewModel.setCompulsoryRebate(institutionRebateHistory.getCompulsoryRebate());
        return historyViewModel;
    }

    public static InstitutionHistoryRebateHistoryViewModel createViewData(InstitutionRebateHistoryTemp institutionRebateHistory) {
        InstitutionHistoryRebateHistoryViewModel historyViewModel = new InstitutionHistoryRebateHistoryViewModel();
        historyViewModel.setId(institutionRebateHistory.getId());
        historyViewModel.setCompanyName(institutionRebateHistory.getInsuranceCompany().getName());
        historyViewModel.setAreaName(institutionRebateHistory.getArea().getName());
        historyViewModel.setStartTime(DateUtils.getDateString(institutionRebateHistory.getStartTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        historyViewModel.setEndTime(DateUtils.getDateString(institutionRebateHistory.getEndTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        historyViewModel.setCommercialRebate(institutionRebateHistory.getCommercialRebate());
        historyViewModel.setCompulsoryRebate(institutionRebateHistory.getCompulsoryRebate());
        return historyViewModel;
    }
}
