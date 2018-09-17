/**
 * Created by taguangyao on 2015/11/20.
 */
var conditionsMap = new Map;

$(function(){
    /* init */
    orderSelect.initCompanies();
    orderSelect.initCustomers();
    orderSelect.initOrderCooperationStatus();
    orderSelect.initOrderStatus();
    orderSelect.initVipChannels();
    orderSelect.initChannels();
    orderSelect.initQuoteAreas();
    orderSelect.initCPSChannel();
    orderSelect.initPaymentChannels();
    orderSelect.initInstitution();

    /* 查询 */
    $("#selectBtn").bind({
       click : function(){
           orderSelect.properties.currentPage = 1;
           orderSelect.select();
       }
    });

    /* 导出查询结果 */
    $("#exportLink").bind({
        click : function() {
            orderSelect.exportResult();
        }
    })

    /* 按渠道查找 */
    $("#allChannelSel").bind({
        change : function() {
            orderSelect.changeChannel($(this));
        }
    })

    $("#agent").bind({
        keyup:function(){
            if(common.isEmpty($(this).val())){
                CUI.select.hide();
                $("#agentSel").val("");
                return;
            }
            common.getByAjax(true,"get","json","/orderCenter/resource/agent/getEnableAgentsByKeyword",
                {
                    keyword:$(this).val()
                },
                function(data){
                    if(data == null){
                        return;
                    }
                    var map=new Map();
                    $.each(data, function(i,model){
                        map.put(model.id,model.agentName);
                    });
                    CUI.select.show($("#agent"),300,map,true,$("#agentSel"));
                },function(){}
            );
        }
    })

});

var orderSelect = {
    properties : new Properties(1, ""),
    /* 获取保险公司列表 */
    initCompanies : function() {
        common.getByAjax(true,"get","json","/orderCenter/resource/insuranceCompany/getQuotableCompanies",null,
            function(data){
                if(data == null){
                    return false;
                }
                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });
                $("#insuranceCompanySel").append(options);
            },function(){}
        );
    },
    /* 获取客服列表 */
    initCustomers : function() {
        common.getByAjax(true,"get","json","/orderCenter/resource/internalUser/getAllEnableCustomers",null,
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });
                $("#assignerSel").append(options);
              },function(){}
        );
    },
    /* 获取出单状态列表 */
    initOrderCooperationStatus : function() {
        common.getByAjax(true,"get","json","/orderCenter/resource/orderTransmissionStatus/getAllCooperationStatus",null,
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.status + "</option>";
                });

                $("#statusSel").append(options);
            },function(){}
        );
    },
    /* 获取订单状态列表 */
    initOrderStatus : function() {
        common.getByAjax(true,"get","json","/orderCenter/resource/orderStatus",null,
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.status + "</option>";
                });

                $("#orderStatusSel").append(options);
            },function(){}
        );
    },
    /* 获取大客户列表 */
    initVipChannels : function() {
        common.getByAjax(true,"get","json","/orderCenter/resource/vipCompany/getAllVipCompanies",null,
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });

                $("#vipCompanySel").append(options);
            },function(){}
        );
    },
    /* 获取来源列表 */
    initChannels : function() {
        common.getByAjax(true,"get","json","/orderCenter/resource/channel/getAllChannels",null,
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.description + "</option>";
                });

                $("#channelSel").append(options);
            },function(){}
        );
    },
    /* 获取报价区域列表 */
    initQuoteAreas : function() {
        common.getByAjax(true,"get","json","/orderCenter/nationwide/areaContactInfo/area",null,
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });

                $("#quoteAreaSel").append(options);
            },function(){}
        );
    },
    /* 获取CPS渠道列表 */
    initCPSChannel : function() {
        common.getByAjax(true,"get","json","/orderCenter/resource/cps",null,
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });

                $("#cpsChannelSel").append(options);
            },function(){}
        );
    },
    /* 支付方式列表 */
    initPaymentChannels: function() {
        common.getByAjax(true,"get","json","/orderCenter/resource/paymentChannels/online",null,
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.fullDescription + "</option>";
                });

                $("#paymentChannelSel").append(options);
            },function(){}
        );
    },
    /* 出单机构列表 */
    initInstitution: function() {
        common.getByAjax(true,"get","json","/orderCenter/nationwide/institution/enable",null,
            function(data){
                if(data == null){
                    return false;
                }
                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });

                $("#institutionSel").append(options);
            },function(){}
        );
    },
    check: function(){
        var flag = true,msg="";

        var orderStartDate = $("#orderStartDate").val();
        var orderEndDate = $("#orderEndDate").val();
        if ((orderStartDate && !orderEndDate) || (!orderStartDate && orderEndDate)) {
            flag = false;
            msg = "请将下单日期填写完整";
        }

        if (common.tools.dateTimeCompare(orderStartDate, orderEndDate) < 0) {
            flag = false;
            msg = "结束时间不能早于开始时间";
        }

        return {flag: flag, msg: msg}
    },
    select : function(){
        var checkJson = this.check();
        if (!checkJson.flag) {
            popup.mould.popTipsMould(false, checkJson.msg, popup.mould.first, popup.mould.warning, "", "57%",
                function() {
                    popup.mask.hideFirstMask(false);
                }
            );
            return;
        }
        $("#selectBtn").attr("disabled", true);
        common.getByAjax(true,"get","json","/orderCenter/nationwide/filter",
            {
                insuranceCompany : common.checkToEmpty($("#insuranceCompanySel").val()),
                assigner : common.checkToEmpty($("#assignerSel").val()),
                status : common.checkToEmpty($("#statusSel").val()),
                orderStatus : common.checkToEmpty($("#orderStatusSel").val()),
                orderStartDate: $("#orderStartDate").val(),
                orderEndDate: $("#orderEndDate").val(),
                owner : $.trim($("#carOwner").val()),
                mobile : $.trim($("#mobile").val()),
                licenseNo : $.trim($("#licenseNo").val()),
                agent : common.checkToEmpty($("#agentSel").val()),
                vipCompany : common.checkToEmpty($("#vipCompanySel").val()),
                channel : common.checkToEmpty($("#channelSel").val()),
                quoteArea : common.checkToEmpty($("#quoteAreaSel").val()),
                cpsChannel : common.checkToEmpty($("#cpsChannelSel").val()),
                orderNo : common.checkToEmpty($("#orderNo").val()),
                paymentChannel: common.checkToEmpty($("#paymentChannelSel").val()),
                institution: common.checkToEmpty($("#institutionSel").val()),
                currentPage : orderSelect.properties.currentPage,
                pageSize : orderSelect.properties.pageSize
            },
            function(data){
                $("#selectBtn").attr("disabled", false);
                $("#result_tab tbody").empty();
                if (data.pageInfo.totalElements < 1) {
                    $("#totalCount").text("0");
                    $(".customer-pagination").hide();
                    popup.mould.popTipsMould(false, "无符合条件的结果", popup.mould.first, popup.mould.warning, "", "57%",
                        function() {
                            popup.mask.hideFirstMask(false);
                        }
                    );
                    return false;
                }
                $("#totalCount").text(data.pageInfo.totalElements);
                $("#pageUl").empty();
                if (data.pageInfo.totalPage > 1) {
                    $.jqPaginator('.pagination',
                        {
                            totalPages: data.pageInfo.totalPage,
                            visiblePages: orderSelect.properties.visiblePages,
                            currentPage: orderSelect.properties.currentPage,
                            onPageChange: function (pageNum, pageType) {
                                if (pageType=="change") {
                                    orderSelect.properties.currentPage = pageNum;
                                    orderSelect.select();
                                }
                            }
                        }
                    );
                    $(".customer-pagination").show();
                } else {
                    $(".customer-pagination").hide();
                }

                // 显示列表数据
                $("#tab_content").show();
                $(".detail-together").show();
                $("#result_tab tbody").empty();
                $("#result_tab tbody").append(orderSelect.write(data));
                orderSelect.putMapValues();
                common.tools.scrollToTop();
            },function(){
                $("#selectBtn").attr("disabled", false);
                popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "57%",
                    function() {
                        popup.mask.hideFirstMask(false);
                    }
                );
                return false;
            }
        );
    },
    write : function(data) {
        var content = "";
        $.each(data.viewList,function(i,model){
            content += "<tr class='text-center' id='tab_tr"+ i +"'>" +
                "<td>"+common.getOrderIconByData(model.channelIcon, "<a href='order_detail.html?id=" + model.id + "' target='_blank'>"+ model.orderNo + "</a>") + "</td>" +
                "<td>"+ common.checkToEmpty(model.owner) +"</td>" +
                "<td>"+ common.checkToEmpty(model.licensePlateNo) +"</td>" +
                "<td>"+ common.checkToEmpty(model.area.name) +"</td>" +
                "<td>"+ common.checkToEmpty(model.insuranceCompany.name) +"</td>" +
                "<td>"+ (model.institution == null? "" : common.checkToEmpty(model.institution.name)) +"</td>" +
                "<td>"+ common.formatMoney(model.paidAmount, 2) +"</td>" +
                "<td>"+ common.checkToEmpty(model.orderCreateTime) +"</td>" +
                "<td>"+ common.checkToEmpty(model.assignerName) +"</td>" +
                "<td id='operator_td'>"+ model.operatorName +"</td>" +
                "<td id='updateTime_td'>"+ model.updateTime +"</td>" +
                "<td id='userSource_td'>"+ common.checkToEmpty(model.source) +"</td>" +
                "<td id='payStatus_td'>"+ common.checkToEmpty(model.paymentStatus) +"</td>" +
                "<td id='currentStatus_td'>"+ (model.cooperationStatus == null? "" : model.cooperationStatus.status) +"</td>" +
                "</tr>";
        });
        return content;
    },
    exportResult : function() {
        var url = "/orderCenter/nationwide/export?";
        conditionsMap.each(function(key, value, index) {
            if (index != 0) {
                url += "&";
            }
            url +=  key + "=" + value;
        });
        $("#exportLink").attr("href", url);
    },
    changeChannel: function (obj) {
        var channel = obj.val();
        if (channel == "1") {//CPS
            $("#cpsChannelDiv").show().siblings(".channel").hide();
            $("#cpsChannelDiv").find("select").val("0");
            $("#cpsChannelDiv").siblings(".channel").find("select").val("");
            $("#cpsChannelSel option[value='']").remove();
        } else if (channel == "2") {//大客户
            $("#vipChannelDiv").show().siblings(".channel").hide();
            $("#vipChannelDiv").find("select").val("0");
            $("#vipChannelDiv").siblings(".channel").find("select").val("");
            $("#vipCompanySel option[value='']").remove();
        } else if (channel == "3") {//好车主
            $("#perfectDriverDiv").show().siblings(".channel").hide();
            $("#perfectDriverDiv").find("select").val("0");
            $("#perfectDriverDiv").siblings(".channel").find("select").val("");
            $("#perfectDriverSel option[value='']").remove();
        } else {//所有
            $(".channel").hide();
            $(".channel").find("select").val("");
        }
    },
    putMapValues : function() {
        conditionsMap.clear();
        conditionsMap.put("institution", common.checkToEmpty($("#institutionSel").val()));
        conditionsMap.put("insuranceCompany", common.checkToEmpty($("#insuranceCompanySel").val()));
        conditionsMap.put("assigner", common.checkToEmpty($("#assignerSel").val()));
        conditionsMap.put("status", common.checkToEmpty($("#statusSel").val()));
        conditionsMap.put("orderStatus", common.checkToEmpty($("#orderStatusSel").val()));
        conditionsMap.put("orderStartDate", $("#orderStartDate").val());
        conditionsMap.put("orderEndDate", $("#orderEndDate").val());
        conditionsMap.put("owner", $.trim($("#carOwner").val()));
        conditionsMap.put("mobile", $.trim($("#mobile").val()));
        conditionsMap.put("licenseNo", $.trim($("#licenseNo").val()));
        conditionsMap.put("agent", common.checkToEmpty($("#agentSel").val()));
        conditionsMap.put("vipCompany", common.checkToEmpty($("#vipCompanySel").val()));
        conditionsMap.put("cpsChannel", common.checkToEmpty($("#cpsChannelSel").val()));
        conditionsMap.put("channel", common.checkToEmpty($("#channelSel").val()));
        conditionsMap.put("quoteArea", common.checkToEmpty($("#quoteAreaSel").val()));
        conditionsMap.put("orderNo", common.checkToEmpty($("#orderNo").val()));
        conditionsMap.put("paymentChannel", common.checkToEmpty($("#paymentChannelSel").val()));
        conditionsMap.put("currentPage", orderSelect.properties.currentPage);
        conditionsMap.put("pageSize", orderSelect.properties.pageSize);
    }
}
