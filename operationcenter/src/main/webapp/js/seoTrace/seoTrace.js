/**
 * Created by cxy on 2017/9/1.
 */

var createUrlDataFunction = {
    "data": function (data) {
        data.startDate = $('#startTime').val();
        data.endDate = $('#endTime').val();
    },
    "fnRowCallback": function (nRow, aData) {
        $('td:eq(1)', nRow).html('<a href="/views/seoTrace/seoInfo.html?source='+encodeURI(encodeURI(aData.source))+'&source_id='+ aData.sourceId+ '" target="_blank"> ' + aData.source + '</a><br/>');
    },
}

var urlList = {
    "url": '/operationcenter/accessDetail',
    "type": "GET",
    "table_id": "trace_list",
    "columns": [
        {"data": "id", "title": "序号", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data":  null, "title": "来源", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "mobileNum", "title": "电话数", 'sClass': "text-center", "orderable": false,"sWidth":""},
    ],
}

var datatables;
$(function () {
    datatables = datatableUtil.getByDatatables(urlList, createUrlDataFunction.data, createUrlDataFunction.fnRowCallback);

    $("#searchBtn").unbind("click").bind({
        click: function() {
            datatables.ajax.reload();
        }
    });

    $("#downloadBtn").unbind("click").bind({
        click:function(){
            var url = "/operationcenter/accessDetail/export?"
                + "startDate=" + $("#startTime").val()
                + "&endDate=" + $("#endTime").val()
            $("#downloadBtn").attr("href", url);
        }
    });
});
