package com.cheche365.cheche.baoxian.flow.step

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component


/**
 * 创建续保报价，通过车牌+车主姓名+车主身份证进行车辆信息精确查询
 * @author taicw
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class CreateTaskAByIdentity extends ACreateTaskA {


    @Override
    protected getParams(context){
        def params = super.getParams(context)
        params.carOwner << [idcardNo : context.auto.identity]
        params
    }

}
