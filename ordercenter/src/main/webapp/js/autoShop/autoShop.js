/**
 * Created by wangfei on 2015/5/30.
 */
/*初始化参数*/
var serviceItemIndex = 8;//服务项id
var uploadCount = 0;//图片数量
var brandArray = new Array();//brand列表
var deleteImgArray = new Array();//编辑删除图片id列表
var imgIndex = 0;//图片提交index
var brandIndex = 0;//品牌提交index

/* init bootstrap switch */
$.fn.bootstrapSwitch.defaults.onText = '是';
$.fn.bootstrapSwitch.defaults.offText = '否';
$.fn.bootstrapSwitch.defaults.size = "small";
$.fn.bootstrapSwitch.defaults.state = false;

var AutoShop = function() {
    /* 增加 */
    this.save = function() {
        $("#btn-group").find(".btn").attr("disabled", true);
        common.getByAjax(true, "get", "json", "/orderCenter/autoShop/add", $("#inputForm").serialize(),
            function(data) {
                if (data.result == "success") {
                    common.showTips("保存成功");
                } else {
                    common.showTips(data.message);
                }
                $("#btn-group").find(".btn").attr("disabled", false);
            },
            function() {
                common.showTips("系统异常");
                $("#btn-group").find(".btn").attr("disabled", false);
            }
        );
    };

    /* 删除 */
    this.delete = function(id, properties) {
        $("#btn-group").find(".btn").attr("disabled", true);
        common.getByAjax(true, "get" ,"json", "/orderCenter/autoShop/delete", {id : id},
            function(data) {
                if (data) {
                    common.showTips("删除成功");
                    autoShop.list(properties);
                    showContent();
                } else {
                    common.showTips("删除失败");
                }

                $("#btn-group").find(".btn").attr("disabled", false);
            },
            function(){
                common.showTips("系统异常");
                $("#btn-group").find(".btn").attr("disabled", false);
            }
        );
    };

    /* 更新 */
    this.update = function(properties) {
        $("#btn-group").find(".btn").attr("disabled", true);
        if (deleteImgArray.length > 0) {
            $("#deleteImgIds").val(deleteImgArray.join(","));
        }
        common.getByAjax(true, "get", "json", "/orderCenter/autoShop/update", $("#inputForm").serialize(),
            function(data) {
                if (data.result == "success") {
                    common.showTips("更新成功");
                    autoShop.list(properties);
                    showContent();
                } else {
                    common.showTips(data.message);
                }
                $("#btn-group").find(".btn").attr("disabled", false);
            },
            function() {
                common.showTips("系统异常");
                $("#btn-group").find(".btn").attr("disabled", false);
            }
        );
    };

    /* 编辑 */
    this.edit = function(id) {
        autoShop.clearForm($("#inputForm"));
        showEdit();
        common.getByAjax(true, "get", "json", "/orderCenter/autoShop/findOne", {id:id},
            function(data){
                if(data == null){
                    common.showTips("获取4S店信息失败");
                    return false;
                }

                $("#name").val(data.name);
                $("#contactPerson").val(data.contactPerson);
                $("#contactPersonMobile").val(data.contactPersonMobile);
                $("#contactPersonPhone_zone").val(data.contactPersonPhone_zone)
                $("#contactPersonPhone_local").val(data.contactPersonPhone_local);
                if (data.province) {
                    $("#provinceSel").val(data.province);
                }
                if (data.city) {
                    $("#citySel").val(data.city);
                }
                $("#address").val(common.checkToEmpty(data.address));
                $("#longitude").val(common.checkToEmpty(data.longitude));
                $("#latitude").val(common.checkToEmpty(data.latitude));
                $("#comments").val(common.checkToEmpty(data.comments));
                $("#autoShopId").val(data.id);

                //4s店图片
                var pictures = data.pictures;
                if (pictures) {
                    uploadCount = pictures.length;

                    $.each(pictures, function(n, picture){
                        var inputText = "<input type='hidden' id='picture" + picture.id + "' class='hiddenPicture' name='pictures[" + imgIndex + "].id' value='" + picture.id + "'>";
                        $("#pictureIdHidden").append(inputText);
                        imgIndex ++;

                        var originalName = picture.originalName;
                        var aText = "<a style='text-decoration:none;' class='list-a' href='javascript:;' onclick='operation.recordDeleteImg(" + picture.id + ",this);' title='" + originalName + "'>";
                        if (originalName.length > 3) {
                          aText += originalName.substring(0,3) + "..";
                        } else {
                          aText += originalName;
                        }
                        aText += "<i class='glyphicon glyphicon-remove'></i></a>";

                        $("#imgList").append(aText);
                    });
                }

                //保险公司
                $("input:checkbox[name='insuranceCompanyIds']").prop("checked", false);
                if (data.insuranceCompanyIds) {
                    var companyIds = data.insuranceCompanyIds.split(",");
                    for (var index = 0; index < companyIds.length; index++) {
                          $("input:checkbox[value=" + companyIds[index] + "]").prop('checked', true);
                    }
                }

                //服务项
                var items = data.serviceItems;
                serviceItemIndex = items.length;
                $("#inputForm").find(".service-item").remove();
                $.each(items, function(n, item){
                    var itemContent = "";
                    itemContent += "<div class='form-group service-item'>" +
                                      "<div class='col-sm-2 label-top'>" +
                                          "<span class='control-label'>" + item.name + "</span>" +
                                          "<input type='hidden' name='serviceItems[" + n + "].id' value='" + item.id + "'>" +
                                          "<input type='hidden' name='serviceItems[" + n + "].name' value='" + item.name + "'>" +
                                      "</div>" +
                                      "<div class='col-sm-2'>" +
                                          "<input id='isInField' name='serviceItems[" + n + "].disabled' class='switch' type='checkbox'" + (item.disabled ? "" : "checked") + ">" +
                                      "</div>" +
                                      "<div class='col-sm-3'>" +
                                          "<input type='text' class='text_input comments' id='isInFieldComments' name='serviceItems[" + n + "].comments' value='" + common.checkToEmpty(item.comments) + "'>" +
                                      "</div>" +
                                      "<input type='hidden' class='text_input comments' name='serviceItems[" + n + "].serviceKind' value='" + item.serviceKind + "'>";
                    if (item.serviceKind == "2") {
                        itemContent += "<div class='col-sm-1 label-top' style='padding-left: 0px;'>" +
                                            "<i class='glyphicon glyphicon-minus-sign'></i><a class='removeItem' href='javascript:;' onclick='operation.removeServiceItem(this);'><strong>删除</strong></a>" +
                                        "</div>";
                    }
                    itemContent += "</div>";

                    $(itemContent).insertBefore($('#endItem'));
                    $("input[name='serviceItems[" + n +"].disabled']").bootstrapSwitch();
                    $("input[name='serviceItems[" + n +"].disabled']").on('switchChange.bootstrapSwitch', function (e, state) {
                        $(this).val(state);
                    });
                });

                //品牌列表
                var brands = data.brands;
                if (brands) {
                    $.each(brands, function(index, brand){
                        brandArray.push(brand.id);
                        var aText = "<a style='text-decoration:none;font-size:12px;' class='list-a' href='javascript:;' onclick=operation.removeBrand('" + brand.id + "',this); title='" + brand.name + "'>";
                        if (brand.name.length > 2) {
                            aText += brand.name.substring(0,2) + "..";
                        } else {
                            aText += brand.name;
                        }
                        aText += "<i class='glyphicon glyphicon-remove'></i></a>";

                        $("#brandList").append(aText);

                        var inputText = "<input type='hidden' id='brand" + brand.id + "' name='brands[" + brandIndex + "].id' value='" + brand.id + "'>";
                        $("#brandIdHidden").append(inputText);
                        brandIndex ++;
                    });
                }
            },function(){
                common.showTips("系统异常");
            }
        );
    };

    /* 列表查询 */
    this.list = function(properties) {
        common.getByAjax(true, "get", "json", "/orderCenter/autoShop/list",
            {
              currentPage : properties.currentPage,
              pageSize : properties.pageSize,
              keyword : properties.keyword
            },
            function(data){
                $("#autoShop_tab tbody").empty();

                if (data == null || data.pageInfo.totalElements < 1) {
                    $("#totalCount").text("0");
                    return false;
                }

                $("#page_up_down").show();

                if (properties.currentPage < 2) {
                    $("#page_up_down").find("#pageUp").hide();
                } else {
                    $("#page_up_down").find("#pageUp").show();
                }

                if (properties.currentPage >= data.pageInfo.totalPage) {
                    $("#page_up_down").find("#pageDown").hide();
                } else {
                    $("#page_up_down").find("#pageDown").show();
                }

                if (data.pageInfo.totalElements <= properties.pageSize) {
                    $("#page_up_down").find("#pageDown").hide();
                    $("#page_up_down").find("#pageDown").hide();
                }

                var content = "";
                $.each(data.viewList, function(n, model) {
                    content += "<tr class='text-center'>" +
                                  "<td>" + model.name + "</td>" +
                                  "<td>" + model.fullAddress + "</td>" +
                                  "<td>" + model.strBrands + "</td>" +
                                  "<td>" + model.contactPerson + "</td>" +
                                  "<td>" + model.contactPersonPhone + "</td>" +
                                  "<td>" + model.contactPersonMobile + "</td>" +
                                  "<td>" + model.createTime + "</td>" +
                                  "<td>" + model.updateTime + "</td>" +
                                  "<td>" + model.operator + "</td>" +
                                  "<td><a href='javascript:;' onclick='autoShop.edit(" + model.id + ");'>编辑</a></td>" +
                               "</tr>";
                });

                $("#totalCount").text(data.pageInfo.totalElements);
                $("#autoShop_tab tbody").append(content);
            },
            function() {
                common.showTips("获取4S店列表失败");
            }
        );
    };

    /* 清空form */
    this.clearForm = function(form) {
        form[0].reset();
        //清空品牌列表
        $("#brandList a").each(function() {
            $(this).click();
        });

        //清空照片
        $("#imgList a").each(function() {
            $(this).remove();
        });
        $(".hiddenPicture").remove();

        //清空新增服务项
        $(".removeItem").each(function() {
            $(this).click();
        });

        //恢复数据
        serviceItemIndex = 8;
        uploadCount = 0;
        brandArray.length = 0;
        deleteImgArray.length = 0;
        imgIndex = 0;
        brandIndex = 0;
        $("#deleteImgIds").val("");
    };



}

AutoShop.prototype = new Action();
var autoShop = new AutoShop();

var operation = {
    initCompanies : function() {
        common.getByAjax(true, "get", "json", "/orderCenter/resource/insuranceCompany/getAllCompanies", null,
            function(data){
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function(i, model) {
                    if (i != 0 && parseInt(i%9) == 0) {
                        options += "<br/><label class='checkbox-inline' style='padding-top: 0px;padding-left: 157px;'>" +
                                    "<input type='checkbox' name='insuranceCompanyIds' value='" + model.id + "'> " + model.name +
                                    "</label>";
                    } else {
                        options += "<label class='checkbox-inline' style='padding-top: 0px;'>" +
                                    "<input type='checkbox' name='insuranceCompanyIds' value='" + model.id + "'> " + model.name +
                                    "</label>";
                    }
                });
                $("#insuranceCompanyList").append(options);
            },
            function(){}
        );
    },
    initBrands : function(){
        common.getByAjax(true, "get", "json", "/orderCenter/resource/autoHomeBrand/getAllBrands", null,
            function(data){
                if (data == null) {
                    return false;
                }

                var options = "";
                $.each(data, function(i, model) {
                    options += "<option value='" + model.id + "'>" + model.name + "</option>";
                });
                $("#brandSel").append(options);
            },
            function(){}
        );
    },
    addBrand : function(obj){
        var brandId = $(obj).find("option:selected").val();
        var brandText = $(obj).find("option:selected").text();
        if (brandId == "") {
            return;
        }

        var isExists = false;
        for (var index = 0; index < brandArray.length; index++) {
            if (brandArray[index] == brandId) {
                isExists = true;
                break;
            }
        }

        if (!isExists) {
            brandArray.push(brandId);
            var aText = "<a style='text-decoration:none;font-size:12px;' class='list-a' href='javascript:;' onclick=operation.removeBrand('" + brandId + "',this); title='" + brandText + "'>";
            if (brandText.length > 2) {
                aText += brandText.substring(0,2) + "..";
            } else {
                aText += brandText;
            }
            aText += "<i class='glyphicon glyphicon-remove'></i></a>";

            $("#brandList").append(aText);

            var inputText = "<input type='hidden' id='brand" + brandId + "' name='brands[" + brandIndex + "].id' value='" + brandId + "'>";
            $("#brandIdHidden").append(inputText);
            brandIndex ++;
        }
    },
    showServiceItemTips : function() {
        var content = "<div class='theme_poptit'>" +
                       "<h3>温馨提示</h3>" +
                       "</div>" +
                       "<div style='padding-top: 40px; padding-bottom: 30px; text-align: center; font-size: 16px;'>" +
                          "<label class='tipsContent'>新服务项名称：</label>" +
                          "<input type='text' id='textInput' name='textInput'></br>" +
                          "<div id='warnMsgDiv' style='padding-left: 82px;padding-top: 5px;display: none;'>" +
                              "<span class='text-danger'><i class='glyphicon glyphicon-warning-sign'></i>&nbsp;<span class='warnMsg'>请填写新服务项名称</span></span>" +
                          "</div>" +
                          "<div class='checkbox'>" +
                              "<label><input name='needComments' type='checkbox' checked disabled> 需要备注</label>" +
                          "</div>" +
                       "</div>" +
                       "<div style='text-align: center;'>" +
                          "<div class='btn-group' style='width: 80px;margin-left: 37px;'>" +
                              "<button style='font-size: 16px;' type='button' class='btn btn-primary confirm'>确定</button>" +
                          "</div>" +
                          "<div class='btn-group' style='width: 80px;'>" +
                              "<button style='font-size: 16px;' type='button' class='btn btn-default cancel'>取消</button>" +
                          "</div>" +
                       "</div>";
        common.showUserDefinedTips(content, "310px", "450px");

        window.parent.$("#theme_popover_userDefined").find(".confirm").unbind("click").bind({
            click : function(){
                var inputVal = window.parent.$("#theme_popover_userDefined").find("#textInput").val();
                var needComments = window.parent.$("#theme_popover_userDefined").find("input[name='needComments']:checked").val();

                if(common.isEmpty(window.parent.$("#theme_popover_userDefined").find("#textInput").val())){
                    window.parent.$("#theme_popover_userDefined").find("#warnMsgDiv").show();
                    return false;
                }
                window.parent.$("#theme_popover_userDefined").find("#warnMsgDiv").hide();
                operation.addServiceItem(inputVal, needComments);
                common.hideMask();
            }
        });

        window.parent.$("#theme_popover_userDefined").find(".cancel").unbind("click").bind({
            click : function(){
                common.hideMask();
            }
        });
    },
    addServiceItem : function(inputVal, needComments) {
        var item = "<div class='form-group'>" +
                      "<div class='col-sm-2 label-top'>" +
                          "<span class='control-label'>" + inputVal + "</span>" +
                          "<input type='hidden' name='serviceItems[" + serviceItemIndex +"].name' value='" + inputVal + "'>" +
                      "</div>" +
                      "<div class='col-sm-2'>" +
                          "<input class='switch' name='serviceItems[" + serviceItemIndex +"].disabled' type='checkbox' checked>" +
                      "</div>";
        if (needComments == "on") {
            item += "<div class='col-sm-3'>" +
                        "<input type='text' class='text_input comments' name='serviceItems[" + serviceItemIndex +"].comments'>" +
                      "</div>";
        } else {
            item += "<div class='col-sm-3'></div>";
        }
        item += "<input type='hidden' class='text_input comments' name='serviceItems[" + serviceItemIndex + "].serviceKind' value='2'>" +
                "<div class='col-sm-1 label-top' style='padding-left: 0px;'>" +
                    "<i class='glyphicon glyphicon-minus-sign'></i><a class='removeItem' href='javascript:;' onclick='operation.removeServiceItem(this);'><strong>删除</strong></a>" +
                "</div>" +
                "</div>";

        $(item).insertBefore($('#endItem'));
        $("input[name='serviceItems[" + serviceItemIndex +"].disabled']").bootstrapSwitch();
        $("input[name='serviceItems[" + serviceItemIndex +"].disabled']").on('switchChange.bootstrapSwitch', function (e, state) {
            $(this).val(state);
        });
      serviceItemIndex ++;
    },
    removeServiceItem : function(obj){
        $(obj).parent().parent().remove();
    },
    showUploadTips : function(){
        var content =  "<input type='file' name='uploadify' id='uploadify'>" +
                        "<div id='fileQueue' class='uploadify-queue'></div>" +
                        "<p style='padding-left: 48px;'>" +
                            "<a class='btn btn-primary' href=javascript:$('#uploadify').uploadify('upload','*');>批量上传</a>" +
                            "<a style='margin-left: 10px;' class='btn btn-default' href=javascript:$('#uploadify').uploadify('cancel','*');>取消上传</a>" +
                            "<a class='shutDown btn btn-default' style='margin-left: 223px;'>关闭</a>" +
                        "</p>";
        common.showUserDefinedTips(content, "420px", "560px");
        window.parent.$("#theme_popover_userDefined").find(".shutDown").unbind("click").bind({
            click : function() {
                common.hideMask();
            }
        });
    },
    removeImg : function(index, obj) {
        common.getByAjax(true, "get", "json", "/orderCenter/autoShop/removePicture", {pictureId : index},
            function(data) {
                if (data) {
                    $(obj).remove();
                    $("#picture" + index).remove();
                    uploadCount --;
                } else {
                    common.showTips("删除图片失败");
                }
            },
            function() {
                common.showTips("系统异常");
            }
        );
    },
    recordDeleteImg : function(index, obj) {
        deleteImgArray.push(index);
        $(obj).remove();
        $("#picture" + index).remove();
        uploadCount --;
    },
    removeBrand : function(index, obj){
        $(obj).remove();
        brandArray.remove(index);
        $("#brand" + index).remove();
    },
    onSelectError : function(file, errorCode, errorMsg){
        var msg = "";
        switch (errorCode) {
            case -100 :
                msg = "上传的文件数量已经超出系统限制的" + window.parent.$('#uploadify').uploadify('settings', 'uploadLimit') + "个文件！";
                break;
            case -110 :
                msg = "文件 [" + file.name + "] 大小超出系统限制的" + window.parent.$('#uploadify').uploadify('settings', 'fileSizeLimit') + "大小！";
                break;
            case -120 :
                msg += "上传文件大小不可为0！";
                break;
            case -130 :
                msg += "文件格式不正确，仅限 " + window.parent.$('#uploadify').uploadify('settings', 'fileTypeExts');
                break;
            default:
                msg += "错误代码：" + errorCode + "\n" + errorMsg;
        }

        common.showSecondTips(msg);
    },
    onUploadSuccess : function(file, data, response){
        uploadCount ++;
        var inputText = "<input type='hidden' id='picture" + data + "' class='hiddenPicture' name='pictures[" + imgIndex + "].id' value='" + data + "'>";
        $("#pictureIdHidden").append(inputText);
        imgIndex ++;
        var aText = "";
        if ($("#autoShopId").val()) {
            aText += "<a style='text-decoration:none;font-size:12px;' class='list-a' href='javascript:;' onclick='operation.recordDeleteImg(" + data + ",this);' title='" + file.name + "'>";
        } else {
            aText += "<a style='text-decoration:none;font-size:12px;' class='list-a' href='javascript:;' onclick='operation.removeImg(" + data + ",this);' title='" + file.name + "'>";
        }
        if (file.name.length > 3) {
            aText += file.name.substring(0,3) + "..";
        } else {
            aText += file.name;
        }
        aText += "<i class='glyphicon glyphicon-remove'></i></a>";

        $("#imgList").append(aText);
    },
    onUploadStart : function(file){
        if (uploadCount >= window.parent.$('#uploadify').uploadify('settings', 'uploadLimit')) {
            common.showSecondTips("上传的文件数量已经超出系统限制的" + window.parent.$('#uploadify').uploadify('settings', 'uploadLimit') + "个文件！");
            window.parent.$("#uploadify").uploadify("stop");
        }

        /*common.getByAjax(false, "get", "json", "/orderCenter/autoShop/isExists", {fileName : file.name},
         function(data){
         if (data) {
         common.showSecondTips("文件’" + file.name + "’已经存在，请重命名后重新上传！");
         window.parent.$("#uploadify").uploadify("stop");
         }
         }
         )*/
    },
    onFallback : function(){
        common.showSecondTips("您未安装FLASH控件，无法上传图片！请安装FLASH控件后再试。");
    }
}

