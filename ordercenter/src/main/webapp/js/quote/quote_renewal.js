/**
 * Created by wangfei on 2015/12/7.
 */
var quoteRenewal = {
    renewalInsuranceCompany: "",
    renewalFlag: "",
    renewalCompaniesContent: "",
    initRenewal: function() {
        popup.pop.popInput(true, quote.renewalCompaniesContent, popup.mould.first, "500px", "auto", "40%", null);
        var $popInput = $("#popover_normal_input");
        $popInput.find(".close").unbind("click").bind({
            click: function() {
                window.close();
            }
        });
        var $toRenewalBtn = $popInput.find(".toRenewal");
        $toRenewalBtn.unbind("click").bind({
            click: function() {
                var renewalCompany = $('input[name="renewalCompanyRadio"]:checked').val();
                if (!renewalCompany) {
                    quoteRenewal.showRenewalErrorQuoteMsg("请选择上年保险公司");
                    return;
                }
                $popInput.find(".error-line").hide();
                quoteRenewal.showOrHideRenewalBtn(false);
                quote.interface.getQuote(renewalCompany, true);
            }
        });
    },
    getRenewalQuoteResult: function(data, companyId) {
        if (!data.pass) {
            quoteRenewal.showRenewalErrorQuoteMsg(data.message);
            return;
        }
        var quoteJson = JSON.parse(data.message);
        switch (quoteJson.code) {
            case 200://成功
                quoteRenewal.showRenewalQuoteResult(data, companyId);
                break;
            case "fail"://报价失败
                quoteRenewal.showRenewalErrorQuoteMsg(companyId);
                break;
            case 2013://信息填写错误
                quoteRenewal.showRenewal2013Result(data, companyId);
                break;
            case 2008://需要补填信息
                quoteRenewal.showRenewal2008Result(data, companyId);
                break;
            case 2009://可以预约购险
                quoteRenewal.showRenewalErrorQuoteMsg(quoteJson.message ? quoteJson.message : "车险未到期，不可投保");
                break;
            default:
                quoteRenewal.showRenewalErrorQuoteMsg(quoteJson.message);
        }
    },
    showOrHideRenewalBtn: function(isShowBtn) {
        var $popInput = $("#popover_normal_input");
        var $toRenewalBtn = $popInput.find(".toRenewal");
        isShowBtn ? $toRenewalBtn.attr("disabled", false).show().siblings(".renewal-waiting-img").hide() :
            $toRenewalBtn.attr("disabled", true).hide().siblings(".renewal-waiting-img").show();
    },
    showRenewalQuoteResult: function(data, companyId) {
        var $popInput = $("#popover_normal_input");
        var $toRenewalBtn = $popInput.find(".toRenewal");
        var $renewalCompany = $('input[name="renewalCompanyRadio"]:checked');
        var $quoteContent = $("#quote_content");
        quoteRenewal.showOrHideRenewalBtn(true);

        //quote.newCompaniesMap.put(companyId, $renewalCompany.siblings("img").attr("src"));
        quote.companies.push(companyId);

        $quoteContent.find("#top_title_tr").append("<td colspan=\"2\" class=\"tab-title company-" + companyId + "\"><a href=\"" +
        quote.companyUrlMap.get(companyId) + "\" target=\"_blank\"><img class=\"waiting-img\" width=\"96\" height=\"45\" src=\"" +
        quote.companyImgMap.get(companyId) +"\"></a></td>");
        $quoteContent.find("#middle_item_tr").append("<td colspan=\"2\" style='height: 722px;'  class=\"item-kind company-" + companyId + "\"></td>");
        $quoteContent.find("#bottom_total_tr").append("<td colspan=\"2\" class=\"item-total company-" + companyId + "\"></td>");
        $quoteContent.find("#bottom_btn_tr").append("<td colspan=\"2\" class=\"item-btn company-" + companyId + "\"></td>");
        $quoteContent.show().find("table").width(quote.companies.length*quote.defaultTabSize);
        quote.action.disableAllKinds();
        $(".quote-all").attr("disabled", false).hide().siblings(".change-items").show();

        quoteRenewal.interface.mergeAuto();
        var quoteJson = JSON.parse(data.message);
        quoteRenewal.setRenewalInsurancePackage(quoteJson.data.insurancePackage);
        quote.action.validItemKindPolicy();
        quoteResult.result_200.showResult(quoteJson, companyId);
        quoteRenewal.renewalInsuranceCompany = companyId;
        popup.mask.hideFirstMask(true);
    },
    showRenewalErrorQuoteMsg: function(message) {
        var $popInput = $("#popover_normal_input");
        quoteRenewal.showOrHideRenewalBtn(true);
        if (!message) {
            message = "服务器繁忙，请稍后再试！";
        }
        $popInput.find(".error-msg .errorText").text(message);
        $popInput.find(".error-line").show();
    },
    showRenewal2013Result: function(data, companyId) {
        var $popInputSecond = common.tools.getPopInputDom(popup.mould.second, true);
        popup.mould.popConfirmMould(true, "信息有误，请检查并重新输入！", popup.mould.second, "", "",
            function() {
                quoteRenewal.showOrHideRenewalBtn(true);
                popup.mask.hideSecondMask(true);
                var quoteJson = JSON.parse(data.message);
                quoteResult.result_2013.showErrorFields(quoteJson, companyId, popup.mould.second);
                $popInputSecond.find(".toModify").unbind("click").bind({
                    click: function() {
                        var jsonData = quoteResult.result_2013.validFields($popInputSecond);
                        if (jsonData && !jsonData.flag) {
                            $popInputSecond.find(".error-line").show().find(".error-msg .errorText").text(jsonData.msg);
                            return;
                        }
                        quoteRenewal.showOrHideRenewalBtn(false);
                        popup.mask.hideSecondMask(true);
                        quote.autoInfo.put(companyId, quoteResult.result_2013.setAuto(companyId, $popInputSecond));
                        quote.interface.getQuote(companyId, true);
                    }
                });
            },
            function() {
                popup.mask.hideSecondMask(true);
                quoteRenewal.showOrHideRenewalBtn(true);
            }
        );
        $("#popover_normal_confirm_second").css("left", "57%").css("width", "384px").css("height", "187px").css("top", "41%");
    },
    showRenewal2008Result: function(data, companyId) {
        //var $popInputSecond = common.tools.getPopInputDom(popup.mould.second, true);
        popup.mould.popConfirmMould(true, "根据承保政策，需要您补充信息！", popup.mould.second, "", "",
            function() {
                quoteRenewal.showOrHideRenewalBtn(true);
                popup.mask.hideSecondMask(true);
                var quoteJson = JSON.parse(data.message);
                quoteResult.result_2008.supplementSwitch(quoteJson, companyId, popup.mould.second, true);
                /*$popInputSecond.find(".toSupplement").unbind("click").bind({
                    click: function() {
                        var jsonData = quoteResult.result_2013.validFields($popInputSecond);
                        if (jsonData && !jsonData.flag) {
                            $popInputSecond.find(".error-line").show().find(".error-msg .errorText").text(jsonData.msg);
                            return;
                        }
                        quoteRenewal.showOrHideRenewalBtn(false);
                        popup.mask.hideSecondMask(true);
                        quote.autoInfo.put(companyId, quoteResult.result_2013.setAuto(companyId, $popInputSecond));
                        quote.interface.getQuote(companyId, true);
                    }
                });*/
            },
            function() {
                popup.mask.hideSecondMask(true);
                quoteRenewal.showOrHideRenewalBtn(true);
            }
        );
    },
    setRenewalInsurancePackage: function(renewalInsurancePackage) {
        var items = [
            {name: "compulsory", type: "text"},
            {name: "autoTax", type: "text"},
            {name: "thirdParty", type: "amount"},
            {name: "damage", type: "text"},
            {name: "driver", type: "amount"},
            {name: "passenger", type: "amount"},
            {name: "engine", type: "text"},
            {name: "unableFindThirdParty", type: "text"},
            {name: "glass", type: "amount"},
            {name: "scratch", type: "amount"},
            {name: "theft", type: "text"},
            {name: "spontaneousLoss", type: "text"},
            {name: "iop", type: "text"},
            {name: "designatedRepairShop", type: "text"}
        ];
        for (var item in items) {
            var itemName = items[item].name;
            var itemType = items[item].type;
            var isTouBao;
            if ("iop" == itemName) {
                isTouBao = renewalInsurancePackage["thirdPartyIop"] || renewalInsurancePackage["damageIop"] || renewalInsurancePackage["theftIop"]
                || renewalInsurancePackage["engineIop"] || renewalInsurancePackage["driverIop"] || renewalInsurancePackage["passengerIop"]
                || renewalInsurancePackage["scratchIop"] || renewalInsurancePackage["spontaneousLoss"];
            } else if ("glass" == itemName) {
                isTouBao = null != renewalInsurancePackage["glassType"];
            } else {
                if ("amount" == itemType) {
                    isTouBao = renewalInsurancePackage[itemName + "Amount"] && renewalInsurancePackage[itemName + "Amount"] > 0;
                } else {
                    isTouBao = renewalInsurancePackage[itemName];
                }
            }

            var itemDom = $("#" + itemName + "Chk");
            if (isTouBao) {
                itemDom.attr("checked", true);
                quote.action.switchChkAction(itemDom, true);
                if ("amount" == itemType) {
                    if ("glass" == itemName) {
                        $("#glassType").val(renewalInsurancePackage["glassType"].id);
                    } else {
                        var itemAmount = $("#" + itemName + "Amount");
                        itemAmount.val(renewalInsurancePackage[itemName + "Amount"]);
                    }
                }
            } else {
                itemDom.attr("checked", false);
                quote.action.switchChkAction(itemDom, false);
            }
        }
        $("#item_kind_count").text($("input[name='item_kind_check']:checked").length);
    },
    interface: {
        mergeAuto: function() {
            common.ajax.getByAjax(true, "put", "", "/orderCenter/quote/" + quote.source + "/renewal/auto",
                {
                    userId:           quote.user.userId,
                    licensePlateNo:   quote.licensePlateNo,
                    sourceId:         quote.sourceId
                },
                function() {},
                function() {console.log("合并续保带回车辆信息异常！");}
            );
        }
    }
};
