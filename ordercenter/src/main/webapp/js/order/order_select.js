/**
 * Created by wangfei on 2015/5/13.
 */
$(function () {
    /* init */
    orderSelect.initBtn();
    orderSelect.initCompanies();
    orderSelect.initCustomers();
    orderSelect.initOrderTransmissionStatus();
    orderSelect.initOrderStatus();
    orderSelect.initVipChannels();
    orderSelect.initChannels();
    orderSelect.initCPSChannel();
    orderSelect.initPaymentChannels();
    orderSelect.initOrderSourceTypes();
    orderSelect.initmonitorUrls();

    /* 查询 */
    $("#selectBtn").bind({
        click: function () {
            datatableUtil.params.keyWord = $("#keyWord").val();
            datatableUtil.params.keyType = $("#keyType").val();
            orderSelect.select();
            //支付情况统计
            //if (common.isEmpty($("#paidInfo").text())) {
            //    $(".dataTables_length").append('<span id="paidInfo">&nbsp;&nbsp;&nbsp;已支付：<span id="paidOrderCount" class="detail-all"></span>单，支付过：<span id="alreadyPaidOrderCount" class="detail-all"></span>单</span>');
            //    $(".dataTables_length").append('&nbsp;&nbsp;&nbsp;<a id="exportDelivery" class="btn btn-warning submit" onclick="orderSelect.exportDelivery()">导出配送信息</a>');
            //}
            orderSelect.getCount("paid_order", $("#paidOrderCount"));
            orderSelect.getCount("already_paid_order", $("#alreadyPaidOrderCount"));
        }
    });

    /* excel */
    $("#downloadBtn").unbind("click").bind({
        click:function(){

            orderSelect.exportDelivery();
        }
    });
    /* 按渠道查找 */
    $("#allChannelSel").bind({
        change: function () {
            orderSelect.changeChannel($(this));
        }
    });
    $("#quoteAreaSel").bind({
        keyup: function () {
            if (common.isEmpty($(this).val())) {
                CUI.select.hide();
                $("#quoteArea_sel").val("");
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
                    CUI.select.show($("#quoteAreaSel"), 300, map, false, $("#quoteArea_sel"));
                }, function () {
                }
            );
        }
    });

    $("#agent").bind({
        keyup: function () {
            if (common.isEmpty($(this).val())) {
                CUI.select.hide();
                $("#agentSel").val("");
                $("#agent").val("");
                return;
            }
            common.getByAjax(true, "get", "json", "/orderCenter/resource/agent/getEnableAgentsByKeyword",
                {
                    keyword: $(this).val()
                },
                function (data) {
                    if (data == null) {
                        return;
                    }
                    var map = new Map();
                    $.each(data, function (i, model) {
                        map.put(model.id, model.agentName);
                    });
                    CUI.select.show($("#agent"), 300, map, true, $("#agentSel"));
                }, function () {
                }
            );
        }
    })
});

var dataFunction = {
    "data": function (data) {
        data.insuranceCompany = common.checkToEmpty($("#insuranceSel").val());
        data.assigner = common.checkToEmpty($("#assignerSel").val());
        data.status = common.checkToEmpty($("#statusSel").val());
        data.orderStatus = common.checkToEmpty($("#orderStatusSel").val());
        data.orderStartDate = $("#orderStartDate").val();
        data.orderEndDate = $("#orderEndDate").val();
        data.operateStartDate = $("#operateStartDate").val();
        data.operateEndDate = $("#operateEndDate").val();
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
        data.agent = common.checkToEmpty($("#agentSel").val());
        data.vipCompany = common.checkToEmpty($("#vipCompanySel").val());
        data.channel = common.checkToEmpty($("#channelSel").val());
        data.quoteArea = common.checkToEmpty($("#quoteArea_sel").val());
        data.cpsChannel = common.checkToEmpty($("#cpsChannelSel").val());
        data.perfectDriverChannel = common.checkToEmpty($("#perfectDriverSel").val());
        data.orderNo = common.checkToEmpty($("#orderNo").val());
        data.paymentChannel = common.checkToEmpty($("#paymentChannelSel").val());
        data.orderSourceType = common.checkToEmpty($("#orderSourceTypeSel").val());
        data.orderSourceId = common.checkToEmpty($("#monitorUrlSel").val());
        data.inviter = $.trim($("#inviter").val());
        data.indirectionInviter = $.trim($("#indirectionInviter").val())
    },
    "fnRowCallback": function (nRow, aData) {
        $(".check-box-all").prop("checked", false);
        $orderNo = common.getOrderIcon(aData.channelIcon) + "<a href='order_detail.html?id=" + aData.purchaseOrderId + "' target='_blank'>" + aData.orderNo + "</a><br>" + aData.createTime;
        $comment = "<a href=\"javascript:;\" onclick=\"orderComment.popCommentList(" + aData.purchaseOrderId + ", 'first');\">查看备注</a>";
        $lastOperation = aData.operatorName + '<br/>' + aData.updateTime;
        $('td:eq(1)', nRow).html($orderNo);
        $('td:eq(3)', nRow).html($comment);
        $('td:eq(9)', nRow).html(common.checkToEmpty(aData.assignerName));
        $('td:eq(10)', nRow).html($lastOperation);
    },
}
var orderSelect = {
    param: {
        dataTables: null,
        dataList: null
    },
    page: new Properties(1, ""),
    /* 获取保险公司列表 */
    initBtn: function(){
        if(common.permission.hasPermission("or010301")){
            $("#downloadBtn").show();
        }
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
    },
    /* 获取客服列表 */
    initCustomers: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/internalUser/getAllEnableCustomers", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.id + "'>" + model.name + "</option>";
                });
                $("#assignerSel").append(options);
            }, function () {
            }
        );
    },
    /* 获取出单状态列表 */
    initOrderTransmissionStatus: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/orderTransmissionStatus", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.id + "'>" + model.status + "</option>";
                });

                $("#statusSel").append(options);
            }, function () {
            }
        );
    },
    /* 获取订单状态列表 */
    initOrderStatus: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/orderStatus", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.id + "'>" + model.status + "</option>";
                });

                $("#orderStatusSel").append(options);
            }, function () {
            }
        );
    },
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
    /* 获取来源列表 */
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

                $("#channelSel").append(options);
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
    /* 支付方式列表 */
    initPaymentChannels: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/paymentChannels", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.id + "'>" + model.fullDescription + "</option>";
                });

                $("#paymentChannelSel").append(options);
            }, function () {
            }
        );
    },
    /* 出单机构列表 */
    initOrderSourceTypes: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/orderSourceTypes", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    if (model) {
                        options += "<option value='" + model.id + "'>" + model.name + "</option>";
                    }
                });

                $("#orderSourceTypeSel").append(options);
            }, function () {
            }
        );
    },
    /* 出单机构列表 */
    initmonitorUrls: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/quote/getMonitorUrls", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    if (model) {
                        options += "<option value='" + model.businessActivity + "' title='" + model.scope+"_"+model.source+"_"+model.plan+"_"+model.unit+"_"+model.keyword + "'>" + model.source + "</option>";
                    }
                });

                $("#monitorUrlSel").append(options);
            }, function () {
            }
        );
    },
    generateReqJson: function () {

        var receiveUser = "null";
        var insurance = "null";
        var owner = "null";
        var insuranced = "null";

        switch (datatableUtil.params.keyType) {
            case "" :
                break;
            case "1":
                receiveUser = datatableUtil.params.keyWord;
                break;
            case "2":
                insurance = datatableUtil.params.keyWord;
                break;
            case "3":
                owner = datatableUtil.params.keyWord;
                break;
            case "4":
                insuranced = datatableUtil.params.keyWord;
                break;
        }

        return {
            insuranceCompany: common.checkToEmpty($("#insuranceSel").val()),
            assigner: common.checkToEmpty($("#assignerSel").val()),
            status: common.checkToEmpty($("#statusSel").val()),
            orderStatus: common.checkToEmpty($("#orderStatusSel").val()),
            orderStartDate: $("#orderStartDate").val(),
            orderEndDate: $("#orderEndDate").val(),
            operateStartDate: $("#operateStartDate").val(),
            operateEndDate: $("#operateEndDate").val(),
            receiveUser: common.checkToEmpty(receiveUser),
            insurance: common.checkToEmpty(insurance),
            owner: common.checkToEmpty(owner),
            insuranced: common.checkToEmpty(insuranced),
            mobile: $.trim($("#mobile").val()),
            licenseNo: $.trim($("#licenseNo").val()),
            agent: common.checkToEmpty($("#agentSel").val()),
            vipCompany: common.checkToEmpty($("#vipCompanySel").val()),
            channel: common.checkToEmpty($("#channelSel").val()),
            quoteArea: common.checkToEmpty($("#quoteArea_sel").val()),
            cpsChannel: common.checkToEmpty($("#cpsChannelSel").val()),
            orderNo: common.checkToEmpty($("#orderNo").val()),
            paymentChannel: common.checkToEmpty($("#paymentChannelSel").val()),
            orderSourceType: common.checkToEmpty($("#orderSourceTypeSel").val()),
            orderSourceId: common.checkToEmpty($("#monitorUrlSel").val()),
            currentPage: orderSelect.page.currentPage,
            pageSize: orderSelect.page.pageSize
        };
    },
    check: function () {
        var flag = true, msg = "";
        var orderStartDate = $("#orderStartDate").val();
        var orderEndDate = $("#orderEndDate").val();
        var operateStartDate = $("#operateStartDate").val();
        var operateEndDate = $("#operateEndDate").val();
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

        if (orderSelect.param.dataTables != null) {
            orderSelect.param.dataTables.ajax.reload();
            return false;
        }
        dt_labels.language.sLengthMenu="";
        dt_labels.language.sInfo="";
        orderSelect.param.dataList = {
            "url": '/orderCenter/order/filter',
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
                {
                    "data": "currentStatus",
                    "title": "当前状态",
                    'sClass': "text-center",
                    "orderable": false,
                    "sWidth": "100px"
                },
                {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": "owner", "title": " 车主", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
                {"data": "licenseNo", "title": "车牌号", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": "quoteArea", "title": "城市", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
                {
                    "data": "insuranceCompany",
                    "title": "保险公司",
                    'sClass': "text-center",
                    "orderable": false,
                    "sWidth": "120px"
                },
                {"data": "sumPremium", "title": "实付金额", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": null, "title": "指定人", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
                {"data": null, "title": "最后操作", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
                {"data": "userSource", "title": "来源", 'sClass': "text-center", "orderable": false, "sWidth": "70px"},
                {"data": "inviter", "title": "邀请人", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
                {"data": "indirectionInviter", "title": "间接邀请人", 'sClass': "text-center", "orderable": false, "sWidth": "90px"}
            ]

        };
        orderSelect.param.dataTables = datatableUtil.getByDatatables(orderSelect.param.dataList, dataFunction.data, dataFunction.fnRowCallback);
    },
    getCount: function (fieldName, countElement) {
        var reqJson = orderSelect.generateReqJson();
        reqJson.countField = fieldName;
        common.getByAjax(true, "get", "json", "/orderCenter/order/count", reqJson,
            function (data) {
                countElement.text(data);
            }, function () {
            }
        );
    },
    /*导出配送信息*/
    exportDelivery: function () {
        var checkedIds = dt_labels.selected.join(",");
        if (checkedIds.length < 1) {
            msg = "请选择需要导出的订单！";
            common.showTips(msg);
            $("#downloadBtn").attr("href", "#");
            return;
        }
        var url = "/orderCenter/order/exportDelivery?checkedIds=" + checkedIds;
        $("#downloadBtn").attr("href", url);
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
        }  else {//所有
            $(".channel").hide();
            $(".channel").find("select").val("");
        }
    },
}

$(document).ready(function() {
    $('#channelSel').select2();
    $('#insuranceSel').select2();
    $('#paymentChannelSel').select2();
    $('#monitorUrlSel').select2();
    $('#assignerSel').select2();
});
