/**
 * Created by zhangshitao on 2015/10/31.
 */

var appointmentinsurance = {
    config_validation: {
        onkeyup: false,
        onfocusout: false,
        rules: {
            name: {
                required: true,
                maxlength: 5
            },
            autoNo: {
                required: true
            },
            endDate: {
                required: true
            }
        },
        messages: {
            name: {
                required: "请输入用户姓名",
                maxlength: "地推用户姓名最多可输入5位"
            },
            autoNo: {
                required: "请输入车牌号"
            },
            endDate: {
                required: "请输入车险到期日"
            }
        },
        showErrors: function(errorMap, errorList) {
            if (errorList.length) {
                var errorText = $("#errorText");
                errorText.text(errorList[0].message);
                errorText.parent().parent().show();
            }
        },
        submitHandler: function(form) {
            var errorText = parent.$("#errorText");
            errorText.parent().parent().hide();
            /*if ($("#partnerId").val() == "0") {
                //appointmentinsurance.newAppointmentInsurance.saveAppointmentInsurance(form);
            } else {*/
            appointmentinsurance.editAppointmentInsurance.update(form);
           /* }*/
        }
    },
    initAppointmentInsurance: {
        initPopupContent: function() {
            var popupContent = $("#new_content");
            if (popupContent.length > 0) {
                appointmentinsurance.newAppointmentInsurance.content = popupContent.html();
                popupContent.remove();
            }
        }
    },
    listAppointmentInsurance: {
        properties : new Properties(1, ""),
        list: function() {
        var date = $('#startTime').val();
            common.getByAjax(true, "post", "json", "/operationcenter/appointmentinsurances",
                {
                    currentPage : appointmentinsurance.listAppointmentInsurance.properties.currentPage,
                    pageSize    : appointmentinsurance.listAppointmentInsurance.properties.pageSize,
                    keyword     : appointmentinsurance.listAppointmentInsurance.properties.keyword,
                    keyType     : $("#searchSel").val(),
                    datetime    :date
                },
                function(data) {
                    $("#list_tab tbody").empty();

                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.isEmpty(appointmentinsurance.listAppointmentInsurance.properties.keyword)) {
                            popup.mould.popTipsMould("无符合条件的结果", popup.mould.first, "", "", "",
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
                                visiblePages: appointmentinsurance.listAppointmentInsurance.properties.visiblePages,
                                currentPage: appointmentinsurance.listAppointmentInsurance.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        appointmentinsurance.listAppointmentInsurance.properties.currentPage = pageNum;
                                        appointmentinsurance.listAppointmentInsurance.list(appointmentinsurance.listAppointmentInsurance.properties);
                                        window.scrollTo(0,0);
                                    }
                                }
                            }
                        );
                        $(".customer-pagination").show();
                    } else {
                        $(".customer-pagination").hide();
                    }
                    appointmentinsurance.listAppointmentInsurance.fillTabContent(data);
                    common.scrollToTop();
                },
                function() {
                    popup.mould.popTipsMould("获取地推用户列表失败！", popup.mould.first, popup.mould.error, "", "56%", null);
                }
            );
        },
        fillTabContent: function(data) {
            var content = "";
            var e = 0;
            $.each(data.viewList,function(i,model){
                i++;
                var channel;
                if(!(model.qrCodeChannelCode == "")){
                    channel = "<a href='javascript:;' onclick=\"getDetail('" + common.checkToEmpty(model.qrCodeChannelId) +"','"+common.checkToEmpty(model.actionName)+ "')\">&lt;" + common.checkToEmpty(model.qrCodeChannelCode) +"&gt;<br/>" + common.checkToEmpty(model.qrCodeChannelName) + "</a>";
                }else{
                    channel = "";
                }
                content += "<tr class=\"text-center\">" +
                "<td style='max-width: 50px;' class='text-center'>" + common.checkToEmpty(i) + "</td>" +
                "<td style='max-width: 220px;' class='text-center'>" + common.checkToEmpty(model.user) + "</td>" +
                "<td style='max-width: 220px;' class='text-center'>" + common.checkToEmpty(model.name) + "</td>" +
                "<td style='max-width: 220px;' class='text-center'>" + common.checkToEmpty(model.mobile) + "</td>" +
                "<td style='max-width: 220px;' class='text-center'>" + common.checkToEmpty(model.licensePlateNo) + "</td>" +
                "<td style='max-width: 220px;' class='text-center'>" + common.checkToEmpty(model.expireBefore) + "</td>" +
                "<td style='max-width: 220px;' class='text-center'>" + common.checkToEmpty(model.createTime) + "</td>" +
                "<td style='max-width: 220px;' class='text-center'>&lt;" + common.checkToEmpty(model.count) + "单&gt;</span><br/><span>" + common.checkToEmpty(model.totalMoney) + "元</span></td>" +
                "<td class='text-center' style='max-width: 220px;'>" + channel + "</td>" +
                "<td class='classStatus"+model.id+"' id='appointmentinsurance_status_id_"+model.id+"' style=\"max-width:80px;color: " + (model.status == 1 ? "green" : "red") + "\">" + (model.status == 1 ? "已处理" : "待处理") + "</td>" +
                "<td style=\"max-width: 220px;\"><span title=\"" + common.checkToEmpty(model.comment) + "\">" + common.getFormatComment(model.comment, 10) + "</span></td>" +
                "<td style=\"width: 220px;\">" +
                        "<a class='"+(model.status == 1?"":"none")+" classdisable"+model.id+"'href=\"javascript:;\" onclick=\"appointmentinsurance.editAppointmentInsurance.switchStatus(" + model.id + "," + 0 + ");\" style=\"color:red\"> 待处理 </a>" +
                        "<a class='"+(model.status == 1?"none":"")+" classenable"+model.id+"'href=\"javascript:;\" onclick=\"appointmentinsurance.editAppointmentInsurance.switchStatus(" + model.id + "," + 1 + ");\" style=\"color:green\"> 已处理 </a>" +
                        "<a style=\"padding-left: 15px;\" href=\"javascript:;\" onclick=\"appointmentinsurance.editAppointmentInsurance.popEdit(" + model.id + ");\">编辑</a>" +
                "</td>" +
                "</tr>";
            });
            $("#list_tab tbody").html(content);

        }
    },
    newAppointmentInsurance: {
        content: "",
        popInput: function() {
            appointmentinsurance.initAppointmentInsurance.initPopupContent();
            popup.pop.popInput(appointmentinsurance.newAppointmentInsurance.content, popup.mould.first, "510px", "450px", "40%", "59%");
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function() {
                    popup.mask.hideFirstMask();
                }
            });
            $("#new_form").validate(appointmentinsurance.config_validation);
        }
    },
    editAppointmentInsurance: {
        popEdit: function(id) {
            common.getByAjax(true, "post", "json", "/operationcenter/appointmentinsurances/findOne",
                {
                    appointmentInsuranceId  :id
                },
                function(data) {
                    appointmentinsurance.newAppointmentInsurance.popInput();
                    $("#toCreate").val("更新");
                    $("#name").val(common.checkToEmpty(data.name));
                    $("#autoNo").val(common.checkToEmpty(data.licensePlateNo));
                    $("#endDate").val(common.checkToEmpty(data.expireBefore));
                    $("#comment").val(common.checkToEmpty(data.comment));
                    $("#appointmentInsurance").val(common.checkToEmpty(data.id));
                    $("#updateUserId").val(common.checkToEmpty(data.user));
                },
                function() {
                    popup.mould.popTipsMould("获取信息失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        update: function(form) {
            $("#toCreate").attr("disabled", true);
            common.getByAjax(true, "post", "json", "/operationcenter/appointmentinsurances/update",$(form).serialize(),
                function(data) {
                    $("#toCreate").attr("disabled", false);
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("更新成功！", popup.mould.first, popup.mould.success, "", "57%",
                            function() {
                                popup.mask.hideFirstMask();
                                appointmentinsurance.listAppointmentInsurance.list(appointmentinsurance.listAppointmentInsurance.properties);
                            }
                        );
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function() {
                    $("#toCreate").attr("disabled", false);
                    popup.mould.popTipsMould("更新地推用户信息失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },
        switchStatus: function(id, status) {
            common.getByAjax(true, "post", "json", "/operationcenter/appointmentinsurances/updatestatus",
                {
                    appointmentInsuranceId : id,
                    status                 : status
                },
                function(data) {
                        if(data.pass){
                            if(status == 0){
                                $(".classStatus"+id).css({'color':'red'}).html("待处理");
                                $(".classdisable"+id).hide();
                                $(".classenable"+id).show();
                            }else{
                                $(".classStatus"+id).css({'color':'green'}).html("已处理");
                                $(".classdisable"+id).show();
                                $(".classenable"+id).hide();
                            }
                        }
                },
                function() {
                    popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    timeChange:function(){
        var keyword = $("#keyword").val();
        appointmentinsurance.listAppointmentInsurance.properties.keyword = keyword;
        appointmentinsurance.listAppointmentInsurance.properties.keyType = $("#searchSel").val();
        appointmentinsurance.listAppointmentInsurance.properties.currentPage = 1;
        appointmentinsurance.listAppointmentInsurance.list();
    }
};

var detail_forver = {
    detail_content:'',
    initPopupContent: function() {
        var popupContent = $("#yj_detail_qrcode");
        if (popupContent.length > 0) {
            detail_forver.detail_content = popupContent.html();
            popupContent.remove();
        }
    }
};

var getDetail = function(id,actionName) {
   /* if (!common.permission.validUserPermission("op020205")) {
        return;
    }*/

    //错误：===================================================空置问题
    var parent = window.parent;
    common.getByAjax(true, "get", "json", "/operationcenter/qcchannels/forever/" + actionName + "/" + id, {},
        function(data) {
            detail_forver.initPopupContent();
            popup.pop.popInput(detail_forver.detail_content, popup.mould.first, "530px", "490px", "45%");
            $("#detail_title").text($("#detail_title").html() + " " + data.code);
            $("#detail_code").text(data.code);
            $("#detail_name").text(common.checkToEmpty(data.name));
            if (common.getLength(common.checkToEmpty(data.name)) >= 26) {
                $("#detail_name").attr("title", common.checkToEmpty(data.name));
            }
            if(null!=data.department&&data.department.length>0){
                $("#detail_department").text(data.department);
            }else{
                $("#detail_department").html("&nbsp;");
            }

            $("#detail_scanning_count").text(data.scanningCount==null?0:data.scanningCount);
            $("#detail_attention_count").text(data.attentionCount==null?0:data.attentionCount);
            $("#detail_bindingMobile_count").text(data.bindingMobileCount==null?0:data.bindingMobileCount);
            $("#detail_successOrder_count").text(data.successOrderCount==null?0:data.successOrderCount);
            $("#detail_rebate").text(data.rebate);
            $("#detail_comment").val((data.comment==null)?"":data.comment.replace(/\\r\\n/g,'\n'));
            $("#qrCodeImg").attr("src", "/operationcenter/qcchannels/img/" + actionName + "/" + id);
            $("#qrCodeImg_download").attr("onclick","downloadForeverQrcode(" + id + ");");
            $("#yj_detail_qrcode_close").unbind("click").bind({
                click : function() {
                    popup.mask.hideAllMask(false);
                }
            });
        },
        function() {
            popup.mould.popTipsMould("获取二维码详情失败", popup.mould.first, popup.mould.error, "", "",
                function() {
                    popup.mask.hideFirstMask(false);
                }
            );
        }
    );
}

$(function() {
    appointmentinsurance.listAppointmentInsurance.list();

    $("#searchBtn").bind({
            click: function () {
                var keyword = $("#keyword").val();
                if (common.isEmpty(keyword)) {
                    popup.mould.popTipsMould("请输入搜索内容", "first", "warning", "", "", null);
                    return false;
                }
                appointmentinsurance.listAppointmentInsurance.properties.keyword = keyword;
                appointmentinsurance.listAppointmentInsurance.properties.keyType = $("#searchSel").val();
                appointmentinsurance.listAppointmentInsurance.properties.currentPage = 1;
                appointmentinsurance.listAppointmentInsurance.list();
            }
    });

    $("#exportExcel").bind({
        click: function() {
            var url = "/operationcenter/appointmentinsurances/export";
            $("#exportExcel").attr("href", url);
        }
    });
});
