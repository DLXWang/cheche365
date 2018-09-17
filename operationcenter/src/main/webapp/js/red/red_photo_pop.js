/**
 * Created by wangfei on 2015/10/19.
 */
var param = {
    rot: 0,
    height:400,
    width:400,
    top:0,
    left:0
};

var quote_photo_pop = {
    detailContent:'',
    quoteInit: {
        init: function() {
            this.initDetailContent();
        },
        initDetailContent: function() {
            var detailContent = $("#detail_content");
            if (detailContent.length > 0) {
                quote_photo_pop.detailContent = detailContent.html();
                detailContent.remove();
            }
        }
    },
    quoteDetail: {
        fixAutoContent: function(data, mark) {
            var parent = window.parent;
            if ("text" == mark) {
                if (data.licensePlateNo) {
                    $("#popover_normal_input_second #detail_owner").text(common.checkToEmpty(data.owner));
                    $("#popover_normal_input_second #detail_identity").text(common.checkToEmpty(data.identity));
                    $("#popover_normal_input_second #detail_insuredName").text(common.checkToEmpty(data.insuredName));
                    $("#popover_normal_input_second #detail_insuredIdNo").text(common.checkToEmpty(data.insuredIdNo));

                    this.setInsuredRadio(data);
                    $("input:radio[name='insuredType']").attr("disabled", true);

                    $("#popover_normal_input_second #detail_licensePlateNo").text(data.licensePlateNo);
                    $("#popover_normal_input_second #licensePlateNoHid").val(data.licensePlateNo);
                    $("#popover_normal_input_second #detail_vinNo").text(common.checkToEmpty(data.vinNo));
                    $("#popover_normal_input_second #detail_engineNo").text(common.checkToEmpty(data.engineNo));
                    $("#popover_normal_input_second #detail_enrollDate").text(common.checkToEmpty(data.enrollDate));
                    $("#popover_normal_input_second #detail_expireDate").text(common.checkToEmpty(data.expireDate));
                    $("#popover_normal_input_second #detail_code").text(common.checkToEmpty(data.code));
                    $("#popover_normal_input_second #detail_model").text(common.checkToEmpty(data.model));
                    $("#popover_normal_input_second #detail_mobile").text(common.checkToEmpty(data.mobile));
                }
                $("#popover_normal_input_second .text-input").hide().siblings(".text-show").show();
            }
        },
        setInsuredRadio: function(data) {
            if (data.owner || data.insuredName || data.identity || data.insuredIdNo) {
                if (data.owner == data.insuredName && data.identity == data.insuredIdNo) {
                    $("input:radio[name='insuredType']").eq(0).attr("checked", true);
                } else {
                    $("input:radio[name='insuredType']").eq(1).attr("checked", true);
                }
            }
        },
        popup: function (id) {
            quote_photo_pop.quoteInit.init();
            var parent = window.parent;
            this.findOne(id, function(data) {
                popup.pop.popInput( quote_photo_pop.detailContent, popup.mould.second, "880px", "560px", "38%", "49%");
                $("#popover_normal_input_second .theme_poptit .close").unbind("click").bind({
                    click: function () {
                        popup.mask.hideSecondMask(false);
                    }
                });

                $("#popover_normal_input_second .img-bottom .img-control").unbind("click").bind({
                    click: function (e) {
                        e.preventDefault();
                        $(this).parent().addClass("active").siblings("li").removeClass("active");
                        var imgTarget = $(this).attr("href").substring(1, $(this).attr("href").length);
                        $("#popover_normal_input_second #img_content #" + imgTarget).show().siblings("li").hide();
                    }
                });

                quote_photo_pop.quoteDetail.chooseTag(data);
                $("#id").val(id);
                $("#userId").val(data.userId);
                //图片设置
                quote_photo_pop.quoteDetail.showImg(data.drivingLicensePath,data.ownerIdentityPath);
            });

        },
        showImg:function(drivingLicensePath,ownerIdentityPath){
            $("#img_bottom ul li a img").hide();
            if(!common.isEmpty(drivingLicensePath)){
                $("#img_bottom ul li:last-child a img").attr("src",drivingLicensePath);
                $("#img_bottom ul li:last-child a img").show();
                $("#cropper").attr("src",drivingLicensePath);
            }
            if(!common.isEmpty(ownerIdentityPath)){
                $("#img_bottom ul li:first a img").attr("src",ownerIdentityPath);
                $("#img_bottom ul li:first a img").show();
                $("#cropper").attr("src",ownerIdentityPath);
            }
            $("#img_bottom ul li a img").unbind("click").bind({
                click: function () {
                   $("#cropper").attr("src",this.src);
                }
            });
            $("#leftRotate").bind({
                click: function() {
                    param.rot -=1;
                    if(param.rot <= -1){
                        param.rot = 11;
                    }
                    $("#cropper").removeClass().addClass("rot"+param.rot);
                }
            });
            $("#rightRotate").bind({
                click: function() {
                    param.rot +=1;
                    if(param.rot >= 11){
                        param.rot = -1;
                    }
                    $("#cropper").removeClass().addClass("rot"+param.rot);
                }
            });
            $("#big").bind({
                click: function() {
                    param.height=param.height*1.2;
                    param.width=param.width*1.2;
                    $("#cropper").animate({
                        width: param.height,
                        height:param.width
                    },500);
                }
            });
            $("#lit").bind({
                click: function() {
                    param.height=param.height/1.2;
                    param.width=param.width/1.2;
                    $("#cropper").animate({
                        width: param.height,
                        height:param.width
                    },500);
                }
            });
            $("#up").bind({
                click: function() {
                    param.top=param.top+150;
                    $("#cropper").animate({ top: param.top+"px" }, 500);
                }
            });
            $("#down").bind({
                click: function() {
                    param.top=param.top-150;
                    $("#cropper").animate({ top: param.top+"px" }, 500);
                }
            });
            $("#left").bind({
                click: function() {
                    param.left=param.left+150;
                    $("#cropper").animate({ left: param.left+"px" }, 500);
                }
            });
            $("#right").bind({
                click: function() {
                    param.left=param.left-150;
                    $("#cropper").animate({ left: param.left+"px" }, 500);
                }
            });
            $("#reset").bind({
                click: function() {
                    param.left=0;
                    param.top=0;
                    param.rot=0;
                    param.width=400;
                    param.height=400;
                    $("#cropper").removeClass().addClass("rot"+param.rot);
                    $("#cropper").animate({ left: param.left+"px" }, 10).animate({ top: param.top+"px" }, 10).animate({ width: param.width }, 10).animate({ height: param.height }, 10);
                }
            });
        },

        chooseTag: function(data) {
            var disable = data.disable;
            var licensePlateNo = data.licensePlateNo;
            if (null != disable && !disable) {
                if (licensePlateNo) {
                    this.fixAutoContent(data, "text");
                    $("#popover_normal_input_second #existent_auto_content").show().siblings("div").hide();
                } else {
                    $("#popover_normal_input_second #license_plate_no_content").show().siblings("div").hide();
                }
            } else {
                $("#popover_normal_input_second #disable_content").show().siblings("div").hide();
            }
        },
        findOne: function(id, callBackMethod) {
            common.getByAjax(true, "get", "json", "/operationcenter/red/photo/" + id, {},
                function(data) {
                    if (callBackMethod) {
                        callBackMethod(data);
                    }
                },
                function() {
                    popup.mould.popTipsMould("获取拍照报价详情异常！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            );
        }
    }
};

$(function() {
    //quote_photo_pop.quoteInit.init();
});


