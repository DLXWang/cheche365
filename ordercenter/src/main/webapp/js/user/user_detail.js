/**
 * Created by wangshaobin on 2017/3/30.
 */
var user_management_detail = {
    userId:"",
    detail_content:"",
    page: new Properties(1, ""),
    initPopContent: function() {
        var popupContent = $("#detail_content");
        if (popupContent.length > 0) {
            user_management_detail.detail_content = popupContent.html();
            popupContent.remove();
        }
    },
    initInfo:function(){
        user_management_detail.initUserInfo();
    },
    initUserInfo: function(){
        common.ajax.getByAjax(true, "get", "json",  "/orderCenter/outerUser/detail",
            {id: userId},
            function(data) {
                user_management_detail.fillUserInfo(data);
                user_management_detail.fillTelMarketingInfo(data);
            },function(){
                popup.mould.popTipsMould(false, "获取数据异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    fillUserInfo:function(data){
        $("#userName").html(data.name);
        $("#sex").html(data.sex);
        $("#birthday").html(data.birthday);
        $("#binding").html(data.binding);
        $("#userMobile").html(data.mobile);
        $("#nickName").html(data.nickName);
        $("#userEmail").html(data.email);
        $("#userType").html(data.type);
        $("#registerChannel").html(data.regChannel);
        $("#userSource").html(data.source);
        $("#registerIP").html(data.regIp);
        $("#audit").html(data.audit);
        $("#identity").html(data.identity);
    },
    fillTelMarketingInfo:function(data){
        $("#source").html(data.telMarketingCenterSource);
        $("#operator").html(data.operator);
        $("#triggerTime").html(data.triggerTime);
        $("#createTime").html(data.createTime);
        $("#updateTime").html(data.updateTime);
        $("#status").html(data.status);
    },
    popDetail: function(id) {
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/outerUser/findUserAuto/" + id, {},
            function(data) {
                popup.pop.popInput(true, user_management_detail.detail_content, popup.mould.first, "576px", "582px", "33%", "");
                $("#auto_detail_close").unbind("click").bind({
                    click : function() {
                        popup.mask.hideAllMask(false);
                    }
                });
                user_management_detail.fillDetailContent(data);
            },
            function() {
                popup.mould.popTipsMould(false, "获取车辆信息异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    fillDetailContent: function(data) {
        $("#detail_title").html($("#detail_title").html() + "&nbsp;<" + common.tools.checkToEmpty(data.licensePlateNo) + ">" );
        $("#detail_owner").text(common.tools.checkToEmpty(data.owner));
        $("#detail_identity").text(common.tools.checkToEmpty(data.identity));
        $("#detail_licensePlateNo").text(common.tools.checkToEmpty(data.licensePlateNo));
        $("#detail_vinNo").text(common.tools.checkToEmpty(data.vinNo));
        $("#detail_engineNo").text(common.tools.checkToEmpty(data.engineNo));
        $("#detail_enroll_date").text(common.tools.checkToEmpty(data.enrollDate));
        $("#detail_model").text(common.tools.checkToEmpty(data.model));
        $("#detail_brand_code").text(common.tools.checkToEmpty(data.brandCode));
        $("#detail_expire_date").text(common.tools.checkToEmpty(data.expireDate));
        var content="";
        if(data.userViewModels!=null){
            $.each(data.userViewModels,function(i,user){
                content+="<div class='col-sm-5 text-height-28 text-center'>" +
                    "<span class='text-height-28' id='detail_user_id'>"+common.tools.checkToEmpty(user.id)+"</span>" +
                    "</div>" +
                    "<div class='col-sm-5 text-height-28 text-center'>" +
                    "<span class='text-height-28' id='detail_mobile'>"+common.tools.checkToEmpty(user.mobile)+"</span>" +
                    "</div>";
            });
        }
        $("#auto_user_info").html(content);
        $("#driver_img").attr("src", "");
    },
    addComment: function(purchaseOrderId) {
        orderComment.popCommentList(purchaseOrderId, popup.mould.first);
    }
}
/* 用户订单列表查询 */
var list = {
    orderList: {
        "url": '/orderCenter/outerUser/findOrderInfoByUserId',
        "type": "GET",
        "table_id": "order_tab",
        "columns": [
            {"data": null, "title": "订单号", 'sClass': "text-center", "orderable": false,"sWidth":"140px"},
            {"data": "currentStatus.description", "title": "当前状态", 'sClass': "text-center", "orderable": false,"sWidth":"70px"},
            {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false,"sWidth":"100px"},
            {"data": "auto.owner", "title": "指定人", 'sClass': "text-center", "orderable": false,"sWidth":"70px"},
            {"data": null, "title": "车主车牌", 'sClass': "text-center", "orderable": false,"sWidth":"70px"},
            {"data": "paymentChannel.fullDescription", "title": "支付方式", 'sClass': "text-center", "orderable": false,"sWidth":"70px"},
            {"data": "insuranceCompany.name", "title": "地区</br>保险公司", 'sClass': "text-center", "orderable": false,"sWidth":"70px"},
            {"data": "paidAmount", "title": "实付金额</br>原始金额", 'sClass': "text-center", "orderable": false,"sWidth":"70px"},
            {"data": "confirmNo", "title": "支付号", 'sClass': "text-center", "orderable": false,"sWidth":"210px"},
            {"data": "gift", "title": "礼品", 'sClass': "text-center", "orderable": false},
            {"data": null, "title": "最后操作", 'sClass': "text-center", "orderable": false,"sWidth":"140px"},
        ]
    },
    autoList: {
        "url": '/orderCenter/outerUser/findAutoInfoByUserId',
        "type": "GET",
        "table_id": "auto_tab",
        "columns": [
            {"data": null, "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
            {"data": null, "title": "车牌号", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
            {"data": null, "title": "车主姓名", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        ]
    }
};

var dataFunction = {
    orderDataFunction : {
        "data": function (data) {
            data.id = common.getUrlParam("id");
        },
        "fnRowCallback": function (nRow, aData) {
            $orderNo = common.getOrderIconClean(aData.channelIcon) + '<a href="/page/order/order_detail.html?id=' + aData.purchaseOrderId + '" target="_blank">' + aData.orderNo + '</a><br/>' + aData.createTime;
            $status = common.tools.checkToEmpty(aData.currentStatus==null?"":aData.currentStatus.description);
            $comment = "<a href=\"javascript:;\" onclick=\"user_management_detail.addComment(" + aData.purchaseOrderId + "," + aData.orderOperationInfoId + ");\">查看备注</a>"+ (aData.latestComment ? ("<br/>" + common.tools.getCommentMould(aData.latestComment, 5)) : "");
            $assignerName = common.tools.checkToEmpty(aData.assignerName);
            $owner = common.tools.checkToEmpty(aData.auto==null?"":aData.auto.owner) + "<br/>" + common.tools.checkToEmpty(aData.auto==null?"":aData.auto.licensePlateNo);
            $paymentChannel = common.tools.checkToEmpty(aData.paymentChannel.fullDescription);
            $area = common.tools.checkToEmpty(aData.area.name) + "<br/>" + common.tools.checkToEmpty(aData.insuranceCompany.name);
            $price = common.tools.checkToEmpty(aData.payableAmount) + "<br/>" + common.tools.checkToEmpty(aData.paidAmount);
            $confirmNo = common.tools.checkToEmpty(aData.confirmNo);
            $gift = common.tools.checkToEmpty(aData.gift);
            $lastOperationTime = common.tools.checkToEmpty(aData.updateTime);
            $('td:eq(0)', nRow).html($orderNo);
            $('td:eq(1)', nRow).html($status);
            $('td:eq(2)', nRow).html($comment);
            $('td:eq(3)', nRow).html($assignerName);
            $('td:eq(4)', nRow).html($owner);
            $('td:eq(5)', nRow).html($paymentChannel);
            $('td:eq(6)', nRow).html($area);
            $('td:eq(7)', nRow).html($price);
            $('td:eq(8)', nRow).html($confirmNo);
            $('td:eq(9)', nRow).html($gift);
            $('td:eq(10)', nRow).html($lastOperationTime);
        }
    },
    autoDataFunction : {
        "data": function (data) {
            data.id = common.getUrlParam("id");
        },
        "fnRowCallback": function (nRow, aData) {
            $id = "<a href='javascript:;' onclick=user_management_detail.popDetail('" + aData.id + "');>" + aData.id + "</a>";
            $licensePlateNo = common.checkToEmpty(aData.licensePlateNo);
            $owner = common.checkToEmpty(aData.owner);
            $('td:eq(0)', nRow).html($id);
            $('td:eq(1)', nRow).html($licensePlateNo);
            $('td:eq(2)', nRow).html($owner);
        }
    }
};

var orderDatatables = datatableUtil.getByDatatables(list.orderList, dataFunction.orderDataFunction.data, dataFunction.orderDataFunction.fnRowCallback);
var autoDatatables = datatableUtil.getByDatatables(list.autoList, dataFunction.autoDataFunction.data, dataFunction.autoDataFunction.fnRowCallback);

$(function(){
    userId = common.getUrlParam("id");
    popup.insertHtml("#popupHtml");
    user_management_detail.initPopContent();
    user_management_detail.initInfo();
})
