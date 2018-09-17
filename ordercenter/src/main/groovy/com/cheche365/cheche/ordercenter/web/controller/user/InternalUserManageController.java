package com.cheche365.cheche.ordercenter.web.controller.user;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.InternalUserViewData;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfei on 2015/5/19.
 */
@RestController
@RequestMapping("/orderCenter/user")
public class InternalUserManageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VisitorPermission("or0402")
    public DataTablePageViewModel list(PublicQuery query) {
        return orderCenterInternalUserManageService.listInternalUser(query);
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @VisitorPermission("or0401")
    public ModelAndViewResult add(@Valid InternalUserViewData viewData, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("when add internal user,validation has error");
            return new ModelAndViewResult(ModelAndViewResult.RESULT_FAIL, "请将信息填写完整");
        }

        if (!viewData.getPassword().equals(viewData.getConfirmPassword())) {
            logger.error("when add internal user,password and confirm password is not equal.");
            return new ModelAndViewResult(ModelAndViewResult.RESULT_FAIL, "两次输入的密码不一致");
        }
        return orderCenterInternalUserManageService.add(viewData);
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public ModelAndViewResult update(@Valid InternalUserViewData viewData, BindingResult bindingResult) {
        ModelAndViewResult result = new ModelAndViewResult();
        if (bindingResult.hasErrors()) {
            logger.info("when update internal user,validation has error");
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("请将信息填写完整");
            return result;
        }
        if (viewData.getId() == null || viewData.getId() < 1) {
            logger.info("when update internal user,id can not be null or less than 1");
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("请求参数异常");
            return result;
        }
        return orderCenterInternalUserManageService.update(viewData);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ModelAndViewResult delete(@RequestParam(value = "id", required = true) Long id) {
        ModelAndViewResult result = new ModelAndViewResult();
        if (id == null || id < 1) {
            logger.info("delete internal user by id, id can not be null or less than 1.");
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("请求参数异常");
            return result;
        }
        return orderCenterInternalUserManageService.delete(id);
    }

    @RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
    public ModelAndViewResult modifyPassword(@RequestParam(value = "id", required = true) Long id,
                                             @RequestParam(value = "password", required = true) String password) {
        ModelAndViewResult result = new ModelAndViewResult();
        if (id == null || id < 1) {
            logger.info("when update internal user password,id can not be null or less than 1");
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("请求参数异常");
            return result;
        }
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        if (!id.equals(internalUser.getId())) {
            logger.info("对不起，账号ID为{}不能修改修改账号ID为{}的密码！", internalUser.getId(), id);
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("对不起，您不能修改其他人的密码！");
            return result;
        }
        return orderCenterInternalUserManageService.modifyPasswordInOrderCenter(id, password);
    }

    @RequestMapping(value = "/findOne", method = RequestMethod.GET)
    public ModelAndViewResult findOne(@RequestParam(value = "id", required = true) Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("find internal user by id,id: " + id);
        }
        return orderCenterInternalUserManageService.findOneById(id);
    }

    @RequestMapping(value = "/getCurrentUser", method = RequestMethod.GET)
    public InternalUserViewData getCurrentUser() {
        return orderCenterInternalUserManageService.getCurrentUser();
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public Map<String, String> findAll() {
        return orderCenterInternalUserManageService.findAllUsers();
    }

    @RequestMapping(value = "/findAllRoleUsers", method = RequestMethod.GET)
    public Map<String, String> findAllRoleUsers() {
        return orderCenterInternalUserManageService.findAllRoleUsers();
    }

    @RequestMapping(value = "/listRole", method = RequestMethod.GET)
    public List<String> listRole() {
        return orderCenterInternalUserManageService.listRole();
    }

    @RequestMapping(value = "/config/{internalUserId}", method = RequestMethod.GET)
    public boolean add(@PathVariable Long internalUserId) {
        if (internalUserId == null || internalUserId < 1) {
            logger.info("config user, internalUserId can not be null or less than 1");
            return false;
        }
        return orderCenterInternalUserManageService.config(internalUserId);
    }
}
