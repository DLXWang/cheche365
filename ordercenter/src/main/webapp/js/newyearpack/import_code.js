var include_code={
    form:{
        changeValue: function () {
            $("#codeFileFake").val($("#codeFile").val());
        },
        validate:function(){
            if(common.isEmpty($("#name").val())){
                this.error("请输入兑换码名称");
                return false;
            }
            if($("#codeGroup").val()==0){
                this.error("请选择兑换码类型");
                return false;
            }
            if(!common.isEmpty($("#effectiveDate").val())&&!common.isEmpty($("#expireDate").val())){
                if(!common.tools.dateCompare($("#effectiveDate").val(),$("#expireDate").val())){
                    this.error("有效开始日期不能晚于结束日期");
                    return false;
                }
            }
            if(common.isEmpty($("#codeFile").val())){
                this.error("请选择需上传的兑换码文件");
                return false;
            }
            return true;
        },
        error:function(msg){
            $("#errorText").html(msg);
            $(".error-msg").show().delay(2000).hide(0);
        },
        submit:function(callback){
            var form = $("#includeCodeForm");
            var options = {
                url : "/orderCenter/newyearpack/upload",
                type : "post",
                dataType: "json",
                success : function(result) {
                    if(result == "error") {
                        popup.mould.popTipsMould(true, "上传失败", "first", "error", "", "56%",
                            function() {
                                popup.mask.hideFirstMask(true);
                            }
                        );
                        return false;
                    } else {
                        if(callback){
                            callback(result);
                        }
                    }
                },
                error : function() {
                    common.showTips("系统异常");
                    common.hideMask();
                }
            };
            form.ajaxSubmit(options);
        },
        init:{
                initCodeType:function(){
                    common.ajax.getByAjax(true,"get","json","/orderCenter/newyearpack/upload/type",null,
                        function(data){
                            if(data == null){
                                return false;
                            }

                            var options = "";
                            $.each(data, function(i,model){
                                options += "<option value='"+ model.id +"'>" + model.description + "</option>";
                            });

                            $("#codeType").append(options);
                        },function(){}
                    );
                },
                initCodeGroup:function(){
                    common.ajax.getByAjax(true,"get","json","/orderCenter/newyearpack/upload/group",null,
                        function(data){
                            if(data == null){
                                return false;
                            }
                            var options = "";
                            $.each(data, function(i,model){
                                options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                            });
                            $("#codeGroup").append(options);
                        },function(){}
                    );
                }
            }
    },
    grid:{
        load:function(data){
            $("#include_button").show();
            CUI.grid.store=data;
            CUI.grid.dom=$("#code_table tbody");
            CUI.grid.addIndex=true;
            CUI.grid.columns=[
                {dataIndex:'name'},
                {dataIndex:'code',renderer:function(value,rowIndex,rowStore){
                    if(!common.validations.isEmpty(rowStore.id)){
                        $("#tip").find("strong").html("<span style='color: red;'>导入了重复的兑换码，请核对标红的兑换码后重新导入</span>");
                        $("#include_button").hide();
                        return "<font style='color:red;'>"+value+"</font>";
                    }
                }},
                {dataIndex:'codeGroup.name'},
                {dataIndex:'codeType.description'},
                {dataIndex:'effectiveDate',renderer:function(value){
                    if(common.isEmpty(value)){
                        return "";
                    }
                    return common.formatDate(new Date(value),"yyyy-MM-dd");
                }},
                {dataIndex:'expireDate',renderer:function(value){
                    if(common.isEmpty(value)){
                        return "";
                    }
                    return common.formatDate(new Date(value),"yyyy-MM-dd");
                }},
            ];
            CUI.grid.result={callback:function(result){
                if(result==CUI.grid.results.success){
                    $("#form").animate({height:"0px"}).hide();
                    $("#result").show();
                    $("#tip").find("strong").html("本次将导入兑换码总共：<span style='color: red;'>"+data.viewList.length+"</span>个，请仔细核对后导入");
                }
                if(result==CUI.grid.results.notFound){

                }
            }};
            CUI.grid.fill();
        }
    },
    include:function(){
        common.ajax.getByAjax(true,"post","text","/orderCenter/newyearpack/upload/save",null,
            function(data){
                if(data == "success"){
                    popup.mould.popTipsMould(false, "兑换码导入成功！", popup.mould.first, popup.mould.success, "", "57%", null);
                    $("#form").show();
                    $("#result").hide();
                }else if(data =="failure"){
                    popup.mould.popTipsMould(false, "导入失败，请勿重复导入！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            },function(){
                common.showTips("系统异常");
            }
        );
    }
}

$(function(){
    include_code.form.init.initCodeGroup();
    include_code.form.init.initCodeType();
    $("#save_button").unbind("click").bind({
            click:function(){
                if(include_code.form.validate()){
                    include_code.form.submit(function(data){
                        include_code.grid.load(data);
                    });
                }
            }
        }
    ),
    $("#include_button").unbind("click").bind({
            click:function(){
                include_code.include();
            }
        }
    )
    $("#repeat_button").unbind("click").bind({
            click:function(){
                $("#form").show();
                $("#result").hide();
            }
        }
    )

})
