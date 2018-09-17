package com.cheche365.cheche.picc.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * Created by suyq on 2015/7/10.
 * 从人保的中科软车型库获取车型，用requestType 02查询，如果成功，后面还需要用03选中一个车型
 */
@Component
@Slf4j
class FindCarModelByBrandName extends AFindCarModelByBrandName {
}
