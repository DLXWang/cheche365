/**
 * Created by wangfei on 2015/5/22.
 */
$(function(){
    //清空
    $("#clear_button").bind({
       click : function(){
           cpsChannel.clearForm($("#channelForm"));
       }
    });

    //保存
    $("#save_button").bind({
        click : function(){
            if(!validation.vaild($("#channelForm"))){
                return false;
            }

            cpsChannel.save();
        }
    });
});
