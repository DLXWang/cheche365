/**
 * Created by liyh on 2015/9/6.
 */
var permission = {
    initPermission : {
        initAllotPopupContent: function() {
            var popupContent = $("#allot_permission");
            if (popupContent.length > 0) {
                permission.allotPermission.content = popupContent.html();
                popupContent.remove();
            }
        }
    },

    listPermission : {
        properties : new Properties(1, ""),

        /**
         * 权限列表
         */
        list : function() {
            common.ajax.getByAjax(false, "get", "json", "/admin/permission",
                {
                    keyword: permission.listPermission.properties.keyword,
                    currentPage: permission.listPermission.properties.currentPage,
                    pageSize: permission.listPermission.properties.pageSize
                },
                function(data) {
                    $("#permission_list_tab tbody").empty();
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!permission.listPermission.properties.keyword) {
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
                                visiblePages: permission.listPermission.properties.visiblePages,
                                currentPage: permission.listPermission.properties.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        permission.listPermission.properties.currentPage = pageNum;
                                        permission.listPermission.list();
                                        window.scrollTo(0,0);
                                    }
                                }
                            }
                        );
                    } else {
                        $(".customer-pagination").hide();
                    }

                    // 显示列表数据
                    $("#permission_list_tab tbody").append(permission.listPermission.write(data));
                    common.tools.scrollToTop();
                }, function () {
                    popup.mould.popTipsMould("获取列表失败！", popup.mould.first, popup.mould.error, "", "56%", null);
                }
            )
        },

        /**
         * 把数据写入页面
         */
        write: function(data){
            var content = "";
            $.each(data.viewList,function(i,model){
                content += "<tr class='text-center'>" +
                "<td>" + model.id + "</td>" +
                "<td>" + common.tools.checkToEmpty(model.firstMenu) + "</td>" +
                "<td>" + common.tools.checkToEmpty(model.secondMenu) + "</td>" +
                "<td>" + common.tools.checkToEmpty(model.thirdMenu) + "</td>" +
                "<td>" + model.name + "</td>" +
                "<td><a onclick = 'allotPage("+ model.id +",\""+model.name+"\")'>分配权限</a></td>" +
                "</tr>";
            });
            return content;
        }

    },

    allotPermission:{
        content: "",
        /**
         * 分配权限页面
         */
        allotPage: function(id,name) {
            if (!common.permission.validUserPermission("ad040203")) {
                return;
            }
            permission.initPermission.initAllotPopupContent();
            popup.pop.popInput(permission.allotPermission.content, "first", "600px", "400px", "46%", null);
            $("#allot_id").html("分配权限&lt"+name+"&gt");
            /*关闭分配权限弹出框*/
            $("#popover_normal_input .theme_poptit .close").unbind("click").bind({
                click: function() {
                    popup.mask.hideFirstMask();
                }
            });
            permission.allotPermission.allotChecked(id);

            $("#allot_save").click(function(){
                permission.allotPermission.save(id);
            });
        },
        list :function(){
            common.ajax.getByAjax(false, "get", "json", "/admin/roles/userType",null,
                function(data) {
                    $("#allot_tab tbody").empty();
                    // 显示列表数据
                    $("#allot_tab tbody").append(permission.allotPermission.write(data));

                }, function () {
                    popup.mould.popTipsMould("获取列表失败！", popup.mould.first, popup.mould.error, "", "56%", null);
                }
            )
        },
        write: function(data){
            var content = "";
            $.each(data,function(i,model){
                content += "<tr class='text-center'>" +
                "<td>" + model.id + "</td>" +
                "<td>" + model.roleType + "</td>" +
                "<td>" + model.name + "</td>" +
                "<td> <input id='"+model.id+"' name='allot_role' type='checkbox' value='"+model.id+"'></td>" +
                "</tr>";
            });
            return content;
        },
        allotChecked: function(id){
            common.ajax.getByAjax(false, "get", "html", "/admin/permission/" + id, null,
                function(data) {
                    var myArr=new Array()
                    myArr = data.substring(0,data.length-1).split(",")
                    for(var i=0; i<myArr.length; i++){
                        $("#"+parseInt(myArr[i])).prop("checked",true);
                    }
                }, function () {
                }
            )
        },
        save: function(id){
            var checkedVal = new Array();
            $(':checkbox').each(function(){
                 if($(this).prop("checked")==true){
                    checkedVal.push(this.value);
                 }
            })
            var roleList = checkedVal.join(",");
            common.ajax.getByAjax(true, "post", "json", "/admin/permission", {roleList : roleList,permissionId : id},
                function(data) {
                    if (data.pass) {
                        popup.mask.hideAllMask();
                        popup.mould.popTipsMould("权限分配成功！", popup.mould.first, popup.mould.success, "", "57%",null);
                    } else {
                        popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "57%", null);
                    }
                },
                function() {
                    popup.mould.popTipsMould("操作失败，请重试！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    }

};

    /**
     * 分配页面
     */
    function allotPage(id,name){
        permission.allotPermission.allotPage(id,name);
    }


$(function() {
    permission.listPermission.list();
    permission.allotPermission.list();
    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (!keyword) {
                popup.mould.popTipsMould("请输入搜索内容", "first", "warning", "", "", null);
                return false;
            }
            permission.listPermission.properties.keyword = keyword;
            permission.listPermission.properties.keyType = $("#keyType").val();
            permission.listPermission.properties.currentPage = 1;
            permission.listPermission.list();
        }
    });

});
