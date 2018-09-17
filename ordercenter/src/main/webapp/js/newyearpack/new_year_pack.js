var dataFunction = {
    "data": function (data) {
        data.newYearPackStatus = $("#newYearPack_statusId").val();
        data.channel = ($("#sourceChannelSel").val());
        data.mobile = $("#keyword").val();//手机号
    },
    "fnRowCallback": function (nRow, aData) {
        $giftOrderNo = common.getOrderIcon(aData.channelIcon) + aData.giftOrderNo + "<br />" + aData.createTime;
        if (aData.orderStatus == '已取消') {
            $orderStatus = "<span style='color:red;'>已取消</span>";
        } else {
            $orderStatus = common.tools.checkToEmpty(aData.orderStatus);
        }
        if (aData.orderStatus == '已使用') {
            // 车险订单
            $insuranceOrderId = "<a href='../../page/order/order_detail.html?id=" + aData.insuranceOrderId + "' target='_blank'>"
                + common.tools.checkToEmpty(aData.insuranceOrderNo) + "</a><br />" + common.tools.checkToEmpty(aData.insuranceCreateTime);
            // 车车管家服务
            if (aData.serviceName == null) {
                $serviceName = "";
            } else {
                $serviceName = aData.serviceName;
                if (aData.serviceStartDate == null) {
                    $serviceName = $serviceName + "<br /><span style='color:red;'>尚未录入起止日期，与起保日期一致</span>";
                } else if (aData.serviceStartDate != null) {
                    $serviceName = $serviceName + "<br /><span style='color: green;'>起:</span>" + common.tools.checkToEmpty(aData.serviceStartDate);
                }
                if (aData.serviceEndDate != null) {
                    $serviceName = $serviceName + "<br /><span style='color: red;'>止:</span>" + aData.serviceEndDate;
                }
            }
            // 加油卡发送状态
            $fuelCardCount = (aData.fuelCardCount == 0 ? "" : "已发放");
            // 加油99折发放情况
            if (aData.fuelDiscountSendCount != null && aData.fuelDiscountSendCount > 0) {
                $fuelDiscountSendCount = "已发放：" + common.tools.checkToEmpty(aData.fuelDiscountSendCount) + "次" + "<br />"
                    + "最后发放：" + common.tools.checkToEmpty(aData.fuelDiscountSendDate);
            } else {
                $fuelDiscountSendCount = "";
            }
        } else {
            $insuranceOrderId = "";
            $serviceName = "";
            $fuelCardCount = "";
            $fuelDiscountSendCount = "";
        }
        // 备注
        $note = "<a href=\"javascript:;\" onclick=\"orderComment.popCommentList('" + aData.giftOrderId + "', 'first');\">查看备注</a>"
        // 操作
        if (aData.orderStatus == "新建" || aData.orderStatus == "已支付") {
            $operation = "<a id='operation_a_" + aData.giftOrderId + "' href=\"javascript:;\" style='color: red;' onclick=\"listNewYearPack.cancel('" + aData.giftOrderId + "');\">取消礼包订单</a>";
        } else {
            $operation = "";
        }
        $('td:eq(0)', nRow).html($giftOrderNo);
        $('td:eq(3)', nRow).html($orderStatus);
        $('td:eq(4)', nRow).html($insuranceOrderId);
        $('td:eq(5)', nRow).html($serviceName);
        $('td:eq(6)', nRow).html($fuelCardCount);
        $('td:eq(7)', nRow).html($fuelDiscountSendCount);
        $('td:eq(8)', nRow).html($note);
        $('td:eq(9)', nRow).html($operation);
    }
};
var listNewYearPack = {
    "url": '/orderCenter/newyearpack',
    "type": "GET",
    "table_id": "gift_order_list_tab",
    "columns": [
        {"data": null, "title": "礼包订单号", 'sClass': "text-center", "orderable": false, "sWidth": "210px"},
        {"data": "mobile", "title": "手机号", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "sourceChannelName", "title": "产品平台", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "状态", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "车险订单", 'sClass': "text-center", "orderable": false, "sWidth": "150px"},
        {"data": null, "title": "管家服务", 'sClass': "text-center", "orderable": false, "sWidth": "250px"},
        {"data": null, "title": "加油卡发放状态", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "加油99折发放情况", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "120px"}
    ],
    initChannels: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/channel/getAllChannels", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.id + "'>" + model.description + "</option>";
                });

                $("#sourceChannelSel").append(options);
            }, function () {
            }
        );
    },
    /**
     * 取消礼包订单
     * @param id
     */
    cancel: function (purchaseOrderId) {
        if (!common.permission.validUserPermission("or080101")) {
            return;
        }
        popup.mould.popConfirmMould(false, "确定取消礼包订单？", popup.mould.first, "", "",
            function () {
                common.ajax.getByAjax(true, "get", "json", "/orderCenter/newyearpack/cancel/" + purchaseOrderId, null,
                    function (data) {
                        popup.mask.hideFirstMask(false);
                        if (data.pass) {
                            popup.mould.popTipsMould(false, "取消礼包订单成功！", popup.mould.first, popup.mould.success, "", "", null);
                            // 修改当前行的订单状态，删除操作项
                            $("#order_status_td_" + purchaseOrderId).css({'color': 'red'});
                            $("#order_status_td_" + purchaseOrderId).html("已取消");
                            $("#operation_a_" + purchaseOrderId).hide();
                        } else {
                            popup.mould.popTipsMould(false, "取消礼包订单失败！", popup.mould.first, popup.mould.error, "", "", null);
                        }
                    },
                    function () {
                        popup.mould.popTipsMould(false, "修改礼包订单状态失败！", popup.mould.first, popup.mould.error, "", "", null);
                    }
                );
            },
            function () {
                popup.mask.hideFirstMask(false);
            }
        );
    },
    /**
     * 第三方加油卡和99这优惠兑换码剩余数量
     */
    codeCount: function () {
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/newyearpack/quantity", null,
            function (data) {
                if (data.pass) {
                    var values = data.message.split(",");
                    $("#fuelCardCount").html(values[0]);
                    var freeCardCounts = values[1].split(";");
                    $("#oneHundred").html(freeCardCounts[0]);
                    $("#fiveHundred").html(freeCardCounts[1]);
                    $("#oneThousand").html(freeCardCounts[2]);
                } else {
                    popup.mould.popTipsMould(false, "获取兑换码剩余数量失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            }, function () {
                popup.mould.popTipsMould(false, "获取兑换码剩余数量失败！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        )
    }
};
var datatables = datatableUtil.getByDatatables(listNewYearPack, dataFunction.data, dataFunction.fnRowCallback);

$(function () {

    listNewYearPack.initChannels();

    listNewYearPack.codeCount();
    /**
     * 搜索框
     */
    $("#searchBtn").unbind("click").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            datatables.ajax.reload();
        }
    });

    /**
     * 更改状态查询列表
     */
    $("#newYearPack_statusId,#sourceChannelSel").unbind("change").bind({
        change: function () {
            datatables.ajax.reload();
        }
    });
});
