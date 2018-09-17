package com.cheche365.cheche.baoxian.flow.step

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component


/**
 * 创建续保报价，通过车牌+车主姓名查询车辆信息
 * @author taicw
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class CreateTaskAByName extends ACreateTaskA {

}
