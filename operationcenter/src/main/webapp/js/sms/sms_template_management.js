/**
 * Created by wangfei on 2015/8/25.
 */
var dataFunction = {
    "data": function (data) {
        data.keyword = datatableUtil.params.keyWord;
        data.keyType = datatableUtil.params.keyType;
    },
    "fnRowCallback": function (nRow, aData) {
        $templetNo = (aData.zucpCode == "" ? "" : "漫道：" + aData.zucpCode + "<br>") + (aData.yxtCode == "" ? "" : "盈信通：" + common.checkToEmpty(aData.yxtCode));
        $disable = "<span id='smsTemplate_status_id_" + aData.id + "' style=\"color: " + (aData.disable == 0 ? "green" : "red") + "\">" + (aData.disable == 0 ? "已启用" : "已禁用") + "</span>";
        $comment = "<span id='comment_id_" + aData.id + "' " +
            "title='" + ((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, '\n')) + "'>" +
            common.getFormatComment((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, ''), 20) +
            "</span>";
        $operation = "<span class='" + (aData.disable == 0 ? "" : "none") + "' id='disable_smsTemplate_status_action_id_" + aData.id + "'><a  style='color: red' href='javascript:;' onclick=smsTemplate.common.switchStatus(" + aData.id + "," + 1 + ")>禁用</a></span>" +
            "<span class='" + (aData.disable == 0 ? "none" : "") + "' id='enable_smsTemplate_status_action_id_" + aData.id + "'><a  style='color: green' href='javascript:;' onclick=smsTemplate.common.switchStatus(" + aData.id + "," + 0 + ")>启用</a></span>" +
            "<span style='margin-left:20px;'><a href='javascript:;' onclick=smsTemplate.editSmsTemplate.editComment(" + aData.id + ");>修改备注</a></span>";
        $('td:eq(1)', nRow).html($templetNo);
        $('td:eq(4)', nRow).html($disable);
        $('td:eq(5)', nRow).html($comment);
        $('td:eq(6)', nRow).html($operation);
    }
};
var smsTemplate = {
    "url": '/operationcenter/sms/template',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": "id", "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "108px"},
        {"data": null, "title": "模板号", 'sClass': "text-center", "orderable": false, "sWidth": "205px"},
        {"data": "name", "title": "模板名", 'sClass': "text-center", "orderable": false, "sWidth": "205px"},
        {"data": "content", "title": "短信内容", 'sClass': "text-center", "orderable": false, "sWidth": "593px"},
        {"data": null, "title": "状态", 'sClass': "text-center", "orderable": false, "sWidth": "108px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "108px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "205px"}

    ],
    config_validation: {
        onkeyup: false,
        onfocusout: false,
        rules: {
            name: {
                required: true,
                maxlength: 30
            },
            zucpCode: {
                required: true,
                maxlength: 30
            },
            content: {
                required: true,
                maxlength: 1000
            }
        },
        messages: {
            name: {
                required: "请输入名称",
                maxlength: "短信模板名称最多可输入30位"
            },
            zucpCode: {
                required: "漫道模板号不能为空",
                maxlength: "漫道模板号最多可输入30位"
            },
            content: {
                required: "短信内容不能为空",
                maxlength: "短信内容最多可输入1000位"
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
            var errorText = parent.$("#errorText");
            errorText.parent().parent().hide();
            if ($("#smsTemplateId").val() == "0") {
                smsTemplate.newSmsTemplate.saveSmsTemplate(form);
            }
        }
    },
    initSmsTemplate: {
        init: function () {

        },

        initPopupContent: function () {
            var popupContent = $("#new_content");
            if (popupContent.length > 0) {
                smsTemplate.newSmsTemplate.content = popupContent.html();
                popupContent.remove();
            }
        },
        initVariable: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/messageVariables", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            options += "<option value=\"" + model.code + "\">" + model.name + "【" + model.code + "】" + "</option>";
                        });
                        $("#variable_multiselect").append(options);
                    }
                },
                function () {
                }
            );
        }
    },
    newSmsTemplate: {
        content: "",
        popInput: function () {
            smsTemplate.initSmsTemplate.initPopupContent();
            smsTemplate.initSmsTemplate.initVariable();
            popup.pop.popInput(smsTemplate.newSmsTemplate.content, popup.mould.first, "660px", "540px", "30%", "49%");
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }
            });

            $("#new_form").validate(smsTemplate.config_validation);

            /**
             * 双击变量选项
             */
            $("#variable_multiselect").unbind("dblclick").bind({
                dblclick: function () {
                    $("#variable_multiselect option:selected").each(function () {
                        smsTemplate.newSmsTemplate.insertVariable(this.value);
                    });
                }
            });

            /**
             * 单击插入按钮
             */
            $("#add_variable").unbind("click").bind({
                click: function () {
                    $("#variable_multiselect option:selected").each(function () {
                        smsTemplate.newSmsTemplate.insertVariable(this.value);
                    });
                }
            });
        },
        /**
         * 在光标处插入数据
         * @param value
         */
        insertVariable: function (value) {
            var contentTextArea = document.getElementById("content");
            if (document.selection) {//IE
                contentTextArea.focus();
                var range = document.selection.createRange();
                range.text = value;
                contentTextArea.focus();
            } else {//非IE浏览器
                var startPos = contentTextArea.selectionStart;
                var endPos = contentTextArea.selectionEnd;
                var scrollTop = contentTextArea.scrollTop;
                contentTextArea.value = contentTextArea.value.substring(0, startPos) + value + contentTextArea.value.substring(endPos, contentTextArea.value.length);
                contentTextArea.focus();
                contentTextArea.selectionStart = startPos + value.length;
                contentTextArea.selectionEnd = startPos + value.length;
                contentTextArea.scrollTop = scrollTop;
            }
        },
        saveSmsTemplate: function (form) {
            $("#toCreate").attr("disabled", true);
            if (!smsTemplate.common.validateYxtCode()) {
                return;
            }
            var new_comment = $("#comment").val();
            if (!common.isEmpty(new_comment) && common.isSingleQuote(new_comment)) {
                var errorText = $("#errorText");
                errorText.text("备注不允许输入单引号");
                errorText.parent().parent().show();
                $("#toCreate").attr("disabled", false);
                return;
            }
            common.getByAjax(true, "post", "json", "/operationcenter/sms/template", $(form).serialize(),
                function (data) {
                    $("#toCreate").attr("disabled", false);
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("新建短信模板成功！", popup.mould.first, popup.mould.success, "", "57%",
                            function () {
                                popup.mask.hideFirstMask();
                                datatableUtil.params.keyWord = keyword;
                                datatables.ajax.reload();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    $("#toCreate").attr("disabled", false);
                    popup.mould.popTipsMould("新建短信模板失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    editSmsTemplate: {
        editComment: function (smsTemplateId) {
            if (!common.permission.validUserPermission("op030103")) {
                return;
            }
            var comment = $("#comment_id_" + smsTemplateId).attr("title");
            var content = "<div class=\"theme_poptit\">" +
                "<a id=\"comment_close\" href=\"javascript:;\" title=\"关闭\" class=\"close\"><i class=\"glyphicon glyphicon-remove\"></i></a>" +
                "<h4 class=\"text-center\">修改备注&nbsp;</h4>" +
                "</div>" +
                "<div style=\"padding-top: 15px;padding-left: 25px;\">" +
                "<textarea id=\"comment_content\" style=\"width:400px;resize:none;vertical-align:middle;\" maxlength=\"2000\" rows=\"6\">" + comment + "</textarea>" +
                "</div>" +
                "<div id=\"btnGroup\" class=\"text-center\" style=\"padding-top: 30px;\">" +
                "<button id=\"comment_save\" class=\"btn btn-danger\">保存</button>" +
                "</div>";

            popup.pop.popInput(content, "first", "450px", "280px", "49%");
            window.parent.$("#comment_close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            window.parent.$("#comment_save").unbind("click").bind({
                click: function () {
                    var new_comment = window.parent.$("#comment_content").val();
                    if (!common.isEmpty(new_comment) && common.isSingleQuote(new_comment)) {
                        popup.mould.popTipsMould("不允许输入单引号！", popup.mould.second, popup.mould.warning, "", "55%",
                            function () {
                                popup.mask.hideSecondMask();
                            });
                        return false;
                    }
                    common.getByAjax(true, "put", "json", "/operationcenter/sms/template/comment",
                        {
                            smsTemplateId: smsTemplateId,
                            comment: new_comment
                        },
                        function (data) {
                            if (data.pass) {
                                popup.mask.hideAllMask();
                                popup.mould.popTipsMould("保存成功！", popup.mould.first, popup.mould.success, "", "57%",
                                    function () {
                                        popup.mask.hideFirstMask();
                                        datatables.ajax.reload();
                                    }
                                );
                            } else {
                                popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                            }
                        }, function () {
                            popup.mould.popTipsMould("操作失败", popup.mould.second, popup.mould.warning, "", "57%", null);
                        }
                    )
                }
            });
        }
    },
    common: {
        switchStatus: function (smsTemplateId, status) {
            if (!common.permission.validUserPermission("op030102")) {
                return;
            }
            common.getByAjax(true, "put", "json", "/operationcenter/sms/template/" + smsTemplateId + "/" + status, {},
                function (data) {
                    if (data.pass) {
                        if (status == 1) {
                            $("#smsTemplate_status_id_" + smsTemplateId).css({'color': 'red'}).html("已禁用");
                            $("#disable_smsTemplate_status_action_id_" + smsTemplateId).hide();
                            $("#enable_smsTemplate_status_action_id_" + smsTemplateId).show();
                        } else {
                            $("#smsTemplate_status_id_" + smsTemplateId).css({'color': 'green'}).html("已启用");
                            $("#disable_smsTemplate_status_action_id_" + smsTemplateId).show();
                            $("#enable_smsTemplate_status_action_id_" + smsTemplateId).hide();
                        }


                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        validateYxtCode: function () {
            var flag = true;
            var yxtCode = $("#yxtCode").val();
            var zucpCode = $("#zucpCode").val();
            if (!common.isEmpty(yxtCode) && zucpCode != yxtCode) {
                var errorText = $("#errorText");
                errorText.text("盈信通模板号必须和漫道模板号相同");
                errorText.parent().parent().show();
                $("#toCreate").attr("disabled", false);
                flag = false;
            }
            return flag;
        }
    }
};

var datatables = datatableUtil.getByDatatables(smsTemplate, dataFunction.data, dataFunction.fnRowCallback);

$(function () {
    smsTemplate.initSmsTemplate.init();

    /**
     * 新建
     */
    $("#toNew").bind({
        click: function () {
            if (!common.permission.validUserPermission("op030101")) {
                return;
            }
            smsTemplate.newSmsTemplate.popInput();
        }
    });

    /**
     * 搜索
     */
    $("#searchBtn").bind({
        click: function () {
            if (!common.permission.validUserPermission("op0301")) {
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
