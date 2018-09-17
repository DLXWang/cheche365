package com.cheche365.cheche.wechat.web.controller;

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.wechat.MenuManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by liqiang on 8/11/15.
 */
@Controller
public class WechatMenuController {

    private static Logger logger = LoggerFactory.getLogger(WechatMenuController.class);

    private static final String SAFE_VALUE = "Che_365";

    @Autowired
    private MenuManager menuManager;

    @RequestMapping(value = "/web/wechat/menu/redirect/{state}", method = RequestMethod.GET)
    @ResponseBody
    public String redirect(@PathVariable("state") String state) {
        if (!"downloadApp".equals(state)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "网页链接错误");
        }
        return "redirect:" + WebConstants.getDomainURL() + "/m/share.html";
    }

    @RequestMapping(value = "/web/wechat/menu/create", method = RequestMethod.POST)
    @ResponseBody
    public String createMenu(@RequestBody String menu, @RequestParam String safeValue) {
        if (!SAFE_VALUE.equals(safeValue)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "安全参数验证错误");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("received create menu request [" + menu + "]");
        }

        return menuManager.createMenu(menu).getErrmsg();
    }

}
