/**
 * Created by zhangshitao on 2015/11/02.
 */
var invite_code = {
    content: "",
    addressContent: "",
    initInviteCode: {
        initPopupContent: function() {
            var popupContent = $("#invited_list_div");
            if (popupContent.length > 0) {
                invite_code.content = popupContent.html();
                popupContent.remove();
            }
        },
        initAddressContents: function() {
            var addressContents = $("#new_content");
            if (addressContents.length > 0) {
                invite_code.addressContent = addressContents.html();
                addressContents.remove();
            }
        }
    },
    listInviteCode: {
        properties : new Properties(1, ""),
        list: function() {
            common.getByAjax(true, "get", "json", "/operationcenter/invitecode",
                {
                    currentPage : invite_code.listInviteCode.properties.currentPage,
                    pageSize    : invite_code.listInviteCode.properties.pageSize,
                    keyword     : invite_code.listInviteCode.properties.keyword,
                    keyType     : "1"
                },
                function(data) {
                    $("#list_tab tbody").empty();

                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.isEmpty(invitecode.listInviteCode.properties.keyword)) {
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
                                visiblePages: invite_code.listInviteCode.properties.visiblePages,
                                currentPage: invite_code.listInviteCode.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        invite_code.listInviteCode.properties.currentPage = pageNum;
                                        invite_code.listInviteCode.list(invite_code.listInviteCode.properties);
                                        window.scrollTo(0,0);
                                    }
                                }
                            }
                        );
                        $(".customer-pagination").show();
                    } else {
                        $(".customer-pagination").hide();
                    }

                    invite_code.listInviteCode.fillTabContent(data);
                    common.scrollToTop();
                },
                function() {
                    popup.mould.popTipsMould("获取用户邀请码列表页失败！", popup.mould.first, popup.mould.error, "", "56%", null);
                }
            );
        },
        fillTabContent: function(data) {
            var content = "";
            $.each(data.viewList,function(i,model){
                content += "<tr class=\"text-center\">" +
                    "<td>" + model.userId + "</td>" +
                    "<td>" + common.checkToEmpty(model.phoneNo) + "</td>" +
                    "<td>" + common.checkToEmpty(model.inviteCode) + "</td>" +
                    "<td>" + common.checkToEmpty(model.createTime) + "</td>" +
                    "<td>" + model.inviteCount + "</td>" +
                    "<td><a id='getInvitedList"+ model.userId+"' style=\"margin-left: 10px;\" href='javascript:;' >查看邀请用户列表</a></td>" +
                    "<td><a id='getAddress"+ model.userId+"' style=\"margin-left: 10px;\" href='javascript:;' >查看收货信息详情</a></td>" +
                "</tr>";
            });
            $("#list_tab tbody").html(content);
            $("[id^='getInvitedList']").unbind("click").bind({
                click: function () {
                    invite_code.getInvitedList.popInput();
                    invite_code.getInvitedList.showList(this.id.replace('getInvitedList' ,''));
                }
            })
            $("[id^='getAddress']").unbind("click").bind({
                click: function() {
                    invite_code.getAddressList.popAddress();
                    invite_code.getAddressList.showAddress(this.id.replace('getAddress',''));
                }
            })
        }
    },
    getInvitedList: {
        popInput: function() {
            invite_code.initInviteCode.initPopupContent();
            popup.pop.popInput(invite_code.content, popup.mould.first, "600px", "554px", "36%", "59%");
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function() {
                    popup.mask.hideFirstMask();
                }
            });
        },
        showList: function(userId) {
            var content = "";
            common.getByAjax(true, "get", "json", "/operationcenter/invitecode/"+ userId,{},
                function(data) {
                    if(data.length > 0){
                        $.each(data, function(i, view) {
                            content += "<tr class='text-center'>" +
                            "<td>" + view.id + "</td>" +
                            "<td>" + view.registTime + "</td>" +
                            "<td>" + view.createTime + "</td>" +
                            "<td>" + view.userId + "</td>" +
                            "<td>" + view.phoneNo + "</td>" +
                            "</tr>";
                        });
                    }
                    $("#invited_list_tab tbody").html(content);
                },
                function() {
                    popup.mould.popTipsMould("获取邀请用户列表失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        }
    },
    getAddressList: {
        popAddress: function() {
            invite_code.initInviteCode.initAddressContents();
            popup.pop.popInput(invite_code.addressContent, popup.mould.first, "400px", "250px", "36%", "59%");
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function() {
                    popup.mask.hideFirstMask();
                }
            });
        },
        showAddress: function(userId) {
            common.getByAjax(true, "get", "json", "/operationcenter/invitecode/address",{userId:userId},
                function(data) {
                    $("#userName").text(common.checkToEmpty(data.userName));
                    $("#phoneNo").text(common.checkToEmpty(data.phoneNo));
                    $("#address").text(common.checkToEmpty(data.address));
                },
                function() {
                    popup.mould.popTipsMould("获取收货信息详情失败！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        }
    }

};
$(function() {
    invite_code.listInviteCode.list();

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
            invite_code.listInviteCode.properties.keyword = keyword;
            invite_code.listInviteCode.properties.currentPage = 1;
            invite_code.listInviteCode.list();
        }
    });
});
