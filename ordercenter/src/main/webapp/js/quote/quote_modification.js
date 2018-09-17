/**
 * Created by wangfei on 2016/5/3.
 */
var quoteModification = {
    premiumLowPercent: 0.5,
    init: function(callback) {
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
                        quote.action.toQuote(false);
                    }
                } else {
                    callback();
                }
            },
            function() {
                popup.mould.popTipsMould(true, "获取缓存保险公司及套餐异常", popup.mould.first, popup.mould.error, "", "", null);
            }
        );
    },
    setCompareInsurancePackageResult: function(companyId, insurancePackage) {
        quoteModification.interface.getCacheInsurancePackageCompareResult(companyId, insurancePackage,
            function(data) {
                if (!data.compareResult) {
                    var btnTd = quoteResult.dom.getInsuranceCompanyBtnDom(companyId);
                    btnTd.find(".toEditPd").hide().siblings(".toSavePd").show();
                    quoteModification.enablePremiumPdInput(companyId);
                }
            },
            function() {}
        );
    },
    setModificationItemPremium: function(insurancePackage, insuranceResult, companyId, callback) {
        quoteModification.interface.getQuoteRecordCache(companyId,
            function(data) {
                if (data.quoteRecord) {
                    var insuranceResultPd = quoteResult.result_200.setResult(JSON.parse(data.quoteRecord));
                    quoteModification.setItemPremiumPdAction(insurancePackage, insuranceResult, companyId, insuranceResultPd);

                    //设置输入框只读
                    quoteModification.disablePremiumPdInput(companyId);

                    //险种发生增删改时比较套餐是否发生变化，变化显示输入，无变化则显示只读
                    quoteModification.setCompareInsurancePackageResult(companyId, insurancePackage);
                }

                callback(data);
            },
            function() {
            }
        );
    },
    setItemPremiumPdAction: function(insurancePackage, insuranceResult, companyId, insuranceResultPd) {
        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
        var itemNames = ["item", "compulsory", "autoTax", "thirdParty", "damage", "scratch", "driver", "passenger",
            "theft", "spontaneousLoss", "glass", "engine", "iop", "unableFindThirdParty","designatedRepairShop"];
        var promptItems = ["engine", "unableFindThirdParty","designatedRepairShop"];
        for (var i=0; i<itemNames.length; i++) {
            var name = itemNames[i];
            if ("compulsory,autoTax,iop".indexOf(name) > -1) {
                itemTd.find("." + name).parent().removeClass("col-sm-5").addClass("col-sm-3");
                if (insuranceResult[name]) {
                    itemTd.find("#" + name + "PremiumPd").parent().show().addClass("col-sm-4");
                    itemTd.find("#" + name + "PremiumPd").val(insuranceResultPd && insuranceResultPd[name] ? insuranceResultPd[name] : insuranceResult[name]);
                    quoteModification.setNumInputAction(companyId, "#" + name + "PremiumPd", insuranceResult[name]);
                }
            } else {
                if (name == "glass") {
                    itemTd.find(".glassType").parent().removeClass("col-sm-5").addClass("col-sm-3");
                } else if (promptItems.indexOf(name) > -1) {
                    itemTd.find("." + name).parent().removeClass("col-sm-5").addClass("col-sm-3");
                } else {
                    itemTd.find("." + name + "Amount").parent().removeClass("col-sm-5").addClass("col-sm-3");
                }
                if (name == "item") {
                    itemTd.find("#itemPremiumPd").parent().show().addClass("col-sm-4");
                } else {
                    if (insuranceResult[name + "Premium"]) {
                        itemTd.find("#" + name + "PremiumPd").parent().show().addClass("col-sm-4");
                        itemTd.find("#" + name + "PremiumPd").val(insuranceResultPd && insuranceResultPd[name + "Premium"] ? insuranceResultPd[name + "Premium"] : insuranceResult[name + "Premium"]);
                        quoteModification.setNumInputAction(companyId, "#" + name + "PremiumPd", insuranceResult[name + "Premium"]);
                    }
                }
            }
            itemTd.find("." + name + "Premium").parent().removeClass("col-sm-7").addClass("col-sm-5");
        }

        //加上真实价格列
        quoteModification.setPdTotalPremiumAction(companyId);

        //计算总保费
        quoteModification.setTotalPremiumValue(companyId);

        //设置参考保费表头
        quoteModification.setPremiumTitle(companyId);
    },
    setPdTotalPremiumAction: function(companyId) {
        var totalTd = quoteResult.dom.getInsuranceCompanyTotalDom(companyId);
        totalTd.find(".premium-margin-left-106").removeClass("premium-margin-left-106");
        totalTd.removeAttr("colspan");
        var text = "<td class=\"item-total company-" + companyId + "-pd\">" +
            "<div class=\"text-left\">" +
            "<span>真实价格:</br></span>" +
            "<span>车船税 <span id=\"autoTaxReality\">0</span>元</br></span>" +
            "<span>不含车船税 <span id=\"noAutoTaxReality\">0</span>元</br></span>" +
            "<span>交强险 <span id=\"compulsoryReality\">0</span>元</br></span>" +
            "<span>商业险 <span id=\"commercialReality\">0</span>元</br></span>" +
            "<span>总计 <span id=\"sumPremiumReality\">0</span>元</br></span>" +
            "</div>" +
            "</td>";
        if(totalTd.parent().find(".company-" + companyId + "-pd").length == 0)
            $(text).insertAfter(totalTd);
    },
    getToTalPremium: function(companyId) {
        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
        var itemNames = ["compulsoryPremiumPd", "autoTaxPremiumPd", "thirdPartyPremiumPd", "damagePremiumPd", "driverPremiumPd", "passengerPremiumPd", "enginePremiumPd",
            "glassPremiumPd", "scratchPremiumPd", "theftPremiumPd", "spontaneousLossPremiumPd", "iopPremiumPd", "unableFindThirdPartyPremiumPd","designatedRepairShopPremiumPd"];

        var autoTax=0,noTax= 0,compulsory=0,commercial=0,sumPremium=0;
        for (var i=0; i<itemNames.length; i++) {
            var name = itemNames[i];
            var premium = itemTd.find("#" + name).val();
            if (premium && premium > 0) {
                if ("autoTaxPremiumPd" == name) {
                    autoTax = premium;
                } else {
                    if ("compulsoryPremiumPd" == name) {
                        compulsory = premium;
                    } else {
                        commercial = parseFloat(premium) + parseFloat(commercial);
                    }
                    noTax = parseFloat(noTax) + parseFloat(premium);
                }

                sumPremium = parseFloat(premium) + parseFloat(sumPremium);
            }
        }
        return {autoTax: autoTax, noTax: noTax, compulsory: compulsory, commercial: commercial, sumPremium: sumPremium};
    },
    setTotalPremiumValue: function(companyId) {
        var premiumJson = quoteModification.getToTalPremium(companyId);
        var totalTd = quoteResult.dom.getInsuranceCompanyTotalPdDom(companyId);
        totalTd.find("#autoTaxReality").text(common.tools.formatMoney(premiumJson.autoTax, 2));
        totalTd.find("#noAutoTaxReality").text(common.tools.formatMoney(premiumJson.noTax, 2));
        totalTd.find("#compulsoryReality").text(common.tools.formatMoney(premiumJson.compulsory, 2));
        totalTd.find("#commercialReality").text(common.tools.formatMoney(premiumJson.commercial, 2));
        totalTd.find("#sumPremiumReality").text(common.tools.formatMoney(premiumJson.sumPremium, 2));
    },
    setNumInputAction: function(companyId, name, premiumBak) {
        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
        common.tools.setDomNumAction(itemTd.find(name));
        itemTd.find(name).unbind("blur").bind({
            blur: function() {
                //校验保费范围
                if (!quoteValidation.validPremiumRange($(this), premiumBak)) {
                    return;
                }
                //设置总保费
                quoteModification.setTotalPremiumValue(companyId);
            }
        });
    },
    disablePremiumPdInput: function(companyId) {
        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
        itemTd.find(".premiumPd").each(function(index) {
            $(this).attr("readonly", true);
        });
    },
    enablePremiumPdInput: function(companyId) {
        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
        itemTd.find(".premiumPd").each(function(index) {
            $(this).attr("readonly", false);
        });
    },
    setPremiumTitle: function(companyId) {
        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
        itemTd.find(".itemPremium").text("参考保费");
    },
    interface: {
        getPaidAmount: function(companyId, callback, error) {
            common.ajax.getByAjaxWithJsonAndHeader(true, "get", "json", "/orderCenter/quote/paidAmount?companyId=" + companyId, null,
                function(data) {
                    callback(data);
                },
                function() {
                    error();
                },
                quote.interface.getHeaderMap());
        },
        getPreInsurancePackageCompareResult: function(insurancePackage, callback, error) {
            common.ajax.getByAjaxWithJson(true, "post", "json", "/orderCenter/quoteModifications/insurancePackage/compare",
                {
                    quoteModification: {
                        insurancePackage: insurancePackage,
                        quoteSourceId: quote.sourceId
                    },
                    strQuoteSource: quote.source
                },
                function(data) {
                    callback(data);
                },
                function() {
                    error();
                }
            );
        },
        getCacheInsurancePackageCompareResult: function(companyId, insurancePackage, callback, error) {
            common.ajax.getByAjaxWithJson(true, "post", "json", "/orderCenter/quoteRecordCaches/insurancePackage/compare",
                {
                    strQuoteSource: quote.source,
                    quoteModification: {
                        quoteSourceId: quote.sourceId
                    },
                    insuranceCompany: {
                        id: companyId
                    },
                    quoteRecord: {
                        insurancePackage: insurancePackage
                    }
                },
                function(data) {
                    callback(data);
                },
                function() {
                    error();
                }
            );
        },
        getQuoteModification: function(callback, error) {
            common.ajax.getByAjaxWithJson(true, "get", "json", "/orderCenter/quoteModifications/" + quote.source + "/" + quote.sourceId, null,
                function(data) {
                    callback(data);
                },
                function() {
                    error();
                }
            );
        },
        getQuoteRecordCache: function(companyId, callback, error) {
            var type =  3;
            common.ajax.getByAjaxWithJson(true, "get", "json", "/orderCenter/quoteRecordCaches/" + quote.sourceId + "/" + companyId + "?type=" + type + "&quoteSource=" + quote.source+"&licensePlateNo="+quote.licensePlateNo, null,
                function(data) {
                    callback(data);
                },
                function() {
                    error();
                }
            );
        },
        saveQuoteRecordCache: function(quoteJson, companyId, insurancePackage, insuranceResult, description, callback, error) {
            var insurancePackagePd = quote.interface.convertInsurancePackage(quote.insurancePackage);
            var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
            common.ajax.getByAjaxWithJson(true, "post", "", "/orderCenter/quoteRecordCaches/default",
                [
                    {
                        quoteModification: {
                            quoteSourceId: quote.sourceId,
                            insurancePackage: insurancePackagePd
                        },
                        strQuoteSource: quote.source,
                        insuranceCompany: {
                            id: companyId
                        },
                        type: 1,
                        quoteRecord: {
                            auto: {
                                id: quoteJson.data.auto ? quoteJson.data.auto.id : null,
                                licensePlateNo: quote.licensePlateNo
                            },
                            area: {
                                id: quoteJson.data.area ? quoteJson.data.area.id : null
                            },
                            insurancePackage: insurancePackage,
                            compulsoryPremium: insuranceResult.compulsory,
                            autoTax: insuranceResult.autoTax,
                            thirdPartyPremium: insuranceResult.thirdPartyPremium,
                            thirdPartyAmount: insuranceResult.thirdPartyAmountValue,
                            damagePremium: insuranceResult.damagePremium,
                            damageAmount: insuranceResult.damageAmountValue,
                            theftPremium: insuranceResult.theftPremium,
                            theftAmount: insuranceResult.theftAmountValue,
                            enginePremium: insuranceResult.enginePremium,
                            driverPremium: insuranceResult.driverPremium,
                            driverAmount: insuranceResult.driverAmountValue,
                            passengerPremium: insuranceResult.passengerPremium,
                            passengerAmount: insuranceResult.passengerAmountValue,
                            spontaneousLossPremium: insuranceResult.spontaneousLossPremium,
                            spontaneousLossAmount: insuranceResult.spontaneousLossAmountValue,
                            unableFindThirdPartyPremium: insuranceResult.unableFindThirdPartyPremium,
                            designatedRepairShopPremium: insuranceResult.designatedRepairShopPremium,
                            glassPremium: insuranceResult.glassPremium,
                            scratchPremium: insuranceResult.scratchPremium,
                            scratchAmount: insuranceResult.scratchAmountValue,
                            iopTotal: insuranceResult.iop
                        },
                        policyDescription: description
                    },
                    {
                        quoteModification: {
                            quoteSourceId: quote.sourceId,
                            insurancePackage: insurancePackagePd
                        },
                        strQuoteSource: quote.source,
                        insuranceCompany: {
                            id: companyId
                        },
                        type: 3,
                        quoteRecord: {
                            auto: {
                                id: quoteJson.data.auto ? quoteJson.data.auto.id : null,
                                licensePlateNo: quote.licensePlateNo
                            },
                            area: {
                                id: quoteJson.data.area ? quoteJson.data.area.id : null
                            },
                            insurancePackage: insurancePackage,
                            compulsoryPremium: itemTd.find("#compulsoryPremiumPd").val(),
                            autoTax: itemTd.find("#autoTaxPremiumPd").val(),
                            thirdPartyPremium: itemTd.find("#thirdPartyPremiumPd").val(),
                            thirdPartyAmount: insuranceResult.thirdPartyAmountValue,
                            damagePremium: itemTd.find("#damagePremiumPd").val(),
                            damageAmount: insuranceResult.damageAmountValue,
                            theftPremium: itemTd.find("#theftPremiumPd").val(),
                            theftAmount: insuranceResult.theftAmountValue,
                            enginePremium: itemTd.find("#enginePremiumPd").val(),
                            driverPremium: itemTd.find("#driverPremiumPd").val(),
                            driverAmount: insuranceResult.driverAmountValue,
                            passengerPremium: itemTd.find("#passengerPremiumPd").val(),
                            passengerAmount: insuranceResult.passengerAmountValue,
                            spontaneousLossPremium: itemTd.find("#spontaneousLossPremiumPd").val(),
                            spontaneousLossAmount: insuranceResult.spontaneousLossAmountValue,
                            unableFindThirdPartyPremium: itemTd.find("#unableFindThirdPartyPremiumPd").val(),
                            designatedRepairShopPremium: itemTd.find("#designatedRepairShopPremiumPd").val(),
                            glassPremium: itemTd.find("#glassPremiumPd").val(),
                            scratchPremium: itemTd.find("#scratchPremiumPd").val(),
                            scratchAmount: insuranceResult.scratchAmountValue,
                            iopTotal: itemTd.find("#iopPremiumPd").val()
                        },
                        policyDescription: description
                    }
                ],
                function(data) {
                    callback(data);
                },
                function() {
                    error();
                }
            );
        }
    }
};
