package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.ordercenter.web.model.InsuranceCompanyData;
import com.cheche365.cheche.ordercenter.web.model.area.AreaViewData;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created by wangfei on 2015/11/13.
 */
public class InstitutionViewModel {
    private Long id;
    @NotNull
    private String name;//出单机构名称
    private String comment;//备注
    @NotNull
    private String contactName;//机构联系人姓名
    @NotNull
    private String contactMobile;//机构联系人手机号
    private String contactEmail;//机构联系人邮箱
    private String contactQq;//机构联系人QQ
    @NotNull
    private String checheName;//车车责任人姓名
    @NotNull
    private String checheMobile;//车车责任人手机号
    @NotNull
    private String checheEmail;//车车责任人邮箱
    private String checheQq;//车车责任人QQ
    private Boolean enable;//启用禁用标记
    private String createTime;//创建时间
    private String updateTime;//修改时间
    private String operator;//操作人
    private List<AreaViewData> areaList;
    private List<InsuranceCompanyData> insuranceCompanyList;
    private List<InstitutionBankAccountViewModel> bankAccountViewModelList;
    private List<InstitutionRebateViewModel> rebateViewModelList;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactQq() {
        return contactQq;
    }

    public void setContactQq(String contactQq) {
        this.contactQq = contactQq;
    }

    public String getChecheName() {
        return checheName;
    }

    public void setChecheName(String checheName) {
        this.checheName = checheName;
    }

    public String getChecheMobile() {
        return checheMobile;
    }

    public void setChecheMobile(String checheMobile) {
        this.checheMobile = checheMobile;
    }

    public String getChecheEmail() {
        return checheEmail;
    }

    public void setChecheEmail(String checheEmail) {
        this.checheEmail = checheEmail;
    }

    public String getChecheQq() {
        return checheQq;
    }

    public void setChecheQq(String checheQq) {
        this.checheQq = checheQq;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<AreaViewData> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<AreaViewData> areaList) {
        this.areaList = areaList;
    }

    public List<InsuranceCompanyData> getInsuranceCompanyList() {
        return insuranceCompanyList;
    }

    public void setInsuranceCompanyList(List<InsuranceCompanyData> insuranceCompanyList) {
        this.insuranceCompanyList = insuranceCompanyList;
    }

    public List<InstitutionBankAccountViewModel> getBankAccountViewModelList() {
        return bankAccountViewModelList;
    }

    public void setBankAccountViewModelList(List<InstitutionBankAccountViewModel> bankAccountViewModelList) {
        this.bankAccountViewModelList = bankAccountViewModelList;
    }

    public List<InstitutionRebateViewModel> getRebateViewModelList() {
        return rebateViewModelList;
    }

    public void setRebateViewModelList(List<InstitutionRebateViewModel> rebateViewModelList) {
        this.rebateViewModelList = rebateViewModelList;
    }

    // 获取出单机构的基本信息，一般是在选择中使用
    public static InstitutionViewModel createViewModel(Institution institution) {
        if (null == institution) {
            return null;
        }
        InstitutionViewModel model = new InstitutionViewModel();
        model.setId(institution.getId());
        model.setName(institution.getName());
        return model;
    }

    // 获取出单机构的基本信息，一般是在选择中使用
    public static InstitutionViewModel createViewModel(InstitutionTemp institutionTemp) {
        if (null == institutionTemp) {
            return null;
        }
        InstitutionViewModel model = new InstitutionViewModel();
        model.setId(institutionTemp.getId());
        model.setName(institutionTemp.getName());
        return model;
    }

    // 获取出单机构的详细信息，一般是在详情中使用
    public static InstitutionViewModel createDetailViewModel(Institution institution) {
        if (null == institution) {
            return null;
        }
        InstitutionViewModel viewModel = new InstitutionViewModel();
        viewModel.setId(institution.getId());
        viewModel.setEnable(institution.getEnable());
        viewModel.setName(institution.getName());
        viewModel.setComment(institution.getComment());
        String[] properties = new String[]{
            "id", "name", "enable", "comment",
            "contactName", "contactMobile", "contactEmail", "contactQq",
            "checheName", "checheMobile", "checheEmail", "checheQq"
        };
        BeanUtil.copyPropertiesContain(institution, viewModel, properties);
        viewModel.setCreateTime(DateUtils.getDateString(
            institution.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(
            institution.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOperator(institution.getOperator() == null ? "" : institution.getOperator().getName());//操作人
        if (CollectionUtils.isNotEmpty(institution.getBankAccountList())) {
            List<InstitutionBankAccountViewModel> bankAccountViewModelList = new ArrayList<>();
            for (InstitutionBankAccount institutionBankAccount : institution.getBankAccountList()) {
                bankAccountViewModelList.add(InstitutionBankAccountViewModel.createViewModel(institutionBankAccount));
            }
            viewModel.setBankAccountViewModelList(bankAccountViewModelList);
        }
        if (CollectionUtils.isNotEmpty(institution.getRebateList())) {
            Map<Long, AreaViewData> areaViewDataMap = new HashMap<>();
            Map<Long, InsuranceCompanyData> insuranceCompanyDataMap = new HashMap<>();
            List<InstitutionRebateViewModel> rebateViewModelList = new ArrayList<>();
            for (InstitutionRebate institutionRebate : institution.getRebateList()) {
                if (areaViewDataMap.get(institutionRebate.getArea().getId()) == null) {
                    areaViewDataMap.put(institutionRebate.getArea().getId(),
                        AreaViewData.createViewModel(institutionRebate.getArea()));
                }
                if (insuranceCompanyDataMap.get(institutionRebate.getInsuranceCompany().getId()) == null) {
                    insuranceCompanyDataMap.put(institutionRebate.getInsuranceCompany().getId(),
                        InsuranceCompanyData.createViewModel(institutionRebate.getInsuranceCompany()));
                }
                rebateViewModelList.add(InstitutionRebateViewModel.createViewModel(institutionRebate));
            }
            viewModel.setAreaList(new ArrayList<>(areaViewDataMap.values()));
            viewModel.setInsuranceCompanyList(new ArrayList<>(insuranceCompanyDataMap.values()));
            viewModel.setRebateViewModelList(rebateViewModelList);
        }
        return viewModel;
    }

    // 获取出单机构的详细信息，一般是在详情中使用
    public static InstitutionViewModel createDetailViewModel(InstitutionTemp institutionTemp) {
        if (null == institutionTemp) {
            return null;
        }
        InstitutionViewModel viewModel = new InstitutionViewModel();
        viewModel.setId(institutionTemp.getId());
        viewModel.setEnable(institutionTemp.getEnable());
        viewModel.setName(institutionTemp.getName());
        viewModel.setComment(institutionTemp.getComment());
        String[] properties = new String[]{
            "id", "name", "enable", "comment",
            "contactName", "contactMobile", "contactEmail", "contactQq",
            "checheName", "checheMobile", "checheEmail", "checheQq"
        };
        BeanUtil.copyPropertiesContain(institutionTemp, viewModel, properties);
        viewModel.setCreateTime(DateUtils.getDateString(
            institutionTemp.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(
            institutionTemp.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOperator(institutionTemp.getOperator() == null ? "" : institutionTemp.getOperator().getName());//操作人
        if (CollectionUtils.isNotEmpty(institutionTemp.getbankAccountTempList())) {
            List<InstitutionBankAccountViewModel> bankAccountViewModelList = new ArrayList<>();
            for (InstitutionBankAccountTemp institutionBankAccount : institutionTemp.getbankAccountTempList()) {
                bankAccountViewModelList.add(InstitutionBankAccountViewModel.createViewModel(institutionBankAccount));
            }
            viewModel.setBankAccountViewModelList(bankAccountViewModelList);
        }
        if (CollectionUtils.isNotEmpty(institutionTemp.getRebateListTemp())) {
            Map<Long, AreaViewData> areaViewDataMap = new HashMap<>();
            Map<Long, InsuranceCompanyData> insuranceCompanyDataMap = new HashMap<>();
            List<InstitutionRebateViewModel> rebateViewModelList = new ArrayList<>();
            for (InstitutionRebateTemp institutionRebateTemp : institutionTemp.getRebateListTemp()) {
                if (areaViewDataMap.get(institutionRebateTemp.getArea().getId()) == null) {
                    areaViewDataMap.put(institutionRebateTemp.getArea().getId(),
                        AreaViewData.createViewModel(institutionRebateTemp.getArea()));
                }
                if (insuranceCompanyDataMap.get(institutionRebateTemp.getInsuranceCompany().getId()) == null) {
                    insuranceCompanyDataMap.put(institutionRebateTemp.getInsuranceCompany().getId(),
                        InsuranceCompanyData.createViewModel(institutionRebateTemp.getInsuranceCompany()));
                }
                rebateViewModelList.add(InstitutionRebateViewModel.createViewModel(institutionRebateTemp));
            }
            viewModel.setAreaList(new ArrayList<>(areaViewDataMap.values()));
            viewModel.setInsuranceCompanyList(new ArrayList<>(insuranceCompanyDataMap.values()));
            viewModel.setRebateViewModelList(rebateViewModelList);
        }
        return viewModel;
    }
}
