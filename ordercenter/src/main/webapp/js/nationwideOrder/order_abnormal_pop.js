var abnormal_pop = {
    popup: function (id,callBackMethod) {
        $.post("order_abnormal_pop.html", {}, function (detailContent) {
            abnormal_pop.initReason();
            popup.pop.popInput(false, detailContent, 'first', "500px", "150px", "50%", "57%");
            parent.$("#abnormal_pop_close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            parent.$(".btn_exception").unbind("click").bind({
                click: function () {
                    abnormal_pop.submit(id,function(data){
                        if (callBackMethod) {
                            callBackMethod(data);
                        }
                    });
                }
            });
            parent.$("#exceptionReason").bind({
                change: function () {
                    if ($(this).val() == 0) {
                        parent.$(".btn_exception").hide();
                    } else {
                        parent.$(".btn_exception").show();
                    }
                }
        	});
        })
    },
    initReason: function(){
        common.ajax.getByAjax(true,"get","json","/orderCenter/nationwide/abnormity/reasons",null,
            function(data){
                if(data == null){
                    return false;
                }
                parent.$("#exceptionReason").empty();
                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.index +"'>" + model.content + "</option>";
                });
                parent.$("#exceptionReason").append(options);

            },function(){}
        );
    },
    submit:function(id,callBackMethod){
        common.ajax.getByAjax(true,"put","json","/orderCenter/orderCooperationInfos/"+id+"/status",{
                newStatus:7,
                reasonId:parent.$("#exceptionReason").val()
            },
            function(data){
                if (callBackMethod) {
                    callBackMethod(data);
                }
            },function(){
                popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    }

}
