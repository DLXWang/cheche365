/**
 * Created by lyh on 2015/10/15.
 */

var dataFunction = {
    "data": function (data) {
        data.keyword = datatableUtil.params.keyWord;
        data.keyType = datatableUtil.params.keyType;
    },
    "fnRowCallback": function (nRow, aData) {
        $templetNo = (aData.zucpCode == "" ? "" : "漫道：" + aData.zucpCode + "<br>") + (aData.yxtCode == "" ? "" : "盈信通：" + common.checkToEmpty(aData.yxtCode));
        $filterUserName = (aData.mobile ? common.checkToEmpty(aData.mobile) : common.checkToEmpty(aData.filterUserName));
        if (aData.statusId == 1) {
            $status = "<div style=\"color:#F4A460\">" + "<span>等待审核</span>" + "<br>" + "<span><" + (aData.sendFlag == 0 ? "立即发送" : aData.sendTime) + "></span>" + "<div/>";
        } else if (aData.statusId == 2) {
            $status = "<div style=\"color:red\">" + "<span>审核失败</span>" + "<br>" + "<span><" + (aData.sendFlag == 0 ? "立即发送" : aData.sendTime) + "></span>" + "<div/>";
        } else if (aData.statusId == 3) {
            $status = "<div style=\"color:blue\">" + "<span >等待发送</span>" + "<br>" + "<span><" + (aData.sendFlag == 0 ? "立即发送" : aData.sendTime) + "></span>" + "<div/>";
        } else if (aData.statusId == 4) {
            $status = "<div style=\"color:green\">" + "<span>发送成功</span>" + "<br>" + "<br>" + "<span><" + (aData.sendFlag == 0 ? "立即发送" : aData.sendTime) + "></span>" + "<div/>";
        } else if (aData.statusId == 5) {
            $status = "<div style=\"color:red\">" + "<span>发送失败</span>" + "<br>" + "<span><" + (aData.sendFlag == 0 ? "立即发送" : aData.sendTime) + "></span>" + "<div/>";
        } else {
            $status = "<div style=\"color:green\">" + "<span><span/>" + "<div/>";
        }
        $send = "<span>已发送：" + (aData.sentCount == 0 ? 0 : common.checkToEmpty(aData.sentCount)) + "</span>" + "<br>" +
            "<span>总发送：" + (aData.totalCount == 0 ? 0 : common.checkToEmpty(aData.totalCount)) + "</span>" + "<br>" +
            "<span>送达率：" + (aData.sendRate == 0 ? "0%" : common.checkToEmpty(aData.sendRate)) + "</span>";
        $comment = "<span " + "title='" + ((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, '\n')) + "'>" +
            common.getFormatComment((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, ''), 20) + "</span>";
        $updateTime = common.checkToEmpty(aData.operator) + "<br>" + (aData.updateTime ? common.checkToEmpty(aData.updateTime) : common.checkToEmpty(aData.createTime));
        if (aData.statusId == 1) {
            $operator = "<a style=\"color:green\" href=\"javascript:;\" onclick=\"adhocMessage.reviewSms.reviewSuccess(" + aData.id + ",'success');\">审核通过</a>" + "<br>" +
                "<a style=\"color:red\" href=\"javascript:;\" onclick=\"adhocMessage.reviewSms.reviewFail(" + aData.id + ",'fail');\">审核失败</a>" + "<br>" +
                "<a href=\"javascript:;\" onclick=\"adhocMessage.editAdhocMessage.popEdit(" + aData.id + ");\">编辑</a>";
        } else if (aData.statusId == 2) {
            $operator = "<a style='text-decoration:none; color:#8B8B7A'>审核通过</a>" + "<br>" + "<a style='text-decoration:none; color:#8B8B7A'>审核失败</a>" + "<br>" + "<a href=\"javascript:;\" onclick=\"adhocMessage.editAdhocMessage.popEdit(" + aData.id + ");\">编辑</a>";
        } else {
            $operator = "<a style='text-decoration:none; color:#8B8B7A'>审核通过</a>" + "<br>" + "<a style='text-decoration:none; color:#8B8B7A'>审核失败</a>" + "<br>" + "<a style='text-decoration:none; color:#8B8B7A' href=\"javascript:;\" >编辑</a>" + "<div>";
        }
        $('td:eq(1)', nRow).html($templetNo);
        $('td:eq(3)', nRow).html($filterUserName);
        $('td:eq(4)', nRow).html($status);
        $('td:eq(5)', nRow).html($send);
        $('td:eq(6)', nRow).html($comment);
        $('td:eq(7)', nRow).html($updateTime);
        $('td:eq(8)', nRow).html($operator);
    }
};

var adhocMessage = {
    "url": '/operationcenter/sms/adhoc',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": "id", "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "45px"},
        {"data": null, "title": "模板号", 'sClass': "text-center", "orderable": false, "sWidth": "163px"},
        {"data": "smsContentView", "title": "短信内容", 'sClass': "text-center", "orderable": false, "sWidth": "476px"},
        {"data": null, "title": "发送用户手机号", 'sClass': "text-center", "orderable": false, "sWidth": "163px"},
        {"data": null, "title": "发送状态", 'sClass': "text-center", "orderable": false, "sWidth": "163px"},
        {"data": null, "title": "发送数据", 'sClass': "text-center", "orderable": false, "sWidth": "123px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "163px"},
        {"data": null, "title": "最后编辑", 'sClass': "text-center", "orderable": false, "sWidth": "163px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "84px"}
    ],
    configValidation: {
        variableFlag: true,
        variableType: "",
        validate: function () {
            if (!$("#templateName").val()) {
                adhocMessage.configValidation.showErrors("请选择短信模板！");
                return;
            } else {
                adhocMessage.configValidation.variableFlag = true;
            }
            if ($("#sendUser").val() == 2) {
                if (!common.isTelphone($("#singleUser").val())) {
                    adhocMessage.configValidation.showErrors("请填写正确的手机号码！");
                    return;
                }
            } else {
                adhocMessage.configValidation.variableFlag = true;
            }
            if (!$('input:radio[name="sendFlag"]:checked').val()) {
                adhocMessage.configValidation.showErrors("请选择发送时间！");
                return;
            } else if ($('input:radio[name="sendFlag"]:checked').val() == 1) {
                if (!$("#userTime").val()) {
                    adhocMessage.configValidation.showErrors("请选择定时发送时间！");
                    return;
                } else {
                    adhocMessage.configValidation.variableFlag = true;
                }
            } else {
                adhocMessage.configValidation.variableFlag = true;
            }
            $("#variable input").each(function (i) {
                if (!$(this).val()) {
                    adhocMessage.configValidation.showErrors($(this).attr("label") + "信息不能为空！");
                }
            });
            if (!common.isEmpty($("#comment").val()) && common.isSingleQuote($("#comment").val())) {
                adhocMessage.configValidation.showErrors("备注中不允许输入单引号！");
                return;
            }
            return adhocMessage.configValidation.variableFlag;
        },
        showErrors: function (errorText) {
            $(".error-msg").show();
            $("#errorText").text(errorText);
            adhocMessage.configValidation.variableFlag = false;
        }
    },
    initAdhocMessage: {
        init: function () {
            adhocMessage.initAdhocMessage.initPopupContent();
            adhocMessage.initAdhocMessage.initTemplateContent();
            adhocMessage.initAdhocMessage.initPreviewContent();
        },
        initPopupContent: function () {
            var popupContent = $("#new_content");
            if (popupContent.length > 0) {
                adhocMessage.newAdhocMessage.content = popupContent.html();
                popupContent.remove();
            }
        },
        initTemplateContent: function () {
            var templateContent = $("#template_div");
            if (templateContent.length > 0) {
                adhocMessage.listTemplate.content = templateContent.html();
                templateContent.remove();
            }
        },
        initPreviewContent: function () {
            var previewContent = $("#preview_content_div");
            if (previewContent.length > 0) {
                adhocMessage.previewContent.content = previewContent.html();
                previewContent.remove();
            }
        },
        filterUser: function () {
            common.getByAjax(false, "get", "json", "/operationcenter/resource/filterUsers", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                        });
                        $("#filterUserList").append(options);
                        adhocMessage.initAdhocMessage.filterUserCount(data[0].id);
                    }
                },
                function () {
                }
            );
        },
        filterUserCount: function (filterUserId) {
            common.getByAjax(false, "get", "json", "/operationcenter/resource/" + filterUserId + "/filterUsersCount", {},
                function (data) {
                    if (data) {
                        $("#userCount").html(data);
                    }
                },
                function () {
                }
            );
        }
        ,
        addSmsContentHtml: function (smsTemplateId) {
            adhocMessage.messageVariable.getMessageVariableHtml(smsTemplateId);
        },
        initPageContent: function () {
            $("#new_smsContent_close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }
            });

            $("#templateName").unbind("click").bind({
                click: function () {
                    adhocMessage.listTemplate.searchTemplate();
                }
            });

            $("#toCreate").unbind("click").bind({
                click: function () {
                    adhocMessage.previewContent.jumpToView();
                }
            });

            $("#userTime1").unbind("click").bind({
                click: function () {
                    $("#userTime").val("");
                }
            });

            $("#userTime").unbind("click").bind({
                click: function () {
                    $("#userTime1").prop("checked", false);
                    $("#userTime2").prop("checked", true);
                }
            });

            $("#sendUser").unbind("change").bind({
                change: function () {
                    if (this.value == 2) {
                        $("#userTime1").prop("checked", true);
                        $("#userTime2").prop("checked", false);
                        $("#singleUserDiv").show();
                        $("#filterUserListDiv").hide();
                        $("#immediateDiv").show();
                        $("#timedDiv").hide();
                        $("#filterUserList").val(null);
                        $("#userCountDiv").hide();
                    } else {
                        $("#singleUserDiv").hide();
                        $("#filterUserListDiv").show();
                        $("#immediateDiv").show();
                        $("#timedDiv").show();
                        $("#singleUser").val(null);
                        $("#userCountDiv").show();
                    }
                }
            });
            $("#filterUserList").unbind("change").bind({
                change: function () {
                    adhocMessage.initAdhocMessage.filterUserCount(this.value);
                }
            });
        }
    },
    listTemplate: {
        content: "",
        properties: new Properties(1, ""),
        list: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/smsTemplate",
                {
                    currentPage: adhocMessage.listTemplate.properties.currentPage,
                    pageSize: 5
                },
                function (data) {
                    $("#template_list_tab tbody").empty();

                    if (data.pageInfo.totalElements < 1) {
                        $("#template_page_div").hide();
                        return false;
                    }
                    $("#pageUl").empty();
                    if (data.pageInfo.totalPage > 1) {
                        $.jqPaginator('#template_pagination',
                            {
                                totalPages: data.pageInfo.totalPage,
                                visiblePages: adhocMessage.listTemplate.properties.visiblePages,
                                currentPage: adhocMessage.listTemplate.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType == "change") {
                                        adhocMessage.listTemplate.properties.currentPage = pageNum;
                                        adhocMessage.listTemplate.list(adhocMessage.listTemplate.properties);
                                    }
                                }
                            }
                        );
                        $("#template_page_div").show();
                    } else {
                        $("#template_page_div").hide();
                    }
                    common.scrollToTop();
                    adhocMessage.listTemplate.fillTabContent(data);

                },
                function () {
                    popup.mould.popTipsMould("获取信息模板列表失败！", popup.mould.first, popup.mould.error, "", "56%", null);
                }
            )
        },
        fillTabContent: function (data) {
            var content = "";
            $.each(data.viewList, function (i, model) {
                content += "<tr class=\"text-center\">" +
                    "<td>" + model.id + "</td>" +
                    "<td style=\"max-width:200px;word-wrap:break-word;\">" + (model.zucpCode == "" ? "" : "漫道：" + model.zucpCode + "<br>") + (model.yxtCode == "" ? "" : "盈信通：" + common.checkToEmpty(model.yxtCode)) + "</td>" +
                    "<td class='vertical-middle' style=\"max-width: 100px;word-wrap:break-word;\">" + common.checkToEmpty(model.name) + "</td>" +
                    "<td class='vertical-middle' style=\"max-width: 100px;word-wrap:break-word;\">" + common.checkToEmpty(model.content) + "</td>" +
                    "<td><input value='选择' type='submit' class='btn btn-success' onclick='adhocMessage.listTemplate.selectTemplate(" + model.id + ",\"" + model.name + "\")'></td>" +
                    "</tr>";
            });
            $("#template_list_tab tbody").html(content);
        },
        searchTemplate: function () {
            adhocMessage.initAdhocMessage.initTemplateContent();
            popup.pop.popInput(adhocMessage.listTemplate.content, popup.mould.second, "900px", "600px", "36%", "50%");
            $("#ls_template_close").unbind("click").bind({
                click: function () {
                    popup.mask.hideSecondMask();
                }
            });
            adhocMessage.listTemplate.list();
        },
        selectTemplate: function (id, name) {
            adhocMessage.initAdhocMessage.addSmsContentHtml(id);
            $("#smsTemplateId").val(id);
            $("#templateName").val(name);
            popup.mask.hideSecondMask();
        }
    },
    newAdhocMessage: {
        content: "",
        popInput: function () {
            adhocMessage.initAdhocMessage.initPopupContent();
            popup.pop.popInput(adhocMessage.newAdhocMessage.content, popup.mould.first, "660px", "544px", "36%", "59%");
            adhocMessage.initAdhocMessage.filterUser();
            adhocMessage.initAdhocMessage.initPageContent();
        },
        saveAdhocMessage: function () {
            $("#toSave").attr("disabled", true);
            common.getByAjax(true, "post", "json", "/operationcenter/sms/adhoc", $("#new_form").serialize(),
                function (data) {
                    $("#toSave").attr("disabled", false);
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("新建主动短信成功！", popup.mould.first, popup.mould.success, "", "57%",
                            function () {
                                popup.mask.hideFirstMask();
                                datatableUtil.params.keyWord = "";
                                datatables.ajax.reload();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    $("#toSave").attr("disabled", false);
                    popup.mould.popTipsMould("新建主动短信失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    editAdhocMessage: {
        popEdit: function (id) {
            if (!common.permission.validUserPermission("op030504")) {
                return;
            }
            adhocMessage.configValidation.variableType = "edit";
            common.getByAjax(true, "get", "json", "/operationcenter/sms/adhoc/" + id, {},
                function (data) {
                    adhocMessage.initAdhocMessage.initPopupContent();
                    popup.pop.popInput(adhocMessage.newAdhocMessage.content, popup.mould.first, "660px", "554px", "36%", "59%");
                    adhocMessage.initAdhocMessage.filterUser();
                    adhocMessage.messageVariable.getMessageVariableHtml(data.smsTemplateId, data.parameter);
                    adhocMessage.initAdhocMessage.initPageContent();
                    $("#popover_normal_input #conditions_title").text("编辑主动发送短信");
                    if (data.sendFlag == 0) {
                        $("#userTime1").prop("checked", true);
                    } else {
                        $("#userTime2").prop("checked", true);
                        $("#userTime").val(data.sendTime);
                    }
                    if (data.mobile) {
                        $("#sendUser").val(2);
                        $("#singleUser").val(data.mobile);
                        $("#singleUserDiv").show();
                        $("#filterUserListDiv").hide();
                        $("#immediateDiv").show();
                        $("#timedDiv").hide();

                    } else {
                        $("#sendUser").val(1);
                    }
                    $("#filterUserList").val(data.filterUserId);
                    $("#templateName").val(data.smsTemplateName);
                    $("#templateSel").val(data.smsTemplateId);
                    $("#content").val(data.smsContent);
                    $("#comment").val(data.comment.replace(/\\r\\n/g, ''));
                    $("#smsContentView").val(data.smsContentView);
                    $("#smsTemplateId").val(data.smsTemplateId);
                    $("#id").val(data.id);
                },
                function () {
                    popup.mould.popTipsMould("获取信息失败，请重新再试！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        updateAdhocMessage: function (form) {
            $("#toCreate").attr("disabled", true);
            common.getByAjax(true, "put", "json", "/operationcenter/sms/adhoc/" + $("#id").val(), $("#new_form").serialize(),
                function (data) {
                    $("#toCreate").attr("disabled", false);
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("更新成功！", popup.mould.first, popup.mould.success, "", "57%",
                            function () {
                                popup.mask.hideFirstMask();
                                datatables.ajax.reload();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    $("#toCreate").attr("disabled", false);
                    popup.mould.popTipsMould("更新主动发送短信失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    previewContent: {
        content: "",
        jumpToView: function () {//弹出短信预览
            if (!adhocMessage.configValidation.validate()) {
                return;
            }
            var smsContentView = $("#content").val();
            var parameter = '';
            $("#variable input").each(function (i) {
                smsContentView = smsContentView.replace($("#content").html().match(/\$\{((\w*).?(\w*))}/g)[i], $(this).val());
                parameter += $(this).val() + ',';
            });
            parameter = parameter.substring(0, parameter.length - 1);
            $("#parameter").val(parameter);
            popup.pop.popInput(adhocMessage.previewContent.content, popup.mould.second, "500px", "360px", "45%", "61%");
            $("#toFix").unbind("click").bind({
                click: function () {
                    popup.mask.hideSecondMask();
                }
            });
            if (adhocMessage.configValidation.variableType == "edit") {
                $("#preview_title").text("编辑短信预览");
            }
            if ($("#sendUser").val() == 2) {
                $("#previewUser").text($("#singleUser").val());
                $("#toSave").val("立即发送");
            } else {
                $("#previewUser").text($('#filterUserList').find('option:selected').text());
            }
            $("#previewContent").val(smsContentView);
            $("#toSave").unbind("click").bind({
                click: function () {
                    if ($("#id").val() > 0) {
                        adhocMessage.editAdhocMessage.updateAdhocMessage();
                    } else {
                        adhocMessage.newAdhocMessage.saveAdhocMessage();
                    }
                }
            });
        }
    },
    reviewSms: {
        reviewSuccess: function (id, previewStatus) {
            if (!common.permission.validUserPermission("op030502")) {
                return;
            }
            adhocMessage.reviewSms.review(id, previewStatus);
        },
        reviewFail: function (id, previewStatus) {
            if (!common.permission.validUserPermission("op030503")) {
                return;
            }
            adhocMessage.reviewSms.review(id, previewStatus);
        },
        review: function (id, previewStatus) {
            common.getByAjax(true, "put", "json", "/operationcenter/sms/adhoc/" + id + "/" + previewStatus, {},
                function (data) {
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        if (previewStatus == 'success') {
                            data.message = "审核通过操作成功";
                        } else {
                            data.message = "审核失败操作成功";
                        }
                        popup.mould.popTipsMould(data.message, popup.mould.first, popup.mould.success, "", "57%",
                            function () {
                                popup.mask.hideFirstMask();
                                datatableUtil.params.keyWord = "";
                                datatables.ajax.reload();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                    }
                },
                function () {
                    $("#toCreate").attr("disabled", false);
                    popup.mould.popTipsMould("操作失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    messageVariable: {
        content: "",
        getMessageVariableHtml: function (smsTemplateId, parameter) {
            $("#variable").empty();
            common.getByAjax(true, "get", "json", "/operationcenter/sms/adhoc/smsContentHtml", {smsTemplateId: smsTemplateId},
                function (data) {
                    if (data) {
                        var variableHtml = "";
                        var contentHtml = data.smsContent;
                        $.each(data.variable, function (i, messageVariable) {
                            contentHtml = contentHtml.replace(contentHtml.match(/\$\{((\w*).?(\w*))}/g)[i], "<font color='red'>" + contentHtml.match(/\$\{((\w*).?(\w*))}/g)[i] + "</font>");
                            switch (messageVariable.type) {
                                case "number":
                                    variableHtml += "<div class='form-group'><span class='col-sm-7 text-height-28 text-right'>" + messageVariable.name + " 【 <font color='red'>" + messageVariable.code + " </font>】：</span><div class='col-sm-4 text-left'><input class='form-control text-input-220' style='resize: none;' placeholder='" + (messageVariable.placeholder ? messageVariable.placeholder : "") + "'maxlength='" + messageVariable.length + "'label='" + messageVariable.name + "'></div></div>";
                                    break;
                                case "time":
                                    variableHtml += "<div class='form-group'><span class='col-sm-7 text-height-28 text-right'>" + messageVariable.name + " 【 <font color='red'>" + messageVariable.code + " </font>】：</span><div class='col-sm-4 text-left'><input type='text' class='form-control text-height-28 Wdate' onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});\" readonly placeholder='" + (messageVariable.placeholder ? messageVariable.placeholder : "") + "'label='" + messageVariable.name + "'></div></div>";
                                    break;
                                case "date":
                                    variableHtml += "<div class='form-group'><span class='col-sm-7 text-height-28 text-right'>" + messageVariable.name + " 【 <font color='red'>" + messageVariable.code + " </font>】：</span><div class='col-sm-4 text-left'><input type='text' class='form-control text-height-28 Wdate' onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd'});\" readonly placeholder='" + (messageVariable.placeholder ? messageVariable.placeholder : "") + "'label='" + messageVariable.name + "'></div></div>";
                                    break;
                                default:
                                    variableHtml += "<div class='form-group'><span class='col-sm-7 text-height-28 text-right'>" + messageVariable.name + " 【 <font color='red'>" + messageVariable.code + " </font>】：</span><div class='col-sm-4 text-left'><input class='form-control text-input-220' style='resize: none;' placeholder='" + (messageVariable.placeholder ? messageVariable.placeholder : "") + "'maxlength='" + messageVariable.length + "'label='" + messageVariable.name + "'></div></div>";
                            }
                        });
                        $("#variable").append(variableHtml);
                        $("#content").val(data.smsContent);
                        $("#content").html(contentHtml);
                        $("#smsContentView").val(data.smsContentView);
                        if (parameter) {
                            var arr = Array();
                            arr = parameter.split(',');
                            $("#variable input").each(function (i) {
                                $(this).val(arr[i]);
                            });
                        }
                    }
                },
                function () {
                }
            );
        }
    }

};

var datatables = datatableUtil.getByDatatables(adhocMessage, dataFunction.data, dataFunction.fnRowCallback);

$(function () {

    if (!common.permission.validUserPermission("op0305")) {
        return;
    }
    adhocMessage.initAdhocMessage.init();
    /**
     * 新建主动短信
     */
    $("#toNew").bind({
        click: function () {
            if (!common.permission.validUserPermission("op030501")) {
                return;
            }
            adhocMessage.newAdhocMessage.popInput();
        }
    });

    /**
     * 搜索
     */
    $("#searchBtn").bind({
        click: function () {
            if (!common.permission.validUserPermission("op0305")) {
                return;
            }
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould("请输入搜索内容", "first", "warning", "", "", null);
                return false;
            }
            datatableUtil.params.keyWord = keyword;
            datatableUtil.params.keyType = $("#keyType").val();
            datatables.ajax.reload();
        }
    });
});
