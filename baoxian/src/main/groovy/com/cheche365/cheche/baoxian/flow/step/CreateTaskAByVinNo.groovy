package com.cheche365.cheche.baoxian.flow.step

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component



/**
 * 创建续保报价，（江苏地区）通过车牌+车主姓名+车架号进行车辆信息精确查询
 * @author taicw
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class CreateTaskAByVinNo extends ACreateTaskA {

    @Override
    protected getParams(context){
        def params = super.getParams(context)
        params.carInfo << [vinCode : context.auto.vinNo]
        params
    }

}
