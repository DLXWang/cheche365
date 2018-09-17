
$(function() {
    var id = common.getUrlParam("id");
    var source = common.getUrlParam("source");
    var renewalFlag = common.getUrlParam("renewalFlag");
    quote.init.init(id,source,renewalFlag,handler.init(source,id).get(source));
})

var handler={
    init:function(source,id){
        var map=new Map();
        map.put("quoteRecord",new QuoteRecord(source,id));
        map.put("phone",new QuotePhone(source,id));
        map.put("photo",new QuotePhoto(source,id));
        map.put("renewInsurance",new RenewInsurance(source,id))
        return map;
    }
}
