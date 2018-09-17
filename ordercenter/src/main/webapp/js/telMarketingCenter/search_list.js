/**
 * Created by lyh on 2015/11/16.
 */
var search_list = {
    detailContent: "",
    autoContent: "",
    quoteListContent: "",
    editType: "",
    quoteContentUrl:"/page/quote/quote_phone_pop.html",
    page: new Properties(1, ""),
    //参数集合，后期将用到的参数转为map
    param:new Map(),
    init: {
        init: function () {
            this.initPopupContent();
        },
        initPopupContent: function () {
            var detailContent = $("#detail_content");
            if (detailContent.length > 0) {
                search_list.detailContent = detailContent.html();
                detailContent.remove();
            }
        },
        initSecondContent: function () {
            var quoteListContent = $("#new_content");
            if (quoteListContent.length > 0) {
                search_list.quoteListContent = quoteListContent.html();
                quoteListContent.remove();
            }
        },
        //增：修改页面条件查询渠道来源
        initSearchChannel: function (type) {
            search_list.init.getChannelsByUrl(type,"/orderCenter/resource/channel/getOrderChannels");
        },
        //修改页面条件查询enable渠道来源
        initSearchChannelEnable: function (type) {
            search_list.init.getChannelsByUrl(type,"/orderCenter/resource/channel/getOrderChannelsEnable");
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
                            //$("#channelType").append(options);
                        } else {
                            parent.$("#sourceChannel").append(options);
                        }
                        if(search_list.param.get("activeUrlId") != null){
                            $("#sourceChannel").val(11).hide().after("出单中心");

                        }
                    }

                },
                function () {
                }
            )
        },
    },
    searchList: {
        list: function (type) {
            common.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/phones",
                {
                    currentPage: search_list.page.currentPage,
                    pageSize: search_list.page.pageSize,
                    mobileNo: $("#mobilePhone").val()
                },
                function (data) {
                    $("#list_tab tbody").empty();
                    search_list.editType = type;
                    search_list.searchList.fillListContent(data);
                    if (data.pageInfo.totalElements < 1) {
                        $("#mobileCount").text("0");
                        $("#customer_pagination_idv").show();
                    }
                    $("#mobileCount").text(data.pageInfo.totalElements);
                    $("#mobileNo").text($("#mobilePhone").val());
                    if (data.pageInfo.totalPage > 1) {
                        $("#customer_pagination_idv").show();
                        $.jqPaginator('#pagination_ul',
                            {
                                totalPages: data.pageInfo.totalPage,
                                visiblePages: search_list.page.visiblePages,
                                currentPage: search_list.page.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType == "change") {
                                        search_list.page.currentPage = pageNum;
                                        search_list.searchList.list(type);
                                        common.scrollToTop();
                                    }
                                }
                            }
                        );
                    } else {
                        $("#customer_pagination_idv").hide();
                    }
                }, function () {
                    popup.mould.popTipsMould(false, "获取信息列表异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        fillListContent: function (data) {
            var editType = search_list.editType;
            var content = "";
            $.each(data.viewList, function (n, view) {
                content += "<tr class='text-center'>" +
                    "<td>" + common.getOrderIconByData(view.channelIcon, view.mobile) + "</td>" +
                    "<td>" + common.checkToEmpty(view.name) + "</td>" +
                    "<td>" + common.checkToEmpty(view.expireTime) + "</td>" +
                    "<td>" + common.checkToEmpty(view.sourceName) + "</td>" +
                    "<td>" + common.checkToEmpty(view.dealResult) + "</td>" +
                    "<td>" + common.checkToEmpty(view.operator) + "</td>" +
                    "<td><a style=\"margin-left: 10px;\" href='/page/telMarketingCenter/search_list.html?clickType=purposeCustomer&editType=" + editType + "&id=" + view.id + "&hisId=" + view.hisId + "' target='_blank')>编辑</a></td>" +
                    "</tr>";
            });

            $("#search_list_tab tbody").empty();
            $("#detail_div").show();
            $("#chart_div").hide();
            $("#search_list_tab tbody").append(content);
            common.scrollToTop();
        }

    },
    detailInfo: {
        showTriggerTimeStatus: [1, 20, 30, 40, 70, 90, 91, 3, 4, 95],//显示触发时间的状态
        requiredTriggerTimeStatus: [20, 30, 40, 4],//必填触发时间的状态
        showTrigerOrdernoStatus: [60],//必填出发订单号的状态
        showTriggerCityStatus: [50],//显示触发城市的状态
        init: function (id, hisId, editType) {//0表示从优先和正常列表编辑，1从意向客户搜索编辑，2从工作查看编辑
            if (!id)
                return;

            if (editType == 1) {
                if (!common.permission.validUserPermission("or060103")) {
                    return;
                }
            } else if (editType == 2) {
                if (!common.permission.validUserPermission("or060211")) {
                    return;
                }
            }
            search_list.detailInfo.getContent(id, hisId, editType);
            window.parent.$("#result_detail").unbind("keyup").bind({
                keyup: function () {
                    if (common.isEmpty($(this).val())) {
                        CUI.select.hide();
                        $("#result_detail").val("");
                        return;
                    }
                    common.getByAjax(true, "get", "json", "/orderCenter/resource/areas/getByKeyWord",
                        {
                            keyword: $(this).val()
                        },
                        function (data) {
                            if (data == null) {
                                return;
                            }
                            var map = new Map();
                            $.each(data, function (i, model) {
                                map.put(model.id, model.name);
                            });
                            CUI.select.show(window.parent.$("#result_detail"), 300, map, false, window.parent.$("#trigger_city"));
                        }
                    );
                }
            })
            window.parent.$("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            window.parent.$("#handleResultSel").unbind("change").bind({
                change: function () {
                    window.parent.$("#trigger_time").val('');
                    window.parent.$("#result_detail").val('');
                    window.parent.$("#order_no").val('');
                    window.parent.$("#triggerTimeDiv").hide();
                    window.parent.$("#triggerCityDiv").hide();
                    window.parent.$("#triggerOrdernoDiv").hide();
                    if (search_list.detailInfo.showTriggerTimeStatus.indexOf(this.value) > -1) {
                        //显示触发时间
                        window.parent.$("#triggerTimeDiv").show();
                    } else if (search_list.detailInfo.showTriggerCityStatus.indexOf(this.value) > -1) {
                        //显示城市
                        window.parent.$("#triggerCityDiv").show();
                    } else if (search_list.detailInfo.showTrigerOrdernoStatus.indexOf(this.value) > -1) {
                        //显示订单号
                        window.parent.$("#triggerOrdernoDiv").show();
                    }
                }
            });
        }
        ,
        getContent: function (id, hisId) {
            var url = "/orderCenter/telMarketingCenter/" + id;
            common.getByAjax(true, "get", "json", url, {hisId: hisId},
                function (data) {
                    search_list.detailInfo.getHandleStatus(data.statusId);//处理结果下拉列表

                    if (data.autoInfoList && data.autoInfoList.length > 0) {
                        search_list.detailInfo.getAutoList(data.autoInfoList);//获取号码关联车辆信息
                    }

                    if (data.dealHisList && data.dealHisList.length > 0) {
                        search_list.detailInfo.getHandleResultList(data.dealHisList);//联系状况列表
                    }

                    search_list.detailInfo.getRepeatList(data.repeatList, data.sourceCreateTime, data.sourceName, id, data.mobile, data.sourceId);//联系状况列表//获取号码存在记录，此号码参加过哪些资源

                    var userInvitation = data.invited ? "&nbsp;&nbsp;<span style='color:red;'>推荐</span>" : "";
                    window.parent.$("#tmc_id").val(data.id);
                    window.parent.$("#span_mobile").html(data.mobile + userInvitation);
                    window.parent.$("#span_name").text(common.checkToEmpty(data.userName));
                    window.parent.$("#expire_time").val(data.expireTime);
                    window.parent.$("#span_deal_times").text(data.processedNumber);
                    window.parent.$("#span_source").text(common.checkToEmpty(data.sourceName));
                    window.parent.$("#span_in_sys_time").text(common.checkToEmpty(data.createTime));
                    window.parent.$("#handleResultSel").val(data.statusId);
                    if (search_list.detailInfo.showTriggerTimeStatus.indexOf(data.statusId) > -1) {
                        window.parent.$("#trigger_time").val(data.triggerTime);
                        window.parent.$("#triggerTimeDiv").show();
                    } else if (search_list.detailInfo.showTriggerCityStatus.indexOf(data.statusId) > -1) {
                        window.parent.$("#triggerCityDiv").show();
                    } else if (search_list.detailInfo.showTrigerOrdernoStatus.indexOf(data.statusId) > -1) {
                        window.parent.$("#triggerOrdernoDiv").show();
                    }
                    window.parent.$("#toSave").unbind("click").bind({
                        click: function () {
                            search_list.detailInfo.savePurposeCustomer();
                        }
                    });
                    //window.parent.$("#toQuoteDIV").unbind("click").bind({
                    //    click: function() {
                    //
                    //    }
                    //});
                    //window.parent.$("#sendSMSDIV").unbind("click").bind({
                    //    click: function() {
                    //        search_list.relateAction.sendSMS(data.id);
                    //    }
                    //});
                    search_list.detailInfo.setLinkClick(id, data.mobile, data.sourceId);
                }, function () {
                    popup.mould.popTipsMould(false, "获取数据异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        }
        ,
        setLinkClick: function (centerId, mobile, sourceId) {
            window.parent.$(".source-tr").unbind("click").bind({
                click: function () {
                    $(this).css("background-color", "#f5f5f5").siblings("tr").css("background-color", "#fff");
                    search_list.detailInfo.changeAutoList($(this).attr("value"));
                }
            });
            window.parent.$(".quote").unbind("click").bind({
                click: function () {
                    if ($(this).attr("sourceTable") == 'quote_photo') {
                        quote_photo_pop.quoteDetail.popup($(this).attr("sourceId"), 'first');
                    }else{
                        search_list.detailInfo.changeAutoList($(this).attr("value"));
                        var licenseNoSelName = window.parent.$("#licenseNoSel  option:selected").text();
                        var phoneNo = window.parent.$("#span_mobile").text();
                        var owner = window.parent.$("#owner").text();
                        var engineNo = window.parent.$("#engine_no").text();
                        var vinNo = window.parent.$("#vin_no").text();
                        var identityCard = window.parent.$("#identity_card").text();
                        var enrollDate = window.parent.$("#enroll_date").text();
                        var autoTypeCode = window.parent.$("#auto_type_code").text();
                        //清除广告来源的参数，根据情况重新赋值
                        search_list.param.remove("activeUrlId");
                        var activeUrlId = $(this).attr("activeUrlId");
                        if(activeUrlId != "null")
                            search_list.param.put("activeUrlId", activeUrlId);
                        search_list.relateAction.quoteOperation(centerId, mobile, licenseNoSelName, phoneNo, owner, engineNo, vinNo, identityCard, enrollDate, autoTypeCode, sourceId);
                    }
                }
            });
            window.parent.$(".quote_detail").unbind("click").bind({
                click: function () {
                    var repeatId = $(this).attr("value");
                    window.open("quote_record_history_detail.html?repeatId=" + repeatId);
                }
            })
        },
        changeAutoList: function (repeatId) {
            common.getByAjax(false, "get", "json", "/orderCenter/telMarketingCenter/auto/" + repeatId, {},
                function (data) {
                    search_list.detailInfo.getAutoList(data);
                },
                function () {
                    search_list.detailInfo.fillEmptyAutoContent();
                }
            );
        },
        getAutoList: function (autoList) {
            if (autoList.length == 0) {
                search_list.detailInfo.fillEmptyAutoContent();
                return;
            }
            var options = "";
            if(common.isEmpty(common.getUrlParam("autoId"))){
                $.each(autoList, function (i, autoInfo) {
                    options += "<option value=\"" + autoInfo.id + "\">" + autoInfo.licensePlateNo + "</option>";
                    if (i == 0) {//车牌号比较多的情况下，默认显示第一个车牌号相关信息
                        search_list.detailInfo.fillAutoContent(autoInfo);
                    }
                });
                window.parent.$("#licenseNoSel").html(options);
            }else{//如果有autoId信息 按照autoId显示
                $.each(autoList, function (i, autoInfo) {
                    options += "<option value=\"" + autoInfo.id + "\">" + autoInfo.licensePlateNo + "</option>";
                    if (autoInfo.id == common.getUrlParam("autoId")) {//车牌号比较多的情况下，默认显示第一个车牌号相关信息
                        search_list.detailInfo.fillAutoContent(autoInfo);
                    }
                });
                window.parent.$("#licenseNoSel").html(options);
                window.parent.$("#licenseNoSel").val(common.getUrlParam("autoId"));
            }

            window.parent.$("#licenseNoSel").unbind("change").bind({
                change: function () {
                    var selVal = this.value;
                    $.each(autoList, function (i, autoInfo) {
                        if (selVal == autoInfo.id) {
                            search_list.detailInfo.fillAutoContent(autoInfo);
                            return;
                        }
                    })
                }
            });
        }
        ,

        fillAutoContent: function (autoInfo) {
            window.parent.$("#enroll_date").text(common.checkToEmpty(autoInfo.enrollDate));
            window.parent.$("#owner").text(common.checkToEmpty(autoInfo.owner));
            window.parent.$("#identity_card").text(common.checkToEmpty(autoInfo.identity));
            window.parent.$("#engine_no").text(common.checkToEmpty(autoInfo.engineNo));
            window.parent.$("#vin_no").text(common.checkToEmpty(autoInfo.vinNo));
            window.parent.$("#auto_type_code").text(common.checkToEmpty(autoInfo.brandCode));

            window.parent.$("#mongoSeats").text(common.checkToEmpty(autoInfo.seats));
            window.parent.$("#mongoCompulsoryEndDate").text(common.checkToEmpty(autoInfo.compulsoryExpireDate));//交强
            window.parent.$("#mongoCommercialEndDate").text(common.checkToEmpty(autoInfo.commercialExpireDate));//商业
        }
        ,
        fillEmptyAutoContent: function () {
            window.parent.$("#licenseNoSel").empty();
            window.parent.$("#enroll_date").text("");
            window.parent.$("#owner").text("");
            window.parent.$("#identity_card").text("");
            window.parent.$("#engine_no").text("");
            window.parent.$("#vin_no").text("");
            window.parent.$("#auto_type_code").text("");
        }
        ,
        getHandleStatus: function (statusId) {
            common.getByAjax(true, "get", "json", "/orderCenter/resource/telMarketingStatus", {},
                function (data) {
                    if (data) {
                        var options = "";
                        $.each(data, function (i, model) {
                            if (model.id == statusId) {
                                options += "<option value=\"" + model.id + "\" selected>" + model.name + "</option>";
                            } else {
                                options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                            }
                        });
                        window.parent.$("#handleResultSel").append(options);
                    }
                },
                function () {
                }
            );
        }
        ,
        getHandleResultList: function (dealHisList) {
            var hisContent = "";
            $.each(dealHisList, function (i, view) {
                hisContent += "<tr class='text-center'>" +
                    "<td style='width:200px'><span title='" + ((view.comment == null) ? "" : view.comment.replace(/\\r\\n/g, '\n')) + "'>"
                    + common.getFormatComment((view.comment == null) ? "" : view.comment.replace(/\\r\\n/g, ''), 30) + "</span></td>" +
                    "<td>" + common.checkToEmpty(view.dealResult) + "</td>" +
                    "<td>" + view.createTime + "</td>" +
                    "<td>" + common.checkToEmpty(view.resultDetail) + "</td>" +
                    "</tr>";
            });
            window.parent.$("#contact_status_tab tbody").append(hisContent);
        }
        ,
        getRepeatList: function (data, sourceCreateTime, source, centerId, mobile, sourceId) {
            if (data != null) {
                search_list.detailInfo.setRepeatTable(data.viewList);
                if (data.pageInfo.totalPage > 1) {
                    $("#source_page_div").show();
                    $.jqPaginator('#source_page_ul',
                        {
                            totalPages: data.pageInfo.totalPage,
                            visiblePages: search_list.page.visiblePages,
                            currentPage: search_list.page.currentPage,
                            onPageChange: function (pageNum, pageType) {
                                if (pageType == "change") {
                                    search_list.page.currentPage = pageNum;
                                    search_list.detailInfo.getTelMarketingCenterRepeat(centerId, mobile, sourceId);
                                }
                            }
                        }
                    );
                } else {
                    $("#source_page_div").hide();
                }
            }
        },
        getTelMarketingCenterRepeat: function (centerId, mobile, sourceId) {
            common.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/getTelMarketingCenterRepeat",
                {
                    currentPage: search_list.page.currentPage,
                    pageSize: search_list.page.pageSize,
                    centerId: centerId
                },
                function (data) {
                    $("#repeat_list_tab tbody").empty();
                    search_list.detailInfo.setRepeatTable(data.viewList);
                    search_list.detailInfo.setLinkClick(centerId, mobile, sourceId);

                }, function () {
                    popup.mould.popTipsMould(false, "获取来源信息列表异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        setRepeatTable: function (repeatList) {
            var sourceIds = [42, 43, 70, 144];//上年未支付订单中，也需要展示"订单详情",对应的sourceId为144
            var repeatContent = "";
            if (repeatList && repeatList.length > 0) {
                $.each(repeatList, function (i, view) {
                    var operate = "";
                    if (view.source.id == 147) {//报价数据,展示报价详情
                        operate += "<a href='javascript:;' class='quote_detail' sourceTable='" + view.sourceTable + "' sourceId='" + view.sourceId + "' value='" + view.id + "'>报价详情</a>&nbsp;&nbsp;";
                    } else if (sourceIds.indexOf(view.source.id) > -1 && view.orderId != null) {
                        operate += '<a href="/page/order/order_detail.html?id=' + view.orderId + '" target="_blank">订单详情</a>&nbsp;&nbsp;';
                    }
                    operate += "<a href='javascript:;' class='quote' sourceTable='" + view.sourceTable + "' activeUrlId='" + view.activeUrlId + "' sourceId='" + view.sourceId + "' value='" + view.id + "'>我要报价</a>&nbsp;&nbsp;";
                    if (view.source.type == 5)//续保
                        operate += '<a href="/page/quote/quote.html?renewalFlag=1&source=renewInsurance&id=' + view.orderId + '" class="renew_insurance" target="_blank">一键续保</a>&nbsp;&nbsp;';
                    repeatContent += "<tr class='text-center source-tr' value='" + view.id + "'>" +
                        "<td>" + common.getOrderIcon(view.channelIcon) + view.createTime + "</td>" +
                        "<td>" + view.sourceName + "(" + view.channelName + ")" + "</td>" +
                        "<td>" + operate + "</td>" +
                        "</tr>";
                });
                window.parent.$("#repeat_list_tab tbody").append(repeatContent);
            }
        },
        savePurposeCustomer: function() {
            if(!quote_help.quoteValidate.validate("detail_content_errorText")){

                return;
            }
            window.parent.$("#toSave1").attr("disabled",true);
            window.parent.$("#toSave").prop("disabled",true);
            common.getByAjax(true, "post", "json","/orderCenter/telMarketingCenter",window.parent.$("#new_form").serialize(),
                function(data) {
                    if (data.pass) {
                        popup.mould.popTipsMould(false, "保存成功！", popup.mould.first, popup.mould.success, "", "67%", function () {
                            window.opener.location.reload();
                            window.close();
                        });
                        var editType = search_list.editType;//获取编辑类型，空则为意向客户优先列表和正常列表的编辑，1是客户意向搜索出来的编辑，2是工作查看搜索的编辑
                        if (editType == '') {
                            $("#channelSel").val("");
                            $("#typeSel").val("");
                            $('#channelSel').multiselect('deselectAll', false);
                            $('#channelSel').multiselect('updateButtonText');
                            $('#typeSel').multiselect('deselectAll', false);
                            $('#typeSel').multiselect('updateButtonText');
                            $("#areaType").val("");
                            $("#expireTime").val("");
                            $("#expireTime").hide();
                            purpose_customer.allList.list();
                        } else {
                            search_list.searchList.list(editType);
                        }
                    } else {
                        popup.mould.popTipsMould(false, data.message, popup.mould.second, popup.mould.error, "", "67%", null);
                        window.parent.$("#toSave1").attr("disabled",false);
                        window.parent.$("#toSave").prop("disabled",false);
                    }
                },
                function () {
                }
            );
        },
        threewayCall:function(mobile){
            common.getByAjax(true, "get", "json","/orderCenter/telMarketingCenter/telMarketer/call" ,{customerNumber:mobile},
                function(data) {
                    if (data.pass) {
                        //popup.mould.popTipsMould(true, "拨打成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                    } else {
                        popup.mould.popTipsMould(false,data.message, popup.mould.first, popup.mould.error, "", "53%", null);
                        window.parent.$("#toSave").attr("disabled",false);
                    }
                },
                function() {
                    popup.mould.popTipsMould(false,"发生异常,获取不到data,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                    window.parent.$("#toSave").attr("disabled",false);
                }
            );
        },
    },
    relateAction: {
        quoteOperation: function(id, mobile, licenseNoSelName, phoneNo, owner, engineNo, vinNo, identityCard, enrollDate, autoTypeCode,sourceId,content) {
            common.getByAjax(true, "get", "json","/orderCenter/telMarketingCenter/" + id + "/quote",{mobile: mobile},function(data) {},function() {});
            search_list.init.initSecondContent();
            popup.pop.popInput(false, search_list.quoteListContent, popup.mould.first, "600px", "120px", "50%", "55%");
            window.parent.$("#popover_normal_input .diy-height").show();
            window.parent.$("#popover_normal_input").height("580px").width("850px").css("top", "36%").css("left", "52%");
            window.parent.$("#popover_normal_input .btn-finish").show();
            window.parent.$("#licensePlateNo").val(licenseNoSelName);
            quote_help.auto.getAutoByLicensePlateNo(licenseNoSelName, function (data) {
                if (data.phones.length > 0 || data.photos.length > 0) {
                    var phones = data.phones;
                    var photos = data.photos;
                    if (phones && phones.length > 0) {
                        search_list.newQuote.getPhonesList(phones);
                    }
                    if (photos && photos.length > 0) {
                        search_list.newQuote.getPhotosList(photos);
                    }
                } else {
                    window.parent.$(" .none-not-find").show();
                    window.parent.$(" .none-content").hide();
                }
            });
            window.parent.$("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            window.parent.$("#popover_normal_input .form-input .toSearch").unbind("click").bind({
                click: function () {
                    if (common.isEmpty(licenseNoSelName) || !common.validateLicenseNo(licenseNoSelName)) {
                        window.parent.$("#createError").find("span").html("车牌号格式错误");
                        window.parent.$("#createError").show().delay(2000).hide(0);
                        return false;
                    }
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
                                search_list.newQuote.getPhonesList(phones);
                            }
                            if (photos && photos.length > 0) {
                                search_list.newQuote.getPhotosList(photos);
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
                    window.parent.$("#detail_userId").parent().parent().hide();
                    var licensePlateNo = window.parent.$("#popover_normal_input #licensePlateNo").val();
                    if (common.isEmpty(licensePlateNo) || !common.validateLicenseNo(licensePlateNo)) {
                        window.parent.$("#createError").find("span").html("车牌号格式错误");
                        window.parent.$("#createError").show().delay(2000).hide(0);
                        return false;
                    }
                    //search_list.init.initAutoContent();
                    $.post(search_list.quoteContentUrl, {}, function (detailContent) {
                        popup.pop.popInput(false, detailContent, popup.mould.second, "600px", "600px", "33%", "52%");
                        search_list.init.initSearchChannelEnable(1);

                        quote_help.getIdentityTypes("input_identityType","input_insuredIdType");
                        $("#buttonArea #toEdit").hide();
                        $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                            click: function() {
                                popup.mask.hideFirstMask(false);
                            }
                        });
                        quote_help.auto.getAutoContent(licensePlateNo, "input", function() {
                            $("#input_mobile").val(phoneNo);
                            if (sourceId == 41 || sourceId == 61){
                                $("#input_owner").val(owner);
                                $("#input_identity").val(identityCard);
                                $("#input_insuredName").val(owner);
                                $("#input_insuredIdNo").val(identityCard);
                                $("#input_vinNo").val(vinNo);
                                $("#input_engineNo").val(engineNo);
                                $("#input_enrollDate").val(enrollDate);
                                $("#input_code").val(autoTypeCode);
                            }
                            $("#popover_normal_input .form-input .toSave").unbind("click").bind({
                                click : function() {
                                    search_list.saveQuote.save('first');
                                }
                            });
                            $(".toChgQuoteAutoInfo").unbind("click").bind({
                                click : function() {
                                    var owner = $("#input_owner").val();
                                    var identity = $("#input_identity").val();
                                    var identityType = window.parent.$("#input_identityType").val();
                                    quote_help.auto.getAutoContentByNameId(licensePlateNo,"input",owner,identityType,identity,"amend_search_list");
                                }
                            });
                        }, "amend_search_list");
                        $("input:radio[name='insuredType']").eq(0).attr("checked",'checked');
                        quote_help.auto.setReadOnly("");
                        $("input:radio[name='insuredType']").unbind("change").bind({
                            change: function() {
                                if ($(this).val() == "1") {
                                    quote_help.auto.setReadOnly("");
                                } else {
                                    quote_help.auto.setEdit("");
                                }
                            }
                        });
                        $("#quote_phone_edit_close #auto_detail_close").unbind("click").bind({
                            click: function() {
                                $(".error-msg").hide();
                                popup.mask.hideSecondMask(false);
                            }
                        });

                        $(".toSave").unbind("click").bind({
                            click: function () {
                                search_list.saveQuote.save('second');
                            }
                        });
                    })
                }
            });
        },
        sendSMS: function (id) {
            var mobile = window.parent.$("#span_mobile").text();
            common.getByAjax(true, "get", "json", "/orderCenter/telMarketingCenter/" + id + "/sendSMS", {mobile: mobile}, function (data) {
            }, function () {
            });
        }
    },
    newQuote: {
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
                    "<td><a id='comment" + model.id + "'><input type='button' class='btn btn-danger btn-sm' value='查看'></a></td>" +
                    "<td><a id='seeDetail" + model.id + "'><input type='button' class='btn btn-danger btn-sm' value='查看'></a></td>" +
                    "</tr>";
            });
            window.parent.$("#popover_normal_input #phone_tab tbody").empty();
            window.parent.$("#popover_normal_input #phone_tab tbody").append(phoneContent);
            $("#phone_tab [id^='comment']").unbind("click").bind({
                click: function () {
                    applicationLog.popCommentList("quote_phone", this.id.replace('comment', ''), popup.mould.second);
                }
            });
            window.parent.parent.$("[id^='seeDetail']").unbind("click").bind({
                click: function () {
                    search_list.detailQuote.findDetailPage(this.id.replace('seeDetail', ''), 'second');
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
                    "<td style=\"max-width: 150px;\"><span title='" + ((model.comment == null) ? "" : model.comment.replace(/\\r\\n/g, '\n')) + "'>"
                    + common.getFormatComment((model.comment == null) ? "" : model.comment.replace(/\\r\\n/g, ''), 10) + "</span></td>" +
                    "<td><a id='seePhotoDetail" + model.id + "'><input type='button' class='btn btn-danger btn-sm' value='查看'></a></td>" +
                    "</tr>";
            });
            window.parent.$("#popover_normal_input #photo_tab tbody").empty();
            window.parent.$("#popover_normal_input #photo_tab tbody").append(photoContent);
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
    detailQuote: {
        findDetailPage: function (id, popupInput) {
            //search_list.init.initAutoContent();
            $.post(search_list.quoteContentUrl, {}, function (detailContent) {
                var mouldVal;
                if (popupInput == 'second') {
                    mouldVal = popup.mould.second;
                } else {
                    mouldVal = popup.mould.first;
                }
                popup.pop.popInput(false, detailContent, mouldVal, "600px", "600px", "33%", "52%");
                search_list.init.initSearchChannel(1);//渲染产品平台
                quote_help.getIdentityTypes("input_identityType","input_insuredIdType");
                window.parent.$("#detail_userId").parent().parent().parent().show();
                window.parent.$(".toChgQuoteAutoInfo").hide();
                window.parent.$("#quote_phone_edit_close #auto_detail_close").unbind("click").bind({
                    click: function () {
                        $("#toSave1").show();
                        $(".error-msg").hide();
                        popup.mask.hideSecondMask(false);
                    }
                });
                search_list.detailQuote.getDetailQuote(popupInput, id, "text");
                window.parent.$("#buttonArea #toSave1").hide();
            })
        },
        getDetailQuote: function (popupInput, id, mark) {
            window.parent.$("#toSave1").hide();
            common.getByAjax(false, "get", "json", "/orderCenter/quote/phone/"+id, {},
                function(data) {
                    quote_help.auto.fixAutoContent(data, "text", "", popupInput);
                    parent.$(".toEdit").unbind("click").bind({
                        click: function () {
                            quote_help.auto.fixAutoContent(data, "input", "", popupInput);
                            window.parent.$("#userInfo #user_id").hide();
                            window.parent.$("#userInfo #user_mobile").hide();
                            window.parent.$("#input_identityType").attr("disabled", false);
                            window.parent.$("#input_insuredIdType").attr("disabled", false);
                            var sourceChannel = window.parent.$("#sourceChannel").val();
                            window.parent.$("#sourceChannel").html('');
                            search_list.init.initSearchChannelEnable(1);
                            window.parent.$("#sourceChannel").val(sourceChannel);
                            window.parent.$("#sourceChannel").attr("disabled", false);
                            window.parent.$("#buttonArea #toEdit").hide();
                            window.parent.$("#buttonArea #toSave1").show();
                            window.parent.$(".toChgQuoteAutoInfo").show();
                            window.parent.$(".toChgQuoteAutoInfo").unbind("click").bind({
                                click: function () {
                                    var owner = window.parent.$("#input_owner").val();
                                    var identity = window.parent.$("#input_identity").val();
                                    var identityType = window.parent.$("#input_identityType").val();
                                    var licensePlateNo = window.parent.$("#licensePlateNoHid").val();
                                    quote_help.auto.getAutoContentByNameId(licensePlateNo, "input", owner, identityType, "amend_search_list");
                                }
                            });
                        }
                    });
                    //$("#buttonArea #toEdit").hide();
                    //$("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                    //    click: function() {
                    //        popup.mask.hideFirstMask(false);
                    //    }
                    //});
                    window.parent.$(".toSave").unbind("click").bind({
                        click: function () {
                            search_list.saveQuote.save(popupInput);
                            window.parent.$(".toChgQuoteAutoInfo").hide();
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
                            //如果由活动来的且有广告来源ID的，则跳转链接添加上广告来源的ID
                            var activeUrlId = search_list.param.get("activeUrlId");
                            if (activeUrlId)
                                window.open("/page/quote/quote.html?source=phone&id=" + id + "&activeUrlId=" + activeUrlId);
                            else
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
                    popup.mould.popTipsMould(false, "未获取到车辆信息！", popup.mould.second, popup.mould.error, "", "", null);
                }
            );
        }
    },
    saveQuote: {
        save: function (popupInput) {
            if(!quote_help.quoteValidate.validateQuote("auto_content_errorText",false)){
                return;
            }
            $(this).attr("disabled", true);
            var form = window.parent.$("#popover_normal_input #phone_auto_form");
            if (popupInput == 'second') {
                form = window.parent.$("#popover_normal_input_second #phone_auto_form");
            }
            common.getByAjax(true, "post", "json", "/orderCenter/quote/phone", form.serialize(),
                function (data) {
                    window.parent.$("#errorT").hide();
                    search_list.saveQuote.success(data, popupInput);
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
            var licenseNoSelName = window.parent.$("#detail_licensePlateNo").text();
            quote_help.auto.getAutoByLicensePlateNo(licenseNoSelName, function (data) {
                if (data.phones.length > 0) {
                    var phones = data.phones;
                    if (phones && phones.length > 0) {
                        search_list.newQuote.getPhonesList(phones);
                    }
                } else {
                    window.parent.$(" .none-not-find").show();
                    window.parent.$(" .none-content").hide();
                }
            });
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
                    //如果由活动来的且有广告来源ID的，则跳转链接添加上广告来源的ID
                    var activeUrlId = search_list.param.get("activeUrlId");
                    if (activeUrlId)
                        window.open("/page/quote/quote.html?source=phone&id=" + data.id + "&activeUrlId=" + activeUrlId);
                    else
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

$(function () {
    //url
    if (!common.permission.hasPermission("or060104") || !common.permission.isAbleCall()) {
        $("#infoCallBtn").hide();
    }
    var id = common.getUrlParam("id");
    var clickType = common.getUrlParam("clickType");
    var editType = common.getUrlParam("editType");
    popup.insertHtml($("#popupHtml"));
    var hisId = common.getUrlParam("hisId");
    if(clickType){
        search_list.detailInfo.init(id, hisId,editType);
    }

    /* 拨打电话 */
    $("#infoCallBtn").unbind("click").bind({
        click:function(){
            $("#infoCallBtn").attr("disabled",true);
            setTimeout('$("#infoCallBtn").attr("disabled",false)' ,5000);
            search_list.detailInfo.threewayCall($("#span_mobile").text());
        }
    });
    //if(clickType=="purposeCustomer"){
    //    var editType = common.getUrlParam("editType");
    //    var hisId = common.getUrlParam("hisId");
    //   //0表示从优先和正常列表编辑，1从意向客户搜索编辑，2从工作查看编辑 大于10时表示source*/
    //}else if(clickType=="workDetail"){
    //    var hisId = common.getUrlParam("hisId");
    //    work_details.detailInfo.init(id,hisId);
    //}
});
