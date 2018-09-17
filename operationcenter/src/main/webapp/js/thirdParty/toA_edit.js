var dataFunction = {
    "data": function (data) {
        data.partnerId = common.getUrlParam("id");
    },
    "fnRowCallback": function (nRow, aData) {
    },
}
var logList = {
    "url": '/operationcenter/partners/log',
    "type": "GET",
    "table_id": "log_list",
    "columns": [
        {"data": "operationTime", "title": "操作时间", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "operator", "title": "操作员", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "operationContent", "title": "操作内容", 'sClass': "text-center", "orderable": false, "sWidth": ""},
    ],
}
var channelDetail = {
    downloadUrl:function(){
        var id = common.getUrlParam("id");
        window.location.href="/operationcenter/thirdParty/tocCooperate/updateUrl?id="+id;
    },
    //展示详情
    overview: function (id) {
        common.getByAjax(true, "get", "json", "/operationcenter/thirdParty/tocCooperate/findDetailsInfo", {id: id},
            function (data) {
                //渠道详情
                $("#partnerName").text(data.partner);
                $("#channelName").text(data.channelName);
                $("#channelCode").text(data.channelCode);
                $("#butUoint").text(data.butUoint ? "是" : "否");
                $("#remark").text(data.remark);
                $("#id").text(data.id);
                // if (data.disabledChannel == true) {
                //     $("#disable").text("已禁用")
                // } else {
                //     $("#disable").text("已启用")
                // }
                // if(){
                //
                // }


            },
            function () {
                $("#add_form").html("系统异常");
                $("#toSave").hide();
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },
    initTemplateUrl: function (id) {
        common.getByAjax(true, "get", "json", "/operationcenter/thirdParty/tocCooperate/updateUrl", {id: id},
            function (response) {
                // $("#url_template").prop("href", response.message);
            }, function () {
                popup.mould.popTipsMould("模版地址初始化异常！！", popup.mould.first, popup.mould.error, "", "53%", null);
            });
    },
    //编辑
    editChannelConf: function (id) {
        $("#updateChannel").unbind("click").bind({
            click: function () {

            }
        });
    },
    init_switch: function () {

        $("#myTab a").click(function (e) {
            e.preventDefault();
            $(this).tab("show");
            var href = $(this).attr("href").replace("#", "");
            if ("config_info" == href) {
                $("#config_info").show();
                $("#operate_log").hide();
            } else {
                if (!datatables) {
                    datatables = datatableUtil.getByDatatables(logList, dataFunction.data, dataFunction.fnRowCallback);
                    $("#log_list_length").hide();
                }
                $("#log_list").attr('style', 'width:100%');
                $("#config_info").hide();
                $("#operate_log").show();
            }
        });
    }
}
var datatables;
$(function () {
    var id = common.getUrlParam("id");
    channelDetail.init_switch();
    channelDetail.overview(id);
    channelDetail.initTemplateUrl(id);
    channelDetail.editChannelConf(id);
});
