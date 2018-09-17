package com.cheche365.cheche.ordercenter.service.quote;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.AutoService;
import com.cheche365.cheche.core.util.AutoUtils;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.ordercenter.model.Preference;
import com.cheche365.cheche.ordercenter.model.QuoteQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangfei on 2015/11/9.
 */
@Service
public class DefaultQuotePhotoService  extends DefaultNormalQuoteService {
    private Logger logger = LoggerFactory.getLogger(DefaultQuotePhotoService.class);

    @Autowired
    private QuotePhotoService quotePhotoService;

    @Autowired
    private AutoService autoService;

    @Override
    public void updateOriginalInfoBeforeQuote(QuoteQuery quoteQuery) {
        super.updateOriginalInfoBeforeQuote(quoteQuery);
        quotePhotoService.updateByAuto(quoteQuery.getSourceId(), quoteQuery.getAuto(), quoteQuery.getPref().getFlowType());
    }

    @Override
    protected void getRequestAuto(QuoteQuery quoteQuery) {
        Auto auto = quoteQuery.getAuto();
        String[] contains = new String[]{"licensePlateNo", "engineNo", "owner", "vinNo", "enrollDate", "identity","identityType"};
        QuotePhoto quotePhoto = quotePhotoService.findById(quoteQuery.getSourceId());
        BeanUtil.copyPropertiesContain(quotePhoto, auto, contains);
        AutoType autoType;
        QuoteQuery.AdditionalParameters parameters;
        if(null != quoteQuery.getAdditionalParameters()){
            parameters=quoteQuery.getAdditionalParameters();
        }else{
            parameters=quoteQuery.new AdditionalParameters();
        }
        if (null != quoteQuery.getAuto().getAutoType()) {
            autoType = quoteQuery.getAuto().getAutoType();
        } else {
            autoType = new AutoType();
        }
        autoType.setCode(quotePhoto.getCode());
        auto.setAutoType(autoType);
        //补充信息
        setSupplementInfo(parameters, quotePhoto.getTransferDate());
        auto.setArea(AutoUtils.getAreaOfAuto(quotePhoto.getLicensePlateNo()));
        if (StringUtils.isEmpty(auto.getIdentity()) ) {
            auto.setIdentity(quotePhoto.getIdentity());
        }
        if(StringUtils.isEmpty(auto.getInsuredIdNo())){
            auto.setInsuredIdNo(quotePhoto.getInsuredIdNo());
        }
//        if (Preference.isRenewal(quoteQuery.getPref().getFlowType())) {
//            auto.setIdentity(quotePhoto.getInsuredIdNo());
//        }
        auto.setLicensePlateNo(quotePhoto.getLicensePlateNo().toUpperCase());
    }

    @Override
    public SupplementInfo getSupplementInfo(Long sourceId) {
        SupplementInfo supplementInfo = new SupplementInfo();
        QuotePhoto quotePhoto = quotePhotoService.findById(sourceId);
        supplementInfo.setTransferDate(quotePhoto.getTransferDate());
        return supplementInfo;
    }
}
