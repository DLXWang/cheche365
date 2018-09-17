package com.cheche365.cheche.admin.web.controller.user;

import com.cheche365.cheche.admin.service.user.UserManagementService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.admin.web.model.user.UserViewModel;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * Created by wangfei on 2015/9/6.
 */
@RestController
@RequestMapping("/admin/users")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("ad0101")
    public PageViewModel<UserViewModel> findAll(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                @RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "keyType", required = false) Integer keyType) throws UnsupportedEncodingException {
        if (currentPage == null || currentPage < 1) {
            throw new FieldValidtorException("list user info, currentPage can not be null or less than 1");
        }

        if (pageSize == null || pageSize < 1) {
            throw new FieldValidtorException("list user info, pageSize can not be null or less than 1");
        }
        return userManagementService.findAllUserInfos(
            currentPage, pageSize, keyword, keyType);
    }

}
