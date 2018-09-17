/**
 * Created by wangfei on 2015/10/19.
 */
var param = {
    rot: 0,
    height: 400,
    width: 400,
    top: 0,
    left: 0,
    popupLevel: '',
    mouldLv: '',
    popoverLv: ''
};

var quote_photo_pop = {
    quoteInit: {
        init: function () {
            this.initDetailContent();
        },
        initDetailContent: function () {
            var detailContent = $("#quote_detail_content");
            if (detailContent.length > 0) {
                quote_photo_pop.detailContent = detailContent.html();
                detailContent.remove();
            }
        }
    },
    quoteDetail: {
        popup: function (id, popupLevel) {
            var parent = window.parent;
            param.popupLevel = popupLevel;
            if (popupLevel == 'second') {
                param.mouldLv = popup.mould.second;
                param.popoverLv = '#popover_normal_input_second';
            } else {
                param.mouldLv = popup.mould.first;
                param.popoverLv = '#popover_normal_input';
            }
            this.findOne(id, function (data) {
                $.post("/page/quote/quote_photo_pop.html", {}, function (detailContent) {
                    popup.pop.popInput(false, detailContent, param.mouldLv, "880px", "560px", "38%", "49%");
                    quote_help.getIdentityTypes("input_identityType","input_insuredIdType");
                    parent.$(param.popoverLv + " .theme_poptit .close").unbind("click").bind({
                        click: function () {
                            if (param.popupLevel == 'second') {
                                popup.mask.hideSecondMask(false);
                            } else {
                                popup.mask.hideFirstMask(false);
                            }
                        }
                    });
                    parent.$(param.popoverLv + " .img-bottom .img-control").unbind("click").bind({
                        click: function (e) {
                            e.preventDefault();
                            $(this).parent().addClass("active").siblings("li").removeClass("active");
                            var imgTarget = $(this).attr("href").substring(1, $(this).attr("href").length);
                            parent.$(param.popoverLv + " #img_content #" + imgTarget).show().siblings("li").hide();
                        }
                    });

                    quote_photo_pop.quoteDetail.chooseTag(data);
                    parent.$("#id").val(id);
                    parent.$("#userId").val(data.userId);
                    //图片设置
                    quote_photo_pop.quoteDetail.showImg(data.drivingLicensePath, data.ownerIdentityPath);
                    parent.$("#btn_create").unbind("click").bind({
                        click: function () {
                            var licensePlateNo = parent.$("#new_license_plate_no").val();
                            if (common.isEmpty(licensePlateNo) || !common.validateLicenseNo(licensePlateNo)) {
                                common.showSecondTips("请正确填写车牌号");
                                return;
                            }
                            quote_photo_pop.quoteDetail.changeAuto(0, licensePlateNo, data.id);
                        }
                    });

                })
            });
        },
        showImg: function (drivingLicensePath, ownerIdentityPath) {
            parent.$("#img_bottom ul li a img").hide();
            if (!common.isEmpty(drivingLicensePath)) {
                parent.$("#img_bottom ul li:last-child a img").attr("src", drivingLicensePath);
                parent.$("#img_bottom ul li:last-child a img").show();
                parent.$("#cropper").attr("src", drivingLicensePath);
            }
            if (!common.isEmpty(ownerIdentityPath)) {
                parent.$("#img_bottom ul li:first a img").attr("src", ownerIdentityPath);
                parent.$("#img_bottom ul li:first a img").show();
                parent.$("#cropper").attr("src", ownerIdentityPath);
            }
            parent.$("#img_bottom ul li a img").unbind("click").bind({
                click: function () {
                    parent.$("#cropper").attr("src", this.src);
                    quote_photo_pop.quoteDetail.revertImg();
                }
            });
            parent.$("#leftRotate").bind({
                click: function () {
                    param.rot -= 1;
                    if (param.rot <= -1) {
                        param.rot = 11;
                    }
                    parent.$("#cropper").removeClass().addClass("rot" + param.rot);
                }
            });
            parent.$("#rightRotate").bind({
                click: function () {
                    param.rot += 1;
                    if (param.rot >= 11) {
                        param.rot = -1;
                    }
                    parent.$("#cropper").removeClass().addClass("rot" + param.rot);
                }
            });
            parent.$("#big").bind({
                click: function () {
                    param.height = param.height * 1.2;
                    param.width = param.width * 1.2;
                    parent.$("#cropper").animate({
                        width: param.height,
                        height: param.width
                    }, 500);
                }
            });
            parent.$("#lit").bind({
                click: function () {
                    param.height = param.height / 1.2;
                    param.width = param.width / 1.2;
                    parent.$("#cropper").animate({
                        width: param.height,
                        height: param.width
                    }, 500);
                }
            });
            parent.$("#up").bind({
                click: function () {
                    param.top = param.top + 150;
                    parent.$("#cropper").animate({top: param.top + "px"}, 500);
                }
            });
            parent.$("#down").bind({
                click: function () {
                    param.top = param.top - 150;
                    parent.$("#cropper").animate({top: param.top + "px"}, 500);
                }
            });
            parent.$("#left").bind({
                click: function () {
                    param.left = param.left + 150;
                    parent.$("#cropper").animate({left: param.left + "px"}, 500);
                }
            });
            parent.$("#right").bind({
                click: function () {
                    param.left = param.left - 150;
                    parent.$("#cropper").animate({left: param.left + "px"}, 500);
                }
            });
            parent.$("#reset").bind({
                click: function () {
                    quote_photo_pop.quoteDetail.revertImg();
                    //param.left=0;
                    //param.top=0;
                    //param.rot=0;
                    //param.width=400;
                    //param.height=400;
                    //parent.$("#cropper").removeClass().addClass("rot"+param.rot);
                    //parent.$("#cropper").animate({ left: param.left+"px" }, 10).animate({ top: param.top+"px" }, 10).animate({ width: param.width }, 10).animate({ height: param.height }, 10);
                }
            });
        },
        revertImg: function () {
            param.left = 0;
            param.top = 0;
            param.rot = 0;
            param.width = 400;
            param.height = 400;
            parent.$("#cropper").removeClass().addClass("rot" + param.rot);
            parent.$("#cropper").animate({left: param.left + "px"}, 10).animate({top: param.top + "px"}, 10).animate({width: param.width}, 10).animate({height: param.height}, 10);
        },
        chooseTag: function (data) {
            var disable = data.disable;
            var licensePlateNo = data.licensePlateNo;
            if (null != disable && !disable) {
                if (licensePlateNo) {
                    quote_help.auto.fixAutoContent(data, "text", "", param.popupLevel);
                    parent.$(param.popoverLv + " #existent_auto_content").show().siblings("div").hide();
                    parent.$(param.popoverLv + " .toChangeAuto").unbind("click").bind({
                        click: function () {
                            parent.$(param.popoverLv + " #license_plate_no_content").show().siblings("div").hide();
                            quote_photo_pop.quoteDetail.findLicensePlateNos(data.userId, data.id);
                        }
                    });
                    parent.$(param.popoverLv + " .toEdit").unbind("click").bind({
                        click: function () {
                            quote_help.auto.fixAutoContent(data, "input", "", param.popupLevel);
                            window.parent.$("#input_identityType").attr("disabled", false);
                            window.parent.$("#input_insuredIdType").attr("disabled", false);
                            parent.$(param.popoverLv + " #detail_mobile").text(common.checkToEmpty(data.mobile));
                            parent.$(param.popoverLv + " #mobile_content .text-show").show().siblings(".text-input").hide();
                            parent.$(param.popoverLv + " #btn_group .btn-edit").show().siblings(".btn-show").hide();
                            parent.$(param.popoverLv + " #btn_group .toSave").unbind("click").bind({
                                click: function () {
                                    if (!quote_help.quoteValidate.validateQuote()) {
                                        return;
                                    }
                                    parent.$("#toSave").attr("disabled", true);
                                    window.parent.$(".toChgQuoteAutoInfo").hide();
                                    common.getByAjax(true, "post", "json", "/orderCenter/quote/photo/update", parent.$(param.popoverLv + " #quote_photo_form").serialize(),
                                        function (data2) {
                                            data = data2;
                                            quote_help.auto.fixAutoContent(data, "text", "", param.popupLevel);
                                            parent.$(param.popoverLv + " #btn_group .btn-edit").hide().siblings(".btn-show").show()
                                            parent.$("#toSave").attr("disabled", false);
                                        }, function () {
                                            common.showSecondTips("系统异常");
                                            parent.$("#toSave").attr("disabled", false);
                                        }
                                    );
                                }
                            });
                            parent.$(param.popoverLv + " #btn_group .toCancel").unbind("click").bind({
                                click: function () {
                                    quote_help.auto.fixAutoContent(data, "text", "", param.popupLevel);
                                    parent.$(param.popoverLv + " #btn_group .btn-edit").hide().siblings(".btn-show").show();
                                    parent.$(param.popoverLv + " .alert-danger").hide();
                                }
                            });
                            window.parent.$(".toChgQuoteAutoInfo").show();
                            window.parent.$("#popover_normal_input .form-input .toChgQuoteAutoInfo").unbind("click").bind({
                                click: function () {
                                    var owner = window.parent.$("#input_owner").val();
                                    var identityType = window.parent.$("#input_identityType").val();
                                    var identity = window.parent.$("#input_identity").val();
                                    var licensePlateNo = window.parent.$("#popover_normal_input #detail_licensePlateNo").text();
                                    quote_help.auto.getAutoContentByNameId(licensePlateNo, "input", owner,identityType, identity);
                                }
                            });
                        }
                    });
                    parent.$(param.popoverLv + " #btn_group .toQuote").unbind("click").bind({
                        click: function () {
                            window.open("/page/quote/quote.html?source=photo&id=" + data.id);
                        }
                    });
                    parent.$(param.popoverLv + " #btn_group .toRenewal").unbind("click").bind({
                        click: function () {
                            var renewalValidation = quote_help.checkRenewal(data);
                            if (!renewalValidation.flag) {
                                quote_photo_pop.quoteDetail.error(renewalValidation.msg);
                                return;
                            }
                            window.open("/page/quote/quote.html?renewalFlag=1&source=photo&id=" + data.id);
                        }
                    });
                } else {
                    quote_photo_pop.quoteDetail.findLicensePlateNos(data.userId, data.id);
                    parent.$(param.popoverLv + " #license_plate_no_content").show().siblings("div").hide();
                }
            } else {
                parent.$(param.popoverLv + " #disable_content").show().siblings("div").hide();
                parent.$(param.popoverLv + " #disable_content .enable").unbind("click").bind({
                    click: function () {
                        quote_photo_pop.quoteDetail.setDisable(data.id, 0, function (returnData) {
                            quote_photo_pop.quoteDetail.chooseTag(returnData);
                        });
                    }
                });
                parent.$(param.popoverLv + " #disable_content .disable").unbind("click").bind({
                    click: function () {
                        quote_photo_pop.quoteDetail.setDisable(data.id, 1, function (returnData) {
                            /*parent.$(param.popoverLv + " #disable_content .btn").hide();
                            $.ajax({
                                async: false,
                                type: "GET",
                                dataType: "json",
                                url: "/orderCenter/quote/photo/hint",
                                data: {id:data.id},
                                success: function (data) {
                                   if(data.hint!="k"){
                                       parent.$(param.popoverLv + " #showhint").html("不通过审核的原因: "+data.hint);
                                       parent.$(param.popoverLv + " #showhint").show();
                                   }else{
                                       parent.$(param.popoverLv + " #reason").show();
                                   }
                                },
                                error: function () {

                                }
                            });*/
                            if (param.popupLevel == 'second') {
                                popup.mask.hideSecondMask(false);
                            } else {
                                popup.mask.hideFirstMask(false);
                            }

                        });
                    }
                });
                /*parent.$(param.popoverLv + " .answerr").unbind("click").bind({
                    click: function () {
                        var a =parent.$("#rselect1").find("option:selected").text();
                        var id1=parent.$("#id").val();
                        $.ajax({
                            async: false,
                            type: "GET",
                            dataType: "json",
                            url: "/orderCenter/quote/photo/setHint",
                            data: {id:id1,hint:a},
                            success: function (data) {
                                if (param.popupLevel == 'second') {
                                    popup.mask.hideSecondMask(false);
                                } else {
                                    popup.mask.hideFirstMask(false);
                                }
                            },
                            error: function () {
                                popup.mould.popTipsMould(false, "保存拍照失败原因异常！", param.mouldLv, popup.mould.error, "", "57%", null);
                            }
                        });
                    }
                });
                parent.$(param.popoverLv + " .answere").unbind("click").bind({
                    click: function () {
                        var b =parent.$("#rselect2").val();
                        var id2=parent.$("#id").val();
                        $.ajax({
                            async: false,
                            type: "GET",
                            dataType: "json",
                            url: "/orderCenter/quote/photo/setHint",
                            data: {id:id2,hint:b},
                            success: function (data) {
                                if (param.popupLevel == 'second') {
                                    popup.mask.hideSecondMask(false);
                                } else {
                                    popup.mask.hideFirstMask(false);
                                }
                            },
                            error: function () {
                                popup.mould.popTipsMould(false, "保存拍照失败原因异常！", param.mouldLv, popup.mould.error, "", "57%", null);
                            }
                        });
                    }
                });*/
            }
        },
        findOne: function (id, callBackMethod) {
            common.getByAjax(true, "get", "json", "/orderCenter/quote/photo/" + id, {},
                function (data) {
                    if (callBackMethod) {
                        callBackMethod(data);
                    }
                },
                function () {
                    popup.mould.popTipsMould(false, "获取拍照报价详情异常！", param.mouldLv, popup.mould.error, "", "57%", null);
                }
            );
        },
        findLicensePlateNos: function (userId, id) {
            common.getByAjax(true, "get", "json", "/orderCenter/quote/photo/licensePlateNo", {userId: userId},
                function (data) {
                    if (data.length > 0) {
                        var content = "";
                        var current = "";
                        for (var i = 0; i < data.length; i++) {
                            current = "";
                            var array = data[i].split(",");
                            if (array[0] == id) {
                                current = "(当前)";
                            }
                            content += "<tr class='text-center'><td>" + "<a href='javascript:;'class='change' licensePlateNo='" + array[1] + "'  id='" + array[0] + "'>" + array[1] + current + "</a></td></tr>"
                        }
                        parent.$("#old_license_plate_no_group table tbody").children().remove();
                        parent.$("#old_license_plate_no_group table tbody").append(content);
                        parent.$(".change").each(function (index, obj) {
                            $(obj).unbind("click").bind({
                                click: function () {
                                    quote_photo_pop.quoteDetail.changeAuto($(this).attr("id"), $(this).attr("licensePlateNo"), id)
                                }
                            });
                        });
                    }
                },
                function () {
                    popup.mould.popTipsMould(false, "获取历史车牌号异常！", param.mouldLv, popup.mould.error, "", "57%", null);
                }
            );
        },
        changeAuto: function (id, licensePlateNo, currentId) {
            common.getByAjax(true, "get", "json", "/orderCenter/quote/photo/auto", {
                    licensePlateNo: licensePlateNo,
                    id: id,
                    currentId: currentId
                },
                function (data) {
                    quote_photo_pop.quoteDetail.chooseTag(data);
                },
                function () {
                    popup.mould.popTipsMould(false, "更换车牌号异常！", param.mouldLv, popup.mould.error, "", "57%", null);
                }
            );
        },
        setDisable: function (id, disable, callBackMethod) {
            common.getByAjax(true, "put", "json", "/orderCenter/quote/photo/disable/" + id, {disable: disable},
                function (data) {
                    if (callBackMethod) {
                        callBackMethod(data);
                    }
                },
                function () {
                    popup.mould.popTipsMould(false, "设置拍照报价有效状态异常！", param.mouldLv, popup.mould.error, "", "57%", null);
                }
            );
        },

        error: function (msg) {
            parent.$("#error").html(msg);
            parent.$("#error").show().delay(2000).hide(0);
        }

    }
};

$(function () {
    quote_photo_pop.quoteInit.init();
});




