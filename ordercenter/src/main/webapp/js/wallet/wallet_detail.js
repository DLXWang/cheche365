/**
 * Created by wangshaobin on 2017/5/16.
 */
/* 银行卡列表查询 */
var bankList = {
    "url": '/orderCenter/wallet/bankCardList',
    "type": "GET",
    "table_id": "bank_card_list_tab",
    "columns": [
        {"data": null, "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": null, "title": "银行名称", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "银行卡号", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "提现金额", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
    ]
};

var bankDataFunction = {
    "data": function (data) {
        data.walletId = common.getUrlParam("walletId")
    },
    "fnRowCallback": function (nRow, aData) {
        $bankName = common.checkToEmpty(aData.bankName);
        $bankNo = common.tools.checkToEmpty(aData.bankNo);
        $outAmount = common.formatMoney(aData.outAmount, 2);
        $('td:eq(1)', nRow).html($bankName);
        $('td:eq(2)', nRow).html($bankNo);
        $('td:eq(3)', nRow).html($outAmount);
    },
    "fnDrawCallback": function (datatable) {
        var api = datatable.api();
        var startIndex = api.context[0]._iDisplayStart;//获取到本页开始的条数
        api.column(0).nodes().each(function (cell, i) {
            cell.innerHTML = startIndex + i + 1;
        });
    }
};

var bankDatatables = datatableUtil.getByDatatables(bankList, bankDataFunction.data, bankDataFunction.fnRowCallback, bankDataFunction.fnDrawCallback);

/* 交易列表查询 */
var transactionList = {
    "url": '/orderCenter/wallet/transactionList',
    "type": "GET",
    "table_id": "transaction_list_tab",
    "columns": [
        {"data": null, "title": "流水号", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "类型", 'sClass': "text-center", "orderable": false, "sWidth": "200px"},
        /*{"data": null, "title": "车牌号", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},*/
        {"data": null, "title": "银行", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "当前状态", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": null, "title": "订单失败原因", 'sClass': "text-center", "orderable": false, "sWidth": "200px"},
        {"data": null, "title": "操作金额", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
        {"data": null, "title": "余额", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
        {"data": null, "title": "时间", 'sClass': "text-center", "orderable": false, "sWidth": "200px"},
        {"data": null, "title": "操作平台", 'sClass': "text-center", "orderable": false, "sWidth": "150px"},
    ]
};

var transactionDataFunction = {
    "data": function (data) {
        data.walletId = common.getUrlParam("walletId");
        data.type = $("#keyType").val();
        data.sources = $("#sourceSel").val() ? $("#sourceSel").val().join(",") : null;
        data.statuses = $("#statusSel").val() ? $("#statusSel").val().join(",") : null;
        data.platforms = $("#platformSel").val() ? $("#platformSel").val().join(",") : null;
        data.startTime = $("#startTime").val();
        data.endTime = $("#endTime").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $outTradeNo = common.checkToEmpty(aData.tradeNo);
        $type = common.tools.checkToEmpty(aData.type);
        /*$licenseNo = common.checkToEmpty(aData.licenseNo);*/
        $bankName = common.checkToEmpty(aData.bankName);
        $status = common.tools.checkToEmpty(aData.status);
        $failReason = common.tools.checkToEmpty(aData.failReason);
        $operatorAmount = common.formatMoney(aData.opeateAmount, 2) + " 元";
        $amount = common.formatMoney(aData.balance, 2);
        $time = common.tools.checkToEmpty(aData.lastOperatorTime);
        $platform = common.checkToEmpty(aData.platform);
        $('td:eq(0)', nRow).html($outTradeNo);
        $('td:eq(1)', nRow).html($type);
        /*$('td:eq(2)', nRow).html($licenseNo);*/
        $('td:eq(2)', nRow).html($bankName);
        $('td:eq(3)', nRow).html($status);
        $('td:eq(4)', nRow).html($failReason);
        $('td:eq(5)', nRow).html($operatorAmount);
        $('td:eq(6)', nRow).html($amount);
        $('td:eq(7)', nRow).html($time);
        $('td:eq(8)', nRow).html($platform);
    },
    "fnDrawCallback": function (datatable) {
        if (common.isEmpty($("#transaction_amount").text())) {
            $("#transaction_info .dataTables_length").append('<span id="transaction_amount" style="margin-left: 20px;">入账金额：<span id="transaction_in_amount" class="detail-all"></span>元&nbsp;&nbsp;&nbsp;&nbsp;提现金额：<span id="transaction_out_amount" class="detail-all"></span>元</span>');
        }
        wallet_detail.walletDetailAmount();
    }
};

var transactionDatatables = datatableUtil.getByDatatables(transactionList, transactionDataFunction.data, transactionDataFunction.fnRowCallback, transactionDataFunction.fnDrawCallback);

var wallet_detail = {
    init: function(){
        wallet_detail.initUserInfo();
        wallet_detail.initChannel();
        wallet_detail.initSource();
        wallet_detail.initStatus();
    },
    walletDetailAmount: function(){
        common.getByAjax(true, "get", "json", "/orderCenter/wallet/transactionAmount",
            {
                walletId : common.getUrlParam("walletId"),
                type : $("#keyType").val(),
                sources : $("#sourceSel").val() ? $("#sourceSel").val().join(",") : null,
                statuses : $("#statusSel").val() ? $("#statusSel").val().join(",") : null,
                platforms : $("#platformSel").val() ? $("#platformSel").val().join(",") : null,
                startTime : $("#startTime").val(),
                endTime : $("#endTime").val()
            },
            function (data) {
                $("#transaction_out_amount").text(common.formatMoney(data.outAmount, 2));
                $("#transaction_in_amount").text(common.formatMoney(data.inAmount, 2));
            }, function () {
                popup.mould.popTipsMould(false,"钱包提现总额、总余额获取异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    initChannel: function() {
        common.getByAjax(true, "get", "json", "/orderCenter/wallet/channel", {},
            function(data) {
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                    });
                    if($("#platformSel").length>0){
                        $("#platformSel").append(options);
                        $("#platformSel").multiselect({
                            nonSelectedText: '请选择渠道',
                            buttonWidth: '180',
                            maxHeight: '180',
                            includeSelectAllOption: true,
                            selectAllNumber: false,
                            selectAllText: '全部',
                            allSelectedText: '全部'
                        });
                    }
                }
            },function() {}
        );
    },
    initSource: function() {
        common.getByAjax(true, "get", "json", "/orderCenter/wallet/source", {},
            function(data) {
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                    });
                    if($("#sourceSel").length>0){
                        $("#sourceSel").append(options);
                        $("#sourceSel").multiselect({
                            nonSelectedText: '请选择来源',
                            buttonWidth: '180',
                            maxHeight: '180',
                            includeSelectAllOption: true,
                            selectAllNumber: false,
                            selectAllText: '全部',
                            allSelectedText: '全部'
                        });
                    }
                }
            },function() {}
        );
    },
    initStatus: function() {
        common.getByAjax(true, "get", "json", "/orderCenter/wallet/status", {},
            function(data) {
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                    });
                    if($("#statusSel").length>0){
                        $("#statusSel").append(options);
                        $("#statusSel").multiselect({
                            nonSelectedText: '请选择状态',
                            buttonWidth: '180',
                            maxHeight: '180',
                            includeSelectAllOption: true,
                            selectAllNumber: false,
                            selectAllText: '全部',
                            allSelectedText: '全部'
                        });
                    }
                }
            },function() {}
        );
    },
    initUserInfo: function(){
        common.getByAjax(true, "get", "json", "/orderCenter/wallet/userInfo",
            {
                walletId: common.getUrlParam("walletId")
            },
            function (data) {
                $("#userMobile").text(data.mobile);
                $("#balance").text(common.formatMoney(data.balance, 2));
                $("#platform").text(data.regChannel);
                $("#outAmount").text(common.formatMoney(data.outAmount, 2));
            }, function () {
                popup.mould.popTipsMould(false,"用户信息获取异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    }
}

$(function() {
    wallet_detail.init();
    /* 搜索 */
    $("#searchBtn").bind({
        click : function(){
            transactionDatatables.ajax.reload();
        }
    });
});

$(document).ready(function() {
    /*$('#platformSel').select2();*/
});
