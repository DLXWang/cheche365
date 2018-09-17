/**
 * Created by wangfei on 2015/6/12.
 */

$(function(){
    /* 多张订单分配时原操作人列表 */
    $("#dataTable").hide();
    redistribution.initOldOperatorList();

    $(".tabs a").bind({
        click : function(e) {
            e.preventDefault();
            if ($(this).hasClass("selected")) {
                return false;
            }

            $(this).siblings(".selected").removeClass("selected").addClass("btn-default");
            $(this).addClass("selected").removeClass("btn-default");

            var target = $(this).attr("href").replace("#", "");
            var targetObj = $("#" + target);
            targetObj.show().siblings().not(".tabs").hide();
            targetObj.find(".first").show();
            targetObj.find(".last").hide();
        }
    });

    $("#order_one_next").bind({
        click : function(){
            var orderNo = $("#orderNo").val();
            if (common.validations.isEmpty(orderNo)) {
                popup.mould.popTipsMould(false, "请填写订单号！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            redistribution.checkOrderNo(orderNo);
        }
    });

    $("#order_more_next").bind({
        click : function(){
            dt_labels.selected=[];
            $("#dataTable").show();
            var oldOperator = $("#order_more_first").find(".oldOperatorList").val();
            if (common.validations.isEmpty(oldOperator)) {
                popup.mould.popTipsMould(false, "请选择订单当前操作人！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            redistribution.param.operatorId = oldOperator;
            redistribution.checkOldOperator(oldOperator);
        }
    });

    $("#order_one_form").find(".submit").bind({
        click : function() {
            var newOperator = $("#order_one_form").find(".operatorList").val();
            if (common.validations.isEmpty(newOperator)) {
                popup.mould.popTipsMould(false, "请选择新指定人！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            redistribution.redistributeByOrder($("#orderNo").val(), newOperator);
        }
    });

    $("#order_more_form").find(".submit").bind({
        click : function() {
            var newOperator = $("#order_more_form").find(".operatorList").val();
            var distributionMethod = $('input:radio[name="distributionMethod"]:checked').val();

            if (distributionMethod == "1" && common.validations.isEmpty(newOperator)) {
                popup.mould.popTipsMould(false, "请选择新指定人！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }

            if (distributionMethod == "0" && dt_labels.selected.length == 0) {
                popup.mould.popTipsMould(false, "请选择要分配的数据！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            redistribution.redistributeByOperator($("#oldOperatorId").val(), newOperator, distributionMethod);
        }
    });

    $(".modify").bind({
        click : function() {
            $(this).parent().parent().parent().hide();
            $(this).parent().parent().parent().parent().find(".first").show();
        }
    });

    $("input:radio[name='distributionMethod']").bind({
          click : function() {
            var checkVal = $(this).val();
            if (checkVal == "1" || checkVal == "0") {
                $("#order_more_form").find(".newOperatorRow").show();
            } else {
                $("#order_more_form").find(".newOperatorRow").hide();
                $("#order_more_form").find(".operatorList").val("");
            }
        }
    });
});

var redistribution = {
    param: {
        dataTables: null,
        dataList: null,
        operatorId: 0,
        count: 0
    },
    checkSelected: function () {
        if ($(".check-box-single:checked").length > 0) {
            $('.inputradio').attr("checked", false);
            $('.inputradio').eq(0).prop("checked", true);
        } else {
            $(".check-box-all").attr("checked", false);
        }
    },
    initOldOperatorList : function() {
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/resource/internalUser/getAllCustomers", null,
            function(data) {
                if(data == null) {
                    return false;
                }

                var options = "<option value=''>请选择</option>";
                $.each(data, function(i, model) {
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });
                $("#order_more_first").find(".oldOperatorList").empty();
                $("#order_more_first").find(".oldOperatorList").append(options);
            },function(){}
        );
    },
    checkOldOperator : function(operatorId) {
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/order/findByOperator", {operatorId : operatorId},
            function(data) {
                if (data.pass == false) {
                    popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
                redistribution.initPageInfo(operatorId);
                redistribution.writeOperatorText(operatorId, data);
            },
            function() {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
        );
    },
    checkOrderNo : function(orderNo) {
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/order/findByOrder", {orderNo : orderNo},
            function(data) {
                if (data.pass == false) {
                    popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
                redistribution.writeOrderText(orderNo, data);
            },
            function() {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
        );
    },
    redistributeByOrder : function(orderNo, newOperatorId, distributionMethod) {
        $("#order_one_form").find(".submit").attr("disabled", true);
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/order/ReassignByOrder",
            {
              orderNo : orderNo,
              newOperatorId : newOperatorId,
              method : distributionMethod
            },
            function(data) {
                if (data.pass == false) {
                    popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
                popup.mould.popTipsMould(false, "分配成功", popup.mould.first, popup.mould.success, "", "57%", null);
                redistribution.writeOrderText(orderNo, data);
                $("#order_one_form").find(".submit").attr("disabled", false);
            },
            function() {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                $("#order_one_form").find(".submit").attr("disabled", false);
                return false;
            }
        );
    },
    redistributeByOperator : function(oldOperatorId, newOperatorId, distributionMethod) {
        $("#order_more_form").find(".submit").attr("disabled", true);
        common.ajax.getByAjax(true, "get", "json", "/orderCenter/order/ReassignByOperator",
            {
                oldOperatorId : oldOperatorId,
                newOperatorId : newOperatorId,
                distributionMethod : distributionMethod,
                checkedIds: dt_labels.selected.join(",")
            },
            function(data) {
                if (data.pass == false) {
                    popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
                popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.success, "", "57%", null);
                $("#order_more_first").show();
                $("#order_more_form").hide();
                $("#order_more_form").find(".submit").attr("disabled", false);
            },
            function() {
                popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%", null);
                $("#order_more_form").find(".submit").attr("disabled", false);
                return false;
            }
        );
    },
    writeOrderText : function(orderNo, data) {
        var order_one_form = $("#order_one_form");

        order_one_form.find(".orderNoText").text(orderNo);
        order_one_form.find(".operatorText").text(common.tools.checkToEmpty(data.oldOperatorName));

        var options = "<option value=''>请选择</option>";
        $.each(data.newOperatorList, function(i, model){
            options += "<option value='" + model.id + "'>" + model.name + "</option>";
        });
        order_one_form.find(".operatorList").empty();
        order_one_form.find(".operatorList").append(options);

        $("#order_one_first").hide();
        order_one_form.show();
    },
    writeOperatorText : function(operatorId, data) {
        var order_more_form = $("#order_more_form");

        order_more_form.find(".operatorText").text(common.tools.checkToEmpty(data.oldOperatorName));
        order_more_form.find("#oldOperatorId").val(data.oldOperatorId);

        var options = "<option value=''>请选择</option>";
        $.each(data.newOperatorList, function(i, model){
            options += "<option value='" + model.id + "'>" + model.name + "</option>";
        });

        order_more_form.find(".operatorList").empty();
        order_more_form.find(".operatorList").append(options);

        $("input:radio[name='distributionMethod']").eq(0).click();
        $("#order_more_first").hide();
        order_more_form.show();
    },

    initPageInfo: function (operatorId) {
        if (redistribution.param.dataTables != null) {
            redistribution.param.dataTables.ajax.reload();
            return false;
        }
        redistribution.param.dataList = {
            url: "/orderCenter/order/redistributionList",
            type: "get",
            table_id: "redistribution_table",
            "columns": [
                {
                    data: "orderOperationInfoId",
                    "title": '<input type="checkbox" onchange="redistribution.checkSelected();" class="data-checkbox check-box-all">',
                    render: function (data, type, row) {
                        if (type === 'display') {
                            return '<input type="checkbox" value="' + data + '" onchange="redistribution.checkSelected();" class="data-checkbox check-box-single">';
                        }
                        return data;
                    },
                    className: "text-center checkbox-width",
                    "orderable": false
                },
                {"data": "orderNo", "title": "订单号", 'sClass': "text-center", "orderable": false},
                {"data": "currentStatus.description", "title": "当前状态", 'sClass': "text-center", "orderable": false},
                {"data": "createTime", "title": "创建时间", 'sClass': "text-center", "orderable": false},
                {"data": "updateTime", "title": "修改时间", 'sClass': "text-center", "orderable": false},
            ]
        };

        redistribution.param.dataTables = datatableUtil.getByDatatables(redistribution.param.dataList, function (data) {
            data.operatorId = redistribution.param.operatorId;
        }, function (nRow, aData) {
            $(".check-box-all").prop("checked", false);
            if ($.inArray(aData.orderOperationInfoId + "", dt_labels.selected) != -1) {
                $(nRow).find(".check-box-single").attr("checked", true);
            }
        });
    },

}

$(document).ready(function() {
    $('.oldOperatorList').select2();
});

