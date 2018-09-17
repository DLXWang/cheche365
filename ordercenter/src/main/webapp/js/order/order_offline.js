/**
 * Created by wangfei on 2015/4/30.
 */
Order.prototype = {
    read : function readData(data, order){
        var content = "";
        $.each(data.viewList,function(i,model){
            content += "<tr class='text-center' id='tab_tr" + model.orderOperationId + "'>" +
                "<td><a href='order_detail.html?id=" + model.purchaseOrderId + "' target='_blank'>" + model.orderNo + "</a></td>" +
                "<td>" + common.checkToEmpty(model.owner) + "</td>" +
                "<td>" + common.checkToEmpty(model.licenseNo) + "</td>" +
                "<td>" + common.checkToEmpty(model.insuranceCompany) + "</td>" +
                "<td>" + common.checkToEmpty(model.sumPremium) + "</td>" +
                "<td>" + common.checkToEmpty(model.createTime) + "</td>" +
                "<td>" + common.checkToEmpty(model.assignerName) + "</td>" +
                "<td id='operator_td'>" + common.checkToEmpty(model.operatorName) + "</td>" +
                "<td id='updateTime_td'>" + common.checkToEmpty(model.updateTime) + "</td>" +
                "<td id='payStatus_td'>" + common.checkToEmpty(model.payStatus) + "</td>" +
                "<td id='currentStatus_td'>" + common.checkToEmpty(model.currentStatus) + "</td>" +
                "<td id='operationSelect_td'>" + model.optionValue;
            //if (model.insuranceCompanyCode == "SINOSIG" && (model.statusId == 5 || model.statusId == 6)) {
            //    content += "<div id='verificationCodeBtn' style='margin-top: -15px;'></br><button style='width: 98%;height: 25px;' id='sendBtn' type='button' class='button btn-primary' onclick='sendVerificationCode(" + model.purchaseOrderId + "," +  model.orderOperationId + ");'>发送验证码</button></br><button style='width: 98%;margin-top: 4px;height: 25px;' id='saveBtn' type='button' class='button btn-primary' onclick='showVerificationCodeTips(" + model.purchaseOrderId + ");'>保存验证码</button></div>";
            //} else {
            //    content += "<div id='verificationCodeBtn' style='margin-top: -15px; display: none;'></br><button style='width: 98%;height: 25px;' id='sendBtn' type='button' class='button btn-primary' onclick='sendVerificationCode(" + model.purchaseOrderId + "," +  model.orderOperationId + ");'>发送验证码</button></br><button style='width: 98%;margin-top: 4px;height: 25px;' id='saveBtn' type='button' class='button btn-primary' onclick='showVerificationCodeTips(" + model.purchaseOrderId + ");'>保存验证码</button></div>";
            //}
            content += "<div id='verificationCodeBtn' style='margin-top: -15px; display: none;'></br><button style='width: 98%;height: 25px;' id='sendBtn' type='button' class='button btn-primary' onclick='sendVerificationCode(" + model.purchaseOrderId + "," +  model.orderOperationId + ");'>发送验证码</button></br><button style='width: 98%;margin-top: 4px;height: 25px;' id='saveBtn' type='button' class='button btn-primary' onclick='showVerificationCodeTips(" + model.purchaseOrderId + ");'>保存验证码</button></div>";
            if(model.statusId == 1||model.statusId == 2){
                content += "<div id='changeToOnlinePayBtn' style='margin-top: 5px;'><button style='width: 98%;height: 25px;' type='button' class='button btn-primary' onclick='changeToOnlinePay(\"" +$('#keyword').val()+"\","+ model.purchaseOrderId + ");'>转为线上支付</button></div>";
            }else{
                content += "<div id='changeToOnlinePayBtn' style='margin-top: 5px;display: none;'><button style='width: 98%;height: 25px;' type='button' class='button btn-primary' onclick='changeToOnlinePay(\"" +$('#keyword').val()+"\","+ model.purchaseOrderId + ");'>转为线上支付</button></div>";
            }
            if(model.statusId == 10){
                content += "<div id='insuranceLinkBtn' style='margin-top: 5px;'><button style='width: 98%;height: 25px;' type='button' class='button btn-primary' onclick='checkToInsurance(" + model.purchaseOrderId + ");'>录入保单</button></div>";
            }else{
                content += "<div id='insuranceLinkBtn' style='margin-top: 5px; display: none;'><button style='width: 98%;height: 25px;' type='button' class='button btn-primary' onclick='checkToInsurance(" + model.purchaseOrderId + ");'>录入保单</button></div>";
            }
            content += "</td>" +
                "<td id='remark_td'><span title='" +((model.remark==null)?"":model.remark.replace(/\\r\\n/g,'\n'))+ "'>"
                + common.getFormatComment((model.remark==null)?"":model.remark.replace(/\\r\\n/g,''), 7)
                + "</span><a style=\"margin-left: 10px;float: right;\" href=\"javascript:;\" onclick=\"offline.editRemark('" + order.keyword + "'," + model.orderOperationId + ",'" + model.orderNo  + "');\">修改</a></td>" +
                "<td id='payTime_td' style='display: none;'>" + common.checkToEmpty(model.payTime) + "</td>" +
                "</tr>";
        });
        return content;
    }
}
$(function(){
    var order = new Order();
    order.mark = "offline";

    //common.getByAjax(true, "get", "json", "/orderCenter/order/dump", null,
    //    function(data){
    //        if(data){
    //            offline.listOrder(order);
    //        }
    //    },
    //    function(){}
    //);
    offline.listOrder(order);

    $("#pageUp").bind({
        click : function(){
            order.currentPage-- ;
            offline.listOrder(order);
        }
    });

    $("#pageDown").bind({
        click : function(){
            order.currentPage++ ;
            offline.listOrder(order);
        }
    });

    $("#searchBtn").bind({
        click : function(){
            if($.trim($("#keyword").val()) == "" ){
                common.showTips("请输入搜索内容");
                return false;
            }
            order.keyword = $("#keyword").val();
            order.currentPage = 1;
            offline.listOrder(order);
        }
    });

    /* 父页面的元素 */
    var parent = window.parent;
    /* 带时间确认 */
    var dateConfirm = parent.$("#theme_popover_date");
    /* 返回上一步 */
    var returnBack = parent.$("#theme_popover_return");
    /* 正常的确认 */
    var reConfirm = parent.$("#theme_popover_publicConfirm");
    /* 带输入域 */
    var textInput = parent.$("#theme_popover_input");

    /* 再次确认 收款 派送 */
    dateConfirm.find("#chooseDate").find(".confirm").unbind("click").bind({
        click : function(){
            if($.trim(dateConfirm.find("#reConfirmDate").val()) == ""){
                common.showSecondTips("请选择时间");
                return false;
            }
            dateConfirm.find("#chooseDate").hide();
            dateConfirm.find("#showDate").show();
            dateConfirm.find("#showDate").find("#dateText").text(dateConfirm.find("#reConfirmDate").val());
        }
    });
    dateConfirm.find("#chooseDate").find(".cancel").unbind("click").bind({
        click : function(){
            common.hideMask();
            showFirstOption();
        }
    });
    dateConfirm.find("#showDate").find(".back").unbind("click").bind({
        click : function(){
            dateConfirm.find("#chooseDate").show();
            dateConfirm.find("#showDate").hide();
        }
    });
    dateConfirm.find("#showDate").find(".confirm").unbind("click").bind({
        click : function(){
            var date = dateConfirm.find("#reConfirmDate").val();//日期
            offline.operateOrder($("#operationId").val(), $("#optionVal").val(), date, "", "", $("#tabNo").val());
        }
    });

    /* 出单 出单完成 录入保单 订单完成 用户取消 */
    reConfirm.find(".confirm").unbind("click").bind({
       click : function(){
           offline.operateOrder($("#operationId").val(), $("#optionVal").val(), "", "", "", $("#tabNo").val(), order);
       }
    });
    reConfirm.find(".cancel").unbind("click").bind({
        click : function(){
            common.hideMask();
            showFirstOption();
        }
    });

    /* 核保完成 */
    textInput.find("#showInput").find(".confirm").unbind("click").bind({
        click : function(){
            if($.trim(textInput.find("#showInput").find("#textInput").val()) == ""){
                common.showSecondTips("请输入确认单号");
                return false;
            }
            offline.operateOrder($("#operationId").val(), $("#optionVal").val(), "", "", textInput.find("#showInput").find("#textInput").val(), $("#tabNo").val());
        }
    });
    textInput.find("#showInput").find(".cancel").unbind("click").bind({
        click : function(){
            common.hideMask();
            showFirstOption();
        }
    });

    /* 返回未确认 返回去出单 返回去收费 返回派送 返回录入保单 */
    returnBack.find("#reasonSel").unbind("click").bind({
        change : function(){
            if($(this).val() == "other"){
                returnBack.find("#reasonDiv").show();
            }else{
                returnBack.find("#reasonDiv").hide();
            }
        }
    });
    returnBack.find(".confirm").unbind("click").bind({
       click : function(){
           var reason;//原因
           if(returnBack.find("#reasonSel").val() == "other"){
               if($.trim(returnBack.find("#reasonDiv").find("#reason").val()) == ""){
                   common.showSecondTips("请输入返回原因");
                   return false;
               }
               reason = returnBack.find("#reasonDiv").find("#reason").val();
           }else{
               reason = returnBack.find("#reasonSel").val();
           }
           offline.operateOrder($("#operationId").val(), $("#optionVal").val(), "", reason, "", $("#tabNo").val());
       }
    });
    returnBack.find(".cancel").unbind("click").bind({
        click : function(){
            common.hideMask();
            showFirstOption();
        }
    });
});

//function changeToOnlinePay(keyword,purchaseOrderId,orderNo){
//    var order = new Order();
//    order.mark = "offline";
//    var turnHtml = "<div class='theme_poptit'>"+
//    "<a id='pay_channel_close' title='关闭' class='close'><i class='glyphicon glyphicon-remove'></i></a>"+
//    "<h4 class='text-center'>请选择订单"+orderNo+"的线上支付渠道</h4></div>"+
//    "<div style='padding-top: 15px;padding-left: 25px;'>"+
//    "<form id='pay_channel_form' class='form-input form-horizontal'>"+
//    "<div class='col-sm-3'><label class='control-label'>支付方式:</label></div>"+
//    "<div class='col-sm-5'><ul><li style='list-style-type:none;'><label class='radio-inline'><input type='radio' name='payChannel'  value='WEBCHAT' checked> 微信支付</label></li>"+
//    "<li style='list-style-type:none;'><label class='radio-inline'><input type='radio' name='payChannel'  value='UNIONPAY'> 银联支付</label></li></ul></div>"+
//    "<div style='padding-top: 25px' class='form-group'>"+
//    "<div class='text-center'>"+
//    "<input id='chance_pay_channel' class='btn btn-danger text-input-200' value='提交'>"+
//    "</div></div></form></div>"
//    popup.pop.popInput(false,turnHtml, "first", "490px", "220px", "56%");
//
//   window.parent.$('#pay_channel_close').unbind('click').click( function() {
//            popup.mould.popConfirmMould(false, '确定放弃当前操作?', 'second',
//            ['YES', 'NO'], null,
//            function() { popup.mask.hideAllMask(false);},
//            function() { popup.mask.hideSecondMask(false); }
//   )});
//   window.parent.$('#chance_pay_channel').unbind('click').bind({
//         click :function(){
//                 $("#chance_pay_channel").attr("disabled", true);
//                 common.getByAjax(true, 'put', 'json', '/orderCenter/order/payment/channel/change',  {orderId :purchaseOrderId, payChannel:window.parent.$("input[name='payChannel']:checked").val()},
//                      function(data) {
//                           if(data.result=='success'){
//                                 popup.mould.popTipsMould(false, '成功修改支付渠道', 'second', 'success', '', '56%',
//                                 function() {
//                                      popup.mask.hideAllMask(false);
//                                      order.keyword = keyword;
//                                                  order.currentPage = 1;
//                                      offline.listOrder(order);
//                                 }
//                                 );
//                           }else{
//                                 popup.mould.popTipsMould(false, '操作失败，请稍候重试', 'second', 'error', '', '56%',
//                                 function() {
//                                       popup.mask.hideSecondMask(false);
//                                 }
//                                 );
//                            }
//                      },function(){
//                            popup.mould.popTipsMould(false, '操作失败，请稍候重试', 'second', 'error', '', '56%',
//                                  function() {
//                                       popup.mask.hideSecondMask(false);
//                                  }
//                            );
//                      }
//                 )
//         }
//   })
//}

function changeToOnlinePay(keyword, purchaseOrderId) {
    popup.mould.popConfirmMould(false, "确认转为线上支付方式？", "first", "", "56%",
        function() {
            offline.changePaymentChannel(keyword, purchaseOrderId);
        },
        function() {
            popup.mask.hideFirstMask(false);
        }
    );
}

var offline = {
    changePaymentChannel : function (keyword, purchaseOrderId) {
        common.getByAjax(true, 'put', 'json', '/orderCenter/order/payment/channel/change',
            {
                orderId: purchaseOrderId
            },
            function (data) {
                popup.mask.hideFirstMask(false);
                if (data.result == 'success') {
                    var order = new Order();
                    order.keyword = keyword;
                    order.currentPage = 1;
                    order.mark = "offline";
                    offline.listOrder(order);
                } else {
                    popup.mould.popTipsMould(false, "转换线上支付方式失败！", "first", "error", "", "56%",
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
    },
    listOrder : function(order){
        common.getByAjax(true,"get","json","/orderCenter/order/offline",{keyword : order.keyword,currentPage : order.currentPage,
                pageSize : order.pageSize, mark : order.mark},
            function(data){
                $("#offline_tab tbody").empty();
                if(data == null){
                    common.showTips("未检索到未付款的订单");
                    return false;
                }
                $("#totalCount").text(data.pageInfo.totalElements);
                if(data.pageInfo.totalPage > 1){
                    $("#page_up_down").show();
                }
                if(order.currentPage < 2){
                    $("#page_up_down").find("#pageUp").hide();
                }else{
                    $("#page_up_down").find("#pageUp").show();
                }
                if(order.currentPage >= data.pageInfo.totalPage){
                    $("#page_up_down").find("#pageDown").hide();
                }else{
                    $("#page_up_down").find("#pageDown").show();
                }
                if(data.pageInfo.totalElements <= order.pageSize){
                    $("#page_up_down").find("#pageDown").hide();
                    $("#page_up_down").find("#pageDown").hide();
                }
                $("#offline_tab tbody").append(order.read(data, order));
                window.parent.scrollTo(0,0);
            },function(){
                common.showTips("获取线下订单列表失败！");
            }
        );
    },
    operateOrder : function(orderOperationId, optionVal, date, reason, confirmNo, i, orderObj){
        window.parent.$(".confirm").attr("disabled", true);
        window.parent.$(".cancel").attr("disabled", true);
        common.getByAjax(true,"get","json","/orderCenter/order/transition",
            {
                orderId:orderOperationId,
                currentStatus:optionVal,
                date:date,reason:reason,
                confirmNo:confirmNo
            },
            function(data){
                common.hideMask();
                window.parent.$(".confirm").attr("disabled", false);
                window.parent.$(".cancel").attr("disabled", false);
                if(data.result == "fail"){
                    common.showTips(data.message);
                    showFirstOption();
                    return false;
                }
                if(data.result == "success" && data.objectMap.sendMessage == "2"){
                    common.showTips("发送邮件成功");
                }
                if(data.result == "success" && data.objectMap.sendMessage == "3"){
                    common.showTips("发送邮件失败");
                }
                if(data.result == "success" && data.objectMap.sendMessage == "4"){
                    common.showTips("发送短信成功");
                }
                if(data.result == "success" && data.objectMap.sendMessage == "5"){
                    common.showTips("发送短信失败");
                }

                //用户取消后后动态移除并重新查询
                if (optionVal == "12") {
                    $("#tab_tr"+i).remove();
                    offline.listOrder(orderObj);
                    return false;
                }

                var order = data.objectMap.order;
                $("#tab_tr"+i).find("#operator_td").html(common.checkToEmpty(order.operatorName));
                $("#tab_tr"+i).find("#updateTime_td").html(common.checkToEmpty(order.updateTime));
                $("#tab_tr"+i).find("#payStatus_td").html(common.checkToEmpty(order.payStatus));
                $("#tab_tr"+i).find("#currentStatus_td").html(common.checkToEmpty(order.currentStatus));

                $("#tab_tr"+i).find("#operationSelect_td select").remove();
                if (order.optionValue == "无") {
                    $("#tab_tr"+i).find("#operationSelect_td").html("无");
                } else {
                    var first = $("#tab_tr"+i).find("#operationSelect_td").children().first();
                    $(order.optionValue).insertBefore($(first));
                }

                //if (order.insuranceCompanyCode == "SINOSIG" && (order.statusId == 5 || order.statusId == 6)) {
                //    $("#tab_tr"+i).find("#operationSelect_td").find("#verificationCodeBtn").show();
                //} else {
                //    $("#tab_tr"+i).find("#operationSelect_td").find("#verificationCodeBtn").hide();
                //}

                if(order.statusId == 1||order.statusId == 2){
                    $("#tab_tr"+i).find("#operationSelect_td").find("#changeToOnlinePayBtn").show();
                }else{
                    $("#tab_tr"+i).find("#operationSelect_td").find("#changeToOnlinePayBtn").hide();
                }

                if(order.statusId == 10){
                    $("#tab_tr"+i).find("#operationSelect_td").find("#insuranceLinkBtn").show();
                }else{
                    $("#tab_tr"+i).find("#operationSelect_td").find("#insuranceLinkBtn").hide();
                }

                //$("#tab_tr"+i).find("#remark_td").html(common.checkToEmpty(order.remark));
            },
            function(){
                window.parent.$(".confirm").attr("disabled", false);
                window.parent.$(".cancel").attr("disabled", false);
                common.showTips("操作失败");
                showFirstOption();
            }
        );
    },
    editRemark: function(keyword, orderOperationId, orderNo) {
        var remark = $("#tab_tr" + orderOperationId).find("#remark_td span").attr("title");
        var content = "<div class=\"theme_poptit\">" +
            "<a id=\"order_remark_close\" href=\"javascript:;\" title=\"关闭\" class=\"close\"><i class=\"glyphicon glyphicon-remove\"></i></a>" +
            "<h4 class=\"text-center\">修改订单" + orderNo + "备注&nbsp;</h4>" +
            "</div>" +
            "<div style=\"padding-top: 15px;padding-left: 25px;\">" +
            "<textarea id=\"order_remark_content\" style=\"width:400px;resize:none;vertical-align:middle;\" maxlength=\"2000\" rows=\"6\">" + remark + "</textarea>" +
            "</div>" +
            "<div id=\"btnGroup\" class=\"text-center\" style=\"padding-top: 20px;\">" +
            "<button id=\"order_remark_save\" class=\"btn btn-danger\">保存</button>" +
            "</div>";

        popup.pop.popInput(false, content, "first", "450px", "280px", "49%", null);
        window.parent.$("#order_remark_close").unbind("click").bind({
            click : function() {
                popup.mask.hideFirstMask(false);
            }
        });
        window.parent.$("#order_remark_save").unbind("click").bind({
            click : function() {
                var comment = window.parent.$("#order_remark_content").val();
                if(!common.isEmpty(comment) && common.isSingleQuote(comment)) {
                    popup.mould.popTipsMould(false, "不允许输入单引号！", "second", "error", "", "55%",
                        function() {
                            popup.mask.hideSecondMask(false);
                        });
                    return false;
                }
                common.getByAjax(true, 'put', 'json', '/orderCenter/order/comment',
                    {
                        comment: comment,
                        orderOperationInfoId: orderOperationId
                    },
                    function (data) {
                        if (data.result == 'success') {
                            popup.mould.popTipsMould(false, "保存成功！", "second", "success", "", "55%",
                                function() {
                                    var order = new Order();
                                    order.keyword = keyword;
                                    order.currentPage = 1;
                                    order.mark = "offline";
                                    offline.listOrder(order);
                                    popup.mask.hideAllMask(false);
                                }
                            );
                        } else {
                            popup.mould.popTipsMould(false, "保存失败！", "second", "error", "", "55%",
                                function() {
                                    popup.mask.hideSecondMask(false);
                                }
                            );
                        }
                    }, function () {
                        popup.mould.popTipsMould(false, "操作失败！", "second", "error", "", "55%",
                            function() {
                                popup.mask.hideFirstMask(false);
                            }
                        );
                    }
                )
            }
        });
    }
}
