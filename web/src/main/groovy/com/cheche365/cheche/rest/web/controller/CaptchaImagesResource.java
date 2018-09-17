package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.web.service.CaptchaImageService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.cheche365.cheche.core.exception.BusinessException.Code.INPUT_FIELD_NOT_VALID;

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/captchaimages")
@VersionedResource(from = "1.8")
public class CaptchaImagesResource extends ContextResource {

    @Autowired
    private CaptchaImageService captchaImageService;

    @RequestMapping(value = "",method = RequestMethod.GET)
    HttpEntity<RestResponseEnvelope> getCaptchaImages() {

        Map result = captchaImageService.getCaptchaImageMap(session.getId());

        return getResponseEntity(result);
    }

    @RequestMapping(value = "/validation", method = RequestMethod.GET)
    HttpEntity<RestResponseEnvelope> validateCaptchaImage(@RequestParam String imageCode) {

        if (!captchaImageService.needSupplyCaptchaImage(session.getId(), imageCode)) {
            throw new BusinessException(INPUT_FIELD_NOT_VALID, "不需要进行图片验证");
        }

        return getResponseEntity(captchaImageService.validate(session.getId(), imageCode));
    }
}
