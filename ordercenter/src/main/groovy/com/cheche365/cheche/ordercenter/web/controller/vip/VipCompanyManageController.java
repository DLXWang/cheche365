package com.cheche365.cheche.ordercenter.web.controller.vip;

import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.vip.VipCompanyViewData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 大客户管理
 * Created by sunhuazhong on 2015/6/5.
 */
@RestController
@RequestMapping("/orderCenter/vip")
public class VipCompanyManageController {

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public ModelAndViewResult list(@RequestParam(value = "currentPage",required = true) Integer currentPage,
                     @RequestParam(value = "pageSize",required = true) Integer pageSize,
                     @RequestParam(value = "keyword",required = false) String keyword) {
        return null;
    }

    @RequestMapping(value = "/add",method = RequestMethod.GET)
    public ModelAndViewResult add(@Valid VipCompanyViewData viewData, BindingResult bindingResult) {

        return null;
    }

    @RequestMapping(value = "/update",method = RequestMethod.GET)
    public ModelAndViewResult update(@Valid VipCompanyViewData viewData, BindingResult bindingResult) {
        return null;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public ModelAndViewResult delete(@RequestParam(value = "id",required = true) Long id) {
       return null;
    }

    @RequestMapping(value = "/findOne",method = RequestMethod.GET)
    public ModelAndViewResult findOne(@RequestParam(value = "id",required = true) Long id) {
        return null;
    }

}
