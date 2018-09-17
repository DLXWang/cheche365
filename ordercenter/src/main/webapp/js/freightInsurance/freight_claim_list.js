var list = {
    "url": '/orderCenter/freightInsurance/claim',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": "policyNo", "title": "保单号", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "channelName", "title": "渠道名称", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "categoryName", "title": "商品类别", 'sClass': "text-center", "orderable": false, "sWidth": "200px"},
        {"data": "productName", "title": "商品名称", 'sClass': "text-center", "orderable": false, "sWidth": "130px"},
        {"data": "amount", "title": "保额(元)", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "premium", "title": "保费(元)", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "compensation", "title": "理赔金额(元)", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "claimTime", "title": "理赔时间", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "240px"}
    ]
};

var dataFunction = {
    "data": function (data) {
        data.channel = $(".channel").val();
        data.thirdPartyOrderNo = $(".thirdPartyOrderNo").val();
        data.categoryCode = $(".categoryCode").val();
        data.productName = $(".productName").val();
        data.compensationStart = $(".compensationStart").val();
        data.compensationEnd = $(".compensationEnd").val();
        data.claimTimeStart = $(".claimTimeStart").val();
        data.claimTimeEnd = $(".claimTimeEnd").val();
        data.claimNo = $(".claimNo").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $detailLink = "<a href='../../page/freightInsurance/freight_order_detail.html?orderId=" + aData.id + "' target='_blank'>详情</a>";
        $('td:eq(8)', nRow).html($detailLink);
    },
    "fnDrawCallback": function (datatable) {
        //总理赔金额（元）/ 总单数（单）
        var api = datatable.api();
        var json = api.ajax.json();
        var data = json.data;
        if (common.isEmpty($("#extra_info").text())) {
            $(".dataTables_length").append('&nbsp;&nbsp;&nbsp;&nbsp;<label id="extra_info">总理赔金额 ' +
                '<span id="totalCompensation">' + data.totalCompensation +
                ' </span>（元）/ 总单数 <span id="totalClaimCount">' + data.totalClaimCount +
                ' </span>（单）</label> ');
        } else {
            $("#totalCompensation").text(data.totalCompensation);
            $("#totalClaimCount").text(data.totalClaimCount);
        }
    }
};

var datatables = datatableUtil.getByDatatables(list, dataFunction.data, dataFunction.fnRowCallback, dataFunction.fnDrawCallback);

var freight_claim_list = {
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
            }
        );
    }
}

$(function () {
    freight_claim_list.initChannels();
    $(".searchBtn").bind({
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
                freight_claim_list.initCategorys(channelId);
            } else {
                $('.channel_relate_input').prop("value", "");
                $('.channel_relate').addClass('useless');
            }
        }
    });
});
