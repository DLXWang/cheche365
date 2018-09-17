/**
 * Created by lyh on 2015/10/30.
 */
var quote_photo=null;
var work_details = {
    type : 1,//默认呼出记录
    timeSlot : 1,//默认是今天
    startTime : "",//起始时间
    endTime : "",//结束时间
    page: new Properties(1, ""),
    quoteListContent: "",
    detailContent: "",
    init: {
        init: function() {
            work_details.workEntryList.list();
            work_details.init.initSource();//获取数据来源下拉
            work_details.init.initChannel();//获取渠道下拉
            work_details.init.initType();//获取类型下拉
            work_details.init.initOrderStatus();//获取出单状态下拉列表
            work_details.init.initOperator();//获取操作人下拉
            work_details.init.initStatus();//获取处理结果
            work_details.init.initCity();//初始化城市列表
        },
        /*initAutoContent: function() {
            var autoContent = $("#auto_content");
            if (autoContent.length > 0) {
                work_details.autoContent = autoContent.html();
                autoContent.remove();
            }
        },*/
        /*initSecondContent: function() {
            var quoteListContent = $("#new_content");
            if (quoteListContent.length > 0) {
                work_details.quoteListContent = quoteListContent.html();
                quoteListContent.remove();
            }
        },*/
        /*initPopupContentEdit: function() {
            var detailContent = $("#detail_content");
            if (detailContent.length > 0) {
                work_details.detailContent = detailContent.html();
                detailContent.remove();
            }
        },*/
        initPopupContent: function() {
            common.getByAjax(true, "get", "html", "/page/telMarketingCenter/search_list.html",{},
                function(data) {
                    $("#all_search_list_div").html(data);
                    $("#editType").val(2);//因为功能模块是一个，但要加权限，所以要判断从哪个功能发起的编辑
                },
                function(){
                    popup.mould.popTipsMould(false, "获取页面信息异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        initSource: function() {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/telMarketingSource", {},
                function(data) {
                    if (data) {
                        var options = "";
                        $.each(data, function(i, model){
                            options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                        });
                        $("#sourceSel").append(options);
                    }
                },function() {}
            );
        },
        initChannel: function() {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/dataSourceChannel", {},
                function(data) {
                    if (data) {
                        var options = "";
                        $.each(data, function(i, model){
                            options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                        });
                        if($("#channelSel").length>0){
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
                    }
                },function() {}
            );
        },
        initType: function() {
            common.getByAjax(false, "get", "json", "/orderCenter/resource/dataSourceType", {},
                function(data) {
                    if (data) {
                        var options = "";
                        $.each(data, function(i, model){
                            options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                        });
                        if($("#typeSel").length>0){
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
                    }
                },function() {}
            )
        },
        initOrderStatus : function(){
            common.getByAjax(false, "get", "json", "/orderCenter/resource/orderTransmissionStatus", {},
                function(data) {
                    if (data) {
                        var options = "";
                        $.each(data, function(i, model){
                            options += "<option value=\"" + model.id + "\">" + model.status + "</option>";
                        });
                        if($("#orderStatusSel").length>0){
                            $("#orderStatusSel").append(options);
                            $("#orderStatusSel").multiselect({
                                nonSelectedText: '请选择类型',
                                buttonWidth: '180',
                                maxHeight: '180',
                                includeSelectAllOption: true,
                                selectAllNumber: false,
                                selectAllText: '全部',
                                allSelectedText: '全部'
                            });
                        }
                    }
                },function() {}
            )
        },
        initStatus: function() {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/telMarketingStatus", {},
                function(data) {
                    if (data) {
                        var options = "";
                        $.each(data, function(i, model){
                            if (model.id > 1) {
                                options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                            }
                        });
                        $("#statusSel").append(options);
                    }
                },
                function() {
                }
            );
        },
        initCity: function(){
            common.getByAjax(true,"get","json","/orderCenter/resource/areas",null,
                function(data){
                    var options = "";
                    $.each(data, function(i,model){
                        options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                    });
                    $("#citySel").append(options);
                    $("#citySel").multiselect({
                        nonSelectedText: '请选择城市',
                        buttonWidth: '180',
                        maxHeight: '180',
                        includeSelectAllOption: true,
                        selectAllNumber: true,
                        selectAllText: '全部开通城市',
                        allSelectedText: '全部开通城市',
                        readonly: true
                    });
                    $("#citySel").multiselect('refresh');
                },function(){}
            );
        },
        initOperator: function(){
            common.getByAjax(true, "get", "json", "/orderCenter/resource/telMarketingOperator", {},
                function(data) {
                    if (data) {
                        var options = "";
                        $.each(data, function(i, model){
                            options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                        });
                        $("#operatorSel").append(options);
                    }
                },function() {}
            );
        }
    },
    configValidation: {
        validate:function() {
            if(window.parent.$("#handleResultSel").val() == 1){
                this.showErrors("请选择处理结果");
                return false;
            }
            if(search_list.detailInfo.requiredTriggerTimeStatus.indexOf(window.parent.$("#handleResultSel").val())>-1
                &&common.isEmpty(window.parent.$("#trigger_time").val())){
                this.showErrors("请选择到期时间");
                return false;
            }
            return true;
        },
        validateQuote: function(){
            if(common.isEmpty(parent.$("#input_owner").val()) ||  common.getLength(parent.$("#input_owner").val()) > 45){
                this.showErrors("车主姓名格式错误");
                return false;
            }
            if(common.isEmpty(parent.$("#input_identity").val()) || !common.isIdCardNo(parent.$("#input_identity").val())){
                this.showErrors("车主身份证号错误");
                return false;
            }
            if(common.isEmpty(parent.$("#input_insuredName").val()) || !common.validateName(parent.$("#input_insuredName").val())){
                this.showErrors("被保险人姓名格式错误");
                return false;
            }
            if(common.isEmpty(parent.$("#input_insuredIdNo").val()) || !common.validations.isIdCardNo(parent.$("#input_insuredIdNo").val())){
                this.showErrors("被保险人身份证号格式错误");
                return false;
            }
            if(common.isEmpty(parent.$("#input_vinNo").val()) || !common.validateVinNo(parent.$("#input_vinNo").val())){
                this.showErrors("车架号格式错误");
                return false;
            }
            if(common.isEmpty(parent.$("#input_engineNo").val()) || !common.validateEngineNo(parent.$("#input_engineNo").val())){
                this.showErrors("发动机号格式错误");
                return false;
            }
            if(parent.$("#input_mobile").val()==''){
                this.showErrors("电话号码不能为空");
                return false;
            }else if(!common.isMobile(parent.$("#input_mobile").val())){
                this.showErrors("电话号码格式错误");
                return false;
            }
            if(parent.$("#sourceChannel").val()==''){
                this.showErrors("请选择产品平台");
                return false;
            }
            return true;
        },
        showErrors: function(errorText) {
            window.parent.$(".error-msg").show();
            window.parent.$("#errorText").text(errorText);
        },
        showButton: function(){
            if($("#beginTime").val()&&$("#endTime").val()){
                $("#timeSlotButton").attr("disabled",false);
            }else{
                $("#timeSlotButton").attr("disabled",true);
            }
        }
    },
    workEntryList: {
        list: function() {
            common.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/history",
                {
                    currentPage : work_details.page.currentPage,
                    pageSize : work_details.page.pageSize,
                    type: work_details.type,
                    timeSlot : work_details.timeSlot,
                    startTime : work_details.startTime,
                    endTime : work_details.endTime,
                    userId : $("#operatorSel").val(),
                    status : $("#statusSel").val(),
                    channelIds : $("#channelSel").val()?$("#channelSel").val().join(","):null,
                    telTypes : $("#typeSel").val()?$("#typeSel").val().join(","):null,
                    orderStatus : $("#orderStatusSel").val()?$("#orderStatusSel").val().join(","):null,
                    handleMode : $("#handleModeSel").val()?$("#handleModeSel").val():2,//默认按行为查询
                    areaId : $("#citySel").val()?$("#citySel").val().join(","):null,
                    isHandled : $("input[name=handled]:checked").val()!=null?$("input[name='handled']:checked").val():null
                },
                function(data) {
                    $("#list_tab tbody").empty();
                    if(work_details.type != 7)
                        work_details.workEntryList.fillListContent(data, work_details.type);
                    else
                        work_details.workEntryList.fillInStoreContent(data);
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $("#work_detail_page_div").hide();
                    }
                    $("#totalCount").text(data.pageInfo.totalElements);
                    if (data.pageInfo.totalPage > 1) {
                        $("#work_detail_page_div").show();
                        $.jqPaginator('#work_detail_page_ul',
                            {
                                totalPages: data.pageInfo.totalPage,
                                visiblePages: work_details.page.visiblePages,
                                currentPage: work_details.page.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        work_details.page.currentPage = pageNum;
                                        work_details.workEntryList.list();
                                    }
                                }
                            }
                        );
                    } else {
                        $("#work_detail_page_div").hide();
                    }
                    work_details.searchOperate.search();
                },function(){
                    popup.mould.popTipsMould(false, "获取信息列表异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        fillListContent: function(data, type) {
            var content = "";
            $.each(data.viewList, function(n, view) {
                content += "<tr class='text-center'>" +
                    "<td>" + common.checkToEmpty(view.operator) + "</td>" ;
                if(view.mobile){
                    var tmpMobile = null;
                    if (type == 4 || view.isTelMaster == 'Y'){
                        tmpMobile = view.mobile;
                    }else {
                        tmpMobile = view.mobile.substring(0,3)+'****'+view.mobile.substring(7,11);
                    }
                    content += "<td>" +common.getOrderIconByData(view.channelIcon,tmpMobile)+ "</td>" ;
                }else{
                    content += "<td>" +common.getOrderIconByData(view.channelIcon,"") + "</td>";
                }
                content += "<td>";
                if(type == 1){
                    content+="呼出时间：";
                }else if(type == 2){
                    content+="短信时间：";
                }else if(type == 3){
                    content+="报价时间：";
                }else if(type == 4){
                    content+="成单时间：";
                }else if(type == 6){
                    content+="预约时间：";
                }
                content+= common.checkToEmpty(view.createTime) + "</td>"
                if (type == 1) {
                    content += "<td><a style=\"margin-left: 10px;\" href='/page/telMarketingCenter/search_list.html?clickType=workDetail&id="+view.id+"' target='_blank'>编辑</a></td>"
                }
                if (type == 4)
                    content += "<td>" + common.checkToEmpty(view.orderNo) + "</td><td>" + common.checkToEmpty(view.orderStatus) + "</td>" ;
                content += "</tr>";
            });
            $("#detail_list_tab tbody").empty();
            $("#detail_list_tab tbody").append(content);
            common.scrollToTop();
        },
        fillInStoreContent : function(data){
            var content = "";
            $.each(data.viewList, function(n, view) {
                var sourceCreateTime = common.tools.formatDate(new Date(view[9]), "yyyy-MM-dd hh:mm:ss");
                content += "<tr class='text-center'><td>" + common.checkToEmpty(view[0]) + "</td>" ;
                content += "<td>" + common.checkToEmpty(sourceCreateTime) + "</td>" ;
                content += "<td>" + common.checkToEmpty(view[5]) + "</td>" ;
                content += "<td>" + common.checkToEmpty(view[6]) + "</td>" ;
                content += "<td>" + common.checkToEmpty(view[1]) + "</td>";
                content += "<td>" + (view[3]=="null"?"未处理":view[3]>view[4]?"已处理":"未处理") + "</td>";
                content += "<td>" + common.checkToEmpty(view[2]) + "</td>";
                content += "<td><a style=\"margin-left: 10px;\" href='/page/telMarketingCenter/search_list.html?clickType=workDetail&editType=0&id="+view[7]+"' target='_blank'>编辑</a></td>";
                content += "</tr>";
            });
            $("#detail_list_tab tbody").empty();
            $("#detail_list_tab tbody").append(content);
            common.scrollToTop();
        },
        getGraphic: function(timeSlot,startTime,endTime,status,sourceId,userId){
            common.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/wholeSituation",
                {
                    timeSlot : timeSlot,
                    startTime : startTime,
                    endTime : endTime,
                    status : status,
                    source : sourceId,
                    userId : userId
                },
                function(data) {
                    var msgStyle = "min-width: 300px; width:21%; height: 260px; auto; display:inline;float:left";
                    $("#callMsg1").empty(); $("#callMsg2").empty(); $("#callMsg3").empty(); $("#callMsg4").empty(); $("#callMsg5").empty(); $("#callMsg6").empty();
                    $("#callMsg1").attr("style",""); $("#callMsg2").attr("style",""); $("#callMsg3").attr("style",""); $("#callMsg4").attr("style","");
                    if(data.callPercentage){
                        $("#callMsg1").attr("style",msgStyle);
                        work_details.graphicSymbol.getPieChart("callMsg1","" ,'' ,eval(data.callPercentage));
                    }
                    if(!status||status == 60){
                        if(data.effectivePercentage){
                            $("#callMsg2").attr("style",msgStyle);
                            work_details.graphicSymbol.getPieChart("callMsg2","" ,'<b>{point.percentage:.1f}%</b>', eval(data.effectivePercentage));
                        }
                        if(data.averageOrderTime){
                            $("#callMsg3").attr("style",msgStyle);
                            work_details.graphicSymbol.getPieChart("callMsg3","" ,'' ,eval(data.averageOrderTime));
                        }
                        if(data.orderByCallTimes){
                            $("#callMsg4").attr("style",msgStyle);
                            work_details.graphicSymbol.getPieChart("callMsg4","" ,'' ,eval(data.orderByCallTimes));
                        }
                    }

                    if(data.statusX && data.statusY){
                        work_details.graphicSymbol.getColumnChart("callMsg5","数据状态","", eval(data.statusX),"数量",[ {  name: '各状态数量',  data: eval(data.statusY)  }]);
                    }
                    if(data.orderX && data.orderY && data.orderPercent){
                        work_details.graphicSymbol.getColumnChart("callMsg6","成单数据来源情况","", eval(data.orderX),"数量",[ {  name: '数量',  data: eval(data.orderY)  },{  name: '百分占比',  data: eval(data.orderPercent)  }]);
                    }
                    $(".highcharts-button").empty();//干掉图标右上角的下载图片那个小按钮
                },
                function() {}
            )
        },

        setUrlParams: function(){
            var params = "?";
            params += "currentPage=" + work_details.page.currentPage;
            params += "&pageSize=" + work_details.page.pageSize;
            params += "&type=" + work_details.type;
            params += "&timeSlot=" + work_details.timeSlot;
            params += "&startTime=" + work_details.startTime;
            params += "&endTime=" + work_details.endTime;
            params += "&userId=" + $("#operatorSel").val();
            params += "&status=" + $("#statusSel").val();
            params +="&channelIds=";
            if($("#channelSel").val())
                params +=$("#channelSel").val().join(",");
            params +="&telTypes=";
            if($("#typeSel").val())
                params +=$("#typeSel").val().join(",");
            params +="&orderStatus=";
            if($("#orderStatusSel").val())
                params +=$("#orderStatusSel").val().join(",");
            params += "&handleMode="
            if($("#handleModeSel").val())
                params +=$("#handleModeSel").val();
            else
                params +=2;
            params += "&areaId=";
            if($("#citySel").val())
                params +=$("#citySel").val().join(",");
            params += "&isHandled=";
            if($("input[name=handled]:checked").val())
                params +=$("input[name='handled']:checked").val();
            return params;
        },

        exportData: function(){
            //TODO 将参数传入到后台，调用查询方法进行数据查询，然后进行Excel的拼装
            var url = "/orderCenter/telMarketingCenter/exportWorkDetail";
            url += work_details.workEntryList.setUrlParams();
            $("#exportWorkDetail").attr("href", url);
        }

    },
    searchOperate: {
        search: function(){
            /*呼出记录、短信数量、报价记录、成单记录、整体情况*/
            $("#entry_div li").unbind("click").bind({
                click: function(){
                    switch(this.value){
                        case 1 :
                            if (!common.permission.validUserPermission("or060201")) {
                                return;
                            };
                            break;
                        case 2 :
                            if (!common.permission.validUserPermission("or060203")) {
                                return;
                            };
                            break;
                        case 3 :
                            if (!common.permission.validUserPermission("or060205")) {
                                return;
                            };
                            break;
                        case 4 :
                            if (!common.permission.validUserPermission("or060207")) {
                                return;
                            };
                            break;
                        case 6 :
                            if (!common.permission.validUserPermission("or060212")) {
                                return;
                            };
                            break;
                        case 7 :
                            if (!common.permission.validUserPermission("or060214")) {
                                return;
                            };
                            break;
                    }
                    $("#entry_div li").css("background","");
                    $("#"+this.id).css("background","#f0ad4e");
                    $("#beginTime").val("");
                    $("#endTime").val("");
                    work_details.startTime = "";
                    work_details.endTime = "";
                    work_details.type = this.value;
                    work_details.timeSlot = 1;//默认开始都是今天
                    $("#time_div [id^='time']").css("background","");
                    $("#"+$("#time_div [id^='time']")[0].id).css("background","#f0ad4e");
                    work_details.searchOperate.handlerDiv(this.value);
                    if (this.value == 5){
                        if (!common.permission.validUserPermission("or060209")) {
                            return;
                        };
                        work_details.workEntryList.getGraphic( work_details.timeSlot, work_details.startTime, work_details.endTime,
                            $("#statusSel").val(), $("#sourceSel").val(), $("#operatorSel").val());
                    }else{
                        work_details.page.currentPage = 1;
                        work_details.workEntryList.list();
                    }
                }
            });
            /*今天、昨天、最近七天、最近一个月、时间段*/
            $("#time_div [id^='time']").unbind("click").bind({
                click: function(){
                    $("#time_div [id^='time']").css("background","");
                    $("#"+this.id).css("background","#f0ad4e");
                    $("#beginTime").val("");
                    $("#endTime").val("");
                    work_details.startTime = "";
                    work_details.endTime = "";
                    if(work_details.type == 5){
                        work_details.timeSlot = $("#"+this.id).attr("label");
                        work_details.workEntryList.getGraphic( $("#"+this.id).attr("label"), work_details.startTime, work_details.endTime,
                            $("#statusSel").val(), $("#sourceSel").val(), $("#operatorSel").val())
                    }else{
                        work_details.timeSlot = $("#"+this.id).attr("label");
                        work_details.startTime = "";
                        work_details.endTime = "";
                        work_details.page.currentPage = 1;
                        work_details.workEntryList.list();
                    }

                }
            });

            $('#beginTime').mousemove(function (){ //失去焦点时触发的时间
                if($("#beginTime").val() && $("#endTime").val()){
                    $("#timeSlotButton").attr("disabled",false);
                }
            })
            $('#endTime').mousemove(function (){ //失去焦点时触发的时间
                if($("#beginTime").val() && $("#endTime").val()){
                    $("#timeSlotButton").attr("disabled",false);
                }
            })

            $("#timeSlotButton").unbind("click").bind({
                click: function(){
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                    $("#time_div [id^='time']").css("background","");
                }
            });

            $("#statusSel").unbind("change").bind({
                change: function(){
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                }
            });

            $("#sourceSel").unbind("change").bind({
                change: function(){
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                }
            });

            $("#channelSel").unbind("change").bind({
                change: function(){
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                }
            });

            $("#typeSel").unbind("change").bind({
                change: function(){
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                }
            });

            $("#orderStatusSel").unbind("change").bind({
                change: function(){
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                }
            });

            $("#citySel").unbind("change").bind({
                change: function(){
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                }
            });

            $("#is_handled").unbind("click").bind({
                click: function(){
                    if($(this).prop('checked')){
                        $("#not_handled").removeAttr('checked');
                    }
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                }
            });

            $("#not_handled").unbind("click").bind({
                click: function(){
                    if($(this).prop('checked')){
                        $("#is_handled").removeAttr('checked');
                    }
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                }
            });

            $("#handleModeSel").unbind("change").bind({
                change: function(){
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                }
            });

            $("#operatorSel").unbind("change").bind({
                change: function(){
                    if (!(common.permission.validUserPermission("or060202")
                        ||common.permission.validUserPermission("or060204")
                        ||common.permission.validUserPermission("or060206")
                        ||common.permission.validUserPermission("or060208")
                        ||common.permission.validUserPermission("or060213"))) {
                        $("#operatorSel").val('');
                        return;
                    }
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();
                    work_details.searchOperate.refreshList();
                }
            });

            $("#statusSel").unbind("change").bind({
                change: function(){
                    work_details.page.currentPage = 1;
                    work_details.startTime = $("#beginTime").val();
                    work_details.endTime = $("#endTime").val();

                }
            });
        },
        refreshList:function(){
            if( work_details.type == 5){
                work_details.workEntryList.getGraphic( work_details.timeSlot, work_details.startTime, work_details.endTime,
                    $("#statusSel").val(), $("#sourceSel").val(), $("#operatorSel").val())
            } else {
                work_details.page.currentPage = 1;
                work_details.workEntryList.list();
            }
        },
        handlerDiv: function(entryDivId) {
            $("#detail_list_tab tbody").empty();
            if (entryDivId == 5){
                $("#status_result1").show();
                $("#status_result2").show();
                $("#condition_channel").hide();
                $("#condition_type").hide();
                $("#condition_source").show();
                $("#detail_div").hide();
                $("#chart_div").show();
                $("#channelSel").val("");
                $("#typeSel").val("");
                $("#condition_order_status").hide();
                $("#order_no").hide();
                $("#order_statue").hide();
                $("#query_Mode").hide();
                $("#city").hide();
                $("#handled_radio").hide();
                $("#operator").show();
                $("#exportData").hide();
            } else if (entryDivId == 1) {
                $("#status_result1").show();
                $("#status_result2").show();
                $("#condition_channel").show();
                $("#condition_type").show();
                $("#condition_source").hide();
                $("#process_status").show();
                $("#detail_div").show();
                $("#chart_div").hide();
                $("#sourceSel").val("");
                $("#condition_order_status").hide();
                $("#order_no").hide();
                $("#order_statue").hide();
                $("#query_Mode").hide();
                $("#city").hide();
                $("#handled_radio").hide();
                $("#dataNum_tr").hide();
                $("#operator").show();
                $("#common_tr").show();
                $("#exportData").hide();
            } else if (entryDivId == 4) {
                $("#status_result1").hide();
                $("#status_result2").hide();
                $("#condition_channel").hide();
                $("#condition_source").hide();
                $("#condition_type").hide();
                $("#process_status").hide();
                $("#chart_div").hide();
                $("#detail_div").show();
                $("#order_no").show();
                $("#order_statue").show();
                $("#condition_order_status").show();
                $("#query_Mode").hide();
                $("#city").hide();
                $("#handled_radio").hide();
                $("#dataNum_tr").hide();
                $("#common_tr").show();
                $("#operator").show();
                $("#exportData").hide();
            } else if (entryDivId == 7) {
                $("#status_result1").hide();
                $("#status_result2").hide();
                $("#process_status").hide();
                $("#chart_div").hide();
                $("#order_no").hide();
                $("#order_statue").hide();
                $("#condition_order_status").hide();
                $("#condition_source").hide();
                $("#detail_div").show();
                $("#condition_type").show();
                $("#condition_channel").show();
                $("#query_Mode").show();
                $("#city").show();
                $("#handled_radio").show();
                $("#common_tr").hide();
                $("#dataNum_tr").show();
                $("#operator").hide();
                $("#exportData").show();
            } else {
                $("#status_result1").hide();
                $("#status_result2").hide();
                $("#condition_channel").hide();
                $("#condition_source").hide();
                $("#condition_type").hide();
                $("#process_status").hide();
                $("#detail_div").show();
                $("#chart_div").hide();
                $("#statusSel").val("");
                $("#channelSel").val("");
                $("#typeSel").val("");
                $("#sourceSel").val("");
                $("#order_no").hide();
                $("#order_statue").hide();
                $("#condition_order_status").hide();
                $("#query_Mode").hide();
                $("#city").hide();
                $("#handled_radio").hide();
                $("#dataNum_tr").hide();
                $("#operator").show();
                $("#common_tr").show();
                $("#exportData").hide();
            }
        }
    },
    graphicSymbol: {
        getPieChart: function(id, title ,pointFormat ,data){
            $('#'+id).highcharts({
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false
                },
                title: {
                    text: title
                },
                tooltip: {
                    pointFormat: pointFormat,
                    percentageDecimals: 1
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true,
                            color: '#D2691E',
                            connectorColor: '#D2691E',
                            formatter: function() {
                                return '<b>'+ this.point.name +'</b>';
                            }
                        }
                    }
                },
                series: [{
                    type: 'pie',
                    name: 'data share',
                    data: data
                }]
            });
        },
        getColumnChart: function( id, title, subtitleText, xText, yText,yVal){
            $('#'+id).highcharts({
                chart: {
                    type: 'column'
                },
                title: {
                    text: title
                },
                subtitle: {
                    text: subtitleText
                },
                xAxis: {
                    categories: xText
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: yText
                    }
                },
                tooltip: {
                    headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                    pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                    '<td style="padding:0"><b>{point.y} </b></td></tr>',
                    footerFormat: '</table>',
                    shared: true,
                    useHTML: true
                },
                plotOptions: {
                    column: {
                        pointPadding: 0.2,
                        borderWidth: 0
                    }
                },
                series: yVal
            });

        }
    }
};

$(function() {
    work_details.init.init();
    /* 搜索 */
    $("#searchBtn").bind({
        click : function(){
            var keyword = $("#mobilePhone").val();
            if(common.isEmpty(keyword)){
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            search_list.page.currentPage = 1;
            search_list.searchList.list(2);
            $("#firstPage").hide();
            $("#lastPage").show();
            search_list.page.currentPage = 1;
            search_list.page.keyword = keyword;
        }
    });

});


$(document).ready(function() {
    $('#operatorSel').select2();
    $('#operatorSel').on("select2:select", function(e) {
        var k= $('#operatorSel').select2('data')[0].text;
        $('#select2-operatorSel-container').text(k);
    });
    $('#statusSel').select2();
    $('#statusSel').on("select2:select", function(e) {
        var k= $('#statusSel').select2('data')[0].text;
        $('#select2-statusSel-container').text(k);
    });
    /*$("[name='channelIds']").select2({
        placeholder: "请选择渠道",
        width:'200px'
    });*/
});
