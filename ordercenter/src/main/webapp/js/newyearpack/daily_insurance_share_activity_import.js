var daily_share = {
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
            var form = $("#dailyShareForm");
            var options = {
                url: "/orderCenter/dailyInsuranceOffer/report/upload",
                async: false,
                type: "post",
                dataType: "text",
                success: function (responseStr) {
                    if (responseStr == 'success') {
                        $("#codeFileFake").val(null);
                        popup.mould.popTipsMould(false, "上传成功！", popup.mould.second, popup.mould.success, "", "57%", null);
                    } else {
                        $("#codeFileFake").val(null);
                        popup.mould.popTipsMould(false, responseStr, popup.mould.second, popup.mould.error, "", "57%", null);
                    }
                },
                error: function (responseStr) {
                    popup.mould.popTipsMould(false, "上传失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            };
            form.ajaxSubmit(options);
        },
    },
}

$(function () {
    $("#save_button").unbind("click").bind({
            click: function () {
                if (!common.permission.validUserPermission("or0803")) {
                    return;
                }
                if (daily_share.form.validate()) {
                    daily_share.form.submit();
                }
            }
        }
    )
})
