var dataFunction = {
    "data": function (data) {
        data.partnerId = common.getUrlParam("id");
    },
    "fnRowCallback": function (nRow, aData) {
    },
}
var logList = {
    "url": '/operationcenter/partners/log',
    "type": "GET",
    "table_id": "log_list",
    "columns": [
        {"data": "operationTime", "title": "操作时间", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "operator", "title": "操作员", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "operationContent", "title": "操作内容", 'sClass': "text-center", "orderable": false,"sWidth":""},
    ],
}
var channelDetail = {
    downloadUrl:function(){
        var id = common.getUrlParam("id");
        window.location.href="/operationcenter/thirdParty/tocCooperate/updateUrl?id="+id;
    },
    //展示详情
    overview: function (id) {
        common.getByAjax(true, "get", "json", "/operationcenter/thirdParty/tocCooperate/findDetailsInfo", {id: id},
            function (data) {
                //渠道详情
                $("#partnerName").text(data.partner);
                $("#channelName").text("第三方渠道名称："+data.channelName);
                $("#channelCode").text(data.channelCode);
                $("#createdTime").html(data.createdTime)
                $("#id").text(data.id);
                // if (data.disabledChannel == false) {
                //     $("#disable").text("已禁用")
                // } else {
                //     $("#disable").text("已启用")
                // }
                $('#disabledChannel').bootstrapSwitch('state', !data.disabledChannel, true)
                if (data.buttJoint == true) {
                    $("#platform").text("A端");
                } else {
                    $("#platform").text("C端");
                }
                //前端页面配置
                if (data.reserve != null && data.reserve == true) {
                    $("#reserve").text("支持");
                } else {
                    $("#reserve").text("不支持");
                }
                if (data.supportPhoto != null && data.supportPhoto == true) {
                    $("#supportPhoto").text("支持");
                } else {
                    $("#supportPhoto").text("不支持");
                }
                if (data.showCustomService != null && data.showCustomService == true) {
                    $("#showCustomService").text("支持");
                } else {
                    $("#showCustomService").text("不支持");
                }
                if (data.serviceTel != null) {
                    $("#serviceTel").text(data.serviceTel);
                }
                if (data.home != null && data.home == true) {
                    $("#home").text("首页");
                } else {
                    $("#home").text("基本信息页");
                    $("#homePage").hide();
                }
                if (data.singleCompany != null && data.singleCompany == true) {
                    $("#quoteWay").text("直投");
                } else {
                    $("#quoteWay").text("比价");
                }
                if (data.homeFixBottom != null && data.homeFixBottom == true) {
                    $("#homeFixBottom").text("显示");
                } else {
                    $("#homeFixBottom").text("不显示");
                }
                if (data.showPartner != null && data.showPartner == true) {
                    $("#showPartner").text("显示");
                } else {
                    $("#showPartner").text("不显示");
                }
                if (data.baseLogin != null && data.baseLogin == true) {
                    $("#baseLogin").text("显示");
                } else {
                    $("#baseLogin").text("不显示");
                }
                if (data.baseCustomAndPhoto != null && data.baseCustomAndPhoto == true) {
                    $("#baseCustomAndPhoto").text("支持");
                } else {
                    $("#baseCustomAndPhoto").text("不支持");
                }
                if (data.baseBanner != null && data.baseBanner == true) {
                    $("#baseBanner").text("支持");
                } else {
                    $("#baseBanner").text("不支持");
                }
                if (data.baseOrder!=null && data.baseOrder==true){
                    $("#baseOrder").text("显示订单中心");
                }
                if (data.baseMine!=null && data.baseMine==true){
                    $("#baseOrder").text("显示个人中心");
                }
                if (data.baseOrder==false && data.baseMine==false){
                    $("#baseOrder").text("不显示")
                }
                if(data.hasWallet !=null && data.hasWallet==true){
                    $("#cheWallet").text("展示");
                }else{
                    $("#cheWallet").text("不展示");
                }
                if (data.cheWallet !=null && data.cheWallet==true){
                    $("#verifyWay").text("密码");
                } else if (data.cheWallet !=null && data.cheWallet==false) {
                    $("#verifyWay").text("验证码");
                } else {
                    $("#verifyWay").text("无");

                }
                if(data.orderGift!=null && data.orderGift==true){
                    $("#orderGift").text("显示");
                }else{
                    $("#orderGift").text("不显示");
                }
                if(data.orderInsuredCar !=null && data.orderInsuredCar==true){
                    $("#orderInsuredCar").text("支持");
                }else{
                    $("#orderInsuredCar").text("不支持");
                }
                if (data.successOrder !=null && data.successOrder==true){
                    $("#successOrder").text("显示")
                }else{
                    $("#successOrder").text("不显示")
                }
                if(data.homeUrl==null || data.homeUrl.length == 0){
                    $("#isHomePage").text("是");
                    $("#homeAddress").hide();
                }else{
                    $("#isHomePage").text("否");
                    $("#homeAddress").show();
                    $("#homeUrl").text(data.homeUrl);
                }
                if (data.googleTrackId!=null){
                    $("#googleTrackId").text(data.googleTrackId);
                }
                if (data.themeColor!=null){
                    $("#pageDownTone").text(data.themeColor);
                }
                if (data.hasOrderCenter!=null &&data.hasOrderCenter==true){
                    $("#hasOrderCenter").text("是");
                }else{
                    $("#hasOrderCenter").text("否");
                }
                if (data.isTelemarketing!=null && data.isTelemarketing==true){
                    $("#isTelemarketing").text("是");
                }else{
                    $("#isTelemarketing").text("否");
                }
                if (data.needSyncOrder != null && data.needSyncOrder == true) {
                    $("#isSync").text("是");
                    if (data.syncOrderUrl != null) {
                        $("#syncSite").text(data.syncOrderUrl);
                    }
                } else {
                    $("#isSync").text("否");
                    $(".isSyncHide").hide();
                }
                if (data.signature!=null){
                    $("#sign").text(data.signature);
                }
                if (data.supportAmend !=null && data.supportAmend == true) {
                    $("#isSupplement").text("是");
                } else {
                    $("#isSupplement").text("否");
                }
                if (data.logoImage != null && data.logoImage.length != 0) {
                    var src=data.logoImage+'?random='+Math.random();
                    $("#logoImage").attr("src",src);
                } else {
                    var logo = document.getElementById("logoImage");
                    logo.style.display="none";
                }
            },
            function () {
                $("#add_form").html("系统异常");
                $("#toSave").hide();
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },
    // initTemplateUrl: function (id) {
    //     common.getByAjax(true, "get", "json", "/operationcenter/channelRebate/template/url", {id: id},
    //         function (response) {
    //             $("#url_template").prop("href", response.message);
    //         }, function () {
    //             popup.mould.popTipsMould("模版地址初始化异常！！", popup.mould.first, popup.mould.error, "", "53%", null);
    //         });
    // },
    //编辑
    editChannelConf: function (id) {
        $("#updateChannel").unbind("click").bind({
            click: function () {
               window.open("toC_edit.html?id=" + id +"&random="+Math.random(),'_self');
            }
        });
    },
    init_switch:function(){
        $("#myTab a").click(function (e) {
            e.preventDefault();
            $(this).tab("show");
            var href = $(this).attr("href").replace("#", "");
            if ("config_info" == href) {
                $("#config_info").show();
                $("#operate_log").hide();
            } else {
                if(!datatables){
                    datatables = datatableUtil.getByDatatables(logList,dataFunction.data,dataFunction.fnRowCallback);
                    $("#log_list_length").hide();
                }
                $("#log_list").attr('style','width:100%');
                $("#config_info").hide();
                $("#operate_log").show();
            }
        });
    }
}
var datatables;

$(function () {
    var id = common.getUrlParam("id");
    channelDetail.init_switch();
    channelDetail.overview(id);
    //  channelDetail.initTemplateUrl(id);
    channelDetail.editChannelConf(id);
});
