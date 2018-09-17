/**
 * 订单页相关参数
 * @param user 用户
 * @param quoteId 报价ID
 * @param insured 被保险人信息
 * @param owner 车主信息
 * @param premiumJson 保费JSON
 * @param areaId 地区ID
 * @param channelId 渠道ID
 * @param paidAmount 直减活动使用保费
 * @param companyId 保险公司ID
 * @param skipInsure 是否跳过核保
 * @param flag 1：正常报价、0：手工报价
 * @constructor
 */
function QuoteOrder(user, quoteId, insured, insure, owner, premiumJson, areaId, channelId, paidAmount, companyId, skipInsure, quoteCode, manualFlag){
    this.quoteId = quoteId;
    this.user = user;
    this.insured = insured;
    this.insure = insure;
    this.owner = owner;
    this.areaId = areaId;
    this.channelId = channelId;
    this.companyId = companyId;
    this.skipInsure = skipInsure;
    this.quoteCode = quoteCode;
    this.manualFlag = manualFlag;
    orderPremium.initParams(premiumJson, paidAmount);
    this.init();
}

/**
 * 更新相关订单页参数 参数意义参照初始化方法
 * @param quoteId
 * @param owner
 * @param premiumJson
 * @param paidAmount
 * @param companyId
 * @param skipInsure
 */
QuoteOrder.prototype.refreshOrderItems = function(quoteId, owner, premiumJson, paidAmount, companyId, skipInsure) {
    this.quoteId = quoteId;
    this.owner = owner;
    this.companyId = companyId;
    this.skipInsure = skipInsure;
    console.log("refresh quoteOrder property...");
    orderPremium.initParams(premiumJson, paidAmount);
    quoteGift.clearUserGift();
    orderPremium.clearPreferential();
    //刷新保费信息
    orderPremium.initOrderPremium(true);
};

QuoteOrder.prototype.init = function() {

    this.initDomAction();
    this.initPolicyAddress();
    this.initMonitorUrl();
    this.initIdentityType();
    this.setUserInfo();
    if(quote.companyId == '65000' || quote.companyId == '50000'|| quote.companyId == '205000'){
       // this.switchBtn(false);
        var btnTd = quoteResult.dom.getInsuranceCompanyBtnDom(quote.companyId);
        btnTd.find(".toEditPd").hide();
    }

};

QuoteOrder.prototype.initPolicyAddress = function() {
    var self = this;
    if(quote.companyId == '50000'){
        this.getPolicyAddressByAuto(function(result){
            var address=result
            console.log('用户当前的地址为：', address);
            if (address) {
                $('#policyName').val(address.name);
                $('#policyMobile').val(address.mobile);
                $('#policyStreet').val(address.street);
                self.address = address;
            }
            self.getProvinces(address);
            self.getCities('', address);
            self.getDistricts('', address);
        });
    }else{
        this.getPolicyAddress(function(list){
            var address = list.data.content[0];
            console.log('用户当前的地址为：', address);
            if (address) {
                $('#policyName').val(address.name);
                $('#policyMobile').val(address.mobile);
                $('#policyStreet').val(address.street);
                self.address = address;
            }
            self.getProvinces(address);
            self.getCities('', address);
            self.getDistricts('', address);
        });
    }


};

QuoteOrder.prototype.initMonitorUrl = function() {
    common.ajax.getByAjax(true, "get", "json", "/orderCenter/quote/getMonitorUrls", {},
        function(result) {
            var $monitorUrlSel = $('#monitorUrlSel');
            $monitorUrlSel.empty()
                .append($("<option></option>")
                    .attr("value",'')
                    .text('请选择'));
            if (result)
                result.forEach(function(item){
                    $monitorUrlSel
                        .append($("<option></option>")
                            .attr("title", item.scope+"_"+item.source+"_"+item.plan+"_"+item.unit+"_"+item.keyword)
                            .attr("value",item.businessActivity)
                            .attr("selected", item.businessActivity == quote.param.get("activeUrlId") ? true: false)
                            .text(item.source));
                });
        },
        function() {
            popup.mould.popTipsMould(true, "获取广告来源异常！", popup.mould.first, popup.mould.error, "", "57%", null);
        }
    );
};

QuoteOrder.prototype.setUserInfo = function() {
    var user = this.user;
    $('#insuredName').val(this.insured.name);
    $('#insuredIdentity').val(this.insured.identity);
    $('#insuredIdentityType').val(this.insured.identityType.id);
    $('#insureName').val(this.insure.name);
    $('#insureIdentity').val(this.insure.identity);
    $('#insuredIdentityType').val(this.insured.identityType.id);
    $('#insureIdentityType').val(this.insure.identityType.id);

    if (this.owner.name == this.insured.name && this.owner.identity == this.insured.identity) {
        $("#insuredType").val("1");
        $("#insuredName").attr("disabled", true);
        $("#insuredIdentity").attr("disabled", true);
        common.tools.setSelectReadonly($("#insuredIdentityType"));
    } else {
        $("#insuredType").val("0");
    }
    //支付方式
    if (user.type == 'agent') {
        $('#orderAgent').show();
        $('#userType').text('代理人');
        $('#userName').text("(" + user.name + ")");
        $('#rebate').text(user.rebate + "%");
    } else {
        $('#orderNormal').show();
        $('#userType').text('普通用户');
    }
};

QuoteOrder.prototype.getPolicyAddress = function(callback) {
    common.ajax.getByAjaxWithHeader(true, "get", "json", "/orderCenter/quote/users/address", {userId: this.user.userId},
        function(result) {
            var list = JSON.parse(result.message)
            callback(list);
        },
        function() {
            popup.mould.popTipsMould(true, "获取用户地址信息异常！", popup.mould.first, popup.mould.error, "", "57%", null);
        },
    quote.interface.getHeaderMap());
};

QuoteOrder.prototype.getPolicyAddressByAuto = function(callback) {
    common.ajax.getByAjaxWithHeader(true, "get", "json", "/orderCenter/quote/licensePlateNo/address", {licensePlateNo: quote.licensePlateNo},
        function(result) {
           // var list = JSON.parse(result.message)
            callback(result);
        },
        function() {
            popup.mould.popTipsMould(true, "获取用户地址信息异常！", popup.mould.first, popup.mould.error, "", "57%", null);
        },
        quote.interface.getHeaderMap());
};

QuoteOrder.prototype.getProvinces = function(address) {
    common.ajax.getByAjax(true, "get", "json", "/orderCenter/quote/areas/provinces", {},
        function(result) {
            var list = JSON.parse(result.message)
            var provinces = [];
            for(var i=0;i<list.data.length;i++){
                for(var j=0;j<list.data.length-i-1;j++){
                    if(list.data[j].id>list.data[j+1].id){
                        temp=list.data[j];
                        list.data[j]=list.data[j+1];
                        list.data[j+1]=temp;
                    }
                }
            }
            // callback(list);
            list.data.forEach(function(item){
                $('#policyProvince')
                    .append($("<option></option>")
                        .attr("value",item.id)
                        .attr('type', item.type)
                        .text(item.name));
            });

            if(address) {
                //直辖市若带出的省为空，直接赋值上市
                if (address.province) {
                    $('#policyProvince').val(address.province);
                } else {
                    $('#policyProvince').val(address.city);
                }
            }
        },
        function() {
            popup.mould.popTipsMould(true, "获取省份异常！", popup.mould.first, popup.mould.error, "", "57%", null);
        }
    );
};

QuoteOrder.prototype.getOnlyProvinces = function (address) {
    common.ajax.getByAjax(true, "get", "json", "/orderCenter/quote/areas/provinces", {},
        function (result) {
            var list = JSON.parse(result.message)
            for (var i = 0; i < list.data.length; i++) {
                for (var j = 0; j < list.data.length - i - 1; j++) {
                    if (list.data[j].id > list.data[j + 1].id) {
                        temp = list.data[j];
                        list.data[j] = list.data[j + 1];
                        list.data[j + 1] = temp;
                    }
                }
            }

            if (address) {
                list.data.forEach(function (item) {
                    if (item.id == address.province || item.id == address.city) {
                        $('#policyProvince')
                            .append($("<option></option>")
                                .attr("value", item.id)
                                .attr('type', item.type)
                                .text(item.name));
                    }

                });

                //直辖市若带出的省为空，直接赋值上市
                if (address.province) {

                    $('#policyProvince').val(address.province);
                } else {
                    $('#policyProvince').val(address.city);
                }
            }
        },
        function () {
            popup.mould.popTipsMould(true, "获取省份异常！", popup.mould.first, popup.mould.error, "", "57%", null);
        }
    );
};

QuoteOrder.prototype.getCities = function(provinceId, address) {
    if (address) {
        //直辖市若带出的省为空，直接赋值上市
        if (address.province) {
            provinceId = address.province;
        } else {
            provinceId = address.city;
        }
    }

    if(!provinceId) {
        return;
    }
    if (address && address.city && address.cityName) {
        $('#policyCity')
            .empty()
            .append($("<option></option>")
                .attr("value",address.city)
                .text(address.cityName));
    }
    var url = '/orderCenter/quote/areas/' + provinceId + '/cities';
    common.ajax.getByAjax(true, "get", "json", url, {},
        function(result) {
            var list = JSON.parse(result.message)
            if (list.data) {
                list.data.forEach(function(item){
                    $('#policyCity')
                        .append($("<option></option>")
                            .attr("value",item.id)
                            .text(item.name));
                });
                if (address && address.city) {
                    $('#policyCity').val(address.city);
                }
            }
        },
        function() {
            popup.mould.popTipsMould(true, "获取城市异常！", popup.mould.first, popup.mould.error, "", "57%", null);
        }
    );
};

QuoteOrder.prototype.getOnlyCities = function(provinceId, address) {
    if (address) {
        //直辖市若带出的省为空，直接赋值上市
        if (address.province) {
            provinceId = address.province;
        } else {
            provinceId = address.city;
        }
    }

    if(!provinceId) {
        return;
    }
    if (address && address.city && address.cityName) {
        $('#policyCity')
            .empty()
            .append($("<option></option>")
                .attr("value",address.city)
                .text(address.cityName));
    }
};

QuoteOrder.prototype.getDistricts = function(cityId, address) {
    var $policyDistrictDiv = $('#policyDistrictDiv');
    var $policyDistrict = $('#policyDistrict');
    $policyDistrict.empty();
    if (address && address.city) {
        cityId = address.city;
    }
    if(!cityId) {
        return;
    }
    var url = '/orderCenter/quote/areas/' + cityId + '/districts';
    common.ajax.getByAjax(true, "get", "json", url, {},
        function(result) {
            var list = JSON.parse(result.message);
            $policyDistrict.empty()
                .append($("<option></option>")
                    .attr("value",'')
                    .text('请选择'));
            if(list.data) {
                if (list.data.length > 0) {
                    list.data.forEach(function(item){
                        $policyDistrict.append($("<option></option>")
                                .attr("value",item.id)
                                .text(item.name));
                    });
                    //从市下面没有分区切换回有分区的添加上required限制
                    if (typeof($policyDistrict.attr("required")) == "undefined") {
                        $policyDistrict.attr("required", true);
                    }
                    $policyDistrictDiv.show();
                } else {
                    //市下面没有分区隐藏并去除required限制
                    $policyDistrictDiv.hide();
                    $policyDistrict.removeAttr("required");
                }
            }

            if (address && address.district) {
                $('#policyDistrict').val(address.district);
            }
        },
        function() {
            popup.mould.popTipsMould(true, "获取地区异常！", popup.mould.first, popup.mould.error, "", "57%", null);
        }
    );
};

QuoteOrder.prototype.setPayTypeAction = function() {
    var user = this.user;
    var self = this;
    //支付宝、百度、汽车之家、途虎、车享，新华不容许线下支付
    var noOfflineChannels = [10,12,15,16,13,17,18,203,19,20,21,22,23,24,25,26];

    //非北京或合作渠道不支持线下支付
    if (noOfflineChannels.indexOf(self.channelId) > -1 || self.areaId != 110000) {
        $("#payType option[value='offline']").remove();
    }
};
QuoteOrder.prototype.initIdentityType = function() {
    common.ajax.getByAjax(false, "get", "json", "/orderCenter/resource/identityTypes", {},
        function(result) {
            $("#insureIdentityType").empty();
            $("#insuredIdentityType").empty();
            if(result) {
                result.forEach(function(item){
                    $("#insureIdentityType").append($("<option></option>")
                        .attr("value",item.id)
                        .text(item.name));
                    $("#insuredIdentityType").append($("<option></option>")
                        .attr("value",item.id)
                        .text(item.name));
                });
            }
        },
        function() {
            popup.mould.popTipsMould(true, "获取证件类型异常！", popup.mould.first, popup.mould.error, "", "57%", null);
        }
    )
}

QuoteOrder.prototype.setUserGiftAction = function() {
    var self = this;

    $("#giftDisplayBtn").unbind("click").bind({
        click: function() {
            $(this).attr("disabled", true);
            quoteGift.displayUserGifts(popup.mould.first, orderPremium.premiumJson["sumPremium"], self);
        }
    });
};

QuoteOrder.prototype.setMarketingAction = function() {
    var self = this;
    $("#marketingDisplayBtn").unbind("click").bind({
        click: function() {
            $(this).attr("disabled", true);
            quoteMarketing.displayMarketings(popup.mould.first,orderPremium.premiumJson["sumPremium"], self, self.manualFlag);
        }
    });
};

QuoteOrder.prototype.setResendUserGiftAction = function() {
    var self = this;
    $("#resendGiftDisplayBtn").unbind("click").bind({
        click: function() {
            quoteResendGift.displayResendGifts(true,popup.mould.first);
        }
    });
};

QuoteOrder.prototype.initDomAction = function() {
    var self = this;
    var user = this.user;
    var owner = this.owner;
    var insured = this.insured;
    var quoteCode = this.quoteCode;
    console.log(quote.user);
    console.log(quote.insured);
    quoteRebate.channel.getRebate(this.quoteId);
    orderPremium.initOrderPremium(true);
    $('#policyProvince').change(function(e){
        $('#policyCity')
            .empty()
            .append($("<option></option>")
                .attr("value",'')
                .text('请选择'));
        $('#policyDistrict').empty().val('');
        if(!this.value) {
            return;
        }
        var current = $('option:selected', this);
        if(current.attr('type') == 2) {
            $('#policyCity')
                .empty()
                .append($("<option></option>")
                    .attr("value",this.value)
                    .text(current.text()));
            self.getDistricts(this.value);
        } else {
            self.getCities(this.value);
        }

    });
    $('#policyCity').change(function(e){
        if(!this.value) {
            return;
        }
        self.getDistricts(this.value);
    });
    $("#insuredType").unbind("change").bind({
        change: function() {
            if ($(this).val() == "1") {
                $("#insuredName").val(owner.name).attr("disabled", true);
                $("#insuredIdentity").val(owner.identity).attr("disabled", true);
                common.tools.setSelectReadonly($("#insuredIdentityType"));
            } else {
                $("#insuredName").val(insured.name).attr("disabled", false);
                $("#insuredIdentity").val(insured.identity).attr("disabled", false);
                common.tools.unsetSelectReadonly($("#insuredIdentityType"));
            }
        }
    });

    //支付方式控制以及change()
    self.setPayTypeAction();

    //如果是渠道折扣的，活动、优惠劵、百分比优惠、绝对值优惠不可用
    if(quoteRebate.isAgentChannel){
        $("#preferentialBtn").attr("disabled", true);
        $("#preferentialLimitBtn").attr("disabled", true);
        $("#marketingDisplayBtn").attr("disabled", true);
        $("#giftDisplayBtn").attr("disabled", true);
    }

    //优惠券相关
    self.setUserGiftAction();

    //活动领取相关
    self.setMarketingAction();

    //再送礼品相关
    self.setResendUserGiftAction();

    //电销自定义优惠相关
    orderPremium.setPreferentialBtnAction();

    $('#orderForm').submit(function(e){
        e.preventDefault();
        popup.mask.showOrHideOpacityMask(true, true);
        self.switchBtn(false);
        self.saveOrder(function(order) {
            popup.mask.showOrHideOpacityMask(true, false);
            var orderJson = JSON.parse(order)
            //安心核保失败订单单独处理 20180515.开放全部
            if((orderJson.code == 2008 || orderJson.code == 2013) ){
                QuoteOrder.flow = "reInsure";
                //如果是图片上传，暂时先直接展示订单提交成功，显示订单号，不提供链接
                var imageFlag = QuoteOrder.images.showSuccessInfo(orderJson);
                if(!imageFlag)
                    orderResult.supplement.supplementSwitch(orderJson, self, quote.companyId, popup.mould.first, false);
                return;
            }
            if (orderJson.code != 200) {
                if (orderJson.code == 2009) {
                    popup.mould.popTipsMould(true, "车险未到期，不可投保！", popup.mould.first, popup.mould.warning, "", "57%", null);
                    self.switchBtn(true);
                    return;
                }
                popup.mould.popTipsMould(true, orderJson.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                self.switchBtn(true);
                return;
            }
            if (orderJson.data.insureFailure) {
                popup.mould.popTipsMould(true, "订单核保失败！", popup.mould.first, popup.mould.warning, "", "57%", null);
                self.switchBtn(true);
                return;
            }
            popup.mould.popTipsMould(true, "订单提交成功！订单编号 <a target=\"_blank\" href=\"/orderCenter/quote/" + orderJson.data.id + "/detail\">" + orderJson.data.purchaseOrderNo + "</a>",
                popup.mould.first, popup.mould.success, "", "57%", function() {window.close()});
            if(quoteCode != 200)
                manualQuoteObj.saveManualLogs(quoteCode, orderJson.data.id);
            self.switchBtn(true);
            self.clearSession();
            self.sendOrderMsg(orderJson.data.purchaseOrderNo);
        });
        return false;
    });

    $("#returnBtn").unbind("click").bind({
        click: function() {
            quote.action.switchOrderPage(false, null);
            quote.returnFlag = true;
        }
    });
};

QuoteOrder.prototype.clearSession = function() {
    common.ajax.getByAjaxWithHeader(true, "delete", "json", "/orderCenter/quote/" + this.user.userId + "/session", {},
        function(data) {
        },function() {
        },
    quote.interface.getHeaderMap());
};

QuoteOrder.prototype.sendOrderMsg = function(orderNo) {
    common.ajax.getByAjax(true, "post", "json", "/orderCenter/quote/order/sms",
        {
            mobile:  this.user.mobile,
            orderNo: orderNo
        },
        function(data) {
        },function() {
        }
    );
};

QuoteOrder.prototype.checkUserAddress = function() {
    var defaultStr = getStrAddress(this.address);
    var currentStr = getStrAddress({
        province: $("#policyProvince").val(),
        city: $("#policyCity").val(),
        district: $("#policyDistrict").val(),
        street: $("#policyStreet").val(),
        name: $("#policyName").val(),
        mobile: $("#policyMobile").val()
    });
    console.log("defaultStr: " + defaultStr + ", currentStr: " + currentStr);
    return defaultStr == currentStr;

    function getStrAddress(address) {
        var uniqueAddress = "";
        if (address) {
            uniqueAddress = (address.province == address.city ? "" : common.tools.checkToEmpty(address.province)) + "_" +
            common.tools.checkToEmpty(address.city) + "_" +
            common.tools.checkToEmpty(address.district) + "_" +
            $.trim(address.street) + "_" +
            $.trim(address.name) + "_" +
            $.trim(address.mobile);
        }
        return uniqueAddress;
    }
};



QuoteOrder.prototype.saveOrder = function(callBackMethod) {
    var self = this;
    QuoteOrder.flow = "";
    var params = QuoteOrder.params.initParams(self);
    common.ajax.getByAjaxWithJsonAndHeader(true, "post", "json", "/orderCenter/quote/" + this.quoteId + "/order",
        params,
        function(data) {
            if(!data.pass) {
                popup.mask.showOrHideOpacityMask(true, false);
                popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                self.switchBtn(true);
                return;
            }
            callBackMethod(data.message);
        },function() {
            popup.mask.showOrHideOpacityMask(true, false);
            popup.mould.popTipsMould(true, "提交订单异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            self.switchBtn(true);
        },
    quote.interface.getHeaderMap());
};

QuoteOrder.prototype.saveOrderSupplement = function(orderObj, orderJson) {
    var self = orderObj;
    var params = orderResult.fillSupplementInfo(self,orderJson);
    var orderNo = orderJson.data[0].meta.orderNo;
    common.ajax.getByAjaxWithJsonAndHeader(true, "post", "json", "/orderCenter/quote/supplementOrder/" + orderNo + "/order",
        params,
        function(data) {
            if(!data.pass) {
                popup.mask.showOrHideOpacityMask(true, false);
                popup.mould.popTipsMould(true, data.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                self.switchBtn(true);
                return;
            }
            popup.mask.showOrHideOpacityMask(true, false);
            var orderJson = JSON.parse(data.message);
            //安心核保失败订单单独处理
            if((orderJson.code == 2008 || orderJson.code == 2013) && quote.companyId == 65000){
                //如果是图片上传，暂时先直接展示订单提交成功，显示订单号，不提供链接
                var imageFlag = QuoteOrder.images.showSuccessInfo(orderJson);
                if(!imageFlag)
                    orderResult.supplement.supplementSwitch(orderJson, self, quote.companyId, popup.mould.first, false);
                return;
            }
            if (orderJson.code != 200) {
                if (orderJson.code == 2009) {
                    popup.mould.popTipsMould(true, "车险未到期，不可投保！", popup.mould.first, popup.mould.warning, "", "57%", null);
                    self.switchBtn(true);
                    return;
                }
                popup.mould.popTipsMould(true, orderJson.message, popup.mould.first, popup.mould.warning, "", "57%", null);
                self.switchBtn(true);
                return;
            }
            if (orderJson.data.insureFailure) {
                popup.mould.popTipsMould(true, "订单核保失败！", popup.mould.first, popup.mould.warning, "", "57%", null);
                self.switchBtn(true);
                return;
            }
            popup.mould.popTipsMould(true, "订单提交成功！订单编号 <a target=\"_blank\" href=\"/orderCenter/quote/" + orderJson.data.id + "/detail\">" + orderJson.data.purchaseOrderNo + "</a>",
                popup.mould.first, popup.mould.success, "", "57%", function() {window.close()});
            self.switchBtn(true);
            self.clearSession();
            self.sendOrderMsg(orderJson.data.purchaseOrderNo);
        },function() {
            popup.mask.showOrHideOpacityMask(true, false);
            popup.mould.popTipsMould(true, "提交订单异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            self.switchBtn(true);
        },
        quote.interface.getHeaderMap());
};

QuoteOrder.images = {
    showSuccessInfo : function(orderJson){
        var fieldPath = orderJson.data[0].fieldPath;
        var fieldCode = fieldPath.substring(fieldPath.lastIndexOf(".")+1);
        if(fieldCode == "images"){
            popup.mould.popTipsMould(true, "订单提交成功！订单编号 " + orderJson.data[0].meta.orderNo, popup.mould.first, popup.mould.success, "", "57%", function() {window.close()});
            return true;
        }
        return false;
    }
}

/**
 * 订单补充信息标识，如果是核保的话，为reInsure
 * **/
QuoteOrder.flow = "";
QuoteOrder.params = {
    initParams : function(orderObj){
        var sourceId = $("#monitorUrlSel").val();
        var params = {
            deliveryAddress: {
                id:                  (orderObj.checkUserAddress() ? orderObj.address.id : null),
                province:            $("#policyProvince").val() == $("#policyCity").val() ? null:$("#policyProvince").val(),
                provinceName:        $("#policyProvince option:selected").text(),
                city:                $("#policyCity").val(),
                cityName:            $("#policyCity").val() ? $("#policyCity option:selected").text() : "",
                district:            $("#policyDistrict").val(),
                districtName:        $("#policyDistrict").val() ? $("#policyDistrict option:selected").text() : "",
                mobile:              $("#policyMobile").val(),
                name:                $("#policyName").val(),
                street:              $("#policyStreet").val()
            },
            insuredName:             $("#insuredName").val(),
            insuredIdNo:             $("#insuredIdentity").val(),
            insuredIdentityType:{
                id:                 $("#insuredIdentityType").val(),
            },
            applicantName:           $("#insureName").val(),
            applicantIdNo:           $("#insureIdentity").val(),
            applicantIdentityType:{
              id:                   $("#insureIdentityType").val()
            },
            giftId:                  $("#giftId").val(),
            channel: {
                id:                  4
            },
            applicant: {
                id:                  orderObj.user.userId
            },
            orderSourceType: !common.isEmpty(sourceId) ? {id: 1} : null,
            orderSourceId:   !common.isEmpty(sourceId) ? sourceId : null,
            skipInsure: orderObj.skipInsure,
            compulsoryPercent: orderPremium.percent.compulsory,
            commercialPercent: orderPremium.percent.commercial,
            premiumType:orderPremium.premiumType,
            additionalParameters:{
                supplementInfo:{
                }
            },
            flow:QuoteOrder.flow,
            resendGiftList: quoteResendGift.giftList,
            comment:$("#comment").val()
        }
        return params;
    }
}

QuoteOrder.prototype.switchBtn = function(showFlag) {
    var $submitOrder = $("#submitOrder");
    var $returnBtn = $("#returnBtn");
    var $preferentialBtnGroup = $("#preferentialBtnGroup");

    if (!showFlag) {
        $submitOrder.attr("disabled", true).hide().siblings(".submitImg").show();
        $returnBtn.attr("disabled", true).hide();
        $preferentialBtnGroup.hide();
    } else {
        $preferentialBtnGroup.show();
        //orderPremium.switchPreferentialBtn();
        $submitOrder.attr("disabled", false).show().siblings(".submitImg").hide();
        $returnBtn.attr("disabled", false).show();

    }
};
