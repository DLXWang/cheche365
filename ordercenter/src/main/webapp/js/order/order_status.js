/**
 * Created by chenxy on 2016/09/13.
 */
var orderStatusList = {
    getAllOrderStatus: function() {
        common.ajax.getByAjax(false, "get", "json", "/orderCenter/resource/orderTransmissionStatusKeys", {},
            function(data) {
                for(key in data){
                    orderStatusList[key] = data[key];

                }
            },
            function() {}
        );
    },
};

/**
 * Created by wangfei on 2015/12/21.
 */
var orderStatus = {
    statusContent: "",
    showEnableStatus: function(orderOperationInfoId, position, clickMethod) {
        if (common.validations.isEmpty(orderStatus.statusContent)) {
            var internalItems = "";
            var inputItems = "";
            var adminItems = "";
            var internalFlag,inputFlag,adminFlag = false;
            var interVal = setInterval(function() {
                if (internalFlag && inputFlag && adminFlag){
                    clearInterval(interVal);
                    $.post("order_status.html", {}, function (changeStateContent) {
                        switchStatus(changeStateContent);
                    });
                }
            }, 100);
            orderStatus.interface.getAllEnableAdmin(
                function(adminList) {
                    $.each(adminList, function(index, adminUser) {
                        adminItems += '<option value="' + adminUser.id + '">' + adminUser.name + '</option>';
                    });
                    adminFlag = true;
                },
                function() {
                    clearInterval(interVal);
                }
            );
            orderStatus.interface.getAllEnableInternal(
                function(internalUserList) {
                    $.each(internalUserList, function(index, internalUser) {
                        internalItems += '<option value="' + internalUser.id + '">' + internalUser.name + '</option>';
                    });
                    internalFlag = true;
                },
                function() {
                    clearInterval(interVal);
                }
            );
            orderStatus.interface.getAllEnableInput(
                function(inputList) {
                    $.each(inputList, function(index, inputUser) {
                        inputItems += '<option value="' + inputUser.id + '">' + inputUser.name + '</option>';
                    });
                    inputFlag = true;
                },
                function() {
                    clearInterval(interVal);
                }
            );
        } else {
            switchStatus();
        }
        function switchStatus(changeStateContent) {
            orderStatus.interface.getSwitchStatus(orderOperationInfoId, function(data) {
                popup.pop.popInput(false, !common.validations.isEmpty(orderStatus.statusContent) ? orderStatus.statusContent : changeStateContent, position,
                    "446px", "auto", "50%", "54%");
                var $popInput = common.tools.getPopInputDom(position, false);
                if (common.validations.isEmpty(orderStatus.statusContent)) {
                    $popInput.find("#internalUser").append(internalItems);
                    $popInput.find("#inputUser").append(inputItems);
                    $popInput.find("#adminUser").append(adminItems);
                    orderStatus.statusContent = $popInput.find("#state_change_content").html();
                }
                var $switchStatus = $popInput.find("#switchStatus");
                if (data.switchStatus) {
                    var options = "";
                    $.each(data.switchStatus, function(index, status) {
                        options += '<option value="' + status.id + '">' + status.status + '</option>';
                    });
                    $switchStatus.empty().append(options);
                }
                $popInput.find(".theme_poptit .close").unbind("click").bind({
                    click: function () {
                        popup.mask.hideFirstMask(false);
                    }
                });
                $switchStatus.unbind("change").bind({
                    change: function () {
                        orderStatus.adjustStateView(data.oriStatus, $(this).val(), $popInput,orderOperationInfoId);
                    }
                });
                orderStatus.adjustStateView(data.oriStatus, $switchStatus.val(), $popInput,orderOperationInfoId);
                $popInput.find(".changeOrderStatus").unbind("click").bind({
                    click: function() {
                        orderStatus.hideError($popInput);
                        var confirmNo = "";
                        var owner = "";
                        var institutionId="";
                        var commercialRebate="";
                        var compulsoryRebate="";
                        var newStatus = $switchStatus.val();
                        var $confirmNo = $popInput.find("#confirmNoDiv").find("#confirmNo");
                        var $internalUser = $popInput.find("#internalUserDiv").find("#internalUser");
                        var $inputUser = $popInput.find("#inputUserDiv").find("#inputUser");
                        var $adminUser = $popInput.find("#adminUserDiv").find("#adminUser");
                        var $institution=$popInput.find("#institutionDiv").find("#institution");
                        switch (newStatus) {
                            case "13"://确认出单
                                owner = $internalUser.val();
                                if (!owner) {
                                    orderStatus.showError($popInput, "请指定内勤");
                                    return;
                                }
                                institutionId=$institution.val();
                                if(common.isEmpty(institutionId)){
                                    orderStatus.showError($popInput, "请选择出单机构");
                                    return;
                                }
                                commercialRebate=$institution.find("option:selected").attr("commercialRebate");
                                compulsoryRebate=$institution.find("option:selected").attr("compulsoryRebate");
                                break;
                            case "14"://已付款，出单完成
                                if (data.oriStatus == "13") {
                                    owner = $inputUser.val();
                                    if (!owner) {
                                        orderStatus.showError($popInput, "请指定录单员");
                                        return;
                                    }
                                }
                                break;
                            case "16"://申请退款
                                owner = $internalUser.val();
                                if (!owner) {
                                    orderStatus.showError($popInput, "请指定内勤");
                                    return;
                                }
                                break;
                            case "18"://申请取消
                                owner = $adminUser.val();
                                if (!owner) {
                                    orderStatus.showError($popInput, "请指定管理员");
                                    return;
                                }
                                break;
                            case "22"://追加付款
                            case "23"://核保失败
                            default :
                        }
                        orderStatus.interface.changeOrderStatus(orderOperationInfoId, newStatus, owner,commercialRebate,compulsoryRebate,institutionId, function(data) {
                            clickMethod(data);
                        });
                    }
                });
            });
        }
    },
    showError: function($popInput, msg) {
        var $errorLine = $popInput.find(".error-line");
        $errorLine.find(".errorText").text(msg);
        $errorLine.show();
    },
    hideError: function($popInput) {
        $popInput.find(".error-line").hide();
    },
    adjustStateView: function(oriStatus, newStatus, $popInput,orderOperationInfoId) {
        orderStatus.hideError($popInput);
        var $confirmNoDiv = $popInput.find("#confirmNoDiv");
        var $internalUserDiv = $popInput.find("#internalUserDiv");
        var $inputUserDiv = $popInput.find("#inputUserDiv");
        var $adminUserDiv = $popInput.find("#adminUserDiv");
        var $institutionDiv=$popInput.find("#institutionDiv");
        switch (newStatus) {
            case "13"://确认出单
                $internalUserDiv.show();
                $institutionDiv.show();
                $adminUserDiv.hide();
                $inputUserDiv.hide();
                initInstitution(orderOperationInfoId, $popInput);
                break;
            case "14"://已付款，出单完成
                if (oriStatus == "13") {
                    $inputUserDiv.show();
                } else {
                    $inputUserDiv.hide();
                }
                $internalUserDiv.hide();
                $institutionDiv.hide();
                $confirmNoDiv.hide();
                $adminUserDiv.hide();
                break;
            case "16"://申请退款
                $confirmNoDiv.hide();
                $internalUserDiv.show();
                $institutionDiv.hide();
                $inputUserDiv.hide();
                $adminUserDiv.hide();
                break;
            case "18"://申请取消
                $confirmNoDiv.hide();
                $internalUserDiv.hide();
                $institutionDiv.hide();
                $adminUserDiv.show();
                $inputUserDiv.hide();
                break;
            default :
                $confirmNoDiv.hide();
                $internalUserDiv.hide();
                $institutionDiv.hide();
                $inputUserDiv.hide();
                $adminUserDiv.hide();
                break;
        }
    },
    interface: {
        getAllEnableAdmin: function(callback, errorback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/internalUser/admin", {},
                function(data) {
                    callback(data);
                },
                function() {
                    errorback();
                }
            );
        },
        getAllEnableInternal: function(callback, errorback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/internalUser/internal", {},
                function(data) {
                    callback(data);
                },
                function() {
                    errorback();
                }
            );
        },
        getAllEnableInput: function(callback, errorback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/internalUser/input", {},
                function(data) {
                    callback(data);
                },
                function() {
                    errorback();
                }
            );
        },
        changeOrderStatus: function(orderOperationInfoId, newStatus, owner,commercialRebate,compulsoryRebate,institutionId, callback) {
            common.ajax.getByAjax(true, "put", "json", "/orderCenter/orderOperationInfos/" + orderOperationInfoId + "/status",
                {
                    newStatus:      newStatus,
                    owner:          owner,
                    commercialRebate:commercialRebate,
                    compulsoryRebate:compulsoryRebate,
                    institutionId:institutionId
                },
                function(data) {
                    callback(data);
                },
                function() {
                    console.log("状态更新异常");
                }
            );
        },
        getSwitchStatus: function(orderOperationInfoId, callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderOperationInfos/" + orderOperationInfoId + "/status/enable", {},
                function(data) {
                    callback(data);
                },
                function() {
                    console.log("获取订单状态异常");
                }
            );
        },
    }
};



/* 出单机构 */
function initInstitution(orderOperationInfoId,$popInput){
    common.getByAjax(true, "get", "json", "/orderCenter/orderOperationInfos/rebate/"+orderOperationInfoId,null,
        function(data){
            if(data.length==0){
                orderStatus.showError($popInput, "查询出单机构失败，请添加出单机构和费率！");
                return false;
            }
            var options = "<option value=''>请选择出单机构</option>";
            $.each(data, function(i,model){
                options += "<option value='"+ model.institutionId +"' commercialRebate='"+model.commercialRebate+"' compulsoryRebate='"+model.compulsoryRebate+"' >" + model.institutionName + "</option>";
            });
            $popInput.find("#institution").html(options);
        },function(){}
    );
}
