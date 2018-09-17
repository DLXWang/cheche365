var order_done = {
    initOrder: {
        /**
         * 初始化下拉列表
         */
        init: function(){
            order_done.initOrder.initArea();
        },

        /**
         * 获取区域
         */
        initArea: function(){
            common.ajax.getByAjax(true,"get","json","/orderCenter/nationwide/areaContactInfo/area",null,
                function(data){
                    if(data == null){
                        return false;
                    }
                    var options = "<option value='0'>全部区域</option>";
                    $.each(data, function(i,model){
                        options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                    });
                    $("#areaSel").append(options);
                },function(){}
            );
        }
    },
    listOrder: {
        properties : new Properties(1, ""),

        findResult: function() {
            order_done.listOrder.properties.keyword = $("#keyword").val();
            order_done.listOrder.properties.keyType = $("#keyType").val();
            order_done.listOrder.properties.currentPage = 1;
            order_done.listOrder.list();
        },

        /**
         * 合作订单列表
         */
        list : function() {
            //if (!common.permission.validUserPermission("op0102")) {
            //    return;
            //}
            var reqParams = {
                currentPage:        order_done.listOrder.properties.currentPage,
                pageSize:           order_done.listOrder.properties.pageSize,
                sourceChannel:      order_done.listOrder.properties.sourceChannel,
                areaId:             $("#areaSel").val()
            };
            switch (order_done.listOrder.properties.keyType) {
                case "1":
                    reqParams.orderNo = order_done.listOrder.properties.keyword;
                    break;
                case "2":
                    reqParams.owner = order_done.listOrder.properties.keyword;
                    break;
                case "3":
                    reqParams.licensePlateNo = order_done.listOrder.properties.keyword;
                    break;
                case "4":
                    reqParams.policyNo = order_done.listOrder.properties.keyword;
                    break;
                case "5":
                    reqParams.trackingNo = order_done.listOrder.properties.keyword;
                    break;
            }
            //需要同步，这样页码才能获取到数据
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/done", reqParams,
                function(data) {
                    $("#done_order_tab tbody").empty();
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.validations.isEmpty(order_done.listOrder.properties.keyword)) {
                            popup.mould.popTipsMould(false, "无符合条件的结果", popup.mould.first, popup.mould.warning, "", "57%",
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
                        $.jqPaginator('.pagination',
                            {
                                totalPages: data.pageInfo.totalPage,
                                visiblePages: order_done.listOrder.properties.visiblePages,
                                currentPage: order_done.listOrder.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        order_done.listOrder.properties.currentPage = pageNum;
                                        order_done.listOrder.list();
                                    }
                                }
                            }
                        );
                        $(".customer-pagination").show();
                    } else {
                        $(".customer-pagination").hide();
                    }

                    // 显示列表数据
                    $("#done_order_tab tbody").append(order_done.listOrder.write(data));
                    common.tools.scrollToTop();
                }, function () {
                    popup.mould.popTipsMould(false, "获取已出单订单列表失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            )
        },

        /**
         * 把数据写入页面
         */
        write: function(data){
            var content = "";
            $.each(data.viewList,function(i,model){
                var compulsoryPolicyNo = "", commercialPolicyNo = "", policyNo = "", deliveryInfo = "";
                if(model.quoteRecord != null && model.quoteRecord.compulsoryPolicyNo != null) {
                    compulsoryPolicyNo = "交强险:" + model.quoteRecord.compulsoryPolicyNo;
                }
                if(model.quoteRecord != null && model.quoteRecord.commercialPolicyNo != null) {
                    commercialPolicyNo = "商业险:" + model.quoteRecord.commercialPolicyNo;
                }
                if(compulsoryPolicyNo == "" && commercialPolicyNo == "") {
                    policyNo = "";
                } else if(compulsoryPolicyNo != "" && commercialPolicyNo == "") {
                    policyNo = compulsoryPolicyNo;
                } else if(compulsoryPolicyNo == "" && commercialPolicyNo != "") {
                    policyNo = commercialPolicyNo;
                } else if(compulsoryPolicyNo != "" && commercialPolicyNo != "") {
                    policyNo = compulsoryPolicyNo + "<br/>" + commercialPolicyNo;
                }
                if(model.deliveryInfo != null) {
                    deliveryInfo = common.tools.checkToEmpty(model.deliveryInfo.expressCompany) + "<br/>" + common.tools.checkToEmpty(model.deliveryInfo.trackingNo);
                }
                content +=
                    "<tr class='text-center' id='tab_tr_" + model.id + "'>" +
                    "<td>"+common.getOrderIcon(model.channelIcon)+"<a href='order_detail.html?id=" + model.id + "' target='_blank'>" + model.orderNo + "</a><br />" + model.orderCreateTime + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.owner) + "</a><br />" + common.tools.checkToEmpty(model.licensePlateNo) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.area.name) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.insuranceCompany.name) + "</td>" +
                    "<td>" + common.tools.formatMoney(model.paidAmount, 2) + "</td>" +
                    "<td>" + (model.institution == null? "" : common.tools.checkToEmpty(model.institution.name)) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(policyNo) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(deliveryInfo) + "</td>" +
                    "<td id='operator_td_" + model.id + "'>" + common.tools.checkToEmpty(model.operatorName) + "<br />" + common.tools.checkToEmpty(model.updateTime) + "</td>" +
                    "<td><a href=\"javascript:;\" onclick=\"orderComment.popCommentList('" + model.purchaseOrderId + "', 'first');\">查看备注</a></td>" +
                    "<td><a href=\"javascript:;\" onclick=\"order_done.listOrder.inputInsurance('" + model.orderNo + "');\">录入保单</a></td>" +
                    "</tr>";
            });
            return content;
        },

        inputInsurance: function(orderNo) {
            if (!common.permission.validUserPermission("or07010901")) {
                return;
            }
            window.open("/page/nationwideOrder/order_insurance_record.html?orderNo=" + orderNo);
        }
    }
}

$(function(){
    // 初始化
    order_done.initOrder.init();

    // 查询全部订单
    order_done.listOrder.list();

    /**
     * 区域
     */
    $("#areaSel").unbind("change").bind({
        change: function() {
            order_done.listOrder.properties.sourceChannel = false;
            order_done.listOrder.findResult();
        }
    });

    /**
     * 搜索框
     */
    $("#searchBtn").unbind("click").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.validations.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            order_done.listOrder.properties.sourceChannel = false;
            order_done.listOrder.findResult();
        }
    });
    //支付宝订单
    $("#alipayBtn").bind({
        click : function(){
            order_done.listOrder.properties.sourceChannel = true;
            order_done.listOrder.findResult();
        }
    });
});
