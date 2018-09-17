/**
 * Created by wangshaobin on 2017/8/29.
 */
var answernInsuranceNewlyInput = {
    initChannel: function() {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/channel/getAllChannels", {},
            function(data) {
                if (data) {
                    var options = "<option value=''>请选择渠道</option>";
                    $.each(data, function(i, model){
                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                    });
                    answernInsuranceNewlyInput.setChannelSel("insuranceChannelSel",options);
                    answernInsuranceNewlyInput.setChannelSel("paymentChannelSel",options);
                }
            },function() {}
        );
    },
    setChannelSel: function(id,options){
        if($("#" + id).length>0){
            $("#" + id).append(options);
        }
    },
    checkChannel: function(id){
        var channel = $("#" + id).val();
        if (channel == ""){
            popup.mould.popTipsMould(false, "渠道不能为空", popup.mould.second, popup.mould.warning, "", "", null);
            return false;
        }
        return true;
    },
    checkMobile: function(mobile){
        if(mobile == ""){
            popup.mould.popTipsMould(false, "手机号不能为空", popup.mould.second, popup.mould.warning, "", "", null);
            return false;
        }
        if(!common.isMobile(mobile)){
            popup.mould.popTipsMould(false, "请输入正确的手机号", popup.mould.second, popup.mould.warning, "", "", null);
            return false;
        }
        return true;
    },
    checkOrderNo: function(orderNo){
        if(orderNo == ""){
            popup.mould.popTipsMould(false, "订单号不能为空", popup.mould.second, popup.mould.warning, "", "", null);
            return false;
        }
        return true;
    },
    getUserByMobile: function(mobile,channel){
        common.getByAjax(true, "get", "json", "/orderCenter/order/user",
            {
                mobile: mobile
            },
            function (data) {
                answernInsuranceNewlyInput.getUserCallBack(data,channel);
            }, function () {
            }
        );
    },
    getStatusByOrderNo: function (orderNo) {
        common.getByAjax(true, "get", "json", "/orderCenter/order/status",
            {
                orderNo: orderNo
            },
            function (data) {
                answernInsuranceNewlyInput.getStatusCallBack(data);
            }, function () {
            }
        );
    },
    getUserCallBack: function(data,channel){
        if(data.pass){
            var userId = data.message;
            window.open("/page/order/browser_insurance_input.html?source=answern&channel=" + channel + "&userId=" + userId);
        }else{
            popup.mould.popTipsMould(false, data.message, "first", "warning", "", "56%",
                function () {
                    popup.mask.hideFirstMask(false);
                }
            );
        };
    },
    getStatusCallBack: function(data){
        if(data.pass){
            var orderId = data.message;
            window.open("/page/order/client_insurance_input.html?source=answern&id=" + orderId);
        }else{
            popup.mould.popTipsMould(false, data.message, "first", "warning", "", "56%",
                function () {
                    popup.mask.hideFirstMask(false);
                }
            );
        };
    }
}

$(function(){
   //初始化三个按钮的点击事件
    $("#add_insurance_btn").bind({
        click: function () {
            var mobile = $("#insurance_mobile").val();
            var channel = $("#insuranceChannelSel").val();
            if(answernInsuranceNewlyInput.checkChannel("insuranceChannelSel") && answernInsuranceNewlyInput.checkMobile(mobile)){
                answernInsuranceNewlyInput.getUserByMobile(mobile,channel);
            }
        }
    });
    $("#payment_error_btn").bind({
        click: function () {
            var mobile = $("#payment_mobile").val();
            var channel = $("#paymentChannelSel").val();
            if(answernInsuranceNewlyInput.checkChannel("paymentChannelSel") && answernInsuranceNewlyInput.checkMobile(mobile)){
                answernInsuranceNewlyInput.getUserByMobile(mobile,channel);
            }
        }
    });
    $("#payment_over_btn").bind({
        click: function () {
            var orderNo = $("#order_no").val();
            if(answernInsuranceNewlyInput.checkOrderNo(orderNo)){
                answernInsuranceNewlyInput.getStatusByOrderNo(orderNo);
            }
        }
    });
    answernInsuranceNewlyInput.initChannel();
});
