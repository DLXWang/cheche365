/**
 * Created by cxy on 2017/6/8.
 */

var createUrlDataFunction = {
    "data": function (data) {
        data.scope = $('#search_scope').val();
        data.source = $('#search_source').val();
        data.plan = $('#search_plan').val();
        data.unit = $('#search_unit').val();
        data.keyword = $('#search_keyword').val();
        data.url = $('#search_url').val();
        data.startTimeStr = $('#startTime').val();
        data.endTimeStr = $('#endTime').val();
    },
    "fnRowCallback": function (nRow, aData) {

    },
}
var urlList = {
    "url": '/operationcenter/dataStatistics/createUrl/urlList',
    "type": "GET",
    "table_id": "url_list",
    "columns": [
        {
            "data": "url",
            "title": "网址",
            "sClass": "text-center",
            "orderable": false,
            "render": function (data) {
                var reg = /((https|http|ftp|rtsp|mms):\/\/)?(([0-9a-z_!~*'().&=+$%-]+:)?[0-9a-z_!~*'().&=+$%-]+@)?(([0-9]{1,3}\.){3}[0-9]{1,3}|([0-9a-z_!~*'()-]+\.)*([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\.[a-z]{2,6})(:[0-9]{1,4})?((\/?)|(\/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+\/?)/g;
                domain = data.match(reg);
                return ((domain != null && domain.length > 0) ? domain[0] : "");
            }
        },
        {"data": "scope", "title": "岗位", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "source", "title": "渠道", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "plan", "title": "计划", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "unit", "title": "单元", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "keyword", "title": "关键词", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "createTime", "title": "生成时间", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "url", "title": "生成的链接", 'sClass': "text-center", "orderable": false, "sWidth": ""},
        {"data": "referralLink", "title": "推广链接", "sClass": "text-center", "orderable": false},
        {"data": "quote", "title": "可报价", "sClass": "text-center", "orderable": false, "render": function (data) {
            return data?"是":"否";
        }
        },
        {
            "data": "id",
            "title": "操作",
            "sClass": "text-center",
            "sWidth": "30px",
            "orderable": false,
            "render": function (data) {
                if(common.permission.getPermissionCodeArray().indexOf("op070201")>-1){
                    return "<a href='javascript:;' onclick = create_url.delBtn(" + data + ")>删除</a><br/>";
                }else{
                    return "";
                }
            }
        },
    ],
}
var create_url = {
    url_content: '',
    initUrlContent: function () {
        var detailContent = $("#url_content");
        if (detailContent.length > 0) {
            create_url.url_content = detailContent.html();
            detailContent.remove();
        }
    },
    initUrlPath: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/dataStatistics/template/url", null, function (response) {
            window.parent.$("#url_template").prop("href", response.message);
        }, function () {
            popup.mould.popTipsMould("模版地址初始化异常！！", popup.mould.first, popup.mould.error, "", "53%", null);
        });

    },
    searchBtn: function () {
        window.parent.$("#searchBtn").unbind("click").bind({
            click: function () {
                datatables.ajax.reload();
            }
        });
    },
    createBtn: function () {
        window.parent.$("#createUrlBtn").unbind("click").bind({
            click: function () {
                popup.pop.popInput(create_url.url_content, 'first', "500px", "490px", "35%", "55%");
                window.parent.$("#toSave").unbind("click").bind({
                    click: function () {
                        if (!common.checkInput('checkInput')) {
                            create_url.error("请填全信息");
                            return false;
                        }
                        common.getByAjax(true, "post", "json", "/operationcenter/dataStatistics/createUrl/add", window.parent.$("#add_form").serialize(),
                            function (data) {
                                if (data.pass) {
                                    popup.mould.popTipsMould("保存成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                                    datatables.ajax.reload();
                                } else {
                                    create_url.error("发生异常,data返回异常,可能是推广链接格式不对");
                                }
                            },
                            function () {
                                popup.mould.popTipsMould("发生异常,获取不到data,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                            }
                        );
                    }
                });
                window.parent.$(".close").unbind("click").bind({
                    click: function () {
                        popup.mask.hideFirstMask(false);
                    }
                });
            }
        });
    },
    delBtn: function (id) {
        popup.mould.popConfirmMould("确定删除链接？", popup.mould.first, "", "",
            function () {
                common.getByAjax(true, "put", "json", "/operationcenter/dataStatistics/createUrl/del/" + id, null,
                    function (data) {
                        popup.mask.hideFirstMask(false);
                        if (data.pass) {
                            datatables.ajax.reload();
                        } else {
                            popup.mould.popTipsMould("删除数据出现异常！", popup.mould.second, popup.mould.error, "", "57%", null);
                        }
                    },
                    function () {
                        popup.mould.popTipsMould("删除数据出现异常！", popup.mould.second, popup.mould.error, "", "57%", null);
                    }
                );
            },
            function () {
                popup.mask.hideFirstMask(false);
            }
        );
    },

    toSave: function () {
        window.parent.$("#toSave").unbind("click").bind({
            click: function () {
                if (!common.checkInput('checkInput')) {
                    popup.mould.popTipsMould("请填全信息", popup.mould.second, popup.mould.error, "", "57%", null);
                    return false;
                }
                if(!common.checkUrl($("#url").val())){
                    popup.mould.popTipsMould("请输入正确的网址", popup.mould.second, popup.mould.warning, "", "57%", null);
                    return false;
                }
                common.getByAjax(true, "post", "json", "/operationcenter/dataStatistics/createUrl/add", window.parent.$("#add_form").serialize(),
                    function (data) {
                        if (data.pass) {
                            popup.mould.popTipsMould("保存成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                            datatables.ajax.reload();
                            $(".checkInput").each(function () {
                                this.value = "";
                            });
                        } else {
                            create_url.error("发生异常,data返回异常,可能是推广链接格式不对");
                        }
                    },
                    function () {
                        popup.mould.popTipsMould("发生异常,获取不到data,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                    }
                );
            }
        });
    },
    uploadExl: function () {
        window.parent.$("#uploadExl").unbind("click").bind({
            click: function () {
                var formData = new FormData();
                var files = window.parent.$("#codeFile").val();
                if (!files) {
                    popup.mould.popTipsMould("请先选择文件！", popup.mould.second, popup.mould.warning, "", "57%", null);
                    return false;
                }
                formData.append("codeFile", window.parent.$("#codeFile")[0].files[0]);
                $.ajax({
                    url: "/operationcenter/dataStatistics/upload",
                    type: 'POST',
                    data: formData,
                    // 告诉jQuery不要去处理发送的数据
                    processData: false,
                    // 告诉jQuery不要去设置Content-Type请求头
                    contentType: false,
                    beforeSend: function () {
                    },
                    success: function (responseStr) {
                        if (responseStr == 'success') {
                            window.parent.$("#codeFileFake").val('');
                            window.parent.$("#codeFile").val('');
                            create_url.changeValue();
                            popup.mould.popTipsMould("上传成功！", popup.mould.second, popup.mould.success, "", "57%", null);
                            datatables.ajax.reload();
                        } else {
                            window.parent.$("#codeFileFake").val('');
                            window.parent.$("#codeFile").val('');
                            create_url.changeValue();
                            popup.mould.popTipsMould(responseStr, popup.mould.second, popup.mould.error, "", "57%", null);
                        }
                    },
                    error: function (responseStr) {
                        popup.mould.popTipsMould("上传失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                    }
                });
            }
        });
    },
    changeValue: function () {
        var fakePath = $("#codeFile").val();
        if (fakePath) {
            window.parent.$('#uploadExl').prop('disabled', false);
        } else {
            window.parent.$('#uploadExl').prop('disabled', true);
        }
        var index = fakePath.lastIndexOf("\\");
        var name = fakePath.substring(index + 1);
        $("#codeFileFake").val(name);
    }
}

var datatables;
$(function () {
    if (!common.permission.validUserPermission("op0701")) {
        return;
    }
    datatables = datatableUtil.getByDatatables(urlList, createUrlDataFunction.data, createUrlDataFunction.fnRowCallback);
    create_url.initUrlContent();
    create_url.initUrlPath();
    create_url.searchBtn();
    create_url.toSave();
    create_url.uploadExl();
});
