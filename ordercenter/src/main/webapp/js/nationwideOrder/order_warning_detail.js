/**
 * Created by wangfei on 2015/11/18.
 */
var warningDetail = {
    popDetail: function(id, purchaseOrderId, position, btn_control, passMethod, disPassMethod) {
        $.post("order_warning_detail.html", {}, function (warningContent) {
            warningDetail.interface.getOrderCooperationInfo(id, function(cooperationInfo) {
                var institutionInsurancePackage = cooperationInfo.quoteRecord ? cooperationInfo.quoteRecord.insurancePackage : null;
                warningDetail.interface.getOrderInsurancePackage(purchaseOrderId, function(orderInsurancePackage) {
                    popup.pop.popInput(false, warningContent, position, "850px", "auto", "36%", "46%");
                    var $popInput;
                    switch (position) {
                        case popup.mould.first:
                            $popInput = window.parent.$("#popover_normal_input");
                            break;
                        case popup.mould.second:
                            $popInput = window.parent.$("#popover_normal_input_second");
                            break;
                    }
                    $popInput.find(".theme_poptit .close").unbind("click").bind({
                        click: function () {
                            popup.mask.hideFirstMask(false);
                        }
                    });
                    warningDetail.fixDetailContent(cooperationInfo, orderInsurancePackage, institutionInsurancePackage, $popInput);
                    var $btnGroup = $popInput.find(".btn-group-check");
                    btn_control ? $btnGroup.show() : $btnGroup.hide();

                    if (btn_control) {
                        $btnGroup.show();
                        $popInput.find(".toPass").unbind("click").bind({
                            click: function () {
                                popup.mould.popConfirmMould(false, "确定通过审核？", popup.mould.second, "", "",
                                    function() {
                                        warningDetail.interface.setOrderStatus(id, 3, null, function(data) {
                                            passMethod(data);
                                        });
                                    },
                                    function() {
                                        popup.mask.hideSecondMask(false);
                                    }
                                );
                            }
                        });
                        $popInput.find(".toDisPass").unbind("click").bind({
                            click: function () {
                                popup.mould.popConfirmMould(false, "确定该订单审核不通过吗？该订单将被置为订单异常。", popup.mould.second, "", "",
                                    function() {
                                        warningDetail.interface.setOrderStatus(id, 7, 5, function(data) {
                                            disPassMethod(data);
                                        });
                                    },
                                    function() {
                                        popup.mask.hideSecondMask(false);
                                    }
                                );
                            }
                        });
                    } else {
                        $btnGroup.hide();
                    }
                });
            });
        });
    },
    fixDetailContent: function(cooperationInfo, orderInsurancePackage, institutionInsurancePackage, $popInput) {
        var tdPrefix = "<td class=\"text-center\">";
        var tdEnd = "</td>";
        var $orderTab = $popInput.find("#detail_order_tab");
        var $compareTab = $popInput.find("#detail_compare_tab");
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
        $compareTab.find("#detail_income_td").html(
            cooperationInfo.incomeStatus ? "<span class=\"font-18\" style=\"color: #008000;\">正常</span>" : "<span class=\"font-18\" style=\"color: red;\">异常</span>"
        );
        $compareTab.find("#detail_payableAmount_td").text(common.tools.formatMoney(cooperationInfo.payableAmount, 2) + "元");
        $compareTab.find("#detail_paidAmount_td").text(cooperationInfo.quoteRecord ? (common.tools.formatMoney(cooperationInfo.quoteRecord.premium - cooperationInfo.quoteRecord.rebate, 2) + "元") : "0.00元");

        $compareTab.find("#detail_match_td").html(
            cooperationInfo.matchStatus ? "<span class=\"font-18\" style=\"color: #008000;\">匹配</span>" : "<span class=\"font-18\" style=\"color: red;\">不匹配</span>"
        );

        var itemsArray = this.getCompareItems();
        $compareTab.find("#detail_cheche_td").html(warningDetail.getCompareItemsContent(orderInsurancePackage, null, false, itemsArray));
        $compareTab.find("#detail_institution_td").html(warningDetail.getCompareItemsContent(institutionInsurancePackage, orderInsurancePackage, true, itemsArray));
    },
    getCompareItems: function() {
        return [
            {
                name: "compulsory",
                type: "text",
                source: "compulsory"
            },
            {
                name: "autoTax",
                type: "text",
                source: "compulsory"
            },
            {
                name: "thirdPartyAmount",
                type: "amount",
                source: "commercial"
            },
            {
                name: "damage",
                type: "text",
                source: "commercial"
            },
            {
                name: "driverAmount",
                type: "amount",
                source: "commercial"
            },
            {
                name: "passengerAmount",
                type: "amount",
                source: "commercial"
            },
            {
                name: "engine",
                type: "text",
                source: "commercial"
            },
            {
                name: "glass",
                type: "text",
                source: "commercial"
            },
            {
                name: "scratchAmount",
                type: "amount",
                source: "commercial"
            },
            {
                name: "theft",
                type: "text",
                source: "commercial"
            },
            {
                name: "spontaneousLoss",
                type: "text",
                source: "commercial"
            },
            {
                name: "iop",
                type: "text",
                source: "commercial"
            }
        ];
    },
    getCompareItemsContent: function(compareInsurancePackage, reference, needCompare, compareItems) {
        if (!compareInsurancePackage) {
            return "";
        }
        var commercialItems =
            "<div class=\"form-group form-group-fix\">" +
            "<div class=\"col-sm-12 text-left\">" +
            "<span class=\"text-center bold-font\" style=\"padding-left: 0;padding-right: 0;\">商业险</span>" +
            "</div>" +
            "</div>";
        var compulsoryItems =
            "<div class=\"form-group form-group-fix\">" +
            "<div class=\"col-sm-12 text-left\">" +
            "<span class=\"text-center bold-font\" style=\"padding-left: 0;padding-right: 0;\">国家强制险</span>" +
            "</div>" +
            "</div>";
        var isChecked,spanStyle,itemVal,differenceStyle;
        for (var item in compareItems) {
            isChecked = spanStyle = itemVal = differenceStyle = "";
            var itemName = compareItems[item].name;
            switch (compareItems[item].type) {
                case "amount":
                    if (compareInsurancePackage[itemName] && compareInsurancePackage[itemName] > 0) {
                        isChecked = "checked";
                        itemVal = compareInsurancePackage[itemName];
                    } else {
                        spanStyle = "color: darkgray;";
                        itemVal = "不投保";
                    }
                    if (needCompare && reference) {
                        var compareAmount = compareInsurancePackage[itemName];
                        var referenceAmount = reference[itemName];
                        if (compareAmount && referenceAmount) {
                            if (compareInsurancePackage[itemName] != reference[itemName]) {
                                differenceStyle = "color: red;";
                            }
                        } else if (!(!compareAmount && !referenceAmount)) {
                            differenceStyle = "color: red;";
                        }
                    }
                    break;
                case "text":
                    if (itemName == "iop") {
                        if (compareInsurancePackage["thirdPartyIop"] || compareInsurancePackage["damageIop"] || compareInsurancePackage["theftIop"]
                            || compareInsurancePackage["engineIop"] || compareInsurancePackage["driverIop"] || compareInsurancePackage["passengerIop"]
                            || compareInsurancePackage["scratchIop"]) {
                            isChecked = "checked";
                            itemVal = "投保";
                        } else {
                            spanStyle = "color: darkgray;";
                            itemVal = "不投保";
                        }
                    } else {
                        if (compareInsurancePackage[itemName]) {
                            isChecked = "checked";
                            switch (itemName) {
                                case "autoTax":
                                    itemVal = "缴纳";
                                    break;
                                case "glass":
                                    itemVal = compareInsurancePackage["glassType"].name;
                                    break;
                                default :
                                    itemVal = "投保";
                                    break;
                            }
                        } else {
                            spanStyle = "color: darkgray;";
                            switch (itemName) {
                                case "autoTax":
                                    itemVal = "未缴纳";
                                    break;
                                default :
                                    itemVal = "不投保";
                                    break;
                            }
                        }
                    }
                    if (needCompare && reference) {
                        switch (itemName) {
                            case "glass":
                                //一个投保，一个未投保
                                var condition1 = (compareInsurancePackage[itemName] != reference[itemName]);
                                //都投保但类型不同
                                var condition2 = compareInsurancePackage[itemName] && reference[itemName] && compareInsurancePackage["glassType"].name != reference["glassType"].name;

                                if (condition1 || condition2) {
                                    differenceStyle = "color: red;";
                                }
                                break;
                            case "iop":
                                var needCompareIop = compareInsurancePackage["thirdPartyIop"] || compareInsurancePackage["damageIop"] || compareInsurancePackage["theftIop"]
                                    || compareInsurancePackage["engineIop"] || compareInsurancePackage["driverIop"] || compareInsurancePackage["passengerIop"]
                                    || compareInsurancePackage["scratchIop"];
                                var referenceIop = reference["thirdPartyIop"] || reference["damageIop"] || reference["theftIop"]
                                    || reference["engineIop"] || reference["driverIop"] || reference["passengerIop"]
                                    || reference["scratchIop"];

                                if (needCompareIop != referenceIop) {
                                    differenceStyle = "color: red;";
                                }
                                break;
                            default :
                                if (compareInsurancePackage[itemName] != reference[itemName]) {
                                    differenceStyle = "color: red;"
                                }
                                break;
                        }
                    }
                    break;
            }
            var itemContent =
                "<div class=\"form-group form-group-fix\">" +
                "<div class=\"col-sm-8 text-left\">" +
                "<div class=\"checkbox\">" +
                "<label>" +
                "<input  type=\"checkbox\" " + isChecked + " disabled> <span style=\"height:40px;" + spanStyle + differenceStyle + "\">" + this.getKindName(itemName) + "</span>" +
                "</label>" +
                "</div>" +
                "</div>" +
                "<div class=\"col-sm-4 text-center\">" +
                "<span style=\"height:40px;line-height:40px;" + spanStyle + differenceStyle + "\">" + itemVal + "</span>" +
                "</div>" +
                "</div>";
            if (compareItems[item].source == "commercial") {
                commercialItems += itemContent;
            } else if (compareItems[item].source == "compulsory") {
                compulsoryItems += itemContent;
            }
        }
        return compulsoryItems + commercialItems;
    },
    getKindName: function(name) {
        switch (name) {
            case "compulsory":
                return "机动车交通事故责任强制险";
            case "autoTax":
                return "车船使用税";
            case "thirdPartyAmount":
                return "机动车第三者责任保险";
            case "damage":
                return "机动车损失险";
            case "driverAmount":
                return "车上人员责任险（司机）";
            case "passengerAmount":
                return "车上人员责任险（乘客）";
            case "engine":
                return "发动机特别损失险";
            case "glass":
                return "玻璃单独破碎险";
            case "scratchAmount":
                return "车身划痕损失险";
            case "theft":
                return "机动车盗抢险";
            case "spontaneousLoss":
                return "自燃损失险";
            case "iop":
                return "不计免赔险";
        }
    },
    interface: {
        getOrderInsurancePackage: function(purchaseOrderId, callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/order/" + purchaseOrderId + "/insurancePackage", {},
                function(data) {
                    callback(data.orderInsurancePackage);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单险种套餐异常！", popup.mould.first, popup.mould.error, "", "", null);
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
