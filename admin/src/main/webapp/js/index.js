/**
 * Created by wangfei on 2015/4/30.
 */
$(function(){
    index.init();
});

var index = {
    beforeElement: "",
    init: function() {
        index.initIndexWidth();
        index.initIndexHeight();
        index.fixIndexContent("welcome.jsp");
        index.initMenuBar();
        index.initSideBar();
        index.tabInit();
    },
    initMenuBar: function() {
        $(".left_div ul li a").bind({
            click : function() {
                var href = $(this).attr("href");
                var position = $(".position-div .breadcrumb");

                if (href.indexOf("#") < 0) {
                    index.fixIndexContent(href);

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
                common.tools.scrollToTop();
            }
        });

        $("#nav-top li.panel").find("a:first").bind({
            click : function() {
                $(index.beforeElement).css("background-color", "");
                $(index.beforeElement).css("color", "");
                $(this).css("background-color", "#ce5f48");
                $(this).css("color", "#fff");

                index.beforeElement = $(this);
            }
        });

        index.handlerMenuBar();
    },
    initSideBar: function() {
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
        index.handlerSideBar();
    },
    initIndexWidth: function() {
        $(".layout_div").css("width", "97%");
        $(".right_div").css("width", "100%");
    },
    initIndexHeight: function() {
        $(".right_div").height($(".layout_div").height() - $(".top_div").height());
    },
    fixIndexContent: function(href) {
        $.post(href, {}, function (res) {
            $(".right_div").html(res);
        });
    },
    handlerSideBar: function() {

    },
    handlerMenuBar: function() {

    },
    tabInit : function() {
        common.ajax.getByAjax(true, "get", "json", "/admin/internalUser/currentUser", {},
            function(data) {
                if (data != null) {
                    $("#login_user_name").text(data.name);
                    $("#internalUserId").val(data.id);
                    //保存用户名和权限到cookie
                    cookie.setValue("login_user_name",data.name);
                    //登录用户名
                    cookie.setValue("login_user_email",data.email);
                    cookie.setValue("adm_permission_code",data.permissionCode);
                    index.menuInit();
                    if (data.resetPasswordLockFlag) {
                        index.resetPasswordLock();
                    } else if (data.resetPasswordFlag) {
                        index.resetPassword();
                    }
                }
            },function(){
                /* 出现异常回到登录页，防止多余的标签显示 */
                window.location.href = "../home.jsp";
            }
        );
    },
    menuInit : function() {
        var permissionCodeArray = common.permission.getPermissionCodeArray();
        if(permissionCodeArray && permissionCodeArray.length>0){
            //用户管理
            if(permissionCodeArray.indexOf("ad0101")>=0){//用户管理
                $("#users_menu").show();
                $("#users_li").show();
            }else{
                $("#users_menu").remove();
            }
            //车辆管理
            if(permissionCodeArray.indexOf("ad0201")>=0){//车辆管理
                $("#cars_menu").show();
                $("#cars_li").show();
            }else{
                $("#cars_menu").remove();
            }
            //系统账号
            if(permissionCodeArray.indexOf("ad0301")>=0 || permissionCodeArray.indexOf("ad0302")>=0){
                $("#accounts_menu").show();
                if(permissionCodeArray.indexOf("ad0301")>=0){//内部账号
                    $("#in_li").show();
                }
                if(permissionCodeArray.indexOf("ad0302")>=0){//外部账号
                    $("#out_li").show();
                }
            }else{
                $("#accounts_menu").remove();
            }

            //角色权限管理
            if(permissionCodeArray.indexOf("ad0401")>=0 || permissionCodeArray.indexOf("ad0402")>=0){
                $("#roles_menu").show();
                if(permissionCodeArray.indexOf("ad0401")>=0){//角色属性
                    $("#role_li").show();
                }
                if(permissionCodeArray.indexOf("ad0402")>=0){//权限条目
                    $("#permission_li").show();
                }
            }else{
                $("#roles_menu").remove();
            }


            //定时任务管理
            if(permissionCodeArray.indexOf("ad0501")>=0){
                $("#task_menu").show();
            }else{
                $("#task_menu").remove();
            }
            //var menuArray = $("li[class='menu_li']");
            //for(var w=0;w<menuArray.length;w++){
            //    if(permissionCodeArray.indexOf(menuArray[w].getAttribute("name"))>=0){
            //        menuArray[w].parentNode.parentNode.style.display="block";
            //        menuArray[w].style.display="block";
            //        noPermissionFlag = false;
            //    } else {
            //        //if(menuArray[w] && menuArray[w].parentNode && menuArray[w].parentNode.parentNode &&　menuArray[w].parentNode.parentNode.parentNode ){
            //        //    menuArray[w].parentNode.parentNode.parentNode.removeChild(menuArray[w].parentNode.parentNode);
            //        //}
            //        // if(menuArray[w] && menuArray[w].parentNode && menuArray[w].parentNode.parentNode &&　menuArray[w].parentNode.parentNode.parentNode ){
            //        //    menuArray[w].parentNode.parentNode.parentNode.removeChild(menuArray[w].parentNode.parentNode);
            //        //    }
            //        menuArray[w].parentNode.removeChild(menuArray[w]);
            //    }
            //}
        }
    },
    logout : function(){
        //清空cookie
        cookie.deleteCookie("login_user_name");
        cookie.deleteCookie("adm_permission_code");
        window.location.href="/admin/logout";
    },
    resetPassword: function() {
        modify_password.init();
        $(".tipsContent").text("您的密码不安全，请重新设置密码");
        $("#safe_pwd_tip_id").show();
        $("#popover_normal_input").height(400);
        $("#content_close").hide();
    },
    resetPasswordLock: function () {
        modify_password.init();
        $(".tipsContent").text("请重新设置密码，过期将锁定账户");
        $("#safe_pwd_tip_id").show();
        $("#popover_normal_input").height(400);
        $("#content_close").hide();
    }
};




