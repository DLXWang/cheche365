var dataFunction = {
    "data": function (data) {
        data.partner = $('#partner').val();//合作商
        data.channel = $('#channelName').val();//第三方渠道名称
        data.apiPartner = $('#channelEngSel').val();//渠道英文名称
        data.landingPage = $('#landingPage').val();//落地页
        data.quoteWay = $('#quoteWay').val();//报价方式
        data.status = $('#status').val();//状态
    },
    "fnRowCallback": function (nRow, aData) {
        $enable = "";
        if (aData.status) {
            $enable = '<span style="color: green;">启用中</span>' + ' <a id="disable" download="disable" onclick="partner.chgStatus('+ aData.id +',false)"><button type="button" class="btn btn-danger disable">禁用</button> </a>';
        } else {
            $enable = '<span style="color: darkred;">禁用中</span>' + ' <a id="disable" download="disable" onclick="partner.chgStatus('+ aData.id +',true)"><button type="button" class="btn btn-warning enable">启用</button> </a>';
        }
        $('td:eq(5)', nRow).html($enable);
    },
}
var tocList = {
    "url": '/operationcenter/thirdParty/tocCooperate/partnerList',
    "type": "GET",
    "table_id": "partner_list",
    "columns": [
        {"data": "id", "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "name", "title": "合作商", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "enName", "title": "渠道英文简称", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "channelName", "title": "第三方渠道名称", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "landingPage", "title": "落地页", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        //{"data": "quoteWay", "title": "报价方式", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        //{"data": "productLink", "title": "生产环境链接", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": null, "title": "启用/禁用", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        // {
        //     "data": null,
        //     "title": "启用/禁用",
        //     render: function (data, type, row) {
        //         return '<span style="color: green;">启用中</span>' +  ' <a id="url_template" download="url_template" href=""><button id="downloadTemplate" type="button" class="btn btn-danger">禁用</button> </a>'
        //     },
        //     "className": "text-center",
        //     "orderable": false
        // },
        // {
        //     "data": "id",
        //     "title": "操作",
        //     render: function (data, type, row) {
        //         return"<a href='/views/thirdParty/toA_details.html?id="+ id +"' target='_blank'>查看详情</a>" ;
        //     },
        //     "className": "text-center",
        //     "orderable": false
        // },
        {
            "data": "id",
            "title": "编辑",
            render: function (data, type, row) {
                return " <a href='/views/thirdParty/toC_details.jsp?id=" + data + "&random="+Math.random()+"' target='_blank'>查看详情</a>";
            },
            "className": "text-center",
            "orderable": false
        },
    ],
}
var partner = {

    chgList: function () {
        /* 搜索 */
        $("#searchBtn").unbind("click").bind({
            click: function () {
                datatables.ajax.reload();
            }
        });
        $("#cancelBtn").unbind("click").bind({
            click: function () {
                // console.log("清除数据")
                $("#partner").val("");
                $("#channelName").val("");
                $("#channelEngSel").val("");
                $("#landingPage").val("");
                $("#quoteWay").val("");
                $("#status").val("");
                datatables.ajax.reload();
            }
        });
    },

    /* 合作商下拉列表查询 */
    partnerList: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/thirdParty/tocCooperate/partnerNameList", {},
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                    });
                    $("#partner").append(options);
                }
            },
            function () {
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    /* 第三方渠道名称下拉列表查询 */
    channelNameList: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/thirdParty/tocCooperate/channelNameList", {},
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                    });
                    $("#channelName").append(options);
                }
            },
            function () {
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    /* 渠道英文名称下拉列表查询 */
    channelEnNameList: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/thirdParty/tocCooperate/channelCodeList", {},
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value=\"" + model.id + "\">" + model.code + "</option>";
                    });
                    $("#channelEngSel").append(options);
                }
            },
            function () {
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    chgStatus: function(id,status){
        var str = status?"确认启用此第三方渠道合作内容吗？":"确认禁用此第三方渠道吗？禁用后此第三方渠道的生产环境链接将失效，你还要继续吗？";
        popup.mould.popConfirmMould(str, popup.mould.first, "", "",
            function () {
                common.getByAjax(true, "get", "json", "/operationcenter/thirdParty/tocCooperate/chgAble", {'id':id,'status':status},
                    function (data) {
                        popup.mask.hideFirstMask(false);
                        if (data.pass) {
                            datatables.ajax.reload();
                        } else {
                            popup.mould.popTipsMould("禁用出现异常！", popup.mould.second, popup.mould.error, "", "57%", null);
                        }
                    },
                    function () {
                        popup.mould.popTipsMould("禁用出现异常！", popup.mould.second, popup.mould.error, "", "57%", null);
                    }
                );
            },
            function () {
                popup.mask.hideFirstMask(false);
            }
        );
    },

}
var datatables = datatableUtil.getByDatatables(tocList, dataFunction.data, dataFunction.fnRowCallback);
$(function () {
    if (!common.permission.validUserPermission('op1102')) {
        return;
    }
    partner.partnerList();
    partner.chgList();
    partner.channelNameList();
    partner.channelEnNameList();

});
