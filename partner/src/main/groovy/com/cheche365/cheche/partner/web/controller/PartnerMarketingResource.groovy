package com.cheche365.cheche.partner.web.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.Marketing
import com.cheche365.cheche.core.repository.MarketingRepository
import com.cheche365.cheche.partner.handler.index.PartnerIndexDirector
import com.cheche365.cheche.web.ContextResource
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest
import static com.cheche365.cheche.core.exception.BusinessException.Code.*

@Slf4j
@Controller
@RequestMapping("/partner/{code}/")
class PartnerMarketingResource extends ContextResource {

    @Autowired
    private PartnerIndexDirector partnerIndexDirector
    @Autowired
    private MarketingRepository marketingRepository

    @RequestMapping(value = "marketing/{marketingCode}", method = RequestMethod.GET)
    String marketing(@PathVariable String code, @PathVariable String marketingCode, HttpServletRequest req) {
        ApiPartner partner = ApiPartner.findByCode(code)
        Marketing marketing = marketingRepository.findFirstByCode(marketingCode)
        if (!partner || !marketing) {
            throw new BusinessException(UNAUTHORIZED_ACCESS, "partnerCode:" + code + ", marketingCode:" + marketingCode + " URL无效。")
        }
        try {
            String path = partnerIndexDirector.toMarketingIndexPageUrl(partner, marketing, req)
            log.debug("第三方合作 【{}】 进入活动页 【{}】, 重定向到url:{}", code, marketingCode, path)
            return "redirect:" + path
        } catch (DataIntegrityViolationException e) {
            log.error("第三方合作 【{}】 绑定手机号出错，活动 【{}】，重定向到错误提示页面，url:{}, queryString={}, exception = {}", code, marketingCode, req.requestURI, req.queryString, e)
            return "redirect:/m/error.html?code=" + MULTIPLE_BOUNDED_MOBILE.codeValue
        } catch (Exception e) {
            log.error("第三方合作 【{}】 进入活动页 【{}】 错误，重定向到错误提示页面，url:{}, queryString={}, exception = {}", code, marketingCode, req.requestURI, req.queryString, e)
            String errorCode = (e instanceof BusinessException) ? (e.code.codeValue as String) : ""
            return "redirect:/m/error.html?code=" + errorCode
        }
    }

}
