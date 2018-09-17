/**
 * Created by Administrator on 2015/11/9 0009.
 */
$(function(){

    // 定单审核通过事件
    $("#check_pass").bind({
        click:function(){
            popup.mould.popConfirmMould(false, "确定审核通过？", "second", "", "55%",
                function() {
                    popup.mask.hideSecondMask(false);
                    // 业务逻辑处理;
                    popup.mould.popTipsMould(false, "定单已审核通过！", "first", "success", "", "56%",
                        function() {
                            popup.mask.hideFirstMask(false);
                        }
                    );
                },
                function() {
                    popup.mask.hideSecondMask(false);
                    //obj.val(status==2?1:2);
                    return false;
                }
            );
        }
    });

    // 定单审核不通过事件
    $("#check_no_pass").bind({
        click:function(){
            popup.mould.popConfirmMould(false, "确定不审核通过该订单？该订单将被置为订单异常。", "second", "", "55%",
                function() {
                    popup.mask.hideSecondMask(false);
                    // 业务逻辑处理;
                    popup.mould.popTipsMould(false, "定单审核不通过！", "first", "success", "", "56%",
                        function() {
                            popup.mask.hideFirstMask(false);
                        }
                    );
                },
                function() {
                    popup.mask.hideSecondMask(false);
                    //obj.val(status==2?1:2);
                    return false;
                }
            );
        }
    });

});
