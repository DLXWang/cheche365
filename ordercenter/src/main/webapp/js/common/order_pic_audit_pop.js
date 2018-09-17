/**
 * Created by xu.yelong on 2016/3/2.
 */

var param = {
    rot: 0,
    height: 425,
    width: 675,
    top: 0,
    left: 0,
    ulLeft: 0,
    audit: 0,
    pass: 0,
    nopass: 0,
    imgId: 0,
    imgTypeId: 0,
    orderId: 0,
    imgTypes: ["9", "11"],
    hint:0,
};
var parent = window.parent;
var validateCar = {
    imgType: [[1, 2, 3, 4], [6, 7, 8, 9, 10, 11], [5]],
    imgCount: 0,
    pop: function (orderId, callback) {
        $.post("../order/order_pic_audit_pop.html", {}, function (content) {

            validateCar.operation.getPics(orderId, function (data) {
                popup.pop.popInput(false, content, popup.mould.first, "920px", "610px", "30%", "43%");
                validateCar.operation.initUploadContent();
                //给表单orderId赋值
                param.orderId = common.getUrlParam("id");
                parent.$("#orderId").val(param.orderId);
                validateCar.operation.showImg(data, validateCar.imgType[0]);
                parent.$(".theme_poptit .close").unbind("click").bind({
                    click: function () {
                        popup.mask.hideFirstMask(false);
                        param.ulLeft = 0;
                        parent.$("#box").animate({left: param.ulLeft + "px"}, 500);
                        window.location.reload();
                    }
                });
                $("#auto_detail_close").unbind("click").bind({
                    click : function() {
                        if(param.hint != 0){
                            alert("请输入照片审核不通过原因");
                        }else{
                            popup.mask.hideAllMask(false);
                        }
                    }
                });
                parent.$("#myTab li a").unbind("click").bind({
                    click: function () {
                        var index = $(this).parent().index();
                        if (index == 3) {
                            parent.$("#message_form").show().prev("div").hide();
                        } else {
                            parent.$("#message_form").hide().prev("div").show();
                            validateCar.operation.getPics(orderId, function (data) {
                                validateCar.operation.showImg(data, validateCar.imgType[index]);
                            });
                        }
                        param.ulLeft = 0;
                        parent.$("#box").find(".active-gray").removeClass("active-gray");
                        parent.$("#box").find("li:first").addClass("active-gray");
                        parent.$("#box").animate({left: param.ulLeft + "px"}, 500);
                        parent.$("#cropper").attr("src", parent.$("#box").find("li:first").find("img:last").attr("src"));
                    }
                });
                parent.$(".enable").unbind("click").bind({
                    click: function () {
                        var id = parent.$("#box").find(".active-gray").find("a").attr("id");
                        var hasImg = parent.$("#box").find(".active-gray").find("a").attr("status");
                        var currentStatus = parent.$("#box").find(".active-gray").find("a").attr("status");
                        if (common.validations.isEmpty(id) || id == "null" || hasImg == 0) {
                            popup.mould.popTipsMould(false, "请先上传照片!", popup.mould.second, popup.mould.error, "", "57%", null);
                            return;
                        }
                        if (hasImg == 2) {
                            popup.mould.popTipsMould(false, "当前已经是审核通过状态!", popup.mould.second, popup.mould.error, "", "57%", null);
                            return;
                        }
                        if (validateCar.operation.review(true, id)) {
                            if (currentStatus == 3) {
                                param.pass++;
                                if (param.nopass > 0) {
                                    param.nopass--;
                                }
                            }
                            if (currentStatus == 1) {
                                param.pass++;
                                if (param.audit > 0) {
                                    param.audit--;
                                }
                            }
                            validateCar.operation.setAuditStatus();
                        }
                        $.ajax({
                            async: false,
                            type: "GET",
                            dataType: "json",
                            url: "/orderCenter/quote/photo/setLog",
                            data: {id:id,status:currentStatus,hint:""},
                            success: function (data) {

                            },
                            error: function () {

                            }
                        });
                    }
                });
                parent.$(".disable").unbind("click").bind({
                    click: function () {
                        param.hint = 1;
                        var id = parent.$("#box").find(".active-gray").find("a").attr("id");
                        var hasImg = parent.$("#box").find(".active-gray").find("a").attr("status");
                        var currentStatus = parent.$("#box").find(".active-gray").find("a").attr("status");
                        if (common.validations.isEmpty(id) || id == "null" || hasImg == 0) {
                            param.hint = 0;
                            popup.mould.popTipsMould(false, "请先上传照片!", popup.mould.second, popup.mould.error, "", "57%", null);
                            return;
                        }
                        parent.$("#disable_content .btn").hide();
                        $.ajax({
                            async: false,
                            type: "GET",
                            dataType: "json",
                            url: "/orderCenter/quote/photo/hint",
                            data: {id:id},
                            success: function (data) {
                                if(data.hint != "k"){
                                    param.hint = 0;
                                    parent.$("#showhint").html("不通过审核的原因: "+data.hint);
                                    parent.$("#showhint").show();
                                    parent.$("#reason").show();
                                    parent.$("#rselect2").hide();

                                }else{
                                    parent.$("#reason").show();
                                    parent.$("#rselect2").hide();
                                }
                            },
                            error: function () {
                                popup.mould.popTipsMould(false, "当前已经是审核未通过状态!", popup.mould.second, popup.mould.error, "", "57%", null);
                                if (param.popupLevel == 'second') {
                                    popup.mask.hideSecondMask(false);
                                } else {
                                    popup.mask.hideFirstMask(false);
                                }
                            }
                        });
                        var hint;
                        var sel=document.getElementById("rselect1");
                        sel.onchange=function(){
                            if(sel.options[sel.selectedIndex].value == 5){
                                parent.$("#rselect2").show();
                            }else {
                                parent.$("#rselect2").hide();
                            }
                        }
                        parent.$(".answerr").unbind("click").bind({
                            click: function () {
                                var a =parent.$("#rselect1").find("option:selected").text();
                                var b =parent.$("#rselect1").val();
                                if(b == 5){
                                    a =parent.$("#rselect2").val();
                                    if(!a){
                                        popup.mould.popTipsMould(false, "输入原因不得为空！", popup.mould.second, popup.mould.error, "", "57%", null);
                                        return
                                    }
                                }
                                $.ajax({
                                    async: false,
                                    type: "GET",
                                    dataType: "json",
                                    url: "/orderCenter/quote/photo/setHint",
                                    data: {id:id,hint:a},
                                    success: function (data) {
                                        param.hint = 0;
                                        hint = a;
                                        $.ajax({
                                            async: false,
                                            type: "GET",
                                            dataType: "json",
                                            url: "/orderCenter/quote/photo/setLog",
                                            data: {id:id,status:currentStatus,hint:hint},
                                            success: function (data) {

                                            },
                                            error: function () {

                                            }
                                        });
                                        popup.mould.popTipsMould(false, "保存成功！", popup.mould.second, popup.mould.success, "", "57%", null);
                                    },
                                    error: function () {
                                        param.hint = 0;
                                        popup.mould.popTipsMould(false, "保存失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                                        if (param.popupLevel == 'second') {
                                            popup.mask.hideSecondMask(false);
                                        } else {
                                            popup.mask.hideFirstMask(false);
                                        }
                                    }
                                });
                            }
                        });
                        /*if (hasImg == 3) {
                            popup.mould.popTipsMould(false, "当前已经是审核未通过状态!", popup.mould.second, popup.mould.error, "", "57%", null);
                            return;
                        }*/
                        if (validateCar.operation.review(false, id)) {
                            if (currentStatus == 2) {
                                if (param.pass > 0) {
                                    param.pass--;
                                }
                                param.nopass++;
                            }
                            if (currentStatus == 1) {
                                if (param.audit > 0) {
                                    param.audit--;
                                }
                                param.nopass++;
                            }
                            validateCar.operation.setAuditStatus();
                        }

                    }
                });
                parent.$(".reset").unbind("click").bind({
                    click: function () {
                        parent.$(".result").hide();
                        parent.$(".audit").show();
                    }
                });
                parent.$(".upload").unbind("click").bind({
                    click: function () {
                        popup.pop.popInput(false, validateCar.uploadContent, popup.mould.second, "448px", "180px", "50%", "50%");
                        parent.$(".upload_div .close").unbind("click").bind({
                            click: function () {
                                popup.mask.hideSecondMask(true);
                            }
                        });
                        parent.$("#imgId").val(param.imgId);
                        parent.$("#imgTypeId").val(param.imgTypeId);
                        parent.$("#orderId").val(param.orderId);
                    }
                });
            });
        });
    },
    operation: {
        initUploadContent: function () {
            var uploadContent = $("#uploadImg");
            if (uploadContent.length > 0) {
                validateCar.uploadContent = uploadContent.html();
                uploadContent.remove();
            }
        },
        getPics: function (orderId, callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/order/image/list/" + orderId, {},
                function (data) {
                    callback(data);
                },
                function () {
                    popup.mould.popTipsMould(false, "获取订单记录异常！", popup.mould.first, popup.mould.error, "", "58%", null);
                }
            );
        },
        showImg: function (data, typeArray) {

            parent.$("#left_btn").hide();
            parent.$("#right_btn").hide();
            if (data) {

                validateCar.imgCount = 0;
                var html = "";
                validateCar.index = 0;
                $.each(data, function (index, viewModel) {
                    var imageType = viewModel.purchaseOrderImageType;
                    var subTypeList = viewModel.subTypeList;

                    $.each(subTypeList, function (index, imgTypeModel) {
                        var subImageType = imgTypeModel.purchaseOrderImageType;
                        var img = imgTypeModel.purchaseOrderImage;
                        var imageTypeId = subImageType.id;

                        var imgUrl;
                        if (common.validations.isEmpty(img.url)) {
                            imgUrl = "../../images/wutupian.jpg";
                        } else {
                            imgUrl = img.url;
                        }
                        var statusPic = "";
                        if (img.status == 0) {
                            statusPic = "<img src=" + imgUrl + " width='106' height='60' style='float:left;position: absolute;opacity:0.7'>";
                        } else if (img.status == 2) {
                            statusPic = "<img src='../../images/true.png' width='106' height='60' style='float:left;position: absolute;opacity:0.7'>";
                        } else if (img.status == 3) {
                            statusPic = "<img src='../../images/false.png' width='106' height='60' style='float:left;position: absolute;opacity:0.7'>";
                        }
                        html += "<li name=" + img.expireDate + " style='float: left;margin-right:10px;width:110px;height:80px;'";
                        if (param.imgTypeId == subImageType.id) {
                            html += "class='active-gray'";

                            //判断是否隐藏日期控件
                            //如果是行驶证或身份证就显示时间框,如果暂无照片就不显示
                            if ($.inArray(subImageType.id.toString(), param.imgTypes) != -1) {
                                if (!common.validations.isEmpty(img.id) && img.id != "null" && img.status != 0) {
                                    parent.$("#expireTimeDiv").show();
                                    var expireData = img.expireDate;
                                    if (expireData == "null") {
                                        parent.$("#expireTimeInput").val(null);
                                    } else {
                                        parent.$("#expireTimeInput").val(expireData);
                                    }
                                } else {
                                    parent.$("#expireTimeDiv").hide();
                                    parent.$("#expireTimeInput").val(null);
                                }
                            } else {
                                parent.$("#expireTimeDiv").hide();
                                parent.$("#expireTimeInput").val(null);
                            }

                            parent.$("#cropper").attr("src", imgUrl);
                            param.imgId = img.id;
                            param.imgTypeId = subImageType.id;
                            validateCar.index = validateCar.imgCount;

                        }
                        html += "><a href='javascript:void(0);' class='img-control' id='" + img.id + "' type='" + subImageType.id + "' name='" + subImageType.name + "' status='" + img.status + "'>";
                        var imageText = "<img class='pic' src=" + imgUrl + " width='106' height='60'></a>";
                        html += statusPic + imageText +
                            "<p style='text-align: center;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;font-size:12px;'>" + subImageType.name + "</p></li>";
                        validateCar.imgCount++;
                        if (img.status == 1) {
                            param.audit++;
                        } else if (img.status == 2) {
                            param.pass++;
                        } else if (img.status == 3) {
                            param.nopass++;
                        }
                    });

                });

                validateCar.operation.showRightBtn();
                parent.$("#left_btn").show();
                param.ulLeft = param.ulLeft - (validateCar.index * 120);
                parent.$("#box").animate({left: param.ulLeft + "px"}, 500);
                if (validateCar.index + 1 == validateCar.imgCount) {
                    parent.$("#right_btn").hide();
                }
                if (validateCar.index == 0) {
                    parent.$("#left_btn").hide();
                }

                parent.$("#box ul").html(html);

                validateCar.operation.setAuditStatus();
            }
            parent.$("#box").css("width", 130 * validateCar.imgCount);
            parent.$("#right_btn").unbind("click").bind({
                click: function () {
                    if(param.hint != 0){
                        alert("请输入照片审核不通过原因");
                    }else {
                        validateCar.operation.right();
                    }

                }
            });
            parent.$("#left_btn").unbind("click").bind({
                click: function () {
                    if(param.hint != 0){
                        alert("请输入照片审核不通过原因");
                    }else {
                        validateCar.operation.left();
                    }
                }
            });
            parent.$(".img-bottom .img-control").unbind("click").bind({
                click: function () {
                    if(param.hint != 0){
                        alert("请输入照片审核不通过原因");
                        //popup.mould.popTipsMould(false, "", popup.mould.first, popup.mould.error, "", "58%", null);
                    }else{
                        validateCar.operation.revertImg();
                        $(this).parent().addClass("active-gray").siblings("li").removeClass("active-gray");
                        parent.$("#cropper").attr("src", $(this).find("img:last").attr("src"));
                        parent.$("#picName").html($(this).attr("name"));
                        //parent.$("#status").html(validateCar.operation.getStatus($(this).attr("status")));
                        parent.$("#imgId").val();
                        param.imgId = $(this).attr("id");
                        param.imgTypeId = $(this).attr("type");
                        var status = parent.$("#box").find(".active-gray a").attr("status");
                        //如果是行驶证或身份证就显示时间框,如果暂无照片就不显示
                        if ($.inArray(param.imgTypeId, param.imgTypes) != -1) {
                            if (!common.validations.isEmpty(param.imgId) && param.imgId != "null" && status != 0) {
                                parent.$("#expireTimeDiv").show();
                                var expireData = parent.$("#box").find(".active-gray").attr("name");
                                if (expireData == "null") {
                                    parent.$("#expireTimeInput").val(null);
                                } else {
                                    parent.$("#expireTimeInput").val(expireData);
                                }
                            } else {
                                parent.$("#expireTimeDiv").hide();
                                parent.$("#expireTimeInput").val(null);
                            }
                        } else {
                            parent.$("#expireTimeDiv").hide();
                            parent.$("#expireTimeInput").val(null);
                        }
                        //alert(param.hint);

                        parent.$("#showhint").hide();
                        parent.$("#reason").hide();
                        parent.$("#rselect2").hide();
                        parent.$("#disable_content .btn").show();
                        parent.$("#rselect1").val(1);
                    }
                }
            });
            parent.$("#leftRotate").unbind("click").bind({
                click: function () {
                    param.rot -= 1;
                    if (param.rot <= -1) {
                        param.rot = 11;
                    }
                    parent.$("#cropper").removeClass().addClass("rot" + param.rot);
                }
            });
            parent.$("#rightRotate").unbind("click").bind({
                click: function () {
                    param.rot += 1;
                    if (param.rot >= 11) {
                        param.rot = -1;
                    }
                    parent.$("#cropper").removeClass().addClass("rot" + param.rot);
                }
            });
            parent.$("#big").unbind("click").bind({
                click: function () {
                    var width = parent.$("#item img").css("max-width").substr(0, parent.$("#item img").css("max-width").indexOf("px"));
                    width = parseInt(width) * 1.2;
                    var height = parent.$("#item img").css("max-height").substr(0, parent.$("#item img").css("max-height").indexOf("px"));
                    height = parseInt(height) * 1.2;
                    parent.$("#item img").css("max-width", width);
                    parent.$("#item img").css("max-height", height);
                }
            });
            parent.$("#lit").unbind("click").bind({
                click: function () {
                    var width = parent.$("#item img").css("max-width").substr(0, parent.$("#item img").css("max-width").indexOf("px"));
                    width = parseInt(width) / 1.2;
                    var height = parent.$("#item img").css("max-height").substr(0, parent.$("#item img").css("max-height").indexOf("px"));
                    height = parseInt(height) / 1.2;
                    parent.$("#item img").css("max-width", width);
                    parent.$("#item img").css("max-height", height);
                }
            });
            parent.$("#up").unbind("click").bind({
                click: function () {
                    param.top = param.top + 150;
                    parent.$("#cropper").animate({top: param.top + "px"}, 500);
                }
            });
            parent.$("#down").unbind("click").bind({
                click: function () {
                    param.top = param.top - 150;
                    parent.$("#cropper").animate({top: param.top + "px"}, 500);
                }
            });
            parent.$("#left").unbind("click").bind({
                click: function () {
                    param.left = param.left + 150;
                    parent.$("#cropper").animate({left: param.left + "px"}, 500);
                }
            });
            parent.$("#right").unbind("click").bind({
                click: function () {
                    param.left = param.left - 150;
                    parent.$("#cropper").animate({left: param.left + "px"}, 500);
                }
            });
            parent.$("#reset").unbind("click").bind({
                click: function () {
                    validateCar.operation.revertImg();
                }
            });
        }, revertImg: function () {
            param.left = 0;
            param.top = 0;
            param.rot = 0;
            parent.$("#cropper").removeClass().addClass("rot" + param.rot);
            parent.$("#cropper").animate({left: param.left + "px"}, 10).animate({top: param.top + "px"}, 10);
            parent.$("#item img").css("max-width", 675);
            parent.$("#item img").css("max-height", 425);
        }, left: function () {
            validateCar.operation.revertImg();
            validateCar.operation.showRightBtn();
            if (param.ulLeft + 10 < 0) {
                param.ulLeft = param.ulLeft + 120;
                parent.$("#box").animate({left: param.ulLeft + "px"}, 200);
            }
            var index = parent.$("#box").find(".active-gray").index();
            if (index == 1) {
                parent.$("#left_btn").hide();
            }
            parent.$("#box").find(".active-gray").removeClass("active-gray").prev().addClass("active-gray");
            param.imgId = parent.$("#box").find(".active-gray a").attr("id");
            param.imgTypeId = parent.$("#box").find(".active-gray a").attr("type");

            var status = parent.$("#box").find(".active-gray a").attr("status");
            //如果是行驶证或身份证就显示时间框,如果暂无照片就不显示
            if ($.inArray(param.imgTypeId, param.imgTypes) != -1) {
                if (!common.validations.isEmpty(param.imgId) && param.imgId != "null" && status != 0) {
                    parent.$("#expireTimeDiv").show();
                    var expireData = parent.$("#box").find(".active-gray").attr("name");
                    if (expireData == "null") {
                        parent.$("#expireTimeInput").val(null);
                    } else {
                        parent.$("#expireTimeInput").val(expireData);
                    }
                } else {
                    parent.$("#expireTimeDiv").hide();
                    parent.$("#expireTimeInput").val(null);
                }
            } else {
                parent.$("#expireTimeDiv").hide();
                parent.$("#expireTimeInput").val(null);
            }

            parent.$("#status").html(validateCar.operation.getStatus(parent.$("#box").find(".active-gray a").attr("status")));
            parent.$("#cropper").attr("src", parent.$("#box").find(".active-gray a img:last").attr("src"));
        }, right: function () {
            validateCar.operation.revertImg();
            parent.$("#left_btn").show();
            if (param.ulLeft - 120 > (0 - (validateCar.imgCount - 1) * 120)) {
                param.ulLeft = param.ulLeft - 120;
                parent.$("#box").animate({left: param.ulLeft + "px"}, 200);
            }
            var index = parent.$("#box").find(".active-gray").index();
            if (index == validateCar.imgCount - 2) {
                parent.$("#right_btn").hide();
            }
            parent.$("#box").find(".active-gray").removeClass("active-gray").next().addClass("active-gray");
            param.imgId = parent.$("#box").find(".active-gray a").attr("id");
            param.imgTypeId = parent.$("#box").find(".active-gray a").attr("type");

            var status = parent.$("#box").find(".active-gray a").attr("status");
            //如果是行驶证或身份证就显示时间框,如果暂无照片就不显示
            if ($.inArray(param.imgTypeId, param.imgTypes) != -1) {
                if (!common.validations.isEmpty(param.imgId) && param.imgId != "null" && status != 0) {
                    parent.$("#expireTimeDiv").show();
                    var expireData = parent.$("#box").find(".active-gray").attr("name");
                    if (expireData == "null") {
                        parent.$("#expireTimeInput").val(null);
                    } else {
                        parent.$("#expireTimeInput").val(expireData);
                    }
                } else {
                    parent.$("#expireTimeDiv").hide();
                    parent.$("#expireTimeInput").val(null);
                }
            } else {
                parent.$("#expireTimeDiv").hide();
                parent.$("#expireTimeInput").val(null);
            }

            parent.$("#status").html(validateCar.operation.getStatus(parent.$("#box").find(".active-gray a").attr("status")));
            parent.$("#cropper").attr("src", parent.$("#box").find(".active-gray a img:last").attr("src"));
        }, review: function (pass, picId) {
            var flag = true;//是否需要计算照片待审核 审核完成... 的数量
            var status = 2;
            var html = "<img src='../../images/true.png' width='106' height='60' style='float:left;position: absolute;opacity:0.7'>";
            if (!pass) {
                html = "<img src='../../images/false.png' width='106' height='60' style='float:left;position: absolute;opacity:0.7'>";
                status = 3;
            }
            parent.$("#status").html(validateCar.operation.getStatus(status));
            if (pass && $.inArray(param.imgTypeId, param.imgTypes) != -1) {
                var isOk = validateCar.operation.saveExpireTime();
                flag = false;
                if (!isOk) {
                    return false;
                }
            }
            common.ajax.getByAjax(true, "put", "json", "/orderCenter/order/image/" + picId + "/" + status, {},
                function (data) {
                    if (data.flag) {
                        parent.$("#box").find(".active-gray a").attr("status", status);
                        parent.$("#box").find(".active-gray img:last").siblings("img").remove();
                        parent.$("#box").find(".active-gray img:last").before(html);
                        var index = parent.$("#box").find(".active-gray").index();
                        if (index < validateCar.imgCount - 1) {
                            /*validateCar.operation.right();*/
                        }
                        //检查所有图片状态
                        if (data.status == "2") {
                            parent.$(".audit").hide();
                            parent.$(".result").show();
                            parent.$("#result_text").html(data.statusMessage);
                        } else if (data.status == "3") {
                            parent.$(".audit").hide();
                            parent.$(".result").show();
                            parent.$("#result_text").html(data.statusMessage);
                        }
                    } else {
                        popup.mould.popTipsMould(false, data.message, popup.mould.first, popup.mould.error, "", "58%", null);
                    }
                },
                function () {
                    popup.mould.popTipsMould(false, "系统异常", popup.mould.first, popup.mould.error, "", "58%", null);
                }
            );
            return flag;
        }, showRightBtn: function () {
            if (validateCar.imgCount > 1) {
                parent.$("#right_btn").show();
            }
        }, getStatus: function (status) {
            status = parseInt(status);
            switch (status) {
                case 1:
                    return "审核状态：审核通过";
                    break;
                case 2:
                    return "审核状态：审核未通过";
                    break;
                default:
                    return "审核状态:未审核";
            }
        }, setAuditStatus: function () {
            parent.$("#audit").html(param.audit);
            parent.$("#pass").html(param.pass);
            parent.$("#nopass").html(param.nopass);
        }, uploadImageFile: function () {
            var imageFile = $("#imageFile").val();
            var imgTypeId = $("#imgTypeId").val();
            if (common.validations.isEmpty(imgTypeId)) {
                popup.mould.popTipsMould(false, "请选择要上传的类型;", popup.mould.second, popup.mould.error, "", "57%", null);
                return;
            }
            if ($.trim(imageFile) == "") {
                popup.mould.popTipsMould(false, "请选择图片！", popup.mould.second, popup.mould.error, "", "57%", null);
                return;
            }
            $("#uploadImageFile").prop('disabled', "true");//禁用上传按钮
            $("#uploadForm").ajaxSubmit({
                success: function (data) {
                    popup.mould.popTipsMould(false, "上传照片成功!", popup.mould.second, popup.mould.success, "", "57%", null);
                    popup.mask.hideSecondMask(false);
                    param.ulLeft = 0;
                    parent.$("#box").animate({left: param.ulLeft + "px"}, 500);
                    detail.init_detail_pop();
                },
                error: function (data) {
                    popup.mould.popTipsMould(false, "上传失败!", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            });

        }, saveExpireTime: function () {
            var expireTime = $("#expireTimeInput").val();
            var imgId = param.imgId;

            if (common.validations.isEmpty(imgId) || imgId == 'null') {
                popup.mould.popTipsMould(false, "请选择中图片再进行操作！", popup.mould.second, popup.mould.error, "", "57%", null);
                return false;
            }
            if (common.validations.isEmpty(expireTime) || expireTime == '') {
                return true;
            }

            common.getByAjax(false, "post", "json", "/orderCenter/order/image/expireTime",
                {
                    "expireTime": expireTime,
                    "imageId": imgId
                },
                function (data) {
                    //popup.mould.popTipsMould(false, "保存成功!" ,popup.mould.second , popup.mould.success, "", "57%", null);
                    param.ulLeft = 0;
                    parent.$("#box").animate({left: param.ulLeft + "px"}, 500);
                    detail.init_detail_pop();
                },
                function (data) {
                    popup.mould.popTipsMould(false, "设置过期时间失败!", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
            return true;
        }
    }

}
