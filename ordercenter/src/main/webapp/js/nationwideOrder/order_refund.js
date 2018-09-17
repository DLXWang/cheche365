var order_refund = {
    initOrder: {
        /**
         * 初始化下拉列表
         */
        init: function(){
            order_refund.initOrder.initArea();
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
                    $("#areaSel").empty();
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
            order_refund.listOrder.properties.keyword = $("#keyword").val();
            order_refund.listOrder.properties.keyType = $("#keyType").val();
            order_refund.listOrder.properties.currentPage = 1;
            order_refund.listOrder.list();
        },

        /**
         * 合作订单列表
         */
        list : function() {
            //if (!common.permission.validUserPermission("op0102")) {
            //    return;
            //}
            var reqParams = {
                currentPage:        order_refund.listOrder.properties.currentPage,
                pageSize:           order_refund.listOrder.properties.pageSize,
                sourceChannel:      order_refund.listOrder.properties.sourceChannel,
                areaId:             $("#areaSel").val()
            };
            switch (order_refund.listOrder.properties.keyType) {
                case "1":
                    reqParams.orderNo = order_refund.listOrder.properties.keyword;
                    break;
                case "2":
                    reqParams.owner = order_refund.listOrder.properties.keyword;
                    break;
                case "3":
                    reqParams.licensePlateNo = order_refund.listOrder.properties.keyword;
                    break;
            }
            //需要同步，这样页码才能获取到数据
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/refund", reqParams,
                function(data) {
                    $("#refund_order_tab tbody").empty();
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.validations.isEmpty(order_refund.listOrder.properties.keyword)) {
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
                                visiblePages: order_refund.listOrder.properties.visiblePages,
                                currentPage: order_refund.listOrder.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        order_refund.listOrder.properties.currentPage = pageNum;
                                        order_refund.listOrder.list();
                                    }
                                }
                            }
                        );
                        $(".customer-pagination").show();
                    } else {
                        $(".customer-pagination").hide();
                    }

                    // 显示列表数据
                    $("#refund_order_tab tbody").append(order_refund.listOrder.write(data));
                    common.tools.scrollToTop();
                }, function () {
                    popup.mould.popTipsMould(false, "获取退款订单列表失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            )
        },

        /**
         * 把数据写入页面
         */
        write: function(data){
            var content = "";
            $.each(data.viewList,function(i,model){
                var institutionPaidAmount = 0.00;
                if(model.quoteRecord != null) {
                    institutionPaidAmount = parseFloat(parseFloat(model.quoteRecord.premium) - parseFloat(model.quoteRecord.rebate));
                }
                var userStatus = "<span style='color: orange;'>待退款给用户</span>";
                var userStatusURL = "<a href=\"javascript:;\" onclick=\"order_refund.listOrder.refundToUser('" + model.id + "');\">已退款给用户</a>";
                var checheStatus = "<span style='color: orange;'>待退款给车车</span>";
                var checheStatusURL = "<a href=\"javascript:;\" onclick=\"order_refund.listOrder.refundToCheche('" + model.id + "');\">已退款给车车</a>";
                if(model.refund != null) {
                    if(model.refund.userStatus != null && model.refund.userStatus) {
                        userStatus = "<span style='color: green;'>已退款给用户</span>";
                        userStatusURL = "<span style='color: darkgray;'>已退款给用户</span>";
                    }
                    if(model.refund.checheStatus != null && model.refund.checheStatus) {
                        checheStatus = "<span style='color: green;'>已退款给车车</span>";
                        checheStatusURL = "<span style='color: darkgray;'>已退款给车车</span>";
                    }
                }
                var refundStatus = userStatus + (checheStatus == ""? "" : "<br/>" + checheStatus);
                var refundStatusURL = userStatusURL + (checheStatusURL == ""? "" : "<br/>" + checheStatusURL);
                content += "<tr class='text-center' id='tab_tr_" + model.id + "'>" +
                    "<td>"+common.getOrderIcon(model.channelIcon)+"<a href='order_detail.html?id=" + model.id + "' target='_blank'>" + model.orderNo + "</a><br />" + model.orderCreateTime + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.owner) + "</a><br />" + common.tools.checkToEmpty(model.licensePlateNo) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.area.name) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.insuranceCompany.name) + "</td>" +
                    "<td>" + (model.institution == null? "" : common.tools.checkToEmpty(model.institution.name)) + "</td>" +
                    "<td>" + common.tools.formatMoney(model.paidAmount, 2) + "</td>" +
                    "<td>" + common.tools.formatMoney(institutionPaidAmount, 2) + "</td>" +
                    "<td id='refund_status_td_" + model.id+ "'>" + refundStatus + "</td>" +
                    "<td id='operator_td_" + model.id + "'>" + common.tools.checkToEmpty(model.operatorName) + "<br />" + common.tools.checkToEmpty(model.updateTime) + "</td>" +
                    "<td><a href=\"javascript:;\" onclick=\"orderComment.popCommentList('" + model.purchaseOrderId + "', 'first');\">查看备注</a></td>" +
                    "<td id='refund_status_url_td_" + model.id+ "'>" + refundStatusURL + "</td>";
            });
            return content;
        },

        /**
         * 已退款给用户
         * @param id
         */
        refundToUser: function(id) {
            if (!common.permission.validUserPermission("or07010501")) {
                return;
            }
            popup.mould.popConfirmMould(false, "您确定已退款给用户?", popup.mould.first, "", "57%",
                function (){
                    popup.mask.hideFirstMask(false);
                    order_refund.listOrder.changeStatus(id, 1, function(data) {
                        order_refund.listOrder.refreshItem(data);
                        popup.mask.hideFirstMask(false);
                    });
                },
                function(){
                    popup.mask.hideFirstMask(false);
                }
            );
        },

        /**
         * 已退款给车车
         * @param id
         */
        refundToCheche: function(id) {
            if (!common.permission.validUserPermission("or07010501")) {
                return;
            }
            popup.mould.popConfirmMould(false, "您确定已退款给车车?", popup.mould.first, "", "57%",
                function (){
                    popup.mask.hideFirstMask(false);
                    order_refund.listOrder.changeStatus(id, 2, function(data) {
                        order_refund.listOrder.refreshItem(data);
                        popup.mask.hideFirstMask(false);
                    });
                },
                function(){
                    popup.mask.hideFirstMask(false);
                }
            );
        },

        /**
         * 修改退款状态
         * @param id
         * @param refundType
         * @param callback
         */
        changeStatus: function(id, refundType, callback) {
            var url = "/orderCenter/nationwide/refund/user/" + id;
            if(refundType == "2") {
                url = "/orderCenter/nationwide/refund/cheche/" + id;
            }
            common.ajax.getByAjax(true, "put", "json", url, {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "更新状态错误！", popup.mould.second, popup.mould.error, "", "", null);
                }
            );
        },

        /**
         * 刷新指定行数据
         * @param data
         */
        refreshItem: function(model) {
            var userStatus = "<span style='color: orange;'>待退款给用户</span>";
            var userStatusURL = "<a href=\"javascript:;\" onclick=\"order_refund.listOrder.refundToUser('" + model.id + "');\">已退款给用户</a>";
            var checheStatus = "<span style='color: orange;'>待退款给车车</span>";
            var checheStatusURL = "<a href=\"javascript:;\" onclick=\"order_refund.listOrder.refundToCheche('" + model.id + "');\">已退款给车车</a>";
            if(model.refund != null) {
                if(model.refund.userStatus != null && model.refund.userStatus) {
                    userStatus = "<span style='color: green;'>已退款给用户</span>";
                    userStatusURL = "<span style='color: darkgray;'>已退款给用户</span>";
                }
                if(model.refund.checheStatus != null && model.refund.checheStatus) {
                    checheStatus = "<span style='color: green;'>已退款给车车</span>";
                    checheStatusURL = "<span style='color: darkgray;'>已退款给车车</span>";
                }
            }
            var refundStatus = userStatus + (checheStatus == ""? "" : "<br/>" + checheStatus);
            var refundStatusURL = userStatusURL + (checheStatusURL == ""? "" : "<br/>" + checheStatusURL);
            $("#refund_status_td_" + model.id).html(refundStatus);
            $("#operator_td_" + model.id).html(common.tools.checkToEmpty(model.operatorName) + "<br />" + common.tools.checkToEmpty(model.updateTime));
            $("#refund_status_url_td_" + model.id).html(refundStatusURL);
        }
    }
}

$(function(){
    // 初始化
    order_refund.initOrder.init();

    // 查询全部订单
    order_refund.listOrder.list();

    /**
     * 区域
     */
    $("#areaSel").unbind("change").bind({
        change: function() {
            order_refund.listOrder.properties.sourceChannel = false;
            order_refund.listOrder.findResult();
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
            order_refund.listOrder.properties.sourceChannel = false;
            order_refund.listOrder.findResult();
        }
    });
    //支付宝订单
    $("#alipayBtn").bind({
        click : function(){
            order_refund.listOrder.properties.sourceChannel = true;
            order_refund.listOrder.findResult();
        }
    });
});
