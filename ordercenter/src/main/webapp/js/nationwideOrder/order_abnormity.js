var order_abnormity = {
    params: {
        refundContent: ""
    },
    initOrder: {
        /**
         * 初始化下拉列表
         */
        init: function(){
            order_abnormity.initOrder.initArea();
            order_abnormity.initOrder.initReason();
            order_abnormity.initOrder.initParams();
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
        },

        /**
         * 获取异常原因
         */
        initReason: function(){
            common.ajax.getByAjax(true,"get","json","/orderCenter/nationwide/abnormity/reasons",null,
                function(data){
                    if(data == null){
                        return false;
                    }
                    $("#reasonSel").empty();
                    var options = "<option value='0'>全部异常原因</option>";
                    $.each(data, function(i,model){
                        options += "<option value='"+ model.index +"'>" + model.content + "</option>";
                    });
                    $("#reasonSel").append(options);
                },function(){}
            );
        },

        initParams: function() {
            var $refundContent = $("#refund_content");
            if ($refundContent.length > 0) {
                order_abnormity.params.refundContent = $refundContent.html();
                $refundContent.remove();
            }
        }
    },
    listOrder: {
        properties : new Properties(1, ""),

        findResult: function() {
            order_abnormity.listOrder.properties.keyword = $("#keyword").val();
            order_abnormity.listOrder.properties.keyType = $("#keyType").val();
            order_abnormity.listOrder.properties.currentPage = 1;
            order_abnormity.listOrder.list();
        },

        /**
         * 合作订单列表
         */
        list : function() {
            //if (!common.permission.validUserPermission("op0102")) {
            //    return;
            //}
            var reqParams = {
                currentPage:        order_abnormity.listOrder.properties.currentPage,
                pageSize:           order_abnormity.listOrder.properties.pageSize,
                sourceChannel:      order_abnormity.listOrder.properties.sourceChannel,
                areaId:             $("#areaSel").val(),
                warningReasonId:    $("#reasonSel").val()
            };
            switch (order_abnormity.listOrder.properties.keyType) {
                case "1":
                    reqParams.orderNo = order_abnormity.listOrder.properties.keyword;
                    break;
                case "2":
                    reqParams.owner = order_abnormity.listOrder.properties.keyword;
                    break;
                case "3":
                    reqParams.licensePlateNo = order_abnormity.listOrder.properties.keyword;
                    break;
            }
            //需要同步，这样页码才能获取到数据
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/abnormity", reqParams,
                function(data) {
                    $("#abnormity_order_tab tbody").empty();
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.validations.isEmpty(order_abnormity.listOrder.properties.keyword)) {
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
                                visiblePages: order_abnormity.listOrder.properties.visiblePages,
                                currentPage: order_abnormity.listOrder.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        order_abnormity.listOrder.properties.currentPage = pageNum;
                                        order_abnormity.listOrder.list();
                                    }
                                }
                            }
                        );
                        $(".customer-pagination").show();
                    } else {
                        $(".customer-pagination").hide();
                    }

                    // 显示列表数据
                    $("#abnormity_order_tab tbody").append(order_abnormity.listOrder.write(data));
                    common.tools.scrollToTop();
                }, function () {
                    popup.mould.popTipsMould(false, "获取异常订单列表失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            )
        },

        /**
         * 把数据写入页面
         */
        write: function(data){
            var content = "";
            $.each(data.viewList,function(i,model){
                content += "<tr class='text-center' id='tab_tr_" + model.id + "'>" +
                    "<td>"+common.getOrderIcon(model.channelIcon)+"<a href='order_detail.html?id=" + model.id + "' target='_blank'>" + model.orderNo + "</a><br />" + model.orderCreateTime + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.owner) + "</a><br />" + common.tools.checkToEmpty(model.licensePlateNo) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.area.name) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.insuranceCompany.name) + "</td>" +
                    "<td>" + (model.institution == null? "" : common.tools.checkToEmpty(model.institution.name)) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.reason) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(model.operatorName) + "<br />" + common.tools.checkToEmpty(model.updateTime) + "</td>" +
                    "<td><a href=\"javascript:;\" onclick=\"orderComment.popCommentList('" + model.purchaseOrderId + "', 'first');\">查看备注</a></td>" +
                    "<td><a href=\"javascript:;\" onclick=\"order_abnormity.listOrder.refund('" + model.id + "');\">退款</a>" +
                        "<a style='padding-left: 5px;' href=\"javascript:;\" onclick=\"order_abnormity.listOrder.resend('" + model.id + "');\">订单重发</a></td>";
            });
            return content;
        },

        /**
         * 退款
         */
        refund: function(id) {
            if (!common.permission.validUserPermission("or07010401")) {
                return;
            }
            popup.pop.popInput(false, order_abnormity.params.refundContent, popup.mould.first, "400px", "auto", "50%", "54%");
            var $popInput = window.parent.$("#popover_normal_input");
            $popInput.find(".theme_poptit .close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            $popInput.find("#refundBtn").unbind("click").bind({
                click: function() {
                    var refundTo = "";
                    $popInput.find("[name='refundTo']:checked").each(function(){
                        refundTo += $(this).val()+ ",";
                    });
                    refundTo = refundTo.substring(0, refundTo.length - 1);
                    common.ajax.getByAjax(true, "put", "json", "/orderCenter/nationwide/abnormity/refund/" + id, {
                            refundTo : refundTo
                        },
                        function(data) {
                            if (data.pass) {
                                popup.mask.hideFirstMask(false);
                                popup.mould.popTipsMould(false, "退款设置成功！", popup.mould.first, popup.mould.success, "", "57%",
                                    function() {
                                        popup.mask.hideFirstMask(false);
                                        order_abnormity.listOrder.list();
                                    }
                                );
                            } else {
                                popup.mould.popTipsMould(false, "退款设置失败，请重试！", popup.mould.first, popup.mould.error, "", "", null);
                            }
                        },
                        function() {
                            popup.mould.popTipsMould(false, "退款设置失败，请重试！", popup.mould.second, popup.mould.error, "", "", null);
                        }
                    );
                }
            });
        },

        /**
         * 订单重发
         */
        resend: function(id) {
            if (!common.permission.validUserPermission("or07010401")) {
                return;
            }
            popup.mould.popConfirmMould(false, "该订单确认重发吗?", popup.mould.first, "", "57%",
                function (){
                    popup.mask.hideFirstMask(false);
                    common.ajax.getByAjax(true, "put", "json", "/orderCenter/nationwide/abnormity/resend/" + id, {},
                        function(data) {
                            if (data.pass) {
                                popup.mask.hideAllMask();
                                popup.mould.popTipsMould(false, "订单重发成功！", popup.mould.first, popup.mould.success, "", "57%",
                                    function() {
                                        popup.mask.hideFirstMask(false);
                                        order_abnormity.listOrder.list();
                                    }
                                );
                            } else {
                                popup.mould.popTipsMould(false, "订单重发失败，请重试！", popup.mould.first, popup.mould.error, "", "57%", null);
                            }
                        },
                        function() {
                            popup.mould.popTipsMould(false, "订单重发失败，请重试！", popup.mould.first, popup.mould.error, "", "57%", null);
                        }
                    );
                },
                function(){
                    popup.mask.hideFirstMask(false);
                }
            );
        }
    }
}

$(function(){
    // 初始化
    order_abnormity.initOrder.init();

    // 查询全部订单
    order_abnormity.listOrder.list();

    /**
     * 区域
     */
    $("#areaSel").unbind("change").bind({
        change: function() {
            order_abnormity.listOrder.properties.sourceChannel = false;
            order_abnormity.listOrder.findResult();
        }
    });

    /**
     * 异常原因
     */
    $("#reasonSel").unbind("change").bind({
        change: function() {
            order_abnormity.listOrder.properties.sourceChannel = false;
            order_abnormity.listOrder.findResult();
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
            order_abnormity.listOrder.properties.sourceChannel = false;
            order_abnormity.listOrder.findResult();
        }
    });
    //支付宝订单
    $("#alipayBtn").bind({
        click : function(){
            order_abnormity.listOrder.properties.sourceChannel = true;
            order_abnormity.listOrder.findResult();
        }
    });
});
