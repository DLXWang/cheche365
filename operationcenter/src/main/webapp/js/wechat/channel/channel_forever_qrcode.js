/**
 * Created by wangfei on 2015/7/28.
 */
var dataFunction = {
    "data": function (data) {
        data.keyword = datatableUtil.params.keyWord;
        data.keyType = datatableUtil.params.keyType;
        data.qrCodeType = QR_LIMIT_SCENE;
    },
    "fnRowCallback": function (nRow, aData) {
        $id = "<a href='javascript:;' onclick='getDetail(" + aData.id + ")'>" + common.checkToEmpty(aData.code) + "</a>";
        $comment = "<span title='" + ((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, '\n')) + "'>" +
            common.getFormatComment((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, ''), 40) +
            "</span>";
        $operator = "<a href='javascript:;' onclick='downloadForeverQrcode(" + aData.id + ")' >下载二维码<a>";
        $('td:eq(0)', nRow).html($id);
        $('td:eq(8)', nRow).html($comment);
        $('td:eq(9)', nRow).html($operator);
    }
};

var qrCodeChannelList = {
    "url": '/operationcenter/qcchannels/forever',
    "type": "GET",
    "table_id": "channel_tab",
    "columns": [
        {"data": null, "title": "渠道号", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "name", "title": "渠道名", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "department", "title": "所属部门", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "scanCount", "title": "扫描数", 'sClass': "text-center", "orderable": false, "sWidth": "30px"},
        {"data": "subscribeCount", "title": "关注数", 'sClass': "text-center", "orderable": false, "sWidth": "30px"},
        {"data": "bindingMobileCount", "title": "绑定手机数", 'sClass': "text-center", "orderable": false, "sWidth": "30px"},
        {"data": "successOrderCount", "title": "成交订单数", 'sClass': "text-center", "orderable": false, "sWidth": "30px"},
        {"data": "rebate", "title": "返点金额（元）", 'sClass': "text-center", "orderable": false, "sWidth": "30px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "60px"}
    ]
};

var datatables = datatableUtil.getByDatatables(qrCodeChannelList, dataFunction.data, dataFunction.fnRowCallback);

$(function () {
    var properties = new Properties(1, "");

    if (!common.permission.validUserPermission("op0202")) {
        return;
    }

    $("#searchBtn").unbind("click").bind({
        click: function () {
            if (!common.permission.validUserPermission("op0202")) {
                return;
            }
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould("请输入搜索内容", popup.mould.first, popup.mould.warning, "", "",
                    function () {
                        popup.mask.hideFirstMask(false);
                    }
                );
                return false;
            }
            datatableUtil.params.keyWord = keyword;
            datatableUtil.params.keyType = $("#keyType").val();
            datatables.ajax.reload();
        }
    });

    $("#keyType").change(function () {
        $("#keyword").attr("placeholder", this[this.selectedIndex].innerText);
    });

    $("#export_excel").unbind("click").bind({
        click: function () {
            if (!common.permission.validUserPermission("op020201")) {
                return;
            }
            conditionsMap.clear();
            conditionsMap.put("keyType", $("#keyType").val());
            conditionsMap.put("keyword", $("#keyword").val());
            conditionsMap.put("currentPage", 1);
            conditionsMap.put("pageSize", properties.pageSize);
            conditionsMap.put("qrCodeType", QR_LIMIT_SCENE);
            qrCodeChannel.export_excel(conditionsMap, QR_LIMIT_SCENE);
        }
    });

    var validOptions_new = {
        onkeyup: false,
        onfocusout: false,
        rules: {
            name: {
                required: true,
                maxlength: 20
            },
            department: {
                required: true,
                maxlength: 20
            },
            expireTime: {
                required: true
            },
            rebate: {
                required: true,
                number: true
            },
            newCount: {
                required: true,
                min: 1,
                max: 999
            },
            comment: {
                maxlength: 200
            }
        },
        messages: {
            name: {
                required: "请输入渠道名",
                maxlength: "渠道名最多可输入20字"
            },
            department: {
                required: "请输入所属部门",
                maxlength: "所属部门最多可输入20字"
            },
            expireTime: {
                required: "请选择到期时间"
            },
            rebate: {
                required: "请输入返点金额",
                number: "请输入正确的返点金额"
            },
            newCount: {
                required: "请输入新建数量",
                min: "新建数量至少为1",
                max: "新建数量最多为999"
            },
            comment: {
                maxlength: "备注最多可输入200字"
            }
        },
        showErrors: function (errorMap, errorList) {
            if (errorList.length) {
                var errorText = $("#errorText");
                errorText.text(errorList[0].message);
                errorText.parent().parent().show();
            }
        },
        submitHandler: function (form) {
            var errorText = $("#errorText");
            errorText.parent().parent().hide();
            var newCount = $("#newCount").val();
            if (newCount > 1) {
                if (!common.permission.validUserPermissionForSecondPup("op020203")) {
                    return;
                }
                var newChannelNameLength = $("#name").val().length + newCount.length;
                if (newChannelNameLength > 20) {
                    errorText.html("批量新建后的渠道名将超过20字，请修改渠道名或新建数量");
                    errorText.parent().parent().show();
                    return;
                }
                var newChannelDepartmentLength = $("#department").val().length + newCount.length;
                if (newChannelDepartmentLength > 20) {
                    errorText.html("批量新建后的所属部门内容将超过20字，请修改所属部门内容或新建数量");
                    errorText.parent().parent().show();
                    return;
                }
                popup.mould.popConfirmMould("将批量新建" + newCount + "个永久二维码，确定这样做吗？", popup.mould.second, "", "55%",
                    function () {
                        popup.mask.hideSecondMask(false);
                        qrCodeChannel.save(form, true);
                    },
                    function () {
                        popup.mask.hideSecondMask(false);
                        return false;
                    }
                );
            } else {
                if (!common.permission.validUserPermissionForSecondPup("op020202")) {
                    return;
                }
                qrCodeChannel.save(form);
            }
        }
    };

    $("#yj_new").unbind("click").bind({
        click: function () {
            if (!common.permission.validUserPermission("op020202")) {
                return;
            }
            common.getByAjax(true, "get", "html", "/operationcenter/qcchannels/ids/" + QR_LIMIT_SCENE, {},
                function (data) {
                    new_forver.initPopupContent();
                    popup.pop.popInput(new_forver.new_content, popup.mould.first, "490px", "650px", "40%");
                    $("#channelNoText").text(data);
                    $("#code").val(data);
                    $("#qrCodeSpan").text(data);
                    $("#newCount").numeral();
                    $("#minus").unbind("click").bind({
                        click: function () {
                            var count = $("#newCount");
                            if (common.isEmpty(count.val())) {
                                count.val(1);
                                return;
                            }
                            if (count.val() <= 1) {
                                return;
                            } else {
                                count.val(parseInt(count.val()) - 1);
                            }
                        }
                    });
                    $("#plus").unbind("click").bind({
                        click: function () {
                            var count = $("#newCount");
                            if (common.isEmpty(count.val())) {
                                count.val(1);
                                return;
                            }
                            if (count.val() >= 999) {
                                return;
                            } else {
                                count.val(parseInt(count.val()) + 1);
                            }
                        }
                    });
                    $("#yj_new_qrcode_close").unbind("click").bind({
                        click: function () {
                            popup.mould.popConfirmMould("正在新建永久二维码 " + $("#code").val() + "，确定放弃?", popup.mould.second,
                                ["继续新建", "放弃"], null,
                                function () {
                                    popup.mask.hideSecondMask(false);
                                },
                                function () {
                                    popup.mask.hideAllMask(false);
                                }
                            );
                        }
                    });
                    //判断是否有下载权限
                    //var permissions = common.permission.getPermissionCodeArray();
                    //if (permissions.indexOf("op020204") < 0) {//没有下载权限禁用checkbox
                    //    $("#downLoadFlag").attr("checked",false);
                    //    $("#downLoadFlag").attr("disabled","disabled");
                    //}
                    $("#yj_qrcode_new_form").validate(validOptions_new);
                },
                function () {
                    popup.mould.popTipsMould("获取渠道码失败，请重试", popup.mould.first, popup.mould.error, "", "",
                        function () {
                            popup.mask.hideFirstMask(false);
                        }
                    );
                }
            );
        }
    });
});


var new_forver = {
    new_content: '',
    initPopupContent: function () {
        var popupContent = $("#yj_qrcode_new");
        if (popupContent.length > 0) {
            edit_forver.initPopupContent();
            new_forver.new_content = popupContent.html() + edit_forver.edit_content;
            popupContent.remove();
        }
    }
};

var edit_forver = {
    edit_content: '',
    initPopupContent: function () {
        var popupContent = $("#yj_new_qrcode_content");
        if (popupContent.length > 0) {
            edit_forver.edit_content = popupContent.html();
            popupContent.remove();
        }
    }
};

var detail_forver = {
    detail_content: '',
    initPopupContent: function () {
        var popupContent = $("#yj_detail_qrcode");
        if (popupContent.length > 0) {
            detail_forver.detail_content = popupContent.html();
            popupContent.remove();
        }
    }
};

var getDetail = function (id) {
    if (!common.permission.validUserPermission("op020205")) {
        return;
    }
    common.getByAjax(true, "get", "json", "/operationcenter/qcchannels/forever/" + QR_LIMIT_SCENE + "/" + id, {},
        function (data) {
            detail_forver.initPopupContent();
            popup.pop.popInput(detail_forver.detail_content, popup.mould.first, "530px", "515px", "45%");
            $("#detail_title").text($("#detail_title").html() + " " + data.code);
            $("#detail_code").text(data.code);
            $("#detail_name").text(common.checkToEmpty(data.name));
            if (common.getLength(common.checkToEmpty(data.name)) >= 26) {
                $("#detail_name").attr("title", common.checkToEmpty(data.name));
            }
            $("#detail_scan_count").text(data.scanCount);
            $("#detail_subscribe_count").text(data.subscribeCount);
            if (null != data.department && data.department.length > 0) {
                $("#detail_department").text(data.department);
            } else {
                $("#detail_department").html("&nbsp;");
            }
            $("#detail_bindingMobile_count").text(data.bindingMobileCount);
            $("#detail_successOrder_count").text(data.successOrderCount);
            $("#detail_rebate").text(data.rebate);
            $("#detail_comment").val((data.comment == null) ? "" : data.comment.replace(/\\r\\n/g, '\n'));
            $("#qrCodeImg").attr("src", "/operationcenter/qcchannels/img/" + QR_LIMIT_SCENE + "/" + id);
            $("#qrCodeImg_download").attr("onclick", "downloadForeverQrcode(" + id + ");");
            $("#yj_detail_qrcode_close").unbind("click").bind({
                click: function () {
                    popup.mask.hideAllMask(false);
                }
            });
            $("#export_count_excel").unbind("click").bind({
                click: function () {
                    if (!common.permission.validUserPermissionForSecondPup("op020206")) {
                        return;
                    }
                    qrCodeChannel.export_count_excel(QR_LIMIT_SCENE, $("#detail_code").text());
                }
            });
            $("#channel_forever_qrcode_edit").unbind("click").bind({
                click: function () {
                    if (!common.permission.validUserPermissionForSecondPup("op020207")) {
                        return;
                    }


                    edit_forver.initPopupContent();
                    $("#yj_detail_qrcode_content").html(edit_forver.edit_content);
                    $("#yj_detail_qrcode_content").find("#channelNoText").text(data.code);
                    $("#yj_detail_qrcode_content").find("#code").val(data.code);
                    $("#yj_detail_qrcode_content").find("#name").val(data.name);
                    $("#yj_detail_qrcode_content").find("#department").val(data.department);
                    $("#yj_detail_qrcode_content").find("#rebate").val(data.rebate);


                    $("#yj_detail_qrcode_content").find("#comment").val((data.comment == null) ? "" : data.comment.replace(/\\r\\n/g, '\n'));
                    $("#qrcodeImgDiv").remove();
                    $("#yj_detail_qrcode_content").find("#newCountGroup").remove();
                    $("#yj_detail_qrcode_content").find("#remarkGroup").remove();
                    $("#yj_detail_qrcode_content").find("#downloadGroup").remove();
                    var btnText = "<div class=\"text-center\">" +
                        "<input id=\"updateChannelByNew\" type=\"submit\" class=\"btn btn-danger text-input-200\" style=\"margin: -15px 5px;\" value=\"更新渠道号并保存\">" +
                        "<input id=\"updateChannel\" type=\"submit\" class=\"btn btn-danger\" value=\"保存\">" +
                        "</div>";
                    $("#yj_detail_qrcode_content").find("#btnGroup").html(btnText);
                    var options = {
                        onkeyup: false,
                        onfocusout: false,
                        rules: {
                            name: {
                                required: true,
                                maxlength: 20
                            },
                            department: {
                                required: true,
                                maxlength: 20
                            },
                            rebate: {
                                required: true,
                                number: true
                            },
                            newCount: {
                                required: true,
                                min: 1,
                                max: 999
                            },
                            comment: {
                                maxlength: 200
                            }
                        },
                        messages: {
                            name: {
                                required: "请输入渠道名",
                                maxlength: "渠道名最多可输入20字"
                            },
                            department: {
                                required: "请输入所属部门",
                                maxlength: "所属部门最多可输入20字"
                            },
                            rebate: {
                                required: "请输入返点金额",
                                number: "请输入正确的返点金额"
                            },
                            newCount: {
                                required: "请输入新建数量",
                                min: "新建数量至少为1",
                                max: "新建数量最多为999"
                            },
                            comment: {
                                maxlength: "备注最多可输入200字"
                            }
                        },
                        showErrors: function (errorMap, errorList) {
                            if (errorList.length) {
                                var errorText = $("#errorText");
                                errorText.text(errorList[0].message);
                                errorText.parent().parent().show();
                            }
                        },
                        submitHandler: function (form) {
                            var errorText = $("#errorText");
                            errorText.parent().parent().hide();

                            var new_comment = $("#comment").val();
                            if (!common.isEmpty(new_comment) && common.isSingleQuote(new_comment)) {
                                errorText.text("备注不允许输入单引号");
                                errorText.parent().parent().show();
                                $("#toCreate").attr("disabled", false);
                                return;
                            }

                            var updateFlag = $("#yj_detail_qrcode_content").find("#yj_qrcode_new_form").find("#updateFlag").val();
                            if (updateFlag == "1") {
                                if (!common.permission.validUserPermissionForSecondPup("op020208")) {
                                    return;
                                }
                                popup.mould.popConfirmMould("更新渠道号后将作为一个新渠道二维码使用，过去数据将不被保存到新渠道二维码中，确定这样做吗？", popup.mould.second, "", "56%",
                                    function () {
                                        popup.mask.hideSecondMask(false);
                                        qrCodeChannel.edit(form, id, updateFlag);
                                    },
                                    function () {
                                        popup.mask.hideSecondMask(false);
                                    }
                                );
                                $("#popover_normal_confirm_second").height("240px");
                            } else {
                                if (!common.permission.validUserPermissionForSecondPup("op020207")) {
                                    return;
                                }
                                qrCodeChannel.edit(form, id, updateFlag);
                            }
                        }
                    };
                    $("#yj_detail_qrcode_content").find("#updateChannelByNew").unbind("click").bind({
                        click: function () {
                            $("#yj_detail_qrcode_content").find("#yj_qrcode_new_form").find("#updateFlag").val("1");
                            $("#yj_detail_qrcode_content").find("#yj_qrcode_new_form").validate(options);
                        }
                    });
                    $("#yj_detail_qrcode_content").find("#updateChannel").unbind("click").bind({
                        click: function () {
                            $("#yj_detail_qrcode_content").find("#yj_qrcode_new_form").find("#updateFlag").val("2");
                            $("#yj_detail_qrcode_content").find("#yj_qrcode_new_form").validate(options);
                        }
                    });
                }
            });
        },
        function () {
            popup.mould.popTipsMould("获取二维码详情失败", popup.mould.first, popup.mould.error, "", "",
                function () {
                    popup.mask.hideFirstMask(false);
                }
            );
        }
    );
};

var downloadForeverQrcode = function (id) {

    window.location.href = "/operationcenter/qcchannels/" + id + "/" + QR_LIMIT_SCENE + "/download";
};
