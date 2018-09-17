/**
 * Created by wangfei on 2015/12/10.
 */
$(function() {
    orderList.initialization.init();
    orderList.list.listByPage();

    $("#searchBtn").unbind("click").bind({
        click : function(){
            var keyType = $("#keyType").val();
            if (keyType == "5") {
                var orderStartDate = $("#orderStartDate").val();
                var orderEndDate = $("#orderEndDate").val();
                if (!orderStartDate && !orderEndDate) {
                    popup.mould.popTipsMould(false, "请选择查询时间段", popup.mould.first, popup.mould.warning, "", "", null);
                    return false;
                }

                if ((orderStartDate && !orderEndDate) || (!orderStartDate && orderEndDate)) {
                    popup.mould.popTipsMould(false, "请将下单日期填写完整", popup.mould.first, popup.mould.warning, "", "", null);
                    return false;
                }

                if (common.tools.dateTimeCompare(orderStartDate, orderEndDate) < 0) {
                    popup.mould.popTipsMould(false, "结束时间不能早于开始时间", popup.mould.first, popup.mould.warning, "", "", null);
                    return false;
                }
                orderList.params.page.orderStartDate = orderStartDate;
                orderList.params.page.orderEndDate = orderEndDate;
            } else if (keyType == "4") {
                var keyword = $("#keyword").val();
                if(common.isEmpty(keyword)){
                    popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                    return false;
                }
                orderList.params.page.keyword = keyword;
            }
            orderList.params.page.currentPage = 1;
            orderList.params.keyType = keyType;
            orderList.list.listByPage();
        }
    });

    $("#orderStatus, #paymentChannel, #area, #paymentStatus, #sort,#insuranceSel,#channelSel").unbind("change").bind({
        change : function(){
            orderList.params.page.currentPage = 1;
            orderList.list.listByPage();
        }
    });

    $("#keyType").unbind("change").bind({
        change: function() {
            if ($(this).val() == "4") {
                $("#keyword").show();
                $("#dateSelect").hide();
            } else if ($(this).val() == "5") {
                $("#keyword").hide();
                $("#dateSelect").show();
            }
        }
    });
});

var orderList = {
    params: {
        userRoles: "",
        page: new Properties(1, ""),
        keyType: ""
    },
    initialization: {
        init: function() {
            this.initParams();
            this.initPage();
        },
        initParams: function() {
        },
        initPage: function() {
            orderList.interface.getAllOrderStatus(function(statusList) {
                var statusItems = "";
                $.each(statusList, function(index, status) {
                    statusItems += '<option value="' + status.id + '">' + status.status + '</option>';
                });
                $("#orderStatus").append(statusItems);
            });

            orderList.interface.getAllPaymentChannel(function(channelList) {
                var paymentChannelItems = "";
                $.each(channelList, function(index, channel) {
                    paymentChannelItems += '<option value="' + channel.id + '">' + channel.fullDescription + '</option>';
                });
                $("#paymentChannel").append(paymentChannelItems);
            });

            orderList.interface.getAllAreas(function(areaList) {
                var areaItems = "";
                $.each(areaList, function(index, area) {
                    areaItems += '<option value="' + area.id + '">' + area.name + '</option>';
                });
                $("#area").append(areaItems);
            });
            orderList.interface.initCompanies();
            orderList.interface.initChannels();
        }
    },
    list: {
        listByPage: function() {
            orderList.interface.getOrdersByPage(function (data) {
                var $pagination = $(".customer-pagination");
                //var $totalCount = $("#totalCount");
                var $tabBody = $("#list_tab tbody");
                $tabBody.empty();
                if (data.pageInfo.totalElements < 1) {
                    //$totalCount.text("0");
                    $pagination.hide();
                    if (!common.isEmpty(orderList.params.page.keyword) || (!orderList.params.page.orderStartDate && !orderList.params.page.orderEndDate)) {
                        popup.mould.popTipsMould(false, "无符合条件的结果", popup.mould.first, "", "", "", null);
                    }
                    return false;
                }
               // $totalCount.text(data.pageInfo.totalElements);
                if (data.pageInfo.totalPage > 1) {
                    $pagination.show();
                    $.jqPaginator('.pagination',
                        {
                            totalPages: data.pageInfo.totalPage,
                            visiblePages: orderList.params.page.visiblePages,
                            currentPage: orderList.params.page.currentPage,
                            onPageChange: function (pageNum, pageType) {
                                if (pageType == "change") {
                                    orderList.params.page.currentPage = pageNum;
                                    orderList.list.listByPage();
                                }
                            }
                        }
                    );
                } else {
                    $pagination.hide();
                }
                $tabBody.append(orderList.list.fixTabContent(data.viewList));
                common.tools.scrollToTop();
            });
        },
        fixTabContent: function(dataList) {
            var content = "";
            $.each(dataList, function(index, model) {
                content +=
                    "<tr id=\"item_" + model.orderOperationInfoId + "\">" +
                    orderList.list.fixOneItem(model) +
                    "</tr>";
            });
            return content;
        },
        fixOneItem: function(model) {
            var tdPrefix = "<td class=\"text-center\">";
            var tdEnd = "</td>";
            var changeStatusText = "";
            var paymentStatusText = "";
            var switchToOnlinePay = "";
            var insuranceInputLink = "";
            var insuranceFailureLink = "";
            //支付状态
            if (model.paymentStatus) {
                if (model.paymentStatus.id == 2) {
                    paymentStatusText = '<span style="color: green;">已支付</span>';
                } else {
                    paymentStatusText = '<span style="color: red;">未支付</span>';
                }
            } else {
                paymentStatusText = '<span style="color: red;">未支付</span>';
            }
            // 非众安保险显示出单操作信息
            if(model.insuranceCompany != null && model.insuranceCompany.id != 50000) {
                //线下转线上支付
                if (!model.onlinePay && model.currentStatus.id == 1) {
                    switchToOnlinePay = '<br/><a href="javascript:;" onclick="orderList.paymentChannel.toOnlinePay(' + model.purchaseOrderId + ');">线下转线上支付</a>';
                }
                //录入保单
                if (model.currentStatus.id == 14) {
                    insuranceInputLink = '<br/><a target="_blank" href="/page/order/client_insurance_input.html?id=' + model.purchaseOrderId + '">录入保单</a>';
                }
                //更改订单状态
                if (model.currentStatus.id == 17 || model.currentStatus.id == 19) {
                    changeStatusText = '<span style="color:#A5A0A0;">更改订单状态</span>';
                } else {
                    changeStatusText = '<a href="javascript:;" onclick="orderList.status.changeOrderStatus(' + model.orderOperationInfoId + ')">更改订单状态</a>';
                }
                //核保失败可支付
                if (model.currentStatus.id == 1 && model.orderStatus && model.orderStatus.id == 7 && model.onlinePay && model.paymentStatus && model.paymentStatus.id != 2) {
                    insuranceFailureLink = '<br/><a href="javascript:;" onclick="orderList.status.resetOrderStatus(' + model.purchaseOrderId + ')">让用户可支付</a>';
                }
            } else {
                changeStatusText = '<span style="color:#A5A0A0;">更改订单状态</span>';
            }

            return (
            tdPrefix +
            '<a href="/orderCenter/orderOperationInfos/temp/transferStation?purchaseOrderId=' + model.purchaseOrderId + '" target="_blank">' + model.purchaseOrderIdBak + '</a>' +
            tdEnd +
            tdPrefix +
            model.currentStatus.status +
            tdEnd +
            tdPrefix +
            changeStatusText + insuranceFailureLink + insuranceInputLink + switchToOnlinePay +
            tdEnd +
            tdPrefix +
            common.checkToEmpty(model.assignerName) +
            tdEnd +
            tdPrefix +
            paymentStatusText +
            tdEnd +
            tdPrefix +
            model.insuranceCompany.name +
            tdEnd
            );
        },
        addComment: function(purchaseOrderId, orderOperationInfoId) {
            orderComment.popCommentList(purchaseOrderId, popup.mould.first, function() {
                orderList.interface.getOrderOperationInfoById(orderOperationInfoId, function(data) {
                    $("#item_" + orderOperationInfoId).empty().append(orderList.list.fixOneItem(data));
                });
            });
        }
    },
    status: {
        changeOrderStatus: function(orderOperationInfoId) {
            orderStatus.showEnableStatus(orderOperationInfoId, popup.mould.first, function (order) {
                orderList.list.listByPage();
                popup.mask.hideFirstMask(false);
            });
        },
        resetOrderStatus: function(purchaseOrderId) {
            popup.mould.popConfirmMould(false, "确定更新订单状态让用户可支付？", popup.mould.first, "", "",
                function() {
                    popup.mask.hideFirstMask(false);
                    orderList.interface.resetInsuranceFailureStatus(purchaseOrderId, function(data) {
                        orderList.list.listByPage();
                    });
                },
                function() {
                    popup.mask.hideFirstMask(false);
                }
            );
        }
    },
    paymentChannel: {
        toOnlinePay: function(purchaseOrderId) {
            popup.mould.popConfirmMould(false, "确定转为线上支付？", popup.mould.first, "", "",
                function() {
                    popup.mask.hideFirstMask(false);
                    orderList.interface.changeToOnlinePay(purchaseOrderId, function(data) {
                        if (data.result == 'success') {
                            orderList.list.listByPage();
                        } else {
                            popup.mould.popTipsMould(false, "线下转线上支付失败！", popup.mould.first, popup.mould.error, "", "", null);
                        }
                        orderList.list.listByPage();
                    });
                },
                function() {
                    popup.mask.hideFirstMask(false);
                }
            );
        }
    },
    interface: {
        getOrderOperationInfoById: function(orderOperationInfoId, callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderOperationInfos/temp/" + orderOperationInfoId, {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单信息异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        resetInsuranceFailureStatus: function(purchaseOrderId, callback) {
            common.ajax.getByAjax(true, 'put', 'json', '/orderCenter/order/' + purchaseOrderId + '/status', {},
                function (data) {
                    callback(data);
                }, function () {
                    popup.mould.popTipsMould(false, "重置订单创建状态异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        changeToOnlinePay: function(purchaseOrderId, callback) {
            common.ajax.getByAjax(true, 'put', 'json', '/orderCenter/order/payment/channel/change', {orderId: purchaseOrderId},
                function (data) {
                    callback(data);
                }, function () {
                    popup.mould.popTipsMould(false, "更新订单支付方式异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        getAllOrderStatus: function(callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/orderTransmissionStatus", {},
                function(data) {
                    callback(data);
                },
                function() {}
            );
        },
        getAllPaymentChannel: function(callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/paymentChannels", {},
                function(data) {
                    callback(data);
                },
                function() {}
            );
        },
        getAllAreas: function(callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/areas", {},
                function(data) {
                    callback(data);
                },
                function() {}
            );
        },
        getOrdersByPage: function(callback) {
            var reqParams = {
                currentPage :     orderList.params.page.currentPage,
                pageSize :        orderList.params.page.pageSize,
                orderStatus:      $("#orderStatus").val(),
                //paymentStatus:    $("#paymentStatus").val(),
                //paymentChannel:   $("#paymentChannel").val(),
                //area:             $("#area").val(),
                //sort:             $("#sort").val()
                insuranceCompany: $("#insuranceSel").val(),
                sourceChannel: $("#channelSel").val()
            };
            switch (orderList.params.keyType) {
                case "1":
                    reqParams.owner = orderList.params.page.keyword;
                    break;
                case "2":
                    reqParams.licensePlateNo = orderList.params.page.keyword;
                    break;
                case "3":
                    reqParams.mobile = orderList.params.page.keyword;
                    break;
                case "4":
                    reqParams.orderNo = orderList.params.page.keyword;
                    break;
                case "5":
                    reqParams.orderStartDate = orderList.params.page.orderStartDate;
                    reqParams.orderEndDate = orderList.params.page.orderEndDate;
                    break;
            }
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderOperationInfos/temp", reqParams,
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单列表异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        initCompanies: function () {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/insuranceCompany/getEnableCompanies", null,
                function (data) {
                    if (data == null) {
                        return false;
                    }
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.name + "</option>";
                    });
                    $("#insuranceSel").append(options);
                }, function () {
                }
            );
        },initChannels: function () {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/channel/getAllChannels", null,
                function (data) {
                    if (data == null) {
                        return false;
                    }

                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.description + "</option>";
                    });

                    $("#channelSel").append(options);
                }, function () {
                }
            );
        }
    }
};
