/**
 * Created by lyh on 2015/10/30.
 */
var purpose_customer = {
    dataTablesPriority: null,
    dataTablesNormal: null,
    dataTablesToday:null,
    init: {
        initHtml: function () {
            purpose_customer.init.initChannel();
            purpose_customer.init.initType();
            purpose_customer.init.initArea();
            purpose_customer.init.initDataList();
        },
        getEndDate: function () {
            var date = new Date();
            var value = "" + date.getFullYear() + "-";
            value += ((date.getMonth() + 1) > 9 ? (date.getMonth() + 1).toString() : '0' + (date.getMonth() + 1)) + "-";
            value += (date.getDate() > 9 ? date.getDate().toString() : '0' + date.getDate());
            return value;
        },
        getStartDate: function () {
            var date = new Date();
            date.setMonth(date.getMonth() - 4);
            var value = "" + date.getFullYear() + "-";
            value += ((date.getMonth() + 1) > 9 ? (date.getMonth() + 1).toString() : '0' + (date.getMonth() + 1)) + "-";
            value += (date.getDate() > 9 ? date.getDate().toString() : '0' + date.getDate());
            return value;
        },
        initChannel: function () {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/dataSourceChannel", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
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
                }, function () {
                }
            );
        },
        initType: function () {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/dataSourceType", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
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
                }, function () {
                }
            )
        },
        initArea: function () {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/areas", null,
                function (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.name + "</option>";
                    });
                    $("#areaSel").append(options);
                    $("#areaSel").multiselect({
                        nonSelectedText: '请选择城市',
                        buttonWidth: '180',
                        maxHeight: '180',
                        includeSelectAllOption: true,
                        selectAllNumber: true,
                        selectAllText: '全部',
                        allSelectedText: '全部'
                    });
                    //$('#areaSel option').each(function(i,content){
                    //    if(content.value !=- 1){
                    //        this.selected=true;
                    //    }
                    //});
                    $("#areaSel").multiselect('refresh');
                }, function () {
                }
            );
        },
        initDataList: function () {
            purpose_customer.dataTablesNormal = datatableUtil.getByDatatables(purpose_customer.dataList.normal, purpose_customer.dataList.data, function (nRow, aData) {
            });
            dt_labels.language.sEmptyTable = "无数据！";
            purpose_customer.dataTablesPriority = datatableUtil.getByDatatables(purpose_customer.dataList.priority, purpose_customer.dataList.data, function (nRow, aData) {
            });
            purpose_customer.dataTablesToday = datatableUtil.getByDatatables(purpose_customer.dataList.today, purpose_customer.dataList.data, function (nRow, aData) {
            });
        },
        reloadDataTables: function () {
            purpose_customer.dataTablesNormal.ajax.reload();
            purpose_customer.dataTablesPriority.ajax.reload();
            purpose_customer.dataTablesToday.ajax.reload();
        }
    },
    dataList: {
        // "fnRowCallbackNormal": function (nRow, aData) {
        //     $orderNo = common.getOrderIconClean(aData.channelIcon) + '<a href="#" onclick="">' + aData.orderNo + '</a><br/>' + aData.createTime;
        //     $('td:eq(1)', nRow).html($orderNo);
        // },
        // "fnRowCallbackPriority": function (nRow, aData) {
        //     $orderNo = common.getOrderIconClean(aData.channelIcon) + '<a href="#" onclick="">' + aData.orderNo + '</a><br/>' + aData.createTime;
        //     $('td:eq(10)', nRow).html($orderNo);
        // },
        "data": function (data) {
            var dataType = $("#searchType").val();
            var dataLevel = null;
            var telTypes = null;
            if (dataType === "1") {
                dataLevel = null;
                telTypes = $("#typeSel").val() ? $("#typeSel").val().join(",") : null;
            } else if (dataType == "2") {
                telTypes = null;
                dataLevel = $("#levelSel").val();
            }

            data.channelIds = $("#channelSel").val() ? $("#channelSel").val().join(",") : null;
            data.telTypes = telTypes;
            data.areaType = $('#areaType').is(':checked') ? 1 : null;
            data.expireTime = $("#expireTime").val();
            data.startTime = $("#startTime").val();
            data.endTime = $("#endTime").val();
            data.areaId = $("#areaSel").val() ? $("#areaSel").val().join(",") : null;
            data.dataLevel = dataLevel;
        },
        normal: {
            url: "/orderCenter/telMarketingCenter/dataList/normal",
            type: "get",
            table_id: "normal_list_tab",
            "columns": [
                {
                    "data": "encyptMobile",
                    "title": "电话",
                    "render": function (data, type, row) {
                        var iconPath = row.channelIcon;
                        if (!common.isEmpty(iconPath)) {
                            return "<span><img src='" + iconPath + "' style='height:16px;width:16px;margin-right:3px;'>" + data + "</span>";
                        } else {
                            return data;
                        }
                    },
                    // render: function (data, type, row) {
                    //     var reutrnStr;
                    //     var iconPath = row.channelIcon;
                    //     if (!common.isEmpty(iconPath)) {
                    //         reutrnStr = "<span><img src='" + iconPath + "' style='height:16px;width:16px;margin-right:3px;'>" + data + "</span>";
                    //     } else {
                    //         reutrnStr = data;
                    //     }
                    //     if(data){
                    //         return reutrnStr + '<a href="#" onclick="threewayCall(' + data + ')">拨打电话</a>';
                    //     }
                    // },
                    'sClass': "text-center",
                    "orderable": false
                },
                {"data": "userName", "title": "姓名", 'sClass': "text-center", "orderable": false},
                {"data": "expireTime", "title": "车险到期日", 'sClass': "text-center", "orderable": false},
                {"data": "sourceName", "title": "来源", 'sClass': "text-center", "orderable": false},
                {"data": "statusName", "title": "处理结果", 'sClass': "text-center", "orderable": false},
                {
                    data: "id",
                    "title": '操作',
                    render: function (data, type, row) {
                        if (type === 'display') {
                            return "<a id='editAction" + row.mobile + "' telId='" + data + "' style=\"margin-left: 10px;\" href='/page/telMarketingCenter/search_list.html?clickType=purposeCustomer&editType=0&id=" + data + "' target='_blank'>编辑</a>";
                        }
                        return data;
                    },
                    className: "text-center checkbox-width",
                    "orderable": false
                },
            ]
        },
        priority: {
            url: "/orderCenter/telMarketingCenter/dataList/priority",
            type: "get",
            table_id: "priority_list_tab",
            "columns": [
                {
                    "data": "triggerTime", "title": "预约时间",
                    "render": function (data, type, row) {
                        var iconPath = row.channelIcon;
                        if (!common.isEmpty(iconPath)) {
                            return "<span class='glyphicon glyphicon-bell' style='color: #337ab7; text-indent:1em;'></span><span><img src='" + iconPath + "' style='height:16px;width:16px;margin-right:3px;'>" + data + "</span>";
                        } else {
                            return data;
                        }
                    },
                    // render: function (data, type, row) {
                    //     var reutrnStr;
                    //     var iconPath = row.channelIcon;
                    //     if (!common.isEmpty(iconPath)) {
                    //         reutrnStr = "<span class='glyphicon glyphicon-bell' style='color: #337ab7; text-indent:1em;'></span><span><img src='" + iconPath + "' style='height:16px;width:16px;margin-right:3px;'>" + data + "</span>";
                    //     } else {
                    //         reutrnStr = data;
                    //     }
                    //     if(data){
                    //         return reutrnStr + '<a href="#" onclick="threewayCall(' + data + ')">拨打电话</a>';
                    //     }
                    // },
                    'sClass': "text-center",
                    "orderable": false
                },
                {"data": "encyptMobile", "title": "电话", 'sClass': "text-center", "orderable": false},
                {"data": "userName", "title": "姓名", 'sClass': "text-center", "orderable": false},
                {"data": "expireTime", "title": "车险到期日", 'sClass': "text-center", "orderable": false},
                {"data": "sourceName", "title": "来源", 'sClass': "text-center", "orderable": false},
                {"data": "statusName", "title": "处理结果", 'sClass': "text-center", "orderable": false},
                {"data": "comment", "title": "备注", 'sClass': "text-center", "orderable": false},
                {
                    data: "id",
                    "title": '操作',
                    render: function (data, type, row) {
                        if (type === 'display') {
                            return "<a id='editAction" + row.mobile + "' telId='" + data + "' style=\"margin-left: 10px;\" href='/page/telMarketingCenter/search_list.html?clickType=purposeCustomer&editType=0&id=" + data + "' target='_blank'>编辑</a>";
                        }
                        return data;
                    },
                    className: "text-center checkbox-width",
                    "orderable": false
                },
            ]
        },
        today: {
            url: "/orderCenter/telMarketingCenter/dataList/today",
            type: "get",
            table_id: "today_list_tab",
            "columns": [
                {"data": "countDown", "title": "车险到期倒计时（天）", 'sClass': "text-center", "orderable": false},
                {"data": "encyptMobile", "title": "电话", 'sClass': "text-center", "orderable": false},
                {"data": "userName", "title": "姓名", 'sClass': "text-center", "orderable": false},
                {"data": "expireTime", "title": "车险到期日", 'sClass': "text-center", "orderable": false},
                {"data": "sourceName", "title": "来源", 'sClass': "text-center", "orderable": false},
                {"data": "statusName", "title": "处理结果", 'sClass': "text-center", "orderable": false},
                {"data": "comment", "title": "备注", 'sClass': "text-center", "orderable": false},
                {
                    data: "id",
                    "title": '操作',
                    render: function (data, type, row) {
                        if (type === 'display') {
                            return "<a id='editAction" + row.mobile + "' telId='" + data + "' style=\"margin-left: 10px;\" href='/page/telMarketingCenter/search_list.html?clickType=purposeCustomer&editType=0&id=" + data + "&autoId=" + row.autoId + "' target='_blank'>编辑</a>";
                        }
                        return data;
                    },
                    className: "text-center checkbox-width",
                    "orderable": false
                },
            ]
        },
        threewayCall:function(mobile){
            common.getByAjax(true, "get", "json","/orderCenter/telMarketingCenter/telMarketer/call" ,{customerNumber:mobile},
                function(data) {
                    if (data.pass) {
                        //popup.mould.popTipsMould(true,"拨打成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                    } else {
                        popup.mould.popTipsMould(false,data.message, popup.mould.first, popup.mould.error, "", "53%", null);
                    }
                },
                function() {
                    popup.mould.popTipsMould(false,"发生异常,获取不到data,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                }
            );
        },
    }
}

$(function () {
    if (!common.permission.hasPermission("or060104") || !common.permission.isAbleCall()) {
        $("#callBtn").hide();
    }
    // 设置数据起始时间和数据截止时间
    $("#startTime").val(purpose_customer.init.getStartDate());
    $("#endTime").val(purpose_customer.init.getEndDate());
    purpose_customer.init.initHtml();

    /* 选择类型 */
    $("#typeSel").unbind("change").bind({
        change: function () {
            var telTypes = $(this).val() ? $(this).val().join(",") : "";
            if (telTypes.indexOf("1") >= 0 || telTypes.indexOf("5") >= 0) {
                $("#expireTime").show();
            } else {
                $("#expireTime").hide();
                $("#expireTime").val("");
            }
        }
    });

    /* 搜索 */
    $("#searchBtn").unbind("click").bind({
        click: function () {
            if (!common.permission.validUserPermission("or060102")) {
                return;
            }
            search_list.page.currentPage = 1;
            var keyword = $("#mobilePhone").val();
            if (common.isEmpty(keyword)) {
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

    $("#paramSearch").unbind("click").bind({
        click: function () {
            var flag = true, error_message = "";
            var startTime = $("#startTime").val();
            var endTime = $("#endTime").val();
            if (common.tools.dateTimeCompare(startTime, endTime) < 0) {
                flag = false;
                error_message = "截止时间不能早于起始时间";
            }
            if (!flag) {
                $("#paramSearch").attr("disabled", false);
                popup.mould.popTipsMould(false, error_message, popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            purpose_customer.init.reloadDataTables();
        }
    });

    /* 数据详细类型、高低级别条件对应 */
    $("#searchType").bind({
        change: function () {
            var dataType = $("#searchType").val();
            if (dataType == "1") {//按数据详细类型筛选
                $("#type-sel").attr("style", "display:inline");
                $("#level-sel").attr("style", "display:none");
                $("#level-sel").val("");
            } else if (dataType == "2") {//按数据高低级别筛选
                $("#level-sel").attr("style", "display:inline");
                $("#type-sel").attr("style", "display:none");
                $("#type-sel").val("");
            }
        }
    });

    /* 拨打电话 */
    $("#callBtn").unbind("click").bind({
        click:function(){
            if(common.isMobile($("#mobilePhone").val())){
                $("#callBtn").attr("disabled",true);
                setTimeout('$("#callBtn").attr("disabled",false)' ,5000);
                purpose_customer.dataList.threewayCall($("#mobilePhone").val());
            }else{
                popup.mould.popTipsMould(false, "请输入正确手机号！", popup.mould.first, popup.mould.warning, "", "57%", null);
            }
        }
    });
});


$(document).ready(function() {
/*    $('#channelSel').select2();
    $('#typeSel').select2();
    $('#areaSel').select2();*/
});
