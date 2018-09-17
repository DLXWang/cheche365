var order_total = {
    initOrder: {
        /**
         * 初始化下拉列表
         */
        init: function(){
            order_total.initOrder.initArea();
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
            order_total.listOrder.properties.keyword = $("#keyword").val();
            order_total.listOrder.properties.keyType = $("#keyType").val();
            order_total.listOrder.properties.currentPage = 1;
            order_total.listOrder.list();
        },

        /**
         * 合作订单列表
         */
        list : function() {
            //if (!common.permission.validUserPermission("op0102")) {
            //    return;
            //}
            var reqParams = {
                currentPage:        order_total.listOrder.properties.currentPage,
                pageSize:           order_total.listOrder.properties.pageSize,
                sourceChannel:      order_total.listOrder.properties.sourceChannel,
                areaId:             $("#areaSel").val(),
                paymentStatusId:    $("#statusSel").val()
            };
            switch (order_total.listOrder.properties.keyType) {
                case "1":
                    reqParams.orderNo = order_total.listOrder.properties.keyword;
                    break;
                case "2":
                    reqParams.owner = order_total.listOrder.properties.keyword;
                    break;
                case "3":
                    reqParams.licensePlateNo = order_total.listOrder.properties.keyword;
                    break;
            }
            //需要同步，这样页码才能获取到数据
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/nationwide/total", reqParams,
                function(data) {
                    $("#total_order_tab tbody").empty();
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.validations.isEmpty(order_total.listOrder.properties.keyword)) {
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
                                visiblePages: order_total.listOrder.properties.visiblePages,
                                currentPage: order_total.listOrder.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        order_total.listOrder.properties.currentPage = pageNum;
                                        order_total.listOrder.list();
                                    }
                                }
                            }
                        );
                        $(".customer-pagination").show();
                    } else {
                        $(".customer-pagination").hide();
                    }

                    // 显示列表数据
                    $("#total_order_tab tbody").append(order_total.listOrder.write(data));
                    common.tools.scrollToTop();
                }, function () {
                    popup.mould.popTipsMould(false, "获取全部订单列表失败！", popup.mould.first, popup.mould.error, "", "57%", null);
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
                    "<td>" + common.tools.checkToEmpty(model.paidAmount) + "</td>" +
                    "<td>" + (model.institution == null? "" : common.tools.checkToEmpty(model.institution.name)) + "</td>";
                content += "<td id='cooperation_status_td_" + model.id + "'>"+(model.cooperationStatus == null ? "" : common.tools.checkToEmpty(model.cooperationStatus.status))+"</span></td>";
                if(model.paymentStatus == '未支付') {
                    content += "<td id='payment_status_td_" + model.id + "' style='color: orange;'>"+common.tools.checkToEmpty(model.paymentStatus)+"</span></td>";
                } else if(model.paymentStatus == '放弃支付') {
                    content += "<td id='payment_status_td_" + model.id + "' style='color: red;'>"+common.tools.checkToEmpty(model.paymentStatus)+"</span></td>";
                } else {
                    content += "<td id='payment_status_td_" + model.id + "'>"+common.tools.checkToEmpty(model.paymentStatus)+"</span></td>";
                }
                content += "<td id='operator_td_" + model.id + "'>" + common.tools.checkToEmpty(model.operatorName) + "<br />" + common.tools.checkToEmpty(model.updateTime) + "</td>" +
                    "<td><a href=\"javascript:;\" onclick=\"orderComment.popCommentList('" + model.purchaseOrderId + "', 'first');\">查看备注</a></td>";
                if(model.paymentStatus == '未支付') {
                    content += "<td id='operate_td_" + model.id + "'><a style='color:red;' href=\"javascript:;\" onclick=\"order_total.listOrder.giveUpPay('" + model.id + "');\">放弃支付</a></td>";
                } else {
                    content += "<td id='operate_td_" + model.id + "'></td>";
                }
                content += "</tr>";
            });
            return content;
        },

        /**
         * 放弃支付
         */
        giveUpPay: function(id) {
            if (!common.permission.validUserPermission("or07010201")) {
                return;
            }
            popup.mould.popConfirmMould(false, "该订单确认放弃支付吗?", popup.mould.first, "", "57%",
                function (){
                    popup.mask.hideFirstMask(false);
                    common.ajax.getByAjax(true, "put", "json", "/orderCenter/nationwide/total/giveUpPay/" + id, null,
                        function(data) {
                            if(data.paymentStatus == '未支付') {
                                $("#payment_status_td_"+id).css({'color':'orange'});
                            } else if(data.paymentStatus == '放弃支付') {
                                $("#payment_status_td_"+id).css({'color':'red'});
                            }
                            $("#payment_status_td_"+id).html(common.tools.checkToEmpty(data.paymentStatus));
                            $("#cooperation_status_td_"+id).html(data.cooperationStatus == null ? "" : common.tools.checkToEmpty(data.cooperationStatus.status));
                            $("#operator_td_"+id).html(common.tools.checkToEmpty(data.operatorName) + "<br />" + common.tools.checkToEmpty(data.updateTime));
                            if(data.paymentStatus == '未支付') {
                                $("#operate_td_"+id).html("<a href=\"javascript:;\" onclick=\"order_total.listOrder.giveUpPay('" + data.id + "');\"><span style='color:red;'>放弃支付</span></a>");
                            } else {
                                $("#operate_td_"+id).html("");
                            }
                        },
                        function() {
                            popup.mould.popTipsMould(false, "操作失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                        }
                    );
                },
                function(){
                    popup.mask.hideFirstMask(false);
                });
        }
    }
}

$(function(){
    // 初始化
    order_total.initOrder.init();

    // 查询全部订单
    order_total.listOrder.list();

    /**
     * 区域
     */
    $("#areaSel").unbind("change").bind({
        change: function() {
            order_total.listOrder.properties.sourceChannel = false;
            order_total.listOrder.findResult();
        }
    });

    /**
     * 订单合作状态
     */
    $("#statusSel").unbind("change").bind({
        change: function() {
            order_total.listOrder.properties.sourceChannel = false;
            order_total.listOrder.findResult();
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
            order_total.listOrder.properties.sourceChannel = false;
            order_total.listOrder.findResult();
        }
    });
    //支付宝订单
    $("#alipayBtn").bind({
        click : function(){
            order_total.listOrder.properties.sourceChannel = true;
            order_total.listOrder.findResult();
        }
    });
});
