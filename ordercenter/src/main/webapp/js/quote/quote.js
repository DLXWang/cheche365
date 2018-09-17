/**
 * Created by wangfei on 2015/10/22.
 */
var InsurancePackage = function() {
    var compulsory, autoTax, thirdPartyAmount, thirdPartyIop, damage, damageIop, theft, theftIop, engine, engineIop,
        glass, glassType, driverAmount, driverIop, passengerAmount, passengerIop, spontaneousLoss, scratchAmount,
        scratchIop;
};
var Auto = function() {
    var licensePlateNo,vinNo,engineNo,owner,enrollDate,code,autoModel,identity,commercialStartDate,compulsoryStartDate,verifyCode;
};

var quote = {
    defaultTabSize: 340,
    uniqueId: "",
    sourceChannel: "",
    quoteOrder: "",
    returnFlag: false,
    areaId: "",
    source: "",
    sourceId: "",
    internalUserId: "",
    user: {
        userId: "",
        mobile: "",
        type: "",
        name: "",
        rebate: ""
    },
    insured: {
        name: "",
        identity: "",
        identityType:""
    },

    insure: {
        name: "",
        identity: "",
        identityType:""
    },
    owner: {
        name: "",
        identity: ""
    },
    licensePlateNo: "",
    companies: new Array(),
    newCompanies: new Array(),
    companiesContent: "",
    insurancePackage: new InsurancePackage(),
    supplementInfo: new Map(),
    autoInfo: new Map(),
    companyUrlMap: new Map(),
    companyImgMap: new Map(),
    btnControlIndex: "",
    quoteRecordKey:new Map(),
    companyId:"",
    orderId:0,
    auto:{},
    handler:"",
    //参数集合，后期将用到的参数转为map
    param:new Map(),
    editFlag:false,//是否是“编辑”弹出框确定按钮触发的
    supplementParams:{},//用于保存补充信息
    init: {
        init: function(id, source, renewalFlag,handler) {
            quote.handler=handler;
            quote.source = source;
            quote.sourceId = id;
            quoteRenewal.renewalFlag = (renewalFlag && renewalFlag == "1");
            var activeUrlId = common.getUrlParam("activeUrlId");
            if(activeUrlId){
                quote.param.put("activeUrlId", activeUrlId);
            }

            handler.getQuoteInfo(function(data){
                var record = eval(data);
                quote.init.quoteInfo(record);
            })
        },
        quoteInfo : function(record){
            var validation = quoteValidation.validQuoteInitParams(quote);
            if (!validation.flag) {
                popup.mould.popTipsMould(true, validation.msg, popup.mould.first, popup.mould.warning, "", "57%", function() {
                    window.close();
                });
                return;
            }
            quote.interface.validInternalUser(function(validJson) {
                validJson = JSON.parse(validJson);
                if (validJson.code != 200) {
                    popup.mould.popTipsMould(true, "很抱歉，您没有报价权限，请联系管理员！", popup.mould.first, popup.mould.error, "", "57%", function() {
                        window.close();
                    });
                    return;
                }
                quote.uniqueId = validJson.data.token;
                console.log("uniqueId: " + quote.uniqueId);
                quote.init.initPage(record);
            });
        },
        initPage: function(data) {
            $("#mobile_text").text(quote.user.mobile);
            $("#license_plate_no_text").text(quote.licensePlateNo);
            $("#source_channel_text").text(this.getChannelText());
            quoteRebate.channel.isAgentChannel();
            this.initCompaniesContent(data);
        },
        getChannelText: function() {
            var sourceChannelId = quote.sourceChannel;
                var sourceChannelName = '';
                common.getByAjax(false,"get","json","/orderCenter/resource/channel/"+ sourceChannelId,{},
                    function(data){
                        if (data) {
                            sourceChannelName = data.description;
                        }
                    },
                    function(){}
                );
                if (sourceChannelName != ''){
                    return sourceChannelName;
                }
                return '无结果';
        },
        initCompaniesContent: function(sourceData) {
            quote.interface.getCompanies(quote.licensePlateNo, function(message) {
                var companyJson = JSON.parse(message);
                if (companyJson.code != 200) {
                    popup.mould.popTipsMould(true, companyJson.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                    return;
                } else {
                    var companies = companyJson.data;
                    var imgContext = "";
                    var renewalImgContent = "";
                    var renewalIndex = 0;
                    $.each(companies,function(index, company) {
                        //获取活动所支持保险公司与正常的获取保险公司接口返回json格式存在差异
                        quote.companyUrlMap.put(company.id, common.tools.checkToEmpty(company.websiteUrl));
                        quote.companyImgMap.put(company.id, common.tools.checkToEmpty(company.logoUrl));
                        if (index%3 == 0) {
                            imgContext += "<div class=\"form-group\">";
                        }
                        imgContext += "<div class=\"col-sm-4\">" +
                        "<div class=\"checkbox\"><label><input class=\"text-height-60\" name=\"companyChk\" type=\"checkbox\" value=\"" + company.id + "\"><img width=\"96\" height=\"57\" src=\"" + company.logoUrl + "\"></label></div>" +
                        "</div>";
                        if ((index+1)%3 == 0) {
                            imgContext += "</div>";
                        }
                        if (quoteRenewal.renewalFlag && company.renewSupport) {
                            if (renewalIndex%3 == 0) {
                                renewalImgContent += "<div class=\"form-group\">";
                            }
                            renewalImgContent += "<div class=\"col-sm-4\">" +
                            "<div class=\"radio\"><label><input class=\"text-height-60\" name=\"renewalCompanyRadio\" type=\"radio\" value=\"" + company.id + "\"><img width=\"96\" height=\"57\" src=\"" + company.logoUrl + "\"></label></div>" +
                            "</div>";
                            if ((renewalIndex+1)%3 == 0) {
                                renewalImgContent += "</div>";
                            }
                            renewalIndex++;
                        }
                    });
                    $("#companies_content #company_list").html(imgContext);
                    var companiesContent = $("#companies_content");
                    if (companiesContent.length > 0) {
                        quote.companiesContent = companiesContent.html();
                        companiesContent.remove();
                    }
                    $(".quote-all").attr("disabled", false);
                    $("#addCompany").attr("disabled", false);

                    //好车主初始化带出缓存报价
                    //正常初始化带出缓存报价
                    if(quote.source != "renewInsurance"){
                        quoteModification.init(
                            function() {
                                if (quoteRenewal.renewalFlag) {
                                    $("#renewal_companies_content #renewal_company_list").html(renewalImgContent);
                                    var renewalCompaniesContent = $("#renewal_companies_content");
                                    if (renewalCompaniesContent.length > 0) {
                                        quote.renewalCompaniesContent = renewalCompaniesContent.html();
                                        renewalCompaniesContent.remove();
                                    }
                                    quoteRenewal.initRenewal();
                                }
                            }
                        );
                    }
                }
            });
        }
    },
    action: {
        sendQuoteMsg: function(companyId, quoteJson, insuranceResult) {
            var sendBtn = $(".quote-tab #bottom_btn_tr .company-" + companyId + " .toSend");
            var totalSeconds = 30;
            quote.interface.sendMessage(companyId, quoteJson, insuranceResult, function(data) {
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
        validItemKindPolicy: function() {
            this.setInsurancePackage();
            var validation = quoteValidation.validItemKind(quote.insurancePackage);
            if (!validation.flag) {
                popup.mould.popTipsMould(true, validation.msg, popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            return true;
        },
        getAllCompanies: function() {
            //TODO 这里后期处理是否需要校验一键续保的车辆六要素
            if (quoteRenewal.renewalFlag && quote.source != "renewInsurance") {
                var $addCompany = $("#addCompany");
                $addCompany.attr("disabled", true);
                quote.interface.doQuoteValidation(function(quoteValidationJson) {
                    $addCompany.attr("disabled", false);
                    if (!quoteValidationJson.pass) {
                        popup.mould.popTipsMould(true, quoteValidationJson.message, popup.mould.first, popup.mould.warning, "", "", null);
                        return;
                    }
                    toAdd();
                });
            } else {
                toAdd();
            }
            function toAdd() {
                popup.pop.popInput(true, quote.companiesContent, popup.mould.first, "500px", "auto", "40%", null);
                $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                    click: function() {
                        popup.mask.hideFirstMask(false);
                    }
                });
                $("input[name='companyChk']").each(function(index, company) {
                    var companyId = $(company).val();
                    if (quote.companies.indexOf(companyId) > -1) {
                        $(company).attr("checked", true).attr("disabled", true);
                    }
                });

                $("#popover_normal_input .toAdd").unbind("click").bind({
                    click: function() {
                        var isExistentQuote = false;
                        if (quote.companies.length > 0) {
                            isExistentQuote = true;
                        }

                        quote.newCompanies.length = 0;
                        $("input[name='companyChk']:checked").each(function(index, company) {
                            var companyId = $(company).val();
                            if (quote.companies.indexOf(companyId) == -1) {
                                quote.newCompanies.push(companyId);
                             //   quote.companies.push(companyId);
                            }
                        });
                        quote.action.toQuote(isExistentQuote);
                        popup.mask.hideFirstMask(true);
                    }
                });

                $("#popover_normal_input .toManualQuote").unbind("click").bind({
                    click: function() {

                        quote.newCompanies.length = 0;
                        $("input[name='companyChk']:checked").each(function(index, company) {
                            var companyId = $(company).val();
                            if (quote.companies.indexOf(companyId) == -1) {
                                quote.newCompanies.push(companyId);
                             //   quote.companies.push(companyId);
                            }
                        });
                        quote.action.toManualQuote();
                        popup.mask.hideFirstMask(true);
                    }
                });
            }
        },
        toManualQuote: function() {
            if (quote.newCompanies.length > 0) {
                quote.action.toManualQuoteAll();
                $("#quote_content").show();
                $("#quote_content table").width(quote.companies.length*quote.defaultTabSize);
            }
        },
        toManualQuoteAll: function() {
            this.disableAllKinds();
            $.each(quote.newCompanies, function(index, companyId) {
                manualQuoteObj.manualQuote(companyId,200);
                if(quote.companies.indexOf(companyId) == -1 && quote.newCompanies.indexOf(companyId) > -1 ){
                    quote.companies.push(companyId);
                }
            });
        },
        toQuote: function(isExistentQuote) {
            if (quote.newCompanies.length > 0) {
                $.each(quote.newCompanies, function(index, companyId) {
                    $("#quote_content #top_title_tr").append("<td colspan=\"2\" class=\"tab-title company-" + companyId + "\"><a href=\"" + quote.companyUrlMap.get(companyId) + "\" target=\"_blank\"><img class=\"waiting-img\" width=\"96\" height=\"45\" src=\"" + quote.companyImgMap.get(companyId) +"\"></a></td>");
                    if (isExistentQuote) {
                        $("#quote_content #middle_item_tr").append("<td colspan=\"2\" style='height: 722px;' class=\"item-kind company-" + companyId + "\"><button onclick=\"quote.action.quoteOne(" + companyId + ")\" class=\"btn btn-danger text-input-100\">报价</button></td>");
                    } else {
                        $("#quote_content #middle_item_tr").append("<td colspan=\"2\" style='height: 722px;' class=\"item-kind company-" + companyId + "\"></td>");
                    }
                    $("#quote_content #bottom_total_tr").append("<td style='height: 177px;' colspan=\"2\" class=\"item-total company-" + companyId + "\"></td>");
                    $("#quote_content #bottom_btn_tr").append("<td colspan=\"2\" class=\"item-btn company-" + companyId + "\"></td>");
                    if(quote.companies.indexOf(companyId) == -1){
                        quote.companies.push(companyId);
                    }
                });

                if (!isExistentQuote) {
                    quote.action.quoteAll();
                }
                $("#quote_content").show();

                $("#quote_content table").width(quote.companies.length*quote.defaultTabSize);
            }
        },
        getQuoteResult: function(data, companyId) {
            this.showChangeItemBtn();
            if (!data.pass) {
                this.getExceptionQuoteResult(companyId, data.message);
                return;
            }
            var quoteJson = JSON.parse(data.message);
            switch (quoteJson.code) {
                case 200://成功
                    quoteResult.result_200.showResult(quoteJson, companyId);
                    break;
                case "fail"://报价失败
                    this.getExceptionQuoteResult(companyId);
                    break;
                case 2008://补充信息
                    quoteResult.result_2008.showResult(quoteJson, companyId);
                    break;
                case 2009://可以预约购险
                    this.getExceptionQuoteResult(companyId, quoteJson.message ? quoteJson.message : "车险未到期，不可投保");
                    break;
                case 2013://信息填写错误
                    quoteResult.result_2013.showResult(quoteJson, companyId);
                    break;
                case 2017://已知错误信息
                    this.getExceptionQuoteResult(companyId, quoteJson.message);
                    break;
                default:
                    this.getExceptionQuoteResult(companyId, quoteJson.message);
            }
            if(quoteJson.code!=200)
                manualQuoteObj.setManualQuoteBtn(companyId,quoteJson.code);
        },
        getExceptionQuoteResult: function(companyId, message) {
            if (!message) {
                message = "服务器繁忙，请稍后再试！";
            }
            $("#quote_content #middle_item_tr .company-" + companyId + " img").hide();
            $("#quote_content #middle_item_tr .company-" + companyId).html("<label class=\"red-font row-break\">" + message + "</label>");
        },
        disableAllKinds: function() {
            $(".quote-all").attr("disabled", true);
            $("#kind_tab").find("input[type='checkbox']").attr("disabled", true);
            $("#kind_tab").find("select").attr("disabled", true);
        },
        showChangeItemBtn: function() {
            quote.btnControlIndex --;
            if (quote.btnControlIndex == 0) {
                $(".quote-all").attr("disabled", false).hide().siblings(".change-items").show();
            }
        },
        enableAllKinds: function() {
            $(".quote-all").show().siblings(".change-items").hide();
            $("#kind_tab").find("input[type='checkbox']").attr("disabled", false);
            $("#kind_tab select").attr("disabled", false);
            $("#designatedRepairShopChk").attr("disabled","disabled");
            $(".quote-tab button").attr("disabled", true);
        },
        quoteAll: function() {
            this.disableAllKinds();
            $.each(quote.companies, function(index, companyId) {
                if (quote.action.quoteOne(companyId)) {
                    return false;
                }
            });
            quote.editFlag = false;
        },
        quoteOne: function(companyId) {
            $("#quote_content #middle_item_tr .company-" + companyId).removeClass("vertical-top").addClass("text-center");
            $("#quote_content #middle_item_tr .company-" + companyId).html("<img src=\"../../images/loading.GIF\">");
            $("#quote_content #bottom_total_tr .company-" + companyId).attr("colspan", "2").html("");
            $("#quote_content #bottom_btn_tr .company-" + companyId).html("");
            $("#quote_content #bottom_total_tr .company-" + companyId + "-pd").remove();
            quote.btnControlIndex ++;
            quote.handler.getQuote(companyId);
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
            }
        },
        /**
         * 提交报价
         * @param companyId 保险公司ID
         * @param insurancePackage 险种套餐
         * @param areaId 地区ID
         * @param premiumJson 相关保费JSON
         * @param paidAmount 直减活动报价返回的保费，若有则使用paidAmount替代sumPremium
         * @returns {boolean}
         */
        commitQuote: function(companyId, insurancePackage, areaId, premiumJson, paidAmount, quoteCode, manualFlag,quoteRecordKey) {
            var $selectBtn = quoteResult.dom.getInsuranceCompanyBtnDom(companyId).find(".btn");

            var result = quoteValidation.validQuoteCommit(companyId);
            if (!result.flag) {
                popup.mould.popTipsMould(true, result.msg, popup.mould.first, popup.mould.warning, "", "", null);
                $selectBtn.attr("disabled", false);
                return false;
            }

            //保存报价
            quote.interface.saveQuote(companyId, insurancePackage, areaId,quoteRecordKey, function(quoteSaveData) {
                var saveQuoteJson = JSON.parse(quoteSaveData);
                if (saveQuoteJson.code != 200) {
                    popup.mould.popTipsMould(true, saveQuoteJson.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                    $selectBtn.attr("disabled", false);
                    return;
                }
                //如果是toA的，从保存报价返回的数据中取discounts中的amount信息【toA返点金额】保存到参数中，用于后面展示使用；
                if(saveQuoteJson.data.discounts)
                    quote.param.put("discounts", saveQuoteJson.data.discounts[0].amount);
                quote.action.checkAgent(saveQuoteJson,areaId,companyId, premiumJson, paidAmount, quoteCode, manualFlag);
                $selectBtn.attr("disabled", false);
            });
        },
        checkAgent : function(saveQuoteJson,areaId,companyId, premiumJson, paidAmount, quoteCode, manualFlag){
            //用户类型
            quote.interface.checkAgent(quote.user.userId, function(agentMap) {
                if (agentMap.agent) {
                    quote.user.type = "agent";
                    quote.user.name = agentMap.agent.name;
                    quote.user.rebate = agentMap.agent.rebate;
                } else {
                    quote.user.type = "normal";
                }
                quote.action.switchOrderPage(true, companyId);
                //是否需要核保步骤，不需要核保返回1
                var skipInsure = (saveQuoteJson.data.skipInsure == "1");
                if(manualFlag == "manual")
                    skipInsure = true;
                if (quote.returnFlag) {
                    //刷新订单页相关参数
                    quote.quoteOrder.refreshOrderItems(saveQuoteJson.data.quoteRecordId, quote.owner, premiumJson, paidAmount, companyId, skipInsure);
                } else {
                    //首次提交报价至订单页
                    quote.quoteOrder = new QuoteOrder(quote.user, saveQuoteJson.data.quoteRecordId, quote.insured, quote.insure, quote.owner, premiumJson, areaId, quote.sourceChannel, paidAmount, companyId, skipInsure, quoteCode, manualFlag);
                }
            });
        },
        switchOrderPage: function(isNext, companyId) {
            var $companyAddContent = $("#company_add_content");
            var $editSupplementInfo = $("#edit_supplement_info");
            var $quoteContent = $("#quote_content");
            var $itemBtn = $("#kind_tab .item-btn");
            var $topTitleTr = $quoteContent.find("#top_title_tr");
            var $middleItemTr = $quoteContent.find("#middle_item_tr");
            var $bottomTotalTr = $quoteContent.find("#bottom_total_tr");
            var $bottomBtnTr = $quoteContent.find("#bottom_btn_tr");
            var $orderAddContent = $("#order_add_content");
            if (isNext) {
                $companyAddContent.hide();
                $editSupplementInfo.hide();
                $itemBtn.parent().hide();
                $topTitleTr.find(".company-" + companyId).siblings("td").hide();
                $middleItemTr.find(".company-" + companyId).siblings("td").hide();
                $bottomTotalTr.find(".company-" + companyId).siblings("td").not(".company-" + companyId + "-pd").hide();
                $bottomBtnTr.find(".company-" + companyId).hide().siblings("td").hide();
                $quoteContent.removeClass("col-sm-7").addClass("col-sm-2").find("table").width(quote.defaultTabSize);
                $orderAddContent.show();
                $orderAddContent.css("margin-left", "141px");
            } else {
                $companyAddContent.show();
                $editSupplementInfo.show();
                $itemBtn.parent().show();
                $topTitleTr.find("td").show();
                $middleItemTr.find("td").show();
                $bottomTotalTr.find("td").show();
                $bottomBtnTr.find("td").show();
                $bottomTotalTr.find("td").show();
                $quoteContent.removeClass("col-sm-2").addClass("col-sm-7").find("table").width(quote.companies.length * quote.defaultTabSize);
                $orderAddContent.hide();
            }
        },
        switchChkAction: function(chkDom, isChecked) {
            if (isChecked) {
                $(chkDom).siblings("span").removeClass("no");
                $(chkDom).parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
            } else {
                $(chkDom).siblings("span").addClass("no");
                $(chkDom).parent().parent().parent().siblings("div").find(".yes").hide().siblings(".no").show();
            }
        },
        getSupplementParams: function(isRenewal){
            var reqJson = {
                source:           quote.source,
                sourceId:         quote.sourceId,
                userId:           quote.user.userId,
                auto: {
                    licensePlateNo:   quote.licensePlateNo
                },
                additionalParameters:{
                    supplementInfo:{
                    }
                }
            };
            quote.action.setSupplementAdditionalParameters(reqJson);
            quote.action.setReqJsonPackage(reqJson, isRenewal);
            common.ajax.getByAjaxWithJsonAndHeader(true, "post", "json", "/orderCenter/quote/getSupplementParams", reqJson,
                function(data) {
                    if (!data.pass) {
                        if (!data.message) {
                            data.message = "补充信息获取异常!";
                        }
                        popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                        return;
                    }
                    //填充补充信息
                    var quoteJson = JSON.parse(data.message);
                    quote.action.fillSupplementParams(quoteJson, popup.mould.first);
                },
                function() {
                    popup.mould.popTipsMould(true, "补充信息获取异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                },
                quote.interface.getHeaderMap());
        },
        fillSupplementParams: function(quoteJson, position) {
            var $popInput = common.tools.getPopInputDom(position, true);
            var items = "";
            $.each(quoteJson.data, function(index, supplementInfo) {
                var fieldPath = supplementInfo.fieldPath;
                var field = fieldPath.substring(fieldPath.lastIndexOf(".")+1, fieldPath.length);
                var label = "<div class=\"form-group form-group-fix\">" +
                    "<div class=\"col-sm-12 text-left form-inline\">" +
                    "<label>" + supplementInfo.fieldLabel + "</label>" +
                    "</div>" +
                    "</div>";
                switch (field) {
                    case "enrollDate":
                        items += label +
                            "<div class=\"form-group form-group-fix\">" +
                            "<div class=\"col-sm-12 text-center\">";
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28 Wdate\" onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd',maxDate: '%y-%M-%d'});\" style=\"height:28px;\" value=\""+ (supplementInfo.originalValue ? supplementInfo.originalValue : "") +"\">";
                        items +=
                            "</div>" +
                            "</div>";
                        break;
                    case "compulsoryStartDate":
                    case "commercialStartDate":
                        items += label +
                            "<div class=\"form-group form-group-fix\">" +
                            "<div class=\"col-sm-12 text-center\">";
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28 Wdate\" onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-{%d+1}'});\" style=\"height:28px;\" value=\"" + supplementInfo.originalValue + "\">";
                        items +=
                            "</div>" +
                            "</div>";
                        break;
                    case "transferDate":
                        items += label +
                            "<div class=\"form-group form-group-fix\">" +
                            "<div class=\"col-sm-12 text-center\">";
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28 Wdate\" onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true});\" style=\"height:28px;\" value=\"" + supplementInfo.originalValue + "\" readonly>";
                        items +=
                            "</div>" +
                            "</div>";
                        break;
                    case "autoModel":
                        items += label;
                        $.each(supplementInfo.options, function(idx, op) {
                            var sed = op.value == supplementInfo.originalValue?"checked ":"";
                            items += "<div class=\"form-group form-group-fix\">" +
                                "<div class=\"col-sm-12 radio\">" +
                                "<label>" + "<input type=\"radio\" name=\"" + field + "\" value=\"" + op.value + "\" "+sed+"> " + op.text + "</label>" +
                                "</div>" +
                                "</div>";
                        });



                        break;
                    default :
                        items += "<div class=\"form-group form-group-fix\">" +
                            "<div class=\"col-sm-12 text-left form-inline\">" +
                            "<label>" + supplementInfo.fieldLabel + "</label>" +
                            "</div>" +
                            "</div>" +
                            "<div class=\"form-group form-group-fix\">" +
                            "<div class=\"col-sm-12 text-center\">";
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28\"  value=\""+ (supplementInfo.originalValue ? supplementInfo.originalValue : "") +"\""+ (field == "licensePlateNo" ? ' disabled="disabled"':"") +">";
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
                        quote.editFlag = true;
                        quote.action.setSupplementParams();
                        quote.action.quoteAll();
                        position == popup.mould.first ? popup.mask.hideFirstMask(true) : popup.mask.hideSecondMask(true);
                    }
                });
            }
        },
        setSupplementParams: function(){
            for(var k in quote.supplementParams){
                var paramVal = $("#popover_normal_input input[name='" + k +"']").val();
                if(paramVal !="" && paramVal !=undefined)
                    quote.supplementParams[k] = $("#popover_normal_input input[name='" + k +"']").val();
            }
        },
        getReqJson: function(companyId, isRenewal){
            var reqJson = {
                source:           quote.source,
                sourceId:         quote.sourceId,
                sourceIdStr:      quote.sourceId,
                userId:           quote.user.userId,
                auto: {
                    licensePlateNo:   quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).licensePlateNo : quote.licensePlateNo,
                    vinNo:            quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).vinNo : "",
                    engineNo:         quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).engineNo : "",
                    owner:            quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).owner : "",
                    enrollDate:       quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).enrollDate : "",
                    identity:         quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).identity : "",
                    insuredIdNo:      quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).insuredIdNo : "",
                    autoType: {
                        code:         quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).code : "",
                        seats:        quote.autoInfo.get(companyId) ? (quote.autoInfo.get(companyId).seats ? quote.autoInfo.get(companyId).seats : "") : "",
                        supplementInfo: {
                            autoModel:quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).autoModel : "",
                            commercialStartDate:quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).commercialStartDate : "",
                            compulsoryStartDate:quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).compulsoryStartDate : "",
                            commercialCaptchaImage:quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).commercialCaptchaImage : "",
                            compulsoryCaptchaImage:quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).compulsoryCaptchaImage : ""
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
                        autoModel:quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).autoModel : "",
                        commercialStartDate:quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).commercialStartDate : "",
                        compulsoryStartDate:quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).compulsoryStartDate : "",
                        commercialCaptchaImage:quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).commercialCaptchaImage : "",
                        compulsoryCaptchaImage:quote.autoInfo.get(companyId) ? quote.autoInfo.get(companyId).compulsoryCaptchaImage : ""
                    }
                }
            };
            if(quote.editFlag){
                reqJson.auto.vinNo = $("#popover_normal_input input[name='vinNo']").val();
                reqJson.auto.engineNo = $("#popover_normal_input input[name='engineNo']").val();
                reqJson.auto.owner = $("#popover_normal_input input[name='owner']").val();
                reqJson.auto.enrollDate = $("#popover_normal_input input[name='enrollDate']").val();
                reqJson.auto.identity = $("#popover_normal_input input[name='identity']").val();
                reqJson.auto.autoType.code = $("#popover_normal_input input[name='code']").val();
                for(var k in quote.supplementParams){
                    reqJson.additionalParameters.supplementInfo[k] = $("#popover_normal_input input[name='"+ k +"']").val();
                }
            }
            return reqJson;
        },
        setSupplementAdditionalParameters: function (reqJson) {
            var additionalParameters = reqJson.additionalParameters.supplementInfo;
            for(var k in quote.supplementParams){
                additionalParameters[k] = quote.supplementParams[k];
            }
        },
        setReqJsonPackage: function(reqJson, isRenewal){
            if(!isRenewal) {
                reqJson.insurancePackage = {
                    compulsory: quote.insurancePackage.compulsory,
                    autoTax: quote.insurancePackage.autoTax,
                    thirdPartyAmount: quote.insurancePackage.thirdPartyAmount,
                    thirdPartyIop: quote.insurancePackage.thirdPartyIop,
                    damage: quote.insurancePackage.damage,
                    damageIop: quote.insurancePackage.damageIop,
                    theft: quote.insurancePackage.theft,
                    theftIop: quote.insurancePackage.theftIop,
                    engine: quote.insurancePackage.engine,
                    engineIop: quote.insurancePackage.engineIop,
                    glass: quote.insurancePackage.glass,
                    glassTypeId: quote.insurancePackage.glassType,
                    glassType: {
                        id: quote.insurancePackage.glassType
                    },
                    driverAmount: quote.insurancePackage.driverAmount,
                    driverIop: quote.insurancePackage.driverIop,
                    passengerAmount: quote.insurancePackage.passengerAmount,
                    passengerIop: quote.insurancePackage.passengerIop,
                    spontaneousLoss: quote.insurancePackage.spontaneousLoss,
                    spontaneousLossIop: quote.insurancePackage.spontaneousLossIop,
                    unableFindThirdParty: quote.insurancePackage.unableFindThirdParty,
                    designatedRepairShop: quote.insurancePackage.designatedRepairShop,
                    scratchAmount: quote.insurancePackage.scratchAmount,
                    scratchIop: quote.insurancePackage.scratchIop,
                    iopTotal: quote.insurancePackage.iopTotal
                };
            }
        }
    },
    interface: {
        convertInsurancePackage: function(insurancePackage) {
            return {
                compulsory:       insurancePackage.compulsory,
                autoTax:          insurancePackage.autoTax,
                thirdPartyAmount: insurancePackage.thirdPartyAmount,
                thirdPartyIop:    insurancePackage.thirdPartyIop,
                damage:           insurancePackage.damage,
                damageIop:        insurancePackage.damageIop,
                theft:            insurancePackage.theft,
                theftIop:         insurancePackage.theftIop,
                engine:           insurancePackage.engine,
                engineIop:        insurancePackage.engineIop,
                glass:            insurancePackage.glass,
                glassType:        insurancePackage.glassType ? {id: insurancePackage.glassType} : null,
                driverAmount:     insurancePackage.driverAmount,
                driverIop:        insurancePackage.driverIop,
                passengerAmount:  insurancePackage.passengerAmount,
                passengerIop:     insurancePackage.passengerIop,
                spontaneousLoss:  insurancePackage.spontaneousLoss,
                spontaneousLossIop:insurancePackage.spontaneousLossIop,
                unableFindThirdParty:insurancePackage.unableFindThirdParty,
                designatedRepairShop:insurancePackage.designatedRepairShop,
                scratchAmount:    insurancePackage.scratchAmount,
                scratchIop:       insurancePackage.scratchIop,
                iopTotal:         insurancePackage.iopTotal
            };
        },
        getHeaderMap: function() {
            var headerMap = new Map();
            headerMap.put("uniqueId", quote.uniqueId);
            return headerMap;
        },
        getUniqueId: function(callBackMethod) {
            common.ajax.getByAjax(true, "get", "html", "/orderCenter/quote/uniqueId", {},
                function(data) {
                    callBackMethod(data);
                },
                function() {
                    popup.mould.popTipsMould(true, "获取流程唯一标识异常，无法继续报价！", popup.mould.first, popup.mould.error, "", "57%", function() {
                        window.close();
                    });
                }
            );
        },
        sendMessage: function(companyId, quoteJson, insuranceResult, callBackMethod) {
            common.ajax.getByAjaxWithJsonAndHeader(true, "post", "json", "/orderCenter/quote/" + companyId + "/sms",
                {
                    insurancePackage:      quoteJson.insurancePackage,
                    insuranceCompany:      quoteJson.insuranceCompany,
                    applicant: {
                        id:                quote.user.userId,
                        mobile:            quote.user.mobile
                    },
                    area:                  quoteJson.area,
                    premium:               quoteJson.total.base,
                    compulsoryPremium:     insuranceResult.compulsory,
                    autoTax:               insuranceResult.autoTax,
                    thirdPartyPremium:     insuranceResult.thirdPartyPremium,
                    thirdPartyAmount:      quoteJson.insurancePackage.thirdPartyAmount,
                    damagePremium:         insuranceResult.damagePremium,
                    theftPremium:          insuranceResult.theftPremium,
                    enginePremium:         insuranceResult.enginePremium,
                    driverPremium:         insuranceResult.driverPremium,
                    driverAmount:          quoteJson.insurancePackage.driverAmount,
                    passengerPremium:      insuranceResult.passengerPremium,
                    passengerAmount:       quoteJson.insurancePackage.passengerAmount,
                    spontaneousLossPremium:insuranceResult.spontaneousLossPremium,
                    glassPremium:          insuranceResult.glassPremium,
                    scratchPremium:        insuranceResult.scratchPremium,
                    scratchAmount:         quoteJson.insurancePackage.scratchAmount,
                    iopTotal:              insuranceResult.iop,
                    damageIop:             insuranceResult.damageIop,
                    thirdPartyIop:         insuranceResult.thirdPartyIop,
                    theftIop:              insuranceResult.theftIop,
                    engineIop:             insuranceResult.engineIop,
                    driverIop:             insuranceResult.driverIop,
                    passengerIop:          insuranceResult.passengerIop,
                    scratchIop:            insuranceResult.scratchIop,
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
        checkAgent: function(userId, callBackMethod) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/agent/user/" + userId, {},
                function(data) {
                    callBackMethod(data);
                },
                function() {
                    popup.mould.popTipsMould(true, "判断用户类型异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        validInternalUser: function(callBackMethod) {
            common.ajax.getByAjaxWithHeader(false, "post", "json", "/orderCenter/quote/" + quote.user.userId + "/login",
                {
                    channelId: quote.sourceChannel,
                    source: quote.source,
                    sourceId: quote.sourceId
                },
                function(data) {
                    if(!data.pass) {
                        popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.warning, "", "57%", function() {
                            window.close();
                        });
                        return;
                    }
                    callBackMethod(data.message);
                },
                function() {
                    popup.mould.popTipsMould(true, "用户校验异常！", popup.mould.first, popup.mould.error, "", "57%", function() {
                        window.close();
                    });
                },
            quote.interface.getHeaderMap());
        },
        getCompanies: function(licensePlateNo, callBackMethod) {
            common.ajax.getByAjaxWithHeader(true, "get", "json", "/orderCenter/quote/" + licensePlateNo + "/companies", {},
                function(data) {
                    if(!data.pass) {
                        popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                        return;
                    }
                    callBackMethod(data.message);
                },
                function() {
                    popup.mould.popTipsMould(true, "获取保险公司接口异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                },
            quote.interface.getHeaderMap());
        },
        getQuote: function(companyId, isRenewal) {
            if (quote.autoInfo.get(companyId) && quote.autoInfo.get(companyId).licensePlateNo) {
                quote.autoInfo.get(companyId).licensePlateNo = quote.autoInfo.get(companyId).licensePlateNo.toUpperCase();
            }
            var reqJson = quote.action.getReqJson(companyId, isRenewal);
            quote.action.setReqJsonPackage(reqJson, isRenewal);
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
        },
        saveQuote: function(companyId, insurancePackage, areaId,quoteRecordKey, callBackMethod) {
            common.ajax.getByAjaxWithJsonAndHeader(true, "post", "json", "/orderCenter/quote/" + companyId + "/quoteRecord",
                {
                    insurancePackage:      insurancePackage,
                    applicant: {
                        id:                quote.user.userId
                    },
                    area: {
                        id:                areaId
                    },
                    insuranceCompany: {
                        id:                companyId
                    },
                    auto:{
                        licensePlateNo: quote.licensePlateNo
                    },
                    quoteRecordKey:quoteRecordKey
                },
                function(data) {
                    if(!data.pass) {
                        popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                        var $selectBtn = $("#quote_content #bottom_btn_tr .company-" + companyId + " .btn");
                        $selectBtn.attr("disabled", false);
                        return;
                    }
                    callBackMethod(data.message);
                },
                function() {
                    popup.mould.popTipsMould(true, "保存报价异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                    var $selectBtn = $("#quote_content #bottom_btn_tr .company-" + companyId + " .btn");
                    $selectBtn.attr("disabled", false);
                },
            quote.interface.getHeaderMap());
        },
        doQuoteValidation: function(callBackMethod) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/quote/" + quote.source + "/validation", {sourceId: quote.sourceId},
                function(data) {
                    callBackMethod(data);
                },
                function() {
                    console.log("报价前校验车辆信息六要素异常！");
                }
            );
        },
        threewayCall:function(mobile){
            common.getByAjax(true, "get", "json","/orderCenter/telMarketingCenter/telMarketer/call" ,{customerNumber:mobile},
                function(data) {
                    if (data.pass) {
                        //popup.mould.popTipsMould(true, "拨打成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                    } else {
                        popup.mould.popTipsMould(false,data.message, popup.mould.first, popup.mould.error, "", "53%", null);
                        window.parent.$("#toSave").attr("disabled",false);
                    }
                },
                function() {
                    popup.mould.popTipsMould(false,"发生异常,获取不到data,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                    window.parent.$("#toSave").attr("disabled",false);
                }
            );
        },
    }
};

$(function() {
     if (!common.permission.hasPermission("or060104") || !common.permission.isAbleCall()) {
        $("#callBtn").hide();
    }
    popup.insertHtml($("#popupHtml"));
    var id = common.getUrlParam("id");
    var source = common.getUrlParam("source");
    var renewalFlag = common.getUrlParam("renewalFlag");
  //  quote.init.init(id, source, renewalFlag);
    $("input[name='item_kind_check']").each(function(index, checkBox) {
        $(checkBox).unbind("change").bind({
            change: function() {
                if($(this).attr("id")=='compulsoryChk'){
                    $("#autoTaxChk").prop("checked",this.checked);
                    quote.action.switchChkAction($("#autoTaxChk"), $("#autoTaxChk").is(':checked'));
                }else if($(this).attr("id")=='autoTaxChk'){
                    $("#compulsoryChk").prop("checked",this.checked);
                    quote.action.switchChkAction($("#compulsoryChk"), $("#compulsoryChk").is(':checked'));
                }
                quote.action.switchChkAction(this,$(this).is(':checked'));
                $("#item_kind_count").text($("input[name='item_kind_check']:checked").length);
            }
        });
    });

    $(".quote-all").unbind("click").bind({
        click: function() {
            if (!quote.action.validItemKindPolicy()) {
                return;
            }
            if (quote.companies.length > 0) {
                quote.action.quoteAll();
            } else {
                quote.action.getAllCompanies();
            }
        }
    });

    $(".change-items").unbind("click").bind({
        click: function() {
            quote.action.enableAllKinds();
        }
    });

    $("#addCompany").unbind("click").bind({
        click: function() {
            if (!quote.action.validItemKindPolicy()) {
                return;
            }
            quote.action.getAllCompanies();
        }
    });

    $("#editSupplement").unbind("click").bind({
        click: function() {
            if (!quote.action.validItemKindPolicy()) {
                return;
            }
            quote.action.getSupplementParams(false);
        }
    });

    /* 拨打电话 */
    $("#callBtn").unbind("click").bind({
        click:function(){
            $("#callBtn").attr("disabled",true);
            setTimeout('$("#callBtn").attr("disabled",false)' ,5000);
            quote.interface.threewayCall($("#mobile_text").text());
        }
    });
});
