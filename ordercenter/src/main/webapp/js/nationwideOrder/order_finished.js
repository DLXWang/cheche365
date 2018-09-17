var order_finished = {
    page: new Properties(1, ""),
    listInfo: {
        findResult: function() {
            order_finished.page.currentPage = 1;
            order_finished.page.keyType = $("#keyType").val();
            order_finished.page.keyword = $("#keyword").val();
            order_finished.listInfo.list();
        },
        list: function () {
            var reqParams = {
                currentPage:        order_finished.page.currentPage,
                pageSize:           order_finished.page.pageSize,
                sourceChannel:      order_finished.page.sourceChannel,
                areaId:             $("#select_area").val(),
                auditStatus:        $("#select_audit").val()
            };
            switch (order_finished.page.keyType) {
                case "1":
                    reqParams.orderNo = order_finished.page.keyword;
                    break;
                case "2":
                    reqParams.owner = order_finished.page.keyword;
                    break;
                case "3":
                    reqParams.licensePlateNo = order_finished.page.keyword;
                    break;
                case "4":
                    reqParams.institutionName = order_finished.page.keyword;
                    break;
                case "5":
                    reqParams.policyNo = order_finished.page.keyword;
                    break;
                case "6":
                    reqParams.trackingNo = order_finished.page.keyword;
                    break;
            }
            common.getByAjax(true, "get", "json", "/orderCenter/nationwide/finished", reqParams,
                function (data) {
                    $("#list_tab tbody").empty();
                    if (data == null) {
                        popup.mould.popTipsMould(false, "获取分站信息列表失败！", popup.mould.first, popup.mould.warning, "", "57%", null);
                        return false;
                    }
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.isEmpty(order_finished.page.keyword)) {
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
                                visiblePages: order_finished.page.visiblePages,
                                currentPage: order_finished.page.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType == "change") {
                                        order_finished.page.currentPage = pageNum;
                                        order_finished.listInfo.list();
                                    }
                                }
                            }
                        );
                    } else {
                        $(".customer-pagination").hide();
                    }
                    order_finished.listInfo.fillTabContent(data);
                    window.parent.scrollTo(0, 0);
                }, function () {
                    popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        getAuditState: function(visited) {
            if (visited==null) {
                return "<span style=\"color:#FFA81F;\">待审核</span>";
            } else if(visited) {
                return "<span style=\"color: green;\">审核通过</span>";
            }else{
                return "<span style=\"color: red;\">审核不通过</span>";
            }
        },
        fillTabContent: function (data) {
            if (data.viewList) {
                var content = "";
                $.each(data.viewList, function (n, model) {
                    content += "<tr class='text-center' id='tab_tr" + model.id + "'>" +
                        "<td>"+common.getOrderIcon(model.channelIcon)+"<a href='order_detail.html?id=" + model.id + "' target='_blank'>" + model.orderNo + "</a><br>"+model.createTime+"</td>" +
                        "<td>" + model.owner + "<br>" + common.checkToEmpty(model.licensePlateNo) + "</td>" +
                        "<td>" + common.checkToEmpty(model.area.name) + "</td>" +
                        "<td>" + common.checkToEmpty(model.insuranceCompany.name) + "</td>" +
                        "<td>" + common.checkToEmpty(model.institution ? model.institution.name : "") + "</td>" +
                        "<td>交强险:" + common.checkToEmpty(model.quoteRecord ? model.quoteRecord.compulsoryPolicyNo : "") + "<br>商业险:"+common.checkToEmpty(model.quoteRecord ? model.quoteRecord.commercialPolicyNo : "")+"</td>" +
                        "<td>" + common.checkToEmpty(model.deliveryInfo.expressCompany) + "<br>"+common.checkToEmpty(model.deliveryInfo.trackingNo)+"</td>" +
                        "<td>" + model.operatorName + "<br>"+ common.checkToEmpty(model.updateTime)+"</td>" +
                        "<td>" + order_finished.listInfo.getAuditState(model.auditStatus) + "</td>" +
                        "<td id='remark_td'><a href=\"javascript:;\" onclick=\"orderComment.popCommentList(" + model.purchaseOrderId + ", 'first');\">查看备注</a></td>"
                    content += "<td>";
                    if(model.rebateStatus){
                        content += "<a href='javascript:;' style='color:green;' onclick='order_finished.auditStatus(" + model.id + ",1);'>审核通过</a>";
                        content += "&nbsp;&nbsp;<a href='javascript:;' style='color:red;' onclick='order_finished.auditStatus(" + model.id + ",0);'>审核不通过</a>";
                    }else if(model.rebateStatus==null||!model.rebateStatus){
                        content += "<a href='javascript:;' style='color:blue;' onclick='order_finished.rebateStatus(" + model.id + ");'>确认佣金到帐</a>";
                    }
                    content += "</td></tr>";
                });
                $("#list_tab tbody").append(content);
            }
        }
    },
    rebateStatus:function(id){
        if (!common.permission.validUserPermission("or07011001")) {
            return;
        }
        popup.mould.popConfirmMould(false, "确定佣金已到帐?", "first", "", "55%",
            function() {
                popup.mask.hideFirstMask(false);
                common.getByAjax(true, "put", "json", "/orderCenter/nationwide/finished/"+id+"/rebate", {},
                    function (data) {
                        if (data.pass) {
                            order_finished.listInfo.currentPage = 1;
                            order_finished.listInfo.keyword = $("#keyword").val();
                            order_finished.listInfo.list();
                        }
                    },
                    function () {
                        popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                    }
                );
            },
            function() {
                popup.mask.hideFirstMask(false);
                return false;
            }
        );
    },
     auditStatus:function(id,auditStatus){
         if (!common.permission.validUserPermission("or07011002")) {
             return;
         }
         common.getByAjax(true, "put", "json", "/orderCenter/nationwide/finished/"+id+"/audit", {auditStatus:auditStatus},
             function (data) {
                 if (data.pass) {
                     order_finished.listInfo.currentPage = 1;
                     order_finished.listInfo.keyword = $("#keyword").val();
                     order_finished.listInfo.list();
                 }
             },
             function () {
                 popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
             }
         );
     }
    ,
    initArea: function () {
        common.getByAjax(true, "get", "json", "/orderCenter/nationwide/areaContactInfo/area", {},
            function (data) {
                if (data) {
                    var options = "<option value='0'>全部区域</option>";
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

$(function(){
    order_finished.listInfo.list();
    order_finished.initArea();
    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            order_finished.page.sourceChannel = false;
            order_finished.listInfo.findResult();
        }
    });
    $("#select_area").bind({
        change: function () {
            order_finished.page.sourceChannel = false;
            order_finished.listInfo.findResult();
        }
    });

    $("#select_audit").bind({
        change: function () {
            order_finished.page.sourceChannel = false;
            order_finished.listInfo.findResult();
        }
    });
    //支付宝订单
    $("#alipayBtn").bind({
        click : function(){
            order_finished.page.sourceChannel = true;
            order_finished.listInfo.findResult();
        }
    });
});
