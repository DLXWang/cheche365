package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.Marketing;
import com.cheche365.cheche.core.repository.MarketingRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.MarketingViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangfei on 2015/6/16.
 */
@Component("opMarketingResource")
public class MarketingResource extends BaseService<Marketing, Marketing> {

    @Autowired
    private MarketingRepository marketingRepository;

    public List<Marketing> listAll() {
        return super.getAll(marketingRepository);
    }

    public List<Marketing> listEnable(String marketingType) {
        Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        return marketingRepository.findByMarketingTypeAndBeginDateLessThanEqualAndEndDateGreaterThanEqual(marketingType, currentTime, currentTime);
    }

    public List<MarketingViewModel> createViewData(List<Marketing> marketingList) {
        if (marketingList == null)
            return null;

        List<MarketingViewModel> viewDataList = new ArrayList<>();
        marketingList.forEach(marketing -> {
            MarketingViewModel viewData = new MarketingViewModel();
            viewData.setId(marketing.getId());
            viewData.setName(marketing.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }
}
