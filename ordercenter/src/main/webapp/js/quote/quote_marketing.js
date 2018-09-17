/**
 * Created by wangfei on 2016/5/11.
 */
var quoteMarketing = {
    position: "",
    marketingDisplayContent: "",
    order: "",
    initDisplayContent: function() {
        var $marketingContent = $("#marketingContent");
        if ($marketingContent.length > 0) {
            quoteMarketing.marketingDisplayContent = $marketingContent.html();
            $marketingContent.remove();
        }
    },
    displayMarketings: function(position,totalPremium, order,manualFlag) {
        quoteMarketing.position = position;
        quoteGift.totalPremium = totalPremium;
        quoteMarketing.interface.getMarketings(order.quoteId,
            function(data) {
                var marketingJson = JSON.parse(data.message);
                $("#marketingDisplayBtn").attr("disabled", false);
                //返回非200
                if (marketingJson.code != 200) {
                    popup.mask.hideAllMask(true);
                    popup.mould.popTipsMould(true, marketingJson.message, quoteMarketing.position, popup.mould.warning, "", "57%", null);
                    return;
                }
                quoteMarketing.initDisplayContent();
                popup.pop.popInput(true, quoteMarketing.marketingDisplayContent, quoteMarketing.position, "600px", "auto", "40%", "51%");
                var $popInput = common.tools.getPopInputDom(quoteMarketing.position, true);
                var $marketingInfo = $popInput.find("#marketingInfo");
                $popInput.find(".theme_poptit .close").unbind("click").bind({
                    click: function() {
                        popup.mask.hideMaskByPosition(quoteMarketing.position, true);
                    }
                });

                //无活动
                if (marketingJson.data.length < 1) {
                    $marketingInfo.find("#noMarketingText").show();
                    return;
                }

                //填充数据
                var marketingItems = "";
                $.each(marketingJson.data, function(index, marketing) {
                    marketingItems +=
                        '<tr class="text-center">' +
                        '<td style="padding: 3px;width:66%;">' +
                        marketing.name +
                        '</td>' +
                        '<td style="padding: 3px;width:17%;">' +
                        '<a target="_blank" href="' + marketing.detailUrl + '">活动详情</a>' +
                        '</td>' +
                        '<td style="padding: 3px;width:17%;">' +
                        '<button id="marketingBtn' + marketing.code + '" class="btn btn-sm ' + (marketing.involved ? "btn-danger" : "btn-success") + '"' + (marketing.involved ? " disabled=true" : "") +
                        (manualFlag =="manual" ?
                            '" onclick="quoteMarketing.joinMarketing(' + marketing.code + ',\''+manualFlag+'\')">' + (marketing.involved ? "已参加" : "参加") + '</button>'
                            :
                            '" onclick="quoteMarketing.joinMarketing(' + marketing.code + ')">' + (marketing.involved ? "已参加" : "参加") + '</button>') +
                        '</td>' +
                        '</tr>';
                });
                $marketingInfo.find("#marketingTab").append(marketingItems).show();
            },
            function() {
                $("#marketingDisplayBtn").attr("disabled", false);
                popup.mask.hideAllMask(true);
                popup.mould.popTipsMould(true, "获取活动列表异常！", quoteMarketing.position, popup.mould.error, "", "55%", null);
            }
        );
    },
    joinMarketing: function(code,manualFlag) {
        var $popInput = common.tools.getPopInputDom(quoteMarketing.position, true);
        $popInput.find("#marketingBtn" + code).attr("disabled", true);
        quoteMarketing.interface.addMarketing(code,manualFlag,
            function(result) {
                if (!result.pass) {
                    popup.mould.popTipsMould(true, result.message, popup.mould.second, popup.mould.warning, "", "55%", null);
                    $popInput.find("#marketingBtn" + code).attr('disabled', false);
                    return;
                }
                var marketingJson = JSON.parse(result.message);
                if (marketingJson.code != 200) {
                    popup.mould.popTipsMould(true, marketingJson.message, popup.mould.second, popup.mould.warning, "", "55%", null);
                    $popInput.find("#marketingBtn" + code).attr('disabled', false);
                    return;
                }
                if(marketingJson.data.gift){
                    quoteGift.giftMap.put(marketingJson.data.gift.id, marketingJson.data.gift);
                    quoteGift.useGift(marketingJson.data.gift.id);
                }
                $popInput.find("#marketingBtn" + code).removeClass("btn-success").addClass("btn-danger").text('已参加');
            },
            function() {
                popup.mould.popTipsMould(true, "参加活动异常！", popup.mould.second, popup.mould.warning, "", "55%", null);
                return;
            }
        );
    },
    interface: {
        getMarketings: function(quoteRecordId, callback_success, callback_fail) {
            common.ajax.getByAjaxWithHeader(true, "get", "json", "/orderCenter/quote/" + quoteRecordId + "/marketings", null,
                function(data) {
                    callback_success(data);
                },
                function() {
                    callback_fail();
                },
                quote.interface.getHeaderMap()
            );
        },
        addMarketing: function(code, manualFlag, callback_success, callback_fail) {
            var quoteRecordKey=quote.quoteRecordKey.get(quote.companyId);
            var url= "/orderCenter/quote/marketings/"+quoteRecordKey+"/" + code;
            if(quote.source=="order"){
                url="/orderCenter/quote/manualMarketings/"+quote.newQuoteRecordId+"/" + code;
            }else if(manualFlag=="manual" || common.isEmpty(quoteRecordKey)){
                var quoteRecordId = quote.quoteOrder.quoteId;
                url = "/orderCenter/quote/manualMarketings/"+quoteRecordId+"/" + code;
            }
            common.ajax.getByAjaxWithHeader(true, "post", "json",url, null,
                function(result) {
                    callback_success(result);
                },
                function() {
                    callback_fail();
                },
                quote.interface.getHeaderMap());
        },



    }
};
