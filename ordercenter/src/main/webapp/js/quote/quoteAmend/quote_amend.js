var quote = {
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
        identityType: {id: ""}
    },
    insure: {
        name: "",
        identity: "",
        identityType: {id: ""}
    },
    owner: {
        name: "",
        identity: ""
    },
    insurancePackage: {},
    insuranceCompany: {},
    institutionRebate: {
        commercialRebate: "",
        compulsoryRebate: ""
    },
    companyUrlMap: new Map(),
    companyImgMap: new Map(),
    quoteItemMap: new Map(),
    quoteRecordKey: new Map(),
    defaultTabSize: 200,
    successItemsContent: "",
    companies: [],
    companiesContent: "",
    newQuoteRecordId: "",
    newCompanyId: "",
    premiumJson: "",
    //参数集合，后期将用到的参数转为map
    param: new Map(),
    init: {
        init: function (id, source) {
            this.initQuoteDiv();
            this.initQuote(id, source);
        },
        initQuoteDiv: function () {
            var successItemsContent = $("#item_kind_show");
            if (successItemsContent.length > 0) {
                quote.successItemsContent = successItemsContent.html();
                successItemsContent.remove();
            }
        },
        initQuote: function (id, source) {
            quote.orderId = id;
            quote.source = source;
            quote.action.getQuoteInfo(quote.orderId, function (data) {
                var dataList = data.recordList;
                quote.init.initParams(dataList[dataList.length - 1]);
                quoteRebate.channel.isAgentChannel();
                var validation = quoteValidation.validQuoteInitParams(quote);
                if (!validation.flag) {
                    popup.mould.popTipsMould(true, validation.msg, popup.mould.first, popup.mould.warning, "", "57%", function () {
                        window.close();
                    });
                    return;
                }
                quote.interface.validInternalUser(function (validJson) {
                    validJson = JSON.parse(validJson);
                    if (validJson.code != 200) {
                        popup.mould.popTipsMould(true, "很抱歉，您没有报价权限，请联系管理员！", popup.mould.first, popup.mould.error, "", "57%", function () {
                            window.close();
                        });
                        return;
                    }
                    quote.uniqueId = validJson.data.token;
                    console.log("uniqueId: " + quote.uniqueId);
                    quote.init.initPage(dataList[dataList.length - 1]);
                });

                $.each(dataList, function (index, model) {
                    quote.init.setPrevItemPremium(model);
                    if (dataList.length == 1 || (dataList.length > 1 && index == 1))
                        quote.init.initAmount(data, model.id);
                });
                //quote.init.setNewItemPremium(dataList[dataList.length-1]);

                $(".quote-submit").unbind("click").bind({
                    click: function () {
                        //$(this).attr("disabled","true");
                        quote.action.submit(function (data) {

                            $("#kind_tab").find("input[type='checkbox']").attr("disabled", true);
                            $("#kind_tab").find("select").attr("disabled", true);

                            quote.newQuoteRecordId = data;
                            quote.action.orderInfo(function (order) {
                                quote.owner.name = order.ownerName;
                                quote.owner.identity = order.ownerIdentity;
                                quote.insured.name = order.insuredName;
                                quote.insured.identity = order.insuredIdentity;
                                quote.insured.identityType.id = order.insuredIdentityTypeId;
                                quote.insure.name = order.applicantName;
                                quote.insure.identity = order.applicantIdNo;
                                quote.insure.identityType.id = order.applicantIdentityTypeId;
                                //用户类型
                                var $orderAddContent = $("#order_add_content");
                                $orderAddContent.show();
                                var paidAmount = 0;
                                quote.interface.getPaidAmount(quote.newCompanyId,
                                    function (data) {
                                        console.log("get paidAmount api return json: " + data.message);
                                        var paidAmountJson = JSON.parse(data.message);
                                        if (paidAmountJson.code == 200) {
                                            var paidAmount = paidAmountJson.data ? paidAmountJson.data : quote.premiumJson.sumPremium;
                                        } else {
                                            popup.mould.popTipsMould(true, paidAmountJson.message, popup.mould.first, popup.mould.warning, "", "", null);
                                            return false;
                                        }
                                    },
                                    function () {
                                        popup.mould.popTipsMould(true, "获取直减金额接口异常！", popup.mould.first, popup.mould.warning, "", "", null);
                                        return false;
                                    }
                                );
                                quote.interface.checkAgent(quote.user.userId, function (agentMap) {
                                    if (agentMap.agent) {
                                        quote.user.type = "agent";
                                        quote.user.id = agentMap.agent.id;
                                        quote.user.name = agentMap.agent.name;
                                        quote.user.rebate = agentMap.agent.rebate;
                                        $("#preferentialBtn").hide();
                                        $("#preferentialLimitBtn").hide();
                                    } else {
                                        quote.user.type = "normal";
                                    }
                                    quote.quoteOrder = new QuoteOrder(quote.user, quote.newQuoteRecordId, quote.insured, quote.insure, quote.owner, quote.premiumJson, order.areaId, order.sourceId, paidAmount, quote.newCompanyId, false);
                                    //返回上一步的点击事件
                                    $("#returnBackBtn").unbind("click").bind({
                                        click: function () {
                                            $orderAddContent.hide();
                                            quote.init.restoreQuote();
                                            orderPremium.initOrderPremium(true);
                                            orderPremium.clearPreferential();
                                            quoteGift.clearUserGift();
                                            $("#rebateReason").clean().hide();
                                            //  quote.param.remove("rebate");
                                        }
                                    });

                                    orderPremium.premium.paidAmount = quote.premiumJson.sumPremium;
                                    $('#payType').val("online").attr("readOnly", true);
                                    quote.init.setPdReadOnly();
                                    $("#payType option[value='offline']").remove();
                                    $('.order-online').show();
                                    quote.init.showAmendInfo(common.tools.formatMoney(quote.premiumJson.sumPremium, 2));
                                });
                            })
                        })
                    }
                });
            });
        },
        initParams: function (data) {
            quote.licensePlateNo = data.auto.licensePlateNo;
            quote.user.userId = data.applicant.id;
            quote.user.mobile = data.applicant.mobile;
            quote.insured.name = data.insuredName;
            quote.insured.identity = data.insuredIdNo;
            quote.insure.name = data.applicantName;
            quote.insure.identity = data.applicantIdNo;
            quote.sourceChannel = data.channel != null ? data.channel.id : "";
        },
        initPage: function (data) {
            $("#mobile_text").text(quote.user.mobile);
            $("#license_plate_no_text").text(quote.licensePlateNo);
            // $("#source_channel_text").text(this.getChannelText());
            quote.init.initCompaniesContent();
            this.fillContent(data);
        },
        initCompaniesContent: function () {
            quote.interface.getCompanies(quote.licensePlateNo, function (message) {
                var companyJson = JSON.parse(message);
                if (companyJson.code != 200) {
                    popup.mould.popTipsMould(true, companyJson.message, popup.mould.first, popup.mould.warning, "", "57%", null);

                } else {
                    var companies = companyJson.data;
                    var imgContext = "";
                    var renewalImgContent = "";
                    var renewalIndex = 0;
                    $.each(companies, function (index, company) {
                        //获取活动所支持保险公司与正常的获取保险公司接口返回json格式存在差异
                        quote.companyUrlMap.put(company.id, common.tools.checkToEmpty(company.websiteUrl));
                        quote.companyImgMap.put(company.id, common.tools.checkToEmpty(company.logoUrl));
                        if (index % 3 == 0) {
                            imgContext += "<div class=\"form-group\">";
                        }
                        imgContext += "<div class=\"col-sm-4\">" +
                            "<div class=\"radio\"><label><input class=\"text-height-60\" name=\"companyChk\" type=\"radio\" value=\"" + company.id + "\"><img width=\"96\" height=\"57\" src=\"" + company.logoUrl + "\"></label></div>" +
                            "</div>";
                        if ((index + 1) % 3 == 0) {
                            imgContext += "</div>";
                        }
                    });
                    $("#companies_content #company_list").html(imgContext);
                    var companiesContent = $("#companies_content");
                    if (companiesContent.length > 0) {
                        quote.companiesContent = companiesContent.html();
                        companiesContent.remove();
                    }
                    $(".quote-all").attr("disabled", false);
                    // $("#addCompany").attr("disabled", false);
                }
            });
        },
        initAmount: function (data, quoteRecordId) {
            var $quoteContent = $("#quote_content_prev");
            var quoteTd = $quoteContent.find(".bottom_btn_tr").find(".quote-" + quoteRecordId);
            var amountText = "<div class=\"text-left\">" +
                "<span>优惠(减免优惠的钱)： <span>" + common.tools.formatMoney(data.giftAmount, 2) + "</span>元</br></span>" +
                "<span>礼品： <span>" + data.giftDetail + "</span></br></span>" +
                "<span>应付金额： <span id='latestPayableAmount'>" + common.tools.formatMoney(data.payableAmount, 2) + "</span>元</br></span>" +
                "<span>实付金额： <span id='latestPaidAmount'>" + common.tools.formatMoney(data.paidAmount, 2) + "</span>元</br></span>" +
                "</div>";
            quoteTd.html(amountText);
        },
        fillContent: function (data) {
            if (data) {
                quote.institutionRebate.commercialRebate = data.commercialRebate;
                quote.institutionRebate.compulsoryRebate = data.compulsoryRebate;
                $("#quote_id").val(data.id);
                if (data.insurancePackage == null) {
                    return;
                }
                $("#compulsoryPremium").val(data.compulsoryPremium);
                $("#autoTax").val(data.autoTax);
                $("#commercialPremium").val(data.commercialPremium);
                $("#quoteTotal").html(data.compulsoryPremium + data.autoTax + data.commercialPremium);
                $(".premiumPd").val(0).attr("disabled", true);
                $("#kind_tab").find(".tab-title").html("<a href=\"" +
                    data.insuranceCompany.websiteUrl + "\" target=\"_blank\"><img class=\"waiting-img\" width=\"96\" height=\"45\" src=\"" +
                    data.insuranceCompany.logoUrl + "\"></a>");

                if (data.insurancePackage.compulsory == true) {
                    $("#compulsoryChk").attr("checked", 'true');
                    $("#compulsoryChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                    $("#compulsoryPremiumPd").val(data.compulsoryPremium).attr("disabled", false);
                }
                if (data.insurancePackage.autoTax == true) {
                    $("#autoTaxChk").attr("checked", 'true');
                    $("#autoTaxChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                    $("#autoTaxPremiumPd").val(data.autoTax).attr("disabled", false);
                }
                if (data.insurancePackage.thirdPartyAmount != null && data.insurancePackage.thirdPartyAmount != 0) {
                    $("#thirdPartyChk").attr("checked", 'true');
                    $("#thirdPartyChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.thirdPartyAmount).show().siblings(".no").hide();
                    $("#thirdPartyPremiumPd").val(data.thirdPartyPremium).attr("disabled", false);
                }
                if (data.insurancePackage.damage == true) {
                    $("#damageChk").attr("checked", 'true');
                    $("#damageChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                    $("#damagePremiumPd").val(data.damagePremium).attr("disabled", false);
                }
                if (data.insurancePackage.driverAmount != null && data.insurancePackage.driverAmount != 0) {
                    $("#driverChk").attr("checked", 'true');
                    $("#driverChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.driverAmount).show().siblings(".no").hide();
                    $("#driverPremiumPd").val(data.driverPremium).attr("disabled", false);
                }
                if (data.insurancePackage.passengerAmount != null && data.insurancePackage.passengerAmount != 0) {
                    $("#passengerChk").attr("checked", 'true');
                    $("#passengerChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.passengerAmount).show().siblings(".no").hide();
                    $("#passengerPremiumPd").val(data.passengerPremium).attr("disabled", false);
                }
                if (data.insurancePackage.engine == true) {
                    $("#engineChk").attr("checked", 'true');
                    $("#engineChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.engine).show().siblings(".no").hide();
                    $("#enginePremiumPd").val(data.enginePremium).attr("disabled", false);
                }
                if (data.insurancePackage.glass == true) {
                    $("#glassChk").attr("checked", 'true');
                    $("#glassChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.glassType.id).show().siblings(".no").hide();
                    $("#glassPremiumPd").val(data.glassPremium).attr("disabled", false);
                }
                if (data.insurancePackage.scratchAmount != null && data.insurancePackage.scratchAmount != 0) {
                    $("#scratchChk").attr("checked", 'true');
                    $("#scratchChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.scratchAmount).show().siblings(".no").hide();
                    $("#scratchPremiumPd").val(data.scratchPremium).attr("disabled", false);

                }
                if (data.insurancePackage.theft == true) {
                    $("#theftChk").attr("checked", 'true');
                    $("#theftChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                    $("#theftPremiumPd").val(data.theftPremium).attr("disabled", false);
                    $("#theftAmount").val(data.theftAmount).attr("disabled", false);
                }
                if (data.insurancePackage.spontaneousLoss == true) {
                    $("#spontaneousLossChk").attr("checked", 'true');
                    $("#spontaneousLossChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                    $("#spontaneousLossPremiumPd").val(data.spontaneousLossPremium).attr("disabled", false);
                    $("#spontaneousLossAmount").val(data.spontaneousLossAmount).attr("disabled", false);
                }
                if (data.insurancePackage.unableFindThirdParty == true) {
                    $("#unableFindThirdPartyChk").attr("checked", 'true');
                    $("#unableFindThirdPartyChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.unableFindThirdParty).show().siblings(".no").hide();
                    $("#unableFindThirdPartyPremiumPd").val(data.unableFindThirdPartyPremium).attr("disabled", false);
                }
                //指定专修厂险
                if (data.insurancePackage.designatedRepairShop == true) {
                    $("#designatedRepairShopChk").attr("checked", 'true');
                    $("#designatedRepairShopChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.designatedRepairShop).show().siblings(".no").hide();
                    $("#designatedRepairShopPremiumPd").val(data.designatedRepairShopPremium).attr("disabled", true);
                }


                if (data.insurancePackage.thirdPartyIop == true || data.insurancePackage.damageIop == true || data.insurancePackage.theftIop == true
                    || data.insurancePackage.engineIop == true || data.insurancePackage.driverIop == true || data.insurancePackage.passengerIop == true
                    || data.insurancePackage.scratchIop == true || data.insurancePackage.spontaneousLossIop == true) {
                    $("#iopChk").attr("checked", 'true');
                    $("#iopChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                    $("#iopPremiumPd").val(data.iopTotal).attr("disabled", false);
                }

                //quote.quoteItemMap.put()

            }
            $("#kind_tab").find(".total").html(quote.init.setTotalPremium(data));
            $("#item_kind_count").text($("input[name='item_kind_check']:checked").length);
            $("input[name='item_kind_check']").each(function (index, checkBox) {
                $(checkBox).unbind("change").bind({
                    change: function () {
                        quote.action.switchChkAction(this, $(this).is(':checked'));
                        $("#item_kind_count").text($("input[name='item_kind_check']:checked").length);
                        //设置总保费
                        quote.init.setTotalPremiumValue(quote.companyId, data);
                    }
                });
            });


            $(".premiumPd").each(function (index) {
                $(this).unbind("blur").bind({
                    blur: function () {
                        //校验保费范围
                        var name = $(this).attr("id").substr(0, $(this).attr("id").indexOf("Pd"));
                        if (!quoteValidation.validPremiumRange($(this), data[name])) {
                            return;
                        }
                        //设置总保费
                        quote.init.setTotalPremiumValue(quote.companyId, data);
                    }
                });
            });


            // data.insuranceCompany.id=15000;
            // this.setItemPremium(data);
        },

        setItemPremium: function ($quoteContent, data) {
            var companyId = data.insuranceCompany.id;
            var quoteRecordId = data.id;
            quote.newCompanyId = companyId;
            quote.companies.push(companyId);
            $quoteContent.find(".top_title_tr").append("<td colspan=\"2\" class=\"tab-title quote-" + quoteRecordId + "\"><a href=\"" +
                data.insuranceCompany.websiteUrl + "\" target=\"_blank\"><img class=\"waiting-img\" width=\"96\" height=\"45\" src=\"" +
                data.insuranceCompany.logoUrl + "\"></a></td>");
            $quoteContent.find(".middle_item_tr").append("<td colspan=\"2\" class=\"item-kind quote-" + quoteRecordId + "\"></td>");
            $quoteContent.find(".bottom_total_tr").append("<td colspan=\"2\" class=\"item-total quote-" + quoteRecordId + "\"></td>");
            $quoteContent.find(".bottom_btn_tr").append("<td colspan=\"2\" class=\"item-btn quote-" + quoteRecordId + "\" style='height: 177px;'></td>");
            var itemTd = $quoteContent.find(".middle_item_tr .quote-" + quoteRecordId);
            var totalTd = $quoteContent.find(".bottom_total_tr .quote-" + quoteRecordId);
            //var btnTd=$("#quote_content #bottom_btn_tr").find(".company-" + companyId);

            //var itemTd = $("#quote_content_prev .middle_item_tr .quote-" + quoteRecordId);
            itemTd.html(quote.successItemsContent);
            if (data.insurancePackage.compulsory == true) {
                itemTd.find(".compulsory").text("缴纳");
                itemTd.find(".compulsoryPremium").text(data.compulsoryPremium);
            }
            if (data.insurancePackage.autoTax == true) {
                itemTd.find(".autoTax").text("缴纳");
                itemTd.find(".autoTaxPremium").text(data.autoTax);
            }
            if (data.insurancePackage.thirdPartyAmount != null && data.insurancePackage.thirdPartyAmount != 0) {
                itemTd.find(".thirdPartyAmount").text(data.thirdPartyAmount);
                itemTd.find(".thirdPartyPremium").text(data.thirdPartyPremium);
            }
            if (data.insurancePackage.damage == true) {
                itemTd.find(".damageAmount").text(data.damageAmount);
                itemTd.find(".damagePremium").text(data.damagePremium);
            }
            if (data.insurancePackage.driverAmount != null && data.insurancePackage.driverAmount != 0) {
                itemTd.find(".driverAmount").text(data.driverAmount);
                itemTd.find(".driverPremium").text(data.driverPremium);
            }
            if (data.insurancePackage.passengerAmount != null && data.insurancePackage.passengerAmount != 0) {
                itemTd.find(".passengerAmount").text(data.passengerAmount);
                itemTd.find(".passengerPremium").text(data.passengerPremium);
            }
            if (data.insurancePackage.engine == true) {
                itemTd.find(".engine").text("投保");
                itemTd.find(".enginePremium").text(data.enginePremium);
            }
            if (data.insurancePackage.glass == true) {
                itemTd.find(".glassType").text(data.insurancePackage.glassType.name);
                itemTd.find(".glassPremium").text(data.glassPremium);
            }
            if (data.insurancePackage.scratchAmount != null && data.insurancePackage.scratchAmount != 0) {
                itemTd.find(".scratchAmount").text(data.scratchAmount);
                itemTd.find(".scratchPremium").text(data.scratchPremium);
            }
            if (data.insurancePackage.theft == true) {
                itemTd.find(".theftAmount").text(data.theftAmount);
                itemTd.find(".theftPremium").text(data.theftPremium);
            }
            if (data.insurancePackage.spontaneousLoss == true) {
                itemTd.find(".spontaneousLossAmount").text(data.spontaneousLossAmount);
                itemTd.find(".spontaneousLossPremium").text(data.spontaneousLossPremium);
            }
            if (data.insurancePackage.unableFindThirdParty == true) {
                itemTd.find(".unableFindThirdParty").text("投保");
                itemTd.find(".unableFindThirdPartyPremium").text(data.unableFindThirdPartyPremium);
            }
            //指定专修厂险
            if (data.insurancePackage.designatedRepairShop == true) {
                itemTd.find(".designatedRepairShop").text("投保");
                itemTd.find(".designatedRepairShopPremium").text(data.designatedRepairShopPremium);
            }

            if (data.insurancePackage.thirdPartyIop == true || data.insurancePackage.damageIop == true || data.insurancePackage.theftIop == true
                || data.insurancePackage.engineIop == true || data.insurancePackage.driverIop == true || data.insurancePackage.passengerIop == true
                || data.insurancePackage.scratchIop == true || data.insurancePackage.spontaneousLossIop == true) {
                itemTd.find(".iop").text("投保");
                itemTd.find(".iopPremium").text(data.iopTotal);
            }

            totalTd.html(this.setTotalPremium(data));

        },
        setPrevItemPremium: function (data) {
            var $quoteContent = $("#quote_content_prev");
            this.setItemPremium($quoteContent, data, false);
            $quoteContent.show().find("table").width(quote.companies.length * quote.defaultTabSize);
        },
        setTotalPremium: function (data) {
            quote.premiumJson = this.getToTalPremium();
            var autoTax = common.tools.formatMoney(data.autoTax, 2);
            var compulsory = common.tools.formatMoney(data.compulsoryPremium, 2);
            var premium = common.tools.formatMoney(data.premium, 2);
            var noTax = common.tools.formatMoney(parseFloat(compulsory) + parseFloat(premium), 2);
            var totalPremium = common.tools.formatMoney(data.totalPremium, 2);
            return "<div class=\"text-left\">" +
                "<span>车船税 <span id='autoTaxReality'>" + autoTax + "元</span></br></span>" +
                "<span>不含车船税 <span id='noAutoTaxReality'>" + noTax + "元</span></br></span>" +
                "<span>交强险 <span id='compulsoryReality'>" + compulsory + "元</span></br></span>" +
                "<span>商业险 <span id='commercialReality'>" + premium + "元</span></br></span>" +
                "<span>总计 <span id='sumPremiumReality'>" + totalPremium + "元</span></br></span>" +
                "</div>";
        },
        setTotalPremiumValue: function (companyId, data) {
            quote.premiumJson = this.getToTalPremium();
            var noTax = common.tools.formatMoney(parseFloat(data.compulsoryPremium) + parseFloat(data.premium), 2);
            $("#kind_tab").find(".change").remove();
            $("#kind_tab").find("#autoTaxReality").html(common.tools.formatMoney(quote.premiumJson.autoTax, 2) + "元").after(this.getTrend(data.autoTax, quote.premiumJson.autoTax));
            $("#kind_tab").find("#noAutoTaxReality").html(common.tools.formatMoney(quote.premiumJson.noTax, 2) + "元").after(this.getTrend(noTax, quote.premiumJson.noTax));
            $("#kind_tab").find("#compulsoryReality").html(common.tools.formatMoney(quote.premiumJson.compulsory, 2) + "元").after(this.getTrend(data.compulsoryPremium, quote.premiumJson.compulsory));
            $("#kind_tab").find("#commercialReality").html(common.tools.formatMoney(quote.premiumJson.commercial, 2) + "元").after(this.getTrend(data.premium, quote.premiumJson.commercial));
            $("#kind_tab").find("#sumPremiumReality").html(common.tools.formatMoney(quote.premiumJson.sumPremium, 2) + "元").after(this.getTrend(data.totalPremium, quote.premiumJson.sumPremium));
        },
        setPdReadOnly: function () {
            $("input[name='item_kind_check']:checked").each(function (index, checkBox) {
                $("#" + this.id.substr(0, this.id.length - 3) + "PremiumPd").attr("disabled", "disabled");
            });
        },
        showAmendInfo: function (sumAmount) {
            var latestPaidAmount = $("#latestPaidAmount").html();
            if (quote.user.type == "agent" && quoteRebate.rebateAmount == 0) {
                quote.action.getAgentRebateAmount(function (amount) {
                    sumAmount = sumAmount - Number(amount);
                })
            } else if (quoteRebate.rebateAmount != 0) {
                //sumAmount=sumAmount-quoteRebate.rebateAmount;
                //orderPremium.premium.paidAmount=orderPremium.premium.paidAmount-quoteRebate.rebateAmount;
                //是折扣渠道的话，展示“渠道折扣xx元”，否则展示“直减优惠xx元”
                if (quoteRebate.isAgentChannel)
                    $("#rebateReason").show().text("奖励金" + quoteRebate.rebateAmount + "元");
                else
                    $("#rebateReason").show().text("直减优惠" + quoteRebate.rebateAmount + "元");
            }
            sumAmount = Number(common.tools.formatMoney((sumAmount), 2));
            if (Number(latestPaidAmount) < sumAmount) {
                $("#cheChePayInfo").removeClass("none");
                $("#cheChePayTitle").html("需支付金额");
                $("#cheChePaySumPremium").html(common.tools.formatMoney(sumAmount - Number(latestPaidAmount), 2));
                $("#cheChePay").parent().css("display", "inline-block");
                if (Number(latestPaidAmount) <= 0)
                    $("#cheChePay").parent().css("display", "none");
            } else if (Number(latestPaidAmount) > sumAmount) {
                $("#cheChePayTitle").html("需退款金额");
                $("#cheChePay").parent().css("display", "none");
                $("#cheChePayInfo").removeClass("none");
                $("#cheChePaySumPremium").html(common.tools.formatMoney(Number(latestPaidAmount) - sumAmount, 2));
            } else {
                $("#cheChePayInfo").addClass("none");
            }
        },
        restoreQuote: function () {
            $("input[name='item_kind_check']").each(function (index, checkBox) {
                //还原多选框
                checkBox.removeAttribute("disabled");
                if (checkBox.checked) {
                    //还原险种“黑”、“灰”颜色
                    $(checkBox).next().removeClass("no").addClass("yes");
                    //还原保费的编辑区
                    $("#" + checkBox.id.substr(0, this.id.length - 3) + "PremiumPd").attr("disabled", false);
                }
                //还原下拉框可编辑
                var selectItem = $("#" + checkBox.id.substr(0, this.id.length - 3) + "Amount");
                if (checkBox.id == "glassChk")
                    selectItem = $("#glassType");
                if (selectItem != undefined)
                    selectItem.attr("disabled", false);
            });
        },
        getToTalPremium: function () {
            var autoTax = 0, noTax = 0, compulsory = 0, commercial = 0, sumPremium = 0;
            $(".premiumPd").each(function (index) {
                var premium = $(this).val();
                var id = $(this).attr("id");
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
        },
        getTrend: function (value1, value2) {
            value1 = parseFloat(common.tools.formatMoney(parseFloat(value1), 2));
            value2 = parseFloat(common.tools.formatMoney(parseFloat(value2), 2));
            if (value1 < value2) {
                return "&nbsp;<span class='red-font change' >↑" + common.tools.formatMoney(value2 - value1, 2) + "元</span>";
            } else if (value1 > value2) {
                return "&nbsp;<span class='green-font change'>↓" + common.tools.formatMoney(value1 - value2, 2) + "元</span>";
            }
            return "";
        },
        setButtons: function () {
            return "<button class=\"btn btn-success btn-sm text-input-100 toCommit\" style=\"margin-left: 5px;\">提交报价</button>";
        }
    },
    action: {
        getQuoteInfo: function (orderId, callBackMethod) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/quote/amend/" + orderId, null, function (data) {
                    callBackMethod(data);
                },
                function () {
                    popup.mould.popTipsMould(true, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        switchChkAction: function (chkDom, isChecked) {
            if (isChecked) {
                $(chkDom).siblings("span").removeClass("no");
                $(chkDom).parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                $(chkDom).parent().parent().parent().siblings("div").find(".premiumPd").attr("disabled", false);
            } else {
                $(chkDom).siblings("span").addClass("no");
                $(chkDom).parent().parent().parent().siblings("div").find(".yes").hide().siblings(".no").show();
                $(chkDom).parent().parent().parent().siblings("div").find(".premiumPd").attr("disabled", true).val(0);
            }
        },
        validItemKindPolicy: function () {
            this.setInsurancePackage();
            var validation = quoteValidation.validItemKind(quote.insurancePackage);
            if (!validation.flag) {
                popup.mould.popTipsMould(true, validation.msg, popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            return true;
        },
        getAllCompanies: function () {
            toAdd();

            function toAdd() {
                popup.pop.popInput(true, quote.companiesContent, popup.mould.first, "500px", "auto", "40%", null);
                $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                    click: function () {
                        popup.mask.hideFirstMask(false);
                    }
                });
                $("input[name='companyChk']").each(function (index, company) {
                    var companyId = $(company).val();
                    if (quote.companies.indexOf(companyId) > -1) {
                        $(company).attr("checked", true).attr("disabled", true);
                    }
                });

                $("#popover_normal_input .toAdd").unbind("click").bind({
                    click: function () {
                        $("input[name='companyChk']:checked").each(function (index, company) {
                            quote.newCompanyId = $(company).val();
                            quote.companies.length = 0;
                            quote.companies.push($(company).val());
                            $("#kind_tab").find(".tab-title").html("<a href=\"" +
                                quote.companyUrlMap.get(quote.newCompanyId) + "\" target=\"_blank\"><img class=\"waiting-img\" width=\"96\" height=\"45\" src=\"" +
                                quote.companyImgMap.get(quote.newCompanyId) + "\"></a>");
                        });
                        popup.mask.hideFirstMask(true);
                    }
                });
            }
        },
        getAgentRebateAmount: function (callBackMethod) {
            common.ajax.getByAjax(false, "get", "json", "/orderCenter/quote/amend/agent/rebate",
                {
                    quoteRecordId: Number(quote.newQuoteRecordId),
                    agentId: Number(quote.user.id)
                }, function (data) {
                    callBackMethod(data);
                },
                function () {
                    popup.mould.popTipsMould(true, "获取代理人优惠异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        setInsurancePackage: function () {
            //var premiumJson = quote.init.getToTalPremium();
            quote.compulsoryPremium = !common.isEmpty($("#compulsoryPremiumPd").val()) ? $("#compulsoryPremiumPd").val() : 0.00;
            quote.autoTax = !common.isEmpty($("#autoTaxPremiumPd").val()) ? $("#autoTaxPremiumPd").val() : 0.00;
            quote.commercialPremium = common.tools.formatMoney(quote.premiumJson.commercial, 2);
            quote.thirdPartyPremium = !common.isEmpty($("#thirdPartyPremiumPd").val()) ? $("#thirdPartyPremiumPd").val() : 0.00;
            quote.damagePremium = !common.isEmpty($("#damagePremiumPd").val()) ? $("#damagePremiumPd").val() : 0.00;
            quote.theftPremium = !common.isEmpty($("#theftPremiumPd").val()) ? $("#theftPremiumPd").val() : 0.00;
            quote.enginePremium = !common.isEmpty($("#enginePremiumPd").val()) ? $("#enginePremiumPd").val() : 0.00;
            quote.driverPremium = !common.isEmpty($("#driverPremiumPd").val()) ? $("#driverPremiumPd").val() : 0.00;
            quote.passengerPremium = !common.isEmpty($("#passengerPremiumPd").val()) ? $("#passengerPremiumPd").val() : 0.00;
            quote.spontaneousLossPremium = !common.isEmpty($("#spontaneousLossPremiumPd").val()) ? $("#spontaneousLossPremiumPd").val() : 0.00;
            quote.glassPremium = !common.isEmpty($("#glassPremiumPd").val()) ? $("#glassPremiumPd").val() : 0.00;
            quote.scratchPremium = !common.isEmpty($("#scratchPremiumPd").val()) ? $("#scratchPremiumPd").val() : 0.00;
            quote.theftAmount = !common.isEmpty($("#theftAmount").val()) ? $("#theftAmount").val() : 0.00;
            quote.spontaneousLossAmount = !common.isEmpty($("#spontaneousLossAmount").val()) ? $("#spontaneousLossAmount").val() : 0.00;
            quote.iopTotal = !common.isEmpty($("#iopPremiumPd").val()) ? $("#iopPremiumPd").val() : 0.00;
            quote.unableFindThirdPartyPremium = !common.isEmpty($("#unableFindThirdPartyPremiumPd").val()) ? $("#unableFindThirdPartyPremiumPd").val() : 0.00;
            quote.designatedRepairShopPremium = !common.isEmpty($("#designatedRepairShopPremiumPd").val()) ? $("#designatedRepairShopPremiumPd").val() : 0.00;
            quote.insurancePackage.compulsory = $("#compulsoryChk").is(':checked');
            quote.insurancePackage.autoTax = $("#autoTaxChk").is(':checked');
            quote.insurancePackage.thirdPartyAmount = $("#thirdPartyChk").is(':checked') ? $("#thirdPartyAmount").val() : 0.00;
            quote.insurancePackage.damage = $("#damageChk").is(':checked');
            quote.insurancePackage.driverAmount = $("#driverChk").is(':checked') ? $("#driverAmount").val() : 0.00;
            quote.insurancePackage.passengerAmount = $("#passengerChk").is(':checked') ? $("#passengerAmount").val() : 0.00;
            quote.insurancePackage.engine = $("#engineChk").is(':checked');
            quote.insurancePackage.glass = $("#glassChk").is(':checked');
            quote.insurancePackage.glassType = $("#glassChk").is(':checked') ? $("#glassType").val() : null;
            quote.insurancePackage.scratchAmount = $("#scratchChk").is(':checked') ? $("#scratchAmount").val() : 0.00;
            quote.insurancePackage.theft = $("#theftChk").is(':checked');
            quote.insurancePackage.spontaneousLoss = $("#spontaneousLossChk").is(':checked');
            quote.insurancePackage.unableFindThirdParty = $("#unableFindThirdPartyChk").is(':checked');
            quote.insurancePackage.designatedRepairShop = $("#designatedRepairShopChk").is(':checked');

            if ($("#iopChk").is(':checked')) {
                quote.insurancePackage.thirdPartyIop = quote.insurancePackage.thirdPartyAmount != 0;
                quote.insurancePackage.damageIop = quote.insurancePackage.damage;
                quote.insurancePackage.theftIop = quote.insurancePackage.theft;
                quote.insurancePackage.engineIop = quote.insurancePackage.engine;
                quote.insurancePackage.driverIop = quote.insurancePackage.driverAmount != 0;
                quote.insurancePackage.passengerIop = quote.insurancePackage.passengerAmount != 0;
                quote.insurancePackage.scratchIop = quote.insurancePackage.scratchAmount != 0;
                quote.insurancePackage.spontaneousLossIop = quote.insurancePackage.spontaneousLoss;
            } else {
                quote.insurancePackage.thirdPartyIop = false;
                quote.insurancePackage.damageIop = false;
                quote.insurancePackage.theftIop = false;
                quote.insurancePackage.engineIop = false;
                quote.insurancePackage.driverIop = false;
                quote.insurancePackage.passengerIop = false;
                quote.insurancePackage.scratchIop = false;
                quote.insurancePackage.spontaneousLossIop = false;
            }
            //quote
        },
        submit: function (callBackMethod) {
            this.setInsurancePackage();
            if (!this.validate()) {
                return;
            }
            //parent.$(".quote_submit").hide();
            var glassType = {
                id: quote.insurancePackage.glassType
            };
            if (quote.insurancePackage.glassType == null) {
                glassType = null;
            }
            common.ajax.getByAjaxWithJson(true, "post", "json", "/orderCenter/quote/amend/saveQuote",
                {
                    purchaseOrderId: quote.orderId,
                    insuranceCompanyId: quote.newCompanyId,
                    premium: quote.commercialPremium,
                    compulsoryPremium: quote.compulsoryPremium,
                    autoTax: quote.autoTax,
                    thirdPartyPremium: quote.thirdPartyPremium,
                    damagePremium: quote.damagePremium,
                    theftPremium: quote.theftPremium,
                    enginePremium: quote.enginePremium,
                    driverPremium: quote.driverPremium,
                    passengerPremium: quote.passengerPremium,
                    spontaneousLossPremium: quote.spontaneousLossPremium,
                    glassPremium: quote.glassPremium,
                    scratchPremium: quote.scratchPremium,
                    theftAmount: quote.theftAmount,
                    spontaneousLossAmount: quote.spontaneousLossAmount,
                    unableFindThirdPartyPremium: quote.unableFindThirdPartyPremium,
                    designatedRepairShopPremium: quote.designatedRepairShopPremium,
                    iopTotal: quote.iopTotal,
                    insurancePackage: {
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
                        glassType: glassType,
                        driverAmount: quote.insurancePackage.driverAmount,
                        driverIop: quote.insurancePackage.driverIop,
                        passengerAmount: quote.insurancePackage.passengerAmount,
                        passengerIop: quote.insurancePackage.passengerIop,
                        spontaneousLoss: quote.insurancePackage.spontaneousLoss,
                        unableFindThirdParty: quote.insurancePackage.unableFindThirdParty,
                        designatedRepairShop: quote.insurancePackage.designatedRepairShop,
                        spontaneousLossIop: quote.insurancePackage.spontaneousLossIop,
                        scratchAmount: quote.insurancePackage.scratchAmount,
                        scratchIop: quote.insurancePackage.scratchIop
                    },
                }, function (data) {
                    if (callBackMethod) {
                        callBackMethod(data);
                    }
                },
                function () {
                    popup.mould.popTipsMould(true, "保存报价异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        validate: function () {
            var quote = true;
            var check = false;
            $("input[name='item_kind_check']:checked").each(function (index, checkBox) {
                var edit = $("#" + this.id.substr(0, this.id.length - 3) + "PremiumPd").val();
                if (!common.isEmpty(edit) && !isNaN(edit) && Number(edit) <= 0)
                    quote = false;
            });

            parent.$("input[name='item_kind_check']").each(function (index, checkBox) {
                if ($(checkBox).is(':checked')) {
                    check = true;
                }
            });

            if (!quote || !check) {
                popup.mould.popTipsMould(true, "保费不能为0，请重新核对保费", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            if ($("#theftChk").is(':checked') && !isNaN($("#theftAmount").val()) && Number($("#theftAmount").val()) <= 0) {
                popup.mould.popTipsMould(true, "保额不能为0，请重新核对保费", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            if ($("#spontaneousLossChk").is(':checked') && !isNaN($("#spontaneousLossAmount").val()) && Number($("#spontaneousLossAmount").val()) <= 0) {
                popup.mould.popTipsMould(true, "保额不能为0，请重新核对保费", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            return true;
        },
        orderInfo: function (callbackMethod) {
            common.getByAjax(false, "get", "json", "/orderCenter/order/detail", {purchaseOrderId: quote.orderId},
                function (data) {
                    if (data == null) {
                        common.showTips("获取订单详情失败");
                        return false;
                    }
                    if (callbackMethod) {
                        callbackMethod(data);
                    }
                },
                function () {
                    popup.mould.popTipsMould(false, "系统异常", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },
        saveOrder: function () {
            common.ajax.getByAjaxWithJsonAndHeader(true, "post", "json", "/orderCenter/quote/amend/modify/" + quote.orderId,
                {
                    objId: quote.newQuoteRecordId,
                    giftId: $("#giftId").val(),
                    giftAmount: $("#giftAmount").val(),
                    compulsoryPercent: orderPremium.percent.compulsory,
                    commercialPercent: orderPremium.percent.commercial,
                    payableAmount: quote.premiumJson.sumPremium,
                    isCheChePay: $("#cheChePay").is(':checked') ? 1 : 0,
                    premiumType: orderPremium.premiumType,
                    paidAmount: orderPremium.premium.paidAmount,
                    resendGiftList: quoteResendGift.giftList,
                    comment: $("#comment").val()
                },
                function (data) {
                    if (!data.pass) {
                        popup.mask.showOrHideOpacityMask(true, false);
                        popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                    } else {
                        if (data.message === "2" && !$("#cheChePay").is(':checked')) {//需增补
                            popup.mould.popConfirmMould(true, "订单修改成功，是否发送短信", "first", "", "55%",
                                function () {
                                    popup.mask.hideFirstMask(true);
                                    quote.action.sendMessage();
                                    window.close();
                                },
                                function () {
                                    popup.mask.hideFirstMask(true);
                                    window.close();
                                    return false;
                                }
                            );
                        } else {
                            popup.mould.popTipsMould(true, "订单更新成功", popup.mould.first, popup.mould.success, "", "57%", function () {
                                window.close();
                            });
                        }

                    }
                }, function () {
                    popup.mask.showOrHideOpacityMask(true, false);
                    popup.mould.popTipsMould(true, "提交订单异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                },
                quote.interface.getHeaderMap());
        },
        sendMessage: function () {
            common.ajax.getByAjax(false, "post", "json", "/orderCenter/order/sendMessage",
                {
                    quoteRecordId: quote.newQuoteRecordId,
                    paymentType: 2
                },
                function (data) {
                },
                function () {
                    popup.mould.popTipsMould(true, "发送短信异常", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    interface: {
        getCompanies: function (licensePlateNo, callBackMethod) {
            common.ajax.getByAjaxWithHeader(true, "get", "json", "/orderCenter/quote/" + licensePlateNo + "/companies", {},
                function (data) {
                    if (!data.pass) {
                        popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                        return;
                    }
                    callBackMethod(data.message);
                },
                function () {
                    popup.mould.popTipsMould(true, "获取保险公司接口异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                },
                quote.interface.getHeaderMap());
        },
        getHeaderMap: function () {
            var headerMap = new Map();
            headerMap.put("uniqueId", quote.uniqueId);
            return headerMap;
        },
        getUniqueId: function (callBackMethod) {
            common.ajax.getByAjax(true, "get", "html", "/orderCenter/quote/uniqueId", {},
                function (data) {
                    callBackMethod(data);
                },
                function () {
                    popup.mould.popTipsMould(true, "获取流程唯一标识异常，无法继续报价！", popup.mould.first, popup.mould.error, "", "57%", function () {
                        window.close();
                    });
                }
            );
        },
        validInternalUser: function (callBackMethod) {
            common.ajax.getByAjaxWithHeader(true, "post", "json", "/orderCenter/quote/" + quote.user.userId + "/login",
                {
                    channelId: quote.sourceChannel,
                    source: quote.source,
                    sourceId: quote.orderId
                },
                function (data) {
                    if (!data.pass) {
                        popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.warning, "", "57%", function () {
                            window.close();
                        });
                        return;
                    }
                    callBackMethod(data.message);
                },
                function () {
                    popup.mould.popTipsMould(true, "用户校验异常！", popup.mould.first, popup.mould.error, "", "57%", function () {
                        window.close();
                    });
                },
                quote.interface.getHeaderMap());
        },
        checkAgent: function (userId, callBackMethod) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/agent/user/" + userId, {},
                function (data) {
                    callBackMethod(data);
                },
                function () {
                    popup.mould.popTipsMould(true, "判断用户类型异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        getPaidAmount: function (companyId, callback, error) {
            common.ajax.getByAjaxWithJsonAndHeader(true, "get", "json", "/orderCenter/quote/amend/paidAmount?companyId=" + companyId + "&quoteRecordId=" + quote.newQuoteRecordId, null,
                function (data) {
                    callback(data);
                },
                function () {
                    error();
                },
                quote.interface.getHeaderMap());
        }
    }
};

$(function () {
    popup.insertHtml($("#popupHtml"));
    var id = common.getUrlParam("id");
    var source = common.getUrlParam("source");
    quote.init.init(id, source);
    $("#addCompany").unbind("click").bind({
        click: function () {
            if (!quote.action.validItemKindPolicy()) {
                return;
            }
            quote.action.getAllCompanies();
        }
    });
    $("#modifyForm").submit(function (e) {
        e.preventDefault();
        popup.mask.showOrHideOpacityMask(true, true);
        quote.action.saveOrder();
    });
});


$(document).ready(function() {
    $('#policyProvince').select2();
    $('#policyCity').select2();
    $('#policyDistrict').select2();
});
