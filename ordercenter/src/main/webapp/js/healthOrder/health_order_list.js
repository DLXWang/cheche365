var dataFunction = {
    "data": function (data) {
        data.orderStatus = $("#orderStatus").val();
        data.channel = $("#channel").val();
        data.orderNo = $("#orderNo").val();
        data.orderStartDate = $("#startTime").val();
        data.orderEndDate = $("#endTime").val();
        data.mobile = $("#mobile").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $orderNo = common.getOrderIcon(aData.channelIcon) + '<a href="/page/healthOrder/healthorder_detail.html?id=' + aData.insurancePolicyId + '" target="_blank">' + aData.orderNo + '</a><br/>' + aData.createTime;
        $('td:eq(0)', nRow).html($orderNo);
    },
}
var dataList = {
    "url": '/orderCenter/healthOrder',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data":null, "title": "订单编号", 'sClass': "text-center", "orderable": false},
        {"data": "orderStatus..description", "title": "当前订单状态", 'sClass': "text-center", "orderable": false},
        {"data": "channel.description", "title": "购买平台", 'sClass': "text-center", "orderable": false},
        {"data": "insuranceCompany.name", "title": "保险公司", 'sClass': "text-center", "orderable": false},
        {"data": "applicantPerson.name", "title": "投保人", 'sClass': "text-center", "orderable": false},
        {"data": "insuranceCompany.name", "title": "实付金额", 'sClass': "text-center", "orderable": false},
        {
            "data": "insurancePolicyId",
            "title": "操作",
            "sClass": "text-center",
            "orderable": false,
            "render": function(data, type, row, meta) {
                return '<a href="/page/healthOrder/healthorder_detail.html?id=' + data + '" target="_blank"> 查看详情</a><br/>';
            }},
    ],
}
var orderList = {
    channel:[3,40],
    orderStatus:[1,5,6,9,10],
    interface: {
        getAllOrderStatus: function(callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/orderStatus", {},
                function(data) {
                    callback(data);
                },
                function() {}
            );
        },
        getAllChannel: function(callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/quote/photo/channels", {},
                function(data) {
                    callback(data);
                },
                function() {}
            );
        },
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
                var options = "";
                $.each(statusList, function(i, model){
                    for(j=0;j<statusList.length;j++){
                        if(orderList.orderStatus[j] == model.id){
                            options += "<option value=\"" + model.id + "\">" + model.status + "</option>";
                        }
                    }
                });
                $("#orderStatus").append(options)
            });

            orderList.interface.getAllChannel(function(channelList) {
                    var options = "";
                    $.each(channelList, function(i, model){
                        for(j=0;j<channelList.length;j++){
                            if(orderList.channel[j] == model.id){
                                options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                            }
                        }
                    });
                    $("#channel").append(options);
            });
        }
    },
};
var datatables;

$(function() {
    datatables = datatableUtil.getByDatatables(dataList,dataFunction.data,dataFunction.fnRowCallback);
    orderList.initialization.initPage();
    $("#orderStatus, #channel").unbind("change").bind({
        change : function(){
            datatables.ajax.reload();
        }
    });
    $("#searchBtn").unbind("click").bind({
        click : function(){
            datatables.ajax.reload();
        }
    });
});
