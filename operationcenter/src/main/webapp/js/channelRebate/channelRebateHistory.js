//@ sourceURL=channelRebateHistory.js
var channel_rebate_history = {
    param: {
        dataTable: null,
        channelRebateId: 0
    },
    dt_table_history: {
        dt_list: {
            url: "/operationcenter/channelRebate/history",
            type: "get",
            table_id: "history_pop_html",
            columns: [
                {"data": "id", "title": "ID", 'sClass': "text-center idtd", "orderable": false},
                {"data": "effectiveDate", "title": "生效时间", 'sClass': "text-center effectiveDatetd", "orderable": false},
                {"data": "expireDate", "title": "失效时间", 'sClass': "text-center expireDatetd", "orderable": false},
                {"data": "channelType", "title": "渠道类型", 'sClass': "text-center channelTypetd", "orderable": false},
                {"data": "channelName", "title": "渠道名称", 'sClass': "text-center channelNametd", "orderable": false},
                {"data": "areaName", "title": "地区", 'sClass': "text-center areaNametd", "orderable": false},
                {
                    "data": "insuranceCompanyName",
                    "title": "保险公司",
                    'sClass': "text-center insuranceCompanyNametd",
                    "orderable": false
                },
                {
                    "data": "onlyCommercialRebate",
                    "title": "单商业险",
                    defaultContent: '',
                    'sClass': "text-center onlyCommercialRebatetd",
                    "orderable": false
                },
                {
                    "data": "onlyCompulsoryRebate",
                    "title": "单交强险",
                    defaultContent: '',
                    'sClass': "text-center onlyCompulsoryRebatetd",
                    "orderable": false
                },
                {
                    "data": "commercialRebate",
                    "title": "组合商业险",
                    'sClass': "text-center commercialRebatetd",
                    "orderable": false
                },
                {
                    "data": "compulsoryRebate",
                    "title": "组合交强险",
                    'sClass': "text-center compulsoryRebatetd",
                    "orderable": false
                },
                {"data": "status", "title": "状态", 'sClass': "text-center statustd", "orderable": false}
            ]
        }
    },
};
$(function () {
    channel_rebate_history.param.dataTable = datatableUtil.getByDatatables(channel_rebate_history.dt_table_history.dt_list, function (data) {
        data.channelRebateId = channel_rebate.param.channelRebateId;
    }, function () {
    })
});