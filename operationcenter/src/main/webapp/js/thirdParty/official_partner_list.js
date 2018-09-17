
var dataFunction = {
    "data": function (data) {
        data.keyword = datatableUtil.params.keyWord;
        data.name = datatableUtil.params.keyType;
    },
    "fnRowCallback": function (nRow, aData) {
        $id = "<a href='../../views/partner/coop_situation.html?id=" + aData.id + "' target='_blank'>" + "查看详情" + "</a>";
        $comment = "<span title=\"" + common.checkToEmpty(aData.comment) + "\">" + common.getFormatComment(aData.comment, 40) + "</span>";
        $('td:eq(1)', nRow).html($comment);
        $('td:eq(2)', nRow).html($id);
    },
}
var partnerList = {
    "url": '/operationcenter/thirdParty/officialPartner',
    "type": "GET",
    "table_id": "partner_list",
    "columns": [
        {"data": "name", "title": "合作商名称", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
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
            }
        },
        messages: {
            name: {
                required: "请输入合作商名称",
                maxlength: "合作商名称最多可输入20位"
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
            partnerList.newPartner.savePartner(form);
        }
    },

    initPopupContent: function () {
        var popupContent = $("#new_content");
        if (popupContent.length > 0) {
            partnerList.newPartner.content = popupContent.html();
            popupContent.remove();
        }
    },

    newPartner: {
        content: "",
        popInput: function () {
            partnerList.initPopupContent();
            popup.pop.popInput(partnerList.newPartner.content, popup.mould.first, "520px", "554px", "36%", "59%");
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }
            });
            $("#new_form").validate(partnerList.config_validation);
        },
        savePartner: function (form) {
            common.getByAjax(false, "get", "json", "/operationcenter/thirdParty/officialPartner/check",
                {
                    name: $("#name").val()
                },
                function (data) {
                    if (data) {
                        $("#toCreate").attr("disabled", true);
                        common.getByAjax(true, "post", "json", "/operationcenter/thirdParty/officialPartner", $(form).serialize(),
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
}
var channelAgent = {

    chgList:function(){
        $("#keyword").unbind("change").bind({
            change : function(){
                datatables.ajax.reload();
            }
        });
    },

}


var datatables = datatableUtil.getByDatatables(partnerList,dataFunction.data,dataFunction.fnRowCallback);
$(function(){
    if (!common.permission.validUserPermission('op1101')) {
        return;
    }
    channelAgent.chgList();

    /* 新增 */
    $("#newPartner").unbind("click").bind({
        click : function(){
            partnerList.newPartner.popInput();
        }
    });

    /**
     * 搜索
     */
    $("#searchButton").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                datatableUtil.params.keyType = "";
                datatables.ajax.reload();
                return false;
            }
            datatableUtil.params.keyType = keyword;
            datatables.ajax.reload();
        }
    });
});
