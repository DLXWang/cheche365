/**
 * Created by Luly on 2017/4/21.
 */
$(function () {
    /* init */
    stopRestartSelect.initVipChannels();
    stopRestartSelect.initQuoteAreas();
    stopRestartSelect.initCPSChannel();

    /* 查询 */
    $("#selectBtn").bind({
        click: function () {
            datatableUtil.params.keyWord = $("#keyWord").val();
            datatableUtil.params.keyType = $("#keyType").val();
            stopRestartSelect.select();
            //支付情况统计
            if (common.isEmpty($("#exportStopRestart").text())) {
                $(".dataTables_length").append('&nbsp;&nbsp;&nbsp;<a id="exportStopRestart" class="btn btn-warning submit" onclick="stopRestartSelect.exportStopRestart()">导出停复驶订单</a>');
            }
        }
    });

    /* 按渠道查找 */
    $("#allChannelSel").bind({
        change: function () {
            stopRestartSelect.changeChannel($(this));
        }
    })
});

var dataFunction = {
    "data": function (data) {
        data.orderStartDate = $("#orderStartDate").val();
        data.orderEndDate = $("#orderEndDate").val();
        data.operateStartDate = $("#operateStartDate").val();
        data.operateEndDate = $("#operateEndDate").val();

        data.stopBeginDate = $("#stopBeginDate").val();
        data.stopEndDate = $("#stopEndDate").val();
        data.restartBeginDate = $("#restartBeginDate").val();
        data.restartEndDate = $("#restartEndDate").val();
        switch (datatableUtil.params.keyType) {
            case "" :
                break;
            case "1":
                data.receiveUser = datatableUtil.params.keyWord;
                break;
            case "2":
                data.insurance = datatableUtil.params.keyWord;
                break;
            case "3":
                data.owner = datatableUtil.params.keyWord;
                break;
            case "4":
                data.insuranced = datatableUtil.params.keyWord;
                break;
        }
        data.mobile = $.trim($("#mobile").val());
        data.licenseNo = $.trim($("#licenseNo").val());
        data.vipCompany = common.checkToEmpty($("#vipCompanySel").val());
        data.quoteArea = common.checkToEmpty($("#quoteAreaSel").val());
        data.cpsChannel = common.checkToEmpty($("#cpsChannelSel").val());
        data.orderNo = common.checkToEmpty($("#orderNo").val());
    },
    "fnRowCallback": function (nRow, aData) {
        $(".check-box-all").prop("checked", false);
        $orderNo = common.getOrderIcon(aData.channelIcon) + "<a href='../../page/order/order_detail.html?id=" + aData.id + "' target='_blank'>" + aData.orderNo + "</a><br>" + aData.createTime;
        $('td:eq(1)', nRow).html($orderNo);
    },
}
var stopRestartSelect = {
    param: {
        dataTables: null,
        dataList: null
    },
    page: new Properties(1, ""),

    initVipChannels: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/vipCompany/getAllVipCompanies", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.id + "'>" + model.name + "</option>";
                });

                $("#vipCompanySel").append(options);
            }, function () {
            }
        );
    },

    /* 获取报价区域列表 */
    initQuoteAreas: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/areas", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.id + "'>" + model.name + "</option>";
                });

                $("#quoteAreaSel").append(options);
            }, function () {
            }
        );
    },
    /* 获取CPS渠道列表 */
    initCPSChannel: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/cps", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.id + "'>" + model.name + "</option>";
                });

                $("#cpsChannelSel").append(options);
            }, function () {
            }
        );
    },


    check: function () {
        var flag = true, msg = "";
        var stopBeginDate = $("#stopBeginDate").val();
        var stopEndDate = $("#stopEndDate").val();
        var restartBeginDate = $("#restartBeginDate").val();
        var restartEndDate = $("#restartEndDate").val();

        var orderStartDate = $("#orderStartDate").val();
        var orderEndDate = $("#orderEndDate").val();
        var operateStartDate = $("#operateStartDate").val();
        var operateEndDate = $("#operateEndDate").val();

        if (!((stopBeginDate && stopEndDate) || (restartBeginDate && restartEndDate))) {
            flag = false;
            msg = "停复驶日期区间至少完整填选一个";
        }
        if (common.tools.dateTimeCompare(stopBeginDate,stopEndDate) < 0) {
            flag = false;
            msg = "停驶日期结束时间不能早于开始时间";
        }
        if (common.tools.dateTimeCompare(restartBeginDate,restartEndDate) < 0) {
            flag = false;
            msg = "复驶日期结束时间不能早于开始时间";
        }

        if ((orderStartDate && !orderEndDate) || (!orderStartDate && orderEndDate)) {
            flag = false;
            msg = "请将下单日期填写完整";
        }
        if (common.tools.dateTimeCompare(orderStartDate, orderEndDate) < 0) {
            flag = false;
            msg = "下单日期结束时间不能早于开始时间";
        }
        if ((operateStartDate && !operateEndDate) || (!operateStartDate && operateEndDate)) {
            flag = false;
            msg = "请将操作日期填写完整";
        }
        if (common.tools.dateTimeCompare(operateStartDate, operateEndDate) < 0) {
            flag = false;
            msg = "操作日期结束时间不能早于开始时间";
        }
        return {flag: flag, msg: msg}
    },
    select: function () {
        dt_labels.selected.splice(0, dt_labels.selected.length);
        var checkJson = this.check();
        if (!checkJson.flag) {
            common.showTips(checkJson.msg);
            return;
        }

        if (stopRestartSelect.param.dataTables != null) {
            stopRestartSelect.param.dataTables.ajax.reload();
            return false;
        }

        stopRestartSelect.param.dataList = {
            "url": '/orderCenter/order/stopRestartFilter',
            "type": "get",
            "table_id": "result_tab",
            "columns": [
                {
                    data: "id",
                    "title": '<input type="checkbox" class="data-checkbox check-box-all">',
                    render: function (data, type, row) {
                        if (type === 'display') {
                            return '<input type="checkbox" value="' + data + '" class="data-checkbox check-box-single">';
                        }
                        return data;
                    },
                    className: "text-center checkbox-width",
                    "orderable": false, "sWidth": "15px"
                },
                {"data": null, "title": "订单编号", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
                {"data": "owner", "title": " 车主", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
                {"data": "licenseNo", "title": "车牌号", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": "quoteArea", "title": "城市", 'sClass': "text-center", "orderable": false, "sWidth": "70px"},
                {"data": "channel", "title": "产品平台", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
                {"data": "paidAmount", "title": "实付金额", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": "premium", "title": "商业险", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
                {"data": "stopNum", "title": "总停驶(次)", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
                {"data": "totalRefundAmount", "title": "共退还(元)", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": "status", "title": "当前状态", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": "lastStopBeginDate", "title": "最近停驶日", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": "lastStopDays", "title": "最近停驶(天)", 'sClass': "text-center", "orderable": false, "sWidth": "110px"},
                {"data": "lastRestartBeginDate", "title": "最近复驶日", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": "orderSource", "title": "来源", 'sClass': "text-center", "orderable": false, "sWidth": "70px"}
            ]
        };

        stopRestartSelect.param.dataTables = datatableUtil.getByDatatables(stopRestartSelect.param.dataList, dataFunction.data, dataFunction.fnRowCallback);
    },

    /*导出停复驶订单*/
    exportStopRestart: function () {
        var checkedIds = dt_labels.selected.join(",");
        if (checkedIds.length < 1) {
            msg = "请选择需要导出的订单！";
            common.showTips(msg);
            $("#exportStopRestart").attr("href", "#");
            return;
        }
        var url = "/orderCenter/order/exportStopRestart?checkedIds=" + checkedIds;
        $("#exportStopRestart").attr("href", url);
    },

    changeChannel: function (obj) {
        var channel = obj.val();
        if (channel == "1") {//CPS
            $("#cpsChannelDiv").show().siblings(".channel").hide();
            $("#cpsChannelDiv").find("select").val("0");
            $("#cpsChannelDiv").siblings(".channel").find("select").val("");
            $("#cpsChannelSel option[value='']").remove();
        } else if (channel == "2") {//大客户
            $("#vipChannelDiv").show().siblings(".channel").hide();
            $("#vipChannelDiv").find("select").val("0");
            $("#vipChannelDiv").siblings(".channel").find("select").val("");
            $("#vipCompanySel option[value='']").remove();
        } else {//所有
            $(".channel").hide();
            $(".channel").find("select").val("");
        }
    },
}
