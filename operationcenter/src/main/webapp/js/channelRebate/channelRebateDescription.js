//@ sourceURL=channelRebateDescription.js
var channelRebate = {

    getDescription: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/channelRebate/description/getDescription",
            {
                channelRebateId: channel_rebate.param.channelRebateId
            }, function (data) {
                $("#descriptionText").val(data.description)
            }, function () {
                alert("error!!");
            }
        );
    },

    saveDescription: function () {
        $("#saveOrUpdateBtn").unbind("click").bind({
            click: function () {
                if ($(this).hasClass("editDescription")) {
                    $(this).text("保存");
                    $("#descriptionText").removeAttr("disabled");
                    $(this).removeClass("editDescription");
                    $(this).addClass("saveDesctiption");
                } else {
                    common.getByAjax(true, "post", "json", "/operationcenter/channelRebate/description/save",
                        {
                            channelRebateId: channel_rebate.param.channelRebateId,
                            description: $("#descriptionText").val()
                        }, function () {
                            layer.msg('保存成功 ! ', {time: 500});
                            let $saveOrUpdateBtn = $("#saveOrUpdateBtn");
                            $saveOrUpdateBtn.text("编辑");
                            $("#descriptionText").attr("disabled", true);
                            $saveOrUpdateBtn.addClass("editDescription");
                            $saveOrUpdateBtn.removeClass("saveDesctiption");
                        }, function () {
                            alert("error!!");
                        }
                    );
                }

            }
        });
    }

};
$(function () {
    channelRebate.getDescription();
    channelRebate.saveDescription();

});
