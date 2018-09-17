package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.Partner;
import com.cheche365.cheche.core.repository.PartnerRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.operationcenter.web.model.partner.PartnerViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/6/16.
 */
@Component
public class PartnerResource extends BaseService<Partner, Partner> {

    @Autowired
    private PartnerRepository partnerRepository;

    public List<Partner> getAllEnablePartners() {
        return partnerRepository.findByEnable(true);
    }

    public List<PartnerViewModel> createViewData(List<Partner> partnerList) {
        if (partnerList == null)
            return null;

        List<PartnerViewModel> viewDataList = new ArrayList<>();
        partnerList.forEach(partner -> {
            PartnerViewModel viewData = new PartnerViewModel();
            viewData.setId(partner.getId());
            viewData.setName(partner.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }
}
