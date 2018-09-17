package com.cheche365.cheche.manage.common.service;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.GlassType;
import com.cheche365.cheche.core.model.InsurancePackage;
import com.cheche365.cheche.core.repository.InsurancePackageRepository;
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xu.yelong on 2016/11/18.
 */
@Service
public class OrderInsurancePackageService {

    @Autowired
    private InsurancePackageRepository insurancePackageRepository;

    public InsurancePackage createInsurancePackage(OrderReverse model) {
        InsurancePackage insurancePackage = new InsurancePackage();

        if (DoubleUtils.moreThanZero(model.getCompulsoryPremium())) {
            insurancePackage.setCompulsory(true);
            insurancePackage.setAutoTax(DoubleUtils.moreThanZero(model.getAutoTax() ));
        } else {
            insurancePackage.setCompulsory(false);
            insurancePackage.setAutoTax(false);
        }
        if (DoubleUtils.moreThanZero(model.getThirdPartyPremium())) {
            insurancePackage.setThirdPartyAmount(model.getThirdPartyAmount());
            insurancePackage.setThirdPartyIop(DoubleUtils.moreThanZero(model.getThirdPartyIop()) || DoubleUtils.moreThanZero(model.getIop() ));
        } else {
            insurancePackage.setThirdPartyAmount(null);
            insurancePackage.setThirdPartyIop(false);
        }
        if (DoubleUtils.moreThanZero(model.getDamagePremium())) {
            insurancePackage.setDamage(true);
            insurancePackage.setDamageIop(DoubleUtils.moreThanZero(model.getDamageIop())  || DoubleUtils.moreThanZero(model.getIop() ));
        } else {
            insurancePackage.setDamage(false);
            insurancePackage.setDamageIop(false);
        }
        if (DoubleUtils.moreThanZero(model.getTheftPremium())) {
            insurancePackage.setTheft(true);
            insurancePackage.setTheftIop(DoubleUtils.moreThanZero(model.getTheftIop() ) || DoubleUtils.moreThanZero(model.getIop() ));
        } else {
            insurancePackage.setTheft(false);
            insurancePackage.setTheftIop(false);
        }
        if (DoubleUtils.moreThanZero(model.getEnginePremium())) {
            insurancePackage.setEngine(true);
            insurancePackage.setEngineIop(DoubleUtils.moreThanZero(model.getEngineIop() )|| DoubleUtils.moreThanZero(model.getIop() ));
        } else {
            insurancePackage.setEngine(false);
            insurancePackage.setEngineIop(false);
        }
        if (DoubleUtils.moreThanZero(model.getGlassPremium())) {
            insurancePackage.setGlass(true);
            insurancePackage.setGlassType(GlassType.Enum.findById(model.getGlassType()));
        } else {
            insurancePackage.setGlass(false);
            insurancePackage.setGlassType(null);
        }
        if (DoubleUtils.moreThanZero(model.getDriverPremium())) {
            insurancePackage.setDriverAmount(model.getDriverAmount());
            insurancePackage.setDriverIop(DoubleUtils.moreThanZero(model.getDriverIop() )||DoubleUtils.moreThanZero( model.getIop() ));
        } else {
            insurancePackage.setDriverAmount(null);
            insurancePackage.setDriverIop(false);
        }
        if (DoubleUtils.moreThanZero(model.getPassengerPremium())) {
            insurancePackage.setPassengerAmount(model.getPassengerAmount());
            insurancePackage.setPassengerIop(DoubleUtils.moreThanZero(model.getPassengerIop() ) || DoubleUtils.moreThanZero(model.getIop() ));
        } else {
            insurancePackage.setPassengerAmount(null);
            insurancePackage.setPassengerIop(false);
        }
        if (DoubleUtils.moreThanZero(model.getSpontaneousLossPremium())) {
            insurancePackage.setSpontaneousLoss(true);
            insurancePackage.setSpontaneousLossIop(DoubleUtils.moreThanZero(model.getSpontaneousLossIop() ) || DoubleUtils.moreThanZero(model.getIop() ));
        }
        if (DoubleUtils.moreThanZero(model.getScratchPremium())) {
            insurancePackage.setScratchAmount(model.getScratchAmount());
            insurancePackage.setScratchIop(DoubleUtils.moreThanZero(model.getScratchIop()) || DoubleUtils.moreThanZero(model.getIop()));
        } else {
            insurancePackage.setScratchAmount(null);
            insurancePackage.setScratchIop(false);
        }
        insurancePackage.setUnableFindThirdParty(DoubleUtils.moreThanZero(model.getUnableFindThirdPartyPremium()));
        insurancePackage.setDesignatedRepairShop(DoubleUtils.moreThanZero(model.getDesignatedRepairShopPremium()));
        insurancePackage.calculateUniqueString();

        InsurancePackage old = insurancePackageRepository.findFirstByUniqueString(insurancePackage.getUniqueString());
        if (null != old)
            return old;

        return insurancePackageRepository.save(insurancePackage);
    }
}
