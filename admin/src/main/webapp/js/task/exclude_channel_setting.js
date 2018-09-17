/**
 * Created by wangshaobin on 2016/8/29.
 */
var excludeChannelSetting = {
    settingArray : new Array(),
    newContent: "",
    showMark: "check",//查看权限标志
    editMark: "edit",//编辑权限标志
    initPopupContent: function() {
        var newContent = $("#setting_content");
        if (newContent.length > 0) {
            excludeChannelSetting.newContent = newContent.html();
            newContent.remove();
        }
    },

    crearSettingArray : function(){
        excludeChannelSetting.settingArray.length = 0;
    },

    initEditChannel : function(){
        common.ajax.getByAjax(true, "get", "json", "/admin/excludeChannelSetting/channels",
            {},
            function(data){
                var tab = $("#setting_tab tbody");
                tab.empty();

                var content = "";
                var number = 1;
                if (data) {
                    $.each(data, function(index, channel) {
                        content += "<tr>" +
                            "<td class=\"text-center\">" + number + "</td>" +
                            "<td class=\"text-center\">" + channel.id + "</td>" +
                            "<td class=\"text-center\">" + channel.description + "</td>" +
                            "<td class=\"text-center\">" + channel.name + "</td>" +
                            "<td class=\"text-center\"><input onchange=\"\" type=\"checkbox\" name=\"settingChk\" value=\"" + channel.id + "\"></td>" +
                            "</tr>";
                        number++;
                    });
                }
                tab.append(content);
            },
            function () {
                popup.mould.popTipsMould("获取渠道列表失败！", popup.mould.first, popup.mould.error, "", "56%", null);
            }
        )
    },

    list : function(){
        common.ajax.getByAjax(true, "get", "json", "/admin/excludeChannelSetting/list",
            {},
            function(data){
                var tab = $("#setting_list_tab tbody");
                tab.empty();
                $("#settingTotalCount").html(0);

                var content = "";
                var number = 1;
                if (data) {
                    $("#settingTotalCount").html(data.length);
                    $.each(data, function(index, setting) {
                        content += "<tr>" +
                            "<td class=\"text-center\">" + number + "</td>" +
                            "<td class=\"text-center\">" + setting.taskType + "</td>" +
                            "<td class=\"text-center\">" + setting.createTime + "</td>" +
                            "<td class=\"text-center\">" + setting.updateTime + "</td>" +
                            "<td class=\"text-center\">" + setting.operator + "</td>" +
                            "<td class=\"text-center\">" + "<a href='javascript:;' onclick=\"excludeChannelSetting.edit('" + setting.id + "');\")> 过滤渠道配置 </a></td>"
                            "</tr>";
                        number++;
                    });
                }
                tab.append(content);
            },
            function () {
                popup.mould.popTipsMould("获取过滤渠道配置列表失败！", popup.mould.first, popup.mould.error, "", "56%", null);
            }
        )
    },

    edit : function(settingId){
        common.ajax.getByAjax(true, "post", "json", "/admin/excludeChannelSetting/" + settingId,
            {},
            function(data){
                excludeChannelSetting.initPopupContent();
                popup.pop.popInput(excludeChannelSetting.newContent, popup.mould.first, "850px", "550px", "36%", "48%");
                excludeChannelSetting.crearSettingArray();
                excludeChannelSetting.setOptionsChk(data.excludeChannels);
                $(".theme_poptit .close").unbind("click").bind({
                    click: function() {
                        popup.mask.hideFirstMask();
                    }
                });
                $(".btn-finish .toCreate").unbind("click").bind({
                    click: function() {
                        excludeChannelSetting.updateSetting(settingId);
                    }
                });
            },
            function () {
                popup.mould.popTipsMould("获取该定时任务过滤渠道失败！", popup.mould.first, popup.mould.error, "", "56%", null);
            }
        )
    },

    setOptionsChk : function(settings){
        if (!common.validation.isEmpty(settings)) {
            excludeChannelSetting.settingArray = settings.split(",");
        }
        if ($("input[name=\"settingChk\"]").length > 0) {
            $("input[name=\"settingChk\"]").each(function () {
                if (excludeChannelSetting.settingArray.length > 0 && excludeChannelSetting.settingArray.indexOf($(this).val()) > -1) {
                    $(this).attr("checked",'checked');
                }
            });
        }
    },

    updateSetting : function(settingId){
        excludeChannelSetting.crearSettingArray();
        $('input[name="settingChk"]:checked').each(function(){
            excludeChannelSetting.settingArray.push($(this).val());
        });
        common.ajax.getByAjax(true, "post", "json", "/admin/excludeChannelSetting/update",
            {
                id:                 settingId,
                excludeChannels:    excludeChannelSetting.settingArray.toString()
            },
            function(data){
                $(".btn-finish .toCreate").attr("disabled", false);
                if (data.pass) {
                    popup.mould.popTipsMould("过滤渠道更新成功！", popup.mould.second, popup.mould.success, "", "59%",
                        function() {
                            popup.mask.hideAllMask();
                            excludeChannelSetting.list();
                        }
                    );
                } else {
                    popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.warning, "", "59%", null);
                }
            },
            function () {
                $(".btn-finish .toCreate").attr("disabled", false);
                popup.mould.popTipsMould("过滤渠道更新失败，请重试！", popup.mould.second, popup.mould.error, "", "59%", null);
            }
        )
    }

}
$(function(){
    excludeChannelSetting.list();
    excludeChannelSetting.initEditChannel();
})
