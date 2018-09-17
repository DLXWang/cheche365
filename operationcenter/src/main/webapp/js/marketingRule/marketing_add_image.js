/**
 * Created by xu.cxy on 2016/8/24.
 */
$(function () {
    $("#shared_icon_pic_file").bind({
        change : function(){
            var url="/operationcenter/image/upload/sharedIcon";
            uploadFile(url,function(result){
                $("#sharedIconImage").show();
                $("#sharedIconImage").next().show();
                $("#sharedIconImage").attr("src",result);
                $("#sharedIconPic").val(result);
            })
        }
    });
    $("#activity_pic_file").bind({
        change : function(){
            var url="/operationcenter/image/upload/topImage";
            uploadFile(url,function(result){
                $("#activityImage").show();
                $("#activityImage").next().show();
                $("#activityImage").attr("src",result);
                $("#activityPic").val(result);
            })
        }
    });

    $(".del").bind({
        click : function(){
            $(this).hide();
            $(this).prev().hide();
            $(this).next().val("");
            if($(this).attr("type")=="sharedIcon"){
                $("#sharedIconPic").val("");
            }else if($(this).attr("type")=="activityPic"){
                $("#activityPic").val("");
            }
        }
    });
    imagePreview();
});

function uploadFile(url,callbackMethod){
    var form = $("#add_form");
    var options = {
        url : url,
        type : "post",
        dataType: "text",
        success : function(result) {
            if(result == "error") {
                popup.mould.popTipsMould(true, "图片上传失败，文件格式错误！", "first", "error", "", "56%",
                    function() {
                        popup.mask.hideFirstMask(true);
                    }
                );
                return false;
            } else {
                if(callbackMethod){
                    callbackMethod(result);
                }
            }
        },
        error : function() {
            common.showTips("系统异常");
            common.hideMask();
        }
    };
    form.ajaxSubmit(options);
}
function imagePreview(){
    var xOffset = 10;
    var yOffset = 30;
    $(".image").hover(function(e){
            var c = (this.alt != "") ? "<br/>" + this.alt : "";
            $("body").append("<p id='preview'><img src='"+ this.src +"' style='height:300px;height:300px;'/>"+ c +"</p>");
            $("#preview")
                .css("top",(e.pageY-250 + xOffset) + "px")
                .css("left",(e.pageX + yOffset) + "px")
                .fadeIn("fast");
        },
        function(){
            $("#preview").remove();
        });
    $(".image").mousemove(function(e){
        $("#preview")
            .css("top",(e.pageY-250 + xOffset) + "px")
            .css("left",(e.pageX + yOffset) + "px");
    });
};
