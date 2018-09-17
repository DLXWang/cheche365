/**
 * Created by wangfei on 2015/6/12.
 */
$(function () {

    health_order_excel.initialization.initPage();

    $(".tabs a").bind({
        click: function (e) {
            e.preventDefault();
            if ($(this).hasClass("selected")) {
                return false;
            }

            $(this).siblings(".selected").removeClass("selected").addClass("btn-default");
            $(this).addClass("selected").removeClass("btn-default");

            var target = $(this).attr("href").replace("#", "");
            var targetObj = $("#" + target);
            targetObj.show().siblings().not(".tabs").hide();
            targetObj.find(".first").show();
            targetObj.find(".last").hide();

            $(".data-checkbox").prop("checked", false);
        }
    });

    $(".inputradio").bind({
        click: function () {
            var downloadType = $('input:radio[name="downloadType"]:checked').val();

            if (downloadType == "1") {
                $("#searchItemsDiv").hide();
            } else {
                $("#searchItemsDiv").show();
            }
        }
    });
});

var health_order_excel = {
    channel: [3, 40],
    orderStatus: [1, 5, 6, 9, 10],
    param: {},
    initialization: {
        initPage: function () {
            health_order_excel.interface.getAllOrderStatus(function (statusList) {
                var options = "";
                $.each(statusList, function (i, model) {
                    for (j = 0; j < statusList.length; j++) {
                        if (health_order_excel.orderStatus[j] == model.id) {
                            options += "<option value=\"" + model.id + "\">" + model.status + "</option>";
                        }
                    }
                });
                $("#orderStatus").append(options)
            });

            health_order_excel.interface.getAllChannel(function (channelList) {
                var options = "";
                $.each(channelList, function (i, model) {
                    for (j = 0; j < channelList.length; j++) {
                        if (health_order_excel.channel[j] == model.id) {
                            options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                        }
                    }
                });
                $("#channel").append(options);
            });
        }
    },
    operation: {
        uploadFile: function () {
            var codeFile = $("#codeFile").val();
            var exceltype = codeFile.substring(codeFile.lastIndexOf('.') + 1).toLowerCase();

            if (common.validations.isEmpty(codeFile)) {
                popup.mould.popTipsMould(false, "请选择要上传的文件！", popup.mould.second, popup.mould.warning, "", "57%", null);
                return;
            }
            if (!(exceltype == "xls" || exceltype == "xlsx")) {
                popup.mould.popTipsMould(false, "格式必须为excel 2003或者2007的一种！", popup.mould.second, popup.mould.warning, "", "57%", null);
                return false;
            }
            $("#file_form").ajaxSubmit({
                success: function (data) {
                    $(".file-input").val("");
                    popup.mould.popTipsMould(false, "上传成功！", popup.mould.second, popup.mould.success, "", "57%", null);
                },
                error: function (data) {
                    $(".file-input").val("");
                    popup.mould.popTipsMould(false, "上传失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            });

        },
        export: function () {
            if ($("input[name = 'downloadType']:checked").val() == "1") {
                var url = "/orderCenter/health/excel/export/new";
            } else {
                var paramString = "?";
                var orderNo = $("#orderNo").val();
                if (!common.isEmpty(orderNo)) {
                    paramString += "orderNo=" + orderNo + "&";
                }
                var orderStatus = $("#orderStatus").val();
                if (!common.isEmpty(orderStatus)) {
                    paramString += "orderStatus=" + orderStatus + "&";
                }
                var channel = $("#channel").val();
                if (!common.isEmpty(channel)) {
                    paramString += "channel=" + channel + "&";
                }
                var effectiveDate = $("#effectiveDate").val();
                if (!common.isEmpty(effectiveDate)) {
                    paramString += "effectiveDate=" + effectiveDate + "&";
                }
                var expireDate = $("#expireDate").val();
                if (!common.isEmpty(expireDate)) {
                    paramString += "expireDate=" + expireDate + "&";
                }
                var mobile = $("#mobile").val();
                if (!common.isEmpty(mobile)) {
                    paramString += "mobile=" + mobile + "&";
                }

                var url = "/orderCenter/health/excel/export/all" + paramString;
            }
            $("#download_button").attr("href", url);
        },
        changeValue: function () {
            $("#codeFileFake").val($("#codeFile").val());
        }
    },
    interface: {
        getAllOrderStatus: function (callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/orderStatus", {},
                function (data) {
                    callback(data);
                },
                function () {
                }
            );
        }
        ,
        getAllChannel: function (callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/quote/photo/channels", {},
                function (data) {
                    callback(data);
                },
                function () {
                }
            );
        }

    }
    ,
}

