var insurance_import = {
    initTemplateUrl: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/insurance/import/template/url", null, function (response) {
            for (var key in response) {
                $("#url_template" + key).prop("href", response[key]);
            }
        }, function () {
            popup.mould.popTipsMould("模版地址初始化异常！！", popup.mould.first, popup.mould.error, "", "53%", null);
        });
    },
    form: {
        changeValue: function (sourceType) {
            $("#codeFileFake" + sourceType).val($("#codeFile" + sourceType).val());
        },
        validate: function (sourceType) {
            if (common.isEmpty($("#codeFile" + sourceType).val())) {
                popup.mould.popTipsMould(false, "请选择需上传的文件！", popup.mould.second, popup.mould.error, "", "57%", null);
                return false;
            }
            return true;
        },
        error: function (msg) {
            $("#errorText").html(msg);
            $(".error-msg").show().delay(2000).hide(0);
        },
        submit: function (sourceType) {
            var form = $("#insuranceImportForm" + sourceType);
            var options = {
                url: "/orderCenter/insurance/import/" + sourceType,
                async: false,
                type: "post",
                dataType: "text",
                beforeSend: function () {
                    if (sourceType === 'Fanhua') {
                        if (common.validations.isEmpty($("#area").val())) {
                            popup.mould.popTipsMould(false, "请输入并选择地区！", popup.mould.second, popup.mould.error, "", "57%", null);
                            $("#save_button" + sourceType).removeAttr('disabled');
                            return false;
                        }
                    } else if (sourceType === 'FanhuaTemp') {
                        if (common.validations.isEmpty($("#areaFanhuaTemp").val())) {
                            popup.mould.popTipsMould(false, "请输入并选择地区！", popup.mould.second, popup.mould.error, "", "57%", null);
                            $("#save_button" + sourceType).removeAttr('disabled');
                            return false;
                        }
                    }
                },
                success: function (responseStr, statusText) {
                    responseStr = $.trim(responseStr);
                    if (statusText == 'success') {
                        if (responseStr === 'success') {
                            popup.mould.popTipsMould(false, "数据上传成功,正在处理中,处理结果请查看邮件", popup.mould.second, popup.mould.success, "", "57%", null);
                            $(".form_input").val("");
                        } else if (responseStr.startsWith('<')) {
                            popup.mould.popTipsMould(false, "上传文件大小超过限制,请修改！", popup.mould.second, popup.mould.error, "", "57%", null);
                        } else {
                            popup.mould.popTipsMould(false, responseStr, popup.mould.second, popup.mould.error, "", "57%", null);
                        }
                    } else {
                        popup.mould.popTipsMould(false, "上传失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                    }
                    $("#save_button" + sourceType).removeAttr('disabled');
                },
                error: function (responseStr) {
                    $("#save_button" + sourceType).removeAttr('disabled');
                    popup.mould.popTipsMould(false, "上传失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            };
            form.ajaxSubmit(options);
        }
    }
};

$(function () {
    insurance_import.initTemplateUrl();
    $(".save_button").unbind("click").bind({
            click: function () {
                if (!common.permission.validUserPermission("or0804")) {
                    return;
                }
                var sourceType = $(this).attr('sourceType');
                if (insurance_import.form.validate(sourceType)) {
                    $(this).attr('disabled', 'disabled');
                    insurance_import.form.submit(sourceType);
                }
            }
        }
    );


    $("#areaLabel").bind({
        keydown: function () {

            $("#areaLabel").autocomplete({
                source: function (request, response) {
                    var keyword = request.term.trim();
                    if (common.validations.isEmpty(keyword) && common.validations.isEmpty($('#areaLabel').val())) {
                        $("#area").val('');
                        return false
                    }
                    $.ajax({
                        url: "/orderCenter/resource/areas/" + keyword,
                        type: "get",
                        dataType: "json",
                        data: {
                            pageSize: 10
                        },
                        success: function (data) {
                            var models = [];
                            $.each(data, function (index, model) {
                                models.push({
                                    "label": model.name,
                                    "value": model.id
                                });
                            });
                            response(models);
                        }
                    });
                },
                minLength: 0,
                focus: function (event, ui) {
                    event.preventDefault();
                    $(this).val(ui.item.label);
                },
                select: function (event, ui) {
                    event.preventDefault();
                    $(this).val(ui.item.label);
                    $("#area").val(ui.item.value);
                },
                open: function () {
                    $(this).removeClass("ui-corner-all").addClass("ui-corner-top");
                },
                close: function () {
                    $(this).removeClass("ui-corner-top").addClass("ui-corner-all");
                }
            });
        }
    });

    $("#areaLabelFanhuaTemp").bind({
        keydown: function () {
            $("#areaLabelFanhuaTemp").autocomplete({
                source: function (request, response) {
                    var keyword = request.term.trim();
                    if (common.validations.isEmpty(keyword) && common.validations.isEmpty($('#areaLabelFanhuaTemp').val())) {
                        $("#areaFanhuaTemp").val('');
                        return false
                    }
                    $.ajax({
                        url: "/orderCenter/resource/areas/" + keyword,
                        type: "get",
                        dataType: "json",
                        data: {
                            pageSize: 10
                        },
                        success: function (data) {
                            var models = [];
                            $.each(data, function (index, model) {
                                models.push({
                                    "label": model.name,
                                    "value": model.id
                                });
                            });
                            response(models);
                        }
                    });
                },
                minLength: 0,
                focus: function (event, ui) {
                    event.preventDefault();
                    $(this).val(ui.item.label);
                },
                select: function (event, ui) {
                    event.preventDefault();
                    $(this).val(ui.item.label);
                    $("#areaFanhuaTemp").val(ui.item.value);
                },
                open: function () {
                    $(this).removeClass("ui-corner-all").addClass("ui-corner-top");
                },
                close: function () {
                    $(this).removeClass("ui-corner-top").addClass("ui-corner-all");
                }
            });
        }
    });
})
