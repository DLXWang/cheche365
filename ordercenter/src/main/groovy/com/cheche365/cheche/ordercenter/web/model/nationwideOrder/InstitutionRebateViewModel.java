package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.core.model.InstitutionRebate;
import com.cheche365.cheche.core.model.InstitutionRebateTemp;
import com.cheche365.cheche.ordercenter.web.model.InsuranceCompanyData;
import com.cheche365.cheche.ordercenter.web.model.area.AreaViewData;

/**
 * Created by sunhuazhong on 2015/11/16.
 */
public class InstitutionRebateViewModel {
    private Long id;
    private AreaViewData areaViewData;
    private InsuranceCompanyData insuranceCompanyData;
    private Long institutionId;
    private String institutionName;
    private Double commercialRebate;//商业险佣金
    private Double compulsoryRebate;//交强险佣金

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AreaViewData getAreaViewData() {
        return areaViewData;
    }

    public void setAreaViewData(AreaViewData areaViewData) {
        this.areaViewData = areaViewData;
    }

    public InsuranceCompanyData getInsuranceCompanyData() {
        return insuranceCompanyData;
    }

    public void setInsuranceCompanyData(InsuranceCompanyData insuranceCompanyData) {
        this.insuranceCompanyData = insuranceCompanyData;
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

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public static InstitutionRebateViewModel createViewModel(InstitutionRebate institutionRebate) {
        if (null == institutionRebate)
            return null;

        InstitutionRebateViewModel viewModel = new InstitutionRebateViewModel();
        viewModel.setId(institutionRebate.getId());
        viewModel.setCommercialRebate(institutionRebate.getCommercialRebate());
        viewModel.setCompulsoryRebate(institutionRebate.getCompulsoryRebate());
        viewModel.setAreaViewData(AreaViewData.createViewModel(institutionRebate.getArea()));
        viewModel.setInsuranceCompanyData(InsuranceCompanyData.createViewModel(institutionRebate.getInsuranceCompany()));
        viewModel.setInstitutionId(institutionRebate.getInstitution().getId());
        viewModel.setInstitutionName(institutionRebate.getInstitution().getName());
        return viewModel;
    }


    public static InstitutionRebateViewModel createViewModel(InstitutionRebateTemp institutionRebate) {
        if (null == institutionRebate)
            return null;

        InstitutionRebateViewModel viewModel = new InstitutionRebateViewModel();
        viewModel.setId(institutionRebate.getId());
        viewModel.setCommercialRebate(institutionRebate.getCommercialRebate());
        viewModel.setCompulsoryRebate(institutionRebate.getCompulsoryRebate());
        viewModel.setAreaViewData(AreaViewData.createViewModel(institutionRebate.getArea()));
        viewModel.setInsuranceCompanyData(InsuranceCompanyData.createViewModel(institutionRebate.getInsuranceCompany()));
        viewModel.setInstitutionId(institutionRebate.getInstitutionTemp().getId());
        viewModel.setInstitutionName(institutionRebate.getInstitutionTemp().getName());
        return viewModel;
    }
}
