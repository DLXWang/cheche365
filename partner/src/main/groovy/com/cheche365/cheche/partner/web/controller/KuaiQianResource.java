package com.cheche365.cheche.partner.web.controller;
import com.cheche365.cheche.partner.api.kuaiqian.DesEncryptUtil;
import com.cheche365.cheche.web.ContextResource;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by shanxf on 17-7-27.
 */
@RestController
@RequestMapping("/partner/kuaiqian")
public class KuaiQianResource extends ContextResource {


    @RequestMapping(value = "/encrypt", method = RequestMethod.GET)
    public HttpEntity encrypt(HttpServletRequest request, @RequestParam("mobile") String mobile) {

        String mobileResult=DesEncryptUtil.encode(mobile);
       /* Map map = Maps.newHashMap();
        map.put("signResult", mobileResult);*/
        return getResponseEntity(mobileResult);
    }
}
