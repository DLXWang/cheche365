/**
 * Created by sunhuazhong on 2015/8/11.
 */
$(function(){
    /* 初始化 */
    var properties = new Properties(1, "");
    statusChange.listForStatusChange(properties);

    /* 上一页 */
    $("#page_up_down").find("#pageUp").bind({
        click : function(){
            properties.currentPage --;
            statusChange.listForStatusChange(properties);
        }
    });

    /* 下一页 */
    $("#page_up_down").find("#pageDown").bind({
        click : function(){
            properties.currentPage ++;
            statusChange.listForStatusChange(properties);
        }
    });

    /* 搜索 */
    $("#searchBtn").bind({
        click : function(){
            if($.trim($("#keyword").val()) == ""){
                common.showTips("请输入搜索内容");
                return false;
            }
            properties.currentPage = 1;
            properties.keyword = $("#keyword").val();
            statusChange.listForStatusChange(properties);
        }
    });
});

var statusChange = {
    listForStatusChange : function(properties){
        common.getByAjax(true, "get", "json", "/orderCenter/user/list",
            {
                currentPage : properties.currentPage,
                pageSize : properties.pageSize,
                keyword : properties.keyword
            },
            function(data){
                $("#user_tab tbody").empty();

                $("#totalCount").text(data.pageInfo.totalElements);
                $("#pageUl").empty();
                if(data.pageInfo.totalElements>0){
                    $.jqPaginator('#pageUl',
                        {
                            totalPages: data.pageInfo.totalPage,
                            visiblePages: properties.visiblePages,
                            currentPage: properties.currentPage,
                            onPageChange: function (pageNum, pageType) {
                                if(pageType=="change"){
                                    properties.currentPage = pageNum;
                                    statusChange.listForStatusChange(properties);
                                }
                            }
                        }
                    );
                }
                var content = "";
                $.each(data.viewList, function(n, user) {
                    content += "<tr class='text-center' id='tab_tr" + user.id + "'>" +
                    "<td>" + common.checkToEmpty(user.email) + "</td>" +
                    "<td>" +  common.checkToEmpty(user.name) + "</td>" +
                    "<td>" +  common.checkToEmpty(user.mobile) + "</td>";
                    if(common.checkToEmpty(user.statusChange) == "") {
                        content += "<td id='config_td'>" + "<a href='javascript:;' onclick=statusChange.config('" + user.id + "');>提交配置</a></td>";
                    } else {
                        content += "<td id='config_td'>" + "<a href='javascript:;' onclick=statusChange.config('" + user.id + "');>取消配置</a></td>";
                    }
                    content += "</tr>";
                });

                $("#user_tab tbody").append(content);
                window.parent.scrollTo(0,0);
            },function(){
                common.showTips("系统异常");
            }
        );
    },

    config : function(internalUserId){
        common.getByAjax(true, "get", "json", "/orderCenter/user/config/" + internalUserId, {},
            function(data){
                if(data) {
                    common.showTips("配置成功");
                    var config_td = $("#tab_tr" + internalUserId).find("#config_td");
                    if (config_td.text() == "提交配置") {
                        config_td.html("<a href='javascript:;' onclick=statusChange.config('" + internalUserId + "');>取消配置</a>");
                    } else if (config_td.text() == "取消配置") {
                        config_td.html("<a href='javascript:;' onclick=statusChange.config('" + internalUserId + "');>提交配置</a>");
                    }
                } else{
                    common.showTips("配置失败");
                }
            },
            function(){
                common.showTips("系统异常");
            }
        );
    }
}
