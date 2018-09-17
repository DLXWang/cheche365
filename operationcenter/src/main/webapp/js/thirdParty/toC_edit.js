var baseInfo = {
    id: '',
    reserve: '',
    supportPhoto: '',
    showCustomService: '',
    serviceTel: '',
    home: '',
    singleCompany: '',
    homeFixBottom: '',
    showPartner: '',
    baseLogin: '',
    baseCustomAndPhoto: '',
    baseBanner: '',
    baseOrder: '',
    baseMine: '',
    hasWallet: '',
    cheWallet: '',
    orderGift: '',
    orderInsuredCar: '',
    successOrder: '',
    orderUrl: '',
    homeUrl: '',
    googleTrackId: '',
    themeColor: '',
    synchro: '',
    address: '',
    Supplement:''

}
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
        {"data": "operationTime", "title": "操作时间", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "operator", "title": "操作员", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "operationContent", "title": "操作内容", 'sClass': "text-center", "orderable": false, "sWidth": ""},
    ],
}
var channelDetailedit = {
    downloadUrl: function () {
        var id = common.getUrlParam("id");
        window.location.href = "/operationcenter/thirdParty/tocCooperate/updateUrl?id=" + id;
    },
    //展示详情
    edit: function (id) {
        common.getByAjax(true, "get", "json", "/operationcenter/thirdParty/tocCooperate/findDetailsInfo", {id: id},
            this.initPage.bind(this),
            function () {
                $("#add_form").html("系统异常");
                $("#toSave").hide();
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },
    initPage: function (result) {
        var data
        if (result) {
            data = result
            this.pageData = result
        } else {
            data = this.pageData
        }
        //渠道详情
        $("#partnerName").text(data.partner);
        $("#channelName").text(data.channelName);
        $("#channelCode").text(data.channelCode);
        $("#createdTime").html(data.createdTime);
        $("#remark").text(data.remark);
        $("#id").text(data.id);
        if (data.buttJoint == true) {
            $("#buttJoint").text("ToA");
        } else {
            $("#buttJoint").text("ToC");
        }
        //前端页面配置
        if (data.reserve != null && data.reserve == true) {
            $("#reserve_1").prop("checked", true);
        } else {
            $("#reserve_2").prop("checked", true);
        }
        if (data.supportPhoto != null && data.supportPhoto == true) {
            $("#supportPhoto_1").prop("checked", true);
        } else {
            $("#supportPhoto_2").prop("checked", true);
        }
        if (data.showCustomService != null && data.showCustomService == true) {
            $("#showCustomService_1").prop("checked", true);
        } else {
            $("#showCustomService_2").prop("checked", true);
        }
        if (data.serviceTel != null) {
            $("#serviceTel").val(data.serviceTel);
        }
        if (data.home != null && data.home == true) {
            $("#homePageSelect").prop("selected", true);
            $("#homePage").show();
        } else {
            $("#basicInfoPage").prop("selected", true);
            $("#homePage").hide();
        }
        if (data.singleCompany != null && data.singleCompany == true) {
            $("#direct").prop("selected", true);
        } else {
            $("#parity").prop("selected", true);
        }
        if (data.homeFixBottom != null && data.homeFixBottom == true) {
            $("#homeFixBottom_1").prop("checked", true);
        } else {
            $("#homeFixBottom_2").prop("checked", true);
        }
        if (data.showPartner != null && data.showPartner == true) {
            $("#showPartner_1").prop("checked", true);
        } else {
            $("#showPartner_2").prop("checked", true);
        }
        if (data.baseLogin != null && data.baseLogin == true) {
            $("#baseLogin_1").prop("checked", true);
        } else {
            $("#baseLogin_2").prop("checked", true);
        }
        if (data.baseCustomAndPhoto != null && data.baseCustomAndPhoto == true) {
            $("#baseCustomAndPhoto_1").prop("checked", true);
        } else {
            $("#baseCustomAndPhoto_2").prop("checked", true);
        }
        if (data.baseBanner != null && data.baseBanner == true) {
            $("#baseBanner_1").prop("checked", true);
        } else {
            $("#baseBanner_2").prop("checked", true);
        }
        //订单中心/个人中心
        if (data.baseOrder == true && data.baseMine == true) {
            $("#base_1").prop("checked", true);
            $("#center_select").show();
        } else if (data.baseOrder == true && data.baseMine == false) {
            $("#base_1").prop("checked", true);
            $("#center_select").show();
        } else if (data.baseOrder == false && data.baseMine == true) {
            $("#base_1").prop("checked", true);
            $("#center_select").show();
        } else {
            $("#base_2").prop("checked", true);
            $("#center_select").hide();
        }
        if (data.baseOrder != null && data.baseOrder == true) {
            $("#baseOrder").prop("selected", true);
            $("#showWallet").hide();
        }
        if (data.baseMine != null && data.baseMine == true) {
            $("#baseMine").prop("selected", true);
            $("#showWallet").show();
        }
        if (data.hasWallet != null && data.hasWallet == true) {
            $("#hasWallet_1").prop("checked", true);
            $("#verifyWay").show();
        } else {
            $("#hasWallet_2").prop("checked", true);
            $("#verifyWay").hide();
        }
        if (data.cheWallet != null && data.cheWallet == true) {
            $("#pass").prop("selected", true);
        } else {
            $("#verifyCode").prop("selected", true);
        }
        if (data.orderGift != null && data.orderGift == true) {
            $("#orderGift_1").prop("checked", true);
        } else {
            $("#orderGift_2").prop("checked", true);
        }
        if (data.orderInsuredCar != null && data.orderInsuredCar == true) {
            $("#orderInsuredCar_1").prop("checked", true);
        } else {
            $("#orderInsuredCar_2").prop("checked", true);
        }
        if (data.successOrder != null && data.successOrder == true) {
            $("#successOrder_1").prop("checked", true);
        } else {
            $("#successOrder_2").prop("checked", true);
        }
        if (data.homeUrl == null || data.homeUrl.length == 0) {
            $("#isHomePage_1").prop("checked", true);
            $("#homeLinkUrl").hide();
        } else {
            $("#isHomePage_2").prop("checked", true);
            $("#homeLinkUrl").show();
            $("#homeUrl").val(data.homeUrl);
        }
        if (data.googleTrackId != null) {
            $("#googleTrackId").val(data.googleTrackId);
        }
        if (data.themeColor != null) {
            $("#themeColor").val(data.themeColor);
        }
        if (data.hasOrderCenter != null && data.hasOrderCenter == true) {
            $("#hasOrderCenter").text("是");
        } else {
            $("#hasOrderCenter").text("否");
        }
        if (data.isTelemarketing != null && data.isTelemarketing == true) {
            $("#isTelemarketing").text("是");
        } else {
            $("#isTelemarketing").text("否");
        }
        if (data.needSyncOrder != null && data.needSyncOrder == true) {
            $("#needSyncOrder_1").prop("checked", true);
            $("#syncOrder").show();
        } else {
            $("#needSyncOrder_2").prop("checked", true);
            $("#syncOrder").hide();
        }
        if (data.syncOrderUrl != null) {
            $("#syncOrderUrl").val(data.syncOrderUrl);
        }
        if (data.signature != null) {
            $("#signature").val(data.signature);
        }
        if (data.supportAmend != null && data.supportAmend == true) {
            $("#isSupplement_1").prop("checked",true);
        } else{
            $("#isSupplement_2").prop("checked",true);
        }
        if (data.logoImage !=null && data.logoImage.length != 0) {
            var src=data.logoImage+'?random='+Math.random();
            $("#logoImage").attr("src",src);
        }
        $('#disabledChannel').bootstrapSwitch('state', !data.disabledChannel, true)
    },
    initTemplateUrl: function (id) {
        common.getByAjax(true, "get", "json", "/operationcenter/thirdParty/tocCooperate/updateUrl", {id: id},
            function (response) {
                // $("#url_template").prop("href", response.message);
            }, function () {
                popup.mould.popTipsMould("模版地址初始化异常！！", popup.mould.first, popup.mould.error, "", "53%", null);
            });
    },
    //编辑
    updateChannelConf: function (id) {
        $("#commit").unbind("click").bind({
            click: function () {
                baseInfo.id = id;
                baseInfo.reserve = $("input[name='reserve']:checked").val();
                baseInfo.supportPhoto = $("input[name='supportPhoto']:checked").val();
                baseInfo.showCustomService = $("input[name='showCustomService']:checked").val();
                baseInfo.serviceTel = $("#serviceTel").val();
                baseInfo.home = $("#home").val();
                baseInfo.singleCompany = $("#singleCompany").val();
                baseInfo.homeFixBottom = $("input[name='homeFixBottom']:checked").val();
                baseInfo.showPartner = $("input[name='showPartner']:checked").val();
                baseInfo.baseLogin = $("input[name='baseLogin']:checked").val();
                baseInfo.baseCustomAndPhoto = $("input[name='baseCustomAndPhoto']:checked").val();
                baseInfo.baseBanner = $("input[name='baseBanner']:checked").val();
                if ($("input[name='base']:checked").val() == 1) {
                    if ($("#base_select").val() == 1) {
                        baseInfo.baseOrder = 1;
                        baseInfo.baseMine = 0;
                        baseInfo.hasWallet = 0;
                    } else {
                        baseInfo.baseOrder = 0;
                        baseInfo.baseMine = 1;
                        if ($("input[name='hasWallet']:checked").val() == 1) {
                            baseInfo.hasWallet = 1;
                            baseInfo.cheWallet = $("#cheWallet").val();
                        } else {
                            baseInfo.hasWallet = 0;
                        }
                    }

                } else {
                    baseInfo.baseOrder = 0;
                    baseInfo.baseMine = 0;
                    baseInfo.hasWallet = 0;
                }
                baseInfo.orderGift = $("input[name='orderGift']:checked").val();
                baseInfo.orderInsuredCar = $("input[name='orderInsuredCar']:checked").val();
                baseInfo.successOrder = $("input[name='successOrder']:checked").val();
                baseInfo.orderUrl = $("#orderUrl").val();
                if ($("input[name='isHomePage']:checked").val() == 1) {
                    baseInfo.homeUrl = "";
                } else {
                    baseInfo.homeUrl = $("#homeUrl").val();
                }
                baseInfo.googleTrackId = $("#googleTrackId").val();
                baseInfo.themeColor = $("#themeColor").val();
                baseInfo.synchro = $("input[name='synchro']:checked").val();
                baseInfo.address = $("#syncOrderUrl").val();
                baseInfo.supplement = $("input[name='supplement']:checked").val();

                common.getByAjax(true, "post", "json", "/operationcenter/thirdParty/tocCooperate/updateDetails", baseInfo,
                    function (data) {
                        if (data.pass) {
                            window.open("toC_details.jsp?id=" + data.message +"&random="+Math.random(),'_self');
                        } else {
                            alert(data.message);
                            popup.mould.popTipsMould("渠道修改异常！！", popup.mould.first, popup.mould.error, "", "53%", null);
                        }
                    },
                    function () {
                        popup.mould.popTipsMould("发生异常,获取不到data,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                    }
                );

            }
        });
    },
    init_switch: function () {

        $("#center_select").hide();
        $("#showWallet").hide();
        $("#verifyWay").hide();
        $("#orderLinkUrl").hide();
        $("#homeLinkUrl").hide();
        $("#syncOrder").hide();
        $("#myTab a").click(function (e) {
            e.preventDefault();
            $(this).tab("show");
            var href = $(this).attr("href").replace("#", "");
            if ("config_info" == href) {
                $("#config_info").show();
                $("#operate_log").hide();
            } else {
                if (!datatables) {
                    datatables = datatableUtil.getByDatatables(logList, dataFunction.data, dataFunction.fnRowCallback);
                    $("#log_list_length").hide();
                }
                $("#log_list").attr('style', 'width:100%');
                $("#config_info").hide();
                $("#operate_log").show();
            }
        });
    },
    clickEvents: function () {
        $("#home").unbind("change").bind({
            change: function () {
                if ($("#home").val() == 1) {
                    $("#homePage").show();
                } else {
                    $("#homePage").hide();
                }
            }
        });
        $("input[name='base']").unbind("change").bind({
            change: function () {
                if ($("input[name='base']:checked").val() == 1) {
                    $("#center_select").show();
                    if (($("#base_select").val() == 0)) {
                        $("#showWallet").show();
                        if ($("input[name='hasWallet']:checked").val() == 1) {
                            $("#verifyWay").show();
                        } else {
                            $("#verifyWay").hide();
                        }
                    } else {
                        $("#showWallet").hide();
                        $("#verifyWay").hide();
                    }
                } else {
                    $("#center_select").hide();
                    $("#showWallet").hide();
                    $("#verifyWay").hide();
                }
            }
        });
        $("#base_select").unbind("change").bind({
            change: function () {
                if (($("#base_select").val() == 0)) {
                    $("#showWallet").show();
                    if ($("input[name='hasWallet']:checked").val() == 1) {
                        $("#verifyWay").show();
                    } else {
                        $("#verifyWay").hide();
                    }
                } else {
                    $("#showWallet").hide();
                    $("#verifyWay").hide();
                }
            }
        });
        $("input[name='hasWallet']").unbind("change").bind({
            change: function () {
                if ($("input[name='hasWallet']:checked").val() == 1) {
                    $("#verifyWay").show();
                } else {
                    $("#verifyWay").hide();
                }
            }
        });
        $("input[name='isOrderPage']").unbind("change").bind({
            change: function () {
                if ($("input[name='isOrderPage']:checked").val() == 1) {
                    $("#orderLinkUrl").show();
                } else {
                    $("#orderLinkUrl").hide();
                }
            }
        });
        $("input[name='isHomePage']").unbind("change").bind({
            change: function () {
                if ($("input[name='isHomePage']:checked").val() == 0) {
                    $("#homeLinkUrl").show();
                } else {
                    $("#homeLinkUrl").hide();
                }
            }
        });
        $("input[name='synchro']").unbind("change").bind({
            change: function () {
                if ($("input[name='synchro']:checked").val() == 1) {
                    $("#syncOrder").show();
                } else {
                    $("#syncOrder").hide();
                }
            }
        });
        $("#homeUrl").unbind("change").bind({
            change: function () {
                var strRegex = '(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]';
                var re = new RegExp(strRegex);
                if (!re.test($("#homeUrl").val())) {
                    alert("请输入正确的首页链接地址！！");
                    $("#homeUrl").val("");
                }
            }
        });


    }
}

var datatables;
$(function () {
    var id = common.getUrlParam("id");
    channelDetailedit.init_switch();
    channelDetailedit.edit(id);
    channelDetailedit.clickEvents();
    // channelDetailedit.initTemplateUrl(id);
    channelDetailedit.updateChannelConf(id);
});
