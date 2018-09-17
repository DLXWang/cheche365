/**
 * Created by cxy on 2017/6/8.
 */
var logFunction = {
    "configId":"",
    "data": function (data) {
        data.configId = logFunction.configId
    },
    "fnRowCallback": function (nRow, aData) {}
}
var logList = {
    "url": '/operationcenter/quoteFlowConfig/logOperateList',
    "type": "GET",
    "table_id": "log_list_tab",
    "columns": [
        {"data": "operator" , "title": "操作者", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "operateInfo" , "title": "操作内容", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "comment" , "title": "原因", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "operateTime" , "title": "操作时间", 'sClass': "text-center", "orderable": false,"sWidth":""}
    ],
}
var dataFunction = {
    "data": function (data) {
        data.insureCompanys = $('#insureComp').val();
        data.areas = $('#trigger_city').val();
        data.channels = $('#channel').val();
        data.enable = $('#status').val();
    },
    "fnRowCallback": function (nRow, aData) {
        var quote_type = new Array;
        quote_type[2] = '自有';
        quote_type[4] = '接口';
        quote_type[6] = '泛华';
        quote_type[7] = '参考';
        quote_type[8] = '模糊';
        quote_type[9] = '鳄鱼报价';
        quote_type[11] = '金斗云';
        var operationLog = "<a href='javascript:;' onclick=quoteFlowConfig.operationLogClick("
            + aData.id + ",'"
            + aData.insuranceCompany + "','"
            + aData.area + "','"
            + quote_type[aData.configValue] + "');>操作</a>";
        var operation = "<a href='javascript:;' onclick=quoteFlowConfig.quoteFlowConfigClick("+aData.id+","+aData.configValue+","+aData.enable+");>编辑</a>";
        //operation += "<a href='#' style='margin-left: 20px;'>佣金比例查看<a/> <a href='#' style='margin-left: 20px;'>活动配置查看<a/>";
        $('td:eq(6)', nRow).html(quote_type[aData.configValue]);
        $('td:eq(7)', nRow).html(operationLog);
        $('td:eq(8)', nRow).html(operation);
    },
}
var quoteFlowConfigList = {
    "url": '/operationcenter/quoteFlowConfig',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": "insuranceCompany" , "title": "保险公司", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "area", "title": "地区", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "clientType", "title": "ToA/ToC", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "channelType", "title": "自有/第三方", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "channel", "title": "渠道", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {
            "data": "enable",
            "title": "状态",
            "sClass": "text-center",
            "orderable": false,
            "render": function(data) {
                if(data){
                    return '上线';
                }
                return '下线'
            }},
        {"data": null, "title": "接入方式", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": null, "title": "操作日志", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": null, "title": "操作", "sClass": "text-center", "orderable": false},
    ],
}
var quoteFlowConfig = {
    url_content:'',
    initUrlContent:function(){
        var detailContent=$("#url_content");
        if (detailContent.length > 0) {
            create_url.url_content = detailContent.html();
            detailContent.remove();
        }
    },
    searchBtn:function(){
        window.parent.$("#searchBtn").unbind("click").bind({
            click: function() {
                datatables.ajax.reload();
            }
        });
    },

    areaInfo:function(){
        window.parent.$("#result_detail").unbind("change").bind({
            keyup:function(){
                if(common.isEmpty($(this).val())){
                    CUI.select.hide();
                    $("#result_detail").val("");
                    $("#trigger_city").val("");
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
                        CUI.select.show($("#result_detail"),300,map,false,$("#trigger_city"));
                    },function(){}
                );
            }
        });
    },

    channelSelectbox:function(){
        common.getByAjax(true, "get", "json", "/operationcenter/resource/channel/ableChannel",{},
            function(data){
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                    });
                    $("#channel").append(options);
                }
            },
            function(){
                popup.mould.popTipsMould( "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    companySelectbox:function(){
        common.getByAjax(true, "get", "json", "/operationcenter/resource/insuranceCompanys",{},
            function(data){
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                    });
                    $("#insureComp").append(options);
                }
            },
            function(){
                popup.mould.popTipsMould( "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    operationLogClick:function(id, insureComp, area, configValue){
        popup.pop.popInput(quoteFlowConfig.logContent, 'first', "1100px", "580px", "30%", "37%");
        $("#insureCompany").html("保险公司：" + insureComp);
        $("#area").html("地区：" + area);
        $("#config").html("接入方式：" + configValue);
        logFunction.configId = id;
        logDatatables = datatableUtil.getByDatatables(logList,logFunction.data,logFunction.fnRowCallback);
        $("#log_list_tab_length").hide();
        parent.$("#close").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
    },
    quoteFlowConfigClick:function(id,configValue,enable){
        popup.pop.popInput(quoteFlowConfig.quoteFlowConfig, 'first', "600px", "550px", "30%", "50%");
        parent.$("#close1,#cancel").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
        $("#toOnline").attr("checked","checked");
        $("#quote_type" + configValue).attr("checked","checked");
        if(enable){
            $("#toOnline").attr("checked","checked");
        }else{
            $("#toOffline").attr("checked","checked");
        }
        window.parent.$("#toSave").unbind("click").bind({
            click: function() {
                var booleanEnable = $('input[name="enable"]:checked').val() == 1?true:false;
                if($('input[name="quoteWay"]:checked').val()== configValue && booleanEnable == enable){
                    $("#notice").text("没有修改过");
                    $("#notice").css('color','#AE0000');
                    return;
                }
                if(common.isEmpty($("#reason").val())){
                    $("#notice").text("请输入操作简要原因");
                    $("#notice").css('color','#AE0000');
                    return;
                }
                common.getByAjax(false, "POST", "json","/operationcenter/quoteFlowConfig/editQuoteOffline",{
                        id:id,
                        quoteWay:$('input[name="quoteWay"]:checked').val(),
                        enable:booleanEnable,
                        operateTime:$('input[name="operate_time"]:checked').val(),
                        reason:$("#reason").val()
                    },
                    function(data) {
                        if (data.pass) {
                            popup.mould.popTipsMould("保存成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                            datatables.ajax.reload();
                        } else {
                            popup.mould.popTipsMould("发生异常,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                            window.parent.$("#toSave").attr("disabled",false);
                        }
                    },
                    function() {
                    }
                );
            }
        });
    },
    initOperationLog:function(){
        var detailContent = $("#operation_log");
        if (detailContent.length > 0) {
            quoteFlowConfig.logContent = detailContent.html();
            detailContent.remove();
        }
    },
    initQuoteOffline:function(){
        var detailContent = $("#quote_offline");
        if (detailContent.length > 0) {
            quoteFlowConfig.quoteFlowConfig = detailContent.html();
            detailContent.remove();
        }
    },
    changeValue:function () {
        $("#codeFileFake").val($("#codeFile").val());
    },
    uploadFile:function () {
        common.excelImport("/operationcenter/quoteFlowConfig/upload", $("#file_form"), $("#codeFile").val());
        datatables.ajax.reload();
    },
    initTemplateUrl: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/quoteFlowConfig/template/url", null, function (response) {
            $("#url_template").prop("href", response.message);
        }, function () {
            popup.mould.popTipsMould("模版地址初始化异常！！", popup.mould.first, popup.mould.error, "", "53%", null);
        });
    },
}

var datatables;
$(function(){
    if (!common.permission.validUserPermission("op0901")) {
        return;
    }
    $("#insureComp, #status, #channel").unbind("change").bind({
        change : function(){
            datatables.ajax.reload();
        }
    });
    quoteFlowConfig.areaInfo();
    datatables = datatableUtil.getByDatatables(quoteFlowConfigList,dataFunction.data,dataFunction.fnRowCallback);
    quoteFlowConfig.companySelectbox();
    quoteFlowConfig.channelSelectbox();
    quoteFlowConfig.initOperationLog();
    quoteFlowConfig.initQuoteOffline();
    quoteFlowConfig.searchBtn();
    quoteFlowConfig.initTemplateUrl();
});
