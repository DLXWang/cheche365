package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getModelsByBrands
import static com.cheche365.cheche.parser.util.BusinessUtils.getModelsByCondition
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static groovyx.net.http.ContentType.BINARY

/**
 * 获取车型信息列表
 */
@Component
@Slf4j
class GetCarModelsList implements IStep {

    private static final _API_PATH_NET_CAR_MODELS_DATA = 'Net/netCarModelsDataAction.action'

    @Override
    run(context) {
        def client = context.client
        def args = getRequestParams context, 1, ''

        def brandList, rawCarModels, pageCount
        client.post args, { resp, stream ->
            def html = htmlParser.parse(stream).depthFirst()
            brandList = html.SELECT.find {
                'brandId' == it.@id
            }?.OPTION?.@value?.minus('')

            rawCarModels = getRawCarModels html

            pageCount = getPageCount html.TR.last().TD.text()
        }

        if (brandList) {
            brandList = brandList.unique()
            def initCarModelList = (pageCount > 1
                ? getModelsByCondition(context, 1..pageCount, extractCarModelList(rawCarModels), _GET_CAR_MODELS_BY_PAGE.curry(brandList.first()))
                : extractCarModelList(rawCarModels))
            initCarModelList = initCarModelList.unique {it.rbCode}
            def brandModels = getModelsByBrands context, brandList, initCarModelList, _GET_CAR_MODELS_BY_BRAND
            getSelectedCarModelFSRV context, brandModels, brandList
        } else {
            log.error '根据品牌型号查询车型失败'
            getValuableHintsFSRV (context, [_VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING])
        }
    }

    private getRequestParams(context, pageNo, brandId) {
        def auto = context.auto
        def queryVehicle = auto.autoType?.code ?: ''
        [
            contentType: BINARY,
            path       : _API_PATH_NET_CAR_MODELS_DATA,
            body       : [
                vehicleName: queryVehicle,
                frameNo    : auto.vinNo,
                id         : context.token,
                frameNoFlag: 'true',
                pageNo     : pageNo,
                brandId    : brandId,
            ]
        ]
    }

    private final _GET_CAR_MODELS_BY_BRAND = { context, brandId ->
        def client = context.client
        def args = getRequestParams context, 1, brandId
        def pageCount, rawCarModels
        client.post args, { resp, stream ->
            def html = htmlParser.parse(stream).depthFirst()
            rawCarModels = getRawCarModels html
            pageCount = getPageCount html.TR.last().TD.text()
        }

        getModelsByCondition context, 1..pageCount, extractCarModelList(rawCarModels), _GET_CAR_MODELS_BY_PAGE.curry(brandId)
    }

    private final _GET_CAR_MODELS_BY_PAGE = { brandId, context, pageNo ->
        def client = context.client
        def args = getRequestParams context, pageNo, brandId
        def rawCarModels
        client.post args, { resp, stream ->
            def html = htmlParser.parse(stream).depthFirst()
            rawCarModels = getRawCarModels html
        }
        extractCarModelList(rawCarModels)
    }

    private extractCarModelList(carModels) {
        carModels.collect { tr ->
            def clickText = tr.@onclick
            def info = (clickText[(clickText.indexOf('(') + 1)..clickText.lastIndexOf(')') - 1].replace('\'', '')).split ','
            def tds = tr.TD

            [
                standardName  : info[0],
                parentVehName : info[1],
                seat          : info[2] as int,
                rbCode        : info[3],
                vehicleFgwCode: info[4],
                displacement  : info[5] as double,
                brandName     : tds[0].text(),
                familyName    : tds[1].text(),
                engineDesc    : tds[2].text(),
                gearboxName   : tds[3].text(),
                text          : tds[4].text(),
                price         : tds[5].text(),
            ]
        }
    }

    private getPageCount(text) {
        def m = text =~ /共(\d+)页/
        (m ? m[0][1] : 0) as int
    }

    private getRawCarModels(html) {
        def rawCarModels = html.TR.findAll {
            it.@onclick?.startsWith 'getCarModelValue'
        }

        rawCarModels && !(rawCarModels instanceof List) ? [rawCarModels] : rawCarModels
    }
}
