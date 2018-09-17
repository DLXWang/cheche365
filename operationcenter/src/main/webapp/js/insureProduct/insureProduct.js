var include_code={
    form:{
        validate:function(){
            if(common.isEmpty($("#codeFile").val())){
                this.error("请选择需导入的Excel文件");
                return false;
            }
            return true;
        },
        error:function(msg){
            $("#errorText").html(msg);
            $(".error-msg").show().delay(2000).hide(0);
        },
        submit:function(){
            $("#save_button").attr("disabled", true);
            var form = $("#includeCodeForm");
            var options = {
                url : "/operationcenter/insureProduct",
                type : "post",
                dataType: "json",
                async:false,
                success : function(result) {
                    $("#save_button").attr("disabled", false);
                    if(result.pass) {
                        popup.mould.popTipsMould("上传成功!导入数量"+result.message,  popup.mould.first, popup.mould.success, "", "56%",
                            function() {
                                popup.mask.hideFirstMask(true);
                            }
                        );
                    }else{
                        popup.mould.popTipsMould("导入excel数据失败", popup.mould.first, popup.mould.error, "", "57%", null);
                    }
                },
                error : function() {
                    $("#save_button").attr("disabled", false);
                    popup.mould.popTipsMould("导入excel数据失败", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            };
            form.ajaxSubmit(options);
        },

    },

}

$(function(){
    $("#save_button").unbind("click").bind({
            click:function(){
                if(include_code.form.validate()){
                    include_code.form.submit();
                }
            }
        }
    );
    $("#download_button").unbind("click").bind({
            click:function(){
                var url = "/operationcenter/insureProduct/outputExcels";
                $("#download_button").attr("href", url);
            }
        }
    );
})
