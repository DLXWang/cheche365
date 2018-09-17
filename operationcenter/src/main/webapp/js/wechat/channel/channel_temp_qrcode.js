/**
 * Created by wangfei on 2015/7/28.
 */
var dataFunction = {
    "data": function (data) {
        data.keyword = datatableUtil.params.keyWord;
        data.keyType = datatableUtil.params.keyType;
        data.qrCodeType = QR_SCENE;
    },
    "fnRowCallback": function (nRow, aData) {
        $id = "<a href='javascript:;' onclick='getDetail(" + aData.id + ")'>" + common.checkToEmpty(aData.code) + "</a>";
        if (aData.status == '有效') {
            $status = "<span style='color:green'>" + common.checkToEmpty(aData.status) + "</span>";
        } else {
            $status = "<span style='color:red'>" + common.checkToEmpty(aData.status) + "</span>";
        }
        $comment = "<span title='" + ((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, '\n')) + "'>" +
            common.getFormatComment((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, ''), 40) +
            "</span>";
        if (aData.status == '有效') {
            //content += "<td><a href='/operationcenter/qcchannels/" + aData.id + "/" + QR_SCENE + "/download' >下载二维码<a></td>";
            $operator = "<a href='javascript:;' onclick='downloadTempQrcode(" + aData.id + ")' >下载二维码<a>";
        } else {
            $operator = "<span>下载二维码</span>";
        }
        $('td:eq(0)', nRow).html($id);
        $('td:eq(3)', nRow).html($status);
        $('td:eq(10)', nRow).html($comment);
        $('td:eq(11)', nRow).html($operator);
    }
};

var qrCodeChannelList = {
    "url": '/operationcenter/qcchannels/temp',
    "type": "GET",
    "table_id": "channel_tab",
    "columns": [
        {"data": null, "title": "渠道号", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "name", "title": "渠道名", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "department", "title": "所属部门", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "有效状态", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "expireTime", "title": "到期时间", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "scanCount", "title": "扫描数", 'sClass': "text-center", "orderable": false, "sWidth": "40px"},
        {"data": "subscribeCount", "title": "关注数", 'sClass': "text-center", "orderable": false, "sWidth": "40px"},
        {"data": "bindingMobileCount", "title": "绑定手机数", 'sClass': "text-center", "orderable": false, "sWidth": "40px"},
        {"data": "successOrderCount", "title": "成交订单数", 'sClass': "text-center", "orderable": false, "sWidth": "40px"},
        {"data": "rebate", "title": "返点金额（元）", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "60px"}
    ]
};

var datatables = datatableUtil.getByDatatables(qrCodeChannelList, dataFunction.data, dataFunction.fnRowCallback);

$(function () {
    var properties = new Properties(1, "");
    var conditionHtml = $("#conditionDiv").html();

    if (!common.permission.validUserPermission("op0201")) {
        return;
    }

    $("#searchBtn").unbind("click").bind({
        click: function () {
            if (!common.permission.validUserPermission("op0201")) {
                return;
            }
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould("请输入搜索内容", popup.mould.first, "warning", "", "",
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
        if (this.value == 003) {
            $("#conditionDiv").empty();
            $("#conditionDiv").html("<input id='expireTimeShow' name='expireTimeShow' type='text' " +
                "class='form-control' placeholder='到期时间' " +
                "onfocus=\"WdatePicker({dateFmt:'yyyy年MM年dd日 HH时',vel:'keyword',realDateFmt:'yyyy-MM-dd',realTimeFmt:'HH:mm:ss',minDate:'%y-%M-%d {%H+1}',maxDate:'%y-%M-{%d+7} {%H+1}'});\" readonly='readonly'>" +
                "<input id='keyword' name='keyword' type='hidden' value=''>" +
                "<span class='input-group-btn'><button id='searchBtn' class='btn btn-default' type='button'>搜索</button></span>");
        } else {
            $("#conditionDiv").empty();
            $("#conditionDiv").html(conditionHtml);
            $("#keyword").attr("placeholder", this[this.selectedIndex].innerText);
        }
        $("#searchBtn").unbind("click").bind({
            click: function () {
                var keyword = $("#keyword").val();
                if (common.isEmpty(keyword)) {
                    popup.mould.popTipsMould("请输入搜索内容", popup.mould.first, "warning", "", "",
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
    });

    $("#export_excel").unbind("click").bind({
        click: function () {
            if (!common.permission.validUserPermission("op020101")) {
                return;
            }
            conditionsMap.clear();
            conditionsMap.put("keyType", $("#keyType").val());
            conditionsMap.put("keyword", $("#keyword").val());
            conditionsMap.put("currentPage", 1);
            conditionsMap.put("pageSize", properties.pageSize);
            conditionsMap.put("qrCodeType", QR_SCENE);
            qrCodeChannel.export_excel(conditionsMap, QR_SCENE);
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
            expireTimeShow: {
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
            expireTimeShow: {
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
                if (!common.permission.validUserPermissionForSecondPup("op020103")) {
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
                popup.mould.popConfirmMould("将批量新建" + newCount + "个临时二维码，确定这样做吗？", popup.mould.second, "", "55%",
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
                qrCodeChannel.save(form);
            }
        }
    };

    $("#ls_new").unbind("click").bind({
        click: function () {
            //if (!common.permission.validUserPermission("op020102")) {//新建临时二维码不加权限控制
            //    return;
            //}
            common.getByAjax(true, "get", "html", "/operationcenter/qcchannels/ids/" + QR_SCENE, {},
                function (data) {
                    new_temp.initPopupContent();
                    popup.pop.popInput(new_temp.new_content, popup.mould.first, "490px", "650px", "40%");
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
                    $("#ls_new_qrcode_close").unbind("click").bind({
                        click: function () {
                            popup.mould.popConfirmMould("正在新建临时二维码 " + $("#code").val() + "，确定放弃?", popup.mould.second,
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
                    //if (permissions.indexOf("op020104") < 0) {//没有下载权限禁用checkbox
                    //    $("#downLoadFlag").attr("checked",false);
                    //    $("#downLoadFlag").attr("disabled","disabled");
                    //}
                    $("#ls_qrcode_new_form").validate(validOptions_new);
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

var new_temp = {
    new_content: '',
    initPopupContent: function () {
        var popupContent = $("#ls_new_qrcode");
        if (popupContent.length > 0) {
            new_temp.new_content = popupContent.html();
            popupContent.remove();
        }
    }
};

var detail_temp = {
    detail_content: '',
    initPopupContent: function () {
        var popupContent = $("#ls_detail_qrcode");
        if (popupContent.length > 0) {
            detail_temp.detail_content = popupContent.html();
            popupContent.remove();
        }
    }
};

var getDetail = function (id) {
    if (!common.permission.validUserPermission("op020105")) {
        return;
    }
    common.getByAjax(true, "get", "json", "/operationcenter/qcchannels/temp/" + QR_SCENE + "/" + id, {},
        function (data) {
            detail_temp.initPopupContent();
            popup.pop.popInput(detail_temp.detail_content, popup.mould.first, "530px", "570px", "40%");
            $("#detail_title").text($("#detail_title").html() + " " + data.code);
            $("#detail_code").text(data.code);
            $("#detail_name").text(common.checkToEmpty(data.name));
            if (common.getLength(common.checkToEmpty(data.name)) >= 26) {
                $("#detail_name").attr("title", common.checkToEmpty(data.name));
            }
            if (data.department == null || data.department.length == 0) {
                $("#detail_department").html("&nbsp;")
            } else {
                $("#detail_department").text(common.checkToEmpty(data.department));
            }
            if (common.getLength(common.checkToEmpty(data.department)) >= 26) {
                $("#detail_department").attr("title", common.checkToEmpty(data.department));
            }
            $("#detail_status").text(data.status);
            $("#detail_expire_time").text(data.expireTime);
            $("#detail_scan_count").text(data.scanCount);
            $("#detail_subscribe_count").text(data.subscribeCount);
            $("#detail_bindingMobile_count").text(data.bindingMobileCount);
            $("#detail_successOrder_count").text(data.successOrderCount);
            $("#detail_rebate").text(data.rebate);
            $("#detail_comment").val((data.comment == null) ? "" : data.comment.replace(/\\r\\n/g, '\n'));
            $("#qrCodeImg").attr("src", "/operationcenter/qcchannels/img/" + QR_SCENE + "/" + id);
            if (data.status == '有效') {
                $("#detail_status").css("color", "green");
                $("#qrCodeImg_download").attr("onclick", "downloadTempQrcode(" + id + ");");
            } else {
                $("#detail_status").css("color", "red");
                $("#qrCodeImg_download").hide();
            }
            $("#ls_detail_qrcode_close").unbind("click").bind({
                click: function () {
                    popup.mask.hideAllMask(false);
                }
            });
            $("#export_count_excel").unbind("click").bind({
                click: function () {
                    if (!common.permission.validUserPermissionForSecondPup("op020106")) {
                        return;
                    }
                    qrCodeChannel.export_count_excel(QR_SCENE, $("#detail_code").text());
                }
            });
        },
        function () {

        }
    );
};

var downloadTempQrcode = function (id) {
    //if (!common.permission.validUserPermissionForSecondPup("op020104")) {下载临时二维码不加权限
    //    return;
    //}
    window.location.href = "/operationcenter/qcchannels/" + id + "/" + QR_SCENE + "/download";
};
