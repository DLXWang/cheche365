quoteRebate={
    rebateAmount:0,
    isAgentChannel:false,
    channel:{
        getRebate:function(quoteId){
            if(quote.source=="order"){
                quoteRebate.channel.getChannelRebateAmountHistory(quoteId,function(data){
                    //增补订单中，先去查询历史的费率；如果没有的话，则查询当前的费率
                    if(data != 0.0){
                        quoteRebate.rebateAmount= parseFloat(data);
                    }else{
                        quoteRebate.channel.getChannelRebateAmount(quoteId,function(data){
                            quoteRebate.rebateAmount= parseFloat(data);
                        });
                    }
                });
            }
        },
        getChannelRebateAmount:function(quoteId,callBackMethod){
            common.ajax.getByAjaxWithHeader(false, "get", "json", "/orderCenter/quote/rebate",
                {
                    quoteRecordId: quoteId
                },
                function(data) {
                        callBackMethod(data);
                },
                function() {
                    popup.mould.popTipsMould(true, "获取渠道折扣信息异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                },
                quote.interface.getHeaderMap());
        },
        //增补的渠道返点计算历史结果
        getChannelRebateAmountHistory:function(quoteId,callBackMethod){
            common.ajax.getByAjaxWithHeader(false, "get", "json", "/orderCenter/quote/amend/channel/rebate",
                {
                    orderId:quote.orderId,
                    quoteRecordId: quoteId
                },
                function(data) {
                    callBackMethod(data);
                },
                function() {
                    popup.mould.popTipsMould(true, "获取历史渠道折扣信息异常！", popup.mould.first, popup.mould.error, "", "57%", null);
                });
        },
        isAgentChannel: function() {
            var sourceChannelId = quote.sourceChannel;
            common.getByAjax(false,"get","json","/orderCenter/resource/isAgentChannel/"+ sourceChannelId,{},
                function(data){
                    quoteRebate.isAgentChannel=data;
                },
                function(){}
            );
        }
    }
}

