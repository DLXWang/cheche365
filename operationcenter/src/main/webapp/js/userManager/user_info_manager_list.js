var dataFunction = {
    "data": function (data) {
        $(".search_item").each(function () {
            data[this.id] = this.value ? this.value.trim() : null;
        });
    },
    "fnRowCallback": function (nRow, aData) {
        $city = "";
        $enable = "";
        if (aData.areaName.length == 1 && aData.areaName != null) {
            $city = '<span>' + aData.areaName[0].name + '</span>';
            // $enable = '<a id="edit" download="edit" onclick="channelAgent.editArea(' + aData.channelId + ',' + aData.id + ',\'' + aData.areaName[0].name + '\')"><span style="color: #1E90FF ;">修改</span> </a>'
        } else {
            $city = '<a id="cityList" download="cityList" onclick="channelAgent.selectAreaList(' + aData.id + ')"><span style="color: #1E90FF ;">查看</span></a>';
            // $enable = '<a id="edit" download="edit" onclick="channelAgent.editArea(' + aData.channelId + ',' + aData.id + ',\'' + null + '\')"><span style="color: #1E90FF ;">修改</span> </a>'
        }
        $('td:eq(3)', nRow).html($city);

        if (!aData.status) {
            $enable += ' <a id="disable" download="disable" onclick="channelAgent.selectStatus(' + aData.id + ',true)"><span style="color: #1E90FF ">禁&nbsp;用</span> </a>';
            $enable += ' <a id="disable" download="disable" onclick="channelAgent.editApproveInfo(' + aData.id + ')"><span >编&nbsp;辑</span> </a>';
        } else {
            $enable += ' <a id="disable" download="disable" onclick="channelAgent.selectStatus(' + aData.id + ',false)"><span style="color: #1E90FF ;">开&nbsp;启</span> </a>';
            $enable += ' <a id="disable" download="disable" onclick="channelAgent.editApproveInfo(' + aData.id + ')")"><span >编&nbsp;辑</span> </a>';
        }
        $('td:eq(16)', nRow).html($enable);
    },
};
var channelAgentList = {
    order: false,
    "url": '/operationcenter/channleAgent/channelAgentList ',
    "type": "GET",
    "table_id": "user_list",
    "columns": [
        {"data": "id", "title": "序号"},
        {"data": "name", "title": "姓名"},
        {"data": "agentLevel", "title": "团队角色"},
        {"data": null, "title": "所属城市"},
        {"data": "shopDesc", "title": "公司名称/门店"},
        {"data": "channelDesc", "title": "所属渠道"},
        {"data": "mobile", "title": "电话"},
        {"data": "identity", "title": "身份证号"},
        {"data": "orderCount", "title": "成单数量"},
        {"data": "totalAmount", "title": "保费金额"},
        {"data": "invitePerson", "title": "邀请人"},
        {"data": "topInvitePerson", "title": "顶级邀请码申请人"},
        {"data": "topInviteTime", "title": "顶级邀请码申请时间"},
        {"data": "inviteCode", "title": "邀请码"},
        {"data": "registerTime", "title": "注册时间"},
        {"data": "approveStatus", "title": "认证状态"},
        {"data": null, "title": "操作"},
    ]
};
var channelAgent = {

    chgList: function () {
        /* 搜索 */
        $("#searchBtn").unbind("click").bind({
            click: function () {
                datatables.ajax.reload();
            }
        });
        $(".search_item").unbind("change").bind({
            change: function () {
                datatables.ajax.reload();
            }
        });
        $("#cancelBtn").unbind("click").bind({
            click: function () {
                $(".search_item").each(function () {
                    $(this).val("");
                });
                datatables.ajax.reload();
            }
        });
    },

    initChannelList: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/channleAgent/channels", {},
            function (data) {
                if (data) {
                    var options = "<option value=''>请选择渠道类型</option>";
                    $.each(data, function (i, model) {
                        options += `<option value=${model.id}>${model.description}</option>`;
                    });
                    $("#channel").html(options);
                }
            },
            function () {
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },

    /* 团队角色列表查询 */
    agentLevelList: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/agentLevel/agentLevelList", {},
            function (data) {
                if (data) {
                    var options = "<option value=''>请选择团队角色</option>";
                    $.each(data, function (i, model) {
                        options += `<option value=${model.id}>${model.description}</option>`;
                    });
                    $("#agentLevel").html(options);
                }
            },
            function () {
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },

    /* 认证状态列表查询*/
    approveStatusList: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/channleAgent/approveStatusList", {},
            function (data) {
                if (data) {
                    var options = "<option selected='selected' disabled='disabled'  style='display: none' value=''></option>";
                    options += `<option value= 0>全部</option>`;
                    $.each(data, function (i, model) {
                        options += `<option value=${model.id}>${model.description}</option>`;
                    });
                    $("#approveStatus").html(options);
                }
            },
            function () {
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    /*下拉框提示语*/
    changeApproveStatus: function () {
        $("#approveStatus").on('change', function () {
            var approve = document.getElementById("approveStatusReminder");
                if ($("#approveStatus").val() == 0) {
                    approve.style.display = "inline ";
                } else if ($("#approveStatus").val() == 2) {
                    approve.style.display = "inline ";
                } else {
                    approve.style.display  = "none";
                }
            }
        );
    },

    batchAddPop: function () {
        $("#createInvitationCode").on('click', function () {
                $.get('/views/userManager/invite_code_apply.html', function (content) {
                    layer.open({
                        type: 1,
                        title: '批量生成邀请码',
                        skin: 'layui-layer-rim', //加上边框
                        area: ['30%', '35%'], //宽高
                        content: content,
                        scrollbar: false,
                        success: function (layero, index) {
                            $(layero).find('.popDiv').data('layer-index', index);
                        }
                    });
                })
            }
        );
    },
    // 起禁用
    selectStatus: function (id, status) {
        var str = status ? "禁用后，被选中用户将无权限操作任何功能，是否禁用用户权限？" : "开启后，被选中的用户将恢复其应用操作权限，是否恢复用户权限？";
        popup.mould.popConfirmMould(str, popup.mould.first, "", "",
            function () {
                common.getByAjax(true, "get", "json", "/operationcenter/channleAgent/selectStatus", {
                        'id': id,
                        'status': status
                    },
                    function (data) {
                        popup.mask.hideFirstMask(false);
                        if (data.pass) {
                            datatables.ajax.reload();
                        } else {
                            popup.mould.popTipsMould("禁用出现异常！", popup.mould.second, popup.mould.error, "", "57%", null);
                        }
                    },
                    function () {
                        popup.mould.popTipsMould("禁用出现异常！", popup.mould.second, popup.mould.error, "", "57%", null);
                    }
                );
            },
            function () {
                popup.mask.hideFirstMask(false);
            }
        );
    },
//修改渠道业务地区 --
//     initEditArea: function () {
//         var detailContent = $("#edit_area_content");
//         if (detailContent.length > 0) {
//             channelAgent.editAreaContent = detailContent.html();
//             detailContent.remove();
//         }
//     },
//     editArea: function (channelId, id, areaName) {
//         popup.pop.popInput(channelAgent.editAreaContent, 'first', "550px", "280px", "50%", "55%");
//         channelAgent.initEditAreaList(channelId, areaName, id);
//         parent.$("#editclose").unbind("click").bind({
//             click: function () {
//                 popup.mask.hideFirstMask(false);
//             }
//         });
//     },
//     initEditAreaList: function (channelId, areaName, id) {
//         let content = '<option value="全部">全部</option>';
//         let $select = $('#areaSel');
//         common.getByAjax(false, 'get', 'json', `/operationcenter/channleAgent/resource/${channelId}/supportArea`, {}, function (data) {
//             $.each(data, function (index, province) {
//                 content += `<optgroup label=${province.provinceName} class=${"province" + province.provinceId}>`;
//                 let cityList = province.cityList;
//                 for (let city of cityList) {
//                     if (areaName == city.cityName) {
//                         content += `<option value=${city.cityId}  selected>${city.cityName}</option>`;
//                     } else {
//                         content += `<option value=${city.cityId} >${city.cityName}</option>`;
//                     }
//
//                 }
//             });
//             $select.html(content);
//             parent.$("#areaSel").unbind("keyup").bind({
//                 click: function () {
//                     var options = $("#areaSel option:selected"); //获取选中的项
//                     var map = new Map();
//                     map.put(options.val(), options.text());
//                     CUI.select.showTag(window.parent.$("#areaSel"), 300, map, false, window.parent.$("#trigger_city"));
//                 }
//             });
//             parent.$("#saveArea").unbind("click").bind({
//                 click: function () {
//                     channelAgent.updateArea(id, channelId);
//                     popup.mask.hideFirstMask(false);
//                 }
//             });
//         }, function () {
//         });
//     },
//     updateArea: function (id, channelId) {
//         var baseInfo = {
//             id: '',
//             area: '',
//             channelId: '',
//         }
//         baseInfo.id = id;
//         for (var i = 0; i < $(".areaId").length; i++) {
//             if ($(".areaId")[i].value == "全部") {
//                 baseInfo.channelId = channelId;
//             } else {
//                 baseInfo.area += ($(".areaId")[i].value + ",");
//             }
//         }
//         common.getByAjax(true, "post", "json", "/operationcenter/channleAgent/updateArea", baseInfo,
//             function (data) {
//                 if (data.pass) {
//                     window.location.reload();
//                 } else {
//                     alert(data.message);
//                     popup.mould.popTipsMould("修改渠道业务地区异常！！", popup.mould.first, popup.mould.error, "", "53%", null);
//                 }
//             },
//             function () {
//                 popup.mould.popTipsMould("发生异常,获取不到data,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
//             }
//         );
//     },
//查看所属城市 --
    initAreaList: function (id) {
        common.getByAjax(true, "get", "json", "/operationcenter/channleAgent/areaList", {id: id},
            function (data) {
                if (data) {
                    var content = `<br><b><span>支持城市：</span></b>`;
                    content += `<p></p>`;
                    $.each(data, function (index, province) {
                        content += `<b><span style="margin-left: 25px">${province.provinceName}：</span></b>`;
                        content += `<p align="left" style="margin-left: 45px">`;
                        let cityList = province.cityList;
                        for (let city of cityList) {
                            // content += `<option value=${city.cityId} >${city.cityName}</option>`;
                            content += `<span>${city.cityName}</span>&emsp;`;
                        }
                        content += `</p>`;
                        content += "<p></p>";
                    });
                    parent.$("#areaNameList").append(content);
                }
            },
            function () {
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    selectAreaList: function (id) {
        popup.pop.popInput(channelAgent.areaContent, 'first', "550px", "280px", "50%", "55%");
        channelAgent.initAreaList(id);
        parent.$("#close").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
        parent.$("#yes").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
    },
    initArea: function () {
        var detailContent = $("#show_area_content");
        if (detailContent.length > 0) {
            channelAgent.areaContent = detailContent.html();
            detailContent.remove();
        }
    },

    batchAddPop: function () {
        $("#createInvitationCode").on('click', function () {
                $.get('/views/userManager/invite_code_apply.html', function (content) {
                    layer.open({
                        type: 1,
                        title: '批量生成邀请码',
                        skin: 'layui-layer-rim', //加上边框
                        area: ['50%', '60%'], //宽高
                        content: content,
                        scrollbar: false,
                        success: function (layero, index) {
                            $(layero).find('.popDiv').data('layer-index', index);
                        }
                    });
                })
            }
        );
    }
}

var datatables = datatableUtil.getByDatatables(channelAgentList, dataFunction.data, dataFunction.fnRowCallback);
$(function () {
    if (!common.permission.validUserPermission('op1001')) {
        return;
    }
    var approve = document.getElementById("approveStatusReminder");
    approve.style.display = "none";
    channelAgent.agentLevelList();
    channelAgent.initChannelList();
    channelAgent.chgList();
    channelAgent.batchAddPop();
    channelAgent.initArea();
    // channelAgent.initEditArea();
    channelAgent.approveStatusList();
    channelAgent.changeApproveStatus();
});
