/**
 * Created by wangshaobin on 2016/9/19.
 */
var manualQuoteObj = {
    area:new Map(),
    areaId:"",
    auto:new Map(),
    autoId:"",
    setManualQuoteBtn : function(companyId,quoteCode){
        $("#quote_content #middle_item_tr .company-" + companyId + " img").hide();
        $("#quote_content #middle_item_tr .company-" + companyId).append("<br/><br/><button type='button' onclick=\"manualQuoteObj.manualQuote('" + companyId + "','" + quoteCode + "');\" class='manual-btn btn btn-danger btn-sm'>手动报价</button>");
    },
    manualQuote : function(companyId,quoteCode){
        manualQuoteObj.result_manual.supportManual(companyId,function(support){
            if(!support){
                popup.mould.popTipsMould(true, "所选保险公司不支持手动报价!", popup.mould.second, popup.mould.warning, "", "", null);
                quote.newCompanies.splice(quote.newCompanies.indexOf(companyId),1);
            }else{
                //用于区分是报价之后手动报价或者是够选多个保险公司手动报价，前者报价时创建了TD
                if(quote.companies.indexOf(companyId) == -1){
                    $("#quote_content #top_title_tr").append("<td colspan=\"2\" class=\"tab-title company-" + companyId + "\"><a href=\"" + quote.companyUrlMap.get(companyId) + "\" target=\"_blank\"><img class=\"waiting-img\" width=\"96\" height=\"45\" src=\"" + quote.companyImgMap.get(companyId) +"\"></a></td>");
                    $("#quote_content #middle_item_tr").append("<td colspan=\"2\" style='height: 747px;' class=\"item-kind company-" + companyId + "\"></td>");
                    $("#quote_content #bottom_total_tr").append("<td colspan=\"2\" style='height: 177px;' class=\"item-total company-" + companyId + "\"></td>");
                    $("#quote_content #bottom_btn_tr").append("<td colspan=\"2\" class=\"item-btn company-" + companyId + "\"></td>");
               }
                var btnTd = quoteResult.dom.getInsuranceCompanyBtnDom(companyId);
                manualQuoteObj.setInsurancePackage();
                var insuranceResult = manualQuoteObj.result_manual.setInsuranceResult(quote.insurancePackage);
                manualQuoteObj.result_manual.manualQuote(quote.insurancePackage,companyId,insuranceResult,quoteCode);
                btnTd.html(quoteResult.result_200.setButtons(companyId));
                manualQuoteObj.result_manual.setBtnAction(quoteCode, quote.insurancePackage, insuranceResult, companyId, null, null);
                manualQuoteObj.getAutoInfo(quote.user.userId,quote.licensePlateNo);
            }
        });
    },
    setInsurancePackage: function() {
        quote.insurancePackage.compulsory = $("#compulsoryChk").is(':checked');
        quote.insurancePackage.autoTax = $("#autoTaxChk").is(':checked');
        quote.insurancePackage.thirdPartyAmount = $("#thirdPartyChk").is(':checked') ? $("#thirdPartyAmount").val() : null;
        quote.insurancePackage.damage = $("#damageChk").is(':checked');
        quote.insurancePackage.driverAmount = $("#driverChk").is(':checked') ? $("#driverAmount").val() : null;
        quote.insurancePackage.passengerAmount = $("#passengerChk").is(':checked') ? $("#passengerAmount").val() : null;
        quote.insurancePackage.engine = $("#engineChk").is(':checked');
        quote.insurancePackage.glass = $("#glassChk").is(':checked');
        quote.insurancePackage.glassType = $("#glassChk").is(':checked') ? $("#glassType").val() : null;
        quote.insurancePackage.scratchAmount = $("#scratchChk").is(':checked') ? $("#scratchAmount").val() : null;
        quote.insurancePackage.theft = $("#theftChk").is(':checked');
        quote.insurancePackage.spontaneousLoss = $("#spontaneousLossChk").is(':checked');
        quote.insurancePackage.unableFindThirdParty = $("#unableFindThirdPartyChk").is(':checked');
        quote.insurancePackage.designatedRepairShop = $("#designatedRepairShopChk").is(':checked');
        if ($("#iopChk").is(':checked')) {
            quote.insurancePackage.thirdPartyIop = quote.insurancePackage.thirdPartyAmount != null;
            quote.insurancePackage.damageIop = quote.insurancePackage.damage;
            quote.insurancePackage.theftIop = quote.insurancePackage.theft;
            quote.insurancePackage.engineIop = quote.insurancePackage.engine;
            quote.insurancePackage.spontaneousLossIop = quote.insurancePackage.spontaneousLoss;
            quote.insurancePackage.driverIop = quote.insurancePackage.driverAmount != null;
            quote.insurancePackage.passengerIop = quote.insurancePackage.passengerAmount != null;
            quote.insurancePackage.scratchIop = quote.insurancePackage.scratchAmount != null;
            quote.insurancePackage.iopTotal = true;
            quote.insurancePackage.iop = true;
        } else {
            quote.insurancePackage.thirdPartyIop = false;
            quote.insurancePackage.damageIop = false;
            quote.insurancePackage.theftIop = false;
            quote.insurancePackage.engineIop = false;
            quote.insurancePackage.driverIop = false;
            quote.insurancePackage.passengerIop = false;
            quote.insurancePackage.scratchIop = false;
            quote.insurancePackage.spontaneousLossIop = false;
            quote.insurancePackage.iopTotal = false;
            quote.insurancePackage.iop = false;
        }
    },
    sendQuoteMsg: function(companyId, insurancePackage, insuranceResult, insuranceCompany) {
        var sendBtn = $(".quote-tab #bottom_btn_tr .company-" + companyId + " .toSend");
        var totalSeconds = 30;
        manualQuoteObj.sendMessage(companyId, insurancePackage, insuranceResult, insuranceCompany, function(data) {
            if(!data.pass) {
                popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                return;
            }
            var interval = setInterval(function() {
                if (totalSeconds == 1) {
                    sendBtn.text("发送短信").attr("disabled", false);
                    clearInterval(interval);
                } else {
                    totalSeconds --;
                    sendBtn.text(totalSeconds + "秒后再次发送").attr("disabled", true);
                }
            },1000);
        });
    },
    sendMessage: function(companyId, insurancePackage, insuranceResult, insuranceCompany, callBackMethod) {
        common.ajax.getByAjaxWithJsonAndHeader(true, "post", "json", "/orderCenter/quote/" + companyId + "/sms",
            {
                insurancePackage:      insurancePackage,
                insuranceCompany:      insuranceCompany,
                applicant: {
                    id:                quote.user.userId,
                    mobile:            quote.user.mobile
                },
                premium:               insuranceResult.totalPremium,
                compulsoryPremium:     insuranceResult.compulsory,
                autoTax:               insuranceResult.autoTax,
                thirdPartyPremium:     insuranceResult.thirdPartyPremium,
                thirdPartyAmount:      insuranceResult.thirdPartyAmount,
                damagePremium:         insuranceResult.damagePremium,
                theftPremium:          insuranceResult.theftPremium,
                enginePremium:         insuranceResult.enginePremium,
                driverPremium:         insuranceResult.driverPremium,
                driverAmount:          insuranceResult.driverAmount,
                passengerPremium:      insuranceResult.passengerPremium,
                passengerAmount:       insuranceResult.passengerAmount,
                spontaneousLossPremium:insuranceResult.spontaneousLossPremium,
                glassPremium:          insuranceResult.glassPremium,
                scratchPremium:        insuranceResult.scratchPremium,
                scratchAmount:         insuranceResult.scratchAmount,
                iopTotal:              insuranceResult.iop,
                damageIop:             insuranceResult.damageIop,
                thirdPartyIop:         insuranceResult.thirdPartyIop,
                theftIop:              insuranceResult.theftIop,
                engineIop:             insuranceResult.engineIop,
                driverIop:             insuranceResult.driverIop,
                passengerIop:          insuranceResult.passengerIop,
                scratchIop:            insuranceResult.scratchIop,
                spontaneousLossIop:    insuranceResult.spontaneousLossIop,
                unableFindThirdParty:  insuranceResult.unableFindThirdParty,
                designatedRepairShop:  insuranceResult.designatedRepairShop,
                channel: {
                    id:                quote.sourceChannel
                }
            },
            function(data) {
                callBackMethod(data);
            },
            function() {
                popup.mould.popTipsMould(true, "发送报价短信异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            },
            quote.interface.getHeaderMap());
    },
    getAutoInfo: function(userId,licensePlateNo){
        var reqJson = {
            source:           quote.source,
            sourceId:         quote.sourceId,
            userId:           userId,
            auto: {
                licensePlateNo:   "",
                vinNo:            "",
                engineNo:         "",
                owner:            "",
                enrollDate:       "",
                identity:         "",
                insuredIdNo:      "",
                autoType: {
                    code:         "",
                    seats:        "",
                    supplementInfo: {
                        autoModel:"",
                        commercialStartDate:"",
                        compulsoryStartDate:""
                    }
                }
            }
        };
        common.ajax.getByAjaxWithJsonAndHeader(true, "post", "json", "/orderCenter/quote/findAndCreateAuto", reqJson,
            function(data) {
                manualQuoteObj.auto.put(data.id,data);
                manualQuoteObj.autoId = data.id;
                manualQuoteObj.area.put(data.area.id,data.area);
                manualQuoteObj.areaId = data.area.id;
            },
            function() {},
            quote.interface.getHeaderMap());
    },
    saveManualLogs : function(quoteCode,orderId){
        common.ajax.getByAjax(true, "post", "json", "/orderCenter/quote/saveManualQuoteLog",
            {
                quoteCode:  quoteCode,
                orderId:    orderId
            },
            function(data) {
                if(!data.pass)
                    popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
            },
            function() {
                popup.mould.popTipsMould(true, "保存手动报价日志异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    result_manual : {
        manualQuote : function(insurancePackage, companyId,insuranceResult,quoteCode){
            var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
            itemTd.removeClass("text-center").addClass("vertical-top");
            itemTd.html(quoteResult.successItemsContent);
            //普通保费设置
            this.setItemAmount(insurancePackage,companyId,insuranceResult);
            quoteModification.setTotalPremiumValue(companyId);
            var totalTd = quoteResult.dom.getInsuranceCompanyTotalDom(companyId);
            totalTd.html(this.setTotalPremium());
        },
        setTotalPremium : function(){
            return "";
        },
        setPdTotalPremiumAction: function(companyId) {
            var totalTd = quoteResult.dom.getInsuranceCompanyTotalDom(companyId);
            totalTd.removeAttr("colspan");
            totalTd.width("1px");
            totalTd.css({'border-right':'1px solid #fff'});
            var text = "<td class=\"item-total company-" + companyId + "-pd\" style='width:321px;'>" +
                "<div class=\"text-left\">" +
                "<span>真实价格:</br></span>" +
                "<span>车船税 <span id=\"autoTaxReality\">0</span>元</br></span>" +
                "<span>不含车船税 <span id=\"noAutoTaxReality\">0</span>元</br></span>" +
                "<span>交强险 <span id=\"compulsoryReality\">0</span>元</br></span>" +
                "<span>商业险 <span id=\"commercialReality\">0</span>元</br></span>" +
                "<span>总计 <span id=\"sumPremiumReality\">0</span>元</br></span>" +
                "</div>" +
                "</td>";
            totalTd.siblings(".company-" + companyId + "-pd").remove();
            $(text).insertAfter(totalTd);
        },
        setItemAmount : function(insurancePackage, companyId,insuranceResult){
            var itemTd = $("#quote_content #middle_item_tr .company-" + companyId);
            var selectItems = ["thirdParty","driver","passenger","scratch"];
            var writeItems = ["damage","engine","theft","spontaneousLoss","unableFindThirdParty"];
            var typeItems = ["glass"];
            var achievableItems = ["compulsory","autoTax","iop"];
            var itemNames = ["compulsory", "autoTax", "thirdParty", "damage", "scratch", "driver", "passenger",
                "theft", "spontaneousLoss", "glass", "engine", "iop","unableFindThirdParty","designatedRepairShop"];
            itemTd.find(".itemPremium").parent().hide();
            itemTd.find("#itemPremiumPd").parent().show().addClass("col-sm-5");
            for (var i=0; i<itemNames.length; i++) {
                var name = itemNames[i];
                if(typeItems.indexOf(name) > -1){
                    if (insurancePackage[name]) {
                        itemTd.find("." + name + "Type").text(document.getElementById("glassType").options[parseInt(insurancePackage[name + "Type"])-1].innerHTML);
                        itemTd.find("#" + name + "PremiumPd").parent().show().addClass("col-sm-5");
                        itemTd.find("#" + name + "PremiumPd").val("0.00");
                        quoteModification.setNumInputAction(companyId, "#" + name + "PremiumPd");
                        itemTd.find("#" + name + "Premium").attr("readonly", true);

                    }else{
                        itemTd.find("." + name + "Type").text("未投保");
                    }
                } else if(selectItems.indexOf(name) > -1){
                    if (insurancePackage[name + "Amount"]) {
                        itemTd.find("." + name + "Amount").text(insuranceResult[name + "Amount"]);
                        itemTd.find("#" + name + "PremiumPd").parent().show().addClass("col-sm-5");
                        itemTd.find("#" + name + "PremiumPd").val("0.00");
                        quoteModification.setNumInputAction(companyId, "#" + name + "PremiumPd");
                        itemTd.find("#" + name + "Premium").attr("readonly", true);
                    }else{
                        itemTd.find("." + name + "Amount").text("未投保");
                    }
                }  else if(writeItems.indexOf(name) > -1){
                    if (insurancePackage[name]) {
                        itemTd.find("." + name + "Amount").text(insuranceResult[name + "Amount"]);
                        itemTd.find("#" + name + "PremiumPd").parent().show().addClass("col-sm-5");
                        itemTd.find("#" + name + "PremiumPd").val("0.00");
                        quoteModification.setNumInputAction(companyId, "#" + name + "PremiumPd");
                        itemTd.find("#" + name + "Premium").attr("readonly", true);
                        if(name=="engine" || name=="unableFindThirdParty")
                            itemTd.find("." + name).text("投保");
                        itemTd.find("#" + name + "PremiumPd").val("0.00");
                    }else{
                        itemTd.find("." + name + "Amount").text("未投保");
                        if(name=="engine" || name=="unableFindThirdParty")
                            itemTd.find("." + name).text("未投保");
                    }
                } else if (achievableItems.indexOf(name) > -1) {
                    if (insurancePackage[name]) {
                        itemTd.find("." + name).text(name == "autoTax" ? "缴纳" : "投保");
                        itemTd.find("#" + name + "PremiumPd").parent().show().addClass("col-sm-5");
                        itemTd.find("#" + name + "PremiumPd").val("0.00");
                        quoteModification.setNumInputAction(companyId, "#" + name + "PremiumPd");
                        itemTd.find("#" + name + "Premium").attr("readonly", true);
                    }else{
                        itemTd.find("." + name).text(name == "autoTax" ? "未缴纳" : "未投保");
                    }
                }
                itemTd.find("#" + name + "PremiumPd").attr("readonly", true);
            }
        },
        setBtnAction: function(quoteCode, insurancePackage, insuranceResult, companyId, description, cacheData) {
            var btnTd = quoteResult.dom.getInsuranceCompanyBtnDom(companyId);
            if(companyId == '65000'){
                btnTd.find(".toCommit").hide();
            }
            //编辑
            btnTd.find(".toEditPd").unbind("click").bind({
                click: function() {
                    btnTd.find(".toEditPd").attr("disabled", false);
                    btnTd.find(".toEditPd").hide().siblings(".toSavePd").show();
                    manualQuote.setItemPremiumPdAction(insurancePackage, insuranceResult, companyId);
                    //取消输入框只读
                    quoteModification.enablePremiumPdInput(companyId);
                    manualQuoteObj.result_manual.enableAmountInput(companyId);
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

                    var middleTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
                    for(var p in insurancePackage){
                        if(insurancePackage[p]){
                            if(p != "compulsory" && p != "autoTax"){
                                var editTd = middleTd.find("#" + p + "PremiumPd");
                                if(p == 'thirdPartyIop')
                                    editTd = middleTd.find("#thirdPartyPremiumPd");
                                if(editTd.val() <= parseFloat("0.00")){
                                    popup.mould.popTipsMould(true, "保费不能为0，请重新核对保费", popup.mould.first, popup.mould.warning, "好", "",null);
                                    return;
                                }
                            }

                            var amountTd = middleTd.find("#" + p);
                            if(p == 'thirdPartyIop')
                                amountTd = middleTd.find("#thirdParty");
                            if(amountTd.val() <= parseFloat("0.00")){
                                popup.mould.popTipsMould(true, "保额不能为0，请重新核对保额", popup.mould.first, popup.mould.warning, "好", "",null);
                                return;
                            }
                        }
                    }

                    //校验是否有保费改动
                    if (!quoteValidation.validPremiumChange(cacheData, companyId)) {
                        popup.mould.popConfirmMould(true, "价格有更改，是否需要再次检查一下~", popup.mould.first, ["好", "已仔细检查"], "",
                            function() {
                                popup.mask.hideFirstMask(true);
                            },
                            function() {
                                var compulsoryPd = middleTd.find("#compulsoryPremiumPd").val();
                                var autoTaxPd = middleTd.find("#autoTaxPremiumPd").val();
                                var msg = "";
                                if(Number(compulsoryPd) + Number(autoTaxPd) == 0)
                                    msg = "交强险、车船税";
                                else
                                    msg = compulsoryPd == 0 ? "交强险" : autoTaxPd == 0 ? "车船税" : "";
                                if(msg != ""){
                                    popup.mould.popConfirmMould(true, msg + "为0，确定要保存？", popup.mould.first, ["取消", "保存"], "",
                                        function() {
                                            popup.mask.hideFirstMask(true);
                                        },
                                        function() {
                                            popup.mask.hideFirstMask(true);
                                            doWork();
                                        }
                                    );
                                }else{
                                    popup.mask.hideFirstMask(true);
                                    doWork();
                                }
                            }
                        );
                        return;
                    }

                    function doWork() {
                        btnTd.find(".toSavePd").attr("disabled", true);

                        //没填的默认保费为0
                        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
                        itemTd.find(".premiumPd").each(function(index) {
                            if (!$(this).val() || $(this).val() < 0) {
                                $(this).val(0);
                            }
                        });

                        manualQuoteObj.result_manual.saveQuoteRecordCache(companyId, quote.interface.convertInsurancePackage(insurancePackage), insuranceResult, description,
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
                        manualQuoteObj.result_manual.disableAmountInput(companyId);
                        popup.mould.popTipsMould(true, "保存报价成功", popup.mould.first, popup.mould.success, "", "", function() {popup.mask.hideFirstMask(true);});
                        btnTd.find(".toSavePd").attr("disabled", false).hide().siblings(".toEditPd").show();
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
                    $.ajax({
                        type: "get",
                        contentType : "application/json",
                        url: "/orderCenter/quote/findInsuranceCompany",
                        data: {companyId : parseInt(companyId)},
                        success: function(data){
                            manualQuoteObj.sendQuoteMsg(companyId, quote.interface.convertInsurancePackage(insurancePackage), insuranceResult, data);
                        }
                    });
                }
            });
            //提交报价
            btnTd.find(".toCommit").unbind("click").bind({
                click: function() {
                    var totalTdPd = quoteResult.dom.getInsuranceCompanyTotalPdDom(companyId);
                    if (totalTdPd.find("#sumPremiumReality").length == 0 || totalTdPd.find("#sumPremiumReality").text() == 0) {
                        popup.mould.popTipsMould(true, "请输入保额和保费。", popup.mould.first, popup.mould.warning, "", "", null);
                        return;
                    }
                    var $selectBtn = quoteResult.dom.getInsuranceCompanyBtnDom(companyId).find(".btn");
                    //置相关按钮不可用
                    $selectBtn.attr("disabled", true);
                    quoteModification.interface.getQuoteRecordCache(companyId,
                        function(cacheData) {
                            //保费json
                            var premiumJson = getPremiumJson(companyId);
                            if (cacheData && cacheData.quoteRecord) {
                                //获得直减后的金额,，修改报价需要
                                quoteModification.interface.getPaidAmount(companyId,
                                    function(data) {
                                        console.log("get paidAmount api return json: " + data.message);
                                        var paidAmountJson = JSON.parse(data.message);
                                        if (paidAmountJson.code == 200) {
                                            var paidAmount = paidAmountJson.data ? paidAmountJson.data : null;
                                            quote.action.commitQuote(companyId, quote.interface.convertInsurancePackage(insurancePackage), manualQuoteObj.areaId, premiumJson, paidAmount, quoteCode, "manual",null);
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
                                quote.action.commitQuote(companyId, quote.interface.convertInsurancePackage(insurancePackage), manualQuoteObj.areaId, premiumJson, null, quoteCode, "manual",null);
                            }
                            var $quoteContent = $("#quote_content");
                            var $bottomTotalTr = $quoteContent.find("#bottom_total_tr");
                        },
                        function() {
                        }
                    );
                    /**
                     * 组织订单页的保费项
                     * @param insuranceResult 组装的报价结果
                     */
                    function getPremiumJson(companyId) {
                        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
                        var autoTax = 0, noTax = 0, compulsory = 0, commercial = 0, sumPremium = 0;
                        itemTd.find(".premiumPd").each(function(index) {
                            var premium=$(this).val();
                            var id=$(this).attr("id");
                            if (premium && premium > 0) {
                                if ("autoTaxPremiumPd" == id) {
                                    autoTax = premium;
                                } else {
                                    if ("compulsoryPremiumPd" == id) {
                                        compulsory = premium;
                                    } else {
                                        commercial = parseFloat(premium) + parseFloat(commercial);
                                    }
                                    noTax = parseFloat(noTax) + parseFloat(premium);
                                }
                                sumPremium = parseFloat(premium) + parseFloat(sumPremium);
                            }
                        });
                        return {
                            autoTax: autoTax,
                            noTax: noTax,
                            compulsory: compulsory,
                            commercial: commercial,
                            sumPremium: sumPremium
                        };
                    }
                }
            });
        },
        setInsuranceResult:function(insurancePackage){
            var result = new InsuranceResult();
            for(var p in insurancePackage){
                switch (p) {
                    case "compulsory"://交强险
                        if(insurancePackage[p])
                            result.compulsory = "0.00";
                        break;
                    case "autoTax"://车船税
                        if(insurancePackage[p])
                            result.autoTax = "0.00";
                        break;
                    case "thirdPartyAmount"://机动车第三者责任保险
                        if(insurancePackage[p]){
                            result.thirdPartyAmount = insurancePackage["thirdPartyAmount"];
                            result.thirdPartyAmountValue = insurancePackage["thirdPartyAmount"];
                        }
                        result.thirdPartyPremium = "0.00";
                        result.thirdPartyIop = quote.insurancePackage.iopTotal && insurancePackage[p]?1:0;
                        break;
                    case "damage"://机动车损失险
                        if(insurancePackage[p]){
                            result.damageAmount = "0.00";
                            result.damageAmountValue = "0.00";
                        }
                        result.damagePremium = "0.00";
                        result.damageIop = quote.insurancePackage.iopTotal && insurancePackage[p]?1:0;
                        break;
                    case "scratchAmount"://车身划痕损失险
                        if(insurancePackage[p]){
                            result.scratchAmount = insurancePackage["scratchAmount"];
                            result.scratchAmountValue = insurancePackage["scratchAmount"];
                        }
                        result.scratchPremium = "0.00";
                        result.scratchIop = quote.insurancePackage.iopTotal && insurancePackage[p]?1:0;
                        break;
                    case "driverAmount"://车上人员责任险(司机)
                        if(insurancePackage[p]){
                            result.driverAmount = insurancePackage["driverAmount"];
                            result.driverAmountValue = insurancePackage["driverAmount"];
                        }
                        result.driverPremium = "0.00";
                        result.driverIop = quote.insurancePackage.iopTotal && insurancePackage[p]?1:0;
                        break;
                    case "passengerAmount"://车上人员责任险(乘客)
                        if(insurancePackage[p]){
                            result.passengerAmount = insurancePackage["passengerAmount"];
                            result.passengerAmountValue = insurancePackage["passengerAmount"];
                        }
                        result.passengerPremium = "0.00";
                        result.passengerIop = quote.insurancePackage.iopTotal && insurancePackage[p]?1:0;
                        break;
                    case "theft"://机动车盗抢险
                        if(insurancePackage[p]){
                            result.theftAmount = "0.00";
                            result.theftAmountValue = "0.00";
                        }
                        result.theftPremium = "0.00";
                        result.theftIop = quote.insurancePackage.iopTotal && insurancePackage[p]?1:0;
                        break;
                    case "spontaneousLoss"://自燃损失险
                        if(insurancePackage[p]){
                            result.spontaneousLossAmount = "0.00";
                            result.spontaneousLossAmountValue = "0.00";
                        }
                        result.spontaneousLossPremium = "0.00";
                        result.spontaneousLossIop = quote.insurancePackage.iopTotal && insurancePackage[p]?1:0;
                        break;
                    case "glass"://玻璃单独破碎险
                        if(insurancePackage[p]){
                            result.glassType = document.getElementById("glassType").options[parseInt(insurancePackage["glassType"])-1].innerHTML;
                            result.glassPremium = "0.00";
                        }
                        break;
                    case "engine"://发动机特别损失险
                        if(insurancePackage[p]){
                            result.enginePremium = "0.00";
                        }
                        result.engineIop = quote.insurancePackage.iopTotal && insurancePackage[p]?1:0;
                        break;
                    case "iop"://不计免赔险
                        if(insurancePackage[p])
                            result.iop = "0.00";
                        break;
                    case "unableFindThirdParty"://机动车损失保险无法找到第三方特约险保费
                        if(insurancePackage[p])
                            result.unableFindThirdPartyPremium = "0.00";
                        break;
                    case "designatedRepairShop"://指定专修厂险保费
                        if(insurancePackage[p])
                            result.designatedRepairShopPremium = "0.00";
                        break;
                }
            }
            return result;
        },
        disableAmountInput : function(companyId) {
            var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
            itemTd.find(".amount").each(function(index) {
                $(this).attr("readonly", true);
            });
        },
        enableAmountInput: function(companyId) {
            var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
            itemTd.find(".amount").each(function(index) {
                $(this).attr("readonly", false);
            });
        },
        saveQuoteRecordCache: function(companyId, insurancePackage, insuranceResult, description, callback, error) {
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
                                id: manualQuoteObj.autoId,
                                licensePlateNo: quote.licensePlateNo
                            },
                            area: {
                                id: manualQuoteObj.areaId
                            },
                            insurancePackage: insurancePackage,
                            compulsoryPremium: itemTd.find("#compulsoryPremiumPd").val(),
                            autoTax: itemTd.find("#autoTaxPremiumPd").val(),
                            thirdPartyPremium: itemTd.find("#thirdPartyPremiumPd").val(),
                            thirdPartyAmount: itemTd.find("#thirdParty").val(),
                            damagePremium: itemTd.find("#damagePremiumPd").val(),
                            damageAmount: itemTd.find("#damage").val(),
                            theftPremium: itemTd.find("#theftPremiumPd").val(),
                            theftAmount: itemTd.find("#theft").val(),
                            enginePremium: itemTd.find("#enginePremiumPd").val(),
                            driverPremium: itemTd.find("#driverPremiumPd").val(),
                            driverAmount: itemTd.find("#driver").val(),
                            passengerPremium: itemTd.find("#passengerPremiumPd").val(),
                            passengerAmount: itemTd.find("#passenger").val(),
                            unableFindThirdPartyPremium: itemTd.find("#unableFindThirdPartyPremiumPd").val(),
                            designatedRepairShopPremium: itemTd.find("#designatedRepairShopPremiumPd").val(),
                            spontaneousLossPremium: itemTd.find("#spontaneousLossPremiumPd").val(),
                            spontaneousLossAmount: itemTd.find("#spontaneousLoss").val(),
                            glassPremium: itemTd.find("#glassPremiumPd").val(),
                            scratchPremium: itemTd.find("#scratchPremiumPd").val(),
                            scratchAmount: itemTd.find("#scratch").val(),
                            iopTotal: itemTd.find("#iopPremiumPd").val()
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
                                id: manualQuoteObj.autoId,
                                licensePlateNo: quote.licensePlateNo
                            },
                            area: {
                                id: manualQuoteObj.areaId
                            },
                            insurancePackage: insurancePackage,
                            compulsoryPremium: itemTd.find("#compulsoryPremiumPd").val(),
                            autoTax: itemTd.find("#autoTaxPremiumPd").val(),
                            thirdPartyPremium: itemTd.find("#thirdPartyPremiumPd").val(),
                            thirdPartyAmount: itemTd.find("#thirdParty").val(),
                            damagePremium: itemTd.find("#damagePremiumPd").val(),
                            damageAmount: itemTd.find("#damage").val(),
                            theftPremium: itemTd.find("#theftPremiumPd").val(),
                            theftAmount: itemTd.find("#theft").val(),
                            enginePremium: itemTd.find("#enginePremiumPd").val(),
                            driverPremium: itemTd.find("#driverPremiumPd").val(),
                            driverAmount: itemTd.find("#driver").val(),
                            passengerPremium: itemTd.find("#passengerPremiumPd").val(),
                            passengerAmount: itemTd.find("#passenger").val(),
                            unableFindThirdPartyPremium: itemTd.find("#unableFindThirdPartyPremiumPd").val(),
                            designatedRepairShopPremium: itemTd.find("#designatedRepairShopPremiumPd").val(),
                            spontaneousLossPremium: itemTd.find("#spontaneousLossPremiumPd").val(),
                            spontaneousLossAmount: itemTd.find("#spontaneousLoss").val(),
                            glassPremium: itemTd.find("#glassPremiumPd").val(),
                            scratchPremium: itemTd.find("#scratchPremiumPd").val(),
                            scratchAmount: itemTd.find("#scratch").val(),
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
        },
        supportManual:function(companies,callbackMethod){
            common.ajax.getByAjax(false, "get", "text", "/orderCenter/quote/supportManual", {
                    licensePlateNo: quote.licensePlateNo,
                    insuranceCompanyIds: $.isArray(companies)?companies.join(","):companies,
                    channelId:quote.sourceChannel
                },
                function(data) {
                    if(callbackMethod){
                        return callbackMethod(data=="true"?true:false);
                       //return callbackMethod(false);
                    }
                },
                function() {
                }
            );
        }
    }
}

var manualQuote = {
    setItemPremiumPdAction: function(insurancePackage, insuranceResult, companyId) {
        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
        var selectItems = ["thirdParty","driver","passenger","scratch"];
        var writeItems = ["damage","engine","theft","spontaneousLoss","unableFindThirdParty"];
        var typeItems = ["glass"];
        var achievableItems = ["compulsory","autoTax","iop"];
        var itemNames = ["compulsory", "autoTax", "thirdParty", "damage", "scratch", "driver", "passenger",
            "theft", "spontaneousLoss", "glass", "engine", "iop","unableFindThirdParty","designatedRepairShop"];
        itemTd.find(".itemPremium").parent().hide();
        itemTd.find("#itemPremiumPd").parent().show().addClass("col-sm-5");
        for (var i=0; i<itemNames.length; i++) {
            var name = itemNames[i];
            itemTd.find("#" + name + "PremiumPd").attr("readonly", true);
            var editItem = $('<input type="text" id='+ name +' class="bottom-input amount text-width-70 text-center">');
            if(typeItems.indexOf(name) > -1){
                if (insurancePackage[name]) {
                    itemTd.find("#" + name + "PremiumPd").parent().show().addClass("col-sm-5");
                    manualQuote.setNumInputAction(companyId, "#" + name + "PremiumPd");
                    itemTd.find("#" + name + "Premium").attr("readonly", true);
                    editItem.val(insuranceResult[name + "Amount"]);
                    editItem.insertAfter(itemTd.find("." + name));
                    itemTd.find("." + name).remove();
                    manualQuote.setNumInputAction(companyId, "#" + name, "amount");
                }
            } else if(selectItems.indexOf(name) > -1){
                if (insurancePackage[name + "Amount"]) {
                    itemTd.find("#" + name + "PremiumPd").parent().show().addClass("col-sm-5");
                    manualQuote.setNumInputAction(companyId, "#" + name + "PremiumPd");
                    itemTd.find("#" + name + "Premium").attr("readonly", true);
                    editItem.val(insuranceResult[name + "Amount"]);
                    editItem.insertAfter(itemTd.find("." + name + "Amount"));
                    itemTd.find("." + name + "Amount").remove();
                    manualQuote.setNumInputAction(companyId, "#" + name, "amount");
                }
            }  else if(writeItems.indexOf(name) > -1){
                if (insurancePackage[name]) {
                    itemTd.find("#" + name + "PremiumPd").parent().show().addClass("col-sm-5");
                    manualQuote.setNumInputAction(companyId, "#" + name + "PremiumPd");
                    editItem.val(insuranceResult[name + "Amount"]);
                    itemTd.find("#" + name + "Premium").attr("readonly", true);
                    if(name!="engine" && name!="unableFindThirdParty"){
                        editItem.insertAfter(itemTd.find("." + name + "Amount"));
                        itemTd.find("." + name + "Amount").remove();
                    }
                    manualQuote.setNumInputAction(companyId, "#" + name, "amount");
                }
            } else if (achievableItems.indexOf(name) > -1) {
                if (insurancePackage[name]) {
                    itemTd.find("#" + name + "PremiumPd").parent().show().addClass("col-sm-5");
                    if(name == "iop")
                        manualQuote.setNumInputAction(companyId, "#" + name + "PremiumPd");
                }
            }
            editItem.parent().parent().height(40);//保持原来的行距格式统一
            editItem.parent().append("元");
        }

        //加上真实列
        manualQuoteObj.result_manual.setPdTotalPremiumAction(companyId);
        //计算总保费
        quoteModification.setTotalPremiumValue(companyId);

    },
    setNumInputAction: function(companyId, name, flag) {
        var itemTd = quoteResult.dom.getInsuranceCompanyMiddleDom(companyId);
        common.tools.setDomNumAction(itemTd.find(name));
        itemTd.find(name).unbind("blur").bind({
            blur: function() {
                //校验保费范围
                if (!manualQuote.validPremiumRange($(this), "0.00",flag)) {
                    return;
                }
                //设置总保费
                quoteModification.setTotalPremiumValue(companyId);
            }
        });
    },
    validPremiumRange: function($premium, premiumBak,flag) {
        if ($premium.val() <= parseFloat(premiumBak)) {
            console.log($premium + "保费为0");
            var str = "保费不能为0，请重新核对保费";
            if(flag == "amount")
                str = "保额不能为0，请重新核对保额";
            popup.mould.popTipsMould(true, str, popup.mould.first, popup.mould.warning, "好", "",
                function() {
                    $premium.focus();
                    popup.mask.hideFirstMask(true);
                }
            );
            return false;
        }
        return true;
    }
}
