var orderConsole = {
    params: {
        page: new Properties(1, ""),
        changeStateContent: "",
        keyType: ""
    },
    initialization: {
        init: function() {
            this.initParams();
            this.initPage();
        },
        initParams: function() {
            var params = orderConsole.params;
            var $exceptionReason = $("#exceptionReason");
            orderConsole.interface.getExceptionReasons(function(reasons) {
                var reasonOptions = "";
                $.each(reasons, function(i,reason){
                    reasonOptions += "<option value='"+ reason.index +"'>" + reason.content + "</option>";
                });
                $exceptionReason.append(reasonOptions);
                var $changeStateContent = $("#state_change_content");
                if ($changeStateContent.length > 0) {
                    params.changeStateContent = $changeStateContent.html();
                    $changeStateContent.remove();
                }
            });
        },
        initPage: function() {
            orderConsole.interface.getAreas(function(data) {
                var $area = $("#areaId");
                var options = "";
                $.each(data, function(index, area) {
                    options += "<option value=\"" + area.id + "\">" + area.name + "</option>";
                });
                $area.append(options);
                $area.unbind("change").bind({
                    change: function() {
                        orderConsole.params.page.currentPage = 1;
                        orderConsole.params.page.keyword = $("#keyword").val();
                        orderConsole.params.keyType = $("#keyType").val();
                        orderConsole.params.page.sourceChannel = false;
                        orderConsole.list.listOrders();
                    }
                })
            });
        }
    },
    list: {
        listOrders: function() {
            if (!common.permission.validUserPermission("or070101")) {
                return;
            }
            orderConsole.interface.getOrdersByPage(function(data) {
                var $pagination = $(".customer-pagination");
                var $totalCount = $("#totalCount");
                var $tabBody = $("#list_tab tbody");
                $tabBody.empty();
                if (data.pageInfo.totalElements < 1) {
                    $totalCount.text("0");
                    $pagination.hide();
                    if (!common.isEmpty(orderConsole.params.page.keyword)) {
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
                            visiblePages: orderConsole.params.page.visiblePages,
                            currentPage: orderConsole.params.page.currentPage,
                            onPageChange: function (pageNum, pageType) {
                                if (pageType == "change") {
                                    orderConsole.params.page.currentPage = pageNum;
                                    orderConsole.list.listOrders();
                                }
                            }
                        }
                    );
                } else {
                    $pagination.hide();
                }
                $tabBody.append(orderConsole.list.fixTabContent(data.viewList));
                common.scrollToTop();
            });
        },
        fixTabContent: function(dataList) {
            var content = "";
            $.each(dataList, function(index, model) {
                content +=
                    "<tr id=\"item_" + model.id + "\">" +
                    orderConsole.list.fixOneItem(model) +
                    "</tr>";
            });
            return content;
        },
        fixOneItem: function(model) {
            var tdPrefix = "<td class=\"text-center\">";
            var tdEnd = "</td>";
            var paymentSpanColor;
            var operationContent = "";
            switch (model.paymentStatus) {
                case "未支付":
                    paymentSpanColor = "color: #ffcc00;";
                    break;
                case "已支付":
                    paymentSpanColor = "color: green;";
                    break;
                case "放弃支付":
                    paymentSpanColor = "color: #ff0000;";
                    break;
            }
            if ((model.auditStatus && model.cooperationStatus && model.cooperationStatus.id == 6) || (model.cooperationStatus && model.cooperationStatus.id == 7)
                || model.paymentStatus == "未支付" || model.paymentStatus == "放弃支付") {
                operationContent = "<span style=\"color:#A5A0A0;\">更改订单状态</span>";
            } else {
                operationContent = "<a href=\"javascript:;\" onclick=\"orderConsole.update.updateStatus(" + model.id + ");\">更改订单状态</a>";
            }
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
                "<span id=\"item_status_" + model.id + "\">" + (model.cooperationStatus ? model.cooperationStatus.status : "") + "</span>" +
                tdEnd +
                tdPrefix +
                "<span style=\"" + paymentSpanColor + "\">" + model.paymentStatus + "</span>" +
                tdEnd +
                tdPrefix +
                "<span id=\"item_operator_" + model.id + "\">" + common.tools.checkToEmpty(model.operatorName) + "</span><br/>" +
                "<span id=\"item_updateTime_" + model.id + "\">" + common.tools.checkToEmpty(model.updateTime) + "</span>" +
                tdEnd +
                tdPrefix +
                "<a href=\"javascript:;\" onclick=\"orderComment.popCommentList(" + model.purchaseOrderId + ", 'first');\">查看备注</a>" +
                tdEnd +
                tdPrefix +
                operationContent +
                tdEnd;
            return itemTd;
        }
    },
    update: {
        updateStatus: function(id) {
            if (!common.permission.validUserPermission("or07010101")) {
                return;
            }
            orderConsole.interface.getOrderStatus(id, function(data) {
                popup.pop.popInput(false, orderConsole.params.changeStateContent, popup.mould.first, "446px", "auto", "50%", "54%");
                var $popInput = window.parent.$("#popover_normal_input");
                var $orderStatus = $popInput.find("#orderStatus");
                var $exceptionReasonDiv = $popInput.find("#exceptionReasonDiv");
                var $refundDiv = $popInput.find("#refundDiv");
                if (data.switchStatus) {
                    var options = "";
                    $.each(data.switchStatus, function(index, status) {
                        options += "<option value=\"" + status.id + "\"" + (index==0?"selected":"") + ">" + status.status + "</option>";
                    });
                    $orderStatus.empty().append(options);
                }
                $orderStatus.unbind("change").bind({
                    change: function () {
                        orderConsole.update.adjustStateView($(this).val());
                    }
                });
                orderConsole.update.adjustStateView($orderStatus.val());
                $popInput.find(".theme_poptit .close").unbind("click").bind({
                    click: function () {
                        popup.mask.hideFirstMask(false);
                    }
                });
                $popInput.find(".changeOrderStatus").unbind("click").bind({
                    click: function() {
                        var newStatus = $orderStatus.val();
                        var reasonId = null;
                        var refundObject = "";
                        if (newStatus == "7") {
                            reasonId = $exceptionReasonDiv.find("#exceptionReason").val();
                        } else if (newStatus == "8") {
                            $refundDiv.find("[name='refundChk']:checked").each(function() {
                                refundObject += $(this).val()+ ",";
                            });
                            refundObject = refundObject.substring(0, refundObject.length-1);
                        }
                        orderConsole.interface.changeStatus(id, newStatus, reasonId, refundObject, function(order) {
                            orderConsole.update.refreshItem(order);
                            popup.mask.hideFirstMask(false);
                        });
                    }
                });
            });
        },
        adjustStateView: function(status) {
            var $popInput = window.parent.$("#popover_normal_input");
            var $exceptionReasonDiv = $popInput.find("#exceptionReasonDiv");
            var $refundDiv = $popInput.find("#refundDiv");
            if (status == "7") {
                $exceptionReasonDiv.show();
            } else {
                $exceptionReasonDiv.hide();
            }
            if (status == "8") {
                $refundDiv.show();
            } else {
                $refundDiv.hide();
            }
        },
        refreshItem: function(order) {
            var $item = $("#item_" + order.id);
            var itemTd = orderConsole.list.fixOneItem(order);
            $item.empty();
            $item.append(itemTd);
        }
    },
    interface: {
        getOrdersByPage: function(callback) {
            var reqParams = {
                currentPage :  orderConsole.params.page.currentPage,
                pageSize :     orderConsole.params.page.pageSize,
                areaId:        $("#areaId").val(),
                sourceChannel: orderConsole.params.page.sourceChannel
            };
            switch (orderConsole.params.keyType) {
                case "1":
                    reqParams.orderNo = orderConsole.params.page.keyword;
                    break;
                case "2":
                    reqParams.owner = orderConsole.params.page.keyword;
                    break;
                case "3":
                    reqParams.licensePlateNo = orderConsole.params.page.keyword;
                    break;
            }
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderCooperationInfos/console", reqParams,
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单列表异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        getAllStatus: function(callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderCooperationInfos/status", {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单状态异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        changeStatus: function(id, newStatus, reasonId, refundObject, callback) {
            common.ajax.getByAjax(true, "put", "json", "/orderCenter/orderCooperationInfos/" + id + "/status",
                {
                    newStatus: newStatus,
                    reasonId:  reasonId,
                    refundObject: refundObject
                },
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "状态更新异常！", popup.mould.second, popup.mould.error, "", "", null);
                }
            );
        },
        getOrderStatus: function(id, callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderCooperationInfos/" + id + "/status", {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单状态异常！", popup.mould.first, popup.mould.error, "", "", null);
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
        getExceptionReasons: function(callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/abnormity/reasons", null,
                function(data){
                    callback(data);
                },
                function(){
                    popup.mould.popTipsMould(false, "获取异常原因异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        }
    }
};

$(function(){
    orderConsole.initialization.init();
    orderConsole.list.listOrders();

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
            orderConsole.params.page.currentPage = 1;
            orderConsole.params.page.keyword = keyword;
            orderConsole.params.keyType = $("#keyType").val();
            orderConsole.params.page.sourceChannel = false;
            orderConsole.list.listOrders();
        }
    });
    //支付宝订单
    $("#alipayBtn").bind({
        click : function(){
            orderConsole.params.page.sourceChannel = true;
            orderConsole.params.page.currentPage = 1;
            orderConsole.params.page.keyword = $("#keyword").val();
            orderConsole.params.keyType = $("#keyType").val();
            orderConsole.list.listOrders();
        }
    });
});
