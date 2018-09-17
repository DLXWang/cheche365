/**
 * Created by wangfei on 2015/10/14.
 */
var dataFunction = {
    "data": function (data) {
        data.visited = $("#visited").val();
        data.channel = $("#channel").val();
        switch (datatableUtil.params.keyType) {
            case "" :
                break;
            case "1":
                data.mobile = datatableUtil.params.keyWord;
                break;
            case "2":
                data.licensePlateNo = datatableUtil.params.keyWord;
                break;
        }
    },
    "fnRowCallback": function (nRow, aData) {
        $id = common.getOrderIcon(aData.channelIcon) + common.checkToEmpty(aData.id);
        $visited = "<span id='visit_status_id_" + aData.id + "' style='color: " + (aData.visited ? "green" : "red") + "' >" + (aData.visited ? "已回访" : "需回访") + "</span>";
        $comment = "<a href=\"javascript:;\" onclick=\"applicationLog.popCommentList('quote_phone'," + aData.id + ",'first');\">查看备注</a>";
        $operation = "<a class='" + (aData.visited ? "none" : "") + "' id='quote_phone_visited" + aData.id + "' style='margin-right: 10px;color: green' href='javascript:;' onclick=listQuote.editQuote.changeStatus(" + aData.id + "," + 1 + ")>已回访</a>" +
            "<a class='" + (aData.visited ? "" : "none") + "'id='quote_phone_visit" + aData.id + "' style='margin-right: 10px;color: red' href='javascript:;' onclick=listQuote.editQuote.changeStatus(" + aData.id + "," + 0 + ")>需回访</a>" +
            "<a style=\"margin-left: 10px;\" href='javascript:;' onclick=listQuote.detailQuote.findDetailPage(" + aData.id + ")>详细信息</a>";
        $('td:eq(0)', nRow).html($id);
        $('td:eq(5)', nRow).html($visited);
        $('td:eq(6)', nRow).html($comment);
        $('td:eq(7)', nRow).html($operation);
    }
};
var listQuote = {
    "url": '/orderCenter/quote/phone',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": null, "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "userId", "title": "用户ID", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "encyptMobile", "title": "用户手机号", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "licensePlateNo", "title": "车牌号", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "createTime", "title": "提交时间", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
        {"data": null, "title": "回访状态", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "180px"}
    ],
    newContent: "",
    autoContent: "",
    quoteContentUrl:"quote_phone_pop.html",
    page: new Properties(1, ""),
    init: {
        initPopupContent: function () {
            var newContent = $("#new_content");
            if (newContent.length > 0) {
                listQuote.newContent = newContent.html();
                newContent.remove();
            }
        },
        //initAutoContent: function () {
        //    var autoContent = $("#auto_content");
        //    if (autoContent.length > 0) {
        //        listQuote.autoContent = autoContent.html();
        //        autoContent.remove();
        //    }
        //},
        //initSourceChannel: function (type) {
        //    common.getByAjax(true, "get", "json", "/orderCenter/quote/phone/channels", {},
        //        function (data) {
        //            if (data) {
        //                var options = "";
        //                $.each(data, function (i, model) {
        //                    if (model.id <= 10) {
        //                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
        //                    }
        //                });
        //                if (type == 0) {
        //                    $("#channel").append(options);
        //                } else {
        //                    parent.$("#sourceChannel").append(toChgQuoteAutoInfooptions);
        //                }
        //            }
        //        }, function () {
        //        }
        //    );
        //},

        //增：修改页面条件查询渠道来源
        initSearchChannel: function (type) {
            listQuote.init.getChannelsByUrl(type,"/orderCenter/resource/channel/getOrderChannels");
        },

        //修改页面条件查询enable渠道来源
        initSearchChannelEnable: function (type) {
            listQuote.init.getChannelsByUrl(type,"/orderCenter/resource/channel/getOrderChannelsEnable");
        },

        getChannelsByUrl: function (type,url) {
            common.getByAjax(false, "get", "json", url, {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                        });
                        if (type == 0) {
                            $("#channel").append(options);
                        } else {
                            parent.$("#sourceChannel").append(options);
                        }
                    }
                },
                function () {
                }
            )
        },
    },
    newQuote: {
        popup: function () {
            listQuote.init.initPopupContent();
            popup.pop.popInput(false, listQuote.newContent, popup.mould.first, "600px", "120px", "50%", "55%");
            window.parent.$("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            window.parent.$("#popover_normal_input .form-input .toSearch").unbind("click").bind({
                click: function () {
                    var licensePlateNo = window.parent.$("#popover_normal_input #licensePlateNo").val();
                    if (common.isEmpty(licensePlateNo) || !common.validateLicenseNo(licensePlateNo)) {
                        window.parent.$("#createError").find("span").html("车牌号格式错误");
                        window.parent.$("#createError").show().delay(2000).hide(0);
                        return false;
                    }
                    window.parent.$("#popover_normal_input .diy-height").show();
                    window.parent.$("#popover_normal_input").height("580px").width("850px").css("top", "36%").css("left", "52%");
                    window.parent.$("#popover_normal_input .btn-finish").show();
                    quote_help.auto.getAutoByLicensePlateNo(licensePlateNo, function (data) {
                        if (data.phones.length > 0 || data.photos.length > 0) {
                            var phones = data.phones;
                            var photos = data.photos;
                            if (phones && phones.length > 0) {
                                listQuote.newQuote.getPhonesList(phones);
                            }
                            if (photos && photos.length > 0) {
                                listQuote.newQuote.getPhotosList(photos);
                            }
                        } else {
                            window.parent.$(" .none-not-find").show();
                            window.parent.$(" .none-content").hide();
                        }
                    });
                }
            });
            window.parent.$("#popover_normal_input .form-input .toCreate").unbind("click").bind({
                click: function () {
                    $.post(listQuote.quoteContentUrl, {}, function (detailContent) {
                        window.parent.$("#detail_userId").parent().parent().hide();
                        var licensePlateNo = window.parent.$("#popover_normal_input #licensePlateNo").val();
                        if (common.isEmpty(licensePlateNo) || !common.validateLicenseNo(licensePlateNo)) {
                            window.parent.$("#createError").find("span").html("车牌号格式错误");
                            window.parent.$("#createError").show().delay(2000).hide(0);
                            return false;
                        }
                        popup.pop.popInput(false, detailContent, popup.mould.first, "580px", "580px", "33%", "52%");
                        listQuote.init.initSearchChannelEnable(1);
                        quote_help.getIdentityTypes("input_identityType","input_insuredIdType");
                        window.parent.$("#toEdit").hide();
                        window.parent.$("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                            click: function () {
                                popup.mask.hideFirstMask(false);
                            }
                        });
                        quote_help.auto.getAutoContent(licensePlateNo, "input", function () {
                            window.parent.$("#popover_normal_input .form-input .toSave").unbind("click").bind({
                                click: function () {
                                    listQuote.saveQuote.save('first');
                                }
                            });
                            window.parent.$("#popover_normal_input .form-input .toChgQuoteAutoInfo").unbind("click").bind({
                                click: function () {
                                    var owner = window.parent.$("#input_owner").val();
                                    var identityType = window.parent.$("#input_identityType").val();
                                    var identity = window.parent.$("#input_identity").val();
                                    quote_help.auto.getAutoContentByNameId(licensePlateNo, "input", owner, identityType, identity);
                                }
                            });
                            quote_help.auto.getAutoContentByNameId(licensePlateNo, "input", null, null, null);
                        });
                        window.parent.$("input:radio[name='insuredType']").eq(0).attr("checked", 'checked');
                        quote_help.auto.setReadOnly("");
                        window.parent.$("input:radio[name='insuredType']").unbind("change").bind({
                            change: function () {
                                if ($(this).val() == "1") {
                                    quote_help.auto.setReadOnly("");
                                } else {
                                    quote_help.auto.setEdit("");
                                }
                            }
                        });
                        listQuote.getselect2.get2();
                    })
                }
            });
        },

        getPhonesList: function (phones) {
            var phoneContent = "";
            $.each(phones, function (i, model) {
                phoneContent += "<tr class=\"text-center\" id='tab_tr" + model.id + "'>" +
                    "<td>" + common.getOrderIconByData(model.channelIcon, model.id) + "</td>" +
                    "<td>" + common.checkToEmpty(model.userId) + "</td>" +
                    "<td>" + common.checkToEmpty(model.mobile) + "</td>" +
                    "<td>" + common.checkToEmpty(model.licensePlateNo) + "</td>" +
                    "<td>" + common.checkToEmpty(model.createTime) + "</td>" +
                    "<td>" + quote_help.getVisitState(model.visited) + "</td>" +
                    "<td><a id='phoneComment" + model.id + "'><input type='button' class='btn btn-danger btn-sm' value='查看'></a></td>" +
                    "<td><a id='seePhoneDetail" + model.id + "'><input type='button' class='btn btn-danger btn-sm' value='查看'></a></td>" +
                    "</tr>";
            });
            window.parent.$("#popover_normal_input #phone_tab tbody").empty();
            window.parent.$("#popover_normal_input #phone_tab tbody").append(phoneContent);
            window.parent.parent.$("[id^='phoneComment']").unbind("click").bind({
                click: function () {
                    applicationLog.popCommentList("quote_phone", this.id.replace('phoneComment', ''), popup.mould.second);
                }
            });
            window.parent.parent.$("[id^='seePhoneDetail']").unbind("click").bind({
                click: function () {
                    listQuote.detailQuote.findDetailPage(this.id.replace('seePhoneDetail', ''), 'second');
                }
            });
            window.parent.$("#popover_normal_input .none-content").show();
            window.parent.$("#popover_normal_input .none-not-find").hide();
        },
        getPhotosList: function (photos) {
            var photoContent = "";
            $.each(photos, function (i, model) {
                photoContent += "<tr class=\"text-center\">" +
                    "<td>" + common.getOrderIconByData(model.channelIcon, model.id) + "</td>" +
                    "<td>" + common.checkToEmpty(model.userId) + "</td>" +
                    "<td>" + common.checkToEmpty(model.mobile) + "</td>" +
                    "<td>" + common.checkToEmpty(model.licensePlateNo) + "</td>" +
                    "<td>" + common.checkToEmpty(model.userImg) + "</td>" +
                    "<td>" + common.checkToEmpty(model.createTime) + "</td>" +
                    "<td>" + quote_help.getdisableState(model.disable) + "</td>" +
                    "<td>" + quote_help.getVisitState(model.visited) + "</td>" +
                    "<td><a id='photoComment" + model.id + "'><input type='button' class='btn btn-danger btn-sm' value='查看'></a></td>" +
                    "<td><a id='seePhotoDetail" + model.id + "'><input type='button' class='btn btn-danger btn-sm' value='查看'></a></td>" +
                    "</tr>";
            });
            window.parent.$("#popover_normal_input #photo_tab tbody").empty();
            window.parent.$("#popover_normal_input #photo_tab tbody").append(photoContent);
            window.parent.parent.$("[id^='photoComment']").unbind("click").bind({
                click: function () {
                    applicationLog.popCommentList("quote_photo", this.id.replace('photoComment', ''), popup.mould.second);
                }
            });
            window.parent.parent.$("[id^='seePhotoDetail']").unbind("click").bind({
                click: function () {
                    quote_photo_pop.quoteDetail.popup(this.id.replace('seePhotoDetail', ''), 'second');
                    window.parent.$("#toEdit").hide();
                }
            });
            window.parent.$("#popover_normal_input .none-content").show();
            window.parent.$("#popover_normal_input .none-not-find").hide();
        }
    },
    editQuote: {
        changeStatus: function (id, visited) {
            common.getByAjax(false, "put", "json", "/orderCenter/quote/phone/visited/" + id, {visited: visited},
                function (data) {
                    if (data.pass) {
                        if (visited == 0) {
                            $("#visit_status_id_" + id).css({'color': 'red'}).html("需回访");
                            $("#quote_phone_visited" + id).show();
                            $("#quote_phone_visit" + id).hide();
                        } else {
                            $("#visit_status_id_" + id).css({'color': 'green'}).html("已回访");
                            $("#quote_phone_visited" + id).hide();
                            $("#quote_phone_visit" + id).show();
                        }
                    } else {
                        popup.mould.popTipsMould(false, data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function () {
                    popup.mould.popTipsMould(false, "操作失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    detailQuote: {
        findDetailPage: function (id, popupInput) {
            $.post(listQuote.quoteContentUrl, {}, function (detailContent) {
                var mouldVal;
                if (popupInput == 'second') {
                    mouldVal = popup.mould.second;
                } else {
                    mouldVal = popup.mould.first;
                }
                popup.pop.popInput(false, detailContent, mouldVal, "580px", "580px", "33%", "52%");
                listQuote.init.initSearchChannel(1);//渲染产品平台
                quote_help.getIdentityTypes("input_identityType","input_insuredIdType");
                window.parent.$("#detail_userId").parent().parent().parent().show();
                window.parent.$("#sourcechannel").select2({
                    dropdownCss:{'z-index':9999}
                });
                window.parent.$("#quote_phone_edit_close").unbind("click").bind({
                    click: function () {
                        if (popupInput == 'second') {
                            popup.mask.hideSecondMask(false);
                        } else {
                            popup.mask.hideFirstMask(false);
                        }
                    }
                });
                listQuote.detailQuote.getDetailQuote(popupInput, id, "text");
                window.parent.$("#toSave1").hide();
            })
        },
        getDetailQuote: function (popupInput, id, mark) {
            if (id) {
                window.parent.$(".toChgQuoteAutoInfo").hide();
            }
            common.getByAjax(false, "get", "json", "/orderCenter/quote/phone/" + id, {},
                function (data) {
                    quote_help.auto.fixAutoContent(data, "text", "", popupInput);
                    parent.$(".toEdit").unbind("click").bind({
                        click: function () {
                            quote_help.auto.fixAutoContent(data, "input", "", popupInput);
                            window.parent.$("#user_id").hide();
                            window.parent.$("#user_mobile").hide();
                            window.parent.$("#input_identityType").attr("disabled", false);
                            window.parent.$("#input_insuredIdType").attr("disabled", false);
                            var sourceChannel = window.parent.$("#sourceChannel").val();
                            window.parent.$("#sourceChannel").html('');
                            listQuote.init.initSearchChannelEnable(1);
                            window.parent.$("#sourceChannel").val(sourceChannel);
                            window.parent.$("#sourceChannel").attr("disabled", false);
                            window.parent.$("#toEdit").hide();
                            window.parent.$("#toSave1").show();
                            window.parent.$(".toChgQuoteAutoInfo").show();
                            window.parent.$("#popover_normal_input .form-input .toChgQuoteAutoInfo").unbind("click").bind({
                                click: function () {
                                    var owner = window.parent.$("#input_owner").val();
                                    var identityType = window.parent.$("#input_identityType").val();
                                    var identity = window.parent.$("#input_identity").val();
                                    var licensePlateNo = window.parent.$("#popover_normal_input #detail_licensePlateNo").text();
                                    quote_help.auto.getAutoContentByNameId(licensePlateNo, "input", owner, identityType, identity);
                                }

                            });
                            listQuote.getselect2.get2();
                        }
                    });
                    window.parent.$(".toSave").unbind("click").bind({
                        click: function () {
                            listQuote.saveQuote.save(popupInput);
                        }
                    });
                    var input;
                    if (popupInput == 'second') {
                        input = window.parent.$("#popover_normal_input_second");
                    } else {
                        input = window.parent.$("#popover_normal_input");
                    }
                    input.find(".toQuote").unbind("click").bind({
                        click: function () {
                            window.open("/page/quote/quote.html?source=phone&id=" + id);
                        }
                    });
                    input.find(".toRenewal").unbind("click").bind({
                        click: function () {
                            var renewalValidation = quote_help.checkRenewal(data);
                            if (!renewalValidation.flag) {
                                window.parent.$("#errorText").text(renewalValidation.msg);
                                window.parent.$(".error-msg").show().delay(2000).hide(0);
                                return;
                            }
                            window.open("/page/quote/quote.html?renewalFlag=1&source=phone&id=" + id);
                        }
                    });
                },
                function () {
                    popup.mould.popTipsMould(false, "获取车辆信息异常！", popup.mould.second, popup.mould.error, "", "", null);
                }
            );
        }
    },
    getselect2:{
      get2:function () {
          window.parent.$("#sourceChannel").select2({dropdownCss:{'z-index':9999}});
          return;
      }
    },
    saveQuote: {
        save: function (popupInput) {
            if (!quote_help.quoteValidate.validateQuote()) {
                return;
            }
            $(this).attr("disabled", true);
            var form = window.parent.$("#popover_normal_input #phone_auto_form");
            if (popupInput == 'second') {
                form = window.parent.$("#popover_normal_input_second #phone_auto_form");
            }
            common.getByAjax(false, "post", "json", "/orderCenter/quote/phone", form.serialize(),
                function (data) {
                    window.parent.$(".toChgQuoteAutoInfo").hide();
                    window.parent.$("#errorT").hide();
                    if (popupInput == 'second') {
                        popup.mould.popTipsMould(false, "保存成功！", popup.mould.second, popup.mould.success, "", "", function () {
                            listQuote.saveQuote.success(data, popupInput);
                        });
                    } else {
                        popup.mould.popTipsMould(false, "保存成功！", popup.mould.first, popup.mould.success, "", "", function () {
                            listQuote.saveQuote.success(data, popupInput);
                        });
                    }
                    listQuote.page.currentPage = 1;
                    listQuote.page.keyword = $("#keyword").val();
                },
                function () {
                    $(this).attr("disabled", false);
                    popup.mould.popTipsMould(false, "保存电话报价车辆信息异常！", popup.mould.second, popup.mould.error, "", "", null);
                }
            );
        },
        success: function (data, popupInput) {
            var tips = window.parent.$("#popover_normal_tips");
            if (popupInput == "second") {
                tips = window.parent.$("#popover_normal_tips_second").hide();
            }
            tips.hide();
            window.parent.$("#toEdit").show();
            window.parent.$("#toSave1").hide();
            quote_help.auto.fixAutoContent(data, "text", "", popupInput);
            parent.$(".toEdit").unbind("click").bind({
                click: function () {
                    quote_help.auto.fixAutoContent(data, "input", "", popupInput);
                    window.parent.$("#input_identityType").attr("disabled", false);
                    window.parent.$("#input_insuredIdType").attr("disabled", false);
                    window.parent.$("#sourceChannel").attr("disabled", false);
                    window.parent.$("#toEdit").hide();
                    window.parent.$("#toSave1").show();
                    window.parent.$(".toChgQuoteAutoInfo").show();
                }
            });
            parent.$(".toQuote").unbind("click").bind({
                click: function () {
                    window.open("/page/quote/quote.html?source=phone&id=" + data.id);
                }
            });
            parent.$(".toRenewal").unbind("click").bind({
                click: function () {
                    var renewalValidation = quote_help.checkRenewal(data);
                    if (!renewalValidation.flag) {
                        window.parent.$("#errorText").text(renewalValidation.msg);
                        window.parent.$(".error-msg").show().delay(2000).hide(0);
                        return;
                    }
                    window.open("/page/quote/quote.html?renewalFlag=1&source=phone&id=" + data.id);
                }
            });
        }
    }
};

var datatables = datatableUtil.getByDatatables(listQuote, dataFunction.data, dataFunction.fnRowCallback);

$(function () {
    //listQuote.init.init();
    listQuote.init.initSearchChannel(0);
    quote_help.getIdentityTypes("input_identityType","input_insuredIdType");
    $("#channel").val(11);//默认出单中心
    $("#toNew").unbind("click").bind({
        click: function () {
            listQuote.newQuote.popup();
        }
    });

    /* 搜索 */
    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            datatableUtil.params.keyWord = keyword;
            datatableUtil.params.keyType = $("#keyType").val();
            datatables.ajax.reload();
        }
    });

    $("#visited,#channel").unbind("change").bind({
        change: function () {
            datatables.ajax.reload();

        }
    });
});

$(document).ready(function() {
    $('#channel').select2();
});

