/**
 * Created by wangfei on 2015/7/28.
 */
var conditionsMap = new Map;
var QR_SCENE = "QR_SCENE";
var QR_LIMIT_SCENE = "QR_LIMIT_SCENE";
var QRCodeChannel = function () {
    /**
     * 新建
     */
    this.save = function (form, isBatch) {
        var new_comment = $("#comment").val();
        if (!common.isEmpty(new_comment) && common.isSingleQuote(new_comment)) {
            var errorText = $("#errorText");
            errorText.text("备注不允许输入单引号");
            errorText.parent().parent().show();
            $("#toCreate").attr("disabled", false);
            return;
        }
        var baseUrl = "";
        var qrCodeType = $("#qrCodeType").val();
        if (qrCodeType == QR_SCENE) {
            if (isBatch) {
                baseUrl = "/operationcenter/qcchannels/temp/batch";
            } else {
                baseUrl = "/operationcenter/qcchannels/temp";
            }
        } else {
            if (isBatch) {
                baseUrl = "/operationcenter/qcchannels/forever/batch";
            } else {
                baseUrl = "/operationcenter/qcchannels/forever";
            }
        }
        $("#toCreate").attr("disabled", true);
        common.getByAjax(true, "post", "json", baseUrl, $(form).serialize(),
            function (data) {
                if ($("#downLoadFlag").is(":checked")) {
                    var downloadForm = $("#downloadForm");
                    downloadForm.attr("action", "/operationcenter/qcchannels/" + data.ids + "/" + qrCodeType + "/download");
                    downloadForm.submit();
                }

                popup.mould.popTipsMould("渠道创建成功", popup.mould.second, popup.mould.success, "", "56%",
                    function () {
                        popup.mask.hideAllMask(false);
                        var properties = new Properties(1, "");
                        qrCodeChannel.list(properties, qrCodeType, 1);
                    }
                );
                $("#toCreate").attr("disabled", false);
            },
            function () {
                popup.mould.popTipsMould("创建失败，请稍候重试", popup.mould.second, popup.mould.error, "", "56%",
                    function () {
                        popup.mask.hideSecondMask(false);
                        $("#toCreate").attr("disabled", false);
                    }
                );
            }
        );
    };

    /**
     *修改
     */
    this.edit = function (form, id, updateFlag) {

        $("#yj_detail_qrcode_content").find("#btnGroup").find(".btn").attr("disabled", true);
        var url = "";
        if (updateFlag == "1") {
            url = "/operationcenter/qcchannels/updateChannel/";
        } else {
            url = "/operationcenter/qcchannels/";
        }
        common.getByAjax(true, "put", "json", url + id, $(form).serialize(),
            function (data) {
                if (data) {
                    var tipMsg = "";
                    if (updateFlag == "1") {
                        tipMsg = "更新渠道号并保存成功！新渠道号为：" + data.code;
                    } else {
                        tipMsg = "渠道信息更新成功！";
                    }
                    popup.mould.popTipsMould(tipMsg, popup.mould.second, popup.mould.success, "", "57%",
                        function () {
                            popup.mask.hideAllMask(false);
                            var properties = new Properties(1, "");
                            qrCodeChannel.list(properties, QR_LIMIT_SCENE, 1);
                        }
                    );
                } else {
                    popup.mould.popTipsMould("更新失败", popup.mould.second, popup.mould.error, "", "56%",
                        function () {
                            popup.mask.hideSecondMask(false);
                        }
                    );
                }
                $("#yj_detail_qrcode_content").find("#btnGroup").find(".btn").attr("disabled", false);
            },
            function () {
                popup.mould.popTipsMould("系统异常，请重试", popup.mould.second, popup.mould.error, "", "56%",
                    function () {
                        popup.mask.hideSecondMask(false);
                    }
                );
                $("#yj_detail_qrcode_content").find("#btnGroup").find(".btn").attr("disabled", false);
            }
        );
    };

    /**
     *导出当前列表到Excel
     */
    this.export_excel = function (conditionsMap, qrCodeType) {
        var url = "";
        if (QR_SCENE == qrCodeType) {
            url = "/operationcenter/qcchannels/temp/export?";
        } else {
            url = "/operationcenter/qcchannels/forever/export?";
        }
        conditionsMap.each(function (key, value, index) {
            if (index != 0) {
                url += "&";
            }
            url += key + "=" + value;
        });
        $("#export_excel").attr("href", url);
    };

    /**
     *导出扫描关注数到Excel
     */
    this.export_count_excel = function (qrCodeType, channelCode) {
        var url = "";
        if (QR_SCENE == qrCodeType) {
            url = "/operationcenter/qcchannels/" + qrCodeType + "/" + channelCode + "/export/temp";
        } else {
            url = "/operationcenter/qcchannels/" + qrCodeType + "/" + channelCode + "/export/forever";
        }
        $("#export_count_excel").attr("href", url);
    }
};

QRCodeChannel.prototype = new Action();
var qrCodeChannel = new QRCodeChannel();

function getFormatComment(str) {
    if (!str) {
        return "";
    }

    if (str.length > 40) {
        return str.substring(0, 40) + "……";
    } else {
        return str;
    }
}
