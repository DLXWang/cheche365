package com.cheche365.cheche.picc.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * Created by zhanghuabin on 2016/6/25
 * 从人保的中科软车型库获取车型，如果前面先用requestType 02查找车型，
 * 则这里就要用04选择车型，如果成功，后面就不用找车型了
 */
@Component
@Slf4j
class FindCarModelByBrandName03 extends AFindCarModelByBrandName {
}
