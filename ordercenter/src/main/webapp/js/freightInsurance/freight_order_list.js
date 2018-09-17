var list = {
    "url": '/orderCenter/freightInsurance/order',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": "orderNo", "title": "订单号", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "channelName", "title": "渠道名称", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "categoryName", "title": "商品类别", 'sClass': "text-center", "orderable": false, "sWidth": "200px"},
        {"data": "productName", "title": "商品名称", 'sClass': "text-center", "orderable": false, "sWidth": "160px"},
        {"data": "amount", "title": "保额(元)", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "premium", "title": "保费(元)", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "effectiveTime", "title": "承保时间", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "policyNo", "title": "保单号", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "claimTime", "title": "理赔时间", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "compensation", "title": "赔付金额", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "240px"}
    ]
};

var dataFunction = {
    "data": function (data) {
        data.orderNo = $(".orderNo").val();
        data.policyNo = $(".policyNo").val();
        data.claimed = $(".claimed").val();
        data.channel = $(".channelSel").val();
        data.thirdPartyOrderNo = $(".thirdPartyOrderNo").val();
        data.categoryCode = $(".categoryCode").val();
        data.productName = $(".productName").val();
        data.premiumStart = $(".premiumStart").val();
        data.premiumEnd = $(".premiumEnd").val();
        data.insureTimeStart = $(".insureTimeStart").val();
        data.insureTimeEnd = $(".insureTimeEnd").val();
        data.effectiveTimeStart = $(".effectiveTimeStart").val();
        data.effectiveTimeEnd = $(".effectiveTimeEnd").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $detailLink = "<a href='../../page/freightInsurance/freight_order_detail.html?orderId=" + aData.id + "' target='_blank'>详情</a>";
        $('td:eq(10)', nRow).html($detailLink);
    },
    "fnDrawCallback": function (datatable) {
        //总保费（元）/ 总单数（单）/ 赔付率
        var api = datatable.api();
        var json = api.ajax.json();
        var data = json.data;
        if (common.isEmpty($("#extra_info").text())) {
            $(".dataTables_length").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label id="extra_info">' +
                '<span>总保费 <span id="totalPremium">' + data.totalPremium +
                '</span> (元) / 总单数 <span id="totalOrderCount">' + data.totalOrderCount +
                '</span> (单) / 赔付率 <span id="compensationRate">' + data.compensationRate +
                ' </span></span></label>');
        } else {
            $("#totalPremium").text(data.totalPremium);
            $("#totalOrderCount").text(data.totalOrderCount);
            $("#compensationRate").text(data.compensationRate);
        }
    }
};

var datatables = datatableUtil.getByDatatables(list, dataFunction.data, dataFunction.fnRowCallback, dataFunction.fnDrawCallback);

var freight_order_list = {
    initChannels: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/freightInsurance/channel", null,
            function (data) {
                if (data == null) {
                    return false;
                }
                var options = "<option value=''>请选择渠道</option>";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.channel + "'>" + model.channelName + "</option>";
                });
                $("#channelSel").append(options);
            }, function () {
                popup.mould.popTipsMould(false, "获取渠道列表异常！", popup.mould.first, popup.mould.error, "", "", null);
            }
        );
    },
    initCategorys: function (channel) {
        common.getByAjax(true, "get", "json", "/orderCenter/freightInsurance/category/" + channel, null,
            function (data) {
                if (data == null) {
                    return false;
                }
                var options = "<option value=''>请选择商品类型</option>";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.categoryCode + "'>" + model.categoryName + "</option>";
                });
                $(".categoryCode").html(options);
            }, function () {
                popup.mould.popTipsMould(false, "获取商品列表异常！", popup.mould.first, popup.mould.error, "", "", null);
            }
        );
    }
}

$(function () {
    freight_order_list.initChannels();
    $("#searchBtn").bind({
        click: function () {
            datatables.ajax.reload();
        }
    });
    /* 按渠道查找 */
    $("#channelSel").bind({
        change: function () {
            var channelId = $(this).val();
            if ($(this).val()) {
                $('.channel_relate').removeClass('useless');
                freight_order_list.initCategorys(channelId);
            } else {
                $('.channel_relate_input').prop("value", "");
                $('.channel_relate').addClass('useless');
            }
        }
    });
});
