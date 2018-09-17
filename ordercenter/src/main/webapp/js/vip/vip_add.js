/**
 * Created by sunhuazhong on 2015/6/5.
 */
$(function () {
    //清空
    $("#clear_button").bind({
        click: function () {
            vip.clearForm($("#vipInputForm"));
        }
    });

    //保存
    $("#save_button").bind({
        click: function () {
            if ($.trim($("#name").val()) == "") {
                common.showTips("请填写名称");
                return false;
            }
            if ($.trim($("#code").val()) == "") {
                common.showTips("请填写编号");
                return false;
            }
            if ($.trim($("#startDate").val()) == "") {
                common.showTips("请填写起始日期");
                return false;
            }
            if ($.trim($("#endDate").val()) == "") {
                common.showTips("请填写到期日期");
                return false;
            }
            vip.save();
        }
    });
});
