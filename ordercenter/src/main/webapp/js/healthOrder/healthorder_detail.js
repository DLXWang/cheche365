/**
 * Created by cxy on 2016/12/26.
 */
var id = common.getUrlParam("id");
var dataFunction = {
    "data": function (data) {
        data.insurancePolicyId = id;
    },
    "fnRowCallback": function (nRow, aData) {
    },
    "fnDrawCallback": function (datatable) {
        var api = datatable.api();
        var startIndex = api.context[0]._iDisplayStart;//获取到本页开始的条数
        api.column(0).nodes().each(function (cell, i) {
            cell.innerHTML = startIndex + i + 1;
        });
    }
}
var orderList = {
    "url": '/orderCenter/healthOrder/paymentInfoDetail',
    "type": "POST",
    "table_id": "paymentTab",
    "columns": [
        {"data": null, "title": "序号", 'sClass': "text-center", "orderable": false},
        {
            "data": "amount",
            "title": "金额",
            "sClass": "text-center",
            "orderable": false,
            "render": function(data) {
                return common.checkToEmpty(data);
            }},
        {"data": "status", "title": "支付状态", 'sClass': "text-center", "orderable": false},
        {"data": "operateTime", "title": "操作时间", 'sClass': "text-center", "orderable": false},
        {"data": "channel", "title": "交付通道", 'sClass': "text-center", "orderable": false},
        {
            "data": "outTradeNo",
            "title": "车车流水号",
            "sClass": "text-center",
            "orderable": false,
            "render": function(data) {
                return common.checkToEmpty(data);
            }},
        {
            "data": "thirdpartyPaymentNo",
            "title": "支付平台流水号",
            "sClass": "text-center",
            "orderable": false,
            "render": function(data, type, row, meta) {
                return common.checkToEmpty(data);
            }},
        {"data": "paymentType", "title": "支付方式", 'sClass': "text-center", "orderable": false},
    ],
}
$(function () {
    detail.getDetail(id);
    dt_labels.bLengthChange = false;
    dt_labels.paging = false;
    dt_labels.info = false;
    datatableUtil.getByDatatables(orderList,dataFunction.data,dataFunction.fnRowCallback, dataFunction.fnDrawCallback);
});
var detail = {
    displayFlag: false,
    imageTypeContent: "",
    getDetail: function (id, no) {
        common.getByAjax(false, "get", "json", "/orderCenter/healthOrder/"+id, null,
            function (data) {
                if (data == null) {
                    common.showTips("获取订单详情失败");
                    return false;
                }
                detail.write(data, no, id);
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.second, popup.mould.error, "", "57%", null);
            }
        );
    },
    write: function (data, no, id) {
        /* 订单详情 */
        $("#insuranceProductName").text(common.tools.checkToEmpty(data.insuranceProduct.name));
        $("#orderNo").text(common.tools.checkToEmpty(data.orderNo));
        $("#insureStatus").text(common.tools.checkToEmpty(data.insureStatus));
        $("#premium").text(common.tools.checkToEmpty(data.premium));
        $("#insuranceCompName").text(common.tools.checkToEmpty(data.insuranceCompany.name));
        $("#policyNo").text(common.tools.checkToEmpty(data.policyNo));
        $("#effectiveDate").text(common.tools.checkToEmpty(data.effectiveDate));

        $("#expireDate").text(common.tools.checkToEmpty(data.expireDate));
        $("#amount").text(common.tools.checkToEmpty(data.amount));
        $("#waitingDays").text(common.tools.checkToEmpty(data.waitingDays));

        /* 被保险人信息 */
        $("#insuredName").text(common.tools.checkToEmpty(data.insurancePerson.name));
        $("#insuredIdentity").text(common.tools.checkToEmpty(data.insurancePerson.identity));
        if(data.insurancePerson.industry){
            $("#insuredIdentityType").text(common.tools.checkToEmpty(data.insurancePerson.industry.name));
        }
        $("#socialSecurity").text(common.tools.checkToEmpty(data.insurancePerson.socialSecurity)?"有":"无");
        /* 投保人信息 */
        $("#applicantName").text(common.tools.checkToEmpty(data.applicantPerson.name));
        $("#aapplicantdentityType").text(common.tools.checkToEmpty(data.applicantPerson.identity));
        $("#relation").text(common.tools.checkToEmpty(data.relation));
        $("#applicantMobile").text(common.tools.checkToEmpty(data.applicantPerson.mobile));

        if(!common.isEmpty(data.agentName)){
            $("#agentName").text(common.tools.checkToEmpty(data.agentName));
           // $("#cardNum").text(common.tools.checkToEmpty(data.cardNum));
           // $("#rebate").text(common.formatMoney(common.tools.checkToEmpty(data.rebate),2)+"%");
            $(".agent").show();
        }

    },
}

