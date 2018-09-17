$(function(){
    init.htmlInit();
    init.tabInit();
    popup.insertHtml($("#popupHtml"));

    $(".left_div ul li a").bind({
        click : function() {
            var href = $(this).attr("href");
            var position = $("#breadcrumbs");
            $(this).next().hide();
            //$(this).parent().addClass("active");
            //出单机构work around
            if ($(this).parent().attr('id') == 'institution_li'||$(this).parent().attr('id') == 'institution_li_temp') {
                document.getElementsByTagName('iframe')[0].style.height = '800px';
            } else {
                document.getElementsByTagName('iframe')[0].style.height = $(".right_div").height() + "px";
            }
            if (href.indexOf("#") < 0) {
                $("#someId").attr("src", href);
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
            window.scrollTo(0,0);
        }
    });

    /* 用户菜单 */
    $("#user_dropdown").hover(function(){
        $(".dropdown-menu").show();
    },function(){
        $(".dropdown-menu").hide();
    });
    $(".dropdown-menu").hover(function(){
        $(".dropdown-menu").show();
    },function(){
        $(".dropdown-menu").hide();
    });

});

function MyTab(element){
    this.aLis = $(element);
    var that=this;
    this.aLis.click(function(){
        that.aLis.removeClass("active");
        $(this).addClass("active");
    });
}

var init = {
    htmlInit : function() {
        //if ($(window).width() > 1500) {
        //    $(".layout_div").css("width", "100%");
        //    $(".right_div").css("width", "100%");
        //} else {
        //    $(".layout_div").css("width", "1635px");
        //    $(".right_div").css("width", "1635px");
        //}

        $(".right_div").height($('.layout_div').height() - $('.top_div').height());
        $('#someId').width($('.right_div').width() - 7);
        $('#someId').height($(".right_div").height());
        $('#someId').css("border","none");
        $("#someId").attr("src","welcome.html");
        $(".right_div").show();
        $("#left_navbar").height($('.layout_div').height() + $('#head-nav').height() + 52);

        new MyTab("#sidebar li.panel");
    },
    tabInit : function() {
        common.getByAjax(true, "get", "json", "/orderCenter/user/getCurrentUser", {},
            function(data) {
                if (data != null) {
                    $("#login_user_name").text(data.name);
                    $("#internalUserId").val(data.id);
                    cookie.setValue("odc_permission_code", data.permissionCode);
                    cookie.setValue("able_call", data.ableCall);
                    menu.init();
                    if (data.resetPasswordLockFlag) {
                        init.resetPasswordLock();
                    } else if (data.resetPasswordFlag) {
                        init.resetPassword();
                    }
                }
            },function(){
                /* 出现异常回到登录页，防止多余的标签显示 */
                window.location.href = "../home.jsp";
            }
        );
    },

    resetPassword: function() {
        user.init();
        $(".tipsContent").text("您的密码不安全，请重新设置密码");
        $("#safe_pwd_tip_id").show();
        $("#popover_normal_input").height(390);
        $("#content_close").hide();
    },
    resetPasswordLock: function () {
        user.init();
        $(".tipsContent").text("请重新设置密码，过期将锁定账户");
        $("#safe_pwd_tip_id").show();
        $("#popover_normal_input").height(390);
        $("#content_close").hide();
    }
}


