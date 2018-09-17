/**
 * Created by cxy on 2016/8/11.
 * Created datatable by cxy on 2017/1/13.
 */

var dataFunction = {
    "sortRule" : "DESC",
    "sort" : "id",
    "data": function (data) {
        var param = new Array;
        param[0] = 'amu.id';//没用
        param[7] = 'pvs';
        param[8] = 'uvs';
        param[9] = 'users';
        param[10] = 'orders';
        param[11] = 'paynum';
        param[12] = 'amount';
        param[13] = 'tel';
        data.orderColumn = data.order[0].column;
        data.sortRule = data.order[0].dir;
        dataFunction.sortRule = data.sortRule;
        data.sort = param[data.order[0].column];
        dataFunction.sort = data.sort;
        data.scope = $('#search_scope').val();
        data.source = $('#search_source').val();
        data.plan = $('#search_plan').val();
        data.unit = $('#search_unit').val();
        data.keyword = $('#search_keyword').val();
        data.startTimeStr = $('#startTime').val();
        data.endTimeStr = $('#endTime').val();
    },
    "fnRowCallback": function (nRow, aData) {
    },
};
var dataList = {
    "url": '/operationcenter/dataStatistics/dataSearch/dataList',
    "type": "GET",
    "table_id": "data_list",
    "order": [[0, "desc"]],
    "columns": [
        {"data": "url.id", "title": "序号", 'sClass': "text-center", "orderable": true,"sWidth":""},
        {"data": "url.scope", "title": "岗位", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "url.source", "title": "渠道", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "url.plan", "title": "计划", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "url.unit", "title": "单元", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "url.keyword", "title": "关键词", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "url.url", "title": "来源", 'sClass': "text-center", "orderable": false,"sWidth":""},
        // {"data": "url.url",
        //     "title": "来源",
        //     'sClass': "text-center",
        //     "orderable": false,
        //     "sWidth":"",
        //     "render": function(data) {
        //         var reg = /^http(s)?:\/\/(.*?)\//;
        //         return data.replace(reg, "");
        //     }},
        {"data": "pv", "title": "pv", 'sClass': "text-center", "orderable": true,"sWidth":""},
        {"data": "uv", "title": "uv", 'sClass': "text-center", "orderable": true,"sWidth":""},
        {"data": "register", "title": "注册数", 'sClass': "text-center", "orderable": true,"sWidth":""},
        {"data": "submitCount", "title": "订单数", 'sClass': "text-center", "orderable": true,"sWidth":""},
        {"data": "paymentCount", "title": "出单数", 'sClass': "text-center", "orderable": true,"sWidth":""},
        {"data": "paymentAmount", "title": "保费金额", 'sClass': "text-center", "orderable": true,"sWidth":""},
        {"data": "telCount", "title": "电话数", 'sClass': "text-center", "orderable": true,"sWidth":""},
    ],
}
var dataSearch = {
    getDataSearchList : function (groupByOthers,buttomName){
        var url = "/operationcenter/dataStatistics/dataSearch/export?"
            + "scope=" + $("#search_scope").val()
            + "&source=" + $("#search_source").val()
            + "&plan=" + $("#search_plan").val()
            + "&unit=" + $("#search_unit").val()
            + "&keyword=" + $("#search_keyword").val()
            + "&startTimeStr=" + $("#startTime").val()
            + "&endTimeStr=" + $("#endTime").val()
            + "&sortRule=" + dataFunction.sortRule
            + "&sort=" + dataFunction.sort + groupByOthers;
        $("#" + buttomName).attr("href", url);
    },
}
var datatables;
$(function(){
    if (!common.permission.validUserPermission("op0701")) {
        return;
    }
    dt_labels.order = [[0, "desc"]];//第2列的数据倒序排序 此条会通过参数传给服务器
    datatables = datatableUtil.getByDatatables(dataList,dataFunction.data,dataFunction.fnRowCallback);
    $("#searchBtn").unbind("click").bind({
        click: function() {
            if(common.isEmpty($('#startTime').val()) != common.isEmpty($('#endTime').val())){
                $("#dateWarn").show().delay(3000).hide(0);
            }
            datatables.ajax.reload();
        }
    });
    $("#downloadBtn").unbind("click").bind({
        click:function(){
            dataSearch.getDataSearchList("","downloadBtn");
        }
    });
    $("#downloadDetailBtn").unbind("click").bind({
        click:function(){
            dataSearch.getDataSearchList("&groupByDay=1","downloadDetailBtn");
        }
    });
});
