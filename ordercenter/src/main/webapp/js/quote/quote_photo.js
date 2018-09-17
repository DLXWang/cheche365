/**
 * Created by wangfei on 2015/10/19.
 */
var dataFunction = {
    "data": function (data) {
        data.disable = $("#disable").val();
        data.visited = $("#visited").val();
        data.channel = $("#sourceChannel").val();
        data.quoteEntrance = $('#quoteEntrance').val()
        switch (datatableUtil.params.keyType) {
            case "" :
                break;
            case "1":
                data.mobile = datatableUtil.params.keyWord;
                break;
            case "2":
                data.licensePlateNo = datatableUtil.params.keyWord;
                break;
        }
    },
    "fnRowCallback": function (nRow, aData) {
        $id = common.getOrderIcon(aData.channelIcon) + common.checkToEmpty(aData.id);
        $comment = "<a href=\"javascript:;\" onclick=\"applicationLog.popCommentList('quote_photo'," + aData.id + ",'first');\">查看备注</a>";
        if (aData.visited) {
            $operation = "<a href='javascript:;' onclick='listQuote.setVisited(" + aData.id + ",1)'><span style=\"color: red;\">需回访</span></a>"
                + "&nbsp;&nbsp;<a href='javascript:;' onclick='quote_photo_pop.quoteDetail.popup(" + aData.id + ");'>详细信息</a>";
        } else {
            $operation = "<a href='javascript:;' onclick='listQuote.setVisited(" + aData.id + ",0)'><span style=\"color: green;\">已回访</span></a>"
                + "&nbsp;&nbsp;<a href='javascript:;' onclick='quote_photo_pop.quoteDetail.popup(" + aData.id + ");'>详细信息</a>";
        }
        $('td:eq(0)', nRow).html($id);
        $('td:eq(9)', nRow).html($comment);
        $('td:eq(10)', nRow).html($operation);
    }
};
var listQuote = {
    "url": '/orderCenter/quote/photo/list',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": null, "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "userId", "title": "用户ID", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "encyptMobile", "title": "用户手机号", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "licensePlateNo", "title": "车牌号", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "userImg", "title": "上传图片数", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "quoteEntrance", "title": "来源", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "createTime", "title": "提交时间", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
        {
            "data": "disable",
            "title": "有效状态",
            "sClass": "text-center",
            "orderable": false,
            "sWidth": "60px",
            "render": function(data) {
                return common.checkToEmpty(quote_help.getdisableState(data));
            }},
        {
            "data": "visited",
            "title": "回访状态",
            "sClass": "text-center",
            "sWidth": "60px",
            "orderable": false,
            "render": function(data) {
                return common.checkToEmpty(quote_help.getVisitState(data));
            }},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "180px"}
    ],
    quoteContentUrl:"quote_photo_pop.html",
    initChannels: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/quote/photo/channels", {},
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
    getIdentityTypes:function(id1,id2){
        common.getByAjax(false, "get", "json", "/orderCenter/resource/identityTypes",{},
            function(data){
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<option value='"+model.id+"'>" + model.description + "</option>";
                    });
                    parent.$("#" + id1).append(options);
                    parent.$("#" + id2).append(options);
                }
            },
            function(){
                popup.mould.popTipsMould( "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    setVisited: function (id, visited) {
        common.getByAjax(false, "put", "json", "/orderCenter/quote/photo/visited/" + id, {visited: visited},
            function (data) {
                if (data.result == 'success') {
                    datatables.ajax.reload();
                } else {
                    popup.mould.popTipsMould(false, "设置拍照报价回访有效状态异常！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            },
            function () {
                popup.mould.popTipsMould(false, "设置拍照报价回访有效状态异常！", popup.mould.second, popup.mould.error, "", "57%", null);
            }
        );
    },
    setDisable: function (id, disable, callBackMethod) {
        common.getByAjax(true, "put", "json", "/orderCenter/quote/photo/disable/" + id, {disable: disable},
            function (data) {
                if (callBackMethod) {
                    callBackMethod(data);
                }
            },
            function () {
                popup.mould.popTipsMould(false, "设置拍照报价有效状态异常！", popup.mould.second, popup.mould.error, "", "57%", null);
            }
        );
    },
    addComment: function (logType, logTable, objId) {
        applicationLog.popCommentList(logType, logTable, objId, popup.mould.first, function () {
            orderList.interface.getOrderOperationInfoById(orderOperationInfoId, function (data) {
                $("#item_" + orderOperationInfoId).empty().append(orderList.list.fixOneItem(data));
            });
        });
    }
};

var datatables = datatableUtil.getByDatatables(listQuote, dataFunction.data, dataFunction.fnRowCallback);

$(function () {
    listQuote.initChannels();
    /* 搜索 */
    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            datatableUtil.params.keyWord = keyword;
            datatableUtil.params.keyType = $("#keyType").val();
            datatables.ajax.reload();
        }
    });
    $("#disable, #visited, #sourceChannel, #quoteEntrance").unbind("change").bind({
        change: function () {
            datatables.ajax.reload();
        }
    });
});


$(document).ready(function() {
    $('#sourceChannel').select2();
});
