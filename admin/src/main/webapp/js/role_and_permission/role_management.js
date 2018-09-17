/**
 * Created by wangfei on 2015/9/6.
 */
var role = {
    newContent: "",//角色编辑内容
    permissionContent: "",//权限编辑内容
    page: new Properties(1, ""),//分页
    showMark: "check",//查看权限标志
    editMark: "edit",//编辑权限标志
    validation: function() {
        var errorText = $("#popover_normal_input .error-line #errorText");
        var name = $("#popover_normal_input #new_form #name").val();
        if (common.validation.isEmpty(name)) {
            errorText.text("请输入角色名称");
            errorText.parent().parent().show();
            return false;
        }
        if (name.length > 10) {
            errorText.text("请输入角色名称");
            errorText.parent().parent().show();
            return false;
        }
        return true;
    },
    initRole: {
        initPopupContent: function() {
            var newContent = $("#new_content");
            if (newContent.length > 0) {
                role.newContent = newContent.html();
                newContent.remove();
            }

            var permissionContent = $("#role_permission_content");
            if (permissionContent.length > 0) {
                role.permissionContent = permissionContent.html();
                permissionContent.remove();
            }
        },
        init: function() {
            role.menu.initMenuSel();
        }
    },
    newRole: {
        popup: function() {
            role.initRole.initPopupContent();
            popup.pop.popInput(role.newContent, popup.mould.first, "496px", "auto", "44%", "57%");
            //超级管理员对角色类型可见
            if (common.permission.isSuperMan()) {
                $("#popover_normal_input").find("#roleLevelDiv").show();
            } else {
                $("#popover_normal_input").find("#roleLevelDiv").hide();
            }
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function() {
                    popup.mask.hideFirstMask();
                }
            });
            $("#popover_normal_input .btn-finish .toCreate").unbind("click").bind({
                click: function() {
                    $("#popover_normal_input .error-line #errorText").parent().parent().hide();
                    if (!role.validation()) {
                        return;
                    }
                    role.newRole.saveRole();
                }
            });
            $("#popover_normal_input .form-input-top #role_permission_btn").unbind("click").bind({
                click: function() {
                    popup.pop.popInput(role.permissionContent, popup.mould.second, "850px", "550px", "36%", "48%");
                    role.permission.clearPermissionArray();
                    role.permission.setOptionsChk($("#popover_normal_input #permissions").val(), role.editMark);
                    $("#popover_normal_input_second .theme_poptit .close").unbind("click").bind({
                        click: function() {
                            popup.mask.hideSecondMask();
                        }
                    });
                    //超级管理员列出特殊权限
                    if (common.permission.isSuperMan()) {
                        $("#popover_normal_input_second .permission-div").css("height", "58%");
                        $("#popover_normal_input_second #showSpecialPermissionBtnDiv").show();
                        $("#popover_normal_input_second .form-input-top #showSpecialPermissionBtn").unbind("click").bind({
                            click: function() {
                                role.menu.restoreMenu(popup.mould.second);
                                role.menu.showSpecialPermissions(role.editMark);
                            }
                        });
                    } else {
                        $("#popover_normal_input_second .permission-div").css("height", "63%");
                        $("#popover_normal_input_second #showSpecialPermissionBtnDiv").remove();
                    }
                    $("#popover_normal_input_second .form-input-top #level_1_select").unbind("change").bind({
                        change: function() {
                            role.menu.changeMenu($(this).val(), 1, role.editMark);
                        }
                    });
                    $("#popover_normal_input_second .form-input-top #level_2_select").unbind("change").bind({
                        change: function() {
                            role.menu.changeMenu($(this).val(), 2, role.editMark);
                        }
                    });
                    $("#popover_normal_input_second .form-input-top #level_3_select").unbind("change").bind({
                        change: function() {
                            role.menu.changeMenu($(this).val(), 3, role.editMark);
                        }
                    });
                    $("#popover_normal_input_second .btn-finish .toCreate").unbind("click").bind({
                        click: function() {
                            $("#popover_normal_input #permissions").val(role.permission.appendPermissions());
                            role.permission.clearPermissionArray();
                            popup.mask.hideSecondMask();
                        }
                    });
                }
            });
        },
        saveRole: function() {
            $("#popover_normal_input .btn-finish .toCreate").attr("disabled", true);
            common.ajax.getByAjax(true, "post", "json", "/admin/roles", $("#new_form").serialize(),
                function(data) {
                    $("#popover_normal_input .btn-finish .toCreate").attr("disabled", false);
                    if (data.pass) {
                        popup.mould.popTipsMould("新建角色成功！", popup.mould.second, popup.mould.success, "", "59%",
                            function() {
                                popup.mask.hideAllMask();
                                role.page.currentPage = 1;
                                role.page.keyword = "";
                                role.list.load();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "59%", null);
                    }
                },
                function() {
                    $("#popover_normal_input .btn-finish .toCreate").attr("disabled", false);
                    popup.mould.popTipsMould("新建角色失败，请重试！", popup.mould.second, popup.mould.error, "", "59%", null);
                }
            );
        }
    },
    editRole: {
        popup: function(id) {
            if (!common.permission.validUserPermission("ad040102")) {
                return;
            }
            common.ajax.getByAjax(true, "get", "json", "/admin/roles/" + id, {},
                function(data) {
                    role.newRole.popup();
                    $("#popover_normal_input").find("#roleLevelDiv").hide();
                    $("#popover_normal_input .theme_poptit .title").text("编辑角色" + data.name);
                    $("#popover_normal_input .btn-finish .toCreate").val("更新");
                    $("#popover_normal_input #name").val(common.tools.checkToEmpty(data.name));
                    $("input:radio[name='roleType'][value='" + data.roleType + "']").attr("checked",'checked');
                    $("#popover_normal_input #description").val(common.tools.checkToEmpty(data.description));
                    $("#popover_normal_input #permissions").val(common.tools.checkToEmpty(data.permissions));
                    $("#popover_normal_input #roleId").val(common.tools.checkToEmpty(data.id));
                    $("#popover_normal_input .btn-finish .toCreate").unbind("click").bind({
                        click: function() {
                            $("#popover_normal_input .error-line #errorText").parent().parent().hide();
                            if (!role.validation()) {
                                return;
                            }
                            role.editRole.updateRole(data.id);
                        }
                    });
                },
                function() {
                    popup.mould.popTipsMould("获取角色信息失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        updateRole: function(roleId) {
            $("#popover_normal_input .btn-finish .toCreate").attr("disabled", true);
            common.ajax.getByAjax(true, "put", "json", "/admin/roles/" + roleId, $("#new_form").serialize(),
                function(data) {
                    $("#popover_normal_input .btn-finish .toCreate").attr("disabled", false);
                    if (data.pass) {
                        popup.mould.popTipsMould("更新角色信息成功！", popup.mould.second, popup.mould.success, "", "59%",
                            function() {
                                popup.mask.hideAllMask();
                                role.list.load();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "59%", null);
                    }
                },
                function() {
                    $("#popover_normal_input .btn-finish .toCreate").attr("disabled", false);
                    popup.mould.popTipsMould("更新角色信息失败，请重试！", popup.mould.second, popup.mould.error, "", "59%", null);
                }
            );
        }
    },
    list: {
        load: function(){
            common.ajax.getByAjax(false, "get", "json", "/admin/roles/list",
                {
                    currentPage : role.page.currentPage,
                    pageSize : role.page.pageSize,
                    keyword : role.page.keyword
                },
                function(data){
                    $("#list_tab tbody").empty();
                    if(data == null){
                        popup.mould.popTipsMould("获取角色列表失败！", popup.mould.first, null, "", "57%", null);
                        return false;
                    }
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.validation.isEmpty(role.page.keyword)) {
                            popup.mould.popTipsMould("无符合条件的结果", popup.mould.first, "", "", "",
                                function() {
                                    popup.mask.hideFirstMask(false);
                                }
                            );
                        }
                        return false;
                    }
                    $("#totalCount").text(data.pageInfo.totalElements);
                    $("#pageUl").empty();
                    if (data.pageInfo.totalPage > 1) {
                        $(".customer-pagination").show();
                        $.jqPaginator('.pagination',
                            {
                                totalPages: data.pageInfo.totalPage,
                                visiblePages: role.page.visiblePages,
                                currentPage: role.page.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        role.page.currentPage = pageNum;
                                        role.list.load();
                                    }
                                }
                            }
                        );
                    } else {
                        $(".customer-pagination").hide();
                    }
                    var content = "1";
                    $.each(data.viewList, function(n, view) {
                        content+="<tr class='text-center'>"+
                            "<td>" + common.tools.checkToEmpty(view.id) + "</td>"+
                            "<td>" + common.tools.checkToEmpty(view.name) + "</td>"+
                            "<td>" + common.tools.checkToEmpty(view.roleType) + "</td>"+
                            "<td>" + "<a href='javascript:;' onclick=\"role.permission.editPermission('" + view.id + "');\")> 查看权限范围详情 </a></td>" +
                            "<td style='max-width: 220px'  title='"+common.tools.checkToEmpty(view.description)+"'>" + common.tools.getFormatComment(common.tools.checkToEmpty(view.description), 20) + "</td>"+
                            "<td>"+
                            "<span id='role_status_id_"+view.id+"' style='color: "+(view.disable==0?"green":"red")+"' >"+(view.disable==0?"已启用":"已禁用")+"</span>"+
                            "</td>"+
                            "<td>"+
                            "<span class='"+(view.disable==0?"":"none")+"' id='disable_role_status_action_id_"+view.id+"'><a  style='color: red' href='javascript:;' onclick=role.list.changeStatus("+view.id+","+1+")>禁用</a></span>"+
                            "<span class='"+(view.disable==0?"none":"")+"' id='enable_role_status_action_id_"+view.id+"'><a  style='color: green' href='javascript:;' onclick=role.list.changeStatus('"+view.id+"','"+0+"')>启用</a></span>"+
                            "<span style='margin-left:20px;'><a href='javascript:;' onclick=role.editRole.popup("+view.id+");>编辑</a></span>"+
                            "</td>"+
                            "</tr>";
                    });
                    $("#list_tab tbody").append(content);
                    common.tools.scrollToTop();
                },function(){
                    popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        changeStatus:function(id,status){
            common.ajax.getByAjax(true, "put", "json", "/admin/roles/" + id + "/" + status, {},
                function(data) {
                    if (data.pass) {
                        if(status==1){
                            $("#role_status_id_"+id).css({'color':'red'}).html("已禁用");
                            $("#disable_role_status_action_id_"+id).hide();
                            $("#enable_role_status_action_id_"+id).show();
                        }else{
                            $("#role_status_id_"+id).css({'color':'green'}).html("已启用");
                            $("#disable_role_status_action_id_"+id).show();
                            $("#enable_role_status_action_id_"+id).hide();
                        }
                    } else {
                        popup.mould.popTipsMould("操作失败！", popup.mould.first, popup.mould.warning, "", "57%", null);
                    }
                },
                function() {
                    popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    menu: {
        restoreMenu: function(position) {
            var popInput = common.tools.getPopInputDom(position, true);
            popInput.find("#level_1_select").val("0");
            popInput.find("#level_2_select").val("0");
            popInput.find("#level_3_select").val("0");
        },
        showSpecialPermissions: function(mark) {
            common.ajax.getByAjax(true, "get", "json", "/admin/resources/1/permission", {},
                function(data) {
                    role.permission.listTab(data.permissions);

                    if (role.editMark == mark) {
                        role.permission.setOptionsChk($("#popover_normal_input #permissions").val(), mark);
                    } else if (role.showMark == mark) {
                        role.permission.setOptionsChk(role.permission.appendPermissions(), mark);
                    }
                },
                function() {
                    popup.mould.popTipsMould("获取特殊权限列表异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        initMenuSel: function() {
            var options_level_1 = "<option value=\"0\">全部</option>";
            var options_level_2 = "<option value=\"0\">全部</option>";
            var options_level_3 = "<option value=\"0\">全部</option>";
            common.ajax.getByAjax(true, "get", "json", "/admin/resources/menus/", {},
                function(data) {
                    if (!data) {
                        return;
                    }
                    if (data.firstMenus) {
                        $.each(data.firstMenus, function(i, model){
                            options_level_1 += "<option value=\""+ model.id +"\">" + model.name + "</option>";
                        });
                        $("#level_1_select").append(options_level_1);
                    }

                    if (data.secondMenus) {
                        $.each(data.secondMenus, function(i, model){
                            options_level_2 += "<option value=\""+ model.id +"\">" + model.name + "</option>";
                        });
                        $("#level_2_select").append(options_level_2);
                    }

                    if (data.thirdMenus) {
                        $.each(data.thirdMenus, function(i, model){
                            options_level_3 += "<option value=\""+ model.id +"\">" + model.name + "</option>";
                        });
                        $("#level_3_select").append(options_level_3);
                    }
                    role.permission.listTab(data.permissions);
                },
                function() {
                    popup.mould.popTipsMould("初始化模块列表失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        refreshMenus: function(data) {
            var _options_level_1 = "";
            var _level_1 = $("#level_1_select");
            var _options_level_2 = "<option value=\"0\">全部</option>";
            var _options_level_3 = "<option value=\"0\">全部</option>";
            var _level_2 = $("#level_2_select");
            var _level_3 = $("#level_3_select");

            if (data.parent) {
                if (data.firstMenus) {
                    $.each(data.firstMenus, function(i, model){
                        _options_level_1 = model.id;
                    });
                    _level_1.val(_options_level_1);
                }
            }

            if (data.secondMenus) {
                $.each(data.secondMenus, function(i, model){
                    _options_level_2 += "<option value=\""+ model.id +"\">" + model.name + "</option>";
                });
                _level_2.empty().append(_options_level_2);
            }

            if (data.thirdMenus) {
                $.each(data.thirdMenus, function(i, model){
                    _options_level_3 += "<option value=\""+ model.id +"\">" + model.name + "</option>";
                });
                _level_3.empty().append(_options_level_3);
            }
        },
        changeMenu: function(newMenuId, level, mark) {
            if (level == 2 && newMenuId == 0) {
                role.menu.changeMenu($("#level_1_select").val(), 1, mark);
                return;
            }
            if (level == 3 && newMenuId == 0) {
                role.menu.changeMenu($("#level_2_select").val(), 2, mark);
                return;
            }

            common.ajax.getByAjax(true, "get", "json", "/admin/resources/menus/" + newMenuId, {level: level},
                function(data) {
                    if (!data) {
                        return;
                    }

                    var _level_2 = $("#level_2_select");
                    var _level_3 = $("#level_3_select");
                    switch (level) {
                        case 1:
                            role.menu.refreshMenus(data);
                            break;
                        case 2:
                            role.menu.refreshMenus(data);
                            _level_2.val(data.current.id);
                            break;
                        case 3:
                            role.menu.refreshMenus(data);
                            _level_2.val(data.parent.id);
                            _level_3.val(data.current.id);
                            break;
                    }
                    role.permission.listTab(data.permissions);
                    if (role.editMark == mark) {
                        role.permission.setOptionsChk($("#popover_normal_input #permissions").val(), mark);
                    } else if (role.showMark == mark) {
                        role.permission.setOptionsChk(role.permission.appendPermissions(), mark);
                    }
                },
                function() {
                    popup.mould.popTipsMould("菜单联动失败了！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    permission: {
        permissionArray: new Array(),
        appendPermissions: function() {
            var permissions_chk = "";
            if (role.permission.permissionArray.length > 0) {
                for (var index=0; index<role.permission.permissionArray.length; index++) {
                    permissions_chk += role.permission.permissionArray[index] + ",";
                }
                permissions_chk = permissions_chk.substring(0, permissions_chk.length-1);
            }
            return permissions_chk;
        },
        listTab: function(permissions) {
            var tab = $("#permission_tab tbody");
            tab.empty();

            var content = "";
            var number = 1;
            if (permissions) {
                $.each(permissions, function(index, permission) {
                    content += "<tr>" +
                    "<td class=\"text-center\">" + number + "</td>" +
                    "<td class=\"text-center\">" + permission.id + "</td>" +
                    "<td class=\"text-center\">" + permission.firstMenu + "</td>" +
                    "<td class=\"text-center\">" + permission.secondMenu + "</td>" +
                    "<td class=\"text-center\">" + permission.thirdMenu + "</td>" +
                    "<td class=\"text-center\">" + permission.name + "</td>" +
                    "<td class=\"text-center\"><input onchange=\"role.permission.changeArrayValues(this);\" type=\"checkbox\" name=\"permissionsChk\" value=\"" + permission.id + "\"></td>" +
                    "</tr>";
                    number++;
                });
            }
            tab.append(content);
        },
        changeArrayValues: function(chk) {
            var chkVal = $(chk).val();
            if ($(chk).is(':checked')) {
                if (role.permission.permissionArray.indexOf(chkVal) == -1) {
                    role.permission.permissionArray.push(chkVal);
                }
            } else {
                if (role.permission.permissionArray.indexOf(chkVal) > -1) {
                    role.permission.permissionArray.remove(chkVal);
                }
            }
        },
        clearPermissionArray: function() {
            role.permission.permissionArray.length = 0;
        },
        setOptionsChk: function(permissions, mark) {
            if (!common.validation.isEmpty(permissions)) {
                role.permission.permissionArray = permissions.split(",");
            }

            if ($("input[name=\"permissionsChk\"]").length > 0) {
                $("input[name=\"permissionsChk\"]").each(function () {
                    if (role.editMark == mark) {
                        $(this).attr('disabled', false);
                    } else if (role.showMark == mark) {
                        $(this).attr('disabled', true);
                    }

                    if (role.permission.permissionArray.length > 0 && role.permission.permissionArray.indexOf($(this).val()) > -1) {
                        $(this).attr("checked",'checked');
                    }
                });
            }
        },
        editPermission: function(roleId) {
            if (!common.permission.validUserPermission("ad040105")) {
                return;
            }
            common.ajax.getByAjax(true, "get", "json", "/admin/roles/" + roleId, {},
                function(data) {
                    role.initRole.initPopupContent();
                    popup.pop.popInput(role.permissionContent, popup.mould.first, "850px", "550px", "36%", "48%");
                    role.permission.clearPermissionArray();
                    role.permission.setOptionsChk(data.permissions, role.showMark);
                    $("#popover_normal_input .btn-finish .toCreate").val("编辑");
                    $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                        click: function() {
                            popup.mask.hideFirstMask();
                        }
                    });
                    //超级管理员列出特殊权限
                    if (common.permission.isSuperMan()) {
                        $("#popover_normal_input .permission-div").css("height", "58%");
                        $("#popover_normal_input #showSpecialPermissionBtnDiv").show();
                        $("#popover_normal_input .form-input-top #showSpecialPermissionBtn").unbind("click").bind({
                            click: function() {
                                role.menu.restoreMenu(popup.mould.first);
                                role.menu.showSpecialPermissions(role.showMark);
                            }
                        });
                    } else {
                        $("#popover_normal_input .permission-div").css("height", "63%");
                        $("#popover_normal_input #showSpecialPermissionBtnDiv").remove();
                    }
                    $("#popover_normal_input .form-input-top #level_1_select").unbind("change").bind({
                        change: function() {
                            role.menu.changeMenu($(this).val(), 1, role.showMark);
                        }
                    });
                    $("#popover_normal_input .form-input-top #level_2_select").unbind("change").bind({
                        change: function() {
                            role.menu.changeMenu($(this).val(), 2, role.showMark);
                        }
                    });
                    $("#popover_normal_input .form-input-top #level_3_select").unbind("change").bind({
                        change: function() {
                            role.menu.changeMenu($(this).val(), 3, role.showMark);
                        }
                    });
                    $("#popover_normal_input .btn-finish .toCreate").unbind("click").bind({
                        click: function() {
                            if (!common.permission.validUserPermission("ad040102")) {
                                return;
                            }

                            role.permission.setOptionsChk(data.permissions, role.editMark);
                            $("#popover_normal_input .btn-finish .toCreate").val("保存");

                            $("#popover_normal_input .form-input-top #level_1_select").unbind("change").bind({
                                change: function() {
                                    role.menu.changeMenu($(this).val(), 1, role.editMark);
                                }
                            });
                            $("#popover_normal_input .form-input-top #level_2_select").unbind("change").bind({
                                change: function() {
                                    role.menu.changeMenu($(this).val(), 2, role.editMark);
                                }
                            });
                            $("#popover_normal_input .form-input-top #level_3_select").unbind("change").bind({
                                change: function() {
                                    role.menu.changeMenu($(this).val(), 3, role.editMark);
                                }
                            });
                            $("#popover_normal_input .btn-finish .toCreate").unbind("click").bind({
                                click: function() {
                                    role.permission.updatePermission(roleId);
                                }
                            });
                        }
                    });
                },
                function() {
                    popup.mould.popTipsMould("获取角色信息失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        updatePermission: function(roleId) {
            $("#popover_normal_input .btn-finish .toCreate").attr("disabled", true);
            common.ajax.getByAjax(true, "put", "json", "/admin/roles/" + roleId + "/permissions", {permissions: role.permission.appendPermissions()},
                function(data) {
                    $("#popover_normal_input .btn-finish .toCreate").attr("disabled", false);
                    if (data.pass) {
                        popup.mould.popTipsMould("更新角色权限成功！", popup.mould.second, popup.mould.success, "", "59%",
                            function() {
                                popup.mask.hideAllMask();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "59%", null);
                    }
                },
                function() {
                    $("#popover_normal_input .btn-finish .toCreate").attr("disabled", false);
                    popup.mould.popTipsMould("更新角色权限失败，请重试！", popup.mould.second, popup.mould.error, "", "59%", null);
                }
            );
        }
    }
};

$(function() {
    role.initRole.init();
    $("#toNew").unbind("click").bind({
      click: function() {
          if (common.permission.validUserPermission("ad040101")) {
              role.newRole.popup();
          }
      }
    });
    /* 初始化 */
    role.list.load();
    /* 搜索 */
    $("#searchBtn").bind({
        click : function(){
            if(common.validation.isEmpty($("#keyword").val())){
                popup.mould.popTipsMould("请输入搜索内容！", popup.mould.first, null, "", "57%", null);
                return false;
            }
            role.page.currentPage = 1;
            role.page.keyword = $("#keyword").val();
            role.list.load();
        }
    });
});
