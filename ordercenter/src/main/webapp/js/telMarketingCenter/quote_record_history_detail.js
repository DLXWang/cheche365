var qrhd = {
    param: {
        repeatId: "",
        pageContentText: "",
        quoteRecordDetailContent: "",
        parent: window.parent
    },
    init: {
        initPage: function () {
            popup.insertHtml($("#popupHtml"));
            qrhd.init.initQuoteRecordDetailDivContent();
            qrhd.interface.getQuoteRecordData();
        },
        initQuoteRecordDetailDivContent: function () {
            var $detailContent = $("#quoteRecordPopDiv");
            qrhd.param.quoteRecordDetailContent = $detailContent.html();
            $detailContent.remove();
        }
    },
    interface: {
        getQuoteRecordData: function () {
            common.getByAjax(false, "get", "json", "/orderCenter/telMarketingCenter/quote",
                {
                    repeatId: qrhd.param.repeatId
                },
                function (data) {
                    qrhd.method.createDataHtml(data);
                },
                function () {
                    popup.mould.popTipsMould(false, "获取报价信息错误", popup.mould.first, popup.mould.warning, "", "57%", null);
                }
            )
        }
    },
    operation: {
        //我要报价 超链接
        toQuotePage: function (thisLink) {
            window.open("/page/quote/quote.html?source=quoteRecord&id=" + $(thisLink).attr("logId"));
        },
        //险种详情
        showQuoteItemDetail: function (thisLink) {
            var quoteRecordExt = JSON.parse($(thisLink).attr("detailjson"));
            if (quoteRecordExt != null) {
                popup.pop.popInput(false, qrhd.param.quoteRecordDetailContent, popup.mould.first, "600px", "600px", "33%", "52%");
                parent.$("#popover_normal_input .close").unbind("click").bind({
                    click: function () {
                        popup.mask.hideFirstMask(true);
                    }
                });
                qrhd.method.fillQuoteRecordDetailContent(quoteRecordExt);
            } else {
                popup.mould.popTipsMould(false, "没有对应的报价记录信息", popup.mould.first, popup.mould.warning, "", "57%", null);
            }
        }
    },
    method: {
        createDataHtml: function (data) {
            $.each(data, function (i, carViewData) {
                qrhd.param.pageContentText +=
                    '	<div class="row" style="margin-top: 40px">                                                                                      ' +
                    '		<div class="tab-top-1">                                                                                                     ' +
                    '			<span class="text-left bold-font">车牌号:' + carViewData.carNo + '&nbsp;&nbsp;&nbsp;&nbsp;车架号:' + carViewData.carVin + ' &nbsp;&nbsp;&nbsp;&nbsp;发动机号:' + carViewData.engineNo +
                    ' &nbsp;&nbsp;&nbsp;&nbsp;初登日期:' + carViewData.enrollDate + ' &nbsp;&nbsp;&nbsp;&nbsp;品牌型号:' + carViewData.autoTypeName + ' &nbsp;&nbsp;&nbsp;&nbsp;车型:' + carViewData.autoModel + '</span>                                                           ' +
                    '		</div>                                                                                                                      ' +
                    '                                                                                                                                   ' +

                    qrhd.method.createCompanyDataHtml(carViewData.quoteCompanyDataList) +

                    '                                                                                                                                   ' +
                    '	</div>                                                                                                                          ';

            });
            $("#detail_content").html(qrhd.param.pageContentText);
        },
        createCompanyDataHtml: function (compantDataList) {
            var companyDataHtml = "";
            $.each(compantDataList, function (i, companyData) {
                companyDataHtml +=
                    '		<div class="col-sm-12">                                                                                                     ' +
                    '			<table class="table table-bordered table-hover" width="100%">                                                           ' +
                    '				<tr>                                                                                                                ' +
                    '					<td colspan="10">                                                                                               ' +
                    '						<div class="text-left table-hover">                                                                         ' +
                    '							<span class="bold-font" style="color: darkslategrey;">' + companyData.companyName + '</span>                   ' +
                    '							<span class="bold-font" style="color: darkslategrey;">报价次数:' + companyData.quoteNum + '</span>                  ' +
                    '						</div>                                                                                                      ' +
                    '					</td>                                                                                                           ' +
                    '				</tr>                                                                                                               ' +
                    '				<tr class="active text-center">                                                                                     ' +
                    '					<th class="text-center" width="6%">交强险</th>                                                                  ' +
                    '					<th class="text-center" width="6%">车船税</th>                                                                  ' +
                    '					<th class="text-center" width="7%">商业险(参考)</th>                                                                  ' +
                    '					<th class="text-center" width="6%">总计(参考)</th>                                                                    ' +
                    '					<th class="text-center" width="5%">险种数量</th>                                                                ' +
                    '					<th class="text-center" width="28%">商业险险种及保额</th>                                                       ' +
                    '					<th class="text-center" width="10%">报价平台</th>                                                                ' +
                    '                   <th class="text-center" width="5%">报价方式</th>                                                               ' +
                    '					<th class="text-center" width="12%">报价时间</th>' +
                    '					<th class="text-center" width="14%">操作</th>                                                                   ' +
                    '				</tr>                                                                                                               ' +
                    '                                                                                                                                   ' +

                    qrhd.method.createQuoteItemHtml(companyData.quoteRecordExtList) +

                    '                                                                                                                                   ' +
                    '			</table>                                                                                                                ' +
                    '		</div>                                                                                                                      ';
            })
            return companyDataHtml;
        },
        createQuoteItemHtml: function (quoteRecordExtList) {
            var quoteItemHtml = "";
            $.each(quoteRecordExtList, function (i, quoteRecordExt) {
                var createTimeTemp = quoteRecordExt.createTimeString;
                var quoteDetailString = quoteRecordExt.quoteDetailString;
                quoteRecordExt.createTimeString = null;
                quoteRecordExt.additionalParameters = null;
                quoteRecordExt.quoteDetailString = null;
                quoteRecordExt.autoTypeName = null;
                var detailJson = JSON.stringify(quoteRecordExt);
                quoteItemHtml +=
                    '				<tr class="active">                                                                                                 ' +
                    '					<td>' + common.tools.formatMoney(quoteRecordExt.compulsoryPremium, 2) + '</td>                                                                                                      ' +
                    '					<td>' + quoteRecordExt.autoTax + '</td>                                                                                                      ' +
                    '					<td>' + quoteRecordExt.premium + '</td>                                                                                                      ' +
                    '					<td>' + common.tools.formatMoney(quoteRecordExt.totalAmout, 2) + '</td>                                                                                                      ' +
                    '					<td>' + quoteRecordExt.quoteKindNum + '</td>                                                                                                      ' +
                    '					<td style="word-wrap:break-word;word-break:break-all;">' + quoteDetailString + '</td>                                                   ' +
                    '					<td>' + quoteRecordExt.channelName + '</td>                                                                                                      ' +
                    '					<td>' + quoteRecordExt.quoteKind + '</td>                                                                                    ' +
                    '					<td>' + createTimeTemp + '</td>                                                                                    ' +
                    '					<td>                                                                                                            ' +
                    '						<a detailjson=' + detailJson + ' onclick="qrhd.operation.showQuoteItemDetail(this);">险种详情</a>                                       ' +
                    '						<a logId='+quoteRecordExt.logId+' onclick="qrhd.operation.toQuotePage(this);">我要报价</a>                                                    ' +
                    '					</td>                                                                                                           ' +
                    '				</tr>                                                                                                               ';
            });


            return quoteItemHtml;
        },
        fillQuoteRecordDetailContent: function (data) {
            var insurancePackage = data.insurancePackage;
            $("#amend_insuranceCompany").text(common.tools.checkToEmpty(data.insuranceCompanyName));
            $("#totalAmout").text(common.tools.formatMoney(data.totalAmout));
            //$("#amend_orderNo").text(common.tools.checkToEmpty(data.orderNo));
            //$("#amend_commercialPolicyNo").text(common.tools.checkToEmpty(data.commercialPolicyNo));
            //$("#amend_compulsoryPolicyNo").text(common.tools.checkToEmpty(data.compulsoryPolicyNo));
            //$("#amend_orderStatus").text(common.tools.checkToEmpty(data.orderStatus));
            //$("#amend_payableAmount").text(common.tools.formatMoney(data.payableAmount, 2));
            $("#amend_discount_info").text(data.giftDetails);
            $("#amend_compulsoryPremium").text(common.tools.formatMoney(data.compulsoryPremium, 2));
            if (insurancePackage.compulsory) {
                $("#amend_compulsoryChk").attr("checked", true);
            }
            $("#amend_autoTax").text(common.tools.formatMoney(data.autoTax, 2));
            if (insurancePackage.autoTax) {
                $("#amend_autoTaxChk").attr("checked", true);
            }
            $("#amend_commercialPremium").text(common.tools.formatMoney(data.premium, 2));
            if (common.tools.formatMoney(data.premium, 2) > 0) {
                $("#amend_commercialChk").attr("checked", true);
            }
            $("#amend_thirdPartyPremium").text(common.tools.formatMoney(data.thirdPartyPremium, 2));
            $("#amend_thirdPartyAmount").text(common.tools.formatMoney(data.thirdPartyAmount, 2));
            if (insurancePackage.thirdPartyAmount != null && insurancePackage.thirdPartyAmount > 0) {
                $("#amend_thirdPartyChk").attr("checked", true);
            }
            $("#amend_scratchPremium").text(common.tools.formatMoney(data.scratchPremium, 2));
            $("#amend_scratchAmount").text(common.tools.formatMoney(data.scratchAmount, 2));
            if (insurancePackage.scratchAmount != null && insurancePackage.scratchAmount > 0) {
                $("#amend_scratchChk").attr("checked", true);
            }
            $("#amend_damagePremium").text(common.tools.formatMoney(data.damagePremium, 2));
            $("#amend_damageAmount").text(common.tools.formatMoney(data.damageAmount, 2));
            if (insurancePackage.damage) {
                $("#amend_damageChk").attr("checked", true);
            }
            $("#amend_iop").text(common.tools.formatMoney(data.iopTotal, 2));
            if (insurancePackage.thirdPartyIop || insurancePackage.damageIop || insurancePackage.theftIop || insurancePackage.engineIop
                || insurancePackage.driverIop || insurancePackage.passengerIop || insurancePackage.scratchIop || data.iopTotal > 0) {
                $("#amend_iopChk").attr("checked", true);
            }
            $("#amend_driverPremium").text(common.tools.formatMoney(data.driverPremium, 2));
            $("#amend_driverAmount").text(common.tools.formatMoney(data.driverAmount, 2));
            if (insurancePackage.driverAmount != null && insurancePackage.driverAmount > 0) {
                $("#amend_driverChk").attr("checked", true);
            }
            $("#amend_passengerPremium").text(common.tools.formatMoney(data.passengerPremium, 2));
            $("#amend_passengerAmount").text(common.tools.formatMoney(data.passengerAmount, 2));
            if (insurancePackage.passengerAmount != null && insurancePackage.passengerAmount > 0) {
                $("#amend_passengerChk").attr("checked", true);
            }
            $("#amend_theftPremium").text(common.tools.formatMoney(data.theftPremium, 2));
            $("#amend_theftAmount").text(common.tools.formatMoney(data.theftAmount, 2));
            if (insurancePackage.theft) {
                $("#amend_theftChk").attr("checked", true);
            }
            $("#amend_spontaneousLossPremium").text(common.tools.formatMoney(data.spontaneousLossPremium, 2));
            $("#amend_spontaneousLossAmount").text(common.tools.formatMoney(data.spontaneousLossAmount, 2));
            if (insurancePackage.spontaneousLoss) {
                $("#amend_spontaneousLossChk").attr("checked", true);
            }
            $("#amend_enginePremium").text(common.tools.formatMoney(data.enginePremium, 2));
            if (insurancePackage.engine) {
                $("#amend_engineChk").attr("checked", true);
            }
            $("#amend_glassPremium").text(common.tools.formatMoney(data.glassPremium, 2));
            if (insurancePackage.glass) {
                $("#amend_glassChk").attr("checked", true);
                $("#amend_glassType").text(common.tools.checkToEmpty(data.insurancePackage.glassType.name));
            }
        }
    }
}


$(function () {
    qrhd.param.repeatId = common.getUrlParam("repeatId");
    qrhd.init.initPage();
});
