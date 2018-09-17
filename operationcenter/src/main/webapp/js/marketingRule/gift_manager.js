//@ sourceURL=gift_manager.js
var gift = {
    param: {
        dataTable: null,
        giftTypeOptions: "",
        useTypeOptions: ""
    },
    init: {
        initDataTable: function () {
            gift.interface.initDataTable();
        },
        initGiftTypeCategorys: function () {
            gift.interface.initCategorys();
        }
        //initUseTypes: function () {
        //    gift.interface.getUseTypes();
        //}
    },
    dtList: {
        url: "/operationcenter/marketingRule/giftType",
        type: "get",
        table_id: "gift-data-table",
        "columns": [
            {"data": "id", "title": "ID", 'sClass': "text-center", "orderable": false},
            {"data": "giftName", "title": "礼物名称", 'sClass': "text-center", "orderable": false},
            {"data": "giftType", "title": "类型", 'sClass': "text-center", "orderable": false},
            {"data": "description", "title": "描述", 'sClass': "text-center", "orderable": false},
            {"data": "useType", "title": "使用类型", 'sClass': "text-center", "orderable": false},
            {"data": "operator", "title": "操作人", 'sClass': "text-center", "orderable": false},
            {"data": "deliveryFlag", "title": "是否配送", 'sClass': "text-center", "orderable": false},
            {
                "data": "giftStatus",
                "title": "状态",
                render: function (data, type, row) {
                    if (type === 'display') {
                        if (data === '已启用') {
                            return '<span style="color:green ;"> 已启用</span>';
                        } else if (data === '已禁用') {
                            return '<span style="color:red ;"> 已禁用</span>';
                        }
                    }
                    return "";
                },
                'sClass': "text-center",
                "orderable": false
            },
            {
                data: "id",
                "title": '操作',
                render: function (data, type, row) {
                    if (type === 'display') {
                        if (row.giftStatus === '已启用') {
                            return "<a style=\"margin-left: 10px;\" onclick='gift.operation.changeStatus(" + data + ",1)' target='_blank'><span style='color: coral;'>禁用</span></a>";
                        } else if (row.giftStatus === '已禁用') {
                            return "<a style=\"margin-left: 10px;\" onclick='gift.operation.changeStatus(" + data + ",0)' target='_blank'>启用</a>";
                        }
                    }
                    return "";
                },
                className: "text-center checkbox-width",
                "orderable": false
            }
        ]
    },
    dtParam: function (param) {
        param.giftName = $("#giftName").val().trim();
        param.giftType = $("#giftType").val().trim();
        param.giftStatus = $("#giftStatus").val().trim();
    },
    data: {},
    operation: {
        toAdd: function () {
            var $addHtml = $("#add-gift-div").html();
            popup.pop.popInput($addHtml, popup.mould.first, "500px", "400px", "50%", "55%");

            $("#popover_normal_input #category-sel").unbind("change").bind({
                change: function () {
                    if ($("#popover_normal_input #category-sel").val() === "6") {
                        $("#popover_normal_input input[name='deliveryFlag'][value='1']").prop("checked", true);
                    } else {
                        $("#popover_normal_input input[name='deliveryFlag'][value='0']").prop("checked", true);
                    }
                }
            });

        },
        toDetail: function () {
            $.post("/views/marketing_rule/gift_detail.html", {}, function (content) {
                popup.pop.popInput(content, popup.mould.first, "700px", "500px", "50%", "55%");
            });
        },
        changeStatus: function (id, disableStatus) {
            gift.interface.updateStatus(id, disableStatus);
        },
        saveForm: function () {
            var requestParam = {
                giftName: $("#popover_normal_input #name").val().trim(),
                description: $("#popover_normal_input #description").val().trim(),
                giftType: $("#popover_normal_input #category-sel").val().trim(),
                useType: $("#popover_normal_input #useTypeSel").val().trim(),
                deliveryFlag: $("#popover_normal_input input[name='deliveryFlag']:checked").val().trim()
            };

            if (common.isEmpty(requestParam.giftName)) {
                popup.mould.popTipsMould("礼物名称不能为空！", popup.mould.second, popup.mould.warning, "", "57%", null);
                return false;
            } else {
                var length = common.getLength(requestParam.giftName);
                if (length > 20) {
                    popup.mould.popTipsMould("礼物名称应为10个以下中文字符！", popup.mould.second, popup.mould.warning, "", "57%", null);
                    return false;
                }
            }

            if (common.isEmpty(requestParam.giftType)) {
                popup.mould.popTipsMould("请选择礼物类型！", popup.mould.second, popup.mould.warning, "", "57%", null);
                return false;
            }


            if (common.isEmpty(requestParam.useType)) {
                popup.mould.popTipsMould("请选择使用类型！", popup.mould.second, popup.mould.warning, "", "57%", null);
                return false;
            }

            gift.interface.saveForm(requestParam);
        },
        paramSearch: function () {
            gift.param.dataTable.ajax.reload();
        }

    },
    interface: {
        initDataTable: function () {
            gift.param.dataTable = datatableUtil.getByDatatables(gift.dtList, gift.dtParam, function (data) {
            });
        },
        initCategorys: function () {
            common.getByAjax(false, "get", "json", "/operationcenter/marketingRule/giftType/giftTypeCategorys", {}, function (data) {
                for (var key in data) {
                    gift.param.giftTypeOptions += '<option value=' + key + '>' + data[key] + '</option>';
                }
                $(".category-sel").append(gift.param.giftTypeOptions);
            }, function () {
            })
        },
        getUseTypes: function () {
            common.getByAjax(false, "get", "json", "/operationcenter/marketingRule/giftType/useTypes", {}, function (data) {
                $.each(data, function (index, useType) {
                    gift.param.useTypeOptions += '<option value=' + useType.id + '>' + useType.name + '</option>';
                });
                $(".use-type-sel").html(gift.param.useTypeOptions);
            }, function () {
            })
        },
        saveForm: function (requestParam) {
            common.getByAjax(false, "post", "json", "/operationcenter/marketingRule/giftType/add", requestParam, function (data) {
                if (data.pass) {
                    popup.mask.hideAllMask();
                    popup.mould.popTipsMould("保存成功！", popup.mould.first, popup.mould.success, "", "57%",
                        function () {
                            popup.mask.hideFirstMask();
                            gift.param.dataTable.ajax.reload();
                        }
                    );
                } else {
                    popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                }
            }, function () {
            })
        },
        updateStatus: function (id, disableStatus) {
            common.getByAjax(false, "post", "json", "/operationcenter/marketingRule/giftType/" + id + "/" + disableStatus, {}, function (data) {
                    popup.mould.popTipsMould("操作成功！", popup.mould.first, popup.mould.success, "", "57%", null);
                    gift.param.dataTable.ajax.reload();
                }, function () {
                    popup.mould.popTipsMould("操作失败！", popup.mould.first, popup.mould.warning, "", "57%", null);
                }
            )
        }
    }
};

$(function () {
    gift.init.initDataTable();
    gift.init.initGiftTypeCategorys();
    //gift.init.initUseTypes();

    $("#giftName").bind({
        keydown: function () {
            $("#giftName").autocomplete({
                source: function (request, response) {
                    $.ajax({
                        url: "/operationcenter/marketingRule/giftType/getByName",
                        type: "get",
                        dataType: "json",
                        data: {
                            paramWord: request.term.trim(),
                            pageSize: 10
                        },
                        success: function (data) {
                            response(data);
                        }
                    });
                },
                minLength: 0,
                autoFocus: false
            });
        }
    });

    $(".change-search").unbind("change").bind({
        change: function () {
            gift.param.dataTable.ajax.reload();
        }
    });

});
