var area_contact_info = {
    page: new Properties(1, ""),
    listInfo: {
        list: function() {
            common.getByAjax(true, "get", "json", "/orderCenter/nationwide/areaContactInfo/list",
                {
                    currentPage : area_contact_info.page.currentPage,
                    pageSize : area_contact_info.page.pageSize,
                    keyword : area_contact_info.page.keyword,
                    keyType: $("#keyType").val()
                },
                function(data){
                    $("#list_tab tbody").empty();
                    if(data == null){
                        popup.mould.popTipsMould(false, "获取分站信息列表失败！", popup.mould.first, popup.mould.warning, "", "57%", null);
                        return false;
                    }
                    if (data.pageInfo.totalElements < 1) {
                        $("#totalCount").text("0");
                        $(".customer-pagination").hide();
                        if (!common.isEmpty(quote_photo.page.keyword)) {
                            popup.mould.popTipsMould(false, "无符合条件的结果", popup.mould.first, popup.mould.warning, "", "57%", null);
                        }
                        return false;
                    }
                    $("#totalCount").text(data.pageInfo.totalElements);
                    $("#pageUl").empty();
                    if (data.pageInfo.totalPage > 1) {
                        $(".customer-pagination").show();
                        $.jqPaginator('.pagination',
                            {
                                totalPages: data.pageInfo.totalPage,
                                visiblePages: area_contact_info.page.visiblePages,
                                currentPage: area_contact_info.page.currentPage,
                                onPageChange: function (pageNum, pageType) {
                                    if (pageType=="change") {
                                        area_contact_info.page.currentPage = pageNum;
                                        area_contact_info.listInfo.list();
                                    }
                                }
                            }
                        );
                    } else {
                        $(".customer-pagination").hide();
                    }
                    area_contact_info.listInfo.fillTabContent(data);
                    window.parent.scrollTo(0,0);
                },function(){
                    popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        fillTabContent: function(data) {
            CUI.grid.store=data;
            CUI.grid.dom=$("#list_tab tbody");
            CUI.grid.columns=[
                {dataIndex:'id'},
                {dataIndex:'areaName'},
                {dataIndex:'name'},
                {dataIndex:'mobile'},
                {dataIndex:'operator'},
                {dataIndex:'updateTime'},
                {dataIndex:'comment',renderer:function(value){
                   return "<span title='" +((value==null)?"":value.replace(/\\r\\n/g,'\n'))+ "'>"
                    + common.getFormatComment((value==null)?"":value.replace(/\\r\\n/g,''), 15)
                    + "</span>";
                }},
                {dataIndex:'',renderer:function(value,rowIndex,rowStore){
                    return "&nbsp;&nbsp;<a href='javascript:;' onclick='area_contact_info_pop.infoDetail.popup("+ rowStore.id + ");'>查看详情</a>";
                }}
                ]
            CUI.grid.fill();
        }
    }

}
var parent = window.parent;
var area_contact_info_pop = {
    infoInit: {
        init: function () {
            var detailContent = $("#detail_content");
            if (detailContent.length > 0) {
                area_contact_info_pop.detailContent = detailContent.html();
                detailContent.remove();
            }
            area_contact_info_pop.infoInit.initArea();
        },
        initArea: function(area) {
            if(area==null){
                area=0;
            }
            common.getByAjax(true, "get", "json", "/orderCenter/nationwide/areaContactInfo/allArea", {},
                function(data) {
                    if (data) {
                        var options = "<option value='0'>请选择</option>";
                        $.each(data, function(i, model){
                            options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                        });
                        parent.$("#select_area").append(options);
                        parent.$("#select_area").val(area);
                    }
                },
                function() {  popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);}
            );
        },

        initAddress:function(parent,value,obj,level){
            //alert("parent=="+parent+",value=="+value+",obj=="+obj+",level="+level);
            if(level!=1&&parent<=0){
                return;
            }
            if(value==null){
                value=0;
            }
            var href="";
            if(level==1){
                href="/orderCenter/resource/provinces";
            }else if(level==2){
                href= "/orderCenter/resource/"+parent+"/cities";
            }else{
                href="/orderCenter/resource/"+parent+"/districts"
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
                        obj.val(value);
                        obj.show();
                    }
                },
                function() { popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);}
            );
        }
    },
    infoDetail: {
        popup: function (id) {
            this.findOne(id, function (data) {
                area_contact_info_pop.infoDetail.detail();
                area_contact_info_pop.infoDetail.fillContent(data,"text");
                parent.$(".toEdit").unbind("click").bind({
                    click: function () {
                        if (!common.permission.validUserPermissionForSecondPup("or07020102")) {
                            return;
                        }
                        area_contact_info_pop.infoDetail.edit(data);
                    }
                });
                parent.$(".toCancel").unbind("click").bind({
                    click: function () {
                        area_contact_info_pop.infoDetail.cancel(data);
                    }
                });
             });
        },
        detail:function(){
            popup.pop.popInput(false, area_contact_info_pop.detailContent, 'first', "620px", "535px", "33%", "50%");
            parent.$("#site_detail_close").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            parent.$(".toSave").unbind("click").bind({
                click: function () {
                    area_contact_info_pop.infoDetail.save();
                }
            });
            parent.$("#select_area").unbind("change").bind({
                change: function () {
                    //查询此城市的省ID
                    var cityId=$(this).val();
                    common.getByAjax(true, "get", "json", "/orderCenter/resource/"+cityId+"/province", {},function(data) {
                            if (data) {
                                parent.$("#select_province").val(data.id);
                                parent.$("#select_city").val(null).hide();
                                parent.$("#select_district").val(null).hide();
                                if(data.type==1){
                                    area_contact_info_pop.infoInit.initAddress(data.id,cityId,parent.$("#select_city"),2);
                                    area_contact_info_pop.infoInit.initAddress(cityId,null,parent.$("#select_district"),3);
                                }else if(data.type==2){
                                    area_contact_info_pop.infoInit.initAddress(data.id,null,parent.$("#select_city"),2);
                                }
                            }
                        },
                        function() { popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);}
                    );
                }
            });

            parent.$("#select_province").unbind("change").bind({
                change: function () {
                    parent.$("#select_city").val(null).hide();
                    parent.$("#select_district").val(null).hide();
                    area_contact_info_pop.infoInit.initAddress($(this).val(),null,parent.$("#select_city"),2);
                }
            });
            parent.$("#select_city").unbind("change").bind({
                change: function () {
                    area_contact_info_pop.infoInit.initAddress($(this).val(),null,parent.$("#select_district"),3);
                }
            });
        },
        findOne: function (id,callBackMethod) {
            common.getByAjax(true, "get", "json", "/orderCenter/nationwide/areaContactInfo/" + id, {},
                function(data) {
                    if (callBackMethod) {
                        callBackMethod(data);
                    }
                },
                function() {
                    popup.mould.popTipsMould(false, "系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                }
            );
        },
        fillContent: function (data, mark) {
            if (mark == "text") {
                parent.$("#detail_area").text(common.checkToEmpty(data.areaName));
                parent.$("#detail_name").text(common.checkToEmpty(data.name));
                parent.$("#detail_mobile").text(common.checkToEmpty(data.mobile));
                parent.$("#detail_email").text(common.checkToEmpty(data.email));
                parent.$("#detail_qq").text(common.checkToEmpty(data.qq));
                parent.$("#detail_address").text(common.checkToEmpty(data.provinceName)+""+common.checkToEmpty(data.cityName)+common.checkToEmpty(data.districtName));
                parent.$("#detail_street").text(common.checkToEmpty(data.street));
                parent.$(".textarea").val(common.checkToEmpty(data.comment)).attr("readonly","readonly");
                parent.$(".text-input").hide().siblings(".text-show").show();
                parent.$(".select").hide().siblings(".text-show").show();
            } else if (mark == "input") {
                if(data==null){
                    parent.$(".text-show").hide().siblings(".text-input").show();
                    return;
                }
                parent.$("#id").val(common.checkToEmpty(data.id));
                parent.$("#select_area").val(common.checkToEmpty(data.area));
                parent.$("#input_name").val(common.checkToEmpty(data.name));
                parent.$("#input_mobile").val(common.checkToEmpty(data.mobile));
                parent.$("#input_email").val(common.checkToEmpty(data.email));
                parent.$("#input_qq").val(common.checkToEmpty(data.qq));
                parent.$("#input_street").val(common.checkToEmpty(data.street));
                parent.$("#input_comment").val(common.checkToEmpty(data.comment));
                parent.$(".textarea").val(common.checkToEmpty(data.comment)).removeAttr("readonly");
                parent.$(".text-show").hide().siblings(".text-input").show();
            }
        },
        create:function(){
            area_contact_info_pop.infoDetail.detail();
            parent.$(".toCancel").unbind("click").bind({
                click: function () {
                    popup.mask.hideFirstMask(false);
                }
            });
            parent.$("#toEdit").hide().siblings("#toSave").show();
            area_contact_info_pop.infoInit.initArea();
            area_contact_info_pop.infoInit.initAddress(0,null,parent.$("#select_province"),1);
            area_contact_info_pop.infoDetail.fillContent(null,"input");
        },
        edit: function (data) {
            area_contact_info_pop.infoInit.initArea(data.area);
            area_contact_info_pop.infoInit.initAddress(0,data.province,parent.$("#select_province"),1);
            area_contact_info_pop.infoInit.initAddress(data.province,data.city,parent.$("#select_city"),2);
            area_contact_info_pop.infoInit.initAddress(data.city,data.district,parent.$("#select_district"),3);
            this.fillContent(data,"input");
            parent.$("#toEdit").hide().siblings("#toSave").show();
        },
        save: function () {
            if(!area_contact_info_pop.infoDetail.validate()){
                return;
            }
            parent.$("#toSave").hide();
            var url = "/orderCenter/nationwide/areaContactInfo/update";
            var id = $("#id").val();
            if(common.isEmpty(id)){
                url = "/orderCenter/nationwide/areaContactInfo/save";
            }
            common.getByAjax(true, "put", "json", url, parent.$("#area_contact_info_form").serialize(),
                function(data){
                    if (data.pass) {
                        area_contact_info.listInfo.currentPage = 1;
                        area_contact_info.listInfo.keyword = $("#keyword").val();
                        area_contact_info.listInfo.list();
                        popup.mould.popTipsMould(false,"保存成功！",popup.mould.first, popup.mould.success, "", "59%", null);
                    }else{
                        popup.mould.popTipsMould(false,data.message,popup.mould.first, popup.mould.error, "", "59%", null);
                    }
                },function(){
                    common.showSecondTips("系统异常");
                }
            );
            parent.$("#toSave").hide().siblings("#toEdit").show();
        },
        cancel: function (data) {
            parent.$(".text-show").hide().siblings(".text-input").show();
            parent.$("#toSave").hide().siblings("#toEdit").show();
            area_contact_info_pop.infoDetail.fillContent(data,"text");
        },
        validate:function(){
            if(parent.$("#select_area").val()<=0){
                area_contact_info_pop.infoDetail.error("请选择城市");
                return false;
            }
            if(common.isEmpty(parent.$("#input_name").val())){
                area_contact_info_pop.infoDetail.error("姓名不能为空！");
                return false;
            }
            if(!common.validateName(parent.$("#input_name").val())){
                area_contact_info_pop.infoDetail.error("姓名格式错误！");
                return false;
            }
            if(common.isEmpty(parent.$("#input_mobile").val())){
                area_contact_info_pop.infoDetail.error("电话不能为空！");
                return false;
            }
            if(!common.isMobile(parent.$("#input_mobile").val())){
                area_contact_info_pop.infoDetail.error("电话号码格式错误！");
                return false;
            }
            if(!common.isEmpty(parent.$("#input_email").val())&&!common.isEmail(parent.$("#input_email").val())){
                area_contact_info_pop.infoDetail.error("邮箱格式错误！");
                return false;
            }
            if(!common.isEmpty(parent.$("#input_qq").val())&&!common.validations.isQQ(parent.$("#input_qq").val())){
                area_contact_info_pop.infoDetail.error("QQ号格式错误！");
                return false;
            }
            if(parent.$("#select_province").val()==0||parent.$("#select_city").val()==0||parent.$("#select_district").val()==0){
                area_contact_info_pop.infoDetail.error("请正确选择地址！");
                return false;
            }
            if(common.isEmpty(parent.$("#input_street").val())){
                area_contact_info_pop.infoDetail.error("地址不能为空！");
                return false;
            }
            if(!common.isEmpty(parent.$("#input_street").val())&&common.getByteLength(parent.$("#input_street").val())>30){
                area_contact_info_pop.infoDetail.error("地址长度不能超过30字符！");
                return false;
            }

            if(!common.isEmpty(parent.$("#input_comment").val())&&common.getByteLength(parent.$("#input_comment").val())>200){
                area_contact_info_pop.infoDetail.error("备注信息不能超过200字符！");
                return false;
            }
            return true;
        },
        error:function(msg){
            parent.$("#errorText").html(msg);
            parent.$(".error-msg").show().delay(2000).hide(0);
        }
    }
}

$(function () {
    area_contact_info.listInfo.list();
    area_contact_info_pop.infoInit.init();
    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould(false, "请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            area_contact_info.page.currentPage = 1;
            area_contact_info.page.keyword = keyword;
            area_contact_info.listInfo.list();
        }
    });
    $("#newInsuranceBtn").bind({
        click: function () {
            if (!common.permission.validUserPermission("or07020101")) {
                return;
            }
            area_contact_info_pop.infoDetail.create();
        }
    });
});
