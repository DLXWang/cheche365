/**
 * Created by sunhuazhong on 2015/06/05.
 */
var Vip = function(){
    /* 增加 */
    this.save = function(){
        $("#save_button").attr("disabled", true);
        common.getByAjax(true, "get", "", "/orderCenter/vip/add", $("#vipInputForm").serialize(),
            function(data){
                if(data.result == "success"){
                    common.showTips("保存成功");
                }else{
                    common.showTips(data.message);
                }
                $("#vipInputForm")[0].reset();
                $("#save_button").attr("disabled", false);
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
        common.getByAjax(true, "get" ,"", "/orderCenter/vip/delete", {id:id},
            function(data){
                if(data.result == "success"){
                    common.showTips("删除成功");
                    vip.list(properties);
                    showContent();
                    $("#vipInputForm")[0].reset();
                } else {
                    common.showTips("删除失败");
                }

                $("#delete_button").attr("disabled", false);
            },function(){
                common.showTips("系统异常");
                $("#delete_button").attr("disabled", true);
            }
        );
    };

    /* 更新 */
    this.update = function(properties){
        $("#update_button").attr("disabled", true);
        common.getByAjax(true, "get", "", "/orderCenter/vip/update", $("#vipInputForm").serialize(),
            function(data){
                if(data.result == "success"){
                    common.showTips("更新成功");
                    vip.list(properties);
                    showContent();
                    $("#vipInputForm")[0].reset();
                }else{
                    common.showTips(data.message);
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
        common.getByAjax(true, "get", "json", "/orderCenter/vip/list",
            {
                currentPage : properties.currentPage,
                pageSize : properties.pageSize,
                keyword : properties.keyword
            },
            function(data){
                $("#vip_tab tbody").empty();

                if(data == null || data.objectMap.model == null || data.objectMap.model.pageInfo.totalElements < 1){
                    return false;
                }

                if(data == null || data.objectMap.model.pageInfo.totalElements < 1){
                    $("#totalCount").text("0");
                    return false;
                }
                $("#page_up_down").show();
                if(properties.currentPage < 2){
                    $("#page_up_down").find("#pageUp").hide();
                }else{
                    $("#page_up_down").find("#pageUp").show();
                }
                if(properties.currentPage >= data.objectMap.model.pageInfo.totalPage){
                    $("#page_up_down").find("#pageDown").hide();
                }else{
                    $("#page_up_down").find("#pageDown").show();
                }
                if(data.objectMap.model.pageInfo.totalElements <= properties.pageSize){
                    $("#page_up_down").find("#pageDown").hide();
                    $("#page_up_down").find("#pageDown").hide();
                }

                $("#totalCount").text(data.objectMap.model.pageInfo.totalElements);

                var content = "";
                $.each(data.objectMap.model.viewList, function(n, vip) {
                    content += "<tr class='text-center'>" +
                                    "<td>" + common.checkToEmpty(vip.name) + "</td>" +
                                    "<td>" + common.checkToEmpty(vip.code) + "</td>" +
                                    "<td>" +  common.checkToEmpty(vip.startDate) + "</td>" +
                                    "<td>" +  common.checkToEmpty(vip.endDate) + "</td>" +
                                    "<td>" +  common.checkToEmpty(vip.createTime) + "</td>" +
                                    "<td>" +  common.checkToEmpty(vip.updateTime) + "</td>" +
                                    "<td>" +  common.checkToEmpty(vip.operator) + "</td>" +
                                    "<td>" +  common.checkToEmpty(vip.comment) + "</td>" +
                                    "<td>" + "<a href='javascript:;' onclick=vip.edit('" + vip.id + "');>编辑</a></td>" +
                                "</tr>";
                });

                $("#vip_tab tbody").append(content);
            },function(){
                common.showTips("系统异常");
            }
        );
    };

    /* 编辑 */
    this.edit = function(id){
        showEdit();
        common.getByAjax(true, "get", "json", "/orderCenter/vip/findOne", {id:id},
            function(data){
                if(data == null || data.result == "fail"){
                    common.showTips("获取大客户信息失败");
                    return false;
                }

                $("#name").val(data.objectMap.model.name);
                $("#code").val(data.objectMap.model.code);
                $("#startDate").val(data.objectMap.model.startDate);
                $("#endDate").val(data.objectMap.model.endDate);
                $("#comment").val(data.objectMap.model.comment);
                $("#id").val(id);
            },function(){
                common.showTips("系统异常");
            }
        );
    };
}

Vip.prototype = new Action();
var vip = new Vip();
