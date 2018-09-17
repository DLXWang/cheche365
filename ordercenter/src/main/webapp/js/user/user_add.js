/**
 * Created by wangfei on 2015/5/26.
 */
$(function(){
    //初始化角色
    user.initAllRoles();

    //清空
    $("#clear_button").bind({
        click : function(){
            user.clearForm($("#userForm"));
        }
    });

    //保存
    $("#register_button").bind({
        click : function(){
            if(common.isEmpty($("#email").val()) || common.isEmpty($("#name").val()) || common.isEmpty($("#mobile").val())
                || common.isEmpty($("#password").val()) || common.isEmpty($("#confirmPassword").val())){
                common.showTips("请将信息填写完整");
                return false;
            }
            if($("#password").val() != $("#confirmPassword").val()){
                common.showTips("两次填写密码不一致");
                return false;
            }
            var pass = true;

            $("#userForm").find('.errorMsg').each(function(){
                if($(this).is(':visible')){
                    pass = false;
                    return false;
                }
            });

            if(!pass){
                common.showTips("请更正错误提示处信息");
                return false;
            }

            user.save();
        }
    });

    $("#email").bind({
        blur : function(){
            if(!common.isEmail($(this).val())){
                $(this).parent().siblings().find(".errorMsg .control-label").text("请输入有效的邮件地址！");
                $(this).parent().siblings().find(".errorMsg").show();
            }else{
                $(this).parent().siblings().find(".errorMsg").hide();
            }
        }
    });

    $("#name").bind({
        blur : function(){
            if(common.isEmpty($(this).val())){
                $(this).parent().siblings().find(".errorMsg .control-label").text("请输入姓名！");
                $(this).parent().siblings().find(".errorMsg").show();
            }else{
                $(this).parent().siblings().find(".errorMsg").hide();
            }
        }
    });

    $("#mobile").bind({
        blur : function(){
            if(!common.isMobile($(this).val())){
                $(this).parent().siblings().find(".errorMsg .control-label").text("请输入正确的手机号码！");
                $(this).parent().siblings().find(".errorMsg").show();
            }else{
                $(this).parent().siblings().find(".errorMsg").hide();
            }
        }
    });

    $("#password").bind({
        blur : function(){
            if(!common.isPassword($(this).val())){
                $(this).parent().parent().siblings().find(".errorMsg .control-label").text("密码应由6到12位字母数字下划线组成！");
                $(this).parent().parent().siblings().find(".errorMsg").show();
            }else if(!common.isPasswordEx($(this).val())){
                $(this).parent().parent().siblings().find(".errorMsg .control-label").text("密码必须包含大小写字母和数字！");
                $(this).parent().parent().siblings().find(".errorMsg").show();
            }else{
                $(this).parent().parent().siblings().find(".errorMsg").hide();
            }
        }
    });

    $("#confirmPassword").bind({
        blur : function(){
            if(!common.isPassword($(this).val())){
                $(this).parent().parent().siblings().find(".errorMsg .control-label").text("密码应由6到12位字母数字下划线组成！");
                $(this).parent().parent().siblings().find(".errorMsg").show();
            }else{
                $(this).parent().parent().siblings().find(".errorMsg").hide();
            }
        }
    });
    var pwd_show_flag = false;
    $("#show-hide-pwd").unbind("click").bind({
        click: function(){
            if(!pwd_show_flag){
                $("#password").attr('type','text');
                $("#show-hide-pwd").css("color","#ff5a35");
                pwd_show_flag = true;
            }else{
                $("#password").attr('type','password');
                $("#show-hide-pwd").css("color","#555");
                pwd_show_flag = false;
            }
        }
    });
    var confirm_pwd_show_flag = false;
    $("#confirm-show-hide-pwd").unbind("click").bind({
        click: function(){
            if(!confirm_pwd_show_flag){
                $("#confirmPassword").attr('type','text');
                $("#confirm-show-hide-pwd").css("color","#ff5a35");
                confirm_pwd_show_flag = true;
            }else{
                $("#confirmPassword").attr('type','password');
                $("#confirm-show-hide-pwd").css("color","#555");
                confirm_pwd_show_flag = false;
            }
        }
    });

});
