/**
 * Created by wangfei on 2015/5/13.
 */
$(function () {
    if (common.isEmpty($("#userMobile").text()) || !common.permission.hasPermission("or060104") || !common.permission.isAbleCall()) {
        $("#orderCallBtn").hide();
    }
    var id = common.getUrlParam("id");
    var no = common.getUrlParam("no");
    if (id == null) {
        popup.mould.popTipsMould(false, "异常参数!", popup.mould.second, popup.mould.error, "", "57%", null);
        return false;
    }
    popup.insertHtml($("#popupHtml"));
    detail.initImageTypeContent();
    detail.getDetail(id, no);
    detail.getNeededPics(id, no);//加载所需照片区域
    detail.amend.initBtn(id);


    if (!no) {
        var AllHet = $(window).height();
        var mainHet = $('#note a img').height();
        var fixedTop = (AllHet - mainHet) / 2
        $('#note').css({top: fixedTop + 'px'});
        $(window).scroll(scrolls);
        scrolls();

        function scrolls() {
            var sTop = $(window).scrollTop();
            var topPx = sTop + fixedTop
            $('#note').stop().animate({top: topPx});
        }
    } else {
        $('#note').hide();
    }

    $("#order_amend").bind({
        click: function () {
            window.open("/page/quote/quote_amend.html?source=order&id=" + id);
        }
    })

    $("#daily_insurance").bind({
        click: function () {
            window.open("/page/order/daily_insurance.html?source=order&id=" + id);
        }
    })

    $("#followInfo").bind({
        click: function () {
            if (!common.isEmpty($("#followPerson").text()) || !common.isEmpty($("#inputPerson").text()) || !common.isEmpty($("#telFollowPerson").text())) {
                return;
            }
            var id = common.getUrlParam("id");
            detail.getFollowInfo(id);
        }
    })

    /* 拨打电话 */
    $("#orderCallBtn").unbind("click").bind({
        click: function () {
            $("#orderCallBtn").attr("disabled", true);
            setTimeout('$("#orderCallBtn").attr("disabled",false)', 5000);
            detail.threewayCall($("#userMobile").text());
        }
    });
});

var detail = {
    displayFlag: false,
    imageTypeContent: "",
    getFollowInfo: function (id) {
        common.getByAjax(false, "get", "json", "/orderCenter/order/followInfo", {purchaseOrderId: id},
            function (data) {
                if (!data) {
                    common.showTips("获取跟进信息失败");
                    return false;
                }
                detail.writeFollowInfo(data, id);
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.second, popup.mould.error, "", "57%", null);
            }
        );
    },

    getDetail: function (id, no) {
        common.getByAjax(false, "get", "json", "/orderCenter/order/detail", {purchaseOrderId: id},
            function (data) {
                if (data == null) {
                    common.showTips("获取订单详情失败");
                    return false;
                }
                detail.write(data, no, id);
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.second, popup.mould.error, "", "57%", null);
            }
        );
    },
    getNeededPics: function (orderId, orderNo) {
        if (detail.displayFlag) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/order/image/list/" + orderId, {},
                function (data) {
                    /*照片种类列表*/
                    var $neededImageDiv = $("#neededImageDiv");
                    if (!common.isEmpty(data) && data.length > 0) {
                        var $neededImageDivText = "";
                        $.each(data, function (index, imageTypeModel) {
                            $neededImageDivText +=
                                "  <div style=\"min-width:50%; width:auto;float:left;  border:solid 1px white; margin-top: 20px \">                                                                                                         " +
                                "  	<div class=\"col-sm-4\" style=\"width: 100%; font-size: 20px\">" + imageTypeModel.purchaseOrderImageType.name + "</div>                                                                                                                             " +
                                "  	<div class=\"col-md-4\" style=\"width: 100%;\">                                                                                                                                                         " +

                                getInnerImageHtml(imageTypeModel) +

                                "  		</div>                                                                                                                                                                                              " +
                                "  	</div>                                                                                                                                                                                                  " +
                                "  </div>                                                                                                                                                                                                   ";
                        });
                        $neededImageDiv.html($neededImageDivText);
                        common.tools.setDomNumAction($neededImageDiv.find(".commercialRebateInputVal"));
                        common.tools.setDomNumAction($neededImageDiv.find(".compulsoryRebateInputVal"));
                    }
                },
                function () {
                    popup.mould.popTipsMould(false, "获取订单记录异常！", popup.mould.first, popup.mould.error, "", "58%", null);
                }
            );
        }
    },
    initImageTypeContent: function () {
        var imageTypeContent = $("#image_type_pop");
        if (imageTypeContent.length > 0) {
            detail.imageTypeContent = imageTypeContent.html();
            imageTypeContent.remove();
        }
    },

    writeFollowInfo: function (data, id) {
        /* 跟进信息 */
        $("#followPerson").text(common.tools.checkToEmpty(data.followPerson));
        $("#inputPerson").text(common.tools.checkToEmpty(data.inputPerson));
        $("#telFollowPerson").text(common.tools.checkToEmpty(data.telFollowPerson));
    },


    write: function (data, no, id) {
        /* 用户信息 */
        $("#userMobile").text(common.tools.checkToEmpty(data.userMobile));
        if (common.isEmpty(data.userMobile)) {
            $("#orderCallBtn").hide();
        }
        $("#nickName").text(common.tools.checkToEmpty(data.nickName));
        $("#ownerMobile").text(common.tools.checkToEmpty(data.ownerMobile));
        $("#source").text(common.tools.checkToEmpty(data.source));
        $("#platform").text(common.tools.checkToEmpty(data.platform));
        $("#inviter").text(common.tools.checkToEmpty(data.inviter));//邀请人
        $("#indirectInviter").text(common.tools.checkToEmpty(data.indirectInviter));//间接邀请人
        $("#inviterTwo").text(common.tools.checkToEmpty(data.inviter));//奖励 -直接邀请人
        $("#awardInviter").text(common.tools.formatMoney(data.inviterAward, 2));
        $("#indirectionInviterTwo").text(common.tools.checkToEmpty(data.indirectInviter));// 奖励 -间接邀请人
        $("#awardIdirectionInvitern").text(common.tools.formatMoney(data.indirectInviterAward, 2));//间接邀请人奖励
        if (data.specialRemarks && data.specialRemarks == "fanhua") {//如果是泛华,则标红显示出单机构
            $("#institution-div").show();
            $("#institution").text('泛华');
        }

        /*代理人费率*/
        if (!common.isEmpty(data.agentName)) {
            $("#agentName").text(common.tools.checkToEmpty(data.agentName));
            // $("#cardNum").text(common.tools.checkToEmpty(data.cardNum));
            // $("#compulsoryRebate").text(common.formatMoney(common.tools.checkToEmpty(data.compulsoryRebate),2)+"%");
            // $("#commercialRebate").text(common.formatMoney(common.tools.checkToEmpty(data.commercialRebate),2)+"%");
            $(".agent").show();
        }


        /* 车辆信息 */
        $("#licenseNo").text(common.tools.checkToEmpty(data.licenseNo));
        $("#vinNo").text(common.tools.checkToEmpty(data.vinNo));
        $("#engineNo").text(common.tools.checkToEmpty(data.engineNo));
        $("#carOwner").text(common.tools.checkToEmpty(data.carOwner));
        $("#enrollDate").text(common.tools.checkToEmpty(data.enrollDate));
        $("#modelName").text(common.tools.checkToEmpty(data.modelName));
        $("#seats").text(common.tools.checkToEmpty(data.seats));
        $("#code").text(common.tools.checkToEmpty(data.code));
        $("#realQuote").text(data.quoteType);
        if (data.supportAmend && data.supportChangeStatus && data.paymentChannel === 'online') {
            $("#order_amend").removeClass("none");
        }
        if (data.dailyInsurance) {
            $("#daily_insurance").removeClass("none");
        }


        /* 车辆补充信息 */
        var $autoSupplementInfo = $("#autoSupplementInfo");
        var autoSupplementInfo = "";
        if (data.supplementInfos && data.supplementInfos.length > 0) {
            var autoModel = false;//老报价接口两条车型列表，展示其中一条
            $.each(data.supplementInfos, function (index, supplementInfo) {
                if (index % 3 == 0) {
                    autoSupplementInfo += "<div class=\"row\">";
                }
                if ("autoModel" == supplementInfo.fieldPath) {
                    if (!autoModel) {
                        autoModel = true;
                        autoSupplementInfo += "<div class=\"col-sm-4\"><span class=\"col-sm-2\" style=\"margin-left:-14px;width:112px;\">" + supplementInfo.label + "：</span><span class=\"col-sm-10\" style=\"margin-left: -40px;\">" + supplementInfo.value + "</span></div>";
                    }
                } else if ("selectedAutoModel" == supplementInfo.fieldPath) {
                    if (!autoModel) {
                        autoModel = true;
                        var data = $.parseJSON(supplementInfo.value);
                        autoSupplementInfo += "<div class=\"col-sm-4\"><span class=\"col-sm-2\" style=\"margin-left:-14px;width:112px;\">" + supplementInfo.label + "：</span><span class=\"col-sm-10\" style=\"margin-left: -40px;\">" + data.text + "</span></div>";
                    }
                } else {
                    autoSupplementInfo += "<div class=\"col-sm-4\"><span>" + supplementInfo.label + "：</span><span>" + supplementInfo.value + "</span></div>";
                }

                if ((index + 1) % 3 == 0) {
                    autoSupplementInfo += "</div>";
                }
            });
        } else {
            autoSupplementInfo = "<div class=\"row\"><div class=\"col-sm-12\">无</div></di       v>";
        }
        $autoSupplementInfo.append(autoSupplementInfo);

        /* 费率信息 */
        var $rebateInfo = $("#rebateInfo");
        if (common.permission.hasPermission("or0108") && common.isEmpty(data.agentName)) {
            if (data.rebates && data.rebates.length > 0) {
                var rebateText = "";
                var canModify = data.commercialPolicyNo || data.compulsoryPolicyNo;
                if (canModify) {
                    $("#rebateOperation").show();
                } else {
                    $("#rebateOperation").remove();
                }
                $.each(data.rebates, function (index, rebate) {
                    rebateText += "<tr id=\"" + rebate.type + "Rebate\">" +
                        "<td class='text-center'>" + common.tools.checkToEmpty(rebate.categoryName) + "</td>" +
                        "<td class='text-center'>" + common.tools.checkToEmpty(rebate.name) + "</td>" +
                        "<td class='text-center'><span class='compulsoryRebate'>" + (rebate.compulsoryRebate ? rebate.compulsoryRebate : 0) + "%</span>" +
                        "<span class='compulsoryRebateInput none'><input type='text' class='compulsoryRebateInputVal bottom-input rebate-input text-center' onchange='calculateRebateAmount(\"" + rebate.type + "\",\"compulsory\",this" + ");' value='" + rebate.compulsoryRebate + "'>%</span>" +
                        "</td>" +
                        "<td class='text-center'><span class='commercialRebate'>" + (rebate.commercialRebate ? rebate.commercialRebate : 0) + "%</span>" +
                        "<span class='commercialRebateInput none'><input type='text' class='commercialRebateInputVal bottom-input rebate-input text-center' onchange='calculateRebateAmount(\"" + rebate.type + "\",\"commercial\",this" + ");' value='" + rebate.commercialRebate + "'>%</span>" +
                        "</td>" +
                        "<td class='text-center'><span class='compulsoryAmount'>" + (rebate.compulsoryAmount ? rebate.compulsoryAmount : 0) + "</span></td>" +
                        "<td class='text-center'><span class='commercialAmount'>" + (rebate.commercialAmount ? rebate.commercialAmount : 0) + "</span></td>" +
                        "<td class='text-center'><span class='sumAmount'>" + (rebate.sumAmount ? rebate.sumAmount : 0) + "</span></td>" +
                        (canModify ? "<td class='text-center'><a class='toAction' href='javascript:;' onclick='modifyRebate(\"" + rebate.type + "\"," + id + ",\"modify\");'>修改</a></td>" : "") +
                        "</tr>";
                });
                $rebateInfo.find("#rebateTab tbody").empty().append(rebateText);
                common.tools.setDomNumAction($rebateInfo.find(".commercialRebateInputVal"));
                common.tools.setDomNumAction($rebateInfo.find(".compulsoryRebateInputVal"));
            } else {
                $rebateInfo.find("#rebateTabDiv").text("无").find("#rebateTab").remove();
            }
        } else {
            $rebateInfo.remove();
        }
        /* 险种信息 */
        $("#insuranceCompany").text(common.tools.checkToEmpty(data.insuranceCompany));
        $("#orderNo").text(common.tools.checkToEmpty(no ? no : data.orderNo));
        $("#commercialPolicyNo").text(common.tools.checkToEmpty(data.commercialPolicyNo));
        $("#commercialPolicyEffectiveDate").text(common.tools.checkToEmpty(data.commercialPolicyEffectiveDate));
        $("#commercialPolicyExpireDate").text(common.tools.checkToEmpty(data.commercialPolicyExpireDate));
        $("#compulsoryPolicyNo").text(common.tools.checkToEmpty(data.compulsoryPolicyNo));
        $("#compulsoryPolicyEffectiveDate").text(common.tools.checkToEmpty(data.compulsoryPolicyEffectiveDate));
        $("#compulsoryPolicyExpireDate").text(common.tools.checkToEmpty(data.compulsoryPolicyExpireDate));
        if (data.orderStatus == '核保失败') {
            $("#orderStatusLink").text(data.orderStatus);
        } else {
            if (data.orderStatus == '出单中' && data.insuranceCompany == '安心保险') {
                $("#orderStatusLink").text('承保失败');
            }
            if (data.statusDisplay) {
                $("#orderStatus").text(common.tools.checkToEmpty(data.orderStatus) + '(' + data.statusDisplay + ')');
            } else {
                $("#orderStatus").text(common.tools.checkToEmpty(data.orderStatus));
            }
        }
        $("#payableAmount").text(common.tools.formatMoney(data.payableAmount, 2));
        $("#paidAmount").text(common.tools.formatMoney(data.paidAmount, 2));
        $("#compulsoryPremium").text(common.tools.formatMoney(data.compulsoryPremium, 2));
        if (common.tools.formatMoney(data.compulsoryPremium, 2) > 0) {
            $("#compulsoryChk").attr("checked", true);
        }
        $("#autoTax").text(common.tools.formatMoney(data.autoTax, 2));
        if (common.tools.formatMoney(data.autoTax, 2) > 0) {
            $("#autoTaxChk").attr("checked", true);
        }
        $("#commercialPremium").text(common.tools.formatMoney(data.commercialPremium, 2));
        if (common.tools.formatMoney(data.commercialPremium, 2) > 0) {
            $("#commercialChk").attr("checked", true);
        }
        $("#thirdPartyPremium").text(common.tools.formatMoney(data.thirdPartyPremium, 2));
        $("#thirdPartyAmount").text(common.tools.formatMoney(data.thirdPartyAmount, 2));
        if (data.thirdPartyPremium > 0) {
            $("#thirdPartyChk").attr("checked", true);
        }
        $("#scratchPremium").text(common.tools.formatMoney(data.scratchPremium, 2));
        $("#scratchAmount").text(common.tools.formatMoney(data.scratchAmount, 2));
        if (data.scratchPremium > 0) {
            $("#scratchChk").attr("checked", true);
        }
        $("#damagePremium").text(common.tools.formatMoney(data.damagePremium, 2));
        $("#damageAmount").text(common.tools.formatMoney(data.damageAmount, 2));
        if (data.damagePremium > 0) {
            $("#damageChk").attr("checked", true);
        }
        $("#iop").text(common.tools.formatMoney(data.iop, 2));
        if (data.iop > 0) {
            $("#iopChk").attr("checked", true);
        }
        $("#driverPremium").text(common.tools.formatMoney(data.driverPremium, 2));
        $("#driverAmount").text(common.tools.formatMoney(data.driverAmount, 2));
        if (data.driverPremium > 0) {
            $("#driverChk").attr("checked", true);
        }
        $("#passengerPremium").text(common.tools.formatMoney(data.passengerPremium, 2));
        $("#passengerAmount").text(common.tools.formatMoney(data.passengerAmount, 2));
        if (data.passengerPremium > 0) {
            $("#passengerChk").attr("checked", true);
        }
        $("#theftPremium").text(common.tools.formatMoney(data.theftPremium, 2));
        $("#theftAmount").text(common.tools.formatMoney(data.theftAmount, 2));
        if (data.theftPremium > 0) {
            $("#theftChk").attr("checked", true);
        }
        $("#spontaneousLossPremium").text(common.tools.formatMoney(data.spontaneousLossPremium, 2));
        $("#spontaneousLossAmount").text(common.tools.formatMoney(data.spontaneousLossAmount, 2));
        if (data.spontaneousLossPremium > 0) {
            $("#spontaneousLossChk").attr("checked", true);
        }
        $("#enginePremium").text(common.tools.formatMoney(data.enginePremium, 2));
        if (data.enginePremium > 0) {
            $("#engineChk").attr("checked", true);
        }
        $("#glassPremium").text(common.tools.formatMoney(data.glassPremium, 2));
        if (data.glassPremium > 0) {
            $("#glassChk").attr("checked", true);
            $("#glassType").text(common.tools.checkToEmpty(data.glassType));
        }
        $("#unableFindThirdPartyPremium").text(common.tools.formatMoney(data.unableFindThirdPartyPremium, 2));
        if (data.unableFindThirdPartyPremium > 0) {
            $("#unableFindThirdPartyChk").attr("checked", true);
        }
        $("#designatedRepairShopPremium").text(common.tools.formatMoney(data.designatedRepairShopPremium, 2));
        if (data.designatedRepairShopPremium > 0) {
            $("#designatedRepairShopChk").attr("checked", true);
        }

        /** 支付信息 **/
        var $paymentInfo = $("#paymentInfo");
        if (data.paymentInfos && data.paymentInfos.length > 0) {
            var paymentText = "";
            var num = 1;
            $.each(data.paymentInfos, function (index, payment) {
                paymentText += "<tr id=\"" + payment.id + "Payment\">" +
                    "<td class='text-center'>" + num + "</td>" +
                    "<td class='text-center'>" + payment.type + "</td>" +
                    "<td class='text-center'>" + common.checkToEmpty(payment.amount) + "</td>" +
                    "<td class='text-center'>" + payment.status + "</td>" +
                    "<td class='text-center'>" + payment.operateTime + "</td>" +
                    "<td class='text-center'>" + payment.channel + "</td>" +
                    "<td class='text-center'>" + common.checkToEmpty(payment.outTradeNo) + "</td>" +
                    "<td class='text-center'>" + common.checkToEmpty(payment.thirdpartyPaymentNo) + "</td>" +
                    "<td class='text-center'>" + common.checkToEmpty(payment.mchId) + "</td>" +
                    "<td class='text-center'>" + common.checkToEmpty(payment.paymentPlatform) + "</td>" +
                    "<td class='text-center'>" + (data.quoteType != '模糊' && payment.statusId == 1 && (payment.typeId == 1 || payment.typeId == 2) ? "<a onclick=\"detail.amend.sendPaymentMessage(" + payment.typeId + "," + payment.recordId + ",'" + payment.orderNo + "')\">发送支付短信</a>" : "") +
                    "</td>" +
                    "</tr>";
                num++;
            });
            $paymentInfo.find("#paymentTab tbody").empty().append(paymentText);
        }

        /* 车主信息 */
        $("#ownerName").text(common.tools.checkToEmpty(data.ownerName));
        $("#ownerIdentityType").text(common.tools.checkToEmpty(data.ownerIdentityType));
        $("#ownerIdentity").text(common.tools.checkToEmpty(data.ownerIdentity));

        /* 被保险人信息 */
        $("#insuredName").text(common.tools.checkToEmpty(data.insuredName));
        $("#insuredIdentityType").text(common.tools.checkToEmpty(data.insuredIdentityType));
        $("#insuredIdentity").text(common.tools.checkToEmpty(data.insuredIdentity));

        /*投保人信息*/
        $("#applicantName").text(common.tools.checkToEmpty(data.applicantName));
        $("#applicantIdentityType").text(common.tools.checkToEmpty(data.applicantIdentityType));
        $("#applicantIdNo").text(common.tools.checkToEmpty(data.applicantIdNo));

        /* 配送信息 */
        $("#receiverName").text(common.tools.checkToEmpty(data.receiver));
        $("#receiverIdentity").text(common.tools.checkToEmpty(data.identityNumber));
        $("#receiverMobile").text(common.tools.checkToEmpty(data.receiverMobile));
        $("#address").text(common.tools.checkToEmpty(data.address));
        if (data.paymentChannel == "online") {
            $("#sendDateLabel").text("派送日期：");
            $("#timePeriodLabel").text("派送时段：");
        } else if (data.paymentChannel == "offline") {
            $("#sendDateLabel").text("收款日期：");
            $("#timePeriodLabel").text("收款时段：");
        }
        $("#sendDate").text(common.tools.checkToEmpty(data.sendDate));
        $("#timePeriod").text(common.tools.checkToEmpty(data.timePeriod));
        detail.fillAddInfo(data, id);

        /* 礼品信息 */
        $("#giftDetails").text(common.tools.checkToEmpty(data.giftDetails));

        /*快递信息*/
        $("#expressCompany").text(common.tools.checkToEmpty(data.expressCompany));
        $("#trackingNo").text(common.tools.checkToEmpty(data.trackingNo));

        /*商业险保单照片：*/
        if (!common.isEmpty(data.insuranceImage)) {
            $("#insuranceImage").attr("src", data.insuranceImage.endsWith("pdf") ? "../../images/pdf.jpg" : data.insuranceImage);
            $("#insuranceImage").attr("url", data.insuranceImage);
            $("#insuranceImage").show();

        }
        /*交强险保单照片：*/
        if (!common.isEmpty(data.compulsoryInsuranceImage)) {
            $("#compulsoryInsuranceImage").attr("src", data.compulsoryInsuranceImage.endsWith("pdf") ? "../../images/pdf.jpg" : data.compulsoryInsuranceImage);
            $("#compulsoryInsuranceImage").attr("url", data.compulsoryInsuranceImage);
            $("#compulsoryInsuranceImage").show();
        }
        if (!common.isEmpty(data.compulsoryStamp)) {
            $("#compulsoryStamp").attr("src", data.compulsoryStamp.endsWith("pdf") ? "../../images/pdf.jpg" : data.compulsoryStamp);
            $("#compulsoryStamp").attr("url", data.compulsoryStamp);
            $("#compulsoryStamp").show();
        }
        // var enablePicStatus = [1, 13, 14, 15, 20, 22, 23];
        /*判断是否加载所需照片区域*/
        // if (enablePicStatus.indexOf(data.currentStatus) > -1) {
        detail.displayFlag = true;
        // } else {
        //     detail.displayFlag = false;
        //     $("#imageParentDiv").remove();
        //     $("#lastRowDiv").attr("class", "tab-top-1");
        // }


    },
    showDetailMessage: function () {
        var purchaseOrderId = common.getUrlParam("id");
        common.getByAjax(true, "get", "json", "/orderCenter/order/getDetailMessage", {
            purchaseOrderId: purchaseOrderId
        }, function (data) {
            var message = "";
            $.each(data, function (index, messageItem) {
                message += (messageItem + "<br>");
            });
            var content = "" +
                '<!DOCTYPE html>' +
                '<html>' +
                '<head>' +
                '    <meta charset="UTF-8">' +
                '</head>' +
                '<body>' +
                '<div style="border-bottom: 2px solid #ccc;margin-bottom: 10px;">' +
                '    <span style="font-size: 30px;margin-left:350px;">失败原因</span>' +
                '    <a onclick="popup.mask.hideFirstMask(true);" id="auto_detail_close" href="javascript:;" title="关闭" class="close"><i' +
                '            class="glyphicon glyphicon-remove"></i></a>' +
                '</div>' +
                '<div id="show_detail_message" style="width: 870px;height: 505px;overflow:auto;">' +
                '    <div style="margin-left: 20px;margin-top: 20px;">' + message +
                '    </div>' +
                '</div>' +
                '</body>' +
                '</html>';

            content = message === "" ? "无记录!" : content;
            popup.pop.popInput(false, content, popup.mould.first, "880px", "560px", "38%", "49%");
        }, function () {
            popup.mould.popTipsMould(true, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
        });
    },
    validateAddInfo: function () {
        var msg = "";
        var province = $("#receiverProvince").val();
        var city = $("#receiverCity").val();
        var district = $("#receiverDistrict").val();
        var street = $("#receiverStreet").val();
        var name = $("#edit_receiverName").val();
        var mobile = $("#edit_receiverMobile").val();
        msg = name == "" ? "收件人" : mobile == "" ? "手机号" : province == "" ? "省份" : city == "" ? "市" : street == "" ? "街道" : "";
        if (msg != "")
            msg += "不能为空";
        if (msg == "")
            msg = common.isMobile(mobile) ? "" : "手机号格式不正确";
        return msg;
    },

    fillAddInfo: function (data, id) {
        $("#edit_receiverName").val(common.tools.checkToEmpty(data.receiver));
        $("#edit_receiverMobile").val(common.tools.checkToEmpty(data.receiverMobile));
        detail.getProvinces(data.addressInfo);
        detail.getCities('', data.addressInfo);
        detail.getDistricts('', data.addressInfo);
        $("#receiverStreet").val(common.tools.checkToEmpty(data.addressInfo.street));

        $("#addEdit").bind({
            click: function () {
                $("#addSave").attr("disabled", false);
                //展示可编辑框
                $("#receiverName").addClass("none");
                $("#receiverMobile").addClass("none");
                $("#address").addClass("none");
                $("#edit_receiverName").removeClass("none");
                $("#edit_receiverMobile").removeClass("none");
                $("#addressInfo").removeClass("none");
            }
        })

        $("#addSave").bind({
            click: function () {
                var validateStr = detail.validateAddInfo();
                if (validateStr == "") {
                    //保存后台，在成功的回调中，提示保存成功，展示数据
                    common.ajax.getByAjaxWithJson(true, "post", "json", "/orderCenter/order/updatePurchaseOrder",
                        {
                            orderId: id,
                            originalAddressId: data.addressInfo.id,
                            newAddress: {
                                province: $("#receiverProvince").val(),
                                city: $("#receiverCity").val(),
                                district: $("#receiverDistrict").val(),
                                street: $("#receiverStreet").val(),
                                name: $("#edit_receiverName").val(),
                                mobile: $("#edit_receiverMobile").val()
                            }
                        },
                        function (data) {
                            popup.mould.popTipsMould(false, "订单修改成功", popup.mould.first, popup.mould.success, "", "57%", null);
                            var addObj = eval('(' + data.message + ')');
                            $("#receiverName").html($("#edit_receiverName").val());
                            $("#receiverMobile").html($("#edit_receiverMobile").val());
                            $("#address").html(common.checkToEmpty(addObj.provinceName) +
                                common.checkToEmpty(addObj.cityName) +
                                common.checkToEmpty(addObj.districtName) +
                                common.checkToEmpty(addObj.street));
                            $("#addSave").attr("disabled", true);
                            $("#receiverName").removeClass("none");
                            $("#receiverMobile").removeClass("none");
                            $("#address").removeClass("none");
                            $("#edit_receiverName").addClass("none");
                            $("#edit_receiverMobile").addClass("none");
                            $("#addressInfo").addClass("none");
                        },
                        function () {
                            popup.mould.popTipsMould(false, "订单修改异常", popup.mould.first, popup.mould.error, "", "57%", null);
                        }
                    );
                } else {
                    popup.mould.popTipsMould(true, validateStr, popup.mould.first, popup.mould.warning, "", "57%", null);
                }
            }
        });

        $('#receiverProvince').change(function (e) {
            $('#receiverCity')
                .empty()
                .append($("<option></option>")
                    .attr("value", '')
                    .text('请选择'));
            $('#receiverDistrict').empty().val('');
            if (!this.value) {
                return;
            }
            var current = $('option:selected', this);
            if (current.attr('type') == 2) {
                $('#receiverCity')
                    .empty()
                    .append($("<option></option>")
                        .attr("value", this.value)
                        .text(current.text()));
                detail.getDistricts(this.value);
            } else {
                detail.getCities(this.value);
            }

        });

        $('#receiverCity').change(function (e) {
            if (!this.value) {
                return;
            }
            detail.getDistricts(this.value);
        });
    },
    getProvinces: function (address) {
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/quote/areas/provinces", {},
            function (result) {
                var list = JSON.parse(result.message)
                var provinces = [];
                for (var i = 0; i < list.data.length; i++) {
                    for (var j = 0; j < list.data.length - i - 1; j++) {
                        if (list.data[j].id > list.data[j + 1].id) {
                            temp = list.data[j];
                            list.data[j] = list.data[j + 1];
                            list.data[j + 1] = temp;
                        }
                    }
                }
                // callback(list);
                list.data.forEach(function (item) {
                    $('#receiverProvince')
                        .append($("<option></option>")
                            .attr("value", item.id)
                            .attr('type', item.type)
                            .text(item.name));
                });

                if (address) {
                    //直辖市若带出的省为空，直接赋值上市
                    if (address.province) {
                        $('#receiverProvince').val(address.province);
                    } else {
                        $('#receiverProvince').val(address.city);
                    }
                }
            },
            function () {
                popup.mould.popTipsMould(true, "获取省份异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    getCities: function (provinceId, address) {
        if (address) {
            //直辖市若带出的省为空，直接赋值上市
            if (address.province) {
                provinceId = address.province;
            } else {
                provinceId = address.city;
            }
        }

        if (!provinceId) {
            return;
        }
        if (address && address.city && address.cityName) {
            $('#receiverCity')
                .empty()
                .append($("<option></option>")
                    .attr("value", address.city)
                    .text(address.cityName));
        }
        var url = '/orderCenter/quote/areas/' + provinceId + '/cities';
        common.ajax.getByAjax(true, "get", "json", url, {},
            function (result) {
                var list = JSON.parse(result.message)
                if (list.data) {
                    list.data.forEach(function (item) {
                        $('#receiverCity')
                            .append($("<option></option>")
                                .attr("value", item.id)
                                .text(item.name));
                    });
                    if (address && address.city) {
                        $('#receiverCity').val(address.city);
                    }
                }
            },
            function () {
                popup.mould.popTipsMould(true, "获取城市异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    getDistricts: function (cityId, address) {
        var $policyDistrict = $('#receiverDistrict');
        $policyDistrict.empty();
        if (address && address.city) {
            cityId = address.city;
        }
        if (!cityId) {
            return;
        }
        var url = '/orderCenter/quote/areas/' + cityId + '/districts';
        common.ajax.getByAjax(true, "get", "json", url, {},
            function (result) {
                var list = JSON.parse(result.message);
                $policyDistrict.empty();
                if (list.data) {
                    $policyDistrict.parent().show();
                    if (list.data.length > 0) {
                        list.data.forEach(function (item) {
                            $policyDistrict.append($("<option></option>")
                                .attr("value", item.id)
                                .text(item.name));
                        });
                        //从市下面没有分区切换回有分区的添加上required限制
                        if (typeof($policyDistrict.attr("required")) == "undefined") {
                            $policyDistrict.attr("required", true);
                        }
                    } else {
                        $policyDistrict.parent().hide();
                    }
                }

                if (address && address.district) {
                    $('#receiverDistrict').val(address.district);
                }
            },
            function () {
                popup.mould.popTipsMould(true, "获取地区异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );

    },
    init_detail_pop: function () {
        param.pass = 0;
        param.nopass = 0;
        param.audit = 0;
        validateCar.pop(common.getUrlParam("id"), "first");
    },
    detail_pop: function (thisLink) {
        param.imgId = 0;
        param.imgTypeId = thisLink.id;
        param.orderId = common.getUrlParam("id");

        detail.init_detail_pop();
    },
    image_del: function (thisLink) {
        param.imgId = thisLink.name;
        param.imgTypeId = thisLink.id;
        var lingParent = thisLink.parentNode;
        var parentpa = lingParent.parentElement;
        var lingSrc = $(parentpa).find("img").attr("src");
        var orderId = common.getUrlParam("id");

        popup.mould.popConfirmMould(false, "确定要删除该照片吗?", popup.mould.first, "", "57%",
            function () {
                if (common.validations.isEmpty(param.imgId) || param.imgId == "null" || lingSrc == "../../images/wutupian.jpg") {
                    popup.mould.popTipsMould(false, "当前类型还没有上传照片,不能执行此操作", popup.mould.second, popup.mould.error, "", "57%", null);
                    return;
                }
                common.getByAjax(false, "post", "json", "/orderCenter/order/image/" + param.imgId,
                    {
                        "imageTypeId": param.imgTypeId,
                        "orderId": orderId
                    }, function (data) {
                        if (data.pass) {
                            $.ajax({
                                async: false,
                                type: "GET",
                                dataType: "json",
                                url: "/orderCenter/quote/photo/setHint",
                                data: {id:param.imgId,hint:"k"},
                                success: function (data) {
                                },
                                error: function () {
                                }
                            });
                            popup.mould.popTipsMould(false, "删除成功!", popup.mould.second, popup.mould.success, "", "57%", null);
                            window.location.reload();
                        }
                    }, function (data) {
                        popup.mould.popTipsMould(false, "删除失败!", popup.mould.second, popup.mould.error, "", "57%", null);
                    }
                )
            },
            function () {
                popup.mask.hideFirstMask(false);
            }
        );


    },
    sendMessage: function () {
        var message = "";
        var mobile = "";
        //获取message
        common.getByAjax(false, "get", "json", "/orderCenter/order/image/getMessage",
            {
                'purchaseOrderId': common.getUrlParam("id")
            }, function (data) {
                message = data.sendMessage;
                mobile = data.mobile;
            }, function () {
                popup.mould.popTipsMould(false, "获取短信内容失败!", popup.mould.second, popup.mould.error, "", "57%", null);
            }
        );

        //弹出确认发送短信框
        popup.mould.popSendSmsConfirmMould(false, message, popup.mould.first, "", "57%",
            function () {
                popup.mask.hideFirstMask(false);
                //发送短信
                common.getByAjax(false, "post", "json", "/orderCenter/order/image/sendMessage",
                    {
                        'purchaseOrderId': common.getUrlParam("id")
                    }, function () {
                        popup.mould.popTipsMould(false, "发送成功!", popup.mould.second, popup.mould.success, "", "57%", null);
                    }, function () {
                        popup.mould.popTipsMould(false, "发送失败!", popup.mould.second, popup.mould.error, "", "57%", null);
                    }
                );
            },
            function () {
                popup.mask.hideFirstMask(false);
            }
        );
    },
    threewayCall: function (mobile) {
        common.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/telMarketer/call", {customerNumber: mobile},
            function (data) {
                if (data.pass) {
                    //popup.mould.popTipsMould(true,"呼叫成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                } else {
                    popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "53%", null);
                    window.parent.$("#toSave").attr("disabled", false);
                }
            },
            function () {
                popup.mould.popTipsMould(false, "发生异常,获取不到data,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                window.parent.$("#toSave").attr("disabled", false);
            }
        );
    },
    amend: {
        orderAmendListContent: "",
        amendQuoteRecordContent: "",
        sendPaymentMessage: function (type, recordId, orderNo) {
            common.getByAjax(false, "post", "json", "/orderCenter/order/sendMessage",
                {
                    quoteRecordId: recordId,
                    paymentType: type,
                    orderNo: orderNo
                },
                function (data) {
                    if (data.pass)
                        popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.success, "", "57%", null);
                },
                function () {
                    popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        initBtn: function (id) {
            common.getByAjax(false, "get", "json", "/orderCenter/order/findAmendRecordsByOrderId", {purchaseOrderId: id},
                function (data) {
                    if (data.length > 0) {
                        $("#seeQuoteRecord").removeClass("none");
                        $("#seeQuoteRecord").unbind("click").bind({
                            click: function () {
                                detail.amend.initDivContent.initQuoteRecordListDivContent();
                                popup.pop.popInput(false, detail.amend.orderAmendListContent, popup.mould.first, "880px", "560px", "38%", "49%");
                                parent.$("#popover_normal_input .close").unbind("click").bind({
                                    click: function () {
                                        popup.mask.hideFirstMask(true);
                                    }
                                });
                                detail.amend.initOrderAmendListContent(data, id);
                            }
                        });
                    }
                },
                function () {
                    popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        initDivContent: {
            initQuoteRecordListDivContent: function () {
                var autoContent = $("#orderAmendList");
                if (autoContent.length > 0) {
                    detail.amend.orderAmendListContent = autoContent.html();
                    autoContent.remove();
                }
            },
            initAmendQuoteRecordDivContent: function () {
                var autoContent = $("#amendQuoteRecord");
                if (autoContent.length > 0) {
                    detail.amend.amendQuoteRecordContent = autoContent.html();
                    autoContent.remove();
                }
            }
        },
        initOrderAmendListContent: function (data, purchaseOrderId) {
            var orderAmendListInfo = $("#orderAmendListContent");
            var orderAmendListText = "";
            var num = 1;
            $.each(data, function (index, orderAmend) {
                orderAmendListText += "<tr id=\"" + orderAmend.recordId + "Amend\">" +
                    "<td class='text-center'>" + num + "</td>" +
                    "<td class='text-center'>" + common.tools.formatMoney(common.checkToEmpty(orderAmend.compulsoryAmount), 2) + "</td>" +
                    "<td class='text-center'>" + common.tools.formatMoney(common.checkToEmpty(orderAmend.autoTax), 2) + "</td>" +
                    "<td class='text-center'>" + common.tools.formatMoney(common.checkToEmpty(orderAmend.commercialAmount), 2) + "</td>" +
                    "<td class='text-center'>" + common.tools.formatMoney(common.checkToEmpty(orderAmend.totalAmount), 2) + "</td>" +
                    "<td class='text-center'>" + orderAmend.createTime + "</td>" +
                    "<td class='text-center'><a onclick='detail.amend.quoteRecordDetail(" + orderAmend.orderHistoryId + ")'>报价详情</a></td>" +
                    "</tr>";
                num++;
            });
            orderAmendListInfo.find("#quote_list_tab tbody").empty().append(orderAmendListText);
        },
        quoteRecordDetail: function (orderHistoryId) {
            common.getByAjax(false, "get", "json", "/orderCenter/order/findQuoteRecordByPurchaseOrderHistoryId",
                {
                    orderHistoryId: orderHistoryId
                },
                function (data) {
                    if (data != null) {
                        detail.amend.initDivContent.initAmendQuoteRecordDivContent();
                        popup.pop.popInput(false, detail.amend.amendQuoteRecordContent, popup.mould.second, "600px", "625px", "33%", "52%");
                        parent.$("#popover_normal_input_second .close").unbind("click").bind({
                            click: function () {
                                popup.mask.hideSecondMask(true);
                            }
                        });
                        detail.amend.fillQuoteRecordDetailContent(data);
                    } else {
                        popup.mould.popTipsMould(false, "没有对应的报价记录信息", popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    popup.mould.popTipsMould(false, "系统异常", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },
        fillQuoteRecordDetailContent: function (data) {
            $("#amend_insuranceCompany").text(common.tools.checkToEmpty(data.insuranceCompany));
            $("#amend_orderNo").text(common.tools.checkToEmpty(data.orderNo));
            $("#amend_commercialPolicyNo").text(common.tools.checkToEmpty(data.commercialPolicyNo));
            $("#amend_compulsoryPolicyNo").text(common.tools.checkToEmpty(data.compulsoryPolicyNo));
            $("#amend_orderStatus").text(common.tools.checkToEmpty(data.orderStatus));
            $("#amend_payableAmount").text(common.tools.formatMoney(data.payableAmount, 2));
            $("#amend_discount_info").text(data.giftDetails);
            $("#amend_compulsoryPremium").text(common.tools.formatMoney(data.compulsoryPremium, 2));
            if (common.tools.formatMoney(data.compulsoryPremium, 2) > 0) {
                $("#amend_compulsoryChk").attr("checked", true);
            }
            $("#amend_autoTax").text(common.tools.formatMoney(data.autoTax, 2));
            if (common.tools.formatMoney(data.autoTax, 2) > 0) {
                $("#amend_autoTaxChk").attr("checked", true);
            }
            $("#amend_commercialPremium").text(common.tools.formatMoney(data.commercialPremium, 2));
            if (common.tools.formatMoney(data.commercialPremium, 2) > 0) {
                $("#amend_commercialChk").attr("checked", true);
            }
            $("#amend_thirdPartyPremium").text(common.tools.formatMoney(data.thirdPartyPremium, 2));
            $("#amend_thirdPartyAmount").text(common.tools.formatMoney(data.thirdPartyAmount, 2));
            if (data.thirdPartyPremium > 0) {
                $("#amend_thirdPartyChk").attr("checked", true);
            }
            $("#amend_scratchPremium").text(common.tools.formatMoney(data.scratchPremium, 2));
            $("#amend_scratchAmount").text(common.tools.formatMoney(data.scratchAmount, 2));
            if (data.scratchPremium > 0) {
                $("#amend_scratchChk").attr("checked", true);
            }
            $("#amend_damagePremium").text(common.tools.formatMoney(data.damagePremium, 2));
            $("#amend_damageAmount").text(common.tools.formatMoney(data.damageAmount, 2));
            if (data.damagePremium > 0) {
                $("#amend_damageChk").attr("checked", true);
            }
            $("#amend_iop").text(common.tools.formatMoney(data.iop, 2));
            if (data.iop > 0) {
                $("#amend_iopChk").attr("checked", true);
            }
            $("#amend_driverPremium").text(common.tools.formatMoney(data.driverPremium, 2));
            $("#amend_driverAmount").text(common.tools.formatMoney(data.driverAmount, 2));
            if (data.driverPremium > 0) {
                $("#amend_driverChk").attr("checked", true);
            }
            $("#amend_passengerPremium").text(common.tools.formatMoney(data.passengerPremium, 2));
            $("#amend_passengerAmount").text(common.tools.formatMoney(data.passengerAmount, 2));
            if (data.passengerPremium > 0) {
                $("#amend_passengerChk").attr("checked", true);
            }
            $("#amend_theftPremium").text(common.tools.formatMoney(data.theftPremium, 2));
            $("#amend_theftAmount").text(common.tools.formatMoney(data.theftAmount, 2));
            if (data.theftPremium > 0) {
                $("#amend_theftChk").attr("checked", true);
            }
            $("#amend_spontaneousLossPremium").text(common.tools.formatMoney(data.spontaneousLossPremium, 2));
            $("#amend_spontaneousLossAmount").text(common.tools.formatMoney(data.spontaneousLossAmount, 2));
            if (data.spontaneousLossPremium > 0) {
                $("#amend_spontaneousLossChk").attr("checked", true);
            }
            $("#amend_enginePremium").text(common.tools.formatMoney(data.enginePremium, 2));
            if (data.enginePremium > 0) {
                $("#amend_engineChk").attr("checked", true);
            }
            $("#amend_glassPremium").text(common.tools.formatMoney(data.glassPremium, 2));
            if (data.glassPremium > 0) {
                $("#amend_glassChk").attr("checked", true);
                $("#amend_glassType").text(common.tools.checkToEmpty(data.glassType));
            }
            $("#amend_unableFindThirdPartyPremium").text(common.tools.formatMoney(data.unableFindThirdPartyPremium, 2));
            if (data.unableFindThirdPartyPremium > 0) {
                $("#amend_unableFindThirdPartyChk").attr("checked", true);
            }
            $("#amend_designatedRepairShopPremium").text(common.tools.formatMoney(data.designatedRepairShopPremium, 2));
            if (data.designatedRepairShopPremium > 0) {
                $("#amend_designatedRepairShopPremiumChk").attr("checked", true);
            }
        }

    },
};


function getInnerImageHtml(data) {
    var innerImageHtml = "";

    $.each(data.subTypeList, function (index, subImageTypeModel) {
        var statusPic = "";
        var imageInfo = subImageTypeModel.purchaseOrderImage;
        var subImageType = subImageTypeModel.purchaseOrderImageType;
        if (!common.validations.isEmpty(imageInfo)) {
            var imageSrc;
            var imageStatus;
            if (!common.validations.isEmpty(imageInfo.url)) {
                imageSrc = imageInfo.url;
                imageStatus = imageInfo.status;
            } else {
                imageSrc = "../../images/wutupian.jpg";
                imageStatus = 0;
            }
            if (imageInfo.status == 2) {
                statusPic = "<img src='../../images/true.png' width='50' height='30' style='float:left;position: absolute;opacity:0.7'>";
            } else if (imageInfo.status == 3) {
                statusPic = "<img src='../../images/false.png' width='50' height='30' style='float:left;position: absolute;opacity:0.7'>";
            }

            var operationsText = "<a id=" + subImageType.id + " onclick='detail.detail_pop(this);' href=\"javascript:;\" >编辑</a>";
            if (imageStatus == 0 || imageStatus == 2) {//未上传 或 审核通过
                //只显示编辑
                operationsText = "<a id=" + subImageType.id + " onclick='detail.detail_pop(this);' href=\"javascript:;\" >编辑</a>";
            } else if (imageStatus == 1) {//待审核
                //只显示审核
                operationsText = "<a id=" + subImageType.id + " onclick='detail.detail_pop(this);' href=\"javascript:;\" >审核</a>";
            } else if (imageStatus == 3) {//审核为通过
                //要显示  编辑 删除
                operationsText =
                    "       <a id=" + subImageType.id + " onclick='detail.detail_pop(this);' href=\"javascript:;\" >编辑</a>&#12288;" +
                    "       <a id=" + subImageType.id + " name=" + imageInfo.id + " onclick='detail.image_del(this);'>删除</a>";
            }

            innerImageHtml +=
                "<div style=\"float:left; margin-left: 40px; width:100px; height: 200px; \">                                                                                                                                        " +
                "	<div style=\"text-align: center;width: 100%;\">&nbsp;</div>                                                                                                                                     " +
                "	<div style=\"height: 100px;\">" + statusPic + "<img id=" + subImageType.id + " src=" + imageSrc + "  class=\"image\" alt=" + subImageType.name + " style=\"width:100%;height: 100px;\"></div>                                                                                       " +
                "	<div style=\"text-align: center;width: 100%;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;width:100px;\" title=" + subImageType.name + ">" + subImageType.name + "</div>                          " +
                "	<div style=\"text-align: center;width: 100%; margin-top: 5px\">" +
                operationsText +
                "   </div>    " +
                "</div>                                                                                                                                                                                              ";
        } else {//该分支现在不可能满足,所有的imageInfo都不会为空
            innerImageHtml +=
                "<div style=\"float:left; margin-left: 40px; width:100px; height: 200px \">                                                                                                                                        " +
                "	<div style=\"text-align: center;width: 100%;\">&nbsp;</div>                                                                                                                                     " +
                "	<div style=\"height: 100px;\"><img src=../../images/wutupian.jpg class=\"image\" alt=无照片  style=\"width:100%;height:100px;\"></div>                                                                                     " +
                "	<div style=\"text-align: center;width: 100%;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;width: 100px;\">" + subImageType.name + "</div>                          " +
                "	<div style=\"text-align: center;width: 100%; margin-top: 5px\">" +
                "       <a id=" + subImageType.id + " onclick='detail.detail_pop(this);' href=\"javascript:;\" >编辑</a>&#12288;" +
                "</div>                                                                                                                                                                                              ";
        }

    });
    return innerImageHtml;
}

function modifyRebate(type, purchaseOrderId, actionName) {
    var $typeRebate = $("#rebateTab").find("#" + type + "Rebate");
    if (actionName == "modify") {
        $typeRebate.find(".compulsoryRebate").hide().siblings(".compulsoryRebateInput").show();
        $typeRebate.find(".commercialRebate").hide().siblings(".commercialRebateInput").show();
        $typeRebate.find(".toAction").text("保存").attr("onclick", "modifyRebate(\"" + type + "\"," + purchaseOrderId + ",\"save\")");
    } else if (actionName == "save") {
        if (!$typeRebate.find(".commercialRebateInputVal").val() && !$typeRebate.find(".compulsoryRebateInputVal").val()) {
            popup.mould.popTipsMould(true, "请填写完费率再保存", popup.mould.first, popup.mould.warning, "", "", null);
            return false;
        }
        common.ajax.getByAjax(true, "put", "json", "/orderCenter/insuranceOrderRebate/purchaseOrder/" + purchaseOrderId,
            {
                purchaseOrderId: purchaseOrderId,
                type: type,
                commercialRebate: $typeRebate.find(".commercialRebateInputVal").val(),
                compulsoryRebate: $typeRebate.find(".compulsoryRebateInputVal").val()
            },
            function (data) {
                $typeRebate.find(".toAction").text("修改").attr("onclick", "modifyRebate(\"" + type + "\"," + purchaseOrderId + ",\"modify\")");
                $typeRebate.find(".compulsoryRebate").show().text($typeRebate.find(".compulsoryRebateInputVal").val() + "%").siblings(".compulsoryRebateInput").hide();
                $typeRebate.find(".commercialRebate").show().text($typeRebate.find(".commercialRebateInputVal").val() + "%").siblings(".commercialRebateInput").hide();
            },
            function () {
                popup.mould.popTipsMould(true, "修改费率异常", popup.mould.first, popup.mould.error, "", "", null);
            }
        );
    }
}

function calculateRebateAmount(type, name, rebateDom) {
    var $typeRebate = $("#rebateTab").find("#" + type + "Rebate");
    var $nameAmount = $typeRebate.find("." + name + "Amount");
    var $sumAmount = $typeRebate.find(".sumAmount");
    var oriPremium = $("#" + name + "Premium").text();
    $nameAmount.text(common.tools.formatMoney(parseFloat(parseFloat(oriPremium) * $(rebateDom).val() / 100), 2));
    $sumAmount.text(common.tools.formatMoney(parseFloat($typeRebate.find(".compulsoryAmount").text()) + parseFloat($typeRebate.find(".commercialAmount").text()), 2));
}

$(document).ready(function() {
    $('#receiverProvince').select2();
    $('#receiverCity').select2();
    $('#receiverDistrict').select2();
});
