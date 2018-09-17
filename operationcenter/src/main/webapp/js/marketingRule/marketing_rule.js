/**
 * Created by cxy on 2016/8/11.
 * Created datatable by cxy on 2017/1/13.
 */

var dataFunction = {
    "data": function (data) {
        data.title = $('#marketingName').val();
        data.activityType = $('#activityType').val();
        data.channelId = $('#channel').val();
        data.channel = $('#channelFather').val();
        data.area = $('#trigger_city').val();
        data.insuranceCompany = $('#insuranceCompany').val();
        data.status = marketingRule.status;
    },
    "fnRowCallback": function (nRow, aData) {

    },
}
var marketingList = {
    "url": '/operationcenter/marketingRule/marketingList',
    "type": "GET",
    "table_id": "marketing_list",
    "columns": [
        {
            "data": "id",
            "title": '<input type="checkbox" onchange="marketingRule.checkSelected();" class="data-checkbox check-box-all" id="check-box-all">',
            "render": function (data, type, row) {
                return '<input type="checkbox" value="' + data + '" onchange="marketingRule.checkSelected();" class="data-checkbox check-box-single">';
            },
            "className": "text-center checkbox-width",
            "orderable": false
        },
        {"data": "id", "title": "ID", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "effectiveDate", "title": "生效日期", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "activityTypeInfo", "title": "优惠类别", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "channel", "title": "支持的平台", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "title", "title": "主标题", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "subTitle", "title": "副标题", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "insuranceCompany", "title": "支持的保险公司", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "area", "title": "支持的城市", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {"data": "statusDesc", "title": "状态", 'sClass': "text-center", "orderable": false,"sWidth":""},
        {
            "data": "id",
            "title": "编辑",
            render: function (data, type, row) {
                return"<a href='/views/marketing_rule/marketing_add.jsp?id="+ data +"&clickType=overview' target='_blank'>查看</a>" +
                    "             <a href='/views/marketing_rule/marketing_add.jsp?id="+ data +"&clickType=edit' target='_blank'>编辑</a>" +
                    "             <a href='javascript:;' onclick=marketingRule.history('" + data + "');>历史信息</a>";
            },
            "className": "text-center",
            "orderable": false
        },
    ],
}
var marketingRule = {
    properties : new Properties(1, ""),
    acticityTypeId:[4,5,6,7],
    channel:[3,4,5,6,8,9],
    status:2,

    checkSelected: function () {
        if ($(".check-box-single:checked").length > 0) {
            $('.inputradio').attr("checked", false);
            $('.inputradio').eq(0).prop("checked", true);
        } else {
            $(".check-box-all").attr("checked", false);
        }
    },
    chgList:function(){
        /* 搜索 */
        $("#searchBtn").unbind("click").bind({
            click : function(){
                datatables.ajax.reload();
            }
        });
        $("#activityType, #channel, #insuranceCompany").unbind("change").bind({
            change : function(){
                datatables.ajax.reload();
            }
        });;
        $("#status").unbind("change").bind({
            change : function(){
                marketingRule.status = $("#status").val();
                datatables.ajax.reload();
            }
        });
    },
    /* 活动类型列表查询 */
    acticityTypeList : function(){
        common.getByAjax(true, "get", "json", "/operationcenter/marketingRule/activityTypeList",{},
            function(data){
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        for(j=0;j<marketingRule.acticityTypeId.length;j++){
                            if(marketingRule.acticityTypeId[j] == model.id){
                                options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                            }
                        }
                    });
                    $("#activityType").append(options);
                }
            },
            function(){
                popup.mould.popTipsMould( "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    /*活动类型 */
    statusList : function(){
        common.getByAjax(false, "get", "json", "/operationcenter/marketingRule/statusList",{},
            function(data){
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                    });
                    $("#status").append(options);
                    $("#status").val(2);
                }
            },
            function(){
                popup.mould.popTipsMould( "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    initHistoryList: function(id) {
        common.getByAjax(true, "get", "json", "/operationcenter/marketingRule/historyList",
            {
                id : id
            },
            function(data) {
                parent.$("#tabHistory tbody").empty();
                if (data) {
                    var content = "";
                    $.each(data, function(i, model){
                        content += "<tr>" +
                            "<td class='text-center'>" + common.checkToEmpty(model.id) + "</td>" +
                            "<td class='text-center'>" + common.checkToEmpty(model.effectiveDate) + "</td>" +
                            "<td class='text-center'>" + common.checkToEmpty(model.activityTypeInfo) + "</td>" +
                            "<td class='text-center'>" + common.checkToEmpty(model.channel) + "</td>" +
                            "<td class='text-center'>" + common.checkToEmpty(model.title) + "</td>" +
                            "<td class='text-center'>" + common.checkToEmpty(model.subTitle) + "</td>" +
                            "<td>" + common.getFormatComment(model.description,20) + "</td>" +
                            "<td class='text-center'>" + common.checkToEmpty(model.insuranceCompany) + "</td>" +
                            "<td class='text-center'>" + common.checkToEmpty(model.area) + "</td>" +
                            "<td class='text-center'>" + common.checkToEmpty(model.statusDesc) +"</td>" +
                            "<td>  <a href='/views/marketing_rule/marketing_add.jsp?id="+ model.id +"&clickType=overview' target='_blank'>查看</a></td>" +
                            "</tr>";
                    });
                    parent.$("#tabHistory tbody").append(content);
                }
            },function() {}
        );
    },
    history:function(id){
        popup.pop.popInput(marketingRule.historyContent, 'first', "1300px", "550px", "30%", "30%");
        marketingRule.initHistoryList(id);
        parent.$("#close").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
    },
    initHistory:function(){
        var detailContent=$("#history_content");
        if (detailContent.length > 0) {
            marketingRule.historyContent = detailContent.html();
            detailContent.remove();
        }
    },
    channelChild:function(){

        $("#channelFather").unbind("change").bind({
            change:function(){

                $("#channel").empty();
                if($("#channelFather").val() == "0"){
                    marketingRule.getChannelByUrl("/operationcenter/resource/channel/all");
                }else if($("#channelFather").val() == "official"){
                    marketingRule.getChannelByUrl("/operationcenter/resource/channel/official");
                }else if($("#channelFather").val() == "thirdParty"){;
                    marketingRule.getChannelByUrl("/operationcenter/resource/channel/thirdParty");
                }else if($("#channelFather").val() == "all"){
                    marketingRule.getChannelByUrl("/operationcenter/resource/channel/all");
                }
                datatables.ajax.reload();
            }
        });
    },

    getChannelByUrl:function(url){
        common.getByAjax(true, "get", "json", url,{},
            function(data){
                if (data) {
                    var options = "";
                    options+= "<option value=''>请选择活动平台子分类</option>";
                    $.each(data, function(i, model){
                        options += " <option value='"+model.id+"'>" + model.description + "</option>";
                    });
                    //$("#" + name + "Div").html(options);
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
                    $("#insuranceCompany").append(options);
                }
            },
            function(){
                popup.mould.popTipsMould( "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
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
    refreshCheckbox:function(){
        window.parent.$("#refreshSelect").unbind("change").bind({
            change:function(){
                if(this.checked){
                    $(".refresh").prop("checked",true);
                }else{
                    $(".refresh").prop("checked",false);
                }
            }
        });
    },
    refreshBtn:function(){
        window.parent.$("#refreshBtn").unbind("click").bind({
            click: function() {
                if(dt_labels.selected.length == 0){
                    popup.mould.popTipsMould("请选择活动", popup.mould.first, popup.mould.error, "", "53%", null);
                    return;
                }
                common.getByAjax(true, "post", "json","/operationcenter/marketingRule/refreshRules",{
                        refresh: dt_labels.selected.join(",")},
                    function(data) {
                        if (data.pass) {
                            popup.mould.popTipsMould("刷新成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                        } else {
                            popup.mould.popTipsMould("发生异常,data返回异常,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                            window.parent.$("#toSave").attr("disabled",false);
                        }
                    },
                    function() {
                        popup.mould.popTipsMould("发生异常,获取不到data,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                        window.parent.$("#toSave").attr("disabled",false);
                    }
                );
            }
        });
    },
    checkCheckBox:function(){
        var result = false;
        $(".refresh").each(function(){		//type=checkbox实行便利循环
            if(this.checked == true){
                result = true;
            }				//删除type=checkbox里面添加checked="checked" 取消选中的意思
        });
        return result;
    },
}

var datatables = datatableUtil.getByDatatables(marketingList,dataFunction.data,dataFunction.fnRowCallback);
$(function(){
    if (!common.permission.validUserPermission("op0501")) {
        return;
    }
    marketingRule.statusList();
    marketingRule.chgList();
    marketingRule.channelChild();
    marketingRule.initHistory();
    marketingRule.companySelectbox();
    marketingRule.acticityTypeList();
    marketingRule.areaInfo();
    marketingRule.getChannelByUrl("/operationcenter/resource/channel/all");
    marketingRule.refreshCheckbox();
    marketingRule.refreshBtn();
});
