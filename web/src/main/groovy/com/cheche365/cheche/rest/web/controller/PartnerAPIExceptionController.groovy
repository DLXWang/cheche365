package com.cheche365.cheche.rest.web.controller

import com.cheche365.cheche.core.exception.BusinessException
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import static com.cheche365.cheche.core.exception.BusinessException.toCode

/**
 * Created by liheng on 2018/8/22 0022.
 */
@RestController
@RequestMapping('/api/public/error')
class PartnerAPIExceptionController {

    @RequestMapping
    def error(@RequestParam(required = false) Integer code,
              @RequestParam(required = false) String msg) throws Exception {
        throw new BusinessException(toCode(code ?: 2002), msg)
    }
}
