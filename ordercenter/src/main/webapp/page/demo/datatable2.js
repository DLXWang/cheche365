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
        //data.sort = param[order[0].column];
        //data = order_list.datatables.formatData(data);
    },
    "fnRowCallback": function (nRow, aData) {
        $orderNo = common.getOrderIcon(aData.channelIcon) + '<a href="/page/order/order_detail.html?id=' + aData.purchaseOrderId + '" target="_blank">' + aData.orderNo + '</a><br/>' + aData.createTime;
        $comment = "<a href=\"javascript:;\" onclick=\"orderList.list.addComment(" + aData.purchaseOrderId + "," + aData.orderOperationInfoId + ");\">查看备注</a>"
            + (aData.latestComment ? ("<br/>" + common.tools.getCommentMould(aData.latestComment, 5)) : "");
        $owner = aData.auto.owner + '<br/>' + aData.auto.licensePlateNo;
        $area = (aData.area ? aData.area.name : '') + '<br/>' + aData.insuranceCompany.name;
        $price = common.checkToEmpty(aData.paidAmount) + '<br/>' + common.checkToEmpty(aData.payableAmount);
        $lastOperation = aData.operatorName + '<br/>' + aData.updateTime;
        $('td:eq(0)', nRow).html($orderNo);
        $('td:eq(2)', nRow).html($comment);
        $('td:eq(3)', nRow).html(orderList.list.fixOneItem(aData));
        $('td:eq(4)', nRow).html(common.checkToEmpty(aData.assignerName));
        $('td:eq(5)', nRow).html($owner);
        $('td:eq(7)', nRow).html($area);
        $('td:eq(8)', nRow).html($price);
        $('td:eq(11)', nRow).html($lastOperation);
    },
}
var orderList = {
    "url": '/orderCenter/dataTable',
    "type": "POST",
    "table_id": "list_tab",
    "columns": [
        {"data": null, "title": "订单号", 'sClass': "text-center", "orderable": false},
        {"data": "currentStatus.description", "title": "当前状态", 'sClass': "text-center", "orderable": false},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false},
        {"data": "auto.owner", "title": "指定人", 'sClass': "text-center", "orderable": false},
        {"data": null, "title": "车主车牌", 'sClass': "text-center", "orderable": false},
        {"data": "paymentChannel.fullDescription", "title": "支付方式", 'sClass': "text-center", "orderable": false},
        {"data": "insuranceCompany.name", "title": "地区保险公司", 'sClass': "text-center", "orderable": false},
        {"data": "paidAmount", "title": "实付金额原始金额", 'sClass': "text-center", "orderable": false},
        {"data": "confirmNo", "title": "支付号", 'sClass': "text-center", "orderable": false},
        {"data": "gift", "title": "礼品", 'sClass': "text-center", "orderable": false},
        {"data": null, "title": "最后操作", 'sClass': "text-center", "orderable": false},
    ],
    paymentChannel: {
        toOnlinePay: function(purchaseOrderId) {
            popup.mould.popConfirmMould(false, "确定转为线上支付？", popup.mould.first, "", "",
                function() {
                    popup.mask.hideFirstMask(false);
                    orderList.interface.changeToOnlinePay(purchaseOrderId, function(data) {
                        if (data.result == 'success') {
                            datatables.ajax.reload();
                        } else {
                            popup.mould.popTipsMould(false, "线下转线上支付失败！", popup.mould.first, popup.mould.error, "", "", null);
                        }
                        datatables.ajax.reload();
                    });
                },
                function() {
                    popup.mask.hideFirstMask(false);
                }
            );
        },
        'ajax': {
            "type" : "GET",
            "url" : '/orderCenter/dataTable',
            "data" : function( data ) {
                data.currentPage= 1;
                data.pageSize = $("[name='list_tab_length']").val();
                data.sort= 'updateTime';

            },
        },
        "bPaginate" : true,// 分页按钮
        "sPaginationType" : "full_numbers",
        "columns": [
            { "data": null, "title":"订单号", 'sClass': "text-center"},
            { "data": "currentStatus.description" ,"title":"当前状态", 'sClass': "text-center"},
            { "data": null, "title":"备注", 'sClass': "text-center"},
            { "data": null, "title":"操作", 'sClass': "text-center"},
            { "data": "auto.owner" ,"title":"指定人", 'sClass': "text-center"},
            { "data": null, "title":"车主车牌", 'sClass': "text-center"},
            { "data": "paymentChannel.fullDescription","title":"支付方式", 'sClass': "text-center"},
            { "data": "insuranceCompany.name" ,"title":"地区保险公司", 'sClass': "text-center"},
            { "data": "paidAmount" ,"title":"实付金额原始金额", 'sClass': "text-center"},
            { "data": "confirmNo" ,"title":"支付号", 'sClass': "text-center"},
            { "data": "gift" ,"title":"礼品", 'sClass': "text-center"},
            { "data": null, "title":"最后操作", 'sClass': "text-center"},
        ],
        "fnRowCallback":function (nRow, aData) {
            $orderNo = '<a href="/page/order/order_detail.html?id=' + aData.purchaseOrderId + '" target="_blank">' + aData.orderNo + '</a><br/>' + aData.createTime;
            $comment = "<a href=\"javascript:;\" onclick=\"orderList.list.addComment(" + aData.purchaseOrderId + "," + aData.orderOperationInfoId + ");\">查看备注</a>" +
                (aData.latestComment ? ("<br/>" + common.tools.getCommentMould(aData.latestComment, 5)) : "");
            $owner = aData.auto.owner + '<br/>' + aData.auto.licensePlateNo;
            $area = (aData.area ? aData.area.name : '') + '<br/>' + aData.insuranceCompany.name;
            $price = common.checkToEmpty(aData.paidAmount) + '<br/>' + common.checkToEmpty(aData.payableAmount);
            $lastOperation = aData.operatorName + '<br/>' + aData.updateTime;


            orderList.interface.getAllAreas(function(areaList) {
                $("#area").append(common.getFormatOptionList(areaList,'id','name'));
            });
        }
    },
    list: {
        disableInsuranceCompanyIds:[50000,55000,205000],//这些保险公司不允许更改订单状态：50000-众安保险;55000-安盛天平;205000-华安保险
        fixOneItem: function(model) {
            var changeStatusText = "";
            var switchToOnlinePay = "";
            var insuranceInputLink = "";
            var additionPaid = "";
            var insuranceFailureLink = "";
            var orderImageStatusLink = "";
            var applyForRefund = "";

        }
    },
};
var datatables = datatableUtil.getByDatatables(orderList,dataFunction.data,dataFunction.fnRowCallback);

$(function() {
    orderStatusList.getAllOrderStatus();//订单状态 后台传
    orderList.initialization.initPage();
    $("#orderStatus, #paymentChannel, #area, #paymentStatus, #sort").unbind("change").bind({
        change : function(){
            datatables.ajax.reload();
        }
    });
    $("#searchBtn").unbind("click").bind({
        click : function(){
            var keyword = $("#keyword").val();
            if(common.isEmpty(keyword)){
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            datatableUtil.params.keyword = keyword;
            datatableUtil.params.keyType = $("#keyType").val();
            datatables.ajax.reload();
        }
    });
});
