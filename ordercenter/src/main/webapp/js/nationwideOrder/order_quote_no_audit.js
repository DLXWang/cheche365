/**
 * Created by wangfei on 2015/11/17.
 */
var quoteNoAudit = {
    params: {
        page: new Properties(1, ""),
        keyType: ""
    },
    initialization: {
        init: function() {
            this.initParams();
            this.initPage();
        },
        initParams: function() {
            var params = quoteNoAudit.params;
        },
        initPage: function() {
            quoteNoAudit.interface.getAreas(function(data) {
                var $area = $("#areaId");
                var options = "";
                $.each(data, function(index, area) {
                    options += "<option value=\"" + area.id + "\">" + area.name + "</option>";
                });
                $area.append(options);
                $area.unbind("change").bind({
                    change: function() {
                        quoteNoAudit.params.page.currentPage = 1;
                        quoteNoAudit.params.page.keyword = $("#keyword").val();
                        quoteNoAudit.params.keyType = $("#keyType").val();
                        quoteNoAudit.params.page.sourceChannel = false;
                        quoteNoAudit.list.listOrders();
                    }
                })
            });
        }
    },
    list: {
        listOrders: function() {
            quoteNoAudit.interface.getOrdersByPage(function(data) {
                var $pagination = $(".customer-pagination");
                var $totalCount = $("#totalCount");
                var $tabBody = $("#list_tab tbody");
                $tabBody.empty();
                if (data.pageInfo.totalElements < 1) {
                    $totalCount.text("0");
                    $pagination.hide();
                    if (!common.isEmpty(quoteNoAudit.params.page.keyword)) {
                        popup.mould.popTipsMould(false, "无符合条件的结果", popup.mould.first, "", "", "", null);
                    }
                    return false;
                }
                $totalCount.text(data.pageInfo.totalElements);
                if (data.pageInfo.totalPage > 1) {
                    $pagination.show();
                    $.jqPaginator('.pagination',
                        {
                            totalPages: data.pageInfo.totalPage,
                            visiblePages: quoteNoAudit.params.page.visiblePages,
                            currentPage: quoteNoAudit.params.page.currentPage,
                            onPageChange: function (pageNum, pageType) {
                                if (pageType == "change") {
                                    quoteNoAudit.params.page.currentPage = pageNum;
                                    quoteNoAudit.list.listOrders();
                                }
                            }
                        }
                    );
                } else {
                    $pagination.hide();
                }
                $tabBody.append(quoteNoAudit.list.fixTabContent(data.viewList));
                common.scrollToTop();
            });
        },
        fixTabContent: function(dataList) {
            var content = "";
            $.each(dataList, function(index, model) {
                content +=
                    "<tr id=\"item_" + model.id + "\">" +
                        quoteNoAudit.list.fixOneItem(model) +
                    "</tr>";
            });
            return content;
        },
        fixOneItem: function(model) {
            var tdPrefix = "<td class=\"text-center\">";
            var tdEnd = "</td>";
            var itemTd =
                tdPrefix +common.getOrderIcon(model.channelIcon)+
                    "<a href=\"/page/nationwideOrder/order_detail.html?id=" + model.id + "\" target=\"_blank\">" + model.orderNo + "</a><br/>" +
                    "<span>" + model.createTime + "</span>" +
                tdEnd +
                tdPrefix +
                    "<span>" + common.tools.checkToEmpty(model.owner) + "</span><br/>" +
                    "<span>" + common.tools.checkToEmpty(model.licensePlateNo) + "</span>" +
                tdEnd +
                tdPrefix +
                    model.area.name +
                tdEnd +
                tdPrefix +
                    model.insuranceCompany.name +
                tdEnd +
                tdPrefix +
                    (model.institution ? model.institution.name : "") +
                tdEnd +
                tdPrefix +
                    "<span style=\"color: " + (model.incomeStatus ? "green" : "red") + "\">收益：" + (model.incomeStatus ? "正常" : "异常")  + "</span><br/>" +
                    "<span style=\"color: " + (model.matchStatus ? "green" : "red") + "\">险种保额：" + (model.matchStatus ? "匹配" : "不匹配")  + "</span>" +
                tdEnd +
                tdPrefix +
                    "<span id=\"item_operator_" + model.id + "\">" + common.tools.checkToEmpty(model.operatorName) + "</span><br/>" +
                    "<span id=\"item_updateTime_" + model.id + "\">" + common.tools.checkToEmpty(model.updateTime) + "</span>" +
                tdEnd +
                tdPrefix +
                    "<a href=\"javascript:;\" onclick=\"orderComment.popCommentList(" + model.purchaseOrderId + ", 'first');\">查看备注</a>" +
                tdEnd +
                tdPrefix +
                    "<a href=\"javascript:;\" onclick=\"quoteNoAudit.quoteInfo.updateQuote(" + model.id + ");\">修改报价</a>" +
                    "<a href=\"javascript:;\" onclick=\"quoteNoAudit.warningInfo.detail(" + model.id + "," + model.purchaseOrderId + ");\" style=\"margin-left: 10px;\">预警详情</a>" +
                tdEnd;
            return itemTd;
        }
    },
    warningInfo: {
        detail: function(id, purchaseOrderId) {
            if (!common.permission.validUserPermission("or07010601")) {
                return;
            }
            warningDetail.popDetail(id, purchaseOrderId, popup.mould.first, true,
                function() {
                    popup.mask.hideAllMask(false);
                    quoteNoAudit.list.listOrders();
                },
                function() {
                    popup.mask.hideAllMask(false);
                    quoteNoAudit.list.listOrders();
                }
            );
        }
    },
    quoteInfo: {
        updateQuote: function(id) {
            if (!common.permission.validUserPermission("or07010601")) {
                return;
            }
            quote_pop.listInfo.popup(id, function() {
                popup.mask.hideAllMask(false);
                quoteNoAudit.params.page.currentPage = 1;
                quoteNoAudit.params.page.keyword = "";
                quoteNoAudit.params.keyType = "";
                quoteNoAudit.list.listOrders();
            });
        }
    },
    interface: {
        getOrdersByPage: function(callback) {
            var reqParams = {
                currentPage :  quoteNoAudit.params.page.currentPage,
                pageSize :     quoteNoAudit.params.page.pageSize,
                sourceChannel :quoteNoAudit.params.page.sourceChannel,
                areaId:        $("#areaId").val(),
                statusId:      2
            };
            switch (quoteNoAudit.params.keyType) {
                case "1":
                    reqParams.orderNo = quoteNoAudit.params.page.keyword;
                    break;
                case "2":
                    reqParams.owner = quoteNoAudit.params.page.keyword;
                    break;
                case "3":
                    reqParams.licensePlateNo = quoteNoAudit.params.page.keyword;
                    break;
            }
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderCooperationInfos", reqParams,
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单列表异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        getAreas: function(callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/areaContactInfo/area", {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取区域异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        getOrderInsurancePackage: function(purchaseOrderId, callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/order/" + purchaseOrderId + "/insurancePackage", {},
                function(data) {
                    callback(data.orderInsurancePackage);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单险种套餐异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        getOrderCooperationInfo: function(id, callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderCooperationInfos/" + id, {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单信息异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        }
    }
};

$(function() {
    quoteNoAudit.initialization.init();
    quoteNoAudit.list.listOrders();

    /*
     * 搜索框
     * */
    $("#searchBtn").unbind("click").bind({
        click : function(){
            var keyword = $("#keyword").val();
            if(common.isEmpty(keyword)){
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            quoteNoAudit.params.page.currentPage = 1;
            quoteNoAudit.params.page.keyword = keyword;
            quoteNoAudit.params.keyType = $("#keyType").val();
            quoteNoAudit.params.page.sourceChannel = false;
            quoteNoAudit.list.listOrders();
        }
    });
    //支付宝订单
    $("#alipayBtn").bind({
        click : function(){
            quoteNoAudit.params.page.currentPage = 1;
            quoteNoAudit.params.page.keyword = $("#keyword").val();
            quoteNoAudit.params.keyType = $("#keyType").val();
            quoteNoAudit.params.page.sourceChannel = true;
            quoteNoAudit.list.listOrders();
        }
    });
});
