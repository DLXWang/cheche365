var order_add = {
    page: new Properties(1, ""),
    listInfo: {
        findResult: function() {
            order_add.page.keyType = $("#keyType").val();
            order_add.page.keyword = $("#keyword").val();
            order_add.page.currentPage = 1;
            order_add.listInfo.list();
        },
        list: function () {
            var reqParams = {
                currentPage:        order_add.page.currentPage,
                pageSize:           order_add.page.pageSize,
                sourceChannel:      order_add.page.sourceChannel,
                areaId:             $("#select_area").val()
            };
            switch (order_add.page.keyType) {
                case "1":
                    reqParams.orderNo = order_add.page.keyword;
                    break;
                case "2":
                    reqParams.owner = order_add.page.keyword;
                    break;
                case "3":
                    reqParams.licensePlateNo = order_add.page.keyword;
                    break;
            }
            if(order_add.page.quoteTime != null && order_add.page.quoteTime) {
                reqParams.quoteTime = true;
            }
            common.getByAjax(true, "get", "json", "/orderCenter/nationwide/new", reqParams,
                function (data) {
                    $("#list_tab tbody").empty();
                    if (data == null) {
                        popup.mould.popTipsMould(false, "获取分站信息列表失败！", popup.mould.first, popup.mould.warning, "", "57%", null);
                        return false;
                    }
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.isEmpty(order_add.page.keyword)) {
                            popup.mould.popTipsMould(false, "无符合条件的结果", popup.mould.first, popup.mould.warning, "", "57%", null);
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
                                visiblePages: order_add.page.visiblePages,
                                currentPage: order_add.page.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType == "change") {
                                        order_add.page.currentPage = pageNum;
                                        order_add.listInfo.list();
                                    }
                                }
                            }
                        );
                    } else {
                        $(".customer-pagination").hide();
                    }
                    order_add.listInfo.fillTabContent(data);
                    window.parent.scrollTo(0, 0);
                }, function () {
                    popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        fillTabContent: function (data) {
            if (data.viewList) {
                var content = "";
                $.each(data.viewList, function (n, model) {
                    var institutionId=null;
                    var institutionName=null;
                    var appointTime=null;
                    if(model.institution!=null){
                        institutionId=model.institution.id;
                        institutionName=model.institution.name;
                        appointTime=order_add.listInfo.calculate(common.checkToEmpty(model.appointTime));
                    }
                    content += "<tr class='text-center' id='tab_tr" + model.id + "'>" +
                        "<td>"+common.getOrderIcon(model.channelIcon)+"<a href='order_detail.html?id=" + model.id + "' target='_blank'>" + model.orderNo + "</a><br>"+model.createTime+"</td>" +
                        "<td>" + model.owner + "<br>" + common.checkToEmpty(model.licensePlateNo) + "</td>" +
                        "<td>" + common.checkToEmpty(model.area.name) + "</td>" +
                        "<td>" + common.checkToEmpty(model.insuranceCompany.name) + "</td>" +
                        "<td>" + common.checkToEmpty(model.payableAmount) + "</td>" +
                        "<td>" + common.checkToEmpty(model.paidAmount) + "</td>" +
                        "<td>" + common.checkToEmpty(institutionName) + "<br><" + common.checkToEmpty(appointTime) + "m></td>" +
                        "<td>" + model.operatorName + "<br>"+ model.updateTime+"</td>" +
                        "<td id='remark_td'><a href=\"javascript:;\" onclick=\"orderComment.popCommentList(" + model.purchaseOrderId + ", 'first');\">查看备注</a></td>"
                    content += "<td>";
                    content += "&nbsp;&nbsp;<a href='javascript:;' onclick='assign_pop.listInfo.popup(" + model.area.id + "," + model.insuranceCompany.id + "," + model.id + "," + institutionId + ");'>指派出单机构</a>";

                    if(common.isEmpty(institutionId)){
                        content += "&nbsp;&nbsp;<span style='color:#8a8a8a'>报价</span>";
                    }else{
                        content += "&nbsp;&nbsp;<a href='javascript:;' onclick='order_add.listInfo.quote_pop(" + model.id + ");'>报价</a>";
                    }
                    content += "&nbsp;&nbsp;<a href='javascript:;' onclick='order_add.listInfo.abnormal_pop(" + model.id + ");'>订单异常</a>";
                    content += "</td></tr>";
                });
                $("#list_tab tbody").append(content);
            }
        },
        calculate: function (datetime) {
            datetime = datetime.replace(/-/g, "/");
            var date1 = new Date(datetime);
            var date2 = new Date();
            var minutes = Math.abs(date2 - date1) / 1000 / 60
            return parseInt(minutes);
        },
        quote_pop:function(id){
            if (!common.permission.validUserPermission("or07010301")) {
                return;
            }
            quote_pop.listInfo.popup(id,function(data){
                if (data.pass) {
                    order_add.listInfo.currentPage = 1;
                    order_add.listInfo.keyword = $("#keyword").val();
                    order_add.listInfo.list();
                    popup.mask.hideFirstMask();
                } else {
                    popup.mould.popTipsMould(false, data.message, popup.mould.second, popup.mould.error, "", "59%", null);
                }
            })
        },
        abnormal_pop:function(id){
            if (!common.permission.validUserPermission("or07010301")) {
                return;
            }
            abnormal_pop.popup(id,function(data){
                if(data){
                    popup.mask.hideFirstMask();
                    order_add.listInfo.currentPage = 1;
                    order_add.listInfo.keyword = $("#keyword").val();
                    order_add.listInfo.list();
                }else{
                    popup.mould.popTipsMould(false, data.message, popup.mould.second, popup.mould.error, "", "59%", null);
                }
            })
        }
    },
    initArea: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/nationwide/areaContactInfo/area", {},
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                    });
                    $("#select_area").append(options);
                }
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    }
}
var parent = window.parent;

var assign_pop = {
    //指派出单机构
    init: function () {
        var detailContent = $("#assign_pop");
        if (detailContent.length > 0) {
            assign_pop.detailContent = detailContent.html();
            detailContent.remove();
        }
    },
    listInfo: {
        popup: function (areaId, insuranceCompanyId, id, institutionId) {
            if (!common.permission.validUserPermission("or07010301")) {
                return;
            }
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/institution/assigned", {
                    areaId: areaId,
                    insuranceCompanyId: insuranceCompanyId
                }, function (data) {
                    popup.pop.popInput(false, assign_pop.detailContent, 'first', "500px", "340px", "33%", "57%");
                    assign_pop.listInfo.fillTabContent(data, id, institutionId);
                    parent.$("#assign_pop_close").unbind("click").bind({
                        click: function () {
                            popup.mask.hideFirstMask(false);
                        }
                    });
                    parent.$(".assign_submit").unbind("click").bind({
                        click: function () {
                            assign_pop.submit();
                        }
                    });
                },
                function () {
                    popup.mould.popTipsMould(false, "系统异常！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },
        fillTabContent: function (data, orderCooperationInfoId, institutionId) {
            if (data) {
                var content = "";
                var checked = false;
                $.each(data, function (i, model) {
                    content += "<tr class='text-center' id='tab_tr" + model.id + "'>" +
                        "<td><input type='radio' id='cho_assign' name='cho_assign' value='" + common.checkToEmpty(model.institutionId) + "'" ;
                    if (institutionId == model.institutionId) {
                        content +="checked";
                        parent.$(".assign_submit").removeAttr("disabled");
                    }
                    content+=">"+ "<input type='hidden' id='"+model.institutionId+"' orderCooperationInfoId='" + orderCooperationInfoId + "' compulsoryRebate='"+model.compulsoryRebate+"' commercialRebate='"+model.commercialRebate+"'></td>" +
                        "<td>" + common.checkToEmpty(model.institutionName) + "</td>" +
                        "<td>" + common.checkToEmpty(model.compulsoryRebate) + "%</td>" +
                        "<td>" + common.checkToEmpty(model.commercialRebate) + "%</td></tr>";
                    checked = false;
                });
                parent.$("#list_assign tbody").append(content);
                parent.$('input[name="cho_assign"]').unbind("click").bind({
                    click : function(){
                        parent.$(".assign_submit").removeAttr("disabled");
                    }
                });
            }
        }
    },
    submit: function () {
        var radio= parent.$('input[name="cho_assign"]:checked');
        var institutionId = radio.val();
        var orderCooperationInfoId=radio.next().attr("orderCooperationInfoId");
        var compulsoryRebate=radio.next().attr("compulsoryRebate");
        var commercialRebate=radio.next().attr("commercialRebate");
        common.getByAjax(true, "put", "json", "/orderCenter/nationwide/new/appoint", {
                id: orderCooperationInfoId,
                institutionId: institutionId,
                compulsoryRebate:compulsoryRebate,
                commercialRebate:commercialRebate
            }, function (data) {
                if (data.pass) {
                    order_add.listInfo.currentPage = 1;
                    order_add.listInfo.keyword = $("#keyword").val();
                    order_add.listInfo.list();
                    popup.mould.popTipsMould(false, "保存成功！", popup.mould.first, popup.mould.success, "", "59%", null);
                } else {
                    popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "59%", null);
                }
            },
            function () {
                popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    }

}





$(function () {
    order_add.listInfo.list();
    order_add.initArea();
    assign_pop.init();
    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            order_add.page.quoteTime = false;
            order_add.page.sourceChannel = false;
            order_add.listInfo.findResult();
        }
    });
    $("#btn_quote_time").bind({
        click: function () {
            order_add.page.quoteTime = true;
            order_add.page.sourceChannel = false;
            order_add.listInfo.findResult();
        }
    });
    $("#select_area").bind({
        change: function () {
            order_add.page.quoteTime = false;
            order_add.page.sourceChannel = false;
            order_add.listInfo.findResult();
        }
    });
    //支付宝订单
    $("#alipayBtn").bind({
        click : function(){
            order_add.page.quoteTime = false;
            order_add.page.sourceChannel = true;
            order_add.listInfo.findResult();
        }
    });
});
