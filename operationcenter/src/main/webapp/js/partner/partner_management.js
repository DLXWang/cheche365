/**
 * Created by wangfei on 2015/8/25.
 */
var dataFunction = {
    "data": function (data) {
        data.keyword = datatableUtil.params.keyWord;
    },
    "fnRowCallback": function (nRow, aData) {
        var cooperationModes = "";
        $.each(aData.cooperationModes, function (i, mode) {
            cooperationModes += common.checkToEmpty(mode.name) + "、";
        });

        $cooperationModes = cooperationModes.substring(0, cooperationModes.length - 1);
        $partnerStatus = "<span id='partner_status_id_" + aData.id + "' style=\"color: " + (aData.enable ? "green" : "red") + "\">" + (aData.enable ? "已启用" : "已禁用") + "</span>";
        $lastAlter = common.checkToEmpty(aData.operator) + "  " + common.checkToEmpty(aData.updateTime);
        $comment = "<span title=\"" + common.checkToEmpty(aData.comment) + "\">" + common.getFormatComment(aData.comment, 40) + "</span>";
        $operation = "<div >" +
            "<a class='" + (aData.enable ? "" : "none") + "'  id='disable_partner_status_action_id_" + aData.id + "' href=\"javascript:;\" onclick=\"listPartner.editPartner.switchStatus(" + aData.id + "," + 0 + ");\" style=\"color:red\"> 禁用 </a>" +
            "<a class='" + (aData.enable ? "none" : "") + "'  id='enable_partner_status_action_id_" + aData.id + "'href=\"javascript:;\" onclick=\"listPartner.editPartner.switchStatus(" + aData.id + "," + 1 + ");\" style=\"color:green\"> 启用 </a>" +
            ((aData.partnerAttachment != null && !common.isEmpty(aData.partnerAttachment.contractUrl)) ? "<a name=\"pactfile\" style=\"padding-left: 27px;\" target=\"_self\" onclick=\"downloadPact(" + aData.id + ")\" >下载合同</a>" : "<span style=\"color: darkgrey;padding-left: 27px;\">下载合同</span>") +
            "</div>" +
            "<div >" +
            "<a style=\"padding-left: 15px;\" href=\"javascript:;\" onclick=\"listPartner.editPartner.popEdit(" + aData.id + ");\">编辑</a>" +
            ((aData.partnerAttachment != null && !common.isEmpty(aData.partnerAttachment.technicalDocumentUrl)) ? "<a name=\"docfile\" style=\"padding-left: 15px;\" target=\"_self\" onclick=\"downloadDoc(" + aData.id + ")\" >下载技术文档</a>" : "<span style=\"color: darkgrey;padding-left: 15px;\">下载技术文档</span>") +
            "</div>";

        $('td:eq(2)', nRow).html($cooperationModes);
        $('td:eq(5)', nRow).html($partnerStatus);
        $('td:eq(6)', nRow).html($lastAlter);
        $('td:eq(7)', nRow).html($comment);
        $('td:eq(8)', nRow).html($operation);
    }
};
var listPartner = {
    "url": '/operationcenter/partners',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": "id", "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "name", "title": "合作商名称", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
        {"data": null, "title": "支持合作方式", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "partnerType.name", "defaultContent": "","title": "合作商类型", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {
            "data": "cooperationTime",
            "title": "预计首次合作日期",
            'sClass': "text-center",
            "orderable": false,
            "sWidth": "120px"
        },
        {"data": null, "title": "状态", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "最后修改", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "240px"}
    ],
    config_validation: {
        onkeyup: false,
        onfocusout: false,
        rules: {
            name: {
                required: true,
                maxlength: 20
            },
            cooperationMode: {
                required: true
            },
            partnerType: {
                required: true
            },
            cooperationTime: {
                required: true
            }
        },
        messages: {
            name: {
                required: "请输入合作商名称",
                maxlength: "合作商名称最多可输入20位"
            },
            cooperationMode: {
                required: "请选择支持合作方式"
            },
            partnerType: {
                required: "请选择合作商类型"
            },
            cooperationTime: {
                required: "请选择预计首次合作日期"
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
            if ($("#partnerId").val() == "0") {
                listPartner.newPartner.savePartner(form);
            } else {
                listPartner.editPartner.updatePartner(form);
            }
        }
    },
    initPartner: {
        init: function () {
            listPartner.initPartner.initCooperationMode();
            listPartner.initPartner.initPartnerType();
        },
        initCooperationMode: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/cooperationModes", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            options += "<div class=\"checkbox\"><label><input type=\"checkbox\" name=\"cooperationMode\" value=\"" + model.id + "\"> " + model.name + "</label> </div>";
                        });
                        $("#cooperationModes").html(options);
                    }
                },
                function () {
                }
            );
        },
        initPartnerType: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/partnerTypes", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                        });
                        $("#partnerTypeSel").append(options);
                    }
                },
                function () {
                }
            );
        },
        initPopupContent: function () {
            var popupContent = $("#new_content");
            if (popupContent.length > 0) {
                listPartner.newPartner.content = popupContent.html();
                popupContent.remove();
            }

            var uploadContent = $("#upload_content");
            if (uploadContent.length > 0) {
                listPartner.files.content = uploadContent.html();
                uploadContent.remove();
            }
        },
        initConstants: function () {
            listPartner.files.uploadCount_1 = 0;
            listPartner.files.uploadCount_2 = 0;
        }
    },
    newPartner: {
        content: "",
        popInput: function () {
            listPartner.initPartner.initPopupContent();
            listPartner.initPartner.initConstants();
            popup.pop.popInput(listPartner.newPartner.content, popup.mould.first, "520px", "554px", "36%", "59%");
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }
            });
            $("#upload_btn_1").unbind("click").bind({
                click: function () {
                    $("#fileType").val(1);
                    listPartner.files.popUpload();
                }
            });
            $("#upload_btn_2").unbind("click").bind({
                click: function () {
                    $("#fileType").val(2);
                    listPartner.files.popUpload();
                }
            });
            $("#upload_btn_1_remove").unbind("click").bind({
                click: function () {
                    $("#fileType").val(1);
                    listPartner.files.removeFile();
                }
            });
            $("#upload_btn_2_remove").unbind("click").bind({
                click: function () {
                    $("#fileType").val(2);
                    listPartner.files.removeFile();
                }
            });
            $("#new_form").validate(listPartner.config_validation);
        },
        savePartner: function (form) {
            common.getByAjax(false, "get", "json", "/operationcenter/partners/check",
                {
                    name: $("#name").val()
                },
                function (data) {
                    if (data) {
                        $("#toCreate").attr("disabled", true);
                        common.getByAjax(true, "post", "json", "/operationcenter/partners", $(form).serialize(),
                            function (data) {
                                $("#toCreate").attr("disabled", false);
                                if (data.pass) {
                                    popup.mask.hideAllMask();
                                    popup.mould.popTipsMould("新建合作商成功！", popup.mould.first, popup.mould.success, "", "57%",
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
                                popup.mould.popTipsMould("新建合作商失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                            }
                        );
                    } else {
                        popup.mould.popTipsMould("该合作商名称已使用，请重新填写！", popup.mould.second, popup.mould.error, "", "57%", null);
                        return false;
                    }
                }, function () {
                }
            )
        }
    },
    editPartner: {
        popEdit: function (id) {
            if (!common.permission.validUserPermission("op010102")) {
                return;
            }
            common.getByAjax(true, "get", "json", "/operationcenter/partners/" + id, {},
                function (data) {
                    listPartner.newPartner.popInput();
                    $("#popover_normal_input #partner_title").text("编辑合作商");
                    $("#toCreate").val("更新");
                    $("#popover_normal_input #name").val(common.checkToEmpty(data.name));
                    $("#popover_normal_input #name").val(common.checkToEmpty(data.name));

                    $.each(data.cooperationModes, function (i, mode) {
                        $("input[name='cooperationMode']").each(function () {
                            if ($(this).val() == mode.id) {
                                $(this).attr("checked", true);
                            }
                        });
                    });
                    $("#popover_normal_input #partnerTypeSel").val(data.partnerType.id);
                    $("#popover_normal_input #cooperationTime").val(data.cooperationTime);

                    if (data.partnerAttachment != null) {
                        if (!common.isEmpty(data.partnerAttachment.contractUrl)) {
                            listPartner.files.uploadCount_1 = 1;
                            var btn_1_text = $("#upload_btn_1_text");
                            var contractName = data.partnerAttachment.contractName;
                            var _contractName = contractName.substring(0, contractName.lastIndexOf("."));
                            if (_contractName.length > 10) {
                                var prefix = contractName.substring(contractName.lastIndexOf("."), contractName.length);
                                btn_1_text.text(_contractName.substring(0, 10) + "..." + prefix);
                                btn_1_text.attr("title", contractName);
                            } else {
                                btn_1_text.text(contractName);
                            }
                        }

                        if (!common.isEmpty(data.partnerAttachment.technicalDocumentUrl)) {
                            listPartner.files.uploadCount_2 = 1;
                            var btn_2_text = $("#upload_btn_2_text");
                            var technicalDocumentName = data.partnerAttachment.technicalDocumentName;
                            var _technicalDocumentName = technicalDocumentName.substring(0, technicalDocumentName.lastIndexOf("."));
                            if (_technicalDocumentName.length > 10) {
                                var prefix = technicalDocumentName.substring(technicalDocumentName.lastIndexOf("."), technicalDocumentName.length);
                                btn_2_text.text(_technicalDocumentName.substring(0, 10) + "..." + prefix);
                                btn_2_text.attr("title", contractName);
                            } else {
                                btn_2_text.text(contractName);
                            }
                        }
                    }

                    $("#popover_normal_input #comment").val(data.comment);
                    $("#popover_normal_input #partnerAttachmentId").val(data.partnerAttachment != null ? data.partnerAttachment.id : "0");
                    $("#popover_normal_input #partnerId").val(data.id);
                    if (data.partnerAttachment != null) {
                        $("#contractUrlNew").val(common.checkToEmpty(data.partnerAttachment.contractUrl));
                        $("#contractNameNew").val(common.checkToEmpty(data.partnerAttachment.contractName));
                        $("#technicalDocumentUrlNew").val(common.checkToEmpty(data.partnerAttachment.technicalDocumentUrl));
                        $("#technicalDocumentNameNew").val(common.checkToEmpty(data.partnerAttachment.technicalDocumentName));
                    }
                    listPartner.files.buttonControl();
                },
                function () {
                    popup.mould.popTipsMould("获取合作商信息失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        updatePartner: function (form) {
            $("#toCreate").attr("disabled", true);
            common.getByAjax(true, "put", "json", "/operationcenter/partners/" + $("#partnerId").val(), $(form).serialize(),
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
                    popup.mould.popTipsMould("更新合作商信息失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },
        switchStatus: function (partnerId, status) {
            if (!common.permission.validUserPermission("op010103")) {
                return;
            }
            common.getByAjax(true, "put", "json", "/operationcenter/partners/" + partnerId + "/" + status, {},
                function (data) {
                    if (data.pass) {
                        if (status == 0) {
                            $("#partner_status_id_" + partnerId).css({'color': 'red'}).html("已禁用");
                            $("#disable_partner_status_action_id_" + partnerId).hide();
                            $("#enable_partner_status_action_id_" + partnerId).show();
                        } else {
                            $("#partner_status_id_" + partnerId).css({'color': 'green'}).html("已启用");
                            $("#disable_partner_status_action_id_" + partnerId).show();
                            $("#enable_partner_status_action_id_" + partnerId).hide();
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
    },
    files: {
        upload_config: {
            auto: false,
            multi: true,
            queueID: "fileQueue",
            uploader: "",
            fileObjName: "file",
            uploadLimit: 1,
            queueSizeLimit: 1,
            simUploadLimit: 1,
            removeCompleted: false,
            swf: "../../libs/uploadify-3.2.1/uploadify.swf",
            buttonText: "添加文件",
            fileDesc: "请选择文件",
            fileTypeExts: "*.doc;*.docx;*.ppt;*.pptx;*.pdf;*.xls;*.xlsx;*.txt;*.jpg;*.png;*.bmp;*.jpeg;*.zip;*.rar",
            fileSizeLimit: "20MB",
            overrideEvents: ["onDialogClose", "onUploadStart", 'onUploadSuccess', 'onSelectError']
        },
        uploadCount_1: 0,
        uploadCount_2: 0,
        content: "",
        popUpload: function () {
            listPartner.files.upload_config.onFallback = listPartner.files.onFallback;
            listPartner.files.upload_config.onSelectError = listPartner.files.onSelectError;
            listPartner.files.upload_config.onUploadSuccess = listPartner.files.onUploadSuccess;
            listPartner.files.upload_config.onUploadStart = listPartner.files.onUploadStart;
            listPartner.files.upload_config.uploader = "/operationcenter/partners/" + $("#partnerId").val() + "/" + $("#fileType").val() + "/" + $("#partnerAttachmentId").val();

            popup.pop.popInput(listPartner.files.content, popup.mould.second, "560px", "226px", "45%", "53%");
            $("#uploadify").uploadify(listPartner.files.upload_config);
            $("#popover_normal_input_second .shutDown").unbind("click").bind({
                click: function () {
                    popup.mask.hideSecondMask();
                }
            });
        },
        onSelectError: function (file, errorCode, errorMsg) {
            var uploadConfig = $("#uploadify");
            var msg = "";
            switch (errorCode) {
                case -100 :
                    msg = "上传的文件数量已经超出系统限制的" + uploadConfig.uploadify('settings', 'uploadLimit') + "个文件！";
                    break;
                case -110 :
                    msg = "文件大小超出系统限制的" + uploadConfig.uploadify('settings', 'fileSizeLimit') + "大小！";
                    break;
                case -120 :
                    msg += "上传文件大小不可为0！";
                    break;
                case -130 :
                    msg += "文件格式不支持，仅限Word、Excel、PPT、PDF、图片、文本、压缩文件！";
                    break;
                default:
                    msg += "错误代码：" + errorCode + "\n" + errorMsg;
            }

            popup.mask.hideSecondMask();
            popup.mould.popTipsMould(msg, popup.mould.second, popup.mould.warning, "", "57%", null);
        },
        onUploadSuccess: function (file, data, response) {
            var type = $("#fileType").val();
            var partnerId = $("#partnerId").val();
            type == 1 ? listPartner.files.uploadCount_1++ : listPartner.files.uploadCount_2++;
            var jsonObj = JSON.parse(data);
            if (jsonObj && jsonObj.id) {
                $("#partnerAttachmentId").val(jsonObj.id);
            } else {
                $("#partnerAttachmentId").val("0");
            }

            var _fileName = file.name.substring(0, file.name.lastIndexOf("."));
            var btn_text = (type == 1) ? $("#upload_btn_1_text") : $("#upload_btn_2_text");
            if (_fileName.length > 10) {
                var prefix = file.name.substring(file.name.lastIndexOf("."), file.name.length);
                btn_text.text(_fileName.substring(0, 10) + "..." + prefix);
                btn_text.attr("title", file.name);
            } else {
                btn_text.text(file.name);
            }
            listPartner.files.buttonControl();

            if (partnerId != 0) {
                if (type == 1) {
                    $("#contractUrlNew").val(jsonObj.contractUrl);
                    $("#contractNameNew").val(jsonObj.contractName);
                } else {
                    $("#technicalDocumentUrlNew").val(jsonObj.technicalDocumentUrl);
                    $("#technicalDocumentNameNew").val(jsonObj.technicalDocumentName);
                }
            }
        },
        onUploadStart: function (file) {
            var type = $("#fileType").val();
            var uploadCount = (type == 1) ? listPartner.files.uploadCount_1 : listPartner.files.uploadCount_2;
            var uploadConfig = $("#uploadify");
            if (uploadCount >= uploadConfig.uploadify('settings', 'uploadLimit')) {
                popup.mask.hideSecondMask();
                popup.mould.popTipsMould("上传的文件数量已经超出系统限制的" + uploadConfig.uploadify('settings', 'uploadLimit') + "个文件！", popup.mould.second, popup.mould.warning, "", "", null);
                uploadConfig.uploadify("stop");
            }
        },
        onFallback: function () {
            popup.mould.popTipsMould("您未安装FLASH控件，无法上传文件！请安装FLASH控件后再试。", popup.mould.first, popup.mould.warning, "", "", null);
        },
        removeFile: function () {
            var _btn;
            var type = $("#fileType").val();
            var partnerId = $("#partnerId").val();
            var partnerAttachmentId = $("#partnerAttachmentId").val();
            _btn = (type == 1) ? $("#upload_btn_1_text") : $("#upload_btn_2_text");

            popup.mould.popConfirmMould("确定移除该文件吗？", popup.mould.second, "", "",
                function () {
                    common.getByAjax(true, "delete", "json", "/operationcenter/partners/" + partnerId + "/" + type + "/" + partnerAttachmentId, {},
                        function (data) {
                            popup.mask.hideSecondMask();
                            _btn.text("");
                            type == 1 ? listPartner.files.uploadCount_1-- : listPartner.files.uploadCount_2--;
                            if (!data.id) {
                                $("#partnerAttachmentId").val("0")
                            }
                            listPartner.files.buttonControl();

                            if (partnerId != 0) {
                                if (type == 1) {
                                    $("#contractUrlNew").val("");
                                    $("#contractNameNew").val("");
                                } else {
                                    $("#technicalDocumentUrlNew").val("");
                                    $("#technicalDocumentNameNew").val("");
                                }
                            }
                        },
                        function () {
                            popup.mask.hideSecondMask();
                            popup.mould.popTipsMould("删除文件失败！", popup.mould.second, popup.mould.error, "", "56%", null);
                        }
                    );
                },
                function () {
                    popup.mask.hideSecondMask();
                }
            );
        },
        buttonControl: function () {
            if (listPartner.files.uploadCount_1 > 0) {
                $("#upload_btn_1").hide().siblings("div").show();
            } else {
                $("#upload_btn_1").show().siblings("div").hide();
            }

            if (listPartner.files.uploadCount_2 > 0) {
                $("#upload_btn_2").hide().siblings("div").show();
            } else {
                $("#upload_btn_2").show().siblings("div").hide();
            }
        }
    }
};
function downloadPact(id) {
    if (!common.permission.validUserPermission("op010105")) {
        return;
    }
    var downloadUrl = "/operationcenter/partners/" + id + "/pactfile/1";
    $("a[name='pactfile']").attr("href", downloadUrl);
    return;
}

function downloadDoc(id) {
    if (!common.permission.validUserPermission("op010106")) {
        return;
    }
    var downloadUrl = "/operationcenter/partners/" + id + "/docfile/2";
    $("a[name='docfile']").attr("href", downloadUrl);
    return;
}
var datatables = datatableUtil.getByDatatables(listPartner, dataFunction.data, dataFunction.fnRowCallback);
$(function () {
    if (!common.permission.validUserPermission("op0101")) {
        return;
    }
    listPartner.initPartner.init();

    /**
     * 新建合作商
     */
    $("#toNew").bind({
        click: function () {
            if (!common.permission.validUserPermission("op010101")) {
                return;
            }
            listPartner.newPartner.popInput();
        }
    });

    /**
     * 搜索
     */
    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould("请输入搜索内容", "first", "warning", "", "", null);
                return false;
            }
            datatableUtil.params.keyWord = keyword;
            datatables.ajax.reload();
        }
    });
});
