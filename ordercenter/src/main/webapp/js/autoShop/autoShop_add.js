/**
 * Created by wangfei on 2015/6/2.
 */
$(function(){
    $(".switch").each(function(){
        $(this).bootstrapSwitch();
        $(this).on('switchChange.bootstrapSwitch', function (e, state) {
            $(this).val(state);
        });
    });

    //保险公司列表
    operation.initCompanies();

    //车型品牌列表
    operation.initBrands();

    /* 增加新服务项 */
    $("#addNewServiceItem").bind({
        click : function(){
            operation.showServiceItemTips();
        }
    });

    /* 保存 */
    $("#save_button").bind({
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
            autoShop.save();
        }
    });

    //清空
    $("#clear_button").bind({
        click : function(){
            autoShop.clearForm($("#inputForm"));
        }
    });

    //车型列表
    $("#brandSel").bind({
        change : function(){
            operation.addBrand($(this));
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

});

