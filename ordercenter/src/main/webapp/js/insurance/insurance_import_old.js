var insurance_import = {
    initTemplateUrl: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/insurance/import/template/url", null, function (response) {
            $("#url_template").prop("href", response.message);
        }, function () {
            popup.mould.popTipsMould("模版地址初始化异常！！", popup.mould.first, popup.mould.error, "", "53%", null);
        });
    },
    form: {
        changeValue: function () {
            $("#codeFileFake").val($("#codeFile").val());
        },
        validate: function () {
            if (common.isEmpty($("#codeFile").val())) {
                this.error("请选择需上传的文件");
                return false;
            }
            return true;
        },
        error: function (msg) {
            $("#errorText").html(msg);
            $(".error-msg").show().delay(2000).hide(0);
        },
        submit: function () {
            var form = $("#insuranceImportForm");
            var options = {
                url: "/orderCenter/insurance/import",
                async: false,
                type: "post",
                dataType: "text",
                success: function (responseStr, statusText) {
                    if (statusText == 'success') {
                        $("#save_button").removeAttr('disabled');
                        if (responseStr == 'success') {
                            popup.mould.popTipsMould(false, "数据上传成功,正在处理中,处理结果请查看邮件", popup.mould.second, popup.mould.success, "", "57%", null);
                        } else if (responseStr.startsWith('<')) {
                            popup.mould.popTipsMould(false, "上传文件大小超过限制,请修改！", popup.mould.second, popup.mould.error, "", "57%", null);
                        } else {
                            popup.mould.popTipsMould(false, responseStr, popup.mould.second, popup.mould.error, "", "57%", null);
                        }
                    } else {
                        popup.mould.popTipsMould(false, "上传失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                    }
                },
                error: function (responseStr) {
                    $("#save_button").removeAttr('disabled');
                    popup.mould.popTipsMould(false, "上传失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            };
            form.ajaxSubmit(options);
        },
    },
}

$(function () {
    insurance_import.initTemplateUrl();
    $("#save_button").unbind("click").bind({
            click: function () {
                if (!common.permission.validUserPermission("or0804")) {
                    return;
                }
                if (insurance_import.form.validate()) {
                    $(this).attr('disabled', 'disabled');
                    insurance_import.form.submit();
                }
            }
        }
    )
})
