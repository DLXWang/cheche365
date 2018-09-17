/**
 * Created by wangfei on 2015/5/30.
 */
$(function(){
    $("#uploadBtn").bind({
        click : function(){
          upload.uploadExcel($("#uploadForm"));
        }
    });

    $("#chooseFileBtn").bind({
        click : function(){
            $("#inputFile").click();
        }
    });

    $("#inputFile").bind({
        change : function(){
            $("#fileText").val($(this).val());
        }
    });

});

var upload = {
    /* 上传excel */
    uploadExcel : function(form) {
        var options = {
            url : "/orderCenter/autoShop/uploadExcel",
            type : "post",
            dataType: "text",
            beforeSubmit: function() {
                $("#warnMsg").hide();
                var filePath = $("#inputFile").val();
                if (filePath == "") {
                  common.showTips("请选择上传文件");
                  return false;
                }

                var extStart = filePath.lastIndexOf(".");
                var ext = filePath.substring(extStart, filePath.length);
                if (ext != ".xls" && ext != ".xlsx") {
                  common.showTips("上传文件必须为excel文件");
                  return false;
                }

                common.showWaitTips("文件上传中，请稍候");
            },
            success : function(result) {
                common.hideMask();
                if (result == "error") {
                    common.showTips("导入数据失败");
                    return false;
                }

                $("#warnMsg").text(result);
                $("#warnMsg").show();
              },
              error : function() {
                  common.showTips("系统异常");
                  common.hideMask();
              }
        };
        form.ajaxSubmit(options);
    }
}
