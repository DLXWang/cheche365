/**
 * Created by cxy on 2016/8/11.
 * Created datatable by cxy on 2017/1/13.
 */

var dataFunction = {
    "sourceStr" :null,
    "data": function (data) {
        data.startDate = $('#startTime').val();
        data.endDate = $('#endTime').val();
        data.source = dataFunction.sourceStr;
    },
    "fnRowCallback": function (nRow, aData) {
    },
};
var dataList = {
    "url": '/operationcenter/accessDetail/info',
    "type": "GET",
    "table_id": "trace_list",
    "columns": [
        {"data": "id", "title": "序号", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "url", "title": "访问链接", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "mobileNum", "title": "信息数", 'sClass': "text-center", "orderable": false,"sWidth":""}
    ],
}
var datatables;
$(function(){
    if (!common.permission.validUserPermission("op0703")) {
        return;
    }
    dataFunction.sourceStr = common.getUrlParam("source_id");
    $('#detail_title').text(decodeURI(decodeURI(common.getUrlParam("source"))) + "SEO跟踪详情");
    datatables = datatableUtil.getByDatatables(dataList,dataFunction.data,dataFunction.fnRowCallback);
    $("#searchBtn").unbind("click").bind({
        click: function() {
            datatables.ajax.reload();
        }
    });

    $("#downloadBtn").unbind("click").bind({
        click:function(){
            var url = "/operationcenter/accessDetail/info/export?"
                + "source=" + dataFunction.sourceStr
                + "&startTime=" + $("#startTime").val()
                + "&endTime=" + $("#endTime").val();
            $("#downloadBtn").attr("href", url);
        }
    });
});
