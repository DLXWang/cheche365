/**
 * Created by wangfei on 2016/1/4.
 */
var quoteGift = {
    page: 1,
    position: "",
    giftDisplayContent: "",
    totalPremium: "",
    giftMap: new Map(),
    order: "",
    initDisplayContent: function() {
        var $giftContent = $("#giftContent");
        if ($giftContent.length > 0) {
            quoteGift.giftDisplayContent = $giftContent.html();
            $giftContent.remove();
        }
    },
    displayUserGifts: function(position, totalPremium, order) {
        quoteGift.position = position;
        quoteGift.order = order;
        quoteGift.giftMap.clear();
        quoteGift.interface.getUserGifts(quoteGift.page, quoteGift.order.quoteId,
            function(data) {
                var giftJson = JSON.parse(data.message);
                $("#giftDisplayBtn").attr("disabled", false);
                //返回非200
                if (giftJson.code != 200) {
                    popup.mask.hideAllMask(true);
                    popup.mould.popTipsMould(true, giftJson.message, quoteGift.position, popup.mould.warning, "", "57%", null);
                    return;
                }
                quoteGift.totalPremium = totalPremium;
                quoteGift.initDisplayContent();
                popup.pop.popInput(true, quoteGift.giftDisplayContent, quoteGift.position, "560px", "auto", "40%", "51%");
                var $popInput = common.tools.getPopInputDom(quoteGift.position, true);
                var $giftInfo = $popInput.find("#giftInfo");
                $popInput.find(".theme_poptit .close").unbind("click").bind({
                    click: function() {
                        popup.mask.hideMaskByPosition(quoteGift.position, true);
                    }
                });

                //无可使用的优惠券
                if (giftJson.data.totalElements < 1) {
                    $giftInfo.find("#noGiftText").show();
                    return;
                }

                //填充数据
                var giftItems = "";
                var tdStyle = "padding: 3px;";
                quoteGift.giftMap.clear();
                $.each(giftJson.data.content, function(index, gift) {
                    var amount=0;
                    if(gift.giftType.useType.id==1){
                        amount=(gift.giftAmount && gift.giftAmount > 0) ? gift.giftAmount : 0;
                    }else{
                        amount=(gift.giftDisplay && gift.giftDisplay > 0) ? gift.giftDisplay : 0;
                    }
                    giftItems +=
                        '<tr class="text-center">' +
                        '<td style="' + tdStyle + '">' +
                        gift.reason + gift.giftDisplay +
                        '</td>' +
                        '<td style="' + tdStyle + '">' +amount+ '元' +
                        '</td>' +
                        '<td style="' + tdStyle + '">' +
                        '<button id="giftBtn' + gift.id + '" class="btn btn-sm btn-success" onclick="quoteGift.useGift(' + gift.id + ')">使用</button>' +
                        '</td>' +
                        '</tr>';
                    //方便使用时从map中取出使用的优惠券obj
                    quoteGift.giftMap.put(gift.id, gift);
                });
                $giftInfo.find("#giftTab").append(giftItems).show();

                //当前正在使用的优惠券
                var giftId = $("#giftId").val();
                if (giftId > 0) {
                    var $giftBtn= $giftInfo.find("#giftTab #giftBtn" + giftId);
                    $giftBtn.removeClass("btn-success").addClass("btn-danger").text("取消使用");
                    $giftBtn.unbind("click").bind({
                        click:function(){
                            quoteGift.clearUserGift();
                            if(quote.source=='order'){
                                quote.init.showAmendInfo(quoteGift.totalPremium);
                            }
                        }
                    })
                    $giftInfo.find("#giftTab #giftBtn" + giftId).removeClass("btn-success").addClass("btn-danger").text("取消使用");

                    //$giftInfo.find("#giftTab #giftBtn" + giftId).attr("disabled", true).removeClass("btn-success").addClass("btn-danger").text("当前使用");
                }

                //分页
                if (giftJson.data.totalPages > 1) {
                    $popInput.find(".customer-pagination").show();
                    $giftInfo.css("min-height", "225px");
                    $.jqPaginator('#giftPagination',
                        {
                            totalPages: giftJson.data.totalPages,
                            visiblePages: 3,
                            currentPage: quoteGift.page,
                            onPageChange: function (pageNum, pageType) {
                                if (pageType == "change") {
                                    quoteGift.page = pageNum;
                                    quoteGift.displayUserGifts(quoteGift.position, quoteGift.totalPremium,quoteGift.order);
                                }
                            }
                        }
                    );
                } else {
                    $giftInfo.css("min-height", "auto");
                }
            },
            function() {
                $("#giftDisplayBtn").attr("disabled", false);
                popup.mask.hideAllMask(true);
                popup.mould.popTipsMould(true, "获取用户优惠券异常！", quoteGift.position, popup.mould.error, "", "57%", null);
            }
        );
    },
    useGift: function(giftId,giftAmount) {
        popup.mask.hideMaskByPosition(quoteGift.position, true);
        var gift = quoteGift.giftMap.get(giftId);
        console.log("gift obj: " + gift);
        $("#giftId").val(giftId);
        var $giftAmountText = $("#giftAmountText");
        var giftAmount = gift.giftAmount;
        $("#giftAmount").val(giftAmount);
        //使用优惠券不可使用优惠
        $("#cancelPreferentialBtn").click();
        //将优惠后的金额进行还原
        $("#paidSumPremium").html("0.00");
        //还原保费
        orderPremium.initOrderPremium(false);
        if (giftAmount > 0 && (!gift.giftType || (gift.giftType.category != 4 && gift.giftType.category != 6))) {
            $giftAmountText.text("（已使用" + giftAmount + "元" + gift.giftType.name + "）");
            var newAmount = parseFloat(quoteGift.totalPremium) - parseFloat(giftAmount);
            //防止保费小于0
            newAmount = newAmount < 0 ? 0 : newAmount;
            //$totalPremiumText.text(common.formatMoney(newAmount, 2));
            //保费变化效果
            var $payableSumPremium = $("#payableSumPremium");
            new CountUp(
                "payableSumPremium", $payableSumPremium.text(), newAmount, 2, 0.5,
                {useEasing: true, useGrouping: false}
            ).start();
            if(quote.source=='order'){
                var paidSumPremium=Number($("#paidSumPremium").html());
                if(common.isEmpty(paidSumPremium) || paidSumPremium == 0){
                    paidSumPremium=newAmount;
                }
                orderPremium.premium.paidAmount=newAmount;
                quote.init.showAmendInfo(paidSumPremium);
            }
        } else {
            if (gift.giftType) {
                switch (gift.giftType.category) {
                    case 4:
                    case 6:
                        var giftDisplay = gift.giftDisplay;
                        var amountText = "（送" + (giftDisplay > 0 ? giftDisplay + '元' : '') + gift.giftType.name + "）";
                        if(giftAmount > 0)
                            amountText = "（返现" + giftAmount + "元）";
                        $giftAmountText.text(amountText);
                        if(quote.source=='order'){
                            quote.init.showAmendInfo(quoteGift.totalPremium);
                        }
                        break;
                }
            }
        }
    },
    /**
     * 清空使用优惠券
     */
    clearUserGift: function() {
        orderPremium.initOrderPremium(true);
        $("#giftId").val("");
        $("#giftAmountText").empty();
        $("#giftAmount").val("");
    },
    interface: {
        getUserGifts: function(page, quoteRecordId, callback_success, callback_fail) {
            common.ajax.getByAjaxWithHeader(true, "get", "json", "/orderCenter/quote/gifts", {page: page-1, quoteRecordId: quoteRecordId,purchaseOrderId:quote.orderId},
                function(data) {
                    callback_success(data);
                },
                function() {
                    callback_fail();
                },
                quote.interface.getHeaderMap()
            );
        }
    }
};
