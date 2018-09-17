/**
 * Created by wangfei on 2015/6/12.
 */
$(function () {
    $("#dataTable").hide();
    redistribution.operation.initOldOperatorList();

    if (common.permission.hasPermission("or060301")) {
        $("#a_user_assign").show();
        redistribution.interface.getUserAssignInfo();
        redistribution.init.initChannels();
        redistribution.init.initType();
        redistribution.init.initArea();
        redistribution.init.initStatus();
    } else {
        $("#a_user_assign").hide();
        $("#div_user_assign").hide();
    }


    $(".tabs a").bind({
        click: function (e) {
            e.preventDefault();
            if ($(this).hasClass("selected")) {
                return false;
            }

            $(this).siblings(".selected").removeClass("selected").addClass("btn-default");
            $(this).addClass("selected").removeClass("btn-default");

            var target = $(this).attr("href").replace("#", "");
            var targetObj = $("#" + target);
            targetObj.show().siblings().not(".tabs").hide();
            targetObj.find(".first").show();
            targetObj.find(".second").hide();

            $(".data-checkbox").prop("checked", false);
        }
    });

    $("#mobile_one_next").bind({
        click: function () {
            var phoneNo = $("#phoneNo").val();
            if (common.validations.isEmpty(phoneNo)) {
                popup.mould.popTipsMould(false, "请填写手机号！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            redistribution.checkPhoneNo(phoneNo);
        }
    });

    $("#nominator_more_next").bind({
        click: function () {
            dt_labels.selected = [];
            $("#dataTable").show();
            var oldOperator = $("#nominator_more_first").find(".oldOperatorList").val();
            if (common.validations.isEmpty(oldOperator)) {
                popup.mould.popTipsMould(false, "请选择当前操作人！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            redistribution.param.operatorId = oldOperator;
            redistribution.param.status= $("#statusSel").val();
            redistribution.checkOldOperator(oldOperator);
        }
    });

    $("#mobile_one_form").find(".submit").bind({
        click: function () {
            var newOperator = $("#mobile_one_form").find(".operatorList").val();
            if (common.validations.isEmpty(newOperator)) {
                popup.mould.popTipsMould(false, "请选择新指定人！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            redistribution.redistributeByMobile($("#phoneNo").val(), newOperator);
        }
    });

    $("#nominator_more_form").find(".submit").bind({
        click: function () {
            var newOperator = $("#nominator_more_form").find(".operatorList").val();
            var distributionMethod = $('input:radio[name="distributionMethod"]:checked').val();

            if (distributionMethod == "1" && common.validations.isEmpty(newOperator)) {
                popup.mould.popTipsMould(false, "请选择新指定人！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }

            if (distributionMethod == "0" && dt_labels.selected.length == 0) {
                popup.mould.popTipsMould(false, "请选择要分配的数据！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }

            redistribution.redistributeByOperator($("#oldOperatorId").val(), newOperator, distributionMethod);

        }
    });

    $(".modify").bind({
        click: function () {
            $(this).parent().parent().parent().hide();
            $(this).parent().parent().parent().parent().find(".first").show();
            $("#statusSel").val(null);
        }
    });

    $("input:radio[name='distributionMethod']").bind({
        click: function () {
            var checkVal = $(this).val();
            if (checkVal == "1" || checkVal == "0") {
                $("#nominator_more_form").find(".newOperatorRow").show();
            } else {
                $("#nominator_more_form").find(".newOperatorRow").hide();
                $("#nominator_more_form").find(".operatorList").val("");
            }
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
    })

    $("#statusSel").unbind("change").bind({
        change: function(){
            redistribution.param.status= $("#statusSel").val();
            redistribution.checkOldOperator();
        }
    });


    var start = 1991; // 指定开始年份
    var end = new Date().getFullYear(); // 获取当前年份
    for(i=start;i<=end;i++){
        $("#renewalDate").append("<option value="+i+">"+i+"</option>")
    }

});

var redistribution = {
    param: {
        dataTables: null,
        dataList: null,
        operatorId: 0,
        operatorListText: "",
        status:null
    },
    init: {
        /* 获取来源列表 */
        initChannels: function () {
            common.getByAjax(false, "get", "json", "/orderCenter/resource/dataSourceChannel", {},
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
        /*获取类型*/
        initType: function () {
            common.getByAjax(false, "get", "json", "/orderCenter/resource/dataSourceType", {},
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
        /* 获取城市列表 */
        initArea: function () {
            common.getByAjax(false, "get", "json", "/orderCenter/resource/areas", null,
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
                        allSelectedText: '全部',
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
        /*获取处理状态*/
        initStatus: function() {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/telMarketingStatus", {},
                function(data) {
                    if (data) {
                        var options = "";
                        $.each(data, function(i, model){
                            if (model.id > 0) {
                                options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                            }
                        });
                        $("#statusSel").append(options);
                    }
                },
                function() {
                }
            );
        }
    },
    checkSelected: function () {
        if ($(".check-box-single:checked").length > 0) {
            $('.inputradio').attr("checked", false);
            $('.inputradio').eq(0).prop("checked", true);
        } else {
            $(".check-box-all").attr("checked", false);
        }
    },
    initPageInfo: function (operatorId) {
        if (redistribution.param.dataTables != null) {
            redistribution.param.dataTables.ajax.reload();
            return false;
        }
        redistribution.param.dataList = {
            url: "/orderCenter/telMarketingCenter/redistribution/list",
            type: "get",
            table_id: "redistribttion_data_table",
            "columns": [
                {
                    data: "id",
                    "title": '<input type="checkbox" onchange="redistribution.checkSelected();" class="data-checkbox check-box-all">',
                    render: function (data, type, row) {
                        if (type === 'display') {
                            return '<input type="checkbox" value="' + data + '" onchange="redistribution.checkSelected();" class="data-checkbox check-box-single">';
                        }
                        return data;
                    },
                    className: "text-center checkbox-width",
                    "orderable": false
                },
                {"data": "mobile", "title": "电话", 'sClass': "text-center", "orderable": false},
                {"data": "userName", "title": "姓名", 'sClass': "text-center", "orderable": false},
                {"data": "expireTime", "title": "车险到期日", 'sClass': "text-center", "orderable": false},
                {"data": "sourceName", "title": "来源", 'sClass': "text-center", "orderable": false},
                {"data": "statusName", "title": "处理结果", 'sClass': "text-center", "orderable": false}
            ]
        };

        redistribution.param.dataTables = datatableUtil.getByDatatables(redistribution.param.dataList, function (data) {
            data.operatorId = redistribution.param.operatorId;
            data.status = $("#statusSel").val();
        }, function (nRow, aData) {
            $(".check-box-all").prop("checked", false);
        });
    },
    fillDataList: function (dataList) {
        var content = "";
        $.each(dataList, function (i, view) {
            content += "<tr class='text-center'>" +
                '<td><input type="checkbox" value=' + view.id + '></td>' +
                "<td>" + common.getOrderIconByData(view.channelIcon, view.mobile) + "</td>" +
                "<td>" + common.checkToEmpty(view.userName) + "</td>" +
                "<td>" + common.checkToEmpty(view.expireTime) + "</td>" +
                "<td>" + common.checkToEmpty(view.sourceName) + "</td>" +
                "<td>" + common.checkToEmpty(view.statusName) + "</td>" +
                "</tr>";
        });
        $("#dataTable tbody").empty();
        $("#dataTable tbody").append(content);
    },
    checkOldOperator: function (operatorId) {
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/redistribution/findByOperator", {
            operatorId: redistribution.param.operatorId,
                status: redistribution.param.status
            },
            function (data) {
                if (data.pass == false) {
                    popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
                redistribution.initPageInfo(operatorId);
                redistribution.writeOperatorText(operatorId, data);

            },
            function () {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
        );
    },
    checkPhoneNo: function (phoneNo) {
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/redistribution/mobile", {phoneNo: phoneNo},
            function (data) {
                if (data.pass == false) {
                    popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
                redistribution.writePhoneText(phoneNo, data);
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
        );
    },
    redistributeByMobile: function (phoneNo, newOperatorId, distributionMethod) {
        $("#mobile_one_form").find(".submit").attr("disabled", true);
        common.ajax.getByAjax(true, "post", "json", "/orderCenter/telMarketingCenter/redistribution/reassignByMobile",
            {
                phoneNo: phoneNo,
                newOperatorId: newOperatorId,
                method: distributionMethod
            },
            function (data) {
                if (data.pass == false) {
                    popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
                popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.success, "", "57%", null);
                //redistribution.writePhoneText(phoneNo, data);
                //$("#mobile_one_form").find(".submit").attr("disabled", false);
                $("#mobile_one_form").hide();
                $("#mobile_one_first").show();
                $("#phoneNo").val("");
                $("#mobile_one_form").find(".submit").attr("disabled", false);

            },
            function () {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                $("#mobile_one_form").find(".submit").attr("disabled", false);
                return false;
            }
        );
    },
    redistributeByOperator: function (oldOperatorId, newOperatorId, distributionMethod) {
        $("#nominator_more_form").find(".submit").attr("disabled", true);
        common.ajax.getByAjax(true, "post", "json", "/orderCenter/telMarketingCenter/redistribution/reassignByOperator",
            {
                oldOperatorId: oldOperatorId,
                newOperatorId: newOperatorId,
                distributionMethod: distributionMethod,
                checkedIds: dt_labels.selected.join(",")
            },
            function (data) {
                if (data.pass == false) {
                    popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
                popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.success, "", "57%", null);
                $("#nominator_more_first").show();
                $("#nominator_more_form").hide();
                $(".oldOperatorList").val("");
                $("#statusSel").val(null);
                $("#nominator_more_form").find(".submit").attr("disabled", false);
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                $("#nominator_more_form").find(".submit").attr("disabled", false);
                return false;
            }
        );
    },
    writePhoneText: function (phoneNo, data) {
        var mobile_one_form = $("#mobile_one_form");

        mobile_one_form.find(".phoneNoText").text(phoneNo);
        mobile_one_form.find(".operatorText").text(common.tools.checkToEmpty(data.oldOperatorName));

        var options = "<option value=''>请选择</option>";
        $.each(data.newOperatorList, function (i, model) {
            options += "<option value='" + model.id + "'>" + model.name + "</option>";
        });
        mobile_one_form.find(".operatorList").empty();
        mobile_one_form.find(".operatorList").append(options);

        $("#mobile_one_first").hide();
        mobile_one_form.show();
    },
    writeOperatorText: function (operatorId, data) {
        var nominator_more_form = $("#nominator_more_form");

        nominator_more_form.find(".countText").text(common.tools.checkToEmpty(data.count));
        nominator_more_form.find(".operatorText").text(common.tools.checkToEmpty(data.oldOperatorName));
        nominator_more_form.find("#oldOperatorId").val(data.oldOperatorId);

        var options = "<option value=''>请选择</option>";
        $.each(data.newOperatorList, function (i, model) {
            options += "<option value='" + model.id + "'>" + model.name + "</option>";
        });

        nominator_more_form.find(".operatorList").empty();
        nominator_more_form.find(".operatorList").append(options);

        $("input:radio[name='distributionMethod']").eq(0).click();
        $("#nominator_more_first").hide();
        nominator_more_form.show();
    },
    operation: {
        toDetailHistory: function () {
            window.open("/page/telMarketingCenter/data_assign_history.html")
        },
        addAssignDiv: function () {
            var assignDivText = '' +
                '             <div class="row assign_div average_div_added" style="width: 55%; float: left;">' +
                '                <div class="div-left" style="font-size: 14px">' +
                '                   <div class="div-left">' +
                '                       <input class="assign_num_input assign-form-value" style="width: 200px;height: 35px" type="text" placeholder="请输入分配的数值">' +
                '                       <span>&nbsp;分配给</span>' +
                '                   </div>' +
                '                   <div class="div-left">' +
                '                      <select class="form-control text-input-150 assigner_input add-user-div assign-form-value">' +
                '                           <option value="">请选择</option>' + redistribution.param.operatorListText +
                '                      </select>' +
                '                   </div>' +
                '                </div>' +
                '             </div>';

            $("#assignDiv").append(assignDivText);
        },
        initOldOperatorList: function () {
            redistribution.interface.getOldOperatorList(function (data) {
                if (data == null) {
                    return false;
                }

                $.each(data, function (i, model) {
                    redistribution.param.operatorListText += "<option value='" + model.id + "'>" + model.name + "</option>";
                });

                $("#nominator_more_first").find(".oldOperatorList").empty();
                $("#nominator_more_first").find(".oldOperatorList").append(redistribution.param.operatorListText);


                $(".userAssigners").empty();
                $(".userAssigners, .assigner_input").append(redistribution.param.operatorListText);
                $(".userAssigners").multiselect({
                    nonSelectedText: '请选择新指定人',
                    buttonWidth: '180',
                    maxHeight: '180',
                    includeSelectAllOption: true,
                    selectAllNumber: false,
                    selectAllText: '全部',
                    allSelectedText: '全部'
                });
            }, function () {
            });
        },
        getSearchParams: function () {
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

            var param = {
                channelIds: $("#channelSel").val() ? $("#channelSel").val().join(",") : null,
                telTypes: telTypes,
                areaType: $('#areaType').is(':checked') ? 1 : null,
                expireTime: $("#expireTime").val(),
                startTime: $("#createStartDate").val(),
                endTime: $("#createEndDate").val(),
                areaId: $("#areaSel").val() ? $("#areaSel").val().join(",") : null,
                dataLevel: dataLevel,
                operatorIds: null,
                operatorAssignNums: null,
                renewalDate:$("#renewalDate").val() == 0 ? null : $("#renewalDate").val()
            }
            return param;
        },
        paramSearch: function () {
            $("#resultDiv").hide();
            $("#load_div").show();
            var param = redistribution.operation.getSearchParams();
            redistribution.interface.paramSearch(param, function (dataMap) {
                $("#load_div").hide();
                $("#resultDiv").show();
                $(".assign-form-value").val("");
                $(".assign-form-checkbox").attr("checked", false);
                $('.assign-form-select').multiselect('clearSelection');
                $("#resultNum").text(dataMap.resultNum);
                $(".average_div_added").remove();
            }, function () {
                popup.mould.popTipsMould(false, "查询失败!", popup.mould.first, popup.mould.error, "", "57%", null);
            })
        },
        chooseThis: function (falseId) {
            $("#assignType" + falseId).attr("checked", false);
        },
        assign: function () {
            var resultNum = $("#resultNum").text();
            if (resultNum == 0) {
                popup.mould.popTipsMould(false, "筛选结果无可分配数据！", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            var assignType = $("input[name='assign']:checked").val();
            if (common.isEmpty(assignType)) {
                popup.mould.popTipsMould(false, "请选择分配方式！", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            } else if (assignType === "1") {
                if (common.isEmpty($("#users").val())) {
                    popup.mould.popTipsMould(false, "请选择要被分配的人！", popup.mould.first, popup.mould.error, "", "57%", null);
                    return;
                }
                var assigners = $("#users").val().join(",");
                redistribution.interface.averageAssign(assigners);
            } else if (assignType == "2") {
                redistribution.operation.customAssign();
            }
            $("#submitBtn").attr("disabled", false);
        },
        customAssign: function () {
            var assignerNumMap = new Map();
            var flag = true;
            $(".assign_num_input").each(function (index) {
                var rowNum = index + 1;
                var $textInput = $(this);
                var inputValue = $textInput.val();
                var $selectInput = $textInput.parent().next().find("select");
                var selectValue = $selectInput.val();
                if (!common.isEmpty(selectValue) && !common.isEmpty(inputValue)) {
                    assignerNumMap.put(selectValue, inputValue);
                } else {
                    if (common.isEmpty(selectValue) && common.isEmpty(inputValue)) {
                        console.log("数据被丢弃");
                    } else {
                        flag = false;
                        popup.mould.popTipsMould(false, "第" + rowNum + "行数据未填写完整,请清空改或补全该行数据", popup.mould.first, popup.mould.error, "", "57%", null);
                    }
                }
            });
            if (!flag) {
                return false;
            }
            redistribution.interface.customAssign(assignerNumMap);
        },
        assignSuccessCallBack: function (resultMap) {
            $(".assign-form-value").val("");
            $(".assign-form-checkbox").attr("checked", false);
            $('.assign-form-select').multiselect('clearSelection');
            $("#resultDiv").hide();
            //刷新当前用户的待分配信息
            if (resultMap.result == "success") {
                window.scrollTo(0, 0);
                $("#currentUserName").text(resultMap.userName);
                $("#assignNum").text(resultMap.assignNum);
                popup.mould.popTipsMould(false, "分配成功！", popup.mould.first, popup.mould.success, "", "57%", null);
            }
        }

    },
    interface: {
        averageAssign: function (assigners) {
            var param = redistribution.operation.getSearchParams();
            param.operatorIds = assigners;
            $("#submitBtn").attr("disabled", true);
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/assign/average", param,
                function (resultMap) {
                    redistribution.operation.assignSuccessCallBack(resultMap);
                }, function () {
                    popup.mould.popTipsMould(false, "分配失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                });
        },
        customAssign: function (assignerNumMap) {
            if (assignerNumMap.keys.length == 0) {
                popup.mould.popTipsMould(false, "请选择被分配人并填写对应的数量!", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            var param = redistribution.operation.getSearchParams();

            var operatorIds = assignerNumMap.keys.join(",");
            param.operatorIds = operatorIds;
            var valueArray = new Array();
            $.each(assignerNumMap.keys, function (index, key) {
                valueArray.push(assignerNumMap.get(key));
            });
            var operatorAssignNums = valueArray.join(",");
            param.operatorAssignNums = operatorAssignNums;
            $("#submitBtn").attr("disabled", true);
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/assign/custom", param,
                function (resultMap) {
                    redistribution.operation.assignSuccessCallBack(resultMap);
                }, function () {
                    popup.mould.popTipsMould(false, "分配失败!", popup.mould.first, popup.mould.error, "", "57%", null);
                });
        },
        getUserAssignInfo: function () {//获取当前登录用户名下的待分配信息
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/redistribution/userAssignInfo",
                {},
                function (data) {
                    $("#currentUserName").text(data.userName);
                    $("#assignNum").text(data.assignNum);
                    return true;
                }),
                function () {
                }
        },
        getOldOperatorList: function (successCallBack, failedCallBack) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/internalUser/getAllEnableTelCommissioner", null, successCallBack, failedCallBack);
        },
        paramSearch: function (param, successCallBack, failedCallBack) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/redistribution/param/list", param, successCallBack, failedCallBack);
        }
    }

};

$(document).ready(function() {
    $('.oldOperatorList').select2();
    /*$('#channelSel').select2();
    $('#areaSel').select2();*/
});
