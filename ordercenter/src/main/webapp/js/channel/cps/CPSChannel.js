/**
 * Created by wangfei on 2015/5/22.
 */
var CPSChannel = function(){
    /* 增加 */
    this.save = function(){
        $("#save_button").attr("disabled", true);
        common.getByAjax(true, "get", "json", "/orderCenter/cps/add", $("#channelForm").serialize(),
            function(data){
                if(data){
                    common.showTips("添加成功");
                }else{
                    common.showTips("添加失败");
                }
                $("#save_button").attr("disabled", false);
                $("#channelForm")[0].reset();
            },
            function(){
                common.showTips("系统异常");
                $("#save_button").attr("disabled", false);
            }
        );
    };

    /* 删除 */
    this.delete = function(id, properties){
        $("#delete_button").attr("disabled", true);
        common.getByAjax(true, "get", "", "/orderCenter/cps/delete", {channelId:id},
            function(data){
                if(data){
                    common.showTips("删除成功");
                    cpsChannel.list(properties);
                    showContent();
                    $("#channelForm")[0].reset();
                }else{
                    common.showTips("删除失败");
                }
                $("#delete_button").attr("disabled", false);
            },function(){
                common.showTips("系统异常");
                $("#delete_button").attr("disabled", false);
            }
        );
    };

    /* 更新 */
    this.update = function(properties){
        $("#update_button").attr("disabled", true);
        common.getByAjax(true, "get", "", "/orderCenter/cps/update", $("#channelForm").serialize(),
            function(data){
                if(data){
                    common.showTips("更新成功");
                    cpsChannel.list(properties);
                    showContent();
                    $("#channelForm")[0].reset();
                }else{
                    common.showTips("更新失败");
                }
                $("#update_button").attr("disabled", false);
            },function(){
                common.showTips("系统异常");
                $("#update_button").attr("disabled", false);
            }
        );
    };

    /* 列表查询 */
    this.list = function(properties){
        common.getByAjax(true, "get", "json", "/orderCenter/cps/list",
            {
                currentPage : properties.currentPage,
                pageSize    : properties.pageSize,
                keyword     : properties.keyword
            },
            function(data){
                $("#channel_tab tbody").empty();
                if(data == null){
                    common.showTips("获取CPS渠道列表失败");
                    return false;
                }

                if (data == null || data.pageInfo.totalElements < 1) {
                    $("#totalCount").text("0");
                    $(".customer-pagination").hide();
                    return false;
                }

                $("#totalCount").text(data.pageInfo.totalElements);
                $("#pageUl").empty();
                if (data.pageInfo.totalPage > 1) {
                    $.jqPaginator('.pagination',
                        {
                            totalPages: data.pageInfo.totalPage,
                            visiblePages: properties.visiblePages,
                            currentPage: properties.currentPage,
                            onPageChange: function (pageNum, pageType) {
                                if (pageType=="change") {
                                    properties.currentPage = pageNum;
                                    cpsChannel.list(properties);
                                }
                            }
                        }
                    );
                    $(".customer-pagination").show();
                } else {
                    $(".customer-pagination").hide();
                }

                var content = "";
                $.each(data.viewList, function(n, model) {
                    content += "<tr class='text-center'>" +
                                    "<td>" + common.checkToEmpty(model.channelNo) + "</td>" +
                                    "<td>" + common.checkToEmpty(model.name) + "</td>" +
                                    "<td>" + common.checkToEmpty(model.startDate) + "</td>" +
                                    "<td>" + common.checkToEmpty(model.endDate) + "</td>" +
                                    "<td>" + model.rebate + "%</td>" +
                                    "<td></td>" +
                                    "<td>" +  common.checkToEmpty(model.createTime) + "</td>" +
                                    "<td>" +  common.checkToEmpty(model.updateTime) + "</td>" +
                                    "<td>" +  common.checkToEmpty(model.operator) + "</td>" +
                                    "<td><a href='javascript:;' onclick='cpsChannel.edit(" + model.id + ");'>编辑</a></td>" +
                                "</tr>";
                });
                $("#channel_tab tbody").append(content);
                window.parent.scrollTo(0,0);
            },
            function(){
                common.showTips("系统异常");
            }
        );
    };

    /* 编辑 */
    this.edit = function(id){
        common.getByAjax(true, "get", "json", "/orderCenter/cps/findOne", {channelId:id},
            function(data){
                if(data == null){
                    common.showTips("获取渠道信息失败");
                    return false;
                }

                showEdit();
                $("#name").val(common.checkToEmpty(data.name));
                $("#rebate").val(data.rebate);
                $("#channelNo").val(common.checkToEmpty(data.channelNo));
                $("#wapUrl").val(common.checkToEmpty(data.wapUrl));
                $("#startDate").val(common.checkToEmpty(data.startDate));
                $("#endDate").val(common.checkToEmpty(data.endDate));
                $("#linkMan").val(common.checkToEmpty(data.linkMan));
                $("#mobile").val(common.checkToEmpty(data.mobile));
                $("#email").val(common.checkToEmpty(data.email));
                if(data.enable){
                    $("#no").attr("checked", false);
                    $("#yes").attr("checked", true);
                }else{
                    $("#no").attr("checked", true);
                    $("#yes").attr("checked", false);
                }
                if (data.display) {
                    $("#display_no").attr("checked", false);
                    $("#display_yes").attr("checked", true);
                } else {
                    $("#display_no").attr("checked", true);
                    $("#display_yes").attr("checked", false);
                }
                if(data.frequency == 1){
                    $("#eachWeek").attr("checked", true);
                }else{
                    $("#eachMonth").attr("checked", true);
                }
                $("#id").val(data.id);
            },function(){common.showTips("系统异常");}
        )
    };
}

CPSChannel.prototype = new Action();
var cpsChannel = new CPSChannel();

var validation = {
    vaild : function(form){
        var pass = true;
        form.find('[validation]').each(function(){
            var id = $(this).attr("id");
            var value = $.trim($(this).val());
            switch (id) {
                case "name":
                    if(common.isEmpty(value)) {
                        common.showTips("请输入渠道名称");
                        pass = false;
                        return false;
                    }
                    break;
                case "rebate":
                    if(common.isEmpty(value)) {
                        common.showTips("请输入返点位");
                        pass = false;
                        return false;
                    } else if(!common.isNumber(value)) {
                        common.showTips("请输入有效的返点位");
                        pass = false;
                        return false;
                    }
                    break;
                case "channelNo":
                    if(common.isEmpty(value)) {
                        common.showTips("请输入渠道编号");
                        pass = false;
                        return false;
                    }
                    break;
                case "wapUrl":
                    if(common.isEmpty(value)) {
                        //common.showTips("请点击'生成wap URL一键生成'按钮生成链接");
                        common.showTips("请输入WAP地址");
                        pass = false;
                        return false;
                    }
                    break;
                case "startDate":
                    if(common.isEmpty(value)) {
                        common.showTips("请选择合作开始日期");
                        pass = false;
                        return false;
                    }
                    break;
                case "endDate":
                    if(common.isEmpty(value)) {
                        common.showTips("请选择合作结束日期");
                        pass = false;
                        return false;
                    }
                    break;
                case "linkMan":
                    if(common.isEmpty(value)) {
                        common.showTips("请输入联系人");
                        pass = false;
                        return false;
                    }
                    break;
                case "mobile":
                    if(common.isEmpty(value)) {
                        common.showTips("请输入联系人手机");
                        pass = false;
                        return false;
                    } else if(!common.isMobile(value)) {
                        common.showTips("请输入有效的联系人手机");
                        pass = false;
                        return false;
                    }
                    break;
                case "email":
                    if(common.isEmpty(value)) {
                        common.showTips("请输入联系人邮箱");
                        pass = false;
                        return false;
                    } else if(!common.isEmail(value)) {
                        common.showTips("请输入有效的联系人邮箱地址");
                        pass = false;
                        return false;
                    }
                    break;
            }
        });
        return pass;
    }
}
