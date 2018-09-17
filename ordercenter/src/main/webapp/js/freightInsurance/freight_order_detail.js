var freight_order_detail = {
    orderId: '',
    initPreClaimTable: function (thisLink) {
        if ($(".inited").val() === 'false') {
            freight_order_detail.interface.IgetPreClaimList(freight_order_detail.orderId);
            $(".inited").prop('value', "true");
        }
        $(".preClaimTable").toggleClass("hidden");
        $(thisLink).find('.glyphicon').toggleClass("glyphicon-chevron-right").toggleClass("glyphicon-chevron-down");
    },
    interface: {
        IgetOrderDetail: function (orderId) {
            common.ajax.getByAjax(true, 'get', 'json', '/orderCenter/freightInsurance/order/' + orderId, {}, function (result) {
                if (result.code == "200") {
                    //订单信息
                    var data = result.data;
                    for (var i in data) {
                        $("#" + i).html(data[i]);
                    }

                    //商品信息
                    var products = data.products;
                    var productContent = "";
                    products.forEach(function (model, index, arr) {
                        var no = index + 1;
                        productContent +=
                            '<tr class="active text-center">' +
                            '<td>' + no + '</td>' +
                            '<td>' + model.productName + '</td>' +
                            '<td>' + model.categoryName + '</td>' +
                            '<td>' + model.unitPrice + '</td>' +
                            '<td>' + model.quantity + '</td>' +
                            '<td>' + model.sumPrice + '</td>' +
                            '<tr>';
                    });
                    $("#productTBody").append(productContent);

                    //理赔信息
                    var claims = data.claims;
                    if (claims) {
                        $(".claim_info_div").show();
                        for (var i in claims) {
                            $("#" + i).html(claims[i]);
                        }
                    }
                } else {
                    popup.mould.popTipsMould(false, "获取订单详情异常！", popup.mould.first, popup.mould.error, "", "", null);
                }
            }, function () {
                popup.mould.popTipsMould(false, "获取订单详情异常！", popup.mould.first, popup.mould.error, "", "", null);
            })
        },
        IgetPreClaimList: function (orderId) {
            common.ajax.getByAjax(true, 'get', 'json', '/orderCenter/freightInsurance/preCliam/' + orderId, {}, function (result) {
                var context = '';
                result.forEach(function (model, index, arr) {
                    var no = index + 1;
                    context += '' +
                        '<tr class="active text-center">' +
                        '	<td>' + no + '</td>' +
                        '	<td>' + common.checkToEmpty(model.trackingNo) + '</td>' +
                        '	<td>' + common.checkToEmpty(model.expressCompanyName) + '</td>' +
                        '	<td>' + common.checkToEmpty(model.deliveryTime) + '</td>' +
                        '	<td>' + common.checkToEmpty(model.status) + '</td>' +
                        '	<td>' + common.checkToEmpty(model.rescissionTime) + '</td>' +
                        '</tr>';

                });
                $(".preClaimTbody").append(context);
            }, function () {

            })
        }
    }
}

$(function () {
    freight_order_detail.orderId = common.getUrlParam('orderId');
    freight_order_detail.interface.IgetOrderDetail(freight_order_detail.orderId);

});
