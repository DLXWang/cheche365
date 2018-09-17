/**
 * Created by wangfei on 2015/5/26.
 */
var dataFunction = {
    "data": function (data) {
        data.keyword = $("#keyword").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $roleName = common.tools.getCommentMould(common.tools.checkToEmpty(aData.roleName), 15);
        $id = "<a href='javascript:;' onclick=user.edit('" + aData.id + "');>编辑</a>";
        $('td:eq(2)', nRow).html($roleName);
        $('td:eq(6)', nRow).html($id);
    }
};
/* 内部用户列表查询 */
var list = {
    "url": '/orderCenter/user/list',
    "type": "GET",
    "table_id": "user_tab",
    "columns": [
        {"data": "name", "title": "姓名", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "email", "title": "邮箱", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "角色", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "mobile", "title": "手机号", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": "createTime", "title": "创建时间", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": "updateTime", "title": "最后操作时间", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
    ]
};

var User = function () {
    /* 初始化角色 */
    this.initEnableRoles = function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/roles/enable", {},
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                    });
                    $("#roleId").append(options);
                }
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    };
    /* 初始化角色 */
    this.initAllRoles = function () {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/roles/oc/enable", {},
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                    });
                    $("#roleId").append(options);
                    $("#roleId").multiselect({
                        nonSelectedText: '请选择角色',
                        buttonWidth: '200',
                        maxHeight: '180',
                        includeSelectAllOption: true,
                        selectAllNumber: false,
                        selectAllText: '全部',
                        allSelectedText: '全部'
                    });
                }
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    };
    /* 增加 */
    this.save = function () {
        $("#register_button").attr("disabled", true);
        common.getByAjax(true, "get", "json", "/orderCenter/user/add", $("#userForm").serialize(),
            function (data) {
                if (data.result == "success") {
                    common.showTips("保存成功");
                } else {
                    common.showTips(data.message);
                }
                $("#userForm")[0].reset();
                $("#register_button").attr("disabled", false);
            }, function () {
                common.showTips("系统异常");
                $("#register_button").attr("disabled", false);
            }
        )
    };

    /* 删除 */
    this.delete = function (id) {
        $("#delete_button").attr("disabled", true);
        common.getByAjax(true, "get", "", "/orderCenter/user/delete", {id: id},
            function (data) {
                if (data.result == "success") {
                    common.showTips("删除成功");
                    datatables.ajax.reload();
                    showContent();
                    $("#userForm")[0].reset();
                } else {
                    common.showTips("删除失败");
                }

                $("#delete_button").attr("disabled", false);
            }, function () {
                common.showTips("系统异常");
                $("#delete_button").attr("disabled", true);
            }
        );
    };

    /* 更新 */
    this.update = function () {
        $("#update_button").attr("disabled", true);
        common.getByAjax(true, "get", "", "/orderCenter/user/update", $("#userForm").serialize(),
            function (data) {
                if (data.result == "success") {
                    common.showTips("更新成功");
                    datatables.ajax.reload();
                    showContent();
                    $("#userForm")[0].reset();
                } else {
                    common.showTips(data.message);
                }
                $("#update_button").attr("disabled", false);
            }, function () {
                common.showTips("系统异常");
                $("#update_button").attr("disabled", false);
            }
        );
    };

    /* 编辑 */
    this.edit = function (id) {
        showEdit();
        common.getByAjax(true, "get", "json", "/orderCenter/user/findOne", {id: id},
            function (data) {
                if (data == null || data.result == "fail") {
                    common.showTips("获取用户信息失败");
                    return false;
                }

                $("#email").val(data.objectMap.model.email);
                $("#name").val(data.objectMap.model.name);
                $("#mobile").val(data.objectMap.model.mobile);
                if (data.objectMap.model.gender == 1) {
                    $("#gender1").attr("checked", 'checked');
                } else {
                    $("#gender2").attr("checked", 'checked');
                }
                if (data.objectMap.model.roleIds) {
                    $('#roleId').multiselect('select', data.objectMap.model.roleIds.split(","));
                }
                $("#id").val(id);
            }, function () {
                common.showTips("系统异常");
            }
        );
    };

    /* 配置用户组 */
    this.addGroup = function () {
        $("#save_button").attr("disable", true);
        common.getByAjax(true, "get", "json", "/orderCenter/relation/add",
            {
                customerUserId: $("#customerSel").val(),
                internalUserId: $("#internalSel").val(),
                externalUserId: $("#externalSel").val()
            },
            function (data) {
                if (data) {
                    common.showTips("配置成功");
                    $("#relationForm")[0].reset();
                } else {
                    common.showTips("配置失败");
                }
                $("#save_button").attr("disable", false);
            },
            function () {
                common.showTips("系统异常");
                $("#save_button").attr("disable", false);
            }
        );
    };

    /* 更新用户组 */
    this.updateGroup = function (properties) {
        $("#update_button").attr("disabled", true);
        common.getByAjax(true, "get", "json", "/orderCenter/relation/update",
            {
                id: $("#id").val(),
                customerUserId: $("#customerSel").val(),
                internalUserId: $("#internalSel").val(),
                externalUserId: $("#externalSel").val()
            },
            function (data) {
                if (data.result == "success") {
                    common.showTips("更新成功");
                    user.listGroup(properties);
                    showContent();
                    $("#relationForm")[0].reset();
                } else {
                    common.showTips(data.message);
                }
                $("#update_button").attr("disabled", false);
            },
            function () {
                common.showTips("系统异常");
                $("#update_button").attr("disable", false);
            }
        );
    };

    /* 删除用户组 */
    this.deleteGroup = function (id, properties) {
        $("#delete_button").attr("disabled", true);
        common.getByAjax(true, "get", "", "/orderCenter/relation/delete", {id: id},
            function (data) {
                if (data.result == "success") {
                    common.showTips("删除成功");
                    user.listGroup(properties);
                    showContent();
                    $("#relationForm")[0].reset();
                } else {
                    common.showTips("删除失败");
                }

                $("#delete_button").attr("disabled", false);
            }, function () {
                common.showTips("系统异常");
                $("#delete_button").attr("disabled", true);
            }
        );
    };

    /* 内部用户组列表查询 */
    this.listGroup = function (properties) {
        common.getByAjax(true, "get", "json", "/orderCenter/relation/list",
            {
                currentPage: properties.currentPage,
                pageSize: properties.pageSize,
                keyword: properties.keyword
            },
            function (data) {
                $("#relation_tab tbody").empty();

                if (data == null) {
                    common.showTips("获取用户组列表失败");
                    return false;
                }

                if (data.pageInfo.totalElements < 1) {
                    $("#totalCount").text("0");
                    $(".customer-pagination").hide();
                    return false;
                }

                $("#totalCount").text(data.pageInfo.totalElements);
                $("#pageUl").empty();
                if (data.pageInfo.totalPage > 1) {
                    $.jqPaginator('.pagination',
                        {
                            totalPages: data.pageInfo.totalPage,
                            visiblePages: properties.visiblePages,
                            currentPage: properties.currentPage,
                            onPageChange: function (pageNum, pageType) {
                                if (pageType == "change") {
                                    properties.currentPage = pageNum;
                                    user.listGroup(properties);
                                }
                            }
                        }
                    );
                } else {
                    $(".customer-pagination").hide();
                }

                var content = "";
                $.each(data.viewList, function (n, relation) {
                    content += "<tr class='text-center'>" +
                        "<td>" + common.checkToEmpty(relation.customerUserName) + "</td>" +
                        "<td>" + common.checkToEmpty(relation.internalUserName) + "</td>" +
                        "<td>" + common.checkToEmpty(relation.externalUserName) + "</td>" +
                        "<td>" + "<a href='javascript:;' onclick=user.editGroup('" + relation.id + "');>编辑</a></td>" +
                        "</tr>";
                });

                $("#relation_tab tbody").append(content);
                window.parent.scrollTo(0, 0);
            }, function () {
                common.showTips("系统异常");
            }
        );
    };

    /* 编辑用户组 */
    this.editGroup = function (id) {
        showEdit();
        $("#customerSel").empty();
        $("#internalSel").empty();
        $("#externalSel").empty();
        common.getByAjax(true, "get", "json", "/orderCenter/user/findAllRoleUsers", null,
            function (data) {
                if (data.customerOptions != null && data.customerOptions != "") {
                    $("#customerSel").append(data.customerOptions);
                }
                if (data.internalOptions != null && data.internalOptions != "") {
                    $("#internalSel").append(data.internalOptions);
                }
                if (data.externalOptions != null && data.externalOptions != "") {
                    $("#externalSel").append(data.externalOptions);
                }
                common.getByAjax(true, "get", "json", "/orderCenter/relation/findOne", {id: id},
                    function (data) {
                        if (data == null || data.result == "fail") {
                            common.showTips("获取用户组信息失败");
                            return false;
                        }
                        $("#customerSel").val(data.objectMap.model.customerUserId);
                        $("#internalSel").val(data.objectMap.model.internalUserId);
                        $("#externalSel").val(data.objectMap.model.externalUserId);
                        $("#id").val(id);
                    }, function () {
                        common.showTips("系统异常");
                    }
                );
            },
            function () {
                common.showTips("获取列表信息失败");
            }
        );
    }
}

User.prototype = new Action();
var user = new User();
