/**
 * Created by zhangshitao on 2015/11/02.
 */
var prize_distribution = {
    config_validation: {
        rules: {
            street: {
                required: true
            }
        },
        messages: {
            street: {
                required: "请输入街道"
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
            var errorText = $("#errorText");
            if($("#select_province").val() == 0) {
                errorText.text("请选择省份");
                errorText.parent().parent().show();
                return false;
            }
            if($("#select_city").val() == 0) {
                errorText.text("请选择城市");
                errorText.parent().parent().show();
                return false;
            }
            if($("#select_district").val() == 0) {
                errorText.text("请选择地区");
                errorText.parent().parent().show();
                return false;
            }
            errorText.parent().parent().hide();
        }
    },
    initPrizeDistribution: {
        initAddressContent: function() {
            var addressContent = $("#prize_address_div");
            if (addressContent.length > 0) {
                prize_distribution.popupPage.addressContent = addressContent.html();
                addressContent.remove();
            }
        },
        initCommentContent: function() {
            var commentContent = $("#prize_comment_div");
            if (commentContent.length > 0) {
                prize_distribution.popupPage.commentContent = commentContent.html();
                commentContent.remove();
            }
        },
        initAddress:function(parent, obj, level){
//            alert("parent=="+parent+",value=="+value+",obj=="+obj+",level="+level);
            if(level!=1&&parent<=0){
                return;
            }
            var href="";
            if(level==1){
                href="/operationcenter/resource/provinces";
            }else if(level==2){
                href= "/operationcenter/resource/"+parent+"/cities";
            }else{
                href="/operationcenter/resource/"+parent+"/districts"
            }
            common.getByAjax(true, "get", "json", href, {},
                function(data) {
                    if (data.length>0) {
                        var options = "<option value='0'>请选择</option>";
                        $.each(data, function(i, model){
                            options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                        });
                        obj.children().remove();
                        obj.append(options);
                       // obj.val(value);
                        obj.show();
                    }
                },
                function() { popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);}
            );
        }
    },
    listPrizeDistribution: {
        properties : new Properties(1, ""),
        list: function(status) {
            //if (!common.permission.validUserPermission("op0101")) {
            //    return;
            //}
            common.getByAjax(true, "get", "json", "/operationcenter/prizesend",
                {
                    currentPage : prize_distribution.listPrizeDistribution.properties.currentPage,
                    pageSize    : prize_distribution.listPrizeDistribution.properties.pageSize,
                    keyword     : prize_distribution.listPrizeDistribution.properties.keyword,
                    keyType     : $("#keyType").val(),
                    status      : status
                },
                function(data) {
                    $("#list_tab tbody").empty();

                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.isEmpty(prize_distribution.listPrizeDistribution.properties.keyword)) {
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
                                visiblePages: prize_distribution.listPrizeDistribution.properties.visiblePages,
                                currentPage: prize_distribution.listPrizeDistribution.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        prize_distribution.listPrizeDistribution.properties.currentPage = pageNum;
                                        prize_distribution.listPrizeDistribution.list(invitecode.listPrizeDistribution.properties);
                                        window.scrollTo(0,0);
                                    }
                                }
                            }
                        );
                        $(".customer-pagination").show();
                    } else {
                        $(".customer-pagination").hide();
                    }

                    prize_distribution.fillContent.fillTabContent(data);
                    common.scrollToTop();
                },
                function() {
                    popup.mould.popTipsMould("获取奖品发布列表页失败！", popup.mould.first, popup.mould.error, "", "56%", null);
                }
            );
        }
    },
    popupPage: {
        addressContent: "",//弹出的地址
        commentContent: "",//弹出的备注
        popupAddress: function(id) {
            prize_distribution.initPrizeDistribution.initAddressContent();
            popup.pop.popInput(prize_distribution.popupPage.addressContent, popup.mould.first, "620px", "300px", "36%", "59%");
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function() {
                    popup.mask.hideFirstMask();
                }
            });
            prize_distribution.fillContent.fillAddressContent(id);
            prize_distribution.initPrizeDistribution.initAddress(0,parent.$("#select_province"),1);
            parent.$("#select_province").unbind("change").bind({
                change: function () {
                    parent.$("#select_city").val(null).hide();
                    parent.$("#select_district").val(null).hide();
                    prize_distribution.initPrizeDistribution.initAddress($(this).val(),parent.$("#select_city"),2);
                }
            });
            parent.$("#select_city").unbind("change").bind({
                change: function () {
                    prize_distribution.initPrizeDistribution.initAddress($(this).val(),parent.$("#select_district"),3);
                }
            });
        },
        popupComment: function() {
            prize_distribution.initPrizeDistribution.initCommentContent();
            popup.pop.popInput(prize_distribution.popupPage.commentContent, popup.mould.first, "500px", "350px", "36%", "59%");
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function() {
                    popup.mask.hideFirstMask();
                }
            });
            window.parent.$("#toCreate").unbind("click").bind({
                click: function() {
                    prize_distribution.infoSave.commentSave();
                }
            });
        },
        popupAddAddress: function(id, userName, phoneNo) {
            $("#read_address").hide();
            $("#add_address").show();
            $("#select_province").attr("disabled", "disabled");
            $("#input_street").attr("disabled", "disabled");
            $("#add_street").attr("style", "visibility:visible");
            $("#toCancel").hide();
            $("#toClose").hide();
            $("#toSave").hide();
            $("#addUserName").attr("disabled", "disabled");
            $("#addUserName").val(userName);
            $("#addPhoneNo").attr("disabled", "disabled");
            $("#addPhoneNo").val(phoneNo);
            $("#toAdd").unbind("click").bind({
                click: function(){
                    prize_distribution.infoSave.addressSave(id);
                }
            });
        }
    },
    fillContent:{
        fillTabContent: function(data) {
            var content = "";

            $.each(data.viewList,function(i,model){
                content += "<tr class=\"text-center\">" +
                    "<td>" + model.id + "</td>" +
                    "<td>" + model.userInfo + "</td>" +
                    "<td>" + model.prizeName + "</td>" +
                    "<td>" + model.prizeTypeName + "</td>" +
                    "<td>" + model.num + "</td>" +
                    "<td>" + model.createTime + "</td>" +
                    "<td><a id='getAddress"+ model.id+"' style=\"margin-left: 10px;\" href='javascript:;' >查看收货信息详情</a></td>" +
                    "<td id='status_id_"+model.id+"'>" + (model.status==1?'<font color=\'green\'>已处理</font>':'<font color=\'red\'>未处理</font>') + "</td>" +
                    "<td><a id='getComment"+ model.id+"' style=\"margin-left: 10px;\" href='javascript:;' >查看记录详情</a></td>" +
                    "<td>" +
                    "<a class='"+(model.status==1?'':'none')+"'  id='no_status_action_id_"+model.id+"' href=\"javascript:;\" onclick=\"prize_distribution.fillContent.switchStatus(" + model.id + ",0);\" style=\"color:red\"> 待处理 </a>" +
                    "<a class='"+(model.status==0?'':'none')+"'  id='yes_status_action_id_"+model.id+"'href=\"javascript:;\" onclick=\"prize_distribution.fillContent.switchStatus(" + model.id + ",1);\" style=\"color:green\"> 已处理 </a>" +
                    "<a id='sendMsg"+ model.id+"' style=\"margin-left: 10px;\" href='javascript:;' >发送短信</a>" + "</td>" +
                    "</tr>";
            });
            $("#list_tab tbody").html(content);

            $("[id^='getComment']").unbind("click").bind({
                click: function () {
                    prize_distribution.popupPage.popupComment();
                    prize_distribution.fillContent.fillCommentContent(this.id.replace('getComment' ,''));
                }
            });

            $("[id^='getAddress']").unbind("click").bind({
                click: function () {
                    prize_distribution.popupPage.popupAddress(this.id.replace('getAddress' ,''));
                }
            });
            $("[id^='sendMsg']").unbind("click").bind({
                click: function () {
                    //短信发送功能
                    //prize_distribution.popupPage.popupAddress(this.id.replace('sendMsg' ,''));
                }
            });
        },
        fillCommentContent: function(id){
            common.getByAjax(true, "get", "json", "/operationcenter/prizesend/"+id,{},
                function(data) {
                    var content = "";
                    $.each(data,function(i,model){
                        content += "<tr class=\"text-left\" style='margin:5px 0;'>" +
                            "<td  style='color:#0099FF;width:180px;'>[" + model.createTime + "] " + model.operatorName + ": </td>" +
                            "<td>" + model.comment + "</td>" +
                            "</tr>";
                    });
                    $("#comment_list").append(content);
                    $("#prizeSendId").val(id);
                },
                function() {
                    popup.mould.popTipsMould("获取备注信息失败！", popup.mould.first, popup.mould.error, "", "56%", null);
                }
            );
        },
        fillAddressContent: function(id){
            common.getByAjax(true, "get", "json", "/operationcenter/prizesend/address", {id : id},
                function(data) {
                    if (data.province != null) {
                        $("#userName").text(common.checkToEmpty(data.userName));
                        $("#phoneNo").text(common.checkToEmpty(data.phoneNo));
                        $("#read_address").show();
                        $("#address").text(common.checkToEmpty(data.province) + common.checkToEmpty(data.city) + common.checkToEmpty(data.district) + common.checkToEmpty(data.street));
                        $("#add_address").hide();
                        $("#add_street").attr("style", "visibility:hidden");
                        $("#toAdd").hide();
                        $("#toCancel").hide();
                        $("#toSave").hide();
                        $("#toClose").unbind("click").bind({
                            click: function () {
                                popup.mask.hideFirstMask();
                                prize_distribution.fillContent.savePrizeSendAddress(id, data.flag, data.province, data.city, data.district, data.street);
                            }
                        });
                    } else {
                        //礼品发放表和地址表都没记录时，显示新增
                        prize_distribution.popupPage.popupAddAddress(id, data.userName, data.phoneNo);
                    }
                },
                function() {}
            )
        },
        switchStatus: function(id , status){
            common.getByAjax(true, "put", "json", "/operationcenter/prizesend/" + id + "/" + status, {},
                function(data) {
                    if (data.pass) {
                        if(status==0){
                            $("#status_id_"+id).css({'color':'red'}).html("未处理");
                            $("#yes_status_action_id_"+id).show();
                            $("#no_status_action_id_"+id).hide();
                        }else{
                            $("#status_id_"+id).css({'color':'green'}).html("已处理");
                            $("#yes_status_action_id_"+id).hide();
                            $("#no_status_action_id_"+id).show();
                        }
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                    }
                },
                function() {
                    popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        savePrizeSendAddress: function(id, flag, province, city, district, street) {
            //礼品发放地址表有地址时flag = 1
            if (flag == '1') {
                return;
            }
            //保存到prize_send_address 表
            common.getByAjax(true, "post", "json", "/operationcenter/prizesend/savePrizeSendAddress",
                {id:id, province:province,city:city,district:district,street:street}, function(data){},function(){});
        }
    },
    infoSave: {
        commentSave: function(){
            if(common.isEmpty($("#comment").val())){
                return;
            }
            common.getByAjax(true, "post", "json", "/operationcenter/prizesend", $("#comment_form").serialize(),
                function(data) {
                    $("#toCreate").attr("disabled", false);
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("备注成功！", popup.mould.first, popup.mould.success, "", "57%", null);
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function() {
                    $("#toCreate").attr("disabled", false);
                    popup.mould.popTipsMould("备注失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        },
        addressSave: function(id){
            $("#select_province").removeAttr("disabled");
            $("#input_street").removeAttr("disabled");
            $("#toAdd").hide();
            $("#toSave").show();
            $("#toCancel").show();
            $("#toCancel").unbind("click").bind({
                click: function() {
                    popup.mask.hideFirstMask();
                }
            })
            $("#toSave").unbind("click").bind({
                click: function () {
                    $("#new_form").validate(prize_distribution.config_validation);
                    var province = $("#select_province").find("option:selected").text();
                    var city = $("#select_city").find("option:selected").text();
                    var district = $("#select_district").find("option:selected").text();
                    var street = $("#input_street").val();
                    common.getByAjax(true, "post", "json", "/operationcenter/prizesend/savePrizeSendAddress",
                        {id:id, province:province, city:city, district:district, street:street},
                        function(data) {
                            if (data.pass) {
                                popup.mask.hideAllMask();
                                popup.mould.popTipsMould("新增地址成功！", popup.mould.first, popup.mould.success, "", "57%",
                                    function() {
                                        popup.mask.hideFirstMask();
                                    }
                                );
                            } else {
                                popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                            }
                        },
                        function(){}
                    )
                }
            })
        }
    },
    exportExcel: {
        prizeListExport: function(){
            var urlParameter = "";
            var keyType = $("#keyType").val();
            var keyword = $("#keyword").val();
            var status = $("#statusSel").val();
            urlParameter += "currentPage=" + prize_distribution.listPrizeDistribution.properties.currentPage +"&pageSize=" + prize_distribution.listPrizeDistribution.properties.pageSize;
            if(!common.isEmpty(keyword)){
                             urlParameter += "&keyTyp=" + keyType +"&keyword=" + keyword;
                         } else {
                             urlParameter += "&keyType=1";
                         }
            if(status){
                urlParameter += "&status=" + status;
            }
            var url = "/operationcenter/prizesend/export?"+urlParameter;
            $("#exportExcel").attr("href", url);
        }
    }
};
$(function() {
    prize_distribution.listPrizeDistribution.list();

    /**
     * 搜索
     */
    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould("请输入搜索内容", "first", "warning", "", "", null);
                return false;
            }
            prize_distribution.listPrizeDistribution.properties.keyword = keyword;
            prize_distribution.listPrizeDistribution.properties.currentPage = 1;
            prize_distribution.listPrizeDistribution.list();
        }
    });
    /**
     * 根据状态查
     */
    $("#statusSel").bind({
        change: function () {
            var keyword = $("#keyword").val();
            var status = $("#statusSel").val();
            if(!common.isEmpty(keyword)){
                prize_distribution.listPrizeDistribution.properties.keyword = keyword;
            }
            prize_distribution.listPrizeDistribution.properties.currentPage = 1;
            prize_distribution.listPrizeDistribution.list(status);
        }
    });
    /**
     * 导出
     */
    $("#exportExcel").bind({
        click: function () {
            prize_distribution.exportExcel.prizeListExport();
        }
    });
});
