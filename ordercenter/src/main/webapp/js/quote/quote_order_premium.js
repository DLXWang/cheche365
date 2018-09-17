/**
 * 订单页保费相关操作
 * Created by wangfei on 2016/5/19.
 */
var orderPremium = {
    premiumJson: {},//保费相关的JSON
    newPremiumJson: {},//修改后保费JSON
    paidAmount: "",//直减活动使用的保费
    percent: {//优惠幅度
        compulsory: 0,//交强优惠幅度
        commercial: 0//商业优惠幅度
    },
    premium:{
        paidAmount:0,
    },
    premiumType:0,
    preferentialLowPercent: 0.5,
    preferentialPercent:0,
    preferentialLimit:1,
    initParams: function(premiumJson, paidAmount) {
        this.paidAmount = paidAmount;
        copyProperties(this.premiumJson, premiumJson, ["autoTax", "compulsory", "commercial", "sumPremium"]);
        copyProperties(this.newPremiumJson, premiumJson, ["autoTax", "compulsory", "commercial", "sumPremium"]);
        console.log("premiumJson -> " + JSON.stringify(this.premiumJson) + ", paidAmount -> " + this.paidAmount);
        //copy属性
        function copyProperties(premiumJsonNew, premiumJson, items) {
            for (var index=0; index<items.length; index++) {
                premiumJsonNew[items[index]] = premiumJson[items[index]];
            }
        };
        if(quote.source!="order"){
            manualQuoteObj.result_manual.supportManual(quote.companyId,function(support){
                var $preferentialBtnGroup = $("#preferentialBtnGroup");
                if(!support){
                    $preferentialBtnGroup.hide();
                }else{
                    $preferentialBtnGroup.show();
                }
            });
        }
    },
    /**
     * 初始化保费显示域
     * @param reduceFlag 是否支持直减的情况
     */
    initOrderPremium: function(reduceFlag) {
        var $payableAutoTax = $("#payableAutoTax");
        var $payableCompulsory = $("#payableCompulsory");
        var $payableCommercial = $("#payableCommercial");
        var $payableSumPremium = $("#payableSumPremium");
        //车船税
        new CountUp(
            "payableAutoTax", $payableAutoTax.text(), this.premiumJson.autoTax, 2, 0.5,
            {useEasing: true, useGrouping: false}
        ).start();
        //交强险
        new CountUp(
            "payableCompulsory", $payableCompulsory.text(), this.premiumJson.compulsory, 2, 0.5,
            {useEasing: true, useGrouping: false}
        ).start();
        //商业险
        new CountUp(
            "payableCommercial", $payableCommercial.text(), this.premiumJson.commercial, 2, 0.5,
            {useEasing: true, useGrouping: false}
        ).start();
        //总保费
        var sumPremiumText = reduceFlag ? (this.paidAmount ? this.paidAmount : this.premiumJson.sumPremium - quoteRebate.rebateAmount) : this.premiumJson.sumPremium - quoteRebate.rebateAmount;
        new CountUp(
            "payableSumPremium", $payableSumPremium.text(), sumPremiumText, 2, 0.5,
            {useEasing: true, useGrouping: false}
        ).start();

        //避免toA进行多次append，先进行清空
        $("#reduceReason").text("");
        //直减增加标示
        if (this.paidAmount && reduceFlag) {
            var reduceAmount = common.tools.formatMoney(parseFloat(this.premiumJson.sumPremium - this.paidAmount), 2);
            if(reduceAmount > 0.0)
                $("#reduceReason").show().text("直减优惠" + reduceAmount + "元");
        } else {
            $("#reduceReason").text("").hide();
        }
        //toA的展示“奖励金xx元”
        var discounts = quote.param.get("discounts");
        if(discounts)
            $("#reduceReason").show().append(" 奖励金" + discounts + "元");
    },
    /**
     * 优惠按钮的展示规则
     */
    switchPreferentialBtn: function() {
        //活动直减不能同时优惠
        var $orderBtnGroup = $("#orderBtnGroup");
        var $preferentialBtnGroup = $("#preferentialBtnGroup");
        if (this.paidAmount && this.paidAmount > 0) {
            $preferentialBtnGroup.hide();
            $orderBtnGroup.css("padding-top", "20px");
        } else {
            manualQuoteObj.result_manual.supportManual(quote.companyId,function(support){
                if(!support){
                    $preferentialBtnGroup.hide();
                    $orderBtnGroup.css("padding-top", "20px");
                }else{
                    $preferentialBtnGroup.show();
                    $orderBtnGroup.css("padding-top", "6px");
                }
            });
        }
    },
    setPreferentialBtnAction: function() {
        var items = ["Compulsory", "Commercial"];
        //优惠
        $("#preferentialBtn").unbind("click").bind({
            click: function() {
                //清空使用优惠券
                quoteGift.clearUserGift();
                //保费使用非直减的
                orderPremium.initOrderPremium(false);
                orderPremium.showPreferentialByItems(items,orderPremium.preferentialPercent);
                $(this).hide().siblings("#cancelPreferentialBtn").show().siblings("#preferentialLimitBtn").hide();
                orderPremium.premiumType=0;
                if(quote.source=="order") {
                    quote.init.showAmendInfo(common.tools.formatMoney(quote.premiumJson.sumPremium,2));
                }
            }
        });
        $("#preferentialLimitBtn").unbind("click").bind({
            click: function() {
                //清空使用优惠券
                quoteGift.clearUserGift();
                //保费使用非直减的
                orderPremium.initOrderPremium(false);
                orderPremium.showPreferentialByItems(items,orderPremium.preferentialLimit);
                $(this).hide().siblings("#preferentialBtn").hide().siblings("#cancelPreferentialBtn").show();
                orderPremium.premiumType=1;
                quote.init.showAmendInfo(common.tools.formatMoney(quote.premiumJson.sumPremium,2));
            }
        });
        //取消优惠
        $("#cancelPreferentialBtn").unbind("click").bind({
            click: function() {
                orderPremium.initOrderPremium(true);
                orderPremium.clearPreferential();
                //拿取消优惠后的总计和实付金额比较
                if(quote.source=="order") {
                    quote.init.showAmendInfo(Number($("#payableSumPremium").html()));
                }
            }
        });
    },
    showPreferentialByItems: function(items,type) {
        for (var index=0; index<items.length; index++) {
            var item = items[index];
            //第一个字母变为小写匹配保费json参数
            var oriItem = item.substring(0, 1).toLowerCase() + item.substring(1, item.length);
            //保费大于0才给优惠
            if (orderPremium.premiumJson[oriItem] && orderPremium.premiumJson[oriItem] > 0) {
                var $payablePercent;
                if(type==orderPremium.preferentialLimit){
                    $payablePercent= $("#payable" + item + "PercentLimit");
                    $("#payable" + item + "PercentLimitSpan").show();
                    common.tools.setDomNumAction($payablePercent);
                }else{
                    $payablePercent = $("#payable" + item + "Percent");
                    $("#payable" + item + "PercentSpan").show();
                    //只能输入正整数
                    $payablePercent.numeral();

                }


                //绑定change方法
                bindChange(oriItem, item, $payablePercent);
                function bindChange(oriItem, item, $payablePercent) {
                    $payablePercent.unbind("change").bind({
                        change: function() {
                            //校验优惠幅度
                            if(this.value.indexOf(".")>0){
                                this.value = this.value.substr(0,this.value.indexOf(".")+3);
                            }
                            if (!quoteValidation.validPreferentialRange($(this),type,item)) {
                                $(this).val("");
                                orderPremium.changePreferentialPercent(oriItem, item, "",type);
                                return;
                        }
                            //拿优惠后的总计和实付金额比较
                            orderPremium.changePreferentialPercent(oriItem, item, $payablePercent.val(),type);
                            if(quote.source=="order"){
                                quote.init.showAmendInfo(Number($("#paidSumPremium").html()));
                            }
                        }
                    });
                }
            }
        }
    },
    /**
     * 优惠后的联动修改
     * @param oriItem 原始项
     * @param item 优惠项
     * @param percentVal 优惠值
     */
    changePreferentialPercent: function(oriItem, item, percentVal,type) {
        //原保费
        var oriPremium = this.premiumJson[oriItem];
        if (percentVal && percentVal>0) {
            //记录优惠幅度
            this.percent[oriItem] = percentVal;
            //优惠后保费
            var newPremium=0;
            if(type==orderPremium.preferentialPercent){
                newPremium = common.tools.formatMoney(parseFloat(oriPremium)*(1-percentVal/100), 4);
            }else{
                newPremium= common.tools.formatMoney(parseFloat(oriPremium-percentVal), 4);
            }

            //将优惠后的值设置为新的保费项
            this.newPremiumJson[oriItem] = newPremium;
            //改变值
            var changePremium = common.tools.formatMoney(parseFloat(oriPremium)-parseFloat(newPremium), 4);
            //赋值
            $("#payable" + item).addClass("decoration-premium");
            $("#paid" + item + "Span").show().find("#paid" + item).text(newPremium);
            $("#payable" + item + "ChangeSpan").show().find("#payable" + item + "Change").text("↓" + changePremium);
        } else {
            this.clearPreferentialItem(oriItem, item);
        }
        //计算总优惠
        this.setTotalPreferential();
    },

    /**
     * 设置总优惠
     */
    setTotalPreferential: function() {
        if (this.percent.commercial>0 || this.percent.compulsory>0) {
            //计算新总保费
            var newSumPremium = common.tools.formatMoney(parseFloat(this.newPremiumJson.autoTax) + parseFloat(this.newPremiumJson.compulsory)
                + parseFloat(this.newPremiumJson.commercial), 2);
            //设置新总保费
            this.newPremiumJson["sumPremium"] = newSumPremium;
            //总保费改变值
            var sumChangePremium = common.tools.formatMoney(parseFloat(this.premiumJson.sumPremium)-parseFloat(newSumPremium), 2);
            //总优惠比
            var sumPercent = common.tools.formatMoney((parseFloat(sumChangePremium)/parseFloat(this.premiumJson.sumPremium))*100, 2);
            //赋值
            $("#payableSumPremium").addClass("decoration-premium");
            $("#payableSumPremiumPercentSpan").show().find("#payableSumPremiumPercent").text(sumPercent);
            $("#paidSumPremiumSpan").show().find("#paidSumPremium").text(newSumPremium);
            orderPremium.premium.paidAmount=newSumPremium;
            $("#payableSumPremiumChangeSpan").show().find("#payableSumPremiumChange").text("↓" + sumChangePremium);
        } else {
            orderPremium.clearPreferentialItem("sumPremium", "SumPremium");
        }
    },
    /**
     * 清空一个优惠项
     * @param oriItem 原始name 如sumPremium
     * @param item dom name 如SumPremium
     */
    clearPreferentialItem: function(oriItem, item) {
        //还原之前的保费
        this.newPremiumJson[oriItem] = this.premiumJson[oriItem];
        $("#payable" + item).removeClass("decoration-premium");
        $("#paid" + item + "Span").hide();
        $("#payable" + item + "ChangeSpan").hide();
        if ("SumPremium" == item) {
            $("#payableSumPremiumPercentSpan").hide();
        } else {
            //还原优惠幅度
            this.percent[oriItem] = 0;
        }
    },
    /**
     * 清空优惠
     */
    clearPreferential: function() {
        $("#cancelPreferentialBtn").hide().siblings("#preferentialBtn").show().siblings("#preferentialLimitBtn").show();

        var items = ["Compulsory", "Commercial", "SumPremium"];
        for (var index=0; index<items.length; index++) {
            var item = items[index];
            var oriItem = item.substring(0, 1).toLowerCase() + item.substring(1, item.length);
            orderPremium.clearPreferentialItem(oriItem, item);
            $("#payable" + item + "PercentSpan").hide().find("#payable" + item + "Percent").val("");
            $("#payable" + item + "PercentLimitSpan").hide().find("#payable" + item + "PercentLimit").val("");
        }
    }

};
