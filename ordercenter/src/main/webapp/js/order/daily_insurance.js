$(function () {
    var id = common.getUrlParam("id");
    if (id == null) {
        popup.mould.popTipsMould(false, "异常参数", popup.mould.second, popup.mould.error, "", "57%", null);
        return false;
    }
    dailyInsurance.initTotal(id);
});

var dailyInsurance = {
    initTotal: function (id) {
        common.getByAjax(false, "get", "json", "/orderCenter/order/findTotalByPurchaseOrderId", {purchaseOrderId: id},
            function (data) {
                dailyInsurance.initDailyInsuranceTotalContent(data);
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    initDailyInsuranceTotalContent: function (data) {
        $("#totalStopDateNumber").text(common.isEmpty(data.totalStopDays) ? 0 : data.totalStopDays);
        $("#totalRestartDateNumber").text(common.isEmpty(data.totalRestartDays) ? 0 : data.totalRestartDays);
        $("#totalRefundAmount").text(common.tools.formatMoney(common.checkToEmpty(data.totalRefundAmount), 2));
        $("#totalRestartPaidAmount").text(common.tools.formatMoney(common.checkToEmpty(data.totalPaidAmount), 2));
        $("#currentState").text(common.checkToEmpty(data.status));
        $("#totalPremium").text(common.tools.formatMoney(data.premium, 2));
    }
};
var dataFunction = {
    "data": function (data) {
        data.orderId = common.getUrlParam("id");
    },
    "fnRowCallback": function (nRow, aData) {

        var $stopTime = aData.beginDate + "---" + aData.endDate + "<br>" + "(停驶" + aData.stopDays + "天)";
        var $restartTime = "";
        var $paidAmount = "";
        var $restartCreateTime = "";
        if (aData.dailyRestartViewModelList) {
            $.each(aData.dailyRestartViewModelList, function (index, model) {
                $restartTime += (model.beginDate == null ? "" : model.beginDate + "---" + model.endDate) + "<br>" + (model.restartDays == null ? "" : "(复驶" + model.restartDays + "天)") + "<br>";
                $paidAmount += ((model.paidAmount == null || model.paidAmount == 0) ? "" : common.tools.formatMoney(common.checkToEmpty(model.paidAmount), 2)) + "<br>";
                $restartCreateTime += model.createTime + "<br>";
            }
        );
}
$refundAmount = ((aData.refundAmount == null || aData.refundAmount == 0) ? "" : common.tools.formatMoney(common.checkToEmpty(aData.refundAmount), 2));
$insurancePackage = aData.insurancePackage;
$stopCreateTime = aData.createTime;

$('td:eq(0)', nRow).html($stopTime);
$('td:eq(1)', nRow).html($refundAmount);
$('td:eq(2)', nRow).html($restartTime);
$('td:eq(3)', nRow).html($paidAmount);
$('td:eq(4)', nRow).html($insurancePackage);
$('td:eq(5)', nRow).html($stopCreateTime);
$('td:eq(6)', nRow).html($restartCreateTime);
}
}
;
var dailyInsuranceList = {
    "url": '/orderCenter/order/findDailyInsuranceByOrderId',
    "type": "POST",
    "table_id": "daily_insurance_list_tab",
    "columns": [
        {"data": null, "title": "停驶时间", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
        {"data": null, "title": "返还金额", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "复驶时间", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
        {"data": null, "title": "再次支付金额", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "险种详情", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "操作停驶时间", 'sClass': "text-center", "orderable": false, "sWidth": "150px"},
        {"data": null, "title": "操作复驶时间", 'sClass': "text-center", "orderable": false, "sWidth": "150px"}
    ]
};
var datatables = datatableUtil.getByDatatables(dailyInsuranceList, dataFunction.data, dataFunction.fnRowCallback);


