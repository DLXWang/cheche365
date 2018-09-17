/**
 * Created by wangshaobin on 2017/5/3.
 */
/* 钱包列表查询 */
var list = {
    "url": '/orderCenter/wallet/list',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": null, "title": "手机号", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": null, "title": "当前状态", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "当前类型", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "入账总金额", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": null, "title": "提现总金额", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": null, "title": "当前余额", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": null, "title": "最后操作时间", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
    ]
};

var dataFunction = {
    "data": function (data) {
        data.keyword = $("#keyword").val();
        data.keyType = $("#keyType").val();
        data.startTime = $("#startTime").val();
        data.endTime = $("#endTime").val();
        data.channel = $("#channelSel").val();
        data.isGroupBy = 1;
    },
    "fnRowCallback": function (nRow, aData) {
        $detailLink = "<a href='../../page/wallet/wallet_detail.html?walletId=" + aData.walletId + "' target='_blank'>" + aData.mobile + "</a>";
        $status = common.checkToEmpty(aData.status);
        $type = common.tools.checkToEmpty(aData.type);
        $inAmount = common.formatMoney(aData.inAmount, 2);
        $outAmount = common.formatMoney(aData.outAmount, 2);
        $balance = common.formatMoney(aData.balance, 2);
        $lastOperatorTime = common.checkToEmpty(aData.lastOperatorTime);
        $('td:eq(0)', nRow).html($detailLink);
        $('td:eq(1)', nRow).html($status);
        $('td:eq(2)', nRow).html($type);
        $('td:eq(3)', nRow).html($inAmount);
        $('td:eq(4)', nRow).html($outAmount);
        $('td:eq(5)', nRow).html($balance);
        $('td:eq(6)', nRow).html($lastOperatorTime);
    },
    "fnDrawCallback": function (datatable) {
        if (common.isEmpty($("#wallet_amount").text())) {
            $(".dataTables_length").append('<span id="wallet_amount" style="margin-left: 20px;">提现总额：<span id="wallet_out_amount" class="detail-all"></span>元&nbsp;&nbsp;&nbsp;&nbsp;总余额：<span id="wallet_total_amount" class="detail-all"></span>元</span>');
        }
        walletList.walletAmount();
    }
};

var datatables = datatableUtil.getByDatatables(list, dataFunction.data, dataFunction.fnRowCallback, dataFunction.fnDrawCallback);

var walletList = {
    initChannels: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/channel/getToAChannels", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "<option value=''>请选择渠道</option>";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.id + "'>" + model.description + "</option>";
                });

                $("#channelSel").append(options);
            }, function () {
            }
        );
    },
    walletAmount: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/wallet/walletAmount",
            {
                keyType: $("#keyType").val(),
                keyword: $("#keyword").val(),
                startTime: $("#startTime").val(),
                endTime: $("#endTime").val(),
                channel: $("#channelSel").val(),
                isGroupBy: 0
            },
            function (data) {
                $("#wallet_out_amount").text(common.formatMoney(data.outAmount, 2));
                $("#wallet_total_amount").text(common.formatMoney(data.balance, 2));
            }, function () {
                popup.mould.popTipsMould(false, "钱包提现总额、总余额获取异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    clickTimeFunc: function () {
        var startTime = $("#startTime").val();
        var endTime = $("#endTime").val();
        if (startTime && endTime)
            datatables.ajax.reload();
    },
    channelChange: function () {
        datatables.ajax.reload();
    },

    initTemplateUrl: function () {
        common.getByAjax(true, "get", "json", "../../orderCenter/walletRemit/template/url", null, function (response) {
            $("#url_template").prop("href", response.back_template);
        }, function () {
            popup.mould.popTipsMould("模版地址初始化异常！！", popup.mould.first, popup.mould.error, "", "53%", null);
        });
    }
};

$(function () {
    //初始化回传打款历史按钮
    if (common.permission.hasPermission("or110101")) {
        $('.dropdown').removeClass('hidden');
    }

    walletList.initChannels();
    walletList.initTemplateUrl();
    $("#searchBtn").bind({
        click: function () {
            if (common.validations.isEmpty($("#keyword").val())) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            datatables.ajax.reload();
        }
    });
    /* 按渠道查找 */
    $("#channelSel").bind({
        change: function () {
            walletList.channelChange();
        }
    });
});

dropBut = function () {
    var drop = document.getElementById("drop");
    var div = document.getElementById("div1");

    if (div.style.display == "block") {
        div.style.display = 'none';
    }
    else {
        div.style.display = 'block';
    }
}

validate = function () {
    var fileName = $("#batchUpload").val();
    if (common.isEmpty($("#batchUpload").val())) {
        popup.mould.popTipsMould(false, "请选择需上传的文件！", popup.mould.second, popup.mould.error, "", "57%", null);
        return false;
    }
    return true;
}

submit = function () {
    var form = $("#batchUpLoadForm");
    var options = {
        url: "/orderCenter/walletRemit/upload",
        async: false,
        type: "post",
        dataType: "text",
        beforeSend: function () {
        },
        success: function (responseStr, statusText) {
            document.getElementById("batchUpload").value = null;
            responseStr = $.trim(responseStr);
            if (statusText === 'success') {
                if (responseStr === 'success') {
                    popup.mould.popTipsMould(false, "处理完成", popup.mould.second, popup.mould.success, "", "57%", null);
                    $(".form_input").val("");
                } else if (responseStr.startsWith('<')) {
                    popup.mould.popTipsMould(false, "上传文件大小超过限制,请修改！", popup.mould.second, popup.mould.error, "", "57%", null);
                } else {
                    popup.mould.popTipsMould(false, responseStr, popup.mould.second, popup.mould.error, "", "57%", null);
                }
            } else {
                popup.mould.popTipsMould(false, "上传失败！", popup.mould.second, popup.mould.error, "", "57%", null);
            }
        },
        error: function (responseStr) {
            document.getElementById("batchUpload").value = null;
            popup.mould.popTipsMould(false, "上传失败！", popup.mould.second, popup.mould.error, "", "57%", null);
        }
    };
    form.ajaxSubmit(options);
}
uploadValue = function () {
    if (!common.permission.validUserPermission("or110101")) {
        return;
    }
    if (validate()) {
        submit();
    }
}

$(document).ready(function() {
    $('#channelSel').select2({ placeholder: "请选择渠道",
                                 dropdownAutoWidth : true});
});
