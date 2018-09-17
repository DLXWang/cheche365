/**
 * Created by sunhuazhong on 2015/6/5.
 */
$(function () {
    user.initAllRoles();

    $("#searchBtn").unbind("click").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            datatableUtil.params.keyword = keyword;
            datatables.ajax.reload();
        }
    });

    /* 更新按钮 */
    $("#update_button").bind({
        click: function () {
            if (common.isEmpty($("#email").val()) || common.isEmpty($("#name").val()) || common.isEmpty($("#mobile").val())) {
                common.showTips("请将信息填写完整");
                return false;
            }
            var pass = true;

            $("#userForm").find('.errorMsg').each(function () {
                if ($(this).is(':visible')) {
                    pass = false;
                    return false;
                }
            });

            if (!pass) {
                common.showTips("请更正错误提示处信息");
                return false;
            }
            user.update();
        }
    });

    /* 取消按钮 */
    $("#cancel_button").bind({
        click: function () {
            $("#userForm")[0].reset();
            showContent();
        }
    });

    /* 删除按钮 */
    $("#delete_button").bind({
        click: function () {
            common.showPublicTips("删除后不可撤销，确认删除？");
        }
    });

    /* 删除确认 */
    var reConfirm = window.parent.$("#theme_popover_publicConfirm");
    reConfirm.find(".confirm").unbind("click").bind({
        click: function () {
            user.delete($("#id").val());
        }
    });
    reConfirm.find(".cancel").unbind("click").bind({
        click: function () {
            common.hideMask();
        }
    });

    $("#email").bind({
        blur: function () {
            if (!common.isEmail($(this).val())) {
                $(this).parent().siblings().find(".errorMsg .control-label").text("请输入有效的邮件地址！");
                $(this).parent().siblings().find(".errorMsg").show();
            } else {
                $(this).parent().siblings().find(".errorMsg").hide();
            }
        }
    });

    $("#name").bind({
        blur: function () {
            if (common.isEmpty($(this).val())) {
                $(this).parent().siblings().find(".errorMsg .control-label").text("请输入姓名！");
                $(this).parent().siblings().find(".errorMsg").show();
            } else {
                $(this).parent().siblings().find(".errorMsg").hide();
            }
        }
    });

    $("#mobile").bind({
        blur: function () {
            if (!common.isMobile($(this).val())) {
                $(this).parent().siblings().find(".errorMsg .control-label").text("请输入正确的手机号码！");
                $(this).parent().siblings().find(".errorMsg").show();
            } else {
                $(this).parent().siblings().find(".errorMsg").hide();
            }
        }
    });

    /*
     $("#password").bind({
     blur : function(){
     if(!common.isPassword($(this).val())){
     $(this).parent().siblings().find(".errorMsg .control-label").text("密码应由6到12位字母数字下划线组成！");
     $(this).parent().siblings().find(".errorMsg").show();
     }else{
     $(this).parent().siblings().find(".errorMsg").hide();
     }
     }
     });

     $("#confirmPassword").bind({
     blur : function(){
     if(!common.isPassword($(this).val())){
     $(this).parent().siblings().find(".errorMsg .control-label").text("密码应由6到12位字母数字下划线组成！");
     $(this).parent().siblings().find(".errorMsg").show();
     }else{
     $(this).parent().siblings().find(".errorMsg").hide();
     }
     }
     });
     */
});

var datatables = datatableUtil.getByDatatables(list, dataFunction.data, dataFunction.fnRowCallback);

function showContent() {
    $("#top_div").show();
    $("#show_div").show();
    $("#edit_div").hide();
}

function showEdit() {
    $("#top_div").hide();
    $("#show_div").hide();
    $("#edit_div").show();
}
