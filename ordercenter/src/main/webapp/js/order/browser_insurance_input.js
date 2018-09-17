/**
 * Created by cxy on 2016/12/13.
 */
var dataFunction = {
    "data": function (data) {
        var param = new Array;
        param[5] = 'createTime';
        param[6] = 'updateTime';
        data.orderColumn = data.order[0].column;
        data.orderDir = data.order[0].dir;
        data.sort = param[data.order[0].column];

        data.keyword = $("#keyword").val();
        //data.sort = $("#sort").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $orderNo = "<a href='order_detail.html?id=" + aData.purchaseOrderId + "' target='_blank'>" + aData.orderNo + "</a>";
        $edit = "<a href='javascript:;' onclick=orderInsurance.edit('" + aData.orderNo + "');>编辑</a>";
        $('td:eq(0)', nRow).html($orderNo);
        $('td:eq(7)', nRow).html($edit);
    },
}
var orderList = {
    "url": '/orderCenter/order/insurances',
    "type": "GET",
    "table_id": "list_tab",
    "order": [[6, "desc"]],
    "columns": [
        {"data": null, "title": "订单号", 'sClass': "text-center", "orderable": false},
        {"data": "licensePlateNo", "title": "车牌号", 'sClass': "text-center", "orderable": false},
        {"data": "owner", "title": "车主姓名", 'sClass': "text-center", "orderable": false},
        {"data": "insuranceInputter", "title": "保单录入者", 'sClass': "text-center", "orderable": false},
        {"data": "insuranceOperator", "title": "最后操作者", 'sClass': "text-center", "orderable": false},
        {"data": "createTime", "title": "创建时间", 'sClass': "text-center", "orderable": true},
        {"data": "updateTime", "title": "最后操作时间", 'sClass': "text-center", "orderable": true},
        {"data": null, "title": "", 'sClass': "text-center", "orderable": false},
    ],
}
var datatables;
/**
 * Created by wangfei on 2015/8/17.
 */
$(function () {
    dt_labels.order = [[6, "desc"]];//第2列的数据倒序排序 此条会通过参数传给服务器
    datatables = datatableUtil.getByDatatables(orderList, dataFunction.data, dataFunction.fnRowCallback);
    var properties = new Properties(1, "");
    /* 初始化查询 */
    orderInsurance.init();

    $("#myTab a").click(function (e) {
        e.preventDefault();
        $(this).tab("show");
        var href = $(this).attr("href").replace("#", "");
        if ("commercial" == href) {
            $("#commercial").show();
            $("#compulsory").hide();
        } else {
            $("#commercial").hide();
            $("#compulsory").show();
        }
    });

    /* 搜索 */
    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容", "first", "warning", "", "",
                    function () {
                        popup.mask.hideFirstMask(false);
                    }
                );
                return false;
            }
            datatableUtil.params.keyword = keyword;
            datatables.ajax.reload();
        }
    });

    var add = false;
    var orderLists = $("#orderList");
    var orderListContent;
    if (orderLists.length > 0) {
        orderListContent = orderLists.html();
        orderLists.remove();
    }
    /**
     * 新建保单
     */
    $("#newInsuranceBtn").bind({
        click: function () {
            $("#insuranceForm")[0].reset();
            $("#commercialEffectiveHour option:first").prop("selected", "selected");
            $("#commercialExpireHour option:last").prop("selected", "selected");
            $("#compulsoryEffectiveHour option:first").prop("selected", "selected");
            $("#compulsoryExpireHour option:last").prop("selected", "selected");
            //$(".image").hide();
            $(".del").hide();
            $(".hidden_input").val("");
            showEdit();
            $("#toCreate").val("添加保单");
            add = true;
        }
    });

    /* 取消按钮 */
    $("#toCancel").bind({
        click: function () {
            showContent();
            window.parent.scrollTo(0, 0);
        }
    });

    /* 保单失效日期联动 */
    $("#commercialEffectiveDate").bind({
        focus: function () {
            var commercialDate = $("#commercialEffectiveDate").val();
            if (!commercialDate) {
                $("#commercialExpireDate").val("");
            } else {
                var endDate = common.tools.addDays(commercialDate, 364)
                $("#commercialExpireDate").val(endDate);
            }
        }
    });

    $("#compulsoryEffectiveDate").bind({
        focus: function () {
            var compulsoryDate = $("#compulsoryEffectiveDate").val();
            if (!compulsoryDate) {
                $("#compulsoryExpireDate").val("");
            } else {
                var endDate = common.tools.addDays(compulsoryDate, 364)
                $("#compulsoryExpireDate").val(endDate);
            }
        }
    });

    $("#licensePlateNo").unbind("blur").bind({
        blur: function () {
            //initInstitution($("#insuranceCompanySel").val(),"");
            if (!add) {
                return;
            }
            common.ajax.getByAjax(false, "get", "json", "/orderCenter/order/licensePlateNo",
                {
                    licensePlateNo: $(this).val()
                },
                function (data) {
                    if (data == null || data.viewList.length == 0) {
                        return;
                    }
                    popup.pop.popInput(false, orderListContent, 'first', "600px", "220px", "50%", "50%");
                    CUI.grid.dom = parent.$("#tabOrder tbody");
                    CUI.grid.store = data;
                    CUI.grid.columns = [
                        {dataIndex: 'orderNo'},
                        {dataIndex: 'licensePlateNo'},
                        {dataIndex: 'owner'},
                        {
                            dataIndex: '', renderer: function (value, rowIndex, rowStore) {
                                if (!common.isEmpty(rowStore.insuranceInputter)) {
                                    return "反录订单";
                                } else {
                                    return "正常订单";
                                }
                            }
                        },
                        {
                            dataIndex: '', renderer: function (value, rowIndex, rowStore) {
                                return "<a class='edit' orderNo='" + rowStore.orderNo + "' href='javascript:;'>编辑</a>";
                                // if (!common.isEmpty(rowStore.insuranceInputter)) {
                                //     return "<a class='edit' orderNo='" + rowStore.orderNo + "' href='javascript:;'>编辑</a>";
                                // } else {
                                //     return "<a target='_blank' href='/page/order/client_insurance_input.html?id=" + rowStore.purchaseOrderId + "' >编辑</a>";
                                // }
                            }
                        },
                    ];
                    CUI.grid.fill();
                    parent.$(".edit").unbind("click").bind({
                        click: function () {
                            orderInsurance.edit($(this).attr("orderNo"));
                            popup.mask.hideFirstMask(false);
                            $("#licensePlateNo").attr("readonly", "readonly");
                        }
                    });
                    parent.$("#order_list_close").unbind("click").bind({
                        click: function () {
                            popup.mask.hideFirstMask(false);
                        }
                    });

                }, function () {
                }
            );
        }
    })

    $("#agent").bind({
        keyup: function () {
            if (common.isEmpty($(this).val())) {
                CUI.select.hide();
                $("#agent").val("");
                return;
            }
            common.getByAjax(true, "get", "json", "/orderCenter/resource/agent/getEnableAgentsByKeyword",
                {
                    keyword: $(this).val()
                },
                function (data) {
                    if (data == null) {
                        return;
                    }
                    var map = new Map();
                    $.each(data, function (i, model) {
                        map.put(model.id, model.agentName);
                    });
                    CUI.select.show($("#agent"), 300, map, false, $("#recommender"));
                }, function () {
                }
            );
        }
    });

    $("#input_area").bind({
        keyup: function () {
            if (common.isEmpty($(this).val())) {
                CUI.select.hide();
                $("#input_area").val("");
                return;
            }
            common.getByAjax(true, "get", "json", "/orderCenter/resource/areas/getByKeyWord",
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
                    CUI.select.show($("#input_area"), 300, map, false, $("#area"));
                }, function () {
                }
            );
        }
    });

    $("input:radio[name='newCar']").unbind("change").bind({
        change: function () {
            if ($(this).val() == "1") {
                $("#licensePlateNo").attr("readonly", true);
            } else {
                $("#licensePlateNo").attr("readonly", false);
            }
        }
    });

    var validOptions_new = {
        onkeyup: false,
        onfocusout: false,
        rules: {
            //licensePlateNo: {
            //    required: true
            //},
            owner: {
                required: true
            },
            //identity: {
            //    required: true
            //},
            vinNo: {
                required: true
            },
            engineNo: {
                required: true
            },
            enrollDate: {
                required: true
            },
            brand: {
                required: true
            },
            //insuredIdNo: {
            //    required: true
            //},
            insuredName: {
                required: true
            },
            insuranceCompany: {
                required: true
            },
            originalPremium: {
                required: true,
                number: true
            },
            rebateExceptPremium: {
                required: true,
                number: true
            },
            recommender: {
                required: true
            },
            area: {
                required: true
            },
            channel: {
                required: true
            }
        },
        messages: {
            licensePlateNo: {
                required: "请输入车牌号"
            },
            owner: {
                required: "请输入车主姓名"
            },
            identity: {
                required: "请输入车主身份证号"
            },
            vinNo: {
                required: "请输入车架号"
            },
            engineNo: {
                required: "请输入发动机号"
            },
            enrollDate: {
                required: "请选择初登日期"
            },
            brand: {
                required: "请输入厂牌型号"
            },
            //insuredIdNo: {
            //    required: "请输入被保险人身份证"
            //},
            insuredName: {
                required: "请输入被保险人姓名"
            },
            insuranceCompany: {
                required: "请选择保险公司"
            },
            originalPremium: {
                required: "请输入保险原始总金额",
                number: "请输入正确的保险原始总金额"
            },
            rebateExceptPremium: {
                required: "请输入扣除返点后总金额",
                number: "请输入正确的扣除返点后总金额"
            },
            recommender: {
                required: "请选择推荐人"
            },
            area: {
                required: "请选择投保区域"
            },
            channel: {
                required: "请选择渠道"
            }
        },
        showErrors: function (errorMap, errorList) {
            if (errorList.length) {
                showErrorMsg(errorList[0].message);
            }
        },
        submitHandler: function (form) {
            $(".premium").each(function () {
                if (common.isEmpty($(this).val())) {
                    $(this).val("0.00");
                }
            });
            if (!personalValidation.valid()) {
                return;
            }
            if (!warningValidation.valid(form, properties)) {
                return;
            } else {
                orderInsurance.dosubmit(form, properties);
            }

        }
    };

    //sourceNewly.init(validOptions_new);
    $("#insuranceForm").validate(validOptions_new);

    $("#resendGiftDisplayBtn").unbind("click").bind({
        click: function () {
            quoteResendGift.displayResendGifts(false, popup.mould.first);
        }
    });
});

//错误提示框
var showErrorMsg = function (msg) {
    popup.mould.popTipsMould(false, msg, "first", "warning", "", "",
        function () {
            popup.mask.hideFirstMask(false);
        }
    );
};

//报警提示框
var showWarningMsg = function (msg, form, properties) {
    popup.mould.popConfirmMould(false, msg, "first", ["继续", "取消"], "",
        function () {
            popup.mask.hideFirstMask(false);
            orderInsurance.dosubmit(form, properties);
        },
        function () {
            popup.mask.hideFirstMask(false);
        }
    );
};

var personalValidation = {
    valid: function () {
        return this.validation.validLicenseNo() && this.validation.validOwner() && this.validation.validIdentity()
            && this.validation.validVinNo() && this.validation.validEngineNo() && this.validation.validInsuredName()
            && this.validation.validInsuredIdNo() && this.validation.validOthers() && this.validation.validateCommercial()
            && this.validation.validateCompulsory() && this.validation.validRecommender() && this.validation.validateApplicant();
    },
    validation: {
        validLicenseNo: function () {
            if ($("input:radio[name='newCar']:checked").val() == 0 && !common.validateLicenseNo($("#licensePlateNo").val())) {
                showErrorMsg("车牌号填写错误");
                return false;
            }

            return true;
        },
        validOwner: function () {
            if (!common.validateName($("#owner").val())) {
                showErrorMsg("车主姓名填写错误");
                return false;
            }

            return true;
        },
        validIdentity: function () {
            //if (!common.isIdCardNo($("#identity").val())) {
            //    showErrorMsg("车主身份证号填写错误");
            //    return false;
            //}

            return true;
        },
        validVinNo: function () {
            if (!common.validateVinNo($("#vinNo").val())) {
                showErrorMsg("车架号填写错误");
                return false;
            }

            return true;
        },
        validEngineNo: function () {
            if (!common.validateEngineNo($("#engineNo").val())) {
                showErrorMsg("发动机号填写错误");
                return false;
            }

            return true;
        },
        validInsuredIdNo: function () {
            //if (!common.isIdCardNo($("#insuredIdNo").val())) {
            //    showErrorMsg("被保险人身份证号填写错误");
            //    return false;
            //}
            return true;
        },
        validInsuredName: function () {
            if (!common.validateName($("#insuredName").val())) {
                showErrorMsg("被保险人姓名填写错误");
                return false;
            }

            return true;
        },
        validRecommender: function () {
            if ($("#orderType").val() == 1 && common.isEmpty($("#mobile").val())) {
                showErrorMsg("请输入用户手机号");
                return false;
            }

            if ($("#orderType").val() == 2 && !common.validateName($("#recommender").val())) {
                showErrorMsg("请选择推荐人");
                return false;
            }
            return true;
        },
        validOthers: function () {
            var _commercialPremium = $("#commercialPremium").val();//商业险总保费
            var _compulsoryPremium = $("#compulsoryPremium").val();//交强险保费
            var institution = $("#institution").val();
            var area = $("#area").val();
            var applicantDate = $("#applicantDate").val();
            if (_commercialPremium <= 0 && _compulsoryPremium <= 0) {
                showErrorMsg("商业险和交强险请至少选择一种进行录入");
                return false;
            }
            if (common.isEmpty(area)) {
                showErrorMsg("请选择投保区域");
                return false;
            }

            if (common.isEmpty(applicantDate)) {
                showErrorMsg("请选择投保时间");
                return false;
            }
            return true;
        },
        /* 验证商业险表单 */
        validateCommercial: function () {
            var flag = true;
            var error_text = "";
            var total_premium = 0;
            let totalIop = 0;
            /*不计免赔总额*/
            var iop = $("#iop").val();
            if (!common.isEmpty(iop) && !common.isMoney(iop)) {
                error_text = "不计免赔总额格式错误";
                flag = false;
            }
            /*商业险浮动系数*/
            var discount = $("#discount").val();
            if (!common.isEmpty(discount) && (discount < 0 || discount > 100 || !common.isNumber(discount))) {
                error_text = "商业险浮动系数格式错误";
                flag = false;
            }

            //是否投保
            var isBuying = false;
            /* 机动车辆损失险 */
            var _amount = $("#damageAmount").val();//保额
            var _premium = $("#damagePremium").val();//保费
            var _iop = $("#damageIop").val();//不计免赔

            if (_iop != 0) {
                isBuying = true;
                if (_amount == 0) {
                    error_text = "请填入机动车辆损失险的保额";
                    flag = false;
                } else if (_premium == 0) {
                    error_text = "请填入机动车辆损失险的保费";
                    flag = false;
                }
            } else {
                if (_premium == 0) {
                    if (_amount != 0) {
                        error_text = "请填入机动车辆损失险的保费";
                        flag = false;
                    }
                } else {
                    if (_amount == 0) {
                        error_text = "请填入机动车辆损失险的保额";
                        flag = false;
                    } else {
                        isBuying = true;
                    }
                }
            }

            if (flag) {
                if (!common.isEmpty(_amount) && !common.isMoney(_amount)) {
                    error_text = "机动车辆损失险的保额格式错误";
                    flag = false;
                } else if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                    error_text = "机动车辆损失险的保费格式错误";
                    flag = false;
                } else if (!common.isEmpty(_iop) && !common.isMoney(_iop)) {
                    error_text = "机动车辆损失险的不计免赔格式错误";
                    flag = false;
                }
                total_premium += parseFloat(_premium);
                totalIop += parseFloat(_iop)
            }
            /* 第三者责任险 */
            if (flag) {
                _amount = $("#thirdPartyAmountSel").val();//保额
                _premium = $("#thirdPartyPremium").val();//保费
                _iop = $("#thirdPartyIop").val();//不计免赔

                if (_iop != 0) {
                    isBuying = true;
                    if (_amount == 0) {
                        error_text = "请选择第三者责任险的保额";
                        flag = false;
                    } else if (_premium == 0) {
                        error_text = "请输入第三者责任险的保费";
                        flag = false;
                    }
                } else {
                    if (_premium == 0) {
                        if (_amount != 0) {
                            error_text = "请输入第三者责任险的保费";
                            flag = false;
                        }
                    } else {
                        if (_amount == 0) {
                            error_text = "请选择第三者责任险的保额";
                            flag = false;
                        } else {
                            isBuying = true;
                        }
                    }
                }
                if (flag) {
                    if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                        error_text = "第三者责任险的保费格式错误";
                        flag = false;
                    } else if (!common.isEmpty(_iop) && !common.isMoney(_iop)) {
                        error_text = "第三者责任险的不计免赔格式错误";
                        flag = false;
                    }
                    total_premium += parseFloat(_premium);
                    totalIop += parseFloat(_iop)
                }
            }

            /* 车上人员责任险(司机) */
            if (flag) {
                _amount = $("#driverAmountSel").val();//保额
                _premium = $("#driverPremium").val();//保费
                _iop = $("#driverIop").val();//不计免赔
                if (_iop != 0) {
                    isBuying = true;
                    if (_amount == 0) {
                        error_text = "请选择车上人员责任险(司机)的保额";
                        flag = false;
                    } else if (_premium == 0) {
                        error_text = "请输入车上人员责任险(司机)的保费";
                        flag = false;
                    }
                } else {
                    if (_premium == 0) {
                        if (_amount != 0) {
                            error_text = "请输入车上人员责任险(司机)的保费";
                            flag = false;
                        }
                    } else {
                        if (_amount == 0) {
                            error_text = "请选择车上人员责任险(司机)的保额";
                            flag = false;
                        } else {
                            isBuying = true;
                        }
                    }
                }
                if (flag) {
                    if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                        error_text = "车上人员责任险(司机)的保费格式错误";
                        flag = false;
                    } else if (!common.isEmpty(_iop) && !common.isMoney(_iop)) {
                        error_text = "车上人员责任险(司机)的不计免赔格式错误";
                        flag = false;
                    }
                    total_premium += parseFloat(_premium);
                    totalIop += parseFloat(_iop)
                }
            }

            /* 车上人员责任险(乘客) */
            if (flag) {
                _amount = $("#passengerAmountSel").val();//保额
                _premium = $("#passengerPremium").val();//保费
                _iop = $("#passengerIop").val();//不计免赔

                if (_iop != 0) {
                    isBuying = true;
                    if (_amount == 0) {
                        error_text = "请选择车上人员责任险(乘客)的保额";
                        flag = false;
                    } else if (_premium == 0) {
                        error_text = "请输入车上人员责任险(乘客)的保费";
                        flag = false;
                    }
                } else {
                    if (_premium == 0) {
                        if (_amount != 0) {
                            error_text = "请输入车上人员责任险(乘客)的保费";
                            flag = false;
                        }
                    } else {
                        if (_amount == 0) {
                            error_text = "请选择车上人员责任险(乘客)的保额";
                            flag = false;
                        } else {
                            isBuying = true;
                        }
                    }
                }
                if (flag) {
                    if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                        error_text = "车上人员责任险(乘客)的保费格式错误";
                        flag = false;
                    } else if (!common.isEmpty(_iop) && !common.isMoney(_iop)) {
                        error_text = "车上人员责任险(乘客)的不计免赔格式错误";
                        flag = false;
                    }
                    total_premium += parseFloat(_premium);
                    totalIop += parseFloat(_iop)
                }
            }

            /* 盗抢险 */
            if (flag) {
                _amount = $("#theftAmount").val();//保额
                _premium = $("#theftPremium").val();//保费
                _iop = $("#theftIop").val();//不计免赔

                if (_iop != 0) {
                    isBuying = true;
                    if (_amount == 0) {
                        error_text = "请输入盗抢险的保额";
                        flag = false;
                    } else if (_premium == 0) {
                        error_text = "请输入盗抢险的保费";
                        flag = false;
                    }
                } else {
                    if (_premium == 0) {
                        if (_amount != 0) {
                            error_text = "请输入盗抢险的保费";
                            flag = false;
                        }
                    } else {
                        if (_amount == 0) {
                            error_text = "请输入盗抢险的保额";
                            flag = false;
                        } else {
                            isBuying = true;
                        }
                    }
                }
                if (flag) {
                    if (!common.isEmpty(_amount) && !common.isMoney(_amount)) {
                        error_text = "盗抢险的保额格式错误";
                        flag = false;
                    } else if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                        error_text = "盗抢险的保费格式错误";
                        flag = false;
                    } else if (!common.isEmpty(_iop) && !common.isMoney(_iop)) {
                        error_text = "盗抢险的不计免赔格式错误";
                        flag = false;
                    }
                    total_premium += parseFloat(_premium);
                    totalIop += parseFloat(_iop)
                }
            }

            /* 车身划痕损失险 */
            if (flag) {
                _amount = $("#scratchAmountSel").val();//保额
                _premium = $("#scratchPremium").val();//保费
                _iop = $("#scratchIop").val();//不计免赔

                if (_iop != 0) {
                    isBuying = true;
                    if (_amount == 0) {
                        error_text = "请选择车身划痕损失险的保额";
                        flag = false;
                    } else if (_premium == 0) {
                        error_text = "请输入车身划痕损失险的保费";
                        flag = false;
                    }
                } else {
                    if (_premium == 0) {
                        if (_amount != 0) {
                            error_text = "请输入车身划痕损失险的保费";
                            flag = false;
                        }
                    } else {
                        if (_amount == 0) {
                            error_text = "请选择车身划痕损失险的保额";
                            flag = false;
                        } else {
                            isBuying = true;
                        }
                    }
                }
                if (flag) {
                    if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                        error_text = "车身划痕损失险的保费格式错误";
                        flag = false;
                    } else if (!common.isEmpty(_iop) && !common.isMoney(_iop)) {
                        error_text = "车身划痕损失险的不计免赔格式错误";
                        flag = false;
                    }
                    total_premium += parseFloat(_premium);
                    totalIop += parseFloat(_iop)
                }
            }

            /* 自燃损失险 */
            if (flag) {
                _amount = $("#spontaneousLossAmount").val();//保额
                _premium = $("#spontaneousLossPremium").val();//保费
                _iop = $("#spontaneousLossIop").val();

                if (_iop != 0) {
                    isBuying = true;
                    if (_amount == 0) {
                        error_text = "请输入自燃损失险的保额";
                        flag = false;
                    } else if (_premium == 0) {
                        error_text = "请输入自燃损失险的保费";
                        flag = false;
                    }
                } else {
                    if (_premium == 0) {
                        if (_amount != 0) {
                            error_text = "请输入自燃损失险的保费";
                            flag = false;
                        }
                    } else {
                        if (_amount == 0) {
                            error_text = "请输入自燃损失险的保额";
                            flag = false;
                        } else {
                            isBuying = true;
                        }
                    }
                }
                if (flag) {
                    if (!common.isEmpty(_amount) && !common.isMoney(_amount)) {
                        error_text = "自燃损失险的保额格式错误";
                        flag = false;
                    } else if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                        error_text = "自燃损失险的保费格式错误";
                        flag = false;
                    }
                    total_premium += parseFloat(_premium);
                    totalIop += parseFloat(_iop)
                }
            }

            /* 玻璃单独破碎险 */
            if (flag) {
                var _glassTypeSel = $("#glassTypeSel").val();//类型
                _premium = $("#glassPremium").val();//保费

                if (_premium == 0) {
                    if (_glassTypeSel != 0) {
                        error_text = "请选择玻璃单独破碎险的保费";
                        flag = false;
                    }
                } else {
                    if (_glassTypeSel == 0) {
                        error_text = "请输入玻璃单独破碎险的类型";
                        flag = false;
                    } else {
                        isBuying = true;
                    }
                }
                if (flag) {
                    if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                        error_text = "玻璃单独破碎险的保费格式错误";
                        flag = false;
                    }
                    total_premium += parseFloat(_premium);
                }
            }

            /* 发动机特别损失险 */
            if (flag) {
                _premium = $("#enginePremium").val();//保费
                _iop = $("#engineIop").val();//不计免赔

                if (_iop != 0) {
                    isBuying = true;
                    if (_premium == 0) {
                        error_text = "请输入发动机特别损失险的保费";
                        flag = false;
                    }
                }
                if (flag) {
                    if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                        error_text = "发动机特别损失险的保费格式错误";
                        flag = false;
                    } else if (!common.isEmpty(_iop) && !common.isMoney(_iop)) {
                        error_text = "发动机特别损失险的不计免赔格式错误";
                        flag = false;
                    }
                    total_premium += parseFloat(_premium);
                    totalIop += parseFloat(_iop)
                }
            }

            /*无法找到第三方特约险*/
            if (flag) {
                _premium = $("#unableFindThirdPartyPremium").val();//保费
                if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                    error_text = "无法找到第三方特约险的保费格式错误";
                    flag = false;
                }
                total_premium += parseFloat(_premium);
            }

            /*指定专修厂险*/
            if (flag) {
                _premium = $("#designatedRepairShopPremium").val();//保费
                if (!common.isEmpty(_premium) && !common.isMoney(_premium)) {
                    error_text = "指定专修厂险的保费格式错误";
                    flag = false;
                }
                total_premium += parseFloat(_premium);
            }

            /* 验证商业险基本信息 */
            if (flag && isBuying) {
                var _commercialPolicyNo = $("#commercialPolicyNo").val();//保单号
                var _commercialPremium = $("#commercialPremium").val();//商业险总保费
                let $iop = $("#iop").val();//商业险总保费
                var _commercialEffectiveDate = $("#commercialEffectiveDate").val();//保险生效日期
                var _commercialExpireDate = $("#commercialExpireDate").val();//保险失效日期
                if (!common.isMoney(_commercialPremium)) {
                    error_text = "商业险总保费格式错误";
                    flag = false;
                } else if (common.isEmpty(_commercialPolicyNo)) {
                    error_text = "请输入商业险保单号";
                    flag = false;
                } else if (common.isEmpty(_commercialPremium)) {
                    error_text = "请输入商业险总保费";
                    flag = false;
                } else if (!_commercialEffectiveDate) {
                    error_text = "请输入商业险的生效日期";
                    flag = false;
                } else if (!_commercialExpireDate) {
                    error_text = "请输入商业险的失效日期";
                    flag = false;
                } else if (Math.abs(parseFloat($iop)- totalIop)>0.01) {
                    error_text = "不计免赔总额错误";
                    flag = false;
                } else if (Math.abs(parseFloat(_commercialPremium)-total_premium-parseFloat(iop))>0.01) {

                    error_text = "商业险总保费错误";
                    flag = false;
                }
            }

            if (!flag) {
                showErrorMsg(error_text);
            }

            return flag;
        },
        /* 验证交强险表单 */
        validateCompulsory: function () {
            var flag = true;
            var error_text = "";
            /*交强险险浮动系数*/
            var discountCI = $("#discountCI").val();
            if (!common.isEmpty(discountCI) && (discountCI < 0 || discountCI > 100 || !common.isNumber(discountCI))) {
                error_text = "交强险浮动系数格式错误";
                flag = false;
            }
            //是否投保
            var isBuying = false;
            var _compulsoryPremium = $("#compulsoryPremium").val();//交强险保费
            var _autoTax = $("#autoTax").val();//车船税
            if (!common.isMoney(_compulsoryPremium)) {
                error_text = "交强险总保费格式错误";
                flag = false;
            }
            if (!common.isMoney(_autoTax)) {
                error_text = "车船税格式错误";
                flag = false;
            }

            if (_autoTax != 0) {
                isBuying = true;
                if (_compulsoryPremium == 0) {
                    error_text = "请填入交强险的车船税";
                    flag = false;
                }
            } else {
                if (_compulsoryPremium != 0) {
                    isBuying = true;
                }
            }

            /* 验证交强险基本信息 */
            if (flag && isBuying) {
                var _compulsoryPolicyNo = $("#compulsoryPolicyNo").val();//保单号
                var _compulsoryEffectiveDate = $("#compulsoryEffectiveDate").val();//保险生效日期
                var _compulsoryExpireDate = $("#compulsoryExpireDate").val();//保险失效日期
                if (common.isEmpty(_compulsoryPolicyNo)) {
                    error_text = "请输入交强险保单号";
                    flag = false;
                } else if (!_compulsoryEffectiveDate) {
                    error_text = "请输入交强险的生效日期";
                    flag = false;
                } else if (!_compulsoryExpireDate) {
                    error_text = "请输入交强险的失效日期";
                    flag = false;
                }
            }

            if (!flag) {
                showErrorMsg(error_text);
            }

            return flag;
        },
        /* 验证投保人是否填写 */
        validateApplicant: function () {
            var flag = true;
            var error_text = '';
            if (common.isEmpty($("#applicantName").val())) {
                error_text = "请输入投保人姓名";
                flag = false;
            }
            //if(common.isEmpty($("#applicantIdNo").val())){
            //    error_text="请输入投保人身份证号";
            //    flag = false;
            //}
            if (!flag) {
                showErrorMsg(error_text);
            }
            return flag;
        }
    }
};

var warningValidation = {
    valid: function (form, properties) {
        return this.validation(form, properties);
    },
    validation: function (form, properties) {
        var flag = true;
        var error_text = "";
        var _amount = 0.0;//保额
        var _premium = 0.0;//保费

        /*交强险保费*/
        if (parseFloat($("#compulsoryPremium").val()) > 5500) {
            error_text += "交强险保费可能存在问题<br/>";
            flag = false;
        }

        /*车船税*/
        if (parseFloat($("#autoTax").val()) > 1200) {
            error_text += "车船税可能存在问题<br/>";
            flag = false;
        }

        /*商业险保费*/
        if (parseFloat($("#commercialPremium").val()) > 20000) {
            error_text += "商业险总保费大于20000元<br/>";
            flag = false;
        }

            /* 第三者责任险 */
            _amount = $("#thirdPartyAmountSel").val();//保额
            _premium = $("#thirdPartyPremium").val();//保费
            if (parseFloat(_premium) > 0 && parseFloat(_premium) /parseFloat( _amount) > 0.06) {
                error_text += "第三者责任险保额或保费数据可能不正确<br/>";
                flag = false;
            }

            /* 车上人员责任险(司机) */
            _amount = $("#driverAmountSel").val();//保额
            _premium = $("#driverPremium").val();//保费
            if (parseFloat(_premium) > 0 &&  parseFloat(_premium) /parseFloat( _amount) > 0.4) {
                error_text += "车上人员责任险(司机)保额或保费数据可能不正确<br/>";
                flag = false;
            }

            /* 车上人员责任险(乘客) */
            _amount = $("#passengerAmountSel").val();//保额
            _premium = $("#passengerPremium").val();//保费
            if (parseFloat(_premium) > 0 &&  parseFloat(_premium) /parseFloat( _amount) > 0.3) {
                error_text += "车上人员责任险(乘客)保额或保费数据可能不正确<br/>";
                flag = false;
            }

            /* 盗抢险 */
            _amount = $("#theftAmount").val();//保额
            _premium = $("#theftPremium").val();//保费
            if (parseFloat(_premium) > 0 &&  parseFloat(_premium) /parseFloat( _amount) > 0.06) {
                error_text += "盗抢险保额或保费数据可能不正确<br/>";
                flag = false;
            }

            /* 机动车辆损失险 */
            _amount = $("#damageAmount").val();//保额
            _premium = $("#damagePremium").val();//保费
            if (parseFloat(_premium) > 0 &&  parseFloat(_premium) /parseFloat( _amount) > 0.06) {
                error_text += "机动车辆损失险保额或保费数据可能不正确<br/>";
                flag = false;
            }

            /* 车身划痕损失险 */
            _amount = $("#scratchAmountSel").val();//保额
            _premium = $("#scratchPremium").val();//保费
            if (parseFloat(_premium) > 0 &&  parseFloat(_premium) /parseFloat( _amount) > 0.4) {
                error_text += "车身划痕损失险保额或保费数据可能不正确<br/>";
                flag = false;
            }

            /* 自燃损失险 */
            _amount = $("#spontaneousLossAmount").val();//保额
            _premium = $("#spontaneousLossPremium").val();//保费
            if (parseFloat(_premium) > 0 &&  parseFloat(_premium) /parseFloat( _amount) > 0.4) {
                error_text += "自燃损失险保额或保费数据可能不正确<br/>";
                flag = false;
            }

            /* 发动机特别损失险 */
            _amount = $("#engineAmount").val();//保额
            _premium = $("#enginePremium").val();//保费
            if (parseFloat(_premium) > 0 &&  parseFloat(_premium) /parseFloat( _amount) > 0.4) {
                error_text += "发动机特别损失险保额或保费数据可能不正确<br/>";
                flag = false;
            }

        if (!flag) {
            showWarningMsg(error_text, form, properties);
        }
        return flag;
    }

};

var orderInsurance = {
    init: function () {
        var options = "";
        for (var index = 0; index < 25; index++) {
            options += "<option value='" + index + "'>" + index + "</option>"
        }
        $("#commercialEffectiveHour").html(options);
        $("#commercialExpireHour").html(options);
        $("#commercialEffectiveHour option:first").prop("selected", "selected");
        $("#commercialExpireHour option:last").prop("selected", "selected");
        $("#compulsoryEffectiveHour").html(options);
        $("#compulsoryExpireHour").html(options);
        $("#compulsoryEffectiveHour option:first").prop("selected", "selected");
        $("#compulsoryExpireHour option:last").prop("selected", "selected");

        /* 保险公司 */
        common.getByAjax(true, "get", "json", "/orderCenter/resource/insuranceCompany/getEnableCompanies", null,
            function (data) {
                if (data == null) {
                    return false;
                }

                $("#insuranceCompanySel").append("<option value=''>请选择保险公司</option>");
                $("#insuranceCompanySel").append(common.getFormatOptionList(data, 'id', 'name'));
            }, function () {
            }
        );

        /* 投保人证件类型 */
        common.getByAjax(false, "get", "json", "/orderCenter/resource/identityTypes", {},
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.description + "</option>";
                    });
                    $("#identityType").append(options);
                    $("#applicantIdType").append(options);
                    $("#insuredIdType").append(options);
                }
            },
            function () {
            }
        );

        /* 投保区域 */
        common.getByAjax(true, "get", "json", "/orderCenter/resource/areas", null,
            function (data) {
                if (data == null) {
                    return false;
                }
                $("#areaSel").append(common.getFormatOptionList(data, 'id', 'name'));
            }, function () {
            }
        );

        $("#insuranceCompanySel").bind({
            change: function () {
                initInstitution();
            }
        });
        $("#institution").bind({
            change: function () {
                $("#downCommercialRebate").val($(this).find("option:selected").attr("commercialRebate"));
                $("#downCompulsoryRebate").val($(this).find("option:selected").attr("compulsoryRebate"));
            }
        });
        $("#applicantDate").bind({
            blur: function () {
                initInstitution();
            }
        });
        $("#channelType").bind({
            change: function () {
                if ($(this).val() == '') {
                    return;
                }
                orderInsurance.initChannel('add' + $(this).val())
            }
        });
        $("#channel").bind({
            change: function () {
                var tag = $(this).find("option:selected").attr("tag");
                if (tag == '138') {
                    $("#resendGiftDiv").hide();
                } else {
                    $("#resendGiftDiv").show();
                }
            }
        });
        $("#orderType").bind({
            change: function () {
                if ($(this).val() == "1") {
                    $("#mobileType").show();
                    $("#agentType").hide();
                    $("#agent").val("");
                } else {
                    $("#mobileType").hide();
                    $("#mobile").val("");
                    $("#agentType").show();
                }
            }
        });
    },
    initChannel: function (type) {
        var url;
        if (type == 'add1') {
            url = "/orderCenter/resource/channel/getSelfChannelEnable";
        } else if (type == 'add2') {
            url = "/orderCenter/resource/dataSourceChannelEnable";
        } else if (type == 'edit1') {
            url = "/orderCenter/resource/channel/getSelfChannel";
        } else if (type == 'edit2') {
            url = "/orderCenter/resource/dataSourceChannel";
        }
        common.getByAjax(false, "get", "json", url, null,
            function (data) {
                if (data) {
                    $("#channel").empty();
                    $.each(data, function (index, channel) {
                        $("#channel").append('<option value="' + channel.id + '" tag="' + channel.tag + '">' + channel.description + '</option>');
                    });
                }
            }, function () {
            }
        );
    },
    saveInsurance: function (form, properties) {
        $("#toCreate").attr("disabled", true);
        var data = $(form).serialize();
        var resendGiftList = quoteResendGift.giftList;
        // data.push("resendGiftList",JSON.stringify(resendGiftList));
        data = decodeURIComponent(data, true);
        data = common.tools.strToObj(data);
        //data.resendGiftList=JSON.stringify(resendGiftList);
        data.resendGiftList = resendGiftList;
        common.getByAjax(true, "post", "json", "/orderCenter/order/insurance", data,
            function (data) {
                if (data.pass) {
                    popup.mould.popTipsMould(false, "添加保单成功", "first", "success", "", "56%",
                        function () {
                            datatableUtil.params.keyword = keyword;
                            datatables.ajax.reload();
                            //properties.currentPage = 1;
                            //properties.keyword = "";
                            //orderInsurance.list(properties);
                            showContent();
                            window.parent.scrollTo(0, 0);
                            popup.mask.hideFirstMask(false);
                        }
                    );
                } else {
                    popup.mould.popTipsMould(false, data.message, "first", "warning", "", "56%",
                        function () {
                            popup.mask.hideFirstMask(false);
                        }
                    );
                }

                $("#toCreate").attr("disabled", false);
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常", "first", "error", "", "56%",
                    function () {
                        popup.mask.hideFirstMask(false);
                    }
                );
                $("#toCreate").attr("disabled", false);
            }
        );
    },
    fillData: function (data, refreshCommercial, refreshCompulsory) {
        $("#orderNo").val(common.checkToEmpty(data.orderNo));
        $("#licensePlateNo").val(common.checkToEmpty(data.licensePlateNo));
        $("#owner").val(common.checkToEmpty(data.owner));
        $("#identity").val(common.checkToEmpty(data.identity));
        $("#identityType").val(common.checkToEmpty(data.identityType.id));
        $("#vinNo").val(common.checkToEmpty(data.vinNo));
        $("#engineNo").val(common.checkToEmpty(data.engineNo));
        $("#enrollDate").val(common.checkToEmpty(data.enrollDate));
        $("#brand").val(common.checkToEmpty(data.brand));
        $("#insuredIdNo").val(common.checkToEmpty(data.insuredIdNo));
        $("#insuredIdType").val(common.checkToEmpty(data.insuredIdType ? data.insuredIdType.id : ""));
        $("#applicantIdType").val(common.checkToEmpty(data.applicantIdType ? data.applicantIdType.id : ""));
        $("#insuredName").val(common.checkToEmpty(data.insuredName));
        $("#insuranceCompanySel").val(common.checkToEmpty(data.insuranceCompany));
        $("#originalPremium").val(common.formatMoney(data.originalPremium, 2));
        $("#rebateExceptPremium").val(common.formatMoney(data.rebateExceptPremium, 2));
        $("#expressCompany").val(common.checkToEmpty(data.expressCompany));
        $("#trackingNo").val(common.checkToEmpty(data.trackingNo));
        $("#recommender").val(common.checkToEmpty(data.recommender));
        $("#giftDetailsText").html(common.checkToEmpty(data.giftInfo));
        $("#channelType").val(common.checkToEmpty(data.channelType));
        $("#orderType").val(common.checkToEmpty(data.orderType));
        orderInsurance.initChannel('edit' + data.channelType)
        $("#channel").val(data.channel);
        if (!common.isEmpty(data.recommenderName)) {
            $("#agent").val(data.recommenderName);
            $("#orderType").val(2);
            $("#agentType").show();
            $("#mobileType").hide();
        } else if (!common.isEmpty(data.mobile)) {
            $("#mobile").val(data.mobile);
            $("#orderType").val(1);
            $("#agentType").hide();
            $("#mobileType").show();
        }
        //$("#areaSel").val(common.checkToEmpty(data.area));
        $("#area").val(common.checkToEmpty(data.area));
        $("#input_area").val(common.checkToEmpty(data.areaName));
        $("#upCommercialRebate").val(common.checkToEmpty(data.insurancePurchaseOrderRebateViewModel != null ? data.insurancePurchaseOrderRebateViewModel.upCommercialRebate : ""));
        $("#upCompulsoryRebate").val(common.checkToEmpty(data.insurancePurchaseOrderRebateViewModel != null ? data.insurancePurchaseOrderRebateViewModel.upCompulsoryRebate : ""));
        $("#downCommercialRebate").val(common.checkToEmpty(data.insurancePurchaseOrderRebateViewModel != null ? data.insurancePurchaseOrderRebateViewModel.downCommercialRebate : ""));
        $("#downCompulsoryRebate").val(common.checkToEmpty(data.insurancePurchaseOrderRebateViewModel != null ? data.insurancePurchaseOrderRebateViewModel.downCompulsoryRebate : ""));
        $("#applicantDate").val(data.createTime);
        $(".applicantDate").hide();
        var $newCar = $("input:radio[name='newCar']").parent().parent();
        $newCar.empty();
        if (data.newCar) {
            $newCar.html("是");
        } else {
            $newCar.html("否");
        }

        initInstitution(data.institution);
        // 商业险
        if (refreshCommercial && data.commercialPremium) {
            $("#commercialPolicyNo").val(common.checkToEmpty(data.commercialPolicyNo));
            $("#commercialPremium").val(common.formatMoney(data.commercialPremium, 2));
            $("#commercialEffectiveDate").val(data.commercialEffectiveDate);
            $("#commercialEffectiveHour").val(data.commercialEffectiveHour == null ? 0 : data.commercialEffectiveHour);
            $("#commercialExpireHour").val(data.commercialExpireHour == null ? 24 : data.commercialExpireHour);
            $("#commercialExpireDate").val(data.commercialExpireDate);
            $("#damagePremium").val(common.formatMoney(data.damagePremium, 2));
            $("#damageAmount").val(common.formatMoney(data.damageAmount, 2));
            $("#damageIop").val(common.formatMoney(data.damageIop, 2));
            $("#thirdPartyPremium").val(common.formatMoney(data.thirdPartyPremium, 2));
            $("#thirdPartyAmountSel").val(data.thirdPartyAmount == null ? 0 : data.thirdPartyAmount);
            $("#thirdPartyIop").val(common.formatMoney(data.thirdPartyIop, 2));
            $("#driverPremium").val(common.formatMoney(data.driverPremium, 2));
            $("#driverAmountSel").val(data.driverAmount == null ? 0 : data.driverAmount);
            $("#driverIop").val(common.formatMoney(data.driverIop, 2));
            $("#passengerPremium").val(common.formatMoney(data.passengerPremium, 2));
            $("#passengerAmountSel").val(data.passengerAmount == null ? 0 : data.passengerAmount);
            $("#passengerIop").val(common.formatMoney(data.passengerIop, 2));
            $("#passengerCount").val(data.passengerCount == null ? 0 : data.passengerCount);
            $("#theftPremium").val(common.formatMoney(data.theftPremium, 2));
            $("#theftAmount").val(common.formatMoney(data.theftAmount, 2));
            $("#theftIop").val(common.formatMoney(data.theftIop, 2));
            $("#scratchAmountSel").val(data.scratchAmount == null ? 0 : data.scratchAmount);
            $("#scratchPremium").val(common.formatMoney(data.scratchPremium, 2));
            $("#scratchIop").val(common.formatMoney(data.scratchIop, 2));
            $("#spontaneousLossPremium").val(common.formatMoney(data.spontaneousLossPremium, 2));
            $("#spontaneousLossAmount").val(common.formatMoney(data.spontaneousLossAmount, 2));
            $("#spontaneousLossIop").val(common.formatMoney(data.spontaneousLossIop, 2));
            $("#enginePremium").val(common.formatMoney(data.enginePremium, 2));
            $("#unableFindThirdPartyPremium").val(common.formatMoney(data.unableFindThirdPartyPremium, 2));
            $("#designatedRepairShopPremium").val(common.formatMoney(data.designatedRepairShopPremium, 2));
            $("#engineIop").val(common.formatMoney(data.engineIop, 2));
            $("#iop").val(common.formatMoney(data.iop, 2));
            $("#engineAmount").val(common.formatMoney(data.engineAmount, 2));
            $("#glassPremium").val(common.formatMoney(data.glassPremium, 2));
            $("#glassTypeSel").val(common.formatMoney(data.glassPremium, 2) == 0.00 ?
                0 : (data.glassType == null ? 0 : data.glassType));
            if (!common.isEmpty(data.insuranceImage)) {
                $("#commercialDiv").removeClass("unvisable-hidden");
                $("#insuranceImage").attr("src", data.insuranceImage.endsWith("pdf") ? "../../images/pdf.jpg" : data.insuranceImage);
                $("#insuranceImage").attr("url", data.insuranceImage);
                $("#insuranceImage").show();
                $("#insuranceImage").next().show();
                $("#inputInsuranceImage").val(data.insuranceImage);

                var filename = data.insuranceImage.substring(data.insuranceImage.lastIndexOf("/") + 1);
                $("#downloadCommercial").attr("href", data.insuranceImage);
                $("#downloadCommercial").attr("download", filename);
            }
            $("#discount").val(common.formatMoney(data.discount, 2));

        }

        // 交强险
        if (refreshCompulsory && data.compulsoryPremium) {
            $("#compulsoryPolicyNo").val(common.checkToEmpty(data.compulsoryPolicyNo));
            $("#compulsoryEffectiveDate").val(data.compulsoryEffectiveDate);
            $("#compulsoryExpireDate").val(data.compulsoryExpireDate);
            $("#compulsoryEffectiveHour").val(data.compulsoryEffectiveHour == null ? 0 : data.compulsoryEffectiveHour);
            $("#compulsoryExpireHour").val(data.compulsoryExpireHour == null ? 24 : data.compulsoryExpireHour);
            $("#compulsoryPremium").val(common.formatMoney(data.compulsoryPremium, 2));
            $("#autoTax").val(common.formatMoney(data.autoTax, 2));
            if (!common.isEmpty(data.compulsoryInsuranceImage)) {
                $("#compulsoryDiv").removeClass("unvisable-hidden");
                $("#compulsoryInsuranceImage").attr("src", data.compulsoryInsuranceImage.endsWith("pdf") ? "../../images/pdf.jpg" : data.compulsoryInsuranceImage);
                $("#compulsoryInsuranceImage").attr("url", data.compulsoryInsuranceImage);
                $("#inputCompulsoryInsuranceImage").val(data.compulsoryInsuranceImage);
                $("#compulsoryInsuranceImage").show();
                $("#compulsoryInsuranceImage").next().show();

                var filename = data.compulsoryInsuranceImage.substring(data.compulsoryInsuranceImage.lastIndexOf("/") + 1);
                $("#downloadCompulsory").attr("href", data.compulsoryInsuranceImage);
                $("#downloadCompulsory").attr("download", filename);
            }

            if (!common.isEmpty(data.compulsoryStampFile)) {
                $("#compulsoryStampDiv").removeClass("unvisable-hidden")
                $("#compulsoryInsuranceStamp").attr("src", data.compulsoryInsuranceImage.endsWith("pdf") ? "../../images/pdf.jpg" : data.compulsoryInsuranceImage);
                $("#compulsoryInsuranceStamp").attr("url", data.compulsoryInsuranceImage);
                $("#inputCompulsoryInsuranceStamp").val(data.compulsoryInsuranceImage);
                $("#compulsoryInsuranceStamp").show();
                $("#compulsoryInsuranceStamp").next().show();

                var filename = data.compulsoryStampFile.substring(data.compulsoryStampFile.lastIndexOf("/") + 1);
                $("#downloadCompulsoryStamp").attr("href", data.compulsoryStampFile);
                $("#downloadCompulsoryStamp").attr("download", data);
            }
            $("#discountCI").val(common.formatMoney(data.discountCI, 2));
        }
        $("#applicantName").val(data.applicantName);
        $("#applicantIdNo").val(data.applicantIdNo);

        showEdit();
        $("#toCreate").val("更新保单");
    },
    edit: function (orderNo) {
        common.getByAjax(true, "get", "json", "/orderCenter/order/" + orderNo + "/insurance", {},
            function (data) {
                orderInsurance.fillData(data, true, true);
                //$("#designatedRepairShopPremium").attr("disabled","disabled");
                $("#resendGiftDiv").hide();
                $("#channelType").attr("disabled", "disabled");
                $("#channel").attr("disabled", "disabled");
                $("#orderType").attr("disabled", "disabled");
                $("#mobile").attr("readOnly", "readOnly");
                $("#agent").attr("readOnly", "readOnly");
            },
            function () {
                popup.mould.popTipsMould(false, "获取保单信息异常", "first", "error", "", "56%",
                    function () {
                        popup.mask.hideFirstMask(false);
                    }
                );
            }
        );
    },
    update: function (form, properties) {
        $("#toCreate").attr("disabled", true);
        $("#channelType").removeAttr("disabled");
        $("#channel").removeAttr("disabled");
        $("#orderType").removeAttr("disabled");
        var data = $(form).serialize();
        data = decodeURIComponent(data, true);
        data = common.tools.strToObj(data);
        data.resendGiftList = quoteResendGift.giftList;
        common.getByAjax(true, "put", "json", "/orderCenter/order/" + $("#orderNo").val() + "/insurance", data,
            function (data) {
                if (data.pass) {
                    popup.mould.popTipsMould(false, "更新保单成功", "first", "success", "", "56%",
                        function () {
                            //properties.currentPage = 1;
                            //properties.keyword = "";
                            //orderInsurance.list(properties);

                            datatableUtil.params.keyword = keyword;
                            datatables.ajax.reload();
                            showContent();
                            window.parent.scrollTo(0, 0);
                            popup.mask.hideFirstMask(false);
                        }
                    );
                } else {
                    popup.mould.popTipsMould(false, data.message, "first", "warning", "", "56%",
                        function () {
                            popup.mask.hideFirstMask(false);
                        }
                    );
                }

                $("#toCreate").attr("disabled", false);
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常", "first", "error", "", "56%",
                    function () {
                        popup.mask.hideFirstMask(false);
                    }
                );
                $("#toCreate").attr("disabled", false);
            }
        );
    },
    dosubmit: function (form, properties) {
        if (common.isEmpty($("#orderNo").val())) {
            orderInsurance.saveInsurance(form, properties);
        } else {
            orderInsurance.update(form, properties);
        }
    }
};


/* 出单机构 */
function initInstitution(institutionId) {
    $("#institution").empty();
    if (common.isEmpty($("#area").val()) || common.isEmpty($("#applicantDate").val()) || common.isEmpty($("#insuranceCompanySel").val())) {
        return;
    }
    common.getByAjax(true, "get", "json", "/orderCenter/nationwide/institution/rebate",
        {
            areaId: $("#area").val(),
            insuranceCompanyId: $("#insuranceCompanySel").val(),
            applicantDate: $("#applicantDate").val()
        },
        function (data) {
            $("#institution").empty();
            //if(data.length==0){
            //    showErrorMsg("请先添加当前车辆所在城市和保险公司的费率信息！");
            //    return false;
            //}
            var options = "<option value=''>请选择出单机构</option>";
            $.each(data, function (i, model) {
                options += "<option  value='" + model.institutionId + "' commercialRebate='" + model.commercialRebate + "' compulsoryRebate='" + model.compulsoryRebate + "' >" + model.institutionName + "</option>";
            });
            $("#institution").html(options);
            $("#institution").val(institutionId);
        }, function () {
        }
    );
}

function showContent() {
    $("#top_div").show();
    $("#count_div").show();
    $("#show_div").show();
    $("#edit_div").hide();
}

function showEdit() {
    $("#top_div").hide();
    $("#count_div").hide();
    $("#show_div").hide();
    $("#edit_div").show();
}

//
// var insuranceSource;
// var sourceNewly = {
//     init: function(validOptions_new){
//         insuranceSource = common.getUrlParam("source");
//         if(insuranceSource=="answern"){
//             //取消代理人的校验，代理人设置不可用，默认ID：999999；将页面传来的userId赋值给隐藏userId
//             delete validOptions_new.rules.recommender.required;
//             delete validOptions_new.messages.recommender;
//             $("#channel").val(common.getUrlParam("channel"));
//             $("#recommender").val(999999);
//             $("#agent").attr("readonly",true);
//             showEdit();
//             //如果是新弹出的页面，没有popupHtml的div，需要手动创建，否则弹出框无效
//             var body = document.body;
//             var div = document.createElement("div");
//             div.setAttribute("id", "popupHtml");
//             body.appendChild(div);
//             popup.insertHtml($("#popupHtml"));
//         }
//     },
//
//     initAnswern: function(data_list,key_name,value_name){
//         var option_list;
//         $.each(data_list, function(index, data) {
//             if(65000 == data[key_name])
//                 option_list += '<option value="' + data[key_name] + '" "selected">' + data[value_name] + '</option>';
//         });
//         return option_list;
//     }
// }
$(document).ready(function() {
    $('#channel').select2();
    $('#insuranceCompanySel').select2();
});
