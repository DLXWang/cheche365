/**
 * Created by liyuanhui on 2015/8/11.
 */
$(function () {
    var order = new Order();
    order.mark = "offline";
    offline_unpaid.listOrder(order);

    $("#pageUp").bind({
        click: function () {
            order.currentPage--;
            offline_unpaid.listOrder(order);
        }
    });

    $("#pageDown").bind({
        click: function () {
            order.currentPage++;
            offline_unpaid.listOrder(order);
        }
    });

    $("#searchBtn").bind({
        click: function () {
            if ($.trim($("#keyword").val()) == "") {
                common.showTips("请输入搜索内容");
                return false;
            }
            order.keyword = $("#keyword").val();
            order.currentPage = 1;
            offline_unpaid.listOrder(order);
        }
    });
    //全选、全不选
    $("#currentPageAllOrders").bind({
         click : function(){
             var allOrdersChecked = $(this).is(":checked");
             $("input[name='selectedId']").each(function () {
                 $(this).prop("checked",allOrdersChecked);
             })
          }
     });

    $("#oneKeyProcess").bind({
        click: function () {
            var orderIds = "";
            $("input[name='selectedId']:checked").each(function () {
                orderIds += $.trim(this.value) + ",";
            });

            common.getByAjax(true, "put", "json", "/orderCenter/order/payment", {orderIds: orderIds},
                function (data) {
                    if (data) {
                        if (data.result == 'success') {
                            common.showTips("一键更新订单状态成功");
                        } else {
                            common.showTips("一键更新订单状态失败");
                        }
                        offline_unpaid.listOrder(order);
                    }
                },
                function () {
                    common.showTips("操作失败");
                }
            )
        }
    })
});
var offline_unpaid = {
    listOrder: function (order) {
        common.getByAjax(true, "get", "json", "/orderCenter/order/unpaid/find",
            {
                keyword: order.keyword,
                currentPage: order.currentPage,
                pageSize: order.pageSize
            },
            function (data) {
                $("#offline_tab tbody").empty();
                if (data == null) {
                    common.showTips("未检索到线下未付款的订单");
                    return false;
                }
                $("#totalCount").text(data.pageInfo.totalElements);
                if (data.pageInfo.totalPage > 1) {
                    $("#page_up_down").show();
                }
                if (order.currentPage < 2) {
                    $("#page_up_down").find("#pageUp").hide();
                } else {
                    $("#page_up_down").find("#pageUp").show();
                }
                if (order.currentPage >= data.pageInfo.totalPage) {
                    $("#page_up_down").find("#pageDown").hide();
                } else {
                    $("#page_up_down").find("#pageDown").show();
                }
                if (data.pageInfo.totalElements <= order.pageSize) {
                    $("#page_up_down").find("#pageDown").hide();
                    $("#page_up_down").find("#pageDown").hide();
                }
                $("#offline_tab tbody").append(order.read(data));
            }, function () {
                common.showTips("获取线下未付款订单列表失败！");
            }
        );
    }
}

Order.prototype = {
    read: function readData(data) {
        var content = "";
        $.each(data.viewList, function (i, model) {
            content += "<tr class='text-center' id='tab_tr" + model.orderOperationId + "'>" +
            "<td><input type='checkbox' name='selectedId' value=" + model.purchaseOrderId + "></td>" +
            "<td><a href='order_detail.html?id=" + model.purchaseOrderId + "' target='_blank'>" + model.orderNo + "</a></td>" +
            "<td>" + common.checkToEmpty(model.owner) + "</td>" +
            "<td>" + common.checkToEmpty(model.licenseNo) + "</td>" +
            "<td>" + common.checkToEmpty(model.insuranceCompany) + "</td>" +
            "<td>" + common.checkToEmpty(model.sumPremium) + "</td>" +
            "<td>" + common.checkToEmpty(model.createTime) + "</td>" +
            "<td>" + common.checkToEmpty(model.assignerName) + "</td>" +
            "<td>" + common.checkToEmpty(model.operatorName) + "</td>" +
            "<td>" + common.checkToEmpty(model.updateTime) + "</td>" +
            "<td>" + common.checkToEmpty(model.payStatus) + "</td>" +
            "<td>" + common.checkToEmpty(model.currentStatus) + "</td>";
            "</tr>";
        });
        return content;
    }
}
