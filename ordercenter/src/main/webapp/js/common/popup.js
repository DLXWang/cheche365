/**
 * Created by wangfei on 2015/7/24.
 */
var popHtml =
    <!-- 一级可输入的弹层  可为单个输入或组输入(form等) 内容、长度、高度需自定义 -->
    "<div id=\"popover_normal_input\" class=\"theme_popover none\"></div>" +
    <!-- 二级可输入的弹层  可为单个输入或组输入(form等) 内容、长度、高度需自定义 -->
    "<div id=\"popover_normal_input_second\" class=\"theme_popover_second none\"></div>" +
    <!-- 一级确认操作提示框 内容、长度、高度需自定义 -->
    "<div id=\"popover_normal_confirm\" class=\"theme_popover none\"></div>" +
    <!-- 二级确认操作提示框 内容、长度、高度需自定义 -->
    "<div id=\"popover_normal_confirm_second\" class=\"theme_popover_second none\"></div>" +
    <!-- 一级提示框 内容、长度、高度需自定义 -->
    "<div id=\"popover_normal_tips\" class=\"theme_popover none\"></div>" +
    <!-- 二级提示框 内容、长度、高度需自定义 -->
    "<div id=\"popover_normal_tips_second\" class=\"theme_popover_second none\"></div>" +
    <!-- 一级等待框 内容、长度、高度需自定义 -->
    "<div id=\"popover_normal_wait_tips\" class=\"theme_popover none\"></div>" +
    <!-- 二级等待框 内容、长度、高度需自定义 -->
    "<div id=\"popover_normal_wait_tips_second\" class=\"theme_popover_second none\"></div>" +
    <!-- 一级遮罩层 -->
    "<div class=\"theme_popover_mask none\"></div>" +
    <!-- 二级遮罩层 -->
    "<div class=\"theme_popover_mask_second none\"></div>" +
    <!-- 透明遮罩，用于防止用户在等待操作结果时操作其他事件引起的问题 -->
    "<div class=\"theme_popover_opacity_mask none\"></div>";

var firstMask = ".theme_popover_mask";
var secondMask = ".theme_popover_mask_second";
var popup = {
    /**
     * 导入弹出层html至页面，可以复用至新页面而不用copy html
     * @param dom domId
     */
    insertHtml: function(dom) {
        $(dom).html(popHtml);
    },
    /**
     * 弹出
     */
    pop: {
        /**
         * 弹出一级输入域、form表单等，内容、长度、高度、页顶百分比需自定义
         * @param isParent 当前是否在父页面，父页面的弹出层不需要window.parent
         * @param content 内容(html格式)
         * @param position 第一层(first)/第二层(second)弹出、目前只加了第一层
         * @param width 宽度 px
         * @param height 高度 px
         * @param topPercent 距离页顶百分比
         * @param leftPercent 距离左侧百分比
         */
        popInput: function(isParent, content, position, width, height, topPercent, leftPercent) {
            var position_w = isParent ? window : window.parent;
            var input;
            var mask;

            if (position == "first") {
                input = position_w.$("#popover_normal_input");
                mask = position_w.$(firstMask);
            } else if (position == "second") {
                input = position_w.$("#popover_normal_input_second");
                mask = position_w.$(secondMask);
            } else {
                console.log("position目前只支持first、second参数，如果需要请另行添加！");
                return false;
            }

            input.html(content);
            input.width(width);
            input.height(height);
            input.show();
            mask.show();

            if (topPercent) {
                input.css("top", topPercent);
            } else {
                input.css("top", "50%");
            }
            if (leftPercent) {
                input.css("left", leftPercent);
            } else {
                input.css("left", "54%");
            }
        },
        /**
         * 弹出确认框  内容、长度、高度、左侧百分比需自定义
         * @param isParent 当前是否在父页面，父页面的弹出层不需要window.parent
         * @param content 内容(html格式)
         * @param position 第一层(first)/第二层(second)弹出
         * @param width 宽度 px
         * @param height 高度 px
         * @param leftPercent 距离左侧百分比
         * @param confirmMethod 第一个按钮click方法 一般为确定
         * @param cancelMethod 第二个按钮click方法 一般为取消
         */
        popConfirm: function(isParent, content, position, width, height, leftPercent, confirmMethod, cancelMethod) {
            var position_w = isParent ? window : window.parent;
            var confirm;
            var mask;
            if (position == "first") {
                confirm = position_w.$("#popover_normal_confirm");
                mask = position_w.$(firstMask);
            } else if (position == "second") {
                confirm = position_w.$("#popover_normal_confirm_second");
                mask = position_w.$(secondMask);
            } else {
                console.log("position目前只支持first、second参数，如果需要请另行添加！");
                return false;
            }

            confirm.html(content);
            confirm.width(width);
            confirm.height(height);
            confirm.show();
            mask.show();

            if (leftPercent) {
                confirm.css("left", leftPercent);
            } else {
                confirm.css("left", "54%");
            }
            if (confirmMethod) {
                confirm.find(".btn-group .confirm").unbind("click").bind({
                    click : function() {
                        confirmMethod();
                    }
                });
            }
            if (cancelMethod) {
                confirm.find(".btn-group .cancel").unbind("click").bind({
                    click : function() {
                        cancelMethod();
                    }
                });
            }
        },
        /**
         * 弹出提示框  内容、长度、高度、左侧百分比需自定义
         * @param isParent 当前是否在父页面，父页面的弹出层不需要window.parent
         * @param content 内容(html格式)
         * @param position 第一层(first)/第二层(second)弹出
         * @param width 宽度 px
         * @param height 高度 px
         * @param leftPercent 距离左侧百分比
         * @param confirmMethod 按钮点击方法
         */
        popTips: function(isParent, content, position, width, height, leftPercent, confirmMethod) {
            var position_w = isParent ? window : window.parent;
            var tips;
            var mask;
            if (position == "first") {
                tips = position_w.$("#popover_normal_tips");
                mask = position_w.$(firstMask);
            } else if (position == "second") {
                tips = position_w.$("#popover_normal_tips_second");
                mask = position_w.$(secondMask);
            } else {
                console.log("position目前只支持first、second参数，如果需要请另行添加！");
                return false;
            }

            tips.html(content);
            tips.width(width);
            tips.height(height);
            tips.show();
            mask.show();

            if (leftPercent) {
                tips.css("left", leftPercent);
            } else {
                tips.css("left", "54%");
            }

            if (confirmMethod) {
                tips.find(".btn-group .confirm").unbind("click").bind({
                    click : function() {
                        confirmMethod();
                    }
                });
            }
        }/*,
        popWaitTips: function(isParent, content, position, width, height, leftPercent) {
            var position_w = isParent ? window : window.parent;
            var tips;
            var mask;
            if (position == "first") {
                tips = position_w.$("#popover_normal_wait_tips");
                mask = position_w.$(firstMask);
            } else if (position == "second") {
                tips = position_w.$("#popover_normal_wait_tips_second");
                mask = position_w.$(secondMask);
            } else {
                console.log("position目前只支持first、second参数，如果需要请另行添加！");
                return false;
            }

            tips.html(content);
            tips.width(width);
            tips.height(height);
            tips.show();
            mask.show();

            if (leftPercent) {
                tips.css("left", leftPercent);
            }
        }*/
    },
    /**
     * 弹出层模具，不满足的需自定义调用
     */
    mould: {
        first: "first",
        second: "second",
        success: "success",
        warning: "warning",
        error: "error",
        /**
         * 弹出确认提示框
         * @param isParent 当前是否在父页面，父页面的弹出层不需要window.parent
         * @param content 内容(html格式)
         * @param position 第一层(first)/第二层(second)弹出
         * @param btnText 按钮文字 数组，长度为2 依次为第一个、第二个按钮的文字
         * @param leftPercent 距离左侧百分比
         * @param confirmMethod 第一个按钮click方法 一般为确定
         * @param cancelMethod 第二个按钮click方法 一般为取消
         */
        popConfirmMould: function(isParent, content, position, btnText, leftPercent, confirmMethod, cancelMethod) {
            var d_width = "446px";
            var d_height = "auto";
            var d_leftPercent = "55%";
            var d_btnText = ["确定", "取消"];
            var length = common.getLength(content);
            var textStyle = "";
            var btnStyle = "";

            if (length > 41) {
                textStyle = "margin: 10px 10px 10px 90px";
                btnStyle = "padding-top: 15px;padding-bottom: 10px;";
            } else {
                textStyle = "margin: 20px 10px 10px 10px";
                btnStyle = "padding-top: 15px;padding-bottom: 10px;padding-right:90px;";
            }
            if (!leftPercent) {
                leftPercent = d_leftPercent;
            }
            if (!btnText) {
                btnText = d_btnText;
            }

            var contentTemplate =
                "<div class=\"theme_poptit\">" +
                "<span style='font-size: 16px;'>温馨提示</span>" +
                "</div>" +
                "<div class=\"popup-content\" style=\"padding: 20px; font-size: 15px;\">" +
                "<span class=\"icon-confirm\"></span>" +
                "<p style=\"" + textStyle + "\" class=\"tipsContent text-left\">" + content + "</p>" +
                "</div>" +
                "<div class=\"text-center\" style=\"" + btnStyle + "\">" +
                "<div class=\"btn-group\" style=\"margin: 0 10px;\">" +
                "<button style=\"font-size: 15px;\" type=\"button\" class=\"btn btn-warning btn-sm confirm\">" + btnText[0] + "</button>" +
                "</div>" +
                "<div class=\"btn-group\" style=\"margin: 0 10px;\">" +
                "<button style=\"font-size: 15px;\" type=\"button\" class=\"btn btn-default btn-sm cancel\">" + btnText[1] + "</button>" +
                "</div>" +
                "</div>";

            popup.pop.popConfirm(isParent, contentTemplate, position, d_width, d_height, leftPercent,
                confirmMethod, cancelMethod);
        },
        popSendSmsConfirmMould: function(isParent, content, position, btnText, leftPercent, confirmMethod, cancelMethod) {
            var d_width = "446px";
            var d_height = "auto";
            var d_leftPercent = "55%";
            var d_btnText = ["确定发送", "取消"];
            var length = common.getLength(content);
            var textStyle = "";
            var btnStyle = "";

            if (length > 41) {
                textStyle = "margin: 10px 10px 10px 10px";
                btnStyle = "padding-top: 15px;";
            } else {
                textStyle = "margin: 20px 10px 10px 10px";
                btnStyle = "padding-top: 15px;padding-right:90px;";
            }
            if (!leftPercent) {
                leftPercent = d_leftPercent;
            }
            if (!btnText) {
                btnText = d_btnText;
            }

            var contentTemplate =
                "<div class=\"theme_poptit\">" +
                "<span style='font-size: 16px;'>以下为您要给用户发送的短信</span>" +
                "</div>" +
                "<div class=\"popup-content\" style=\"padding: 20px; font-size: 15px; word-break:break-all; word-wrap:break-word ;\">" +
                "<p style=\"" + textStyle + "\" class=\"tipsContent text-left\">" + content + "</p>" +
                "</div>" +
                "<div class=\"text-center\" style=\"" + btnStyle + "\">" +
                "<div class=\"btn-group\" style=\"margin: 0 10px;\">" +
                "<button style=\"font-size: 15px;margin-bottom: 20px\" type=\"button\" class=\"btn btn-warning btn-sm confirm\">" + btnText[0] + "</button>" +
                "</div>" +
                "<div class=\"btn-group\" style=\"margin: 0 10px;\">" +
                "<button style=\"font-size: 15px;margin-bottom: 20px\" type=\"button\" class=\"btn btn-default btn-sm cancel\">" + btnText[1] + "</button>" +
                "</div>" +
                "</div>";

            popup.pop.popConfirm(isParent, contentTemplate, position, d_width, d_height, leftPercent,
                confirmMethod, cancelMethod);
        },
        /**
         * 弹出成功信息提示框
         * @param isParent 当前是否在父页面，父页面的弹出层不需要window.parent
         * @param content 内容(html格式)
         * @param position 第一层(first)/第二层(second)弹出
         * @param type 类型，目前有success, error, warning, 默认
         * @param btnText 按钮文字
         * @param leftPercent 距离左侧百分比
         * @param confirmMethod 按钮click方法 一般为确定
         */
        popTipsMould: function(isParent, content, position, type, btnText, leftPercent, confirmMethod) {
            var d_width = "410px";
            var d_height = "auto";
            var d_leftPercent = "55%";
            var d_btnText = "确定";
            var length = common.getLength(content);
            var textStyle = "";
            var btnStyle = "";
            var iconClass = "";
            var btnClass = "btn btn-sm confirm";

            if (length > 36) {
                textStyle = "margin: 10px 10px 10px 90px";
                btnStyle = "padding-top: 15px;"
            } else {
                textStyle = "margin: 20px 10px 10px 90px";
                btnStyle = "padding-top: 15px;padding-right:90px;"
            }
            if (!leftPercent) {
                leftPercent = d_leftPercent;
            }
            if (!btnText) {
                btnText = d_btnText;
            }
            if (!confirmMethod) {
                if (position == "first") {
                    confirmMethod = function() {
                        popup.mask.hideFirstMask(isParent);
                    }
                } else if (position == "second") {
                    confirmMethod = function() {
                        popup.mask.hideSecondMask(isParent);
                    }
                }
            }
            switch (type) {
                case "success":
                    iconClass = "icon-success-tips";
                    btnClass += " btn-success";
                    break;
                case "error":
                    iconClass = "icon-error-tips";
                    btnClass += " customer-btn-error";
                    break;
                case "warning":
                    iconClass = "icon-warning-tips";
                    btnClass += " customer-btn-warn";
                    break;
                default :
                    iconClass = "icon-default-tips";
                    btnClass += " customer-btn-info";
                    break;
            }

            var contentTemplate =
                "<div class=\"theme_poptit\">" +
                "<span style='font-size: 16px;'>温馨提示</span>" +
                "</div>" +
                "<div class=\"popup-content\" style=\"padding: 20px; font-size: 15px;\">" +
                "<span class=\"" + iconClass + "\"></span>" +
                "<p style=\"" + textStyle + "\" class=\"tipsContent text-left\">" + content + "</p>" +
                "</div>" +
                "<div class=\"text-center\" style=\"" + btnStyle + "\">" +
                "<div class=\"btn-group\" style=\"margin-bottom: 25px;\">" +
                "<button style=\"font-size: 15px;\" type=\"button\" class=\"" + btnClass + "\">" + btnText + "</button>" +
                "</div>" +
                "</div>";

            popup.pop.popTips(isParent, contentTemplate, position, d_width, d_height, leftPercent,
                confirmMethod);
        }/*,
        popWaitTisMould: function(isParent, content, position, leftPercent) {
            var d_width = "410px";
            var d_height = "211px";
            var d_leftPercent = "55%";
            var length = common.getLength(content);

            if (length > 36) {
            } else {
            }
            if (!leftPercent) {
                leftPercent = d_leftPercent;
            }

            var contentTemplate =
                "<div class=\"theme_poptit\">" +
                    "<span style='font-size: 16px;'>温馨提示</span>" +
                "</div>" +
                "<div style=\"padding-top: 40px; padding-bottom: 30px; text-align: center; font-size: 16px;\">" +
                    "<label class=\"tipsContent\">确定？</label>&nbsp;<img src=\"../images/loading.GIF\">"
                "</div>";

            popup.pop.popWaitTips(isParent, contentTemplate, position, d_width, d_height, leftPercent);
        }*/
    },
    /**
     * 弹出层隐藏
     */
    mask: {
        /**
         * 隐藏一级弹出层
         * @param isParent 当前是否在父页面，父页面的弹出层不需要window.parent
         */
        hideFirstMask: function(isParent){
            var position = isParent ? window : window.parent;
            position.$(".theme_popover_mask").hide();
            position.$(".theme_popover").hide();
        },
        /**
         * 隐藏二级弹出层
         * @param isParent 当前是否在父页面，父页面的弹出层不需要window.parent
         */
        hideSecondMask: function(isParent){
            var position = isParent ? window : window.parent;
            position.$(".theme_popover_mask_second").hide();
            position.$(".theme_popover_second").hide();
        },
        /**
         * 隐藏所有弹出层
         * @param isParent 当前是否在父页面，父页面的弹出层不需要window.parent
         */
        hideAllMask: function(isParent) {
            popup.mask.hideSecondMask(isParent);
            popup.mask.hideFirstMask(isParent);
        },
        /**
         * 根据当前位置隐藏弹出框
         * @param position
         * @param isParent
         */
        hideMaskByPosition: function(position, isParent) {
            switch (position) {
                case popup.mould.first:
                    popup.mask.hideFirstMask(isParent);
                    break;
                case popup.mould.second:
                    popup.mask.hideSecondMask(isParent);
                    break;
            }
        },
        /**
         * 显示透明遮罩，不同于弹出层的遮罩
         * @param isParent 当前是否在父页面，父页面的弹出层不需要window.parent
         * @param display 是否显示 true/false  显示/隐藏
         */
        showOrHideOpacityMask: function(isParent, display) {
            var position = isParent ? window : window.parent;
            if (display) {
                position.$(".theme_popover_opacity_mask").show();
            } else {
                position.$(".theme_popover_opacity_mask").hide();
            }
        }
    }
};
