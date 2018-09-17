var dataFunction = {
    "data": function (data) {
        //var param = new Array;;
        //param[11] = 'updateTime';
        //data.orderColumn = order[0].column;
        //data.orderDir = order[0].dir;

        data.orderStatus = $("#orderStatus").val();
        data.paymentChannel = $("#paymentChannel").val();
        data.area = $("#area").val();
        data.sort = $("#sort").val();
        switch (datatableUtil.params.keyType) {
            case "" :
                break;
            case "1":
                data.owner = datatableUtil.params.keyword;
                break;
            case "2":
                data.licensePlateNo = datatableUtil.params.keyword;
                break;
            case "3":
                data.mobile = datatableUtil.params.keyword;
                break;
            case "4":
                data.orderNo = datatableUtil.params.keyword;
                break;
        }
        //data.sort = param[order[0].column];
        //data = order_list.datatables.formatData(data);
    },
    "fnRowCallback": function (nRow, aData) {
        $orderNo = common.getOrderIconClean(aData.channelIcon) + '<a href="/page/order/order_detail.html?id=' + aData.purchaseOrderId + '" target="_blank">' + aData.orderNo + '</a><br/>' + aData.createTime;
        $comment = "<a href=\"javascript:;\" onclick=\"orderList.list.addComment(" + aData.purchaseOrderId + "," + aData.orderOperationInfoId + ");\">查看备注</a>"
            + (aData.latestComment ? ("<br/>" + common.tools.getCommentMould(aData.latestComment, 5)) : "");
        $owner = aData.auto.owner + '<br/>' + aData.auto.licensePlateNo;
        $area = (aData.area ? aData.area.name : '') + '<br/>' + aData.insuranceCompany.name;
        $price = common.formatMoney(aData.paidAmount, 2) + '<br/>' + common.formatMoney(aData.payableAmount, 2);
        $lastOperation = aData.operatorName + '<br/>' + aData.updateTime;
        $paymentChannel = aData.paymentChannel ? aData.paymentChannel.fullDescription : "";
        $('td:eq(0)', nRow).html($orderNo);
        $('td:eq(2)', nRow).html($comment);
        $('td:eq(3)', nRow).html(orderList.list.fixOneItem(aData));
        $('td:eq(5)', nRow).html($owner);
        $('td:eq(6)', nRow).html($paymentChannel);
        $('td:eq(7)', nRow).html($area);
        $('td:eq(8)', nRow).html($price);
        $('td:eq(10)', nRow).html($lastOperation);
    },
};
var orderList = {
    "url": '/orderCenter/orderOperationInfos',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": null, "title": "订单号", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {
            "data": "currentStatus.description",
            "title": "当前状态",
            'sClass': "text-center",
            "orderable": false,
            "sWidth": "70px"
        },
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "assignerName", "title": "指定人", 'sClass': "text-center", "orderable": false, "sWidth": "70px"},
        {"data": null, "title": "车主车牌", 'sClass': "text-center", "orderable": false, "sWidth": "70px"},
        {
            "data": null,
            "title": "支付方式",
            'sClass': "text-center",
            "orderable": false,
            "sWidth": "70px"
        },
        {
            "data": "insuranceCompany.name",
            "title": "地区</br>保险公司",
            'sClass': "text-center",
            "orderable": false,
            "sWidth": "140px"
        },
        {"data": null, "title": "实付金额</br>原始金额", 'sClass': "text-center", "orderable": false, "sWidth": "70px"},
        {"data": "gift", "title": "礼品", 'sClass': "text-center", "orderable": false, "sWidth": "150px"},
        {"data": null, "title": "最后操作", 'sClass': "text-center", "orderable": false},
    ],
    paymentChannel: {
        toOnlinePay: function (purchaseOrderId) {
            popup.mould.popConfirmMould(false, "确定转为线上支付？", popup.mould.first, "", "",
                function () {
                    popup.mask.hideFirstMask(false);
                    orderList.interface.changeToOnlinePay(purchaseOrderId, function (data) {
                        if (data.result == 'success') {
                            datatables.ajax.reload();
                        } else {
                            popup.mould.popTipsMould(false, "线下转线上支付失败！", popup.mould.first, popup.mould.error, "", "", null);
                        }
                        datatables.ajax.reload();
                    });
                },
                function () {
                    popup.mask.hideFirstMask(false);
                }
            );
        },
        applyForDefund: function (purchaseOrderId) {
            popup.mould.popConfirmMould(false, "确定申请退款？", popup.mould.first, "", "",
                function () {
                    popup.mask.hideFirstMask(false);
                    orderList.interface.changeToRefund(purchaseOrderId, function (data) {
                        if (data.message == '成功') {
                            datatables.ajax.reload();
                        } else {
                            popup.mould.popTipsMould(false, "申请退款失败！", popup.mould.first, popup.mould.error, "", "", null);
                        }
                        datatables.ajax.reload();
                    });
                },
                function () {
                    popup.mask.hideFirstMask(false);
                }
            );
        },
        fanHuaRefund: function (purchaseOrderId) {
            popup.mould.popConfirmMould(false, "确定退款？", popup.mould.first, "", "",
                function () {
                    popup.mask.hideFirstMask(false);
                    orderList.interface.changeRefund(purchaseOrderId, function (data) {
                        if (data.pass) {
                            datatables.ajax.reload();
                        } else {
                            popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "", null);
                        }
                        datatables.ajax.reload();
                    });
                },
                function () {
                    popup.mask.hideFirstMask(false);
                }
            );
        },
        cancelRefund: function (purchaseOrderId) {
            popup.mould.popConfirmMould(false, "确定撤销退款申请？", popup.mould.first, "", "",
                function () {
                    popup.mask.hideFirstMask(false);
                    orderList.interface.changeCancelRefund(purchaseOrderId, function (data) {
                        if (data.message == '成功') {
                            datatables.ajax.reload();
                        } else {
                            popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "", null);
                        }
                        datatables.ajax.reload();
                    });
                },
                function () {
                    popup.mask.hideFirstMask(false);
                }
            );
        },
        toOfflinePay: function (orderOperationInfoId) {
            if (common.isEmpty(parent.$("#number").val())) {
                popup.mould.popTipsMould(false, "流水号不能为空", popup.mould.first, popup.mould.error, "", "", null);
                return;
            }
            popup.mould.popConfirmMould(false, "确定转为线下支付？", popup.mould.first, "", "",
                function () {
                    popup.mask.hideFirstMask(false);
                    orderList.interface.changeToOfflinePay(orderOperationInfoId, function (data) {
                        if (data.message == '成功') {
                            datatables.ajax.reload();
                        } else {
                            popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "", null);
                        }
                        datatables.ajax.reload();
                    });
                },
                function () {
                    popup.mask.hideFirstMask(false);
                }
            );
        },
    },
    status: {
        changeOrderStatus: function (orderOperationInfoId) {
            orderStatus.showEnableStatus(orderOperationInfoId, popup.mould.first, function (data) {
                if (!data.pass) {
                    popup.mould.popTipsMould(false, data.message, popup.mould.second, popup.mould.error, "", "", null);
                } else {
                    datatables.ajax.reload();
                }
                popup.mask.hideAllMask(false);
            });
        },
        resetOrderStatus: function (purchaseOrderId) {
            popup.mould.popConfirmMould(false, "确定更新订单状态让用户可支付？", popup.mould.first, "", "",
                function () {
                    popup.mask.hideFirstMask(false);
                    orderList.interface.resetInsuranceFailureStatus(purchaseOrderId, function (data) {
                        datatables.ajax.reload();
                    });
                },
                function () {
                    popup.mask.hideFirstMask(false);
                }
            );
        }
    },

    interface: {
        getOrderOperationInfoById: function (orderOperationInfoId, callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderOperationInfos/" + orderOperationInfoId, {},
                function (data) {
                    callback(data);
                },
                function () {
                    popup.mould.popTipsMould(false, "获取订单信息异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        resetInsuranceFailureStatus: function (purchaseOrderId, callback) {
            common.ajax.getByAjax(true, 'put', 'json', '/orderCenter/order/' + purchaseOrderId + '/status', {},
                function (data) {
                    callback(data);
                }, function () {
                    popup.mould.popTipsMould(false, "重置订单创建状态异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        changeToOnlinePay: function (purchaseOrderId, callback) {
            common.ajax.getByAjax(true, 'put', 'json', '/orderCenter/order/payment/channel/change', {orderId: purchaseOrderId},
                function (data) {
                    callback(data);
                }, function () {
                    popup.mould.popTipsMould(false, "更新订单支付方式异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        changeToOfflinePay: function (orderOperationInfoId, callback) {
            common.ajax.getByAjax(true, 'put', 'json', '/orderCenter/orderOperationInfos/' + orderOperationInfoId + "/status", {
                    newStatus: orderStatusList['UNCONFIRMED'],
                    clientType: parent.$("input[name=clientType]:checked").val(),
                    number: parent.$("#number").val()
                },
                function (data) {
                    callback(data);
                },
                function () {
                }
            );
        },
        changeToRefund: function (purchaseOrderId, callback) {
            common.ajax.getByAjax(true, 'put', 'json', '/orderCenter/orderOperationInfos/' + purchaseOrderId + "/status", {newStatus: orderStatusList['APPLY_FOR_REFUND']},
                function (data) {
                    callback(data);
                }, function () {
                    popup.mould.popTipsMould(false, "申请退款异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },

        changeRefund: function (purchaseOrderId, callback) {
            common.ajax.getByAjax(true, 'put', 'json', '/orderCenter/orderOperationInfos/' + purchaseOrderId + "/fanHua/refund", null,
                function (data) {
                    callback(data);
                }, function () {
                    popup.mould.popTipsMould(false, "退款异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        changeCancelRefund: function (purchaseOrderId, callback) {
            common.ajax.getByAjax(true, 'put', 'json', '/orderCenter/orderOperationInfos/' + purchaseOrderId + "/status", {newStatus: orderStatusList['UNCONFIRMED']},
                function (data) {
                    callback(data);
                }, function () {
                    popup.mould.popTipsMould(false, "撤销申请异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        getAllOrderStatus: function (callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/orderTransmissionStatus", {},
                function (data) {
                    callback(data);
                },
                function () {
                }
            );
        },
        getAllPaymentChannel: function (callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/paymentChannels", {},
                function (data) {
                    callback(data);
                },
                function () {
                }
            );
        },
        getAllAreas: function (callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/areas", {},
                function (data) {
                    callback(data);
                },
                function () {
                }
            );
        },
    },
    initialization: {
        init: function () {
            this.initParams();
            this.initPage();
        },
        initParams: function () {
        },
        initPage: function () {
            orderList.interface.getAllOrderStatus(function (statusList) {
                $("#orderStatus").append(common.getFormatOptionList(statusList, 'id', 'status'));
            });

            orderList.interface.getAllPaymentChannel(function (channelList) {
                $("#paymentChannel").append(common.getFormatOptionList(channelList, 'id', 'fullDescription'));
            });

            orderList.interface.getAllAreas(function (areaList) {
                $("#area").append(common.getFormatOptionList(areaList, 'id', 'name'));
            });
        }
    },
    list: {
        disableInsuranceCompanyIds: [50000, 55000, 205000],//这些保险公司不允许更改订单状态：50000-众安保险;55000-安盛天平;205000-华安保险
        enableChannels: [42, 43, 101, 102, 75, 76],
        fixOneItem: function (model) {
            var changeStatusText = "";
            var switchToOnlinePay = "";
            var insuranceInputLink = "";
            var additionPaid = "";
            var insuranceFailureLink = "";
            var orderImageStatusLink = "";
            var applyForRefund = "";
            var fanHuaRefund = "";
            var switchToOfflinePay = "";

            var hiddenOperation = false;
            //众安保险不支持操作
            if (model.insuranceCompany && orderList.list.disableInsuranceCompanyIds.indexOf(model.insuranceCompany.id) < 0) {
                hiddenOperation = true;
            }
            //中华联合走api接口地区不可修改状态
            if (model.insuranceCompany.id == 45000 && model.quoteSource && model.quoteSource.id == 4 && !model.innerPay) {
                hiddenOperation = false;
            }
            if (hiddenOperation) {
                if (model.supportAmend && model.supportChangeStatus) {
                    if (model.paymentChannel.id != 50 && model.paymentChannel.id != 52) {
                        insuranceInputLink = '<br/><a target="_blank" href="/page/quote/quote_amend.html?source=order&id=' + model.purchaseOrderId + '">修改订单</a>';
                    }
                } else if (model.currentStatus.id == orderStatusList['ORDER_INPUTED'] && (!model.fanhua)) {
                    insuranceInputLink = '<br/><a target="_blank" href="/page/order/client_insurance_input.html?id=' + model.purchaseOrderId + '">修改保单</a>';
                }
                //已付款 出单完成
                else if (model.currentStatus.id == orderStatusList['PAID_AND_FINISH_ORDER']) {
                    insuranceInputLink = '<br/><a target="_blank" href="/page/order/client_insurance_input.html?id=' + model.purchaseOrderId + '">录入保单</a>';
                }
                // //线下转线上支付
                // if (!model.onlinePay && model.currentStatus.id == orderStatusList['UNPAID']) {
                //     switchToOnlinePay = '<br/><a href="javascript:;" onclick="orderList.paymentChannel.toOnlinePay(' + model.purchaseOrderId + ');">线下转线上支付</a>';
                // }
                if (model.currentStatus.id == orderStatusList['UNPAID'] && model.paymentStatus != null && model.paymentStatus.id == 1 && common.permission.hasPermission("or010101")) {
                    switchToOfflinePay = '<br/><a href="javascript:;" onclick="orderList.list.toOfflinePay(' + model.orderOperationInfoId + ');">转线下支付</a>';
                }
                //未确认
                if ((model.currentStatus.id == orderStatusList['UNCONFIRMED'] || (model.currentStatus.id == orderStatusList['ADDITION_PAID'] && model.paid )) && model.insuranceCompany.id != 65000 && (!model.fanhua) && (!model.agentChannel)) {
                    applyForRefund = '<br/><a href="javascript:;" onclick="orderList.paymentChannel.applyForDefund(' + model.orderOperationInfoId + ');">申请退款</a>';
                } else if (model.currentStatus.id == orderStatusList['REFUND_FAILED']) {
                    applyForRefund = '<br/><a href="javascript:;" onclick="orderList.paymentChannel.applyForDefund(' + model.orderOperationInfoId + ');">重新申请退款</a>';
                    //退款申请中
                } else if (model.currentStatus.id == orderStatusList['APPLY_FOR_REFUND'] && !model.fanhua) {
                    applyForRefund = '<br/><a href="javascript:;" onclick="orderList.paymentChannel.cancelRefund(' + model.orderOperationInfoId + ');">撤销退款申请</a>';
                }

                //alert(model.currentStatus.id  + " " + orderStatusList['REFUNDED']);
                //更改订单状态
                if (orderList.check.hideChangeStatusBtn(model)) {
                    changeStatusText = '<span style="color:#A5A0A0;">更改订单状态</span>';
                } else {
                    changeStatusText = '<a href="javascript:;" onclick="orderList.status.changeOrderStatus(' + model.orderOperationInfoId + ')">更改订单状态</a>';
                }
                //核保失败可支付
                if (model.currentStatus.id == orderStatusList['UNCONFIRMED'] && model.orderStatus && model.orderStatus.id == 7 && model.onlinePay && model.paymentStatus && model.paymentStatus.id != 2 && model.insuranceCompany.id != 65000 && (!model.fanhua)) {
                    insuranceFailureLink = '<br/><a href="javascript:;" onclick="orderList.status.resetOrderStatus(' + model.purchaseOrderId + ')">让用户可支付</a>';
                }
                //订单的照片状态 //#11593开放全部状态backup：&& model.insuranceCompany.id != 65000 && (!model.fanhua)
                if (model.orderStatus != null ) {
                    orderImageStatusLink = '<br/><a href="/page/order/order_detail.html?id=' + model.purchaseOrderId + '" target="_blank">照片' + model.orderImageStatus + '</a>';
                }
                // 泛华订单状态为“出单中”，可进行退款操作 //#10708关闭泛华退款
                // if ( model.fanhua && model.orderStatus.id==3 && model.orderImageStatus == '未上传'){
                //     fanHuaRefund = '<br/><a href="javascript:;" onclick="orderList.paymentChannel.fanHuaRefund(' + model.orderOperationInfoId + ');">泛华退款</a>';
                // }
            } else {
                changeStatusText = '<span style="color:#A5A0A0;">更改订单状态</span>';
            }
            return (
                changeStatusText + insuranceFailureLink + insuranceInputLink + switchToOnlinePay + orderImageStatusLink + additionPaid + applyForRefund + fanHuaRefund + switchToOfflinePay
            );
        },
        init_underline: function () {
            var detailContent = $("#underline_content");
            if (detailContent.length > 0) {
                orderList.list.underline_content = detailContent.html();
                detailContent.remove();
            }
        },
        addComment: function (purchaseOrderId, orderOperationInfoId) {
            orderComment.popCommentList(purchaseOrderId, popup.mould.first, function () {
                orderList.interface.getOrderOperationInfoById(orderOperationInfoId, function (data) {
                    $("#item_" + orderOperationInfoId).empty().append(orderList.list.fixOneItem(data));
                });
            });
        },
        toOfflinePay: function (id) {
            popup.pop.popInput(false, orderList.list.underline_content, popup.mould.first, "500px", "250px", "45%", "54%");
            parent.$("#close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            parent.$("#offlinePayBtn").unbind("click").bind({
                click: function () {
                    orderList.paymentChannel.toOfflinePay(id);
                }
            });
        },
    },
    check: {
        hideChangeStatusBtn: function (model) {
            let hideStatusIds = [orderStatusList['ORDER_INPUTED'], orderStatusList['APPLY_FOR_REFUND'], orderStatusList['REFUND_FAILED'],
                orderStatusList['REFUNDED'], orderStatusList['CANCELED'], orderStatusList['ADDITION_PAID']];
            if (hideStatusIds.indexOf(model.currentStatus.id) >= 0) {
                return true;
            } else if (model.fanhua) {
                return true
            } else if (!model.supportChangeStatus) {
                return true;
            }
            return false;
        }

    }
};
var datatables;
$(function () {
    dt_labels.language.sLengthMenu = "";
    dt_labels.language.sInfo = "";
    datatables = datatableUtil.getByDatatables(orderList, dataFunction.data, dataFunction.fnRowCallback);
    orderStatusList.getAllOrderStatus();//订单状态 后台传
    orderList.initialization.initPage();
    orderList.list.init_underline();
    $("#orderStatus, #paymentChannel, #area, #paymentStatus, #sort").unbind("change").bind({
        change: function () {
            datatables.ajax.reload();
        }
    });
    $("#searchBtn").unbind("click").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            datatableUtil.params.keyword = keyword;
            datatableUtil.params.keyType = $("#keyType").val();
            datatables.ajax.reload();
        }
    });

    $("#area_input").bind({
        keyup: function () {
            if (common.isEmpty($(this).val())) {
                CUI.select.hide();
                $("#area_input").val("");
                return;
            }
            common.getByAjax(true, "get", "json", "/orderCenter/resource/areas/getByKeyWord",
                {
                    keyword: $(this).val()
                },
                function (data) {
                    if (data == null) {
                        return;
                    }
                    var map = new Map();
                    $.each(data, function (i, model) {
                        map.put(model.id, model.name);
                    });
                    CUI.select.show($("#area_input"), 300, map, false, $("#area"));
                }, function () {
                }
            );
        }
    });
});

$(document).ready(function() {
    $('#paymentChannel').select2();
});


