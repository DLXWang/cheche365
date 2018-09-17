var orderResult ={
    supplement : {
        supplementSwitch: function(orderJson, orderObj, companyId, position, isRenewal) {
            var $popInput = common.tools.getPopInputDom(position, true);
            var items = "";
            var today = common.tools.formatDate(new Date(), "yyyy-MM-dd");
            var tomorrow = common.tools.formatDate(new Date(), "yyyy-MM-dd",1);
            $.each(orderJson.data, function(index, supplementInfo) {
                var fieldPath = supplementInfo.fieldPath;
                var field = fieldPath.substring(fieldPath.lastIndexOf(".")+1, fieldPath.length);
                var label = "<div class=\"form-group form-group-fix\">" +
                    "<div class=\"col-sm-12 text-left form-inline\">" +
                    "<label>" + supplementInfo.fieldLabel + "</label>" +
                    "</div>" +
                    "</div>";
                switch (field) {
                    case "autoModel":
                        break;
                    default :
                        items += "<div class=\"form-group form-group-fix\">" +
                            "<div class=\"col-sm-12 text-left form-inline\">" +
                            "<label>" + supplementInfo.fieldLabel + "</label>" +
                            "</div>" +
                            "</div>" +
                            "<div class=\"form-group form-group-fix\">" +
                            "<div class=\"col-sm-12 text-center\">";
                        items += "<input type=\"text\" name=\"" + field + "\" class=\"field-input form-control text-height-28\" value=\"" + (supplementInfo.originalValue ? supplementInfo.originalValue : "") + "\">";
                        items +=
                            "</div>" +
                            "</div>";
                }
            });
            if (items) {
                popup.pop.popInput(true, quoteResult.supplementContent, position, "330px", "auto", "40%", "59%");
                $popInput.find(".supplement-info").append(items);
                $popInput.find(".theme_poptit .close").unbind("click").bind({
                    click: function() {
                        position == popup.mould.first ? popup.mask.hideFirstMask(true) : popup.mask.hideSecondMask(true);
                    }
                });
                $popInput.find(".toSupplement").unbind("click").bind({
                    click: function() {
                        var jsonData = quoteResult.result_2013.validFields($popInput);
                        if (jsonData && !jsonData.flag) {
                            $popInput.find(".error-line").show().find(".error-msg .errorText").text(jsonData.msg);
                            return;
                        }
                        quote.autoInfo.put(companyId, quoteResult.result_2013.setAuto(companyId, $popInput));
                        orderObj.saveOrderSupplement(orderObj, orderJson);
                        position == popup.mould.first ? popup.mask.hideFirstMask(true) : popup.mask.hideSecondMask(true);
                    }
                });
            }
        }
    },

    fillSupplementInfo: function(self, orderJson){
        var params = QuoteOrder.params.initParams(self);
        var fieldPath = orderJson.data[0].fieldPath;
        var fieldCode = fieldPath.substring(fieldPath.lastIndexOf(".")+1);
        var autoObj = quote.autoInfo.get(quote.companyId);
        var codeValue = autoObj ? autoObj[fieldCode] : "";
        params.additionalParameters.supplementInfo[fieldCode] = codeValue;
        return params;
    }
}

