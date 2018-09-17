/**
 * Created by wangfei on 2015/8/25.
 */
var userRedPacket = {
    show:{
        content:'',
        popInput:function(startTime){
            userRedPacket.initRedPacket.init(startTime);
            userRedPacket.initRedPacket.initPopupContent();
            popup.pop.popInput(userRedPacket.show.content, popup.mould.first, "1100px", "650px", "36%", "40%");
            $("#detail_close").unbind("click").bind({
                click: function() {
                    popup.mask.hideFirstMask();
                }
            });

            /**
             * 搜索
             */
            $("#popover_normal_input #searchBtn").bind({
                click: function () {
                    var keyword = $("#popover_normal_input #keyword").val();
                    if (common.isEmpty(keyword)) {
                        popup.mould.popTipsMould("请输入搜索内容", popup.mould.second, "warning", "", "", null);
                        return false;
                    }
                    userRedPacket.listRedPacket.properties.keyType = $("#popover_normal_input #keyType").val();
                    userRedPacket.listRedPacket.properties.keyword = keyword;
                    userRedPacket.listRedPacket.properties.currentPage = 1;
                    userRedPacket.listRedPacket.list();
                }
            });
            userRedPacket.listRedPacket.properties.keyword = '';
            userRedPacket.listRedPacket.list();
        }
    },
    initRedPacket: {
        init: function(startTime) {
            userRedPacket.listRedPacket.properties = new Properties(1, "");
            userRedPacket.listRedPacket.properties.pageSize = 6;
            userRedPacket.listRedPacket.properties.startTime = startTime;
        },

        initPopupContent: function() {
            var popupContent = $("#user_red_packet_detail_div");
            if (popupContent.length > 0) {
                userRedPacket.show.content = popupContent.html();
                popupContent.remove();
            }
        }
    },
    listRedPacket: {
        properties : new Properties(1, ""),

        list: function() {
            //if (!common.permission.validUserPermission("op0101")) {
            //    return;
            //}
            common.getByAjax(true, "get", "json", "/operationcenter/red/detail",
                {
                    currentPage : userRedPacket.listRedPacket.properties.currentPage,
                    pageSize    : userRedPacket.listRedPacket.properties.pageSize,
                    keyword     : userRedPacket.listRedPacket.properties.keyword,
                    keyType     : userRedPacket.listRedPacket.properties.keyType,
                    startTime   : userRedPacket.listRedPacket.properties.startTime
                },
                function(data) {
                    $("#detail_list_tab tbody").empty();
                    if (data.pageInfo.totalElements < 1) {
                        $("#popover_normal_input #detail_totalCount").text("0");
                        $("#popover_normal_input .customer-pagination").hide();
                        if (!common.isEmpty(userRedPacket.listRedPacket.properties.keyword)) {
                            popup.mould.popTipsMould("无符合条件的结果", popup.mould.second, "", "", "",
                                function() {
                                    popup.mask.hideSecondMask(false);
                                }
                            );
                        }
                        return false;
                    }
                    $("#popover_normal_input #detail_totalCount").text(data.pageInfo.totalElements);
                    $("#popover_normal_input #pageUl").empty();
                    if (data.pageInfo.totalPage > 1) {
                        $.jqPaginator('#template_pagination',
                            {
                                totalPages: data.pageInfo.totalPage,
                                visiblePages: userRedPacket.listRedPacket.properties.visiblePages,
                                currentPage: userRedPacket.listRedPacket.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        userRedPacket.listRedPacket.properties.currentPage = pageNum;
                                        userRedPacket.listRedPacket.list(userRedPacket.listRedPacket.properties);
                                        window.scrollTo(0,0);
                                    }
                                }
                            }
                        );
                        $("#template_page_div").show();
                    } else {
                        $("#template_page_div").hide();
                    }

                    userRedPacket.listRedPacket.fillTabContent(data);
                    common.scrollToTop();
                },
                function() {
                    popup.mould.popTipsMould("获取筛选列表失败！", popup.mould.second, popup.mould.error, "", "56%", null);
                }
            );
        },
        fillTabContent: function(data) {
            var content = "";
            $.each(data.viewList,function(i,model){
                content += "<tr class=\"text-center\">" +
                                "<td>" + model.id + "</td>" +
                                "<td>" + common.checkToEmpty(model.mobile) + "</td>" +
                                "<td>" + common.checkToEmpty(model.licensePlateNo) + "</td>" +
                                "<td>" + common.checkToEmpty(model.quotePhotoCteateDate) + "</td>" +
                                "<td style='color: " + (model.smsFlag == null || model.smsFlag == 2? "orange" : (model.smsFlag == 1? "green" : "red")) + "'>" + (model.smsFlag == null || model.smsFlag == 2? "未发送" : (model.smsFlag == 1? "成功" : "失败")) + "</td>" +
                                "<td>" + common.checkToEmpty(model.smsResult) + "</td>" +
                                "<td style='color: " + (model.redFlag == null || model.redFlag == 2? "orange" : (model.redFlag == 1? "green" : "red")) + "'>" + (model.redFlag == null || model.redFlag == 2? "未发放" : (model.redFlag == 1? "成功" : "失败")) + "</td>" +
                                "<td>" + common.checkToEmpty(model.redResult) + "</td>" +
                                "<td>" + (model.isSatisfied ? "<span style=\"color: green;\">满足</span>" : "<span style=\"color: red;\">不满足</span>") + "</td>" +
                                "<td>" + common.getCommentMould(common.checkToEmpty(model.description), 6) + "</td>" +
                                "<td><span style='margin-left:20px;'><a href='javascript:;' onclick=userRedPacket.listRedPacket.popQuotePhotoDetail("+model.quotePhotoId+");>"+common.checkToEmpty(model.quotePhotoId)+"</a></span></td>"+
                           "</tr>";
            });
            $("#popover_normal_input #detail_list_tab tbody").html(content);

        },
        popQuotePhotoDetail:function(quotePhotoId){
            quote_photo_pop.quoteDetail.popup(quotePhotoId);
        }

    }
};
$(function() {
    //userRedPacket.listRedPacket.list();
    //userRedPacket.initRedPacket.init();
});
