/**
 * Created by wangfei on 2015/4/30.
 */
$(function () {
    init.htmlInit();
    init.tabInit();

    $(".react-component .message_ul a").bind({
        click: function () {
            $(".right_div").html('');
            $(".position-div .breadcrumb").html('');
            $('.react-body').show();
        }
    });
    //$(".left_div ul li a").bind({
    $(".old-left-nav ul li a").bind({
        click: function () {
            var href = $(this).attr("href");
            var position = $(".position-div .breadcrumb");

            if (href.indexOf("#") < 0) {
                init.fixContent(href);
                $('.react-body').hide();
                if ($(this).parent().parent().hasClass("thirdnav")) {
                    $(position).find("#thirdText").html($(this).find("span").html());
                    $(position).find("#secText").html($(this).parent().parent().siblings("a").find("span").html());
                    $(position).find("#firText").html($(this).parent().parent().parent().parent().siblings("a").find("span").html());
                    $(position).find("#thirdText").show();
                } else {
                    $(position).find("#secText").html($(this).find("span").html());
                    $(position).find("#firText").html($(this).parent().parent().siblings("a").find("span").html());
                    $(position).find("#thirdText").hide();
                }

                $(position).show();
            }
            common.scrollToTop();
        }
    });

    var beforeElement;
    $("#nav-top li.panel").find("a:first").bind({
        click: function () {
            $(beforeElement).css("background-color", "");
            $(beforeElement).css("color", "");
            $(this).css("background-color", "#ce5f48");
            $(this).css("color", "#fff");
            beforeElement = $(this);
        }
    });

    /* 用户菜单 */
    $("#user_dropdown").hover(function () {
        $(this).parent().find(".dropdown-menu").show();
    }, function () {
        $(this).parent().find(".dropdown-menu").hide();
    });
    $(".dropdown-menu").hover(function () {
        $(this).show();
    }, function () {
        $(this).hide();
    });

});

var init = {
    fixContent: function (href) {
        $.post(href, {}, function (res) {
            $(".right_div").html(res);
        });
    },
    htmlInit: function () {
        if ($(window).width() > 1500) {
            $(".layout_div").css("width", "98%");
            $(".right_div").css("width", "100%");
        } else {
            $(".layout_div").css("width", "1635px");
            $(".right_div").css("width", "1635px");
        }
        $(".right_div").height($('.layout_div').height() - $('.top_div').height());

        init.fixContent("welcome.jsp");
    },
    tabInit: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/user/getCurrentUser", {},
            function (data) {
                if (data != null) {
                    $("#login_user_name").text(data.name);
                    $("#internalUserId").val(data.id);
                    cookie.setValue("opc_permission_code", data.permissionCode);
                    init.MenuInit(data.permissionCode);
                    if (data.resetPasswordLockFlag) {
                        init.resetPasswordLock();
                    } else if (data.resetPasswordFlag) {
                        init.resetPassword();
                    }
                }
            }, function () {
                /* 出现异常回到登录页，防止多余的标签显示 */
                window.location.href = "../home.jsp";
            }
        );
    },
    MenuInit: function (permissionCode) {
        var permissionCodeArray = common.permission.getPermissionCodeArray();
        if (permissionCodeArray && permissionCodeArray.length > 0) {
            //合作商管理
            if (permissionCodeArray.indexOf("op0101") >= 0 || permissionCodeArray.indexOf("op0102") >= 0) {
                $("#partner_menu").show();
                if (permissionCodeArray.indexOf("op0101") >= 0) {//合作商
                    $("#partner_m").show();
                }
                if (permissionCodeArray.indexOf("op0102") >= 0) {//商务活动
                    $("#activity_m").show();
                }
            } else {
                $("#partner_menu").remove();
            }
            //微信后台管理
            if (permissionCodeArray.indexOf("op0201") >= 0 || permissionCodeArray.indexOf("op0202") >= 0) {
                $("#wechat_menu").show();
                if (permissionCodeArray.indexOf("op0201") >= 0) {//临时二维码
                    $("#temp_qrcode_m").show();
                }
                if (permissionCodeArray.indexOf("op0202") >= 0) {//永久二维码
                    $("#forever_qrcode_m").show();
                }
            } else {
                $("#wechat_menu").remove();
            }
            //短信中心
            if (permissionCodeArray.indexOf("op0301") >= 0 || permissionCodeArray.indexOf("op0302") >= 0 || permissionCodeArray.indexOf("op0303") >= 0 || permissionCodeArray.indexOf("op0304") >= 0 || permissionCodeArray.indexOf("op0305") >= 0) {
                $("#sms_menu").show();
                if (permissionCodeArray.indexOf("op0301") >= 0) {//短信模板管理
                    $("#sms_template_m").show();
                }
                if (permissionCodeArray.indexOf("op0302") >= 0) {//筛选用户功能管理
                    $("#filter_user_m").show();
                }
                if (permissionCodeArray.indexOf("op0303") >= 0) {//条件触发短信
                    $("#schedule_sms").show();
                }
                if (permissionCodeArray.indexOf("op0304") >= 0) {//条件触发短信日志
                    $("#conditions_log_sms").show();
                }
                if (permissionCodeArray.indexOf("op0305") >= 0) {//主动发送短信
                    $("#adhoc_sms").show();
                }
            } else {
                $("#sms_menu").remove();
            }
            //红包发送审核
            if (permissionCodeArray.indexOf("op0401") >= 0) {
                $("#red_menu").show();
                $("#red_m").show();
            } else {
                $("#red_menu").remove();
            }

            //活动
            if (permissionCodeArray.indexOf("op0501") >= 0) {
                $("#activity_menu").show();
                $("#activity_li").show();
                $("#gift_li").show();
            } else {
                $("#activity_menu").remove();
            }
            //平台导入
            if (permissionCodeArray.indexOf("op0601") >= 0) {
                $("#import_menu").show();
                $("#import_li").show();
            } else {
                $("#import_menu").remove();
            }
            if (permissionCodeArray.indexOf("op0701") >= 0 || permissionCodeArray.indexOf("op0702") >= 0 || permissionCodeArray.indexOf("op0703") >= 0) {
                $("#data_statistics_menu").show();
                if (permissionCodeArray.indexOf("op0701") >= 0) {
                    $("#data_search_li").show();
                }
                if (permissionCodeArray.indexOf("op0702") >= 0) {
                    $("#create_url_li").show();
                }
                if (permissionCodeArray.indexOf("op0703") >= 0) {
                    $("#SEOTrace_li").show();
                }
            } else {
                $("#data_statistics_menu").remove();
            }

            //渠道配置
            if (permissionCodeArray.indexOf('op0801') >= 0) {
                $("#channel_rebate_menu").show();
                $("#channel_rebate_li").show();
            } else {
                $("#channel_rebate_menu").remove();
            }
            //报价上下线
            if (permissionCodeArray.indexOf('op0901') >= 0) {
                $("#quote_offline_menu").show();
                $("#quote_offline_rebate_li").show();
            } else {
                $("#quote_offline_menu").remove();
            }

            //用户管理
            if (permissionCodeArray.indexOf('op1001') >= 0) {
                $("#user_manager_menu").show();
                $("#user_manager_info_li").show();
            } else {
                $("#user_manager_menu").remove();
            }

            //第三方合作
            if (permissionCodeArray.indexOf('op1101') >= 0 || permissionCodeArray.indexOf('op1102') >= 0 || permissionCodeArray.indexOf('op1103') >= 0) {
                $("#third_party_menu").show();
                if (permissionCodeArray.indexOf('op1101') >= 0) {
                    $("#official_partner_li").show();
                }
                if (permissionCodeArray.indexOf('op1102') >= 0) {
                    $("#toc_official_partner_li").show();
                }
                if (permissionCodeArray.indexOf('op1103') >= 0) {
                    $("#toa_official_partner_li").show();
                }
            }

            //点位管理
            if (permissionCodeArray.indexOf('op1201') >= 0 || permissionCodeArray.indexOf('op1202') >= 0) {
                $("#rebate_manage_menu").show();
                if (permissionCodeArray.indexOf('op1201') >= 0) {
                    $("#institution_contract_li").show();
                }
                if (permissionCodeArray.indexOf('op1202') >= 0) {
                    $("#institution_rebate_li,#institution_rebate_history_li,#institution_rebate_draft_li").show();
                }
            }
        }

        $('#accordion').slimScroll({
            height: ($(window).height() - 50) + 'px'
        })
        $(window).resize(function () {
            $('#accordion').slimScroll({
                height: ($(window).height() - 50) + 'px'
            })
        })
    },
    logout: function () {
        //清空cookie
        cookie.deleteCookie("internalUserId");
        cookie.deleteCookie("login_user_name");
        cookie.deleteCookie("opc_permission_code");
        window.location.href = "/operationcenter/logout";
    },
    resetPassword: function () {
        user.init();
        $(".tipsContent").text("您的密码不安全，请重新设置密码");
        $("#safe_pwd_tip_id").show();
        $("#popover_normal_input").height(385);
        $("#content_close").hide();
    },
    resetPasswordLock: function () {
        user.init();
        $(".tipsContent").text("请重新设置密码，过期将锁定账户");
        $("#safe_pwd_tip_id").show();
        $("#popover_normal_input").height(385);
        $("#content_close").hide();
    }
}




