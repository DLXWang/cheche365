/**
 * Created by taguangyao on 2015/11/20.
 */


var orderInsuranceRecord = {
    init : function() {
        var options = "";
        for (var index = 0; index < 25; index++) {
            options += "<option value='" + index + "'>" + index + "</option>"
        }
        $("#commercialEffectiveHour").html(options);
        $("#commercialExpireHour").html(options);
        $("#commercialEffectiveHour option:first").prop("selected", "selected");
        $("#commercialExpireHour option:last").prop("selected", "selected");
        $("#compulsoryEffectiveHour").html(options);
        $("#compulsoryExpireHour").html(options);
        $("#compulsoryEffectiveHour option:first").prop("selected", "selected");
        $("#compulsoryExpireHour option:last").prop("selected", "selected");

        /* 保险公司 */
        common.getByAjax(true, "get", "json", "/orderCenter/resource/insuranceCompany/getEnableCompanies", null,
            function(data){
                if(data == null){
                    return false;
                }
                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });
                $("#insuranceCompanySel").append(options);
            },function(){}
        );
        common.getByAjax(true, "get", "json", "/orderCenter/nationwide/institution/enable", null,
            function(data){
                if(data == null){
                    return false;
                }
                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });
                $("#institutionSel").append(options);
            },function(){}
        );
        /* 投保区域 */
        common.getByAjax(true,"get","json","/orderCenter/resource/areas",null,
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });

                $("#areaSel").append(options);
            },function(){}
        );
    },
    edit: function(orderNo) {
        common.getByAjax(true, "get", "json", "/orderCenter/nationwide/" + orderNo + "/orderInsuranceRecord", {},
            function(data) {
                $("#orderNo").val(common.tools.checkToEmpty(data.orderNo));
                $("#licensePlateNo").val(common.tools.checkToEmpty(data.licensePlateNo));
                $("#owner").val(common.tools.checkToEmpty(data.owner));
                $("#identity").val(common.tools.checkToEmpty(data.identity));
                $("#vinNo").val(common.tools.checkToEmpty(data.vinNo));
                $("#engineNo").val(common.tools.checkToEmpty(data.engineNo));
                $("#enrollDate").val(common.tools.checkToEmpty(data.enrollDate));
                $("#brand").val(common.tools.checkToEmpty(data.brand));
                $("#insuredIdNo").val(common.tools.checkToEmpty(data.insuredIdNo));
                $("#insuredName").val(common.tools.checkToEmpty(data.insuredName));
                $("#insuranceCompanySel").val(common.tools.checkToEmpty(data.insuranceCompany));
                $("#originalPremium").val(common.formatMoney(data.originalPremium, 2));
                $("#rebateExceptPremium").val(common.formatMoney(data.rebateExceptPremium, 2));
                $("#trackingNo").val(common.tools.checkToEmpty(data.trackingNo));
                $("#areaSel").val(common.tools.checkToEmpty(data.area));
                $("#institutionSel").val(common.tools.checkToEmpty(data.institution));
                $("#institutionSel").attr('title', $("#institutionSel").find("option:selected").text());
                $("#expressCompany").val(common.tools.checkToEmpty(data.expressCompany));

                // 商业险
                if (data.commercialPremium) {
                    $("#commercialPolicyNo").val(common.tools.checkToEmpty(data.commercialPolicyNo));
                    $("#commercialPremium").val(common.formatMoney(data.commercialPremium, 2));
                    $("#commercialEffectiveDate").val(common.tools.checkToEmpty(data.commercialEffectiveDate));
                    $("#commercialEffectiveHour").val(data.commercialEffectiveHour == null ? 0 : data.commercialEffectiveHour);
                    $("#commercialExpireHour").val(data.commercialExpireHour == null ? 24 : data.commercialExpireHour);
                    $("#commercialExpireDate").val(common.tools.checkToEmpty(data.commercialExpireDate));
                    $("#damagePremium").val(common.formatMoney(data.damagePremium,2));
                    $("#damageAmount").val(common.formatMoney(data.damageAmount,2));
                    $("#damageIop").val(common.formatMoney(data.damageIop,2));
                    $("#thirdPartyPremium").val(common.formatMoney(data.thirdPartyPremium,2));
                    $("#thirdPartyAmountSel").val(data.thirdPartyAmount == null ? 0 : data.thirdPartyAmount);
                    $("#thirdPartyIop").val(common.formatMoney(data.thirdPartyIop,2));
                    $("#driverPremium").val(common.formatMoney(data.driverPremium,2));
                    $("#driverAmountSel").val(data.driverAmount == null ? 0 : data.driverAmount);
                    $("#driverIop").val(common.formatMoney(data.driverIop,2));
                    $("#passengerPremium").val(common.formatMoney(data.passengerPremium,2));
                    $("#passengerAmountSel").val(data.passengerAmount == null ? 0 : data.passengerAmount);
                    $("#passengerIop").val(common.formatMoney(data.passengerIop,2));
                    $("#theftPremium").val(common.formatMoney(data.theftPremium,2));
                    $("#theftAmount").val(common.formatMoney(data.theftAmount,2));
                    $("#theftIop").val(common.formatMoney(data.theftIop,2));
                    $("#scratchAmountSel").val(data.scratchAmount == null ? 0 : data.scratchAmount);
                    $("#scratchPremium").val(common.formatMoney(data.scratchPremium,2));
                    $("#scratchIop").val(common.formatMoney(data.scratchIop,2));
                    $("#spontaneousLossPremium").val(common.formatMoney(data.spontaneousLossPremium,2));
                    $("#spontaneousLossAmount").val(common.formatMoney(data.spontaneousLossAmount,2));
                    $("#enginePremium").val(common.formatMoney(data.enginePremium,2));
                    $("#engineIop").val(common.formatMoney(data.engineIop,2));
                    $("#glassPremium").val(common.formatMoney(data.glassPremium,2));
                    $("#glassTypeSel").val(common.formatMoney(data.glassPremium,2) == 0.00?
                        0 : (data.glassType == null ? 0 : data.glassType));
                    $("#insuranceImage").attr("src",common.tools.checkToEmpty(data.insuranceImage));
                    if(!common.isEmpty(data.insuranceImage)){
                        $("#insuranceImage").show();
                    }
                    $("#passengerCount").val(common.tools.checkToEmpty(data.passengerCount));
                    $("#engineAmount").val(common.tools.checkToEmpty(data.engineAmount));
                    $("#discount").val(common.formatMoney(data.discount,2));
                }

                // 交强险
                if (data.compulsoryPremium) {
                    $("#compulsoryPolicyNo").val(common.tools.checkToEmpty(data.compulsoryPolicyNo));
                    $("#compulsoryEffectiveDate").val(common.tools.checkToEmpty(data.compulsoryEffectiveDate));
                    $("#compulsoryExpireDate").val(common.tools.checkToEmpty(data.compulsoryExpireDate));
                    $("#compulsoryEffectiveHour").val(data.compulsoryEffectiveHour == null ? 0 : data.compulsoryEffectiveHour);
                    $("#compulsoryExpireHour").val(data.compulsoryExpireHour == null ? 24 : data.compulsoryExpireHour);
                    $("#compulsoryPremium").val(common.formatMoney(data.compulsoryPremium,2));
                    $("#autoTax").val(common.formatMoney(data.autoTax,2));
                    $("#compulsoryInsuranceImage").attr("src",common.tools.checkToEmpty(data.compulsoryInsuranceImage));
                    if(!common.isEmpty(data.compulsoryInsuranceImage)){
                        $("#compulsoryInsuranceImage").show();
                    }
                    $("#discountCI").val(common.formatMoney(data.discountCI,2));

                }
            },
            function() {
                popup.mould.popTipsMould(true, "获取保单信息异常", "first", "error", "", "56%",
                    function() {
                        popup.mask.hideFirstMask(true);
                    }
                );
            }
        );
    }
};
$(function(){
    orderInsuranceRecord.init();
    $("#insuranceForm input").attr("disabled",true);
    $("#insuranceForm select").attr("disabled",true);
    var orderNo = common.getUrlParam("orderNo");
    if(orderNo != null){
        orderInsuranceRecord.edit(orderNo);
    }

    $("#toCancel").bind({
        click : function(){
            window.close();
        }
    });


    $("#myTab a").click(function(e) {
        e.preventDefault();
        $(this).tab("show");
        var href = $(this).attr("href").replace("#", "");
        if ("commercial" == href) {
            $("#commercial").show();
            $("#compulsory").hide();
        } else {
            $("#commercial").hide();
            $("#compulsory").show();
        }
    });



});
