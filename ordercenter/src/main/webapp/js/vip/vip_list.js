/**
 * Created by sunhuazhong on 2015/6/5.
 */
$(function(){
    /* 初始化 */
    var properties = new Properties(1, "");
    vip.list(properties);

    /* 上一页 */
    $("#page_up_down").find("#pageUp").bind({
        click : function(){
            properties.currentPage --;
            vip.list(properties);
        }
    });

    /* 下一页 */
    $("#page_up_down").find("#pageDown").bind({
        click : function(){
            properties.currentPage ++;
            vip.list(properties);
        }
    });

    /* 搜索 */
    $("#searchBtn").bind({
        click : function(){
            if($.trim($("#keyword").val()) == ""){
                common.showTips("请输入搜索内容");
                return false;
            }
            properties.currentPage = 1;
            properties.keyword = $("#keyword").val();
            vip.list(properties);
        }
    });

    /* 更新按钮 */
    $("#update_button").bind({
        click : function(){
            if($.trim($("#name").val()) == ""){
                common.showTips("请填写名称");
                return false;
            }
            if($.trim($("#code").val()) == ""){
                common.showTips("请填写编号");
                return false;
            }
            if($.trim($("#startDate").val()) == ""){
                common.showTips("请填写起始日期");
                return false;
            }
            if($.trim($("#endDate").val()) == ""){
                common.showTips("请填写到期日期");
                return false;
            }
            vip.update(properties);
        }
    });

    /* 取消按钮 */
    $("#cancel_button").bind({
        click : function(){
            $("#vipInputForm")[0].reset();
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
            vip.delete($("#id").val(), properties);
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
