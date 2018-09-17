/**
 * Created by wangshaobin on 2017/3/30.
 */
var orderComment = {
    popCommentList: function(purchaseOrderId, position) {
        $.post("../../page/comment/user_order_comment.html", {}, function (commentComment) {
            orderComment.interface.getCommentList(purchaseOrderId, function(data) {
                popup.pop.popInput(true, commentComment, popup.mould.first, "560px", "auto", "45%", "54%");
                var $popInput;
                switch (position) {
                    case popup.mould.first:
                        $popInput = window.parent.$("#popover_normal_input");
                        break;
                    case popup.mould.second:
                        $popInput = window.parent.$("#popover_normal_input_second");
                        break;
                }
                $popInput.find(".theme_poptit .close").unbind("click").bind({
                    click: function () {
                        popup.mask.hideFirstMask(false);
                    }
                });
                orderComment.fixCommentList(data, $popInput);
            });
        });
    },
    fixCommentList: function(data, $popInput) {
        var commentList = data.histories;
        var listContent = "";
        if (commentList) {
            $.each(commentList, function(index, comment) {
                listContent +=
                    "<p>" +
                    "<span style=\"color: #0099FF;\">" +
                    "[" + comment.createTime + "] " + comment.operatorName + "：" +
                    "</span>" +
                    comment.comment +
                    "</p>";
            });
        }
        if (listContent) {
            $popInput.find("#comment_list").html(listContent);
        }
        orderComment.scrollToBottom();
    },
    scrollToBottom: function() {
        var commentDoc = window.parent.document.getElementById("comment_list");
        commentDoc.scrollTop = commentDoc.scrollHeight;
    },
    interface: {
        getCommentList: function(purchaseOrderId, callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/outerUser/purchaseOrder/" + purchaseOrderId, {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单备注信息异常！", popup.mould.first, popup.mould.error, "", "58%", null);
                }
            );
        }
    }
};
