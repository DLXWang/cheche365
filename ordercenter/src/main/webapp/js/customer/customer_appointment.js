var dataFunction = {
    "data": function (data) {
        data.channel = $('#sourceChannel').val();
        data.keyword = $("#keyword").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $contact = common.getOrderIcon(aData.channelIcon) + common.checkToEmpty(aData.contact);
        $status = "<select onchange='customerList.changeStatus($(this)," + aData.id + ");' style='width:100%; height:22px;'>" +
            "<option value='1' " + (aData.status == 1 ? "selected" : "") + " >未处理</option>" +
            "<option value='2' " + (aData.status == 2 ? "selected" : "") + ">已处理</option>" + "</select>";
        $note = "<a href=\"javascript:;\" onclick=\"applicationLog.popCommentList('appointment_insurance'," + aData.id + ",'first');\">查看备注</a>";

        $('td:eq(0)', nRow).html($contact);
        $('td:eq(5)', nRow).html($status);
        $('td:eq(6)', nRow).html($note);
    },
}
var customerList = {
    "url": '/orderCenter/customer/appointment',
    "type": "GET",
    "table_id": "appointment_tab",
    "columns": [
        {"data": null, "title": "客户姓名", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "mobile", "title": "客户手机", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "expireBefore", "title": "客户车险到期日", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "licensePlateNo", "title": "车牌号", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "createTime", "title": "提交时间", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "处理状态", 'sClass': "text-center", "orderable": false, "sWidth": "30px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "60px"}
    ],
    init: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/customer/channels", {},
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                    });
                    $("#sourceChannel").append(options);
                }
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    //改变状态
    changeStatus: function (obj, appointmentInsuranceId) {
        var status = obj.val();
        popup.mould.popConfirmMould(false, "将更改处理状态，是否确认？", popup.mould.first, "", "55%",
            function () {
                popup.mask.hideFirstMask(false);
                customerList.changeAppointmentStatus(status, appointmentInsuranceId);
            },
            function () {
                popup.mask.hideFirstMask(false);
                obj.val(status == 2 ? 1 : 2);
                return false;
            }
        );
    },
    changeAppointmentStatus: function (status, appointmentInsuranceId) {
        common.ajax.getByAjax(true, 'put', 'json', '/orderCenter/customer/status',
            {
                status: status,
                appointmentInsuranceId: appointmentInsuranceId
            },
            function (data) {
                if (data.pass) {
                    popup.mould.popTipsMould(false, "更改处理状态成功！", popup.mould.second, popup.mould.success, "", "56%", null);
                } else {
                    popup.mould.popTipsMould(false, "更改处理状态失败！", popup.mould.second, popup.mould.error, "", "56%", null);
                }
                popup.mask.hideFirstMask(false);
            }, function () {
                popup.mould.popTipsMould(false, "操作失败！", popup.mould.second, popup.mould.error, "", "56%",
                    function () {
                        popup.mask.hideFirstMask(false);
                    }
                );
            }
        )
    }
};
var datatables = datatableUtil.getByDatatables(customerList, dataFunction.data, dataFunction.fnRowCallback);

$(function () {

    customerList.init();
    /* 搜索 */
    $("#searchBtn").unbind("click").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.validations.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            datatables.ajax.reload();
        }
    });
    $("#sourceChannel").unbind("change").bind({
        change: function () {
            datatables.ajax.reload();
        }
    });
});

$(document).ready(function() {
    $('#sourceChannel').select2();
});
