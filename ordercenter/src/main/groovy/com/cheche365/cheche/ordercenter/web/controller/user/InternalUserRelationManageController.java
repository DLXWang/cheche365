package com.cheche365.cheche.ordercenter.web.controller.user;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserRelationManageService;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.user.InternalUserRelationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by sunhuazhong on 2015/7/9.
 */
@RestController
@RequestMapping("/orderCenter/relation")
public class InternalUserRelationManageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IInternalUserRelationManageService internalUserRelationManageService;

    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @VisitorPermission("or0403")
    public boolean add(@RequestParam(value = "customerUserId",required = true) Long customerId,
                       @RequestParam(value = "internalUserId",required = true) Long internalId,
                       @RequestParam(value = "externalUserId",required = true) Long externalId) {
        if (customerId == null || customerId < 1) {
            logger.info("bind user, customerId can not be null or less than 1");
            return false;
        }

        if (internalId == null || internalId < 1) {
            logger.info("bind user, internalId can not be null or less than 1");
            return false;
        }

        if (externalId == null || externalId < 1) {
            logger.info("bind user, externalId can not be null or less than 1");
            return false;
        }

        return internalUserRelationManageService.add(customerId, internalId, externalId);
    }

    @RequestMapping(value = "/update",method = RequestMethod.GET)
    public ModelAndViewResult update(@Valid InternalUserRelationData relationData, BindingResult bindingResult) {
        ModelAndViewResult result = new ModelAndViewResult();
        if (bindingResult.hasErrors()) {
            logger.info("when update internal user relation,validation has error");
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("请将信息填写完整");
            return result;
        }

        if (relationData.getId() == null || relationData.getId() < 1) {
            logger.info("when update internal user relation,id can not be null or less than 1");
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("请求参数异常");
            return result;
        }

        return internalUserRelationManageService.update(relationData);
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public ModelAndViewResult delete(@RequestParam(value = "id",required = true) Long id) {
        ModelAndViewResult result = new ModelAndViewResult();
        if (id == null || id < 1) {
            logger.info("delete internal user relation by id, id can not be null or less than 1.");
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("请求参数异常");
            return result;
        }

        return internalUserRelationManageService.delete(id);
    }

    @RequestMapping(value = "/findOne",method = RequestMethod.GET)
    public ModelAndViewResult findOne(@RequestParam(value = "id",required = true) Long id) {
        return internalUserRelationManageService.findOne(id);
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @VisitorPermission("or0404")
    public PageViewModel<InternalUserRelationData> list(@RequestParam(value = "currentPage",required = true) Integer currentPage,
                                        @RequestParam(value = "pageSize",required = true) Integer pageSize,
                                        @RequestParam(value = "keyword",required = false) String keyword) {
        if (currentPage == null || currentPage < 1 ) {
            logger.error("list internal user relation, currentPage can not be null or less than 1");
            return null;
        }

        if (pageSize == null || pageSize < 1 ) {
            logger.error("list internal user relation, pageSize can not be null or less than 1");
            return null;
        }
        return internalUserRelationManageService.listInternalUserRelation(currentPage, pageSize, keyword);
    }
}
