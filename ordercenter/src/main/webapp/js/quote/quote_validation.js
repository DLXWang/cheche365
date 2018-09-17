/**
 * Created by wangfei on 2015/11/5.
 */
var quoteValidation = {
    /**
     * 险种校验
     * @param insurancePackage
     */
    validItemKind: function(insurancePackage) {
        var flag = true, msg = "";
        if (!insurancePackage.damage) {
            var shortName = "";
            if (insurancePackage.glass) {
                shortName = '玻璃单独破碎险';
                flag = false;
            } else if (insurancePackage.scratchAmount > 0) {
                shortName = '车身划痕损失险';
                flag = false;
            } else if (insurancePackage.spontaneousLoss) {
                shortName = '自燃损失险';
                flag = false;
            } else if (insurancePackage.engine) {
                shortName = '发动机特别损失险';
                flag = false;
            }
            if (!flag) {
                msg = "不投保车损险无法投保" + shortName;
            }
        }
        return {flag: flag, msg: msg};
    },
    validQuoteInitParams: function(quote) {
        var flag = true, msg = "";
        if (!quote.user.userId) {
            flag = false;
            msg = "无用户信息，无法继续报价";
        }
        return {flag: flag, msg: msg};
    },
    /**
     * 发送短信以及提交订单时需要先保存报价
     * @param companyId
     * @returns {{flag: boolean, msg: string}}
     */
    validQuoteCommit: function(companyId) {
        var btnTd = quoteResult.dom.getInsuranceCompanyBtnDom(companyId);
        if (btnTd.find(".toSavePd").is(":visible")) {
            return {flag: false, msg: "请先保存报价再进行其他操作"};
        }
        return {flag: true, msg: ""};
    },
    /**
     * 校验是否有调整险种保费
     * @param cacheData
     * @param companyId
     * @returns {boolean}
     */
    validPremiumChange: function(cacheData, companyId) {
        var premiumStr="",premiumStrPd="";

        //老的保费
        if (cacheData && cacheData.quoteRecord) {
            var result = quoteResult.result_200.setResult(JSON.parse(cacheData.quoteRecord));
            var itemNames = ["compulsory", "autoTax", "thirdPartyPremium", "damagePremium", "driverPremium", "passengerPremium", "enginePremium", "glassPremium",
                "scratchPremium", "theftPremium", "spontaneousLossPremium", "iop"];
            for (var i=0; i<itemNames.length; i++) {
                premiumStr = (premiumStr ? premiumStr + "-" : "") + common.tools.formatMoney(result[itemNames[i]], 2);
            }
        }

        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
        var itemNamesPd = ["compulsoryPremiumPd", "autoTaxPremiumPd", "thirdPartyPremiumPd", "damagePremiumPd", "driverPremiumPd", "passengerPremiumPd", "enginePremiumPd",
            "glassPremiumPd", "scratchPremiumPd", "theftPremiumPd", "spontaneousLossPremiumPd", "iopPremiumPd"];
        for (var j=0; j<itemNamesPd.length; j++) {
            premiumStrPd = (premiumStrPd ? premiumStrPd + "-" : "") + common.tools.formatMoney(itemTd.find("#" + itemNamesPd[j]).val(), 2);
        }

        return premiumStr == premiumStrPd;
    },
    /**
     * 校验保费下限
     * @param $premium
     * @param premiumBak
     * @returns {boolean}
     */
    validPremiumRange: function($premium, premiumBak) {
        if (!common.permission.hasPermission("or0109") && $premium.val() < premiumBak*parseFloat(quoteModification.premiumLowPercent)) {
            console.log($premium + "保费超过下限");
            popup.mould.popTipsMould(true, "您修改的价格下线超过规定的" + (quoteModification.premiumLowPercent * 100).toFixed(0) + "%" + "，请再重新调整价格", popup.mould.first, popup.mould.warning, "好", "",
                function() {
                    $premium.focus();
                    popup.mask.hideFirstMask(true);
                }
            );
            return false;
        }
        return true;
    },
    /**
     * 对所有险种校验保费下限
     * @param insuranceResult
     * @param companyId
     * @returns {boolean}
     */
    validAllPremiumRange: function(insuranceResult, companyId) {
        var itemNames = ["compulsory", "autoTax", "thirdParty", "damage", "scratch", "driver", "passenger",
            "theft", "spontaneousLoss", "glass", "engine", "iop"];
        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
        for (var index=0; index<itemNames.length; index++) {
            var name = itemNames[index];
            if ("compulsory,autoTax,iop".indexOf(name) > -1) {
                if (insuranceResult[name] && insuranceResult[name] > 0) {
                    if (!quoteValidation.validPremiumRange(itemTd.find("#" + name + "PremiumPd"), insuranceResult[name])) {
                        console.log(name + "PremiumPd保费超过下限");
                        return false;
                    }
                }
            } else {
                if (insuranceResult[name + "Premium"] && insuranceResult[name + "Premium"] > 0) {
                    if (!quoteValidation.validPremiumRange(itemTd.find("#" + name + "PremiumPd"), insuranceResult[name + "Premium"])) {
                        console.log(name + "PremiumPd保费超过下限");
                        return false;
                    }
                }
            }
        }
        return true;
    },
    /**
     * 校验电销自定义优惠幅度
     * @param $percent
     * @returns {boolean}
     */
    validPreferentialRange: function($percent,type,item) {
        if($percent.val()){
            var result=true;
            if(type==orderPremium.preferentialPercent&& $percent.val()/100 >= orderPremium.preferentialLowPercent){
                result=false;
            }else{
                if(item=="Commercial"&& $percent.val()>orderPremium.premiumJson.commercial/2){
                    result=false;
                }else if(item=="Compulsory"&& $percent.val()>orderPremium.premiumJson.compulsory/2){
                    result=false;
                }
            }
        }
        if(!result){
            popup.mould.popTipsMould(true, "您优惠的幅度最高不能超过规定的" + (orderPremium.preferentialLowPercent * 100).toFixed(0) + "%" + "，请再重新调整优惠幅度", popup.mould.first, popup.mould.warning, "好的", "",
                function() {
                    popup.mask.hideFirstMask(true);
                }
            );
        }
        return result;
    }
};
