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
public class DefaultQuotePhoneService extends DefaultNormalQuoteService {
    private Logger logger = LoggerFactory.getLogger(DefaultQuotePhoneService.class);

    @Autowired
    private QuotePhoneService quotePhoneService;

    @Autowired
    private AutoService autoService;

    @Override
    public void updateOriginalInfoBeforeQuote(QuoteQuery quoteQuery) {
        super.updateOriginalInfoBeforeQuote(quoteQuery);
        quotePhoneService.updateByAuto(quoteQuery.getSourceId(), quoteQuery.getAuto(), quoteQuery.getPref().getFlowType());
    }

    @Override
    protected void getRequestAuto(QuoteQuery quoteQuery) {
        Auto auto = quoteQuery.getAuto();
        String[] contains = new String[]{"licensePlateNo", "engineNo", "owner", "vinNo", "enrollDate", "identity","identityType"};
        QuotePhone quotePhone = quotePhoneService.getById(quoteQuery.getSourceId());
        BeanUtil.copyPropertiesContain(quotePhone, auto, contains);
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
        autoType.setCode(quotePhone.getCode());
        //补充信息
        setSupplementInfo(parameters, quotePhone.getTransferDate());
        auto.setAutoType(autoType);
//        auto.setAutoTypeExternalCode(quotePhone.getCode());
        auto.setArea(AutoUtils.getAreaOfAuto(quotePhone.getLicensePlateNo()));

        if (StringUtils.isEmpty(auto.getIdentity()) ) {
            auto.setIdentity(quotePhone.getIdentity());
        }
        if(StringUtils.isEmpty(auto.getInsuredIdNo())){
            auto.setInsuredIdNo(quotePhone.getInsuredIdNo());
        }
//        if (Preference.isRenewal(quoteQuery.getPref().getFlowType())) {
//            auto.setIdentity(quotePhone.getInsuredIdNo());
//            //TODO
//        }
        auto.setLicensePlateNo(quotePhone.getLicensePlateNo().toUpperCase());
    }

    @Override
    public SupplementInfo getSupplementInfo(Long sourceId) {
        SupplementInfo supplementInfo = new SupplementInfo();
        QuotePhone quotePhone = quotePhoneService.getById(sourceId);
        supplementInfo.setTransferDate(quotePhone.getTransferDate());
        return supplementInfo;
    }

}
