/**
 * Created by wangfei on 2015/9/21.
 */
var user = {
    page: new Properties(1, ""),
    listUser: {
        list: function() {
            common.ajax.getByAjax(false, "get", "json", "/admin/users",
                {
                    currentPage : user.page.currentPage,
                    pageSize : user.page.pageSize,
                    keyword : user.page.keyword,
                    keyType: user.page.keyType
                },
                function(data){
                    $("#user_list_tab tbody").empty();
                    if(data == null){
                        popup.mould.popTipsMould("获取用户列表失败！", popup.mould.first, popup.mould.warning, "", "57%", null);
                        return false;
                    }
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.validation.isEmpty(auto.page.keyword)) {
                            popup.mould.popTipsMould("无符合条件的结果", popup.mould.first, popup.mould.warning, "", "57%", null);
                        }
                        return false;
                    }
                    $("#totalCount").text(data.pageInfo.totalElements);
                    if (data.pageInfo.totalPage > 1) {
                        $(".customer-pagination").show();
                        $.jqPaginator('.pagination',
                            {
                                totalPages: data.pageInfo.totalPage,
                                visiblePages: user.page.visiblePages,
                                currentPage: user.page.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        user.page.currentPage = pageNum;
                                        user.listUser.list();
                                    }
                                }
                            }
                        );
                    } else {
                        $(".customer-pagination").hide();
                    }
                    user.listUser.fillTabContent(data);
                    common.tools.scrollToTop();
                },function(){
                    popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        fillTabContent: function(data) {
            if (data.viewList) {
                var content = "";
                $.each(data.viewList, function(n, user) {
                    content += "<tr class='text-center'>" +
                    "<td>"  + common.tools.checkToEmpty(user.id) +  "</td>" +
                    "<td>" + common.tools.checkToEmpty(user.mobile) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(user.binding) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(user.regtime) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(user.regChannel) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(user.regIp) + "</td>" +
                    "<td>" + common.tools.checkToEmpty(user.lastLoginTime) + "</td>";
                    content += "<td>";
                    if (user.autos && user.autos.length) {
                        $.each(user.autos, function(i, auto){
                            if (i > 4) {
                                if (i == 5) {
                                    content += "<div id=\"more_id_" + user.id + "\" class=\"none\">";
                                }
                                content += "<p><img src='../../images/auto.png' style='width: 40px;height: 22px;'>" +
                                "<a href='javascript:;' onclick=user.autoDetail.popDetail('" + auto.id + "');>" + common.tools.checkToEmpty(auto.licensePlateNo) + "</a></p>";
                                if (i == user.autos.length-1) {
                                    content += "</div>" +
                                    "<p><a href=\"javascript:;\" onclick=\"user.listUser.showMore('more_id_" + user.id + "',this);\">更多</a></p>";
                                }
                            } else {
                                content += "<p><img src='../../images/auto.png' style='width: 40px;height: 22px;'>" +
                                "<a href='javascript:;' onclick=user.autoDetail.popDetail('" + auto.id + "');>" + common.tools.checkToEmpty(auto.licensePlateNo) + "</a></p>";
                            }
                        });
                    } else {
                        content += "<span style='color: red;'>未绑定车辆</span>";
                    }
                    content += "</td></tr>";
                });
                $("#user_list_tab tbody").append(content);
            }
        },
        showMore: function(more_id,obj) {
            $("#"+more_id).toggle();
            if(obj.innerHTML=="更多"){
                obj.innerHTML="隐藏";
            }else{
                obj.innerHTML="更多"
            }
        }
    },
    autoDetail: {
        detail_content: "",
        initPopContent: function() {
            var popupContent = $("#detail_content");
            if (popupContent.length > 0) {
                user.autoDetail.detail_content = popupContent.html();
                popupContent.remove();
            }
        },
        popDetail: function(id) {
            common.ajax.getByAjax(true, "get", "json", "/admin/auto/" + id, {},
                function(data) {
                    popup.pop.popInput(user.autoDetail.detail_content, popup.mould.first, "576px", "582px", "33%", "");
                    $("#auto_detail_close").unbind("click").bind({
                        click : function() {
                            popup.mask.hideAllMask(false);
                        }
                    });
                    user.autoDetail.fillDetailContent(data);
                },
                function() {
                    popup.mould.popTipsMould("获取车辆信息异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        fillDetailContent: function(data) {
            $("#detail_title").html($("#detail_title").html() + "&nbsp;<" + common.tools.checkToEmpty(data.licensePlateNo) + ">" );
            $("#detail_owner").text(common.tools.checkToEmpty(data.owner));
            $("#detail_identity").text(common.tools.checkToEmpty(data.identity));
            $("#detail_licensePlateNo").text(common.tools.checkToEmpty(data.licensePlateNo));
            $("#detail_vinNo").text(common.tools.checkToEmpty(data.vinNo));
            $("#detail_engineNo").text(common.tools.checkToEmpty(data.engineNo));
            $("#detail_enroll_date").text(common.tools.checkToEmpty(data.enrollDate));
            $("#detail_model").text(common.tools.checkToEmpty(data.model));
            $("#detail_brand_code").text(common.tools.checkToEmpty(data.brandCode));
            $("#detail_expire_date").text(common.tools.checkToEmpty(data.expireDate));
            var content="";
            if(data.userViewModels!=null){
                $.each(data.userViewModels,function(i,user){
                    content+="<div class='col-sm-5 text-height-28 text-center'>" +
                    "<span class='text-height-28' id='detail_user_id'>"+common.tools.checkToEmpty(user.id)+"</span>" +
                    "</div>" +
                    "<div class='col-sm-5 text-height-28 text-center'>" +
                    "<span class='text-height-28' id='detail_mobile'>"+common.tools.checkToEmpty(user.mobile)+"</span>" +
                    "</div>";
                });
            }
            $("#auto_user_info").html(content);
            $("#driver_img").attr("src", "");
        }
    }
};

$(function() {
    user.autoDetail.initPopContent();
    user.listUser.list();

    /* 搜索 */
    $("#searchBtn").bind({
        click : function(){
            var keyword = $("#keyword").val();
            if(common.validation.isEmpty(keyword)){
                popup.mould.popTipsMould("请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            user.page.currentPage = 1;
            user.page.keyword = keyword;
            user.page.keyType = $("#keyType").val();
            user.listUser.list();
        }
    });

});
