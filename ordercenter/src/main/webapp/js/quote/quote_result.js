/**
 * Created by wangfei on 2015/10/26.
 */
var InsuranceResult = function() {
    var compulsory, autoTax, thirdPartyAmount, thirdPartyPremium, damageAmount, damagePremium, theftAmount, theftPremium,
        enginePremium, glassPremium, glassType, driverAmount, driverPremium, passengerAmount, passengerPremium,
        spontaneousLossAmount, spontaneousLossPremium, scratchAmount, scratchPremium, iop, totalPremium, damageIop,
        thirdPartyIop, theftIop, engineIop, driverIop, passengerIop, scratchIop, thirdPartyAmountValue, damageAmountValue,
        theftAmountValue, driverAmountValue, passengerAmountValue, spontaneousLossAmountValue, spontaneousLossIop, scratchAmountValue, premium, unableFindThirdPartyPremium,designatedRepairShopPremium;
};
var quoteResult = {
    successItemsContent: "",
    supplementContent: "",
    errorContent: "",
    insurancePackageCompareMap: new Map(),
    init: function() {
        var successItemsContent = $("#item_kind_show");
        if (successItemsContent.length > 0) {
            quoteResult.successItemsContent = successItemsContent.html();
            successItemsContent.remove();
        }
        var supplementContent = $("#supplement_content");
        if (supplementContent.length > 0) {
            quoteResult.supplementContent = supplementContent.html();
            supplementContent.remove();
        }
        var errorContent = $("#error_content");
        if (errorContent.length > 0) {
            quoteResult.errorContent = errorContent.html();
            errorContent.remove();
        }
    },
    dom: {
        getInsuranceCompanyMiddleDom: function(companyId) {
            return $("#quote_content #middle_item_tr").find(".company-" + companyId);
        },
        getInsuranceCompanyTotalDom: function(companyId) {
            return $("#quote_content #bottom_total_tr").find(".company-" + companyId);
        },
        getInsuranceCompanyTotalPdDom: function(companyId) {
            return $("#quote_content #bottom_total_tr").find(".company-" + companyId + "-pd");
        },
        getInsuranceCompanyBtnDom: function(companyId) {
            return $("#quote_content #bottom_btn_tr").find(".company-" + companyId);
        }
    },
    result_200: {//报价成功
        showResult: function(quoteJson, companyId) {
            var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
            var totalTd = quoteResult.dom.getInsuranceCompanyTotalDom(companyId);
            var btnTd = quoteResult.dom.getInsuranceCompanyBtnDom(companyId);
            itemTd.removeClass("text-center").addClass("vertical-top").height(729);
            itemTd.html(quoteResult.successItemsContent);

           // quote.quoteRecordKey=quoteJson.data.quoteRecordKey;
            quote.quoteRecordKey.put(quoteJson.data.insuranceCompany.id,quoteJson.data.quoteRecordKey);
            var insurancePackage = quoteJson.data.insurancePackage;
            var insuranceResult = this.setResult(quoteJson.data);


            //普通保费设置
            this.setItemPremium(insuranceResult, companyId);

            //修改保费及样式 mark:需要依赖返回结果的写在callback中
            quoteModification.setModificationItemPremium(insurancePackage, insuranceResult, companyId,
                function(cacheData) {
                    //拼接政策所作更改文本
                    var description = quoteResult.result_200.setDescription(quoteJson, cacheData);
                    quoteResult.result_200.appendDescription(companyId, description);
                    //按钮
                    btnTd.html(quoteResult.result_200.setButtons(companyId));
                    quoteResult.result_200.setBtnAction(quoteJson, insurancePackage, insuranceResult, companyId, description, cacheData);
                }
            );

            //总保费
            totalTd.html(this.setTotalPremium(quoteJson, insuranceResult));

            //保费比较
            this.compareInsurancePackage(companyId, insuranceResult);
        },
        appendDescription: function(companyId, description) {
            var itemTd = $("#quote_content #middle_item_tr .company-" + companyId);
            if (!common.isEmpty(description)) {
                itemTd.find("#description").popover({
                    content: description,
                    template: "<div class=\"popover\" role=\"tooltip\" style=\"width: 320px;left: -4px;height: auto;max-height:300px;overflow-y: auto;\"><h3 class=\"popover-title\"></h3><div class=\"popover-content\"></div></div>",
                    title: "承保政策所作修改",
                    trigger: "focus",
                    placement: "top",
                    html: true
                });
                itemTd.find("#description").show();
            } else {
                itemTd.find("#description").hide();
            }
        },
        compareInsurancePackage: function(companyId, newInsuranceResult) {
            var itemTd = $("#quote_content #middle_item_tr .company-" + companyId);
            var totalTd = $("#quote_content #bottom_total_tr .company-" + companyId);
            var oldInsuranceResult = quoteResult.insurancePackageCompareMap.get(companyId);
            if (oldInsuranceResult) {
                itemTd.find(".compulsoryPremium").append(this.compareValue(oldInsuranceResult.compulsory, newInsuranceResult.compulsory));
                itemTd.find(".autoTaxPremium").append(this.compareValue(oldInsuranceResult.autoTax, newInsuranceResult.autoTax));
                itemTd.find(".thirdPartyPremium").append(this.compareValue(oldInsuranceResult.thirdPartyPremium, newInsuranceResult.thirdPartyPremium));
                itemTd.find(".damagePremium").append(this.compareValue(oldInsuranceResult.damagePremium, newInsuranceResult.damagePremium));
                itemTd.find(".driverPremium").append(this.compareValue(oldInsuranceResult.driverPremium, newInsuranceResult.driverPremium));
                itemTd.find(".passengerPremium").append(this.compareValue(oldInsuranceResult.passengerPremium, newInsuranceResult.passengerPremium));
                itemTd.find(".enginePremium").append(this.compareValue(oldInsuranceResult.enginePremium, newInsuranceResult.enginePremium));
                itemTd.find(".glassPremium").append(this.compareValue(oldInsuranceResult.glassPremium, newInsuranceResult.glassPremium));
                itemTd.find(".scratchPremium").append(this.compareValue(oldInsuranceResult.scratchPremium, newInsuranceResult.scratchPremium));
                itemTd.find(".theftPremium").append(this.compareValue(oldInsuranceResult.theftPremium, newInsuranceResult.theftPremium));
                itemTd.find(".spontaneousLossPremium").append(this.compareValue(oldInsuranceResult.spontaneousLossPremium, newInsuranceResult.spontaneousLossPremium));
                itemTd.find(".iopPremium").append(this.compareValue(oldInsuranceResult.iop, newInsuranceResult.iop));
                totalTd.find(".total-premium").append(this.compareValue(oldInsuranceResult.totalPremium, newInsuranceResult.totalPremium));
                totalTd.find(".total-premium span").removeClass("text-height-40");
            }
            quoteResult.insurancePackageCompareMap.put(companyId, newInsuranceResult);
        },
        compareValue: function(oldVal, newVal) {
            if (newVal && oldVal) {
                if (newVal > oldVal) {
                    return "<span class=\"text-height-40 difference\" style=\"color: red;\"'>↑" + common.formatMoney(newVal-oldVal, 2) + "</span>";
                } else if (newVal < oldVal) {
                    return "<span class=\"text-height-40 difference\" style=\"color: green;\"'>↓" + common.formatMoney(oldVal-newVal, 2) + "</span>";
                }
            } else if (newVal && !oldVal) {
                return "<span class=\"text-height-40 difference\" style=\"color: red;\"'>↑" + newVal + "</span>";
            } else if (oldVal && !newVal) {
                return "<span class=\"text-height-40 difference\" style=\"color: green;\"'>↓" + oldVal + "</span>";
            }
        },
        setResult: function(quoteJsonData) {
            var result = new InsuranceResult();
            if (quoteJsonData.fields.length > 0) {
                $.each(quoteJsonData.fields, function(index, kind) {
                    switch (kind.name) {
                        case "compulsoryPremium"://交强险
                            result.compulsory = kind.premium;
                            break;
                        case "autoTax"://车船税
                            result.autoTax = kind.premium;
                            break;
                        case "thirdParty"://机动车第三者责任保险
                            result.thirdPartyAmount = kind.amount.text;
                            result.thirdPartyAmountValue = kind.amount.value;
                            result.thirdPartyPremium = kind.premium;
                            result.thirdPartyIop = kind.iop;
                            break;
                        case "damage"://机动车损失险
                            result.damageAmount = kind.amount.text;
                            result.damageAmountValue = kind.amount.value;
                            result.damagePremium = kind.premium;
                            result.damageIop = kind.iop;
                            break;
                        case "scratch"://车身划痕损失险
                            result.scratchAmount = kind.amount.text;
                            result.scratchAmountValue = kind.amount.value;
                            result.scratchPremium = kind.premium;
                            result.scratchIop = kind.iop;
                            break;
                        case "driver"://车上人员责任险(司机)
                            result.driverAmount = kind.amount.text;
                            result.driverAmountValue = kind.amount.value;
                            result.driverPremium = kind.premium;
                            result.driverIop = kind.iop;
                            break;
                        case "passenger"://车上人员责任险(乘客)
                            result.passengerAmount = kind.amount.text;
                            result.passengerAmountValue = kind.amount.value;
                            result.passengerPremium = kind.premium;
                            result.passengerIop = kind.iop;
                            break;
                        case "theft"://机动车盗抢险
                            result.theftAmount = kind.amount.text;
                            result.theftAmountValue = kind.amount.value;
                            result.theftPremium = kind.premium;
                            result.theftIop = kind.iop;
                            break;
                        case "spontaneousLoss"://自燃损失险
                            result.spontaneousLossAmount = kind.amount.text;
                            result.spontaneousLossAmountValue = kind.amount.value;
                            result.spontaneousLossPremium = kind.premium;
                            result.spontaneousLossIop = kind.iop;
                            break;
                        case "glass"://玻璃单独破碎险
                            result.glassType = kind.amount.text;
                            result.glassPremium = kind.premium;
                            break;
                        case "engine"://发动机特别损失险
                            result.enginePremium = kind.premium;
                            result.engineIop = kind.iop;
                            break;
                        case "iopTotal"://不计免赔险
                            result.iop = kind.premium;
                            break;
                        case "unableFindThirdParty"://机动车损失保险无法找到第三方特约险保费
                            result.unableFindThirdPartyPremium = kind.premium;
                            break;
                        case "designatedRepairShop"://指定专修厂险
                            result.designatedRepairShopPremium = kind.premium;
                            break;
                    }
                });
            }
            //总保费
            result.totalPremium = quoteJsonData.totalPremium;
            //商业险总保费
            result.premium = quoteJsonData.premium;
            return result;
        },
        setDescription: function(quoteJson, cacheData) {
            var description = "";
            if (quoteJson.data.type && quoteJson.data.type.id == 3) {
                description = cacheData.policyDescription;
            } else {
                if (quoteJson.data.quoteFieldStatus) {
                    $.each(quoteJson.data.quoteFieldStatus, function(index, field) {
                        description += "<p>" + (index+1) + "、" + field.description + "</p>";
                    });
                }
            }
            return description;
        },
        setTotalPremium: function(quoteJson, insuranceResult) {
            var autoTax = common.tools.formatMoney(insuranceResult.autoTax, 2);
            var compulsory = common.tools.formatMoney(insuranceResult.compulsory, 2);
            var premium = common.tools.formatMoney(quoteJson.data.premium, 2);
            var noTax = common.tools.formatMoney(parseFloat(compulsory) + parseFloat(premium), 2);
            var totalPremium = common.tools.formatMoney(quoteJson.data.totalPremium, 2);
            return "<div class=\"text-left premium-margin-left-106\">" +
                "<span>参考价:</br></span>" +
                "<span>车船税 <span>" + autoTax + "</span>元</br></span>" +
                "<span>不含车船税 <span>" + noTax + "</span>元</br></span>" +
                "<span>交强险 <span>" + compulsory + "</span>元</br></span>" +
                "<span>商业险 <span>" + premium + "</span>元</br></span>" +
                "<span>总计 <span>" + totalPremium + "</span>元</br></span>" +
                "</div>";
        },
        setButtons: function(companyId) {
            var editBtns="<button class=\"btn btn-primary btn-sm text-input-100 toEditPd\" style=\"margin-right: 5px;\">编辑</button>" +
                "<button class=\"btn btn-primary btn-sm text-input-100 toSavePd none\" style=\"margin-right: 5px;\">保存</button>"
            var operateBtns= "<button class=\"btn btn-danger btn-sm text-input-100 toSend\">发送短信</button>" +
                "<button class=\"btn btn-success btn-sm text-input-100 toCommit\" style=\"margin-left: 5px;\">提交报价</button>";
            var result="";
            manualQuoteObj.result_manual.supportManual(companyId,function(support){
                if(support){
                    result= editBtns+operateBtns;
                }else{
                    result=operateBtns;
                }
            });
            return result;
        },
        setItemPremium: function(insuranceResult, companyId) {
            var itemTd = $("#quote_content #middle_item_tr .company-" + companyId);

            var itemNames = ["compulsory", "autoTax", "thirdParty", "damage", "scratch", "driver", "passenger",
                "theft", "spontaneousLoss", "glass", "engine", "iop", "unableFindThirdParty","designatedRepairShop"];
            var promptItems = ["engine", "unableFindThirdParty","designatedRepairShop"];
            for (var i=0; i<itemNames.length; i++) {
                var name = itemNames[i];
                if ("compulsory,autoTax,iop".indexOf(name) > -1) {
                    if (insuranceResult[name]) {
                        itemTd.find("." + name).text(name == "autoTax" ? "缴纳" : "投保");
                        itemTd.find("." + name + "Premium").text(insuranceResult[name] + "元");
                    } else {
                        itemTd.find("." + name).text(name == "autoTax" ? "未缴纳" : "未投保");
                        itemTd.find("." + name + "Premium").text("--");
                    }
                } else {
                    if (insuranceResult[name + "Premium"]) {
                        if (name == "glass") {
                            itemTd.find(".glassType").text(insuranceResult.glassType);
                        } else if (promptItems.indexOf(name) > -1) {
                            itemTd.find("." + name).text("投保");
                        } else {
                            itemTd.find("." + name + "Amount").text(insuranceResult[name + "Amount"]);
                        }
                        itemTd.find("." + name + "Premium").text(insuranceResult[name + "Premium"] + "元");
                    } else {
                        if (name == "glass") {
                            itemTd.find(".glassType").text("未投保");
                        } else if (promptItems.indexOf(name) > -1) {
                            itemTd.find("." + name).text("未投保");
                        } else {
                            itemTd.find("." + name + "Amount").text("未投保");
                        }
                        itemTd.find("." + name + "Premium").text("--");
                    }
                }
            }
        },
        /**
         * *** 注意维护参数，增加可读性
         * @param quoteJson 报价返回json
         * @param insurancePackage 报价返回套餐
         * @param insuranceResult 组装的返回报价结果
         * @param companyId 保险公司ID
         * @param description 政策描述
         * @param cacheData 缓存报价对象 注意保存修改后的报价不更新此对象
         */
        setBtnAction: function(quoteJson, insurancePackage, insuranceResult, companyId, description, cacheData) {
            var btnTd = quoteResult.dom.getInsuranceCompanyBtnDom(companyId);
            if(companyId == '65000'){
                btnTd.find(".toEditPd").hide();
            }
            //编辑
            btnTd.find(".toEditPd").unbind("click").bind({
                click: function() {
                    btnTd.find(".toEditPd").attr("disabled", true);
                    quoteModification.interface.getQuoteRecordCache(companyId,
                        function(data) {
                            btnTd.find(".toEditPd").attr("disabled", false);
                            btnTd.find(".toEditPd").hide().siblings(".toSavePd").show();
                            //不存在缓存报价调整页面
                            if (!data.quoteRecord) {
                                quoteModification.setItemPremiumPdAction(null, insuranceResult, companyId, insuranceResult);
                            }
                            //取消输入框只读
                            quoteModification.enablePremiumPdInput(companyId);
                        },
                        function() {
                        }
                    );
                }
            });
            //保存
            btnTd.find(".toSavePd").unbind("click").bind({
                click: function() {
                    var totalTdPd = quoteResult.dom.getInsuranceCompanyTotalPdDom(companyId);
                    if (totalTdPd.find("#sumPremiumReality").text() == 0) {
                        popup.mould.popTipsMould(true, "请填写对应险种真实保费", popup.mould.first, popup.mould.warning, "", "", null);
                        return;
                    }

                    //校验保费是否超过限制
                    if (!quoteValidation.validAllPremiumRange(insuranceResult, companyId)) {
                        return;
                    }

                    //校验是否有保费改动
                    if (!quoteValidation.validPremiumChange(cacheData, companyId)) {
                        popup.mould.popConfirmMould(true, "价格有更改，是否需要再次检查一下~", popup.mould.first, ["好", "已仔细检查"], "",
                            function() {
                                popup.mask.hideFirstMask(true);
                            },
                            function() {
                                popup.mask.hideFirstMask(true);
                                doWork();
                            }
                        );
                        return;
                    }

                    doWork();

                    function doWork() {
                        btnTd.find(".toSavePd").attr("disabled", true);

                        //没填的默认保费为0
                        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
                        itemTd.find(".premiumPd").each(function(index) {
                            if (!$(this).val() || $(this).val() < 0) {
                                $(this).val(0);
                            }
                        });
                        quoteModification.interface.saveQuoteRecordCache(quoteJson, companyId, insurancePackage, insuranceResult, description,
                            function(data) {
                                callback(companyId);
                            },
                            function() {
                                error();
                            }
                        );
                    }

                    function callback(companyId) {
                        //设置输入框只读
                        quoteModification.disablePremiumPdInput(companyId);
                        btnTd.find(".toSavePd").attr("disabled", false).hide().siblings(".toEditPd").show();
                        popup.mould.popTipsMould(true, "保存报价成功", popup.mould.first, popup.mould.success, "", "", function() {popup.mask.hideFirstMask(true);});
                    }

                    function error() {
                        btnTd.find(".toSavePd").attr("disabled", false);
                        popup.mould.popTipsMould(true, "保存报价异常", popup.mould.first, popup.mould.error, "", "", function() {popup.mask.hideFirstMask(true);});
                    }
                }
            });
            //发送短信
            btnTd.find(".toSend").unbind("click").bind({
                click: function() {
                    var result = quoteValidation.validQuoteCommit(companyId);
                    if (!result.flag) {
                        popup.mould.popTipsMould(true, result.msg, popup.mould.first, popup.mould.warning, "", "", null);
                        return false;
                    }
                    quote.action.sendQuoteMsg(companyId, quoteJson.data, insuranceResult);
                }
            });
            //提交报价
            btnTd.find(".toCommit").unbind("click").bind({
                click: function() {
                    //设置提交报价的保险公司ID，获取活动时，根据保险公司ID从quoteRecordKeyMap 获取正确的key
                    quote.companyId=companyId;
                    var $selectBtn = quoteResult.dom.getInsuranceCompanyBtnDom(companyId).find(".btn");
                    //置相关按钮不可用
                    $selectBtn.attr("disabled", true);
                    //提交报价前重新获取下缓存报价，不然更新缓存报价后直接提交报价会获取到之前的缓存报价
                    quoteModification.interface.getQuoteRecordCache(companyId,
                        function(cacheData) {
                            //保费json
                            var premiumJson = getPremiumJson(insuranceResult, cacheData);
                            if (cacheData && cacheData.quoteRecord ) {
                                //获得直减后的金额,，修改报价需要
                                quoteModification.interface.getPaidAmount(companyId,
                                    function(data) {
                                        console.log("get paidAmount api return json: " + data.message);
                                        var paidAmountJson = JSON.parse(data.message);
                                        if (paidAmountJson.code == 200) {
                                            var paidAmount = paidAmountJson.data ? paidAmountJson.data : quoteJson.data.paidAmount;
                                            quote.action.commitQuote(companyId, insurancePackage, quoteJson.data.area.id, premiumJson, paidAmount, quoteJson.code,null,quoteJson.data.quoteRecordKey);
                                        } else {
                                            popup.mould.popTipsMould(true, paidAmountJson.message, popup.mould.first, popup.mould.warning, "", "", null);
                                            $selectBtn.attr("disabled", false);
                                            return false;
                                        }
                                    },
                                    function() {
                                        popup.mould.popTipsMould(true, "获取直减金额接口异常！", popup.mould.first, popup.mould.warning, "", "", null);
                                        $selectBtn.attr("disabled", false);
                                        return false;
                                    }
                                );
                            } else {
                                quote.action.commitQuote(companyId, insurancePackage, quoteJson.data.area.id, premiumJson, quoteJson.data.paidAmount, quoteJson.code,null,quoteJson.data.quoteRecordKey);
                            }
                        },
                        function() {
                        }
                    );
                    /**
                     * 组织订单页的保费项
                     * @param insuranceResult 组装的报价结果
                     * @param quoteCacheData 缓存报价OBJ
                     */
                    function getPremiumJson(insuranceResult, quoteCacheData) {
                        var insuranceResultTemp;
                        //存在缓存报价的
                        if (quoteCacheData && quoteCacheData.quoteRecord) {
                            insuranceResultTemp = quoteResult.result_200.setResult(JSON.parse(quoteCacheData.quoteRecord));
                        } else {
                            insuranceResultTemp = insuranceResult;
                        }
                        var autoTax = common.tools.formatMoney(insuranceResultTemp.autoTax, 2);
                        var compulsory = common.tools.formatMoney(insuranceResultTemp.compulsory, 2);
                        var commercial = common.tools.formatMoney(insuranceResultTemp.premium, 2);
                        var sumPremium = common.tools.formatMoney(insuranceResultTemp.totalPremium, 2);
                        return {autoTax: autoTax, compulsory: compulsory, commercial: commercial, sumPremium: sumPremium};
                    }
                }
            });
        }
    },
    result_2008: {//补充信息
        showResult: function(quoteJson, companyId) {
            var companyIdTd = $("#quote_content #middle_item_tr .company-" + companyId);
            companyIdTd.removeClass("vertical-top").addClass("text-center");
            companyIdTd.html("<button class=\"supplement-btn btn btn-danger btn-sm\">补充信息</button>");
            companyIdTd.find(".supplement-btn").unbind("click").bind({
                click: function() {
                    quoteResult.result_2008.supplementSwitch(quoteJson, companyId, popup.mould.first, false);
                }
            });
        },
        supplementSwitch: function(quoteJson, companyId, position, isRenewal) {
            var $popInput = common.tools.getPopInputDom(position, true);
            var items = "";
            var today = common.tools.formatDate(new Date(), "yyyy-MM-dd");
            var tomorrow = common.tools.formatDate(new Date(), "yyyy-MM-dd",1);
            $.each(quoteJson.data, function(index, supplementInfo) {
                var fieldPath = supplementInfo.fieldPath;
                var field = fieldPath.substring(fieldPath.lastIndexOf(".")+1, fieldPath.length);
                var label = "<div class=\"form-group form-group-fix\">" +
                    "<div class=\"col-sm-12 text-left form-inline\">" +
                    "<label>" + supplementInfo.fieldLabel + "</label>" +
                    "</div>" +
                    "</div>";
                switch (field) {
                    case "autoModel":
                        quoteResult.result_2008.showAutoModel(supplementInfo.options, companyId, position, isRenewal);
                        break;
                    case "enrollDate":
                        items += label +
                        "<div class=\"form-group form-group-fix\">" +
                        "<div class=\"col-sm-12 text-center\">";
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28 Wdate\" onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,maxDate: '%y-%M-%d'});\" style=\"height:28px;\" readonly>";
                        items +=
                            "</div>" +
                            "</div>";
                        break;
                    case "compulsoryStartDate":
                    case "commercialStartDate":
                        items += label +
                            "<div class=\"form-group form-group-fix\">" +
                            "<div class=\"col-sm-12 text-center\">";
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28 Wdate\" onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,startDate:'%y-%M-{%d+1}'});\" style=\"height:28px;\" value=\"" + tomorrow + "\" readonly>";
                        items +=
                            "</div>" +
                            "</div>";
                        break;
                    case "transferDate":
                        items += label +
                        "<div class=\"form-group form-group-fix\">" +
                        "<div class=\"col-sm-12 text-center\">";
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28 Wdate\" onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true});\" style=\"height:28px;\" value=\"" + today + "\" readonly>";
                        items +=
                            "</div>" +
                            "</div>";
                        break;
                    case "compulsoryCaptchaImage":
                    case "commercialCaptchaImage":
                        items += "<div class=\"form-group form-group-fix\">" +
                            "<div class=\"col-sm-12 text-left form-inline\">" +
                            "<label>" + supplementInfo.fieldLabel + "</label>" +
                            "</div>" +
                            "</div>" +
                            "<div class=\"form-group form-group-fix\">" +
                            "<div class=\"col-sm-12 text-center\">";
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28\" style=\"display: inline;width:50%\" >";
                        items += "<img style=\"float: right;\"  src=\"data:image;base64,"+ supplementInfo.meta.imageData+"\"/>";
                        items +=
                            "</div>" +
                            "</div>";
                        break;
                    default :
                        items += "<div class=\"form-group form-group-fix\">" +
                        "<div class=\"col-sm-12 text-left form-inline\">" +
                        "<label>" + supplementInfo.fieldLabel + "</label>" +
                        "</div>" +
                        "</div>" +
                        "<div class=\"form-group form-group-fix\">" +
                        "<div class=\"col-sm-12 text-center\">";
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28\">";
                        items +=
                            "</div>" +
                            "</div>";
                }
            });
            if (items) {
                popup.pop.popInput(true, quoteResult.supplementContent, position, "330px", "auto", "40%", "59%");
                $popInput.find(".supplement-info").append(items);
                $popInput.find(".theme_poptit .close").unbind("click").bind({
                    click: function() {
                        position == popup.mould.first ? popup.mask.hideFirstMask(true) : popup.mask.hideSecondMask(true);
                    }
                });
                $popInput.find(".toSupplement").unbind("click").bind({
                    click: function() {
                        var jsonData = quoteResult.result_2013.validFields($popInput);
                        if (jsonData && !jsonData.flag) {
                            $popInput.find(".error-line").show().find(".error-msg .errorText").text(jsonData.msg);
                            return;
                        }
                        quote.autoInfo.put(companyId, quoteResult.result_2013.setAuto(companyId, $popInput));
                        if (isRenewal) {
                            quoteRenewal.showOrHideRenewalBtn(false);
                            quote.interface.getQuote(companyId, true);
                        } else {
                            quote.action.quoteOne(companyId);
                        }
                        position == popup.mould.first ? popup.mask.hideFirstMask(true) : popup.mask.hideSecondMask(true);
                    }
                });
            }
        },
        showAutoModel: function(options, companyId, position, isRenewal) {
            var $popInput = common.tools.getPopInputDom(position, true);
            var items = "";
            $.each(options, function(index, option) {
                items += "<div class=\"form-group form-group-fix\">" +
                "<div class=\"col-sm-12 radio\">" +
                "<label>" + "<input type=\"radio\" name=\"autoTypeRadio\" value=\"" + option.value + "\"> " + option.text + "</label>" +
                "</div>" +
                "</div>";
            });
            popup.pop.popInput(true, quoteResult.supplementContent, position, "500px", "auto", "40%", null);
            $popInput.find(".supplement-info").append(items);
            $popInput.find(".theme_poptit .close").unbind("click").bind({
                click: function() {
                    position == popup.mould.first ? popup.mask.hideFirstMask(true) : popup.mask.hideSecondMask(true);
                }
            });
            $popInput.find(".toSupplement").unbind("click").bind({
                click: function() {
                    var autoModel = $popInput.find("input[name='autoTypeRadio']:checked").val();
                    if (!autoModel) {
                        $popInput.find(".error-line").show().find(".error-msg .errorText").text("请选择您的车型");
                        return;
                    }
                    var auto = quote.autoInfo.get(companyId);
                    if (auto) {
                        auto.autoModel = autoModel;
                    } else {
                        auto = new Auto();
                        auto.autoModel = autoModel;
                    }
                    quote.autoInfo.put(companyId, auto);
                    if (isRenewal) {
                        quoteRenewal.showOrHideRenewalBtn(false);
                        quote.interface.getQuote(companyId, true);
                    } else {
                        quote.action.quoteOne(companyId);
                    }
                    position == popup.mould.first ? popup.mask.hideFirstMask(true) : popup.mask.hideSecondMask(true);
                }
            });
        }
    },
    result_2013: {//信息填写错误
        showResult: function(quoteJson, companyId) {
            var companyIdTd = $("#quote_content #middle_item_tr .company-" + companyId);
            companyIdTd.removeClass("vertical-top").addClass("text-center");
            companyIdTd.html("<button class=\"error-btn btn btn-danger btn-sm\">错误信息</button>");
            companyIdTd.find(".error-btn").unbind("click").bind({
                click: function() {
                    quoteResult.result_2013.showErrorFields(quoteJson, companyId, popup.mould.first);
                }
            });
        },
        showErrorFields: function(quoteJson, companyId, position) {
            var $popInput = common.tools.getPopInputDom(position, true);
            var items = "";
            $.each(quoteJson.data, function(index, errorInfo) {
                var fieldPath = errorInfo.fieldPath;
                var field = fieldPath.substring(fieldPath.lastIndexOf(".")+1, fieldPath.length);
                items += "<div class=\"form-group form-group-fix\">" +
                "<div class=\"col-sm-12 text-left form-inline\">" +
                "<label>" + errorInfo.fieldLabel + "</label>" + (errorInfo.hints[0] ? "<span class=\"red-font\"> (" + errorInfo.hints[0] + ")</span>" : "") +
                "</div>" +
                "</div>" +
                "<div class=\"form-group form-group-fix\">" +
                "<div class=\"col-sm-12 text-center\">";
                switch (field) {
                    case "enrollDate":
                        items +=  "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28 Wdate\" onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,maxDate: '%y-%M-%d'});\" style=\"height:28px;\" " +
                        "value=\"" + (errorInfo.originalValue ? errorInfo.originalValue : "") + "\" readonly>";
                        break;
                    case "compulsoryStartDate":
                    case "commercialStartDate":
                    case "transferDate":
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28 Wdate\" onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true});\" style=\"height:28px;\" " +
                        "value=\"" + (errorInfo.originalValue ? errorInfo.originalValue : "") + "\" readonly>";
                        break;
                    default :
                        var inputVal = errorInfo.originalValue ? errorInfo.originalValue : "";
                        if (field == "licensePlateNo" && inputVal) {
                            inputVal = inputVal.toUpperCase();
                        }
                        items +=  "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28\" value=\"" + inputVal + "\">";
                }
                items +=
                    "</div>" +
                    "</div>";
            });
            popup.pop.popInput(true, quoteResult.errorContent, position, "330px", "auto", "40%", "60%");
            $popInput.find(".error-info").append(items);
            $popInput.find(".theme_poptit .close").unbind("click").bind({
                click: function() {
                    position == popup.mould.first ? popup.mask.hideFirstMask(true) : popup.mask.hideSecondMask(true);
                }
            });
            $popInput.find(".toModify").unbind("click").bind({
                click: function() {
                    var jsonData = quoteResult.result_2013.validFields($popInput);
                    if (jsonData && !jsonData.flag) {
                        $popInput.find(".error-line").show().find(".error-msg .errorText").text(jsonData.msg);
                        return;
                    }
                    quote.autoInfo.put(companyId, quoteResult.result_2013.setAuto(companyId, $popInput));
                    quote.action.quoteOne(companyId);
                    position == popup.mould.first ? popup.mask.hideFirstMask(true) : popup.mask.hideSecondMask(true);
                }
            });
        },
        validFields: function($popInput) {
            var jsonData = null;
            $popInput.find(".field-input").each(function(index, field) {
                var fieldName = $(field).attr("name");
                var fieldValue = $(field).val();
                switch (fieldName) {
                    case "licensePlateNo":
                        jsonData = common.validations.validateLicenseNo(fieldValue);
                        break;
                    case "vinNo":
                        jsonData = common.validations.validateVinNo(fieldValue);
                        break;
                    case "engineNo":
                        jsonData = common.validations.validateEngineNo(fieldValue);
                        break;
                    case "owner":
                        jsonData = common.validations.validateName(fieldValue);
                        break;
                    case "enrollDate":
                        if (!fieldValue) {
                            jsonData = {flag: false, msg: "请选择初登日期"};
                        } else {
                            jsonData = {flag: true, msg: ""};
                        }
                        break;
                    case "transferDate":
                        if (!fieldValue) {
                            jsonData = {flag: false, msg: "请选择过户日期"};
                        } else {
                            jsonData = {flag: true, msg: ""};
                        }
                        break;
                    case "code":
                        if (!fieldValue) {
                            jsonData = {flag: false, msg: "请输入品牌型号"};
                        } else {
                            jsonData = {flag: true, msg: ""};
                        }
                        break;
                    case "identity":
                    case "insuredIdNo":
                        if (fieldValue) {
                            var flag = common.validations.isIdCardNo(fieldValue);
                            if (!flag) {
                                jsonData = {flag: false, msg: "请输入正确的身份证号"};
                            } else {
                                jsonData = {flag: true, msg: ""};
                            }
                        } else {
                            jsonData = {flag: false, msg: "请输入身份证号"};
                        }
                        break;
                    case "compulsoryStartDate":
                        if (!fieldValue) {
                            jsonData = {flag: false, msg: "请输入交强险保单生效日期"};
                        } else {
                            jsonData = {flag: true, msg: ""};
                        }
                        break;
                    case "commercialStartDate":
                        if (!fieldValue) {
                            jsonData = {flag: false, msg: "请输入商业险保单生效日期"};
                        } else {
                            jsonData = {flag: true, msg: ""};
                        }
                        break;
                    case "seats":
                        if (!fieldValue) {
                            jsonData = {flag: false, msg: "请输入车辆座位数"};
                        } else {
                            jsonData = {flag: true, msg: ""};
                        }
                        break;
                    case "verifyCode":
                        if (!fieldValue) {
                            jsonData = {flag: false, msg: "请输入验证码"};
                        } else {
                            jsonData = {flag: true, msg: ""};
                        }
                        break;
                    case "verificationMobile":
                        if(fieldValue){
                            var flag = common.isMobile(fieldValue);
                            if (!flag) {
                                jsonData = {flag: false, msg: "请输入正确的手机号码"};
                            } else {
                                jsonData = {flag: true, msg: ""};
                            }
                        }else{
                            jsonData = {flag: false, msg: "请输入手机号码"};
                        }
                        break;
                    default :
                        if (!fieldValue) {
                            jsonData = {flag: false, msg: "请输入补充信息"};
                        } else {
                            jsonData = {flag: true, msg: ""};
                        }
                        break;
                }
                if (!jsonData.flag) {
                    return false;
                }
            });
            return jsonData;
        },
        setAuto: function(companyId, $popInput) {
            var auto = quote.autoInfo.get(companyId);
            if (!auto) {
                auto = new Auto();
            }

            $($popInput).find(".field-input").each(function(index, field) {
                var fieldName = $(field).attr("name");
                var fieldValue = $(field).val();
                switch (fieldName) {
                    case "licensePlateNo":
                        auto.licensePlateNo = fieldValue;
                        break;
                    case "vinNo":
                        auto.vinNo = fieldValue;
                        break;
                    case "engineNo":
                        auto.engineNo = fieldValue;
                        break;
                    case "owner":
                        auto.owner = fieldValue;
                        break;
                    case "enrollDate":
                        auto.enrollDate = fieldValue;
                        break;
                    case "code":
                        auto.code = fieldValue;
                        break;
                    case "identity":
                        auto.identity = fieldValue;
                        break;
                    case "insuredIdNo":
                        auto.insuredIdNo = fieldValue;
                        break;
                    case "verifyCode":
                        auto.verifyCode = fieldValue;
                        break;
                    default :
                        auto[fieldName] = fieldValue;
                        quote.supplementParams[fieldName] = fieldValue;
                }
            });
            return auto;
        }
    }
};

$(function() {
    quoteResult.init();
});

