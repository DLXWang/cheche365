/**
 * Created by sunhuazhong on 2015/6/5.
 */
$(function(){
    /* 初始化 */
    var properties = new Properties(1, "");
    user.listGroup(properties);

    /* 搜索 */
    $("#searchBtn").bind({
        click : function(){
            if($.trim($("#keyword").val()) == ""){
                common.showTips("请输入搜索内容");
                return false;
            }
            properties.currentPage = 1;
            properties.keyword = $("#keyword").val();
            user.listGroup(properties);
        }
    });

    $("#update_button").bind({
        click : function() {
            if($("#customerSel").val() == ""){
                common.showTips("请选择客服");
                return false;
            }

            if($("#internalSel").val() == ""){
                common.showTips("请选择内勤");
                return false;
            }

            if($("#externalSel").val() == ""){
                common.showTips("请选择外勤");
                return false;
            }

            user.updateGroup(properties);
        }
    });

    /* 取消按钮 */
    $("#cancel_button").bind({
        click : function(){
            $("#relationForm")[0].reset();
            showContent();
        }
    });

    /* 删除按钮 */
    $("#delete_button").bind({
        click : function(){
            common.showPublicTips("删除后不可撤销，确认删除？");
        }
    });

    /* 删除确认 */
    var reConfirm = window.parent.$("#theme_popover_publicConfirm");
    reConfirm.find(".confirm").unbind("click").bind({
        click : function(){
            user.deleteGroup($("#id").val(), properties);
        }
    });
    reConfirm.find(".cancel").unbind("click").bind({
        click : function(){
            common.hideMask();
        }
    });
});

function showContent(){
    $("#top_div").show();
    $("#show_div").show();
    $("#edit_div").hide();
}

function showEdit(){
    $("#top_div").hide();
    $("#show_div").hide();
    $("#edit_div").show();
}
