/**
 * Created by Administrator on 2015/11/12 0012.
 */
$(function(){

    $("#myModal").bind({
        click:function(){
            $('#myModal').modal('toggle');
            return false;
        }
    });

    $("#order_msg").bind({
        click:function(){
            $('#myModal').modal('toggle');
            return false;
        }
    });

});
