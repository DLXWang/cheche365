/**
 * Created by wangfei on 2015/5/27.
 */
$(function(){
    common.getByAjax(true, "get", "json", "/orderCenter/user/findAllRoleUsers", null,
        function(data){
            if(data.customerOptions != null && data.customerOptions != ""){
                $("#customerSel").append(data.customerOptions);
            }
            if(data.internalOptions != null && data.internalOptions != ""){
                $("#internalSel").append(data.internalOptions);
            }
            if(data.externalOptions != null && data.externalOptions != ""){
                $("#externalSel").append(data.externalOptions);
            }
        },
        function(){
            common.showTips("获取列表信息失败");
        }
    );

    $("#save_button").bind({
       click : function() {
           if($("#customerSel").val() == ""){
                common.showTips("请选择客服");
               return false;
           }

           if($("#internalSel").val() == ""){
               common.showTips("请选择内勤");
               return false;
           }

           if($("#externalSel").val() == ""){
               common.showTips("请选择外勤");
               return false;
           }

           user.addGroup();
       }
    });
});
