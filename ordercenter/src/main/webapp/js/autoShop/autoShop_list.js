/**
 * Created by wangfei on 2015/6/9.
 */
$(function(){
    var properties = new Properties(1, "");
    /* 初始化查询 */
    autoShop.list(properties);

    //保险公司列表
    operation.initCompanies();

    //保险公司列表
    operation.initBrands();

    //车型列表
    $("#brandSel").bind({
        change : function(){
            operation.addBrand($(this));
        }
    });

    /* 上一页 */
    $("#page_up_down").find("#pageUp").bind({
        click : function(){
            properties.currentPage --;
            autoShop.list(properties);
        }
    });

    /* 下一页 */
    $("#page_up_down").find("#pageDown").bind({
        click : function(){
            properties.currentPage ++;
            autoShop.list(properties);
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
            autoShop.list(properties);
        }
    });

    /* 增加新服务项 */
    $("#addNewServiceItem").bind({
        click : function(){
            operation.showServiceItemTips();
        }
    });

    /* 添加图片 */
    $("#uploadBtn").bind({
        click : function(){
            operation.showUploadTips();

            window.parent.$("#uploadify").uploadify({
                auto            : false,
                multi           : true,
                queueID         : "fileQueue",
                uploader        : "/orderCenter/autoShop/uploadImages",
                fileObjName     : "image",
                uploadLimit     : 5,
                queueSizeLimit  : 5,
                simUploadLimit  : 5,
                removeCompleted : false,
                swf             : "../../uploadify/uploadify.swf",
                buttonText      : "添加图片",
                fileDesc        : "请选择图片文件",
                fileTypeExts    : "*.gif; *.jpg; *.png",
                fileSizeLimit   : "5MB",
                overrideEvents  : [ "onDialogClose", "onUploadStart", 'onUploadSuccess', 'onSelectError' ],
                onFallback      : operation.onFallback,
                onSelectError   : operation.onSelectError,
                onUploadSuccess : operation.onUploadSuccess,
                onUploadStart   : operation.onUploadStart
            });
        }
    });

    /* 更新按钮 */
    $("#update_button").bind({
        click : function(){
            if (common.isEmpty($("#name").val())) {
                common.showTips("请填写4S店名");
                return false;
            }
            if (common.isEmpty($("#longitude").val())) {
                common.showTips("请填写经度");
                return false;
            }
            if (common.isEmpty($("#latitude").val())) {
                common.showTips("请填写纬度");
                return false;
            }
            if ((common.isEmpty($("#contactPersonPhone_zone").val()) && !common.isEmpty($("#contactPersonPhone_local").val()))
                || (!common.isEmpty($("#contactPersonPhone_zone").val()) && common.isEmpty($("#contactPersonPhone_local").val()))) {
                common.showTips("请填写完整固定电话");
                return false;
            }
            autoShop.update(properties);
        }
    });

    /* 取消按钮 */
    $("#cancel_button").bind({
        click : function(){
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
            autoShop.delete($("#autoShopId").val(), properties);
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
    $("#count_div").show();
    $("#show_div").show();
    $("#edit_div").hide();
}

function showEdit(){
    $("#top_div").hide();
    $("#count_div").hide();
    $("#show_div").hide();
    $("#edit_div").show();
}
