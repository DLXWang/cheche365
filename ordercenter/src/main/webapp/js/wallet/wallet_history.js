var list = {
"url": '/orderCenter/walletRemit/upload/list',
    "type": "GET",
    "table_id": "history_tab",
    "columns": [
    {"data": null, "title": "上传时间", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
    {"data": null, "title": "操作账号", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
    {"data": null, "title": "上传文档名称", 'sClass': "text-center", "orderable": false, "sWidth": "350px"},
    {"data": null, "title": "上传状态", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
]
};

var dataFunction = {
    "data": function (data) {

    },
    "fnRowCallback": function (nRow, aData) {
        $createTime = common.checkToEmpty(aData.createTime);
        $operator = common.checkToEmpty(aData.operator);
        $fileName = common.checkToEmpty(aData.fileName);
        $status = common.checkToEmpty(aData.status);
        $('td:eq(0)', nRow).html($createTime);
        $('td:eq(1)', nRow).html($operator);
        $('td:eq(2)', nRow).html($fileName);
        $('td:eq(3)', nRow).html($status);
    },
    "fnDrawCallback": function (datatable) {

    }
};
dt_labels.aLengthMenu = [[100], [100]];
var datatables = datatableUtil.getByDatatables(list, dataFunction.data, dataFunction.fnRowCallback, dataFunction.fnDrawCallback);
