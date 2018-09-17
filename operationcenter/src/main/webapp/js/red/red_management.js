/**
 * Created by wxq on 2017/3/8.
 */
var dataFunction = {
    "data": function (data) {
    },
    "fnRowCallback": function (nRow, aData) {
        $id = common.checkToEmpty(aData.id);
        $startTime = common.checkToEmpty(aData.startTime);
        $endTime = common.checkToEmpty(aData.endTime);
        $totalNum = common.checkToEmpty(aData.totalNum);
        $sendNum = common.checkToEmpty(aData.sendNum);
        switch (aData.status) {
            case 1:
                $status = "<span style=\"color: orange;\">待审核</span>";
                break;
            case 2:
                $status = "<span style=\"color:green ;\">审核通过</span>";
                break;
            case 3:
                $status = "<span style=\"color:red ;\">审核失败</span>";
                break;
            case 4:
                $status = "<span style=\"color:greenyellow ;\">已发放</span>";
                break;
        }
        if (aData.status <= 1) {
            $operation = "<a id='red_status_success_id_" + aData.id + "' name=\"pactfile\" style=\"color: green;\" target=\"_self\" onclick=\"redList.switchStatus('" + aData.startTime + "'," + 2 + "," + aData.id + ");\" >审核通过</a>"
                + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a id='red_status_fail_id_" + aData.id + "' name=\"pactfile\" style=\"color: red;\" target=\"_self\" onclick=\"redList.switchStatus('" + aData.startTime + "'," + 3 + "," + aData.id + ");\" >审核失败</a>"
                + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a name=\"pactfile\" style=\"color: blue;\" target=\"_self\" onclick=\"redList.detail('" + aData.startTime + "')\" >查看详情</a>";
        } else {
            $operation = "<span style=\"color: darkgrey;\">审核通过</span>"
                + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span id='red_status_fail1_id_" + aData.id + "' style=\"color: darkgrey;\">审核失败</span>"
                + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a name=\"pactfile\" style=\"color: blue;\" target=\"_self\" onclick=\"redList.detail('" + aData.startTime + "')\" >查看详情</a>";
        }
        $('td:eq(0)', nRow).html($id);
        $('td:eq(1)', nRow).html($startTime);
        $('td:eq(2)', nRow).html($endTime);
        $('td:eq(3)', nRow).html($totalNum);
        $('td:eq(4)', nRow).html($sendNum);
        $('td:eq(5)', nRow).html($status);
        $('td:eq(6)', nRow).html($operation);
    }
};
var redList = {
    "url": '/operationcenter/red',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": null, "title": "批次", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "开始时间", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
        {"data": null, "title": "结束时间", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
        {"data": null, "title": "参与总数", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "发放数（已去重）", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "状态", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "360px"}
    ],
    switchStatus: function (startTime, status, id) {
        common.getByAjax(true, "get", "json", "/operationcenter/red/audit", {
                operateDate: startTime.substring(0, 10),
                status: status
            },
            function (data) {
                if (data.pass) {
                    if (status == 2) {
                        $("#red_status_id_" + id).css({'color': 'green'}).html("审核通过");
                    }
                    if (status == 3) {
                        $("#red_status_id_" + id).css({'color': 'red'}).html("审核失败");
                    }
                    $("#red_status_success_id_" + id).hide();
                    $("#red_status_fail_id_" + id).hide();
                    $("#red_status_success_ids_" + id).show();
                    $("#red_status_fail_ids_" + id).show();
                } else {
                    popup.mould.popTipsMould(data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                }
            },
            function () {
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    detail: function (startTime) {
        userRedPacket.show.popInput(startTime);
    }
};
var datatables= datatableUtil.getByDatatables(redList, dataFunction.data, dataFunction.fnRowCallback);
$(function () {
    if (!common.permission.validUserPermission("op0401")) {
        return;
    }
});
