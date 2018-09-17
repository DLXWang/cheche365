package com.cheche365.cheche.parser.client.service

import com.cheche365.cheche.parser.dto.AutoTypeRequestObject
import com.cheche365.cheche.parser.dto.AutoTypeResponseObject



/**
 * 车型列表客户端接口
 * @author Huabin Zhang
 */
interface IAutoTypeFeignClient {

    AutoTypeResponseObject getAutoTypes(AutoTypeRequestObject body)

}
