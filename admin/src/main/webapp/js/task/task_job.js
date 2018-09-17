var parent = window.parent;
var task = {
    properties: new Properties(1, ""),
    list: function () {
        //common.ajax.getByAjax(true, "get", "json", "/operationcenter/task/importMarketingSuccessData/list",
        common.ajax.getByAjax(true, "get", "json", "/admin/task/list",
            {
                jobName: $('#job_name_search').val(),
                status: $('#status_search').val(),
                currentPage: task.properties.currentPage,
                pageSize: task.properties.pageSize
            },
            function (data) {
                CUI.grid.store = data;
                CUI.grid.properties = task.properties;
                CUI.grid.dom = $("#list_tab tbody");
                CUI.grid.columns = [
                    {dataIndex: 'id'},
                    {dataIndex: 'jobName'},
                    //{dataIndex: 'jobClass'},
                    {dataIndex: 'jobCronExpression'},
                    {
                        dataIndex: 'status', renderer: function (value, rowIndex, rowStroe) {
                        return value ? "<span style='color:green;'>启用</span>" : "<span style='color:red;'>停用</span>"
                         }
                    },
                    {dataIndex: 'runningStatus'},
                    {dataIndex: 'previousTime'},
                    {dataIndex: 'nextTime'},
                    {dataIndex: 'createTime'},
                    {dataIndex: 'updateTime'},
                    {dataIndex: 'operator'},
                    {
                        dataIndex: '', renderer: function (value, rowIndex, rowStore) {
                        var result = "";
                        //if (rowStore.status) {
                        //    result = "<a  href=\"javascript:;\" onclick='task.operate.status(" + rowStore.id + ");'>禁用</a>"
                        //} else {
                        //    result = "<a  href=\"javascript:;\" onclick='task.operate.status(" + rowStore.id + ");'>启用</a>"
                        //}
                        return result += "<a href=\"javascript:;\" onclick='task.popup.show(" + rowStore.id + ");' >编辑</a>&nbsp;<a href=\"javascript:;\" onclick='task.popup.showDetail(" + rowStore.id + ");' >执行记录</a>"
                    }
                    }
                ];

                CUI.grid.result = {
                    callback: function (result) {
                        if (result == CUI.grid.results.success) {
                            $(".detail-together").show();
                        }
                        if (result == CUI.grid.results.notFound) {
                            $(".detail-together").hide();
                        }
                    }
                };
                CUI.grid.fill();
                CUI.grid.page(function (load) {
                    if (load) {
                        task.list();
                    }
                });

            }, function () {
                popup.mould.popTipsMould("获取任务列表失败！", popup.mould.first, popup.mould.error, "", "56%", null);
            }
        )

    },
    popup: {
        init: function () {
            var detailContent = $("#popover_normal_input");
            if (detailContent.length > 0) {
                task.popup.detailContent = detailContent.html();
                detailContent.remove();
            }
            var recordContent = $("#popover_record_input");
            if (recordContent.length > 0) {
                task.popup.recordContent = recordContent.html();
                recordContent.remove();
            }
            var redisContent = $("#redis_input");
            if (redisContent.length > 0) {
                task.popup.redisContent = redisContent.html();
                redisContent.remove();
            }
        },
        show: function (id) {
            popup.pop.popInput(task.popup.detailContent, popup.mould.first, "800px", "550px", "40%", "47%");
            parent.$("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            parent.$("#toCreate").unbind("click").bind({
                click: function () {
                    task.operate.save();
                }
            });
            task.popup.fixContent(id);
        },
        showRedis: function () {
            popup.pop.popInput(task.popup.redisContent, popup.mould.first, "800px", "216px", "40%", "47%");
            parent.$("#redis_close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            parent.$("#toUpdateRedis").unbind("click").bind({
                click: function () {
                    task.operate.editRedis();
                }
            });
            $("#redisKey").blur(function(){
                var redisKey = parent.$("#redisKey").val();
                if(redisKey != "")
                    common.ajax.getByAjax(true, "get", "text", "/admin/task/findRedis",
                        {
                            redisKey: redisKey
                        },
                        function (data) {
                            parent.$("#redisValue").val(data);
                        },
                        function () {
                        }
                    );
            });
        },
        showDetail: function(id){
            popup.pop.popInput(task.popup.recordContent, popup.mould.first, "800px", "650px", "40%", "47%");
            parent.$("#record_detail_close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            var list = {
                recordList: {
                    "url": '/admin/task/findJobDetailByJob',
                    "type": "GET",
                    "table_id": "record_tab",
                    "columns": [
                        {"data": null, "title": "开始时间", 'sClass': "text-center", "orderable": false,"sWidth":"140px"},
                        {"data": null, "title": "结束时间", 'sClass': "text-center", "orderable": false,"sWidth":"140px"},
                        {"data": null, "title": "执行时长", 'sClass': "text-center", "orderable": false,"sWidth":"70x"},
                        /*{"data": null, "title": "记录条数", 'sClass': "text-center", "orderable": false,"sWidth":"100px"},*/
                        {"data": null, "title": "状态", 'sClass': "text-center", "orderable": false,"sWidth":"100px"},
                    ]
                }
            };
            var dataFunction = {
                recordDataFunction : {
                    "data": function (data) {
                        data.taskJobId = id;
                    },
                    "fnRowCallback": function (nRow, aData) {
                        $startTime = common.tools.checkToEmpty(aData.startTime);
                        $endTime = common.tools.checkToEmpty(aData.endTime);
                        $executeDuration = common.tools.checkToEmpty(aData.executeDuration);
                        /*$recordAmount = common.tools.checkToEmpty(aData.recordAmount);*/
                        $status = common.tools.checkToEmpty(aData.status);
                        $('td:eq(0)', nRow).html($startTime);
                        $('td:eq(1)', nRow).html($endTime);
                        $('td:eq(2)', nRow).html($executeDuration);
                        /*$('td:eq(3)', nRow).html($recordAmount);*/
                        $('td:eq(3)', nRow).html($status);
                    }
                }
            };
            var recordDatatables = datatableUtil.getByDatatables(list.recordList, dataFunction.recordDataFunction.data, dataFunction.recordDataFunction.fnRowCallback);
        },
        fixContent: function (id) {
            if (id != null) {
                task.operate.findOne(id, function (data) {
                    parent.$("#id").val(data.id);
                    parent.$("#jobName").val(data.jobName);
                    parent.$("#jobClass").val(data.jobClass);
                    parent.$("#jobCronExpression").val(data.jobCronExpression);
                    parent.$("#paramKey1").val(data.paramKey1);
                    parent.$("#paramValue1").val(data.paramValue1);
                    parent.$("#paramKey2").val(data.paramKey2);
                    parent.$("#paramValue2").val(data.paramValue2);
                    parent.$("#paramKey3").val(data.paramKey3);
                    parent.$("#paramValue3").val(data.paramValue3);
                    parent.$("#status").val(data.status?1:0);
                    parent.$("#comment").val(data.comment);
                   // parent.$("#jobClass").attr({readonly: 'true' });
                });
            }
        }
    },
    operate: {
        save: function () {
            if(!task.validate()){
                return;
            }
            common.ajax.getByAjax(true, "post", "json", "/admin/task/update",parent.$("#job_form").serialize(),
                function (data) {
                    if (data.pass) {
                        popup.mould.popTipsMould("保存成功！", popup.mould.second, popup.mould.success, "", "57%",
                            function() {
                                popup.mask.hideAllMask();
                                task.list();
                            }
                        );
                    } else {
                        task.error(data.message);
                    }
                },
                function () {
                }
            );
        },
        editRedis: function(){
            var redisKey = parent.$("#redisKey").val();
            var redisVal = parent.$("#redisValue").val();
            if(redisKey && redisVal)
                common.ajax.getByAjax(true, "post", "json", "/admin/task/updateRedis",
                    {
                        redisKey: redisKey,
                        redisValue: redisVal
                    },
                    function (data) {
                        if (data.pass) {
                            popup.mould.popTipsMould("保存成功！", popup.mould.second, popup.mould.success, "", "57%",
                                function() {
                                    popup.mask.hideAllMask();
                                }
                            );
                        } else {
                            task.error(data.message);
                        }
                    },
                    function () {
                    }
                );
            else
                task.error("key和value不能为空");
        },
        findOne: function (id, callbackMethod) {
            common.ajax.getByAjax(true, "get", "json", "/admin/task/" + id, {},
                function (data) {
                    if (callbackMethod) {
                        callbackMethod(data);
                    }
                },
                function () {
                }
            );
        },

    },
    validate:function(){
        if(common.validation.isEmpty(parent.$("#jobName").val())){
            task.error("任务名称不能为空");
            return false;
        }
        if(common.validation.isEmpty(parent.$("#jobClass").val())){
            task.error("任务名称不能为空");
            return false;
        }
        if(common.validation.isEmpty(parent.$("#jobCronExpression").val())){
            task.error("任务名称不能为空");
            return false;
        }
        return true;
    },error: function (msg) {
        parent.$("#errorText").html(msg);
        parent.$(".error-msg").show().delay(2000).hide(0);
    }

}

$(function () {
    task.list();
    task.popup.init();
    $("#toNew").bind({
        click: function () {
            task.popup.show(null);
        }
    });
    $("#reset").bind({
        click: function () {
            common.ajax.getByAjax(true, "get", "json", "/admin/task/reset",{},
                function (data) {
                    if (data.pass) {
                        popup.mould.popTipsMould("同步成功！", popup.mould.first, popup.mould.success, "", "57%",
                            function() {
                                popup.mask.hideFirstMask()
                                task.list();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould("同步失败！", popup.mould.first, popup.mould.success, "", "57%",
                            function() {
                                popup.mask.hideFirstMask();
                            }
                        );
                    }
                },
                function () {
                }
            );
        }
    });
    $("#updateRedis").bind({
        click: function () {
            task.popup.showRedis();
        }
    })
    $("#search").bind({
        click: function () {
            task.properties.currentPage = 1;
            task.list();
        }
    })
})
