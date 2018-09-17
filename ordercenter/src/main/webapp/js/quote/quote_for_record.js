/**
 * 处理报价来源数据报价
 * */
function QuoteRecord(source,sourceId){
    this.source=source;
    this.sourceId=sourceId;
}
QuoteRecord.prototype={
    getQuoteInfo:function(callBackMethod){
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/quote/log/"+this.sourceId,null,
            function (data) {
                QuoteRecord.prototype.initParam(data);
                QuoteRecord.prototype.fillContent(data);
                callBackMethod(data);
            },
            function () {
                popup.mould.popTipsMould(true, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    initParam:function(data){
        quote.licensePlateNo = data.auto.licensePlateNo;
        if(!common.isEmpty(data.applicant)){
            quote.user.userId = data.applicant.id;
            quote.user.mobile = data.applicant.mobile;
        }
        quote.insured.name = data.auto.owner;
        quote.insured.identity = data.auto.identity;
        quote.insured.identityType=data.auto.identityType;
        quote.insure.name = data.auto.owner;
        quote.insure.identity = data.auto.identity;
        quote.insure.identityType=data.auto.identityType;
        quote.sourceChannel = data.channel!=null?data.channel.id:"";
        quote.owner.name= data.auto.owner;
        quote.owner.identity = data.auto.identity;
        quote.owner.identity= data.auto.identityType;
    },
    getQuote:function(companyId){
        quote.interface.getQuote(companyId, false);
    },
    fillContent: function (data) {
        if (data) {
            $("#quote_id").val(data.id);
            if(data.insurancePackage==null){
                return;
            }
            $("#compulsoryPremium").val(data.compulsoryPremium);
            $("#autoTax").val(data.autoTax);
            $("#commercialPremium").val(data.commercialPremium);
            $("#quoteTotal").html(data.compulsoryPremium+data.autoTax+data.commercialPremium);
            $(".premiumPd").val(0).attr("disabled",true);
            if (data.insurancePackage.compulsory == true) {
                $("#compulsoryChk").attr("checked", 'true');
                $("#compulsoryChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                $("#compulsoryPremiumPd").val(data.compulsoryPremium).attr("disabled",false);
            }else{
                $("#compulsoryChk").attr("checked", false);
                $("#compulsoryChk").parent().parent().parent().siblings("div").find(".yes").hide().siblings(".no").show();
                $("#compulsoryChk").siblings("span").addClass("no");
                $("#compulsoryPremiumPd").val(data.compulsoryPremium).attr("disabled",false);
            }
            if (data.insurancePackage.autoTax == true) {
                $("#autoTaxChk").attr("checked", 'true');
                $("#autoTaxChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                $("#autoTaxPremiumPd").val(data.autoTax).attr("disabled",false);
            }else{
                $("#autoTaxChk").attr("checked", false);
                $("#autoTaxChk").parent().parent().parent().siblings("div").find(".yes").hide().siblings(".no").show();
                $("#autoTaxChk").siblings("span").addClass("no");
                $("#autoTaxPremiumPd").val(data.autoTax).attr("disabled",false);
            }
            if (data.insurancePackage.thirdPartyAmount != null && data.insurancePackage.thirdPartyAmount!=0) {
                $("#thirdPartyChk").attr("checked", 'true');
                $("#thirdPartyChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.thirdPartyAmount).show().siblings(".no").hide();
                $("#thirdPartyPremiumPd").val(data.thirdPartyPremium).attr("disabled",false);
            }else{
                $("#thirdPartyChk").attr("checked", false);
                $("#thirdPartyChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.thirdPartyAmount).hide().siblings(".no").show();
                $("#thirdPartyChk").siblings("span").addClass("no");
                $("#thirdPartyPremiumPd").val(data.thirdPartyPremium).attr("disabled",false);
            }
            if (data.insurancePackage.damage == true) {
                $("#damageChk").attr("checked", 'true');
                $("#damageChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                $("#damagePremiumPd").val(data.damagePremium).attr("disabled",false);
            }else{
                $("#damageChk").attr("checked", false);
                $("#damageChk").parent().parent().parent().siblings("div").find(".yes").hide().siblings(".no").show();
                $("#damageChk").siblings("span").addClass("no");
                $("#damagePremiumPd").val(data.damagePremium).attr("disabled",false);
            }
            if (data.insurancePackage.driverAmount != null && data.insurancePackage.driverAmount!=0) {
                $("#driverChk").attr("checked", 'true');
                $("#driverChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.driverAmount).show().siblings(".no").hide();
                $("#driverChk").siblings("span").removeClass("no");
                $("#driverPremiumPd").val(data.driverPremium).attr("disabled",false);
            }
            if (data.insurancePackage.passengerAmount != null && data.insurancePackage.passengerAmount!=0) {
                $("#passengerChk").attr("checked", 'true');
                $("#passengerChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.passengerAmount).show().siblings(".no").hide();
                $("#passengerChk").siblings("span").removeClass("no");
                $("#passengerPremiumPd").val(data.passengerPremium).attr("disabled",false);
            }
            if (data.insurancePackage.engine == true) {
                $("#engineChk").attr("checked", 'true');
                $("#engineChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.engine).show().siblings(".no").hide();
                $("#engineChk").siblings("span").removeClass("no");
                $("#enginePremiumPd").val(data.enginePremium).attr("disabled",false);
            }
            if (data.insurancePackage.glass == true) {
                $("#glassChk").attr("checked", 'true');
                $("#glassChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.glassType.id).show().siblings(".no").hide();
                $("#glassChk").siblings("span").removeClass("no");
                $("#glassPremiumPd").val(data.glassPremium).attr("disabled",false);
            }
            if (data.insurancePackage.scratchAmount != null && data.insurancePackage.scratchAmount!=0) {
                $("#scratchChk").attr("checked", 'true');
                $("#scratchChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.scratchAmount).show().siblings(".no").hide();
                $("#scratchChk").siblings("span").removeClass("no");
                $("#scratchPremiumPd").val(data.scratchPremium).attr("disabled",false);

            }
            if (data.insurancePackage.theft ==true) {
                $("#theftChk").attr("checked", 'true');
                $("#theftChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                $("#theftChk").siblings("span").removeClass("no");
                $("#theftPremiumPd").val(data.theftPremium).attr("disabled",false);
                $("#theftAmount").val(data.theftAmount).attr("disabled",false);
            }
            if (data.insurancePackage.spontaneousLoss == true) {
                $("#spontaneousLossChk").attr("checked", 'true');
                $("#spontaneousLossChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                $("#spontaneousLossChk").siblings("span").removeClass("no");
                $("#spontaneousLossPremiumPd").val(data.spontaneousLossPremium).attr("disabled",false);
                $("#spontaneousLossAmount").val(data.spontaneousLossAmount).attr("disabled",false);
            }
            if (data.insurancePackage.unableFindThirdParty == true) {
                $("#unableFindThirdPartyChk").attr("checked", 'true');
                $("#unableFindThirdPartyChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.unableFindThirdParty).show().siblings(".no").hide();
                $("#unableFindThirdPartyPremiumPd").val(data.unableFindThirdPartyPremium).attr("disabled",false);
            }
            //指定专修厂险
            $("#designatedRepairShopChk").attr("checked", 'true');
            $("#designatedRepairShopChk").parent().parent().parent().siblings("div").find(".yes").val(data.insurancePackage.designatedRepairShop).show().siblings(".no").hide();
            $("#designatedRepairShopPremiumPd").val(data.designatedRepairShopPremium).attr("disabled",true);

            if (data.insurancePackage.thirdPartyIop == true || data.insurancePackage.damageIop == true || data.insurancePackage.theftIop == true
                || data.insurancePackage.engineIop == true || data.insurancePackage.driverIop == true || data.insurancePackage.passengerIop == true
                || data.insurancePackage.scratchIop == true || data.insurancePackage.spontaneousLossIop == true) {
                $("#iopChk").attr("checked", 'true');
                $("#iopChk").parent().parent().parent().siblings("div").find(".yes").show().siblings(".no").hide();
                $("#iopPremiumPd").val(data.iopTotal).attr("disabled",false);
            }else{
                $("#iopChk").attr("checked", false);
                $("#iopChk").parent().parent().parent().siblings("div").find(".yes").hide().siblings(".no").show();
                $("#iopChk").siblings("span").addClass("no");
                $("#iopPremiumPd").val(data.iopTotal).attr("disabled",false);
            }
        }
        $("#item_kind_count").text($("input[name='item_kind_check']:checked").length);
    },
}

