/**
 * Created by wangfei on 2015/11/20.
 */
var orderComment = {
    popCommentList: function(purchaseOrderId, position, callback) {
        $.post("../comment/order_comment.html", {}, function (commentComment) {
            orderComment.interface.getCommentList(purchaseOrderId, function(data) {
                popup.pop.popInput(false, commentComment, popup.mould.first, "560px", "auto", "45%", "54%");
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
                var addBtn = $popInput.find(".toAddComment");
                addBtn.unbind("click").bind({
                    click: function () {
                        var comment = $popInput.find("#order_comment");
                        if (comment.val()) {
                            addBtn.attr("disabled", true);
                            orderComment.interface.addComment(purchaseOrderId, comment.val(),
                                function() {
                                    orderComment.interface.getCommentList(purchaseOrderId,
                                        function(commentList) {
                                            orderComment.fixCommentList(commentList, $popInput);
                                            addBtn.attr("disabled", false);
                                            comment.val("");
                                            if (callback) {
                                                callback();
                                            }
                                        }
                                    );
                                }
                            );
                        }
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
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/orderProcessHistories/purchaseOrder/" + purchaseOrderId, {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取订单记录异常！", popup.mould.first, popup.mould.error, "", "58%", null);
                }
            );
        },
        addComment: function(purchaseOrderId, comment, callback) {
            common.ajax.getByAjaxWithJson(true, "post", "json", "/orderCenter/orderProcessHistories",
                {
                    purchaseOrder: {
                        id: purchaseOrderId
                    },
                    comment: comment
                },
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "新增备注异常！", popup.mould.second, popup.mould.error, "", "58%", null);
                }
            );
        }
    }
};
