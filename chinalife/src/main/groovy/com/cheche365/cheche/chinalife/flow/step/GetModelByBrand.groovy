package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getCarModel
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * Created by suyq on 2015/9/21.
 * 根据输入的品牌型号查找车型
 */
@Component
@Slf4j
class GetModelByBrand implements IStep {
    private static final _URL_WEB_SALE_QUERY = '/online/webSaleQuery'
    private static final _URL_INIT_BRAND_ICON = '/online/initBrnadIcon'

    @Override
    run(Object context) {
        RESTClient client = context.client
        def Auto auto = context.auto
        def vehicleFgwCode = auto?.autoType?.code?.replace('牌', '')

        def brandList = getBrandList(client, vehicleFgwCode)
        if (!brandList) {
            return getValuableHintsFSRV(context, [_VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING])
        }
        //TODO:以前实现过拿brandList找vehicleList，这里是否能够使用
        def vehicleInfoList = brandList.collect { brand ->
            getVehicleInfo client, brand
        }
        if (vehicleInfoList) {
            context.vehicleInfoList = vehicleInfoList
            getContinueFSRV { vehicleInfoList }
        } else {
            getContinueWithIgnorableErrorFSRV null, '根据品牌获取车型失败'
        }
    }

    private getBrandList(client, vehicleFgwCode) {
        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_WEB_SALE_QUERY,
            body              : [
                requestType   : 'webSaleQuery',
                vehicleFgwCode: vehicleFgwCode
            ]
        ]
        client.post args, { resp, stream ->
            def sourceHtml = new StringWriter().with {
                it << stream
            }.toString()
            if (!sourceHtml || 'null' == sourceHtml) {
                return
            }
            def bodyHtml = sourceHtml =~ /<tr.*\/tr>/
            bodyHtml = "<table>${bodyHtml[0]}</table>"
            def table = htmlParser.parseText(bodyHtml).depthFirst()

            table.TABLE.TBODY.TR.collect { element ->
                def msg = element.@onclick
                def m = msg =~ /^returncar\(this,(?:"(.*)",)+.*/
                def (parentId, fgwCode, price, brandName) = m[0][1].tokenize('","')[1..-1]
                [
                    parentId : parentId,
                    fgwCode  : fgwCode,
                    price    : price,
                    carYear  : element.TD[3].text(),
                    carModel : element.TD[4].text(),
                    brandName: brandName
                ]
            }
        }
    }

    private getVehicleInfo(client, brand) {
        if (brand) {
            def args = [
                requestContentType: URLENC,
                contentType       : JSON,
                path              : _URL_INIT_BRAND_ICON,
                body              : [
                    requestType: 'setValuesOfVin',
                    hidFgwCode : brand.fgwCode,
                    parentVehId: brand.parentId,
                    price      : brand.price
                ]
            ]

            client.post args, { resp, json ->
                getCarModel(json) + [brandName: brand.brandName]
            }
        }
    }

}
