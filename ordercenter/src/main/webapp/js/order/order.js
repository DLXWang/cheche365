/**
 * Created by wangfei on 2015/5/4.
 */
var Order = function(){
    this.currentPage = 1;
    this.keyword = "";
    this.mark = "";
    this.pageSize = 20;
}

function showDateConfirmMask(content){
    window.parent.$("#theme_popover_date").find(".tipsContent").html(content);
    window.parent.$("#theme_popover_date").find("#chooseDate").find("#reConfirmDate").val("");
    window.parent.$("#theme_popover_date").find("#chooseDate").show();
    window.parent.$("#theme_popover_date").find("#showDate").hide();
    window.parent.$(".theme_popover_mask").show();
    window.parent.$("#theme_popover_date").show();
}

function showReturnTips(content){
    window.parent.$("#theme_popover_return").find(".tipsContent").html(content);
    window.parent.$("#theme_popover_return").find("#reasonSel option:first").attr("selected", 'selected');
    window.parent.$("#theme_popover_return").find("#reasonDiv").find("#reason").val("");
    window.parent.$("#theme_popover_return").find("#reasonDiv").hide();
    window.parent.$(".theme_popover_mask").show();
    window.parent.$("#theme_popover_return").show();
}

function showInputText(content){
    var optionVal = $("#optionVal").val();
    if (optionVal == "4") {
        window.parent.$("#theme_popover_input").find("#textInput").attr("maxLength", 20);
    } else {
        window.parent.$("#theme_popover_input").find("#textInput").attr("maxLength", "");
    }
    window.parent.$("#theme_popover_input").find(".tipsContent").html(content);
    window.parent.$("#theme_popover_input").find("#textInput").val("");
    window.parent.$(".theme_popover_mask").show();
    window.parent.$("#theme_popover_input").show();
}

function showFirstOption(){
    var tabNo = $("#tabNo").val();
    $("#tab_tr"+tabNo).find("#operationSelect_td option:first").attr("selected", 'selected');
}

function checkToInsurance(purchaseOrderId){
    common.getByAjax(true, "get", "json", "/orderCenter/user/listRole", null,
        function(data){
            if(!common.isCustomerAdmin(data) && !common.isInternal(data)){
                common.showTips("您没有权限执行该操作");
                return false;
            }
            window.open("client_insurance_input.html?id=" + purchaseOrderId);
        },
        function(){}
    );
}

function changeStatus(optionVal, orderOperationId){
    $("#operationId").val(orderOperationId);
    $("#tabNo").val(orderOperationId);
    if (optionVal.indexOf("return") > -1) {
        $("#optionVal").val(optionVal.replace(/[^0-9]/ig,""));
    } else {
        $("#optionVal").val(optionVal);
    }

    switch (optionVal){
        case "0"://默认 直接返回
            return false;
        case "2"://等待再次确认
            showDateConfirmMask("请选择再次确认时间：");
            return false;
        case "3"://出单
            common.showPublicTips("确认出单？");
            return false;
        case "4"://核保完成
            showInputText("请输入确认单号：");
            return false;
        case "5"://去收款
            showDateConfirmMask("请选择去收款时间：");
            window.parent.$("#theme_popover_date").find("#chooseDate").find("#reConfirmDate").val($("#tab_tr" + i).find("#payTime_td").text());
            return false;
        case "6"://再次去收款
            showDateConfirmMask("请选择再次收款时间：");
            return false;
        case "7"://出单完成
            common.showPublicTips("是否确认出单完成？");
            return false;
        case "8"://派送
            showDateConfirmMask("请选择派送时间：");
            return false;
        case "9"://再次派送
            showDateConfirmMask("请选择再次派送时间：");
            return false;
        case "10"://录入保单
            common.showPublicTips("确定录入保单？");
            return false;
        case "11"://订单完成
            common.showPublicTips("确定订单完成？");
            return false;
        case "12"://用户取消
            common.showPublicTips("确认用户取消？");
            return false;
        case "return1"://返回未确认
            showReturnTips("请选择返回未确认原因：");
            return false;
        case "return3"://返回去出单
            showReturnTips("请选择返回出单原因：");
            return false;
        case "return5"://返回去收款
            showReturnTips("请选择返回去收款原因：");
            return false;
        case "return7"://返回出单完成
            showReturnTips("请选择返回出单完成原因：");
            return false;
        case "return8"://返回去派送
            showReturnTips("请选择返回派送原因：");
            return false;
        case "return10"://返回录入保单
            showReturnTips("请选择返回录入保单原因：");
            return false;
    }
}

//发送验证码
function sendVerificationCode(purchaseOrderId, i) {
    common.getByAjax(true, "get", "json", "/orderCenter/user/listRole", null,
        function(data){
            if(!common.isCustomerAdmin(data) && !common.isCustomer(data)){
                common.showTips("您没有权限执行该操作");
                return false;
            }

            $("#tab_tr"+i).find("#operationSelect_td").find("#sendBtn").attr("disabled",true);
            common.getByAjax(true, "get", "json", "/orderCenter/order/sendVerificationCode", {purchaseOrderId : purchaseOrderId},
                function(data){
                    if(data.code == "401") {
                        common.showTips("该订单不存在，请重新刷新确认。");
                        $("#tab_tr"+i).find("#operationSelect_td").find("#sendBtn").attr("disabled",false);
                        return false;
                    }
                    if(data.code == "402") {
                        common.showTips("系统异常。");
                        $("#tab_tr"+i).find("#operationSelect_td").find("#sendBtn").attr("disabled",false);
                        return false;
                    }
                    if(data.code == "405") {
                        common.showTips("在收款或再次收款状态下才能发送验证码。");
                        $("#tab_tr"+i).find("#operationSelect_td").find("#sendBtn").attr("disabled",false);
                        return false;
                    }
                    if(data.code == "406") {
                        common.showTips("该订单已取消，请重新刷新确认。");
                        $("#tab_tr"+i).find("#operationSelect_td").find("#sendBtn").attr("disabled",false);
                        return false;
                    }
                    if (data.objectMap.successFlag) {
                        common.showTips("验证码发送成功");
                    } else {
                        common.showTips("验证码发送失败");
                    }
                    $("#tab_tr"+i).find("#operationSelect_td").find("#sendBtn").attr("disabled",false);
                },
                function(){
                    common.showTips("系统异常");
                    $("#tab_tr"+i).find("#operationSelect_td").find("#sendBtn").attr("disabled",false);
                }
            );
        },
        function(){}
    );

}

//验证码弹出框
function showVerificationCodeTips(purchaseOrderId){
    common.getByAjax(true, "get", "json", "/orderCenter/user/listRole", null,
        function(data){
            if(!common.isCustomerAdmin(data) && !common.isCustomer(data)){
                common.showTips("您没有权限执行该操作");
                return false;
            }

            showInputText("请输入验证码：");
            window.parent.$("#theme_popover_input").find("#textInput").attr("maxLength", 6);
            window.parent.$("#theme_popover_input").find("#showInput").find(".confirm").unbind("click").bind({
                click : function(){
                    if ($.trim(window.parent.$("#theme_popover_input").find("#textInput").val()) == "") {
                        common.showSecondTips("请填写验证码");
                        return false;
                    }
                    saveVerificationCode(purchaseOrderId, window.parent.$("#theme_popover_input").find("#textInput").val());
                }
            });
            window.parent.$("#theme_popover_input").find(".showInput").find(".confirm").unbind("click").bind({
                click : function(){
                    common.hideMask();
                }
            });
        },
        function(){}
    );
}

//保存验证码
function saveVerificationCode(purchaseOrderId, verificationCode) {
    common.getByAjax(true, "get", "json", "/orderCenter/user/listRole", null,
        function(data){
            if(!common.isCustomerAdmin(data) && !common.isCustomer(data)){
                common.showTips("您没有权限执行该操作");
                return false;
            }

            window.parent.$("#theme_popover_input").find(".showInput .confirm").attr("disabled",true);
            common.getByAjax(true, "get", "json", "/orderCenter/order/saveVerificationCode", {purchaseOrderId : purchaseOrderId, verificationCode: verificationCode},
                function(data){
                    common.hideMask();
                    if(data.code == "401") {
                        common.showTips("该订单不存在，请重新刷新确认。");
                        window.parent.$("#theme_popover_input").find(".showInput .confirm").attr("disabled",false);
                        return false;
                    }
                    if(data.code == "403") {
                        common.showTips("系统异常。");
                        window.parent.$("#theme_popover_input").find(".showInput .confirm").attr("disabled",false);
                        return false;
                    }
                    if(data.code == "404") {
                        common.showTips("验证码为空，请输入验证码。");
                        window.parent.$("#theme_popover_input").find(".showInput .confirm").attr("disabled",false);
                        return false;
                    }
                    if(data.code == "405") {
                        common.showTips("在收款或再次收款状态下才能保存验证码。");
                        window.parent.$("#theme_popover_input").find(".showInput .confirm").attr("disabled",false);
                        return false;
                    }
                    if(data.code == "406") {
                        common.showTips("该订单已取消，请重新刷新确认。");
                        window.parent.$("#theme_popover_input").find(".showInput .confirm").attr("disabled",false);
                        return false;
                    }
                    if (data.objectMap.successFlag) {
                        common.showTips("验证码保存成功");
                    } else {
                        common.showTips("验证码保存失败");
                    }
                    window.parent.$("#theme_popover_input").find(".showInput .confirm").attr("disabled",false);
                },
                function(){
                    common.hideMask();
                    common.showTips("系统异常");
                    window.parent.$("#theme_popover_input").find(".showInput .confirm").attr("disabled",false);
                }
            );
        },
        function(){}
    );
}

/*function saveComment(orderOperationInfoId){
    var comment = window.parent.$("#order_remark_content").val();
    common.getByAjax(true, 'put', 'json', '/orderCenter/order/comment',
        {
            comment: comment,
            orderOperationInfoId:orderOperationInfoId
        },
        function (data) {
            popup.mask.hideFirstMask(false);
            if (data.result == 'success') {
                popup.mould.popTipsMould(false, "保存成功！", "first", "success", "", "56%",
                    function() {
                        popup.mask.hideFirstMask(false);
                    }
                );
            } else {
                popup.mould.popTipsMould(false, "保存失败！", "first", "error", "", "56%",
                    function() {
                        popup.mask.hideFirstMask(false);
                    }
                );
            }
        }, function () {
            popup.mould.popTipsMould(false, "操作失败！", "first", "error", "", "56%",
                function() {
                    popup.mask.hideFirstMask(false);
                }
            );
        }
    )
}*/
