/**
 * Created by wangfei on 2015/5/13.
 */
$(function(){
    var id = common.getUrlParam("id");
    if(id == null){
        alert("异常参数");
        return false;
    }

    detail.getDetail(id);
    popup.insertHtml($("#popupHtml"));

    var AllHet = $(window).height();
    var mainHet= $('#note a img').height();
    var fixedTop = (AllHet - mainHet)/2
    $('#note').css({top:fixedTop+'px'});
    $(window).scroll(scrolls);
    scrolls();
    function scrolls(){
        var sTop = $(window).scrollTop();
        var topPx = sTop+fixedTop
        $('#note').stop().animate({top:topPx});
    }

});

var detail = {
    getDetail : function(id){
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/detail/" + id, null,
            function(data){
                detail.write(data);
                $("#comment").unbind("click").bind({
                    click:function(){
                        orderComment.popCommentList(data.purchaseOrderId,"first");
                    }
                });
                //$("#pic").unbind("click").bind({
                //    click:function(){
                //        validateCar.pop(data.purchaseOrderId);
                //    }
                //});
            },
            function(){
                popup.mould.popTipsMould(false, "获取订单详情异常！", popup.mould.first, popup.mould.error, "", "", null);
            }
        );
    },
    write : function(data) {
        /* 订单信息 */
        $("#area").text(common.tools.checkToEmpty(data.area.name));
        $("#orderNo").text(common.tools.checkToEmpty(data.orderNo));
        $("#orderCreateTime").text(common.tools.checkToEmpty(data.orderCreateTime));
        $("#orderStatus").text(data.cooperationStatus == null? data.paymentStatus : data.cooperationStatus.status);
        if (data.reason != null) {
            $("#reason").show().css({'color':'red'}).text(data.reason);
        }
        /* 出单信息 */
        $("#payableAmount").text(common.tools.formatMoney(data.payableAmount,2));
        $("#paidAmount").text(common.tools.formatMoney(data.paidAmount,2));
        if(data.quoteRecord != null) {
            $("#institutionQuote").text(common.tools.formatMoney(data.quoteRecord.premium,2));
            $("#institutionRebate").text(common.tools.formatMoney(data.quoteRecord.rebate,2));
            $("#institutionQuoteSpan").show();
            $("#institutionRebateSpan").show();
        }
        if (data.rebateStatus != null && data.rebateStatus) {
            $("#rebateStatus").show().css({'color':'green'}).text("已到账");
        }
        if (data.incomeStatus != null && data.incomeStatus) {
            $("#incomeStatus").css({'color':'green'}).text("收益：正常");
        } else if (data.incomeStatus != null && !data.incomeStatus) {
            $("#incomeStatus").css({'color':'red'}).text("收益：异常");
        }
        if (data.matchStatus != null && data.matchStatus) {
            $("#matchStatus").css({'color':'green'}).text("险种保额：匹配");
        } else if (data.matchStatus != null && !data.matchStatus) {
            $("#matchStatus").css({'color':'red'}).text("险种保额：不匹配");
        }
        if(data.incomeStatus != null || data.matchStatus != null) {
            $("#warningDetail").show().html("<a href=\"javascript:;\" onclick=\"detail.warningDetail('" + data.id + "','" + data.purchaseOrderId + "');\">查看详情</a>");
        }
        $("#giftDetails").text(data.giftDetails);
        if(data.institution != null) {
            $("#institutionName").text(data.institution.name);
            $("#institutionName").attr('institutionId', data.institution.id);
        }
        if(data.cooperationStatus != null && data.cooperationStatus.status == '订单完成') {
            if(data.auditStatus == null) {
                $("#auditStatus").css({'color':'orange'}).text("待审核");
            } else if(data.auditStatus) {
                $("#auditStatus").css({'color':'green'}).text("审核通过");
            } else {
                $("#auditStatus").css({'color':'red'}).text("审核不通过");
            }
        }
        /* 用户信息 */
        $("#userMobile").text(common.tools.checkToEmpty(data.userMobile));
        $("#nickName").text(common.tools.checkToEmpty(data.nickName));
        $("#source").text(common.tools.checkToEmpty(data.source));
        $("#platform").text(common.tools.checkToEmpty(data.platform));

        /* 车辆信息 */
        $("#licenseNo").text(common.tools.checkToEmpty(data.licensePlateNo));
        $("#vinNo").text(common.tools.checkToEmpty(data.vinNo));
        $("#engineNo").text(common.tools.checkToEmpty(data.engineNo));
        $("#enrollDate").text(common.tools.checkToEmpty(data.enrollDate));
        $("#modelName").text(common.tools.checkToEmpty(data.modelName));
        $("#seats").text(common.tools.checkToEmpty(data.seats));
        $("#code").text(common.tools.checkToEmpty(data.code));

        /* 车辆补充信息 */
        var $autoSupplementInfo = $("#autoSupplementInfo");
        var autoSupplementInfo = "";
        if (data.supplementInfos && data.supplementInfos.length > 0) {
            $.each(data.supplementInfos, function(index, supplementInfo) {
                if (index%3 == 0) {
                    autoSupplementInfo += "<div class=\"row\">";
                }
                if ("auto.autoType.supplementInfo.autoModel" == supplementInfo.fieldPath) {
                    autoSupplementInfo += "<div class=\"col-sm-4\"><span class=\"col-sm-2\" style=\"margin-left:-14px;width:112px;\">" + supplementInfo.label + "：</span><span class=\"col-sm-10\" style=\"margin-left: -40px;\">" + supplementInfo.value + "</span></div>";
                } else {
                    autoSupplementInfo += "<div class=\"col-sm-4\"><span>" + supplementInfo.label + "：</span><span>" + supplementInfo.value + "</span></div>";
                }

                if ((index+1)%3 == 0) {
                    autoSupplementInfo += "</div>";
                }
            });
        } else {
            autoSupplementInfo = "<div class=\"row\"><div class=\"col-sm-12\">无</div></div>";
        }
        $autoSupplementInfo.append(autoSupplementInfo);

        /* 险种信息 */
        $("#insuranceCompany").text(common.tools.checkToEmpty(data.insuranceCompany.name));
        if(data.orderInsurance.commercialPolicyNo != null) {
            $("#commercialPolicyNo").html("<a href='order_insurance_record_show.html?orderNo=" + data.orderNo + "' target='_blank'>" + data.orderInsurance.commercialPolicyNo + "</a>");
        }
        if(data.orderInsurance.compulsoryPolicyNo != null) {
            $("#compulsoryPolicyNo").html("<a href='order_insurance_record_show.html?orderNo=" + data.orderNo + "' target='_blank'>" + data.orderInsurance.compulsoryPolicyNo + "</a>");
        }
        $("#compulsoryPremium").text(common.tools.formatMoney(data.orderInsurance.compulsoryPremium,2));
        if (common.tools.formatMoney(data.orderInsurance.compulsoryPremium,2) > 0) {
            $("#compulsoryChk").attr("checked", true);
        }
        $("#autoTax").text(common.tools.formatMoney(data.orderInsurance.autoTax,2));
        if (common.tools.formatMoney(data.orderInsurance.autoTax,2) > 0) {
            $("#autoTaxChk").attr("checked", true);
        }
        $("#commercialPremium").text(common.tools.formatMoney(data.orderInsurance.commercialPremium,2));
        if (common.tools.formatMoney(data.orderInsurance.commercialPremium,2) > 0) {
            $("#commercialChk").attr("checked", true);
        }
        $("#thirdPartyPremium").text(common.tools.formatMoney(data.orderInsurance.thirdPartyPremium,2));
        $("#thirdPartyAmount").text(common.tools.formatMoney(data.orderInsurance.thirdPartyAmount,2));
        if (data.orderInsurance.thirdPartyPremium > 0) {
            $("#thirdPartyChk").attr("checked", true);
        }
        $("#scratchPremium").text(common.tools.formatMoney(data.orderInsurance.scratchPremium,2));
        $("#scratchAmount").text(common.tools.formatMoney(data.orderInsurance.scratchAmount,2));
        if (data.orderInsurance.scratchPremium > 0) {
            $("#scratchChk").attr("checked", true);
        }
        $("#damagePremium").text(common.tools.formatMoney(data.orderInsurance.damagePremium,2));
        $("#damageAmount").text(common.tools.formatMoney(data.orderInsurance.damageAmount,2));
        if (data.orderInsurance.damagePremium > 0) {
            $("#damageChk").attr("checked", true);
        }
        $("#iop").text(common.tools.formatMoney(data.orderInsurance.iop,2));
        if (data.orderInsurance.iop > 0) {
            $("#iopChk").attr("checked", true);
        }
        $("#driverPremium").text(common.tools.formatMoney(data.orderInsurance.driverPremium,2));
        $("#driverAmount").text(common.tools.formatMoney(data.orderInsurance.driverAmount,2));
        if (data.orderInsurance.driverPremium > 0) {
            $("#driverChk").attr("checked", true);
        }
        $("#passengerPremium").text(common.tools.formatMoney(data.orderInsurance.passengerPremium,2));
        $("#passengerAmount").text(common.tools.formatMoney(data.orderInsurance.passengerAmount,2));
        if (data.orderInsurance.passengerPremium > 0) {
            $("#passengerChk").attr("checked", true);
        }
        $("#theftPremium").text(common.tools.formatMoney(data.orderInsurance.theftPremium,2));
        $("#theftAmount").text(common.tools.formatMoney(data.orderInsurance.theftAmount,2));
        if (data.orderInsurance.theftPremium > 0) {
            $("#theftChk").attr("checked", true);
        }
        $("#spontaneousLossPremium").text(common.tools.formatMoney(data.orderInsurance.spontaneousLossPremium,2));
        $("#spontaneousLossAmount").text(common.tools.formatMoney(data.orderInsurance.spontaneousLossAmount,2));
        if (data.orderInsurance.spontaneousLossPremium > 0) {
            $("#spontaneousLossChk").attr("checked", true);
        }
        $("#enginePremium").text(common.tools.formatMoney(data.orderInsurance.enginePremium,2));
        if (data.orderInsurance.enginePremium > 0) {
            $("#engineChk").attr("checked", true);
        }
        $("#glassPremium").text(common.tools.formatMoney(data.orderInsurance.glassPremium,2));
        if (data.orderInsurance.glassPremium > 0) {
            $("#glassChk").attr("checked", true);
            $("#glassType").text(common.tools.checkToEmpty(data.orderInsurance.glassTypeName));
        }

        /* 车主信息 */
        $("#ownerName").text(common.tools.checkToEmpty(data.ownerName));
        $("#ownerIdentityType").text(common.tools.checkToEmpty(data.ownerIdentityType));
        $("#ownerIdentity").text(common.tools.checkToEmpty(data.ownerIdentity));

        /* 被保险人信息 */
        $("#insuredName").text(common.tools.checkToEmpty(data.insuredName));
        $("#insuredIdentityType").text(common.tools.checkToEmpty(data.insuredIdentityType));
        $("#insuredIdentity").text(common.tools.checkToEmpty(data.insuredIdentity));

        /* 配送信息 */
        $("#receiverName").text(common.tools.checkToEmpty(data.receiver));
        $("#receiverMobile").text(common.tools.checkToEmpty(data.receiverMobile));
        $("#address").text(common.tools.checkToEmpty(data.address));

        /*投保人信息*/
        $("#applicantName").text(common.tools.checkToEmpty(data.applicantName));
        $("#applicantIdentityType").text(common.tools.checkToEmpty(data.applicantIdentityType));
        $("#applicantIdNo").text(common.tools.checkToEmpty(data.applicantIdNo));

    },

    /**
     * 预警信息
     * @param id
     * @param purchaseOrderId
     */
    warningDetail: function(id, purchaseOrderId) {
        warningDetail.popDetail(id, purchaseOrderId, popup.mould.first, false, null, null);
    }
}
