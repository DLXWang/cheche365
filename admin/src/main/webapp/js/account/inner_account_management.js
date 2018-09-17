/**
 * Created by liyh on 2015/9/6.
 */
var inner_account = {


};
    /**
     * 启用操作
     */
    function onOrOff(id,status){
        if (!common.permission.validUserPermission("ad030103")) {
            return;
        }
       account.switchStatus.onOrOff(id,status);
    };

    /**
     * 编辑页面
     */
    function editPage(id){
        if (!common.permission.validUserPermission("ad030102")) {
            return;
        }
        account.newOrEditAccount.newOrEdit(id);
    };

/**
 * 设置权限操作
 */
function editPermission(id,name){
    if (!common.permission.validUserPermission("ad030102")) {
        return;
    }
    // var name = $("#name" + id).val();
    account.newOrEditPermission.newOrEdit(id,name);

};


/**
     * 修改密码页面
     */
    function rePwdPage(id,name){
        if (!common.permission.validUserPermission("ad030105")) {
            return;
        }
        account.newPwdAccount.newPwd(id,name);
    }


$(function() {
    account.userType = 1;
    account.joinUrl = 'inner';
    account.listAccount.list();
    account.getRoles(1);
    $("#searchBtn").bind({
        click: function () {
              var keyword = $("#keyword").val();
            if (!keyword) {
                popup.mould.popTipsMould("请输入搜索内容", "first", "warning", "", "", null);
                return false;
            }
            account.listAccount.properties.keyword = keyword;
            account.listAccount.properties.currentPage = 1;
            account.listAccount.list();
        }
    });


    /**
     * 新建账号
     */
    $("#toNew").bind({
        click: function() {
            if (!common.permission.validUserPermission("ad030101")) {
                return;
            }
            account.newOrEditAccount.newOrEdit();
        }
    });

});
