package com.cheche365.cheche.partner.web.controller

import com.cheche365.cheche.web.ContextResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/partner/{code}/")
class DeprecatedResource extends ContextResource {

    @Deprecated
    @RequestMapping(value = "photoQuote", method = RequestMethod.GET)
    String photoQuote(@PathVariable String code, HttpServletRequest req, HttpServletResponse res) {
        def reqUrl = req.requestURI.replace("photoQuote", "quote/photo")
        req.getRequestDispatcher(reqUrl).forward(req, res)
    }

    @Deprecated
    @RequestMapping(value = "orderlist", method = RequestMethod.GET)
    String orderList(@PathVariable String code, HttpServletRequest req, HttpServletResponse res) {
        def reqUrl = req.requestURI.replace("orderlist", "orders")
        req.getRequestDispatcher(reqUrl).forward(req, res)
    }

}
