/**
 * Created by lyh on 2015/10/30.
 */
var quote_photo=null;
var purpose_customer = {
    showExpireTimeSource:[42,43,10,90,100,110,113,115,120],//显示车险到期日日期选择框
    page: new Properties(1, ""),
    init: {
        init: function() {
            purpose_customer.init.initChannel();
            purpose_customer.init.initType();
            purpose_customer.allList.list();
            purpose_customer.init.initArea();
        },
        getEndDate : function() {
            var date = new Date();
            var value = "" + date.getFullYear() + "-";
            value += ((date.getMonth() + 1) > 9 ? (date.getMonth() + 1).toString():'0' + (date.getMonth() + 1)) + "-";
            value += (date.getDate() > 9 ? date.getDate().toString():'0' + date.getDate());
            return value;
        },
        getStartDate : function() {
            var date = new Date(Date.parse(purpose_customer.init.getEndDate()) - (86400000 * 3));
            var value = "" + date.getFullYear() + "-";
            value += ((date.getMonth() + 1) > 9 ? (date.getMonth() + 1).toString():'0' + (date.getMonth() + 1)) + "-";
            value += (date.getDate() > 9 ? date.getDate().toString():'0' + date.getDate());
            return value;
        },
        initChannel: function() {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/dataSourceChannel", {},
                function(data) {
                    if (data) {
                        var options = "";
                        $.each(data, function(i, model){
                            options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                        });
                        $("#channelSel").append(options);
                        $("#channelSel").multiselect({
                            nonSelectedText: '请选择渠道',
                            buttonWidth: '180',
                            maxHeight: '180',
                            includeSelectAllOption: true,
                            selectAllNumber: false,
                            selectAllText: '全部',
                            allSelectedText: '全部'
                        });
                    }
                },function() {}
            );
        },
        initType: function() {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/dataSourceType", {},
                function(data) {
                    if (data) {
                        var options = "";
                        $.each(data, function(i, model){
                            options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                        });
                        $("#typeSel").append(options);
                        $("#typeSel").multiselect({
                            nonSelectedText: '请选择类型',
                            buttonWidth: '180',
                            maxHeight: '180',
                            includeSelectAllOption: true,
                            selectAllNumber: false,
                            selectAllText: '全部',
                            allSelectedText: '全部'
                        });
                    }
                },function() {}
            )
        }, initArea : function() {
            common.getByAjax(true,"get","json","/orderCenter/resource/areas",null,
                function(data){
                    var options = "";
                    $.each(data, function(i,model){
                        if(model.id== 420100 || model.id == 440300){
                            options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                        }
                    });
                    $("#areaSel").append(options);
                    $("#areaSel").multiselect({
                        nonSelectedText: '请选择城市',
                        buttonWidth: '180',
                        maxHeight: '180',
                       // includeSelectAllOption: true,
                        //selectAllNumber: true,
                        //selectAllText: '全部',
                        //allSelectedText: '全部',
                    });
                    //$('#areaSel option').each(function(i,content){
                    //    if(content.value !=- 1){
                    //        this.selected=true;
                    //    }
                    //});
                    $("#areaSel").multiselect('refresh');
                },function(){}
            );
        }
    },
    allList: {
        list: function() {
            common.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter",
                {
                    channelIds : $("#channelSel").val()?$("#channelSel").val().join(","):null,
                    telTypes : $("#typeSel").val()?$("#typeSel").val().join(","):null,
                    areaType : $('#areaType').is(':checked')?1:null,
                    expireTime : $("#expireTime").val(),
                    startTime : $("#startTime").val(),
                    endTime : $("#endTime").val(),
                   areaId : $("#areaSel").val()?$("#areaSel").val().join(","):null
                },
                function(data) {
                    var priorityList = data.priorityList;
                    var normalList = data.normalList;
                    $("#precedence_list_tab tbody").empty();
                    $("#normal_list_tab tbody").empty();
                    if (priorityList && priorityList.length > 0) {
                        purpose_customer.allList.priorityList(priorityList);
                    }
                    if(normalList && normalList.length > 0){
                        purpose_customer.allList.normalList(normalList);
                    } else {
                        $("#newBatch").attr("disabled",false);
                    }
                },function(){
                    popup.mould.popTipsMould(false, "获取优先和正常列表异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        priorityList: function(priorityList) {
            var content = "";
            $.each(priorityList, function(i, view) {
                content += "<tr class='text-center'>";
                if(view.sourceId == 42 || view.sourceId == 43)
                    content += "<td ><span class='glyphicon glyphicon-bell' style='color: #337ab7; text-indent:1em;'></span>" + common.getOrderIconByData(view.channelIcon,view.triggerTime) + "</td>";
                else
                    content += "<td>" + common.getOrderIconByData(view.channelIcon,view.triggerTime) + "</td>";
                content += "<td>" + view.mobile + "</td>" +
                    "<td>" + common.checkToEmpty(view.userName) + "</td>" +
                    "<td>" + common.checkToEmpty(view.expireTime) + "</td>" +
                    "<td>" + common.checkToEmpty(view.sourceName) + "</td>" +
                    "<td>" + common.checkToEmpty(view.statusName) + "</td>" +
                    "<td><div title='"+ common.checkToEmpty(view.comment) +"' style='white-space:nowrap;overflow:hidden; text-overflow:ellipsis;width:20em;'>" +common.checkToEmpty(view.comment)+ "</div></td>" +
                    "<td><a id='editAction"+ view.mobile+"' telId='"+ view.id +"' style=\"margin-left: 10px;\" href='/page/telMarketingCenter/search_list.html?clickType=purposeCustomer&editType=0&id="+view.id+"' target='_blank'>编辑</a></td>" +
                    "</tr>";
            });
            $("#precedence_list_tab tbody").append(content);
            common.scrollToTop();
        },
        normalList: function(normalList) {
            var content = "";
            var statusNoDeal = "";
            $.each(normalList, function(i, view) {
                if (view.statusId == 1){
                    statusNoDeal = 1;
                }
                content += "<tr class='text-center'>" +
                    "<td>" + common.getOrderIconByData(view.channelIcon,view.mobile) + "</td>" +
                    "<td>" + common.checkToEmpty(view.userName) + "</td>" +
                    "<td>" + common.checkToEmpty(view.expireTime) + "</td>" +
                    "<td>" + common.checkToEmpty(view.sourceName) + "</td>" +
                    "<td>" + common.checkToEmpty(view.statusName) + "</td>" +
                    "<td><a id='editAction"+ view.mobile+"' telId='"+ view.id +"' style=\"margin-left: 10px;\" href='/page/telMarketingCenter/search_list.html?clickType=purposeCustomer&editType=0&id="+view.id+"' target='_blank'>编辑</a></td>" +
                    "</tr>";
            });
            $("#newBatch").attr("disabled",false);
            if(statusNoDeal == 1){
                $("#newBatch").attr("disabled",true);
            }
            $("#normal_list_tab tbody").append(content);

            common.scrollToTop();
        }
    }
};

$(function() {
// 设置数据起始时间和数据截止时间
    $("#startTime").val(purpose_customer.init.getStartDate());
    $("#endTime").val(purpose_customer.init.getEndDate());
    purpose_customer.init.init();

    /* 选择类型 */
    $("#typeSel").unbind("change").bind({
        change : function(){
            var telTypes = $(this).val()?$(this).val().join(","):"";
            if(telTypes.indexOf("1") >= 0 || telTypes.indexOf("5") >= 0) {
                $("#expireTime").show();
            } else {
                $("#expireTime").hide();
                $("#expireTime").val("");
            }
        }
    });

    /* 搜索 */
    $("#searchBtn").unbind("click").bind({
        click : function(){
            if (!common.permission.validUserPermission("or060102")) {
                return;
            }
            search_list.page.currentPage = 1;
            var keyword = $("#mobilePhone").val();
            if(common.isEmpty(keyword)){
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            search_list.searchList.list(1);
            $("#firstPage").hide();
            $("#lastPage").show();
            search_list.page.currentPage = 1;
            search_list.page.keyword = keyword;
        }
    });

    /*已处理完，换一批*/
    $("#newBatch").unbind("click").bind({
        click : function(){
            $("#newBatch").attr("disabled",true);
            common.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/newBatch",{},
                function(data) {
                    if(data.pass){
                        var flag = true, error_message="";
                        var channelSel = $("#channelSel").val()?$("#channelSel").val().join(","):"";
                        var typeSel = $("#typeSel").val()?$("#typeSel").val().join(","):"";
                        if(common.isEmpty(channelSel) && common.isEmpty(typeSel)){
                            flag = false;
                            error_message = "渠道和类型至少选择一个！";
                        }
                        var startTime = $("#startTime").val();
                        var endTime = $("#endTime").val();
                        if (!startTime) {
                            flag = false;
                            error_message = "数据起始时间不能为空";
                        }
                        if (!endTime) {
                            flag = false;
                            error_message = "数据截止时间不能为空";
                        }
                        if (common.tools.dateTimeCompare(startTime, endTime) < 0) {
                            flag = false;
                            error_message = "截止时间不能早于起始时间";
                        }
                        if(!flag){
                            $("#newBatch").attr("disabled",false);
                            popup.mould.popTipsMould(false, error_message, popup.mould.first, popup.mould.warning, "", "57%", null);
                            return false;
                        }
                        purpose_customer.allList.list();
                    }else {
                        $("#newBatch").attr("disabled",false);
                        popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                    }
                },function(){
                    popup.mould.popTipsMould(false, "获取列表异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        }
    });

    $("#areaType").bind({
        change : function(){
           if( $(this).is(':checked')){
               $(".area").show();
           }else{
               $(".area").hide();
           }
        }
    });
});
