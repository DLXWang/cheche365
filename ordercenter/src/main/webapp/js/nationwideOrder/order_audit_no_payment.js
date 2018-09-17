/**
 * Created by wangfei on 2015/11/19.
 */
var auditNoPayment = {
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
            var params = auditNoPayment.params;
        },
        initPage: function() {
            auditNoPayment.interface.getAreas(function(data) {
                var $area = $("#areaId");
                var options = "";
                $.each(data, function(index, area) {
                    options += "<option value=\"" + area.id + "\">" + area.name + "</option>";
                });
                $area.append(options);
                $area.unbind("change").bind({
                    change: function() {
                        auditNoPayment.params.page.currentPage = 1;
                        auditNoPayment.params.page.keyword = $("#keyword").val();
                        auditNoPayment.params.keyType = $("#keyType").val();
                        auditNoPayment.params.sourceChannel = false;
                        auditNoPayment.list.listOrders();
                    }
                })
            });
        }
    },
    list: {
        listOrders: function() {
            auditNoPayment.interface.getOrdersByPage(function(data) {
                var $pagination = $(".customer-pagination");
                var $totalCount = $("#totalCount");
                var $tabBody = $("#list_tab tbody");
                $tabBody.empty();
                if (data.pageInfo.totalElements < 1) {
                    $totalCount.text("0");
                    $pagination.hide();
                    if (!common.isEmpty(auditNoPayment.params.page.keyword)) {
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
                            visiblePages: auditNoPayment.params.page.visiblePages,
                            currentPage: auditNoPayment.params.page.currentPage,
                            onPageChange: function (pageNum, pageType) {
                                if (pageType == "change") {
                                    auditNoPayment.params.page.currentPage = pageNum;
                                    auditNoPayment.list.listOrders();
                                }
                            }
                        }
                    );
                } else {
                    $pagination.hide();
                }
                $tabBody.append(auditNoPayment.list.fixTabContent(data.viewList));
                common.scrollToTop();
            });
        },
        fixTabContent: function(dataList) {
            var content = "";
            $.each(dataList, function(index, model) {
                content +=
                    "<tr id=\"item_" + model.id + "\">" +
                    auditNoPayment.list.fixOneItem(model) +
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
                model.payableAmount + "元" +
                tdEnd +
                tdPrefix +
                model.paidAmount + "元" +
                tdEnd +
                tdPrefix +
                (model.institution ? model.institution.name : "") +
                tdEnd +
                tdPrefix +
                (model.quoteRecord ? (model.quoteRecord.premium + "元") : "0.00元") +
                tdEnd +
                tdPrefix +
                "<span style=\"color: " + (model.incomeStatus ? "green" : "red") + "\">收益：" + (model.incomeStatus ? "正常" : "异常")  + "</span><br/>" +
                "<span style=\"color: " + (model.matchStatus ? "green" : "red") + "\">险种保额：" + (model.matchStatus ? "匹配" : "不匹配")  + "</span><br/>" +
                "<a href=\"javascript:;\" onclick=\"warningDetail.popDetail(" + model.id + "," + model.purchaseOrderId + ",'first',false,null,null" + ");\">查看详情</a>" +
                tdEnd +
                tdPrefix +
                "<span id=\"item_operator_" + model.id + "\">" + common.tools.checkToEmpty(model.operatorName) + "</span><br/>" +
                "<span id=\"item_updateTime_" + model.id + "\">" + common.tools.checkToEmpty(model.updateTime) + "</span>" +
                tdEnd +
                tdPrefix +
                    "<a href=\"javascript:;\" onclick=\"orderComment.popCommentList(" + model.purchaseOrderId + ", 'first');\">查看备注</a>" +
                tdEnd +
                tdPrefix +
                "<a href=\"javascript:;\" onclick=\"auditNoPayment.update.updateStatus(" + model.id + ",4);\" style=\"margin-left: 10px;\">已结款</a>" +
                tdEnd;
            return itemTd;
        }
    },
    update: {
        updateStatus: function(id, newStatus) {
            if (!common.permission.validUserPermission("or07010701")) {
                return;
            }
            auditNoPayment.interface.setOrderStatus(id, newStatus, function() {
                auditNoPayment.list.listOrders();
            });
        }
    },
    interface: {
        getOrdersByPage: function(callback) {
            var reqParams = {
                currentPage :  auditNoPayment.params.page.currentPage,
                pageSize :     auditNoPayment.params.page.pageSize,
                sourceChannel :auditNoPayment.params.sourceChannel,
                areaId:        $("#areaId").val(),
                statusId:      3
            };
            switch (auditNoPayment.params.keyType) {
                case "1":
                    reqParams.orderNo = auditNoPayment.params.page.keyword;
                    break;
                case "2":
                    reqParams.owner = auditNoPayment.params.page.keyword;
                    break;
                case "3":
                    reqParams.licensePlateNo = auditNoPayment.params.page.keyword;
                    break;
                case "4":
                    reqParams.institutionName = auditNoPayment.params.page.keyword;
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
        setOrderStatus: function(id, status, callback) {
            common.ajax.getByAjax(true, "put", "json", "/orderCenter/orderCooperationInfos/" + id + "/status", {newStatus: status},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "更新审核状态异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        }
    }
};

$(function() {
    auditNoPayment.initialization.init();
    auditNoPayment.list.listOrders();

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
            auditNoPayment.params.page.currentPage = 1;
            auditNoPayment.params.page.keyword = keyword;
            auditNoPayment.params.keyType = $("#keyType").val();
            auditNoPayment.params.sourceChannel = false;
            auditNoPayment.list.listOrders();
        }
    });
    //支付宝订单
    $("#alipayBtn").bind({
        click : function(){
            auditNoPayment.params.page.currentPage = 1;
            auditNoPayment.params.page.keyword = $("#keyword").val();
            auditNoPayment.params.keyType = $("#keyType").val();
            auditNoPayment.params.sourceChannel = true;
            auditNoPayment.list.listOrders();
        }
    });
});
