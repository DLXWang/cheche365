var parent = window.parent;
var task = {
    properties: new Properties(1, ""),
    list: function () {
        common.ajax.getByAjax(true, "get", "json", "/admin/task/importMarketingSuccessData/list",
            {
                currentPage: task.properties.currentPage,
                pageSize: task.properties.pageSize
            },
            function (data) {
                CUI.grid.store = data;
                CUI.grid.properties = task.properties,
                    CUI.grid.dom = $("#list_tab tbody");
                CUI.grid.columns = [
                    {dataIndex: 'id'},
                    {dataIndex: 'marketingName'},
                    {dataIndex: 'marketingCode'},
                    {dataIndex: 'cacheKey'},

                    {dataIndex: 'source.name'},
                    {dataIndex: 'channelName'},
                    {
                        dataIndex: 'enable', renderer: function (value, rowIndex, rowStroe) {
                        return value ? "<span style='color:green;'>启用</span>" : "<span style='color:red;'>禁用</span>"
                    }
                    },
                    //{dataIndex: 'priority'},
                    {
                        dataIndex: '', renderer: function (value, rowIndex, rowStore) {
                        var result = "";

                        if (rowStore.enable) {
                            result = "<a  href=\"javascript:;\" onclick='task.operate.status(" + rowStore.id + ");'>禁用</a>"
                        } else {
                            result = "<a  href=\"javascript:;\" onclick='task.operate.status(" + rowStore.id + ");'>启用</a>"
                        }
                        return result += "&nbsp;&nbsp;<a href=\"javascript:;\" onclick='task.popup.show(" + rowStore.id + ");' >编辑</a>"
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
        },
        show: function (id) {
            popup.pop.popInput(task.popup.detailContent, popup.mould.first, "520px", "380px", "40%", "55%");
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
        fixContent: function (id) {
            if (id != null) {
                task.operate.findOne(id, function (data) {
                    parent.$("#id").val(data.id);
                    task.operate.source(data.source.id);
                    task.operate.channel(data.channelId);
                    parent.$("#marketing").val(data.marketingCode);
                    //parent.$("#priority").val(data.priority);
                    parent.$("#cacheKey").val(data.cacheKey);
                });
            } else {
                task.operate.source(null);
                task.operate.channel(null);
                task.operate.sourceType(null);
            }

        }
    },
    operate: {
        save: function () {
            if(!task.operate.validate()){
                return;
            }
            common.ajax.getByAjax(true, "post", "json", "/admin/task/importMarketingSuccessData/save",
                {
                    id: parent.$("#id").val(),
                    marketing: parent.$("#marketing").val(),
                    source: parent.$("#source").val(),
                    channel: parent.$("#channel").val(),
                    priority: 1,
                    cacheKey: parent.$("#cacheKey").val()
                },
                function (data) {
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("保存成功！", popup.mould.first, popup.mould.success, "", "57%",
                            function() {
                                popup.mask.hideFirstMask();
                                task.list();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.first, popup.mould.error, "", "56%", null);
                    }
                },
                function () {
                }
            );
        },
        validate:function(){
            if(common.validation.isEmpty(parent.$("#marketing").val())){
                task.operate.error("活动Code不能为空！");
                return false;
            }
            if(common.validation.isEmpty(parent.$("#source").val())){
                task.operate.error("请选择电销来源！");
                return false;
            }
            //if(common.validation.isEmpty(parent.$("#priority").val())){
            //    task.operate.error("请设置优先级！");
            //    return false;
            //}
            if(common.validation.isEmpty(parent.$("#cacheKey").val())){
                task.operate.error("请设置缓存key！");
                return false;
            }
            if(common.validation.isEmpty(parent.$("#channel").val())){
                task.operate.error("请选择渠道！");
                return false;
            }
            return true;
        },
        error:function(msg){
            parent.$("#errorText").html(msg);
            parent.$(".error-msg").show().delay(2000).hide(0);
        },
        status: function (id) {
            common.ajax.getByAjax(true, "put", "json", "/admin/task/importMarketingSuccessData/enable/" + id, {},
                function (data) {
                    if (data.pass) {
                        task.list();
                    } else {
                        popup.mould.popTipsMould("设置状态失败！", popup.mould.first, popup.mould.error, "", "56%", null);
                    }
                },
                function () {
                }
            );
        },
        findOne: function (id, callbackMethod) {
            common.ajax.getByAjax(true, "get", "json", "/admin/task/importMarketingSuccessData/" + id, {},
                function (data) {
                    if (callbackMethod) {
                        callbackMethod(data);
                    }
                },
                function () {
                }
            );
        },
        sourceType: function (typeId) {
            common.ajax.getByAjax(true, "get", "json", "/admin/task/importMarketingSuccessData/sourceType", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            if (typeId != null && model.id == typeId) {
                                options += "<option value=\"" + model.id + "\" selected>" + model.name + "</option>";
                            } else {
                                options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                            }
                        });
                        parent.$("#sourceType").append(options);
                    }
                },
                function () {
                }
            );
        },
        source: function (sourceId) {
            common.ajax.getByAjax(true, "get", "json", "/admin/task/importMarketingSuccessData/source", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            if (sourceId != null && model.id == sourceId) {
                                options += "<option value=\"" + model.id + "\" selected>" + model.description + "</option>";
                            } else {
                                options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                            }
                        });
                        parent.$("#source").append(options);
                    }
                },
                function () {
                }
            );
        },
        channel: function (channelId) {
            common.ajax.getByAjax(true, "get", "json", "/admin/task/importMarketingSuccessData/channelList", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            if (channelId != null && model.id == channelId) {
                                options += "<option value=\"" + model.id + "\" selected>" + model.description + "</option>";
                            } else {
                                options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                            }
                        });
                        parent.$("#channel").append(options);
                    }
                },
                function () {
                }
            );
        }
    }
}

$(function () {
    task.list();
    task.popup.init();
    $("#toNew").bind({
        click: function () {
            task.popup.show(null);
        }

    })
})
