package com.cheche365.cheche.admin.web.controller.user;

import com.cheche365.cheche.admin.web.model.user.InternalUserViewData;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.manage.common.constants.ManageCommonConstants;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by guoweifu on 2015/9/14.
 */
@RestController
@RequestMapping("/admin/internalUser")
public class InternalUserManageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
    public ResultModel modifyPassword(@RequestParam(value = "id", required = true) Long id,
                                      @RequestParam(value = "password", required = true) String password) {
        if (id == null || id < 1) {
            return new ResultModel(false, "请求参数异常!");
        }
        InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
        if (!id.equals(internalUser.getId())) {
            logger.info("对不起，账号ID为{}不能修改修改账号ID为{}的密码！", internalUser.getId(), id);
            return new ResultModel(false, "对不起，您不能修改其他人的密码！");
        }
        if (internalUserManageService.modifyPassword(id, password)) {
            stringRedisTemplate.opsForSet().remove(ManageCommonConstants.RESET_PASSWORD_LOCK_KEY, internalUser.getEmail());
            stringRedisTemplate.opsForHash().delete(ManageCommonConstants.USER_LOCK_KEY, internalUser.getEmail());
            return new ResultModel();
        } else {
            return new ResultModel(false, "系统异常!");
        }

    }

    @RequestMapping(value = "/currentUser", method = RequestMethod.GET)
    public InternalUserViewData getCurrentUser() {
        InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
        return createViewData(internalUser);
    }

    private InternalUserViewData createViewData(InternalUser internalUser) {
        InternalUserViewData viewData = new InternalUserViewData();
        viewData.setEmail(internalUser.getEmail());
        viewData.setId(internalUser.getId());
        viewData.setName(internalUser.getName());
        viewData.setPermissionCode(String.join(",", internalUserManageService.listAuthority()));
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(ManageCommonConstants.RESET_PASSWORD_KEY, internalUser.getEmail());
        viewData.setResetPasswordFlag(isMember);
        Boolean resetLockPassword = stringRedisTemplate.opsForSet().isMember(ManageCommonConstants.RESET_PASSWORD_LOCK_KEY, internalUser.getEmail());
        viewData.setResetPasswordLockFlag(resetLockPassword);
        return viewData;
    }
}
