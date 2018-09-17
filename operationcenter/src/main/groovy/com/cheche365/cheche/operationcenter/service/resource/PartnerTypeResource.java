package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.PartnerType;
import com.cheche365.cheche.core.repository.PartnerTypeRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.operationcenter.web.model.partner.PartnerTypeViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/6/16.
 */
@Component
public class PartnerTypeResource extends BaseService<PartnerType, PartnerType> {

    @Autowired
    private PartnerTypeRepository partnerTypeRepository;

    public List<PartnerType> listAll() {
        return super.getAll(partnerTypeRepository);
    }

    public List<PartnerTypeViewModel> createViewData(List<PartnerType> partnerTypeList) {
        if (partnerTypeList == null)
            return null;

        List<PartnerTypeViewModel> viewDataList = new ArrayList<>();
        partnerTypeList.forEach(partnerType -> {
            PartnerTypeViewModel viewData = new PartnerTypeViewModel();
            viewData.setId(partnerType.getId());
            viewData.setName(partnerType.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }
}
