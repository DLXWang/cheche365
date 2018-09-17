/**
 * Created by lyh on 2015/10/12.
 */
var dataFunction = {
    "data": function (data) {
        data.keyword = datatableUtil.params.keyWord;
        data.keyType = datatableUtil.params.keyType;
    },
    "fnRowCallback": function (nRow, aData) {
        $templetNo = (aData.zucpCode == "" ? "" : "漫道：" + aData.zucpCode + "<br>") + (aData.yxtCode == "" ? "" : "盈信通：" + common.checkToEmpty(aData.yxtCode));
        $disable = "<span id='conditions_status_id_" + aData.id + "' style=\"color: " + (aData.disable == 0 ? "green" : "red") + "\">" + (aData.disable == 0 ? "已启用" : "已禁用") + "</span>";
        $comment = "<span id='comment_id_" + aData.id + "' " +
            "title='" + ((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, '\n')) + "'>" +
            common.getFormatComment((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, ''), 20) +
            "</span>";
        $operation = "<a class='" + (aData.disable ? "" : "none") + "'  id='disable_conditions_status_action_id_" + aData.id + "' href=\"javascript:;\" onclick=\"conditions.editConditions.switchStatus(" + aData.id + "," + 0 + ");\" style=\"color:green\"> 启用 </a>" +
            "<a class='" + (aData.disable ? "none" : "") + "'  id='enable_conditions_status_action_id_" + aData.id + "'href=\"javascript:;\" onclick=\"conditions.editConditions.switchStatus(" + aData.id + "," + 1 + ");\" style=\"color:red\"> 禁用 </a>" +
            "<a style=\"padding-left: 15px;\" href=\"javascript:;\" onclick=\"conditions.editConditions.popEdit(" + aData.id + ");\">编辑</a>";
        $('td:eq(1)', nRow).html($templetNo);
        $('td:eq(6)', nRow).html($disable);
        $('td:eq(7)', nRow).html($comment);
        $('td:eq(8)', nRow).html($operation);
    }
};
var conditions = {
    "url": '/operationcenter/sms/schedule',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": "id", "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "45px"},
        {"data": null, "title": "模板号", 'sClass': "text-center", "orderable": false, "sWidth": "163px"},
        {"data": "smsTemplateName", "title": "模板名", 'sClass': "text-center", "orderable": false, "sWidth": "241px"},
        {"data": "content", "title": "短信内容", 'sClass': "text-center", "orderable": false, "sWidth": "476px"},
        {"data": "conditionName", "title": "触发条件", 'sClass': "text-center", "orderable": false, "sWidth": "241px"},
        {"data": "operator", "title": "创建人", 'sClass': "text-center", "orderable": false, "sWidth": "84px"},
        {"data": null, "title": "状态", 'sClass': "text-center", "orderable": false, "sWidth": "84px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "84px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "84px"}
    ],
    initConditions: {
        init: function () {
            conditions.initConditions.initPopupContent
            conditions.initConditions.initTemplateContent();
            conditions.initConditions.triggerConditions();
        },
        initPopupContent: function () {
            var popupContent = $("#new_content");
            if (popupContent.length > 0) {
                conditions.newConditions.content = popupContent.html();
                popupContent.remove();
            }
        },
        initTemplateContent: function () {
            var templateContent = $("#template_div");
            if (templateContent.length > 0) {
                conditions.listTemplate.content = templateContent.html();
                templateContent.remove();
            }
        },
        triggerConditions: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/scheduleCondition", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            options += "<option value=\"" + model.id + "\">" + model.condition + "</option>";
                        });
                        $("#conditionsSel").append(options);
                    }
                },
                function () {
                }
            );
        }
    },
    listTemplate: {
        content: "",
        properties: new Properties(1, ""),
        list: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/smsTemplate",
                {
                    currentPage: conditions.listTemplate.properties.currentPage,
                    pageSize: 5,
                    keyword: conditions.listTemplate.properties.keyword,
                    keyType: "1"
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
                                visiblePages: conditions.listTemplate.properties.visiblePages,
                                currentPage: conditions.listTemplate.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType == "change") {
                                        conditions.listTemplate.properties.currentPage = pageNum;
                                        conditions.listTemplate.list(conditions.listTemplate.properties);

                                    }
                                    window.scrollTo(0, 0);
                                }
                            }
                        );
                        $("#template_page_div").show();
                    } else {
                        $("#template_page_div").hide();
                    }

                    conditions.listTemplate.fillTabContent(data);
                    common.scrollToTop();
                },
                function () {
                    popup.mould.popTipsMould("获取条件短信列表失败！", popup.mould.first, popup.mould.error, "", "56%", null);
                }
            )
        },
        fillTabContent: function (data) {
            var content = "";
            $.each(data.viewList, function (i, model) {
                content += "<tr class=\"text-center\">" +
                    "<td>" + model.id + "</td>" +
                    "<td style=\"max-width:200px;word-wrap:break-word;\" zucpCode='" + common.checkToEmpty(model.zucpCode) + "' yxtCode='" + common.checkToEmpty(model.yxtCode) + "'>" + (model.zucpCode == "" ? "" : "漫道：" + model.zucpCode + "<br>") + (model.yxtCode == "" ? "" : "盈信通：" + common.checkToEmpty(model.yxtCode)) + "</td>" +
                    "<td class='vertical-middle'  style=\"max-width: 100px;word-wrap:break-word;\">" + common.checkToEmpty(model.name) + "</td>" +
                    "<td class='vertical-middle'  style=\"max-width: 100px;word-wrap:break-word;\">" + common.checkToEmpty(model.content) + "</td>" +
                    "<td><input value='选择' type='submit' class='btn btn-success' onclick='conditions.listTemplate.selectTemplate($(this).parent().parent())'></td>" +
                    "</tr>";
            });
            $("#template_list_tab tbody").html(content);

        },
        searchTemplate: function () {
            conditions.initConditions.initTemplateContent();
            popup.pop.popInput(conditions.listTemplate.content, popup.mould.second, "900px", "600px", "36%", "50%");
            $("#ls_template_close").unbind("click").bind({
                click: function () {
                    popup.mask.hideSecondMask();
                }
            });
            conditions.listTemplate.list();
        },
        selectTemplate: function (tr) {
            $("#templateId").val(tr.children().eq(0).text());
            $("#mdTemplateNo").text(tr.children().eq(1).attr('zucpCode'));
            $("#yxtTemplateNo").text(common.checkToEmpty(tr.children().eq(1).attr('yxtCode')));
            $("#templateSel").val(tr.children().eq(2).text());
            $("#content").val(tr.children().eq(3).text());
            popup.mask.hideSecondMask();
        }
    },

    newConditions: {
        content: "",
        popInput: function () {
            conditions.initConditions.initPopupContent();
            popup.pop.popInput(conditions.newConditions.content, popup.mould.first, "520px", "554px", "36%", "59%");
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }
            });
            $("#templateSel").unbind("click").bind({
                click: function () {
                    conditions.listTemplate.searchTemplate();
                }
            });
            $("#toCreate").unbind("click").bind({
                click: function () {
                    if ($("#id").val() == "" || isNaN($("#id").val())) {
                        conditions.newConditions.saveConditions();
                    }
                    else {
                        conditions.editConditions.updateConditions();
                    }
                }
            });
            $("#conditionsSel").unbind("change").bind({
                change: function () {
                    $("#conditionId").val(this.value);
                }
            });
        },
        saveConditions: function () {
            $("#toCreate").attr("disabled", true);
            var new_comment = $("#comment").val();
            if (!common.isEmpty(new_comment) && common.isSingleQuote(new_comment)) {
                popup.mould.popTipsMould("不允许输入单引号！", "second", "error", "", "55%",
                    function () {
                        popup.mask.hideSecondMask();
                    });
                $("#toCreate").attr("disabled", false);
                return;
            }
            common.getByAjax(true, "post", "json", "/operationcenter/sms/schedule", $("#new_form").serialize(),
                function (data) {
                    $("#toCreate").attr("disabled", false);
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("新建条件触发短信成功！", popup.mould.first, popup.mould.success, "", "57%",
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
                    $("#toCreate").attr("disabled", false);
                    popup.mould.popTipsMould("新建条件触发短信失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    editConditions: {
        popEdit: function (id) {
            if (!common.permission.validUserPermission("op030303")) {
                return;
            }
            common.getByAjax(true, "get", "json", "/operationcenter/sms/schedule/" + id, {},
                function (data) {
                    conditions.newConditions.popInput();
                    $("#popover_normal_input #conditions_title").text("编辑条件触发短信");
                    $("#toCreate").val("更新");
                    $("#id").val(id);
                    $("#templateId").val(data.smsTemplateId);
                    $("#templateSel").val(data.smsTemplateName);
                    $("#conditionId").val(data.conditionId);
                    $("#mdTemplateNo").text(data.zucpCode);
                    $("#yxtTemplateNo").text(data.yxtCode);
                    $("#content").val(data.content);
                    $("#conditionsSel").val(data.conditionId);
                    $("#conditionsSel").attr("disabled", true);
                    $("#content").attr("disabled", true);
                    $("#comment").val(data.comment.replace(/\\r\\n/g, ''));
                },
                function () {
                    popup.mould.popTipsMould("获取信息失败，请重新再试！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        updateConditions: function (form) {
            $("#toCreate").attr("disabled", true);
            var new_comment = $("#comment").val();
            if (!common.isEmpty(new_comment) && common.isSingleQuote(new_comment)) {
                popup.mould.popTipsMould("不允许输入单引号！", "second", "error", "", "55%",
                    function () {
                        popup.mask.hideSecondMask();
                    });
                $("#toCreate").attr("disabled", false);
                return;
            }
            common.getByAjax(true, "put", "json", "/operationcenter/sms/schedule/" + $("#id").val(), $("#new_form").serialize(),
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
                    popup.mould.popTipsMould("更新条件触发短信失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },
        switchStatus: function (scheduleMessageId, status) {
            if (!common.permission.validUserPermission("op030302")) {
                return;
            }
            common.getByAjax(true, "put", "json", "/operationcenter/sms/schedule/" + scheduleMessageId + "/" + status, {},
                function (data) {
                    if (data.pass) {
                        if (status == 0) {
                            $("#conditions_status_id_" + scheduleMessageId).css({'color': 'green'}).html("已启用");
                            $("#disable_conditions_status_action_id_" + scheduleMessageId).hide();
                            $("#enable_conditions_status_action_id_" + scheduleMessageId).show();
                        } else {
                            $("#conditions_status_id_" + scheduleMessageId).css({'color': 'red'}).html("已禁用");
                            $("#disable_conditions_status_action_id_" + scheduleMessageId).show();
                            $("#enable_conditions_status_action_id_" + scheduleMessageId).hide();
                        }
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        }
    }
};

var datatables = datatableUtil.getByDatatables(conditions, dataFunction.data, dataFunction.fnRowCallback);

$(function () {
    if (!common.permission.validUserPermission("op0303")) {
        return;
    }
    conditions.initConditions.init();
    /**
     * 新建条件触发短信
     */
    $("#toNew").bind({
        click: function () {
            if (!common.permission.validUserPermission("op030301")) {
                return;
            }
            conditions.newConditions.popInput();
        }
    });

    /**
     * 搜索
     */
    $("#searchBtn").bind({
        click: function () {
            if (!common.permission.validUserPermission("op0303")) {
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
