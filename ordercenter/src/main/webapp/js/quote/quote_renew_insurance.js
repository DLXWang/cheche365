/**
 * Created by wangshaobin on 2017/2/10.
 */

function RenewInsurance(source,sourceId){
    this.source=source;
    this.sourceId=sourceId;
}

RenewInsurance.prototype={
    getQuoteInfo : function (callBackMethod) {
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/quote/renewInsurance/getPurchaseOrderById",
            {
                orderId : this.sourceId
            },
            function (data) {
                var order = eval(data);
                quote.auto = order.auto;
                RenewInsurance.prototype.initParam(data);
                callBackMethod(data);
                RenewInsurance.prototype.initPackageCompany();
            },
            function () {
                popup.mould.popTipsMould(true, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    initParam:function(data){
        quote.licensePlateNo = data.auto.licensePlateNo;
        quote.user.userId = data.applicant.id;
        quote.user.mobile = data.applicant.mobile;
        quote.insured.name = data.auto.owner;
        quote.insured.identity = data.auto.identity;
        quote.insure.name = data.auto.owner;
        quote.insure.identity = data.auto.identity;
        quote.sourceChannel = data.sourceChannel.id;
        quote.owner.name = data.applicant.owner;
        quote.owner.identity = data.applicant.identity;
    },
    initPackageCompany : function(){
        quoteModification.interface.getQuoteModification(
            function(data) {
                if (data.quoteModification) {
                    //预选套餐
                    if (data.quoteModification.insurancePackage) {
                        quoteRenewal.setRenewalInsurancePackage(data.quoteModification.insurancePackage);
                    }
                    //预选保险公司
                    if (data.quoteModification.insuranceCompanyIds) {
                        quote.companies = data.quoteModification.insuranceCompanyIds.split(",");
                        quote.newCompanies = data.quoteModification.insuranceCompanyIds.split(",");
                        quote.action.validItemKindPolicy();
                        RenewInsurance.prototype.setCompanyInfo(sourceData.insuranceCompany);
                        quote.action.toQuote(false);
                    }
                } else {
                    RenewInsurance.prototype.getOrderQuoteRecord();
                }
            },
            function() {
                popup.mould.popTipsMould(true, "获取缓存保险公司及套餐异常", popup.mould.first, popup.mould.error, "", "", null);
            }
        );
    },
    getOrderQuoteRecord : function(){
        common.ajax.getByAjax(true, "POST", "json", "/orderCenter/quote/renewInsurance/getQuoteRecordByOrderId",
            {
                orderId : quote.sourceId
            },
            function (data) {
                RenewInsurance.prototype.setOrderRecordRenewInsurance(data);
            },
            function () {
                popup.mould.popTipsMould(true, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    setOrderRecordRenewInsurance : function(sourceData){
        //预选套餐
        if (sourceData.insurancePackage) {
            quoteRenewal.setRenewalInsurancePackage(sourceData.insurancePackage);
        }
        //预选保险公司
        if (sourceData.insuranceCompany.id) {
            quote.companies.push(sourceData.insuranceCompany.id);
            quote.newCompanies.push(sourceData.insuranceCompany.id);
            quote.action.validItemKindPolicy();
            RenewInsurance.prototype.setCompanyInfo(sourceData.insuranceCompany);
            quote.action.toQuote(false);
        }
    },
    setCompanyInfo: function(insuranceCompany){
        quote.companyUrlMap.put(insuranceCompany.id, common.tools.checkToEmpty(insuranceCompany.websiteUrl));
        quote.companyImgMap.put(insuranceCompany.id, common.tools.checkToEmpty(insuranceCompany.logoUrl));
    },
    getQuote : function(companyId, isRenewal){
        var reqJson = {
            source:           quote.source,
            sourceId:         quote.sourceId,
            userId:           quote.user.userId,
            auto: {
                licensePlateNo:   quote.auto.licensePlateNo ? quote.auto.licensePlateNo : quote.licensePlateNo,
                vinNo:            quote.auto.vinNo ? quote.auto.vinNo : "",
                engineNo:         quote.auto.engineNo ? quote.auto.engineNo : "",
                owner:            quote.auto.owner ? quote.auto.owner : "",
                enrollDate:       quote.auto.enrollDate ? quote.auto.enrollDate : "",
                identity:         quote.auto.identity ? quote.auto.identity : "",
                insuredIdNo:      quote.auto.insuredIdNo ? quote.auto.insuredIdNo : "",
                autoType: {
                    code:         quote.auto.autoType ? (quote.auto.autoType.code ? quote.auto.autoType.code : "") : "",
                    seats:        quote.auto.autoType ? (quote.auto.autoType.seats ? quote.auto.autoType.seats : "") : "",
                    supplementInfo: {
                        autoModel:quote.auto.autoType ? quote.auto.autoType.code : "",
                        commercialStartDate : "",
                        compulsoryStartDate : ""
                    }
                }
            },
            pref: {
                companyIds:       [companyId],
                areaId:           quote.areaId,
                flowType:         quoteRenewal.renewalInsuranceCompany ? (quoteRenewal.renewalInsuranceCompany == companyId ? "2" : "") : (isRenewal ? "2" : "")
            },
            additionalParameters:{
                supplementInfo:{
                    code:
                        quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).autoModel : (quote.auto.autoType ? quote.auto.autoType.code : ""),
                    commercialStartDate: quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).commercialStartDate : "",
                    compulsoryStartDate: quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).compulsoryStartDate : ""
                }
            }
        };
        if (!isRenewal) {
            reqJson.insurancePackage = {
                compulsory:       quote.insurancePackage.compulsory,
                autoTax:          quote.insurancePackage.autoTax,
                thirdPartyAmount: quote.insurancePackage.thirdPartyAmount,
                thirdPartyIop:    quote.insurancePackage.thirdPartyIop,
                damage:           quote.insurancePackage.damage,
                damageIop:        quote.insurancePackage.damageIop,
                theft:            quote.insurancePackage.theft,
                theftIop:         quote.insurancePackage.theftIop,
                engine:           quote.insurancePackage.engine,
                engineIop:        quote.insurancePackage.engineIop,
                glass:            quote.insurancePackage.glass,
                glassTypeId:      quote.insurancePackage.glassType,
                glassType: {
                    id:           quote.insurancePackage.glassType
                },
                driverAmount:     quote.insurancePackage.driverAmount,
                driverIop:        quote.insurancePackage.driverIop,
                passengerAmount:  quote.insurancePackage.passengerAmount,
                passengerIop:     quote.insurancePackage.passengerIop,
                spontaneousLoss:  quote.insurancePackage.spontaneousLoss,
                spontaneousLossIop:quote.insurancePackage.spontaneousLossIop,
                unableFindThirdParty:quote.insurancePackage.unableFindThirdParty,
                designatedRepairShop:quote.insurancePackage.designatedRepairShop,
                scratchAmount:    quote.insurancePackage.scratchAmount,
                scratchIop:       quote.insurancePackage.scratchIop,
                iopTotal:         quote.insurancePackage.iopTotal
            };
        }
        common.ajax.getByAjaxWithJsonAndHeader(true, "post", "json", "/orderCenter/quote/" + companyId, reqJson,
            function(data) {
                isRenewal ? quoteRenewal.getRenewalQuoteResult(data, companyId) : quote.action.getQuoteResult(data, companyId);
            },
            function() {
                if (isRenewal) {
                    quoteRenewal.showRenewalErrorQuoteMsg();
                } else {
                    quote.action.showChangeItemBtn();
                    quote.action.getExceptionQuoteResult(companyId);
                }
            },
            quote.interface.getHeaderMap());

    }
}
