/**
 * Created by wangfei on 2015/10/20.
 */
var quote_help = {
    getVisitState: function (visited) {
        if (visited) {
            return "<span style=\"color: green;\">已回访</span>";
        } else if (!visited && visited != null) {
            return "<span style=\"color: red;\">需回访</span>";
        }
    },
    getdisableState: function (disable) {
        if (disable) {
            return "<span style=\"color: red;\">无效</span>";
        } else if (!disable && disable != null) {
            return "<span style=\"color:green ;\">有效</span>";
        }
    },
    checkRenewal: function (data) {
        if (common.validations.isEmpty(data.licensePlateNo)) {
            return {flag: false, msg: "续保车牌号不可为空"};
        }
        if (common.validations.isEmpty(data.owner)) {
            return {flag: false, msg: "续保车主姓名不可为空"};
        }
        if (common.validations.isEmpty(data.insuredIdType)) {
            return {flag: false, msg: "续保被保险人证件类型不可为空"};
        }
        if (common.validations.isEmpty(data.insuredIdNo)) {
            return {flag: false, msg: "续保被保险人身份证不可为空"};
        }
        return {flag: true, msg: "成功"};
    },
    getIdentityTypes: function (id1, id2) {
        common.getByAjax(false, "get", "json", "/orderCenter/resource/identityTypes", {},
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.description + "</option>";
                    });
                    parent.$("#" + id1).append(options);
                    parent.$("#" + id2).append(options);
                }
            },
            function () {
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    auto: {
        identity_type: [1, 11, 12, 13],
        getAutoContent: function (licensePlateNo, mark, callBackMethod, popupInput) {
            common.getByAjax(false, "get", "json", "/orderCenter/quote/phone/auto", {"licensePlateNo": licensePlateNo},
                function (data) {
                    quote_help.auto.fixAutoContent(data, mark, licensePlateNo, popupInput);
                    if ("input" == mark) {
                        quote_help.auto.getAutoContentByNameId(licensePlateNo, mark, null, null, null);
                    }
                    callBackMethod();
                },
                function () {
                    popup.mould.popTipsMould(false, "获取车辆信息异常！", popup.mould.second, popup.mould.error, "", "", function () {
                        window.parent.$(".theme_popover_mask_second").hide();
                        window.parent.$("#popover_normal_tips_second").hide();
                    });
                }
            );
        },
        getAutoContentByNameId: function (licensePlateNo, mark, owner, insuredIdType, identity, popupInput) {
            // if(common.isEmpty(owner)){
            //     popup.mould.popTipsMould(false, "请填写车主姓名", popup.mould.second, popup.mould.error, "", "", null);
            //     return;
            // }
            // if( common.isEmpty(insuredIdType)){
            //     popup.mould.popTipsMould(false, "请选择车主证件类型", popup.mould.second, popup.mould.error, "", "", null);
            //     return;
            // }
            // if( common.isEmpty(identity)){
            //     popup.mould.popTipsMould(false, "请填写车主证件号", popup.mould.second, popup.mould.error, "", "", null);
            //     return;
            // }
            // if(!common.isIdCardNo(identity)){
            //     popup.mould.popTipsMould(false, "证件格式错误", popup.mould.second, popup.mould.error, "", "", null);
            //     return;
            // }
            window.parent.$("#quoteAutoInfo").attr("disabled", true);

            common.ajax.getByAjaxWithHeader(true, "get", "json", "/orderCenter/quote/quoteAutoInfo", {
                    "owner": owner,
                    "identity": identity,
                    "identityType": insuredIdType,
                    "licensePlateNo": licensePlateNo
                },
                function (data) {
                    parent.$("#quoteAutoInfo").attr("disabled", false);
                    if (!common.isEmpty(data) && data.vehicleLicenseInfo[0].originalValue) {
                        quote_help.auto.fixAutoContentByThird(data, popupInput);
                    } else {
                        popup.mould.popTipsMould(false, "未获取到车辆信息", popup.mould.second, popup.mould.error, "", "", function () {
                            window.parent.$(".theme_popover_mask_second").hide();
                            window.parent.$("#popover_normal_tips_second").hide();
                        });
                    }
                },
                function () {
                    parent.$("#quoteAutoInfo").attr("disabled", false);
                    popup.mould.popTipsMould(false, "获取车辆信息异常！", popup.mould.second, popup.mould.error, "", "", function () {
                        window.parent.$(".theme_popover_mask_second").hide();
                        window.parent.$("#popover_normal_tips_second").hide();
                    });
                }
            );
        },
        getPopIdByPopInput: function (popupInput) {
            if (popupInput == 'second') {
                return '#popover_normal_input_second';
            } else if (popupInput == 'amend_search_list') {
                return "";
            } else {
                return '#popover_normal_input';
            }
        },
        fixAutoContentByThird: function (data, popupInput) {
            var popover = this.getPopIdByPopInput(popupInput);
            var parent = window.parent;
            for(var index = 0; index < data.vehicleLicenseInfo.length; index++ ){
                switch (data.vehicleLicenseInfo[index].key){
                    case "licensePlateNo":
                        parent.$(popover + " #detail_licensePlateNo").text(data.vehicleLicenseInfo[index].originalValue);
                        break;
                    case "owner":
                        parent.$(popover + " #input_owner").val(common.checkToEmpty(data.vehicleLicenseInfo[index].originalValue));
                        parent.$(popover + " #input_insuredName").val(common.checkToEmpty(data.vehicleLicenseInfo[index].originalValue));
                        break;
                    case "identity":
                        parent.$(popover + " #input_insuredIdNo").val(common.checkToEmpty(data.vehicleLicenseInfo[index].originalValue));
                        parent.$(popover + " #input_identity").val(common.checkToEmpty(data.vehicleLicenseInfo[index].originalValue));
                        break;
                    case "vinNo":
                        parent.$(popover + " #input_vinNo").val(common.checkToEmpty(data.vehicleLicenseInfo[index].originalValue));
                        break;
                    case "engineNo":
                        parent.$(popover + " #input_engineNo").val(common.checkToEmpty(data.vehicleLicenseInfo[index].originalValue));
                        break;
                    case "enrollDate":
                        parent.$(popover + " #input_enrollDate").val(common.checkToEmpty(data.vehicleLicenseInfo[index].originalValue));
                        break;
                    case "code":
                        parent.$(popover + " #input_code").val(common.checkToEmpty(data.vehicleLicenseInfo[index].originalValue));
                        break;
                }
            }
        },
        fixAutoContent: function (data, mark, licensePlateNo, popupInput) {
            var popover = this.getPopIdByPopInput(popupInput);
            var parent = window.parent;
            if ("text" == mark) {
                if (data.licensePlateNo) {
                    parent.$(popover + " #detail_owner").text(common.checkToEmpty(data.owner));
                    parent.$(popover + " #detail_owner").attr("title", common.checkToEmpty(data.owner));
                    parent.$(popover + " #input_identityType").val(common.checkToEmpty(data.identityType ? data.identityType.id : 1));
                    parent.$(popover + " #input_identityType").attr("disabled", true);
                    parent.$(popover + " #detail_identity").text(common.checkToEmpty(data.identity));
                    parent.$(popover + " #detail_insuredName").text(common.checkToEmpty(data.insuredName));
                    parent.$(popover + " #detail_insuredName").attr("title", common.checkToEmpty(data.insuredName));
                    parent.$(popover + " #input_insuredIdType").val(common.checkToEmpty(data.insuredIdType ? data.insuredIdType.id : 1));
                    parent.$(popover + " #input_insuredIdType").attr("disabled", true);
                    parent.$(popover + " #detail_insuredIdNo").text(common.checkToEmpty(data.insuredIdNo));
                    this.setInsuredRadio(data);
                    this.setTransferRadio(data);
                    parent.$("input:radio[name='insuredType']").attr("disabled", true);
                    parent.$("input:radio[name='transfer']").attr("disabled", true);
                    parent.$("input:radio[name='renewal']").attr("disabled", true);
                    parent.$(popover + " #detail_licensePlateNo").text(data.licensePlateNo);
                    parent.$(popover + " #licensePlateNoHid").val(data.licensePlateNo);
                    parent.$(popover + " #detail_vinNo").text(common.checkToEmpty(data.vinNo));
                    parent.$(popover + " #detail_engineNo").text(common.checkToEmpty(data.engineNo));
                    parent.$(popover + " #detail_enrollDate").text(common.checkToEmpty(data.enrollDate));
                    parent.$(popover + " #detail_expireDate").text(common.checkToEmpty(data.expireDate));
                    parent.$(popover + " #detail_code").text(common.checkToEmpty(data.code));
                    parent.$(popover + " #detail_model").text(common.checkToEmpty(data.model));
                    parent.$(popover + " #detail_mobile").text(common.checkToEmpty(data.mobile));
                    parent.$(popover + " #detail_userId").text(common.checkToEmpty(data.userId));
                    parent.$(popover + " #sourceChannel").val(common.checkToEmpty(data.sourceChannel));
                    parent.$(popover + " #sourceChannel").attr("disabled", true);
                    parent.$(popover + " #detail_transferDate").text(common.checkToEmpty(data.transferDate));
                }
                parent.$(popover + " .text-input").hide().siblings(".text-show").show();
            } else if ("input" == mark) {
                if (data.licensePlateNo) {
                    parent.$(popover + " #input_owner").val(common.checkToEmpty(data.owner));
                    parent.$(popover + " #input_identityType").val(common.checkToEmpty(data.identityType ? data.identityType.id : 1));
                    parent.$(popover + " #input_identity").val(common.checkToEmpty(data.identity));
                    parent.$(popover + " #input_insuredName").val(common.checkToEmpty(data.insuredName));
                    parent.$(popover + " #input_insuredIdType").val(common.checkToEmpty(data.insuredIdType ? data.insuredIdType.id : 1));
                    parent.$(popover + " #input_insuredIdNo").val(common.checkToEmpty(data.insuredIdNo));
                    this.setInsuredRadio(data, popupInput);
                    this.setTransferRadio(data);
                    parent.$("input:radio[name='insuredType']").attr("disabled", false);
                    parent.$("input:radio[name='insuredType']").unbind("change").bind({
                        change: function () {
                            if ($(this).val() == "1") {
                                quote_help.auto.setReadOnly(popover);
                            } else {
                                quote_help.auto.setEdit(popover);
                            }
                        }
                    });
                    parent.$("input:radio[name='renewal']").attr("disabled", false);
                    parent.$("input:radio[name='transfer']").attr("disabled", false);
                    parent.$("input:radio[name='transfer']").unbind("change").bind({
                        change: function () {
                            if ($(this).val() == "1") {
                                parent.$("#transfer_div").show();
                            } else {
                                parent.$("#input_transferDate").val("");
                                parent.$("#transfer_div").hide();
                            }
                        }
                    });
                    parent.$(popover + " #detail_licensePlateNo").text(data.licensePlateNo);
                    parent.$(popover + " #licensePlateNoHid").val(data.licensePlateNo);
                    parent.$(popover + " #input_vinNo").val(common.checkToEmpty(data.vinNo));
                    parent.$(popover + " #input_engineNo").val(common.checkToEmpty(data.engineNo));
                    parent.$(popover + " #input_enrollDate").val(common.checkToEmpty(data.enrollDate));
                    parent.$(popover + " #input_expireDate").val(common.checkToEmpty(data.expireDate));
                    parent.$(popover + " #input_code").val(common.checkToEmpty(data.code));
                    parent.$(popover + " #input_model").val(common.checkToEmpty(data.model));
                    parent.$(popover + " #input_mobile").val(common.checkToEmpty(data.mobile));
                    parent.$(popover + " #input_transferDate").val(common.checkToEmpty(data.transferDate));
                } else {
                    parent.$(popover + " #detail_licensePlateNo").text(licensePlateNo);
                    parent.$(popover + " #licensePlateNoHid").val(licensePlateNo);
                }
                parent.$(popover + " .text-show").hide().siblings(".text-input").show();
                parent.$(popover + " #id").val(data.id);
                parent.$(popover + " #userId").val(data.userId);
            }
        },
        setInsuredRadio: function (data, popupInput) {
            if (data.identityType) {
                if (data.owner == data.insuredName && data.identity == data.insuredIdNo && data.identityType.id == data.insuredIdType.id) {
                    parent.$("input:radio[name='insuredType']").eq(0).attr("checked", 'checked');
                    this.setReadOnly("", popupInput);
                    return;
                }
            }
            parent.$("input:radio[name='insuredType']").eq(1).attr("checked", 'checked');
            this.setEdit("");
        },
        setTransferRadio: function (data) {
            if (common.isEmpty(data.transferDate)) {
                parent.$("input:radio[name='transfer']").eq(1).attr("checked", 'checked');
                parent.$("#transfer_div").hide();
            } else {
                parent.$("input:radio[name='transfer']").eq(0).attr("checked", 'checked');
                parent.$("#transfer_div").show();
            }
        },
        getAutoByLicensePlateNo: function (licensePlateNo, callBackMethod) {
            common.getByAjax(true, "get", "json", "/orderCenter/quote/phone/records", {"licensePlateNo": licensePlateNo},
                function (data) {
                    callBackMethod(data);
                },
                function () {
                    popup.mould.popTipsMould(false, "获取历史电话报价记录异常！", popup.mould.second, popup.mould.error, "", "", null);
                }
            );
        },
        setReadOnly: function (popover, popupInput) {
            parent.$(popover + " #input_insuredName").attr("readonly", true);
            common.tools.setSelectReadonly(parent.$(popover + " #input_insuredIdType"));
            parent.$(popover + " #input_insuredIdNo").attr("readonly", true);
            if (popupInput == "amend_search_list") {
                parent.$(popover + " #input_insuredName").val(parent.$(" #input_owner").val());
                parent.$(popover + " #input_insuredIdType").val(parent.$(" #input_identityType").val());
                parent.$(popover + " #input_insuredIdNo").val(parent.$(" #input_identity").val());
            } else {
                parent.$(popover + " #input_insuredName").val(parent.$(popover + " #input_owner").val());
                parent.$(popover + " #input_insuredIdType").val(parent.$(popover + " #input_identityType").val());
                parent.$(popover + " #input_insuredIdNo").val(parent.$(popover + " #input_identity").val());
            }
            parent.$(popover + " #input_owner").unbind("blur").bind({
                blur: function () {
                    parent.$(popover + " #input_insuredName").val($(this).val());
                }
            });
            parent.$(popover + " #input_identityType").unbind("blur").bind({
                blur: function () {
                    parent.$(popover + " #input_insuredIdType").val($(this).val());
                }
            });
            parent.$(popover + " #input_identity").unbind("blur").bind({
                blur: function () {
                    parent.$(popover + " #input_insuredIdNo").val($(this).val());
                }
            });
            parent.$(popover + " #identity_type").unbind("blur").bind({
                blur: function () {
                    parent.$(popover + " #insured_id_type").val($(this).val());
                }
            });
        },
        setEdit: function (popover) {
            parent.$(popover + " #input_insuredName").attr("readonly", false);
            common.tools.unsetSelectReadonly(parent.$(popover + " #input_insuredIdType"));
            parent.$(popover + " #input_insuredIdNo").attr("readonly", false);
            parent.$(popover + " #input_owner").unbind("blur");
            parent.$(popover + " #input_identityType").unbind("blur");
            parent.$(popover + " #input_identity").unbind("blur");
            parent.$(popover + " #identity_type").unbind("blur");
        },

    },
    quoteValidate: {
        validate: function (pClass) {
            if (window.parent.$("#handleResultSel").val() == 1) {
                this.showErrors("请选择处理结果", pClass);
                return false;
            }
            if (window.parent.$("#handleResultSel").val() == 92 && common.isEmpty(window.parent.$("#comment").val())) {
                this.showErrors("请输入备注", pClass);
                return false;

            }
            if (search_list.detailInfo.requiredTriggerTimeStatus.indexOf(window.parent.$("#handleResultSel").val()) > -1
                && common.isEmpty(window.parent.$("#trigger_time").val())) {
                this.showErrors("请选择到期时间", pClass);
                return false;
            }
            if (search_list.detailInfo.showTrigerOrdernoStatus.indexOf(window.parent.$("#handleResultSel").val()) > -1
                && common.isEmpty(window.parent.$("#order_no").val())) {
                this.showErrors("请输入订单号", pClass);
                return false;
            }
            return true;
        },
        validateQuote: function (pClass) {
            if (common.isEmpty(parent.$("#input_owner").val()) || !common.validateName(parent.$("#input_owner").val())) {
                this.showErrors("车主姓名格式错误", pClass);
                return false;
            }
            if (common.isEmpty(parent.$("#input_identityType").val())) {
                this.showErrors("车主证件类型", pClass);
                return false;
            }
            if (!common.validIdentity(parent.$("#input_identityType").val(), parent.$("#input_identity").val())) {
                this.showErrors("车主证件错误", pClass);
                return false;
            }
            // if(common.isEmpty(parent.$("#input_identity").val()) || !common.isIdCardNo(parent.$("#input_identity").val())){
            //     this.showErrors("车主证件号错误",pClass);
            //     return false;
            // }
            if (parent.$("#input_mobile").val() == '') {
                this.showErrors("电话号码不能为空", pClass);
                return false;
            } else if (!common.isMobile(parent.$("#input_mobile").val())) {
                this.showErrors("电话号码格式错误", pClass);
                return false;
            }
            if (parent.$("#sourceChannel").val() == '') {
                this.showErrors("请选择产品平台", pClass);
                return false;
            }
            if (parent.$("input[name='transfer']:checked").val() == '1') {
                if (common.isEmpty(parent.$("#input_transferDate").val())) {
                    this.showErrors("请填写过户日期");
                    return false;
                }
            }
            if (parent.$("input[name='renewal']:checked").val() == '2') {
                if (common.isEmpty(parent.$("#input_insuredName").val()) || !common.validateName(parent.$("#input_insuredName").val())) {
                    this.showErrors("被保险人姓名格式错误", pClass);
                    return false;
                }
                if (common.isEmpty(parent.$("#input_insuredIdType").val())) {
                    this.showErrors("被保险人证件类型", pClass);
                    return false;
                }
                // if(common.isEmpty(parent.$("#input_insuredIdNo").val()) || !common.validations.isIdCardNo(parent.$("#input_insuredIdNo").val())){
                //     this.showErrors("被保险人证件号格式错误",pClass);
                //     return false;
                // }

                if (!common.validIdentity(parent.$("#input_insuredIdType").val(), parent.$("#input_insuredIdNo").val())) {
                    this.showErrors("被保险人证件错误", pClass);
                    return false;
                }
                if (common.isEmpty(parent.$("#input_vinNo").val()) || !common.validateVinNo(parent.$("#input_vinNo").val())) {
                    this.showErrors("车架号格式错误", pClass);
                    return false;
                }
                if (common.isEmpty(parent.$("#input_engineNo").val()) || !common.validateEngineNo(parent.$("#input_engineNo").val())) {
                    this.showErrors("发动机号格式错误", pClass);
                    return false;
                }
                if (common.isEmpty(parent.$("#input_code").val())) {
                    this.showErrors("请填写品牌型号", pClass);
                    return false;
                }
            }
            this.hideErrors(pClass);
            return true;
        },
        showErrors: function (errorText, pClass) {
            if (pClass != "" && pClass != undefined) {
                window.parent.$("." + pClass + " .error-msg").show();
                window.parent.$("." + pClass + " #errorText").text(errorText);
            } else {
                window.parent.$(".error-msg").show();
                window.parent.$(".error-msg #errorText").text(errorText);
            }
        },
        hideErrors: function (pClass) {
            if (pClass != "" && pClass != undefined) {
                window.parent.$("." + pClass + " .error-msg").hide();
            } else {
                window.parent.$(".error-msg").hide();
            }
        },
    }
};
