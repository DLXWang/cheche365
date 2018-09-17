/**
 * Created by wangfei on 2015/8/25.
 */
var dataFunction = {
    "data": function (data) {
        data.keyword = datatableUtil.params.keyWord;
        data.keyType = datatableUtil.params.keyType;
    },
    "fnRowCallback": function (nRow, aData) {
        $disable = "<span id='filter_status_id_" + aData.id + "' style=\"color: " + (aData.disable == 0 ? "green" : "red") + "\">" + (aData.disable == 0 ? "已启用" : "已禁用") + "</span>";
        $comment = "<span title='" + ((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, '\n')) + "'>" +
            common.getFormatComment((aData.comment == null) ? "" : aData.comment.replace(/\\r\\n/g, ''), 20) +
            "</span>";
        $operation = "<span class='" + (aData.disable == 0 ? "" : "none") + "' id='disable_filter_status_action_id_" + aData.id + "'><a  style='color: red' href='javascript:;' onclick=filter.common.switchStatus(" + aData.id + "," + 1 + ")>禁用</a></span>" +
            "<span class='" + (aData.disable == 0 ? "none" : "") + "' id='enable_filter_status_action_id_" + aData.id + "'><a  style='color: green' href='javascript:;' onclick=filter.common.switchStatus(" + aData.id + "," + 0 + ")>启用</a></span>" +
            "<span style='margin-left:20px;'><a href='javascript:;' onclick=filter.editFilter.popEdit(" + aData.id + ");>编辑</a></span>";
        $('td:eq(3)', nRow).html($disable);
        $('td:eq(4)', nRow).html($comment);
        $('td:eq(6)', nRow).html($operation);
    }
};
var filter = {
    "url": '/operationcenter/sms/filterUser',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": "id", "title": "ID", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "name", "title": "名字", 'sClass': "text-center", "orderable": false, "sWidth": "242px"},
        {"data": "content", "title": "SQL语句", 'sClass': "text-center", "orderable": false, "sWidth": "600px"},
        {"data": null, "title": "状态", 'sClass': "text-center", "orderable": false, "sWidth": "109px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "207px"},
        {"data": "updateTime", "title": "更新时间", 'sClass': "text-center", "orderable": false, "sWidth": "207px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "109px"}
    ],
    config_validation: {
        onkeyup: false,
        onfocusout: false,
        rules: {
            name: {
                required: true,
                maxlength: 20
            },
            sqlTemplateId: {
                required: true
            }
        },
        messages: {
            name: {
                required: "请输入名称",
                maxlength: "筛选名称最多可输入20位"
            },
            sqlTemplateId: {
                required: "请选择Sql模板"
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
            if ($("#filterId").val() == "0") {
                filter.newFilter.saveFilter(form);
            } else {
                filter.editFilter.updateFilter(form);
            }
        }
    },
    initFilter: {
        init: function () {
            filter.initFilter.initSqlTemplate();
        },

        initPopupContent: function () {
            var popupContent = $("#new_content");
            if (popupContent.length > 0) {
                filter.newFilter.content = popupContent.html();
                popupContent.remove();
            }
        },
        initSqlTemplate: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/sqlTemplates", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                        });
                        $("#sqlTemplateSel").append(options);
                    }
                },
                function () {
                }
            );
        }
    },
    newFilter: {
        content: "",
        popInput: function () {
            filter.initFilter.initPopupContent();
            popup.pop.popInput(filter.newFilter.content, popup.mould.first, "600px", "554px", "36%", "59%");
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }
            });

            $("#new_form").validate(filter.config_validation);

            /**
             * 选择sql模板
             */
            $("#sqlTemplateSel").unbind("change").bind({
                change: function () {
                    var sqlTemplateId = $("#sqlTemplateSel").val();
                    $("#sqlParameter").html("");
                    $("#content").val("");
                    if (sqlTemplateId != "") {
                        filter.common.showParameter(sqlTemplateId);
                    }
                }
            });
        },

        saveFilter: function (form) {
            $("#toCreate").attr("disabled", true);
            //验证参数值
            if (!filter.common.validationParameter()) {
                $("#toCreate").attr("disabled", false);
                return;
            }
            //验证comment
            if (!filter.common.validationComment()) {
                $("#toCreate").attr("disabled", false);
                return;
            }
            filter.common.setParameter();
            common.getByAjax(true, "post", "json", "/operationcenter/sms/filterUser", $(form).serialize(),
                function (data) {
                    $("#toCreate").attr("disabled", false);
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("新建筛选成功！", popup.mould.first, popup.mould.success, "", "57%",
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
                    popup.mould.popTipsMould("新建筛选失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    editFilter: {
        popEdit: function (id) {
            if (!common.permission.validUserPermission("op030203")) {
                return;
            }
            common.getByAjax(true, "get", "json", "/operationcenter/sms/filterUser/" + id, {},
                function (data) {
                    filter.newFilter.popInput();
                    $("#popover_normal_input #filter_title").text("编辑筛选用户功能");
                    $("#toCreate").val("保存");
                    $("#popover_normal_input #filterId").val(data.id);
                    $("#popover_normal_input #name").val(common.checkToEmpty(data.name));
                    $("#popover_normal_input #sqlTemplateSel").val(data.sqlTemplateId);
                    $("#popover_normal_input #content").val(data.sqlTemplateViewModel.content);
                    var parameterStr = data.parameter;
                    var parameterArr = parameterStr.split("&");
                    filter.common.showParameter(data.sqlTemplateViewModel.id, parameterArr, true);
                    $("#popover_normal_input #comment").val(data.comment);
                },
                function () {
                    popup.mould.popTipsMould("获取筛选信息失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        updateFilter: function (form) {
            $("#toCreate").attr("disabled", true);
            if (!filter.common.validationParameter()) {
                $("#toCreate").attr("disabled", false);
                return;
            }
            //验证comment
            if (!filter.common.validationComment()) {
                $("#toCreate").attr("disabled", false);
                return;
            }

            filter.common.setParameter();
            common.getByAjax(true, "put", "json", "/operationcenter/sms/filterUser/" + $("#filterId").val(), $(form).serialize(),
                function (data) {
                    $("#toCreate").attr("disabled", false);
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
                },
                function () {
                    $("#toCreate").attr("disabled", false);
                    popup.mould.popTipsMould("保存筛选信息失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    common: {
        //显示参数
        showParameter: function (sqlTemplateId, parameterArr, flag) {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/sqlTemplate/" + sqlTemplateId, {},
                function (data) {
                    if (data) {
                        //$("#content").val(data.content);
                        var content = data.content;
                        var parameterList = data.sqlParameterViewModelList;
                        var parameters = "";
                        var multiSelectParameter = "";
                        $.each(parameterList, function (i, parameter) {
                            var value = "";
                            if (flag) {
                                value = parameterArr[i];
                            }
                            if (parameter.type == "multiSelect") {
                                multiSelectParameter = parameter;
                            }
                            parameters += filter.common.createParameterModule(parameter, value);
                            content = content.replace(parameter.code, "<span style=\"color: red\">" + parameter.code + "</span>");
                        });
                        $("#content").html(content);
                        $("#sqlParameter").html(parameters);

                        if (multiSelectParameter != "") {
                            $('#parameter_name_' + multiSelectParameter.id).multiselect({
                                nonSelectedText: multiSelectParameter.placeholder,
                                buttonWidth: '280',
                                maxHeight: '100',
                                includeSelectAllOption: true,
                                selectAllNumber: false,
                                selectAllText: '全部',
                                allSelectedText: '全部',
                                numberDisplayed: multiSelectParameter.marketingViewModelList.length
                            });
                            $('#parameter_name_' + multiSelectParameter.id).next().find(".multiselect-container").width(330);
                        }
                    }
                },
                function () {
                }
            );
        },
        //组建参数显示
        createParameterModule: function (parameter, value) {
            var parameters = "<div class=\"form-group\"'>" +
                "<span id='span_parameter_name_" + parameter.id + "' class=\"col-sm-5 text-height-28 text-right\" style=\"padding-right:0px;\" >" + parameter.name + "【<span style='color: red'>" + parameter.code + "</span>】" + "：</span>" +
                "<div class=\"col-sm-7 text-left\">";
            var dataType = parameter.type;
            if (dataType == "date") {
                parameters += "<input type=\"text\" id='parameter_name_" + parameter.id + "' name=\"parameterValue\" class=\"text-height-28 form-control Wdate\" " +
                    "placeholder='" + parameter.placeholder + "' onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd'});\" value='" + value + "' readonly>";
            } else if (dataType == "time") {
                parameters += "<input type=\"text\" id='parameter_name_" + parameter.id + "' name=\"parameterValue\" class=\"text-height-28 form-control Wdate\" " +
                    "placeholder='" + parameter.placeholder + "' onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});\" value='" + value + "' readonly>";
            } else if (dataType == "number") {
                parameters += "<input name=\"parameterValue\" id='parameter_name_" + parameter.id + "' type=\"text\" style=\"ime-mode:disabled;\" " +
                    "onpaste = \"return common.numeral(event)\" onkeypress=\"return common.numeral(event)\"" +
                    " class=\"form-control text-height-28\" placeholder='" + parameter.placeholder + "' maxlength=\"" + parameter.length + "\" value='" + value + "'>";
            } else if (dataType == "select") {
                var options = "";
                if (parameter.code.indexOf("Marketing", 0) >= 0) {
                    $.each(parameter.marketingViewModelList, function (i, model) {
                        if (model.id == value) {
                            options += "<option value=\"" + model.id + "\" selected>" + model.name + "</option>";
                        } else {
                            options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                        }
                    })
                }
                parameters += "<select name=\"parameterValue\" id='parameter_name_" + parameter.id + "' class=\"form-control text-height-28 select-28\">" + options + "</select>";
            } else if (dataType == "multiSelect") {
                var options = "";
                if (parameter.code.indexOf("Marketing", 0) >= 0) {
                    $.each(parameter.marketingViewModelList, function (i, model) {
                        if (value.indexOf(model.id) >= 0) {
                            options += "<option value='" + model.id + "' selected>" + model.name + "</option>";
                        } else {
                            options += "<option value='" + model.id + "'>" + model.name + "</option>";
                        }
                    })
                }
                parameters += "<select name='parameterValue' id='parameter_name_" + parameter.id + "' class='form-control text-height-28 select-28' multiple='multiple'>" + options + "</select>";
            } else {
                parameters += "<input name=\"parameterValue\" id='parameter_name_" + parameter.id + "' type=\"text\" class=\"form-control text-height-28\"  " +
                    "placeholder='" + parameter.placeholder + "' maxlength=\"" + parameter.length + "\" value='" + value + "'>";
            }

            parameters += "</div></div>";
            return parameters;
        },

        //设置隐藏参数
        setParameter: function () {
            var parameterStr = "";
            $("input[name='parameterValue']").each(function (index, item) {
                parameterStr = parameterStr + "&" + $(this).val();
            });
            $("select[name='parameterValue']").each(function (index, item) {
                parameterStr = parameterStr + "&" + $(this).val();
            });
            if (parameterStr.indexOf("&") >= 0) {
                $("#parameter").val(parameterStr.substring(parameterStr.indexOf("&") + 1));
            } else {
                $("#parameter").val(parameterStr);
            }
        },
        //验证参数值
        validationParameter: function () {
            var flag = true;
            $("input[name='parameterValue']").each(function (index, item) {
                var value = $(this).val();
                if (value == "") {
                    var errorText = $("#errorText");
                    var parameterName = $("#span_" + $(this).attr("id")).html();
                    errorText.text(parameterName + "不能为空");
                    errorText.parent().parent().show();
                    flag = false;
                    return;
                }
            });
            return flag;
        },
        //验证备注
        validationComment: function () {
            var flag = true;
            var new_comment = $("#comment").val();
            if (!common.isEmpty(new_comment) && common.isSingleQuote(new_comment)) {
                var errorText = $("#errorText");
                errorText.text("备注不允许输入单引号");
                errorText.parent().parent().show();
                flag = false;
            }
            return flag;
        },
        switchStatus: function (filterId, status) {
            if (!common.permission.validUserPermission("op030202")) {
                return;
            }
            common.getByAjax(true, "put", "json", "/operationcenter/sms/filterUser/" + filterId + "/" + status, {},
                function (data) {
                    if (data.pass) {
                        if (status == 1) {
                            $("#filter_status_id_" + filterId).css({'color': 'red'}).html("已禁用");
                            $("#disable_filter_status_action_id_" + filterId).hide();
                            $("#enable_filter_status_action_id_" + filterId).show();
                        } else {
                            $("#filter_status_id_" + filterId).css({'color': 'green'}).html("已启用");
                            $("#disable_filter_status_action_id_" + filterId).show();
                            $("#enable_filter_status_action_id_" + filterId).hide();
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

var datatables = datatableUtil.getByDatatables(filter, dataFunction.data, dataFunction.fnRowCallback);

$(function () {
    if (!common.permission.validUserPermission("op0302")) {
        return;
    }
    filter.initFilter.init();

    /**
     * 新建
     */
    $("#toNew").bind({
        click: function () {
            if (!common.permission.validUserPermission("op030201")) {
                return;
            }
            filter.newFilter.popInput();
        }
    });

    /**
     * 搜索
     */
    $("#searchBtn").bind({
        click: function () {
            if (!common.permission.validUserPermission("op0302")) {
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
