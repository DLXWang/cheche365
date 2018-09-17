$(function () {
    var delParent;
    var defaults = {
        fileType: ["png"]   // 上传文件的类型
    };
    /*点击图片的文本框*/
    $(".file").change(function () {
        var idFile = $(this).attr("id");
        var file = document.getElementById(idFile);
        var imgContainer = $(this).parents(".z_photo"); //存放图片的父亲元素
        var fileList = file.files; //获取的图片文件
        var input = $(this).parent();//文本框的父亲元素
        var imgArr = [];
        //遍历得到的图片文件
        var numUp = imgContainer.find(".up-section").length;
        var totalNum = numUp + fileList.length;  //总的数量
        if (fileList.length > 1 || totalNum > 1) {
            alert("上传图片数目不可以超过1个，请重新选择");  //一次选择上传1个
        }
        else if (numUp = 1) {
            fileList = validateUp(fileList);
            for (var i = 0; i < fileList.length; i++) {
                var imgUrl = window.URL.createObjectURL(fileList[i]);
                imgArr.push(imgUrl);
                var $section = $("<section class='up-section fl loading'>");
                imgContainer.prepend($section);
                var $span = $("<span class='up-span'>");
                $span.appendTo($section);

                var $img0 = $("<img class='close-upimg'>").on("click", function (event) {
                    event.preventDefault();
                    event.stopPropagation();
                    $(".works-mask").show();
                    delParent = $(this).parent();
                });
                $img0.attr("src", "/images/a7.png").appendTo($section);
                var $img = $("<img class='up-img up-opcity'>");
                $img.attr("src", imgArr[i]);
                $img.appendTo($section);
                var $p = $("<p class='img-name-p'>");
                $p.html(fileList[i].name).appendTo($section);
                var $input = $("<input id='taglocation' name='taglocation' value='' type='hidden'>");
                $input.appendTo($section);
                var $input2 = $("<input id='tags' name='tags' value='' type='hidden'/>");
                $input2.appendTo($section);
            }
            setTimeout(function () {
                $(".up-section").removeClass("loading");
                $(".up-img").removeClass("up-opcity");
            },100);
            numUp = imgContainer.find(".up-section").length;
            if (numUp >= 1) {
                $(this).parent().hide();
            }
        }
    });


    $(".z_photo").delegate(".close-upimg", "click", function () {
        $(".works-mask").show();
        delParent = $(this).parent();
    });

    $(".wsdel-ok").click(function () {
        $(".works-mask").hide();
        var numUp = delParent.siblings().length;
        if (numUp < 2) {
            delParent.parent().find(".z_file").show();
            $("#logoImage").attr("src", "/images/a11.png");
        }
        delParent.remove();
    });

    $(".wsdel-no").click(function () {
        $(".works-mask").hide();
    });

    function validateUp(files) {
        var arrFiles = [];//替换的文件数组
        for (var i = 0, file; file = files[i]; i++) {
            //获取文件上传的后缀名
            var newStr = file.name.split("").reverse().join("");
            if (newStr.split(".")[0] != null) {
                var type = newStr.split(".")[0].split("").reverse().join("");
                if (jQuery.inArray(type, defaults.fileType) > -1) {
                    // 类型符合，可以上传
                    uploadFile(file,function(result){
                        if (result == true){
                            arrFiles.push(file);
                        } else{
                            return arrFiles;
                        }
                    });
                } else {
                    alert("图片类型错误，仅支持png类型图片上传");
                }
            } else {
                alert('图片类型错误，仅支持png类型图片上传');
            }
        }
        return arrFiles;
    }
})

function uploadFile(files,callbackMethod) {
    var data = new FormData();
    data.append("uploadImageFile", files);
    data.append("channelCode", parent.$("#channelEngName").val() || parent.$("#channelCode").text())
    $.ajax({
        url: "/operationcenter/thirdParty/imageUpload",
        xhrFields: {
            withCredentials: true
        },
        type: "POST",
        cache: false,
        data: data,
        processData: false,
        contentType: false,
        async: false,
        success: function (result) {
            if (result == "error") {
                alert("图片尺寸错误，仅支持大小为 200×200 图片");
                if(callbackMethod){
                    callbackMethod(false);
                }
            } else {
                alert("上传成功");
                if(callbackMethod){
                    callbackMethod(true);
                }
            }
        },
        error: function () {
            common.showTips("系统异常");
            common.hideMask();
        }
        //cache 上传文件不需要缓存，所以设置false
        //processData 因为data值是FormData对象，不需要对数据处理
        //contentType 因为是由form表单构造的FormData对象，且已声明了属性enctype，所以为false
    })
}
