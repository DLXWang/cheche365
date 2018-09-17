/**
 * Created by xu.yelong on 2015/12/11.
 */
$(function () {
    $("#insuranceFileBtn").bind({
        click : function(){
            uploadFile("insuranceFile",function(responseStr){
                $("#commercialDiv").removeClass("unvisable-hidden");
                $("#insuranceImage").attr("src",getThumbnail(responseStr));
                $("#insuranceImage").attr("url", responseStr);
                $("#inputInsuranceImage").val(responseStr);
                $("#downloadCommercial").attr("href", responseStr);
            })
        }
    });
    $("#compulsoryInsuranceFileBtn").bind({
        click : function(){
            uploadFile("compulsoryInsuranceFile",function(responseStr){
                $("#compulsoryDiv").removeClass("unvisable-hidden");
                $("#compulsoryInsuranceImage").attr("src",getThumbnail(responseStr));
                $("#compulsoryInsuranceImage").attr("url", responseStr);
                $("#inputCompulsoryInsuranceImage").val(responseStr);
                $("#downloadCompulsory").attr("href", responseStr);
            })
        }
    });

    $("#compulsoryInsuranceStampFileBtn").bind({
        click : function(){
            uploadFile("compulsoryInsuranceStampFile",function(responseStr){
                $("#compulsoryStampDiv").removeClass("unvisable-hidden");
                $("#compulsoryInsuranceStamp").attr("src", getThumbnail(responseStr));
                $("#compulsoryInsuranceStamp").attr("url", responseStr);
                $("#inputCompulsoryInsuranceStamp").val(responseStr);
                $("#downloadCompulsoryStamp").attr("href", responseStr);
            })
        }
    });

    $(".del").bind({
        click : function(){
            $(this).hide();
            $(this).prev().hide();
            $(this).next().val("");
            if($(this).attr("type")=="insurance"){
                $("#insuranceImageFile").val("");
            }else if($(this).attr("type")=="compulsoryInsurance"){
                $("#compulsoryInsuranceImageFile").val("");
            }
        }
    });

    $(".image").bind({
        click:function(){
            var url = $(this).attr("url");
            window.open(url);
        }
    })
    imagePreview();
});
function getThumbnail(src){
    return src.endsWith("pdf") ? "../../images/pdf.jpg" : src;
}

function uploadFile(elementName,callback) {
    var formData = new FormData();
    var orderNo = $("#orderNo").val();
    formData.append("codeFile", $("#"+elementName)[0].files[0]);
    var fileName= $("#"+elementName).val();
    if (common.validations.isEmpty(fileName)) {
        popup.mould.popTipsMould(false, "请选择要上传的文件！", popup.mould.second, popup.mould.warning, "", "57%", null);
        return;
    }
    //判断文件格式
    var suffix = fileName.substring(fileName.lastIndexOf("."));
    var imgeArray = [".jpg", ".jpeg", ".png", ".bmp", ".gif", ".pdf"];
    var suffixeq = false;
    for (var i = 0; i < imgeArray.length; i++) {
        if (suffix == (imgeArray[i]))
            suffixeq = true;
    }
    if (!suffixeq) {
        popup.mould.popTipsMould(false, "上传的格式只能为图片和pdf", popup.mould.second, popup.mould.warning, "", "57%", null);
        return;
    }
    formData.append("orderNo", orderNo);
    $.ajax({
        url:  "/orderCenter/insurance/upload",
        type: 'POST',
        data: formData,
        // 告诉jQuery不要去处理发送的数据
        processData: false,
        // 告诉jQuery不要去设置Content-Type请求头
        contentType: false,
        beforeSend: function () {
        },
        success: function (responseStr) {
            popup.mould.popTipsMould(false, "上传成功！", popup.mould.second, popup.mould.success, "", "57%", null);
            callback(responseStr);
        },
        error: function () {
            popup.mould.popTipsMould(false, "上传失败！", popup.mould.second, popup.mould.error, "", "57%", null);
        }
    });
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
