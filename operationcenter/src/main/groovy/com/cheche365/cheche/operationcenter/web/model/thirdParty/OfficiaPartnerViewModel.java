package com.cheche365.cheche.operationcenter.web.model.thirdParty;

import com.cheche365.cheche.core.model.Partner;

import javax.validation.constraints.NotNull;

public class OfficiaPartnerViewModel {

    @NotNull
    private String name;//合作商名称
    private String comment;//备注
    private Long id;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public static OfficiaPartnerViewModel createViewData(Partner partner){
        OfficiaPartnerViewModel model = new OfficiaPartnerViewModel();
        model.setId(partner.getId());
        model.setName(partner.getName());
        model.setComment(partner.getComment());
        return model;
    }
}
