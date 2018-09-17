var quote = {
    insurancePackage: {},
    insuranceCompany: {},
    institutionRebate: {
        commercialRebate:"",
        compulsoryRebate:""
    }
};

var parent = window.parent;
var order = {
    id:"",
    orderNo: "",
    owner: "",
    area: "",
    insuranceCompanyId: "",
    insuranceCompanyName:"",
    institutionId: ""
}


var quote_pop = {
    listInfo: {
        popup: function (infoId,callBackMethod) {
            $.post("order_quote_pop.html", {}, function (detailContent) {
           popup.pop.popInput(false, detailContent, 'first', "500px", "600px", "33%", "57%");
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/new/" + infoId, {},
                function (data) {
                    order.id = data.id;
                    order.orderNo = data.orderNo;
                    order.owner = data.owner;
                    order.createTime = data.createTime;
                    order.area = data.area.name;
                    order.insuranceCompanyId = data.insuranceCompany.id;
                    order.insuranceCompanyName = data.insuranceCompany.name;
                    order.institutionId = data.institution.id;
                    order.licensePlateNo = data.licensePlateNo;
                    parent.$("#tab_order_no").html(order.orderNo + "<br>" + order.createTime);
                    parent.$("#tab_name").html(order.owner + "<br>" + order.licensePlateNo);
                    parent.$("#tab_area").html(order.area);
                    parent.$("#tab_insurance_company").html(order.insuranceCompanyName);
                    parent.$(".quote_submit").unbind("click").bind({
                        click: function () {
                            quote_pop.submit(function(data){
                                if (callBackMethod) {
                                    callBackMethod(data);
                                }
                            });
                        }
                    });
                    parent.$("#quote_pop_close").unbind("click").bind({
                        click: function () {
                            popup.mask.hideFirstMask(false);
                        }
                    });
                    parent.$("input[name='item_kind_check']").each(function (index, checkBox) {
                        $(checkBox).unbind("change").bind({
                            change: function () {
                                if ($(this).is(':checked')) {
                                    $(this).siblings("span").removeClass("no");
                                    $(this).parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                                } else {
                                    $(this).siblings("span").addClass("no");
                                    $(this).parent().parent().parent().siblings("div").find(".yes").hide().siblings(".no").show();
                                }
                            }
                        });
                    });
                    parent.$("#compulsoryPremium").val(data.compulsoryPremium);
                    parent.$("#autoTax").val(data.autoTax);
                    parent.$("#commercialPremium").val(data.commercialPremium);

                    parent.$(".price").unbind("blur").bind({
                        blur: function () {
                            var total = 0;
                            if (!common.isMoney($(this).val())) {
                                quote_pop.error("请输入正确的价格");
                                return;
                            }
                            $(this).val(common.tools.formatMoney($(this).val(),2));
                            parent.$(".price").each(function (index, obj) {
                                if (!common.isEmpty($(obj).val()) && !isNaN($(obj).val())) {
                                    total += parseFloat($(obj).val())
                                }
                            });
                            parent.$("#quoteTotal").html(total);
                        }
                    });
                    quote_pop.listInfo.initQuote();
                },
                function () {
                    popup.mould.popTipsMould(true, "系统异常", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );

        })
        },
        initQuote:function(){
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/new/"+order.orderNo+"/getQuote",null, function (data) {
                    quote_pop.listInfo.fillContent(data);
                },
                function () {
                    popup.mould.popTipsMould(true, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        fillContent: function (data) {
            if (data) {
                quote.institutionRebate.commercialRebate=data.commercialRebate;
                quote.institutionRebate.compulsoryRebate=data.compulsoryRebate;
                parent.$("#quote_id").val(data.id);
                if(data.insurancePackage==null){
                    return;
                }
                parent.$("#compulsoryPremium").val(data.compulsoryPremium);
                parent.$("#autoTax").val(data.autoTax);
                parent.$("#commercialPremium").val(data.commercialPremium);
                parent.$("#quoteTotal").html(data.compulsoryPremium+data.autoTax+data.commercialPremium);
                if (data.insurancePackage.compulsory == true) {
                    parent.$("#compulsoryChk").attr("checked", 'true');
                    parent.$("#compulsoryChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                }
                if (data.insurancePackage.autoTax == true) {
                    parent.$("#autoTaxChk").attr("checked", 'true');
                    parent.$("#autoTaxChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                }
                if (data.insurancePackage.thirdPartyAmount != null && data.insurancePackage.thirdPartyAmount!=0) {
                    parent.$("#thirdPartyChk").attr("checked", 'true');
                    parent.$("#thirdPartyChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.thirdPartyAmount).show().siblings(".no").hide();
                }
                if (data.insurancePackage.damage == true) {
                    parent.$("#damageChk").attr("checked", 'true');
                    parent.$("#damageChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                }
                if (data.insurancePackage.driverAmount != null && data.insurancePackage.driverAmount!=0) {
                    parent.$("#driverChk").attr("checked", 'true');
                    parent.$("#driverChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.driverAmount).show().siblings(".no").hide();
                }
                if (data.insurancePackage.passengerAmount != null && data.insurancePackage.passengerAmount!=0) {
                    parent.$("#passengerChk").attr("checked", 'true');
                    parent.$("#passengerChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.passengerAmount).show().siblings(".no").hide();
                }
                if (data.insurancePackage.engine == true) {
                    parent.$("#engineChk").attr("checked", 'true');
                    parent.$("#engineChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.engine).show().siblings(".no").hide();
                }
                if (data.insurancePackage.glass == true) {
                    parent.$("#glassChk").attr("checked", 'true');
                    parent.$("#glassChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.glassType.id).show().siblings(".no").hide();
                }
                if (data.insurancePackage.scratchAmount != null && data.insurancePackage.scratchAmount!=0) {
                    parent.$("#scratchChk").attr("checked", 'true');
                    parent.$("#scratchChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.scratchAmount).show().siblings(".no").hide();

                }
                if (data.insurancePackage.theft ==true) {
                    parent.$("#theftChk").attr("checked", 'true');
                    parent.$("#theftChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                }
                if (data.insurancePackage.spontaneousLoss == true) {
                    parent.$("#spontaneousLossChk").attr("checked", 'true');
                    parent.$("#spontaneousLossChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                }
                if (data.insurancePackage.thirdPartyIop == true || data.insurancePackage.damageIop == true || data.insurancePackage.theftIop == true
                    || data.insurancePackage.engineIop == true || data.insurancePackage.driverIop == true || data.insurancePackage.passengerIop == true
                    || data.insurancePackage.scratchIop == true) {
                    parent.$("#iopChk").attr("checked", 'true');
                    parent.$("#iopChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                }

            }

        }
    },
    setInsurancePackage: function () {
        quote.id = parent.$("#quote_id").val();
        quote.orderNo = order.orderNo;
        quote.insuranceCompany.id = order.insuranceCompanyId;
        quote.commercialPremium = !common.isEmpty(parent.$("#commercialPremium").val())?parent.$("#commercialPremium").val():0.00;
        quote.compulsoryPremium = !common.isEmpty(parent.$("#compulsoryPremium").val())?parent.$("#compulsoryPremium").val():0.00;
        quote.autoTax = !common.isEmpty(parent.$("#autoTax").val())?parent.$("#autoTax").val():0.00;
        quote.insurancePackage.compulsory = parent.$("#compulsoryChk").is(':checked');
        quote.insurancePackage.autoTax = parent.$("#autoTaxChk").is(':checked');
        quote.insurancePackage.thirdPartyAmount = parent.$("#thirdPartyChk").is(':checked') ? parent.$("#thirdPartyAmount").val() : 0.00;
        quote.insurancePackage.damage = parent.$("#damageChk").is(':checked');
        quote.insurancePackage.driverAmount = parent.$("#driverChk").is(':checked') ? parent.$("#driverAmount").val() : 0.00;
        quote.insurancePackage.passengerAmount = parent.$("#passengerChk").is(':checked') ? parent.$("#passengerAmount").val() : 0.00;
        quote.insurancePackage.engine = parent.$("#engineChk").is(':checked');
        quote.insurancePackage.glass = parent.$("#glassChk").is(':checked');
        quote.insurancePackage.glassType = parent.$("#glassChk").is(':checked') ? parent.$("#glassType").val() : null;
        quote.insurancePackage.scratchAmount = parent.$("#scratchChk").is(':checked') ? parent.$("#scratchAmount").val() : 0.00;
        quote.insurancePackage.theft = parent.$("#theftChk").is(':checked');
        quote.insurancePackage.spontaneousLoss = parent.$("#spontaneousLossChk").is(':checked');
        if (parent.$("#iopChk").is(':checked')) {
            quote.insurancePackage.thirdPartyIop = quote.insurancePackage.thirdPartyAmount != null;
            quote.insurancePackage.damageIop = quote.insurancePackage.damage;
            quote.insurancePackage.theftIop = quote.insurancePackage.theft;
            quote.insurancePackage.engineIop = quote.insurancePackage.engine;
            quote.insurancePackage.driverIop = quote.insurancePackage.driverAmount != null;
            quote.insurancePackage.passengerIop = quote.insurancePackage.passengerAmount != null;
            quote.insurancePackage.scratchIop = quote.insurancePackage.scratchAmount != null;
        } else {
            quote.insurancePackage.thirdPartyIop = false;
            quote.insurancePackage.damageIop = false;
            quote.insurancePackage.theftIop = false;
            quote.insurancePackage.engineIop = false;
            quote.insurancePackage.driverIop = false;
            quote.insurancePackage.passengerIop = false;
            quote.insurancePackage.scratchIop = false;
        }
    }
    ,
    submit: function (callBackMethod) {
        quote_pop.setInsurancePackage();
        if(!quote_pop.validate()){
            return;
        }
        parent.$(".quote_submit").hide();
        var glassType={
                id: quote.insurancePackage.glassType
            }
        if(quote.insurancePackage.glassType==null){
            glassType=null;
        }
        common.ajax.getByAjaxWithJson(true, "post", "json", "/orderCenter/nationwide/new/saveQuote",
            {
                id: quote.id,
                orderNo: quote.orderNo,
                insuranceCompany: {
                    id: quote.insuranceCompany.id
                },
                commercialPremium: quote.commercialPremium,
                compulsoryPremium: quote.compulsoryPremium,
                autoTax: quote.autoTax,
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
                    scratchAmount: quote.insurancePackage.scratchAmount,
                    scratchIop: quote.insurancePackage.scratchIop
                },
                commercialRebate:quote.institutionRebate.commercialRebate,
                compulsoryRebate:quote.institutionRebate.compulsoryRebate
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
        var quote=false;
        var check=false;
        parent.$(".price").each(function (index, obj) {
            if(!common.isEmpty($(obj).val())&&!isNaN($(obj).val())){
              // quote_pop.error("请输入报价！");
                quote=true;
            }
        });
        parent.$("input[name='item_kind_check']").each(function (index, checkBox) {
            if ($(checkBox).is(':checked')) {
                check=true;
            }
        });
        if(!quote||!check){
            quote_pop.error("请输入至少一项报价以及勾选一项险种");
            return false;
        }
        return true;
    },
    error:function(msg){
        parent.$("#errorText").html(msg);
        parent.$(".error-msg").show().delay(2000).hide(0);
    }
}

