/**
 * Created by liyh on 2015/9/6.
 */
var inner_account = {
};
    /**
     * 启用操作
     */
    function onOrOff(id,status){
        if (!common.permission.validUserPermission("ad030203")) {
            return;
        }
       account.switchStatus.onOrOff(id,status);
    };

    /**
     * 编辑页面
     */
    function editPage(id){
        if (!common.permission.validUserPermission("ad030202")) {
            return;
        }
        account.newOrEditAccount.newOrEdit(id);
    };

    /**
     * 修改密码页面
     */
    function rePwdPage(id,name){
        if (!common.permission.validUserPermission("ad030205")) {
            return;
        }
        account.newPwdAccount.newPwd(id,name);
    }


$(function() {
    account.userType = 3;
    account.joinUrl = 'outer';
    account.listAccount.list();
    account.getRoles(3);
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
            if (!common.permission.validUserPermission("ad030201")) {
                return;
            }
            account.newOrEditAccount.newOrEdit();
        }
    });

});
