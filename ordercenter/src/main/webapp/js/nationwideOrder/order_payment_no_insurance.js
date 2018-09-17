/**
 * Created by wangfei on 2015/11/20.
 */
var paymentNoInsurance = {
    params: {
        page: new Properties(1, ""),
        keyType: "",
        insuranceInfoContent: ""
    },
    initialization: {
        init: function() {
            this.initParams();
            this.initPage();
        },
        initParams: function() {
            var params = paymentNoInsurance.params;
            var insuranceInfoContent = $("#insuranceInfo_content");
            if (insuranceInfoContent.length > 0) {
                params.insuranceInfoContent = insuranceInfoContent.html();
                insuranceInfoContent.remove();
            }
        },
        initPage: function() {
            paymentNoInsurance.interface.getAreas(function(data) {
                var $area = $("#areaId");
                var options = "";
                $.each(data, function(index, area) {
                    options += "<option value=\"" + area.id + "\">" + area.name + "</option>";
                });
                $area.append(options);
                $area.unbind("change").bind({
                    change: function() {
                        paymentNoInsurance.params.page.currentPage = 1;
                        paymentNoInsurance.params.page.keyword = $("#keyword").val();
                        paymentNoInsurance.params.keyType = $("#keyType").val();
                        paymentNoInsurance.params.sourceChannel = false;
                        paymentNoInsurance.list.listOrders();
                    }
                });
            });
        }
    },
    list: {
        listOrders: function() {
            paymentNoInsurance.interface.getOrdersByPage(function(data) {
                var $pagination = $(".customer-pagination");
                var $totalCount = $("#totalCount");
                var $tabBody = $("#list_tab tbody");
                $tabBody.empty();
                if (data.pageInfo.totalElements < 1) {
                    $totalCount.text("0");
                    $pagination.hide();
                    if (!common.isEmpty(paymentNoInsurance.params.page.keyword)) {
                        popup.mould.popTipsMould(false, "无符合条件的结果", popup.mould.first, "", "", "", null);
                    }
                    return false;
                }
                $totalCount.text(data.pageInfo.totalElements);
                if (data.pageInfo.totalPage > 1) {
                    $pagination.show();
                    $.jqPaginator('.pagination',
                        {
                            totalPages: data.pageInfo.totalPage,
                            visiblePages: paymentNoInsurance.params.page.visiblePages,
                            currentPage: paymentNoInsurance.params.page.currentPage,
                            onPageChange: function (pageNum, pageType) {
                                if (pageType == "change") {
                                    paymentNoInsurance.params.page.currentPage = pageNum;
                                    paymentNoInsurance.list.listOrders();
                                }
                            }
                        }
                    );
                } else {
                    $pagination.hide();
                }
                $tabBody.append(paymentNoInsurance.list.fixTabContent(data.viewList));
                common.scrollToTop();
            });
        },
        fixTabContent: function(dataList) {
            var content = "";
            $.each(dataList, function(index, model) {
                content +=
                    "<tr id=\"item_" + model.id + "\">" +
                    paymentNoInsurance.list.fixOneItem(model) +
                    "</tr>";
            });
            return content;
        },
        fixOneItem: function(model) {
            var tdPrefix = "<td class=\"text-center\">";
            var tdEnd = "</td>";
            return (
                tdPrefix +common.getOrderIcon(model.channelIcon)+
                "<a href=\"/page/nationwideOrder/order_detail.html?id=" + model.id + "\" target=\"_blank\">" + model.orderNo + "</a><br/>" +
                "<span>" + model.createTime + "</span>" +
                tdEnd +
                tdPrefix +
                "<span>" + common.tools.checkToEmpty(model.owner) + "</span><br/>" +
                "<span>" + common.tools.checkToEmpty(model.licensePlateNo) + "</span>" +
                tdEnd +
                tdPrefix +
                model.area.name +
                tdEnd +
                tdPrefix +
                model.insuranceCompany.name +
                tdEnd +
                tdPrefix +
                (model.institution ? model.institution.name : "") +
                tdEnd +
                tdPrefix +
                "<span id=\"item_operator_" + model.id + "\">" + common.tools.checkToEmpty(model.operatorName) + "</span><br/>" +
                "<span id=\"item_updateTime_" + model.id + "\">" + common.tools.checkToEmpty(model.updateTime) + "</span>" +
                tdEnd +
                tdPrefix +
                "<a href=\"javascript:;\" onclick=\"orderComment.popCommentList(" + model.purchaseOrderId + ", 'first');\">查看备注</a>" +
                tdEnd +
                tdPrefix +
                "<a href=\"javascript:;\" onclick=\"paymentNoInsurance.insuranceInfo.popInsuranceInfo(" + model.id + "," + model.purchaseOrderId + ");\">出单信息</a>" +
                "<a href=\"javascript:;\" onclick=\"paymentNoInsurance.exceptionStatus.setExceptionStatus(" + model.id + ");\" style=\"margin-left: 10px;\">订单异常</a>" +
                tdEnd
            );
        }
    },
    exceptionStatus: {
        setExceptionStatus: function(id) {
            if (!common.permission.validUserPermission("or07010801")) {
                return;
            }
            abnormal_pop.popup(id, function() {
                popup.mask.hideAllMask(false);
                paymentNoInsurance.list.listOrders();
            });
        }
    },
    insuranceInfo: {
        popInsuranceInfo: function(id, purchaseOrderId) {
            if (!common.permission.validUserPermission("or07010801")) {
                return;
            }
            paymentNoInsurance.interface.getOrderCooperationInfo(id,
                function(cooperationInfo) {
                    var tdPrefix = "<td class=\"text-center\">";
                    var tdEnd = "</td>";
                    popup.pop.popInput(false, paymentNoInsurance.params.insuranceInfoContent, popup.mould.first, "500px", "auto", "40%", "54%");
                    var $popInput = window.parent.$("#popover_normal_input");
                    var $orderTab = $popInput.find("#detail_order_tab");
                    $popInput.find(".theme_poptit .close").unbind("click").bind({
                        click: function () {
                            popup.mask.hideFirstMask(false);
                        }
                    });
                    $orderTab.find("tbody").empty().append(
                        "<tr>" +
                        tdPrefix +
                        "<a href=\"/page/nationwideOrder/order_detail.html?id=" + cooperationInfo.id + "\" target=\"_blank\">" + cooperationInfo.orderNo + "</a><br/>" +
                        "<span>" + cooperationInfo.createTime + "</span>" +
                        tdEnd +
                        tdPrefix +
                        "<span>" + common.tools.checkToEmpty(cooperationInfo.owner) + "</span><br/>" +
                        "<span>" + common.tools.checkToEmpty(cooperationInfo.licensePlateNo) + "</span>" +
                        tdEnd +
                        tdPrefix +
                        cooperationInfo.area.name +
                        tdEnd +
                        tdPrefix +
                        cooperationInfo.insuranceCompany.name +
                        tdEnd +
                        "</tr>"
                    );
                    var areaContactInfo = cooperationInfo.areaContactInfo;
                    if (areaContactInfo) {
                        $popInput.find("#areaContactName").text(areaContactInfo.name);
                        $popInput.find("#areaContactMobile").text(areaContactInfo.mobile);
                        $popInput.find("#areaContactAddress").text(areaContactInfo.detailAddress);
                    }
                    $popInput.find(".toSave").unbind("click").bind({
                        click: function() {
                            var validation = paymentNoInsurance.insuranceInfo.validFields();
                            if (!validation.flag) {
                                paymentNoInsurance.insuranceInfo.showError(validation.msg);
                                return;
                            }
                            $popInput.find(".error-msg").hide();
                            var warnValidation = validWarning();
                            if (!warnValidation.flag) {
                                popup.mould.popConfirmMould(false, warnValidation.msg, popup.mould.second, "", "55%",
                                    function() {
                                        popup.mask.hideSecondMask(false);
                                        updateInsuranceInfo();
                                    },
                                    function() {
                                        popup.mask.hideSecondMask(false);
                                    }
                                );
                            } else {
                                updateInsuranceInfo();
                            }

                            function updateInsuranceInfo() {
                                paymentNoInsurance.interface.updateInsuranceInfo(purchaseOrderId, function() {
                                    paymentNoInsurance.interface.setOrderStatus(id, 5, null, function() {
                                        popup.mould.popTipsMould(false, "保存出单信息成功！", popup.mould.second, popup.mould.success, "", "57%", function() {
                                            popup.mask.hideAllMask(false);
                                            paymentNoInsurance.list.listOrders();
                                        });
                                    });
                                });
                            }

                            function validWarning() {
                                var $compulsoryPolicyNo = $popInput.find("#compulsoryPolicyNo");
                                var $commercialPolicyNo = $popInput.find("#commercialPolicyNo");
                                var flag=true,msg="成功";
                                if (common.validations.isEmpty($compulsoryPolicyNo.val())) {
                                    msg = "交强险保单号未填写， 确定保存？";
                                    flag = false;
                                } else if (common.validations.isEmpty($commercialPolicyNo.val())) {
                                    msg = "商业险保单号未填写， 确定保存？";
                                    flag = false;
                                }
                                return {flag: flag, msg: msg};
                            }
                        }
                    });
                }
            );
        },
        validFields: function() {
            var $popInput = window.parent.$("#popover_normal_input");
            var $compulsoryPolicyNo = $popInput.find("#compulsoryPolicyNo");
            var $commercialPolicyNo = $popInput.find("#commercialPolicyNo");
            var $expressCompany = $popInput.find("#expressCompany");
            var $trackingNo = $popInput.find("#trackingNo");
            if (common.validations.isEmpty($compulsoryPolicyNo.val()) && common.validations.isEmpty($commercialPolicyNo.val())) {
                return {flag: false, msg: "交强险/商业险保单号不可同时为空"};
            }
            if (common.validations.isEmpty($expressCompany.val())) {
                return {flag: false, msg: "请输入快递公司"};
            }
            if (common.validations.isEmpty($trackingNo.val())) {
                return {flag: false, msg: "请输入快递单号"};
            }
            return {flag: true, msg: "成功"};
        },
        showError: function(msg) {
            var $popInput = window.parent.$("#popover_normal_input");
            $popInput.find(".error-msg").show();
            $popInput.find("#errorText").text(msg);
        }
    },
    interface: {
        getOrdersByPage: function(callback) {
            var reqParams = {
                currentPage :  paymentNoInsurance.params.page.currentPage,
                pageSize :     paymentNoInsurance.params.page.pageSize,
                sourceChannel :paymentNoInsurance.params.sourceChannel,
                areaId:        $("#areaId").val(),
                statusId:      4
            };
            switch (paymentNoInsurance.params.keyType) {
                case "1":
                    reqParams.orderNo = paymentNoInsurance.params.page.keyword;
                    break;
                case "2":
                    reqParams.owner = paymentNoInsurance.params.page.keyword;
                    break;
                case "3":
                    reqParams.licensePlateNo = paymentNoInsurance.params.page.keyword;
                    break;
            }
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderCooperationInfos", reqParams,
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单列表异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        getAreas: function(callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/areaContactInfo/area", {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取区域异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        getOrderCooperationInfo: function(id, callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderCooperationInfos/" + id, {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单信息异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            );
        },
        updateInsuranceInfo: function(purchaseOrderId, callback) {
            var $popInput = window.parent.$("#popover_normal_input");
            var $compulsoryPolicyNo = $popInput.find("#compulsoryPolicyNo");
            var $commercialPolicyNo = $popInput.find("#commercialPolicyNo");
            var $expressCompany = $popInput.find("#expressCompany");
            var $trackingNo = $popInput.find("#trackingNo");
            common.ajax.getByAjax(true, "post", "json", "/orderCenter/deliveryInfos/purchaseOrder/" + purchaseOrderId,
                {
                    compulsoryPolicyNo: $compulsoryPolicyNo.val(),
                    commercialPolicyNo: $commercialPolicyNo.val(),
                    expressCompany:     $expressCompany.val(),
                    trackingNo:         $trackingNo.val()
                },
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "更新保单信息异常！", popup.mould.second, popup.mould.error, "", "", null);
                }
            );
        },
        setOrderStatus: function(id, status, reasonId, callback) {
            common.ajax.getByAjax(true, "put", "json", "/orderCenter/orderCooperationInfos/" + id + "/status", {newStatus: status, reasonId: reasonId},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mask.hideSecondMask(false);
                    popup.mould.popTipsMould(false, "更新审核状态异常！", popup.mould.second, popup.mould.error, "", "", null);
                }
            );
        }
    }
};

$(function() {
    paymentNoInsurance.initialization.init();
    paymentNoInsurance.list.listOrders();

    /*
     * 搜索框
     * */
    $("#searchBtn").unbind("click").bind({
        click : function(){
            var keyword = $("#keyword").val();
            if(common.isEmpty(keyword)){
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            paymentNoInsurance.params.page.currentPage = 1;
            paymentNoInsurance.params.page.keyword = keyword;
            paymentNoInsurance.params.keyType = $("#keyType").val();
            paymentNoInsurance.params.sourceChannel = false;
            paymentNoInsurance.list.listOrders();
        }
    });
    //支付宝订单
    $("#alipayBtn").bind({
        click : function(){
            paymentNoInsurance.params.page.currentPage = 1;
            paymentNoInsurance.params.page.keyword = $("#keyword").val();
            paymentNoInsurance.params.keyType = $("#keyType").val();
            paymentNoInsurance.params.sourceChannel = true;
            paymentNoInsurance.list.listOrders();
        }
    });
});
