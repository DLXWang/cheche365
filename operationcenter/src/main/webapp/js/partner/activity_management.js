/**
 * Created by wangfei on 2015/8/25.
 */
var customerFieldIndex = 0;//自定义字段id
var createCustomerFieldCount = 0;//自定义字段数量
var conditionsMap = new Map;
var dataFunction = {
    "data": function (data) {
        data.keyword = datatableUtil.params.keyWord;
        data.keyType = datatableUtil.params.keyType;
    },
    "fnRowCallback": function (nRow, aData) {
        var cityCount = "";
        var cityContent = "";
        if(aData.city && aData.city.length >=2){
            var cities = aData.city.split(",");
            if (cities.length <= 1) {
                cityCount = "";
                cityContent = aData.city;
            } else if (cities.length <= 2) {
                cityCount = "[" + cities.length + "个城市]";
                cityContent = aData.city;
            } else {
                cityCount = "[" + cities.length + "个城市]";
                cityContent = cities[0] + "," + cities[1] + "," + cities[2];
                if (cities.length > 3) {
                    cityContent += "...";
                }
            }
        }

        var statusColor = "#FF6600";
        if (aData.status == '未开始') {
            statusColor = "#FF6600";
        } else if (aData.status == '进行中') {
            statusColor = "#008000";
        } else if (aData.status == '已结束') {
            statusColor = "#FF0000";
        }
        $cityCooperationMode = "<div>" + common.checkToEmpty(cityCount) + "</div><div title='" + aData.city + "'>" + common.checkToEmpty(cityContent) + "</div><div>" + common.checkToEmpty(aData.cooperationModeName) + "</div>";
        $activityCycle = "<div style='color: #008000;'>起：" + common.checkToEmpty(aData.startTime) + "</div><div style='color: #FF0000;'>止：" + common.checkToEmpty(aData.endTime) + "</div><div style='color: " + statusColor + ";'><" + common.checkToEmpty(aData.status) + "></div>";
        $budget = (aData.budget == null ? "" : aData.budget);
        $lastAlter = "<div>" + common.checkToEmpty(aData.operator) + "</div><div>" + common.checkToEmpty(aData.updateTime) + "</div>";
        $comment = "<span title=\"" + common.checkToEmpty(aData.comment) + "\">" + common.getFormatComment(common.checkToEmpty(aData.comment), 40) + "</span>";
        $operation = "<a href='javascript:;' onclick='detail(" + aData.id + ")'>查看详情</a>";
        $('td:eq(3)', nRow).html($cityCooperationMode);
        $('td:eq(4)', nRow).html($activityCycle);
        $('td:eq(5)', nRow).html($budget);
        $('td:eq(6)', nRow).html($lastAlter);
        $('td:eq(8)', nRow).html($comment);
        $('td:eq(9)', nRow).html($operation);
    }
};
var activity = {
    "url": '/operationcenter/activities',
    "type": "GET",
    "table_id": "activity_tab",
    "columns": [
        {"data": "id", "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": "partnerName", "title": "合作商名称", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "name", "title": "商务活动名称", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "城市合作方式", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "活动周期", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "预算（元）", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "最后修改", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
        {"data": "refreshTime", "title": "数据更新时间", 'sClass': "text-center", "orderable": false, "sWidth": "180px"},
        {"data": null, "title": "备注", 'sClass': "text-center", "orderable": false, "sWidth": "60px"},
        {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "120px"}

    ],
    listActivity: {
        properties: new Properties(1, ""),
        /**
         * 导出数据到excel
         */
        exportExcel: function () {
            conditionsMap.clear();
            conditionsMap.put("keyType", $("#keyType").val());
            conditionsMap.put("keyword", $("#keyword").val());
            conditionsMap.put("currentPage", activity.listActivity.properties.currentPage);
            conditionsMap.put("pageSize", activity.listActivity.properties.pageSize);
            var url = "/operationcenter/activities/export?";
            conditionsMap.each(function (key, value, index) {
                if (index != 0) {
                    url += "&";
                }
                url += key + "=" + value;
            });
            $("#exportExcel").attr("href", url);
        }
    },

    config_validation: {
        onkeyup: false,
        onfocusout: false,
        rules: {
            name: {
                required: true,
                maxlength: 20
            },
            code: {
                required: true,
                maxlength: 30
            },
            partner: {
                required: true
            },
            linkMan: {
                maxlength: 10
            },
            mobile: {
                maxlength: 20
            },
            budget: {
                min: 1,
                max: 99999999
            },
            startTimeShow: {
                required: true
            },
            endTimeShow: {
                required: true
            },
            landingPage: {
                required: true,
                maxlength: 100
            },
            landingPageSel: {
                required: true
            },
            comment: {
                maxlength: 200
            }
        },
        messages: {
            name: {
                required: "请输入商务活动名称",
                maxlength: "商务活动名称最多可输入20位"
            },
            code: {
                required: "请输入商务活动编号",
                maxlength: "商务活动编号最多可输入30位"
            },
            partner: {
                required: "请选择合作商"
            },
            linkMan: {
                maxlength: "联系人最多可输入10位"
            },
            mobile: {
                maxlength: "联系方式最多可输入20位"
            },
            budget: {
                min: "预算至少为1",
                max: "预算最多为99999999"
            },
            startTimeShow: {
                required: "请选择活动开始日期"
            },
            endTimeShow: {
                required: "请选择活动结束日期"
            },
            landingPage: {
                required: "请填写落地页URL",
                maxlength: "落地页最多可输入100位"
            },
            landingPageSel: {
                required: "请选择落地页"
            },
            comment: {
                maxlength: "备注最多可输入200位"
            }
        },
        showErrors: function (errorMap, errorList) {
            if (errorList.length) {
                var errorText = $("#errorText");
                errorText.text(errorList[0].message);
                errorText.parent().parent().show();
            }
        },
        submitHandler: function (form) {
            var errorText = $("#errorText");
            if (!common.isValidatePattern($("#code").val(), /^[A-Za-z0-9-]+$/ig)) {
                errorText.text("商务活动编号只能输入英文、数字、-");
                errorText.parent().parent().show();
                return false;
            }
            if (common.isEmpty($("#cooperationModeSel").val())) {
                errorText.text("请选择商务活动");
                errorText.parent().parent().show();
                return false;
            }

            if ($("#budget").val() && !common.isPureNumber($("#budget").val())) {
                errorText.text("预算只能是整数值");
                errorText.parent().parent().show();
                return false;
            }
            if (!common.isEmpty($("#email").val())) {
                var email = $("#email").val();
                var email_content = "";
                // 多个邮箱以";"分隔，验证邮箱格式
                var emails = email.split(";");
                for (var i = 0; i < emails.length; i++) {
                    if (!common.isEmail(emails[i])) {
                        email_content = "请输入有效的报表接收邮箱地址";
                        break;
                    }
                }
                if (email_content != "") {
                    errorText.text(email_content);
                    errorText.parent().parent().show();
                    return false;
                }
            }

            var landingPageSel = $("#landingPageSel").val();
            if (landingPageSel) {
                if (landingPageSel == "1" || landingPageSel == "2") {
                    if (common.isEmpty($("#paymentChannelSel").val())) {
                        errorText.text("请选择支付方式");
                        errorText.parent().parent().show();
                        return;
                    }
                } else if (landingPageSel == "3" || landingPageSel == "4") {
                    if (common.isEmpty($("#marketingSel").val())) {
                        errorText.text("请选择推广活动");
                        errorText.parent().parent().show();
                        return;
                    }
                }
            } else {
                errorText.text("请选择落地页");
                errorText.parent().parent().show();
                return;
            }

            if ($("#cooperationModeSel").find("option:selected").text() == "CPS") {
                if ($("#rebate").val() == "") {
                    errorText.text("请填写佣金");
                    errorText.parent().parent().show();
                    return false;
                }
                if (!common.isPureNumber($("#rebate").val())) {
                    errorText.text("佣金只能是整数值");
                    errorText.parent().parent().show();
                    return false;
                }
                if ($("#rebate").val() < 0 || $("#rebate").val() > 100) {
                    errorText.text("佣金只能是0到100的整数值");
                    errorText.parent().parent().show();
                    return false;
                }
            }
            if (common.isEmpty($("#areaSel").val())) {
                errorText.text("请选择城市");
                errorText.parent().parent().show();
                return false;
            }
            if (common.checkDate($("#startTime").val(), $("#endTime").val())) {
                errorText.text("活动结束时间必须晚于开始时间");
                errorText.parent().parent().show();
                return false;
            }
            if (common.isChineseChar($("#landingPage").val())) {
                errorText.text("落地页不可存在中文字符");
                errorText.parent().parent().show();
                return false;
            }
            if (!common.isEmpty($("#mobile").val()) && !common.isTelphone($("#mobile").val())) {
                errorText.text("请输入有效的联系方式");
                errorText.parent().parent().show();
                return false;
            }
            // 验证自定义字段
            if ($(".customerFieldDiv").length > 0) {
                var errorContent = "";
                $(".customerFieldDiv").each(function (index, element) {
                    var customerFieldName = $(this).find(".customer-field-name").val();
                    if (common.isEmpty(customerFieldName)) {
                        errorContent = "请输入自定义字段名称";
                        return false;
                    }
                    var firstFieldSel = $(this).find(".first-field").val();
                    if (common.isEmpty(firstFieldSel)) {
                        errorContent = "请选择第一个数据项";
                        return false;
                    }
                    var operator = $(this).find(".operator").val();
                    if (common.isEmpty(operator)) {
                        errorContent = "请选择运算符";
                        return false;
                    }
                    var secondFieldSel = $(this).find(".second-field").val();
                    if (common.isEmpty(secondFieldSel)) {
                        errorContent = "请选择第二个数据项";
                        return false;
                    }
                });
                if (errorContent != "") {
                    errorText.text(errorContent);
                    errorText.parent().parent().show();
                    return false;
                }
            }
            errorText.parent().parent().hide();
            activity.newActivity.save();
        }
    },

    edit_validation: {
        onkeyup: false,
        onfocusout: false,
        rules: {
            linkMan: {
                maxlength: 10
            },
            mobile: {
                maxlength: 20
            },
            budget: {
                min: 1,
                max: 99999999
            },
            editStartTimeShow: {
                required: true
            },
            editEndTimeShow: {
                required: true
            },
            comment: {
                maxlength: 200
            }
        },
        messages: {
            linkMan: {
                maxlength: "联系人最多可输入10位"
            },
            mobile: {
                maxlength: "联系方式最多可输入20位"
            },
            budget: {
                min: "预算至少为1",
                max: "预算最多为99999999"
            },
            editStartTimeShow: {
                required: "请选择活动开始日期"
            },
            editEndTimeShow: {
                required: "请选择活动结束日期"
            },
            comment: {
                maxlength: "备注最多可输入200位"
            }
        },
        showErrors: function (errorMap, errorList) {
            if (errorList.length) {
                var errorText = $("#edit_errorText");
                errorText.text(errorList[0].message);
                errorText.parent().parent().show();
            }
        },
        submitHandler: function (form) {
            var errorText = $("#edit_errorText");
            if (!common.isEmpty($("#edit_mobile").val()) && !common.isTelphone($("#edit_mobile").val())) {
                errorText.text("请输入有效的联系方式");
                errorText.parent().parent().show();
                return false;
            }
            if ($("#edit_cooperationModeName").val() == "CPS") {
                if ($("#edit_rebate").val() == "") {
                    errorText.text("请填写佣金");
                    errorText.parent().parent().show();
                    return false;
                }
                if ($("#edit_rebate").val() < 0 || $("#edit_rebate").val() > 100) {
                    errorText.text("佣金只能是0到100的整数值");
                    errorText.parent().parent().show();
                    return false;
                }
                if (!common.isPureNumber($("#edit_rebate").val())) {
                    errorText.text("佣金只能是整数值");
                    errorText.parent().parent().show();
                    return false;
                }
            }
            if (!common.isEmpty($("#edit_email").val()) && common.getLength($("#edit_email").val()) > 200) {
                errorText.text("报表接收邮箱最多可输入200字");
                errorText.parent().parent().show();
                return false;
            }
            if ($("#edit_budget").val() && !common.isPureNumber($("#edit_budget").val())) {
                errorText.text("预算只能是整数值");
                errorText.parent().parent().show();
                return false;
            }
            if (common.checkDate($("#edit_startTime").val(), $("#edit_endTime").val())) {
                errorText.text("活动结束时间必须晚于开始时间");
                errorText.parent().parent().show();
                return false;
            }
            errorText.parent().parent().hide();
            activity.editActivity.save($("#edit_id").val());
        }
    },
    initActivity: {
        init: function () {
            activity.initActivity.initPartner();
            activity.initActivity.initArea();
            activity.initActivity.initBasicMonitorData();
            activity.initActivity.initPaymentChannel();
        },

        /**
         * 获取合作商
         */
        initPartner: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/partners", null,
                function (data) {
                    if (data == null) {
                        return false;
                    }
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.name + "</option>";
                    });
                    $("#partnerSel").append(options);
                }, function () {
                }
            );
        },

        /**
         * 获取城市
         */
        initArea: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/quoteAreas", null,
                function (data) {
                    if (data == null) {
                        return false;
                    }
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.name + "</option>";
                    });
                    $("#areaSel").append(options);
                    activity.initActivity.getCheckedArea(data.length);
                }, function () {
                }
            );
        },

        /*
         *select下拉多选框
         */
        getCheckedArea: function (length) {
            if (length > 12) {
                var input = $("#popover_normal_input");
                input.css("overflow-x", "auto");
            }
            $('#areaSel').multiselect({
                nonSelectedText: '请选择城市',
                buttonWidth: '437',
                maxHeight: '180',
                includeSelectAllOption: true,
                selectAllNumber: false,
                selectAllText: '全国',
                allSelectedText: '全国',
                numberDisplayed: length
            });
        },

        /**
         * 获取marketing活动
         */
        initMarketing: function (marketingType) {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/marketing/enable",
                {
                    marketingType: marketingType
                },
                function (data) {
                    if (data == null) {
                        return false;
                    }
                    $("#marketingSel").empty();
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.name + "</option>";
                    });
                    $("#marketingSel").append(options);
                    $("#marketingSel").change();
                }, function () {
                }
            );
        },

        /**
         * 获取支付方式
         */
        initPaymentChannel: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/paymentChannels/enable", null,
                function (data) {
                    if (data == null) {
                        return false;
                    }
                    $("#paymentChannelSel").empty();
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.channel + "</option>";
                    });
                    $("#paymentChannelSel").append(options);
                    activity.initActivity.getCheckedPaymentChannel(data.length);
                }, function () {
                }
            );
        },

        /*
         *select下拉多选框
         */
        getCheckedPaymentChannel: function (length) {
            if (length > 12) {
                var input = $("#popover_normal_input");
                input.css("overflow-x", "auto");
            }
            $('#paymentChannelSel').multiselect({
                nonSelectedText: '请选择支付方式',
                buttonWidth: '160',
                maxHeight: '180',
                includeSelectAllOption: true,
                selectAllNumber: false,
                selectAllText: '全部',
                allSelectedText: '全部',
                numberDisplayed: length
            });
        },

        /**
         * 获取基础字段
         */
        initBasicMonitorData: function () {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/monitorDataTypes/enable", null,
                function (data) {
                    if (data == null) {
                        return false;
                    }

                    var trContent = "";
                    $.each(data, function (i, model) {
                        trContent += "<td class=\"text-center\">" + model.name + "</td>";
                    });
                    $("#basicMonitorType_tr").html(trContent);
                }, function () {
                }
            );
        },

        /**
         * 获取基础字段
         */
        initBasicMonitorDataSel: function (customerFieldIndex) {
            var value = $.trim($("#cooperationModeSel").find("option:selected").text()).toUpperCase();
            common.getByAjax(true, "get", "json", "/operationcenter/resource/monitorDataTypes", null,
                function (data) {
                    if (data == null) {
                        return false;
                    }
                    var options = "<option value=''>数据项</option>";
                    $.each(data, function (i, model) {
                        if (model.name == "佣金") {
                            activity.newActivity.rebate = model.id;
                            if (value == "CPS") {
                                options += "<option value='" + model.id + "'>" + model.name + "</option>";
                            }
                        } else {
                            options += "<option value='" + model.id + "'>" + model.name + "</option>";
                        }
                    });
                    $("#firstField" + customerFieldIndex).append(options);
                    $("#secondField" + customerFieldIndex).append(options);
                }, function () {
                }
            );
        },

        /**
         * 获取运算符
         */
        initOperatorSel: function (customerFieldIndex) {
            common.getByAjax(true, "get", "json", "/operationcenter/resource/operators", null,
                function (data) {
                    if (data == null) {
                        return false;
                    }
                    var options = "<option value=''>运算符</option>";
                    $.each(data, function (i, model) {
                        options += "<option value='" + model.id + "'>" + model.name + "</option>";
                    });
                    $("#operator" + customerFieldIndex).append(options);
                }, function () {
                }
            );
        },

        initNewPopupContent: function () {
            var popupContent = $("#new_content");
            if (popupContent.length > 0) {
                activity.newActivity.content = popupContent.html();
                popupContent.remove();
            }
        },

        initEditPopupContent: function () {

            var popupContent = $("#edit_content");
            if (popupContent.length > 0) {
                activity.editActivity.content = popupContent.html();
                popupContent.remove();
            }
        },

        initShowPopupContent: function () {
            var popupContent = $("#detail_content");
            if (popupContent.length > 0) {
                activity.showActivity.content = popupContent.html();
                popupContent.remove();
            }
        }
    },
    newActivity: {
        content: "",
        rebate: "",
        /**
         * 新建商务活动页面
         */
        create: function () {
            activity.initActivity.initNewPopupContent();
            popup.pop.popInput(activity.newActivity.content, popup.mould.first, "880px", "548px", "36%", "47%");

            /**
             * 初始化下拉数据
             */
            activity.initActivity.initPartner();
            activity.initActivity.initArea();
            activity.initActivity.initBasicMonitorData();
            activity.initActivity.initPaymentChannel();

            /*关闭新建弹出框*/
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }
            });

            /**
             * 改变合作商更新其合作方式
             */
            $("#partnerSel").unbind("change").bind({
                change: function () {
                    var partnerId = $("#partnerSel").val();
                    activity.newActivity.cooperationMode(partnerId);
                }
            });

            /**
             * 绑定新增自定义字段(PS:此绑定方法放在新建商务活动页面才生效，不要挪出去)
             */
            $("#addCustomerField").unbind("click").bind({
                click: function () {
                    activity.newActivity.addCustomerField();
                }
            });

            /**
             * 合作方式对佣金的影响
             */
            $("#cooperationModeSel").unbind("change").bind({
                change: function () {
                    activity.newActivity.cooperationModeChange();
                }
            });

            /**
             * 落地页的影响
             */
            $("#marketingSel").unbind("change").bind({
                change: function () {
                    activity.newActivity.getMarketingLandingPage();
                }
            });

            /**
             * 落地页的影响
             */
            $("#landingPageSel").unbind("change").bind({
                change: function () {
                    var landingPageSel = $(this).val();
                    if (!landingPageSel) {
                        $("#index_config").hide();
                        $("#landing_config").hide();
                        $("#marketing_div").hide();
                        $("#new_marketing_remark_div").hide();
                        $("#landingPage").val("");
                        $("#marketingSel").empty();
                        return;
                    }
                    if (landingPageSel == "1") {
                        $("#index_config").show();
                        $("#back_display").show();
                        $("#landing_config").show();
                        $("#marketing_div").show();
                        $("#new_marketing_remark_div").hide();
                        $("#marketingSel").empty();
                        activity.newActivity.getLandingPage("mHome");
                    } else if (landingPageSel == "2") {
                        $("#back_display").show();
                        $("#landing_config").show();
                        $("#index_config").show();
                        $("#marketing_div").show();
                        $("#new_marketing_remark_div").hide();
                        $("#marketingSel").empty();
                        activity.newActivity.getLandingPage("mPayment");
                    } else if (landingPageSel == "3") {
                        $("#back_display").hide();
                        $("#landing_config").show();
                        $("#index_config").show();
                        $("#marketing_div").show();
                        activity.initActivity.initMarketing("m");
                    } else if (landingPageSel == "4") {
                        $("#back_display").hide();
                        $("#landing_config").show();
                        $("#index_config").show();
                        $("#marketing_div").show();
                        activity.initActivity.initMarketing("web");
                    } else if (landingPageSel == "5") {
                        $("#back_display").hide();
                        $("#landing_config").show();
                        $("#index_config").show();
                        $("#marketing_div").show();
                        $("#new_marketing_remark_div").show();
                        activity.newActivity.getLandingPage("webHome");
                    }
                }
            });

            /**
             * 生成<M站首页>商务活动URL
             */
            /*
             $("#mHomePage").unbind("click").bind({
             click: function() {
             activity.newActivity.getLandingPage("home");
             }
             });

             */
            /**
             * 生成<M站购买页>商务活动URL
             */
            /*
             $("#mMailPage").unbind("click").bind({
             click: function() {
             activity.newActivity.getLandingPage("payment");
             }
             });*/

            $("#new_form").validate(activity.config_validation);
        },

        /**
         * 获取Marketing活动的URL
         */
        getMarketingLandingPage: function () {
            var marketing = $("#marketingSel").val();
            common.getByAjax(false, "get", "json", "/operationcenter/activities/marketing",
                {
                    marketingId: marketing
                },
                function (data) {
                    $("#landingPage").val(data.landingPage);
                    $("#new_marketing_remark_div").show();
                    $("#startTimeRemark").text(data.startTime);
                    $("#endTimeRemark").text(data.endTime);
                }, function () {
                }
            )
        },

        /**
         * 保存新建的商务活动
         */
        save: function (form) {
            common.getByAjax(false, "get", "json", "/operationcenter/activities/check",
                {
                    code: $("#code").val(),
                    marketingId: $("#marketingSel").val(),
                    startTime: $("#startTime").val()
                },
                function (data) {
                    if (data.pass) {
                        $("#toCreate").attr("disabled", true);
                        common.getByAjax(true, "post", "json", "/operationcenter/activities", $("#new_form").serialize(),
                            function (data) {
                                $("#toCreate").attr("disabled", false);
                                if (data) {
                                    popup.mask.hideAllMask();
                                    popup.mould.popTipsMould("新建商务活动成功！", popup.mould.first, popup.mould.success, "", "57%",
                                        function () {
                                            popup.mask.hideFirstMask();
                                            datatableUtil.params.keyWord = "";
                                            datatables.ajax.reload();
                                        }
                                    );
                                } else {
                                    popup.mould.popTipsMould("新建商务活动失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                                }
                            },
                            function () {
                                $("#toCreate").attr("disabled", false);
                                popup.mould.popTipsMould("新建商务活动失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.error, "", "57%", null);
                        return false;
                    }
                }, function () {
                }
            )
        },

        /**
         * 获取商务活动URL
         */
        getLandingPage: function (urlType) {
            common.getByAjax(false, "get", "html", "/operationcenter/activities/landingPage",
                {
                    urlType: urlType
                },
                function (data) {
                    $("#landingPage").val(data);
                }, function () {
                }
            )
        },

        /**
         * 获取合作方式
         */
        cooperationMode: function (partnerId) {
            if (partnerId == "") {
                $("#cooperationModeSel").empty();
                var options = "<option value=''>选择合作方式</option>";
                $("#cooperationModeSel").append(options);
            } else {
                common.getByAjax(true, "get", "json", "/operationcenter/partners/cooperationModes/" + partnerId, null,
                    function (data) {
                        if (data == null) {
                            return false;
                        }
                        // 清空原合作方式
                        $("#cooperationModeSel").empty();
                        var options = "";
                        $.each(data, function (i, model) {
                            options += "<option value='" + model.id + "'>" + model.name + "</option>";
                        });
                        $("#cooperationModeSel").append(options);
                        activity.newActivity.cooperationModeChange();
                    }, function () {
                    }
                );
            }
        },

        /**
         * 新增一个自定义字段
         */
        addCustomerField: function () {
            if (createCustomerFieldCount >= 5) {
                return false;
            }

            var item =
                "<div id='customerFieldDiv" + customerFieldIndex + "' class='form-group customerFieldDiv' style='border:1px dashed #96c2f1; background:#eff7ff;padding:5px;margin:5px 5px 5px 1px;width:98%;'>"
                + "<fieldset>"
                + "<a id='customerFieldClose" + customerFieldIndex + "' href='javascript:;' title='关闭' onclick='activity.newActivity.removeCustomerField(this);' class='close'><i class='glyphicon glyphicon-remove customer-field-close'></i></a>"
                + "<legend class='text-left' style='font-size: 17px;margin-bottom: 5px;'>自定义字段</legend>"
                + "<div class='col-sm-3'>"
                + "<input type='text' class='form-control customer-field-name' id='customerField" + customerFieldIndex + "' name='customerField[" + customerFieldIndex + "].name' placeholder='最多十个位' style='width:180px' maxlength='10'/>"
                + "</div>"
                + "<span class='col-sm-1 text-center' style='padding-top: 8px;'>=</span>"
                + "<div class='col-sm-3'>"
                + "<select id='firstField" + customerFieldIndex + "' name='customerField[" + customerFieldIndex + "].firstField' class='form-control text-input-80 first-field'>"
                + "</select>"
                + "</div>"
                + "<div class='col-sm-2'>"
                + "<select id='operator" + customerFieldIndex + "' name='customerField[" + customerFieldIndex + "].operator' class='form-control text-input-5 operator'>"
                + "</select>"
                + "</div>"
                + "<div class='col-sm-2'>"
                + "<select id='secondField" + customerFieldIndex + "' name='customerField[" + customerFieldIndex + "].secondField' class='form-control text-input-150 second-field'>"
                + "</select>"
                + "</div>"
                + "</fieldset>"
                + "</div>";

            $(item).insertBefore($('#addCustomerField'));

            // 初始化监控数据和运算符
            activity.initActivity.initBasicMonitorDataSel(customerFieldIndex);
            activity.initActivity.initOperatorSel(customerFieldIndex);

            customerFieldIndex++;
            createCustomerFieldCount++;
        },

        /**
         * 删除自定义字段Div
         * @param obj
         */
        removeCustomerField: function (obj) {
            createCustomerFieldCount--;
            $(obj).parent().parent().remove();
        },

        /**
         * 选择合作方式为CPS时，佣金显示，其他不显示
         */
        cooperationModeChange: function () {
            var value = $.trim($("#cooperationModeSel").find("option:selected").text()).toUpperCase();
            if (value == 'CPS') {
                $("#rebate_div").show();
                $("#rebate_span").show();
                // 自定义字段的基础字段添加佣金
                if ($(".customerFieldDiv .first-field").length > 0) {
                    $(".customerFieldDiv .first-field").each(function (index, element) {
                        $(element).append("<option value='" + activity.newActivity.rebate + "'>佣金</option>");
                    });
                    $(".customerFieldDiv .second-field").each(function (index, element) {
                        $(element).append("<option value='" + activity.newActivity.rebate + "'>佣金</option>");
                    });
                }
            } else {
                $("#rebate_div").hide();
                $("#rebate_span").hide();
                // 自定义字段的基础字段去掉佣金
                if ($(".customerFieldDiv .first-field").length > 0) {
                    $(".customerFieldDiv .first-field").each(function (index, element) {
                        $(element).find("option[value='" + activity.newActivity.rebate + "']").remove();
                    });
                    $(".customerFieldDiv .second-field").each(function (index, element) {
                        $(element).find("option[value='" + activity.newActivity.rebate + "']").remove();
                    });
                }
                // 自定义字段的基础字段去掉佣金
                if ($(".customerFieldDiv .first-field").length > 0) {
                    $(".customerFieldDiv .first-field").each(function (index, element) {
                        $(element).find("option[value='" + activity.newActivity.rebate + "']").remove();
                    });
                    $(".customerFieldDiv .second-field").each(function (index, element) {
                        $(element).find("option[value='" + activity.newActivity.rebate + "']").remove();
                    });
                }
            }
        }
    },

    showActivity: {
        content: "",

        /**
         * 查看活动详情
         */
        detail: function (id) {
            if (!common.permission.validUserPermission("op010203")) {
                return;
            }
            activity.initActivity.initShowPopupContent();
            popup.pop.popInput(activity.showActivity.content, popup.mould.first, "800px", "528px", "38%", "47%");
            common.getByAjax(true, "get", "json", "/operationcenter/activities/" + id, null,
                function (data) {
                    if (data == null) {
                        return false;
                    }
                    activity.showActivity.write(data);
                }, function () {
                    popup.mould.popTipsMould("获取详情失败，请稍后再试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }

            });
        },
        write: function (data) {
            $("#detail_activityId").val(data.id);
            $("#detail_refreshFlag").val(data.refreshFlag);
            $("#detail_id_name").html((common.isEmpty(data.id) ? '' : '&lt' + "HD" + data.id + '&gt') + (data.name == null ? '' : data.name) + "-" + (data.code == null ? '' : data.code));
            $("#detail_partner").text(common.isEmpty(data.partnerName) ? '' : data.partnerName);//合作商名
            $("#detail_cooperationMode").text(common.isEmpty(data.cooperationModeName) ? '' : data.cooperationModeName);//合作方式名
            $("#detail_frequency").text(data.frequency == 1 ? "每周" : (data.frequency == 2 ? "每月" : "不发送"));//邮件报表发送频率
            $("#detail_email").text(common.isEmpty(data.email) ? '' : data.email);//报表接收邮箱

            if (data.landingPageType == "1") {
                $("#detail_top_brand").text(data.topBrand ? "显示" : "不显示");
                $("#detail_my_center").text(data.myCenter ? "显示" : "不显示");
                $("#detail_top_carousel").text(data.topCarousel ? "显示" : "不显示");
                $("#detail_activity_entry").text(data.activityEntry ? "显示" : "不显示");
                $("#detail_our_customer").text(data.ourCustomer ? "显示" : "不显示");
                $("#detail_bottom_carousel").text(data.bottomCarousel ? "显示" : "不显示");
                $("#detail_bottom_info").text(data.bottomInfo ? "显示" : "不显示");
                $("#detail_bottom_download").text(data.bottomDownload ? "显示" : "不显示");
                $("#detail_footer").text(data.footer ? "显示" : "不显示");
                $("#detail_btn").text(data.btn ? "显示" : "不显示");
                $("#detail_app").text(data.app ? "显示" : "不显示");
                $("#detail_enable").text(data.enable ? "允许" : "不允许");
                $("#detail_payment").text(data.paymentChannels);//支付方式
                $("#detail_index_config").show();
                $("#detail_landing_config").show();
                $("#detail_marketing_div").hide();
            } else if (data.landingPageType == "2") {
                $("#detail_footer").text(data.footer ? "显示" : "不显示");
                $("#detail_btn").text(data.btn ? "显示" : "不显示");
                $("#detail_app").text(data.app ? "显示" : "不显示");
                $("#detail_enable").text(data.enable ? "允许" : "不允许");
                $("#detail_payment").text(data.paymentChannels);//支付方式
                $("#detail_landing_config").show();
                $("#detail_marketing_div").hide();
            } else if (data.landingPageType == "3" || data.landingPageType == "4") {
                $("#detail_landing_config").hide();
                $("#detail_marketing").text(common.checkToEmpty(data.marketingName));//活动名称
                $("#detail_marketing_div").show();
            } else if (data.landingPageType == "5") {
                $("#detail_landing_config").hide();
                $("#detail_marketing_div").hide();
            }

            if (data.cooperationModeName == "CPS") {
                $("#detail_rebate").text(common.isEmpty(data.rebate) ? '' : data.rebate + '%');//佣金
                $("#detail_rebate_div").show();
            } else {
                $("#detail_rebate_div").hide();
            }

            var cityContent = "";
            var cities = data.city.split(",");
            if (cities.length <= 10) {
                cityContent = data.city;
            } else {
                for (var i = 0; i <= 9; i++) {
                    if (i == 9) {
                        cityContent += cities[i];
                    } else {
                        cityContent += cities[i] + ",";
                    }
                }
                if (cities.length > 10) {
                    cityContent += "...";
                }
            }
            $("#detail_city").text(cityContent);//活动城市
            $("#detail_city").attr("title", common.checkToEmpty(data.city));//活动城市
            $("#detail_startTime").html(common.isEmpty(data.startTime) ? '' : "<font color='green' style='font-weight:bold'>起：</font>" + data.startTime);//开始时间
            $("#detail_endTime").html(common.isEmpty(data.endTime) ? '' : "<font color='red' style='font-weight:bold'>止：</font>" + data.endTime);//结束时间
            $("#detail_landingPage").text(common.isEmpty(data.landingPage) ? '' : data.landingPage);//落地页
            $("#detail_budget").text(common.isEmpty(data.budget) ? '' : data.budget + '元');//预算
            $("#detail_linkMan").text(common.isEmpty(data.linkMan) ? '' : data.linkMan);//联系人
            $("#detail_mobile").text(common.isEmpty(data.mobile) ? '' : data.mobile);//联系方式
            $("#detail_comment").val(common.isEmpty(data.comment) ? '' : data.comment);//备注

            // // 正在进行中的商务活动才可刷新数据
            // if (data.status != null && (data.status == "已结束" || data.status == "未开始")) {
            //     $("#refreshData").attr("disabled", true);
            // }
            // // 绑定更新据
            // $("#refreshData").bind({
            //     click: function () {
            //         if (!common.permission.validUserPermission("op010204")) {
            //             return;
            //         }
            //         activity.showActivity.refreshData();
            //     }
            // });

            // 绑定导出时段数据到Excel
            $("#exportHourExcel").bind({
                click: function () {
                    if (!common.permission.validUserPermission("op010205")) {
                        return;
                    }
                    activity.showActivity.exportHourExcel(data.id);
                }
            });

            if (data.status != "已结束") {
                // 绑定编辑
                $("#editActivity").bind({
                    click: function () {
                        if (!common.permission.validUserPermission("op010206")) {
                            return;
                        }
                        activity.editActivity.edit(data.id);
                    }
                });
            } else {
                $("#editActivity").hide();
            }

            // 显示支持的城市
            activity.showActivity.showArea(data);

            // 显示监控数据
            $("#refreshTime").text(common.isEmpty(data.refreshTime) ? '无' : data.refreshTime);//数据更新时间

            // Table标题栏显示自定义字段名称
            if (data.customerField != null && data.customerField.length > 0) {
                var content = "";
                $.each(data.customerField, function (i, model) {
                    content += " <th class='text-center'>" + model.name + "</th>";
                });
                $("#monitor_data_tr").append(content);
            }

            // 修改Table的width
            var monitorDataDiv = $("#monitor_data");
            if (data.monitorDataList != null && data.monitorDataList.length > 0) {
                monitorDataDiv.width(950);
            }
            if (data.customerField != null && data.customerField.length > 0) {
                monitorDataDiv.width(monitorDataDiv.width() + (60 * data.customerField.length));
            }

            $("#monitorData_tab tbody").empty();
            $("#monitorData_tab tbody").append(activity.showActivity.writeData(data));
        },

        /**
         * 显示支持的城市
         * @param data
         */
        showArea: function (data) {
            var area_content = "";
            area_content = "<a type='button' href='javascript:;' onclick='activity.showActivity.getCityMonitorData(" + data.id + ",0)' class='btn selected'><span class='text-font'>全国</span></a>";
            if (data.activityArea != null && data.activityArea.length > 0) {
                $.each(data.activityArea, function (i, model) {
                    area_content += "<a type='button' href='javascript:;' onclick='activity.showActivity.getCityMonitorData(" + data.id + "," + model.area + ")' class='btn btn-default'><span class='text-font'>" + model.areaName + "</span></a>";
                });
            }
            area_content += "<a type='button' href='javascript:;' onclick='activity.showActivity.getCityMonitorData(" + data.id + ",-1)' class='btn btn-default'><span class='text-font'>未知来源</span></a>";
            $("#areaGroup").append(area_content);

            // 切换城市时修改样式
            $(".tabs a").bind({
                click: function (e) {
                    e.preventDefault();
                    if ($(this).hasClass("selected")) {
                        return false;
                    }
                    $(this).siblings(".selected").removeClass("selected").addClass("btn-default");
                    $(this).addClass("selected").removeClass("btn-default");
                }
            });
        },

        /**
         * 获取城市的监控数据
         * @param activityId
         * @param areaId
         */
        getCityMonitorData: function (activityId, areaId) {

            common.getByAjax(true, "get", "json", "/operationcenter/activities/" + activityId + "/" + areaId + "/data", null,
                function (data) {
                    if (data == null) {
                        return false;
                    }

                    $("#monitorData_tab tbody").empty();
                    $("#monitorData_tab tbody").append(activity.showActivity.writeData(data));
                }, function () {
                    popup.mould.popTipsMould("获取城市监控数据失败，请稍后再试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },

        writeData: function (data) {
            var data_content = "";
            if (data.monitorDataList != null && data.monitorDataList.length > 0) {
                $.each(data.monitorDataList, function (i, model) {
                    data_content += "<tr class='text-center'>" +
                        "<td>" + common.checkToEmpty(model.monitorTime) + "</td>" +
                        "<td>" + common.checkToEmpty(model.pv) + "</td>" +
                        "<td>" + common.checkToEmpty(model.uv) + "</td>" +
                        "<td>" + common.checkToEmpty(model.register) + "</td>" +
                        "<td>" + common.checkToEmpty(model.quote) + "</td>" +
                        "<td>" + common.checkToEmpty(model.submitCount) + "</td>" +
                        "<td>" + common.formatMoney(model.submitAmount, 2) + "</td>" +
                        "<td>" + common.checkToEmpty(model.paymentCount) + "</td>" +
                        "<td>" + common.formatMoney(model.paymentAmount, 2) + "</td>" +
                        "<td>" + common.formatMoney(model.noAutoTaxAmount, 2) + "</td>" +
                        "<td>" + common.checkToEmpty(model.specialMonitor) + "</td>";
                    if (data.customerField != null && data.customerField.length > 0) {
                        if (data.customerField.length == 1) {
                            data_content += "<td>" + common.formatMoney(model.customerField1, 2) + "</td>";
                        } else if (data.customerField.length == 2) {
                            data_content += "<td>" + common.formatMoney(model.customerField1, 2) + "</td>";
                            data_content += "<td>" + common.formatMoney(model.customerField2, 2) + "</td>";
                        } else if (data.customerField.length == 3) {
                            data_content += "<td>" + common.formatMoney(model.customerField1, 2) + "</td>";
                            data_content += "<td>" + common.formatMoney(model.customerField2, 2) + "</td>";
                            data_content += "<td>" + common.formatMoney(model.customerField3, 2) + "</td>";
                        } else if (data.customerField.length == 4) {
                            data_content += "<td>" + common.formatMoney(model.customerField1, 2) + "</td>";
                            data_content += "<td>" + common.formatMoney(model.customerField2, 2) + "</td>";
                            data_content += "<td>" + common.formatMoney(model.customerField3, 2) + "</td>";
                            data_content += "<td>" + common.formatMoney(model.customerField4, 2) + "</td>";
                        } else if (data.customerField.length == 5) {
                            data_content += "<td>" + common.formatMoney(model.customerField1, 2) + "</td>";
                            data_content += "<td>" + common.formatMoney(model.customerField2, 2) + "</td>";
                            data_content += "<td>" + common.formatMoney(model.customerField3, 2) + "</td>";
                            data_content += "<td>" + common.formatMoney(model.customerField4, 2) + "</td>";
                            data_content += "<td>" + common.formatMoney(model.customerField5, 2) + "</td>";
                        }
                    }
                    data_content += "</tr>";
                });
            }
            return data_content;
        },

        /**
         * 导出数据到excel
         */
        exportHourExcel: function (activityId) {
            var url = "/operationcenter/activities/" + activityId + "/export";
            $("#exportHourExcel").attr("href", url);
        },

        /**
         * 更新监控数据
         * @param activityId
         */
        refreshData: function () {
            // 更新监控数据间隔：一小时更新一次
            var refreshTimeStr = $("#refreshTime").html();
            if (!common.isEmpty(refreshTimeStr)) {
                var currentTime = new Date();
                var refreshTime = new Date(refreshTimeStr);
                var hours = common.dateDiff(refreshTime, currentTime, "hour");
                if (hours < 1) {
                    popup.mould.popTipsMould("一小时只能更新一次监控数据，请稍后再试！", popup.mould.second, popup.mould.error, "", "57%", null);
                    return false;
                }
            }

            // $("#refreshData").attr("disabled", true);
            var activityId = $("#detail_activityId").val();// 商务活动id
            common.getByAjax(true, "get", "json", "/operationcenter/activities/" + activityId + "/update", null,
                function (data) {
                    if (data == null) {
                        return false;
                    }

                    $("#refreshTime").text(common.isEmpty(data.refreshTime) ? '无' : data.refreshTime);//数据更新时间
                    $("#detail_refreshFlag").val(data.refreshFlag);

                    // 监控城市选择全国
                    $(".tabs a").siblings(".selected").removeClass("selected").addClass("btn-default");
                    $(".tabs a:first").removeClass("btn-default").addClass("selected");

                    // 清空原监控数据，更新最新数据
                    $("#monitorData_tab tbody").empty();
                    $("#monitorData_tab tbody").append(activity.showActivity.writeData(data));
                    // $("#refreshData").attr("disabled", false);
                }, function () {
                    popup.mould.popTipsMould("获取监控数据失败，请稍后再试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    },

    editActivity: {
        content: "",

        /**
         * 编辑商务活动页面
         */
        edit: function (id) {
            activity.initActivity.initEditPopupContent();
            popup.pop.popInput(activity.editActivity.content, "first", "600px", "543px", "38%", "56%");
            common.getByAjax(true, "get", "json", "/operationcenter/activities/" + id, null,
                function (data) {
                    if (data == null) {
                        return false;
                    }
                    activity.editActivity.write(data);
                }, function () {
                    popup.mould.popTipsMould("获取详情失败，请稍后再试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );

            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask();
                }

            });

            $("#edit_form").validate(activity.edit_validation);
        },

        /**
         * 显示编辑内容
         * @param data
         */
        write: function (data) {
            var partnerName = common.isEmpty(data.partnerName) ? '无' : data.partnerName;
            var cooperationModeName = common.isEmpty(data.cooperationModeName) ? '无' : data.cooperationModeName;
            var cityContent = "无";
            var cities = data.city.split(",");
            if (cities.length <= 2) {
                cityContent = data.city;
            } else {
                cityContent = cities[0] + "," + cities[1] + "," + cities[2] + "...";
            }
            $("#title_id_name").text('<' + "HD" + data.id + '>' + data.name + "-" + data.code);//<id>活动名称
            /*$("#title_partner_cooperationMode").text(partnerName + " — " + cooperationModeName + " — " + cityContent);//合作商-合作方式-城市
             $("#title_landingPage").text(common.isEmpty(data.landingPage)? '无' : data.landingPage);//落地页*/

            $("#edit_email").val(common.isEmpty(data.email) ? '' : data.email);//报表接收邮箱
            if (data.frequency == 1) {
                $("#edit_frequency_week").attr("checked", true);
                $("#edit_frequency_month").attr("checked", false);
                $("#edit_frequency_no").attr("checked", false);
            } else if (data.frequency == 2) {
                $("#edit_frequency_week").attr("checked", false);
                $("#edit_frequency_month").attr("checked", true);
                $("#edit_frequency_no").attr("checked", false);
            } else if (data.frequency == 3) {
                $("#edit_frequency_week").attr("checked", false);
                $("#edit_frequency_month").attr("checked", false);
                $("#edit_frequency_no").attr("checked", true);
            }
            if (data.cooperationModeName.toUpperCase() == 'CPS') {
                $("#edit_rebate").val(data.rebate == null ? '' : data.rebate);//佣金
            } else {
                $("#edit_rebate_div").hide();
            }
            $("#edit_linkMan").val(common.isEmpty(data.linkMan) ? '' : data.linkMan);//联系人
            $("#edit_mobile").val(common.isEmpty(data.mobile) ? '' : data.mobile);//联系方式
            $("#edit_budget").val(data.budget == null ? '' : data.budget);//预算

            $("#editStartTimeShow").val(data.startTime == null ? '' : data.startTime.replace(":", "："));//开始时间
            $("#edit_startTime").val(data.startTime == null ? '' : (data.startTime + ":00"));//开始时间
            $("#editEndTimeShow").val(data.endTime == null ? '' : data.endTime.replace(":", "："));//结束时间
            $("#edit_endTime").val(data.endTime == null ? '' : (data.endTime + ":00"));//结束时间
            if (data.landingPageType == "3" || data.landingPageType == "4") {
                $("#edit_startTimeRemark").text(common.checkToEmpty(data.beginDate));
                $("#edit_endTimeRemark").text(common.checkToEmpty(data.endDate));
                $("#edit_marketing_remark_div").show();
            } else {
                $("#edit_marketing_remark_div").hide();
            }

            $("#edit_comment").val(data.comment == null ? '' : data.comment);//备注
            //以下字段是用不到的，但是校验框架原因，故加上
            $("#edit_id").val(data.id == null ? '' : data.id);
            $("#edit_name").val(data.name == null ? '' : data.name);//名称
            $("#edit_partner").val(data.partner == null ? '' : data.partner);//合作商id
            $("#edit_objId").val(data.objId == null ? '' : data.objId);//推广活动id
            $("#edit_cooperationMode").val(data.cooperationMode == null ? '' : data.cooperationMode);//合作方式id
            $("#edit_cooperationModeName").val(data.cooperationModeName == null ? '' : data.cooperationModeName);//合作方式名称
            $("#edit_landingPage").val(data.landingPage == null ? '' : data.landingPage);//落地页
        },

        /**
         * 保存编辑活动内容
         * @param id
         */
        save: function (id) {
            common.getByAjax(false, "get", "json", "/operationcenter/activities/check",
                {
                    code: null,
                    marketingId: $("#edit_objId").val(),
                    startTime: $("#edit_startTime").val()
                },
                function (data) {
                    if (data.pass) {
                        $("#update").attr("disabled", true);
                        common.getByAjax(true, "put", "json", "/operationcenter/activities/" + id, $("#edit_form").serialize(),
                            function (data) {
                                $("#update").attr("disabled", false);
                                if (data) {
                                    popup.mask.hideAllMask();
                                    popup.mould.popTipsMould("编辑商务活动成功！", popup.mould.first, popup.mould.success, "", "57%",
                                        function () {
                                            popup.mask.hideFirstMask();
                                            datatableUtil.params.keyWord = "";
                                            datatables.ajax.reload();
                                            window.scrollTo(0, 0);
                                        }
                                    );
                                } else {
                                    popup.mould.popTipsMould("编辑商务活动失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                                }
                            }, function () {
                                $("#update").attr("disabled", false);
                                popup.mould.popTipsMould("操作失败，请稍后再试！", popup.mould.second, popup.mould.error, "", "57%", null);
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.error, "", "57%", null);
                        return false;
                    }
                }, function () {
                }
            );
        }
    }
};
/**
 *查看商务活动详情
 */
function detail(id) {
    activity.showActivity.detail(id);
}

var datatables = datatableUtil.getByDatatables(activity, dataFunction.data, dataFunction.fnRowCallback);

$(function () {
    if (!common.permission.validUserPermission("op0102")) {
        return;
    }

    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould("请输入搜索内容", "first", "warning", "", "", null);
                return false;
            }
            datatableUtil.params.keyWord = keyword;
            datatableUtil.params.keyType = $("#keyType").val();
            datatables.ajax.reload();
        }
    });

    /**
     * 新建商务活动页面
     */
    $("#toNew").bind({
        click: function () {
            if (!common.permission.validUserPermission("op010202")) {
                return;
            }
            activity.newActivity.create();
        }
    });

    /**
     * 导出商务活动到excel中
     */
    $("#exportExcel").bind({
        click: function () {
            if (!common.permission.validUserPermission("op010201")) {
                return;
            }
            activity.listActivity.exportExcel();
        }
    });

});
