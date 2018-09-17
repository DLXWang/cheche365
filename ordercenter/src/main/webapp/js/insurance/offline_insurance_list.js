var dataFunction = {
    "data": function (data) {
        data.area = $("#area").val();//省市
        data.institution = $("#institution").val();//出单机构
        data.insuranceComp = $("#insuranceComp").val();//保险公司
        data.balanceStartTime = $("#balanceStartTime").val();//起始时间
        data.balanceEndTime = $("#balanceEndTime").val();//截止时间
        data.policyNo = $("#policyNo").val();//保单号
        data.orderNo = $("#orderNo").val();//订单号
        data.licensePlateNo = $("#licensePlateNo").val();//车牌号
        if (common.isEmpty($("#issueStartTime").val()) || common.isEmpty($("#issueEndTime").val())) {
            offline_insurance.initialization.initIssueTime();
        }
        data.issueStartTime = $("#issueStartTime").val();//出单时间
        data.issueEndTime = $("#issueEndTime").val();//截止时间
        data.status = $("#status").val();//结算状态
    },
    "fnRowCallback": function (nRow, aData) {
        $payable = "<a href='javascript:;' onclick=offline_insurance.popupSublist('" + aData.policyNo + "','" + aData.insuranceType + "');>" + aData.paidAmount + "</a>";
        $status = (aData.differ == 0) ? "已结清" : aData.differ;
        $('td:eq(0)', nRow).html(aData.rebateId);
        $('td:eq(11)', nRow).html($payable);
        $('td:eq(12)', nRow).html($status);
    },
};
var offline_insurance_list = {
    "url": '/orderCenter/insurance/import/offlineInsuranceList',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": null, "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "area", "title": "省市", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "institution", "title": "出单机构", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "balanceStartTime", "title": "出单时间", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "orderNo", "title": "订单号", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "policyNo", "title": "保单号", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "licensePlateNo", "title": "车牌号", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "insuranceComp", "title": "保险公司", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "compulsoryRebate", "title": "交强险点位", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "commercialRebate", "title": "商业险点位", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "payableAmount", "title": "应收款（元）", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": null, "title": "保险公司已结款（元）", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": null, "title": "结算状态", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
    ],
};
var offline_insurance = {
    subListContent: "",
    initialization: {
        init: function () {
            this.initSelects();
            this.initBtn();
            this.initIssueTime();
            this.initSubList();
        },
        initIssueTime: function () {
            $("#issueEndTime").val(common.formatDate(new Date(), 'yyyy-MM-dd'));
            $("#issueStartTime").val(common.formatDate(new Date(), 'yyyy-MM-dd', -30));
        },
        initSelects: function () {
            offline_insurance.initChannels();
            offline_insurance.initCompanies();
            offline_insurance.initArea();
            offline_insurance.initInstitution();
        },
        initBtn: function () {
            $("#searchBtn").unbind("click").bind({
                click: function () {
                    $("#calculate_info").hide();
                    datatables.ajax.reload();
                }
            });
            $("#refreshBtn").unbind("click").bind({
                    click: function () {
                        $("#calculate_info").hide();
                        $(".able_clean").val("");
                    }
                }
            );
            $("#calculateBtn").unbind("click").bind({
                    click: function () {
                        $("#calculate_info").show();
                        common.getByAjax(true, "get", "json", "/orderCenter/insurance/import/offlineInsuranceCount",
                            {
                                area: $("#area").val(),//省市
                                institution: $("#institution").val(),//出单机构
                                insuranceComp: $("#insuranceComp").val(),//保险公司
                                balanceStartTime: $("#balanceStartTime").val(),//起始时间
                                balanceEndTime: $("#balanceEndTime").val(),//截止时间
                                policyNo: $("#policyNo").val(),//保单号
                                orderNo: $("#orderNo").val(),//订单号
                                licensePlateNo: $("#licensePlateNo").val(),//车牌号
                                issueStartTime: $("#issueStartTime").val(),//出单时间
                                issueEndTime: $("#issueEndTime").val(),//截止时间
                                status: $("#status").val()//结算状态
                            },
                            function (data) {
                                $("#count_num").html(data.num);
                                $("#all_payale").html(data.payableAmount);
                                $("#all_paid").html(data.paidAmount);
                                $("#all_diff").html(data.differ);
                            }, function () {
                            }
                        );
                    }
                }
            );
        },

        initSubList: function () {
            var detailContent = $("#sub_content");
            if (detailContent.length > 0) {
                offline_insurance.subListContent = detailContent.html();
                detailContent.remove();
            }
        },
    },


    popupSublist: function (policyNo, insuranceType) {
        popup.pop.popInput(false, offline_insurance.subListContent, popup.mould.first, "1300px", "550px", "30%", "30%");
        offline_insurance.subList(policyNo, insuranceType);
        parent.$("#close").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
    },

    subList: function (policyNo, insuranceType) {
        common.getByAjax(true, "get", "json", "/orderCenter/insurance/import/subList",
            {
                policyNo: policyNo
            },
            function (data) {
                window.parent.$("#subList tbody").empty();
                if (data) {
                    window.parent.$("#sub_times").html(data.countNum);
                    window.parent.$("#sub_paid").html(data.allRebate);
                    if (data.subList) {
                        var content = "";
                        if (insuranceType == 0) {
                            $.each(data.subList, function (i, model) {
                                content += "<tr>" +
                                    "<td class='text-center'>" + (i + 1) + "</td>" +//结算次数
                                    "<td class='text-center'>" + model.createTime + "</td>" +//导入时间
                                    "<td class='text-center'>" + model.paidTime + "</td>" +//到账时间
                                    "<td class='text-center'>0.00</td>" +//交强险点位
                                    "<td class='text-center'>" + model.rebate + "</td>" +//商业险点位
                                    "<td class='text-center'>" + model.rebateAmount + "</td>" +//已结款
                                    "<td class='text-center'>" + model.aging + "</td>" +//账龄（天）
                                    "</tr>";
                            });
                        } else {
                            $.each(data.subList, function (i, model) {
                                content += "<tr>" +
                                    "<td class='text-center'>" + (i + 1) + "</td>" +//结算次数
                                    "<td class='text-center'>" + model.createTime + "</td>" +//导入时间
                                    "<td class='text-center'>" + model.paidTime + "</td>" +//到账时间
                                    "<td class='text-center'>" + model.rebate + "</td>" +//交强险点位
                                    "<td class='text-center'>0.00</td>" +//商业险点位
                                    "<td class='text-center'>" + model.rebateAmount + "</td>" +//已结款
                                    "<td class='text-center'>" + model.aging + "</td>" +//账龄（天）
                                    "</tr>";
                            });
                        }
                        window.parent.$("#subList tbody").append(content);
                    }
                }
            }, function () {
            }
        );
    },
    initChannels: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/channel/getAllChannels", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function (i, model) {
                    options += "<option value='" + model.id + "'>" + model.description + "</option>";
                });

                $("#channelSel").append(options);
            }, function () {
            }
        );
    },
    initArea: function () {
        $("#area_input").bind({
            keyup: function () {
                if (common.isEmpty($(this).val())) {
                    CUI.select.hide();
                    $("#area_input").val("");
                    $("#area").val("");
                    return;
                }
                common.getByAjax(true, "get", "json", "/orderCenter/resource/areas/" + $(this).val(), {},
                    function (data) {
                        if (data == null) {
                            return;
                        }
                        var map = new Map();
                        $.each(data, function (i, model) {
                            map.put(model.id, model.name);
                        });
                        CUI.select.show($("#area_input"), 300, map, false, $("#area"));
                    }, function () {
                    }
                );
            }
        });
    },
    initInstitution: function () {
        $("#institution_input").bind({
            keyup: function () {
                if (common.isEmpty($(this).val())) {
                    CUI.select.hide();
                    $("#institution_input").val("");
                    $("#institution").val("");
                    return;
                }
                common.getByAjax(true, "get", "json", "/orderCenter/resource/institution/getByKeyWord",
                    {
                        keyword: $(this).val()
                    },
                    function (data) {
                        if (data == null) {
                            return;
                        }
                        var map = new Map();
                        $.each(data, function (i, model) {
                            map.put(model.id, model.name);
                        });
                        CUI.select.show($("#institution_input"), 300, map, false, $("#institution"));
                    }, function () {
                    }
                );
            }
        });
    },
    initCompanies: function () {
        $("#company_input").bind({
            keyup: function () {
                $("#company_input").autocomplete({
                    source: function (request, response) {
                        var keyword = request.term.trim();
                        if (common.validations.isEmpty(keyword) && common.validations.isEmpty($("#company_input").val())) {
                            $("#insuranceComp").val('');
                            return false
                        }
                        $.ajax({
                            url: "/orderCenter/resource/insuranceComp/getByKeyWord",
                            type: "get",
                            dataType: "json",
                            data: {
                                keyword: keyword
                            },
                            success: function (data) {
                                var models = [];
                                $.each(data, function (index, model) {
                                    models.push ({
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
                        $("#insuranceComp").val(ui.item.value);
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

    }
}
var datatables;
$(function () {
    offline_insurance.initialization.init();
    dt_labels.aLengthMenu = [[20, 15, 10], [20, 15, 10]],//暂时只写这一个
        datatables = datatableUtil.getByDatatables(offline_insurance_list, dataFunction.data, dataFunction.fnRowCallback);
});
