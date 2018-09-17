/**
 * Created by sunhuazhong on 2015/7/8.
 */
$(function(){
    $("#modify_password").bind({
        click : function() {
            user.init();
        }
    });
});

var user = {
    init: function() {
        var content =
            "<div class='theme_poptit'>"+
            "<a id='content_close' href='javascript:;' title='关闭' class='close'><i class='glyphicon glyphicon-remove'></i></a>"+
            "<h3 id='update_password' class='text-center'>修改密码</h3>"+
            "</div>"+
            "<div class='new' style='height: 220px;'>"+
            "<form id='user_form' class='form-horizontal'>"+
            "<label id='safe_pwd_tip_id' class=' text-center tipsContent none'  style='height: 0px;margin-top: 20px;margin-left: 50px;color: red;'></label>" +
            "<div  style='height: 100px;width:500px;margin-top: 30px;padding-right:100px;'>"+
            "<div class='form-group' style='height: 40px;'>"+
            "<label class='col-sm-4 control-label text-right'>密码:</label>" +
            "<div class='col-sm-8 text-left input-append input-group'>" +
            "<input id='newPassword' name='newPassword' class='form-control' type='password' placeholder='' maxlength='12' style='display: inline-block;'>" +
            "<span id='show-hide-pwd' tabindex='100' title='显示/隐藏密码' class='add-on input-group-addon' style='cursor: pointer;'>" +
            "<i class='glyphicon icon-eye-open glyphicon-eye-open'></i>" +
            "</span>" +
            "</div>" +
            "</div>"+
            "<div class='form-group' style='height: 40px;'>"+
            "<label class='col-sm-4 control-label text-right'>确认密码:</label>" +
            "<div class='col-sm-8 text-left input-append input-group'>"+
            "<input id='confirmPassword' name='confirmPassword' type='password' class='form-control text-input-280' placeholder='' maxlength='12' style='display: inline-block;'>"+
            "<span id='confirm-show-hide-pwd' tabindex='100' title='显示/隐藏密码' class='add-on input-group-addon' style='cursor: pointer;'>" +
            "<i class='glyphicon icon-eye-open glyphicon-eye-open'></i>" +
            "</span>" +
            "</div>"+
            "</div>"+
            "</div>"+
            "<div class='form-group' style='margin-left:50px;height: 52px;'>"+
                "<span style='color: #bcbcbc'>" +
                    "密码规则：<br>" +
                    "1.密码长度6-12位<br>"+
                    "2.须同时包含大写字母、小写字母、数字三种，允许输入下划线<br>"+
                "</span>"+
            "</div>"+
            "<div class='form-group error-line' style='height:30px;'>"+
            "<span class='col-sm-3'></span>"+
            "<div class='col-sm-8 text-left none' style='height: 30px;'>"+
            "<p class='alert-danger text-input-280 error-msg' style='height: 30px;width: 310px;line-height: 30px;'><i class='glyphicon glyphicon-remove-sign'></i> <span id='errorsText'>错误提示</span></p>"+
            "</div>"+
            "</div>"+
            "<div class='form-group text-center' style='margin-top: 0px;'>"+
            "<input id='update_button' type='button' class='btn btn-danger text-input-100' value='确定'>"+
            "</div>"+
            "</form>"+
            "</div>";
        popup.pop.popInput(content, popup.mould.first, "500px", "360px", "50%", "55%");
        var pwd_show_flag = false;
        window.parent.$("#show-hide-pwd").unbind("click").bind({
            click: function(){
                if(!pwd_show_flag){
                    $("#newPassword").attr('type','text');
                    $("#show-hide-pwd").css("color","#ff5a35");
                    pwd_show_flag = true;
                }else{
                    $("#newPassword").attr('type','password');
                    $("#show-hide-pwd").css("color","#555");
                    pwd_show_flag = false;
                }
            }
        });
        var confirm_pwd_show_flag = false;
        window.parent.$("#confirm-show-hide-pwd").unbind("click").bind({
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

        window.parent.$("#update_button").unbind("click").bind({
            click : function(){
                var newPassword = window.parent.$("#newPassword").val();
                var confirmPassword = window.parent.$("#confirmPassword").val();
                if(newPassword == ""){
                    $("#errorsText").text("请输入密码");
                    $("#newPassword").focus();
                    $("#errorsText").parent().parent().show();
                    return false;
                }
                if(confirmPassword == ""){
                    $("#errorsText").text("请输入确认密码");
                    $("#confirmPassword").focus();
                    $("#errorsText").parent().parent().show();
                }
                if(!common.isPassword(newPassword)){
                    $("#errorsText").text("密码由6到12位大小写字母数字下划线组成！");
                    $("#newPassword").focus();
                    $("#errorsText").parent().parent().show();
                    return false;
                }
                if(!common.isPasswordEx(newPassword)){
                    $("#errorsText").text("密码必须包含大小写字母和数字！");
                    $("#newPassword").focus();
                    $("#errorsText").parent().parent().show();
                    return false;
                }
                //if(!common.isPassword(confirmPassword)){
                //    $("#errorsText").text("确认密码应由6到12位字母数字下划线组成！");
                //    $("#confirmPassword").focus();
                //    $("#errorsText").parent().parent().show();
                //    return false;
                //}
                if(newPassword != confirmPassword){
                    $("#errorsText").text("两次填写密码不一致，请重新填写");
                    $("#confirmPassword").focus();
                    $("#errorsText").parent().parent().show();
                    return false;
                }
                user.modifyPassword(newPassword);
            }
        });
        window.parent.$("#content_close").unbind("click").bind({
            click: function () {
                popup.mask.hideAllMask();
            }
        });
    },
    modifyPassword : function(newPassword) {
        var id = $("#internalUserId").val();
        common.getByAjax(true, "post", "json", "/operationcenter/user/modifyPassword", {id:id,password:newPassword},
            function(data){
                if(data.pass){
                    popup.mould.popTipsMould("修改密码成功！", popup.mould.second, popup.mould.success, "", "59%", null);
                    popup.mask.hideFirstMask();
                    $("#user_form")[0].reset();
                } else {
                    popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "59%", null);
                }
                $("#update_button").attr("disabled", false);
            },function(){
                popup.mould.popTipsMould("系统异常！", popup.mould.second, popup.mould.error, "", "59%", null);
                $("#update_button").attr("disabled", false);
            }
        );
    }
}
