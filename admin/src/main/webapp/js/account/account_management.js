/**
 * Created by liyh on 2015/9/8
 */
var account = {
    userType: "",
    joinUrl: "",
    /*初始化页面*/
    initAccount: {
        initNewOrEditPopupContent: function () {
            var popupContent = $("#new_content");
            if (popupContent.length > 0) {
                account.newOrEditAccount.content = popupContent.html();
                $("#mobile").numeral();
                popupContent.remove();
            }
        },
        initNewOrEditPermission: function () {
            var popupContent = $("#detail_content");
            if (popupContent.length > 0) {
                account.newOrEditAccount.content = popupContent.html();
                $("#mobile").numeral();
                popupContent.remove(); //利用这个方法让原来的元素进行隐藏
            }
        },
        initReNewPwdPopupContent: function () {
            var popupContent = $("#update_pwd_div");
            if (popupContent.length > 0) {

                account.newPwdAccount.content = popupContent.html();

                $("#mobile").numeral();
                popupContent.remove();
            }
        }

    },
    /*list列表*/
    listAccount: {
        properties: new Properties(1, ""),
        list: function () {
            common.ajax.getByAjax(false, "get", "json", "/admin/account/" + account.joinUrl,
                {
                    keyword: account.listAccount.properties.keyword,
                    currentPage: account.listAccount.properties.currentPage,
                    pageSize: account.listAccount.properties.pageSize,
                    userType: account.userType
                },
                function (data) {
                    $("#account_list_tab tbody").empty();
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (account.listAccount.properties.keyword) {
                            popup.mould.popTipsMould("无符合条件的结果", popup.mould.first, "", "", "",
                                function () {
                                    popup.mask.hideFirstMask(false);
                                }
                            );
                        }
                        return false;
                    }
                    $("#totalCount").text(data.pageInfo.totalElements);
                    $("#pageUl").empty();
                    if (data.pageInfo.totalPage > 1) {
                        $.jqPaginator('.pagination',
                            {
                                totalPages: data.pageInfo.totalPage,
                                visiblePages: account.listAccount.properties.visiblePages,
                                currentPage: account.listAccount.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType == "change") {
                                        account.listAccount.properties.currentPage = pageNum;
                                        account.listAccount.list(account.joinUrl);
                                        window.scrollTo(0, 0);
                                    }
                                }
                            }
                        );
                    } else {
                        $(".customer-pagination").hide();
                    }
                    // 显示列表数据
                    if(account.joinUrl == "inner"){
                        $("#account_list_tab tbody").append(account.listAccount.writeinner(data));
                    }else {
                        $("#account_list_tab tbody").append(account.listAccount.writeouter(data));
                    }
                    common.tools.scrollToTop();
                    $(".permission").unbind("click").bind({
                        click:function(){
                            editPermission($(this).attr("id"),$(this).attr("name"));
                        }
                    });
                }, function () {
                    popup.mould.popTipsMould("获取查询数据失败！", popup.mould.first, popup.mould.error, "", "56%", null);
                }
            )
        },
        writeinner: function (data) {
            var content = "";
            $.each(data.viewList, function (i, model) {
                content += "<tr class='text-center'>" +
                    "<td>" + model.id + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.name) + "</td>" +
                    "<td>" + common.tools.getCommentMould(common.tools.checkToEmpty(model.roleName), 20) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.email) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.mobile) + "</td>" +
                    "<td>" +
                    "<span id='account_status_id_" + model.id + "' style='color: " + (model.disable ? "red" : "green") + "' >" + (model.disable ? "已禁用" : "已启用") + "</span>" +
                    "</td>" +
                    "<td><span class='" + (model.disable ? "none" : "") + "' id='disable_account_status_action_id_" + model.id + "'><a  style='color: red;text-decoration:none;' href='javascript:;' onclick=onOrOff(" + model.id + "," + false + ")>禁用</a></span>" +
                    "<span class='" + (model.disable ? "" : "none") + "' id='enable_account_status_action_id_" + model.id + "'><a  style='color: green;text-decoration:none;' href='javascript:;' onclick=onOrOff('" + model.id + "','" + true + "')>启用</a></span>" +
                    "<a style='text-decoration:none;' onclick='editPage(" + model.id + ")'>&nbsp;&nbsp;&nbsp;编辑&nbsp;&nbsp;&nbsp;</a><a style='text-decoration:none;' onclick='rePwdPage(" + model.id + ")'>修改密码</a><a class='permission' style='text-decoration:none;' name='"+model.name+"' id='"+model.id+"' >&nbsp;&nbsp;&nbsp;权限操作&nbsp;&nbsp;&nbsp;</a></td>" +
                    "</tr>";
            });

            return content;
        },
        writeouter:function (data) {
            var content = "";
            $.each(data.viewList, function (i, model) {
                content += "<tr class='text-center'>" +
                    "<td>" + model.id + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.name) + "</td>" +
                    "<td>" + common.tools.getCommentMould(common.tools.checkToEmpty(model.roleName), 20) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.email) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.mobile) + "</td>" +
                    "<td>" +
                    "<span id='account_status_id_" + model.id + "' style='color: " + (model.disable ? "red" : "green") + "' >" + (model.disable ? "已禁用" : "已启用") + "</span>" +
                    "</td>" +
                    "<td><span class='" + (model.disable ? "none" : "") + "' id='disable_account_status_action_id_" + model.id + "'><a  style='color: red;text-decoration:none;' href='javascript:;' onclick=onOrOff(" + model.id + "," + false + ")>禁用</a></span>" +
                    "<span class='" + (model.disable ? "" : "none") + "' id='enable_account_status_action_id_" + model.id + "'><a  style='color: green;text-decoration:none;' href='javascript:;' onclick=onOrOff('" + model.id + "','" + true + "')>启用</a></span>" +
                    "<a style='text-decoration:none;' onclick='editPage(" + model.id + ")'>&nbsp;&nbsp;&nbsp;编辑&nbsp;&nbsp;&nbsp;</a><a style='text-decoration:none;' onclick='rePwdPage(" + model.id + ")'>修改密码</a></td>" +
                    "</tr>";
            });
            return content;
        }
    },
    /*权限设置*/
    newOrEditPermission: {
        name:"",
        content: "",
        newOrEdit: function (id,name) {
            account.newOrEditPermission.name=name;
            account.initAccount.initNewOrEditPermission();
            popup.pop.popInput(account.newOrEditAccount.content, popup.mould.first, "1056px", "auto", "30%", "40%");
            //关闭操作
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }
            });
            //查看所有权限
            $("#lookPermission").unbind("click").bind({
                click: function () {
                    account.newOrEditPermission.lookPermission(id);
                }
            });
            //选择类型
            $("#chooseType").unbind("change").bind({
                change: function () {
                    $("#edit_error").parent().parent().hide();
                    var val = $("#chooseType").val();
                    if($("#field").val() == "" || $("#entity").val() == ""){
                        $("#edit_error").text("请输入查询对象或查询字段");
                        $("#edit_error").parent().parent().show();
                    }else {
                        account.newOrEditPermission.getTypeData(val);
                    }
                }
            });
            //新增操作
            $("#add_premission").unbind("click").bind({
                click: function () {
                    var flag = account.newOrEditPermission.validate();
                    if (flag) {
                        account.newOrEditPermission.add();
                    }

                }

            });

            account.newOrEditPermission.write(id);

        },
        //不仅要获取所有的数据，还要对其进行数据会显
        getTypeData: function (val) {
            common.ajax.getByAjax(true, "get", "json", "/admin/account/permissionType", $.trim($("#addPermissionForm").serialize()),
                function (data) {
                    $('#choosePermission').empty()
                    $('#choosePermission').height("100px").css({
                        "overflow": "scroll",
                        "height": "200px",
                        "overflow-x": "hidden"
                    })
                    if (data && val == 1) {
                        $.each(data.Data, function (num) {
                            if ((num + 1 ) % 9 == 0) {
                                if ($.inArray(this.id, data.choosed) > -1) {
                                    $('#choosePermission')
                                        .append("<label><input checked = 'checked'  name='values' type='checkbox' value= " + this.id + " />" + this.name + "</label> &nbsp;&nbsp;&nbsp;<br/>");
                                } else {
                                    $('#choosePermission')
                                        .append("<label><input name='values' type='checkbox' value= " + this.id + " />" + this.name + "</label> &nbsp;&nbsp;&nbsp;<br/>");
                                }
                            } else {
                                if ($.inArray(this.id, data.choosed) > -1) {
                                    $('#choosePermission')
                                        .append("<label><input checked = 'checked'  name='values' type='checkbox' value= " + this.id + " />" + this.name + "</label> &nbsp;&nbsp;&nbsp;");
                                } else {
                                    $('#choosePermission')
                                        .append("<label><input name='values' type='checkbox' value= " + this.id + " />" + this.name + "</label> &nbsp;&nbsp;&nbsp;");
                                }

                            }
                        });
                    }
                    if (data && val == 2) {
                        $.each(data.Data, function (num) {
                            if ((num + 1 ) % 5 == 0) {
                                if ($.inArray(this.id, data.choosed) > -1) {
                                    $('#choosePermission')
                                        .append("<label><input checked = 'checked'  name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;<br/>");
                                } else {
                                    $('#choosePermission')
                                        .append("<label><input name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;<br/>");
                                }
                            } else {
                                if ($.inArray(this.id, data.choosed) > -1) {
                                    $('#choosePermission')
                                        .append("<label><input checked = 'checked'  name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;");
                                } else {
                                    $('#choosePermission')
                                        .append("<label><input name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;");
                                }
                            }
                        });
                    }
                    if (data && val == 3) {
                        $.each(data.Data, function (num) {
                            if ((num + 1 ) % 7 == 0) {
                                if ($.inArray(this.id, data.choosed) > -1) {
                                    $('#choosePermission')
                                        .append("<label><input checked = 'checked'  name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;<br/>");
                                } else {
                                    $('#choosePermission')
                                        .append("<label><input name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;<br/>");
                                }
                            } else {
                                if ($.inArray(this.id, data.choosed) > -1) {
                                    $('#choosePermission')
                                        .append("<label><input checked = 'checked'  name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;");
                                } else {
                                    $('#choosePermission')
                                        .append("<label><input name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;");
                                }
                            }
                        });
                    }
                    if (data && val == 4) {
                        $.each(data.Data, function (num) {
                            if ((num + 1 ) % 9 == 0) {
                                if ($.inArray(this.id, data.choosed) > -1) {
                                    $('#choosePermission')
                                        .append("<label><input checked = 'checked'  name='values' type='checkbox' value= " + this.id + " />" + this.name + "</label> &nbsp;&nbsp;&nbsp;<br/>");
                                } else {
                                    $('#choosePermission')
                                        .append("<label><input name='values' type='checkbox' value= " + this.id + " />" + this.name + "</label> &nbsp;&nbsp;&nbsp;<br/>");
                                }
                            } else {
                                if ($.inArray(this.id, data.choosed) > -1) {
                                    $('#choosePermission')
                                        .append("<label><input checked = 'checked'  name='values' type='checkbox' value= " + this.id + " />" + this.name + "</label> &nbsp;&nbsp;&nbsp;");
                                } else {
                                    $('#choosePermission')
                                        .append("<label><input name='values' type='checkbox' value= " + this.id + " />" + this.name + "</label> &nbsp;&nbsp;&nbsp;");
                                }
                            }
                        });
                    }
                    if (data && val == 5) {

                        $.each(data.Data, function (num) {
                            if ((num + 1 ) % 4 == 0) {
                                if ($.inArray(this.id, data.choosed) > -1) {
                                    $('#choosePermission')
                                        .append("<label><input checked = 'checked'  name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;<br/>");
                                } else {
                                    $('#choosePermission')
                                        .append("<label><input name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;<br/>");
                                }
                            } else {
                                if ($.inArray(this.id, data.choosed) > -1) {
                                    $('#choosePermission')
                                        .append("<label><input checked = 'checked'  name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;");
                                } else {
                                    $('#choosePermission')
                                        .append("<label><input name='values' type='checkbox' value= " + this.id + " />" + this.description + "</label> &nbsp;&nbsp;&nbsp;");
                                }
                            }
                        });
                    }

                },
                function () {
                    $("#save").attr("disabled", false);
                    popup.mould.popTipsMould("获取数据失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },
        lookPermission: function (val) {
            /*  reqdata: "{entity:'" + $("#entity").text() + "',filed:'" + $("#field").text() + "'}",*/
            common.ajax.getByAjax(true, "POST", "json", "/admin/account/lookPermission/" + val, null,
                function (data) {
                    $('#AllPermisson').empty()
                    // $('#tabRebate').css({height:'auto'});
                    if (data) {
                        debugger
                        $.each(data, function (num) {
                            $('#AllPermisson')
                                .append("<tr class=" + "active" + ">")
                                .append("<td  class=" + "text-left" + ">" + this.entity + "</td>")
                                .append("<td  class=" + "text-center" + ">" + this.field + "</td>")
                                .append("<td  class=" + "text-left" + " width='500px'>" + (this.values ? common.tools.getCommentMould(this.values,35) : "" )+ "</td>")
                                .append("<td  class=" + "text-left" + ">" + this.comment + "</td>")
                                .append("<td  id = 'permission_status_id_" + this.id + "' style='color: " + (this.status ? "green" : "red")+ "'  class=" + "text-left" + ">" +(this.status ? "已启用" : "已禁用") + "</td>")
                              /*  .append("<td  class=" + "text-left" + ">" +
                                    " <a style='text-decoration:none;  onclick='account.newOrEditPermission.switchPermission(" + this.id + "," + true +  ")'>启用&nbsp;&nbsp;</a>"+
                                    "<a style='text-decoration:none; onclick='account.newOrEditPermission.switchPermission( " +this.id　+ " , " + false +  " )'>禁用&nbsp;&nbsp;</a>"+
                                    "</td>")*/
                                .append("<td><span class='" + (this.status ? "none" : "") + "' id='disable_permission_status_id_" + this.id + "'><a  style='color: green;text-decoration:none;' href='javascript:;' onclick='account.newOrEditPermission.switchPermission(" + this.id + "," + true +  ")'>启用</a></span>" +
                                    "<span class='" + (this.status ? "" : "none") + "' id='enable_permission_status_id_" + this.id + "'><a  style='color: red;text-decoration:none;' href='javascript:;' onclick='account.newOrEditPermission.switchPermission(" + this.id + "," + false +  ")'>禁用</a></span>" +
                                    "</td>" )
                                .append("<tr/>")
                        });
                    }


                },
                function () {
                    $("#save").attr("disabled", false);
                    popup.mould.popTipsMould("获取数据失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },
        switchPermission:function (id,status) {
            common.ajax.getByAjax(true, "get", "json", "/admin/account/inner/updateStatusPermission", {
                    id: id,
                    status: status
                },
                function (data) {
                debugger
                    if (data.pass) {
                        if (status) {
                            $("#permission_status_id_" + id).css({'color': 'green'}).html("已启用");
                            $("#disable_permission_status_id_" + id).hide();
                            $("#enable_permission_status_id_" + id).show();
                        } else {
                            $("#permission_status_id_" + id).css({'color': 'red'}).html("已禁用");
                            $("#enable_permission_status_id_" + id).hide();
                            $("#disable_permission_status_id_" + id).show();
                        }
                    } else {
                        popup.mould.popTipsMould("操作失败！", popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    popup.mould.popTipsMould("操作失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );


        },

        write: function (id) {
            $("#userId").val(id);
            $("#pName").val(account.newOrEditPermission.name);
        },

        validate: function () {
            if ($.trim($("#entity").val()) == "") {
                $("#edit_error").text("请输入查询对象");
                $("#edit_error").parent().parent().show();
                return false;
            }

            if ($.trim($("#field").val()) == "") {
                $("#edit_error").text("请输入查询字段");
                $("#edit_error").parent().parent().show();
                return false;
            }
            if ($.trim($("#field").val()) == "") {
                $("#edit_error").text("请输入查询字段");
                $("#edit_error").parent().parent().show();
                return false;
            }


            return true;
        },
        add: function () {
            common.ajax.getByAjax(true, "post", "json", "/admin/account/addPermission", $("#addPermissionForm").serialize(),
                function (data) {
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould(data.message, popup.mould.first, popup.mould.success, "", "59%",
                            function () {
                                popup.mask.hideFirstMask();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "59%", null);
                    }
                },
                function () {
                    $("#accountSave").attr("disabled", false);
                    popup.mould.popTipsMould("新增权限失败，请重试！", popup.mould.second, popup.mould.error, "", "59%", null);
                }
            );
        },
    },
    /* 新建/编辑账号 */
    newOrEditAccount: {
        content: "",
        newOrEdit: function (id) {
            account.initAccount.initNewOrEditPopupContent();
            popup.pop.popInput(account.newOrEditAccount.content, popup.mould.first, "416px", "auto", "40%", "59%");

            if (id) {
                $("#popover_normal_input .notice-p").remove();
                $("#popover_normal_input").height(375);
            }
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }
            });
            $('#roleSel').multiselect({
                nonSelectedText: '请选择角色',
                buttonWidth: '200',
                maxHeight: '180',
                includeSelectAllOption: true,
                selectAllNumber: false,
                selectAllText: '全部',
                allSelectedText: '全部'
            });
            $("input[name='inOrOutRole'][value=" + account.userType + "]").attr("checked", true);
            $("input[name='inOrOutRole']").attr("disabled", true);
            if (id) {
                $("#initializePwd").hide();
                account.newOrEditAccount.write(id);
                $("#id").val(id);
                $("#email").attr("readonly", true);
                $("#addOrEdit").text("修改账号");
                $("#accountSave").click(function () {
                    if (account.newOrEditAccount.validate())
                        account.newOrEditAccount.update();
                })
            } else {
                $("#initializePwd").show();
                $("#internalUserType").val(account.userType);
                $("#addOrEdit").text("新建账号");
                $("#accountSave").click(function () {
                    if (account.newOrEditAccount.validate())
                        account.newOrEditAccount.save();
                })
            }
        },
        write: function (id) {
            common.ajax.getByAjax(true, "get", "json", "/admin/account/" + id, null,
                function (data) {
                    if (data) {
                        if (data.roleIds) {
                            $('#roleSel').multiselect('select', data.roleIds.split(","));
                        }
                        $("#email").val(data.email);
                        $("input[name='inOrOutRole'][value=" + account.userType + "]").attr("checked", true);
                        $("#name").val(data.name);
                        $("#mobile").val(data.mobile);
                    } else {
                        popup.mould.popTipsMould("获取数据失败！", popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    $("#save").attr("disabled", false);
                    popup.mould.popTipsMould("获取数据失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },
        validate: function () {
            if ($.trim($("#email").val()) == "") {
                $("#edit_errorText").text("邮箱不能为空");
                $("#edit_errorText").parent().parent().show();
                return false;
            }
            if (!common.validation.isEmail($("#email").val())) {
                $("#edit_errorText").text("邮箱不正确");
                $("#edit_errorText").parent().parent().show();
                return false;
            }

            if (!$("#roleSel").val()) {
                $("#edit_errorText").text("请至少选择一个角色");
                $("#edit_errorText").parent().parent().show();
                return false;
            }

            if ($.trim($("#name").val()) == "") {
                $("#edit_errorText").text("姓名不能为空");
                $("#edit_errorText").parent().parent().show();
                return false;
            }
            if ($("#mobile").val() == "") {
                $("#edit_errorText").text("电话不能为空");
                $("#edit_errorText").parent().parent().show();
                return false;
            }
            if (!common.validation.isMobile($("#mobile").val()) && !common.validation.isTelphone($("#mobile").val())) {
                $("#edit_errorText").text("电话不符合规格");
                $("#edit_errorText").parent().parent().show();
                return false;
            }
            return true;
        },
        save: function () {
            $("#accountSave").attr("disabled", true);
            common.ajax.getByAjax(true, "post", "json", "/admin/account/" + account.joinUrl, $("#new_form").serialize(),
                function (data) {
                    $("#accountSave").attr("disabled", false);
                    if (data) {
                        if (!data.pass) {
                            popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "59%", null);
                            return;
                        }
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("新增账号成功！", popup.mould.first, popup.mould.success, "", "59%",
                            function () {
                                popup.mask.hideFirstMask();
                                account.listAccount.properties.currentPage = 1;
                                account.listAccount.properties.keyword = $("#keyword").val();
                                account.listAccount.list(account.joinUrl);
                            }
                        );
                    } else {
                        popup.mould.popTipsMould("新增账号失败！", popup.mould.second, popup.mould.warning, "", "59%", null);
                    }
                },
                function () {
                    $("#accountSave").attr("disabled", false);
                    popup.mould.popTipsMould("新增账号失败，请重试！", popup.mould.second, popup.mould.error, "", "59%", null);
                }
            );
        },
        update: function (form) {
            $("#accountSave").attr("disabled", true);
            common.ajax.getByAjax(true, "put", "json", "/admin/account/" + account.joinUrl, $("#new_form").serialize(),
                function (data) {
                    $("#accountSave").attr("disabled", false);
                    if (data) {
                        if (!data.pass) {
                            popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                            return;
                        }
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("修改账号信息成功！", popup.mould.first, popup.mould.success, "", "57%",
                            function () {
                                popup.mask.hideFirstMask();
                                account.listAccount.properties.keyword = $("#keyword").val();
                                account.listAccount.list(account.joinUrl);
                            }
                        );
                    } else {
                        popup.mould.popTipsMould("修改账号信息失败！", popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    $("#accountSave").attr("disabled", false);
                    popup.mould.popTipsMould("修改账号信息失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    /*密码修改*/
    newPwdAccount: {
        content: "",
        newPwd: function (id) {
            account.initAccount.initReNewPwdPopupContent();
            popup.pop.popInput(account.newPwdAccount.content, "first", "500px", "280px", "45%", "59%");
            /*关闭新建弹出框*/
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }
            });
            /*显示隐藏密码*/
            var pwd_show_flag = false;
            $("#show-hide-pwd").unbind("click").bind({
                click: function () {
                    if (!pwd_show_flag) {
                        $("#newPwd").attr('type', 'text');
                        $("#show-hide-pwd").css("color", "#ff5a35");
                        pwd_show_flag = true;
                    } else {
                        $("#newPwd").attr('type', 'password');
                        $("#show-hide-pwd").css("color", "#555");
                        pwd_show_flag = false;
                    }
                }
            });
            $("#pwdSave").click(function () {
                $("#newPwdId").val(id);
                if (account.newPwdAccount.validate())
                    account.newPwdAccount.save();
            })
        },
        validate: function () {
            if ($.trim($("#newPwd").val()) == "") {
                $("#pwd_errorText").text("密码不允许为空");
                $("#pwd_errorText").parent().parent().show();
                return false;
            }
            if (common.tools.getLength($("#newPwd").val()) < 6 || common.tools.getLength($("#newPwd").val()) > 12) {
                $("#pwd_errorText").text("密码长度为6到12位");
                $("#pwd_errorText").parent().parent().show();
                return false;
            }
            if (!common.validation.isPassword($("#newPwd").val())) {
                $("#pwd_errorText").text("密码由6到12位大小写字母数字下划线组成！");
                $("#pwd_errorText").parent().parent().show();
                return false;
            }
            if (!common.validation.isPasswordEx($("#newPwd").val())) {
                $("#pwd_errorText").text("密码必须包含大小写字母和数字！");
                $("#pwd_errorText").parent().parent().show();
                return false;
            }
            return true;
        },
        save: function () {
            common.ajax.getByAjax(true, "post", "json", "/admin/account/" + account.joinUrl + "/modifyPassword", $("#update_pwd").serialize(),
                function (data) {
                    $("#pwdSave").attr("disabled", false);
                    if (data) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("修改密码成功！", popup.mould.first, popup.mould.success, "", "57%",
                            function () {
                                popup.mask.hideFirstMask();
                            }
                        );
                    } else {
                        popup.mould.popTipsMould("修改密码失败！", popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    $("#pwdSave").attr("disabled", false);
                    popup.mould.popTipsMould("修改密码失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    switchStatus: {
        onOrOff: function (id, status) {
            common.ajax.getByAjax(true, "get", "json", "/admin/account/" + account.joinUrl + "/updateStatus", {
                    id: id,
                    status: status
                },
                function (data) {
                    if (data.pass) {
                        if (status) {
                            debugger
                            $("#account_status_id_" + id).css({'color': 'green'}).html("已启用");
                            $("#disable_account_status_action_id_" + id).show();
                            $("#enable_account_status_action_id_" + id).hide();
                        } else {
                            $("#account_status_id_" + id).css({'color': 'red'}).html("已禁用");
                            $("#disable_account_status_action_id_" + id).hide();
                            $("#enable_account_status_action_id_" + id).show();
                        }
                    } else {
                        popup.mould.popTipsMould("操作失败！", popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    popup.mould.popTipsMould("操作失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    getRoles: function (userType) {
        common.ajax.getByAjax(true, "get", "json", "/admin/roles/userType",
            {
                userType: userType
            },
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.name + "</option>";
                    });
                    $("#roleSel").append(options);
                } else {
                }
            },
            function () {
            }
        );
    }
}
