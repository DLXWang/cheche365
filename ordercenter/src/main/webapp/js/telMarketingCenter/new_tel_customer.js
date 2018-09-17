var input_tel_data={
    form:{
        validate:function(){
            if(common.isEmpty($("#mobile").val())){
                input_tel_data.error("请填写手机号码");
                return false;
            }
            if(common.isEmpty($("#channel").val())){
                input_tel_data.error("请选择渠道");
                return false;
            }
            return input_tel_data.form.validateMobile();
        },
        validateMobile:function(){
            var mobiles=$("#mobile").val().split(",");
            var validPhone=true;
            $.each(mobiles, function(i,mobile){
                if(!common.isMobile(mobile)){
                    input_tel_data.error("手机号:"+mobile+" 格式不正确");
                    validPhone=false;
                }
            });
            if(!validPhone){
                return false;
            }
            return true;
        },
        init:{
            initOperators:function(){
                common.ajax.getByAjax(true,"get","json","/orderCenter/resource/internalUser/getAllEnableTelCommissioner",null,
                    function(data){
                        if(data == null){
                            return false;
                        }
                        var options = "";
                        $.each(data, function(i,model){
                            options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                        });

                        $("#operator").append(options);
                    },function(){}
                );
            },
            initChannels:function(){
                common.ajax.getByAjax(true,"get","json","/orderCenter/resource/dataSourceChannelEnable",null,
                    function(data){
                        if(data == null){
                            return false;
                        }
                        var options = "<option value='3'>站内</option>";
                        $.each(data, function(i,model){
                            options += "<option value='"+ model.id +"'>" + model.description + "</option>";
                        });
                        $("#channel").append(options);
                    },function(){}
                );
            }
        }
    },
    save:function(){
        common.ajax.getByAjax(true,"post","json","/orderCenter/telMarketingCenter/inputData/save",
            {
                operatorId:$("#operator").val(),
                channelId:$("#channel").val(),
                mobile:$("#mobile").val(),
                comment:$("#comment").val()
            },
            function(data){
                if(data.pass){
                    popup.mould.popTipsMould(false, "保存成功！", popup.mould.first, popup.mould.success, "", "57%", null);
                    $("#inputDataForm").resetForm();
                }else{
                    popup.mould.popTipsMould(false, "保存失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            },function(){
                common.showTips("系统异常");
            }
        );
    },
    error:function(msg){
        $("#errorText").html(msg);
        $(".error-msg").show().delay(2000).hide(0);
    },
}

$(function() {
    input_tel_data.form.init.initOperators();
    input_tel_data.form.init.initChannels();
    $("#save_button").unbind("click").bind({
            click: function () {
                if (input_tel_data.form.validate()) {
                    input_tel_data.save();
                }
            }
        }
    );

    $("#mobile").blur(function () {
            var mobiles = $(this).val();
            if (common.isEmail(mobiles) || !input_tel_data.form.validateMobile()) {
                return;
            }
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/inputData/check",
                {
                    mobile: $("#mobile").val(),
                },
                function (data) {
                    if (data.pass && !common.isEmpty(data.message)) {
                        $("#oldOperators").html(data.message);
                    } else {
                        $("#oldOperators").html("无");
                    }
                }, function () {
                    common.showTips("系统异常");
                }
            );
        });
})

$(document).ready(function() {
    $('#channel').select2();
    $('#operator').select2();
});
