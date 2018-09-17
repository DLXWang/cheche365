/**
 * Created by lyh on 2015/10/14.
 */
var dataFunction = {
    "data": function (data) {
        data.keyword = datatableUtil.params.keyWord;
        data.keyType = datatableUtil.params.keyType;
        data.status = $("#status").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $templetNo = (aData.zucpCode == "" ? "" : "漫道：" + aData.zucpCode + "<br>") + (aData.yxtCode == "" ? "" : "盈信通：" + common.checkToEmpty(aData.yxtCode));
        $('td:eq(1)', nRow).html($templetNo);
    }
};
var schedulelog = {
    "url": '/operationcenter/sms/schedule/log',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": "id", "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": null, "title": "模板号", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {
            "data": "content",
            "title": "短信内容",
            'sClass': "text-center",
            "orderable": false,
            render: function (data, type, row) {
                return '<div style="width: 660px;word-wrap:break-word;" >' + data + '</div>';
            },
            "sWidth": "40%"
        },
        {
            "data": "condition",
            "title": "触发条件",
            'sClass': "text-center",
            "orderable": false,
            render: function (data, type, row) {
                return '<div style="width: 160px;word-wrap:break-word;" >' + data + '</div>';
            },
            "sWidth": "10%"
        },
        {"data": "mobile", "title": "接受用户手机号", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {
            "data": "sendTime",
            "title": "发送时间",
            'sClass': "text-center",
            "orderable": false,
            render: function (data, type, row) {
                return '<div style="width: 110px;word-wrap:break-word;" >' + data + '</div>';
            },
            "sWidth": "10%"
        },
        {"data": "status", "title": "发送状态", 'sClass': "text-center", "orderable": false, "sWidth": "5%"},
    ]
};

dt_labels.autoWidth = false;
var datatables = datatableUtil.getByDatatables(schedulelog, dataFunction.data, dataFunction.fnRowCallback);

$(function () {
    if (!common.permission.validUserPermission("op0304")) {
        return;
    }
    /**
     * 搜索
     */
    $("#searchBtn").bind({
        click: function () {
            if (!common.permission.validUserPermission("op0304")) {
                return;
            }
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould("请输入搜索内容", "first", "warning", "", "", null);
                return false;
            }
            datatableUtil.params.keyWord = keyword;
            datatableUtil.params.keyType = $("#keyType").val();
            datatables.ajax.reload();
        }
    });

    $("#status").bind({
        change: function () {
            if (!common.permission.validUserPermission("op0304")) {
                return;
            }
            datatables.ajax.reload();
        }
    });
});
