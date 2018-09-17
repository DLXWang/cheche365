/**
 * Created by cxy on 2017/6/8.
 */
var quoteFlowConfigAdd = {
    dataform:{
        clientType:'',
        channelType:''
    },
    checkSelected: function () {
        if ($(".check-box-all:checked").length > 0) {
            $(".check-box-single").each(function(){
                this.checked = true;
            });
        }else {
            $(".check-box-single").each(function(){
                this.checked = false;
            });
        }
    },
    channelSelectbox:function(){
        quoteFlowConfigAdd.dataform.clientType = $("input[name=clientType]:checked").val();
        quoteFlowConfigAdd.dataform.channelType = $("input[name=channelType]:checked").val();
        common.getByAjax(false, "get", "json", "/operationcenter/channelRebate/channelsNoOrdercenter",quoteFlowConfigAdd.dataform,
            function(data){
                if (data) {
                    var options = "";
                    var marginleft = " style='margin-left:10px;'";
                    $.each(data, function(i, model){
                        options += "<label class='checkbox-inline width-180' " +  marginleft + " ><input type='checkbox' name='channels' class='check-box-single'   autocomplete='off'  value='" + model.id + "'  id='" + name + model.id + "'/>" + model.description + "</label>";
                        marginleft = "";
                    });
                    $("#channelDiv").html(options);
                }
            },
            function(){
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },
    companyCheckbox:function(){
        common.getByAjax(true, "get", "json", "/operationcenter/resource/insuranceCompanys",{},
            function(data){
                if (data) {
                    var options = "";
                    var marginleft = "style='margin-left:10px;'";
                    $.each(data, function(i, model){
                        options += "<label class='checkbox-inline width-180 ' " +  marginleft + "><input type='checkbox' name='insureCompanys' value='" + model.id + "'/>" + model.name + "</label>";
                        marginleft = "";
                    });
                    $("#insuranceCompanyDiv").append(options);
                }
            },
            function(){
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },

    checkCheckBox:function(checkBoxName){
        var result = false;
        $("input[name='" + checkBoxName + "']").each(function(){		//type=checkbox实行便利循环
            if(this.checked == true){
                result = true;
            }				//删除type=checkbox里面添加checked="checked" 取消选中的意思
        });
        return result;
    },
    validateCompanyAreaChannel:function(){
        if (!quoteFlowConfigAdd.checkCheckBox('insureCompanys')) {
            popup.mould.popTipsMould( "请选择保险公司", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if (!quoteFlowConfigAdd.checkCheckBox('channels')) {
            popup.mould.popTipsMould( "请选择活动平台", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if ($("[class='tagator_tag']").length <= 0) {
            popup.mould.popTipsMould( "没有城市", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        } else {
            return true;
        }
    },
}

$(function(){
    quoteFlowConfigAdd.companyCheckbox();
    quoteFlowConfigAdd.channelSelectbox();

    window.parent.$("#toA,#toC,#self,#partner").unbind("click").bind({
        click: function() {
            quoteFlowConfigAdd.channelSelectbox();
        }
    });

    window.parent.$("#result_detail").unbind("keyup").bind({
        keyup: function() {
            if(common.isEmpty($(this).val())){
                CUI.select.hide();
                $("#result_detail").val("");
                return;
            }
            common.getByAjax(true,"get","json","/operationcenter/resource/areas/getByKeyWord",
                {
                    keyword:$(this).val()
                },
                function(data){
                    if(data == null){
                        return;
                    }
                    var map=new Map();
                    $.each(data, function(i,model){
                        map.put(model.id,model.name);
                    });
                    CUI.select.showTag(window.parent.$("#result_detail"),300,map,false,window.parent.$("#trigger_city"));
                }
            );
        }
    });

    window.parent.$("#toSave").unbind("click").bind({
        click: function() {
            if(!quoteFlowConfigAdd.validateCompanyAreaChannel()){
                return;
            }
            $("#toSave").attr("disabled",true);
            common.getByAjax(false, "POST", "json","/operationcenter/quoteFlowConfig/add",window.parent.$("#add_form").serialize(),
                function(data) {
                    if (data.pass) {
                        popup.mould.popTipsMould("保存成功！", popup.mould.first, popup.mould.success, "", "53%", function(){
                            window.close();
                        });
                        window.parent.$("#toSave").attr("disabled",false);
                    } else {
                        popup.mould.popTipsMould("发生异常,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                        $("#toSave").attr("disabled",false);
                    }
                },
                function() {
                }
            );
        }
    });
});
