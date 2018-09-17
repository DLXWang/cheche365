/**
 * Created by wangshaobin on 2017/5/5.
 */
var dataFunction = {
    "data": function (data) {
        data.sourceChannel = ($("#sourceChannelSel").val());
        data.keyword = $("#keyword").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $status=agentList.getEnable(aData.enable,true);
        $operation="<a href='javascript:;' onclick=agentList.edit('" + aData.id + "');>编辑</a>"+"&nbsp" +
            "<a href='javascript:;' onclick=agentList.setEnable('" + aData.id + "');>"+agentList.getEnable(aData.enable,false)+"</a>";
        $('td:eq(6)', nRow).html($status);
        $('td:eq(7)', nRow).html($operation);
    }
};
var agentList = {
    "url": '/orderCenter/agentTmp/list',
    "type": "GET",
    "table_id": "agent_tab",
    "columns": [
        {"data": "agentName", "title": "姓名", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "identityType", "title": "证件类型", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "createTime", "title": "创建时间", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "updateTime", "title": "最后操作时间", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "operator", "title": "操作人", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "comment", "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "状态", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "270px"}
    ],
    /* 编辑 */
    edit:function(id){
        if (!common.permission.validUserPermission("or030201")) {
            return;
        }
        agent_pop.show("update");
        agent_pop.detail.info.init(id);
        agent_pop.detail.list.init(id);
    },
    getEnable:function(enable,bool){
        if((enable&&bool)||(!enable&&!bool)){
            return "<span style=\"color: green;\">启用</span>";
        }else{
            return "<span style=\"color:red ;\">禁用</span>";
        }
    },
    setEnable:function(id){
        if (!common.permission.validUserPermission("or030201")) {
            return;
        }
        common.getByAjax(true, "put", "json", "/orderCenter/agentTmp/enable/" + id, null,
            function(data) {
                if(data.pass){
                    datatables.ajax.reload();
                }
            },
            function() {
                popup.mould.popTipsMould(false, "设置代理人状态异常！", popup.mould.second, popup.mould.error, "", "57%", null);
            }
        );
    },
    addAgentRebateHistory:function(id) {
        if (!common.permission.validUserPermission("or030201")) {
            return;
        }
        agent_pop.addAgentRebateHistory(id);
    }
};
var parent = window.parent;
var agent_pop = {
    init:function(){
        var detailContent=$("#detail_content");
        if (detailContent.length > 0) {
            agent_pop.detailContent = detailContent.html();
            detailContent.remove();
        }
    },
    initRebate:function(){
        var detailContent=$("#agent_rebate_history_content");
        if (detailContent.length > 0) {
            agent_pop.agentRebateContent = detailContent.html();
            detailContent.remove();
        }
    },

    initList: function(agentId) {
        common.getByAjax(true, "get", "json", "/orderCenter/agentTmp/rebate/historyList",
            {
                agentId : agentId
            },
            function(data) {
                parent.$("#tabHistory tbody").empty();
                if (data) {
                    var content = "";2
                    $.each(data, function(i, model){
                        content += "<tr>" +
                            "<td>" + model.areaName + "</td>" +
                            "<td>" + model.companyName + "</td>" +
                            "<td align='center'>" + model.commercialRebate + "%</td>" +
                            "<td align='center'>" + model.compulsoryRebate + "%</td>" +
                            "<td>" + model.startTime + "</td>" +
                            "<td>" + common.checkToEmpty(model.endTime) + "</td>" +
                            "</tr>";
                    });
                    parent.$("#tabHistory tbody").append(content);
                }
            },function() {}
        );
    },
    /* 获取城市列表 */
    initArea : function() {
        common.getByAjax(true,"get","json","/orderCenter/resource/areas",null,
            function(data){
                if(data == null){
                    return false;
                }
                var areaId = "";
                var options = "";
                $.each(data, function(i,model){
                    if(i == 0) {
                        areaId = model.id;
                    }
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });

                parent.$("#areaSel").append(options);
                agent_pop.initInsuranceCompanyByArea(areaId);
            },function(){}
        );
    },
    /* 获取保险公司列表 */
    initInsuranceCompanyByArea : function(areaId) {
        common.getByAjax(true,"get","json","/orderCenter/resource/insuranceCompany/getQuotableCompaniesByArea",{areaId:areaId},
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });
                parent.$("#insuranceCompanySel").html(options);
            },function(){}
        );
    },
    validate:function(){
        if(common.validations.isEmpty(parent.$("#agentName").val())){
            agent_pop.error("请输入姓名");
            return false;
        }
        /*if(common.validations.isEmpty(parent.$("#agentMobile").val())||!common.isMobile(parent.$("#agentMobile").val())){
            agent_pop.error("请输入正确的手机号");
            return false;
        }*/
        //if(common.validations.isEmpty(parent.$("#identityNumber").val())){
        //    agent_pop.error("请输入证件号码");
        //    return false;
        //}
        if(common.validations.isEmpty(parent.$("#rebate").val())||!common.isNumber(parent.$("#rebate").val())){
            agent_pop.error("请输入正确的返点信息");
            return false;
        }
        var map=new Map();
        for(var i=0;i<agent_pop.detail.list.rebateIndex;i++){
            var areaSel=parent.$("#area_"+i);
            var companySel=parent.$("#company_"+i);
            var compulsoryRebate=parent.$("#compulsory_"+i);
            var commercialRebate=parent.$("#commercial_"+i);
            if(areaSel.length==0){
                continue;
            }
            if(areaSel.val()==0){
                agent_pop.error("请选择城市");
                return false;
            }
            if(companySel.val()==0){
                agent_pop.error("请选择保险公司");
                return false;
            }
            if(map.get(areaSel.val())==companySel.val()){
                agent_pop.error("城市和保险公司重复");
                return false;
            }
            map.put(areaSel.val(),companySel.val());
            if(common.validations.isEmpty(compulsoryRebate.val())||!common.isNumber(compulsoryRebate.val())){
                agent_pop.error("请输入正确的交强险返点");
                return false;
            }
            if(common.validations.isEmpty(commercialRebate.val())||!common.isNumber(commercialRebate.val())){
                agent_pop.error("请输入正确的商业险返点");
                return false;
            }
        }
        return true;
    },
    validateRebate:function(){
        if(common.validations.isEmpty(parent.$("#areaSel").val())){
            agent_pop.error("请选择城市");
            return false;
        }
        if(common.validations.isEmpty(parent.$("#insuranceCompanySel").val())){
            agent_pop.error("请选择保险公司");
            return false;
        }
        if(common.validations.isEmpty(parent.$("#commercialRebate").val())||!common.isNumber(parent.$("#commercialRebate").val())){
            agent_pop.error("请输入正确的商业险返点");
            return false;
        }
        if(common.validations.isEmpty(parent.$("#compulsoryRebate").val())||!common.isNumber(parent.$("#compulsoryRebate").val())){
            agent_pop.error("请输入正确的交强险返点");
            return false;
        }
        if(common.validations.isEmpty(parent.$("#startTime").val())){
            agent_pop.error("请选择开始时间");
            return false;
        }
        return true;
    },
    error:function(msg){
        parent.$("#errorText").html(msg);
        parent.$(".error-msg").show().delay(2000).hide(0);
    },
    success:function(msg){
        parent.$("#successText").html(msg);
        parent.$(".success-msg").show().delay(2000).hide(0);
    },
    show:function(operate){
        popup.pop.popInput(false, agent_pop.detailContent, 'first', "800px", "595px", "33%", "50%");
        agent_pop.areaSel=agent_pop.detail.list.initArea();
        // agent_pop.companySel=agent_pop.detail.list.initInsuranceCompany();
        CUI.grid.dom=parent.$("#tabRebate tbody");
        agent_pop.detail.list.rebateIndex=0;
        parent.$("#cancel_button").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
        parent.$("#agent_detail_close").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
        parent.$("#agent_create_btn").unbind("click").bind({
            click: function () {
                agent_pop.detail.list.add();
            }
        });
        parent.$(".area").unbind("change").bind({
            change:function(){
                var companySel=agent_pop.detail.list.initInsuranceCompany($(this).val());
                $(this).parent().next().find("select").html(companySel);
            }
        });
        if(operate=='add'){
            parent.$("#update_button").unbind("click").bind({
                click: function () {
                    if(!agent_pop.validate()){
                        return;
                    }
                    common.getByAjax(true, "get", "json", "/orderCenter/agentTmp/check", parent.$("#agentInputForm").serialize(),
                        function(data){
                            if(data.pass&&data.message!="success"){//是否绑定
                                popup.mould.popConfirmMould(false, data.message, popup.mould.second, "", "",
                                    function() {
                                        popup.mask.hideSecondMask(false);
                                        agent_pop.detail.info.save("/orderCenter/agentTmp/add");
                                    },
                                    function() {
                                        popup.mask.hideSecondMask(false);
                                    }
                                );
                            }else if(!data.pass){
                                popup.mould.popTipsMould(false, data.message, popup.mould.second, popup.mould.error, "", "57%", null);
                            }else{
                                agent_pop.detail.info.save("/orderCenter/agentTmp/add");
                            }
                        },function(){
                            popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                        }
                    );
                }
            });
        }else{
            parent.$("#update_button").unbind("click").bind({
                click: function () {
                    if(!agent_pop.validate()){
                        return;
                    }
                    agent_pop.detail.info.save("/orderCenter/agentTmp/update");
                }
            });
        }
    },
    addAgentRebateHistory:function(id){
        popup.pop.popInput(false, agent_pop.agentRebateContent, 'first', "750px", "550px", "30%", "50%");
        agent_pop.initArea();
        agent_pop.initList(id);
        parent.$("#agent_id").val(id);
        parent.$("#agent_rebate_history_close").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
        parent.$("#rebate_cancel_button").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
        parent.$("#areaSel").unbind("change").bind({
            change:function(){
                agent_pop.initInsuranceCompanyByArea($(this).val());
            }
        });
        parent.$("#rebate_save_button").unbind("click").bind({
            click: function () {
                if(!agent_pop.validateRebate()){
                    return;
                }
                common.getByAjax(true, "put", "json", "/orderCenter/agentTmp/rebate", parent.$("#agentRebateHistoryForm").serialize(),
                    function(data){
                        if(data.pass) {
                            agent_pop.initList(parent.$("#agent_id").val());
                            agent_pop.success("保存出单机构历史费率成功！");
                            //popup.mask.hideFirstMask(false);
                            //popup.mould.popTipsMould(false, "保存代理人历史费率成功！", popup.mould.first, popup.mould.success, "", "57%", null);
                            return false;
                        } else {
                            agent_pop.error(data.message);
                        }
                    },function(){
                        popup.mould.popTipsMould(false, "设置代理人历史费率异常", popup.mould.first, popup.mould.error, "", "57%", null);
                    }
                );
            }
        });
    },
    detail:{
        info:{
            init:function(id){
                common.ajax.getByAjax(true, "put", "json", "/orderCenter/agentTmp/"+id, null,
                    function(data){
                        if(data == null){
                            popup.mould.popTipsMould(false, "获取代理人信息失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                            return false;
                        }
                        parent.$("#agentName").val(data.agentName);
                        parent.$("#agentMobile").val(data.agentMobile);
                        parent.$("#identityTypeSel").val(data.identityType);
                        parent.$("#identityNumber").val(data.identityNumber);
                        parent.$("#cardNumber").val(data.cardNumber);
                        parent.$("#openingBank").val(data.openingBank);
                        parent.$("#bankBranch").val(data.bankBranch);
                        parent.$("#rebate").val(data.rebate);
                        parent.$("#bankAccount").val(data.bankAccount);
                        parent.$("#comment").val(data.comment);
                        parent.$("#createTime").text(data.createTime);
                        parent.$("#id").val(id);
                    },function(){
                        popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.warning, "", "57%", null);
                    }
                );
            },
            save:function(url){
                parent.$("#update_button").attr("disabled", true);
                common.getByAjax(true, "get", "json", url, parent.$("#agentInputForm").serialize(),
                    function(data){
                        if(data.pass){
                            popup.mould.popTipsMould(false, "保存成功！", popup.mould.first, popup.mould.success, "", "57%", null);
                            Agent.list();
                            //showContent();
                        }else{
                            agent_pop.error(data.message);
                        }
                        parent.$("#update_button").attr("disabled", false);
                    },function(){
                        popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.warning, "", "57%", null);
                        parent.$("#update_button").attr("disabled", false);
                    }
                );
            },
            cancel:function(){

            }
        },
        list:{
            rebateIndex:0,
            init:function(agentId){
                common.ajax.getByAjax(true,"get","json","/orderCenter/agentTmp/rebate/"+agentId, null,
                    function(data){
                        agent_pop.detail.list.rebateIndex=data.viewList.length;
                        CUI.grid.store=data;
                        CUI.grid.columns=[
                            {dataIndex:'area',renderer:function(value,rowIndex){
                                return "<select id='area_" + rowIndex +"' name='agentRebate[" + rowIndex +"].area' class='area'>"+agent_pop.detail.list.initArea(value)+"</select>";
                            }},
                            {dataIndex:'insuranceCompany',renderer:function(value,rowIndex,rowStore){
                                return "<select id='company_" + rowIndex +"' name='agentRebate[" + rowIndex +"].insuranceCompany' class='area'>"+agent_pop.detail.list.initInsuranceCompany(rowStore.area,value)+"</select>";
                            }},
                            {dataIndex:'compulsoryRebate',renderer:function(value,rowIndex){
                                return "<input type='text' id='compulsory_" + rowIndex +"' name='agentRebate[" + rowIndex +"].compulsoryRebate' value="+value+" size='5'>%";
                            }},
                            {dataIndex:'commercialRebate',renderer:function(value,rowIndex){
                                return "<input type='text' id='commercial_" + rowIndex +"' name='agentRebate[" + rowIndex +"].commercialRebate' value="+value+" size='5'>%";
                            }},
                            {dataIndex:'',renderer:function(value,rowIndex,rowStore){
                                return "<a href='javascript:;' class='del' index='"+rowIndex+"'>删除</a>";
                            }},
                        ];
                        CUI.grid.fill();
                        parent.$(".del").unbind("click").bind({
                            click:function(){
                                agent_pop.detail.list.del($(this).attr("index"));
                            }
                        });
                        parent.$(".area").unbind("change").bind({
                            change:function(){
                                var companySel=agent_pop.detail.list.initInsuranceCompany($(this).val(),null);
                                $(this).parent().next().find("select").html(companySel);
                            }
                        })
                    },function(){
                        popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.warning, "", "57%", null);
                        $("#selectBtn").attr("disabled", false);
                        return false;
                    }
                );
            },
            add:function(){
                var tr="";
                tr += "<tr class='text-center' id='"+ROW.ID+agent_pop.detail.list.rebateIndex+"'>" +
                    "<td><select id='area_" + agent_pop.detail.list.rebateIndex +"' name='agentRebate[" + agent_pop.detail.list.rebateIndex +"].area' class='area'>" + agent_pop.areaSel + "</select></td>" +
                    "<td><select id='company_" + agent_pop.detail.list.rebateIndex +"' name='agentRebate[" + agent_pop.detail.list.rebateIndex +"].insuranceCompany'><option value='0'>请选择保险公司</option>" + agent_pop.companySel + "</select></td>" +
                    "<td><input type='text' id='compulsory_" + agent_pop.detail.list.rebateIndex +"' name='agentRebate[" + agent_pop.detail.list.rebateIndex +"].compulsoryRebate' size='5' value='0'>%</td>" +
                    "<td><input type='text' id='commercial_" + agent_pop.detail.list.rebateIndex +"' name='agentRebate[" + agent_pop.detail.list.rebateIndex +"].commercialRebate' size='5' value='0'>%</td>" +
                    "<td><a href='javascript:;' class='del' index='"+agent_pop.detail.list.rebateIndex+"'>删除</a></td>" +
                    "</tr>";
                CUI.grid.addTR(tr,function(){
                    agent_pop.detail.list.rebateIndex++;
                });
                parent.$(".del").unbind("click").bind({
                    click:function(){
                        agent_pop.detail.list.del($(this).attr("index"));
                    }
                });
                parent.$(".area").unbind("change").bind({
                    change:function(){
                        var companySel=agent_pop.detail.list.initInsuranceCompany($(this).val(),null);
                        $(this).parent().next().find("select").html(companySel);
                    }
                })
            },
            del:function(rowIndex){
                CUI.grid.removeTR(rowIndex,function(){
                    // agent_pop.detail.list.rebateIndex--;
                });
            },
            initArea:function(id){
                var option="";
                common.ajax.getByAjax(false,"get","json","/orderCenter/resource/areas",null,
                    function(data){
                        if(data == null){
                            return false;
                        }
                        option +="<option value='0'>请选择城市</option>"
                        $.each(data, function(i,model){
                            if(id==model.id){
                                option += "<option value='"+ model.id +"' selected='selected'>" + model.name + "</option>";
                            }else{
                                option += "<option value='"+ model.id +"'>" + model.name + "</option>";
                            }
                        });
                    },function(){}
                );
                return option;
            },
            initInsuranceCompany:function(areaId,id){
                var option="";
                common.ajax.getByAjax(false,"get","json","/orderCenter/resource/insuranceCompany/getQuotableCompaniesByArea",{areaId:areaId},
                    function(data){
                        if(data == null){
                            return false;
                        }
                        option +="<option value='0'>请选择保险公司</option>"
                        $.each(data, function(i,model){
                            if(id==model.id){
                                option += "<option value='"+ model.id +"' selected='selected'>" + model.name + "</option>";
                            }else{
                                option += "<option value='"+ model.id +"'>" + model.name + "</option>";
                            }
                        });
                    },function(){}
                );
                return option;
            }
        }
    }
}
var datatables = datatableUtil.getByDatatables(agentList, dataFunction.data, dataFunction.fnRowCallback);

$(function () {

    agent_pop.init();
    agent_pop.initRebate();
    /**
     * 搜索框
     */
    $("#searchBtn").unbind("click").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            datatableUtil.params.keyword = keyword;
            datatables.ajax.reload();
        }
    });


    $("#newAgent").bind({
        click: function () {
            if (!common.permission.validUserPermission("or030201")) {
                return;
            }
            agent_pop.show('add');
        }
    });
});
